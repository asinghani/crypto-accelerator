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
import utils.RotateRight

class MessageScheduleArray extends Module {

    val io = IO(new Bundle {
        val first = Input(Bool())
        val shiftIn = Input(Bool())
        val wordIn = Input(UInt(32.W))

        // Output is one cycle behind shiftIn/wordIn
        val wOut = Output(UInt(32.W))
    })

    val iReg = RegInit(0.U(6.W))
    val i = Wire(UInt(6.W))

    val outWire = Wire(UInt(32.W))
    val out = RegInit(0.U(32.W))
    io.wOut := out
    out := outWire

    when (io.first) {
        i := 0.U
        iReg := 0.U
    } .elsewhen (io.shiftIn) {
        i := iReg + 1.U
        iReg := iReg + 1.U
    } .otherwise {
        i := iReg
    }

    val shreg = Module(new ShiftRegister(DEPTH = 16, WIDTH = 32))
    shreg.io.rev := false.B
    shreg.io.cyc := false.B

    shreg.io.enable := io.shiftIn
    shreg.io.input := outWire // output[0] == w[i-1]

    outWire := 0.U
    when (io.shiftIn) {
        when (i < 16.U) {
            outWire := io.wordIn
        } .otherwise {
            val s0 = RotateRight(shreg.io.output(14), 7) ^ RotateRight(shreg.io.output(14), 18) ^ (shreg.io.output(14) >> 3).asUInt
            val s1 = RotateRight(shreg.io.output(1), 17) ^ RotateRight(shreg.io.output(1), 19) ^ (shreg.io.output(1) >> 10).asUInt
            outWire := shreg.io.output(15) + s0 + shreg.io.output(6) + s1
        }
    }
}
