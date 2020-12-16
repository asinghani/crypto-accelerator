# SPDX-FileCopyrightText: 2020 Anish Singhani
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# SPDX-License-Identifier: Apache-2.0
TARGET=all

SCALA_SOURCES=$(shell find . -name "*.scala")
AES_IDENT="AES128/256 Core"
SHA_IDENT="SHA256 Core"

.PHONY: build
build: build/top.v
build/top.v: $(SCALA_SOURCES)
	mkdir -p build
	sbt --supershell=never 'runMain main.GenerateVerilog $(AES_IDENT) $(SHA_IDENT)'

.PHONY: test
test:
	sbt --supershell=never "test:runMain main.Main $(TARGET)"

.PHONY: synth-aes
synth-aes: build/top.v
	yosys -p 'read_verilog build/top.v; synth -top AesWishbone'

.PHONY: synth-sha
synth-sha: build/top.v
	yosys -p 'read_verilog build/top.v; synth -top Sha256Wishbone'

.PHONY: synth-all
synth-all: build/top.v
	yosys -p 'read_verilog build/top.v; synth -top AcceleratorTop'

clean:
	-rm -r test_run_dir
	-rm -r build
	-rm -r test_build


