package com.example.matefairy01.config

import android.content.Context
import com.example.matefairy01.mcp.McpServerConfig
import com.example.matefairy01.mcp.McpTransportType
import com.example.matefairy01.memory.ConsolidateConfig
import com.example.matefairy01.memory.DreamConfig
import com.example.matefairy01.memory.EmbeddingConfig
import com.example.matefairy01.memory.FactIngestConfig
import com.example.matefairy01.memory.IngestionConfig
import com.example.matefairy01.memory.MemoryConfig
import com.example.matefairy01.memory.RetrievalConfig
import org.json.JSONArray
import org.json.JSONObject

data class AppConfig(
    val ai: AIConfig = AIConfig(),
    val mcpServers: List<McpServerConfig> = emptyList(),
    val webSearch: WebSearchConfig = WebSearchConfig(),
    val memory: MemoryConfig = MemoryConfig()
)

data class AIConfig(
    val provider: LLMProviderType = LLMProviderType.MOCK,
    val systemPrompt: String = "You are a helpful fairy companion.",
    val deepseek: DeepSeekConfig = DeepSeekConfig()
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

/**
 * 联网搜索工具配置。
 *
 * 对应 app_config.json:
 * ```
 * "webSearch": {
 *   "enabled": true,
 *   "provider": "duckduckgo",   // duckduckgo | brave | tavily
 *   "apiKey": "",                 // brave / tavily 需要
 *   "maxResults": 5
 * }
 * ```
 */
data class WebSearchConfig(
    val enabled: Boolean = true,
    /** duckduckgo（免 key 兜底）| brave | tavily */
    val provider: String = "duckduckgo",
    val apiKey: String = "",
    val maxResults: Int = 5,
    val connectTimeoutMs: Long = 10_000L,
    val readTimeoutMs: Long = 15_000L
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
                mcpServers = parseMcpServers(root.optJSONObject("mcpServers")),
                webSearch = parseWebSearchConfig(root.optJSONObject("webSearch")),
                memory = parseMemoryConfig(root.optJSONObject("memory"))
            )
            cachedConfig = config
            return config
        }
    }

    private fun parseMemoryConfig(json: JSONObject?): MemoryConfig {
        val defaults = MemoryConfig()
        if (json == null) return defaults

        return MemoryConfig(
            consolidate = parseConsolidateConfig(json.optJSONObject("consolidate")),
            factIngest = parseFactIngestConfig(json.optJSONObject("factIngest")),
            dream = parseDreamConfig(json.optJSONObject("dream")),
            ingestion = parseIngestionConfig(json.optJSONObject("ingestion")),
            retrieval = parseRetrievalConfig(json.optJSONObject("retrieval")),
            embedding = parseEmbeddingConfig(json.optJSONObject("embedding"))
        )
    }

    private fun parseConsolidateConfig(json: JSONObject?): ConsolidateConfig {
        val defaults = ConsolidateConfig()
        if (json == null) return defaults
        return ConsolidateConfig(
            tokenThreshold = json.optInt("tokenThreshold", defaults.tokenThreshold),
            turnFallback = json.optInt("turnFallback", defaults.turnFallback)
        )
    }

    private fun parseFactIngestConfig(json: JSONObject?): FactIngestConfig {
        val defaults = FactIngestConfig()
        if (json == null) return defaults
        return FactIngestConfig(
            minChars = json.optInt("minChars", defaults.minChars),
            throttleMs = json.optLong("throttleMs", defaults.throttleMs)
        )
    }

    private fun parseDreamConfig(json: JSONObject?): DreamConfig {
        val defaults = DreamConfig()
        if (json == null) return defaults
        val override = json.optString("modelOverride", "").takeIf { it.isNotBlank() }
        return DreamConfig(
            idleMs = json.optLong("idleMs", defaults.idleMs),
            runOnPause = json.optBoolean("runOnPause", defaults.runOnPause),
            maxBatchSize = json.optInt("maxBatchSize", defaults.maxBatchSize),
            maxIterations = json.optInt("maxIterations", defaults.maxIterations),
            modelOverride = override
        )
    }

    private fun parseIngestionConfig(json: JSONObject?): IngestionConfig {
        val defaults = IngestionConfig()
        if (json == null) return defaults
        return IngestionConfig(
            flushTimeoutPerJobMs = json.optLong("flushTimeoutPerJobMs", defaults.flushTimeoutPerJobMs)
        )
    }

    private fun parseRetrievalConfig(json: JSONObject?): RetrievalConfig {
        val defaults = RetrievalConfig()
        if (json == null) return defaults
        return RetrievalConfig(
            weightSimilarity = json.optDouble("weightSimilarity", defaults.weightSimilarity.toDouble()).toFloat(),
            weightRecency = json.optDouble("weightRecency", defaults.weightRecency.toDouble()).toFloat(),
            weightImportance = json.optDouble("weightImportance", defaults.weightImportance.toDouble()).toFloat(),
            recencyHalfLifeDays = json.optDouble("recencyHalfLifeDays", defaults.recencyHalfLifeDays.toDouble()).toFloat(),
            topKEpisodic = json.optInt("topKEpisodic", defaults.topKEpisodic),
            topKFacts = json.optInt("topKFacts", defaults.topKFacts)
        )
    }

    private fun parseEmbeddingConfig(json: JSONObject?): EmbeddingConfig {
        val defaults = EmbeddingConfig()
        if (json == null) return defaults
        return EmbeddingConfig(
            provider = json.optString("provider", defaults.provider),
            baseUrl = json.optString("baseUrl", defaults.baseUrl).trimEnd('/'),
            apiKey = json.optString("apiKey", defaults.apiKey),
            model = json.optString("model", defaults.model),
            dimension = json.optInt("dimension", defaults.dimension),
            maxLength = json.optInt("maxLength", defaults.maxLength),
            connectTimeoutMs = json.optLong("connectTimeoutMs", defaults.connectTimeoutMs),
            readTimeoutMs = json.optLong("readTimeoutMs", defaults.readTimeoutMs),
            batchSize = json.optInt("batchSize", defaults.batchSize)
        )
    }

    private fun parseWebSearchConfig(json: JSONObject?): WebSearchConfig {
        val defaults = WebSearchConfig()
        if (json == null) return defaults
        return WebSearchConfig(
            enabled = json.optBoolean("enabled", defaults.enabled),
            provider = json.optString("provider", defaults.provider),
            apiKey = json.optString("apiKey", defaults.apiKey),
            maxResults = json.optInt("maxResults", defaults.maxResults),
            connectTimeoutMs = json.optLong("connectTimeoutMs", defaults.connectTimeoutMs),
            readTimeoutMs = json.optLong("readTimeoutMs", defaults.readTimeoutMs)
        )
    }

    private fun parseMcpServers(json: JSONObject?): List<McpServerConfig> {        if (json == null) return emptyList()
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
            deepseek = parseDeepSeekConfig(json.optJSONObject("deepseek"))
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
