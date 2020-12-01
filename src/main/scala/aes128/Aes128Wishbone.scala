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
package aes128

import chisel3._
import chisel3.util._
import utils.{RisingEdge, SliceAssign, Wishbone}

class Aes128Wishbone(val LIMIT_KEY_LENGTH: Boolean = true) extends Module {

    val io = IO(new Bundle {
        // Wishbone classic
        val bus = new Wishbone(N=32)
    })


    val data_rd = RegInit(0.U(32.W))
    io.bus.data_rd := data_rd

    val ack = RegInit(false.B)
    io.bus.ack := ack
    io.bus.err := false.B

    val accel = Module(new Aes128Combined)

    val cbcMode = RegInit(false.B)
    val iv = Reg(UInt(128.W))

    val outValid = RegInit(false.B)
    val out = Reg(UInt(128.W))


    val ivNext = Wire(UInt(128.W))
    ivNext := iv
    iv := ivNext

    when (RisingEdge(accel.io.encOutputValid)) {
        out := accel.io.encDataOut
        outValid := true.B
        iv := accel.io.encIvOut
    }

    when (RisingEdge(accel.io.decOutputValid)) {
        out := accel.io.decDataOut
        outValid := true.B
        iv := accel.io.decIvOut
    }

    val dataReg = Reg(UInt(128.W))
    val dataNext = Wire(UInt(128.W))
    dataNext := dataReg
    dataReg := dataNext

    accel.io.encIvIn := Mux(cbcMode, iv, 0.U)
    accel.io.decIvIn := Mux(cbcMode, iv, 0.U)

    accel.io.encDataIn := dataReg
    accel.io.decDataIn := dataReg

    // mode = 0 -> ECB, mode = 1 -> CBC
    // {28'b0, mode, outValid, encReady, decReady}
    val encReady = accel.io.encReady
    val decReady = accel.io.decReady
    val statusReg = Cat(0.U(28.W), cbcMode(0), outValid, encReady, decReady)

    // Key length limited to 56
    val key = if (LIMIT_KEY_LENGTH) { Reg(UInt(56.W)) } else { Reg(UInt(128.W)) }

    val keyUpdated = RegInit(false.B)

    val keyNext = Wire(UInt(128.W))
    keyNext := key

    // If length-limit enabled, trim key
    key := (if (LIMIT_KEY_LENGTH) { keyNext(55, 0) } else { keyNext })

    val keyFull = Wire(UInt(128.W))

    if (LIMIT_KEY_LENGTH) { // Key length limited to 56, pad to full size by duplicating key
        keyFull := Cat(key(15, 0), key, key)
    } else {
        // Use full key
        keyFull := key
    }

    keyUpdated := false.B
    accel.io.keyIn := keyFull
    accel.io.keyValid := keyUpdated

    val startEnc = WireDefault(false.B)
    val startDec = WireDefault(false.B)

    accel.io.encDataValid := startEnc
    accel.io.decDataValid := startDec

    when (startEnc || startDec) { outValid := false.B }

    // 0x00 = status    // RW
    // 0x04 = encStart  // WO
    // 0x08 = decStart  // WO
    // 0x0C = updateKey // WO
    // 0x10 = data[0]   // RW
    // 0x14 = data[4]   // RW
    // 0x18 = data[8]   // RW
    // 0x1C = data[C]   // RW
    // 0x20 = iv[0]     // RW
    // 0x24 = iv[4]     // RW
    // 0x28 = iv[8]     // RW
    // 0x2C = iv[C]     // RW
    // 0x30 = out[0]    // RO
    // 0x34 = out[4]    // RO
    // 0x38 = out[8]    // RO
    // 0x3C = out[C]    // RO
    // 0x40 = key[0]    // RW
    // 0x44 = key[4]    // RW
    // 0x48 = key[8]    // RW
    // 0x4C = key[C]    // RW

    ack := false.B
    when(io.bus.cyc && io.bus.stb && !io.bus.ack) {
        ack := true.B

        // Read value
        data_rd := 0.U(32.W)
        switch(io.bus.addr >> 2) {
            is(0.U)  { data_rd := statusReg }
            // 1.U write-only
            // 2.U write-only
            // 3.U write-only
            /*is(4.U)  { data_rd := dataReg(127, 96) }
            is(5.U)  { data_rd := dataReg(95, 64) }
            is(6.U)  { data_rd := dataReg(63, 32) }
            is(7.U)  { data_rd := dataReg(31, 0) }

            is(8.U)  { data_rd := iv(127, 96) }
            is(9.U)  { data_rd := iv(95, 64) }
            is(10.U) { data_rd := iv(63, 32) }
            is(11.U) { data_rd := iv(31, 0) }*/

            is(12.U) { data_rd := out(127, 96) }
            is(13.U) { data_rd := out(95, 64) }
            is(14.U) { data_rd := out(63, 32) }
            is(15.U) { data_rd := out(31, 0) }

            /*is(16.U) { data_rd := keyFull(127, 96) }
            is(17.U) { data_rd := keyFull(95, 64) }
            is(18.U) { data_rd := keyFull(63, 32) }
            is(19.U) { data_rd := keyFull(31, 0) }*/
        }

        when(io.bus.we) {
            // Write
            switch(io.bus.addr >> 2) {
                is(0.U) {
                    when (io.bus.sel(0)) { cbcMode := io.bus.data_wr(3) } // Mode
                }

                is(1.U) {
                    when (io.bus.sel(0) && io.bus.data_wr(0) && encReady) { startEnc := true.B }
                }

                is(2.U) {
                    when (io.bus.sel(0) && io.bus.data_wr(0) && decReady) { startDec := true.B }
                }

                is(3.U) {
                    when (io.bus.sel(0) && io.bus.data_wr(0) && decReady && encReady) { keyUpdated := true.B }
                }

                is(4.U) {
                    dataNext := Cat(dataReg(95, 0), io.bus.data_wr)
                }

                // IV can be written using a fake decrypt

                // 12.U read-only
                // 13.U read-only
                // 14.U read-only
                // 15.U read-only

                is(16.U) {
                    keyNext := Cat(keyFull(95, 0), io.bus.data_wr)
                }
            }
        }

    }
}
