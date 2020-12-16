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
# Crypto Accelerator

SHA256 and AES128 accelerator in Chisel3.

The accelerator is accessible over wishbone-classic bus. It is optimized for a balance between throughput and area - it includes a high level of parallelism, but the AES/SHA stages are computed sequentially instead of being fully pipelined.

## Build

```
make build/top.v
```

## Usage

TODO

(Note: this implementation has not been verified against side-channel and other attacks and should not be considered cryptographically secure.)

# License

[Apache-2.0](LICENSE)
