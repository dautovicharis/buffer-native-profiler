package data.model

import domain.entity.BufferDemoConfig

data class BatchInfoData(
    val currentBatch: Int,
    val completedBatches: Int,
    val batchConfig: BufferDemoConfig
)

data class BufferTypeStatsData(
    val bufferSize: Int,
    val bufferCapacity: Int,
    val utilizationPct: Int,
    val emissions: Int,
    val consumptions: Int,
    val suspensions: Int,
    val status: BufferStatusData
)

data class BufferStatsData(
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
    val status: BufferStatusData,
    val buffers: Map<String, BufferTypeStatsData>,
    val lastUpdate: Long,
    val batchInfo: BatchInfoData? = null
)
