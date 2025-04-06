package data.repository

import domain.repository.BufferMonitorRepository
import NativeBufferMonitor

internal class BufferMonitorRepositoryImpl(
    private val nativeBufferMonitor: NativeBufferMonitor
) : BufferMonitorRepository {

    override fun createBuffer(capacity: Int): Long {
        return nativeBufferMonitor.safeCreateBuffer(capacity)
    }

    override fun updateBufferMetrics(bufferId: Long, size: Int, memoryUsage: Long) {
        nativeBufferMonitor.safeUpdateBufferMetrics(bufferId, size, memoryUsage)
    }

    override fun recordSuspension(threadId: Long, threadName: String, bufferId: Long, bufferSize: Int, bufferCapacity: Int) {
        nativeBufferMonitor.safeRecordSuspension(threadId, threadName, bufferId, bufferSize, bufferCapacity)
    }

    override fun getObjectSize(obj: Any): Long {
        return nativeBufferMonitor.safeGetObjectSize(obj)
    }

    override fun getBufferSize(bufferId: Long): Int {
        return nativeBufferMonitor.safeGetBufferSize(bufferId)
    }

    override fun getBufferMemoryUsage(bufferId: Long): Long {
        return nativeBufferMonitor.safeGetBufferMemoryUsage(bufferId)
    }

    override fun getTotalTrackedMemory(): Long {
        return nativeBufferMonitor.safeGetTotalTrackedMemory()
    }

    override fun getSuspensionCount(): Int {
        return nativeBufferMonitor.safeGetSuspensionCount()
    }

    override fun recordEmission(bufferId: Long) {
        nativeBufferMonitor.safeRecordEmission(bufferId)
    }

    override fun recordConsumption(bufferId: Long) {
        nativeBufferMonitor.safeRecordConsumption(bufferId)
    }

    override fun getTotalEmissions(): Int {
        return nativeBufferMonitor.safeGetTotalEmissions()
    }

    override fun getTotalConsumptions(): Int {
        return nativeBufferMonitor.safeGetTotalConsumptions()
    }

    override fun getBufferEmissions(bufferId: Long): Int {
        return nativeBufferMonitor.safeGetBufferEmissions(bufferId)
    }

    override fun getBufferConsumptions(bufferId: Long): Int {
        return nativeBufferMonitor.safeGetBufferConsumptions(bufferId)
    }

    override fun getBufferSuspensionCount(bufferId: Long): Int {
        return nativeBufferMonitor.safeGetBufferSuspensionCount(bufferId)
    }

    override fun clearTracking() {
        nativeBufferMonitor.safeClearTracking()
    }
}
