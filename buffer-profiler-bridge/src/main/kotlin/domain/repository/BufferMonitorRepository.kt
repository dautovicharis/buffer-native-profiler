package domain.repository

interface BufferMonitorRepository {
    fun createBuffer(capacity: Int): Long
    fun getObjectSize(obj: Any): Long
    fun getBufferSize(bufferId: Long): Int
    fun getBufferMemoryUsage(bufferId: Long): Long
    fun updateBufferMetrics(bufferId: Long, size: Int, memoryUsage: Long)

    fun recordSuspension(threadId: Long, threadName: String, bufferId: Long, bufferSize: Int, bufferCapacity: Int)
    fun recordEmission(bufferId: Long)
    fun recordConsumption(bufferId: Long)

    fun getBufferEmissions(bufferId: Long): Int
    fun getBufferConsumptions(bufferId: Long): Int
    fun getBufferSuspensionCount(bufferId: Long): Int

    fun getTotalTrackedMemory(): Long
    fun getTotalSuspensions(): Int
    fun getTotalEmissions(): Int
    fun getTotalConsumptions(): Int

    fun clearTracking()
}
