# Buffer Profiler Bridge

A Kotlin interface that provides a bridge between JVM applications and the native C++ buffer profiling library.

## Features

- **Native Memory Monitoring**: Track memory usage of buffers with JNI integration
- **Buffer Operation Tracking**: Monitor buffer creations, emissions, and consumptions
- **Suspension Monitoring**: Track backpressure events in buffer operations
- **Thread-Safe API**: Safe wrappers around native methods

## Tests
```bash
./gradlew :test
# Note: If you are running this from the project root, use:
./gradlew :buffer-profiler-bridge:test
```
