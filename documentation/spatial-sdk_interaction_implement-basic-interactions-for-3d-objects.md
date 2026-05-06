In a spatial app, interaction between users and 3D objects is essential. How to enable users to interact naturally with objects in 3D space has become a critical consideration when optimizing the user experience of a spatial app.
To provide users with a complete interaction experience, you need to complete the following three steps. Each step can be adjusted flexibly based on your actual needs.

* Complete the prerequisites for interactions
* Define the actions that trigger interactions
* Define the visual feedback for successful interactions

The basic 3D object interactions introduced in this article specifically refer to interactions completed through APIs provided by PICO Spatial SDK and PICO Spatial UI. To achieve richer interactions using hand tracking, refer to "[Hand Tracking](/hand-tracking)".

## Complete the prerequisites for interactions
First, it is important to clarify that not all visible 3D objects in a spatial app are interactive. To balance user experience and system performance, only objects that meet both of the following conditions can interact with users:

* **Condition 1: Add a collider to the object**
   By default, 3D objects in a spatial app are often just visible, non-interactive visual content. Without a tangible physical shape, users cannot interact with them in any way. Only by adding `CollisionComponent` to define the geometric boundary of the object does it gain the physical basis for being touched.
   Typically, there are two mainstream methods for adding a collider to a 3D object:
   | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4ea45ebc911447a5a1c8b8afd0dc7ced~tplv-goo7wpa0wc-image.image) <br> **Bounding box** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/56275ceeed664553b1d81ae6e12b233a~tplv-goo7wpa0wc-image.image) <br> **Mesh** |
   | --- | --- |
   * **Bounding box:** The bounding box of a 3D object refers to the smallest geometric shape that can completely enclose it. Using the bounding box directly as the collider is the simplest and most efficient solution. Usually, we select a cube with the same dimensions as the bounding box or a capsule of similar size. The advantage is simple configuration, minimal system computation overhead, and excellent performance. However, because the collision boundary differs from the actual visual shape of the object, users may notice that the touch point does not align with the model surface during interaction, resulting in less precise and refined feedback.
   * **Model shape:** This approach generates the collider directly according to the actual geometric outline of the 3D model. Its advantage is extremely precise interaction. The collision boundary accurately matches the object’s shape, allowing users to realistically touch every detail on the model's surface. This provides a natural and authentic interaction experience. However, using this method results in a higher performance cost. Because model shapes are usually composed of complex triangles, the system requires more computing resources for collision detection.
* **Condition 2: Make the object interactable**
   Even if an object has a collider, it does not necessarily mean users can interact with it directly. In spatial computing, the purpose of the collision component is to provide the object with a physical boundary, giving it a "touchable" property in 3D space. This "touch" can come from user interaction, be used for handling collision detection between objects, or simulate physical effects. To further specify that the object allows for interactions, you must also attach `InteractableComponent` to it. This component is the key switch for activating the object's interaction logic and responding to hand pose or ray.
   When you display a 3D object as an `Entity` in `SpatialView`, by default, the `Entity` does not have `CollisionComponent` and `InteractableComponent` attached automatically; you need to set them manually through code.

### Set the collision component
You need to use `CollisionComponent` to define the object's collision-related properties. There are two core elements in the configuration process:

* Collision geometry: `ShapeResource`, which defines the actual range of the object in physical space and directly determines the area that can be interacted with.
* Physical material: `PhysicsMaterialResource`, which determines the object's behavior in the physics engine, such as friction, elasticity, and other physics properties.

In actual interactions, the main focus is on detecting users' hand poses or ray casting, rather than simulating real physical dynamics. Therefore, the configuration of physics materials has minimal impact on the final effect, and the priority is to clearly define the interaction's trigger range. You should select either a bounding box or the model shape to build `ShapeResource` as appropriate for your requirements. As for the material, you can choose the default `PhysicsMaterialResource`.
#### Method 1: Use a bounding box
You can call an API to retrieve the `BoundingBox` of the model asset, use its dimensions as a reference, generate the corresponding geometric shape using the API provided by `ShapeResource`, and finally assign it to `CollisionComponent`.
The following sample code demonstrates how to extract the bounding box data of the earth model and build two different types of colliders for it: a box and a sphere.

* **Set up a box collider**
   ```Kotlin
   earthEntity = withContext(Dispatchers.IO) {
       var bundle: AssetBundle? = null
       try {
           bundle = AssetBundle.load("asset://editor-asset-earth.bundle")
           load(modelName = "earth_outline", bundle = bundle)
       } catch (e: ResourceLoadingException) {
           null
       } finally {
           bundle?.close()
       }
   }?.apply {
       val boundingBox = getVisualBounds(this, recursive = true, enabledOnly = true)
   
       // Use a box as the collider
       val collisionComponent = CollisionComponent(
           collisionShape = listOf(ShapeResource.createBox(boundingBox.size)),
           physicsMaterial = PhysicsMaterialResource(),
       )
       
       components.set(collisionComponent)
   }
   ```

* **Set up a sphere collider**
   ```Kotlin
   earthEntity = withContext(Dispatchers.IO) {
       var bundle: AssetBundle? = null
       try {
           bundle = AssetBundle.load("asset://editor-asset-earth.bundle")
           load(modelName = "earth_outline", bundle = bundle)
       } catch (e: ResourceLoadingException) {
           null
       } finally {
           bundle?.close()
       }
   }?.apply {
       val boundingBox = getVisualBounds(this, recursive = true, enabledOnly = true)
       
       // Use a sphere as the collider
       val collisionComponent = CollisionComponent(
           collisionShape = listOf(ShapeResource.createSphere(boundingBox.size.x / 2f)),
           physicsMaterial = PhysicsMaterialResource(),
       )
       
       components.set(collisionComponent)
   }
   ```


As shown in the figure below, we compare the effects of the two colliders. The white semi-transparent area in the figure represents the actual effective collision boundary of the object.
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7a9387f7dc7a4a7ca08faa2a3997ba16~tplv-goo7wpa0wc-image.image) <br> **Box collider** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/de937a393b884a26b78abdb7377e1431~tplv-goo7wpa0wc-image.image) <br> **Sphere collider** |
| --- | --- |
#### Method 2: Use the model's mesh
Using the model's own mesh to set up the collider is a more precise and rigorous approach, but it also means the system incurs higher performance overhead. Since the child node with the mesh attached is not necessarily located at the root of the model, you need to locate the specific geometry data node. To simplify this process, you can use the [ Spatial Editor](https://developer.picoxr.com/document/spatial-toolkit/know-spatial-editor/) or other tools to help locate it. With the Spatial Editor, you can intuitively search for and extract the child node of the earth model that has the mesh attached.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ca3eed6760104b009e1d8f6e11bf22af~tplv-goo7wpa0wc-image.image" width="3456px" /></div>

