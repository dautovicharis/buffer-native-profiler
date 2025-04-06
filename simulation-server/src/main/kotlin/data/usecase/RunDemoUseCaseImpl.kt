package data.usecase

import data.util.BufferCoroutineUtils.createConsumer
import data.util.BufferCoroutineUtils.createProducer
import data.util.TestDataGenerator
import domain.entity.BatchInfo
import domain.entity.Buffer
import domain.entity.BufferDemoConfig
import domain.repository.BufferMonitorRepository

import domain.usecase.CreateBufferUseCase
import domain.usecase.MonitorBuffersUseCase
import domain.usecase.RunDemoUseCase
import domain.usecase.WebSocketsUseCase
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class RunDemoUseCaseImpl(
    private val createBufferUseCase: CreateBufferUseCase,
    private val monitorBuffersUseCase: MonitorBuffersUseCase,
    private val visualizeBuffersUseCase: WebSocketsUseCase,
    private val bufferMonitor: BufferMonitorRepository
) : RunDemoUseCase {
    private val logger = LoggerFactory.getLogger(RunDemoUseCaseImpl::class.java)

    private fun CoroutineScope.createVisualizer(
        stringBuffer: Buffer<String>,
        byteArrayBuffer: Buffer<ByteArray>,
        intBuffer: Buffer<Int>,
        config: BufferDemoConfig,
        batchInfo: BatchInfo? = null
    ): Job = launch {
        while (isActive) {
            val stats = monitorBuffersUseCase(stringBuffer, byteArrayBuffer, intBuffer, batchInfo)
            visualizeBuffersUseCase(stats)
            delay(config.updateIntervalMs.milliseconds)
        }
    }

    // Function to generate a different config for each batch
    private fun generateBatchConfig(batchNumber: Int): BufferDemoConfig {
        return BufferDemoConfig(
            bufferCapacity = 10 + (batchNumber % 10) * 2,
            updateIntervalMs = 500L,
            producerItems = Random.nextInt(50, 100),
            emptyBufferPollDelayMs = 100L,
        )
    }

    // Function to run a single batch
    private suspend fun runBatch(
        batchConfig: BufferDemoConfig,
        currentBatch: Int,
        completedBatches: Int
    ) = coroutineScope {
        logger.info("Starting batch $currentBatch with config: $batchConfig")

        // Create buffers for this batch
        val stringBuffer = createBufferUseCase<String>(batchConfig.bufferCapacity)
        val byteArrayBuffer = createBufferUseCase<ByteArray>(batchConfig.bufferCapacity)
        val intBuffer = createBufferUseCase<Int>(batchConfig.bufferCapacity)

        // Create batch info
        val batchInfo = BatchInfo(currentBatch, completedBatches, batchConfig)

        // Create visualization job with batch info
        val visualizer = createVisualizer(stringBuffer, byteArrayBuffer, intBuffer, batchConfig, batchInfo)

        // Create producers using the generic producer function
        val stringProducer = createProducer(
            buffer = stringBuffer,
            itemGenerator = TestDataGenerator::createStringItemWithPadding,
            config = batchConfig,
            itemType = "string",
            logger = logger
        )

        val byteArrayProducer = createProducer(
            buffer = byteArrayBuffer,
            itemGenerator = TestDataGenerator::createByteArrayItem,
            config = batchConfig,
            itemType = "bytearray",
            logger = logger
        )

        val intProducer = createProducer(
            buffer = intBuffer,
            itemGenerator = TestDataGenerator::createIntItem,
            config = batchConfig,
            itemType = "int",
            logger = logger
        )

        // Create consumers
        val stringConsumer = createConsumer(
            buffer = stringBuffer,
            producer = stringProducer,
            itemType = "string",
            config = batchConfig,
            logger = logger
        )

        val byteArrayConsumer = createConsumer(
            buffer = byteArrayBuffer,
            producer = byteArrayProducer,
            itemType = "bytearray",
            config = batchConfig,
            logger = logger
        )

        val intConsumer = createConsumer(
            buffer = intBuffer,
            producer = intProducer,
            itemType = "int",
            config = batchConfig,
            logger = logger
        )

        // Wait for all jobs to complete
        stringProducer.join()
        byteArrayProducer.join()
        intProducer.join()
        stringConsumer.join()
        byteArrayConsumer.join()
        intConsumer.join()

        visualizer.cancel()

        // Close buffers
        stringBuffer.close()
        byteArrayBuffer.close()
        intBuffer.close()

        // Clear all buffer information for next batch
        bufferMonitor.clearTracking()
    }

    override suspend fun invoke(port: Int) = coroutineScope {
        visualizeBuffersUseCase.start()
        var completedBatches = 0
        var currentBatch = 1

        while (true) {
            val batchConfig = generateBatchConfig(currentBatch)
            runBatch(batchConfig, currentBatch, completedBatches)

            completedBatches++
            currentBatch++

            delay(5.seconds)
            bufferMonitor.clearTracking()
        }
    }
}
