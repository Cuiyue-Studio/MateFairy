In certain scenarios, you may want to override the default global physics settings to implement unique or non-terrestrial physics effects. For example, you might want to simulate gravity in a direction different from the typical `Vector3(0f, -9.81f, 0f)` direction, as if you were in space. Alternatively, you may want to control the passage of time by customizing a simulation clock, which can adjust either the speed of time or the time step between physics updates. To achieve a balance between simulation accuracy and performance, you may also need to fine-tune the solver's behavior by adjusting the number of position and velocity iterations in constraint solving.
## About PhysicsWorldComponent
`PhysicsWorldComponent` component allows you to define a custom physics world with localized simulation parameters. By assigning entities to a specific `PhysicsWorldComponent`, you can control their physics behavior independently of the global physics environment. `PhysicsWorldComponent` supports the following properties:
| **Property** | **Description** |
| --- | --- |
| gravity | Defines the local gravity vector applied in this physics world. |
| kinematicCollisionReportMode | You can configure whether the current kinematic body triggers collision event messages when it collides with different types of bodies, such as kinematic or static bodies. |
| solverIterations | Set the number of iterations the physics engine uses when solving position constraints or velocity constraints. This setting directly affects the accuracy and performance of constraint solving: <br>  <br> * **More iterations**: The results are more accurate, but the computational cost is higher, affecting operational efficiency. <br> * **Fewer iterations**: Higher performance, but may result in unstable or inaccurate physics behavior. |
| simulationClock | Specify a local clock for independently driving physics simulation. Includes: <br>  <br> * `fixedTimestep`: Used to control the fixed time interval for calculation updates in a physics system. The smaller the value, the more accurate the calculation, but the greater the impact on performance. The larger the value, the less accurate the calculation, which may increase the risk of an "instantaneous update to an incorrect position". <br> * `maxTimeStep`: Maximum allowed time step. Limits the maximum time interval between physics updates from frame to frame, preventing performance issues caused by excessive `FixedUpdate` calls when the frame rate is low. <br> * `timeSpeed`: Controls the speed at which time passes. The larger the value, the faster the simulation. |
## Create a physics world
To create a custom physics world for a group of entities, use the following steps:

1. Add a `PhysicsWorldComponent` to a `rootEntity`.
2. Add `entity1` and `entity2` as child nodes of `rootEntity`.

Under this hierarchical structure (that is, `entity1` and `entity2`), these entities will share the same localized physics environment. For this physics world, you can configure a custom gravity vector, the number of iterations for constraint solving, a dedicated simulation clock, and the collision reporting behavior for kinematic rigid bodies. All of these can be configured independently of global physics settings. This setting is very useful for simulating isolated areas with different physics properties, for example, space stations, underwater environments, or slow-motion regions within a scene.
To ensure that a collision occurs, all objects involved must belong to the same physics world. In other words, either none of them have the `PhysicsWorldComponent` added, or they must share a common ancestor entity that has the `PhysicsWorldComponent`.

## Code sample
The following code creates a new torus entity and assigns a `PhysicsWorldComponent` to it. Gravity is set to `Vector3(0f, -1f, 0f)` to simulate a low-gravity planetary environment. The time scale for physics simulation is set to `0.5`, causing the physics system to run at half the speed of real time.

                           ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/22723c53204a4514b8976a7aeffd950a~tplv-goo7wpa0wc-image.image)

