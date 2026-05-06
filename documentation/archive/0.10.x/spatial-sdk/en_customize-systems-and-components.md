In PICO Spatial SDK, you can use custom components to store and manage specific states or parameters for entities, and then use custom systems to update entities with specific components on a per-frame basis, enabling complex interactions or animation effects.
This article uses the example of a bird automatically flying around the user to explain how to implement custom systems and components based on the ECS architecture of the PICO Spatial SDK.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.4706572769953052);">

Top view:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/87986628e50241bc8c20d6c86b08f976~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5293427230046949);margin-left: 16px;">

Front view:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ebb2fcd76c7641b3bf82e615cf60ed96~tplv-goo7wpa0wc-image.image)


</div>
</div>

## Customize components
When creating custom components in the Spatial Editor, only String, Int, Bool, and Float data types are supported; however, when creating components programmatically via the PICO Spatial SDK, there are no such data type restrictions.

To implement a custom flight trajectory line, you first need to define a `FlyTrajectoryComponent`. The code sample is as follows:
```Kotlin
class FlyTrajectoryComponent(var isEnabled: Boolean = false) : Component() {
    var elapsedTime = 0.0f

    // starting position and initial phase
    private val centerX = 0f
    private val centerZ = 0f
    private val baseAltitude = 2.5f
    private var phaseShift = -(PI_FLOAT / 2) // start at +Z axis

    // Wing vibration state
    private val altAmp = 1f // Vertical amplitude
    private val altFreq = 0.2f // Vertical vibration frequency (Hz)
    private var wingPhase = 0.0f
    private val wingbeatFreq = 5.0f // Wingbeat frequency (Hz)
    private val bobAmp = 0.02f // 2cm vertical jitter
    private val gravity = 9.81f

    fun updateTransform(
        dt: Float,
        speed: Float,
        radius: Float,
        yawRate: Float,
        climbRate: Float
    ): Transform {
        // Parametric angle along the circumference (φ)
        val phi = (yawRate * elapsedTime + phaseShift)

        // Position on the circumference, with the origin as the center
        val posX = centerX + radius * cos(phi)
        val posZ = centerZ + radius * sin(phi)
        wingPhase = (wingPhase + wingbeatFreq * 2f * PI_FLOAT * dt) % (2f * PI_FLOAT)
        val posY =
            baseAltitude +
                altAmp * sin(2f * PI_FLOAT * altFreq * elapsedTime) +
                bobAmp * sin(wingPhase)
        val position = Vector3(posX, posY, posZ)

        // Rotation calculations
        val pitch = atan2(climbRate, speed).toDegrees()
        val roll = atan2(speed * yawRate, gravity).toDegrees() // For circular motion, r·ω = v² / r
        val forward =
            Vector3(-radius * yawRate * sin(phi), climbRate, radius * yawRate * cos(phi))
                .normalize()
        val yaw = atan2(forward.x, forward.z).toDegrees()
        val rotation = EulerAngles(pitch, yaw, roll)

        return Transform(position, rotation, Vector3(1f))
    }
}

private const val PI_FLOAT = PI.toFloat()

private fun Float.toDegrees() = this * 180f / PI_FLOAT
```

The code above implements the following features:

* **Maintain flight animation parameters**: Maintenance includes parameters such as baseline altitude, wingbeat frequency, minor jitter, and other parameters.
* **Update the transform**: The `updateTransform()` method combines system input parameters (such as speed, radius, yaw rate, and climb rate) with internal parameters (such as altAmp, wingbeatFreq, and more) to calculate slow vertical oscillations of the individual, simulating the body movement of an insect during wingbeats. Finally, a transform is generated for each frame to drive entity movement.
* **Simulate flight motion:**
   * Horizontal motion: uniform circular motion;
   * Vertical motion: a slow sinusoidal undulation combined with rapid wing flutter.;
   * Pose: calculate pitch, yaw, and roll based on speed and turning radius.

