import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

/**
 * Monitors native buffer operations and memory usage.
 * Provides a bridge between Kotlin and C++ native code for buffer profiling.
 */
internal class NativeBufferMonitor {
    private companion object {
        private const val LIBRARY_NAME = "buffer-profiler-1.0.0"

        // Native initialization method
        @JvmStatic
        private external fun initialize(): Boolean

        // Buffer operation methods
        @JvmStatic
        private external fun createBuffer(capacity: Int): Long

        @JvmStatic
        private external fun updateBufferMetrics(bufferId: Long, size: Int, memoryUsage: Long)

        // Suspension tracking methods
        @JvmStatic
        private external fun recordSuspension(
            threadId: Long,
            threadName: String,
            bufferId: Long,
            bufferSize: Int,
            bufferCapacity: Int
        )

        // Statistics and reporting methods
        @JvmStatic
        private external fun getTotalSuspensions(): Int

        @JvmStatic
        private external fun recordEmission(bufferId: Long)

        @JvmStatic
        private external fun recordConsumption(bufferId: Long)

        @JvmStatic
        private external fun getTotalEmissions(): Int

        @JvmStatic
        private external fun getTotalConsumptions(): Int

        @JvmStatic
        private external fun getBufferEmissions(bufferId: Long): Int

        @JvmStatic
        private external fun getBufferConsumptions(bufferId: Long): Int

        @JvmStatic
        private external fun getBufferSuspensionCount(bufferId: Long): Int

        // Memory monitoring methods
        @JvmStatic
        private external fun getObjectSize(obj: Any): Long

        @JvmStatic
        private external fun getBufferSize(bufferId: Long): Int

        @JvmStatic
        private external fun getBufferMemoryUsage(bufferId: Long): Long

        @JvmStatic
        private external fun getTotalTrackedMemory(): Long

        // Reset methods
        @JvmStatic
        private external fun clearTracking()
    }

    private var libraryLoaded = false
    private var memoryMonitoringEnabled = false

