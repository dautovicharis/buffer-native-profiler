# Buffer Profiler

A native C++ library for monitoring and profiling buffer performance in JVM applications.

## Overview

Buffer Profiler is a JNI-based native library that provides detailed metrics and tracking for buffer operations.

## Features

- **Buffer Tracking**: Monitor buffer creation, size changes, and memory usage
- **Memory Tracking**: Track memory allocation and usage patterns
- **Suspension Tracking**: Identify thread suspensions due to buffer operations
- **Emission/Consumption Metrics**: Track buffer emission and consumption rates
- **JVM Integration**: Seamless integration with JVM applications via JNI

## Building

The library can be built using CMake or the provided Gradle tasks:

```bash
# Using Gradle
./gradlew compileBufferProfiler
```

## Usage

The native library is accessed through the `NativeBufferMonitor` class in the buffer-profiler-bridge module, which provides a Kotlin interface to the native functionality.

