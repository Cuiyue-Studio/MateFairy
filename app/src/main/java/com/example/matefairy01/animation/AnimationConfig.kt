package com.example.matefairy01.animation

enum class AnimationPriority {
    INSTRUCTION, // 1. 最高优：指令动作 (非任务型/任务型)
    EMOTION,     // 2. 中优先级：情绪反应
    BASE         // 3. 兜底：常驻动画 (闲置/移动)
}

enum class AnimationType(val priority: AnimationPriority) {
    BASE_IDLE(AnimationPriority.BASE),
    BASE_MOVING(AnimationPriority.BASE),
    EMOTION_REACTION(AnimationPriority.EMOTION),
    NON_TASK_ACTION(AnimationPriority.INSTRUCTION)
}

/**
 * 动画配置映射
 *
 * 根据模型文件 (pico_robot_animated.glb) 中的轨道索引映射：
 * 0: 05_mad_action
 * 1: Action (无用轨道，忽略)
 * 2: idle
 * 3: jump
 * 4: look_around
 * 5: walk_forward
 * 6: wave
 */
enum class FairyAnimation(
    val trackIndex: Int, 
    val animName: String, 
    val type: AnimationType, 
    val durationMs: Long,
    val intent: String? = null // 用于自动注入到 LLM Prompt 中的意图名称
) {
    // --- 情绪动画 (EMOTION) ---
    MAD_ACTION(0, "Mad Action", AnimationType.EMOTION_REACTION, 3000L, "angry"),
    HAPPY_JUMP(3, "Happy Jump", AnimationType.EMOTION_REACTION, 2000L, "happy"), // 复用 jump 动画作为开心反应
    SHY_LOOK(4, "Shy Look", AnimationType.EMOTION_REACTION, 2500L, "shy"), // 复用 look_around 作为害羞反应

    // --- 指令动画 (ACTION) ---
    HELLO_WAVE(6, "Hello Wave", AnimationType.NON_TASK_ACTION, 2000L, "wave"),

    // --- 常驻动画 (BASE) ---
    STANDBY_MODE(2, "Standby", AnimationType.BASE_IDLE, 3000L),
    SPIN_LEAP(3, "Spin Leap", AnimationType.BASE_MOVING, 2000L),
    CURIOUS_LOOK(4, "Curious Look", AnimationType.BASE_IDLE, 2500L),
    TURBO_DASH(5, "Turbo Dash", AnimationType.BASE_MOVING, 1500L)
}

object AnimationConfig {
    val idleAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_IDLE }
    val movingAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_MOVING }

    // 提供给 LLM 的动态支持列表
    val supportedEmotions: List<String> = (listOf("neutral", "sad", "surprised", "thinking") + 
        FairyAnimation.values().filter { it.type == AnimationType.EMOTION_REACTION }.mapNotNull { it.intent }).distinct()
    
    val supportedActions: List<String> = (listOf("none", "fetch_ball") + 
        FairyAnimation.values().filter { it.type == AnimationType.NON_TASK_ACTION }.mapNotNull { it.intent }).distinct()

    fun getAnimationByEmotion(emotion: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.EMOTION_REACTION && it.intent == emotion }
    }

    fun getAnimationByAction(action: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.NON_TASK_ACTION && it.intent == action }
    }
}
