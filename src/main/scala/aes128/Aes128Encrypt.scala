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

class Aes128Encrypt extends Module {
    val io = IO(new Bundle {
        val dataIn = Input(UInt(128.W))
        val ivIn = Input(UInt(128.W))
        val dataValid = Input(Bool())

        val keys = Input(Vec(11, AESMatrixDims()))

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
    // 22 = output valid
    val state = RegInit(1.U(6.W))

    io.ready := (state === 0.U) || (state === 1.U) || (state === 22.U)
    io.outputValid := (state === 22.U)

    val matrix = Reg(AESMatrixDims())

    io.dataOut := FromMatrix(matrix)
    io.ivOut := io.dataOut

    //val initKeyMatrix = Reg(AESMatrixDims())
    //when (io.ready && io.keyValid) { initKeyMatrix := ToMatrix(io.keyIn) }
    //val keyMatrix = Reg(AESMatrixDims())

    // For initial IV + XOR round key 0
    val initOut = ToMatrix(io.dataIn ^ io.ivIn ^ FromMatrix(io.keys(0)))

    val roundPart1 = MatrixShiftRows(AesSbox.OptimizedSbox(matrix))
    val roundPart2 = MatrixXor(MatrixMixCols(matrix), io.keys((state >> 1).asUInt))
    val roundPart2_10 = MatrixXor(matrix, io.keys((state >> 1).asUInt))

    when (io.ready && io.dataValid) {
        matrix := initOut
        //keyMatrix := initKeyMatrix
        state := 2.U
    }

    when (!io.ready) {
        when (state(0) === 0.U) { // Part 1
            matrix := roundPart1
            //keyMatrix := RoundKeyComb(keyMatrix, (state >> 1).asUInt)
        } .otherwise {
            matrix := Mux(state === 21.U, roundPart2_10, roundPart2)
        }

        state := state + 1.U
    }

}
