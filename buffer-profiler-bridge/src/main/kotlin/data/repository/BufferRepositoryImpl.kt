package data.repository

import domain.entity.Buffer
import domain.repository.BufferRepository
import data.source.TrackedBuffer
import domain.repository.BufferMonitorRepository

internal class BufferRepositoryImpl(
    private val bufferMonitorRepository: BufferMonitorRepository
) : BufferRepository {

    override fun <T : Any> createBuffer(capacity: Int): Buffer<T> {
        return TrackedBuffer(capacity, bufferMonitorRepository)
    }
}
