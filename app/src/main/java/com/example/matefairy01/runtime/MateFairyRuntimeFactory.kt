package com.example.matefairy01.runtime

import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.ai.LLMProviderFactory
import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.avatar.DefaultAvatarController
import com.example.matefairy01.config.AppConfig
import com.example.matefairy01.emotion.EmotionEngine
import com.example.matefairy01.emotion.NoOpEmotionRenderer
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.orchestrator.ConversationOrchestrator
import com.example.matefairy01.orchestrator.adapters.ActionRegistryPortAdapter
import com.example.matefairy01.orchestrator.adapters.EmotionEnginePortAdapter

/**
 * 集中组装当前应用运行时所需的核心依赖，替代散落在场景层和全局单例中的装配逻辑。
 */
object MateFairyRuntimeFactory {
    fun create(appConfig: AppConfig): MateFairyRuntime {
        val llmProvider = LLMProviderFactory.create(appConfig.ai)
        val emotionRenderer = NoOpEmotionRenderer()
        val emotionEngine = EmotionEngine(emotionRenderer)
        val actionRegistry = ActionRegistry()
        val contextMemorySystem = ContextMemorySystem(llmProvider)
        val animationController = AnimationModule()
        val avatarController = DefaultAvatarController(animationController)

        val conversationOrchestrator = ConversationOrchestrator(
            llmProvider = llmProvider,
            contextMemorySystem = contextMemorySystem,
            emotionPort = EmotionEnginePortAdapter(emotionEngine),
            actionPort = ActionRegistryPortAdapter(actionRegistry)
        )

        return MateFairyRuntime(
            conversationOrchestrator = conversationOrchestrator,
            avatarController = avatarController
        )
    }
}
