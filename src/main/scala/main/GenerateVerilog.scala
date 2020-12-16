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

import java.io.{BufferedWriter, File, FileWriter}

import aes.AESTestTop
import chisel3.stage.ChiselStage
import sha256.Sha256Wishbone
import utils._

object GenerateVerilog extends App {
    val build_dir = new File("build/")
    if (!build_dir.exists) build_dir.mkdirs

    val outfile = new File("build/top.v")

    val BUILD_ARGS = Array(
        "--target-dir", "build"
    )

    val aes_ident = if (args.length > 0) { args(0) } else { "AES128/256 Core" }
    val sha_ident = if (args.length > 1) { args(1) } else { "SHA256 Core" }

    val verilog = new ChiselStage().emitVerilog(
        new AcceleratorTop(AES_IDENT = aes_ident, SHA_IDENT = sha_ident),
        args = BUILD_ARGS
    )

    val writer = new BufferedWriter(new FileWriter(outfile))
    writer.write(verilog)
    writer.close()
}