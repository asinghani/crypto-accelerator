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
package sha256

import chisel3._
import chisel3.util._
import utils.Wishbone

class Sha256Accel extends Module {

    val io = IO(new Bundle {
        // first must be raised for one cycle before starting an actual write
        val first = Input(Bool())

        val inputData = Input(UInt(32.W))
        val inputValid = Input(Bool())
        val inputReady = Output(Bool())

        val outputData = Output(Vec(8, UInt(32.W)))
        val outputValid = Output(Bool())
    })

    val accel = Module(new CompressionFunction)
    io.outputData := accel.io.out
    io.outputValid := accel.io.valid

    val first = RegInit(true.B)
    when (io.first) { first := true.B }
    when (accel.io.shiftIn) { first := false.B }

    val ctr = RegInit(0.U(8.W))
    accel.io.newChunk := (ctr === 0.U) && accel.io.shiftIn
    accel.io.first := first && accel.io.shiftIn

    accel.io.shiftIn := false.B
    accel.io.wordIn := io.inputData
    io.inputReady := (RegNext(ctr) < 16.U) && (ctr < 16.U) && !io.first
    when (ctr >= 16.U) {
        io.inputReady := false.B
        accel.io.shiftIn := true.B

        ctr := ctr + 1.U
        when (ctr === 63.U) { ctr := 0.U }
    } .elsewhen (io.inputValid) {
        accel.io.shiftIn := true.B
        ctr := ctr + 1.U
    }

    when (io.first) { ctr := 0.U }
}
