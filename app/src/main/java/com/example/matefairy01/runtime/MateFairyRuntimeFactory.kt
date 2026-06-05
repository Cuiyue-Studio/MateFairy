package com.example.matefairy01.runtime

import android.content.Context
import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.action.handlers.GenericAnimationHandler
import com.example.matefairy01.action.handlers.SceneInteractionActionHandler
import com.example.matefairy01.ai.LLMProviderFactory
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.audio.MusicModule
import com.example.matefairy01.avatar.DefaultAvatarController
import com.example.matefairy01.behavior.FairyFollowControlModule
import com.example.matefairy01.config.AppConfig
import com.example.matefairy01.emotion.AvatarEmotionRenderer
import com.example.matefairy01.emotion.EmotionEngine
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.mcp.McpManager
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.orchestrator.ConversationOrchestrator
import com.example.matefairy01.orchestrator.adapters.ActionRegistryPortAdapter
import com.example.matefairy01.orchestrator.adapters.EmotionEnginePortAdapter

/**
 * 集中组装当前应用运行时所需的核心依赖，替代散落在场景层和全局单例中的装配逻辑。
 */
object MateFairyRuntimeFactory {
    fun create(appConfig: AppConfig, context: Context): MateFairyRuntime {
        val mcpManager = McpManager(appConfig.mcpServers)
        val llmProvider = LLMProviderFactory.create(appConfig.ai, mcpManager)
        val contextMemorySystem = ContextMemorySystem(llmProvider)
        val animationModule = AnimationModule()
        InteractionActionRuntimeDependencies.lockState.addListener(animationModule)
        InteractionActionRuntimeDependencies.lockState.addListener(FairyFollowControlModule)
        val avatarController = DefaultAvatarController(animationModule)
        val musicModule = MusicModule(context)

        val emotionRenderer = AvatarEmotionRenderer(animationModule)
        val emotionEngine = EmotionEngine(emotionRenderer)
        InteractionActionRuntimeDependencies.actionRegistry.register(PlayFootballActionController())

        val actionRegistry = ActionRegistry().apply {
            // 自动注册所有非任务型动画的通用 Handler
            AnimationConfig.supportedActions.filter { it != "none" }.forEach { intent ->
                register(GenericAnimationHandler(intent, animationModule))
            }
            register(
                SceneInteractionActionHandler(
                    intent = PlayFootballActionController.ACTION_ID,
                    requestBus = InteractionActionRuntimeDependencies.requestBus,
                    defaultObjectIds = listOf("football")
                )
            )
        }

        val conversationOrchestrator = ConversationOrchestrator(
            llmProvider = llmProvider,
            contextMemorySystem = contextMemorySystem,
            emotionPort = EmotionEnginePortAdapter(emotionEngine),
            actionPort = ActionRegistryPortAdapter(actionRegistry)
        )

        return MateFairyRuntime(
            conversationOrchestrator = conversationOrchestrator,
            avatarController = avatarController,
            animationModule = animationModule,
            mcpManager = mcpManager,
            musicModule = musicModule
        )
    }
}
