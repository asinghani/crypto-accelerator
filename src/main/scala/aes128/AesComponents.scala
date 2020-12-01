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
import Constants._
import aes128.AesComponents._
import aes128.AesSbox.OptSboxComp

class MixColsModule extends Module {
    val io = IO(new Bundle {
        val in = Input(AESMatrixDims())
        val out = Output(AESMatrixDims())
    })

    for (i <- 0 until 4) {
        val x = io.in(i)(0) ^ io.in(i)(1) ^ io.in(i)(2) ^ io.in(i)(3)
        io.out(i)(0) := io.in(i)(0) ^ x ^ _MixFunc(io.in(i)(0) ^ io.in(i)(1))
        io.out(i)(1) := io.in(i)(1) ^ x ^ _MixFunc(io.in(i)(1) ^ io.in(i)(2))
        io.out(i)(2) := io.in(i)(2) ^ x ^ _MixFunc(io.in(i)(2) ^ io.in(i)(3))
        io.out(i)(3) := io.in(i)(3) ^ x ^ _MixFunc(io.in(i)(3) ^ io.in(i)(0))
    }
}

object AesComponents {
    def AESMatrixDims(): AESMatrix = Vec(4, Vec(4, UInt(8.W)))

    def ToMatrix(data: UInt): AESMatrix =
        VecInit(
            VecInit(data(16*8-1, 15*8), data(15*8-1, 14*8), data(14*8-1, 13*8), data(13*8-1, 12*8)),
            VecInit(data(12*8-1, 11*8), data(11*8-1, 10*8), data(10*8-1,  9*8), data( 9*8-1,  8*8)),
            VecInit(data( 8*8-1,  7*8), data( 7*8-1,  6*8), data( 6*8-1,  5*8), data( 5*8-1,  4*8)),
            VecInit(data( 4*8-1,  3*8), data( 3*8-1,  2*8), data( 2*8-1,  1*8), data( 1*8-1,  0*8)),
        )

    def FromMatrix(m: AESMatrix): UInt =
        Cat(m(0)(0), m(0)(1), m(0)(2), m(0)(3),
            m(1)(0), m(1)(1), m(1)(2), m(1)(3),
            m(2)(0), m(2)(1), m(2)(2), m(2)(3),
            m(3)(0), m(3)(1), m(3)(2), m(3)(3))

    def MatrixXor(m1: AESMatrix, m2: AESMatrix): AESMatrix =
        VecInit(
            VecInit(m1(0)(0) ^ m2(0)(0), m1(0)(1) ^ m2(0)(1), m1(0)(2) ^ m2(0)(2), m1(0)(3) ^ m2(0)(3)),
            VecInit(m1(1)(0) ^ m2(1)(0), m1(1)(1) ^ m2(1)(1), m1(1)(2) ^ m2(1)(2), m1(1)(3) ^ m2(1)(3)),
            VecInit(m1(2)(0) ^ m2(2)(0), m1(2)(1) ^ m2(2)(1), m1(2)(2) ^ m2(2)(2), m1(2)(3) ^ m2(2)(3)),
            VecInit(m1(3)(0) ^ m2(3)(0), m1(3)(1) ^ m2(3)(1), m1(3)(2) ^ m2(3)(2), m1(3)(3) ^ m2(3)(3)),
        )

    def MatrixIdentity(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(m(0)(0), m(0)(1), m(0)(2), m(0)(3)),
            VecInit(m(1)(0), m(1)(1), m(1)(2), m(1)(3)),
            VecInit(m(2)(0), m(2)(1), m(2)(2), m(2)(3)),
            VecInit(m(3)(0), m(3)(1), m(3)(2), m(3)(3)),
        )

    def MatrixShiftRows(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(m(0)(0), m(1)(1), m(2)(2), m(3)(3)),
            VecInit(m(1)(0), m(2)(1), m(3)(2), m(0)(3)),
            VecInit(m(2)(0), m(3)(1), m(0)(2), m(1)(3)),
            VecInit(m(3)(0), m(0)(1), m(1)(2), m(2)(3)),
        )

