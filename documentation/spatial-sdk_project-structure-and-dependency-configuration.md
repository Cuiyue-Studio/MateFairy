After creating a project using the Spatial WindowContainer template in Android Studio, the project will automatically generate the default directory structure, the configuration for `Application` and the launch `Activity`, as well as related dependency configurations. This article will provide a detailed introduction to the specific content of these default configurations.
## Project structure
After creating a spatial app using the template, the **Project**  view in Android Studio will display the following directory structure:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/be5f114ba9e84eb8b4d10ce464d89f54~tplv-goo7wpa0wc-image.image)
Among them:

* /app/src/main/java contains Kotlin and Java source code.
* /editor-asset/src/main/res3d contains the Spatial Editor project:
   * "SpatialPackContent" is the name of the Spatial Editor project.
   * /Sources directory includes the Assets and Scenes folders by default. Among them, the Scenes folder contains scenes that can be loaded, that is, .usda files. For information on how to load resources in the Spatial Editor, refer to "[AssetBundle](/asset-bundle)".

## Application and Launch Activity
For spatial apps developed using the PICO Spatial SDK, `Application` and launch `Activity` must meet the following requirements:

* In the `onCreate` function of the `Application` class, call `launch(SpatialAppScope::mainApp)`. In the AndroidManifest.xml file, set the name of the `<application>` tag based on the directory structure.
* The entry function `mainApp` is under the scope of `SpatialAppScope`, and all spatial containers are declared in the `mainApp` function.
* The `Activity` to be launched inherits from `SpatialLaunchActivity`. In the AndroidManifest.xml file, configure the `<activity>` element and the `<intent-filter>` element, specifying the `<action>` and `<category>` tags according to the directory hierarchy.

The default configuration in the template project is as follows:
In the platform folder of the template project, the PICO Spatial SDK defines an `AndroidApplication` class and a `LaunchActivity` class, which correspond to the `<application>` and `<activity>` sections in the AndroidManifest.xml file, respectively.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.35960958596745374);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b90a2d87cdc94f1188f54b52656c23a1~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.6403904140325465);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e4c529e7b774e4aa6baebddf2c7fca4~tplv-goo7wpa0wc-image.image)


</div>
</div>

In the template project, the `AndroidApplication` class inherits from the `Application` class. The PICO Spatial SDK overrides its `onCreate` method and calls the `launch(SpatialAppScope::mainApp)` method within it, so that the `mainApp` function is executed when the app is created.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/69d4c875910f4b43a0e8a67a12cc956b~tplv-goo7wpa0wc-image.image)
The `mainApp` function is the entry function of a spatial app. It serves as the main function defined within the scope of `SpatialAppScope`. All spatial containers used in the spatial app, including both default and non-default spatial containers, must be declared in `mainApp`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/22bb8386e54e4bbcac2bb4f66395a69e~tplv-goo7wpa0wc-image.image)
The `LaunchActivity` class inherits from the `SpatialLaunchActivity` class, serves as the launch `Activity` for spatial apps, and is also the `Activity` corresponding to the default spatial container.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6c416c2ccd24432d91bc681cc8b24889~tplv-goo7wpa0wc-image.image)
For how to register a spatial container, refer to [Register WindowContainer](/register-window-containers) and [Register Stage](/register-stages).
## Dependency configuration
### PICO Spatial SDK
PICO Spatial SDK includes several independent modules, each focused on a specific spatial capability. You can import desired modules as needed. It is recommended to include at least the Core and UI modules to ensure that the basic capabilities can be used.
The introduction to each module is as follows:
| **Module** | **Capability** | **Group ID** | **Artifact ID** |
| --- | --- | --- | --- |
| Spatial Pack | Provides a complete core runtime framework for spatial apps, including a spatial container management system, entity-component-system (ECS) architecture pattern, resource management, event system, rendering and special effects, animation system, physics simulation, spatial audio and video, and more. | com.pico.spatial.core | core |
| Sense Pack | Enhance the app's spatial awareness capability, such as placing persistent spatial anchors in space, scanning spatial meshes in the environment, performing plane detection, and so on. | com.pico.spatial.sense | sense |
| Tracking Pack | Provides comprehensive motion tracking for spatial apps, including HMD tracking, controller tracking, and hand tracking. You can use this module to obtain the position and pose data of HMDs, controllers, and hand joints, enabling the implementation of related features. | com.pico.spatial.tracking | tracking |
| Spatial UI | A declarative UI framework for spatial apps, developed with Jetpack Compose and featuring designs in the PICO Design brand style, for quickly building UI interfaces. It mainly includes the following capabilities: <br>  <br> * Provides capabilities related to app types and spatial containers. <br> * Provides spatial effects for View, including spatial floating, spatial rotation, spatial hovering, and so on. <br> * Supports interactive event handling, including drag, tap, and more. <br> * Provides spatial window-related components, such as augment, subwindow, toolbar, and more. <br> * Provides themes and UI components that comply with PICO Design. | com.pico.spatial.ui | platform |
|  |  |  | foundation |
|  |  |  | design |
| foundation | Provides APIs related to spatial mathematics, annotations, JSON for spatial apps. | com.pico.spatial.foundation  | foundation |
| spatialml | SpatialML is a data-driven runtime framework designed specifically for mixed reality (MR), aiming to fully unlock PICO's spatial computing potential. <br> Custom algorithms can be deployed to SpatialML, including those implemented with OpenCV or machine learning models trained using mainstream frameworks such as PyTorch, TensorFlow, or ONNX. SpatialML deploys models via Qualcomm AI Engine Direct (QNN) **** , leverages the Qualcomm NPU integrated in PICO for hardware-level acceleration of model inference, and can rapidly integrate stereo cameras, depth cameras, spatial positioning, and anchor data as input, ultimately enabling algorithm outputs to directly drive immersive MR interactive experiences. | com.pico.spatial.ml | readback |
|  |  |  | securemr |
In the **settings.gradle.kts** file, repositories used to retrieve plugins and dependencies are defined in both the `pluginManagement` and `dependencyResolutionManagement` sections, including:

