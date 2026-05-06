A bounding box is a geometric volume used to represent the spatial extent occupied by an object in 3D space. It is usually a cuboid used to approximately describe the spatial boundary of the entity. This article explains how to obtain the bounding box of an entity.
## Prerequisites
The calculation of the bounding box depends on the entity's complete hierarchy and space state in the scene. Therefore, before obtaining the bounding box of an entity, ensure that the entity has been added to an entity tree, including:

* Add the entity to `SpatialViewContent`before getting its bounding box.
* Add the entity as a child node of an entity already added to `SpatialViewContent` before getting its bounding box.

## Get an entity's bounding box
Get the bounding box of an entity through the `getVisualBounds` function. `getVisualBounds()` calculates the bounding box in the specified reference space based on the current entity and its hierarchy, and returns a `BoundingBox` object containing the following properties:
| **Property** | **Type** | **Description** |
| --- | --- | --- |
| boundingSphereRadius | Float | The radius of the bounding sphere enclosing the bounding box, used to accelerate collision detection or clipping calculations. Accuracy error: 0.00001F. |
| center | Vector3 | The central point of the bounding box, which is commonly used to determine the object's "reference point" or central position in rotation, scaling, and collision detection. Accuracy error: 0.00001F. |
| halfExtent | Vector3 | Half the edge lengths of the bounding box along the X, Y, and Z axes (that is, half the length, width, and height), which is used to facilitate the calculation of bounding box volume, collision detection boundaries, bounding box visualization, and more. Accuracy error: 0.00001F. |
| max | Vector3 | The maximum coordinate vertice of the bounding box, used to determine the ending point of the bounding box in space, it is usually calculated together with `min` to determine the bounding box's spatial extent and size. Accuracy error: 0.00001F. |
| min | Vector3 | The minimum coordinate vertex of the bounding box, used to determine the starting point of the bounding box in space, it is usually calculated together with `max` to determine the bounding box's spatial extent and size. Accuracy error: 0.00001F. |
| size | Vector3 | The total size of the bounding box in the X, Y, and Z directions, which is expressed as a vector and can be directly used to determine an object's spatial occupancy, scale factor, or physical dimensions. Accuracy error: 0.00001F. |
The code sample is as follows:
```Kotlin
SpatialView { content, _ ->
    // Create a new entity (empty node) as the parent entity in the scene
    val entity = Entity()
    // Add this entity to the scene content of SpatialView
    content.addEntity(entity)
    // Get the bounding box of the entity
    entity.getVisualBounds(null)
    // Create a child entity
    val child = Entity()
    // Mount the child entity under the parent entity to form a parent-child hierarchy
    entity.addChild(child)
    // Get the bounding box of the child entity
    child.getVisualBounds(null)
}
```

