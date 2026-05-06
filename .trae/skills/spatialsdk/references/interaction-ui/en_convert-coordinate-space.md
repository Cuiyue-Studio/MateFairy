In spatial computing, different objects may operate in different coordinate spaces. Coordinate spaces is a coordinate system defined by handedness (left or right), origin, and units of measurement. The coordinate space conversion functionality enables you to convert geometric quantities, such as position, vector, or direction, from one reference coordinate system to another.
To perform the conversion, simply provide the geometric quantity along with its source reference coordinate system and target reference coordinate system. The system will then compute and return the equivalent representation of the geometric quantity in the target space coordinate.
## Different types of coordinate spaces
A coordinate space can be associated with `WindowContainer`, `Stage`, `SpatialView`, `Entity`, or `View`. The properties and behaviors of a coordinate space vary depending on its associated object.
### Coordinate space of WindowContainer
`WindowContainer` defines its coordinate space differently depending on whether it handles a `View` or an `Entity`.
When associated with `View` (including `SpatialView`), the coordinate space of `WindowContainer` is essentially a `ViewCoordinateSpace.Global`. This is a left-handed coordinate system measured in virtual pixels, with its origin (0, 0) located at the top-left corner of the back of `WindowContainer`. This method of defining coordinates is very common in 2D computer graphics, where (0, 0) typically represents the top-left corner of the rendering area and the positive y-axis extends downward, reflecting the arrangement of screen pixels.
```SCSS
(0,0)────────→ +X  
│  
↓ +Y  
(back plane)
```

When associated with `Entity`, the coordinate space of `WindowContainer` is measured in meters, with its origin (0, 0, 0) located at the geometric center of the cube represented by the `WindowContainer`, allowing intuitive object placement and transformation in a 3D environment. In this coordinate space, the positive x-axis points to the right, the positive y-axis points upward, and the positive z-axis extends outward perpendicular to the screen. This is consistent with the standard right-handed Cartesian coordinate system commonly used in 3D graphics.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d552ac5399f4eae9622c77a389c1f4f~tplv-goo7wpa0wc-image.image)
### Coordinate space of Stage 
For the coordinate space of `Stage`, the origin (0, 0, 0) is located at the point where the vertical centerline of the HMD intersects the physical ground. Using the ground anchor as the reference, the positive y-axis points upward, the positive x-axis points to the right, and the positive z-axis points toward the user's face (that is, perpendicular to the scene and extending outward). This approach is consistent with the right-handed Cartesian coordinate system commonly used in 6DoF VR, and ensures that the virtual ground (`Y=0`) fully corresponds to the real ground, thereby maintaining spatial consistency when users move within the `Stage` area defined by them.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8dd1806f24c44598898155efd31ae1c9~tplv-goo7wpa0wc-image.image)
### Coordinate space of SpatialView
For `SpatialView`, the origin (0, 0, 0) is measured in meters and is located at the geometric center of its bounding box. The positive x-axis extends to the right, the positive y-axis points upward, and the positive z-axis points toward the observer (that is, perpendicular to the screen and outward), following the right-handed Cartesian coordinate system.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/69237b24a5a146739755ecb5f8494d48~tplv-goo7wpa0wc-image.image)
Note that each `Entity`'s `TransformComponent` always stores a local transform. When you use `content.addEntity(entity)` to add an `Entity` to `SpatialView`, the local transform of the `Entity` becomes its global transform in the coordinate space of `SpatialView`. Therefore, you can directly set the position, rotation, and scale of the `TransformComponent` to adjust the transform of the `Entity` relative to the origin (that is, the center of `SpatialView`).
However, once you set an `Entity` as the child node of another `Entity`, its `TransformComponent` becomes relative to the parent `Entity`, rather than to the origin. Therefore, modifying the `TransformComponent` of a child `Entity` changes its transform relative to the parent `Entity`, rather than relative to the center of the `SpatialView`. In this case, if you want to implement absolute positioning within the space of `SpatialView`, you need to configure transforms carefully and correctly.
### Coordinate space of Entity
The coordinate space associated with `Entity` typically uses the right-handed Cartesian coordinate system, with the origin (0, 0, 0) located at the "center." The positive x-axis points to the right, the positive y-axis points upward, and the positive z-axis points toward the observer. Distances are measured in meters.
### Coordinate space of View
`View` (such as Android View, Compose nodes, and others) uses a left-handed coordinate system with measurements in virtual pixels. The origin (0, 0) is located at the top-left corner, the positive x-axis extending to the right, and the positive y-axis increasing downward. Pixel is used as a reference to ensure consistent positioning across different screen densities.
In the local context (`ViewCoordinateSpace.Local`) of the coordinate space, the origin (0, 0, 0) is anchored to the top-left corner of the parent `View` or Compose node. Therefore, the position in the local space is relative to that parent element. In the global context (`ViewCoordinateSpace.Global`), the origin is located at the top-left corner of the back face of `WindowContainer`, enabling absolute positioning for the entire `WindowContainer`.
## Convert coordinate spaces
### Convert an entity to the coordinate space of another entity
The following code describes how to convert the transform of the current `Entity` to the coordinate space of the target `Entity`, and redefine the parent-child hierarchy. These two `Entity` can come from different spatial containers.
```Kotlin
private fun Entity.moveAcrossContainersTo(destination: Entity) {
    val positionConverted = convertPositionTo(Vector3.ZERO, destination)
    val rotationConverted = convertRotationTo(Quat.identity(), destination)
    val scaleConverted = convertScaleTo(Vector3.ONE, destination)

    setParent(destination)
}
```

