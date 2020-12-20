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
import chisel3.util._
import utils.Wishbone

class Sha256Wishbone(val IDENT: String = "SHA256 Core") extends Module {

    val io = IO(new Bundle {
        // Wishbone classic
        val bus = new Wishbone(N=32)
    })

    // When not ready, response will be delayed
    // 0x00 = {29'b0, out_valid, ready, new}
    // 0x04 = data stream in (only 32-bit writes are allowed here)
    // 0x08 = unused
    // 0x0C = unused
    // 0x10 = hash 0
    // 0x14 = hash 1
    // 0x18 = hash 2
    // 0x1C = hash 3
    // 0x20 = hash 4
    // 0x24 = hash 5
    // 0x28 = hash 6
    // 0x2C = hash 7

    var identifier_str = IDENT
    while (identifier_str.length % 4 != 3) identifier_str += " "
    identifier_str += '\0'
    val identifier_words = identifier_str.chars().toArray.grouped(4).toArray.map(x => (x(3) << 24) | (x(2) << 16) | (x(1) << 8) | (x(0) << 0))

    val data_rd = Reg(UInt(32.W))
    io.bus.data_rd := data_rd

    val ack = RegInit(false.B)
    io.bus.ack := ack
    io.bus.err := false.B

    val accel = Module(new Sha256Accel)

    accel.io.inputValid := false.B
    accel.io.inputData := io.bus.data_wr
    accel.io.first := false.B

    ack := false.B
    when(io.bus.cyc && io.bus.stb && !io.bus.ack) {
        // Read value
        data_rd := 0.U(32.W)
        switch(io.bus.addr >> 2) {
            is(0.U)  { data_rd := Cat(0.U(29.W), accel.io.outputValid(0), accel.io.inputReady(0), 0.U(1.W)) }
            // 1.U write-only
            // 2.U unused
            // 3.U unused
            is(4.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(0), 0.U(32.W)) }
            is(5.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(1), 0.U(32.W)) }
            is(6.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(2), 0.U(32.W)) }
            is(7.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(3), 0.U(32.W)) }
            is(8.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(4), 0.U(32.W)) }
            is(9.U)  { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(5), 0.U(32.W)) }
            is(10.U) { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(6), 0.U(32.W)) }
            is(11.U) { data_rd := Mux(accel.io.outputValid(0), accel.io.outputData(7), 0.U(32.W)) }
        }

        for((w, i) <- identifier_words.zipWithIndex) {
            when((io.bus.addr >> 2).asUInt === (20 + i).U) {
                data_rd := w.U(32.W)
            }
        }

        ack := true.B
        when(io.bus.we) {
            // Write
            switch(io.bus.addr >> 2) {
                is(0.U)  {
                    when (io.bus.sel(0) && io.bus.data_wr(0)) {
                        accel.io.first := true.B
                    }
                }

                // Data in
                is(1.U)  {
                    when (accel.io.inputReady) {
                        accel.io.inputValid := true.B
                    } .otherwise {
                        // Wait until ready
                        ack := false.B
                    }
                }
            }
        }

    }
}

