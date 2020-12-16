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
package aes

import chisel3._

object Constants {
    def Sbox(): Vec[UInt] = {
        println("WARNING: Using inefficient Sbox lookup table")
        VecInit(Array(
            0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
            0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0,
            0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15,
            0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75,
            0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84,
            0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF,
            0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8,
            0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
            0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
            0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB,
            0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79,
            0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08,
            0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A,
            0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E,
            0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF,
            0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16,
        ).map(_.U(8.W)))
    }

    def InvSbox(): Vec[UInt] = {
        println("WARNING: Using inefficient InvSbox lookup table")

        VecInit(Array(
            0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB,
            0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE, 0xE9, 0xCB,
            0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
            0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25,
            0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92,
            0x6C, 0x70, 0x48, 0x50, 0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84,
            0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06,
            0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
            0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73,
            0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E,
            0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18, 0xBE, 0x1B,
            0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
            0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F,
            0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF,
            0xA0, 0xE0, 0x3B, 0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61,
            0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D,
        ).map(_.U(8.W)))
    }

    def RoundConstants(): Vec[UInt] =
        VecInit(Array(
            0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40,
            0x80, 0x1B, 0x36, 0x6C, 0xD8, 0xAB, 0x4D, 0x9A,
        ).map(_.U(8.W)))

    // https://www.cs.yale.edu/homes/peralta/CircuitStuff/AESDEPTH16SIZE125
    val SboxProgram: String = """T1 = U6 + U4
                                |T2 = U3 + U0
                                |T3 = U1 + U2
                                |T4 = U7 + T3
                                |T5 = T1 + T2
                                |T6 = U1 + U5
                                |T7 = U0 + U6
                                |T8 = T1 + T6
                                |T9 = U6 + T4
                                |T10 = U3 + T4
                                |T11 = U7 + T5
                                |T12 = T5 + T6
                                |T13 = U2 + U5
                                |T14 = T3 + T5
                                |T15 = U5 + T7
                                |T16 = U0 + U5
                                |T17 = U7 + T8
                                |T18 = U6 + U5
                                |T19 = T2 + T18
                                |T20 = T4 + T15
                                |T21 = T1 + T13
                                |T22 = U0 + T4
                                |T39 = T21 + T5
                                |T40 = T21 + T7
                                |T41 = T7 + T19
                                |T42 = T16 + T14
                                |T43 = T22 + T17
                                |T44 = T19 x T5
                                |T45 = T20 x T11
                                |T46 = T12 + T44
                                |T47 = T10 x U7
                                |T48 = T47 + T44
                                |T49 = T7 x T21
                                |T50 = T9 x T4
                                |T51 = T40 + T49
                                |T52 = T22 x T17
                                |T53 = T52 + T49
                                |T54 = T2 x T8
                                |T55 = T41 x T39
                                |T56 = T55 + T54
                                |T57 = T16 x T14
                                |T58 = T57 + T54
                                |T59 = T46 + T45
                                |T60 = T48 + T42
                                |T61 = T51 + T50
                                |T62 = T53 + T58
                                |T63 = T59 + T56
                                |T64 = T60 + T58
                                |T65 = T61 + T56
                                |T66 = T62 + T43
                                |T67 = T65 + T66
                                |T68 = T65 x T63
                                |T69 = T64 + T68
                                |T70 = T63 + T64
                                |T71 = T66 + T68
                                |T72 = T71 x T70
                                |T73 = T69 x T67
                                |T74 = T63 x T66
                                |T75 = T70 x T74
                                |T76 = T70 + T68
                                |T77 = T64 x T65
                                |T78 = T67 x T77
                                |T79 = T67 + T68
                                |T80 = T64 + T72
                                |T81 = T75 + T76
                                |T82 = T66 + T73
                                |T83 = T78 + T79
                                |T84 = T81 + T83
                                |T85 = T80 + T82
                                |T86 = T80 + T81
                                |T87 = T82 + T83
                                |T88 = T85 + T84
                                |T89 = T87 x T5
                                |T90 = T83 x T11
                                |T91 = T82 x U7
                                |T92 = T86 x T21
                                |T93 = T81 x T4
                                |T94 = T80 x T17
                                |T95 = T85 x T8
                                |T96 = T88 x T39
                                |T97 = T84 x T14
                                |T98 = T87 x T19
                                |T99 = T83 x T20
                                |T100 = T82 x T10
                                |T101 = T86 x T7
                                |T102 = T81 x T9
                                |T103 = T80 x T22
                                |T104 = T85 x T2
                                |T105 = T88 x T41
                                |T106 = T84 x T16
                                |T107 = T104 + T105
                                |T108 = T93 + T99
                                |T109 = T96 + T107
                                |T110 = T98 + T108
                                |T111 = T91 + T101
                                |T112 = T89 + T92
                                |T113 = T107 + T112
                                |T114 = T90 + T110
                                |T115 = T89 + T95
                                |T116 = T94 + T102
                                |T117 = T97 + T103
                                |T118 = T91 + T114
                                |T119 = T111 + T117
                                |T120 = T100 + T108
                                |T121 = T92 + T95
                                |T122 = T110 + T121
                                |T123 = T106 + T119
                                |T124 = T104 + T115
                                |T125 = T111 + T116
                                |S0 = T109 + T122
                                |S2 = T123 # T124
                                |T128 = T94 + T107
                                |S3 = T113 + T114
                                |S4 = T118 + T128
                                |T131 = T93 + T101
                                |T132 = T112 + T120
                                |S7 = T113 # T125
                                |T134 = T97 + T116
                                |T135 = T131 + T134
                                |T136 = T93 + T115
                                |S6 = T109 # T135
                                |T138 = T119 + T132
                                |S5 = T109 + T138
                                |T140 = T114 + T136
                                |S1 = T109 # T140""".stripMargin

