plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "3.1.1"
    application
}

group = "com.harisdautovic.profiler"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Ktor
    implementation("io.ktor:ktor-server-core:3.1.1")
    implementation("io.ktor:ktor-server-netty:3.1.1")
    implementation("io.ktor:ktor-server-html-builder:3.1.1")
    implementation("io.ktor:ktor-server-websockets:3.1.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.1")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // Use the buffer-profiler-bridge module
    implementation(project(":buffer-profiler-bridge"))
}

application {
    mainClass.set("MainKt")
}

// Configure the Ktor plugin to create a fat JAR
ktor {
    fatJar {
        archiveFileName.set("buffer-profiler-fat.jar")
    }
}

kotlin {
    jvmToolchain(17)
}

