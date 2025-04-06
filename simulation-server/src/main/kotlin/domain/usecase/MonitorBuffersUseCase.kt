package domain.usecase

import domain.entity.BatchInfo
import domain.entity.Buffer
import domain.entity.BufferStats

interface MonitorBuffersUseCase {
    suspend operator fun invoke(
        stringBuffer: Buffer<String>,
        byteArrayBuffer: Buffer<ByteArray>,
        intBuffer: Buffer<Int>,
        batchInfo: BatchInfo? = null
    ): BufferStats?
}
