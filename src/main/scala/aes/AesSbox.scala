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

import aes.Constants.{InvSbox, Sbox}
import chisel3._
import chisel3.util._

object AesSbox {
    // Parse a program from string where
    // + -> XOR
    // x -> AND
    // # -> XNOR
    def ApplySboxProgram(wires: collection.mutable.Map[String, Bool], program: String): Unit = {
        for (l <- program.lines().toArray) {
            val line = l.toString.strip
            if (line.length > 2) {
                val dst  = line.split(" = ")(0).strip
                val exp  = line.split(" = ")(1).strip
                val src1 = exp.split(" ")(0).strip
                val op   = exp.split(" ")(1).strip
                val src2 = exp.split(" ")(2).strip

                if (!wires.keySet.contains(dst)) {
                    wires(dst) = Wire(Bool())
                }

                if      (op == "+") wires(dst) := wires(src1) ^ wires(src2)
                else if (op == "x") wires(dst) := wires(src1) & wires(src2)
                else if (op == "#") wires(dst) := !(wires(src1) ^ wires(src2))
                else throw new Exception("Invalid operator")
            }
        }
    }

    // Naive implementation
    def NaiveSbox(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(Sbox()(m(0)(0)), Sbox()(m(0)(1)), Sbox()(m(0)(2)), Sbox()(m(0)(3))),
            VecInit(Sbox()(m(1)(0)), Sbox()(m(1)(1)), Sbox()(m(1)(2)), Sbox()(m(1)(3))),
            VecInit(Sbox()(m(2)(0)), Sbox()(m(2)(1)), Sbox()(m(2)(2)), Sbox()(m(2)(3))),
            VecInit(Sbox()(m(3)(0)), Sbox()(m(3)(1)), Sbox()(m(3)(2)), Sbox()(m(3)(3))),
        )

    def OptimizedSbox(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(OptSboxComp(m(0)(0)), OptSboxComp(m(0)(1)), OptSboxComp(m(0)(2)), OptSboxComp(m(0)(3))),
            VecInit(OptSboxComp(m(1)(0)), OptSboxComp(m(1)(1)), OptSboxComp(m(1)(2)), OptSboxComp(m(1)(3))),
            VecInit(OptSboxComp(m(2)(0)), OptSboxComp(m(2)(1)), OptSboxComp(m(2)(2)), OptSboxComp(m(2)(3))),
            VecInit(OptSboxComp(m(3)(0)), OptSboxComp(m(3)(1)), OptSboxComp(m(3)(2)), OptSboxComp(m(3)(3))),
        )


    // Optimized implementation
    // Derived from https://eprint.iacr.org/2011/332.pdf
    def OptSboxComp(in: UInt): UInt = {
        val wires = collection.mutable.Map(
            // Inputs
            "U0" -> in(7), "U1" -> in(6),
            "U2" -> in(5), "U3" -> in(4),
            "U4" -> in(3), "U5" -> in(2),
            "U6" -> in(1), "U7" -> in(0),

            // Outputs
            "S0" -> Wire(Bool()), "S1" -> Wire(Bool()),
            "S2" -> Wire(Bool()), "S3" -> Wire(Bool()),
            "S4" -> Wire(Bool()), "S5" -> Wire(Bool()),
            "S6" -> Wire(Bool()), "S7" -> Wire(Bool()),
        )

        ApplySboxProgram(wires, Constants.SboxProgram)

        Cat(wires("S0"), wires("S1"), wires("S2"), wires("S3"),
            wires("S4"), wires("S5"), wires("S6"), wires("S7"))
    }

    // Naive implementation
    def NaiveInvSbox(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(InvSbox()(m(0)(0)), InvSbox()(m(0)(1)), InvSbox()(m(0)(2)), InvSbox()(m(0)(3))),
            VecInit(InvSbox()(m(1)(0)), InvSbox()(m(1)(1)), InvSbox()(m(1)(2)), InvSbox()(m(1)(3))),
            VecInit(InvSbox()(m(2)(0)), InvSbox()(m(2)(1)), InvSbox()(m(2)(2)), InvSbox()(m(2)(3))),
            VecInit(InvSbox()(m(3)(0)), InvSbox()(m(3)(1)), InvSbox()(m(3)(2)), InvSbox()(m(3)(3))),
        )

    def OptimizedInvSbox(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(OptInvSboxComp(m(0)(0)), OptInvSboxComp(m(0)(1)), OptInvSboxComp(m(0)(2)), OptInvSboxComp(m(0)(3))),
            VecInit(OptInvSboxComp(m(1)(0)), OptInvSboxComp(m(1)(1)), OptInvSboxComp(m(1)(2)), OptInvSboxComp(m(1)(3))),
            VecInit(OptInvSboxComp(m(2)(0)), OptInvSboxComp(m(2)(1)), OptInvSboxComp(m(2)(2)), OptInvSboxComp(m(2)(3))),
            VecInit(OptInvSboxComp(m(3)(0)), OptInvSboxComp(m(3)(1)), OptInvSboxComp(m(3)(2)), OptInvSboxComp(m(3)(3))),
        )

    // Optimized implementation
    // Derived from https://eprint.iacr.org/2011/332.pdf
    def OptInvSboxComp(in: UInt): UInt = {
        val wires = collection.mutable.Map(
            // Inputs
            "U0" -> in(7), "U1" -> in(6),
            "U2" -> in(5), "U3" -> in(4),
            "U4" -> in(3), "U5" -> in(2),
            "U6" -> in(1), "U7" -> in(0),

            // Outputs
            "W0" -> Wire(Bool()), "W1" -> Wire(Bool()),
            "W2" -> Wire(Bool()), "W3" -> Wire(Bool()),
            "W4" -> Wire(Bool()), "W5" -> Wire(Bool()),
            "W6" -> Wire(Bool()), "W7" -> Wire(Bool()),
        )

        ApplySboxProgram(wires, Constants.InvSboxProgram)

        Cat(wires("W0"), wires("W1"), wires("W2"), wires("W3"),
            wires("W4"), wires("W5"), wires("W6"), wires("W7"))
    }
}
