# Copyright 2020 Anish Singhani
#
# SPDX-License-Identifier: Apache-2.0
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
TARGET=all

SCALA_SOURCES=$(shell find . -name "*.scala")

.PHONY: build
build: build/top.v
build/top.v: $(SCALA_SOURCES)
	mkdir -p build
	sbt --supershell=never "runMain main.GenerateVerilog"

.PHONY: test
test:
	sbt --supershell=never "test:runMain main.Main $(TARGET)"

.PHONY: synth-aes56
synth-aes56: build/top.v
	yosys -p 'read_verilog build/top.v; synth -top Aes56Wishbone'

.PHONY: synth-aes128
synth-aes128: build/top.v
	yosys -p 'read_verilog build/top.v; synth -top Aes128Wishbone'

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

