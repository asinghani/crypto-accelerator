<!---
# SPDX-FileCopyrightText: 2020 Anish Singhani
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
-->
# Cryptography Accelerator

This is a cryptography accelerator hardware core (written using Chisel3) supporting AES128 & AES256 (for encryption) and SHA256 (for hashing). It is optimized for a balance between throughput and area, and is targeted primarily at ASIC applications. The accelerator is designed to be connected to a CPU through the wishbone bus (it has been tested with a [picorv32 CPU](https://github.com/cliffordwolf/picorv32), but should work equally well with any other wishbone-capable system).

This design has been taped out in December 2020 using the [SKY130 process node](https://github.com/google/skywater-pdk). The build configurations and glue logic for the ASIC can be found in [asinghani/crypto-accelerator-chip](https://github.com/asinghani/crypto-accelerator-chip), and the build environment (including the final DRC/LVS-clean & signed-off GDS files) can be found in [asinghani/crypto-accelerator-builds](https://github.com/asinghani/crypto-accelerator-builds)

## Table of Contents

- [Architecture](#architecture)
    - [AES Core](#aes-core)
    - [SHA256 Core](#sha256-core)
- [Build & Integration](#build--integration)
    - [Build Instructions](#build-instructions)
    - [Running Tests](#running-tests)
    - [Module Integration](#module-integration)
- [Memory Map & Software Usage](#memory-map--software-usage)
    - [AES Core](#aes-core-1)
    - [SHA256 Core](#sha256-core-1)
    - [AcceleratorTop](#acceleratortop)
- [License](#license)

## Architecture

### AES Core

The module hierarchy of the design is as follows (includes both full Chisel modules as well as "pseudo-modules" generated using static functions):
```
AesWishbone
└─AesCombined
  ├─ShiftRegister (Holds round keys, provides them to encrypt/decrypt cores)
  │
  ├─RoundKeyComb (Generates the round keys and feeds into the shift register)
  │ └─OptSboxComp
  │
  ├─AesEncrypt
  │ ├─MatrixShiftRows
  │ ├─MatrixMixCols
  │ ├─MatrixXor
  │ └─OptimizedSbox
  │   └─OptSboxComp
  │    
  └─AesDecrypt
    ├─MatrixUnshiftRows
    ├─MatrixUnmixCols
    ├─MatrixXor
    └─OptimizedInvSbox
      └─OptInvSboxComp
```

The round keys are generated in advance (when the main AES key is loaded), so they can be used for both encryption and decryption back to back, without the overhead of reloading or regenerating the key every time.

The components of the encryption and decryption cores are fairly standard. Each round is distributed across 2 clock cycles, meaning that it takes ~20 cycles for an AES128 encrypt/decrypt (of a sincle 16-byte block) or 28 cycles for an AES256 encrypt/decrypt (of a sincle 16-byte block). This is intended to provide a balance between area, throughput, and clock frequency.

The Sbox and Inverse Sbox operations are computed using a special module `OptSboxComp` (and `OptInvSboxComp`), which is a highly logic-reduced version of the Sbox, based on [this paper by Joan Boyar and Rene Peralta](https://eprint.iacr.org/2011/332.pdf). At elaboration time, the functions from the paper are loaded from text and parsed into Chisel logic constructs (this made it trivial to experiment with different variants of optimized Sbox logic functions for utilization and throughput).

### SHA256 Core

The SHA256 implementation is implemented in a fairly straightforward manner - all operations are trivial logic operations, so no special logic reduction is needed outside of what is automatically done at synthesis time. Each 512-bit block takes 64 cycles to process - 16 cycles during which data is fed in, followed by 48 cycles (computation of the rest of the iterations of the hash). In order to reduce the number of cycles to compute the hash, the message schedule array is computed in parallel with the compression function instead of being pre-computed in advance (using a shift register to store previous values of the message schedule array.

## Build & Integration

### Build Instructions

Ensure that [SBT](https://www.scala-sbt.org/) (as well as GNU Make) is installed, as it is required to build the Chisel project.

To build the top-level verilog from the Chisel code (which can then be synthesized by any verilog synthesis tool), run the following command:
```sh
make build/top.v
```
Upon completion this will generate the verilog file at `build/top.v`. This verilog file contains all the modules in the design (`AcceleratorTop`, `AesWishbone`, and `Sha256Wishbone`) as separate modules, so it can be used to integrate the AES core, the SHA core, or both, as required. See the [Module Integration](#module-integration) section for details on how to integrate the module into a larger design.

To test synthesizing the core with [Yosys](http://www.clifford.at/yosys/) (as a way to estimate the gate-level resource utilization), use one of the following commands (they will automatically build the top-level verilog if needed):
```sh
make synth-all # Synthesizes AcceleratorTop, which contains both the AES128/256 and SHA256 cores

make synth-aes # Synthesizes AesWishbone, which is just the AES128/256 core

make synth-sha # Synthesizes Sha256Wishbone, which is just the SHA256 core
```

### Running Tests

Self-checking testbenches are used to verify the functionality of each core. The commands below can be used to invoke the tests. Each test will print a success or failure message, as well as a final summary of test-results after all tests have been run.

Tests:
- `messageschedulearray_test` - Does a basic functionality test of the message schedule array component of the SHA256 encryption core
- `compressionfunction_test` - Tests the SHA256 compression function by using it to generate several full hashes (with various sizes of input data)
- `sha256accel_test` - Tests the full SHA256 accelerator module by using it to generate several hashes (including testing flow interruptions such as cancelling a computation and immediately starting a new one) 
- `sha256wishbone_test` - Tests the SHA256 accelerator in the same way as `sha256accel_test` but solely through the wishbone interface

- `aes128encrypt_test` - Tests the AES128 encryption core using ~200 test cases
- `aes128decrypt_test` - Tests the AES128 decryption core using ~200 test cases

- `aes256encrypt_test` - Tests the AES256 encryption core using ~200 test cases
- `aes256decrypt_test` - Tests the AES256 decryption core using ~200 test cases

- `aes128wishbone_test` - Tests the AES core, accessing it only over wishbone, to test AES128 encryption and AES128 decryption using ~400 test cases
- `aeswishbone_test` - Tests the AES core, accessing it only over wishbone, to test AES128 encryption, AES128 decryption, AES256 encryption, and AES256 decryption using ~800 test cases

- `optimizedsbox_test` - Tests the optimized (logic-reduced) SBox and InvSBox implementations to verify their logical equivalence with the naive (lookup-table) implementation

```sh
# To run all tests at once:
make test

# To run a specific subset of tests:
make test TARGET="(space-separated list of tests to run)"

# For example:
make test TARGET="sha256wishbone_test aeswishbone_test"
```

### Module Integration

The three top-level modules available are: `AesWishbone`, `Sha256Wishbone`, and `AcceleratorTop` (which is just a wrapper/multiplexer for the other 2). Each of them has the following module definition (which is just a standard wishbone bus along with clock and reset):
```verilog
module <AesWishbone / Sha256Wishbone / AcceleratorTop> (
    input         clock,
    input         reset,

    input         io_bus_cyc,
    input         io_bus_stb,
    input         io_bus_we,
    input  [3:0]  io_bus_sel,
    input  [31:0] io_bus_addr,
    input  [31:0] io_bus_data_wr,
    output        io_bus_ack,
    output [31:0] io_bus_data_rd
);
```
(Warning: this implementation has not yet been verified against side-channel attacks and should not be used for production applications.)

## Memory Map & Software Usage

### AES Core

The module name for just the AES core is `AesWishbone` and its memory map (through wishbone) is as follows. All reads and writes should be aligned to 4-byte boundaries.

```
0x00 - R/W - Status/Config
     - Status[0] (R/O) = Decrypt-ready
     - Status[1] (R/O) = Encrypt-ready
     - Status[2] (R/O) = Output valid
     - Status[3] (R/W) = Block Mode (0 = ECB, 1 = CBC)
     - Status[4] (R/W) = AES Mode (0 = AES128, 1 = AES256)

0x04 - W/O - Start encrypt (write 1 to start)
0x08 - W/O - Start decrypt (write 1 to start)
0x0C - W/O - Update key
     - For AES256: shift in the first half of the key, write 2 to this address, then shift in the second half and write 1 to this address
     - For AES128: shift in the key, then write 1 to this reg

0x10 - W/O - Input data shift-in (32 bits at a time, in big-endian order)
0x20 - W/O - IV shift-in (32 bits at a time, in big-endian order)
0x30 - R/O - Output data[127:96]
0x34 - R/O - Output data[95:64]
0x38 - R/O - Output data[63:32]
0x3C - R/O - Output data[31:0]
0x40 - W/O - Key shift-in (32 bits at a time, in big-endian order)
```

Following are steps to set up and use the core from software:
1. Configure the AES mode and block mode by setting bits 3 and 4 of the config register at address `0x00` (the other bits are ignored when writing). The bitmask for AES256 mode is `0x10`, and the bitmask for CBC mode is `0x08`.

2. (AES256 only) Load the top half of the key (the 128 uppermost bits), 32 bits at a time, in big-endian order, by writing them to register `0x40`. After those 4 writes, write `2` to `0x0C` to shift the top half of the key into the round-key generation register.

3. Load the bottom 128-bits of the key (which is just the full key for AES128), 32 bits at a time, in big-endian order, by writing them to register `0x40`. After those 4 writes, write `1` to `0x0C` to start the round-key generation process. This will take ~15 cycles (poll the status register `0x00` and wait until bits 0 and 1 are set, which means that the round-keys have been generated).

4. (CBC mode only) Write the initialization vector, 32 bits at a time, in big-endian order, to register `0x20`.

5. Write the input data (cleartext or ciphertext), 32 bits at a time, in big-endian order, to register `0x10`.

6. Write `1` to `0x04` (to encrypt) or `0x08` (to decrypt). Poll the status register `0x00` and wait until bit 2 is set, which means that the encryption or decryption is finished.

7. Read out the result (cleartext or ciphertext) from `0x30` (`result[127:96]`), `0x34` (`result[95:64]`), `0x38` (`result[63:32]`), and `0x3C` (`result[31:0]`).

8. For additional 16-byte blocks (both ECB and CBC), repeat steps 5-7 until all blocks have been completed. To re-initialize CBC (start fresh from the initialization vector), start at step 4. To re-initialize the entire core (including a new key), start at step 1.


### SHA256 Core

The module name for just the SHA core is `Sha256Wishbone` and its memory map (through wishbone) is as follows. All reads and writes should be aligned to 4-byte boundaries.

```
0x00 - R/W - Status/Config
     - Status[0] (W/O) = Start of hash (write 1 here to start a new hash)
     - Status[1] (R/O) = Input data ready
     - Status[2] (R/O) = Output hash valid

0x04 - W/O - Input data shift-in (32 bits at a time, in big-endian order)
0x10 - R/O - Output hash[255:224]
0x14 - R/O - Output hash[223:192]
0x18 - R/O - Output hash[191:160]
0x1C - R/O - Output hash[159:128]
0x20 - R/O - Output hash[127:96]
0x24 - R/O - Output hash[95:64]
0x28 - R/O - Output hash[63:32]
0x2C - R/O - Output hash[31:0]
```

Following are steps to set up and use the core from software:
1. Write `1` to the config register `0x00` to start a new hash.

2. Write the input data to be hashed, 32 bits at a time, in big-endian order (no limit on the size of the input but it must be pre-padded to a multiple of 512 bits). Before each write, poll the status register `0x00` and ensure that bit 1 is set (otherwise, wait until it is set before pushing in more data).

3. After all the input data has been written, poll the status register `0x00` and wait until bit 2 is set, which indicates that the output hash is valid and can be read out from `0x10`, `0x14`, `0x18`, `0x1C`, `0x20`, `0x24`, `0x28`, `0x2C`

### AcceleratorTop

The module that contains both the AES and SHA cores is `AcceleratorTop`. The AES core is at the base address while the SHA core is placed at an offset of `0x10000` (each address in the SHA core when using it inside the `AcceleratorTop` is hence `0x10000 + (address from the SHA core memory map)`). Both cores can run simultaneously because there is no resource-sharing between them (other than the bus itself).

## License

[Apache-2.0](LICENSE)
