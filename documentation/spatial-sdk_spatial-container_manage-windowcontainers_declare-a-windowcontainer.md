Before using WindowContainer, you need to declare it and configure its properties.
The declaration methods for the default WindowContainer and the non-default WindowContainer are different:

* The default WindowContainer is declared through `AndroidManifest.xml`.
* The non-default WindowContainer must be declared in the DSL of `mainApp`, and can also be declared in `AndroidManifest.xml` as needed.

## Declare the default WindowContainer
You need to specify a default spatial container for the application. When the application starts, the default spatial container will be opened first to display the application's initial interface.
* You can declare only one default spatial container for an application.
* To set a Stage as the default spatial container, see [Declare a Stage](/register-stages) for details.

Follow these steps to declare a WindowContainer as the default spatial container.

1. **Declare the default WindowContainer in mainApp**
   Within the `mainApp` `SpatialAppScope` scope, call the `DefaultWindowContainer` function to define the content of the default window.
   ```Kotlin
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultWindowContainer {
           MainPageContent() // Content of the default WindowContainer, which is a Composable function
       }
    }
    
    @Composable
    fun MainPageContent() {
        // ...
    }
   ```

2. **Configure properties in AndroidManifest.xml**
   In the `AndroidManifest.xml` file, configure properties for the default WindowContainer using the `<meta-data>` tag. These properties define the basic behavior and appearance of WindowContainer.
   The default WindowContainer does not support configuring its position when opened.

   ```XML
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:tools="http://schemas.android.com/tools">
   
       <application
           android:name=".platform.SpatialApplication"
           android:dataExtractionRules="@xml/data_extraction_rules"
           android:fullBackupContent="@xml/backup_rules"
           android:icon="@mipmap/ic_launcher"
           android:label="@string/app_name"
           android:roundIcon="@mipmap/ic_launcher_round"
           android:supportsRtl="true"
           android:theme="@style/Theme.SpatialApp"
           tools:targetApi="31">
   
           <activity
               android:name=".platform.LaunchActivity"
               android:exported="true"
               android:theme="@style/Theme.SpatialApp">
               <intent-filter>
                   <action android:name="android.intent.action.MAIN" />
                   <category android:name="android.intent.category.LAUNCHER" />
               </intent-filter>
               <!-- Required: Unique ID of the WindowContainer -->
               <meta-data
                   android:name="pico.spatial.windowcontainer.id"
                   android:value="your_window_container_name" />
               <!-- Optional: Set the form (Planar/Volumetric) -->
               <meta-data
                   android:name="pico.spatial.windowcontainer.style"
                   android:value="1" />
               <!-- Optional: Set the default size -->
               <meta-data android:name="pico.spatial.windowcontainer.defaultsize" android:value="1280x720" />
                <!-- Optional: Enable frosted glass background effect -->
               <meta-data
                   android:name="pico.spatial.windowcontainer.materialbackground"
                   android:value="1" />
               <!-- Other meta-data configurations... -->
   
           </activity>
       </application>
   
   </manifest>
   ```


