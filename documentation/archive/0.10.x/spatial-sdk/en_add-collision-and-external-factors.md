In spatial apps, forces and collisions are primarily used to simulate the interactions and laws of motion of the physical world, making the virtual environment more realistic and the interactions more natural.
## About collisions and rigid bodies
In many game engines and physics systems (including PICO Spatial SDK), an object can only receive forces or participate in physical interactions after it has a collision component. A collision component (commonly referred to as a collider) defines the volume or surface of an object that is recognized by the physics engine, that is, the collision shape. The collision shape determines the physical boundaries of an object in space, and forces such as impulse, gravity, and collision force act on these physical boundaries.
If an object does not have a collision shape, the physics engine cannot accurately identify its position and volume in space, and therefore cannot apply forces or perform collision detection.Note that the visual model of an object used for rendering is usually different from the collision shape used for physics simulation, and there may be significant differences between the two.
The generation and transmission of forces (such as pushing or bouncing) depend on the detection of intersection or contact points between colliders. These contact points are calculated based on the colliders rather than the object’s visual appearance or transform data. Even forces applied uniformly, such as gravity, rely on the collision shape to determine the exact points to apply forces and the distribution of forces.
In practical applications, rigid body components are typically used together with colliders. A rigid body defines the physical properties and dynamic behaviors of an object, such as mass, velocity, and response to forces, while a collider provides the physical boundaries used by the engine for collision detection and physical response. If a collider is missing, the rigid body does not have a clear physical boundary, and the physics engine cannot determine the position and manner in which interactions occur. As a result, collision detection and force calculations for the object may be ignored.
This separated design of rigid bodies and colliders also helps optimize performance. The physics engine skips objects without colliders during broad-phase collision detection and constraint solving to avoid unnecessary computations, thereby improving overall efficiency. This is especially important in large or complex scenes that contain tens of thousands of objects.
## Set up the CollisionComponent
The `CollisionComponent` enables physical interaction for objects by specifying their shape, material, response behavior, filtering rules, and the level of detail for collision reports.
### Collision detection accuracy
The collision detection accuracy of `CollisionComponent` is 0.001 meters, that is, 1 millimeter.
### Important notes
After adding `CollisionComponent` to an entity, the entity will participate in collision detection, but will only exist as a static physical object. This means it can block other entities, but it does not move when force or velocity is applied to it. Unless its transform is manually updated, it will always remain fixed in place.
This behavior is expected. Static colliders are not affected by forces in physics simulation. They are suitable for floors, walls, or any object that should remain stationary but still needs to interact with dynamic objects.
### Property description
`CollisionComponent` contains the following properties:
| **Property** | **Description** |
| --- | --- |
| `collisionShape` | The geometric shape used for collision detection. It accepts a set of `ShapeResource` instances, which can be simple primitives such as boxes, spheres, and capsules, or more complex shapes such as convex polygon meshes and static meshes. Depending on the required level of matching between collision shapes and visual models, developers can use appropriate instances to balance performance and accuracy. |
| `collisionResponseMode` | Controls how the system handles detected collisions. Different modes correspond to different levels of collision data and effects, including: <br>  <br> * `TRIGGER_LITE`: Provides a contact point without additional detailed collision data and without generating collision effects, suitable for event-based interactions. <br> * `TRIGGER_FULL`: Provides detailed data, including contact points, normal vectors, and penetration depth, but still does not apply force or impulse. Suitable for diagnostics or non-physical game logic. <br> * `COLLIDER_FULL`: Provided complete contact data and allows the physics engine to apply responses, such as impulses, resulting in collision effects. |
| `collisionFilter` | Allows you to precisely control which objects can collide with each other. Each object can be assigned to one or more collision groups, and its collision mask determines which groups of objects it can interact with. This mechanism improves performance by restricting interactions to only relevant pairs of objects, thereby avoiding unnecessary computation. Includes two types of filters: <br>  <br> * `COLLISION_FILTER_DEFAULT`: Collides with objects in the default group. <br> * `COLLISION_FILTER_ALL`: Collides with all objects. |
| `physicsMaterial` | The behavior of an object when it comes into contact with other objects. It includes properties such as static friction (for controlling the resistance when an object starts to move), kinetic friction (for resisting sliding during motion), and coefficient of restitution (for defining the level of elasticity after an object collides), and more. These parameters work together to simulate realistic behavior after contact. |
| `collisionInfoDetailLevel` | The level of detail of collision event messages (that is, the amount of data included in the event). <br>  <br> * `BRIEF`: The system only reports high-level summary information, such as average contact position, total accumulated impulse, and maximum penetration depth, making it suitable for scenarios with high performance requirements. <br> * `DETAILED`: In addition to high-level summary information, detailed data for each contact point is provided, including position, normal, impulse, and penetration depth. This option is suitable for scenarios that require detailed collision data to achieve accurate response or for debugging. |
### Code sample
The following code sample demonstrates how to create a plane and add a `CollisionComponent` to it:
```Kotlin
@Composable
fun StaticPlaneExample() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ -> content.addEntity(setUpStaticPlane()) }
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
```

