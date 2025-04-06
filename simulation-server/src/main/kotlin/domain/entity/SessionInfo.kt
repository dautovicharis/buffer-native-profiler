package domain.entity

import kotlinx.serialization.Serializable

/**
 * Represents information about active WebSocket sessions.
 */
@Serializable
data class SessionInfo(
    val type: String = "sessions",
    val count: Int
)
