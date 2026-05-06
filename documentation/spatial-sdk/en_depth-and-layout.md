In spatialized UI, depth represents an object's extension along the Z-axis and is used to describe front-to-back layering relationships. It enables 2D and 3D content to have spatial positioning and alignment capabilities within the same layout. Through depth settings, you can precisely control an object's position in a space.
## What is depth?
In a 2D space, objects usually have only length (X axis) and width (Y axis). Depth is used to describe the extension of an object along the Z-axis (that is, thickness).
Taking paper as an example, adding depth is equivalent to transforming the paper into a box with front and back sides, where the distance between the front and back surfaces is the depth value. During the rendering process, regardless of the depth, 2D content is always located on the back surface of the box.
For 2D objects, depth introduces a computable third dimension to achieve spatial variation effects along the Z-axis direction. Common computing scenarios include:

* **Align**: Adjust alignment according to the Z-axis direction.
* **Padding**: Increase or decrease spacing along the Z-axis.
* **Scale**: Perform scaling calculation along the Z-axis.

For 3D objects, depth is a perceivable physical property. By adjusting the depth value, you can control the model's size, position, or dynamic changes along the Z-axis, thus implementing spatial layering.
## Impact of depth settings on 3D content in the view
In `SpatialView` and `SpatialModelView`, the origin of the 3D content is located at the geometric center of the "box" called `View`. In depth-based layouts, `SpatialView` automatically follows the default depth of `View` (that is, the window depth) and raises 3D content along the Z-axis by half the window depth. When `depth` changes dynamically, the origin of the 3D content also updates in real time, thereby always maintaining a position consistent with the geometric center of the "box".
## Measure the depth of an object
Depth measurement follows Compose's standard measurement process. PICO Spatial SDK has provided methods that support depth measurement in both `LayoutModifierNode` and `MeasurePolicy`.
### Use LayoutModifierNode
`LayoutModifierNode` provides a method for measuring the depth of an object: `measure()`.
```Kotlin
interface LayoutModifierNode : DelegatableNode {
 ...
    fun MeasureScope.measure(measurable: Measurable, constraints: Constraints3D): MeasureResult {
        val res = measure(measurable, constraints.constraints)
        val impl = WrappedMeasureResult(res, constraints.maxDepth)
        return impl
    }
```

If you do not override the `measure()` method, the system will automatically revert to the 2D measurement process. In this case, the depth value in the current node's measurement result (`MeasureResult`) is determined by the depth of its child nodes.
If you override the `measure()` method for the current `LayoutModifierNode`, the depth value in `MeasureResult` is the depth measured by yourself.
### Use MeasurePolicy
`MeasurePolicy` provides an overloaded method for measuring the depth of an object: `measure()`.
```Go
fun interface MeasurePolicy {
    fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints3D
    ): MeasureResult {
        val res = measure(measurables, constraints.constraints)
        return WrappedMeasureResult(res, constraints.maxDepth)
    }
```

In `MeasurePolicy`, if you do not override the `measure()` method, the SpatialUI framework automatically adds default depth measurement logic to the existing 2D measurement process. The result of this default depth measurement logic is determined by the measurement result of the child `NodeCoordinate` of the current `NodeCoordinate` in `NodeChain`.
In the following code sample, if `Box` passes a depth constraint range from `0.dp` to `500.dp` to its child components, then the depth value calculated by `Box`'s `MeasurePolicy` is the maximum depth value reported among all its child nodes. If the maximum depth of the child node is `200.dp`, then the depth value of the `MeasureResult` of the `Box` will also be `200.dp`.
```Kotlin
Box(Modifier.depthIn(0.dp,500.dp)){  
  Child(Modifier.depth(200.dp))
  Child(Modifier.depth(100.dp))
  Child(Modifier.depth(50.dp))
}
```