*  `google()`: for official Android plugins; 
*  `mavenCentral()`: the standard public Maven repository;
*  `gradlePluginPortal()`: the official Gradle plugin portal;
*  Custom Maven repository for fetching plugins or dependencies from a specified private artifact repository:
   ```Kotlin
   maven {
       url = uri("https://artifact.bytedance.com/repository/Volcengine")
       name = "VolcengineMaven"
   }
   ```


The template project uses version catalog for version management, and its **libs.versions.toml** file contains the following default configurations:

* In the `[versions]` section, the SDK version is added. You can modify it according to your needs.
   ```TOML
   [versions]
   // ...
   bom = "sdk_version" # Enter the SDK version you are using
   ```

* In the `[libraries]` section, the following dependencies are added. You can remove dependencies for modules you do not need.
   ```TOML
   [libraries]
   // ...
   bom = { group = "com.pico.spatial", name = "bom", version.ref = "bom" }
   core = { group = "com.pico.spatial.core", name = "core" }
   platform = { group = "com.pico.spatial.ui", name = "platform" }
   foundation = { group = "com.pico.spatial.ui", name = "foundation" }
   design = { group = "com.pico.spatial.ui", name = "design" }
   sense = { group = "com.pico.spatial.sense", name = "sense" }
   tracking = { group = "com.pico.spatial.tracking", name = "tracking" }
   ```


In the module-level **build.gradle.kts** file, the template project has also added the following dependencies in the `dependencies {}` section, and you can remove the dependencies for modules you do not need.
```Kotlin
dependencies {
    // ...
    implementation(platform(libs.bom))
    implementation(libs.core)
    implementation(libs.platform)
    implementation(libs.foundation)
    implementation(libs.design)
    implementation(libs.sense)
    implementation(libs.tracking)
}
```

Additionally, if you do not use version catalog for version management, you can enter the SDK version you want to use and remove dependencies for unneeded modules in the project's module-level **build.gradle.kts** file.
```Kotlin
dependencies {
    //...
    implementation(platform("com.pico.spatial:bom:sdk_version")) // You need to fill in the SDK version you are using
    implementation("com.pico.spatial.core:core")
    implementation("com.pico.spatial.ui:platform")
    implementation("com.pico.spatial.ui:foundation")
    implementation("com.pico.spatial.ui:design")
    implementation("com.pico.spatial.sense:sense")
    implementation("com.pico.spatial.tracking:tracking")
}
```

### Spatial Tools
Spatial Tools is a compiler tool included with the PICO Spatial Plugin. When building a project, it automatically compiles the Spatial Editor project into `.bundle` files through the Gradle build system, and seamlessly integrates it into the `/assets` directory of the APK. Note that these `.bundle` files are generated and packaged dynamically only during the build process, so you will not see these files in the `assets` folder of the local development environment. After packaging is complete, you can use a resource path such as `"asset://YourBundleName.bundle"` in your code to load the AssetBundle, enabling dynamic loading and management of scenes and other resources.
Each Spatial Editor project added to the spatial app project is presented as a single module. In the template project, a module named `editor-asset` has been added. You can see the following module configuration in the project's top-level **settings.gradle.kts** file:
```Kotlin
rootProject.name = "My Application"
include(":app")
include(":editor-asset")
```

In the `editor-asset` module's build.gradle file, the following default configurations exist:

* In the `plugins {}` section, Spatial Tools is configured by default:
   ```Kotlin
   plugins {
       // ...
       id 'com.pico.spatial.tools' version '0.10.1'
   }
   ```

* At the bottom of the file, the `spatial {}` section is configured by default. You can replace the name here with a custom name for your .bundle file:
   ```Kotlin
   spatial {
       name = "editor-asset" // Can be replaced with the name of your .bundle
       spatialToolsVersion = 0.10
   }
   ```


In the `app` module's `build.gradle.kts` file, a dependency on the `editor-asset` module is also added in the `dependencies{}` section:
```Kotlin
dependencies {
    ...
    implementation(project(":editor-asset"))
    ...
}
```

### Resource compression-related
To optimize the loading performance of models and resource files, it is recommended to specify file types that should not be compressed using `noCompress` within the `android {}` block in your app module's `build.gradle.kts` file. This approach provides two key benefits:

*  **Faster loading speed**: Resources are not compressed during packaging, allowing the system to access them directly via memory mapping (mmap) at runtime, eliminating the need for decompression. 
*  **Reduced startup overhead**: The system can preprocess resources (for example, for compatibility upgrades) after app updates, which helps speed up an app's startup. 

Additionally, using `noCompress` involves a trade-off between APK size and runtime performance:

*  **With** **`noCompress`**: The APK's size increases, but resource loading is faster and runtime performance is more stable. 
*  **Without** **`noCompress`**: The APK's size is smaller, but additional decompression overhead is introduced at runtime.

Some file formats (such as `.wav`) are not compressed by the Android system by default, so there is no need to include them in the `noCompress` configuration.

```Kotlin
android {
    ...
    androidResources {
        noCompress.add(".bundle")
        noCompress.add(".glb")
        noCompress.add(".ktx")
        noCompress.add(".usdz")
    }
    ...
}
```

### 
