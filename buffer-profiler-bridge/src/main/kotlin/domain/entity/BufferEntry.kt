package domain.entity

data class BufferEntry<T>(
    val value: T,
    val entryId: Long,
    val size: Long
)
