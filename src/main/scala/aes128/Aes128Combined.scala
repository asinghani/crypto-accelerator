// Copyright 2020 Anish Singhani
//
// SPDX-License-Identifier: Apache-2.0
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
package aes128

import aes128.AesComponents._
import chisel3._
import sha256.ShiftRegister

class Aes128Combined(val LIMIT_KEY_LENGTH: Boolean = true) extends Module {
    val io = IO(new Bundle {
        val encDataIn = Input(UInt(128.W))
        val encIvIn = Input(UInt(128.W))
        val encDataValid = Input(Bool())

        val decDataIn = Input(UInt(128.W))
        val decIvIn = Input(UInt(128.W))
        val decDataValid = Input(Bool())

        // Shared key generator
        val keyIn = Input(UInt(128.W))
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

    val computedKeys = Reg(Vec(11, AESMatrixDims()))
    val keyUpdate = RegInit(false.B)
    val keyInd = RegInit(0.U(6.W))

    val shreg = Module(new ShiftRegister(DEPTH=11, WIDTH=128))

    shreg.io.input := 0.U
    shreg.io.enable := false.B

    when (io.keyValid) {
        keyUpdate := true.B
        keyInd := 1.U

        shreg.io.enable := true.B
        shreg.io.input := io.keyIn
    }

    when (keyUpdate) {
        keyInd := keyInd + 1.U
        when (keyInd + 1.U === 11.U) {
            keyUpdate := false.B
        }

        shreg.io.enable := true.B
        shreg.io.input := FromMatrix(RoundKeyComb(ToMatrix(shreg.io.output(0)), keyInd))
    }

    val keys = VecInit(shreg.io.output.toArray.map(ToMatrix).reverse)

    //val keyUpdate = false.B
    //val keys = VecInit(Array(ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn), ToMatrix(io.keyIn)))

    val enc = Module(new Aes128Encrypt)
    enc.io.dataIn := io.encDataIn
    enc.io.ivIn := io.encIvIn
    enc.io.dataValid := io.encDataValid
    enc.io.keys := keys

    io.encReady := enc.io.ready && !keyUpdate && !io.keyValid
    io.encDataOut := enc.io.dataOut
    io.encIvOut := enc.io.ivOut
    io.encOutputValid := enc.io.outputValid



    val dec = Module(new Aes128Decrypt)
    dec.io.dataIn := io.decDataIn
    dec.io.ivIn := io.decIvIn
    dec.io.dataValid := io.decDataValid
    dec.io.keys := keys

    io.decReady := dec.io.ready && !keyUpdate && !io.keyValid
    io.decDataOut := dec.io.dataOut
    io.decIvOut := dec.io.ivOut
    io.decOutputValid := dec.io.outputValid

    shreg.io.cyc := enc.io.shiftCyc || dec.io.shiftCyc
    shreg.io.rev := enc.io.shiftRev || dec.io.shiftRev
    when (enc.io.shift || dec.io.shift) { shreg.io.enable := true.B }
}