### Property list
You can configure the following properties for the default WindowContainer:
| **Property** | **Description** |
| --- | --- |
| pico.spatial.windowcontainer.id | Any unique string representing the name of the WindowContainer. |
| pico.spatial.windowcontainer.style | The form of the WindowContainer: <br>  <br> * `"0"` (default): System automatically sets it; the current system default is `"1"` <br> * `"1"`：Planar <br> * `"2"`：Volumetric |
| pico.spatial.windowcontainer.defaultsize | The default size of the WindowContainer, in the format `widthxheightxdepth`, with the unit defaulting to dp. Here, `depth` is optional and only applies to Volumetric windows. The depth of a Planar window is fixed at 640 dp. <br>  <br> * The default size of a Planar window is 1280x720x640 dp. <br> * The default size of a Volumetric window is 1280x1280x1280 dp. |
| pico.spatial.windowcontainer.defaultsize.unit | The unit of the `defaultSize` property: <br>  <br> * `"dp"` (default): The size unit is dp <br> * `"meters"`: The size unit is meters |
| pico.spatial.windowcontainer.resizetype | The final size of the WindowContainer is determined by two factors: its own `defaultSize` property and the content size range defined by the Composable received via the `content` parameter using `Modifier.windowConstraints`. <br> The types of WindowContainer size changes are as follows: <br>  <br> * `"0"` (default): System automatically sets it; the current system default is `"1"`. <br> * `"1"`: Only the minimum size is constrained. The WindowContainer cannot be smaller than the minimum size of its content. <br> * `"2"`: Both the maximum and minimum sizes are constrained. The WindowContainer cannot be smaller than the minimum size of its content, nor larger than the maximum size of its content. |
| pico.spatial.windowcontainer.resizerestriction | The scaling mode of the WindowContainer: <br>  <br> * `"0"` (default): Flexible scaling <br> * `"1"`: Proportional scaling |
| pico.spatial.windowcontainer.worldscaletype <br>  | Whether the WindowContainer automatically scales based on the user's viewing distance: <br>  <br> * `"0"` (default): System automatically sets it; the current system default is `"1"`. <br> * `"1"`: The window automatically adjusts its size based on the user's distance, maintaining a visually fixed size. <br> * `"2"`: The actual window size is fixed to the default setting and does not change with the user's distance. |
| pico.spatial.windowcontainer.captionbar <br>  | The display/hide mode of the WindowContainer's title: <br>  <br> * `"0"` (default): Always displayed <br> * `"1"`: If the title bar is not clicked (that is, if the pointer is moved away), it automatically hides after 3 seconds |
| pico.spatial.windowcontainer.materialbackground | Whether to enable the frosted glass effect for the WindowContainer's background panel: <br>  <br> * `"0"`: Disabled <br> * `"1"` (default): Enabled |
| pico.spatial.windowcontainer.volumealignment | The alignment mode of Volumetric windows: <br>  <br> * `"0"` (default): Gravity mode, the side direction of the Volumetric window aligns with the gravity direction, and the bottom is parallel to the ground. <br> * `"1"`: Tilt mode, the Volumetric window tilts toward the user, with the front facing the user. |
| pico.spatial.windowcontainer.volumebasepanel | Whether the base panel of the Volumetric window is shown or hidden: <br>  <br> * `"0"` (default): The base panel is shown during interaction <br> * `"1"`: The base panel is never shown |
## Declare the non-default WindowContainer
You can use the following methods to declare the non-default WindowContainer and configure its properties.

* Declare the non-default WindowContainer and configure its properties in the DSL of `mainApp`.
* Declare the non-default WindowContainer and configure its properties in the `AndroidManifest.xml` file.

* If you declare a non-default WindowContainer in the `AndroidManifest.xml` file, you must also declare it in the DSL using the same container ID. Otherwise, this WindowContainer will not be able to load any Composable content.
* When you set different values for the same property through different methods (for example, DSL and `AndroidManifest.xml`), the system determines which value takes effect based on a predefined priority order. For details, refer to "[Property priority](/sdk/register-window-containers)".

### Declare the non-default WindowContainer using static DSL and AndroidManifest.xml
The following sample code declares the non-default WindowContainer using both static DSL and the `AndroidManifest.xml` file. In both static DSL and `AndroidManifest.xml`, the ID of WindowContainer is `WindowContainerDSLStaticProp`.
Static DSL refers to passing WindowContainer properties directly as named parameters to the `WindowContainer()` function.

```Kotlin
WindowContainer(
    "WindowContainerDSLStaticProp",
    form = Form.Volumetric,
    resizeType = ContainerResizeType.ContentMinSize,
    defaultSize = WindowContainerSize(width = 800.dp, height = 600.dp),
    defaultResizeRestriction = ContainerResizeRestriction.NonUniformResizable,
) {
    SampleBase("WindowContainerDSLStaticProp") { WindowContainerDSLStaticProp() }
}
```

