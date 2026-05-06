Before using WindowContainer, you need to configure its properties.
* When the application starts, it will first open the default spatial container (WindowContainer or Stage) that you have set. If the default spatial container is WindowContainer, refer directly to the section "Set properties for the default WindowContainer" to configure its properties. If the default container is Stage, refer to [Configure Stage properties](/set-properties-for-stages) for configuration.
* The ID of the WindowContainer is a required property; other properties are optional. If not set, the default values will be used.

## Configure default WindowContainer properties
### Configuration method
Configure the default WindowContainer properties in the `AndroidManifest.xml` file's `<meta-data>` section:
```XML
<meta-data
    android:name="pico.spatial.windowcontainer.id"
    android:value="yourCustomName" />
<meta-data
    android:name="pico.spatial.windowcontainer.resizetype"
    android:value="2" />
<meta-data
    android:name="pico.spatial.windowcontainer.style"
    android:value="2" />
<meta-data
    android:name="pico.spatial.windowcontainer.defaultsize"
    android:value="2000x1440x500" />
<meta-data android:name="pico.spatial.windowcontainer.worldscaletype" android:value="2" />
<meta-data
    android:name="pico.spatial.windowcontainer.materialbackground"
    android:value="0" />
```

The default WindowContainer does not support configuring its position when opened.

Set the default WindowContainer content in `mainApp`, that is, place the content to be loaded within the function body of `DefaultWindowContainer{}`. You need to place the content that should be displayed immediately after the application launches (such as the homepage, main scene, and more) in the default WindowContainer.
```Kotlin
fun SpatialAppScope.mainApp() { 
    DefaultWindowContainer { 
        MainPageContent() // The content of the default WindowContainer is a Composable function
    } 
    // ...
} 
 
 @Composable
 fun MainPageContent() {
     // ...
 }
```

### Properties & meta-data reference table
The default WindowContainer supports property configuration in the meta-data key-value format, as follows:
| **Key** | **Value** |
| --- | --- |
| pico.spatial.windowcontainer.id | Any unique string representing the name of the WindowContainer. |
| pico.spatial.windowcontainer.style | The form of the WindowContainer: <br>  <br> * `"0"` (default): Automatically set by the system. The current system default is `"1"`. <br> * `"1"`: Planar <br> * `"2"`: Volumetric |
| pico.spatial.windowcontainer.defaultsize | The default size of the WindowContainer, in the format `widthxheightxdepth`, with the unit defaulting to dp. The depth is optional and only applies to Volumetric windows. The depth of Planar windows is fixed at 640 dp. <br>  <br> * The default size of Planar windows is 1280x720x640 dp. <br> * The default size of Volumetric windows is 1280x1280x1280 dp. |
| pico.spatial.windowcontainer.resizetype | The final size of the WindowContainer is determined by two factors: its own `defaultSize` property, and the content size range defined by the Composable received by its `content` parameter through `Modifier.windowConstraints`. <br> The types of size changes for WindowContainer are as follows: <br>  <br> * `"0"` (default): Automatically set by the system. The current system default is `"1"`. <br> * `"1"`: Only the minimum size is constrained. The WindowContainer cannot be smaller than the minimum size of its content. <br> * `"2"`: Both maximum and minimum sizes are constrained. The WindowContainer cannot be smaller than the minimum size of its content, nor larger than the maximum size of its content. |
| pico.spatial.windowcontainer.resizerestriction | WindowContainer scaling modes: <br>  <br> * `“0”` (default): Flexible scaling <br> * `“1”`: Proportional scaling |
| pico.spatial.windowcontainer.worldscaletype <br>  | Whether the WindowContainer automatically scales according to the user's viewing distance: <br>  <br> * `“0”` (default): System auto setting; the current system default is `“1”`. <br> * `“1”`: The window automatically adjusts its size based on the distance to the user, visually maintaining a fixed size. <br> * `“2”`: The actual window size is fixed to the default setting and does not change with the distance to the user. |
| pico.spatial.windowcontainer.defaultsize.unit | The unit of the `defaultSize` property: <br>  <br> * `“dp”` (default): The size unit is dp <br> * `“meters”`: The size unit is meters |
| pico.spatial.windowcontainer.captionbar <br>  | Display/hide modes for the WindowContainer's title: <br>  <br> * `“0”` (default): Always visible <br> * `“1”`: If you do not click the title bar (that is, if you move the pointer away), it will automatically hide after 3 seconds. |
| pico.spatial.windowcontainer.materialbackground | Whether to enable the frosted glass effect for the WindowContainer's background panel: <br>  <br> * `“0”`: Disabled <br> * `“1”` (default): Enabled |
| pico.spatial.windowcontainer.volumealignment | Alignment modes for volumetric windows: <br>  <br> * `“0”` (default): Gravity mode; the side direction of the volumetric window aligns with the direction of gravity, and the bottom is parallel to the ground. <br> * `“1”`: Tilted mode; the volumetric window tilts toward the user, with the front facing the user. |
| pico.spatial.windowcontainer.volumebasepanel | Whether the base panel of the volumetric window is displayed/hidden: <br>  <br> * `“0”` (default): The base panel is displayed during interaction <br> * `“1”`: The base panel is never displayed |
## Configure non-default WindowContainer properties
If you use other non-default WindowContainers in your application, you can configure WindowContainer properties using any of the following methods and open them for presentation as needed. For information on how to open and close WindowContainer, refer to "[Open or close a WindowContainer](/open-or-close-window-containers)".

