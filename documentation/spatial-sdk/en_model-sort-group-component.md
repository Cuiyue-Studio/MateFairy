In complex 3D scenes, when multiple **semi-transparent** objects overlap or when it is necessary to clearly define the occlusion relationship between foreground and background, depth sorting conflicts (Z-fighting) may occur, resulting in rendering effects that do not meet expectations. To address this issue, the PICO Spatial SDK provides the `DrawOrderGroupComponent` component, allowing you to precisely control the rendering order of entities.
## Draw order groups and rendering priority
The `DrawOrderGroupComponent` component contains two core properties:

* `drawOrderGroup`: Draw order group. All entities with the same `DrawOrderGroup` object belong to a single draw order group.
* `order`: Rendering priority, used to define the rendering priority of an entity within its draw order group. The smaller the value of the `order` property, the higher the rendering priority; the entity will be drawn later and thus appear in front.

Within the same draw order group, the system will prioritize the value of the `order` property to determine rendering priority, which will override the entity's default physical depth sorting.
```Kotlin
// Create a DrawOrderGroup object
val drawOrderGroup = DrawOrderGroup.create()
// Assign the ball entity to the draw order group corresponding to the DrawOrderGroup object, and set the entity's rendering priority to 1
ball.components.set(DrawOrderGroupComponent(drawOrderGroup, 1))
// Assign the plane entity to the draw order group corresponding to the DrawOrderGroup object, and set the entity's rendering priority to 1
plane.components.set(DrawOrderGroupComponent(drawOrderGroup, 2))
```

## Prerequisites
To use the `DrawOrderGroupComponent` component, you must ensure that there are at least two entities in the scene, and:

* These entities have already attached the `ModelComponent` component or the `ParticleComponent` component.
* The texture of these entities must be semi-transparent.

## Sample code
In the sample code below, the green sphere's `order = 1` is less than the red plane's `order = 2`, so the green sphere has a higher rendering priority and will be drawn above the red plane, regardless of their actual spatial relationship. Although the red plane is physically closer to the camera, the green sphere will be rendered in front of the red plane.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d77a3caa69b4d4ea0d6da932703b272~tplv-goo7wpa0wc-image.image)
```Kotlin
fun DrawOrderGroupDemo() {
    val rootEntity by remember { mutableStateOf(Entity()) }
    // set green ball
    val mesh = MeshResource.createSphere(0.05f)
    val material = PhysicallyBasedMaterial.create().apply { setBaseColor(Color4.GREEN) }
    val ball by remember { mutableStateOf(ModelEntity(mesh = mesh, material = material)) }
    // set red plane
    val meshPlane = MeshResource.createBox(Vector3(0.2f, 0.08f, 0.001f), 0f)
    val materialPlane = PhysicallyBasedMaterial.create().apply { setBaseColor(Color4.RED) }
    val plane by remember { mutableStateOf(ModelEntity(mesh = meshPlane, material = materialPlane)) }
    // set position, plane in front of ball
    ball.components.get<TransformComponent>()!!.setPosition(Vector3(0F, 0F, -0.02F))
    plane.components.get<TransformComponent>()!!.setPosition(Vector3(0F, 0F, 0F))
    // set opacity
    ball.components.set(OpacityControllerComponent(0.5f))
    plane.components.set(OpacityControllerComponent(0.5f))
    // set drawOrderGroup
    val drawOrderGroup = DrawOrderGroup.create()
    ball.components.set(DrawOrderGroupComponent(drawOrderGroup, 1))
    plane.components.set(DrawOrderGroupComponent(drawOrderGroup, 2))
    Column {
        SpatialView(
            modifier = Modifier.fillMaxSize(),
            initial = { content, _ ->
                content.addEntity(rootEntity)
                rootEntity.addChild(ball)
                rootEntity.addChild(plane)
            },
        )
    }
}
```

In the sample code below, since the red plane's `order = 1` is less than the green sphere's `order = 2`, the red plane has a higher rendering priority and will be drawn above the green sphere.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa567243fcb0413992f48ee088599dcb~tplv-goo7wpa0wc-image.image)
```Kotlin
fun DrawOrderGroupDemo() {
    val rootEntity by remember { mutableStateOf(Entity()) }
    // set green ball
    val mesh = MeshResource.createSphere(0.05f)
    val material = PhysicallyBasedMaterial.create().apply { setBaseColor(Color4.GREEN) }
    val ball by remember { mutableStateOf(ModelEntity(mesh = mesh, material = material)) }
    // set red plane
    val meshPlane = MeshResource.createBox(Vector3(0.2f, 0.08f, 0.001f), 0f)
    val materialPlane = PhysicallyBasedMaterial.create().apply { setBaseColor(Color4.RED) }
    val plane by remember { mutableStateOf(ModelEntity(mesh = meshPlane, material = materialPlane)) }
    // set position, plane in front of ball
    ball.components.get<TransformComponent>()!!.setPosition(Vector3(0F, 0F, -0.02F))
    plane.components.get<TransformComponent>()!!.setPosition(Vector3(0F, 0F, 0F))
    // set opacity
    ball.components.set(OpacityControllerComponent(0.5f))
    plane.components.set(OpacityControllerComponent(0.5f))
    // set drawOrderGroup
    val drawOrderGroup = DrawOrderGroup.create()
    ball.components.set(DrawOrderGroupComponent(drawOrderGroup, 2))
    plane.components.set(DrawOrderGroupComponent(drawOrderGroup, 1))
    Column {
        SpatialView(
            modifier = Modifier.fillMaxSize(),
            initial = { content, _ ->
                content.addEntity(rootEntity)
                rootEntity.addChild(ball)
                rootEntity.addChild(plane)
            },
        )
    }
}
```

## Caution

* When using the `ModelComponent`, ensure that the model's material is **semi-transparent**, and that depth testing (`material.setDepthTest(true)`) and depth writing (`material.setDepthWrite(true)`) are enabled. Both options are enabled by default.
* Within the same `DrawOrderGroup` object, each entity's `order` value must be unique and cannot be duplicated.

## API reference
`DrawOrderGroupComponent` class provides properties and functions related to draw order groups and rendering order. For details, see [API reference](https://developer.picoxr.com/spatial-api/index.html).
