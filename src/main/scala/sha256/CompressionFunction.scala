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
package sha256

import chisel3._
import utils.RotateRight

class CompressionFunction extends Module {

    val io = IO(new Bundle {
        val first = Input(Bool())
        val newChunk = Input(Bool())
        val shiftIn = Input(Bool())
        val wordIn = Input(UInt(32.W))

        val valid = Output(Bool())
        val out = Output(Vec(8, UInt(32.W)))
    })

    val valid = RegInit(false.B)
    io.valid := valid

    val i = RegInit(0.U(6.W))

    val hash_val = RegInit(Constants.hashInit())
    io.out := hash_val

    val a = RegInit(Constants.hashInit()(0))
    val b = RegInit(Constants.hashInit()(1))
    val c = RegInit(Constants.hashInit()(2))
    val d = RegInit(Constants.hashInit()(3))
    val e = RegInit(Constants.hashInit()(4))
    val f = RegInit(Constants.hashInit()(5))
    val g = RegInit(Constants.hashInit()(6))
    val h = RegInit(Constants.hashInit()(7))

    val messageScheduleArray = Module(new MessageScheduleArray)
    messageScheduleArray.io.first := io.first | io.newChunk
    messageScheduleArray.io.shiftIn := io.shiftIn
    messageScheduleArray.io.wordIn := io.wordIn

    // Ensures validity
    val first = RegNext(io.first)
    val newChunk = RegNext(io.newChunk)
    val shiftIn = RegNext(io.shiftIn)

    when (io.first | io.newChunk) {
        valid := false.B
        i := 0.U
    }

    when (io.first) {
        a := Constants.hashInit()(0)
        b := Constants.hashInit()(1)
        c := Constants.hashInit()(2)
        d := Constants.hashInit()(3)
        e := Constants.hashInit()(4)
        f := Constants.hashInit()(5)
        g := Constants.hashInit()(6)
        h := Constants.hashInit()(7)
        hash_val := Constants.hashInit()

    } .elsewhen (io.newChunk) {
        a := hash_val(0)
        b := hash_val(1)
        c := hash_val(2)
        d := hash_val(3)
        e := hash_val(4)
        f := hash_val(5)
        g := hash_val(6)
        h := hash_val(7)
    }

    when (shiftIn) {
        val cur_i = i

        val S1 = RotateRight(e, 6) ^ RotateRight(e, 11) ^ RotateRight(e, 25)
        val ch = (e & f) ^ ((~e).asUInt & g)
        val temp1 = h + S1 + ch + Constants.roundConstants()(cur_i) + messageScheduleArray.io.wOut
        val S0 = RotateRight(a, 2) ^ RotateRight(a, 13) ^ RotateRight(a, 22)
        val maj = (a & b) ^ (a & c) ^ (b & c)
        val temp2 = S0 + maj

        h := g
        g := f
        f := e
        e := d + temp1
        d := c
        c := b
        b := a
        a := temp1 + temp2

        i := cur_i + 1.U

        when (i === 63.U) {
            valid := true.B

            a := temp1 + temp2 + hash_val(0)
            b := a + hash_val(1)
            c := b + hash_val(2)
            d := c + hash_val(3)
            e := d + temp1 + hash_val(4)
            f := e + hash_val(5)
            g := f + hash_val(6)
            h := g + hash_val(7)

            hash_val(0) := hash_val(0) + temp1 + temp2
            hash_val(1) := hash_val(1) + a
            hash_val(2) := hash_val(2) + b
            hash_val(3) := hash_val(3) + c
            hash_val(4) := hash_val(4) + d + temp1
            hash_val(5) := hash_val(5) + e
            hash_val(6) := hash_val(6) + f
            hash_val(7) := hash_val(7) + g

        } .otherwise {
            valid := false.B
        }
    }

}

