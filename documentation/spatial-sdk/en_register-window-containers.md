Before using WindowContainer, you need to declare it and set its properties. The declaration methods for the default WindowContainer at startup and non-default WindowContainer are different: the default WindowContainer is declared through `AndroidManifest.xml`; the non-default WindowContainer must be declared in `mainApp`'s DSL, and can also be declared in `AndroidManifest.xml` at the same time as needed.
## Declare the default WindowContainer
You need to specify a default spatial container for the application. When the application starts, the default spatial container will be opened first to display the application's initial interface.
* You can declare only one default spatial container for the application.
* To set a Stage as the default spatial container, refer to "[Declare Stage](/register-stages)".

Follow these steps to declare a WindowContainer as the default spatial container.

1. Declare the default WindowContainer in `mainApp`.
   ```Kotlin
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultWindowContainer {
           MainPageContent() // The content of the default WindowContainer, which is a Composable function
       }
    }
    
    @Composable
    fun MainPageContent() {
        // ...
    }
   ```

2. Set the properties for the `Activity` of WindowContainer in the AndroidManifest.xml file. For details, refer to "[Configure WindowContainer properties](/set-properties-for-window-containers)".
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
                   android:name="pico.spatial.windowcontainer.id"
                   android:value="your_window_container_name" />
               <meta-data
                   android:name="pico.spatial.windowcontainer.style"
                   android:value="1" />
               <meta-data android:name="pico.spatial.windowcontainer.defaultsize" android:value="1280x720" />
               <meta-data
                   android:name="pico.spatial.windowcontainer.materialbackground"
                   android:value="1" />
               <!-- Other meta-data configuration... -->
   
           </activity>
       </application>
   
   </manifest>
   ```


## Declare a non-default WindowContainer
You can use the following methods to declare a non-default WindowContainer and set its properties. For detailed instructions on property settings, refer to "[Configure WindowContainer properties](/set-properties-for-window-containers)".

* Declare a non-default WindowContainer and set its properties using DSL in `mainApp`.
* Declare a non-default WindowContainer and set its properties in the `AndroidManifest.xml` file.

* If you declare a non-default WindowContainer in the `AndroidManifest.xml` file, you must also declare it in the DSL using the same container ID. Otherwise, that WindowContainer will not be able to load any Composable content.
* When you set different values for the same property using different methods (such as DSL and `AndroidManifest.xml`), the system determines which value takes effect based on a predefined priority order. For details, refer to "[Configure WindowContainer properties](/set-properties-for-window-containers)".

### Declare a non-default WindowContainer using static DSL and AndroidManifest.xml
The following sample code declares a non-default WindowContainer using both static DSL and the `AndroidManifest.xml` file. In both static DSL and `AndroidManifest.xml`, the ID of WindowContainer is `WindowContainerDSLStaticProp`.
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

### Declare WindowContainer using dynamic DSL and AndroidManifest.xml
The following sample code declares a non-default WindowContainer using both dynamic DSL and `AndroidManifest.xml`. In both dynamic DSL and `AndroidManifest.xml`, the ID of WindowContainer is `WindowContainerDSLDynamicProp`.
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

## Notes

* If the entry interface of your application is not declared through "`DefaultWindowContainer` + `SpatialUI`", you do not need to add `DefaultWindowContainer` in `SpatialAppScope`, nor do you need to set the properties for the default spatial container in the AndroidManifest.xml file.
* If your application contains multiple `Activity` instances, but you do not wish to configure WindowContainer and related properties for each `Activity` individually, they can still start normally and will automatically be assigned to a WindowContainer named `Unspecified`. Please note that `Unspecified` is a reserved name in the PICO Spatial SDK, used exclusively for the above scenario. Therefore, you cannot use it as the name for a custom WindowContainer.