Note that even if the torus entity is correctly configured with the `CollisionComponent` and `RigidBodyMode.DYNAMIC`, it will still pass through the static plane and will not produce the expected collision response. The reason is that after adding `PhysicsWorldComponent` to the torus entity, it is placed in a separate physics world, which is different from the physics world where the static plane (and also the sphere) are located. Therefore, they are in different simulation environments and cannot physically interact.
```Kotlin
@Composable
fun PhysicsWorldExample() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ ->
            content.addEntity(setUpStaticPlane())
            content.addEntity(setUpDynamicSphere())
            content.addEntity(setUpDynamicTorus())
        }
    )
}

fun setUpStaticPlane(): Entity {
    val planeSize = Vector3(1.2f, 0.07f, 0.7f)
    val planePos = Vector3(0f, -0.3f, 0.35f)
    // Create a plane instance
    val staticPlane = createPlaneEntity(planeSize, planePos)
    // Set up the collision component
    addCollisionComponent(staticPlane, ShapeResource.createBox(size = planeSize))
    return staticPlane
}

fun createPlaneEntity(planeSize: Vector3, planePos: Vector3): Entity {
    // Create a mesh
    val mesh = MeshResource.createBox(size = planeSize, cornerRadius = 0.02f)
    // Create a material
    val material =
        BasicMaterial.create(BlendingMode.OPAQUE).apply {
            setBaseColor(Color4.fromLinearHex("0x65697cff"))
        }
    // Create a plane entity
    val plane = ModelEntity(mesh, material).apply { setName("plane") }
    // Adjust the position of the plane
    plane.components[TransformComponent::class.java]!!.position = planePos
    return plane
}

fun setUpDynamicSphere(): Entity {
    val sphereRadius = 0.06f
    val spherePos = Vector3(0f, 1.2f, 0.3f)
    // Create a sphere entity
    val dynamicSphere = createSphereEntity(sphereRadius, spherePos)
    // Set up the collision component
    addCollisionComponent(dynamicSphere, ShapeResource.createSphere(radius = sphereRadius))
    // Set up the rigid body component
    addRigidBodyComponent(dynamicSphere)
    return dynamicSphere
}

fun createSphereEntity(sphereRadius: Float, spherePos: Vector3): Entity {
    // Create a mesh
    val mesh = MeshResource.createSphere(radius = sphereRadius)
    // Create a material
    val material =
        BasicMaterial.create(BlendingMode.OPAQUE).apply {
            setBaseColor(Color4.fromLinearHex("0xa9bbd3ff"))
        }
    // Create a sphere entity
    val sphere = ModelEntity(mesh, material).apply { setName("sphere") }
    // Adjust the position of the sphere
    sphere.components[TransformComponent::class.java]!!.position = spherePos
    return sphere
}

fun setUpDynamicTorus(): Entity {
    val torusOuterRingRadius = 0.15f
    val torusInnerRingRadius = 0.06f
    val torusPos = Vector3(-0.3f, 1.2f, 0.3f)
    // Create a torus mesh
    val mesh =
        MeshResource.createTorus(
            outerRingRadius = torusOuterRingRadius,
            innerRingRadius = torusInnerRingRadius
        )
    // Create a torus entity
    val torus = createTorusEntity(mesh, torusPos)
    Set the collision component
    addCollisionComponent(torus, ShapeResource.createConvexMesh(mesh))
    Configure the rigid body component
    addRigidBodyComponent(torus)
    // Set up the physics world component
    addPhysicsWorldComponent(torus)
    return torus
}

fun createTorusEntity(mesh: MeshResource, torusPos: Vector3): Entity {
    // Create a material
    val material = 
        BasicMaterial.create(BlendingMode.OPAQUE).apply {
            setBaseColor(Color4.fromLinearHex("0x9ad3c5ff"))
        }
    // Create a torus entity
    val torus = ModelEntity(mesh, material).apply { setName("torus") }
    // Adjust the position of the torus
    torus.components[TransformComponent::class.java]!!.position = torusPos
    return torus
}

fun addCollisionComponent(entity: Entity, shapeResource: ShapeResource) {
    entity.components.set(
        CollisionComponent(
            collisionShape = listOf(shapeResource),
            physicsMaterial =
                PhysicsMaterialResource(
                    staticFriction = 0.6f,
                    dynamicFriction = 0.6f,
                    restitution = 0.8f,
                ),
            collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
            collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
            collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
        )
    )
}

fun addRigidBodyComponent(entity: Entity) {
    entity.components.set(
        RigidBodyComponent().apply {
            massProperties =
                MassProperties(
                    mass = 1f,
                    centerOfMass = Vector3.ZERO,
                    inertia = Vector3(0.1f),
                    orientationOfInertia = Quat.identity()
                )
            rigidBodyMode = RigidBodyMode.DYNAMIC
            isAffectedByGravity = true
            collisionDetectionMode = CollisionDetectionMode.CONTINUOUS
        }
    )
}

fun addPhysicsWorldComponent(entity: Entity) {
    entity.components.set(
        PhysicsWorldComponent(
            gravity = Vector3(0f, -1f, 0f),
            simulationClock = SimulationClock(timeSpeed = 0.5f)
        )
    )
}
```

## API reference
The `PhysicsWorldComponent` class provides properties and functions related to the physics world. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