* Configure non-default WindowContainer properties in `mainApp` using DSL.
* Configure non-default WindowContainer properties in the `AndroidManifest.xml` file's `<meta-data>` section.

### Configuration method
For example, using DSL, if the application uses WindowContainer as the default spatial container and also uses another WindowContainer named "OtherWindow", with their respective contents being `HomeContent()` and `OtherWindowContent()`, you can configure them in `mainApp` as follows:
```Kotlin
fun SpatialAppScope.mainApp() { 
    // Set the content of the default WindowContainer
    DefaultWindowContainer { 
        MainPageContent() // Specific content
    } 
     
    // Set the property and content of another WindowContainer named "OtherWindow"
    WindowContainer(
        id = "OtherWindow",
        form = Form.Volumetric,
        defaultSize = WindowContainerSize(width = 1280.dp, height = 720.dp),
        resizeType = ContainerResizeType.ContentMinSize,
        enableMaterialBackground = false
    ) {
        OtherWindowContent() // Specific content
    }
} 
```

### 
### Property priority
Property configuration follows the priority order below, from highest to lowest:

1. Properties dynamically set or statically declared in DSL.
2. Properties configured in the `AndroidManifest.xml` file.

When the same property is set from different sources, the higher-priority configuration will override the lower-priority configuration. The system will merge properties from all sources.
```Kotlin
// Properties that take effect dynamically when opening the container are registered via propertiesDSL
WindowContainer(
    id = "Sample",
    form = Form.Volumetric,
    properties = {
        defaultSize = WindowContainerSize(width = 300.dp, height = 310.dp)
        resizeType = WindowContainerPropertiesManager.resizeType
    },
) {
    Sample()
}
// Register containers with static properties
WindowContainer(
    "Sample",
    form = Form.Planar,
    defaultSize = WindowContainerSize(width = 1600.dp, height = 1600.dp),
    enableMaterialBackground = false,
) {
    Sample()
}
```

If a property is not assigned a value, the following system default values will be used.
```Kotlin
size = Size.unspecified,
resizeType = ContainerResizeType.AUTOMATIC,
defaultResizeRestriction = ContainerResizeRestriction.NON_UNIFORM_RESIZABLE,
form = Form.PLANAR,
volumeAlignment = VolumeAlignment.GRAVITY,
defaultVolumeBasePanelType = VolumeBasePanelType.DEFAULT,
defaultCaptionBarType = CaptionBarType.DEFAULT,
worldScaleType = WorldScaleType.AUTOMATIC,
enableMaterialBackground = true,
beforeSettingPlacementConfiguration = { PlacementConfiguration.default },
activityClass = SpatialStubActivity::class.java,
```

### Detailed description of each property
The following section provides a detailed explanation of each property, using DSL as an example.
#### Type
You can configure the type of WindowContainer using the `from` property.
```Kotlin
WindowContainer(
    ...
    form = Form.Volumetric,
    ...
) {...}
```

