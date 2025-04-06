package di

import domain.repository.BufferMonitorRepository
import domain.repository.BufferRepository
import domain.repository.WebSocketsRepository
import domain.usecase.*
import data.usecase.*
import domain.service.BufferStatsBroadcast
import server.BufferWebSocketServer
import data.repository.WebSocketsRepositoryImpl

object AppModule {
    // Repositories
    private val bufferMonitorRepository: BufferMonitorRepository by lazy {
        BufferModule.provideBufferMonitorRepository()
    }

    private val bufferRepository: BufferRepository by lazy {
        BufferModule.provideBufferRepository(bufferMonitorRepository)
    }

    private fun createBufferStatsBroadcaster(port: Int): BufferStatsBroadcast {
        return BufferWebSocketServer(port)
    }

    private fun createWebSocketsRepository(port: Int): WebSocketsRepository {
        return WebSocketsRepositoryImpl(createBufferStatsBroadcaster(port))
    }

    // Use cases
    private val createBufferUseCase: CreateBufferUseCase by lazy {
        BufferModule.provideCreateBufferUseCase(bufferRepository)
    }

    private val monitorBuffersUseCase: MonitorBuffersUseCase by lazy {
        MonitorBuffersUseCaseImpl(bufferMonitorRepository)
    }

    private fun createWebSocketsUseCase(port: Int): WebSocketsUseCase {
        return WebSocketsUseCaseImpl(createWebSocketsRepository(port))
    }

    /**
     * Creates a RunDemoUseCase with the specified port and configuration.
     *
     * @param port The port to run the visualization server on
     * @return A configured RunDemoUseCase instance
     */
    fun createRunDemoUseCase(
        port: Int
    ): RunDemoUseCase {
        return RunDemoUseCaseImpl(
            createBufferUseCase,
            monitorBuffersUseCase,
            createWebSocketsUseCase(port),
            bufferMonitorRepository
        )
    }
}
