This document provides a detailed explanation of how to compose and control the playback of multiple animation resources, including repeating a single animation, playing multiple animations in parallel or in sequence, managing playback states such as pause, speed adjustment, and seeking through the `AnimationPlaybackController`, as well as achieving animation transitions and blending through `AnimationPlayConfig`.
## Example description
The model file used is as follows. This model file contains a total of five `AnimationResource` instances in order: idle, jump, look_around, walk_forward, and wave. You can preview them using Blender or other third-party DCC software.
This document demonstrates how to control animation playback using the wave animation and a segment of tween animation created in code.
<a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e8dc55fee9449b2adcd9fd4e933250a~tplv-goo7wpa0wc-image.image" filename="pico_robot_animated.glb" download>pico_robot_animated.glb</a>
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1e8583c468a845909a2967a7f2e8f7a9~tplv-goo7wpa0wc-image.image" width="3456px" /></div>

## Preparation
Obtain an `AnimationResource` instance.
An `AnimationResource` represents playable animation data and can be obtained in the following ways:

* **Skeletal animation**: After obtaining an array of `Entity` objects with skinned meshes, you can use the `getAnimationResources ()` function to get the `AnimationResource` bound to each `Entity` instance in the array. For details, refer to "[Skeletal animation](/skeletal-animation)".
* **Tween animation**: You can use the static function `AnimationResource.generateWithTweenAnimation()` to generate an `AnimationResource`. For details, refer to "[Tween animation](/tween-animation)".
* **Orbit animation**: You can convert a created orbit animation into an `AnimationResource`. For details, refer to "[Orbit animation](/orbit-animation)".

## Repeat playback of a single animation
To repeat an animation a specified number of times after the first playback, use `animationResource.repeat(count: Int)`. This function creates a new animation resource based on the provided `count` parameter and sets the number of times it will repeat.
For example: make the robot wave four times (repeat three times) and then stop playing the wave animation.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3b162536dad445a93296b05bd45fd28~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun AnimationCombinationExample() {
    SpatialView(
        initial = { content, _ ->
            // Asynchronously load a model with skeletal animation
            val robot = Entity.loadSuspend("asset://model/pico_robot_animated.glb")
            robot.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, -0.5f, 0f))
                }
                content.addEntity(this)
                playAnimationRepeat(this)
            }
        }
    )
}

fun playAnimationRepeat(entity: Entity) {
    // Find the skinned mesh under the entity
    val skinnedMeshEntityArray = entity.findSkinnedMeshEntity()
    for (skinnedMeshEntity in skinnedMeshEntityArray) {
        // Retrieve all skeletal animation resources
        val skeletalAnimationResources = skinnedMeshEntity.getAnimationResources()
        // The index of the wave animation is 4
        val waveAnimationResource = skeletalAnimationResources[4]
        // Create a new animation resource and set the repeat count to 3 (played 4 times in total)
        val repeat = waveAnimationResource.repeat(3)
        // Play the animation and use `use` to ensure the resource is properly closed/released after use
        repeat.use { skinnedMeshEntity.playAnimation(it) }
    }
}
```

## Compose and play multiple animation resources
After obtaining multiple `AnimationResource` instances, you can combine them into a new single `AnimationResource` instance to achieve parallel playback of multiple animations, as well as sequential playback of multiple animations.
### Parallel playback of multiple animations
To play multiple animations in parallel, use the `AnimationResource.group(with: List<AnimationResource>)` function. This function merges multiple animation resources passed in into a single animation resource. Playing this resource enables parallel playback.
For example: Move the robot from back to front, while waving its hand four times (repeating three times) during the movement, until it reaches the destination. The total duration is approximately 12 seconds.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6f548545f50a43fcac2d2790e7caee51~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun AnimationCombinationExample() {
    SpatialView(
        initial = { content, _ ->
            // Asynchronously load a model with skeletal animation
            val robot = Entity.loadSuspend("asset://model/pico_robot_animated.glb")
            robot.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, -0.5f, -0.9f))
                }
                content.addEntity(this)
                playAnimationGroup(this)
            }
        }
    )
}

fun playAnimationGroup(entity: Entity) {
    // Create a movement animation: move from the rear (-0.9) to the front (0.3) over 12 seconds
    val moveAnimation =
        AnimationResource.generateWithTweenAnimation(
            TweenAnimation.createTweenAnimation(
                bindTarget = AnimationBindTarget.bindPosition(),
                from = Vector3(0f, -0.5f, -0.9f),
                to = Vector3(0f, -0.5f, 0.3f),
                duration = 12f
            )
        )
    // Find the skinned mesh under the entity
    val skinnedMeshEntityArray = entity.findSkinnedMeshEntity()
    for (skinnedMeshEntity in skinnedMeshEntityArray) {
        // Retrieve all skeletal animation resources
        val skeletalAnimationResources = skinnedMeshEntity.getAnimationResources()
        // The index of the wave animation is 4
        val waveAnimationResource = skeletalAnimationResources[4]
        // Set the repeat count of the wave animation to 3 (played 4 times in total)
        val repeat = waveAnimationResource.repeat(3)
        // Combine the move animation and wave animation into a parallel animation group
        val group = AnimationResource.group(listOf(moveAnimation, repeat))
        // Play the animation group, and use `use` to ensure resources are properly closed/released after use
        group.use { skinnedMeshEntity.playAnimation(it) }
    }
}
```