#### Size
You can configure the size of WindowContainer, including length, width, depth, and length unit. Of these, depth configuration is only effective for Volumetric windows; the depth of Planar windows is fixed at 640 dp.

* **Configure size**
   The final size of WindowContainer is determined by two factors: its own `defaultSize` property, and the content size range defined by the Composable received through its `content` parameter via `Modifier.windowConstraints`.
   The default size of WindowContainer must be set using `defaultSize`. The following is a code example:
   ```Kotlin
   WindowContainer(
       id = "xxxx",
       // Specify the default size via defaultSize, the unit can be "meters" or "dp"
       // The `depth` parameter is only effective for Volumetric
       defaultSize = WindowContainerSize(width = 1.2f, height = 0.6f, depth = 0.1f, unit = LengthUnit.Meters),
       defaultSize = WindowContainerSize(width = 1500.dp, height = 750.dp, depth = 100.dp),
       form = Form.Planar,
   ) {
       ...
   }
   ```

   In addition, by using both the `resizeType` property of WindowContainer and the `content` parameter received by its Composable via `Modifier.windowConstraints`, you can further constrain the size of the WindowContainer.
   `Modifier.windowConstraints` not only imposes constraints on the container size, but also constrains the size of its child content, which is the same as the effect of `Modifier.sizeIn()`.

   Different `resizeType` values affect the strategy WindowContainer uses when responding to `Modifier.windowConstraints`. `ContentMinSize` considers only the minimum size constraint (min); `ContentSize` considers both the minimum size constraint (min) and the maximum size constraint (max).
   The following is a code example:
   ```Kotlin
   // The container size is limited to 500x700x300 dp
   WindowContainer(id = "ResizeSample", resizeType = ContainerResizeType.ContentSize) {
       Box(
           modifier = Modifier.windowConstraints(width = 500.dp, height = 700.dp, depth = 300.dp)
       ) {...}
   }
   ```

   **Rules for using `Modifier.windowConstraints` and `resizeType` together:**
   When you set `pico.spatial.windowcontainer.resizetype` in `AndroidManifest.xml` and also use `Modifier.windowConstraints` in the code, the `resizetype` value determines the behavior of `windowConstraints`:
   * When `resizetype` is set to `1`: The system only uses the minimum size set by `windowConstraints` and ignores the maximum size constraint.
   * When `resizetype` is set to `2`: Both the minimum and maximum size constraints set by `windowConstraints` will take effect.
   Therefore, for the maximum size constraint of `windowConstraints` to take effect, you must set the value of `pico.spatial.windowcontainer.resizetype` to `2`.
   ```Kotlin
   <meta-data
       android:name="pico.spatial.windowcontainer.resizetype"
       android:value="2" />
   ```

* **Control scaling ratio**
   By using the `ContainerResizeRestriction` parameter, you can control whether WindowContainer maintains a fixed ratio or allows free scaling during resizing. `UniformResizable` indicates fixed ratio; `NonUniformResizable` indicates free scaling.
   The following is a code example:
   ```Kotlin
   // During the resizing process, the WindowContainer size cannot be less than 500x500x500 dp
   WindowContainer(
       id = "ResizeSample",
       resizeType = ContainerResizeType.ContentMinSize,
       defaultResizeRestriction = ContainerResizeRestriction.UniformResizable // Depending on actual needs, you can replace UniformResizable with NonUniformResizable
   ) {
       Box(
           modifier =
               
               Modifier.windowConstraints(minWidth = 500.dp, minHeight = 500.dp, minDepth = 500.dp)
       ) {}
   }
   ```


#### Scaling

* **Proportional scaling**
   You can control whether WindowContainer performs proportional scaling by using its `defaultResizeRestriction` property.
   ```Kotlin
   WindowContainer(
       ...
       defaultResizeRestriction = DefaultResizeRestriction.UniformResizable, // Proportional scaling
       ...
   ) {...}
   ```

   Note that scaling for Volumetric windows is always proportional. Therefore, this property only applies to Planar windows, and by default, scaling is non-proportional.
* **Automatic scaling based on user viewing distance**
   You can control whether WindowContainer automatically scales based on the user's viewing distance by using its `worldScale` property.
   ```Kotlin
   WindowContainer(
       ...
       // The window automatically adjusts its size based on the distance to the user, visually maintaining a fixed size.
       worldScale = WorldScale.Dynamic, 
       ...
   ) {...}
   ```


