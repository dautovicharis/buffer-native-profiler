package domain.repository

import domain.entity.Buffer

interface BufferRepository {
    fun <T : Any> createBuffer(capacity: Int): Buffer<T>
}