We have now set the plane as a "static platform". Next, let us refer to the "Set up the RigidBodyComponent" section and add a dynamic sphere to the scene so that it can collide with the plane.
## Set up the RigidBodyComponent
The `RigidBodyComponent` grants entities physical behavior based on its configuration.
### Property description
The `RigidBodyComponent` component contains the following properties:
| **Property** | **Description** |
| --- | --- |
| `isAffectedByGravity` | Determines whether the object is affected by gravity. |
| `isTranslationLocked` | Determines whether to lock the rigid body's displacement along all three axes to restrict unnecessary motion. |
| `isRotationLocked` | Determines whether to lock the rigid body's rotation along all three axes to restrict unnecessary motion. |
| `rigidBodyMode` | The object's motion mode. <br>  <br> * When set to `Dynamic`, the entity is driven by the physics engine and responds to forces and collisions. <br> * When set to `Kinematic`, the entity's movement is directly controlled by the user and is not affected by forces. <br>  <br> ***Note***: If `RigidBodyComponent` is not set, the object's motion mode defaults to `STATIC`. |
| `massProperties` | Defines mass-related properties, including the object's mass, center of mass, inertia, and inertia direction. These properties determine how an object responds to external forces and torques. |
| `linearDamping` | Linear damping of a rigid body, which represents the resistance encountered during simulated linear motion. Linear damping slows linear motion over time. |
| `angularDamping` | Angular damping of a rigid body, which represents the resistance encountered during simulated rotational motion. Angular damping is used to smooth angular velocity and dampen rotational motion. |
| `collisionDetectionMode` | Continuous collision detection mode for a rigid body, which is used to control the collision detection method for fast-moving objects in order to balance accuracy and performance. Includes: <br>  <br> * `DISCRETE` (default): No continuous collision detection. <br> * `CONTINUOUS`: Only detect continuous collisions with static objects. <br> * `CONTINUOUS_DYNAMIC`: Detect continuous collisions with static and dynamic objects. <br> * `CONTINUOUS_SPECULATIVE`: Predictive collision detection that detects collisions with static and dynamic objects. |
### Code sample
Based on the previously created static plane, the following code creates a dynamic sphere that is affected by gravity and can fall freely, thereby simulating the collision between a falling ball and the plane.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/525feed8912448338ddef75cb9aaa9be~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun SpherePlaneCollisionExample() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ ->
            content.addEntity(setUpStaticPlane())
            content.addEntity(setUpDynamicSphere())
        }
    )
}

fun setUpStaticPlane(): Entity {
    // Same as above.
    // ...
}

fun createPlaneEntity(planeSize: Vector3, planePos: Vector3): Entity {
    // Same as above
    // ...
}