#### Caption bar
You can control the display and hiding of the caption bar by using the `defaultCaptionBarType` property of WindowContainer.
```Kotlin
WindowContainer(
    ...
    defaultCaptionBarType = defaultCaptionBarType.Default, // Always visible
    ...
) {...}
```

In addition, each time WindowContainer is clicked, the caption bar will reappear.
#### Position when opened
You can control the position of WindowContainer relative to the anchor window (top, bottom, left, right) and the offset when opened by using the `placement` property, as shown in the figure below. The orientation of the new WindowContainer will follow the system definition and automatically rotate to an angle perpendicular to the user's head to ensure content is easy to view.
<div style="text-align: center"></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d88c2175a8fe403db248cbefad84bc3c~tplv-goo7wpa0wc-image.image" width="736px" /></div>


The following is a code example. In this example, if an anchor window exists, the new WindowContainer will appear to its right when opened, and whether the default offset is used is determined by `useSystemDefaultOffset`.
```Kotlin
WindowContainer(
    id = "xxxx",
    form = Form.Planar,
    placement = {
       // Select the anchor window based on conditions
       containers.firstOrNull { it.state.isFocused }?.let { 
           Placement.placement(
               it,
               Placement.Orientation.Right,
               if (useSystemDefaultOffset) Dp.Unspecified else offsetState.dp
           )
       } ?: Placement.none()
    }
)
```

#### Base panel
For Volumetric windows, you can decide whether to display the base panel by setting the `defaultVolumeBasePanelType` property.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e1d2327fea614794bd97297649c5bfd4~tplv-goo7wpa0wc-image.image" width="398px" /></div>

```Kotlin
WindowContainer(
    ...
    defaultVolumeBasePanelType = DefaultVolumeBasePanelType.Default, // The base panel is displayed during interaction
    ...
) {...}
```

#### Alignment mode
For Volumetric windows, you can set the `volumeAlignment` property to determine its alignment mode, thereby controlling the container's posture adjustment strategy during vertical drag operations by the end user.
There are two alignment modes: Gravity and Tilted.

* **Gravity**: Gravity mode. The Volumetric window is always perpendicular to the ground.
* **Tilted**: Tilted mode. The Volumetric window tilts toward the user.

| **Gravity** | **Tilted** |
| --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ff307280d73941609856ef9861dc4a29~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9856cb3445ec46b3ac35a19439d8177c~tplv-goo7wpa0wc-image.image) |
Code example:
```Kotlin
WindowContainer(
    ...
    volumeAlignment = VolumeAlignment.Gravity,
    ...
) {...}
```

#### Viewpoint transformation
The viewpoint describes the user's four horizontal directions relative to the Volumetric window and implicitly includes angle information. You can use the `listener` to obtain the user's direction relative to the current Volumetric window, enabling subsequent operations, such as dynamically adjusting the window's content or rotating a 3D model based on the user's direction.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/314a11cd18354edd83fd40a5fa1e2eb8~tplv-goo7wpa0wc-image.image" width="650px" /></div>

Code example:
```Kotlin
@Composable
fun VolumeViewPointSample() {
    // Get the current ViewPoint manager
    val viewpointManager = LocalVolumeViewPointManager.current
    // Bind the current ViewPoint using Compose state
    val currentViewPoint by viewpointManager.viewpoint
    // Lifecycle binding for listening to ViewPoint changes
    DisposableEffect(viewpointManager) {
        val listener =
            object : VolumeViewPointListener {
                override fun onViewpointChanged(viewpoint: ViewPoint) {
                    // Callback when the viewpoint changes; custom logic can be executed here (such as UI updates, notifications, and more)
                }
            }
        viewpointManager.addViewPointListener(listener)
        onDispose { viewpointManager.removeViewPointListener(listener) }
    }

    // SpatialView rendering container
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        initial = { content, attachments ->
            val entity =
                withContext(Dispatchers.IO) { Entity.loadFrom(Source.assets("alarm.usdz")) }
            entity.setName("alarm")
            content.addEntity(entity)
        },
        // Called on each frame or when data updates, used to dynamically update entity state
        update = { content, attachments ->
            content.entities
                .firstOrNull { it.enabled && it.getName() == "alarm" }
                ?.let {
                    // Obtain and update the TransformComponent to control the entity's rotation
                    it.components[TransformComponent::class.java]?.apply {
                        val eulerAngles = eulerAngles
                        this.setEulerAngles(
                            EulerAngles(
                                roll = eulerAngles.roll,
                                pitch = eulerAngles.pitch,
                                yaw = currentViewPoint.orientation.degree
                            )
                        )
                    }
                }
        }
    )
}
```

