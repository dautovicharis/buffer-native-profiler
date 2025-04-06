package domain.service

import domain.entity.BufferStats

/**
 * Service interface for broadcasting buffer statistics to clients.
 * This interface is part of the domain layer and defines the contract
 * for any implementation that broadcasts buffer statistics.
 */
interface BufferStatsBroadcast {
    /**
     * Starts the broadcasting service
     */
    fun start()
    
    /**
     * Stops the broadcasting service
     */
    fun stop()
    
    /**
     * Broadcasts updated buffer statistics to connected clients
     * @param stats The current buffer statistics to broadcast
     */
    suspend fun sendUpdate(stats: BufferStats?)
}
