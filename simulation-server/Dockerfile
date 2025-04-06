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
COPY ../kotln-app/gradle gradle/
COPY ../kotln-app/gradlew build.gradle.kts settings.gradle.kts ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy all source code
COPY ../kotln-app/src src/

# Compile the native library for Linux with static linking of libstdc++
RUN mkdir -p src/main/resources && \
    cd src/main/cpp && \
    g++ -shared -fPIC -std=c++11 -O2 -static-libstdc++ -o libbuffermonitor.so -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" *.cpp && \
    cp libbuffermonitor.so ../resources/

# Build the application using the project's Gradle wrapper
RUN ./gradlew buildFatJar --no-daemon

# Runtime stage - using a minimal JRE image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/buffer-profiler-fat.jar app.jar

# Expose the port (will be overridden by Render's PORT env var)
EXPOSE 8080

# Command to run the application with explicit library path
CMD ["java", "-Djava.library.path=/app", "-jar", "app.jar"]
