package com.example.matefairy01.avatar

import com.example.matefairy01.animation.AnimationController
import com.example.matefairy01.animation.FairyAnimation
import com.pico.spatial.core.ecs.Entity

class DefaultAvatarController(
    private val animationController: AnimationController
) : AvatarController {
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

    override fun cleanup() {
        animationController.cleanup()
    }
}
