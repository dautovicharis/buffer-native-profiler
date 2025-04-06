# Simulation Server

Server that runs buffer simulations and provides real-time metrics for the Buffer Native Profiler project.

## Overview

The Simulation Server is responsible for:
- Running buffer performance simulations
- Collecting buffer metrics using the native profiler
- Broadcasting metrics via WebSockets
- Serving a web interface for monitoring buffer performance

## Requirements

- JDK 17 or higher
- Gradle 8.0+ (or use the included Gradle wrapper)

## Running the Server

```bash
# From the project root
./gradlew runSimulation
```

The server will start on port 8080 by default, or you can specify a custom port using the PORT environment variable.
Url : http://localhost:8080/index.html

## Project Structure

The server is built using Clean Architecture principles:
- **Domain**: Core business logic and entities
- **Data**: Implementation of repositories and data sources
- **Server**: WebSocket server
- **DI**: Dependency injection to wire everything together

## Integration

This server works with the Buffer Native Profiler project's React dashboard for visualization. The dashboard connects to this server via WebSockets to display real-time buffer metrics.

## Tech Stack

- [Kotlin](https://kotlinlang.org/)
- [Ktor](https://ktor.io/) — framework for building asynchronous servers
- [Netty](https://netty.io/) — high-performance networking engine
- [Gradle](https://gradle.org/)
- WebSockets