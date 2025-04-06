package server

import domain.entity.BufferStats
import domain.entity.SessionInfo
import domain.service.BufferStatsBroadcast
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.io.File
import kotlin.time.Duration.Companion.seconds


/**
 * Implementation of the BufferStatsBroadcaster interface that provides a WebSocket server
 * for broadcasting buffer statistics to connected clients and serves a React web application.
 */
class BufferWebSocketServer(private val port: Int) : BufferStatsBroadcast {
    private val logger: Logger = LoggerFactory.getLogger(BufferWebSocketServer::class.java)
    private val sessions = mutableSetOf<DefaultWebSocketSession>()

    // JSON configuration for serialization
    private val json = Json {
        // Use compact format for network efficiency
        prettyPrint = false
        // Ensure stable field order for consistent output
        encodeDefaults = true
    }

    private var server: NettyApplicationEngine? = null

    companion object {
        // WebSocket configuration
        private val WEBSOCKET_PING_PERIOD = 15.seconds
        private val WEBSOCKET_TIMEOUT = 15.seconds

        // Server shutdown timeouts
        private const val GRACEFUL_SHUTDOWN_MILLIS = 1000L
        private const val TIMEOUT_SHUTDOWN_MILLIS = 2000L

        // Static resources
        private const val STATIC_RESOURCES_PATH = "static"

        // WebSocket paths
        private const val STATS_WEBSOCKET_PATH = "/stats"
    }

    override fun start() {
        val embeddedServer = embeddedServer(Netty, port = port) {
            install(WebSockets) {
                pingPeriod = WEBSOCKET_PING_PERIOD
                timeout = WEBSOCKET_TIMEOUT
            }

            routing {
                staticResources("/", STATIC_RESOURCES_PATH)
                webSocket(STATS_WEBSOCKET_PATH) {
                    sessions.add(this)
                    try {
                        // Send active session count to all clients
                        broadcastSessionCount()

                        // Keep connection alive and handle incoming messages if needed
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> logger.debug("Received message: ${frame.readText()}")
                                else -> logger.debug("Received frame type: ${frame::class.simpleName}")
                            }
                        }
                    } catch (e: Exception) {
                        logger.error("WebSocket error", e)
                    } finally {
                        sessions.remove(this)
                        logger.debug("Client disconnected. Active sessions: ${sessions.size}")
                        // Update session count for remaining clients
                        broadcastSessionCount()
                    }
                }

                // Route for React app
                get("/") {
                    val resource = javaClass.classLoader.getResource("static/index.html")?.toURI()
                    if (resource != null) {
                        call.respondFile(File(resource))
                    } else {
                        call.respondText("index.html not found", status = HttpStatusCode.InternalServerError)
                    }
                }
            }
        }

        server = embeddedServer.engine
        embeddedServer.start(wait = false)
    }

    override fun stop() {
        server?.stop(GRACEFUL_SHUTDOWN_MILLIS, TIMEOUT_SHUTDOWN_MILLIS)
        server = null
        logger.info("Buffer Memory Monitor stopped")
    }

    /**
     * Send updated buffer statistics to all connected WebSocket clients
     * @param stats The current buffer statistics to broadcast
     */
    override suspend fun sendUpdate(stats: BufferStats?) {
        if (sessions.isEmpty() && stats == null) return

        val serializedStats = if (stats != null) json.encodeToString(BufferStats.serializer(), stats) else "null"

        val failedSessions = mutableSetOf<DefaultWebSocketSession>()
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(serializedStats))
            } catch (e: Exception) {
                logger.error("Failed to send update to client", e)
                failedSessions.add(session)
            }
        }

        sessions.removeAll(failedSessions)
    }

    /**
     * Broadcasts the current active session count to all connected clients
     */
    private suspend fun broadcastSessionCount() {
        val sessionCount = sessions.size
        val sessionInfo = SessionInfo(count = sessionCount)
        val serializedMessage = json.encodeToString(SessionInfo.serializer(), sessionInfo)

        val failedSessions = mutableSetOf<DefaultWebSocketSession>()
        sessions.forEach { session ->
            try {
                session.send(Frame.Text(serializedMessage))
            } catch (e: Exception) {
                logger.error("Failed to send session count to client", e)
                failedSessions.add(session)
            }
        }

        sessions.removeAll(failedSessions)
    }
}
