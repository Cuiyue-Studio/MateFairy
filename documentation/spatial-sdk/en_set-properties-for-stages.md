Before using Stage, you need to configure its properties.
* When the application starts, the default spatial container (WindowContainer or Stage) you set will be opened first. If the default spatial container is a Stage, refer directly to the “Set properties for the default Stage” section to configure its properties; if the default container is a WindowContainer, refer to [Configure WindowContainer Properties](/set-properties-for-window-containers) for configuration.
* The ID of the Stage is a required property; other properties are optional. If not set, the default value will be used.

## Configure default Stage properties
### Property configuration methods
Configure the default Stage properties in the `<meta-data>` section of the AndroidManifest.xml file.
```XML
<meta-data
    android:name="pico.spatial.stage.id"
    android:value="immersive_environment" />
<meta-data android:name="pico.spatial.stage.style" android:value="1" />
```

Configure the default Stage content in `mainApp`, specifically, place the content to be loaded inside the function body of `DefaultStage`. You need to place the content that should be displayed immediately after the app launches (such as the homepage, main scene, and more) in the default Stage.
```Kotlin
fun SpatialAppScope.mainApp() { 
    // Declare the DefaultStage
    DefaultStage { 
        HomeContent() 
    } 
    // Declare other SpatialContainers
    // ...
} 
```

### Property & meta-data reference table
The properties supported by the default Stage can be configured in meta-data as key-value pairs, as follows:
| **Key** | **Value** |
| --- | --- |
| pico.spatial.stage.id | Any unique string representing the name of the Stage. |
| pico.spatial.stage.style | Style of the Stage: <br>  <br> * `"0"` (default): Set automatically by the system. The current system default is `"1"`. <br> * `"1"`: Mixed style, immersion is 0. Virtual objects are overlaid in the user's real environment, allowing them to naturally blend into the background. <br> * `"2"`: Progressive style, immersion ranges from [0, 100]. The user can adjust the immersion level via the system UI or HMD knob. The immersion level when the Stage opens depends on the set immersion value; if not set, the default is 50. <br> * `"3"`: Full mode, immersion is 100. The virtual environment is fully displayed, and the user cannot see any objects in the real environment. <br>  <br> ***Note***: <br>  <br> * The default Stage will be automatically opened when the application starts for the first time, and the style set here will be used. <br> * If the default Stage is opened using the `openStage` function in the application, the `style` parameter must be specified within this function. |
| pico.spatial.stage.immersion | An Int value within [0, 100], default is 50. Only effective in Progressive style, specified when declaring the Stage, takes effect when opening the Stage. |
| pico.spatial.stage.immersion_min | An Int value within [0, 100], default is 0. Only effective in Progressive style, specified when declaring the Stage. |
| pico.spatial.stage.immersion_max | An Int value within [0, 100], default is 100. Only effective in Progressive style, specified when declaring the Stage. |
## Configure non-default Stage properties
When using other non-default Stages in your app, you can configure Stage properties using any of the following methods and open them for display as needed. For details on how to open and close a Stage, refer to [Open or close a Stage](/open-or-close-stages).

* Configure non-default Stage properties using DSL in `mainApp`.
* Configure non-default Stage properties in the `AndroidManifest.xml` file's `<meta-data>` section.

### Property configuration methods
For example, using DSL, if the app uses Stage as the default spatial container and also uses another Stage named "OtherStage", with their contents being `HomeContent()` and `OtherStageContent()` respectively, you need to configure the following in `mainApp`:
```Kotlin
fun SpatialAppScope.mainApp() { 
    // Set the content of the default Stage
    DefaultStage { 
        HomeContent() 
    } 
     
    // Set the properties and content of the Stage named "OtherStage"
    Stage(id = "OtherStage", immersion = Immersion(min = 0, max = 100, default = 50)) {
        OtherStageContent()
    }
} 
```

#### Configure default immersion level and adjustable range
In a spatial app, the immersion level affects the degree of integration between the virtual scene and the real world. For a Stage with the `Progressive` style, you need to set the default immersion level and adjustable range when declaring it, and then listen for changes in the immersion level during user interaction.