```XML
<activity
    android:name=".containers.StaticDSLWindowContainerActivity"
    android:configChanges="screenLayout|screenSize|smallestScreenSize|orientation"
    android:exported="true">

    <!-- WindowContainer's name -->
    <meta-data
        android:name="pico.spatial.windowcontainer.id"
        android:value="WindowContainerDSLStaticProp" />
    <meta-data
        android:name="pico.spatial.windowcontainer.resizetype"
        android:value="2" />
    <!-- WindowContainer's style -->
    <meta-data
        android:name="pico.spatial.windowcontainer.style"
        android:value="1" />
    <!-- Default size of the WindowContainer-->
    <meta-data
        android:name="pico.spatial.windowcontainer.defaultsize"
        android:value="500x500" />
    <!-- WindowContainer's resize restriction -->
    <meta-data
        android:name="pico.spatial.windowcontainer.resizerestriction"
        android:value="1" />
    <!-- WindowContainer's volume alignment -->
    <meta-data
        android:name="pico.spatial.windowcontainer.volumealignment"
        android:value="0" />
    <!-- WindowContainer's volume base panel -->
    <meta-data
        android:name="pico.spatial.windowcontainer.volumebasepanel"
        android:value="1" />
    <!-- WindowContainer's caption bar -->
    <meta-data
        android:name="pico.spatial.windowcontainer.captionbar"
        android:value="1" />

    <meta-data
        android:name="pico.spatial.windowcontainer.materialbackground"
        android:value="1" />

</activity>
```

### Declare the non-default WindowContainer using dynamic DSL and AndroidManifest.xml
The following sample code declares the non-default WindowContainer using both dynamic DSL and `AndroidManifest.xml`. In both dynamic DSL and `AndroidManifest.xml`, the ID of WindowContainer is `WindowContainerDSLDynamicProp`.
Dynamic DSL refers to assigning and managing WindowContainer properties collectively through a dedicated `properties = { ... }` lambda block.

```Kotlin
WindowContainer(
    "WindowContainerDSLDynamicProp",
    form = Form.Volumetric,
    properties = {
        defaultSize = WindowContainerSize(width = 300.dp, height = 310.dp)
        resizeType = ContainerResizeType.ContentMinSize 
        volumeAlignment = VolumeAlignment.Tilted 
        defaultResizeRestriction = ContainerResizeRestriction.UniformResizable 
        enableMaterialBackground = true
        targetActivity = StaticDSLWindowContainerActivity::class.java
    },
) {
    SampleBase("WindowContainerDSLDynamicProp") { WindowContainerDSLDynamicProp() }
}
```

```XML
<activity
    android:name=".containers.DynamicDSLWindowContainerActivity"
    android:configChanges="screenLayout|screenSize|smallestScreenSize|orientation"
    android:exported="true">

    <!-- WindowContainer's name -->
    <meta-data
        android:name="pico.spatial.windowcontainer.id"
        android:value="WindowContainerDSLDynamicProp" />
    <meta-data
        android:name="pico.spatial.windowcontainer.resizetype"
        android:value="2" />
    <!-- WindowContainer's style -->
    <meta-data
        android:name="pico.spatial.windowcontainer.style"
        android:value="1" />
    <!-- Default size of the WindowContainer-->
    <meta-data
        android:name="pico.spatial.windowcontainer.defaultsize"
        android:value="500x500" />
    <!-- WindowContainer's resize restriction -->
    <meta-data
        android:name="pico.spatial.windowcontainer.resizerestriction"
        android:value="1" />
    <!-- WindowContainer's volume alignment -->
    <meta-data
        android:name="pico.spatial.windowcontainer.volumealignment"
        android:value="0" />
    <!-- WindowContainer's volume base panel -->
    <meta-data
        android:name="pico.spatial.windowcontainer.volumebasepanel"
        android:value="1" />

    <meta-data
        android:name="pico.spatial.windowcontainer.materialbackground"
        android:value="0" />

</activity>
```

