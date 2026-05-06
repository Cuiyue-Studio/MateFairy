package com.example.matefairy01.action

import com.pico.spatial.core.ecs.Entity

/**
 * 动作处理器接口，用于解耦不同的精灵动作逻辑
 */
interface IActionHandler {
    /**
     * 该 Handler 处理的意图标识，如 "come_to_player", "dance" 等
     */
    val intent: String

    /**
     * 执行具体动作
     * @param fairyEntity 精灵的实体，用于控制动画或移动
     * @param params 附加参数（可选）
     */
    suspend fun execute(fairyEntity: Entity, params: Map<String, Any> = emptyMap())
}
