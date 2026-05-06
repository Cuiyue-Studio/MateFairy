In spatial apps, common interactive objects include floating UI elements, controls with rotation effects, and various 3D models. To provide richer information during event handling and enable more precise manipulation of these elements in three-dimensional space, the PICO Spatial SDK extends spatial gesture functionality and offers additional 3D data in spatial interaction events for monitoring gesture interactions with objects in 3D scenes.
## Capture spatial gesture events
### Tap: SpatialTapGesture
SpatialUI provides a series of `Modifier` and event detection methods for capturing tap events in 3D space. You can set tap response logic for specific entities.
SpatialUI offers two types of tap event detection APIs:

* `PointerInputScope.detectTapGestures()`: Detects 2D tap events. This interface is provided by Jetpack Compose.
* `PointerInputScope. detectSpatialTapGesture()`: Detects 3D tap events. This interface is provided by the PICO Spatial SDK.

You can use the `targetedToEntity` parameter to specify the target entity to listen to, enabling detection of spatial tap events for designated objects or under specific conditions.
The following example demonstrates how to use `SpatialView` and `detectSpatialTapGesture()` to randomly change the color of an entity after it is tapped.
```Kotlin
@Composable
private fun SpatialTapToChangeColorDemo() {
    // Define the color of the entity
    val entityColors: MutableMap<Entity, MutableState<Color>> = remember { mutableStateMapOf() }

    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        SpatialView(
            modifier =
                Modifier.size(300.dp)
                    .background(Color.DarkGray)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectSpatialTapGesture(context, TargetEntity.any()) {
                            // After the entity is tapped, update its color
                            if (entityColors[it.targetEntity]?.value == null) {
                                entityColors[it.targetEntity] =
                                    mutableStateOf(ColorCollection.random())
                            } else {
                                entityColors[it.targetEntity]!!.value = ColorCollection.random()
                            }
                        }
                    },
            update = { content, _ ->
                // Update the color of the entity
                content.entities.forEach {
                    // Note: Renderable is a custom utility class API in the Demo, not an API provided by the SDK
                    if (it is Renderable) {
                        it.color(entityColors[it]?.value ?: Color(color = 0x75757575), true)
                    }
                }
            },
        ) { content, _ ->
            val sphereEntity = SphereEntity(0.05f)
            sphereEntity.apply {
                moveBy(y = 0.1f, z = 0.025f)
                components.set(InteractableComponent())
                components.set(
                    CollisionComponent(
                        collisionShape = listOf(ShapeResource.createSphere(0.05f)),
                        physicsMaterial = PhysicsMaterialResource(),
                    )
                )
            }

            val boxEntity = BoxEntity(0.1f)
            boxEntity.apply {
                this.moveBy(x = -0.1f, y = -0.1f, z = 0.1f)
                components.set(InteractableComponent())
                components.set(
                    CollisionComponent(
                        collisionShape = listOf(ShapeResource.createBox(Vector3(0.1f, 0.1f, 0.1f))),
                        physicsMaterial = PhysicsMaterialResource(),
                    )
                )
            }
            val capsuleEntity = CapsuleEntity(height = 0.1f, radius = 0.05f)
            capsuleEntity.apply {
                this.moveBy(x = 0.1f, y = -0.1f, z = 0.15f)
                components.set(InteractableComponent())
                components.set(
                    CollisionComponent(
                        collisionShape =
                            listOf(ShapeResource.createCapsule(height = 0.2f, radius = 0.05f)),
                        physicsMaterial = PhysicsMaterialResource(),
                    )
                )
            }
            content.addEntity(sphereEntity)
            content.addEntity(boxEntity)
            content.addEntity(capsuleEntity)
        }
    }
}
```

### Drag: SpatialDragGesture
SpatialDragGesture extends drag operations by adding support for the Z axis (forward and backward direction), bringing spatial depth to interactions.
SpatialUI offers two types of drag event detection APIs:

