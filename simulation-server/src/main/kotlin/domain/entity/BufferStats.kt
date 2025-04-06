package domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class BatchInfo(
    val currentBatch: Int,
    val completedBatches: Int,
    val batchConfig: BufferDemoConfig
)

@Serializable
data class BufferTypeStats(
    val bufferSize: Int,
    val bufferCapacity: Int,
    val utilizationPct: Int,
    val emissions: Int,
    val consumptions: Int,
    val suspensions: Int,
    val status: BufferStatus
)

@Serializable
data class BufferStats(
    val bufferSize: Int,
    val bufferCapacity: Int,
    val totalEmissions: Int,
    val totalConsumptions: Int,
    val suspensions: Int,
    val waitingItems: Int,
    val memoryUsage: Long,
    val memoryPerItem: Long,
    val memoryUtilizationPct: Int,
    val utilizationPct: Int,
    val status: BufferStatus,
    val buffers: Map<String, BufferTypeStats>,
    val lastUpdate: Long,
    val batchInfo: BatchInfo? = null
)
