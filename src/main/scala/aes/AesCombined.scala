// SPDX-FileCopyrightText: 2020 Anish Singhani
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// SPDX-License-Identifier: Apache-2.0
package aes

import aes.AesComponents._
import chisel3._
import sha256.ShiftRegister

class AesCombined(val LIMIT_KEY_LENGTH: Boolean = true) extends Module {
    val io = IO(new Bundle {
        val encDataIn = Input(UInt(128.W))
        val encIvIn = Input(UInt(128.W))
        val encDataValid = Input(Bool())

        val decDataIn = Input(UInt(128.W))
        val decIvIn = Input(UInt(128.W))
        val decDataValid = Input(Bool())

        // If false, defaults to AES128 mode
        val aes256 = Input(Bool())

        // Shared key generator
        val keyIn = Input(UInt(128.W))
        val keyShift = Input(Bool())
        val keyValid = Input(Bool())

        val encReady = Output(Bool())
        val decReady = Output(Bool())

        val encDataOut = Output(UInt(128.W))
        val encIvOut = Output(UInt(128.W))
        val encOutputValid = Output(Bool())

        val decDataOut = Output(UInt(128.W))
        val decIvOut = Output(UInt(128.W))
        val decOutputValid = Output(Bool())
    })

    val computedKeys = Reg(Vec(15, AESMatrixDims()))
    val keyUpdate = RegInit(false.B)
    val keyInd = RegInit(0.U(6.W))

    val shreg = Module(new ShiftRegister(DEPTH=15, WIDTH=128, TAP0=4, TAP1=0))
    shreg.io.tap := io.aes256

    shreg.io.input := 0.U
    shreg.io.enable := false.B

    when (io.keyValid) {
        keyUpdate := true.B
        keyInd := 1.U
    }

    when (io.keyValid || io.keyShift) {
        shreg.io.enable := true.B
        shreg.io.input := io.keyIn
    }

    when (keyUpdate) {
        keyInd := keyInd + 1.U
        // Key changes happen infrequently enough that it is sufficient to generate the full 14 keys even when doing AES-128
        when (keyInd + 1.U === Mux(io.aes256, 14.U, 15.U)) { // One less key generated for AES-256 because initial key is double-size
            keyUpdate := false.B
        }

        shreg.io.enable := true.B
        shreg.io.input := FromMatrix(RoundKeyComb(ToMatrix(shreg.io.output(1)), ToMatrix(shreg.io.output(0)), keyInd, io.aes256))
    }

    val keys = VecInit(shreg.io.output.toArray.map(ToMatrix).reverse)

    val enc = Module(new AesEncrypt)
    enc.io.dataIn := io.encDataIn
    enc.io.ivIn := io.encIvIn
    enc.io.dataValid := io.encDataValid
    enc.io.keys := keys
    enc.io.aes256 := io.aes256

    io.encReady := enc.io.ready && !keyUpdate && !io.keyValid
    io.encDataOut := enc.io.dataOut
    io.encIvOut := enc.io.ivOut
    io.encOutputValid := enc.io.outputValid


    val dec = Module(new AesDecrypt)
    dec.io.dataIn := io.decDataIn
    dec.io.ivIn := io.decIvIn
    dec.io.dataValid := io.decDataValid
    dec.io.keys := keys
    dec.io.aes256 := io.aes256

    io.decReady := dec.io.ready && !keyUpdate && !io.keyValid
    io.decDataOut := dec.io.dataOut
    io.decIvOut := dec.io.ivOut
    io.decOutputValid := dec.io.outputValid

    shreg.io.cyc := enc.io.shiftCyc || dec.io.shiftCyc
    shreg.io.rev := enc.io.shiftRev || dec.io.shiftRev
    when (enc.io.shift || dec.io.shift) { shreg.io.enable := true.B }
}