* `PointerInputScope. detectDragGestures()`/`detect Horizontal DragGestures()`/`detect Vertical DragGestures()`/`detectDragGestures AfterLongPress()`: Detects 2D drag events. These interfaces are provided by Jetpack Compose.
* `PointerInputScope`*`. `*`detect Spatial DragGesture()`: Detects 3D drag events. This interface is provided by the PICO Spatial SDK.

You can use the `detectSpatialDragGesture()` function to obtain 3D displacement changes and the position and direction of the device (such as a hand or controller), and implement drag interactions based on this data.
#### Implement drag interactions based on the 3D displacement changes of the gesture
The following code example demonstrates how to use the `detectSpatialDragGesture()` function to obtain the 3D displacement changes of the gesture and apply them to 2D UI.
```Kotlin
@Composable
fun SpatialDragSampleForUI() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        // Define the state of offset
        var offset3D by remember { mutableStateOf(Offset3D.Zero) }
        Box(
            modifier =
                Modifier.size(250.dp)
                    // Apply offset to 2D UI via modifier
                    .offset { IntOffset(x = offset3D.x.roundToInt(), y = offset3D.y.roundToInt()) }
                    .zOffset { offset3D.z }
                    .pointerInput(Unit) {
                        // Detect hand pose interaction
                        detectSpatialDragGesture(context) { spatialDragValue ->
                            // Update offset
                            offset3D += spatialDragValue.dragAmount
                        }
                    }
                    .background(Color.Red)
        )
    }
}
```

#### Implement drag interactions based on the position and direction of the device (such as a hand or controller)
The following code example demonstrates how to use the `detectSpatialDragGesture()` function to obtain the position and orientation of the device (such as the hand or controller), and implement drag interaction based on these data.
```Kotlin
val context = LocalContext.current
val converter = LocalPhysicalLengthConverter.current
val density = LocalDensity.current
var offset3D by remember { mutableStateOf(Offset3D.Zero) }
var rotation by remember { mutableStateOf(Rotation3D.identity()) }
SpatialView(
    modifier =
        Modifier.fillMaxSize().pointerInput(Unit) {
            detectSpatialDragGesture(context = context, targetedToEntity = TargetEntity.any()) {
                offset3D += it.dragAmount
                rotation = it.inputDevicePose.rawRotation
            }
        },
    initial = { content, _ ->
        val entity = Entity()
        // Entity is not interactable by default. To make an entity interactable, you should
        // both
        // add a
        // [InteractableComponent] and a [CollisionComponent].
        entity.components.set(InteractableComponent())
        entity.components.set(
            CollisionComponent(
                collisionShape = listOf(ShapeResource.createSphere(radius = 0.3f)),
                physicsMaterial = PhysicsMaterialResource(),
            )
        )
        content.addEntity(entity)
    },
    update = { content, _ ->
        val convertQuat =
        content.convertRotation(
            rotation.toQuaternion(),
            ViewCoordinateSpace.Global,
            content.localSpatialCoordinateSpace,
        )

        content.entities
            .first()
            .components[TransformComponent::class.java]
        ?.setQuaternion(convertQuat)
        ?.setPosition(
            Vector3(
                x = convertPxToMeter(offset3D.x, density, converter),
                y = convertPxToMeter(-offset3D.y, density, converter),
                z = convertPxToMeter(offset3D.z, density, converter),
            )
        )
    },
) 
```

