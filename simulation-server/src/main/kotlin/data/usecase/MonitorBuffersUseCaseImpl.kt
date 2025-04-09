package data.usecase

import data.model.BatchInfoData
import data.model.BufferStatsData
import data.model.BufferStatusData
import data.model.BufferTypeStatsData
import domain.entity.*
import domain.repository.BufferMonitorRepository
import domain.usecase.MonitorBuffersUseCase
import org.slf4j.LoggerFactory
import java.text.NumberFormat

class MonitorBuffersUseCaseImpl(
    private val bufferMonitorRepository: BufferMonitorRepository
) : MonitorBuffersUseCase {
    private val logger = LoggerFactory.getLogger(MonitorBuffersUseCaseImpl::class.java)
    private val numberFormat = NumberFormat.getNumberInstance()

    override suspend fun invoke(
        stringBuffer: Buffer<String>,
        byteArrayBuffer: Buffer<ByteArray>,
        intBuffer: Buffer<Int>,
        batchInfo: BatchInfo?
    ): BufferStats? {
        try {
            val totalBufferSize = stringBuffer.getCurrentSize() + byteArrayBuffer.getCurrentSize() + intBuffer.getCurrentSize()
            val totalMemory = bufferMonitorRepository.getTotalTrackedMemory()
            val memoryPerItem = if (totalBufferSize > 0) totalMemory / totalBufferSize else 0

            // Calculate memory utilization based on buffer fullness
            val bufferCapacity = stringBuffer.capacity + byteArrayBuffer.capacity + intBuffer.capacity
            val bufferUtilizationPct = ((totalBufferSize.toDouble() / bufferCapacity) * 100).toInt().coerceIn(0, 100)

            // Calculate per-buffer statistics
            val stringSize = stringBuffer.getCurrentSize()
            val stringCapacity = stringBuffer.capacity
            val stringUtilizationPct = ((stringSize.toDouble() / stringCapacity) * 100).toInt().coerceIn(0, 100)
            val stringStatus = when {
                stringSize >= stringCapacity -> BufferStatusData.CRITICAL
                stringUtilizationPct > 70 -> BufferStatusData.WARNING
                else -> BufferStatusData.NORMAL
            }

            val byteArraySize = byteArrayBuffer.getCurrentSize()
            val byteArrayCapacity = byteArrayBuffer.capacity
            val byteArrayUtilizationPct = ((byteArraySize.toDouble() / byteArrayCapacity) * 100).toInt().coerceIn(0, 100)
            val byteArrayStatus = when {
                byteArraySize >= byteArrayCapacity -> BufferStatusData.CRITICAL
                byteArrayUtilizationPct > 70 -> BufferStatusData.WARNING
                else -> BufferStatusData.NORMAL
            }

            val intSize = intBuffer.getCurrentSize()
            val intCapacity = intBuffer.capacity
            val intUtilizationPct = ((intSize.toDouble() / intCapacity) * 100).toInt().coerceIn(0, 100)
            val intStatus = when {
                intSize >= intCapacity -> BufferStatusData.CRITICAL
                intUtilizationPct > 70 -> BufferStatusData.WARNING
                else -> BufferStatusData.NORMAL
            }

            // Create per-buffer stats objects using data models
            val stringStatsData = BufferTypeStatsData(
                bufferSize = stringSize,
                bufferCapacity = stringCapacity,
                utilizationPct = stringUtilizationPct,
                emissions = stringBuffer.getEmissions(),
                consumptions = stringBuffer.getConsumptions(),
                suspensions = stringBuffer.getSuspensions(),
                status = stringStatus
            )

            val byteArrayStatsData = BufferTypeStatsData(
                bufferSize = byteArraySize,
                bufferCapacity = byteArrayCapacity,
                utilizationPct = byteArrayUtilizationPct,
                emissions = byteArrayBuffer.getEmissions(),
                consumptions = byteArrayBuffer.getConsumptions(),
                suspensions = byteArrayBuffer.getSuspensions(),
                status = byteArrayStatus
            )

            val intStatsData = BufferTypeStatsData(
                bufferSize = intSize,
                bufferCapacity = intCapacity,
                utilizationPct = intUtilizationPct,
                emissions = intBuffer.getEmissions(),
                consumptions = intBuffer.getConsumptions(),
                suspensions = intBuffer.getSuspensions(),
                status = intStatus
            )

            // Convert BatchInfo to BatchInfoData if available
            val batchInfoData = batchInfo?.let {
                BatchInfoData(
                    currentBatch = it.currentBatch,
                    completedBatches = it.completedBatches,
                    batchConfig = it.batchConfig
                )
            }

            val bufferStatsData = BufferStatsData(
                bufferSize = totalBufferSize,
                bufferCapacity = bufferCapacity,
                totalEmissions = bufferMonitorRepository.getTotalEmissions(),
                totalConsumptions = bufferMonitorRepository.getTotalConsumptions(),
                suspensions = bufferMonitorRepository.getTotalSuspensions(),
                waitingItems = bufferMonitorRepository.getTotalEmissions() - bufferMonitorRepository.getTotalConsumptions(),
                memoryUsage = totalMemory,
                memoryPerItem = memoryPerItem,
                memoryUtilizationPct = bufferUtilizationPct,
                utilizationPct = bufferUtilizationPct,
                status = when {
                    bufferUtilizationPct > 90 -> BufferStatusData.CRITICAL
                    bufferUtilizationPct > 70 -> BufferStatusData.WARNING
                    else -> BufferStatusData.NORMAL
                },
                buffers = mapOf(
                    "string" to stringStatsData,
                    "bytearray" to byteArrayStatsData,
                    "int" to intStatsData
                ),
                lastUpdate = System.currentTimeMillis(),
                batchInfo = batchInfoData
            )

            logBufferStats(totalBufferSize, stringBuffer, byteArrayBuffer, intBuffer, totalMemory, memoryPerItem)

            // Convert data model to domain entity manually
            return BufferStats(
                bufferSize = bufferStatsData.bufferSize,
                bufferCapacity = bufferStatsData.bufferCapacity,
                totalEmissions = bufferStatsData.totalEmissions,
                totalConsumptions = bufferStatsData.totalConsumptions,
                suspensions = bufferStatsData.suspensions,
                waitingItems = bufferStatsData.waitingItems,
                memoryUsage = bufferStatsData.memoryUsage,
                memoryPerItem = bufferStatsData.memoryPerItem,
                memoryUtilizationPct = bufferStatsData.memoryUtilizationPct,
                utilizationPct = bufferStatsData.utilizationPct,
                status = when (bufferStatsData.status) {
                    BufferStatusData.NORMAL -> BufferStatus.NORMAL
                    BufferStatusData.WARNING -> BufferStatus.WARNING
                    BufferStatusData.CRITICAL -> BufferStatus.CRITICAL
                },
                buffers = bufferStatsData.buffers.mapValues { (_, value) ->
                    BufferTypeStats(
                        bufferSize = value.bufferSize,
                        bufferCapacity = value.bufferCapacity,
                        utilizationPct = value.utilizationPct,
                        emissions = value.emissions,
                        consumptions = value.consumptions,
                        suspensions = value.suspensions,
                        status = when (value.status) {
                            BufferStatusData.NORMAL -> BufferStatus.NORMAL
                            BufferStatusData.WARNING -> BufferStatus.WARNING
                            BufferStatusData.CRITICAL -> BufferStatus.CRITICAL
                        }
                    )
                },
                lastUpdate = bufferStatsData.lastUpdate,
                batchInfo = batchInfo
            )
        } catch (e: Exception) {
            logger.error("Error monitoring buffers", e)
            return null
        }
    }

    private fun logBufferStats(
        totalBufferSize: Int,
        stringBuffer: Buffer<String>,
        byteArrayBuffer: Buffer<ByteArray>,
        intBuffer: Buffer<Int>,
        totalMemory: Long,
        memoryPerItem: Long
    ) {
        logger.info("""
            === BUFFER STATS ===
            Total Buffer Size: $totalBufferSize/${stringBuffer.capacity + byteArrayBuffer.capacity + intBuffer.capacity}
            String Buffer: ${stringBuffer.getCurrentSize()}/${stringBuffer.capacity}
            ByteArray Buffer: ${byteArrayBuffer.getCurrentSize()}/${byteArrayBuffer.capacity}
            Int Buffer: ${intBuffer.getCurrentSize()}/${intBuffer.capacity}
            Memory: ${numberFormat.format(totalMemory)} bytes (${totalMemory / 1024} KB)
            Memory Per Item: ${numberFormat.format(memoryPerItem)} bytes
            Emitted: ${bufferMonitorRepository.getTotalEmissions()}, Consumed: ${bufferMonitorRepository.getTotalConsumptions()}, Suspensions: ${bufferMonitorRepository.getTotalSuspensions()}
        """.trimIndent())
    }
}
