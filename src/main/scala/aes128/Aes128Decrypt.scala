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

class Aes128Decrypt extends Module {
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


    // 0 = output valid
    // 1 = final xor / IV
    // 2 = round 1 part 1
    // 3 = round 1 part 2
    // 4 = round 2 part 1
    // 5 = round 2 part 2
    // ...
    // 20 = round 10 part 1
    // 21 = round 10 part 2
    // 22 = inactive
    val state = RegInit(22.U(6.W))

    io.ready := ((state === 0.U) || (state === 22.U))
    io.outputValid := (state === 0.U)

    val ctSaved = Reg(UInt(128.W))
    val ivSaved = Reg(UInt(128.W))

    val matrix = Reg(AESMatrixDims())

    io.dataOut := FromMatrix(matrix)
    io.ivOut := ctSaved

    // Reflected from encrypt side
    val roundPart1 = AesSbox.OptimizedInvSbox(MatrixUnshiftRows(matrix))
    val roundPart2 = MatrixUnmixCols(MatrixXor(matrix, io.keys((state >> 1).asUInt)))
    val roundPart2_10 = MatrixXor(matrix, io.keys((state >> 1).asUInt))

    val finalOut = ToMatrix(ivSaved ^ FromMatrix(MatrixXor(matrix, io.keys(0))))

    when (io.ready && io.dataValid) {
        matrix := ToMatrix(io.dataIn)
        ctSaved := io.dataIn
        ivSaved := io.ivIn
        state := 21.U
    }

    when (!io.ready) {
        when (state === 1.U) {
            matrix := finalOut
        } .elsewhen (state(0) === 0.U) {
            matrix := roundPart1
        } .otherwise {
            matrix := Mux(state === 21.U, roundPart2_10, roundPart2)
        }

        state := state - 1.U
    }

    //     def decrypt(self, ciphertext):
    //        self.cipher_state = text2matrix(ciphertext)
    //
    //        for i in range(10, 0, -1):
    //            ## CYCLE 1
    //            self.matrix_xor_elementwise(self.cipher_state, self.round_keys[i])
    //            if i != 10: self.unmix_columns(self.cipher_state)
    //
    //            ## CYCLE 2
    //            self.matrix_unshift_rows(self.cipher_state)
    //            self.matrix_invsbox_lookup(self.cipher_state)
    //
    //        self.matrix_xor_elementwise(self.cipher_state, self.round_keys[0])
    //
    //        out = matrix2text(self.cipher_state)
    //
    //        if self.iv is not None:
    //            out = out ^ self.iv
    //            self.iv = ciphertext
    //
    //        return out
}
