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
package utils

import chisel3._

class Wishbone(val N: Int) extends Bundle {
    assert(N % 8 == 0)
    val NUM_BYTES = N / 8

    val cyc = Input(Bool())
    val stb = Input(Bool())
    val we = Input(Bool())
    val sel = Input(UInt(NUM_BYTES.W))
    val addr = Input(UInt(N.W))
    val data_wr = Input(UInt(N.W))

    val ack = Output(Bool())
    val err = Output(Bool())
    val data_rd = Output(UInt(N.W))
}
