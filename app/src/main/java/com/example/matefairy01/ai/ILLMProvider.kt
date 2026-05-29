package com.example.matefairy01.ai

/**
 * 抽象的 LLM 提供者接口，方便后续切换不同大模型（如豆包、DeepSeek 等）
 */
interface ILLMProvider {
    /**
     * 基础系统设定 (System Prompt)
     */
    var systemPrompt: String
    
    /**
     * 发送完整对话历史请求，返回 JSON 解析后的 AIResponse
     */
    suspend fun chat(messages: List<ChatMessage>): AIResponse
    
    /**
     * 用于后台压缩长对话为摘要
     */
    suspend fun summarize(messages: List<ChatMessage>): String

    /**
     * 通用纯文本补全。供记忆系统的 FactExtractor / ContradictionChecker 等
     * 旁路任务使用，**不走主对话的结构化 JSON 协议与 emotion/action 约束**。
     *
     * @param systemPrompt 任务专属 system prompt
     * @param userMessage 任务输入文本
     * @param maxTokens 输出上限；null 表示用 provider 默认
     * @param temperature 温度；null 表示 provider 默认
     * @return 模型 raw content 字符串（已 trim）
     */
    suspend fun complete(
        systemPrompt: String,
        userMessage: String,
        maxTokens: Int? = null,
        temperature: Double? = null
    ): String
}
