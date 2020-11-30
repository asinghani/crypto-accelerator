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
package sha256

import chisel3._

object Constants {
    def hashInit(): Vec[UInt] =
        VecInit(Array(0x6a09e667l, 0xbb67ae85l, 0x3c6ef372l, 0xa54ff53al, 0x510e527fl, 0x9b05688cl, 0x1f83d9abl, 0x5be0cd19).map(_.U(32.W)))

    def roundConstants(): Vec[UInt] =
        VecInit(Array(0x428a2f98l, 0x71374491l, 0xb5c0fbcfl, 0xe9b5dba5l, 0x3956c25bl, 0x59f111f1l, 0x923f82a4l, 0xab1c5ed5l, 0xd807aa98l, 0x12835b01l, 0x243185bel, 0x550c7dc3l, 0x72be5d74l, 0x80deb1fel, 0x9bdc06a7l, 0xc19bf174l, 0xe49b69c1l, 0xefbe4786l, 0x0fc19dc6l, 0x240ca1ccl, 0x2de92c6fl, 0x4a7484aal, 0x5cb0a9dcl, 0x76f988dal, 0x983e5152l, 0xa831c66dl, 0xb00327c8l, 0xbf597fc7l, 0xc6e00bf3l, 0xd5a79147l, 0x06ca6351l, 0x14292967l, 0x27b70a85l, 0x2e1b2138l, 0x4d2c6dfcl, 0x53380d13l, 0x650a7354l, 0x766a0abbl, 0x81c2c92el, 0x92722c85l, 0xa2bfe8a1l, 0xa81a664bl, 0xc24b8b70l, 0xc76c51a3l, 0xd192e819l, 0xd6990624l, 0xf40e3585l, 0x106aa070l, 0x19a4c116l, 0x1e376c08l, 0x2748774cl, 0x34b0bcb5l, 0x391c0cb3l, 0x4ed8aa4al, 0x5b9cca4fl, 0x682e6ff3l, 0x748f82eel, 0x78a5636fl, 0x84c87814l, 0x8cc70208l, 0x90befffal, 0xa4506cebl, 0xbef9a3f7l, 0xc67178f2l).map(_.U(32.W)))

}
