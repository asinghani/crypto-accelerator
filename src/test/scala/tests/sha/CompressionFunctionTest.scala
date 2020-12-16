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

import chisel3.iotesters.PeekPokeTester
import sha256.CompressionFunction

import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

class CompressionFunctionTest(dut: CompressionFunction) extends PeekPokeTester(dut) {
    var dataIn = Array(0l)
    var expected = Array(0l)

    // Small data
    dataIn = Array(
        0x68656C6Cl, 0x6F800000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000028l
    )

    expected = Array(
        0x2CF24DBAl, 0x5FB0A30El, 0x26E83B2Al, 0xC5B9E29El, 0x1B161E5Cl, 0x1FA7425El, 0x73043362l, 0x938B9824l
    )

    poke(dut.io.first, false)
    poke(dut.io.newChunk, false)
    poke(dut.io.shiftIn, false)
    poke(dut.io.wordIn, 0x12345678)

    step(50)

    poke(dut.io.first, true)

    for (i <- 0 until 16) {
        poke(dut.io.shiftIn, true)
        poke(dut.io.wordIn, dataIn(i))
        step(1)
        poke(dut.io.first, false)
        poke(dut.io.shiftIn, false)
        if (Random.nextBoolean()) step(2 + Random.nextInt(8))
    }

    poke(dut.io.wordIn, 0x12345678l)

    breakable {
        for (i <- 0 until 50) {
            poke(dut.io.shiftIn, true)
            step(1)
            if (peek(dut.io.valid) == 1) break
        }
    }

    expect(dut.io.valid, 1)
    for (i <- 0 until 8) {
        assert(peek(dut.io.out)(i) == expected(i))
    }

    // Large data
    dataIn = Array(
        0x33613537l, 0x63643330l, 0x35363066l, 0x62326637l, 0x32383361l, 0x37343333l, 0x65636539l, 0x31393436l, 0x61633664l, 0x61346234l, 0x30343063l, 0x38633337l, 0x61666135l, 0x36343031l, 0x39396465l, 0x64353964l, 0x80000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000200l
    )

    expected = Array(
        0x41812DC6l, 0x0561798Dl, 0xC0CC6E57l, 0x4B641893l, 0xECF4186Dl, 0x4097283Al, 0xF4A6CFF3l, 0xEDDAA4A0l
    )

    poke(dut.io.first, false)
    poke(dut.io.shiftIn, false)
    poke(dut.io.wordIn, 0x12345678)

    step(50)

    poke(dut.io.first, true)

    for (i <- 0 until 16) {
        poke(dut.io.shiftIn, true)
        poke(dut.io.wordIn, dataIn(i))
        step(1)
        poke(dut.io.first, false)
        poke(dut.io.shiftIn, false)
        if (Random.nextBoolean()) step(2 + Random.nextInt(8))
    }

    poke(dut.io.wordIn, 0x12345678l)

    breakable {
        for (i <- 0 until 50) {
            poke(dut.io.shiftIn, true)
            step(1)
            if (peek(dut.io.valid) == 1) break
        }
    }
    poke(dut.io.shiftIn, false)

    expect(dut.io.valid, 1)
    step(1)

    poke(dut.io.newChunk, true)
    for (i <- 0 until 16) {
        poke(dut.io.shiftIn, true)
        poke(dut.io.wordIn, dataIn(16+i))
        step(1)
        poke(dut.io.newChunk, false)
        poke(dut.io.shiftIn, false)
        if (Random.nextBoolean()) step(2 + Random.nextInt(8))
    }

    poke(dut.io.wordIn, 0x12345678l)

    breakable {
        for (i <- 0 until 50) {
            poke(dut.io.shiftIn, true)
            step(1)
            if (peek(dut.io.valid) == 1) break
        }
    }

    expect(dut.io.valid, 1)

    for (i <- 0 until 8) {
        assert(peek(dut.io.out)(i) == expected(i))
    }

    step(50)

    // Very large data
    dataIn = Array(
        0x33613537l, 0x63643330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36303330l, 0x35363361l, 0x35376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36306633l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663732l, 0x38336137l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63366232l, 0x33613533l, 0x61353763l, 0x64336135l, 0x37636433l, 0x30353630l, 0x66623266l, 0x37323833l, 0x61373433l, 0x33656365l, 0x39313934l, 0x36616336l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36376364l, 0x33303536l, 0x30666232l, 0x66373238l, 0x33613734l, 0x33336563l, 0x65393139l, 0x34366163l, 0x36663732l, 0x38336133l, 0x61353763l, 0x64333035l, 0x36306662l, 0x32663766l, 0x62326637l, 0x32383361l, 0x37343333l, 0x65636539l, 0x31393436l, 0x61633637l, 0x34333365l, 0x63653931l, 0x39343661l, 0x63368000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000000l, 0x00000E10l
    )

    expected = Array(
        0x3B71FC79l, 0x40D08FFAl, 0xF968AF1El, 0xD465323El, 0x5C078003l, 0x3F8C1CF5l, 0xA5AADF5El, 0x0B9D36DBl
    )

    poke(dut.io.first, false)
    poke(dut.io.shiftIn, false)
    poke(dut.io.wordIn, 0x12345678)

    step(50)

    poke(dut.io.first, true)

    for (i <- 0 until 16) {
        poke(dut.io.shiftIn, true)
        poke(dut.io.wordIn, dataIn(i))
        step(1)
        poke(dut.io.first, false)
        poke(dut.io.shiftIn, false)
        if (Random.nextBoolean()) step(2 + Random.nextInt(8))
    }

    poke(dut.io.wordIn, 0x12345678l)

    breakable {
        for (i <- 0 until 50) {
            poke(dut.io.shiftIn, true)
            step(1)
            if (peek(dut.io.valid) == 1) break
        }
    }

    assert(dataIn.length % 16 == 0)
    for (ind <- 1 until dataIn.length/16) {
        poke(dut.io.shiftIn, false)
        expect(dut.io.valid, 1)
        step(1)

        poke(dut.io.newChunk, true)
        for (i <- 0 until 16) {
            poke(dut.io.shiftIn, true)
            poke(dut.io.wordIn, dataIn((16*ind)+i))
            step(1)
            poke(dut.io.newChunk, false)
            poke(dut.io.shiftIn, false)
            if (Random.nextBoolean()) step(2 + Random.nextInt(8))
        }

        poke(dut.io.wordIn, 0x12345678l)

        breakable {
            for (i <- 0 until 50) {
                poke(dut.io.shiftIn, true)
                step(1)
                if (peek(dut.io.valid) == 1) break
            }
        }

        expect(dut.io.valid, 1)
    }

    for (i <- 0 until 8) {
        assert(peek(dut.io.out)(i) == expected(i))
    }
}

