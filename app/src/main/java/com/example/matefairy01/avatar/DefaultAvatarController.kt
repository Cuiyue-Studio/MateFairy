package com.example.matefairy01.avatar

import com.example.matefairy01.animation.AnimationController
import com.example.matefairy01.animation.FairyAnimation
import com.pico.spatial.core.ecs.Entity

class DefaultAvatarController(
    private val animationController: AnimationController
) : AvatarController {
    override val currentFacingPolicy
        get() = animationController.currentFacingPolicy

    override fun initialize(robotEntity: Entity) {
        animationController.initialize(robotEntity)
    }

    override fun playSpawnAnimation() {
        animationController.playAnimation(FairyAnimation.TURBO_DASH)
    }

    override fun requestMovingAnimation() {
        animationController.playAnimation(FairyAnimation.TURBO_DASH)
    }

    override fun requestIdleAnimation(): FairyAnimation? {
        return animationController.playRandomIdleAnimation()
    }

    override fun requestStandbyAnimation(): FairyAnimation? {
        animationController.playAnimation(FairyAnimation.STANDBY_MODE)
        return FairyAnimation.STANDBY_MODE
    }

    override fun cleanup() {
        animationController.cleanup()
    }
}
