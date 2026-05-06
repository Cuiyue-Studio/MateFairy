package com.example.matefairy01.di

import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.emotion.EmotionEngine
import com.example.matefairy01.emotion.IEmotionRenderer
import com.example.matefairy01.memory.ContextMemorySystem

/**
 * 轻量级的 Service Locator（服务定位器）或手动依赖注入模块
 * 在初始阶段避免引入过重的 DI 框架（如 Hilt），保证灵活性。
 */
object AppModule {

    lateinit var llmProvider: ILLMProvider
    lateinit var emotionRenderer: IEmotionRenderer

    // 独立的动画管理模块
    val animationModule by lazy {
        AnimationModule()
    }

    // 单例注册表
    val actionRegistry by lazy {
        ActionRegistry()
    }

    // 情绪引擎
    val emotionEngine by lazy {
        require(::emotionRenderer.isInitialized) { "emotionRenderer must be initialized first" }
        EmotionEngine(emotionRenderer)
    }

    // 智能上下文记忆系统
    val contextMemorySystem by lazy {
        require(::llmProvider.isInitialized) { "llmProvider must be initialized first" }
        ContextMemorySystem(llmProvider)
    }

    /**
     * 游戏/应用启动时进行核心组件的初始化
     */
    fun initialize(
        provider: ILLMProvider,
        renderer: IEmotionRenderer
    ) {
        this.llmProvider = provider
        this.emotionRenderer = renderer
        
        // 此处可以注册基础的 ActionHandler
        // actionRegistry.register(ComeToPlayerActionHandler())
    }
}
