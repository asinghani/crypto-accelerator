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
import chisel3.util._

object SliceAssign {
    def apply(x: UInt, y: UInt, top: Int, bot: Int): UInt = {
        assert(x.widthKnown && y.widthKnown && y.getWidth == (top - bot + 1))
        val out = if (top < x.getWidth - 1 && bot > 0) {
            Cat(x(x.getWidth-1, top+1), y, x(bot-1, 0))
        } else if (top < x.getWidth - 1) {
            Cat(x(x.getWidth-1, top+1), y)
        } else if (bot > 0) {
            Cat(y, x(bot-1, 0))
        } else {
            Cat(y)
        }

        assert(out.getWidth == x.getWidth)
        return out
    }
}