### Rotation: spatialRotateHandPose
SpatialRotateHandPose is the spatial rotation hand pose capability provided by PICO OS 6, used for natural and intuitive rotation interactions with 2D and 3D content in spatial scenarios.
Compared to 2D hand poses based on planar touch in Jetpack Compose, SpatialRotateHandPose enables interaction using three-dimensional spatial hand poses, supports free rotation of models in space, and can accurately identify the target entity being interacted with. You can use the `detectSpatialRotateGesture()` function to capture SpatialRotateHandPose.
SpatialRotateHandPose supports all spatial interaction methods in PICO OS 6 (ray, eye-hand, Poke), allows specifying the entity being interacted with, and adding constraint axes for rotation. When the user pinches with both hands and rotates 2D or 3D content, the system triggers the corresponding hand pose callback and returns `SpatialRotateValue`.
#### **Implement spatial rotation of 2D content based on hand pose**
The following code example demonstrates how, when the user rotates a 2D view in space with both hands, the view responds in real time and displays the corresponding three-dimensional rotation effect.
```Kotlin
@Composable
fun SpatialRotate2DViewDemo() {
    val context = LocalContext.current
    // Define rotation state
    var rotate by remember { mutableStateOf(Rotation3D.identity()) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier.align(Alignment.Center)
                    // Apply rotation to 2D view
                    .rotate3D {
                        rotate
                    }
                    .size(300.dp)
                    .background(Color.Yellow)
                    .pointerInput(Unit) {
                        // Detect spatial rotation hand pose
                        detectSpatialRotateGesture(
                            context,
                            onRotateStart = { },
                            onRotateEnd = { },
                        ) {
                            // Update rotation state for `rotate`
                            rotate = rotate.rotateBy(it.rotation)
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Text("rotate me, rotate = $rotate")
        }
    }
}
```

#### **Implement spatial rotation of 3D content based on hand pose**
In the following code example, when the user rotates a 3D entity in space with their hand, the system applies the rotation generated by the hand pose to the model in real time.
```Kotlin
@Composable
fun SpatialRotateOn3DModelDemo() {
    val context = LocalContext.current
    // Define rotation state
    var rotate by remember { mutableStateOf(Rotation3D.identity()) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SpatialView(
            modifier =
                Modifier.size(500.dp)
                    .pointerInput(Unit) {
                        // Detect spatial rotation hand pose
                        detectSpatialRotateGesture(
                            context,
                            targetedToEntity = TargetEntity.any(),
                            onRotateStart = { },
                            onRotateEnd = { },
                        ) {
                            // Update rotation state for `rotate`
                            rotate = rotate.rotateBy(it.rotation)
                        }
                    },
            initial = { content, _ ->
                val boxEntity = BoxEntity(0.1f)
                boxEntity.setName("2")
                boxEntity.components.set(InteractableComponent())
                boxEntity.components.set(
                    CollisionComponent(
                        collisionShape = listOf(ShapeResource.createBox(Vector3(0.1f, 0.1f, 0.1f))),
                        physicsMaterial = PhysicsMaterialResource(),
                    )
                )
                content.addEntity(boxEntity)
            },
            update = { content, _ ->
                // Convert the coordinate system of `rotate` from ViewCoordinateSpace to SpatialCoordinateSpace
                val currentRotate =
                    content.convertRotation(
                        rotate.toQuaternion(),
                        ViewCoordinateSpace.Global,
                        content.localSpatialCoordinateSpace,
                    )

                // Apply rotation to 3D entity
                content.entities
                    .first()
                    .components[TransformComponent::class.java]
                    ?.setQuaternion(currentRotate)
            },
        )
    }
}
```

### Scaling: spatialScaleHandPose
SpatialScaleHandPose is the scaling hand pose capability provided by PICO OS 6, used for scaling 2D and 3D content in spatial scenarios. You can call the `detectSpatialScaleGesture()` function to capture this hand pose.
Compared to 2D hand poses based on planar touch in Android / Compose, SpatialScaleHandPose enables interaction using three-dimensional spatial hand poses, supports scaling models in space, and can accurately identify the target entity being interacted with.
The following code example demonstrates how to use the `detectSpatialScaleGesture()` function to obtain the `scale` value of the hand pose, and then scale the object based on this value.
```Kotlin
@Composable
fun SpatialScaleSampleForUI() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        // Define scale state
        var scale by remember { mutableFloatStateOf(1f) }
        Box(
            modifier =
                Modifier.size(250.dp)
                     // Apply scale to 2D UI
                     .scale(scale)
                     .background(Color.Red)
                     .pointerInput(Unit) {
                        // Listen for the scale value of the gesture
                        detectSpatialScaleGesture(context) { 
                            // Update scale state
                            scale *= it.scaleValue 
                        }
                    }
        )
    }
}
```

