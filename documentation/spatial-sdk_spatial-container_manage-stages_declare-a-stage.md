Before using Stage, you need to declare it and set its properties.
Default Stage and non-default Stage have different declaration methods:

* The default Stage is declared through `AndroidManifest.xml`.
* The non-default Stage must be declared in the DSL of `mainApp`, and can also be declared in `AndroidManifest.xml` as needed.

## Declare the default Stage
You need to specify a default spatial container for the application. When the application starts, the default spatial container will be opened first to display the application's initial interface.
* You can declare only one default spatial container for the app.
* To set a WindowContainer as the default spatial container, refer to "[Declare a WindowContainer](/register-window-containers)".

Follow these steps to declare a Stage as the default spatial container.

1. **Declare the default WindowContainer in mainApp**
   Within the `SpatialAppScope` scope of `mainApp`, call the `DefaultStage` function to define the content of the default window.
   ```Kotlin
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultStage {
           MainStageContent() // Content of the default Stage, which is a Composable function
       }
    }
    
    @Composable
    fun MainStageContent() {
        // ...
    }
   ```

2. **Set properties in AndroidManifest.xml**
   In the `AndroidManifest.xml` file, configure properties for the default Stage using the `<meta-data>` tag. These properties define the basic behavior and appearance of the Stage.
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
               
               <meta-data
                   android:name="pico.spatial.stage.id"
                   android:value="your_stage_name" />
               <meta-data android:name="pico.spatial.stage.style" android:value="1" />
           </activity>
       </application>
   
   </manifest>
   ```


### Property list
You can set the following properties for the default Stage:
| **Property** | **Description** |
| --- | --- |
| pico.spatial.stage.id | An arbitrary unique string representing the name of the Stage. |
| pico.spatial.stage.style | The style of the Stage, used to control the integration method between Video Seethrough (VST) of the real environment and the virtual scene, as well as image-based lighting (IBL) and the rendering behavior of virtual entities. Currently, the system default setting is the `Mixed` style. <br>  <br> * `Automatic`: The style is determined by the system. <br> * `Mixed`: Virtual entities are always rendered, and image-based lighting is entirely sourced from Video Seethrough of the real environment. <br>    The following figure shows a scene with the Mixed style. In this example, a virtual metallic sphere is enclosed by a sky sphere. Although the sky sphere uses the "Night Art Museum" map and enables image-based environmental lighting, the sphere still reflects the real environment (bedroom) via Video Seethrough (VST). This is because, in `Mixed` mode, environmental lighting comes entirely from the real Room's Video Seethrough (VST), not from the Night Art Museum. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19a80d5a89724216a5570af27db82450~tplv-goo7wpa0wc-image.image) <br> * `Progressive`: Allows you to adjust the immersion level to control how the real environment's Video Seethrough (VST) blends with virtual entities. You can set the immersion level using the Stage's `immersion` parameter. The immersion value ranges from 0 to 100: <br>       * **immersion is 0**: The experience is similar to the `Mixed` style; you can still see the real environment. However, unlike the Mixed style, virtual entities are not rendered. Therefore, both the metallic sphere and the Night Art Museum disappear. <br>       * **immersion greater than 0 and less than 100**: As the `immersion` value increases, the rendering of the real environment gradually decreases, while the rendering of virtual entities gradually increases. <br>       * **immersion is 100**: Equivalent to the `Full` style. For details, refer to the description of the `Full` style. <br>    The following image shows the scene with `immersion` set to 50 in Progressive style. In this example, a virtual metallic sphere is enclosed by a sky sphere. The sky sphere uses the "Night Art Museum" map and enables image-based environmental lighting. You can see that the reflection effect on the metallic sphere is a blend of the real environment's (bedroom) Video Seethrough (VST) and the Night Art Museum. The front of the metallic sphere shows the real environment's Video Seethrough (VST), while the edges and back show the art museum. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c2e62feb1274952a37708921af99308~tplv-goo7wpa0wc-image.image) <br> * `Full`: Virtual entities are always rendered, and image-based environmental lighting comes entirely from the virtual scene. Therefore, if you do not set a virtual scene, the application will display a pure black background due to the lack of lighting. <br>    The following image shows the scene in `Full` style. In this example, a virtual metallic sphere is enclosed by a sky sphere. The sky sphere uses the "Night Art Museum" map and enables image-based environmental lighting. You can see that the metal sphere fully reflects the virtual nighttime art museum. The real environment (bedroom) is completely blocked. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3e504c44118a45c480a5e58e83f0068f~tplv-goo7wpa0wc-image.image) |
| pico.spatial.stage.immersion | Default immersion level, with a value range of [0, 100], and a default value of 50. <br> This property is only effective for Stages with the `Progressive` style. It must be set during declaration. |
| pico.spatial.stage.immersion_min | Minimum allowed immersion level, with a value range of [0, 100], and a default value of 0. <br> This property is only effective for Stages with the `Progressive` style. It must be set during declaration. |
| pico.spatial.stage.immersion_max | Maximum allowed immersion level, with a value range of [0, 100], and a default value of 100. <br> This property is only effective for Stages with the `Progressive` style. It must be set during declaration. |
## Declare the non-default Stage
You can use the following methods to declare the non-default Stage and set its properties.

* Declare the non-default Stage and set its properties in the DSL of `mainApp`.
* Declare the non-default Stage and set its properties in the `AndroidManifest.xml` file.

* If you declare a non-default Stage in the `AndroidManifest.xml` file, you must also declare it in the DSL using the same container ID. Otherwise, the Stage will not be able to load any Composable content.
* When you set different values for the same property using different methods (such as DSL and `AndroidManifest.xml`), the system will determine which value takes effect based on a predefined priority order. For details, refer to "[Property priority](/sdk/register-stages)".

### Declare Stage using static DSL and AndroidManifest.xml
The following sample code declares the non-default Stage using both static DSL and `AndroidManifest.xml`.
In both static DSL and `AndroidManifest.xml`, the Stage ID is `ConfigManifestStage`.
```Kotlin
Stage(
    id = "ConfigManifestStage",
    immersion = Immersion(70, 20, 90),
    upperLimbRenderMode = UpperLimbRenderMode.Visible,
) {
    SampleBase("ConfigManifestStage") { ConfigStageSample() }
}
 
