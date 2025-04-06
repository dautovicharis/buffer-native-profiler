package domain.usecase

import domain.entity.Buffer

interface CreateBufferUseCase {
    operator fun <T : Any> invoke(capacity: Int): Buffer<T>
}
