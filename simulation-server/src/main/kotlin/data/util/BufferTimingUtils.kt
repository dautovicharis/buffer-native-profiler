package data.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Utility class for calculating timing delays in buffer operations.
 * Provides methods to determine appropriate delays for producing and consuming items.
 */
object BufferTimingUtils {

    /**
     * Calculates the delay for burst consumption patterns based on buffer utilization.
     * Simple implementation with randomization for realistic simulation.
     *
     * @param bufferUtilization The current buffer utilization as a ratio (0.0 to 1.0)
     * @return The appropriate delay duration for the consumption pattern
     */
    fun consumerDelay(bufferUtilization: Double): Duration {
        val baseDelay = when {
            bufferUtilization > 0.8 -> 100.milliseconds  // Fast when buffer is full
            bufferUtilization > 0.5 -> 500.milliseconds  // Medium speed at medium capacity
            else -> 1000.milliseconds                    // Slow when buffer is emptier
        }

        val randomFactor = kotlin.random.Random.nextDouble(0.5, 1.5)
        return baseDelay * randomFactor
    }

    /**
     * Calculates the delay for burst patterns in producers.
     * Simple implementation with randomization for realistic simulation.
     *
     * @param index The index of the item being produced
     * @return The appropriate delay duration for the burst pattern
     */
    fun producerDelay(index: Int): Duration {
        val baseDelay = when {
            index % 10 == 0 -> 1000.milliseconds  // Longer delay every 10th item
            index % 5 == 0 -> 500.milliseconds    // Medium delay every 5th item
            else -> 100.milliseconds              // Normal delay for other items
        }

        val randomFactor = kotlin.random.Random.nextDouble(0.5, 1.5)
        return baseDelay * randomFactor
    }
}