fun addCollisionComponent(entity: Entity, shapeResource: ShapeResource) {
    Same as above.
    // ...
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

// Add the rigid body component
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
```

Note that when configuring the `CollisionComponent` for a static plane and a dynamic sphere, make sure to set the `collisionResponseMode`to `CollisionResponseMode.COLLIDER_FULL` for both of them. This mode is required for all objects that need to undergo physical collisions and produce collision effects, such as bouncing or movement prevention. In addition, to make the sphere's movement be affected by forces (gravity and the obstruction of a static plane), set its `rigidBodyMode` to `RigidBodyMode.DYNAMIC`.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

When both the plane and the sphere use `COLLIDER_FULL` mode, correct physical interaction is implemented.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/525feed8912448338ddef75cb9aaa9be~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

If either the plane or the sphere does not use `COLLIDER_FULL` mode, the collision effect will be ignored.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6a30fb7e3f97497cb20a902e8ddd2574~tplv-goo7wpa0wc-image.image)


</div>
</div>

In addition, the four collision detection modes for rigid bodies differ in accuracy and performance, and each is suited to specific use cases.
| **Collision detection mode** | **Accuracy** | **Performance** | **Use cases** |
| --- | --- | --- | --- |
| DISCRETE | Low (frame-based detection) | Fastest | Static objects and simple collisions. |
| CONTINUOUS | Intermediate (inter-frame ray detection) | Moderate | Important dynamic objects. |
| CONTINUOUS_DYNAMIC | High (full-range scan detection) | High cost | Important objects that move fast. |
| CONTINUOUS_SPECULATIVE | Predictive | Moderate | Physical consistency requirements (for example, network synchronization). |
When selecting a collision detection mode, you may consider trade-offs:

* Higher-accuracy modes (such as `CONTINUOUS`) can prevent penetrations, but will consume more CPU resources.
* The `DISCRETE` mode is already good enough for most static or non-critical objects.
* The `CONTINUOUS_SPECULATIVE` mode can reduce the impact of latency in multiplayer games through a prediction mechanism.

| **Mode** | **Diagram** |
| --- | --- |
| `DISCRETE` | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/96d1ea47f2e7454795730ce62e30ecaa~tplv-goo7wpa0wc-image.image) |
| `CONTINUOUS` | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/525feed8912448338ddef75cb9aaa9be~tplv-goo7wpa0wc-image.image) |
| `CONTINUOUS_DYNAMIC` | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/73ca0012155642bc95e8786c747fb2ce~tplv-goo7wpa0wc-image.image) |
| `CONTINUOUS_SPECULATIVE` | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5e1da4ea23e04f29bf7094389045418d~tplv-goo7wpa0wc-image.image) |
## Add force or torque via PhysicsForceComponent
`PhysicsForceComponent` applies a continuous and constant force or torque to an entity to generate acceleration, causing its velocity to gradually accumulate over time. This is fundamentally different from instantaneous impulse, because impulse causes an instantaneous change in velocity. To stop the constant effect generated by `PhysicsForceComponent`, you must manually remove this component.
### Prerequisites
Force-based movement is only applicable to dynamic rigid bodies. Therefore, to apply force or torque to an entity, the entity must have both the `CollisionComponent` (used to define the collision shape) and the `RigidBodyComponent` (used to define physical properties such as mass and inertia), and the `rigidBobyMode` property must be set to `DYNAMIC`. The movement of static `STATIC` and kinematic `KINEMATIC` rigid bodies is not governed by the dynamic equations of the physics engine; therefore, applying force to them has no effect.
### Important notes
the forces or torques set in `PhysicsForceComponent` are typically based on the object's local coordinate system rather than the world coordinate system. This means that the direction of the force will change as the object rotates. For example, when a constant forward force is applied to a sphere, its local coordinate system rotates as the sphere rolls, causing the direction of the force in world space to change continuously. As a result, the sphere may roll back and forth. To continuously accelerate an object's rotation around an axis in a given direction, a constant torque should be applied rather than a force. 
When applying force or torque, it is necessary to consider the mass and moment of inertia of the object. When the same force is applied to objects with different masses or shapes, the resulting acceleration is different. In addition, setting excessively large force or torque should be avoided, as this may cause instability in numerical calculations and result in violent, non-physical motion or penetration.
### Property description
`PhysicsForceComponent` includes the following properties:
| **Property** | **Description** |
| --- | --- |
| force | The force applied to a rigid body in the local coordinate system. |
| torque | The torque applied to a rigid body in the local coordinate system. |
### Code sample
The following code demonstrates how to apply a continuous torque to the previously created sphere, causing it to accelerate its rotation.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5c1fe754dd694ba088e89f35dbd8ab7c~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun ApplyConstantTorqueExample() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ ->
            content.addEntity(setUpStaticPlane())
            setUpDynamicSphere().apply {
                content.addEntity(this)
                addConstantForce(this)
            }
        }
    )
}
// Code for creating an entity and setting up collision and rigid body, same as above
fun setUpStaticPlane(): Entity {...}
fun createPlaneEntity(planeSize: Vector3, planePos: Vector3): Entity {...}
fun setUpDynamicSphere(): Entity {...}
fun createSphereEntity(sphereRadius: Float, spherePos: Vector3): Entity {...}
fun addCollisionComponent(entity: Entity, shapeResource: ShapeResource) {...}
fun addRigidBodyComponent(entity: Entity) {...}

// Apply a constant torque with its direction along the negative z-axis, causing the object to accelerate its rolling motion along the positive x-axis.
fun addConstantTorque(entity: Entity) {
    entity.components.set(
        PhysicsForceComponent(force = Vector3(0f, 0f, 0f), torque = Vector3(0f, 0f, -0.5f))
    )
}
```

## Add velocity via PhysicsVelocityComponent
`PhysicsVelocityComponent` is used to handle instantaneous changes in motion, for example when kicked, impacted by an explosion, or experiencing weapon recoil. This component simulates the effect of an instantaneous impulse by directly setting linear and angular velocity. Unlike components that apply force continuously, this component acts only once and instantaneously, and it does not continuously alter the object's motion in subsequent frames. This means it is ideally suited for implementing effects that take effect instantly and do not require continuous action, or for providing an object with an initial burst of motion.
### Important notes
Directly modifying the velocity bypasses the force integration process. Therefore, regardless of whether the rigid body's mode is `DYNAMIC` or `KINEMATIC`, as long as the entity has a `RigidBodyComponent`, `PhysicsVelocityComponent` usually takes effect.
### Property description
`PhysicsVelocityComponent` component includes the following properties:
| **Property** | **Description** |
| --- | --- |
| linearVelocity | The linear velocity of a rigid body. |
| angularVelocity | The angular velocity of a rigid body. |
### Code sample
The following code demonstrates how to apply an initial velocity to the right to the previously created sphere. This simulates the impulse effect at the start of the sphere's fall, so that it moves to the right horizontally.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/23544d6f38ca4f769f9e77989d20399b~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun ApplyInstantVelocityExample() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ ->
            content.addEntity(setUpStaticPlane())
            setUpDynamicSphere().apply {
                content.addEntity(this)
                addInstantVelocity(this)
            }
        }
    )
}

