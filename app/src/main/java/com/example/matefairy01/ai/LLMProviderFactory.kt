package com.example.matefairy01.ai

import com.example.matefairy01.config.AIConfig
import com.example.matefairy01.config.LLMProviderType

object LLMProviderFactory {
    fun create(config: AIConfig): ILLMProvider {
        return when (config.provider) {
            LLMProviderType.DEEPSEEK -> {
                DeepSeekLLMProvider(
                    config = config.deepseek,
                    initialSystemPrompt = config.systemPrompt
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
