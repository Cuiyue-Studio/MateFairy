package com.example.matefairy01.behavior

import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.math.Vector3

enum class FairyState {
    RANDOM_MOVING,      // 随机运动状态
    RANDOM_WAITING,     // 随机运动间的等待/悬浮状态
    FOLLOWING,          // 跟随玩家状态
    FOLLOW_HOVERING     // 跟随结束后的悬停状态（面朝玩家）
}

class FairyBehaviorComponent(
    var innerRadius: Float = 1.0f,
    var outerRadius: Float = 1.5f,
    var speed: Float = 0.5f,
    var followSpeed: Float = 1.5f,
    var hoverHeight: Float = 0.2f, // Relative to HMD
    var zDeviationRange: Float = 0.5f // Z-axis deviation range from HMD Z position
) : Component() {
    var state: FairyState = FairyState.RANDOM_MOVING
    var currentTarget: Vector3? = null
    var waitTimer: Float = 0f
    var isWaitingForAnimation: Boolean = false // 是否正在等待静止时动画播放完毕
    
    var velocity: Vector3 = Vector3(0f, 0f, 0f)
    var currentYaw: Float = 0f
    
    // Initial rotation from the model's USD transform (to keep the robot standing upright)
    // These are set once when the component is attached and should not be modified
    var initialPitch: Float = 0f
    var initialRoll: Float = 0f
    var initialYaw: Float = 0f
    var hasRecordedInitialRotation: Boolean = false
    
    // Floating effect parameters
    var floatTime: Float = 0f
    var floatAmplitude: Float = 0.05f // 5cm float range
    var floatSpeed: Float = 2.0f // Float cycle speed
    var baseY: Float = 0f // Base Y position when idle
    
    // ============================================
    // Inertia sliding system (no tilt)
    // ============================================
    
    // Current inertial velocity (separate from steering velocity for smooth deceleration)
    var inertiaVelocity: Vector3 = Vector3(0f, 0f, 0f)
    
    // Whether the entity is currently in inertia sliding phase
    var isInertiaSliding: Boolean = false
    
    // Deceleration rate during inertia sliding (higher = faster stop)
    var inertiaDeceleration: Float = 2.0f
    
    // Minimum speed to continue inertia sliding
    var inertiaStopThreshold: Float = 0.05f
}