### Property list
You can configure the following properties for the non-default WindowContainer:
| **DSL property** | **AndroidManifest.xml property** | **Description** |
| --- | --- | --- |
| id | pico.spatial.windowcontainer.id | An arbitrary unique string representing the name of the WindowContainer. |
| form <br>   | pico.spatial.windowcontainer.style | The form of the WindowContainer: <br>  <br> * `Form.Automatic` (default): Set automatically by the system; the current system default is Form.Planar <br> * `Form.Planar` <br> * `Form.Volumetric` |
| resizeType <br>   | pico.spatial.windowcontainer.resizetype | The final size of the WindowContainer is determined by two factors: its own `defaultSize` property, and the content size range defined by the Composable received via the `content` parameter using `Modifier.windowConstraints`. <br> The types of WindowContainer size changes are as follows: <br>  <br> * `ContainerResizeType.Automatic` (default): Set automatically by the system; the current system default is `ContainerResizeType.ContentMinSize`. <br> * `ContainerResizeType.ContentMinSize`: Only the minimum size is constrained. The WindowContainer cannot be smaller than the minimum size of its content. <br> * `ContainerResizeType.ContentSize`: Both the maximum and minimum sizes are constrained. The WindowContainer cannot be smaller than the minimum size of its content, nor larger than the maximum size of its content. |
| defaultResizeRestriction | pico.spatial.windowcontainer.resizerestriction | The scaling method of the WindowContainer: <br>  <br> * `ContainerResizeRestriction.NonUniformResizable` (default): Flexible scaling <br> * `ContainerResizeRestriction.UniformResizable`: Uniform scaling |
| worldScale | pico.spatial.windowcontainer.worldscaletype | Whether the WindowContainer automatically scales based on the user's viewing distance: <br>  <br> * `WorldScale.Automatic` (default): Set automatically by the system; the current system default is `WorldScale.Dynamic`. <br> * `WorldScale.Dynamic`: The window automatically adjusts its size based on the user's distance, visually maintaining a fixed size. <br> * `WorldScale.Fixed`: The actual size of the window is fixed to the default setting and does not change with the user's distance. |
| defaultSize | pico.spatial.windowcontainer.defaultsize | The default size of the WindowContainer, in the format `WindowContainerSize(width, height, depth)`. The unit of size is determined by the data type passed in: <br>  <br> * If the data type is Dp, the unit is dp; <br> * If the data type is Float, the unit is meters. <br>  <br> Note that `depth` is an optional parameter and is only valid for `Form.Volumetric` windows. The `depth` of a Planar window is fixed at 640 dp. <br> The default window size values are as follows: <br>  <br> * The default size of a `Form.Planar` window is 1280x720x640 dp. <br> * The default size of a `Form.Volumetric` window is 1280x1280x1280 dp. |
| defaultCaptionBarType | pico.spatial.windowcontainer.captionbar | Display/hide modes for the WindowContainer title bar: <br>  <br> * `CaptionBarType.Default` (default): Always visible <br> * `CaptionBarType.AutomaticHide`: If the title bar is not clicked (that is, the pointer is moved away), it will automatically hide after 3 seconds |
| enableMaterialBackground | pico.spatial.windowcontainer.materialbackground | Whether to enable the frosted glass effect for the WindowContainer background panel: <br>  <br> * `true` (default): Enabled <br> * `false`: Disabled |
| volumeAlignment <br>  | pico.spatial.windowcontainer.volumealignment | Alignment modes for Volumetric windows: <br>  <br> * `VolumeAlignment.Gravity` (default): Gravity mode, the side of the Volumetric window aligns with the gravity direction, and the bottom is parallel to the ground. <br> * `VolumeAlignment.Tilted`: Tilted mode, the Volumetric window tilts toward the user, with the front facing the user. <br>  <br> This property only applies to Volumetric windows. <br>  |
| defaultVolumeBasePanelType <br>  | pico.spatial.windowcontainer.volumebasepanel | Whether to show/hide the base panel of the Volumetric window: <br>  <br> * `VolumeBasePanelType.Default` (default): Show the base panel during interaction <br> * `VolumeBasePanelType.None`: The base panel is never shown <br>  <br> This property only applies to Volumetric windows. <br>  |
## WindowContainer property configuration instructions
### Property priority
Property configuration follows the following priority order, from highest to lowest:

1. Properties declared using dynamic DSL or static DSL.
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

### Property configuration example
The following will use DSL as an example to provide a detailed explanation of each property.
#### Form
You can use the `from` property to set the form of the WindowContainer.
```Kotlin
WindowContainer(
    ...
    form = Form.Volumetric,
    ...
) {...}
```

#### Size
You can set the size of the WindowContainer, including length, width, depth, and the unit of length. The depth setting is only effective for Volumetric windows; the depth of Planar windows is fixed at 640 dp.

