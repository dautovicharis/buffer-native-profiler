package data.repository

import domain.entity.BufferStats
import domain.repository.WebSocketsRepository
import domain.service.BufferStatsBroadcast

class WebSocketsRepositoryImpl(
    private val bufferStatsBroadcaster: BufferStatsBroadcast
) : WebSocketsRepository {

    override fun start() {
        bufferStatsBroadcaster.start()
    }

    override fun stop() {
        bufferStatsBroadcaster.stop()
    }

    override suspend fun sendUpdate(stats: BufferStats?) {
        bufferStatsBroadcaster.sendUpdate(stats)
    }
}
