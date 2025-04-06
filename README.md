# Buffer Native Profiler

Monitoring and visualizing buffer performance with native JNI integration for precise memory tracking.

- Memory usage tracking
- Buffer utilization statistics
- Suspension/backpressure monitoring
- Emission and consumption rates

The project consists of several modules:

- **buffer-profiler**: Core native profiling library with JNI integration
- **buffer-profiler-bridge**: Kotlin interface to the native profiling library
- **simulation-server**: Server that runs buffer simulations and provides real-time metrics
- **react-app**: Web-based visualization dashboard

## Getting Started

### Prerequisites

- JDK 17 or higher
- C++ compiler (g++ or clang++)
- Node.js and npm (for the React app)
- Gradle 8.0+ (or use the included Gradle wrapper)

### Building the Native Library

The native library needs to be compiled before using the profiler.

```bash
# From the project root
cd buffer-profiler
./gradlew compileBufferProfiler
```

This will:
1. Compile the native C++ library
2. Copy the library to both buffer-profiler and buffer-profiler-bridge resources directories

### Running the Simulation Server

```bash
# From the project root
cd simulation-server
./gradlew runSimulation
```

The server will start on port 8080 (or the port specified in the PORT environment variable).

### Running the React Dashboard

```bash
# From the project root
cd react-app
npm install
npm run dev
```
or 
```bash
# From the project root
cd react-app
./gradlew runReactApp
```

The dashboard will be available at http://localhost:3000

## Project Structure

```
buffer-native-profiler/
├── buffer-profiler/            # Native profiling library
│   ├── src/main/cpp/           # C++ source files
│   └── src/main/resources/     # Compiled native libraries
├── buffer-profiler-bridge/     # Kotlin interface to native library
├── simulation-server/          # Simulation server
└── react-app/                  # React visualization dashboard
```

## Development Tasks

### Compiling the Native Library

```bash
# From the project root
./gradlew compileNativeLibrary
```

This task:
- Compiles the C++ code into a native library
- Copies the library to buffer-profiler/src/main/resources
- Copies the library to buffer-profiler-bridge/src/main/resources

### Building the Entire Project

```bash
# From the project root
./gradlew buildAll
```

### Running the Simulation Server

```bash
# From the project root
./gradlew runSimulation
```

### Installing React Dependencies

```bash
# From the project root
./gradlew installReactDeps
```

### Running the React App

```bash
# From the project root
./gradlew runReactApp
```

Note: This task automatically installs dependencies if needed.

This will:
1. Install React dependencies if needed
2. Start the simulation server in the background
3. Launch the React app in the foreground
4. Open the app in your default browser at http://localhost:3000

## License

This project is licensed under the MIT License - see the LICENSE file for details.
