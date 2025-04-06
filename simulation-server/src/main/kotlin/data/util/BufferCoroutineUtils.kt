package data.util

import domain.entity.Buffer
import domain.entity.BufferDemoConfig
import kotlinx.coroutines.*
import org.slf4j.Logger
import kotlin.time.Duration.Companion.milliseconds

/**
 * Utility functions for working with buffers in coroutines.
 * Provides extension functions on CoroutineScope for creating producers and consumers.
 */
object BufferCoroutineUtils {

    /**
     * Creates a generic producer for any type of buffer.
     *
     * @param buffer The buffer to emit items to
     * @param itemGenerator A function that generates items of type T based on an index
     * @param config The buffer demo configuration
     * @param itemType A string describing the type of items for logging purposes
     * @param logger Logger for recording events
     * @return A coroutine Job that produces items
     */
    fun <T : Any> CoroutineScope.createProducer(
        buffer: Buffer<T>,
        itemGenerator: (Int) -> T,
        config: BufferDemoConfig,
        itemType: String,
        logger: Logger
    ): Job = launch {
        repeat(config.producerItems) { i ->
            try {
                val item = itemGenerator(i)
                buffer.emit(item)
                delay(BufferTimingUtils.producerDelay(i))
            } catch (e: Exception) {
                logger.error("$itemType emission error", e)
            }
        }
        logger.info("$itemType producer completed")
    }

    /**
     * Creates a generic consumer for any type of buffer.
     *
     * @param buffer The buffer to consume items from
     * @param producer The producer job that is filling the buffer
     * @param itemType A string describing the type of items for logging purposes
     * @param config The buffer demo configuration
     * @param logger Logger for recording events
     * @return A coroutine Job that consumes items
     */
    fun <T : Any> CoroutineScope.createConsumer(
        buffer: Buffer<T>,
        producer: Job,
        itemType: String,
        config: BufferDemoConfig,
        logger: Logger
    ): Job = launch {
        var keepConsuming = true
        while (keepConsuming && isActive) {
            try {
                val item = buffer.consume()
                if (item != null) {
                    val bufferUtilization = buffer.getCurrentSize().toDouble() / buffer.capacity
                    delay(BufferTimingUtils.consumerDelay(bufferUtilization))
                } else {
                    if (!producer.isActive && buffer.getCurrentSize() == 0) {
                        keepConsuming = false
                        logger.info("No more $itemType items to consume and producer finished. Stopping $itemType consumer")
                    } else {
                        delay(config.emptyBufferPollDelayMs.milliseconds)
                    }
                }
            } catch (e: Exception) {
                logger.error("$itemType consumption error", e)
            }
        }
        logger.info("$itemType consumer completed")
    }
}
