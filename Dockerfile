# Build stage - using an older Ubuntu for compatibility
FROM ubuntu:20.04 as build

# Set environment variables to avoid interactive prompts during installation
ENV DEBIAN_FRONTEND=noninteractive

# Install required build tools and JDK (only what's needed)
RUN apt-get update && apt-get install -y \
    g++ \
    build-essential \
    openjdk-17-jdk \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

WORKDIR /app

# Copy gradle files first for better layer caching
COPY gradle gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy all source code
COPY buffer-profiler buffer-profiler/
COPY buffer-profiler-bridge buffer-profiler-bridge/
COPY simulation-server simulation-server/

# Compile the native library
RUN mkdir -p buffer-profiler/src/main/resources && \
    cd buffer-profiler/src/main/cpp && \
    g++ -shared -fPIC -std=c++11 -O2 -static-libstdc++ -o buffer-profiler-1.0.0.so -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" *.cpp && \
    cp buffer-profiler-1.0.0.so ../resources/

# Copy the native library to the bridge module
RUN mkdir -p buffer-profiler-bridge/src/main/resources && \
    cp buffer-profiler/src/main/resources/buffer-profiler-1.0.0.so buffer-profiler-bridge/src/main/resources/

# Build the application using the project's Gradle wrapper
RUN ./gradlew :simulation-server:buildFatJar --no-daemon

# Runtime stage - using a minimal JRE image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# Copy the built JAR file from the build stage
COPY --from=build /app/simulation-server/build/libs/buffer-profiler-fat.jar app.jar
# Copy the native library
COPY --from=build /app/buffer-profiler/src/main/resources/buffer-profiler-1.0.0.so .

# Expose the port (will be overridden by Render's PORT env var)
EXPOSE 8080

# Command to run the application with explicit library path
CMD ["java", "-Djava.library.path=/app", "-jar", "app.jar"]
