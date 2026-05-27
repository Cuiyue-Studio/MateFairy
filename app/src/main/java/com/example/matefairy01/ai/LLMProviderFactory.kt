package com.example.matefairy01.ai

import com.example.matefairy01.config.AIConfig
import com.example.matefairy01.config.LLMProviderType
import com.example.matefairy01.mcp.McpManager

object LLMProviderFactory {
    fun create(config: AIConfig, mcpManager: McpManager? = null): ILLMProvider {
        return when (config.provider) {
            LLMProviderType.DEEPSEEK -> {
                DeepSeekLLMProvider(
                    config = config.deepseek,
                    initialSystemPrompt = config.systemPrompt,
                    mcpManager = mcpManager
                )
            }

            LLMProviderType.MOCK -> {
                MockLLMProvider().apply {
                    systemPrompt = config.systemPrompt
                }
            }
        }
    }
}
