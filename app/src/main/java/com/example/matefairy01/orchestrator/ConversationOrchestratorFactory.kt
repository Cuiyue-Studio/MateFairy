package com.example.matefairy01.orchestrator

import com.example.matefairy01.ai.LLMProviderFactory
import com.example.matefairy01.config.AppConfig
import com.example.matefairy01.di.AppModule
import com.example.matefairy01.emotion.NoOpEmotionRenderer
import com.example.matefairy01.orchestrator.adapters.ActionRegistryPortAdapter
import com.example.matefairy01.orchestrator.adapters.EmotionEnginePortAdapter

/**
 * 统一收口对话编排相关依赖的组装逻辑，避免场景层直接感知具体依赖图。
 */
object ConversationOrchestratorFactory {
    fun create(appConfig: AppConfig): ConversationOrchestrator {
        val provider = LLMProviderFactory.create(appConfig.ai)
        AppModule.initialize(
            provider = provider,
            renderer = NoOpEmotionRenderer()
        )

        return ConversationOrchestrator(
            llmProvider = AppModule.llmProvider,
            contextMemorySystem = AppModule.contextMemorySystem,
            emotionPort = EmotionEnginePortAdapter(
                emotionEngine = AppModule.emotionEngine
            ),
            actionPort = ActionRegistryPortAdapter(
                actionRegistry = AppModule.actionRegistry
            )
        )
    }
}