### Spatial transformation: spatialTransformHandPose
SpatialTransformHandPose is the spatial transformation hand pose capability provided by PICO OS 6, allowing users to use both hands to simultaneously translate, rotate, and scale 2D or 3D objects. Triggered when the user pinches the target object with both hands. SpatialTransformGesture supports all interaction methods on PICO OS 6, including ray, eye-hand coordination, and near-field poke. The user can specify the target entity (`entity`) for interaction and set constraint axes for rotation operations.
Compared to 2D gestures based on planar touch in Jetpack Compose, SpatialTransformGesture interacts using three-dimensional spatial gestures, allowing free translation, rotation, and scaling of models in space, and accurately identifying the target entity being interacted with.
You can call the `detectSpatialTransformGesture()` function to capture this gesture. This function returns a `SpatialTransformValue` object, which contains incremental data for translation, rotation, and scaling.
If only one hand is used for operation, the `SpatialTransformValue` will only contain the translation delta.

#### Implement spatial transformation of 2D content based on gestures
The following sample code demonstrates how to use the `detectSpatialTransformGesture` function to capture spatial transformation gestures for 2D objects, and perform corresponding translation, rotation, and scaling operations on 2D objects based on the returned incremental data.
```Kotlin
@Composable
fun SpatialTransformOn2DView() {
    val context = LocalContext.current
    // 1.define rotate, drag, scale state
    var rotate by remember { mutableStateOf(Rotation3D.identity()) }
    var drag by remember { mutableStateOf(Offset3D.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                // 4.apply drag
                Modifier.graphicsLayer {
                    translationX = dragAmount.x
                    translationY = dragAmount.y
                }
                    .pointerInput(Unit) {
                      // 2. detect spatial transform gesture
                        detectSpatialTransformGesture(
                            context,
                        ) { value ->
                            //3.Update Scale, rotate, drag
                            scale *= value.scaleValue
                            rotate = rotate.rotateBy(value.rotation)
                            drag += value.dragAmount
                        }
                    }
                    // 4.apply scale and rotate
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationX = rotate.toQuaternion().toEulerAngles().pitch
                        rotationY = rotate.toQuaternion().toEulerAngles().yaw
                        rotationZ = rotate.toQuaternion().toEulerAngles().roll
                    }
                    .size(300.dp)
                    .background(Color.Yellow),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Transform 2D", style = PicoTheme.typography.titleLarge)
        }
    }
}
```

#### Implement spatial transformation of 3D content based on gestures
The following sample code demonstrates how to use the `detectSpatialTransformGesture` function to capture spatial transformation gestures for 3D objects, and perform corresponding translation, rotation, and scaling operations on 3D objects based on the returned incremental data.
```Kotlin
@Composable
fun SpatialTransformOn3DDemo() {
    val context = LocalContext.current
    val density = LocalDensity.current
    val converter = LocalPhysicalLengthConverter.current

    // 1. define rotate, scale, drag state
    var rotate by remember { mutableStateOf(Rotation3D.identity()) }
    var dragAmount by remember { mutableStateOf(Offset3D.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    
    SpatialView(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // 2. detect spatial transform gesture
                    detectSpatialTransformGesture(
                        context,
                        targetedToEntity = TargetEntity.any()
                    ) {
                        // 3. update rotate, scale, drag state
                        scale *= it.scaleValue
                        rotate = rotate.rotateBy(it.rotation)
                        dragAmount += it.dragAmount
                    }
                },
        initial = { content, _ ->
            val boxEntity = BoxEntity(0.1f)
            boxEntity.components.set(InteractableComponent())
            boxEntity.components.set(
                CollisionComponent(
                    collisionShape = listOf(ShapeResource.createBox(Vector3(0.1f, 0.1f, 0.1f))),
                    physicsMaterial = PhysicsMaterialResource(),
                )
            )
            content.addEntity(boxEntity)
        },
        update = { content, _ ->
            // 4. apply rotate, scale, drag state to entity
            val currentRotate =
                content.convertRotation(
                    rotate.toQuaternion(),
                    ViewCoordinateSpace.Global,
                    content.localSpatialCoordinateSpace,
                )
            content.entities
                .first()
                .components[TransformComponent::class.java]?.setScaleVector(Vector3(scale, scale, scale))
                ?.setQuaternion(currentRotate)
                ?.setPosition(
                    Vector3(
                        x = convertPxToMeter(dragAmount.x, density, converter),
                        y = convertPxToMeter(-dragAmount.y, density, converter),
                        z = convertPxToMeter(dragAmount.z, density, converter),
                    )
                )
        }
    )
}
```

