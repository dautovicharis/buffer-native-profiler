plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "buffer-native-profiler"

// Include all subprojects
include("buffer-profiler")
include("buffer-profiler-bridge")
include("simulation-server")

// Set project directories for each module
project(":buffer-profiler").projectDir = file("buffer-profiler")
project(":buffer-profiler-bridge").projectDir = file("buffer-profiler-bridge")
project(":simulation-server").projectDir = file("simulation-server")