* **Set size**
   The final size of the WindowContainer is determined by two parts: its own `defaultSize` property, and the content size range defined by the Composable received by its `content` parameter through `Modifier.windowConstraints`.
   The default size of the WindowContainer must be set via `defaultSize`. The code example is as follows:
   ```Kotlin
   WindowContainer(
       id = "xxxx",
       // Specify the defaultSize to set the default size. The unit can be "meters" or "dp"
       // The `depth` parameter is only valid for Volumetric
       defaultSize = WindowContainerSize(width = 1.2f, height = 0.6f, depth = 0.1f, unit = LengthUnit.Meters),
       defaultSize = WindowContainerSize(width = 1500.dp, height = 750.dp, depth = 100.dp),
       form = Form.Planar,
   ) {
       ...
   }
   ```

   In addition, by using the WindowContainer's `resizeType` property together with the `content` parameter's Composable and its `Modifier.windowConstraints`, you can further constrain the size of the WindowContainer.
   `Modifier.windowConstraints` not only constrains the container size, but also constrains the size of its child content, which is the same as the effect of `Modifier.sizeIn()`.

   Different `resizeType` values affect the strategy the WindowContainer adopts when responding to `Modifier.windowConstraints`. `ContentMinSize` considers only the minimum size constraint (min); `ContentSize` considers both the minimum size constraint (min) and the maximum size constraint (max).
   The code example is as follows:
   ```Kotlin
   // The container size is limited to 500x700x300 dp
   WindowContainer(id = "ResizeSample", resizeType = ContainerResizeType.ContentSize) {
       Box(
           modifier = Modifier.windowConstraints(width = 500.dp, height = 700.dp, depth = 300.dp)
       ) {...}
   }
   ```

   **Collaboration rules between `Modifier.windowConstraints` and `resizeType`:**
   When you set `pico.spatial.windowcontainer.resizetype` in `AndroidManifest.xml` and use `Modifier.windowConstraints` in code at the same time, the value of `resizetype` determines the behavior of `windowConstraints`:
   * When the value of `resizetype` is `1`: the system only adopts the minimum size set by `windowConstraints`, and the maximum size limit will be ignored.
   * When the value of `resizetype` is `2`: both the minimum and maximum size limits set by `windowConstraints` will take effect.
   Therefore, to enable the maximum size limit of `windowConstraints`, you must set the value of `pico.spatial.windowcontainer.resizetype` to `2`.
   ```Kotlin
   <meta-data
       android:name="pico.spatial.windowcontainer.resizetype"
       android:value="2" />
   ```

* **Control scaling ratio**
   By using the `ContainerResizeRestriction` parameter, you can control whether the WindowContainer maintains a fixed ratio or allows free scaling during resizing. `UniformResizable` indicates a fixed ratio; `NonUniformResizable` indicates free scaling.
   The code example is as follows:
   ```Kotlin
   // During the resizing process, the size of the WindowContainer cannot be less than 500x500x500 dp
   WindowContainer(
       id = "ResizeSample",
       resizeType = ContainerResizeType.ContentMinSize,
       defaultResizeRestriction = ContainerResizeRestriction.UniformResizable // Depending on actual requirements, you can replace UniformResizable with NonUniformResizable
   ) {
       Box(
           modifier =
               
               Modifier.windowConstraints(minWidth = 500.dp, minHeight = 500.dp, minDepth = 500.dp)
       ) {}
   }
   ```


#### Scaling

* **Proportional scaling**
   You can use the WindowContainer's `defaultResizeRestriction` property to control whether it performs proportional scaling.
   ```Kotlin
   WindowContainer(
       ...
       defaultResizeRestriction = DefaultResizeRestriction.UniformResizable, // Uniform scaling
       ...
   ) {...}
   ```

   Note that scaling for Volumetric windows is always proportional. Therefore, this property only applies to Planar windows, and by default, proportional scaling is disabled.
* **Automatic scaling based on the user's viewing distance**
   You can use the WindowContainer's `worldScale` property to control whether it automatically scales based on the user's viewing distance.
   ```Kotlin
   WindowContainer(
       ...
       // The window automatically adjusts its size based on the user's distance, maintaining a fixed visual size.
       worldScale = WorldScale.Dynamic, 
       ...
   ) {...}
   ```


