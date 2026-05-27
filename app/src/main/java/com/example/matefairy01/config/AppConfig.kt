package com.example.matefairy01.config

import android.content.Context
import com.example.matefairy01.mcp.McpServerConfig
import com.example.matefairy01.mcp.McpTransportType
import org.json.JSONArray
import org.json.JSONObject

data class AppConfig(
    val ai: AIConfig = AIConfig(),
    val mcpServers: List<McpServerConfig> = emptyList()
)

data class AIConfig(
    val provider: LLMProviderType = LLMProviderType.MOCK,
    val systemPrompt: String = "You are a helpful fairy companion.",
    val deepseek: DeepSeekConfig = DeepSeekConfig(),
    val autoTest: AIAutoTestConfig = AIAutoTestConfig()
)

enum class LLMProviderType {
    MOCK,
    DEEPSEEK;

    companion object {
        fun fromRaw(raw: String?): LLMProviderType {
            return when (raw?.trim()?.lowercase()) {
                "deepseek" -> DEEPSEEK
                else -> MOCK
            }
        }
    }
}

data class DeepSeekConfig(
    val baseUrl: String = "https://api.deepseek.com/chat/completions",
    val apiKey: String = "sk-72a4686e3b31416dad1f35385646f188",
    val model: String = "deepseek-chat",
    val summaryModel: String = "deepseek-chat",
    val connectTimeoutMs: Long = 10_000L,
    val readTimeoutMs: Long = 30_000L,
    val writeTimeoutMs: Long = 30_000L,
    val maxTokens: Int = 512,
    val summaryMaxTokens: Int = 256,
    val temperature: Double = 0.7
)

data class AIAutoTestConfig(
    val enabled: Boolean = false,
    val intervalMs: Long = 3_000L,
    val promptPool: List<String> = emptyList()
)

object AppConfigLoader {
    private const val CONFIG_ASSET_PATH = "app_config.json"

    @Volatile
    private var cachedConfig: AppConfig? = null

    fun load(context: Context): AppConfig {
        cachedConfig?.let { return it }

        synchronized(this) {
            cachedConfig?.let { return it }

            val raw = context.assets.open(CONFIG_ASSET_PATH).bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val config = AppConfig(
                ai = parseAIConfig(root.optJSONObject("ai")),
                mcpServers = parseMcpServers(root.optJSONObject("mcpServers"))
            )
            cachedConfig = config
            return config
        }
    }

    private fun parseMcpServers(json: JSONObject?): List<McpServerConfig> {
        if (json == null) return emptyList()
        val result = mutableListOf<McpServerConfig>()
        val names = json.keys()
        while (names.hasNext()) {
            val name = names.next()
            val item = json.optJSONObject(name) ?: continue
            val url = item.optString("url").trim()
            if (url.isEmpty()) continue

            val toolsArr = item.optJSONArray("enabledTools")
            val enabledTools = if (toolsArr == null) listOf("*") else toolsArr.toStringList()

            result += McpServerConfig(
                name = name,
                type = McpTransportType.fromRaw(item.optString("type")),
                url = url,
                headers = parseStringMap(item.optJSONObject("headers")),
                connectTimeoutMs = item.optLong("connectTimeoutMs", 10_000L),
                readTimeoutMs = item.optLong("readTimeoutMs", 60_000L),
                toolTimeoutMs = item.optLong("toolTimeoutMs", 30_000L),
                enabledTools = enabledTools
            )
        }
        return result
    }

    private fun parseStringMap(json: JSONObject?): Map<String, String> {
        if (json == null) return emptyMap()
        val map = mutableMapOf<String, String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = json.optString(key)
        }
        return map
    }

    private fun parseAIConfig(json: JSONObject?): AIConfig {
        if (json == null) return AIConfig()

        return AIConfig(
            provider = LLMProviderType.fromRaw(json.optString("provider")),
            systemPrompt = json.optString("systemPrompt", AIConfig().systemPrompt),
            deepseek = parseDeepSeekConfig(json.optJSONObject("deepseek")),
            autoTest = parseAutoTestConfig(json.optJSONObject("autoTest"))
        )
    }

    private fun parseDeepSeekConfig(json: JSONObject?): DeepSeekConfig {
        val defaults = DeepSeekConfig()
        if (json == null) return defaults

        return DeepSeekConfig(
            baseUrl = json.optString("baseUrl", defaults.baseUrl),
            apiKey = json.optString("apiKey", defaults.apiKey),
            model = json.optString("model", defaults.model),
            summaryModel = json.optString("summaryModel", defaults.summaryModel),
            connectTimeoutMs = json.optLong("connectTimeoutMs", defaults.connectTimeoutMs),
            readTimeoutMs = json.optLong("readTimeoutMs", defaults.readTimeoutMs),
            writeTimeoutMs = json.optLong("writeTimeoutMs", defaults.writeTimeoutMs),
            maxTokens = json.optInt("maxTokens", defaults.maxTokens),
            summaryMaxTokens = json.optInt("summaryMaxTokens", defaults.summaryMaxTokens),
            temperature = json.optDouble("temperature", defaults.temperature)
        )
    }

    private fun parseAutoTestConfig(json: JSONObject?): AIAutoTestConfig {
        val defaults = AIAutoTestConfig()
        if (json == null) return defaults

        return AIAutoTestConfig(
            enabled = json.optBoolean("enabled", defaults.enabled),
            intervalMs = json.optLong("intervalMs", defaults.intervalMs),
            promptPool = json.optJSONArray("promptPool").toStringList()
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()

        val items = mutableListOf<String>()
        for (index in 0 until length()) {
            val value = optString(index).trim()
            if (value.isNotEmpty()) {
                items += value
            }
        }
        return items
    }
}
