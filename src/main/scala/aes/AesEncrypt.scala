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
import utils.RisingEdge

class AesEncrypt extends Module {
    val io = IO(new Bundle {
        val dataIn = Input(UInt(128.W))
        val ivIn = Input(UInt(128.W))
        val dataValid = Input(Bool())

        val keys = Input(Vec(15, AESMatrixDims()))
        val aes256 = Input(Bool())

        val shiftCyc = Output(Bool())
        val shiftRev = Output(Bool())
        val shift = Output(Bool())

        val ready = Output(Bool())

        val dataOut = Output(UInt(128.W))
        val ivOut = Output(UInt(128.W))
        val outputValid = Output(Bool())
    })

    // 0 = unused
    // 1 = inactive
    // 2 = round 1 part 1
    // 3 = round 1 part 2
    // 4 = round 2 part 1
    // 5 = round 2 part 2
    // ...
    // 20 = round 10 part 1
    // 21 = round 10 part 2
    // ...
    // 28 = round 14 part 1
    // 29 = round 14 part 2
    // 30 = output valid
    val state = RegInit(1.U(6.W))

    io.ready := (state === 0.U) || (state === 1.U) || (state === 30.U)
    io.outputValid := (state === 30.U)

    io.shiftRev := false.B && !io.ready
    io.shiftCyc := !io.ready || io.dataValid
    io.shift := false.B

    val matrix = Reg(AESMatrixDims())

    io.dataOut := FromMatrix(matrix)
    io.ivOut := io.dataOut

    // For initial IV + XOR round key 0
    val initOut = ToMatrix(io.dataIn ^ io.ivIn ^ FromMatrix(io.keys(0)))

    val roundPart1 = MatrixShiftRows(AesSbox.OptimizedSbox(matrix))
    val roundPart2 = MatrixXor(MatrixMixCols(matrix), io.keys(0))
    val roundPart2_last = MatrixXor(matrix, io.keys(0))

    when (io.ready && io.dataValid) {
        matrix := initOut
        state := 2.U
        io.shift := true.B
    }

    when (!io.ready) {
        val last = Mux(io.aes256, state === 29.U, state === 21.U)

        when (state(0) === 0.U) { // First half of round
            matrix := roundPart1
        } .otherwise {
            matrix := Mux(last, roundPart2_last, roundPart2)
            io.shift := true.B
        }

        state := state + 1.U

        when (last) { state := 30.U }
    }

}