// Do not pass in new properties
LocalSpatialNavigator.current.openStage("ConfigManifestStage")
```

```XML
<activity
    android:name=".containers.ConfigManifestStageActivity"
    android:exported="true">

    <meta-data android:name="pico.spatial.stage.id"
        android:value="ConfigManifestStage"/>
    <meta-data android:name="pico.spatial.stage.style" android:value="3" />
    <meta-data android:name="pico.spatial.stage.immersion" android:value="60" />
    <meta-data android:name="pico.spatial.stage.brightness" android:value="bright" />
    <meta-data android:name="pico.spatial.stage.upperlimb" android:value="2" />

</activity>
```

### Declare Stage using static DSL, dynamic properties, and AndroidManifest.xml
The following sample code declares the non-default Stage using static DSL, dynamic properties (passing new properties when opening the Stage with `openStage()`), and `AndroidManifest.xml`. In static DSL, dynamic parameters, and `AndroidManifest.xml`, the Stage ID is `ConfigManifestStage`.
```Kotlin
Stage(
    id = "ConfigManifestStage",
    immersion = Immersion(70, 20, 90),
    upperLimbRenderMode = UpperLimbRenderMode.Visible,
) {
    SampleBase("ConfigManifestStage") { ConfigStageSample() }
}

// Pass in new properties when opening
LocalSpatialNavigator.current.openStage(
    "ConfigManifestStage",
    style = StageStyle.Mixed,
    upperLimbRenderMode = UpperLimbRenderMode.Visible,
)
```

```XML
<activity
    android:name=".containers.ConfigManifestStageActivity"
    android:exported="true">

    <meta-data android:name="pico.spatial.stage.id"
        android:value="ConfigManifestStage"/>
    <meta-data android:name="pico.spatial.stage.style" android:value="3" />
    <meta-data android:name="pico.spatial.stage.immersion" android:value="60" />
    <meta-data android:name="pico.spatial.stage.brightness" android:value="bright" />
    <meta-data android:name="pico.spatial.stage.upperlimb" android:value="2" />

