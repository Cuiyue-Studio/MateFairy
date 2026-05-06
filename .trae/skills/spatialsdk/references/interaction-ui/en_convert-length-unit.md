In Android development, **dp** is the standard unit of length for building adaptive user interfaces, while **px** is required when interacting with hardware or performing precise layout measurements. Therefore, converting between dp and px is a fundamental operation in Android app development. With the development of spatial apps, **meter** has also been introduced to represent dimensions aligned with the physical world. This makes the management of units if length more complex.
This article explains the concepts and characteristics of dp, px, and meter, and provides methods for converting between them.
## About px, dp and meter
px, dp, and meter are described and compared as follows:
| **Length unit** | **Definition** | **Core concept** | **Relationship with the physical world** | **Applications in Android** | **Limitations** |
| --- | --- | --- | --- | --- | --- |
| px | A pixel (px) is a physical screen dot, which is the smallest physical display unit on a screen, and the basic unit that makes up a digital image. | px is a direct mapping to display hardware, representing the smallest controllable light-emitting unit on the screen. | The physical size of a single px depends on screen resolution and cannot be used as a universal standard for physical dimensions. | Underlying drawing, image rendering, obtaining the actual rendering size (such as `onSizeChanged`) | Dependence on specific devices. If you directly use px to define UI layout, it can result in significant visual size inconsistencies across devices with different screen densities. |
| dp | Density-independent pixel (dp) is a virtual unit based on the physical density of the screen. It is the best and only choice for building adaptive 2D interfaces. | Establish a unified "virtual ruler" for 2D interfaces to ensure consistent visual dimensions of UI elements across screens with different densities. | On a 160 dpi screen, `1 dp = 1 px`. Based on this baseline, the Android system uses the following formula to convert between dp and px: `px = dp * (device dpi / 160)`. | The standard unit for UI layout in Jetpack Compose. All size, margin, and padding-related  parameters are declared in dp (for example, `Modifier.size(100.dp)`). | In 3D or spatial computing scenarios that require precise 1:1 mapping to the physical world, dp loses its "density-independent" characteristic. In these scenarios, if multiple planes remain at the same distance from the camera, dp can still be used as a virtual unit in 2D. |
| meter | The meter is the standard unit of physical length defined by the International System of Units (SI). | Aimed at achieving absolute physical realism, enabling virtual objects to visually align with real-world objects at a 1:1 scale. | Directly represents the length in the real world (`1` = 1 meter). | In spatial apps, all 3D coordinates, object dimensions, and distances must use meter as the unit (for example, `setPosition(Vector3(0f, 0.5f, 0f))`). | Meter does not address core topics of 2D UI such as screen density and visual perception, and therefore is not suitable at all for layout in 2D screen interfaces. |
## Convert between units of length
During development, the choice of units of length depends on the domain of the current work. Each domain has its official units, so it is necessary to use the correct units in the appropriate domain.

* **2D interface design**
   This domain focuses on visual consistency. The official unit is dp, which is used in interface layout and the dimensions of controls to ensure consistent display across devices.
* **Hardware interaction**
   This domain focuses on **** direct interaction with the physical screen. The official unit is px, which is used for drawing, event coordinates, retrieving dimensions after rendering, and more.
* **Physical space**
   This domain focuses on alignment with the real world. The official unit is meter, which is used for coordinates, distances, and dimensions in spatial apps to ensure one-to-one alignment with the real world.

When transferring from one domain to another, unit conversion is required. In conjunction with Jetpack Compose, the PICO Spatial SDK provides relevant interfaces.
### dp ↔ px
Use the `Density` class provided by Jetpack Compose to convert between dp and px.
```Kotlin
@Composable
fun Sample() {    
    val density = LocalDensity.current
    
    // ...

    with(density) {
        // Convert from dp to px
        pointInPx = pointInDp.toPx()
        // Convert from px to dp
        pointInDp = pointInPx.toDp()
    }
}
```

### dp ↔ meter
Use the `PhysicalLengthConvert` class provided by the PICO Spatial SDK to convert between dp and meter.
```Kotlin
@Composable
fun Sample() {    
    val physicalLengthConverter = LocalPhysicalLengthConverter.current
    
    // ...

    // Convert from dp to meter
    pointInMeter = physicalLengthConverter.dpToLength(pointInDp, LengthUnit.Meters)
    // Convert from meter to dp
    pointInDp = physicalLengthConverter.lengthToDp(pointInMeter, LengthUnit.Meters)
}
```

### meter ↔ px
Use the `PhysicalLengthConvert` class provided by the PICO Spatial SDK to convert between meters and px.
```Kotlin
@Composable
fun Sample() {
    val density = LocalDensity.current
    val physicalLengthConverter = LocalPhysicalLengthConverter.current

    // ...
    
    with(density) {
        // Convert from meter to px
        pointInPx = physicalLengthConverter
            .lengthToDp(pointInMeter, LengthUnit.Meters)
            .toPx()
        // Convert from px to meter
        pointInMeter = physicalLengthConverter
            .dpToLength(pointInPx.toDp(), LengthUnit.Meters)
    }
}
```

