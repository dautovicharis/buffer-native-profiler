package domain.repository

import domain.entity.BufferStats

interface WebSocketsRepository {
    fun start()
    fun stop()
    suspend fun sendUpdate(stats: BufferStats?)
}