    // https://www.cs.yale.edu/homes/peralta/CircuitStuff/AESReverseDepth.txt
    val InvSboxProgram: String = """T23 = U0 + U3
                                   |T22 = U1 # U3
                                   |T2 = U0 # U1
                                   |T1 = U3 + U4
                                   |T24 = U4 # U7
                                   |R5 = U6 + U7
                                   |T8 = U1 # T23
                                   |T19 = T22 + R5
                                   |T9 = U7 # T1
                                   |T10 = T2 + T24
                                   |T13 = T2 + R5
                                   |T3 = T1 + R5
                                   |T25 = U2 # T1
                                   |R13 = U1 + U6
                                   |T17 = U2 # T19
                                   |T20 = T24 + R13
                                   |T4 = U4 + T8
                                   |R17 = U2 # U5
                                   |R18 = U5 # U6
                                   |R19 = U2 # U4
                                   |Y5 = U0 + R17
                                   |T6 = T22 + R17
                                   |T16 = R13 + R19
                                   |T27 = T1 + R18
                                   |T15 = T10 + T27
                                   |T14 = T10 + R18
                                   |T26 = T3 + T16
                                   |M1 = T13 x T6
                                   |M2 = T23 x T8
                                   |M3 = T14 + M1
                                   |M4 = T19 x Y5
                                   |M5 = M4 + M1
                                   |M6 = T3 x T16
                                   |M7 = T22 x T9
                                   |M8 = T26 + M6
                                   |M9 = T20 x T17
                                   |M10 = M9 + M6
                                   |M11 = T1 x T15
                                   |M12 = T4 x T27
                                   |M13 = M12 + M11
                                   |M14 = T2 x T10
                                   |M15 = M14 + M11
                                   |M16 = M3 + M2
                                   |M17 = M5 + T24
                                   |M18 = M8 + M7
                                   |M19 = M10 + M15
                                   |M20 = M16 + M13
                                   |M21 = M17 + M15
                                   |M22 = M18 + M13
                                   |M23 = M19 + T25
                                   |M24 = M22 + M23
                                   |M25 = M22 x M20
                                   |M26 = M21 + M25
                                   |M27 = M20 + M21
                                   |M28 = M23 + M25
                                   |M29 = M28 x M27
                                   |M30 = M26 x M24
                                   |M31 = M20 x M23
                                   |M32 = M27 x M31
                                   |M33 = M27 + M25
                                   |M34 = M21 x M22
                                   |M35 = M24 x M34
                                   |M36 = M24 + M25
                                   |M37 = M21 + M29
                                   |M38 = M32 + M33
                                   |M39 = M23 + M30
                                   |M40 = M35 + M36
                                   |M41 = M38 + M40
                                   |M42 = M37 + M39
                                   |M43 = M37 + M38
                                   |M44 = M39 + M40
                                   |M45 = M42 + M41
                                   |M46 = M44 x T6
                                   |M47 = M40 x T8
                                   |M48 = M39 x Y5
                                   |M49 = M43 x T16
                                   |M50 = M38 x T9
                                   |M51 = M37 x T17
                                   |M52 = M42 x T15
                                   |M53 = M45 x T27
                                   |M54 = M41 x T10
                                   |M55 = M44 x T13
                                   |M56 = M40 x T23
                                   |M57 = M39 x T19
                                   |M58 = M43 x T3
                                   |M59 = M38 x T22
                                   |M60 = M37 x T20
                                   |M61 = M42 x T1
                                   |M62 = M45 x T4
                                   |M63 = M41 x T2
                                   |P0 = M52 + M61
                                   |P1 = M58 + M59
                                   |P2 = M54 + M62
                                   |P3 = M47 + M50
                                   |P4 = M48 + M56
                                   |P5 = M46 + M51
                                   |P6 = M49 + M60
                                   |P7 = P0 + P1
                                   |P8 = M50 + M53
                                   |P9 = M55 + M63
                                   |P10 = M57 + P4
                                   |P11 = P0 + P3
                                   |P12 = M46 + M48
                                   |P13 = M49 + M51
                                   |P14 = M49 + M62
                                   |P15 = M54 + M59
                                   |P16 = M57 + M61
                                   |P17 = M58 + P2
                                   |P18 = M63 + P5
                                   |P19 = P2 + P3
                                   |P20 = P4 + P6
                                   |P22 = P2 + P7
                                   |P23 = P7 + P8
                                   |P24 = P5 + P7
                                   |P25 = P6 + P10
                                   |P26 = P9 + P11
                                   |P27 = P10 + P18
                                   |P28 = P11 + P25
                                   |P29 = P15 + P20
                                   |W0 = P13 + P22
                                   |W1 = P26 + P29
                                   |W2 = P17 + P28
                                   |W3 = P12 + P22
                                   |W4 = P23 + P27
                                   |W5 = P19 + P24
                                   |W6 = P14 + P23
                                   |W7 = P9 + P16""".stripMargin
}

