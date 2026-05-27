package com.example.matefairy01.animation

enum class AnimationType {
    IDLE,
    MOVING
}

/**
 * 动画配置映射
 *
 * 根据模型文件 (pico_robot_animated.glb) 中的轨道索引映射：
 * STANDBY_MODE(0) - 待机
 * SPIN_LEAP(1) - 旋转跳跃
 * CURIOUS_LOOK(2) - 好奇张望
 * TURBO_DASH(3) - 极速冲刺
 * HELLO_WAVE(4) - 挥手打招呼
 */
enum class FairyAnimation(
    val trackIndex: Int, 
    val animName: String, 
    val type: AnimationType, 
    val durationMs: Long // 估计动画时长，用于计时器
) {
    STANDBY_MODE(0, "Standby", AnimationType.IDLE, 3000L),
    SPIN_LEAP(1, "Spin Leap", AnimationType.MOVING, 2000L),
    CURIOUS_LOOK(2, "Curious Look", AnimationType.IDLE, 2500L),
    TURBO_DASH(3, "Turbo Dash", AnimationType.MOVING, 1500L),
    HELLO_WAVE(4, "Hello Wave", AnimationType.IDLE, 2000L),
    
    // 新增的发狂动画，假设动画师按照建议改名后排在第 6 位（索引为 5）
    MAD_ACTION(5, "Mad Action", AnimationType.MOVING, 3000L)
}

object AnimationConfig {
    val idleAnimations = FairyAnimation.values().filter { it.type == AnimationType.IDLE }
    val movingAnimations = FairyAnimation.values().filter { it.type == AnimationType.MOVING }
}
