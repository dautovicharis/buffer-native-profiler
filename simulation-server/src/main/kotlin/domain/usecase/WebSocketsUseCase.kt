package domain.usecase

import domain.entity.BufferStats

interface WebSocketsUseCase {
    fun start()
    fun stop()
    suspend operator fun invoke(stats: BufferStats?)
}
