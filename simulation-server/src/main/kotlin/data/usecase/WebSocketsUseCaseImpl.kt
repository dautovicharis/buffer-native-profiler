package data.usecase

import domain.entity.BufferStats
import domain.repository.WebSocketsRepository
import domain.usecase.WebSocketsUseCase

class WebSocketsUseCaseImpl(
    private val visualizerRepository: WebSocketsRepository
) : WebSocketsUseCase {

    override fun start() {
        visualizerRepository.start()
    }

    override fun stop() {
        visualizerRepository.stop()
    }

    override suspend operator fun invoke(stats: BufferStats?) {
        visualizerRepository.sendUpdate(stats)
    }
}
