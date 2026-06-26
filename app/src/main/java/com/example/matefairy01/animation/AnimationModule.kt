package com.example.matefairy01.animation

import android.util.Log
import com.example.matefairy01.interaction.InteractionActionListener
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.InteractionActionStatus
import com.example.matefairy01.playerinteraction.PlayerFairyAnimationScheduler
import com.example.matefairy01.playerinteraction.PlayerFairyInteractionRuntimeDependencies
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.resource.AnimationResource
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 统一动画调度中心 (Master Animation Scheduler)
 * 具有严格优先级和生命周期的状态机：
 * 1. 指令层 (Instruction): 最高优，独占控制权。
 * 2. 情绪反应层 (Emotion Reaction): 中优，单次触发，播完降级。
 * 3. 常驻基础层 (Base Idle/Moving): 最低优，永远兜底。
 */
class AnimationModule :
    AnimationController,
    ActionAnimationScheduler,
    PlayerFairyAnimationScheduler,
    InteractionActionListener {
    companion object {
        private const val TAG = "AnimationModule"
    }

    private var skinnedMeshEntity: Entity? = null
    private var animationResources: Array<AnimationResource>? = null
    
    private var activeFacingPolicy: AnimationFacingPolicy = AnimationFacingPolicy.KEEP_BEHAVIOR
    override val currentFacingPolicy: AnimationFacingPolicy
        get() = activeFacingPolicy

    var currentPlayingAnim: FairyAnimation? = null
        private set

    private val moduleScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var activeJob: Job? = null
    private var queuedInstruction: FairyAnimation? = null
    private var activeActionAnimationOwnerId: String? = null
    private var loopingActionAnimationOwnerId: String? = null
    private var loopingActionAnimation: FairyAnimation? = null
    private val lifecycleListeners = CopyOnWriteArrayList<AnimationLifecycleListener>()

    // 频控相关
    private var lastIdleAnimPlayTime: Long = 0
    // 静止动画的冷却时间（毫秒），避免频繁执行显得多动症
    private val IDLE_ANIM_COOLDOWN_MS = 6000L 

    override fun initialize(robotEntity: Entity) {
        val skinnedMeshEntities = robotEntity.findSkinnedMeshEntity().toList()
        if (skinnedMeshEntities.isNotEmpty()) {
            this.skinnedMeshEntity = skinnedMeshEntities.first()
            this.animationResources = this.skinnedMeshEntity?.getAnimationResources()
            Log.d(TAG, "Initialized animation resources: count=${animationResources?.size ?: 0}")
        } else {
            Log.w(TAG, "No skinned mesh entity found; animations will not play")
        }
    }

    /**
     * 对外提供的通用播放接口，内置优先级与序列队列处理
     */
    override fun playAnimation(animation: FairyAnimation) {
        if (InteractionActionRuntimeDependencies.lockState.isLocked) {
            Log.d(TAG, "Ignore ${animation.animName}: interaction action lock is active")
            return
        }

        val resources = animationResources
        if (resources == null) {
            Log.w(TAG, "Ignore ${animation.animName}: animation resources not initialized")
            return
        }
        if (animation.trackIndex !in resources.indices) {
            Log.w(
                TAG,
                "Ignore ${animation.animName}: trackIndex=${animation.trackIndex}, resources=${resources.size}"
            )
            return
        }

        val incomingPriority = animation.type.priority
        val currentPriority = currentPlayingAnim?.type?.priority ?: AnimationPriority.BASE

        when (incomingPriority) {
            AnimationPriority.INSTRUCTION -> {
                if (currentPriority == AnimationPriority.EMOTION) {
                    // 如果正在播放情绪，将指令加入队列，串行执行
                    queuedInstruction = animation
                    return
                }
                // 否则直接打断播放
                executePlay(animation, facingPolicy = animation.defaultFacingPolicy())
            }
            AnimationPriority.EMOTION -> {
                if (currentPriority == AnimationPriority.INSTRUCTION) {
                    // 正在执行高优指令，忽略情绪
                    return
                }
                // 打断兜底动画，播放情绪
                executePlay(animation, facingPolicy = animation.defaultFacingPolicy())
            }
            AnimationPriority.BASE -> {
                if (currentPriority == AnimationPriority.INSTRUCTION || currentPriority == AnimationPriority.EMOTION) {
                    // 忽略基础动画请求，维持高优状态
                    return
                }
                executePlay(animation, facingPolicy = animation.defaultFacingPolicy())
            }
        }
    }

    override fun playActionAnimation(
        ownerActionId: String,
        animation: FairyAnimation,
        options: ActionAnimationPlayOptions
    ): Boolean {
        val lockState = InteractionActionRuntimeDependencies.lockState
        if (lockState.currentActionId != ownerActionId) {
            Log.d(
                TAG,
                "Ignore action animation ${animation.animName}: owner=$ownerActionId, lock=${lockState.currentActionId}"
            )
            return false
        }
        if (!canPlay(animation)) {
            publishLifecycle(ownerActionId, animation, AnimationLifecycleState.FAILED, AnimationEndReason.RESOURCE_MISSING)
            return false
        }

        activeActionAnimationOwnerId = ownerActionId
        publishLifecycle(ownerActionId, animation, AnimationLifecycleState.STARTED)
        val completionDelayMs = options.maxDurationSeconds
            ?.let { (it * 1000L).toLong().coerceAtLeast(0L) }
            ?: animation.durationMs
        executePlay(
            animation = animation,
            completionDelayMs = completionDelayMs,
            publishCompletionEvent = options.publishCompletionEvent,
            facingPolicy = AnimationFacingPolicy.KEEP_ACTION_TARGET
        )
        return true
    }

    override fun subscribeAnimationLifecycle(
        listener: AnimationLifecycleListener
    ): AnimationLifecycleSubscription {
        lifecycleListeners.add(listener)
        return AnimationLifecycleSubscription {
            lifecycleListeners.remove(listener)
        }
    }

    override fun playPlayerFairyInteractionAnimation(
        ownerInteractionId: String,
        animation: FairyAnimation
    ): Boolean {
        if (PlayerFairyInteractionRuntimeDependencies.state.currentInteractionId != ownerInteractionId) {
            Log.d(
                TAG,
                "Ignore player-fairy animation ${animation.animName}: owner=$ownerInteractionId, active=${PlayerFairyInteractionRuntimeDependencies.state.currentInteractionId}"
            )
            return false
        }
        if (!canPlay(animation)) return false
        activeActionAnimationOwnerId = ownerInteractionId
        executePlay(animation, facingPolicy = AnimationFacingPolicy.FACE_PLAYER)
        return true
    }

    override fun stopPlayerFairyInteractionAnimation(ownerInteractionId: String) {
        if (activeActionAnimationOwnerId == ownerInteractionId) {
            stopCurrentPlayback(clearLooping = false)
        }
    }

    override fun isActionAnimationPlaying(ownerActionId: String, animation: FairyAnimation): Boolean {
        return activeActionAnimationOwnerId == ownerActionId && currentPlayingAnim == animation
    }

    override fun startLoopingActionAnimation(ownerActionId: String, animation: FairyAnimation): Boolean {
        val lockState = InteractionActionRuntimeDependencies.lockState
        if (lockState.currentActionId != ownerActionId) {
            Log.d(
                TAG,
                "Ignore looping action animation ${animation.animName}: owner=$ownerActionId, lock=${lockState.currentActionId}"
            )
            return false
        }
        if (!canPlay(animation)) return false

        loopingActionAnimationOwnerId = ownerActionId
        loopingActionAnimation = animation
        activeActionAnimationOwnerId = ownerActionId
        startLoopingJob(ownerActionId, animation)
        return true
    }

    override fun stopLoopingActionAnimation(ownerActionId: String) {
        if (loopingActionAnimationOwnerId != ownerActionId) return
        loopingActionAnimationOwnerId = null
        loopingActionAnimation = null
        stopAllAnimations()
    }

    private fun startLoopingJob(ownerActionId: String, animation: FairyAnimation) {
        activeJob?.cancel()
        activeJob = moduleScope.launch {
            while (loopingActionAnimationOwnerId == ownerActionId && loopingActionAnimation == animation) {
                executePlay(
                    animation = animation,
                    scheduleCompletion = false,
                    facingPolicy = AnimationFacingPolicy.KEEP_ACTION_TARGET
                )
                delay(animation.durationMs)
            }
        }
    }

    private fun resumeLoopingActionAnimationIfNeeded() {
        val ownerActionId = loopingActionAnimationOwnerId ?: return
        val animation = loopingActionAnimation ?: return
        if (!canPlay(animation)) return
        activeActionAnimationOwnerId = ownerActionId
        startLoopingJob(ownerActionId, animation)
    }

    private fun canPlay(animation: FairyAnimation): Boolean {
        val resources = animationResources
        if (resources == null) {
            Log.w(TAG, "Ignore ${animation.animName}: animation resources not initialized")
            return false
        }
        if (animation.trackIndex !in resources.indices) {
            Log.w(
                TAG,
                "Ignore ${animation.animName}: trackIndex=${animation.trackIndex}, resources=${resources.size}"
            )
            return false
        }
        return true
    }

    private fun executePlay(
        animation: FairyAnimation,
        scheduleCompletion: Boolean = true,
        completionDelayMs: Long = animation.durationMs,
        publishCompletionEvent: Boolean = false,
        facingPolicy: AnimationFacingPolicy = animation.defaultFacingPolicy()
    ) {
        val resources = animationResources ?: return
        val meshEntity = skinnedMeshEntity ?: return

        if (scheduleCompletion) {
            activeJob?.cancel() // 取消之前的计时器
        }

        Log.d(TAG, "Play ${animation.animName}, type=${animation.type}, trackIndex=${animation.trackIndex}")
        meshEntity.playAnimation(resources[animation.trackIndex])
        currentPlayingAnim = animation
        activeFacingPolicy = facingPolicy
        
        if (animation.type == AnimationType.BASE_IDLE) {
            lastIdleAnimPlayTime = System.currentTimeMillis()
        }

        // 状态维持与自动降级：情绪或指令，开启协程等待播完
        if (scheduleCompletion && (animation.type.priority == AnimationPriority.EMOTION || animation.type.priority == AnimationPriority.INSTRUCTION)) {
            activeJob = moduleScope.launch {
                delay(completionDelayMs)
                onAnimationComplete(
                    animation = animation,
                    publishCompletionEvent = publishCompletionEvent,
                    endReason = if (completionDelayMs < animation.durationMs) {
                        AnimationEndReason.CLIPPED
                    } else {
                        AnimationEndReason.NATURAL_END
                    }
                )
            }
        }
    }

    private fun onAnimationComplete(
        animation: FairyAnimation,
        publishCompletionEvent: Boolean = false,
        endReason: AnimationEndReason = AnimationEndReason.NATURAL_END
    ) {
        if (currentPlayingAnim != animation) return // 已经被打断
        val ownerActionId = activeActionAnimationOwnerId
        activeActionAnimationOwnerId = null
        if (publishCompletionEvent && ownerActionId != null) {
            publishLifecycle(ownerActionId, animation, AnimationLifecycleState.COMPLETED, endReason)
        }

        if (animation.type.priority == AnimationPriority.EMOTION) {
            // 情绪播完了，看有没有排队的指令
            val queued = queuedInstruction
            if (queued != null) {
                queuedInstruction = null
                executePlay(queued, facingPolicy = queued.defaultFacingPolicy())
                return
            }
        }
        
        // 自动降级到 BASE (清除状态，等待 BehaviorSystem 再次请求 BASE)
        currentPlayingAnim = null
        activeFacingPolicy = AnimationFacingPolicy.KEEP_BEHAVIOR
    }

    private fun publishLifecycle(
        ownerActionId: String?,
        animation: FairyAnimation,
        state: AnimationLifecycleState,
        reason: AnimationEndReason? = null
    ) {
        val event = AnimationLifecycleEvent(
            ownerActionId = ownerActionId,
            animation = animation,
            state = state,
            reason = reason
        )
        lifecycleListeners.forEach { listener ->
            runCatching { listener.onAnimationLifecycleEvent(event) }
                .onFailure { Log.w(TAG, "Animation lifecycle listener failed", it) }
        }
    }

    fun stopAllAnimations() {
        stopCurrentPlayback(clearLooping = true)
    }

    private fun stopCurrentPlayback(clearLooping: Boolean) {
        skinnedMeshEntity?.stopAllAnimations()
        currentPlayingAnim = null
        activeActionAnimationOwnerId = null
        activeFacingPolicy = AnimationFacingPolicy.KEEP_BEHAVIOR
        if (clearLooping) {
            loopingActionAnimationOwnerId = null
            loopingActionAnimation = null
        }
        activeJob?.cancel()
        queuedInstruction = null
    }

    override fun cleanup() {
        InteractionActionRuntimeDependencies.lockState.removeListener(this)
        stopAllAnimations()
        moduleScope.cancel()
        animationResources?.forEach { it.close() }
        animationResources = null
        skinnedMeshEntity = null
    }

    /**
     * 判断是否可以播放静止时动画（频控）
     */
    fun canPlayIdleAnimation(): Boolean {
        return System.currentTimeMillis() - lastIdleAnimPlayTime > IDLE_ANIM_COOLDOWN_MS
    }

    /**
     * 随机播放一个静止时动画
     */
    override fun playRandomIdleAnimation(): FairyAnimation? {
        if (!canPlayIdleAnimation()) return null
        
        val anim = AnimationConfig.idleAnimations.randomOrNull() ?: return null
        playAnimation(anim)
        return anim
    }

    /**
     * 随机播放一个运动时动画
     */
    override fun playRandomMovingAnimation(): FairyAnimation? {
        val anim = AnimationConfig.movingAnimations.randomOrNull() ?: return null
        playAnimation(anim)
        return anim
    }

    override fun onActionStarted(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource
    ) {
        stopCurrentPlayback(clearLooping = false)
    }

    override fun onActionFinished(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource,
        status: InteractionActionStatus
    ) {
        Log.d(TAG, "Interaction action finished: action=$actionId, status=$status")
        resumeLoopingActionAnimationIfNeeded()
    }

    private fun FairyAnimation.defaultFacingPolicy(): AnimationFacingPolicy {
        return when (type) {
            AnimationType.BASE_MOVING -> AnimationFacingPolicy.KEEP_BEHAVIOR
            AnimationType.BASE_IDLE,
            AnimationType.EMOTION_REACTION,
            AnimationType.NON_TASK_ACTION -> AnimationFacingPolicy.FACE_PLAYER
        }
    }
}
