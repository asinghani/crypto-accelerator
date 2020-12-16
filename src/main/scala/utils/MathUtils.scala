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
package utils

object MathUtils {
    def intLog2(x: Int): Int = {
        val log = (Math.log(x) / Math.log(2.0)).round.toInt
        assert((1 << log) == x)
        return log
    }

    implicit class BinStrToInt(val sc: StringContext) extends AnyVal {
        def b(args: Any*): Int = {
            val strings = sc.parts.iterator
            val expressions = args.iterator
            val buf = new StringBuilder(strings.next())
            while(strings.hasNext) {
                buf.append(expressions.next())
                buf.append(strings.next())
            }

            Integer.parseInt("0" + buf, 2)
        }
    }
}

