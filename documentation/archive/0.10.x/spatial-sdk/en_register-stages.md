Before using Stage, you need to declare it and set its properties. The declaration methods for the default Stage and non-default Stage at startup are different: the default Stage is declared through `AndroidManifest.xml`; the non-default Stage must be declared in mainApp's DSL, and can also be declared in `AndroidManifest.xml` at the same time as needed.
## Declare the default Stage
You need to specify a default spatial container for the application. When the application starts, the default spatial container will be opened first to display the application's initial interface.
* You can only declare one default spatial container for the application.
* To set a WindowContainer as the default spatial container, refer to "[Declare WindowContainer](/register-window-containers)".

Follow these steps to declare a Stage as the default spatial container.

1. Declare the default Stage and specify its content in `mainApp`.
   ```Kotlin
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultStage {
           MainStageContent() // The content of the default Stage, which is a Composable function
       }
    }
    
    @Composable
    fun MainStageContent() {
        // ...
    }
   ```

2. Set the properties for the `Activity` of the default Stage in the AndroidManifest.xml file. For details, refer to "[Configure Stage properties](/set-properties-for-stages)".
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


## Declare the non-default Stage
You can use the following methods to declare a non-default Stage and set its properties. For detailed instructions on property settings, refer to "[Configure Stage properties](/set-properties-for-stages)".

* Declare the non-default Stage and set its properties in mainApp's DSL.
* Declare the non-default Stage and set its properties in the `AndroidManifest.xml` file.

* If you declare a non-default Stage in the `AndroidManifest.xml` file, you must also declare it in the DSL using the same container ID. Otherwise, this Stage will not be able to load any Composable content.
* When different values are set for the same property using various methods (such as DSL and `AndroidManifest.xml`), the system determines which value will ultimately take effect based on a predetermined priority order. For details, see "[Configure Stage properties](/set-properties-for-stages)".

### Declare Stage using static DSL and AndroidManifest.xml
The following sample code declares a non-default Stage using both static DSL and `AndroidManifest.xml`. In both static DSL and `AndroidManifest.xml`, the Stage ID is `ConfigManifestStage`.
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
The following sample code declares a non-default Stage using static DSL, dynamic properties (passing new properties when opening Stage with `openStage()`), and `AndroidManifest.xml`. In static DSL, dynamic parameters, and `AndroidManifest.xml`, the Stage ID is `ConfigManifestStage`.
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

## Precautions
If the entry interface of your application is not declared using "`DefaultStage` + `SpatialUI`", there is no need to add `DefaultStage` in `SpatialAppScope`, nor to set the properties for the default spatial container in the AndroidManifest.xml file.
