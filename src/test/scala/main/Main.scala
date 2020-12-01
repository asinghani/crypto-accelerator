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
package main

import tests._
import chisel3._
import chisel3.internal.LegacyModule
import sha256._
import sha._
import aes128._
import aes._

object Main extends App {
    val EXEC_CONFIG = Array(
        "--backend-name", "treadle",
        "--generate-vcd-output", "on",
        "--target-dir", "test_build",
        "-tn", "test_build",
        "--no-dce"
    )

    /**
     * Format:
     * "name" -> (() => new Dut,
     *            (dut: LegacyModule) => new DutTest(dut.asInstanceOf[Dut])),
     */
    val TESTS = Map(
        "messageschedulearray_test" -> (() => new MessageScheduleArray(),
            (dut: LegacyModule) => new MessageScheduleArrayTest(dut.asInstanceOf[MessageScheduleArray])),
        "compressionfunction_test" -> (() => new CompressionFunction(),
            (dut: LegacyModule) => new CompressionFunctionTest(dut.asInstanceOf[CompressionFunction])),
        "sha256accel_test" -> (() => new Sha256Accel(),
            (dut: LegacyModule) => new Sha256AccelTest(dut.asInstanceOf[Sha256Accel])),
        "sha256wishbone_test" -> (() => new Sha256Wishbone(),
            (dut: LegacyModule) => new Sha256WishboneTest(dut.asInstanceOf[Sha256Wishbone])),

        "aes128encrypt_test" -> (() => new Aes128Combined(),
            (dut: LegacyModule) => new Aes128EncryptTest(dut.asInstanceOf[Aes128Combined])),
        "aes128decrypt_test" -> (() => new Aes128Combined(),
            (dut: LegacyModule) => new Aes128DecryptTest(dut.asInstanceOf[Aes128Combined])),
        "aes128wishbone_test" -> (() => new Aes128Wishbone(LIMIT_KEY_LENGTH=false),
            (dut: LegacyModule) => new Aes128WishboneTest(dut.asInstanceOf[Aes128Wishbone])),
        "aes56wishbone_test" -> (() => new Aes56Wishbone(),
            (dut: LegacyModule) => new Aes56WishboneTest(dut.asInstanceOf[Aes56Wishbone])),

        "optimizedsbox_test" -> (() => new OptimizedSboxTestHarness(),
            (dut: LegacyModule) => new OptimizedSboxTest(dut.asInstanceOf[OptimizedSboxTestHarness])),

    )

    var passed_tests = Array[String]()
    var failed_tests = Array[String]()

    // Args should be list of tests to run
    val tests_to_run = if (args(0) == "all") { TESTS.keys.toArray } else { args }
    for (test_name <- tests_to_run) {
        if (TESTS.contains(test_name)) {
            val (dut_func, tester_func) = TESTS(test_name)
            println("=" * 60)
            println("Running test "+test_name)
            println("=" * 60)

            val passed = iotesters.Driver.execute(EXEC_CONFIG, dut_func) { tester_func }

            if (passed) {
                println(Console.GREEN + "PASSED TEST " + test_name + Console.RESET)
                println()
                passed_tests = passed_tests :+ test_name
            } else {
                println(Console.RED_B + Console.BLUE + "FAILED TEST " + test_name + Console.RESET)
                println()
                failed_tests = failed_tests :+ test_name
            }

        } else {
            println(Console.YELLOW + "Warning: skipping unknown test " + test_name + Console.RESET)
            println()
        }
    }

    if (passed_tests.length != 0) {
        println(Console.GREEN + "PASSED TESTS: " + passed_tests.mkString(", ") + Console.RESET)
    }

    if (failed_tests.length != 0) {
        println(Console.RED_B + Console.BLUE + "FAILED TESTS: " + failed_tests.mkString(", ") + Console.RESET)
    } else {
        println(Console.GREEN + "PASSED ALL TESTS!" + Console.RESET)
    }

}