* **Configure default immersion level and adjustable range**
   You can set the initial immersion level and adjustable range for a Stage using the following properties. These settings take effect immediately when the Stage is opened.
   * `Immersion.defaultValue`: Default immersion level
   * `Immersion.minValue`: Minimum immersion level
   * `Immersion.maxValue`: Maximum immersion level
   ```Kotlin
   Stage(
       ...
       immersion = Immersion(min = 0, max = 100, default = 50)
       ...
   ) {...}
   ```

* **Listen for immersion level changes**
   You can listen for changes in the immersion level through the `StageImmersionManager` interface.
   ```Kotlin
   @Composable
   fun Demo() {
       val localProgressiveImmersion = LocalStageImmersionManager.current
       val currentImmersionLevel by localProgressiveImmersion.currentImmersionLevel
       DisposableEffect(localProgressiveImmersion) {
           val listener =
               object : StageImmersionListener {
                   override fun onImmersionChanged(immersionLevel: Int) {
                       // Execute custom logic
                   }
               }
           localProgressiveImmersion.addImmersionListener(listener)
           onDispose { localProgressiveImmersion.removeImmersionListener(listener) }
       }
   
       Text("Current immersion level is $currentImmersionLevel")
       
   }
   ```


### Property priority
Property configuration follows the following order of priority, from highest to lowest:

1. Properties that take effect dynamically when opening a Stage.
2. Properties declared in the DSL.
3. Properties declared in `AndroidManifest.xml`.

When the same property is set from different sources, the configuration with higher priority will override the configuration with lower priority. The system will merge different properties set from all sources.
```Kotlin
// The dynamic properties that take effect when opening the container are equivalent to the dynamic properties passed to openStage
LocalSpatialNavigator.current.openStage(
    "sample",
    style = StageStyle.Mixed,
    upperLimbRenderMode = UpperLimbRenderMode.Visible,
)
// Container for registering static properties
Stage(id = "sample", immersion = Immersion.Default) {
    Sample() 
}
```

If a property is not assigned a value, the following system default value will be used.
```Kotlin
brightness = SpatialContainerInfo.getBrightnessByType(BRIGHTNESS_DEFAULT),
imm = Immersion.DEFAULT,
style = StageStyle.AUTOMATIC,
activityClass = SpatialStubActivity::class.java,
upperLimbRenderMode = UPPER_LIMB_DEFAULT,
```

### Property & meta-data reference table
The properties that can be set for non-default Stages are as follows:
| **Property** | **Value** |
| --- | --- |
| id | Any unique string representing the name of the Stage. |
| style | The style of the Stage, which affects the immersion level: <br>  <br> * `StageStyle.Automatic` (default): The system sets it automatically. The current system default is `StageStyle.Mixed`. <br> * `StageStyle.Mixed`: Immersion level is 0. Virtual objects are overlaid in the user's real environment, allowing them to naturally blend into the background. <br> * `StageStyle.Progressive`: Immersion level ranges from [0, 100]. The user can manually adjust the application's immersion level through the system UI. <br> * `StageStyle.Full`: Immersion level is 100. The application places the user in a virtual environment completely isolated from the real environment. <br>  <br> ***Note***: `style` is not a fixed property of the Stage. It does not need to be specified during declaration, but only when calling the `openStage` function to open it. The same Stage can use different styles each time it is opened. |
| Immersion.defaultValue | Default immersion level, value range is [0, 100], default is 50. <br> ***Note***: This property is only valid for Stages with the `StageStyle.Progressive` style. It must be set during declaration. |
| Immersion.minValue | Allowed minimum immersion level, value range is [0, 100], default is 0. <br> ***Note***: This property is only valid for Stages with the `StageStyle.Progressive` style. It must be set during declaration. |
| Immersion.maxValue | Allowed maximum immersion level, value range is [0, 100], default is 100. <br> ***Note***: This property is only valid for Stages with the `StageStyle.Progressive` style. It must be set during declaration. |
