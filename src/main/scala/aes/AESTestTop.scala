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
package aes

import aes.AesComponents._
import chisel3._

class AESTestTop extends Module {
    val io = IO(new Bundle {
        val dataIn = Input(UInt(128.W))
        val ivIn = Input(UInt(128.W))
        val dataValid = Input(Bool())

        val keyIn = Input(UInt(128.W))
        val keyValid = Input(Bool())

        val encReady = Output(Bool())
        val decReady = Output(Bool())

        val encDataOut = Output(UInt(128.W))
        val encIvOut = Output(UInt(128.W))
        val encOutputValid = Output(Bool())

        val decDataOut = Output(UInt(128.W))
        val decIvOut = Output(UInt(128.W))
        val decOutputValid = Output(Bool())
    })

    val aes = Module(new AesCombined)
    aes.io.encDataIn := io.dataIn
    aes.io.encIvIn := io.ivIn
    aes.io.encDataValid := io.dataValid

    aes.io.decDataIn := io.dataIn
    aes.io.decIvIn := io.ivIn
    aes.io.decDataValid := io.dataValid

    aes.io.keyIn := io.keyIn
    aes.io.keyValid := io.keyValid

    io.encReady := aes.io.encReady
    io.decReady := aes.io.decReady

    io.encDataOut := aes.io.encDataOut
    io.encIvOut := aes.io.encIvOut
    io.encOutputValid := aes.io.encOutputValid

    io.decDataOut := aes.io.decDataOut
    io.decIvOut := aes.io.decIvOut
    io.decOutputValid := aes.io.decOutputValid
}
