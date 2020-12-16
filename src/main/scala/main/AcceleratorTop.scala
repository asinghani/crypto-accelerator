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
package main

import aes.{AesWishbone, Aes56Wishbone}
import chisel3._
import sha256.Sha256Wishbone
import utils.Wishbone

class AcceleratorTop(val SHA_IDENT: String = "SHA256 Core", val AES_IDENT: String = "AES128/256 Core") extends Module {
    val SHORT_KEY = false

    println("Generating AcceleratorTop. Config:")
    println(s"    SHORT_KEY = ${SHORT_KEY}")
    println(s"    AES_IDENT = ${AES_IDENT}")
    println(s"    SHA_IDENT = ${SHA_IDENT}")

    val io = IO(new Bundle {
        val bus = new Wishbone(N=32)
    })

    val aes = Module(new AesWishbone(IDENT=(if (SHORT_KEY) { "(56-bit) " } else { "" }) + AES_IDENT, LIMIT_KEY_LENGTH=SHORT_KEY))
    aes.io.bus.stb := io.bus.stb
    aes.io.bus.we := io.bus.we
    aes.io.bus.sel := io.bus.sel
    aes.io.bus.addr := io.bus.addr(15, 0)
    aes.io.bus.data_wr := io.bus.data_wr

    aes.io.bus.cyc := io.bus.cyc && (io.bus.addr(19, 16) === 0x0.U)

    val sha256 = Module(new Sha256Wishbone(IDENT=SHA_IDENT))
    sha256.io.bus.stb := io.bus.stb
    sha256.io.bus.we := io.bus.we
    sha256.io.bus.sel := io.bus.sel
    sha256.io.bus.addr := io.bus.addr(15, 0)
    sha256.io.bus.data_wr := io.bus.data_wr

    sha256.io.bus.cyc := io.bus.cyc && (io.bus.addr(19, 16) === 0x1.U)

    io.bus.data_rd := Mux(io.bus.addr(19, 16) === 0x0.U, aes.io.bus.data_rd, sha256.io.bus.data_rd)
    io.bus.ack := Mux(io.bus.addr(19, 16) === 0x0.U, aes.io.bus.ack, sha256.io.bus.ack)
    io.bus.err := Mux(io.bus.addr(19, 16) === 0x0.U, aes.io.bus.err, sha256.io.bus.err)
}
