package com.example.matefairy01.di

import android.content.Context
import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.behavior.BehaviorRuntimeDependencies
import com.example.matefairy01.config.AppConfigLoader
import com.example.matefairy01.mcp.McpManager
import com.example.matefairy01.orchestrator.ConversationOrchestrator
import com.example.matefairy01.runtime.MateFairyRuntime
import com.example.matefairy01.runtime.MateFairyRuntimeFactory

/**
 * 应用级依赖容器。组合 [MateFairyRuntime] 提供给 UI 层使用。
 *
 * 入口：调用 [initialize] 一次（幂等），随后通过字段访问运行时组件。
 *
 * 不在 Application 层做 initialize 的原因：HomeStage 需要 LocalContext 才能拿到
 * AppConfig（assets 必须经过 Context.assets），所以延后到 HomeStage 首次 Compose 时。
 */
object AppModule {
    @Volatile
    private var runtime: MateFairyRuntime? = null

    /** 幂等初始化。重复调用会复用已有 runtime（不重新连 MCP） */
    @Synchronized
    fun initialize(context: Context) {
        if (runtime != null) return
        val appConfig = AppConfigLoader.load(context.applicationContext)
        val newRuntime = MateFairyRuntimeFactory.create(appConfig, context.applicationContext)
        // 把 AvatarController 绑定给行为系统全局桥接对象
        BehaviorRuntimeDependencies.bindAvatarController(newRuntime.avatarController)
        runtime = newRuntime
    }

    /** 是否已经初始化 */
    val isReady: Boolean get() = runtime != null

    private fun requireRuntime(): MateFairyRuntime =
        runtime ?: error("AppModule not initialized. Call AppModule.initialize(context) first.")

    /** 对话编排器：用户输入 → LLM（含 MCP 工具调用） → 情绪/动作分发 → 回复文本 */
    val conversationOrchestrator: ConversationOrchestrator
        get() = requireRuntime().conversationOrchestrator

    /** 动画模块：HomeStage 初始化精灵模型后用它播放骨骼动画 */
    val animationModule: AnimationModule
        get() = requireRuntime().animationModule

    /** MCP 工具管理器：暴露用于 debug 或健康检查 */
    val mcpManager: McpManager
        get() = requireRuntime().mcpManager

    /** 音乐模块：用于全局播放背景音乐等 */
    val musicModule: com.example.matefairy01.audio.MusicModule
        get() = requireRuntime().musicModule
}