## Entity size change and bounding box update
When the entity's `scale` value changes, its bounding box updates in real time accordingly. The size of the bounding box is directly related to the entity's world transform; therefore, scaling, rotation, or translation will affect the final extent of the bounding box.
The following code loads a model entity (`pico_robot_static.usdz`) and dynamically adjusts its scale via a slider. The current bounding box size is displayed in real time at the bottom of the interface, making it easy to intuitively see the bounding box change synchronously with the scale factor.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/899f9bf43ee748c491575ff5dbd1533f~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun ScaledVisualBoundsDemo() {
    // Current scale factor, initial value is 0.3
    var scale by remember { mutableFloatStateOf(0.3f) }
    // Load a static model entity and scale it to 0.3 of its original size during initialization
    val picoRobot = remember {
        Entity.load("asset://model/pico_robot_static.usdz").also {
            it.components.get<TransformComponent>()?.scaleBy(0.3f)
        }
    }
    
    // Layout container: vertically centers the scene, slider, and text
    Column(
        modifier = Modifier.fillMaxSize().backgroundMaterial(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SpatialView(modifier = Modifier.size(300.dp, 400.dp)) { content, _ ->
            content.addEntity(picoRobot)
        }
        // Display prompt for current scale factor
        Text("Slide to change entity scale to $scale")
        // Use the slider to dynamically adjust scale factor
        SegmentSlider(
            initialStep = 3,
            segmentCount = 4,
            onStepChange = {
                scale = it / 10f
                // Update the scale vector of the entity
                picoRobot.components.get<TransformComponent>()?.scaleVector =
                    Vector3(scale, scale, scale)
            }
        )
        // Displaythe current bounding box's half size (halfExtent) in real time
        // halfExtent represents the distance from the center of the bounding box to the boundary, used to describe the size of the bounding box
        Text("Current visual bounds = ${picoRobot.getVisualBounds(null).halfExtent}")
    }
}
```

## Lay out 3D content accurately through VisualBounds
When placing multiple models in `SpatialView`, if the bounding box of each model is not known in advance, overlaps may occur between the models. For example, the following image shows the result after setting the spacing between two models to 0.1 meters. They partially overlap.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f8a46402437c4d51ac7be3ca6791882c~tplv-goo7wpa0wc-image.image)
To get a more reasonable spacing, first get the model's bounding box and calculate the precise relative position based on its size. For example, using `boundingBox.halfExtent.x * 2` as the spacing between models can ensure that the two models are placed exactly side by side.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e4681ac6d5304f879081f6e05325c2eb~tplv-goo7wpa0wc-image.image)
The following code demonstrates how to dynamically adjust the relative position of two entities through the bounding box. After clicking the "Relayout 3D" button, new spacing is calculated based on the bounding box size of the first entity to avoid overlap between the two entities.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/82d1f612c406475bbf8984962b7af945~tplv-goo7wpa0wc-image.image)
```Kotlin
@Composable
fun VisualBoundsDemo() {
    // Load the first model entity and scale it to 30%
    val picoRobot = remember {
        Entity.load("asset://model/pico_robot_static.usdz").also {
            it.components.get<TransformComponent>()?.scaleBy(0.3f)
        }
    }    
    // Clone this entity and both use the same material instance
    val picoRobot1 = remember {
        picoRobot.clone(
            cloneOptions = Entity.CloneOptions(recursive = true, shouldShareMaterialInstance = true)
        )
    }   
    // Used to cache the half-size of the first entity's bounding box in the X direction
    var halfExtentX = 0f    
    Control layout switching between empirical spacing and bounding box spacing
    var toggleRelayout3D = true
    // UI layout container: includes 3D view and operation buttons
    Column(
        modifier = Modifier.fillMaxSize().backgroundMaterial(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SpatialView(modifier = Modifier.size(300.dp, 400.dp)) { content, _ ->
            content.addEntity(picoRobot)
            picoRobot1?.let { content.addEntity(it) }
        }
        
        // Button: recalculate the relative position of two entities when clicked
        Button(
            onClick = {
                // If the bounding box's half-size has not yet been calculated, calculate it once
                if (halfExtentX == 0f) {
                    val helmetVisualBounds = picoRobot.getVisualBounds(null)
                    halfExtentX = helmetVisualBounds.halfExtent.x
                }
                // Update the position of the second entity
                picoRobot1?.components?.set(TransformComponent().apply {
                   // When toggleRelayout3D is true, use bounding box width to calculate spacing; otherwise, use a fixed empirical value of 0.1 m
                    position = Vector3(if (toggleRelayout3D) halfExtentX * 2 else 0.1f, 0f, 0f)
                    scaleBy(0.3f)
                })
                toggleRelayout3D = !toggleRelayout3D
            }
        ) {
            Text(text = "Relayout 3D")
        }
    }
}
```

## API reference
For more information about the `getVisualBounds` function and the `BoundingBox` class, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
## FAQs
### Why does an entity’s bounding box size not change after scaling?
This depends on the value passed to the `relativeTo` parameter when retrieving the bounding box.
When `relativeTo` is set to the entity itself, the returned `halfExtent` remains unchanged regardless of how the entity or its parent is scaled. This is an expected result, because in this case the bounding box is returned in local space, and dimensions in local space are not affected by scaling.
Regardless of the value passed to `relativeTo`, you can use the following formula to verify whether the returned result is correct: Returned size × scale of `relativeTo` relative to the container = the result retrieved when `relativeTo` is `null` (that is, relative to the container).
This formula is only intended to verify the relationship between scaling and size. It assumes that the entity has not been rotated. If the entity is rotated, this relationship no longer holds.
### Why does the entity appear visually misaligned?
This is usually because the model's original coordinate origin does not coincide with the center of its bounding box. It is recommended to check the origin settings of the model asset during creation.
### Why does the size of `visualBounds` change after rotating an entity?
`visualBounds` represents an axis-aligned bounding box calculated in a specific coordinate space. When an entity is rotated, the bounding box is recalculated to keep its edges aligned with the coordinate axes while still fully enclosing the object, so its dimensions (width, height, and depth) typically change.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6f78d398e4e5465da9361734e4b37bff~tplv-goo7wpa0wc-image.image)
