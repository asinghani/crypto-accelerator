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
package tests.sha

import chisel3.iotesters.PeekPokeTester
import sha256.Sha256Wishbone

import scala.util.Random

class Sha256WishboneTest(dut: Sha256Wishbone) extends PeekPokeTester(dut) {

    poke(dut.io.bus.stb, false)
    poke(dut.io.bus.cyc, false)
    poke(dut.io.bus.we, false)

    def wishboneWrite(addr: Long, data: Long) : Unit = {
        poke(dut.io.bus.addr, addr)
        poke(dut.io.bus.data_wr, data)
        poke(dut.io.bus.we, true)
        poke(dut.io.bus.sel, 0xF)
        poke(dut.io.bus.cyc, true)
        poke(dut.io.bus.stb, true)
        while(peek(dut.io.bus.ack) != 1) step(1)
        poke(dut.io.bus.cyc, false)
        poke(dut.io.bus.stb, false)
        step(1)
        expect(dut.io.bus.ack, 0)
    }

    def wishboneRead(addr: Long) : Long = {
        poke(dut.io.bus.addr, addr)
        poke(dut.io.bus.we, false)
        poke(dut.io.bus.cyc, true)
        poke(dut.io.bus.stb, true)
        while(peek(dut.io.bus.ack) != 1) step(1)
        poke(dut.io.bus.cyc, false)
        poke(dut.io.bus.stb, false)
        val data_out = peek(dut.io.bus.data_rd).toLong
        step(1)
        expect(dut.io.bus.ack, 0)
        return data_out
    }

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

    var dataIn = Array(0l)
    var expected = Array(0l)

    // Small data, all in one burst
    dataIn = Array(
        0x68656C6Cl, 0x6F800000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000028l
    )

    expected = Array(
        0x2CF24DBAl, 0x5FB0A30El, 0x26E83B2Al, 0xC5B9E29El, 0x1B161E5Cl, 0x1FA7425El, 0x73043362l, 0x938B9824l
    )

    wishboneWrite(0x00, 0x1) // New hash
    dataIn.foreach(wishboneWrite(0x04, _))
    while ((wishboneRead(0x00) & 0x4l) == 0) step(1)
    for (i <- 0 until 8) assert(wishboneRead(0x10 + 4 * i) == expected(i))

    // Very large data in one burst
    dataIn = Array(
        0x33613537l, 0x63643330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36303330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663766l, 0x62326637l, 0x32383361l, 0x37343333l, 0x65636539l, 0x31393436l, 0x61633637l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63368000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000E10l
    )

    expected = Array(
        0x3B71FC79l, 0x40D08FFAl, 0xF968AF1El, 0xD465323El, 0x5C078003l, 0x3F8C1CF5l, 0xA5AADF5El, 0x0B9D36DBl
    )

    wishboneWrite(0x00, 0x1) // New hash
    dataIn.foreach(wishboneWrite(0x04, _))
    while ((wishboneRead(0x00) & 0x4l) == 0) step(1)
    for (i <- 0 until 8) assert(wishboneRead(0x10 + 4 * i) == expected(i))

    // Very large data with gaps
    dataIn = Array(
        0x33613537l, 0x63643330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36303330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663766l, 0x62326637l, 0x32383361l, 0x37343333l, 0x65636539l, 0x31393436l, 0x61633637l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63368000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000E10l
    )

    expected = Array(
        0x3B71FC79l, 0x40D08FFAl, 0xF968AF1El, 0xD465323El, 0x5C078003l, 0x3F8C1CF5l, 0xA5AADF5El, 0x0B9D36DBl
    )

    wishboneWrite(0x00, 0x1) // New hash
    for (x <- dataIn) {
        wishboneWrite(0x04, _)
        if (Random.nextBoolean()) step(3 + Random.nextInt(5))
    }
    while ((wishboneRead(0x00) & 0x4l) == 0) step(1)
    for (i <- 0 until 8) assert(wishboneRead(0x10 + 4 * i) == expected(i))

}