### Property & meta-data reference table
The properties that can be set for non-default WindowContainer are as follows:
| **property** | **value** |
| --- | --- |
| id | Any unique string representing the name of the WindowContainer. |
| form <br>   | The form of the WindowContainer: <br>  <br> * `Form.Automatic` (default): Set automatically by the system; the current system default is Form.Planar <br> * `Form.Planar` <br> * `Form.Volumetric` |
| resizeType <br>   | The final size of the WindowContainer is determined by two factors: its own `defaultSize` property, and the content size range defined by the Composable received via the `content` parameter using `Modifier.windowConstraints`. <br> The types of WindowContainer size changes are as follows: <br>  <br> * `ContainerResizeType.Automatic` (default): Set automatically by the system; the current system default is `ContainerResizeType.ContentMinSize`. <br> * `ContainerResizeType.ContentMinSize`: Only the minimum size is constrained. The WindowContainer cannot be smaller than the minimum size of its content. <br> * `ContainerResizeType.ContentSize`: Both maximum and minimum sizes are constrained. The WindowContainer cannot be smaller than the minimum size of its content, nor larger than the maximum size of its content. |
| defaultResizeRestriction | The scaling method of the WindowContainer: <br>  <br> * `ContainerResizeRestriction.NonUniformResizable` (default): Flexible scaling <br> * `ContainerResizeRestriction.UniformResizable`: Uniform scaling |
| worldScale | Whether the WindowContainer automatically scales with the user's viewing distance: <br>  <br> * `WorldScale.Automatic` (default): Set automatically by the system; the current system default is `WorldScale.Dynamic`. <br> * `WorldScale.Dynamic`: The window automatically adjusts its size based on the distance to the user, visually maintaining a fixed size. <br> * `WorldScale.Fixed`: The actual size of the window is fixed to the default setting and does not change with the distance to the user. |
| defaultSize | The default size of the WindowContainer, in the format `WindowContainerSize(width, height, depth)`. The unit of size is determined by the data type passed in: <br>  <br> * If the data type is Dp, the unit is dp; <br> * If the data type is Float, the unit is meters. <br>  <br> Note that `depth` is an optional parameter and is only valid for `Form.Volumetric` windows. The `depth` of planar windows is fixed at 640 dp. <br> The default values for window size are as follows: <br>  <br> * The default size of `Form.Planar` windows is 1280x720x640 dp. <br> * The default size of `Form.Volumetric` windows is 1280x1280x1280 dp. |
| defaultCaptionBarType | The display/hide method for the WindowContainer's caption bar: <br>  <br> * `CaptionBarType.Default` (default): Always displayed <br> * `CaptionBarType.AutomaticHide`: If the caption bar is not clicked (that is, the pointer is moved away), it will automatically hide after 3 seconds |
| enableMaterialBackground | Whether to enable the frosted glass effect for the WindowContainer's background panel: <br>  <br> * `true` (default): Enabled <br> * `false`: Disabled |
| volumeAlignment <br>  | The alignment mode of volumetric windows: <br>  <br> * `VolumeAlignment.Gravity` (default): Gravity mode. The side direction of the Volumetric window aligns with the direction of gravity, and the base is parallel to the ground. <br> * `VolumeAlignment.Tilted`: Tilted mode. The Volumetric window tilts toward the user, with the front facing the user directly. <br>  <br> ***Note***: This property applies only to the Volumetric window. |
| defaultVolumeBasePanelType <br>  | Whether the base panel of the Volumetric window is shown or hidden: <br>  <br> * `VolumeBasePanelType.Default` (default): The base panel is shown during interaction. <br> * `VolumeBasePanelType.None`: The base panel is never shown. <br>  <br> ***Note***: This property applies only to the Volumetric window. |
### 
