package data.source

import domain.entity.Buffer
import domain.entity.BufferEntry
import domain.repository.BufferMonitorRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException

/**
 * A buffer that tracks its operations and memory usage.
 * It uses a Channel to manage the buffer's contents and a BufferMonitor to track its operations.
 */
internal class TrackedBuffer<T : Any>(
    override val capacity: Int,
    private val bufferMonitor: BufferMonitorRepository
) : Buffer<T> {
    private val channel = Channel<BufferEntry<T>>(capacity)
    private val bufferId = bufferMonitor.createBuffer(capacity)

    override suspend fun emit(value: T) {
        val threadId = Thread.currentThread().id
        val threadName = Thread.currentThread().name

        try {
            val size = bufferMonitor.getObjectSize(value)
            val entry = BufferEntry(value, System.nanoTime(), size)

            val currentSize = bufferMonitor.getBufferSize(bufferId)
            if (currentSize >= capacity) {
                bufferMonitor.recordSuspension(
                    threadId = threadId,
                    threadName = threadName,
                    bufferId = bufferId,
                    bufferSize = currentSize,
                    bufferCapacity = capacity
                )
            }

            // Record emission in native code
            bufferMonitor.recordEmission(bufferId)

            channel.send(entry)

            val currentBufferSize = bufferMonitor.getBufferSize(bufferId)
            val currentMemoryUsage = bufferMonitor.getBufferMemoryUsage(bufferId)

            val newSize = currentBufferSize + 1
            val newMemoryUsage = currentMemoryUsage + size

            bufferMonitor.updateBufferMetrics(
                bufferId = bufferId,
                size = newSize,
                memoryUsage = newMemoryUsage
            )
        } catch (e: ClosedSendChannelException) {
            throw e
        }
    }

    override suspend fun consume(): T? {
        return try {
            channel.tryReceive().getOrNull()?.also { entry ->
                // Record consumption in native code
                bufferMonitor.recordConsumption(bufferId)
                val currentBufferSize = bufferMonitor.getBufferSize(bufferId)
                val currentMemoryUsage = bufferMonitor.getBufferMemoryUsage(bufferId)

                val newSize = currentBufferSize - 1
                val newMemoryUsage = currentMemoryUsage - entry.size

                bufferMonitor.updateBufferMetrics(
                    bufferId = bufferId,
                    size = newSize,
                    memoryUsage = newMemoryUsage
                )
            }?.value
        } catch (e: ClosedReceiveChannelException) {
            null
        }
    }

    override fun getCurrentSize(): Int {
        return bufferMonitor.getBufferSize(bufferId).takeIf { it > 0 } ?: 0
    }

    override fun getId(): Long {
        return bufferId
    }

    override fun getEmissions(): Int {
        return bufferMonitor.getBufferEmissions(bufferId)
    }

    override fun getConsumptions(): Int {
        return bufferMonitor.getBufferConsumptions(bufferId)
    }

    override fun getSuspensions(): Int {
        return bufferMonitor.getBufferSuspensionCount(bufferId)
    }

    override fun close() {
        channel.close()
    }
}
