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

class ShiftRegister(val DEPTH: Int, val WIDTH: Int = 32) extends Module {
    val io = IO(new Bundle {
        val input    = Input(UInt(WIDTH.W))
        val enable   = Input(Bool())

        val output   = Output(Vec(DEPTH, UInt(WIDTH.W)))
    })

    val reg = Reg(Vec(DEPTH, UInt(WIDTH.W)))

    when (io.enable) {
        for (i <- 0 until DEPTH - 1) {
            reg(i+1) := reg(i)
        }

        reg(0) := io.input
    }

    io.output := reg
}
