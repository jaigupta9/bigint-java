# BigInt Arithmetic Library (Java)

A custom arbitrary-precision integer library implemented **from scratch in Java**, without using `java.math.BigInteger`.

## Features
- Supports integers of unlimited size
- Handles positive and negative numbers
- Implemented using a **doubly linked list** with base-10000 blocks

## Supported Operations
- Addition
- Subtraction
- Multiplication
- Division (binary search based)
- Power (fast exponentiation)
- Modulus
- GCD (Euclidean algorithm)
- LCM
- Absolute and signed comparison

## Design Highlights
- Base-10000 digit chunking for efficiency
- Manual carry and borrow handling
- Binary-search division
- Recursive fast exponentiation
- No built-in big integer libraries used

## Usage
Compile and run:

```bash
javac src/*.java
java src.Main
