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

class ShiftRegister(val DEPTH: Int, val WIDTH: Int = 32, var TAP0: Int = -1, var TAP1: Int = -1) extends Module {
    if (TAP0 == -1) TAP0 = DEPTH - 1
    if (TAP1 == -1) TAP1 = DEPTH - 1

    val io = IO(new Bundle {
        val input    = Input(UInt(WIDTH.W))
        val enable   = Input(Bool())

        val rev   = Input(Bool())
        val cyc   = Input(Bool())
        val tap   = Input(Bool())

        val output   = Output(Vec(DEPTH, UInt(WIDTH.W)))
    })

    val reg = Reg(Vec(DEPTH, UInt(WIDTH.W)))

    when (io.enable) {
        when (io.rev) {
            for (i <- 0 until DEPTH - 1) {
                reg(i) := reg(i+1)
            }

            reg(DEPTH-1) := reg(Mux(io.tap, TAP1.U, TAP0.U))
        } .otherwise {
            for (i <- 0 until DEPTH - 1) {
                reg(i+1) := reg(i)
            }

            when (io.cyc) { reg(Mux(io.tap, TAP1.U, TAP0.U)) := reg(DEPTH-1) }
                .otherwise { reg(0) := io.input }
        }
    }

    io.output := reg
}

