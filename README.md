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