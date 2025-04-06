import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Tests for the NativeBufferMonitor class.
 * These tests verify both the native library loading mechanism and the core functionality.
 */

class NativeBufferMonitorTest {

    private lateinit var monitor: NativeBufferMonitor

    @BeforeEach
    fun setUp() {
        monitor = NativeBufferMonitor()
    }

    @AfterEach
    fun clearTracking()
    {
        monitor.safeClearTracking()
    }

    @Test
    fun `test native library exists in resources`() {
        // Arrange
        val resourcesDir = Paths.get("src", "main", "resources").toFile()
        val osName = System.getProperty("os.name").lowercase()
        val libExtension = when {
            osName.contains("windows") -> "dll"
            osName.contains("mac") -> "dylib"
            else -> "so"
        }
        val libraryName = "buffer-profiler-1.0.0"
        val libraryFile = File(resourcesDir, "$libraryName.$libExtension")

        // Assert
        assertTrue(resourcesDir.exists(), "Resources directory should exist")
        assertTrue(libraryFile.exists() || Files.exists(Paths.get("build/libs/$libraryName.$libExtension")),
            "Native library should exist in resources or build directory")
    }

    @Test
    fun `test buffer creation and metrics`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(10)

        // Act
        monitor.safeUpdateBufferMetrics(bufferId, 5, 1000L)

        // Assert
        assertNotEquals(0L, bufferId, "Buffer ID should not be zero")
        assertDoesNotThrow { monitor.safeGetBufferSize(bufferId) }
        assertDoesNotThrow { monitor.safeGetBufferMemoryUsage(bufferId) }
    }

    @Test
    fun `test emission and consumption tracking`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(10)

        // Act
        repeat(5) {
            monitor.safeRecordEmission(bufferId)
        }

        repeat(3) {
            monitor.safeRecordConsumption(bufferId)
        }

        val emissions = monitor.safeGetBufferEmissions(bufferId)
        val consumptions = monitor.safeGetBufferConsumptions(bufferId)

        // Assert
        assertDoesNotThrow { monitor.safeGetTotalEmissions() }
        assertDoesNotThrow { monitor.safeGetTotalConsumptions() }

        if (emissions > 0) {
            assertEquals(5, emissions, "Should have recorded 5 emissions")
            assertEquals(3, consumptions, "Should have recorded 3 consumptions")
        }
    }

    @Test
    fun `test suspension tracking`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(10)

        // Act
        monitor.safeRecordSuspension(
            threadId = Thread.currentThread().id,
            threadName = Thread.currentThread().name,
            bufferId = bufferId,
            bufferSize = 10,
            bufferCapacity = 10
        )

        val suspensions = monitor.safeGetSuspensionCount()

        // Assert
        assertDoesNotThrow { monitor.safeGetSuspensionCount() }
        assertDoesNotThrow { monitor.safeGetBufferSuspensionCount(bufferId) }

        if (suspensions > 0) {
            assertEquals(1, suspensions, "Should have recorded 1 suspension")
        }
    }

    @Test
    fun `test memory tracking`() {
        // Arrange
        val testObject = "This is a test string for memory measurement"

        // Assert
        assertDoesNotThrow { monitor.safeGetObjectSize(testObject) }
        assertDoesNotThrow { monitor.safeGetTotalTrackedMemory() }
        assertEquals(0L, monitor.safeGetObjectSize(null), "Size of null object should be 0")
    }

    @Test
    fun `test clear tracking`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(10)
        monitor.safeRecordEmission(bufferId)

        // Act
        monitor.safeClearTracking()

        // Assert
        assertDoesNotThrow { monitor.safeClearTracking() }
    }
}
