package com.example.matefairy01.behavior

import com.example.matefairy01.animation.AnimationController

/**
 * 行为系统的运行时依赖桥接层。
 *
 * 当前阶段用于把行为模块从 AppModule 中解耦出来，
 * 后续可以继续演进为更显式的依赖注入或场景级上下文对象。
 */
object BehaviorRuntimeDependencies {
    var animationController: AnimationController? = null
        private set

    fun bindAnimationController(controller: AnimationController) {
        animationController = controller
    }

    fun clear() {
        animationController = null
    }
}
