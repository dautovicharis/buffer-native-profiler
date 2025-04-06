package di

import NativeBufferMonitor
import data.repository.BufferMonitorRepositoryImpl
import data.repository.BufferRepositoryImpl
import data.usecase.CreateBufferUseCaseImpl
import domain.repository.BufferMonitorRepository
import domain.repository.BufferRepository
import domain.usecase.CreateBufferUseCase

object BufferModule {
    fun provideBufferMonitorRepository(): BufferMonitorRepository {
        val nativeMonitor = NativeBufferMonitor()
        return BufferMonitorRepositoryImpl(nativeMonitor)
    }

    fun provideBufferRepository(
        bufferMonitorRepository: BufferMonitorRepository
    ): BufferRepository {
        return BufferRepositoryImpl(bufferMonitorRepository)
    }

    fun provideCreateBufferUseCase(
        bufferRepository: BufferRepository
    ): CreateBufferUseCase {
        return CreateBufferUseCaseImpl(bufferRepository)
    }
}