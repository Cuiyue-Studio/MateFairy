package com.example.matefairy01.ai

import com.example.matefairy01.config.DeepSeekConfig
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
    initialSystemPrompt: String
) : ILLMProvider {

    override var systemPrompt: String = initialSystemPrompt

    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeoutMs, TimeUnit.MILLISECONDS)
            .build()

    override suspend fun chat(messages: List<ChatMessage>): AIResponse = withContext(Dispatchers.IO) {
        validateConfig()

        val payload = JSONObject().apply {
            put("model", config.model)
            put("messages", buildChatMessagesJson(messages, structured = true))
            put("max_tokens", config.maxTokens)
            put("temperature", config.temperature)
            put("stream", false)
            put("response_format", JSONObject().put("type", "json_object"))
        }

        parseChatResponse(executeRequest(payload))
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

        executeRequest(payload).ifBlank { "暂无摘要。" }
    }

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
        return """
            $basePrompt

            【严格输出格式要求】
            你是一个严格的接口服务器，你的所有回复都必须且只能是一个合法的 JSON 对象。
            绝对不要输出任何 Markdown 标记（如 ```json 等包围符号）。
            绝对不要输出任何前置寒暄、解释性文字或思考过程。
            请确保你的输出可以被原生的 JSON 解析器直接解析。

            返回的 JSON 结构必须严格如下：
            {
              "reply_text": "显示在精灵气泡中的对话文本",
              "emotion": "neutral/happy/sad 等简短情绪标签",
              "action_intent": "none 或未来可扩展动作意图"
            }
        """.trimIndent()
    }

    private fun executeRequest(payload: JSONObject): String {
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

            val root = JSONObject(bodyText)
            val choices = root.optJSONArray("choices")
            val firstMessage = choices?.optJSONObject(0)?.optJSONObject("message")
            return firstMessage?.optString("content").orEmpty().trim()
        }
    }

    private fun parseChatResponse(rawContent: String): AIResponse {
        val normalized = rawContent.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val json = JSONObject(normalized)
            AIResponse(
                reply_text = json.optString("reply_text").ifBlank { "我刚刚走神了一下，再和我说一遍吧。" },
                emotion = json.optString("emotion", "neutral"),
                action_intent = json.optString("action_intent", "none")
            )
        } catch (e: Exception) {
            // 如果 JSON 解析失败，可能是大模型没有按照要求的 JSON 格式返回，
            // 而是直接返回了纯文本。此时我们不应该直接报错或抛弃，
            // 而是尝试将这段文本作为兜底的 reply_text 显示出来。
            
            // 为了防止出现 "```json\n{" 这样的格式被不完全裁剪导致显示出来，
            // 我们可以在这里做一个更加粗暴的 JSON 花括号匹配提取
            val fallbackText = if (normalized.isNotBlank()) {
                val startIndex = normalized.indexOf('{')
                val endIndex = normalized.lastIndexOf('}')
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    try {
                        val extractedJson = JSONObject(normalized.substring(startIndex, endIndex + 1))
                        return AIResponse(
                            reply_text = extractedJson.optString("reply_text").ifBlank { "我刚刚走神了一下，再和我说一遍吧。" },
                            emotion = extractedJson.optString("emotion", "neutral"),
                            action_intent = extractedJson.optString("action_intent", "none")
                        )
                    } catch (inner: Exception) {
                        // 提取出来的仍然不是有效的 JSON，那就只能把内容当作普通文本返回了
                        normalized
                    }
                } else {
                    normalized
                }
            } else {
                "我刚刚走神了一下，再和我说一遍吧。"
            }
            
            AIResponse(
                reply_text = fallbackText,
                emotion = "neutral",
                action_intent = "none"
            )
        }
    }

    private fun validateConfig() {
        require(config.baseUrl.isNotBlank()) { "DeepSeek baseUrl 未配置" }
        require(config.model.isNotBlank()) { "DeepSeek model 未配置" }
        require(config.apiKey.isNotBlank() && !config.apiKey.contains("PLEASE_REPLACE")) {
            "DeepSeek API Key 未配置，请先在 app_config.json 中填写 apiKey"
        }
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
