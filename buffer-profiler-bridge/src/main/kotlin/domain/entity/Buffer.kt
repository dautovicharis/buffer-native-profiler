package domain.entity

/**
 * Represents a buffer with a fixed capacity that can store and retrieve items.
 */
interface Buffer<T : Any> {
    /**
     * The maximum number of items the buffer can hold.
     */
    val capacity: Int

    /**
     * The current number of items in the buffer.
     */
    fun getCurrentSize(): Int

    /**
     * Adds an item to the buffer. May suspend if the buffer is full.
     */
    suspend fun emit(value: T)

    /**
     * Retrieves and removes an item from the buffer.
     * Returns null if the buffer is empty.
     */
    suspend fun consume(): T?

    /**
     * Returns the number of emissions for this buffer.
     */
    fun getEmissions(): Int

    /**
     * Returns the number of consumptions for this buffer.
     */
    fun getConsumptions(): Int

    /**
     * Returns the number of suspensions for this buffer.
     */
    fun getSuspensions(): Int

    /**
     * Returns the unique identifier of this buffer.
     * Used for tracking metrics in the native code.
     */
    fun getId(): Long

    /**
     * Closes the buffer and releases any resources.
     */
    fun close()
}