If you want a custom component to be cloneable, you need to override the `clone()` method to ensure that all its properties and states are copied correctly.
## Customize systems
The purpose of the custom system is to control and update the flight behavior of the entity with the `FlyTrajectoryComponent`.
### Step 1: Define a system
Customize a system to search for the entity associated with `FlyTrajectoryComponent` and perform the `update` operation:
```Kotlin
class FlyTrajectorySystem : System() {
    private var elapsedTime = 0f

    override fun update(context: SceneUpdateContext) {
        val dt = context.deltaTime
        elapsedTime += dt

        // Control commands
        val speed = 1f // Speed (meters per second)
        val radius = 3.5f // Expected circular radius (meters)
        val yawRate = speed / radius // Yaw rate (radians per second) = v / r

        val altAmp = 0.3f // Vertical amplitude (m)
        val altFreq = 0.1f // High vibration frequency (Hz)
        val climbRate =
            altAmp * (2f * PI_FLOAT * altFreq) * cos(2f * PI_FLOAT * altFreq * elapsedTime)

        val condition =
            EntityQueryCondition.hasComponent(FlyTrajectoryComponent::class.java)
                .and(EntityQueryCondition.hasComponent(ObjectAudioComponent::class.java))
        val filteredEntities = context.scene.queryEntity(condition)
        filteredEntities.forEach { entity ->
            val comp = entity.components[FlyTrajectoryComponent::class.java]!!
            if (comp.isEnabled) {
                comp.elapsedTime = elapsedTime
                val newTransform = comp.updateTransform(dt, speed, radius, yawRate, climbRate)
                entity.components[TransformComponent::class.java]!!.apply {
                    position = newTransform.position
                    eulerAngles = newTransform.rotation
                }
            }
        }
    }
}
```

In the code above, the system is responsible for the update of each frame. In each frame's `update` callback, it performs the following operations:

* **Maintain global time**: Records the accumulated time using its own `elapsedTime`, which is used to calculate the progress of the flight trajectory.
* **Calculate altitude changes**: Use system-level command parameters `altAmp` and `altFreq` to generate a slowly varying altitude trend (`climbRate`) over time, simulating the bird's large-scale climbing and descending behavior.
* **Filter the target entity**: Define query criteria to restrict the target to entities that have the `FlyTrajectoryComponent`.
* **Update the entity's transform**: For the target entity, calculate and update its `Transform` only when its `FlyTrajectoryComponent` is active, to achieve a dynamic flying effect.

In this way, the system and components work together to enable the entity to move continuously along a predefined path within the scene.
### Step 2: Register the system
After defining a system, you must register it first; only then can it receive the underlying `update` callback and execute update logic each frame. To avoid system idling and wasting computing resources when not needed, it is recommended to register systems when necessary only.
```Kotlin
@Composable
fun ImmersiveScene() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val spatialNavigator = LocalSpatialNavigator.current

    DisposableEffect(lifecycleOwner) {
        registerSystem<FlyTrajectorySystem>()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                spatialNavigator.closeWindowContainer(id = WINDOW_ID, tag = WINDOW_TAG)
            }
            if (event == Lifecycle.Event.ON_RESUME) {
                spatialNavigator.openWindowContainer(id = WINDOW_ID, tag = WINDOW_TAG)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            unregisterSystem<FlyTrajectorySystem>()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    SpatialView(initial = { content, _ -> content.addEntity(Environment.await()) })
}
```

System registration is a static operation; essentially, it binds a system instance in the memory of each scene. When the system performs a query, it locates incrementally along the **Scene → WindowContainer/Stage → SpatialView → Entity** hierarchical path. Therefore, the search scope is usually limited to the current spatial container.
Note that if multiple systems are registered at the same time, the program will execute their logic sequentially in the order in which they were registered. For example, the following code registers and executes `FirstSystem`, `SecondSystem`, and `ThirdSystem` in sequence. If the logic of your app depends on the execution order of the system, you must explicitly manage the order of systems during registration to ensure correct logic execution.
```Kotlin
DisposableEffect(key1 = Unit) {
    registerSystem<FirstSystem>()
    registerSystem<SecondSystem>()
    registerSystem<ThirdSystem>()
    onDispose {
        unregisterSystem<FirstSystem>()
        unregisterSystem<SecondSystem>()
        unregisterSystem<ThirdSystem>()
    }
}
```

### Step 3: Unregister the system
After the system is no longer needed, it is recommended to promptly unregister it to conserve resources. For example, you can use `DisposableEffect` to unregister a custom system at the appropriate phase of its lifecycle (in this example, the custom system is unregistered in `onDispose`).
```Kotlin
@Composable
fun ImmersiveScene() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val spatialNavigator = LocalSpatialNavigator.current

    DisposableEffect(lifecycleOwner) {
        registerSystem<FlyTrajectorySystem>()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                spatialNavigator.closeWindowContainer(id = WINDOW_ID, tag = WINDOW_TAG)
            }
            if (event == Lifecycle.Event.ON_RESUME) {
                spatialNavigator.openWindowContainer(id = WINDOW_ID, tag = WINDOW_TAG)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log out of the system
            unregisterSystem<FlyTrajectorySystem>()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    SpatialView(initial = { content, _ -> content.addEntity(Environment.await()) })
}
```

## API reference
For more information on the `Component` class, the `System` class, the `registerSystem` interface, and the `unregisterSystem` interface, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

