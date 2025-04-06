package domain.entity

import kotlinx.serialization.Serializable

/**
 * Configuration parameters for the buffer monitoring demo.
 * Provides customizable settings for buffer sizes, item counts, and timing.
 */
@Serializable
data class BufferDemoConfig(
    /**
     * The capacity of each buffer in the demo.
     */
    val bufferCapacity: Int,

    /**
     * The interval in milliseconds between visualization updates.
     */
    val updateIntervalMs: Long,

    /**
     * The number of items each producer will generate.
     */
    val producerItems: Int,

    /**
     * The delay in milliseconds between polling an empty buffer.
     */
    val emptyBufferPollDelayMs: Long,
)