As shown in the figure, the mesh of the earth model is attached to a child node named `geo_earth`. After the model is loaded, you can use the `findEntity` API to locate this node, extract the actual mesh data from its bound `ModelComponent`, and then generate a high-precision collider:
```Kotlin
earthEntity = withContext(Dispatchers.IO) {
    var bundle: AssetBundle? = null
    try {
        bundle = AssetBundle.load("asset://editor-asset-earth.bundle")
        load(modelName = "earth_outline", bundle = bundle)
    } catch (e: ResourceLoadingException) {
        null
    } finally {
        bundle?.close()
    }
}?.apply {
    val mesh = findEntity("geo_earth")?.components
                ?.get(ModelComponent::class.java)?.mesh

    mesh?.let {
        val collisionComponent = CollisionComponent(
            collisionShape = listOf(ShapeResource.createConvexMesh(it)),
            physicsMaterial = PhysicsMaterialResource(),
        )

        components.set(collisionComponent)
    }
}
```

The collider generated by the above code is shown in the following figure. Since the earth model itself is a regular sphere, you will notice that the collider generated using its mesh **** is almost identical in both visual appearance and interaction logic to the spherical collider previously built based on `BoundingBox`.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/337cca8242e44e56a3325fa4cf5ac9a8~tplv-goo7wpa0wc-image.image" width="1978px" /></div>

### Set the interactable component
Setting the interactable component for 3D objects is very straightforward. Simply add `InteractableComponent` to the object.
```Kotlin
earthEntity = withContext(Dispatchers.IO) {
    var bundle: AssetBundle? = null
    try {
        bundle = AssetBundle.load("asset://editor-asset-earth.bundle")
        load(modelName = "earth_outline", bundle = bundle)
    } catch (e: ResourceLoadingException) {
        null
    } finally {
        bundle?.close()
    }
}?.apply {
    // Setup collision
    
    components.set(InteractableComponent())
}
```

## Define the actions that trigger interactions
Once a 3D object meets the basic conditions for interaction, you can call the APIs provided by the SDK to further integrate functionalities. To cover common user operations in spatial computing, the SDK encapsulates a series of core APIs starting with `detectSpatial`. These APIs form the foundation of spatial interaction logic and can accurately capture and respond to various user operations:
| **Hand Pose** | **Diagram** | **Specific Operation** | **API** |
| --- | --- | --- | --- |
| Tap | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/60bfeee1f34742df94900833ed354e9b~tplv-goo7wpa0wc-image.image) | Quickly pinch the index finger and thumb of one hand together, then slightly release, to perform the action of pinching the object. | detectSpatialTapGesture() |
| Drag | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/daf9c422a5104180b053d382bd83cb5f~tplv-goo7wpa0wc-image.image) | Pinch a point on the object with your index finger and thumb, then move your hand to drag the object in space. <br>  | detectSpatialDragGesture() |
| Scale | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4dc2f1c8b3cc42dba5a857a9d442ece8~tplv-goo7wpa0wc-image.image) | Pinch two points on the object with the index finger and thumb of each hand, then move your hands closer together or farther apart. | detectSpatialScaleGesture() |
| Rotate | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/01487d0ee04f4403aa5fee8d7f69cb57~tplv-goo7wpa0wc-image.image) | Pinch two points on the object with both hands, then simultaneously rotate both hands clockwise or counterclockwise. | detectSpatialRotateGesture() |
| Custom hand pose | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/32aed72fa9424fa89f75ee40c911e4a2~tplv-goo7wpa0wc-image.image) | Touch the object with both hands, then interact freely. | detectSpatialPointerEvent() |
### Single interaction event
To implement a specific interaction, you need to attach the corresponding `Modifier` to the `SpatialView` that hosts the 3D object, which listens for and captures user interaction events. For example, to enable interaction with the object via dragging, you can configure it using the following code:
```Kotlin
SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) {
            // Handle your own drag logic here.
            // For example, move the object to align with your fingers.
        }
    }
) { content, attachments ->
    // Your scene initialization.
}
```

You can use the standard `Modifier.pointerInput` from Jetpack Compose to uniformly listen for user input (including eye movement, hand pose, and controller). Within the DSL of `pointerInput`, by calling the above hand pose APIs, the system will automatically parse and determine the user's input. Once the system recognizes a specific interaction action, it will trigger the corresponding callback function.
These callback functions receive a parameter containing real-time status, allowing you to perform operations on the target entity based on the data included in the parameter. For example, in `detectSpatialDragGesture()`, the callback returns a `SpatialDragValue` that contains two core properties:

* `dragAmount`**:** Data of type `Offset3D`, representing the vector distance of the user's movement in 3D space (unit: pixels).
* `targetEntity`**:** The target `Entity` currently being interacted with.

By using these two parameters, we can map `dragAmount` in real time to the coordinate transformation of `targetEntity`, thereby enabling a drag effect where the object moves along with the user's hand.
### Multiple interaction events
In practical scenarios, an object often needs to support multiple types of interactions. To configure multiple hand poses for a 3D object, you must implement them separately by chaining multiple `Modifier.pointerInput` calls. Because the hand pose APIs are mutually exclusive internally, they block and interfere with each other, resulting in failed hand pose recognition. Therefore, be sure not to call multiple APIs starting with `detectSpatial` within the same `pointerInput` DSL. The correct approach is to assign a separate `pointerInput` for each hand pose.
For example, to implement a composite interaction of "single-handed drag" and "two-handed scaling", you need to apply two `pointerInput` instances: one dedicated to capturing the user's hand translation, and the other focused on parsing the scaling ratio generated by the hand pose.
```Kotlin
SpatialView(
    Modifier
        .pointerInput(Unit) {
            detectSpatialDragGesture(context) {
                // Handle your own drag logic here
            }
        }
        .pointerInput(Unit) {
            detectSpatialScaleGesture(context) {
                // Handle your own scale logic here
            }
        }
) { content, attachments ->
    
}
```

### Specify interactive objects
In complex 3D scenes, there are often multiple interactive entities. To make a specific hand pose affect only a particular object or category of objects, you can precisely target the interaction by using the `targetedToEntity` parameter in hand pose APIs.
All APIs starting with `detectSpatial` support passing a parameter of type `TargetEntity`. The default value of this parameter is `null`, which means the hand pose will apply to all eligible objects in the space. By explicitly specifying `targetedToEntity`, you can flexibly control the scope of interaction responses.
Two common usages of `TargetEntity`:

* By using the `TargetEntity.hit()` API, you can restrict the interaction to a specific entity and all its child nodes within its hierarchy tree. For example, when there are multiple 3D models in the scene but you want the user's drag pose to only affect the "Earth" model, you can pass that model entity as a parameter. In this way, even if the drag pose touches other objects, the system will not trigger the corresponding callback. The code is as follows:
   ```Kotlin
   var earthEntity by remember { mutableStateOf<Entity?>(null) }
   
   SpatialView(
       Modifier.pointerInput(Unit) {
           detectSpatialDragGesture(
               context,
               earthEntity?.let { TargetEntity.hit(it) }
           ) {
               // Handle your own drag logic here
           }
       }
   ) { content, attachments ->
       earthEntity = withContext(Dispatchers.IO) {
           Entity.load("asset://models/earth.usdz")
       }.apply {
           // setup its collision
           // make it interactable
       }.also {
           content.addEntity(it)
       }
   }
   ```