### Rules for gesture event consumption
When you handle gestures with `PointerInput`, you must consider its event consumption rules. Within the same `pointerInput` modifier:

* Multiple gestures (for example, tap and drag) share the same event stream.
* Once a gesture is recognized and consumes the event, the event is no longer delivered to other gestures.

Therefore, if you define gestures for different objects within the same `pointerInput`, conflicts may occur, and only one gesture may take effect, for example:
```Kotlin
Modifier
.pointerInput(Unit) {
detectSpatialTapGesture(context = context, targetedToEntity = TargetEntity.any {it != baseBody}) {...}
detectSpatialDragGesture(context = context, targetedToEntity = TargetEntity.any { it == baseBody }) {...}
}
```

It is recommended that you separate different gestures into different `pointerInput` modifiers to avoid event consumption conflicts and ensure that each gesture takes effect within its own scope, for example:
```Kotlin
Modifier
.pointerInput(Unit) {
detectSpatialTapGesture(context = context, targetedToEntity = TargetEntity.any {it != baseBody}) {...}
}
.pointerInput(baseBody) {
detectSpatialDragGesture(context = context, targetedToEntity = TargetEntity.any { it == baseBody }) {...}
}
```

## Automatically configure interaction components in SpatialModelView
To simplify the development process, in `SpatialModelView`, the system automatically adds `InteractableComponent` and `CollisionComponent` components to the model entity (`modelEntity`) and all its child entities, making the model entity an interactive object.
If these components already exist on the model entity, they will not be overwritten.

When you use any of the following functions to capture spatial gesture events in `SpatialModelView`, if the `targetedToEntity` parameter of the function is set to `null` or `TargetEntity.any()`, the gesture can interact with any 2D or 3D entity in `SpatialModelView`.

* `detectSpatialTapGesture()`
* `detectSpatialDragGesture()`
* `detectSpatialRotateGesture()`
* `detectSpatialScaleGesture()`
* `detectSpatialTransformGesture()`
   The `targetedToEntity` parameter is set to `null` or `TargetEntity.any()`, which respectively mean:
   
   * `null`: No specific target entity is designated.
   * `TargetEntity.any()`: Any entity can be used as the interaction target.


### Sample code
The following sample code demonstrates how to use the `detectSpatialTapGesture()` function in `SpatialModelView` to capture tap events in 3D space. The `detectSpatialTapGesture()` function's `targetedToEntity` parameter is set to `null`, indicating that no specific target entity is designated for interaction, so the gesture can interact with any 2D or 3D entity in `SpatialModelView`.
```Kotlin
SnackbarHost {
    val context = LocalContext.current
    val snackState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    SpatialModelView(
        source = source,
        modifier =
            Modifier.size(150.dp).background(Color.Yellow).pointerInput(Unit) {
                detectSpatialTapGesture(context = context, targetedToEntity = null) {
                    Log.i(TAG, "tap on model: $it")
                    it.targetEntity?.let { targetEntity ->
                        scope.launch {
                            snackState.show(message = "Entity ${targetEntity.id} is tapped")
                        }
                    }
                }
            },
        resizability = Resizability.FitInside,
    ) { state ->
        when (state) {
            is ModelLoadingState.Loading ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is ModelLoadingState.Error ->
                Text(text = "Load model failed: ${state.reason}", color = Color.Red)

            is ModelLoadingState.Success -> Model(model = state.model)
        }
    }
}
```

