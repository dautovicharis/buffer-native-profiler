package data.usecase

import domain.entity.Buffer
import domain.repository.BufferRepository
import domain.usecase.CreateBufferUseCase

internal class CreateBufferUseCaseImpl(
    private val bufferRepository: BufferRepository
) : CreateBufferUseCase {

    override fun <T : Any> invoke(capacity: Int): Buffer<T> {
        return bufferRepository.createBuffer(capacity)
    }
}