* To interact with a group of entities that meet specific criteria, use `TargetEntity.any()`. For example, in a complex universe scene, there may be various entities such as stars, planets, and nebulae. To make the drag pose work only for entities whose names start with "Planet" and ignore other entities, pass a condition into `any()`. This approach greatly enhances flexibility when handling interactions with similar entities.
   ```Kotlin
   SpatialView(
       Modifier.pointerInput(Unit) {
           detectSpatialDragGesture(
               context,
               TargetEntity.any { it.getName().startsWith("Planet") }
           ) {
               // Handle your own drag logic here
           }
       }
   ) { content, attachments ->
       val mercuryEntity = withContext(Dispatchers.IO) {
           Entity.load("asset://models/mercury.usdz")
       }.apply { 
           setName("PlanetMercury")
           // setup its collision
           // make it interactable
       }.also { content.addEntity(it) }
   
       val earthEntity = withContext(Dispatchers.IO) {
           Entity.load("asset://models/earth.usdz")
       }.apply { 
           setName("PlanetEarth") 
           // setup its collision
           // make it interactable
       }.also { content.addEntity(it) }
   
       // add other planets ...
   
       val sunEntity = withContext(Dispatchers.IO) {
           Entity.load("asset://models/sun.usdz")
       }.apply { 
           setName("Sun")
           // setup its collision
           // make it interactable
       }.also { content.addEntity(it) }
   
       val moonEntity = withContext(Dispatchers.IO) {
           Entity.load("asset://models/moon.usdz")
       }.apply { 
           setName("Moon")
           // setup its collision
           // make it interactable
       }.also { content.addEntity(it) }
   }
   ```


### Specify the action type
Although in regular development, we typically focus only on the hand pose itself (such as the drag or scale poses), in certain advanced use cases, you may need to determine the interaction type more precisely.
Except for `detectSpatialPointerEvent()`, all APIs beginning with `detectSpatial` provide the `InteractionKind` property in their callbacks. This property clearly indicates which type of interaction triggers the current operation.

* Single-handed interaction APIs: The callback parameters directly include a variable named `interactionKind`.
* Two-handed interaction APIs: Since different forms of operation may exist for each hand, the callback provides two variables, `leftInteractionKind` (left hand) and `rightInteractionKind` (right hand), respectively.

Currently, the system mainly supports the following `InteractionKind`:
| **Direct pinch** | **Poke** | **Gaze and pinch** | **Tap with ray** | **Pointer** |
| --- | --- | --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/100a21a9c59248d78dd9f3221dec548f~tplv-goo7wpa0wc-image.image) <br> **DirectPinch** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fcafeaa78a4544feae1473ec4f986edb~tplv-goo7wpa0wc-image.image) <br> **Poke** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ddf741e58b4a4bb2ac4bafb4376aa9f4~tplv-goo7wpa0wc-image.image) <br> **GazePinch** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a04fd0dde1d438e858cb3e6a1ba65fb~tplv-goo7wpa0wc-image.image) <br> **RayBasedPinch** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7a799cba2717460f9021c6463a9091de~tplv-goo7wpa0wc-image.image) <br> **Pointer** |
In certain composite interactions, it may be necessary to reuse the same API to implement different functions. For example:

* Single-handed drag: Enables spatial translation of the entity.
* Single-handed swipe: Enables axial rotation of the entity.

You only need to use the core API `detectSpatialDragGesture()` to cover both hand poses simultaneously. The key to implementation lies in determining `InteractionKind`. By identifying different interaction forms, you can configure differentiated response logic for the same physical action:
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e0a425bb8544462393103c1b50111f14~tplv-goo7wpa0wc-image.image) <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6a9e9c0ad82c47fd847a54e25dc5d445~tplv-goo7wpa0wc-image.image) <br> **Drag objects using DirectPinch and GazePinch** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c0a1ab7964c44b41b4acf0b82257debd~tplv-goo7wpa0wc-image.image) <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d9b6c5da3e044eb48fcf24995edd00dd~tplv-goo7wpa0wc-image.image) <br> **Rotate objects using Poke** |
| --- | --- |
In the callback function of `detectSpatialDragGesture()`, the system injects a parameter of type `SpatialDragValue`. By accessing the `interactionKind` property of this parameter, you can accurately determine the user's interaction form and execute the corresponding business logic.
```Kotlin
SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) { dragValue ->
            val kind = dragValue.interactionKind

            when (kind) {
                InteractionKind.DirectPinch, InteractionKind.GazePinch -> {
                    // Handle your drag logic here.
                }
                
                InteractionKind.Poke -> {
                    // Handle your rotate logic here.
                }

                else -> {
                    // You can also handle other types of interactions in your own case.
                }
            }

        }
    }
) { content, attachments ->
    // Your scene initialization.
}
```

## Define the visual feedback for successful interactions
With the above content, you are now able to flexibly define interactive entities and accurately capture hand poses and relevant core data during the interaction process. Now, proceed to the final and most critical step: implementing interaction feedback. Only through intuitive visual or physical feedback can scattered logic be connected into a complete user interaction experience.
In spatial apps, common interaction feedback mainly covers the following aspects: highlight indication, spatial displacement, uniform scaling, and multi-axis rotation. In addition, you can also customize personalized interaction effects for specific use cases. Next, we will introduce the implementation methods for these core interaction effects one by one.
### Object highlighting
In spatial apps, besides direct touch at close range, 'eye-hand coordination' is also an extremely efficient interaction method. When the user's gaze focuses on an object and is combined with hand poses, remote interaction can be achieved. This interaction method can significantly reduce large-scale physical movements in the spatial environment, effectively alleviate interaction fatigue, and greatly improve comfort and fluency of operation.
In this case, visual feedback is crucial. The system must clearly inform the user of the current gaze point and use visual effects to indicate the object currently being interacted with. You need to attach `HoverEffectComponent` to the object to easily achieve highlight indication triggered by gaze movement.
```Kotlin
var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) {
            // Handle your own drag logic here
        }
    }
) { content, attachments ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
        components.set(HoverEffectComponent())
    }.also {
        content.addEntity(it)
    }
}
```

Run the above code in PICO Emulator:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ce18c012850e42e7b8ab46eb0e9041d7~tplv-goo7wpa0wc-image.image></video>
### Object movement
In spatial apps, enabling objects to move according to user operations is the most fundamental interaction effect. By using `detectSpatialDragGesture()` and the returned `dragAmount`, we can manipulate the object's coordinates in 3D space in real time.
However, in real cases, you need to address two key adaptation issues:

* **Unit conversion:** To maintain consistency with Jetpack Compose's standard API, `dragAmount` returns data in pixels. However, in spatial computing, an object's `TransformComponent` is built based on meters. Therefore, unit conversion must be performed before applying displacement to the entity. For more information, refer to "[Convert units of length](/convert-length-unit)".
* **Coordinate system mapping:** `dragAmount` follows Compose View's coordinate system, while object movement occurs in the space's physics coordinate system. Because the Y-axis directions of these two coordinate systems are completely opposite (the View's coordinate system is positive downward, while the space's physics coordinate system is positive upward), the Y-axis must be inverted when performing incremental mapping (that is, use `-dragAmount.y`). For more information, refer to "[Convert coordinate spaces](/convert-coordinate-space)".