    def MatrixUnshiftRows(m: AESMatrix): AESMatrix =
        VecInit(
            VecInit(m(0)(0), m(3)(1), m(2)(2), m(1)(3)),
            VecInit(m(1)(0), m(0)(1), m(3)(2), m(2)(3)),
            VecInit(m(2)(0), m(1)(1), m(0)(2), m(3)(3)),
            VecInit(m(3)(0), m(2)(1), m(1)(2), m(0)(3)),
        )

    // Based on https://github.com/bozhu/AES-Python impl
    def _MixFunc(x: UInt): UInt = Mux((x & 0x80.U) =/= 0.U, ((x << 1).asUInt ^ 0x1B.U) & 0xFF.U, (x << 1).asUInt)

    def MatrixMixCols(m: AESMatrix): AESMatrix = {
        val mixColsModule = Module(new MixColsModule)
        mixColsModule.io.in := m
        mixColsModule.io.out
    }

    def MatrixUnmixCols(m: AESMatrix): AESMatrix = {
        val out = Wire(AESMatrixDims())

        for (i <- 0 until 4) {
            val x = _MixFunc(_MixFunc(m(i)(0) ^ m(i)(2)))
            val y = _MixFunc(_MixFunc(m(i)(1) ^ m(i)(3)))
            out(i)(0) := m(i)(0) ^ x
            out(i)(1) := m(i)(1) ^ y
            out(i)(2) := m(i)(2) ^ x
            out(i)(3) := m(i)(3) ^ y
        }

        MatrixMixCols(out)
    }
    def RoundKeyComb(lastKey: AESMatrix, roundNum: UInt): AESMatrix = {
        val key = Wire(AESMatrixDims())

        val r0b0 = lastKey(0)(0) ^ OptSboxComp(lastKey(3)(1)) ^ RoundConstants()(roundNum)
        val r0b1 = lastKey(0)(1) ^ OptSboxComp(lastKey(3)(2))
        val r0b2 = lastKey(0)(2) ^ OptSboxComp(lastKey(3)(3))
        val r0b3 = lastKey(0)(3) ^ OptSboxComp(lastKey(3)(0))
        key(0) := VecInit(r0b0, r0b1, r0b2, r0b3)

        val r1b0 = lastKey(1)(0) ^ r0b0
        val r1b1 = lastKey(1)(1) ^ r0b1
        val r1b2 = lastKey(1)(2) ^ r0b2
        val r1b3 = lastKey(1)(3) ^ r0b3
        key(1) := VecInit(r1b0, r1b1, r1b2, r1b3)

        val r2b0 = lastKey(2)(0) ^ r1b0
        val r2b1 = lastKey(2)(1) ^ r1b1
        val r2b2 = lastKey(2)(2) ^ r1b2
        val r2b3 = lastKey(2)(3) ^ r1b3
        key(2) := VecInit(r2b0, r2b1, r2b2, r2b3)

        val r3b0 = lastKey(3)(0) ^ r2b0
        val r3b1 = lastKey(3)(1) ^ r2b1
        val r3b2 = lastKey(3)(2) ^ r2b2
        val r3b3 = lastKey(3)(3) ^ r2b3
        key(3) := VecInit(r3b0, r3b1, r3b2, r3b3)

        key
    }

    def RoundKey1Cyc(lastKey: AESMatrix, roundNum: UInt): AESMatrix = {
        RegNext(RoundKeyComb(lastKey, roundNum)) // TODO properly pipeline
    }

    def RoundKey2Cyc(lastKey: AESMatrix, roundNum: UInt): AESMatrix = {
        RegNext(RoundKey1Cyc(lastKey, roundNum)) // TODO properly pipeline
    }
}
