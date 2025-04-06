package data.util

/**
 * Utility class for generating test data items.
 * Provides methods to create string, byte array, and integer items with varying sizes and values.
 */
object TestDataGenerator {
    /**
     * Creates a string item with variable length based on the index.
     * Different indices get different lengths to simulate variable data patterns.
     *
     * @param index The index used to determine the string length
     * @return A string with variable length based on the index
     */
    fun createStringItemWithPadding(index: Int): String {
        val padding = when {
            index % 10 == 0 -> "X".repeat(10000) // Very large item every 10th
            index % 5 == 0 -> "X".repeat(5000)   // Large item every 5th
            index % 3 == 0 -> "X".repeat(1000)   // Medium item every 3rd
            else -> "X".repeat(100)              // Regular items
        }
        return "Item $index [$padding]"
    }

    /**
     * Creates a byte array item with variable length based on the index.
     * Different indices get different lengths to simulate variable data patterns.
     *
     * @param index The index used to determine the byte array length
     * @return A byte array with variable length based on the index
     */
    fun createByteArrayItem(index: Int): ByteArray {
        val size = when {
            index % 10 == 0 -> 10000 // Very large item every 10th
            index % 5 == 0 -> 5000   // Large item every 5th
            index % 3 == 0 -> 1000   // Medium item every 3rd
            else -> 100              // Regular items
        }
        return ByteArray(size) { index.toByte() }
    }

    /**
     * Creates an integer item with variable value based on the index.
     * Different indices get different values to simulate variable data patterns.
     *
     * @param index The index used to determine the integer value
     * @return An integer with variable value based on the index
     */
    fun createIntItem(index: Int): Int {
        return when {
            index % 10 == 0 -> Int.MAX_VALUE  // Very large number every 10th
            index % 5 == 0 -> 1000000        // Large number every 5th
            index % 3 == 0 -> 100000         // Medium number every 3rd
            else -> index                    // Regular numbers
        }
    }
}
