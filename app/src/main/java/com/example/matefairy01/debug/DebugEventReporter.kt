package com.example.matefairy01.debug

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

data class DebugEvent(
    val sessionId: String = "football-flyaway-regression",
    val runId: String = "pre-fix",
    val hypothesisId: String,
    val location: String,
    val message: String,
    val data: Map<String, Any>
)

interface DebugEventReporter {
    fun post(event: DebugEvent)
}

object NoOpDebugEventReporter : DebugEventReporter {
    override fun post(event: DebugEvent) = Unit
}

class BufferedHttpDebugEventReporter(
    private val endpoint: String,
    private val enabled: Boolean = true,
    private val maxQueueSize: Int = 100
) : DebugEventReporter {

    private val channel = Channel<DebugEvent>(capacity = maxQueueSize, onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    init {
        if (enabled) {
            scope.launch {
                for (event in channel) {
                    send(event)
                }
            }
        }
    }

    override fun post(event: DebugEvent) {
        if (!enabled) return
        channel.trySend(event)
    }

    private fun send(event: DebugEvent) {
        try {
            val body = """{"sessionId":"${event.sessionId}","runId":"${event.runId}","hypothesisId":"${event.hypothesisId}","location":"${event.location}","msg":"${event.message.escapeJson()}","data":${event.data.toDebugJson()},"ts":${System.currentTimeMillis()}}"""
            val connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.outputStream.use { it.write(body.toByteArray()) }
            connection.inputStream.close()
            connection.disconnect()
        } catch (_: Throwable) {
            // Ignore to not block the consumer loop
        }
    }

    private fun Map<String, Any>.toDebugJson(): String = entries.joinToString(prefix = "{", postfix = "}") { (key, value) ->
        val encoded = when (value) {
            is Number, is Boolean -> value.toString()
            else -> "\"${value.toString().escapeJson()}\""
        }
        "\"${key.escapeJson()}\":$encoded"
    }

    private fun String.escapeJson(): String = replace("\\", "\\\\").replace("\"", "\\\"")
}