    // Load the native library
    init {
        try {
            loadNativeLibrary()
            libraryLoaded = true
            memoryMonitoringEnabled = initialize()
            println("Native components initialized with memory monitoring: $memoryMonitoringEnabled")
        } catch (e: Exception) {
            println("Failed to initialize native buffer monitor: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Attempts to load the native library from various locations in order:
     * 1. System library path
     * 2. JAR resources
     * 3. Resources directory
     * 4. Common build directories
     */
    private fun loadNativeLibrary() {
        // Try the standard way (system library path) first
        try {
            System.loadLibrary(LIBRARY_NAME)
            println("Native library loaded from system path")
            return
        } catch (e: UnsatisfiedLinkError) {
            println("Could not load from system path: ${e.message}")
        }

        // Get OS-specific library naming
        val osName = System.getProperty("os.name").lowercase()
        val libExtension = when {
            osName.contains("windows") -> "dll"
            osName.contains("mac") -> "dylib"
            else -> "so"
        }

        // Try to extract from JAR resources and load
        tryLoadFromJar(LIBRARY_NAME, libExtension)
            ?.let { return }

        // Try from resources directory
        tryLoadFromResourcesDir(LIBRARY_NAME, libExtension)
            ?.let { return }

        // Try from current directory and build directories
        if (tryLoadFromBuildDirs(LIBRARY_NAME, libExtension)) {
            return
        }

        throw UnsatisfiedLinkError("Could not find native library in any location")
    }

    /**
     * Tries to load the library from the JAR resources.
     * Returns true if the library is successfully loaded, false otherwise.
     */
    private fun tryLoadFromJar(libraryName: String, libExtension: String): Boolean? {
        try {
            val resourcePath = "/${libraryName}.${libExtension}"
            javaClass.getResourceAsStream(resourcePath)?.use { inputStream ->
                println("Found library in resources: $resourcePath")
                val tempFile = File.createTempFile(libraryName, ".${libExtension}")
                tempFile.deleteOnExit()

                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                try {
                    System.load(tempFile.absolutePath)
                    return true
                } catch (e: UnsatisfiedLinkError) {
                    if (e.message?.contains("GLIBCXX") == true) {
                        println("C++ standard library version mismatch: ${e.message}")
                        return null
                    }
                    throw e
                }
            }
        } catch (e: Exception) {
            println("Error extracting library from JAR: ${e.message}")
        }
        return null
    }

    /**
     * Tries to load the library from the resources directory.
     * Returns true if the library is successfully loaded, false otherwise.
     */
    private fun tryLoadFromResourcesDir(libraryName: String, libExtension: String): Boolean? {
        try {
            val currentDir = System.getProperty("user.dir")
            val resourcesDir = Paths.get(currentDir, "src", "main", "resources").toString()
            val libraryPath = "$resourcesDir/${libraryName}.${libExtension}"
            val libraryFile = File(libraryPath)

            if (libraryFile.exists()) {
                System.load(libraryFile.absolutePath)
                println("Loaded library from resources: ${libraryFile.absolutePath}")
                return true
            }
        } catch (e: Exception) {
            println("Error loading from resources: ${e.message}")
        }
        return null
    }

    /**
     * Tries to load the library from common build directories.
     * Returns true if the library is successfully loaded, false otherwise.
     */
    private fun tryLoadFromBuildDirs(libraryName: String, libExtension: String): Boolean {
        val currentDir = System.getProperty("user.dir")
        val possibleLocations = listOf(
            "src/main/cpp/build",
            "src/main/cpp",
            "build/libs",
            "."
        ).map { path ->
            File("$currentDir/$path/${libraryName}.${libExtension}")
        }

        for (location in possibleLocations) {
            if (location.exists()) {
                try {
                    System.load(location.absolutePath)
                    println("Loaded library from: ${location.absolutePath}")
                    return true
                } catch (e: UnsatisfiedLinkError) {
                    println("Failed to load from ${location.absolutePath}: ${e.message}")
                }
            }
        }
        return false
    }

    /**
     * Centralized wrapper for all native library calls.
     * Handles error checking and reporting in one place.
     *
     * @param defaultValue The default value to return if the library is not loaded or an error occurs
     * @param block The native function call to execute
     * @return The result of the native call or the default value if it fails
     */
    private inline fun <T> withNativeLibrary(defaultValue: T, block: () -> T): T {
        if (!libraryLoaded) return defaultValue

        return try {
            block()
        } catch (e: UnsatisfiedLinkError) {
            println("Error in native call: ${e.message}")
            libraryLoaded = false
            defaultValue
        } catch (e: Exception) {
            println("Unexpected error in native call: ${e.message}")
            defaultValue
        }
    }

    /**
     * Creates a buffer with the specified capacity.
     * @return Buffer ID or timestamp if native library is not available
     */
    internal fun safeCreateBuffer(capacity: Int): Long {
        return withNativeLibrary(System.nanoTime()) { createBuffer(capacity) }
    }

    /**
     * Updates metrics for the specified buffer.
     */
    internal fun safeUpdateBufferMetrics(bufferId: Long, size: Int, memoryUsage: Long) {
        withNativeLibrary(Unit) {
            updateBufferMetrics(
                bufferId = bufferId,
                size = size,
                memoryUsage = memoryUsage
            )
        }
    }

    /**
     * Records a suspension event for the specified buffer.
     * Uses current thread information by default.
     */
    internal fun safeRecordSuspension(
        threadId: Long = Thread.currentThread().id,
        threadName: String = Thread.currentThread().name,
        bufferId: Long,
        bufferSize: Int, bufferCapacity: Int
    ) {
        withNativeLibrary(Unit) {
            recordSuspension(
                threadId = threadId,
                threadName = threadName,
                bufferId = bufferId,
                bufferSize = bufferSize,
                bufferCapacity = bufferCapacity
            )
        }
    }

    /**
     * Gets the total number of suspension events recorded across all buffers.
     */
    internal fun safeGetTotalSuspensions(): Int {
        return withNativeLibrary(0) { getTotalSuspensions() }
    }

    /**
     * Records an emission event for the specified buffer.
     */
    internal fun safeRecordEmission(bufferId: Long) {
        withNativeLibrary(Unit) { recordEmission(bufferId) }
    }

    /**
     * Records a consumption event for the specified buffer.
     */
    internal fun safeRecordConsumption(bufferId: Long) {
        withNativeLibrary(Unit) { recordConsumption(bufferId) }
    }

    /**
     * Gets the total number of emissions recorded.
     */
    internal fun safeGetTotalEmissions(): Int {
        return withNativeLibrary(0) { getTotalEmissions() }
    }

    /**
     * Gets the total number of consumptions recorded.
     */
    internal fun safeGetTotalConsumptions(): Int {
        return withNativeLibrary(0) { getTotalConsumptions() }
    }

    /**
     * Gets the number of emissions for a specific buffer.
     */
    internal fun safeGetBufferEmissions(bufferId: Long): Int {
        return withNativeLibrary(0) { getBufferEmissions(bufferId) }
    }

    /**
     * Gets the number of consumptions for a specific buffer.
     */
    internal fun safeGetBufferConsumptions(bufferId: Long): Int {
        return withNativeLibrary(0) { getBufferConsumptions(bufferId) }
    }

    /**
     * Gets the number of suspensions for a specific buffer.
     */
    internal fun safeGetBufferSuspensionCount(bufferId: Long): Int {
        return withNativeLibrary(0) { getBufferSuspensionCount(bufferId) }
    }

    /**
     * Clears all tracking data in the native library.
     */
    internal fun safeClearTracking() {
        withNativeLibrary(Unit) { clearTracking() }
    }

    /**
     * Gets the memory size of an object using JVMTI.
     * Returns 0 if the object is null, memory monitoring is disabled,
     * or if the native library is not available.
     */
    internal fun safeGetObjectSize(obj: Any?): Long {
        if (obj == null) return 0
        if (!memoryMonitoringEnabled) return 0

        return withNativeLibrary(0L) {
            val size = getObjectSize(obj)
            // If JVMTI returns 0 (not available), don't track this object
            if (size == 0L) {
                println("Cannot measure object size, memory tracking disabled")
                memoryMonitoringEnabled = false
            }
            size
        }
    }

    /**
     * Gets the total memory being tracked by the native library.
     */
    internal fun safeGetTotalTrackedMemory(): Long {
        return withNativeLibrary(0L) { getTotalTrackedMemory() }
    }

    /**
     * Gets the size of a specific buffer.
     */
    internal fun safeGetBufferSize(bufferId: Long): Int {
        return withNativeLibrary(0) { getBufferSize(bufferId) }
    }

    /**
     * Gets the memory usage of a specific buffer.
     */
    internal fun safeGetBufferMemoryUsage(bufferId: Long): Long {
        return withNativeLibrary(0L) { getBufferMemoryUsage(bufferId) }
    }
}
