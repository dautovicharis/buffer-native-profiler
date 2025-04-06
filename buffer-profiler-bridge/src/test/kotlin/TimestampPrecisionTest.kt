import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.ConcurrentHashMap


/**
 * Tests specifically focused on timestamp precision issues.
 * These tests verify that buffer IDs are unique even when created in rapid succession.
 */

class TimestampPrecisionTest {

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
    fun `test buffer IDs are unique when created rapidly`() {
        // Arrange
        val bufferIds = mutableSetOf<Long>()

        // Act
        repeat(1000) {
            val bufferId = monitor.safeCreateBuffer(10)
            bufferIds.add(bufferId)
        }

        // Assert
        assertEquals(1000, bufferIds.size,
            "All 1000 buffer IDs should be unique, but only ${bufferIds.size} were unique")
    }

    @RepeatedTest(5)
    fun `test buffer IDs are unique when created concurrently`() {
        // Arrange
        val bufferIds = ConcurrentHashMap.newKeySet<Long>()
        val threadCount = 10
        val buffersPerThread = 100

        val threads = List(threadCount) { _ ->
            Thread {
                repeat(buffersPerThread) {
                    val bufferId = monitor.safeCreateBuffer(10)
                    bufferIds.add(bufferId)
                }
            }
        }

        // Act
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // Assert
        assertEquals(threadCount * buffersPerThread, bufferIds.size,
            "All ${threadCount * buffersPerThread} buffer IDs should be unique, but only ${bufferIds.size} were unique")
    }
}
