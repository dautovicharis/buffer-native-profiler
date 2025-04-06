import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.AfterAll

/**
 * Tests focused on the memory tracking aspects of the buffer profiler.
 * These tests verify that memory usage is correctly tracked for buffers and objects.
 */
class MemoryTrackingTest {

    companion object {
        private val sharedMonitor = NativeBufferMonitor()

        @AfterAll
        @JvmStatic
        fun cleanupAfterAll() {
            sharedMonitor.safeClearTracking()
        }
    }

    private val monitor: NativeBufferMonitor = sharedMonitor
    private var memoryTrackingEnabled = false

    @BeforeEach
    fun setUp() {
        monitor.safeClearTracking()
        memoryTrackingEnabled = monitor.safeGetObjectSize("test") > 0
    }

    @AfterEach
    fun clearTracking() {
        monitor.safeClearTracking()
    }

    /**
     * Helper function to check if memory tracking is enabled.
     * Used with @EnabledIf to conditionally run tests.
     */
    private fun isMemoryTrackingEnabled() = memoryTrackingEnabled

    @Test
    @DisplayName("Object size tracking should work for different types")
    @EnabledIf("isMemoryTrackingEnabled")
    fun `test object size tracking for different types`() {
        // Arrange
        val testString = "This is a test string"
        val testByteArray = ByteArray(1000) { it.toByte() }
        val testIntArray = IntArray(1000) { it }
        val testList = List(1000) { it.toString() }

        // Act
        val stringSize = monitor.safeGetObjectSize(testString)
        val byteArraySize = monitor.safeGetObjectSize(testByteArray)
        val intArraySize = monitor.safeGetObjectSize(testIntArray)
        val listSize = monitor.safeGetObjectSize(testList)

        // Assert
        assertTrue(stringSize > 0, "String size should be greater than 0")
        assertTrue(byteArraySize >= 1000, "ByteArray size should be at least 1000 bytes")
        assertTrue(intArraySize >= 4000, "IntArray size should be at least 4000 bytes (1000 ints * 4 bytes)")
        assertTrue(listSize > 0, "List size should be greater than 0")
        assertTrue(intArraySize > byteArraySize, "IntArray should be larger than ByteArray")
    }

    @Test
    @DisplayName("Buffer memory tracking should work")
    fun `test buffer memory tracking`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(100)

        // Act
        monitor.safeUpdateBufferMetrics(bufferId, 50, 1000L)
        monitor.safeUpdateBufferMetrics(bufferId, 75, 2000L)
        val totalMemory = monitor.safeGetBufferMemoryUsage(bufferId)

        // Assert
        assertEquals(2000L, totalMemory,
            "Buffer memory usage should be 2000 bytes, but was $totalMemory")
    }

    @Test
    @DisplayName("Memory tracking should handle large values")
    fun `test memory tracking with large values`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(1000)
        val largeMemory = 1_000_000_000L

        // Act
        monitor.safeUpdateBufferMetrics(bufferId, 500, largeMemory)
        val bufferMemory = monitor.safeGetBufferMemoryUsage(bufferId)

        // Assert
        assertEquals(largeMemory, bufferMemory,
            "Buffer memory usage should be $largeMemory bytes, but was $bufferMemory")
    }

    @Test
    @DisplayName("Memory tracking should handle multiple buffers")
    fun `test memory tracking with multiple buffers`() {
        // Arrange
        val buffer1 = monitor.safeCreateBuffer(100)
        val buffer2 = monitor.safeCreateBuffer(200)
        val buffer3 = monitor.safeCreateBuffer(300)

        // Act
        monitor.safeUpdateBufferMetrics(buffer1, 50, 1000L)
        monitor.safeUpdateBufferMetrics(buffer2, 100, 2000L)
        monitor.safeUpdateBufferMetrics(buffer3, 150, 3000L)

        val buffer1Memory = monitor.safeGetBufferMemoryUsage(buffer1)
        val buffer2Memory = monitor.safeGetBufferMemoryUsage(buffer2)
        val buffer3Memory = monitor.safeGetBufferMemoryUsage(buffer3)
        val totalBufferMemory = buffer1Memory + buffer2Memory + buffer3Memory

        // Assert
        assertEquals(1000L, buffer1Memory, "Buffer 1 memory usage should be 1000 bytes")
        assertEquals(2000L, buffer2Memory, "Buffer 2 memory usage should be 2000 bytes")
        assertEquals(3000L, buffer3Memory, "Buffer 3 memory usage should be 3000 bytes")
        assertEquals(6000L, totalBufferMemory, "Total buffer memory should be 6000 bytes")
    }

    @Test
    @DisplayName("Memory tracking should be reset when tracking is cleared")
    fun `test memory tracking reset when clearing tracking`() {
        // Arrange
        val bufferId = monitor.safeCreateBuffer(100)
        monitor.safeUpdateBufferMetrics(bufferId, 50, 5000L)
        assertEquals(5000L, monitor.safeGetBufferMemoryUsage(bufferId),
            "Buffer memory usage should be 5000 bytes")

        // Act
        monitor.safeClearTracking()
        val memoryAfterClear = monitor.safeGetBufferMemoryUsage(bufferId)
        val totalMemory = monitor.safeGetTotalTrackedMemory()

        // Assert
        assertEquals(0L, memoryAfterClear,
            "Buffer memory usage should be 0 bytes after clearing tracking")
        assertEquals(0L, totalMemory,
            "Total tracked memory should be 0 bytes after clearing tracking")
    }
}
