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
package tests.sha

import aes.AesSbox
import aes.Constants.{InvSbox, Sbox}
import chisel3._
import chisel3.iotesters.PeekPokeTester
import sha256.Sha256Accel

import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

class OptimizedSboxTestHarness extends Module {
    val io = IO(new Bundle {
        val input = Input(UInt(8.W))

        val naiveOut = Output(UInt(8.W))
        val optimizedOut = Output(UInt(8.W))

        val naiveInvOut = Output(UInt(8.W))
        val optimizedInvOut = Output(UInt(8.W))
    })

    io.naiveOut := Sbox()(io.input)
    io.optimizedOut := AesSbox.OptSboxComp(io.input)

    io.naiveInvOut := InvSbox()(io.input)
    io.optimizedInvOut := AesSbox.OptInvSboxComp(io.input)
}

class OptimizedSboxTest(dut: OptimizedSboxTestHarness) extends PeekPokeTester(dut) {
    for (i <- 0 until 256) {
        poke(dut.io.input, i)
        step(1)
        assert(peek(dut.io.naiveOut) == peek(dut.io.optimizedOut))
        assert(peek(dut.io.naiveInvOut) == peek(dut.io.optimizedInvOut))
    }
}