### Constraints3D transmission and measurement result
In the 3D measurement process, the original 2D layouts (such as `Row`, `Box`, and others) have a `MeasurePolicy` that does not modify the depth constraints (`Constraints3D`). Therefore, when a `Modifier` that affects depth is not introduced, whether `depth` actually takes effect depends on the depth limit passed down by the spatial container. For example:
```Kotlin
Box(Modifier.depth(200.dp))
```

In the 2D measurement process, the depth limit depends on the limit provided by the current spatial container. Assuming the container's depth limit is from `640.dp` to `1280.dp`, then `Modifier.depth(200.dp)` cannot take effect within this fixed range. In the final measurement result, `depth` will be constrained to `640.dp` which is closer to `200.dp`.
To forcibly set `depth` to a custom value, use the following methods:

* Use `Modifier.requiredDepth()` to enforce the use of custom depth;
* Perform layout under the `Box3D` control. Among them, `Box3D`'s `MeasurePolicy` adjusts the current minimum depth constraint to `0` to ensure the depth measurement logic functions correctly.

## Custom 3D layout
You can create custom 3D layout logic through the `layout3D` API. This API further supports the depth layout capability based on the original `layout` and can be used to control the position and depth constraint propagation of child `Measurable`.
```Kotlin
Column(
        modifier =
            Modifier.layout3D { measurable, con ->
               // Measure the depth
                val place = measurable.measure(con)
                currentConstraints = Pair(con.minDepth, con.maxDepth)
                // Save the result
                layout(place.width, place.height, place.depth) { 
                    Custom placement of objects
                    place.place3D(0, 0, 0) 
                }
            }
    )
```

## Customize the depth of an object
`MeasureScope`'s `layout()` method supports specifying the depth of custom objects. You can save your custom 3D measurement results in `MeasureResult`.
```Kotlin
layout(place.width, place.height, place.depth) { 
}
```

## Customize an object's offset on the Z axis

* `placeRelative3D`: Customize the offset of an object (`placeable`) along the Z-axis when placing it. This adapts to RTL (Right-To-Left) on the X-axis.
   ```Kotlin
   layout(place.width, place.height, place.depth) { 
                   // Custom object placement
                   place.placeRelative3D(0, 0, 0) 
               }
   ```

* `place3D`: Customize the offset of an object (`placeable`) along the Z-axis when placing it.
   ```Kotlin
   layout(place.width, place.height, place.depth) { 
                   // Custom object placement
                   place.place3D(0, 0, 0) 
               }
   ```


## Adjust the position of 3D content
In a layout system that supports depth, `View` itself has a default depth, so the initial position of 3D content is also affected by depth. To combine depth and 3D Transform to control the position of 3D content, multiple implementation methods are available.
Using the video player as an example, to make the entity used for video playback closely adhere to the back of the WindowContainer, two methods can be used.

* **Method 1: Use `depth` property**
   Set the `depth` of the `SpatialView` containing the entity to `0`, and set the depth alignment mode to `DepthAlignment.DepthBack` for the parent layout.
   ```Kotlin
   Box(modifier = Modifier.alignDepth(DepthAlignment.DepthBack)) {
       SpatialView(modifier = Modifier.depth(0.dp)
   }
   ```

* **Method 2: Use global position compensation**
   In more complex nested layouts, first calculate the `SpatialView`’s `rootEntity`’s `globalPositionZ` relative to WindowContainer, then use the `globalPositionZ` for compensate the displacement of the target entity to achieve precise alignment.
   ```Kotlin
   SpatialView { content, attachments ->
       val entity = Entity()
       content.addEntity(entity)
       val rootEntity = content.entities.first().getParent()
       val globalPosition = entity?.convertPositionTo(Vector3.ZERO, null)
       val targetEntity = ... // entity whose global position actually needs to be adjusted
       targetEntity.components.get<TransformComponent>()?.let {
           it.position = Vector3(0f, 0f, -globalPosition.z)
       }
       content.add(targetPosition)
       entity.destroy()
   }
   ```
