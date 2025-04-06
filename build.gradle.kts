// Root project build file
plugins {
    base
    kotlin("jvm") version "2.1.10" apply false
}

tasks.register("compileNativeLibrary") {
    group = "build"
    description = "Compiles the native buffer profiler library and copies it to all necessary locations"

    dependsOn(":buffer-profiler:compileBufferProfiler")

    doLast {
        println("✅ Native library compilation complete")
        println("The library has been copied to:")
        println("  - buffer-profiler/src/main/resources/")
        println("  - buffer-profiler-bridge/src/main/resources/")
    }
}

// Define a task to build all modules
tasks.register("buildAll") {
    group = "build"
    description = "Builds all modules in the project"

    dependsOn(
        "compileNativeLibrary",
        ":buffer-profiler:build",
        ":buffer-profiler-bridge:build",
        ":simulation-server:build"
    )

    doLast {
        println("✅ All modules built successfully")
    }
}

// Define a task to run the simulation server
tasks.register("runSimulation") {
    group = "application"
    description = "Runs the simulation server"

    dependsOn(":simulation-server:run")
}

// Define a task to install npm dependencies for the React app
tasks.register<Exec>("installReactDeps") {
    group = "application"
    description = "Installs npm dependencies for the React demo application"

    workingDir = file("react-app")

    doFirst {
        println("Installing npm dependencies for React app...")
    }

    commandLine("sh", "-c", "npm install")
}

// Define a task to run the React demo app
tasks.register<Exec>("runReactApp") {
    group = "application"
    description = "Runs the React demo application"

    // Make sure dependencies are installed first
    dependsOn("installReactDeps")

    workingDir = file("react-app")

    // Check if npm is available
    doFirst {
        println("Starting React demo app...")
        println("Opening browser at http://localhost:3000")
    }

    commandLine("sh", "-c", "npm run dev")
}
