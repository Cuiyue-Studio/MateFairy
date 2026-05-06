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
}
