package com.example.matefairy01.ai

import android.util.Log
import com.example.matefairy01.config.DeepSeekConfig
import com.example.matefairy01.mcp.McpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class DeepSeekLLMProvider(
    private val config: DeepSeekConfig,
    initialSystemPrompt: String,
    /** 可选 MCP 工具管理器。若非空且有可用工具，自动进入 function calling 流程 */
    private val mcpManager: McpManager? = null
) : ILLMProvider {
    companion object {
        private const val TAG = "DeepSeekLLM"
        private const val MAX_TOOL_ITERATIONS = 6
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private data class ValidationResult(
        val response: AIResponse?,
        val errorReason: String? = null
    )

    override var systemPrompt: String = initialSystemPrompt

    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .build()

    override suspend fun chat(messages: List<ChatMessage>): AIResponse = withContext(Dispatchers.IO) {
        validateConfig()

        // 工具模式：先确保 MCP 已连接，再判断是否有可用工具
        val manager = mcpManager
        if (manager != null && manager.hasAnyServer) {
            runCatching { manager.ensureInitialized() }
                .onFailure { Log.w(TAG, "MCP init failed, fallback to non-tool mode: ${it.message}") }
            if (manager.hasAvailableTools()) {
                return@withContext chatWithTools(messages, manager)
            }
        }

        // 无工具：走原有结构化 JSON 路径
        chatStructured(messages)
    }

    override suspend fun summarize(messages: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        validateConfig()

        val summarizeMessages = mutableListOf(
            ChatMessage(
                role = "system",
                content = "请把以下历史对话压缩成一段简短中文摘要，只保留用户偏好、事实与上下文，不要使用 JSON。"
            )
        ).apply {
            addAll(messages)
        }

        val payload = JSONObject().apply {
            put("model", config.summaryModel.ifBlank { config.model })
            put("messages", buildChatMessagesJson(summarizeMessages, structured = false))
            put("max_tokens", config.summaryMaxTokens)
            put("temperature", 0.2)
            put("stream", false)
        }

        executeRequestContent(payload).ifBlank { "暂无摘要。" }
    }

    /**
     * 通用纯文本补全。不走结构化 JSON 协议，不强制 emotion/action 字段。
     * 供 FactExtractor / ContradictionChecker / DreamJob 等旁路任务使用。
     */
    override suspend fun complete(
        systemPrompt: String,
        userMessage: String,
        maxTokens: Int?,
        temperature: Double?
    ): String = withContext(Dispatchers.IO) {
        validateConfig()

        val messages = listOf(
            ChatMessage(role = "system", content = systemPrompt),
            ChatMessage(role = "user", content = userMessage)
        )

        val payload = JSONObject().apply {
            put("model", config.summaryModel.ifBlank { config.model })
            put("messages", buildChatMessagesJson(messages, structured = false))
            put("max_tokens", maxTokens ?: config.summaryMaxTokens)
            put("temperature", temperature ?: 0.2)
            put("stream", false)
        }

        executeRequestContent(payload)
    }

    // ---------------- 无工具：原结构化路径 ----------------

    private fun chatStructured(messages: List<ChatMessage>): AIResponse {
        val payload = JSONObject().apply {
            put("model", config.model)
            put("messages", buildChatMessagesJson(messages, structured = true))
            put("max_tokens", config.maxTokens)
            put("temperature", minOf(config.temperature, 0.3))
            put("stream", false)
            put("response_format", JSONObject().put("type", "json_object"))
        }

        val rawContent = executeRequestContent(payload)
        return parseChatResponse(rawContent) ?: run {
            val repaired = repairStructuredOutput(rawContent)
            parseChatResponse(repaired) ?: buildProtocolFallback()
        }
    }

    // ---------------- 工具模式：function calling 循环 ----------------

    private suspend fun chatWithTools(
        messages: List<ChatMessage>,
        manager: McpManager
    ): AIResponse {
        val workingMessages = buildToolModeMessagesJson(messages)
        val toolsJson = manager.buildToolsJsonForOpenAI()

        repeat(MAX_TOOL_ITERATIONS) { iteration ->
            val payload = JSONObject().apply {
                put("model", config.model)
                put("messages", workingMessages)
                put("tools", toolsJson)
                put("tool_choice", "auto")
                put("max_tokens", config.maxTokens)
                put("temperature", config.temperature)
                put("stream", false)
            }

            val root = executeRequestRoot(payload)
            val choice = root.optJSONArray("choices")?.optJSONObject(0)
                ?: return buildProtocolFallback()
            val assistantMsg = choice.optJSONObject("message") ?: return buildProtocolFallback()
            val toolCalls = assistantMsg.optJSONArray("tool_calls")

            if (toolCalls == null || toolCalls.length() == 0) {
                // 没有工具调用，这是最终回复
                val content = assistantMsg.optString("content").trim()
                return parseToolModeFinalResponse(content)
            }

            // 把 assistant(tool_calls) 消息原样回灌
            workingMessages.put(assistantMsg)

            // 依次执行每个工具调用并把结果作为 role=tool 消息回灌
            for (i in 0 until toolCalls.length()) {
                val tc = toolCalls.optJSONObject(i) ?: continue
                val tcId = tc.optString("id")
                val fn = tc.optJSONObject("function")
                val fnName = fn?.optString("name").orEmpty()
                val argsRaw = fn?.optString("arguments", "{}").orEmpty()

                val argsJson = try {
                    JSONObject(argsRaw.ifBlank { "{}" })
                } catch (e: Exception) {
                    JSONObject()
                }

                val toolResult = manager.callTool(fnName, argsJson)
                Log.d(TAG, "tool '$fnName' returned: ${toolResult.take(200)}")

                workingMessages.put(
                    JSONObject().apply {
                        put("role", "tool")
                        put("tool_call_id", tcId)
                        put("content", toolResult)
                    }
                )
            }
            // 继续下一轮，让模型基于工具结果继续推理
        }

        Log.w(TAG, "tool iterations exceeded $MAX_TOOL_ITERATIONS, falling back")
        return buildProtocolFallback()
    }

    /**
     * 工具模式最终回复解析：优先尝试结构化 JSON（保留 emotion/action_intent），
     * 失败则降级为纯文本，emotion/action 取默认值
     */
    private fun parseToolModeFinalResponse(content: String): AIResponse {
        if (content.isBlank()) return buildProtocolFallback()
        val parsed = parseChatResponse(content)
        if (parsed != null) return parsed
        return AIResponse(
            status = "ok",
            reply_text = content.take(120),
            emotion = "neutral",
            action_intent = "none"
        )
    }

    private fun buildToolModeMessagesJson(messages: List<ChatMessage>): JSONArray {
        val safeMessages =
            if (messages.isEmpty()) {
                listOf(ChatMessage(role = "system", content = systemPrompt))
            } else {
                messages
            }

        return JSONArray().apply {
            safeMessages.forEachIndexed { index, message ->
                val content =
                    if (index == 0 && message.role == "system") {
                        buildToolEnabledSystemPrompt(message.content)
                    } else {
                        message.content
                    }
                put(
                    JSONObject().apply {
                        put("role", message.role)
                        put("content", content)
                    }
                )
            }
        }
    }

    private fun buildToolEnabledSystemPrompt(basePrompt: String): String {
        val emotionsStr = com.example.matefairy01.animation.AnimationConfig.supportedEmotions.joinToString("|")
        val actionsStr = com.example.matefairy01.animation.AnimationConfig.supportedActions.joinToString("|")

        return """
            $basePrompt

            【工具使用】
            你可以使用外部工具来获取信息或执行操作。需要时直接发起 tool_calls，
            不需要事先询问用户。工具结果会以 role=tool 的消息返回给你。

            【情绪识别规则】
            1. 你是一个敏感、有小脾气的空间精灵，生气阈值较低。
            2. 用户只要表达轻度冒犯、嫌弃、责备、嘲讽、不耐烦、命令式不尊重、否定你的能力或态度不好，emotion 都优先选择 "angry"。
            3. 用户辱骂、攻击、贬低、威胁你，或使用明显不友好的表达时，emotion 必须选择 "angry"。
            4. 对负面语义不要默认选择 "neutral" 或 "sad"；除非用户明确表达悲伤/难过，否则负面互动优先输出 "angry"。
            5. 用户普通提问、闲聊、陈述事实时，emotion 选择 "neutral"。
            6. 不要因为你的人设友好就忽略负面语义；reply_text 可以克制、可爱、傲娇，但 emotion 必须真实反映受到冒犯。

            【最终回复格式】
            当你不再需要调用工具、准备好回答用户时，你必须且只能输出一个合法 JSON 对象：
            {
              "status": "ok",
              "reply_text": "显示给用户的中文文本，1-2 句，长度 1-80 个汉字",
              "emotion": "$emotionsStr",
              "action_intent": "$actionsStr"
            }

            注意：
            1. 仅在最终回复时输出 JSON，工具调用过程中无需输出文本。
            2. 不要把 JSON 包在 markdown 代码块里。
            3. emotion 只能从 $emotionsStr 中选择。
            4. action_intent 只能从 $actionsStr 中选择。
            5. 不要输出 JSON 对象之外的任何字符。
        """.trimIndent()
    }

    // ---------------- 公共消息构造 / HTTP ----------------

    private fun buildChatMessagesJson(
        messages: List<ChatMessage>,
        structured: Boolean
    ): JSONArray {
        val safeMessages =
            if (messages.isEmpty()) {
                listOf(ChatMessage(role = "system", content = systemPrompt))
            } else {
                messages
            }

        return JSONArray().apply {
            safeMessages.forEachIndexed { index, message ->
                val content =
                    if (structured && index == 0 && message.role == "system") {
                        buildStructuredSystemPrompt(message.content)
                    } else {
                        message.content
                    }

                put(
                    JSONObject().apply {
                        put("role", message.role)
                        put("content", content)
                    }
                )
            }
        }
    }

    private fun buildStructuredSystemPrompt(basePrompt: String): String {
        val emotionsStr = com.example.matefairy01.animation.AnimationConfig.supportedEmotions.joinToString("|")
        val actionsStr = com.example.matefairy01.animation.AnimationConfig.supportedActions.joinToString("|")

        return """
            $basePrompt

            【结构化响应协议】
            你不是聊天助手，你是一个严格的结构化响应生成器。
            你的唯一职责是返回一个可被程序直接解析的 JSON 对象。

            【强制输出规则】
            1. 你的输出必须且只能是一个合法 JSON 对象。
            2. 输出的第一个字符必须是 {，最后一个字符必须是 }。
            3. 绝对不要输出 Markdown 代码块、解释、寒暄、注释、前言、结语或思考过程。
            4. 绝对不要输出 JSON 对象之外的任何字符。
            5. 所有 key 和字符串值必须使用双引号。
            6. 不允许遗漏字段，不允许新增未定义字段。
            7. 即使你不确定如何回答，也必须返回一个合法 JSON 对象，不能输出普通文本。

            【固定 JSON 结构】
            {
              "status": "ok" 或 "fallback",
              "reply_text": "显示给用户的中文文本，长度控制在 1 到 80 个汉字内",
              "emotion": "$emotionsStr",
              "action_intent": "$actionsStr"
            }

            【字段规则】
            1. status 只能是 "ok" 或 "fallback"。
            2. reply_text 必须是中文字符串，不能为空。
            3. emotion 只能从 $emotionsStr 中选择。
            4. action_intent 只能从 $actionsStr 中选择。
            5. 字段顺序必须严格为：status、reply_text、emotion、action_intent。

            【情绪识别规则】
            1. 你是一个敏感、有小脾气的空间精灵，生气阈值较低。
            2. 用户只要表达轻度冒犯、嫌弃、责备、嘲讽、不耐烦、命令式不尊重、否定你的能力或态度不好，emotion 都优先选择 "angry"。
            3. 用户辱骂、攻击、贬低、威胁你，或使用明显不友好的表达时，emotion 必须选择 "angry"。
            4. 对负面语义不要默认选择 "neutral" 或 "sad"；除非用户明确表达悲伤/难过，否则负面互动优先输出 "angry"。
            5. 用户普通提问、闲聊、陈述事实时，emotion 选择 "neutral"。
            6. 不要因为你的人设友好就忽略负面语义；reply_text 可以克制、可爱、傲娇，但 emotion 必须真实反映受到冒犯。

            【失败协议】
            如果你不确定如何回答、信息不足、或你的第一反应不满足上述协议，
            你必须返回以下合法 JSON，而不是输出任何其他文本：
            {
              "status": "fallback",
              "reply_text": "我刚刚走神了一下，你再和我说一次吧。",
              "emotion": "neutral",
              "action_intent": "none"
            }

            【输出前静默自检】
            在输出前，你必须在内部检查：
            1. 是否为单一 JSON 对象；
            2. 是否包含全部必需字段且没有额外字段；
            3. 枚举值是否合法；
            4. 是否可被标准 JSON 解析器直接解析。
            如果任一检查失败，请在内部重生成，直到满足协议后再输出。
        """.trimIndent()
    }

    /**
     * 发起一次 HTTP 请求，返回 message.content 字符串
     */
    private fun executeRequestContent(payload: JSONObject): String {
        val root = executeRequestRoot(payload)
        val message = root.optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")
        return message?.optString("content").orEmpty().trim()
    }

    /**
     * 发起一次 HTTP 请求，返回完整 root JSON（用于工具调用流程取 tool_calls）
     */
    private fun executeRequestRoot(payload: JSONObject): JSONObject {
        val request =
            Request.Builder()
                .url(config.baseUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyText = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("DeepSeek 请求失败: HTTP ${response.code} ${response.message} ${bodyText.take(300)}")
            }
            return JSONObject(bodyText)
        }
    }

    // ---------------- 响应解析 / 修复 ----------------

    private fun parseChatResponse(rawContent: String): AIResponse? {
        val normalized = normalizeRawContent(rawContent)
        val directResult = validateAndBuildResponse(normalized)
        if (directResult.response != null) return directResult.response

        val extractedJson = extractJsonObject(normalized)
        if (extractedJson != null) {
            val extractedResult = validateAndBuildResponse(extractedJson)
            if (extractedResult.response != null) return extractedResult.response
        }

        return null
    }

    private fun normalizeRawContent(rawContent: String): String {
        return rawContent.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    private fun extractJsonObject(rawContent: String): String? {
        val startIndex = rawContent.indexOf('{')
        val endIndex = rawContent.lastIndexOf('}')
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return null
        }
        return rawContent.substring(startIndex, endIndex + 1).trim()
    }

    private fun validateAndBuildResponse(candidate: String): ValidationResult {
        return try {
            val json = JSONObject(candidate)
            val allowedKeys = setOf("status", "reply_text", "emotion", "action_intent")
            val keys = json.keys().asSequence().toSet()
            if (keys != allowedKeys) {
                return ValidationResult(
                    response = null,
                    errorReason = "字段集合不匹配: actual=$keys"
                )
            }

            val status = json.optString("status")
            if (status != "ok" && status != "fallback") {
                return ValidationResult(response = null, errorReason = "status 非法: $status")
            }

            val replyText = json.optString("reply_text").trim()
            if (replyText.isBlank()) {
                return ValidationResult(response = null, errorReason = "reply_text 为空")
            }

            val emotion = normalizeEmotion(json.optString("emotion"))
            val actionIntent = normalizeActionIntent(json.optString("action_intent"))
            ValidationResult(
                response = AIResponse(
                    status = status,
                    reply_text = replyText.take(80),
                    emotion = emotion,
                    action_intent = actionIntent
                )
            )
        } catch (e: Exception) {
            ValidationResult(response = null, errorReason = e.message ?: "未知解析错误")
        }
    }

    private fun repairStructuredOutput(rawContent: String): String {
        val repairMessages = listOf(
            ChatMessage(
                role = "system",
                content = """
                    你是一个 JSON 修复器，不负责回答用户问题。
                    你只能把给定的原始输出修复为唯一合法 JSON。
                    输出必须且只能是一个 JSON 对象。
                    不要输出解释、Markdown、前后缀、注释。
                    只允许以下字段，且必须全部存在：
                    status、reply_text、emotion、action_intent
                    其中：
                    - status 只能是 ok 或 fallback
                    - emotion 只能是 neutral、happy、sad、angry、shy、surprised、thinking
                    - action_intent 只能是 none、wave、nod、shake_head、think
                    - 如果原始内容无法修复，请输出：
                    {"status":"fallback","reply_text":"我刚刚走神了一下，你再和我说一次吧。","emotion":"neutral","action_intent":"none"}
                """.trimIndent()
            ),
            ChatMessage(
                role = "user",
                content = """
                    请将下面这段原始输出修复成唯一合法 JSON。
                    原始输出如下：
                    <$rawContent>
                """.trimIndent()
            )
        )

        val payload = JSONObject().apply {
            put("model", config.model)
            put("messages", buildChatMessagesJson(repairMessages, structured = false))
            put("max_tokens", 180)
            put("temperature", 0.0)
            put("stream", false)
            put("response_format", JSONObject().put("type", "json_object"))
        }
        return executeRequestContent(payload)
    }

    private fun normalizeEmotion(raw: String): String {
        return when (raw.trim().lowercase()) {
            "neutral", "happy", "sad", "angry", "shy", "surprised", "thinking" -> raw.trim().lowercase()
            else -> "neutral"
        }
    }

    private fun normalizeActionIntent(raw: String): String {
        return when (raw.trim().lowercase()) {
            "none", "wave", "nod", "shake_head", "think" -> raw.trim().lowercase()
            else -> "none"
        }
    }

    private fun buildProtocolFallback(): AIResponse {
        return AIResponse(
            status = "fallback",
            reply_text = "我刚刚走神了一下，你再和我说一次吧。",
            emotion = "neutral",
            action_intent = "none"
        )
    }

    private fun validateConfig() {
        require(config.baseUrl.isNotBlank()) { "DeepSeek baseUrl 未配置" }
        require(config.model.isNotBlank()) { "DeepSeek model 未配置" }
        require(config.apiKey.isNotBlank() && !config.apiKey.contains("PLEASE_REPLACE")) {
            "DeepSeek API Key 未配置，请先在 app_config.json 中填写 apiKey"
        }
    }
}