#### Title bar
You can control the show and hide of the caption bar of WindowContainer via the `defaultCaptionBarType` property.
```Kotlin
WindowContainer(
    ...
    defaultCaptionBarType = defaultCaptionBarType.Default, // Always visible
    ...
) {...}
```

In addition, the caption bar will reappear each time WindowContainer is clicked.
#### Position when opened
You can use the `placement` property to control the position of WindowContainer relative to the anchor window (top, bottom, left, right) and its offset when opened, as shown in the figure below. The orientation of the new WindowContainer will follow the system definition and automatically rotate to an angle perpendicular to the user's head to ensure the content is easy to view.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d88c2175a8fe403db248cbefad84bc3c~tplv-goo7wpa0wc-image.image" width="690px" /></div>

The following is a code example. In this example, if an anchor window exists, the new WindowContainer will appear to its right when opened, and whether to use the default offset is determined by `useSystemDefaultOffset`.
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
For Volumetric windows, you can determine whether to display the base panel by setting the `defaultVolumeBasePanelType` property.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e1d2327fea614794bd97297649c5bfd4~tplv-goo7wpa0wc-image.image" width="365px" /></div>

```Kotlin
WindowContainer(
    ...
    defaultVolumeBasePanelType = DefaultVolumeBasePanelType.Default, // Show the base panel during interaction
    ...
) {...}
```

#### Alignment mode
For Volumetric windows, you can set the alignment mode by configuring the `volumeAlignment` property, thereby controlling the container's posture adjustment strategy during vertical drag operations performed by the end user.
There are two alignment modes: Gravity and Tilted:

* **Gravity**: Gravity mode. The Volumetric window is always perpendicular to the ground.
* **Tilted**: Tilted mode. The Volumetric window tilts toward the user.

| **Gravity** | **Tilted** |
| --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ff307280d73941609856ef9861dc4a29~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9856cb3445ec46b3ac35a19439d8177c~tplv-goo7wpa0wc-image.image) |
The code example is as follows:
```Kotlin
WindowContainer(
    ...
    volumeAlignment = VolumeAlignment.Gravity,
    ...
) {...}
```

#### Viewpoint transformation
The viewpoint describes the user's four horizontal directions relative to the Volumetric window and implicitly includes angle information. You can use the `listener` to obtain the user's direction relative to the current Volumetric window and perform subsequent operations, such as dynamically adjusting the content inside the window or rotating a 3D model based on the user's direction.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/314a11cd18354edd83fd40a5fa1e2eb8~tplv-goo7wpa0wc-image.image" width="650px" /></div>

Code example:
```Kotlin
@Composable
fun VolumeViewPointSample() {
    // Get the current ViewPoint manager
    val viewpointManager = LocalVolumeViewPointManager.current
    // Bind the current ViewPoint using Compose state
    val currentViewPoint by viewpointManager.viewpoint
    // Bind to the lifecycle to listen for ViewPoint changes
    DisposableEffect(viewpointManager) {
        val listener =
            object : VolumeViewPointListener {
                override fun onViewpointChanged(viewpoint: ViewPoint) {
                    // Callback when the viewpoint changes; you can perform custom logic here (such as UI updates, notifications, and so on)
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
        // Call on every frame or data update to dynamically update the entity state
        update = { content, attachments ->
            content.entities
                .firstOrNull { it.enabled && it.getName() == "alarm" }
                ?.let {
                    // Get and update the TransformComponent to control the entity's rotation
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

## Notes

* If the entry interface of your application is not declared via “`DefaultWindowContainer` + `SpatialUI`”, you do not need to add `DefaultWindowContainer` in `SpatialAppScope`, nor do you need to set properties for the default spatial container in the AndroidManifest.xml file.
* If your application contains multiple `Activity` instances, but you do not wish to configure WindowContainer and related properties separately for each `Activity`, they can still start normally and will automatically be included in the WindowContainer named `PICO_SYSTEM_DEFAULT_WINDOWCONTAINER`. Please note that `PICO_SYSTEM_DEFAULT_WINDOWCONTAINER` is a reserved name in the PICO Spatial SDK, used exclusively for the above scenario. Therefore, you cannot use it as the name for a custom WindowContainer.