### Sequentially play multiple animations
To play multiple animations in order, use the `AnimationResource.sequence(with: List<AnimationResource>)` function. This function combines multiple animation resources passed in into an ordered sequence of animations. The next animation automatically starts playing after the previous one ends.
For example: Move the robot from back to front first (about 3 seconds), and after reaching the destination, wave its hand four times (repeating three times).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8f2db1dd8060437aaf2f436ea73926b4~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun AnimationCombinationExample() {
    SpatialView(
        initial = { content, _ ->
            // Asynchronously load a model with skeletal animation
            val robot = Entity.loadSuspend("asset://model/pico_robot_animated.glb")
            robot.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, -0.5f, -0.9f))
                }
                content.addEntity(this)
                playAnimationSequence(this)
            }
        }
    )
}

fun playAnimationSequence(entity: Entity) {
    // Create the move animation: move from the rear (-0.9) to the front (0.3), duration is 3 seconds
    val moveAnimation =
        AnimationResource.generateWithTweenAnimation(
            TweenAnimation.createTweenAnimation(
                bindTarget = AnimationBindTarget.bindPosition(),
                from = Vector3(0f, -0.5f, -0.9f),
                to = Vector3(0f, -0.5f, 0.3f),
                duration = 3f
            )
        )
    // Find the skinned mesh under the entity
    val skinnedMeshEntityArray = entity.findSkinnedMeshEntity()
    for (skinnedMeshEntity in skinnedMeshEntityArray) {
        // Retrieve all skeletal animation resources
        val skeletalAnimationResources = skinnedMeshEntity.getAnimationResources()
        // The index of the wave animation is 4
        val waveAnimationResource = skeletalAnimationResources[4]
        // Set the repeat count of the wave animation to 3 (played a total of 4 times)
        val repeat = waveAnimationResource.repeat(3)
        // Combine the move animation and wave animation into a serial animation sequence (move first, then wave)
        val sequence = AnimationResource.sequence(listOf(moveAnimation, repeat))
        // Play the animation sequence, and use `use` to ensure resources are properly closed/released after use
        sequence.use { skinnedMeshEntity.playAnimation(it) }
    }
}
```

## Control animation playback
After an `Entity` instance calls `playAnimation()` to play an animation, it returns an `AnimationPlaybackController` instance. This instance can be used to control animation playback, such as pausing, resuming, stopping, adjusting speed, seeking to a specific point, and obtaining the current playback status of the animation.
Main features include:
| **Functionality** | **Related functions/properties** |
| --- | --- |
| Playback status and validity check | Related properties: <br>  <br> * Controller validity: `valid`(bool) <br> * Playback status query: `isPlaying()`, `isComplete()`, `isPaused()`, `isStopped()` |
| Playback control | * Pause playback: `pause()` <br> * Resume playback: `resume()` <br> * Stop playback: `stop()` |
| Playback speed and time settings | * Get and set playback speed: `getSpeed()`, `setSpeed(speed)` <br> * Get and set playback time: `getTime()`, `setTime(time)` |
| Resource release | After using an `AnimationPlaybackController` instance, you must manually call `close()` to release it, in order to avoid unnecessary memory usage. |
* Before performing animation playback control, it is recommended to use `controller.valid` to check whether the controller is valid.
* Use `setTime()` to jump to a specific point on the timeline; together with `pause()`, this allows for static preview.

### Notes

* **Skeletal animation playback mechanism**
   Skeletal animations obtained via `skinnedMeshEntity.getAnimationResources()` are played in an infinite loop by default. Therefore, if these animations are directly converted to a `List` and passed into the `sequence()` function, the playback sequence will be stuck on the first infinitely looping animation. To play the animation only once, explicitly call `repeat(0)` before passing it in.
* **AnimationTarget conflicts**
   For multiple `TweenAnimation` instances bound to the same `AnimationTarget`, when passed into the `sequence()` function, all animations can be played sequentially as expected.
   However, when passed into the `group()` function, only the last animation will be played. This is because these animations act on the same `AnimationTarget`, and each subsequent modification overrides the previous one.
   Similarly, when a list of skeletal animations is passed into the `group()` function, only the last animation will be played. The reason is that these skeletal animations act on the same animation target (`AnimationTarget`), and only the last applied animation takes effect.
* **Thread requirements**
   All animation-related functions on `Entity` (such as `entity.playAnimation()`) and functions of `AnimationPlaybackController` (such as `controller.pause()`) are annotated with `@MainThread`. You must call these functions on the main thread.
* **Resource management**
   When an `Entity` instance is destroyed, all associated animation playback controllers are automatically closed.
* **Animation resource input restrictions**
   The same `AnimationResource` instance cannot be passed repeatedly to the `sequence()` or `group()` functions; otherwise, the request will throw an exception.

## Implement animation transitions and blending
To achieve complex animation transitions and blending effects, you can pass an `AnimationPlayConfig` object when calling the `playAnimation()` function. By configuring the `AnimationPlayConfig` object, you can implement the following animation scenarios:

* **Smooth transition**: Create a smooth blending effect between two animations (such as from "walk" to "run").
* **Overlay**: Play a new animation (such as "wave") on top of the existing animation (such as the character's basic idle) without interrupting it.
* **Interrupt and blend**: Interrupt an animation at any moment and smoothly blend from the current pose to the next animation.
* **Layered control**: Achieve complex overlay logic through animation layers (`blendLayer`) and weights (`blendWeight`). For example, place body animation and facial animation on different layers and independently control their contribution levels.

The parameter description of the `AnimationPlayConfig` class is as follows:
| **Parameter name** | **Type** | **Description** |
| --- | --- | --- |
| `transitionDuration` | Float | The duration of the animation transition, in seconds, used to control the time required to smoothly blend from the current animation to the new animation. <br>  <br> * **0.0f**:  **(**default) switches immediately with no transition effect. <br> * **> 0.0f**: Linearly blend from the old animation to the new animation over the specified duration in seconds. <br> * **< 0.0f**: This value will be treated as `0.0f`, and a warning will be logged. |
| `transitionMode` | `AnimationTransitionMode` | The animation transition mode determines how the old animation playing on the entity is handled when a new animation starts playing. `AnimationTransitionMode` is an enumeration class that defines the following four different transition behaviors: <br>  <br> * **`DEFAULT`**: (default) In this mode, the SDK automatically selects the most conventional transition method based on the animation type to simplify your configuration. <br>    * For skeletal animation, its behavior is equivalent to `CROSSFADE`. <br>    * For other types of animation (such as tween animation), its behavior is equivalent to `COMPOSE`. <br> * **`CROSSFADE`**: Smoothly transition from the current animation to the new animation. Within the time specified by `transitionDuration`, the old animation gradually fades out (its weight decreases from the current value to 0), while the new animation gradually fades in (its weight increases from 0 to the target value of `blendWeight`). This is the most common transition method, suitable for action sequences that require seamless connections, such as transitioning from "standing" to "walking" or from "running" to "jumping". <br> * **`COMPOSE`**: Overlay the new animation on top of the existing animation; both will run simultaneously and independently (unless they drive the same property), and the old animation will not stop or fade out as a result. This mode is suitable for scenarios where multiple independent animation effects need to be layered. For example, overlaying a "waving" animation (located at `blendLayer` 1) on top of a looping "breathing" idle animation (located at `blendLayer` 0); both will take effect simultaneously. <br> * **`STOP_AND_CROSSFADE`**: Instantly freeze the pose of all current animations, and use this static pose as the starting point to smoothly transition to the new animation within the `transitionDuration` time. This mode is suitable for scenarios where an urgent switch to another action is needed from a dynamic process, using the current "snapshot" as the basis. For example, when a character is hit in the middle of an attack animation, it is necessary to immediately transition from the current pose to a "hit" animation. |
| `blendLayer` | Int | Defines the animation playback layer, used to organize animation layering and overriding. When animations at different layers affect the same property, the effect of the higher layer animation will override that of the lower layer animation. <br> Values are as follows: <br>  <br> * **-1**: (default) Playback on the default layer. <br> * **>= 0**: Playback on the specified layer. You can separate different types of animations (such as body layer and facial layer) by layer, allowing them to coexist and independently affect the final pose, thereby enabling more complex composite actions. |
| `blendWeight` | Float | The blending weight of the animation, used to control how much this animation contributes to the final blended result. <br> Values are as follows: <br>  <br> * **1.0f**: (default) This animation effect is fully applied. <br> * **0.0f**: This animation does not produce any effect. <br> * **(0.0f, 1.0f)**: Apply the animation effect proportionally and blend it with other active animations. <br> * **Out-of-range values**: Values less than 0.0f will be corrected to 0.0f, and values greater than 1.0f will be corrected to 1.0f. |
The following sample code demonstrates how to use the Crossfade mode to transition from the idle animation to the running animation.
```Kotlin
val idleAnimation: AnimationResource = ...
val runAnimation: AnimationResource = ...

// Play the idle animation first
entity.playAnimation(idleAnimation)

// ... Running is triggered at a certain moment ...

// Create a configuration specifying a transition to the running animation using the CROSSFADE mode within 0.5 seconds
val config = AnimationPlayConfig(
    transitionDuration = 0.5f,
    transitionMode = AnimationTransitionMode.CROSSFADE
)

// Play the running animation, which will smoothly transition from the idle animation
entity.playAnimation(runAnimation, config)
```

