import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests focused on the concurrency aspects of the buffer operations.
 * These tests verify that buffer operations are thread-safe and work correctly under concurrent access.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BufferConcurrencyTest {

    private val monitor = NativeBufferMonitor()

    @AfterAll
    fun cleanupAfterAll() {
        monitor.safeClearTracking()
    }

    @BeforeEach
    fun setUp() {
        monitor.safeClearTracking()
    }

    @AfterEach
    fun clearTracking() {
        monitor.safeClearTracking()
    }

    @Test
    @DisplayName("Multiple coroutines should be able to create buffers concurrently")
    fun `test concurrent buffer creation`() = runBlocking {
        val threadCount = 10
        val buffersPerThread = 100
        val bufferIds = ConcurrentHashMap.newKeySet<Long>()

        coroutineScope {
            repeat(threadCount) {
                launch(Dispatchers.Default) {
                    repeat(buffersPerThread) {
                        val bufferId = monitor.safeCreateBuffer(10)
                        bufferIds.add(bufferId)
                    }
                }
            }
        }

        assertEquals(
            threadCount * buffersPerThread, bufferIds.size,
            "Expected ${threadCount * buffersPerThread} unique buffer IDs, but got ${bufferIds.size}"
        )
    }

    @Test
    @DisplayName("Multiple coroutines should be able to emit and consume from the same buffer")
    fun `test concurrent emission and consumption`() = runBlocking {
        val bufferId = monitor.safeCreateBuffer(1000)
        val coroutineCount = 5
        val operationsPerCoroutine = 1000
        val emissionCount = AtomicInteger(0)
        val consumptionCount = AtomicInteger(0)

        coroutineScope {
            repeat(coroutineCount) {
                launch(Dispatchers.Default) {
                    repeat(operationsPerCoroutine) {
                        monitor.safeRecordEmission(bufferId)
                        emissionCount.incrementAndGet()
                    }
                }
                launch(Dispatchers.Default) {
                    repeat(operationsPerCoroutine) {
                        monitor.safeRecordConsumption(bufferId)
                        consumptionCount.incrementAndGet()
                    }
                }
            }
        }

        val expected = coroutineCount * operationsPerCoroutine
        assertEquals(expected, emissionCount.get(), "Expected $expected emissions, got ${emissionCount.get()}")
        assertEquals(expected, consumptionCount.get(), "Expected $expected consumptions, got ${consumptionCount.get()}")

        val totalEmissions = monitor.safeGetTotalEmissions()
        val totalConsumptions = monitor.safeGetTotalConsumptions()
        val bufferEmissions = monitor.safeGetBufferEmissions(bufferId)
        val bufferConsumptions = monitor.safeGetBufferConsumptions(bufferId)

        assertEquals(expected, bufferEmissions, "Expected $expected buffer emissions, got $bufferEmissions")
        assertEquals(expected, bufferConsumptions, "Expected $expected buffer consumptions, got $bufferConsumptions")
        assertTrue(totalEmissions >= expected, "Expected at least $expected total emissions, got $totalEmissions")
        assertTrue(totalConsumptions >= expected, "Expected at least $expected total consumptions, got $totalConsumptions")
    }

    @Test
    @DisplayName("Multiple coroutines should be able to update metrics for the same buffer")
    fun `test concurrent metrics updates`() = runBlocking {
        val bufferId = monitor.safeCreateBuffer(1000)
        val coroutineCount = 5
        val updatesPerCoroutine = 1000

        coroutineScope {
            repeat(coroutineCount) { threadIndex ->
                launch(Dispatchers.Default) {
                    repeat(updatesPerCoroutine) { i ->
                        val size = (threadIndex * updatesPerCoroutine + i) % 1000
                        val memory = (threadIndex * updatesPerCoroutine + i) * 100L
                        monitor.safeUpdateBufferMetrics(bufferId, size, memory)
                    }
                }
            }
        }

        val size = monitor.safeGetBufferSize(bufferId)
        val memory = monitor.safeGetBufferMemoryUsage(bufferId)

        assertTrue(size in 0..999, "Expected buffer size between 0 and 999, got $size")
        assertEquals(0, memory % 100, "Expected memory usage to be a multiple of 100, got $memory")
    }

    @Test
    @DisplayName("Multiple coroutines should be able to record suspensions concurrently")
    fun `test concurrent suspension recording`() = runBlocking {
        val bufferId = monitor.safeCreateBuffer(10)
        val coroutineCount = 5
        val suspensionsPerCoroutine = 100

        coroutineScope {
            repeat(coroutineCount) { threadIndex ->
                launch(Dispatchers.Default) {
                    repeat(suspensionsPerCoroutine) {
                        monitor.safeRecordSuspension(
                            threadId = Thread.currentThread().id,
                            threadName = "TestCoroutine-$threadIndex",
                            bufferId = bufferId,
                            bufferSize = 10,
                            bufferCapacity = 10
                        )
                    }
                }
            }
        }

        val expected = coroutineCount * suspensionsPerCoroutine
        val totalSuspensions = monitor.safeGetTotalSuspensions()
        val bufferSuspensions = monitor.safeGetBufferSuspensionCount(bufferId)

        assertTrue(totalSuspensions >= expected, "Expected at least $expected total suspensions, got $totalSuspensions")
        assertEquals(expected, bufferSuspensions, "Expected $expected buffer suspensions, got $bufferSuspensions")
    }
}