## Tutorial
This section uses a simple example to illustrate how to convert between dp, px, and meters in a practical scenario.
There is a 2D window in the scene, which contains a button. Clicking the button loads a cube. Afterwards, you can freely drag the cube within this window to change its position. Below are the requirements:

* **2D window size**: 0.4 m x 0.7 m
* **Cube size**: Half the length of the shorter side of the window
* **Interactive operation**: Users can move the cube within the window by dragging.

<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ae0cba80120a4b37a8ed1b402e28afb5~tplv-goo7wpa0wc-image.image></video>
### Step 1: Use dp to design and implement a 2D interface
During the entire initial phase of the app, it is necessary to design a 2D interface. All measurements in the interface, such as window size, page margins, and spacing between components, uniformly use dp as the unit of length.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/21cd1ba3b38f4722ab8919283f438aa5~tplv-goo7wpa0wc-image.image" width="350px" /></div>

Then, use Jetpack Compose to implement the interface you design, and continue to use dp as the unit of length
```Kotlin
// Create a WindowContainer, unit is dp
WindowContainer(id = "sample",
    size = ContainerSize(defaultWidth = 540.dp, defaultHeight = 960.dp),
    form = Form.IN_VOLUME,
    enableMaterialBackground = true,
) {
    // Implementation of other content ...
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // The view component for hosting 3D models
        SpatialView(
        ) { content, _ ->
        }

        Spacer(Modifier.height(16.dp))

        // The button control for triggering 3D model loading
        TextButton(
            text = "Load 3D Model",
            modifier =  Modifier.fillMaxWidth(),
            textStyle = PicoTheme.typography.labelLarge
        ) {
            // Click event: trigger model loading
        }
    }
}
```

### Step 2: Convert px to meter and create a cube entity
The size of the 3D cube model should be set based on the shorter edge of the window, with its dimension being half of that length. Therefore, you first need to obtain the size of the component (`SpatialView`) that hosts the cube model, and then create the cube model according to the length of its shorter edge. The steps are as follows:

1. Retrieve the component's px size through the `onSizeChanged` callback function of `SpatialView`.
2. Calculate the dimensions of the cube based on this px size.
3. Use the `toMeter()` function to convert px to meter and retrieve the actual size of the cube.
4. Create a cube entity using meter.

```Kotlin
val density = LocalDensity.current
val physicalLengthConverter = LocalPhysicalLengthConverter.current

var entity by remember { mutableStateOf<Entity?>(null) }

// Dimensions of the cube entity, in meters
var sizeOfEntity by remember { mutableStateOf(Vector3.ZERO) }

LaunchedEffect(load) {
    entity = createBoxEntity(sizeOfEntity)
}

Column(
    Modifier
        .fillMaxSize()
        .padding(24.dp)
) {
    key(entity) {
        SpatialView(
            modifier = Modifier
                // Use the onSizeChanged callback to retrieve the size of the component
                .onSizeChanged { intSize ->
                    // The unit of intSize is px; it needs to be converted to meter
                    val sideLengthInMeter =
                        min(intSize.width, intSize.height)
                            .toFloat()
                            .toMeter(density, physicalLengthConverter) * 0.5f
                   
                    // Assign meter to the entity's size variable
                    sizeOfEntity = Vector3(sideLengthInMeter)
                }
        ) { content, _ ->
            // Add the entity to SpatialView for rendering
            entity?.let {
                content.addEntity(it)
            }
        }
    }
    
    // Other parts of the interface ...
}
```

### Step 3: Implement drag operation for the cube entity
The offset generated by the drag event is measured in px and must be converted to meter again in order to calculate the distance the cube entity needs to move. Then, update its position in 3D space.
```Kotlin
detectDragGestures(
    context = context,
    targetedToEntity = entity?.let { TargetEntity.hit(it) },
) { _, dragAmount ->
    // The dragAmount of the drag event is measured in px; convert it to a variable measured in meters
    val dragOffset = IntOffset(x = dragAmount.x.toInt(), y = dragAmount.y.toInt())
        .toOffset().toMeter(density, physicalLengthConverter)
        
    // Update the TransformComponent of the cube entity and apply the drag offset to the entity's position in physical space
    entity?.apply {
        components[TransformComponent::class.java]?.apply {
            val currX = position.x
            val currY = position.y
            val currZ = position.z
            setPosition(
                Vector3(
                    currX + dragOffset.x,
                    currY - dragOffset.y,
                    currZ
                )
            )
        }
    }
}
```

## API reference
The `PhysicalLengthConverter` interface provides functions for length unit conversion. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