</activity>
```

### Property list
You can set the following properties for the non-default Stage:
| **DSL property** | **AndroidManifest.xml property** | **Description** |
| --- | --- | --- |
| id | pico.spatial.stage.id | Any unique string representing the name of the Stage. |
| style | pico.spatial.stage.style | The style of the Stage, used to control the fusion method between the real environment's Video Seethrough (VST) and the virtual scene, as well as the behavior of image-based lighting (IBL) and virtual entity rendering. Currently, the system's default setting is the `Mixed` style. <br>  <br> * `Automatic`: The style is determined by the system. <br> * `Mixed`: Virtual entities are always rendered, and image-based lighting is entirely sourced from the real environment's Video Seethrough (VST). <br>    The following figure shows a scene in the Mixed style. In this example, a virtual metal sphere is enclosed by a sky sphere. Although the sky sphere uses the "nighttime art museum" map and enables image-based lighting, the sphere still reflects the real Room's (bedroom) Video Seethrough (VST). This is because, in `Mixed` mode, image-based lighting is entirely sourced from the real Room's Video Seethrough (VST), rather than the nighttime art museum. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19a80d5a89724216a5570af27db82450~tplv-goo7wpa0wc-image.image) <br> * `Progressive`: Allows you to adjust the immersion level to change how the real-world Video Seethrough (VST) and virtual entities are blended. You can set the immersion level using the `Stage` `immersion` parameter. The value range for immersion is 0~100: <br>       * **immersion = 0**: The experience is similar to the `Mixed` style, and you can still see the real environment. However, unlike the Mixed style, virtual entities are not rendered. Therefore, both the metal sphere and the night art gallery will disappear. <br>       * **immersion greater than 0 and less than 100**: As the `immersion` value increases, the rendering of the real environment gradually decreases, while the rendering of virtual entities gradually increases. <br>       * **immersion = 100**: Equivalent to the `Full` style. For details, refer to the description of the `Full` style. <br>    The following figure shows the scene when immersion is 50 in the Progressive style. In this example, a virtual metal sphere is enclosed by a sky sphere. The sky sphere uses the "Night Art Gallery" map and enables image-based environment lighting. You can see that the reflection effect on the metal sphere is a blend of the real environment's (bedroom) Video Seethrough (VST) and the night art gallery. The front of the metal sphere shows the real environment's Video Seethrough (VST), while the edges and back show the art gallery. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c2e62feb1274952a37708921af99308~tplv-goo7wpa0wc-image.image) <br> * `Full`: Virtual entities are always rendered, and image-based environment lighting comes entirely from the virtual scene. Therefore, if you do not set a virtual scene, the application will display a pure black background due to the lack of lighting. <br>    The following figure shows the scene in the `Full` style. In this example, a virtual metal sphere is enclosed by a sky sphere. The sky sphere uses the "Night Art Gallery" map and enables image-based environment lighting. You can see that the metal sphere fully reflects the virtual night art gallery. The real environment (bedroom) is completely blocked. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3e504c44118a45c480a5e58e83f0068f~tplv-goo7wpa0wc-image.image) |
| Immersion.defaultValue | pico.spatial.stage.immersion | Default immersion level, value range is [0, 100], default is 50. <br> This property is only valid for Stage with the `Progressive` style. Must be set at declaration. |
| Immersion.minValue | pico.spatial.stage.immersion_min | Allowed minimum immersion level, value range is [0, 100], default is 0. <br> This property is only valid for Stage with the `Progressive` style. Must be set at declaration. |
| Immersion.maxValue | pico.spatial.stage.immersion_max | The maximum allowed immersion level. Value range: [0, 100]. Default value: 100. <br> This property is only effective for Stages with the `Progressive` style. Must be set during declaration. |
## Stage property configuration instructions
### Property priority
Property configuration follows the following priority order, from highest to lowest:

1. Properties that take effect dynamically when opening the Stage using the `OpenStage()` function.
2. Properties declared in the DSL.
3. Properties declared in `AndroidManifest.xml`.

When the same property is set from different sources, the higher-priority configuration overrides the lower-priority configuration. The system merges different properties set from all sources.
```Kotlin
// Pass the property when opening the Stage using the OpenStage() function
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

If a property is not assigned a value, the following system default values will be used.
```Kotlin
brightness = SpatialContainerInfo.getBrightnessByType(BRIGHTNESS_DEFAULT),
imm = Immersion.DEFAULT,
style = StageStyle.AUTOMATIC,
activityClass = SpatialStubActivity::class.java,
upperLimbRenderMode = UPPER_LIMB_DEFAULT,
```

### Property configuration example
For example, in DSL, if the app uses Stage as the default spatial container and also uses another Stage named "OtherStage", with their contents being `HomeContent()` and `OtherStageContent()` respectively, the following configuration is required in `mainApp`:
```Kotlin
fun SpatialAppScope.mainApp() { 
    // Set the default content of the  Stage
    DefaultStage { 
        HomeContent() 
    } 
     
    // Set the property and content for the Stage named "OtherStage"
    Stage(id = "OtherStage", immersion = Immersion(min = 0, max = 100, default = 50)) {
        OtherStageContent()
    }
} 
```

#### Configure the default immersion level and adjustable range
In a spatial app, the immersion level affects the degree of integration between the virtual scene and the real world. For a Stage with the `Progressive` style, you need to set the default immersion level and adjustable range when declaring it, and then monitor changes in the immersion level of this type of Stage during user interaction.

* **Configure the default immersion level and adjustable range**
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

* **Monitor immersion level changes**
   You can monitor changes in the immersion level through the `StageImmersionManager` interface.
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


## Caution
If the entry interface of your app is not declared using "`DefaultStage` + `SpatialUI`", you do not need to add `DefaultStage` in `SpatialAppScope`, nor do you need to set properties for the default spatial container in the AndroidManifest.xml file.