The following code snippet demonstrates how to achieve precise object drag-and-drop behavior:
```Kotlin
val context  = LocalContext.current
val converter = LocalPhysicalLengthConverter.current
val density = LocalDensity.current

var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) { dragValue ->
            // Convert drag offset into Meters.
            val offsetXInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.x.toDp(), LengthUnit.Meters)
            }
            val offsetYInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.y.toDp(), LengthUnit.Meters)
            }
            val offsetZInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.z.toDp(), LengthUnit.Meters)
            }

            // Update the position of the Earth by offset in meters.
            earthEntity?.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(
                        Vector3(
                            position.x + offsetXInMeter,
                            position.y - offsetYInMeter,
                            position.z + offsetZInMeter,
                        )
                    )
                }
            }
        }
    }
) { content, _ ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
    }.also {
        content.addEntity(it)
    }
}
```

Run the above code in PICO Emulator:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6dd5d653a5e04f04bfc6ec1b7e87e1d1~tplv-goo7wpa0wc-image.image></video>
### Object scaling
In a spatial app, scaling is one of the most immersive experiences. It allows users to observe the microscopic details of a model through a simple stretching pose without changing the object's position. The most intuitive scaling interaction is "two-handed grabbing". The size of the object is manipulated by changing the distance between both hands (moving closer or farther apart).
The key API for implementing this interaction is `detectSpatialScaleGesture()`. In the callback function of this API, the system injects a parameter of type `SpatialScaleValue`. The core data is `scaleValue`, which captures the scaling increment along each axis in 3D space based on the hand pose.
You only need to multiply the `scaleVector` in the object's current `TransformComponent` by the `scaleValue` returned by the hand pose to update the entity's scaling state in real time.
The following code demonstrates how to achieve smooth object scaling through two-handed collaboration:
```Kotlin
var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialScaleGesture(context) { scaleValue ->
            earthEntity?.apply {
                components[TransformComponent::class.java]?.apply {
                    setScaleVector(scaleVector * scaleValue.scaleValue)
                }
            }
        }
    }
) { content, _ ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
    }.also {
        content.addEntity(it)
    }
}
```

Run the above code in PICO Emulator:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d2ed1ea27ebb40d6a960a13811089fa1~tplv-goo7wpa0wc-image.image></video>
### Object rotation
Rotation is also an indispensable form of spatial interaction feedback. It provides users with a comprehensive observation perspective, allowing users to examine every detail of a 3D object in place without having to walk around the entity.
In development, the ways to implement rotation are highly flexible. Rotation can usually be customized according to actual needs, combined with different interactive hand poses.
#### Rotation by single-handed drag
In addition to enabling object movement, `detectSpatialDragGesture()` is also the preferred solution for implementing "drag-triggered rotation," with core logic similar to movement. You can use `dragAmount` provided in the callback to map the user's hand displacement in 3D space to the object's rotation angles on different axes.
Unlike direct coordinate translation, the spatial displacement generated by the interaction between the user and the object cannot be directly converted into an equivalent rotation in radians. To ensure smooth and controllable interaction, it is usually necessary to introduce a custom sensitivity parameter to align the operational feel with user expectations.
In practice, it is recommended to write an extension function for `Offset3D` to encapsulate its logic and convert it into `Rotation3D`. `Rotation3D` is the standard data type for describing rotation transformations in a spatial app.
The implementation of this extension function is as follows:
```Kotlin
fun Offset3D.toRotation3D(sensitivity: Float): Rotation3D {
    val delta = Vector3(x, y, z)
    val axis = delta.normalize()

    return Rotation3D(
        degree = delta.length() * sensitivity,
        axis = RotationAxis3D(-axis.y, axis.x, axis.z),
        pivot = NormalizedPoint3D.Center,
    )
}
```

During implementation, there is a key point to note: the displacement axis of the hand pose is not equivalent to the rotation axis of the object.
To align with intuition, when dragging along the horizontal direction (X axis), it is generally expected that the object rotates around its vertical center (Y axis); similarly, dragging in the vertical direction (Y axis) corresponds to rotation around the X axis. Therefore, when building the mapping, the offset needs to be reorganized in the order of Y, X, and Z.
With this transformation, you can implement rotation using the same approach as handling displacement. The `Rotation3D` type has a built-in `toQuaternion()` method, which can directly convert the rotation increment into a quaternion `Quat`. Finally, by updating the entity's `TransformComponent.rotation` property, the object can precisely respond to the hand pose and achieve the intended spatial rotation. Here, `toRotation3D()` is used, and the `sensitivity` parameter passed in is `180f`. You need to adjust this value yourself to make the user experience closer to real-world interactions.
```Kotlin
val context  = LocalContext.current
val converter = LocalPhysicalLengthConverter.current
val density = LocalDensity.current

var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) { dragValue ->
            // Convert drag offset into Meters.
            val offsetXInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.x.toDp(), LengthUnit.Meters)
            }
            val offsetYInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.y.toDp(), LengthUnit.Meters)
            }
            val offsetZInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.z.toDp(), LengthUnit.Meters)
            }
            
            val rotation3DInMeter = 
                Offset3D(offsetXInMeter, offsetYInMeter, offsetZInMeter)
                    .toRotation3D(180f)
            
            // Update the rotation of the Earth.
            earthEntity?.apply {
                components[TransformComponent::class.java]?.apply {
                    setQuaternion(quaternion * rotation3DInMeter.toQuaternion())
                }
            }
        }
    }
) { content, _ ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
    }.also {
        content.addEntity(it)
    }
}
```

Run the above code in PICO Emulator:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d6967843a0a4cfc81c7c71ff87ff3ef~tplv-goo7wpa0wc-image.image></video>
#### Rotation by two-handed interaction
In addition to single-handed control, the system also provides the `detectSpatialRotateGesture()` API, specifically designed to implement two-handed rotation effects.
This type of interaction simulates the action of rotating an object with both hands in the real world. The user pinches two points of the entity with both hands, and by moving their hands relative to each other in 3D space, the object is synchronously rotated clockwise or counterclockwise across multiple dimensions.
The callback function of this API returns a parameter of type `SpatialRotateValue`. The core data contained within is a `Rotation3D` structure, which accurately captures the rotation increment generated by the user's two hands. By calling its built-in `toQuaternion()` method, you can seamlessly convert this rotation data into a quaternion `Quat` and apply it to the entity's `TransformComponent.rotation`.
```Kotlin
var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialRotateGesture(context) { rotateValue ->
            earthEntity?.apply {
                components[TransformComponent::class.java]?.apply {
                    setQuaternion(quaternion * rotateValue.rotation.toQuaternion())
                }
            }
        }
    }
) { content, _ ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
    }.also {
        content.addEntity(it)
    }
}
```