### Convert between the coordinate space of view and entity
The following code demonstrates how to place an `Entity` in a plane's local coordinate space (a left-handed coordinate system with the origin at the top-left corner of `SpatialView`) at the position `DpOffset(66 dp, 50 dp)`. `content.localSpatialCoordinateSpace` is a spatial coordinate system with its origin at the center of `SpatialView`.
```Kotlin
val offset = with(LocalDensity.current) {
    Offset(66.dp.toPx(), 50.dp.toPx())
}
SpatialView(
    modifier = Modifier.size(200.dp, 100.dp).background(Color.Yellow),
    update = { content, _ ->
        if (sphereAdded) {
            return@SpatialView
        }
        sphereAdded = true
        val childPosition =
            content.convertPosition(
                Vector3(offset.x, offset.y, 0f),
                ViewCoordinateSpace.Local,
                content.localSpatialCoordinateSpace
            )
        content.addEntity(
            SphereEntity(0.02f).apply {
                components.set(TransformComponent().apply { position = childPosition })
            }
        )
    }
)
```

## Important notice

* As `Stage` and `WindowContainer` are stacked in the rendering hierarchy, the content inside `WindowContainer` is always rendered before `Stage`, which may sometimes obscure views. This is a known behavior and not a bug.
* In most cases, simply adjusting the positions of objects will not visually overlap the two corresponding models, because the orientation and size of `WindowContainer` also affect rotation and scaling. To align them correctly, you must transform both rotation and scaling at the same time, or use a dedicated helper function (such as `convertTransformTo`) to ensure that all transform components match.
* When testing rotation and scaling values, you may observe small errors caused by the limitations of floating-point precision. Single-precision floating-point numbers typically provide accuracy to about seven decimal places. If you use equality testing (`==`) directly, minor rounding differences (such as between 0.30000001 and 0.3) may cause the comparison to fail. It is recommended to use a very small threshold (epsilon) for comparison, for example, by checking whether `abs(a - b) < ε` to get a more reliable result.

## API reference
`ViewCoordinateSpace`, `SpatialCoordinateSpace`, `SpatialCoordinateSpaceConverter`, and `Entity` are interfaces and classes related to coordinate space conversion. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
