plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.harisdautovic"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":buffer-profiler-bridge"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.register("cleanBuild") {
    description = "Cleans the build directory"
    group = "build"

    doLast {
        val buildDir = file("src/main/cpp/build")
        if (buildDir.exists()) {
            buildDir.deleteRecursively()
        }
    }
}

tasks.register("compileBufferProfiler") {
    description = "Compiles the native C++ library for JNI integration"
    group = "build"

    // Clean before compiling
    dependsOn("cleanBuild")

    // Source and output directories
    val cppDir = file("src/main/cpp")
    val buildDir = file("${cppDir}/build")

    // Find Java home
    val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
    ?: error("Could not find Java home. Please set JAVA_HOME environment variable.")

    // OS-specific configuration
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")

    doLast {
        println("Using Java home: $javaHome")

        // Check if CMake is available
        val cmakeAvailable = try {
            val process = ProcessBuilder(if (isWindows) "where" else "which", "cmake")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }

        if (!cmakeAvailable) {
            throw GradleException("❌ CMake not found. Please install cmake to compile the native library.")
        }

        // Create build directory
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }

        // Run CMake configure
        val configureProcess = ProcessBuilder(
            if (isWindows) "cmd" else "sh",
            if (isWindows) "/c" else "-c",
            "cd ${buildDir.absolutePath} && cmake -DCMAKE_BUILD_TYPE=Release -DJAVA_HOME=\"$javaHome\" -DPROJECT_VERSION=\"${project.version}\" -DARTIFACT_ID=\"${project.name}\" .."
        ).redirectErrorStream(true).start()

        val configureOutput = configureProcess.inputStream.bufferedReader().readText()
        val configureExitCode = configureProcess.waitFor()

        if (configureExitCode != 0) {
            println("CMake configuration failed:\n$configureOutput")
            throw GradleException("CMake configuration failed")
        }

        // Run CMake build
        val buildProcess = ProcessBuilder(
            if (isWindows) "cmd" else "sh",
            if (isWindows) "/c" else "-c",
            "cd ${buildDir.absolutePath} && cmake --build . --config Release"
        ).redirectErrorStream(true).start()

        val buildOutput = buildProcess.inputStream.bufferedReader().readText()
        val buildExitCode = buildProcess.waitFor()

        if (buildExitCode != 0) {
            println("CMake build failed:\n$buildOutput")
            throw GradleException("CMake build failed")
        }

        println("✅ Library automatically copied to resource directories by CMake")
    }
}
