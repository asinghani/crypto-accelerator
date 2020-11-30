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
import sha256.MessageScheduleArray

import scala.util.Random

class MessageScheduleArrayTest(dut: MessageScheduleArray) extends PeekPokeTester(dut) {
    val dataIn = Array(0x097206B9l, 0xD2F19855l, 0x501657D9l, 0x134F7A40l, 0x6AB4C5D6l, 0xC876D1E3l, 0xC0EF8EB8l, 0x964F5B76l, 0x276E1096l, 0xCC62EA66l, 0x9811DD21l, 0x7EBACE9Cl, 0xC8E60276l, 0x379002AEl, 0x974AB608l, 0x4AF4DADCl)

    val expected = Array(0x097206B9l, 0xD2F19855l, 0x501657D9l, 0x134F7A40l, 0x6AB4C5D6l, 0xC876D1E3l, 0xC0EF8EB8l, 0x964F5B76l, 0x276E1096l, 0xCC62EA66l, 0x9811DD21l, 0x7EBACE9Cl, 0xC8E60276l, 0x379002AEl, 0x974AB608l, 0x4AF4DADCl, 0xBBA42186l, 0x8E7F38D9l, 0xC08DFF92l, 0xE8805190l, 0x4D36A92El, 0x0CB3F020l, 0xB6BEDC76l, 0x84914017l, 0xDA12972El, 0x3C937E9Cl, 0x1E63957Fl, 0x1094F341l, 0xE7E89462l, 0x84615507l, 0xFBDD2B9El, 0xB76BFF8Dl, 0x959F70F6l, 0x6F719A2Dl, 0x50092324l, 0xCE28D75Cl, 0x4501B94Al, 0xC6AE9C3Bl, 0xC966F1F7l, 0x9B19A281l, 0xD0C7BB88l, 0x8A824C64l, 0xD3F1A41Bl, 0xC15D9B6Bl, 0xE016A99El, 0x351CB8B4l, 0x0B1C3D96l, 0x771B0BB0l, 0xEB584CE9l, 0x327F5414l, 0xD6C85632l, 0xE13C26BAl, 0x645F74BAl, 0xB4411F05l, 0x0E4AC8F3l, 0x181CB890l, 0x8C07F3AEl, 0x7185CDF0l, 0x66235960l, 0x10BCA80Bl, 0x9CC222C6l, 0x26788418l, 0x2536EE3El, 0x33314D02l)

    poke(dut.io.first, false)
    poke(dut.io.shiftIn, false)
    poke(dut.io.wordIn, 0x12345678)

    step(50)

    poke(dut.io.first, true)
    for (i <- 0 until 64) {
        poke(dut.io.shiftIn, true)
        poke(dut.io.wordIn, if (i < 16) { dataIn(i) } else { 0x12345678l })
        step(1)
        poke(dut.io.first, false)
        poke(dut.io.shiftIn, false)
        expect(dut.io.wOut, expected(i))
        if (Random.nextBoolean()) step(2 + Random.nextInt(8))
    }
}
