plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "simulation-server"

include(":buffer-profiler-bridge")
project(":buffer-profiler-bridge").projectDir = file("../buffer-profiler-bridge")