Run the above code in PICO Emulator:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bbfe3e94246a4e28a6d04b24a0a4958f~tplv-goo7wpa0wc-image.image></video>
#### Rotation by single-handed swipe
Single-handed drag can be used for both rotation and movement, but this causes semantic conflicts in development. If you want to support both dragging to move and dragging to rotate, you cannot simply map all displacement data to the same transformation. To solve this problem, you need to distinguish the user's intent by checking `InteractionKind`.
You can use the `detectSpatialDragGesture()` API again, but within the callback function, implement differentiated logic based on the interaction kind:

* **Pinch**: `DirectPinch` and `GazePinch`. When the user pinches the object with their fingers, the system calls `dragToMove()` to implement spatial translation of the object.
* **Poke**: `Poke`. When the user pokes the surface of the object with their fingers, the system calls `dragToRotate()` to implement axial rotation of the object.

This design effectively avoids functional conflicts, allowing 3D objects to provide a more realistic interactive experience: pinch to move, poke to rotate.
```Kotlin
val context  = LocalContext.current
val converter = LocalPhysicalLengthConverter.current
val density = LocalDensity.current

var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView(
    Modifier.pointerInput(Unit) {
        detectSpatialDragGesture(context) { dragValue ->
            // Convert drag offset into Meters.
            val offsetXInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.x.toDp(), LengthUnit.Meters)
            }
            val offsetYInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.y.toDp(), LengthUnit.Meters)
            }
            val offsetZInMeter = with(density) {
                converter.dpToLength(dragValue.dragAmount.z.toDp(), LengthUnit.Meters)
            }
            
            val offset3DInMeter = Offset3D(offsetXInMeter, offsetYInMeter, offsetZInMeter)
            val rotation3DInMeter = 
                offset3DInMeter.toRotation3D(180f)
            val kind = dragValue.interactionKind
            val target = dragValue.targetEntity ?: return@detectSpatialDragGesture
            
            // Handle gestures separately
            when (kind) {
                InteractionKind.DirectPinch, InteractionKind.GazePinch -> {
                    dragToMoveEntity(target, offset3DInMeter)
                }
                InteractionKind.Poke -> {
                    dragToRotateEntity(target, rotation3DInMeter)
                }
                else -> { }
            }
        }
    }
) { content, _ ->
    earthEntity = withContext(Dispatchers.IO) {
        Entity.load("asset://models/earth.usdz")
    }.apply { 
        // setup its collision
        // make it interactable
    }.also {
        content.addEntity(it)
    }
}
```


* For the implementation of `dragToMove()`, you can refer to the previous implementation of object movement.
* For the implementation of `dragToRotate()`, you can use the previously mentioned single-handed drag rotation method, or use effects that are even closer to real-world interactions.

## Example use case
After configuring the core components above, the foundation for spatial interaction is now in place. However, to create an experience that is truly intuitive and natural, you still need to follow these three principles during development:

* **Visual focus:** The object being interacted with must provide clear visual feedback (such as highlighting) to ensure the user clearly perceives the subject of the interaction.
* **Interaction intuition:** Operational logic should align with real-world actions, reducing the user's cognitive load and operational burden.
* **Smooth effects:** Interaction feedback should be smooth, ensuring that the movement of virtual objects is closer to the experience in the real world.

To understand how these principles are applied, we will build a specific scene where users can deeply interact with an earth model. Combining the key technical points discussed earlier, the following functional logic is implemented:

* **Single-handed pinch and drag:** Enables spatial translation of the object.
* **Rotation by single-handed poke:** Enables smooth rotation of the object around its axis.
* **Transformation by two-handed operation:** Enables uniform scaling of the object.

Before diving into the code implementation details, let us first experience the final interaction effect through this demonstration video:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ccf16aa32c7549f6a1e02041a210eefa~tplv-goo7wpa0wc-image.image></video>
### Step 1: Build a scene
When building this scene, we prepare two different earth model resources to meet different development needs:

* **Open-source basic earth model:** Free resource from the community, retaining the original mesh and map, suitable for rapid prototyping or basic functionality validation.
* **Spatial enhanced earth model (recommended):** This model is made with [Spatial Editor](https://developer.picoxr.com/document/spatial-toolkit/know-spatial-editor/). In this model, Shader Graph is used to add a unique white edge halo effect to the open-source model. This enhances the technological feel of the model and helps users clearly identify the interactive subject within the spatial background.

You can select and load the corresponding model file according to your own visual standards.
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c10f14eb09b64c62a9e3fa28d6a2dce7~tplv-goo7wpa0wc-image.image) <br> [Earth](https://skfb.ly/6TwGG) by Akshat is licensed under [Creative Commons Attribution](http://creativecommons.org/licenses/by/4.0/) <br> <a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2741008300c54f0bb2955ef0a61d4992~tplv-goo7wpa0wc-image.image" filename="Earth.usdz" download>Earth.usdz</a> | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2d47bc1802674e02a9c752c75114badf~tplv-goo7wpa0wc-image.image) <br> Enhanced version powered by Shader Graph with Spatial Editor <br> <a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/244f18be8b814b18a20c062bd79741c3~tplv-goo7wpa0wc-image.image" filename="editor-asset-earth.bundle" download>editor-asset-earth.bundle</a> |
| --- | --- |
To achieve optimal visual performance, the enhanced earth model will be used for building the scene below. The specific implementation process is as follows:

1. Load the model asset, scale it to a suitable ratio, and accurately place it at the user's preferred interactive viewing distance.
2. Add `CollisionComponent` to the model. Since the earth model is a regular geometric object, although the mesh data can be read directly, for performance optimization, we recommend creating a spherical collider based on the size of its bounding box.
3. Add `InteractableComponent` to the model, which is a prerequisite for enabling the object to respond to hand pose APIs.
4. Add `DirectionalLight` to the scene, and by adjusting the lighting angle and intensity, ensure the model surface's texture is clear, the brightness is appropriate, and realistic 3D shading is produced.
5. Add `HoverEffectComponent` to the model. When the user's gaze or hand ray sweeps across the earth model, the system automatically triggers the preset highlight animation effect.

You can refer to the following code snippet to build and initialize the scene:
```Kotlin
val rootEntity = remember { Entity() }
var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView { content, _ ->
    // Initialize Earth
    earthEntity = withContext(Dispatchers.IO) {
        var bundle: AssetBundle? = null
        try {
            bundle = AssetBundle.load("asset://editor-asset-earth.bundle")
            load(modelName = "earth_outline", bundle = bundle)
        } catch (e: ResourceLoadingException) {
            null
        } finally {
            bundle?.close()
        }
    }?.apply {
        // Scale to the correct size and move it to
        components[TransformComponent::class.java]?.apply {
            scaleVector = Vector3(0.1f)
            position = Vector3(0f, 1.5f, -2f)
        }

        // Setup collision
        val boundingBox = getVisualBounds(this, recursive = true, enabledOnly = true)
        val collisionComponent = CollisionComponent(
            collisionShape = listOf(ShapeResource.createSphere(boundingBox.size.z / 2f)),
            physicsMaterial = PhysicsMaterialResource(),
        )
        components.set(collisionComponent)
        
        // Enable interaction
        components.set(InteractableComponent())

        // Add Hover effect
        components.set(HoverEffectComponent())
    }?.also {
        rootEntity.addChild(it)
    }

    // Setup the light of the scene
    Entity().apply {
        components.set(DirectionalLightComponent(Color.White.toColor4(), 800f))
    }.also {
        content.addEntity(it)
    }

    content.addEntity(rootEntity)
}
```

### Step 2: Define the actions that trigger interactions
In the current scene, we aim to add diverse interactions to the earth model based on hand poses.

* **Movement by single-handed pinch:** With `detectSpatialDragGesture()`, when a pinch gesture is detected, synchronize the earth model's position in real time with the hand's position to implement precise spatial movement.
* **Scaling by two-handed operation:** Call `detectSpatialScaleGesture()`, and by changing the distance between two hands, adjust the earth model's `scaleVector` in real time, allowing users to seamlessly observe surface details.
* **Rotation by single-handed poke:** When the user uses `Poke` (fingertip touch/poke), rotate the object around its Y axis.

Single-handed poke rotation is different from pinch rotation. This is because the user is not holding the object. If you use the previously-mentioned real-time rotation that follows the hand, users might find the interaction unnatural, as if the object is not responding correctly to their swipe. To achieve a more natural experience, it is recommended to introduce the "delayed rotation" mechanism for single-handed poke. This simulates the physics characteristics of poking a globe in reality: the user traces a path across the surface of the object, and at the moment their finger leaves, the object starts rotating based on the speed and direction of the swipe. This poke gesture not only aligns with physics intuition, but also gives the 3D object a realistic sense of mass and inertia.
The following code demonstrates how to effectively handle these three differentiated interactions by determining `InteractionKind`:
```Kotlin
val context  = LocalContext.current
val converter = LocalPhysicalLengthConverter.current
val density = LocalDensity.current

var earthEntity by remember { mutableStateOf<Entity?>(null) }

var isRotated by remember { mutableStateOf(false) }
var draggedOffset by remember { mutableStateOf(Offset3D.Zero) }

SpatialView(
    Modifier
        .pointerInput(Unit) {
            detectSpatialScaleGesture(
                context,
                targetedToEntity = earthEntity?.let {  TargetEntity.hit(it) }
            ) { scaleValue ->
                val target = scaleValue.targetEntity ?: return@detectSpatialScaleGesture
                
                scaleEntity(target, scaleValue.scaleValue)
            }
        }
        .pointerInput(Unit) {
            detectSpatialDragGesture(
                context,
                targetedToEntity = earthEntity?.let {  TargetEntity.hit(it) },
                onDragStart = {
                    draggedOffset = Offset3D.Zero
                },
                onDragEnd = {
                    if (isRotated) {
                        earthEntity?.let {
                            dragToRotateEntity(it, draggedOffset)
                        }
                    }
                }
            ) { dragValue ->
                val offsetXInMeter = with(density) {
                    converter.dpToLength(dragValue.dragAmount.x.toDp(), LengthUnit.Meters)
                }
                val offsetYInMeter = with(density) {
                    converter.dpToLength(dragValue.dragAmount.y.toDp(), LengthUnit.Meters)
                }
                val offsetZInMeter = with(density) {
                    converter.dpToLength(dragValue.dragAmount.z.toDp(), LengthUnit.Meters)
                }

                val offset3DInMeter = Offset3D(offsetXInMeter, offsetYInMeter, offsetZInMeter)
                val kind = dragValue.interactionKind
                val target = dragValue.targetEntity ?: return@detectSpatialDragGesture
  
                when (kind) {
                    InteractionKind.DirectPinch, InteractionKind.GazePinch -> {
                        dragToMoveEntity(target, offset3DInMeter)
                        isRotated = false
                    }
                    InteractionKind.Poke -> {
                        draggedOffset -= offset3DInMeter
                        isRotated = true
                    }
                    else -> {
                        isRotated = false
                    }
                }
            }
        }
) { content, _ ->
    // Scene initialization
}
```

In this implementation, we do not directly drive object rotation in the real-time callback of `detectSpatialDragGesture()`, but instead adopt an asynchronous processing strategy of "record first, trigger later":

* Use the variable `draggedOffset` to accumulate displacement in real time during the interaction.
* Introduce the flag variable `isRotated` to serve as the state machine for interaction. It can accurately distinguish the current drag, and only when the system determines the current hand pose is `Poke` and the hand pose ends, will the "delayed rotation" logic be activated.

Ultimately, by triggering delayed rotation at the moment the hand pose is released based on the state, we simulate the physics feedback of poking a globe in reality.
### Step 3: Define the visual feedback for successful interactions
In the current scene, the three interactions correspond to distinctly different visual feedback. For the special interaction of delayed rotation, we will explore two mainstream implementation approaches in depth, and you can choose according to your needs.
#### Implement delayed rotation with tween animation
Using tween animation to implement object rotation is a common solution, which is simple and intuitive. You only need to define the initial and target states of the rotation, and the system will automatically calculate and fill in the intermediate transitions.
To closely replicate the real effect of poking a globe, we need to pay attention to the following during implementation:

* Lock the rotation to the Y axis to ensure the earth model always rotates steadily around its axis (Y axis), preventing visual confusion caused by multi-axis rotation.
* Use the `EaseType` of tween animation to finely control the timing curve. By configuring a "fast-to-slow" deceleration effect (such as `EASE_OUT`), simulate the process of an object gradually stopping due to friction, making the animation's transition more natural.

The following is the core code for implementing object rotation based on tween animation:
```Kotlin
fun rotateEntityByTweenAnimation(target: Entity, offset: Offset3D) {
    val rotation3D = offset.copy(offset.x, 0f, 0f)
        .toRotation3D(180f)

    val from = target.components[TransformComponent::class.java]?.quaternion ?: return
    val to = from * rotation3D.toQuaternion()

    AnimationResource.generateWithTweenAnimation(
        TweenAnimation.createTweenAnimation(
            bindTarget = AnimationBindTarget.bindRotation(),
            from = from,
            to = to,
            easeType = EaseType.EASE_OUT
        )
    ).use {
        target.playAnimation(it)
    }
}
```

In `detectSpatialDragGesture()`, call `rotateEntityByTweenAnimation()` within `onDragEnd()` to implement the final rotation. The code is as follows:
```Kotlin
SpatialView(
    Modifier
        .pointerInput(Unit) {
            detectSpatialDragGesture(
                context,
                targetedToEntity = earthEntity?.let {  TargetEntity.hit(it) },
                onDragEnd = {
                    if (isRotated) {
                        earthEntity?.let {
                            rotateEntityByTweenAnimation(it, draggedOffset)
                        }
                    }
                }
            ) { dragValue ->
                // Keep previous dragging logic
            }
        }
) { content, _ ->
    // Scene initialization
}
```

Suppose the user performs a left-to-right poke gesture, resulting in a spatial displacement increment of approximately 0.8 meters, that is, `Offset3D(0.8f, 0f, 0f)`.
Based on the mapping logic mentioned earlier, the system converts the linear displacement in the horizontal direction (X axis) into angular displacement around the vertical axis (Y axis). At this point, the earth model will exhibit a smooth left-to-right rotation. This process, handled by interpolation in tween animation, not only accurately reproduces the user's intended action, but also gives the model a realistic sense of physical mass through easing effects.
The dynamic behavior of the earth model is as follows:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/53d3952fdebc4f06a17bd72ab6ac2a57~tplv-goo7wpa0wc-image.image></video>
This implementation meets basic interaction needs, but it has a problem. The following video demonstrates the effect when the user pokes from left to right by 0.8 meters and 1 meter, respectively.

* `Offset3D(0.8f, 0f, 0f)`
   <video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2de87c5da5bf40c8959d99ba04838adc~tplv-goo7wpa0wc-image.image></video>
* `Offset3D(1.0f, 0f, 0f)`
   <video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/78f4ea622db448069e77bb7b6d9c9b5b~tplv-goo7wpa0wc-image.image></video>

Although the logic of tween animation is simple, in scenes involving large-angle rotation, it triggers the unique shortest path rotation issue in 3D space.
Because angles are periodic, when the target rotation angle exceeds 180° (for example, 270°), the interpolation algorithm, for efficiency, rotates via the shortest path (that is, -90°). To the user, the earth model will rotate counterintuitively in the opposite direction. Similarly, if the rotation amplitude exceeds 360°, the object will only display the rotation corresponding to the remainder, losing the full rotation of that cycle.
To use tween animation, you must manually segment the angles. For example, break down 450° into a sequence of multiple 90° segments and use `AnimationResource.sequence()` for chained playback. However, when handling angles that are not divisible by the segment size (such as 455°), this approach requires precisely scaling down the duration of the final animation segment proportionally. Additionally, segmented execution causes noticeable abruptness at the transitions between `EaseType`, making it extremely difficult to simulate smooth, damped deceleration.
Therefore, to implement natural interactions that exceed 180° and deliver a realistic physical feel, introducing a physics engine is a more elegant and fundamental solution.
#### Implement delayed rotation with a physics engine
When the rotation amplitude exceeds 180°, the interpolation mechanism of tween animation can no longer meet the requirements for natural interaction. To break free from the constraints of "shortest path rotation", introducing a physics system is the optimal path to an immersive experience.
The core of the physics system lies in simulating real-world motion mechanics. In our scene, instead of rigidly specifying the target angle, the displacement generated by user interaction is converted into the initial angular velocity applied to the earth model. The physics engine then takes over subsequent dynamics calculations, restoring the object's natural rotation after force is applied.
To implement this physics logic, you need to configure the following:

1. Add `RigidBodyComponent` to the model and configure the physics properties required by the physics engine.
2. Since the spatial position of the earth model is directly controlled by the drag logic, we do not want the physics engine to participate. By setting `isTranslationLocked`, we can strictly limit the physics simulation to the rotational dimension.
3. By setting the angular velocity damping coefficient `angularDamping`, you can simulate air friction. The higher the value, the faster the rotational kinetic energy dissipates, and the quicker the object comes to a stop. We initialize this value to `1.0f`, and you can fine-tune it based on your actual needs.

```Kotlin
var earthEntity by remember { mutableStateOf<Entity?>(null) }

SpatialView { content, _ ->
    // reset part of earthEntity initialization
    
    // Config Physics related properties
    earthEntity?.components.set(RigidBodyComponent().apply {
        this.rigidBodyMode = RigidBodyMode.DYNAMIC
        this.isTranslationLocked = Bool3(true)
        this.angularDamping = 1f
    })
    
    // reset part of scene initialization
}
```

Once we add `RigidBodyComponent` to the earth model, its dynamic behavior is officially managed by the physics engine. Next, we need to convert the kinetic energy accumulated by the hand pose into the object's initial angular velocity.
You can implement this logic using the `PhysicsVelocityComponent` API. We map the user's displacement along the X axis during interaction to the value of the `angularVelocity` parameter. To make the rotation effect more intuitive, it is recommended to introduce a sensitivity coefficient (`Sensitivity`). By fine-tuning this coefficient, you can balance the ratio between the amplitude of the hand pose and the rotation speed of the object, thereby creating a control feel that closely resembles a real globe.
For more information about `RigidBodyComponent` and `PhysicsVelocityComponent`, refer to "[Add collisions and external forces](/add-collisions-and-external-forces)".
```Kotlin
fun rotateEntityByPhysics(target: Entity, offset: Offset3D) {
    val sensitivity = 7f
    val velocity = offset.x * sensitivity

    target.components.set(
        PhysicsVelocityComponent().apply {
            angularVelocity = Vector3(0f, velocity, 0f)
        }
    )
}
```

Similar to the implementation of `TweenAnimation`, in the `detectSpatialDragGesture()` function's `onDragEnd()`, you need to call the newly defined `rotateEntityByPhysics()` to implement the final rotation effect for the earth model. The code is as follows:
```Kotlin
SpatialView(
    Modifier
        .pointerInput(Unit) {
            detectSpatialDragGesture(
                context,
                targetedToEntity = earthEntity?.let {  TargetEntity.hit(it) },
                onDragStart = {
                    draggedOffset = Offset3D.Zero
                    // remove previous PhysicsVelocityComponent to clear effect
                    earthEntity?.components?.remove(
                        PhysicsVelocityComponent::class.java
                    )
                },
                onDragEnd = {
                    if (isRotated) {
                        earthEntity?.let {
                            rotateEntityByPhysics(it, draggedOffset)
                        }
                    }
                }
            ) { dragValue ->
                // Keep previous dragging logic
            }
        }
) { content, _ ->
    // Scene initialization
}
```

There is an important detail here: you must remove the currently attached `PhysicsVelocityComponent` in the `onDragStart()` callback.
If you do not perform the removal, the system will not be able to inject new physics velocity again in `onDragEnd`. This will cause the interaction logic to be locked in the state of the initial poke, making any subsequent pokes ineffective. By adopting the "remove first, inject later" strategy, we ensure that each interaction can precisely capture and apply the latest momentum.
After completing the above configuration, when the user performs a left-to-right poke gesture with a distance of 1 meter, resulting in `Offset3D(1.0f, 0f, 0f)`, the earth model will no longer be restricted by the limitation of "shortest path rotation". It will smoothly start multiple rotations to the right based on the initial angular velocity given by the hand pose, naturally decelerating with angular damping, and presenting a realistic physics simulation effect:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/58974cb12c7f423ebeaaffba62397ffb~tplv-goo7wpa0wc-image.image></video>
By comparing the demonstration videos, it is clear that the physics engine completely resolves the limitations of tween animation. Its advantages are mainly reflected in two core aspects:

* **Breaking constraints on rotation:** The earth model is no longer restricted by the 'shortest path' limitation and can rotate continuously, surpassing 180° or even completing multiple revolutions, depending on the force applied by the user. This accurately reproduces the user's intended operation.
* **Realistic physics feel:** With dynamic simulation based on angular damping, the earth model demonstrates pronounced linear attenuation as it transitions from high-speed rotation to a gradual stop. This non-uniform deceleration effect closely aligns with real-world physics laws, imparting a realistic sense of mass to the virtual model.

### Learn more: The effect of scaling
In previous discussions, whether calculating the rotation angle for tween animation or setting the initial angular velocity for the physics engine, we introduced a sensitivity parameter. In most scenes, this parameter can be set as a constant. However, in certain dynamic scenes, if the object's size changes with scaling interactions, a fixed sensitivity parameter will no longer be suitable.
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/58346fdb25f64ab1a574fe1ce31a28dc~tplv-goo7wpa0wc-image.image) <br> **The scaling ratio of the earth model is 0.1** | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c26dc40254b04b3795ff077acbcfc1d0~tplv-goo7wpa0wc-image.image) <br> **The scaling ratio of the earth model is 0.3** |
| --- | --- |
Changes in the object's scaling ratio directly determine its physical size, thereby affecting the effective range for user interaction. Suppose when the earth model's scaling ratio is 0.1 times, its diameter corresponds to an effective interaction distance of 0.5 meters; when scaled to 0.3 times, this distance expands to 1.5 meters.
If we fix the sensitivity coefficient:

* At a 0.1 scale, when the user swipes across the entire surface (0.5m), the earth model will rotate 120°.
* At a 0.3 scale, when the user swipes across the entire surface (1.5m), the earth model will rotate 360°.

This feedback—'the larger the object, the faster it rotates'—can lead to severe sensory imbalance. On large models, even slight hand movements can cause intense rotation, while small objects are difficult to manipulate. To correct this deviation, we recommend making the sensitivity parameter inversely proportional to the object's scaling ratio. In this way, regardless of whether the object is scaled up or down, when the user moves the same proportional distance on the model's surface, the angular displacement generated by the object always remains consistent. This dynamic compensation mechanism is the key to achieving high-quality, intuitive spatial interaction.
For example, when calculating the initial velocity, we can also take the object's own scaling into account. The updated code is as follows:
```Kotlin
fun rotateEntityByPhysics(target: Entity, offset: Offset3D) {
    val sensitivity = 7f / target.scale().x
    val velocity = offset.x * sensitivity

    target.components.set(
        PhysicsVelocityComponent().apply {
            angularVelocity = Vector3(0f, velocity, 0f)
        }
    )
}
```

## Appendix: Complete code sample
The complete code for interacting with the earth model in the above example use case is as follows:
```Kotlin
@Composable
fun InteractionSDKScreen() {
    val context  = LocalContext.current
    val density = LocalDensity.current
    val converter = LocalPhysicalLengthConverter.current

    var earthEntity by remember { mutableStateOf<Entity?>(null) }

    var isRotated by remember { mutableStateOf(false) }
    var draggedOffset by remember { mutableStateOf(Offset3D.Zero) }

    SpatialView(
        Modifier
            .pointerInput(Unit) {
                detectSpatialScaleGesture(context) { scaleValue ->
                    val target = scaleValue.targetEntity ?: return@detectSpatialScaleGesture

                    scaleEntity(target, scaleValue.scaleValue)
                }
            }
            .pointerInput(Unit) {
                detectSpatialDragGesture(
                    context = context,
                    targetedToEntity = earthEntity?.let {  TargetEntity.hit(it) },
                    onDragStart = {
                        draggedOffset = Offset3D.Zero
                        earthEntity?.components?.remove(PhysicsVelocityComponent::class.java)
                    },
                    onDragEnd = {
                        if (isRotated) {
                            earthEntity?.let {
                                rotateEntityByPhysics(it, draggedOffset)
                            }
                        }
                    }
                ) { dragValue ->
                    val offsetXInMeter = with(density) {
                        converter.dpToLength(dragValue.dragAmount.x.toDp(), LengthUnit.Meters)
                    }
                    val offsetYInMeter = with(density) {
                        converter.dpToLength(dragValue.dragAmount.y.toDp(), LengthUnit.Meters)
                    }
                    val offsetZInMeter = with(density) {
                        converter.dpToLength(dragValue.dragAmount.z.toDp(), LengthUnit.Meters)
                    }

                    val offset3DInMeter = Offset3D(offsetXInMeter, offsetYInMeter, offsetZInMeter)
                    val kind = dragValue.interactionKind
                    val target = dragValue.targetEntity ?: return@detectSpatialDragGesture

                    when (kind) {
                        InteractionKind.DirectPinch, InteractionKind.GazePinch -> {
                            isRotated = false
                            dragToMoveEntity(target, offset3DInMeter)
                        }

                        InteractionKind.Poke -> {
                            isRotated = true
                            draggedOffset += offset3DInMeter
                        }

                        else -> {
                            isRotated = false
                        }
                    }
                }
            }
    ) { content, _ ->
        earthEntity = withContext(Dispatchers.IO) {
            var bundle: AssetBundle? = null

            try {
                bundle = AssetBundle.load("asset://editor-asset-earth.bundle")

                load(modelName = "earth_outline", bundle = bundle)
            } catch (e: ResourceLoadingException) {
                null
            } finally {
                bundle?.close()
            }
        }?.apply {
            components[TransformComponent::class.java]?.apply {
                scaleVector = Vector3(0.2f)
                position = Vector3(0f, 1.5f, -2f)
            }

            val boundingBox = getVisualBounds(this, recursive = true, enabledOnly = true)
            val collisionComponent = CollisionComponent(
                collisionShape = listOf(ShapeResource.createSphere(boundingBox.size.x / 2f)),
                physicsMaterial = PhysicsMaterialResource(),
            )

            components.set(collisionComponent)
            components.set(InteractableComponent())
            components.set(HoverEffectComponent())
            components.set(RigidBodyComponent().apply {
                this.rigidBodyMode = RigidBodyMode.DYNAMIC
                this.isTranslationLocked = Bool3(true)
                this.angularDamping = 1f
            })
        }?.also {
            content.addEntity(it)
        }

        val lightEntity = Entity().apply {
            components.set(DirectionalLightComponent(Color.White.toColor4(), 800f))
        }
        content.addEntity(lightEntity)
    }
}

fun scaleEntity(target: Entity, scaleValue: Float) {
    target.apply {
        components[TransformComponent::class.java]?.apply {
            setScaleVector(scaleVector * scaleValue)
        }
    }
}

fun rotateEntityByPhysics(target: Entity, offset: Offset3D) {
    val sensitivity = 10f / (target.scale().x * 2)
    val velocity = offset.x * sensitivity

    target.components.set(
        PhysicsVelocityComponent().apply {
            angularVelocity = Vector3(0f, velocity, 0f)
        }
    )
}

fun dragToMoveEntity(target: Entity, offset: Offset3D) {
    target.apply {
        components[TransformComponent::class.java]?.apply {
            setPosition(
                Vector3(
                    position.x + offset.x,
                    position.y - offset.y,
                    position.z + offset.z,
                )
            )
        }
    }
}
```

