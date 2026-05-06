package com.example.matefairy01.action

import android.util.Log
import com.pico.spatial.core.ecs.Entity

/**
 * 动作注册表，采用策略模式分发动作意图
 */
class ActionRegistry {
    private val handlers = mutableMapOf<String, IActionHandler>()

    /**
     * 注册动作处理器
     */
    fun register(handler: IActionHandler) {
        handlers[handler.intent] = handler
        Log.d("ActionRegistry", "Registered handler for intent: ${handler.intent}")
    }

    /**
     * 移除动作处理器
     */
    fun unregister(intent: String) {
        handlers.remove(intent)
    }

    /**
     * 根据 intent 分发并执行动作
     */
    suspend fun dispatchAction(intent: String, fairyEntity: Entity, params: Map<String, Any> = emptyMap()) {
        if (intent.isEmpty() || intent == "none") return
        
        val handler = handlers[intent]
        if (handler != null) {
            handler.execute(fairyEntity, params)
        } else {
            Log.w("ActionRegistry", "No handler found for intent: $intent")
        }
    }
}