// Code for creating an entity and setting up collision and rigid body, same as above
fun setUpStaticPlane(): Entity {...}
fun createPlaneEntity(planeSize: Vector3, planePos: Vector3): Entity {...}
fun setUpDynamicSphere(): Entity {...}
fun createSphereEntity(sphereRadius: Float, spherePos: Vector3): Entity {...}
fun addCollisionComponent(entity: Entity, shapeResource: ShapeResource) {...}
fun addRigidBodyComponent(entity: Entity) {...}

// Apply an instantaneous initial velocity (initial impulse) in the positive x-axis direction
fun addInstantVelocity(entity: Entity) {
    entity.components.set(
        PhysicsVelocityComponent(
            linearVelocity = Vector3(0.9f, 0f, 0f),
            angularVelocity = Vector3(0f, 0f, 0f)
        )
    )
}
```

## Use CollisionEvents
You can subscribe to collision events through a valid scene (for example, `entity.scene`) or `SpatialViewContent` (the `content` of SpatialView). In both cases, you need to define a callback function and execute it when the event is triggered. When you no longer need to receive these events, simply call the `cancel()` method on the subscription object to stop listening and release resources. 
### Related classes
The classes related to `CollisionEvent` are as follows:
| **Class** | **Description** | **Member** | **Description** |
| --- | --- | --- | --- |
| CollisionEvents.Enter | An event triggered when two objects collide. | entityA | The first entity involved in the collision. |
|  |  | entityB | The second entity involved in the collision. |
|  |  | position | A position, which is used to represent the estimated contact point. The default value is `Vector3(0.0f, 0.0f, 0.0f)`. |
|  |  | impulse | The total impulse for this collision pair, which is obtained by summing all individual impulses applied at each contact point. The default value is `Vector3(0.0f, 0.0f, 0.0f)`. |
|  |  | penetrationDistance | Estimated overlap distance between two colliding entities in the scene's space coordinates. The default value is `0.0F`. |
|  |  | contacts | A list of contact points for collision, which is only present when `collisionInfoDetailLevel` is set to `DETAILED`. |
| CollisionEvents.Update | An event that is triggered every frame while two objects remain in continuous contact. | Same as CollisionEvents.Enter | / |
| CollisionEvents.Exit | An event that is triggered when two objects that were previously in contact separate. | Only entityA and entityB | / |
| / |  | collisionEventInfo | Used to store collision event messages. |
### Code sample
The following code demonstrates how to use `CollisionEvents.Enter` to change the sphere's color to a random value at the start of each collision. It also shows how to subscribe to this event at runtime and unsubscribe when it is no longer needed.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a399cc10de124a8cafc4c5cbad76c234~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun CollisionEventsEnterExample() {
    var subscription: Cancellable? = null
    DisposableEffect(Unit) { onDispose { subscription?.cancel() } }
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, _ ->
            content.addEntity(setUpStaticPlane())
            content.addEntity(setUpDynamicSphere())
            subscription = content.subscribeCollisionEvents(CollisionEvents.Enter::class.java)
        }
    )
}
// Code for creating an entity and setting up collision and rigid body, same as above
fun setUpStaticPlane(): Entity {...}
fun createPlaneEntity(planeSize: Vector3, planePos: Vector3): Entity {...}
fun setUpDynamicSphere(): Entity {...}
fun createSphereEntity(sphereRadius: Float, spherePos: Vector3): Entity {...}
fun addCollisionComponent(entity: Entity, shapeResource: ShapeResource) {...}
fun addRigidBodyComponent(entity: Entity) {...}

// Subscribe to collision events
fun <T : Event> SpatialViewContent.subscribeCollisionEvents(event: Class<T>): Cancellable? {
    return when (event) {
        CollisionEvents.Enter::class.java -> {
            this.subscribe(event) { collision ->
                val entityA =
                    collision.entityA
                        ?: throw IllegalStateException("One entity in collision is null")
                val entityB =
                    collision.entityB
                        ?: throw IllegalStateException("The other entity in collision is null")
                // Change the color of the sphere when a collision occurs
                if (entityA.getName().contains("sphere")) {
                    val material =
                        entityA.components[ModelComponent::class.java]!!.materials[0]
                            as BasicMaterial
                    material.setBaseColor(
                        Color4(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
                    )
                }
            }
        }
        // Add more event types as needed
        // ...
        else -> {
            null
        }
    }
}
```

### Learn more
For more information on how to use the PICO Spatial SDK's event system, refer to "[Event system](/en_event-system)".
## API reference
The `CollisionComponent`, `RigidBodyComponent`, `PhysicsForceComponent`, `PhysicsVelocityComponent`, and `CollisionEvents` classes provide properties, functions, and events related to collision and force. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

