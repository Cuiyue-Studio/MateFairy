If you already have a mature Android app and plan to port it to PICO OS 6, you need to upgrade the Android-related configurations to ensure that the app runs properly on PICO OS 6.
You can continue using your familiar Android development workflow: use Android Studio as your IDE, Gradle as your build tool, and Kotlin as your primary development language to quickly get started with app development based on PICO OS 6.
While maintaining existing development practices, you may need to make some adjustments in the following areas to ensure that the app runs smoothly on PICO OS 6:

* Upgrade Gradle and AGP (Android Gradle Plugin) versions
* Select compatible Kotlin version
* Add the dependencies of PICO Spatial SDK
* Use Jetpack Compose to build an app interface
* Modify the app's entry point and the window declaration method

## Prerequisites

* All spatial app development tools have been installed as described in [Quickstart](/en_set-up-development-environment).
* Android Studio has been upgraded to version 2025.1.x.

## Step 1: Upgrade Android-related configurations
### Upgrade Gradle and related plugins
When developing apps for PICO OS 6, you must use Android Studio 2025.1.x If your app is currently using a lower version of Gradle, you need to upgrade to a higher version that is suitable for your app. Additionally, pay attention to the following:

* When upgrading Gradle, it is usually also necessary to upgrade AGP.
* Different versions of Android Studio require different minimum versions of AGP, and Gradle must also meet compatibility requirements.

For version correspondences, refer to the [official Android documentation](https://developer.android.com/build/releases/gradle-plugin).
### Upgrade the Kotlin version
After upgrading the Gradle and AGP, you may also need to upgrade the Kotlin version.

* **Recommended version**: Kotlin 1.8.22.
* **Compatibility notes**: If you need to use a different version of Kotlin, refer to the release notes for the corresponding AGP version to ensure that your selected Kotlin version is compatible with the current AGP version. For more information, refer to the [official Android documentation](https://developer.android.com/build/releases/past-releases).

### Upgrade project configurations
After upgrading Gradle and AGP, if the AGP version you previously used was relatively low—for example, below 8.0—your project may require some configuration modifications. Common issues include, but are not limited to, BuildConfig generation issues, namespace configuration issues, and cascading issues with R files. For more information about configuration upgrades, refer to [AGP 8.0 release notes](https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes).
At the same time, it is recommended to use the built-in [AGP Upgrade Assistant](https://developer.android.com/build/agp-upgrade-assistant) in Android Studio to automatically resolve various adjustments to your project caused by upgrading AGP.
### Upgrade build configuration for Android SDK
Because the PICO Spatial SDK uses newer Android Jetpack components, these components require specific versions of Android SDK. Therefore, before using it, you may need to upgrade the Android SDK for the project:

* **compileSdk**: Upgrade to version 35 or later
* **miniSdk**: Upgrade to version 35 or later

If you need to make this adjustment, you can configure as follows in the build.gradle file for each project module:
```Groovy
android {

    defaultConfig {
        compileSdk 35
        minSdkVersion 35
        ...
    }
    
}
```

## Step 2: Add the dependencies of PICO Spatial SDK
You need to add the corresponding dependencies according to your app's needs. To simplify dependency management for the PICO Spatial SDK **** , you can use a BOM file.
You only need to specify the BOM version; the other related modules will automatically download their corresponding versions. You need to add the following content to the build.gradle file of the corresponding project module:
```Groovy
// Version of the PICO Spatial SDK
implementation platform("com.pico.spatial:bom:0.10.7")

// PICO Spatial Pack's dependencies
implementation("com.pico.spatial.core:core")

// SpatialUI's dependencies
implementation("com.pico.spatial.ui:platform")
implementation("com.pico.spatial.ui:foundation")
implementation("com.pico.spatial.ui:design")

// Dependencies of other libraries
implementation("com.pico.spatial.sense:sense")
implementation("com.pico.spatial.tracking:tracking")
implementation("com.pico.spatial.ml:securemr")
implementation("com.pico.spatial.ml:readback")
```

Note that because the SpatialUI component in spatial apps uses Jetpack Compose as the underlying UI framework, you must enable the Compose Compiler and specify the appropriate version in the build.gradle file of any module that uses SpatialUI. The `kotlinCompilerExtensionVersion` should be determined based on the Kotlin version used in your app. For version correspondences, refer to [this article](https://developer.android.com/jetpack/androidx/releases/compose-kotlin).
```Groovy
android {
    ...
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    
    ...
}
```

Meanwhile, because the PICO Spatial SDK has added capabilities for operating various spatial features, PICO has modified and upgraded the original Jetpack Compose code. If your app needs to use and includes components related to SpatialUI, you must remove some existing Android Jetpack Compose dependencies from the project. You can add the following content to the build.gradle file of the module that uses the SpatialUI component:
```Groovy
configurations.all {
    resolutionStrategy {
        exclude group: 'androidx.compose.ui', module: 'ui'
        exclude group: 'androidx.compose.ui', module: 'ui-graphics'
        exclude group: 'androidx.compose.ui', module: 'ui-text'
        exclude group: 'androidx.compose.foundation', module: 'foundation'
    }
}
```

## Step 3: Adjust the app's entry point
At this point, your app already has the basic capabilities of a spatial app, but it is still running in the compatibility mode as a 2D app. To fully utilize spatial capabilities, some interfaces need to be adjusted. Specifically, you need to modify the original main `Application` and main `Activity`, and declare and register the required spatial container. Thus, you need to modify the existing code and add necessary content.
### Declare a spatial container
To use spatial capabilities, each spatial app must declare its own spatial container.
Spatial container must be declared within `SpatialAppScope`. You should perform this operation at the appropriate position. To facilitate future modification and maintenance, it is recommended to create a separate file for this.
Assume that the app package name is `com.example.app`. You can create a file named Main.kt under this package. This file will serve as the entry point for spatial container declarations. In this file, add the following code to declare the default spatial container.
```Kotlin
package com.example.myapp

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope

fun mainApp(scope: SpatialAppScope) = with(scope) {
    DefaultWindowContainer {
         // Content displayed by the default spatial container
    }
}
```

Suppose the launch `Activity` of your existing app is also located in the `com.example.app` package, and the file name is MainActivity.kt. In this case, you may encounter the following situations:

* If your app's interface has already been built using Jetpack Compose, the code in the MainActivity.kt file may be as follows:
   ```Kotlin
   package com.dailystudio.devbricksx.gallery.compose
   
   import android.os.Bundle
   import androidx.activity.ComponentActivity
   import androidx.activity.compose.setContent
   import androidx.core.view.WindowCompat
   import com.dailystudio.devbricksx.gallery.ui.compose.Home
   import com.dailystudio.devbricksx.gallery.theme.GalleryTheme
   
   class MainActivity : ComponentActivity() {
   
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
   
           ...
           
           setContent {
               MyAppTheme {
                   Home()
               }
           }
       }
       
       ...
   }
   ```

   In this case, you need to remove the call to `setContent` from the original `onCreate()` and move all its contents into the declaration of `DefaultWindowContainer`. Other content in `MainActivity` remains unchanged. Finally, the content of the Main.kt file should be as follows:
   ```Kotlin
   package com.example.myapp
   
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.unit.dp
   import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
   import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
   
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultWindowContainer {
           AppTheme {
               HomeScreen()
           }
       }
   }
   ```

* If your app's interface is still built using Android View and XML, then the code in the MainActivity.kt file for the launch activity may be as follows:
   ```Kotlin
   package com.dailystudio.devbricksx.gallery.compose
   
   import android.os.Bundle
   import androidx.activity.ComponentActivity
   import androidx.activity.compose.setContent
   import androidx.core.view.WindowCompat
   import com.dailystudio.devbricksx.gallery.ui.compose.Home
   import com.dailystudio.devbricksx.gallery.theme.GalleryTheme
   
   class MainActivity : ComponentActivity() {
   
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
   
           ...
           
           setContentView(R.layout.activity_main)
       }
       
       ...
   }
   ```

   In this case, you do not need to port or modify the contents of the existing `Activity`. Just keep the original code unchanged. Meanwhile, there is no need to add anything to the declaration of `DefaultWindowContainer`. Finally, the content of the Main.kt file should be as follows:
   ```Kotlin
   package com.example.myapp
   
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.unit.dp
   import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
   import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
   
   fun mainApp(scope: SpatialAppScope) = with(scope) {
       DefaultWindowContainer {
           /* Leave this area empty */
       }
   }
   ```


### Set up the entry function
After declaring a spatial container, you then need to call it at the app's startup. To complete the registration of the spatial container, call the previously defined `mainApp()` function in the `onCreate()` method of the main `Application` class.
Assume your main `Application` class is named `MyApplication`, located in the `com.example.app` package, and the file name is MyApplication.kt. If the class has not been created, you need to create it first. Then, insert the following code into it:
```Groovy
package com.example.myapp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        ...
        
        // Launch to register containers
        launch(::mainApp)
    }
    
    ...

}
```

As a result, the spatial container you declare in `mainApp()` can be properly registered in the system and used as expected in subsequent code.
### Modify the Activity of the default spatial container
In the previous section, you have already declared and registered the app's default spatial container, which corresponds to the launch `Activity` in a traditional Android app. To enable this `Activity` to use spatial app components, it must inherit the unified base class `SpatialLauncherActivity` provided by the PICO Spatial SDK. Therefore, you need to make the following modifications to the app's launch `Activity`:
```Kotlin
package com.example.myapp

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope

class MainActivity : SpatialLaunchActivity() {
    ...
}
```

### Modify the AndroidManifest.xml file
To ensure that previous changes take effect, you need to check the relevant settings in the AndroidManifest.xml file, including:

* The app uses the modified main `Application` class.
* The app uses the modified launch `Activity`.
* The configuration of the default spatial container is correct.

```XML
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    ...
    
    <!-- Use MyApplication as the main application entry -->
    <application
        android:name=".MyApplication">

        <!-- 
            Use MainActivity as the launcher activity and 
            default window container of the spatial app
        -->
        <activity android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/AppTheme">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <!-- Default window container settings -->
            <meta-data android:name="pico.spatial.windowcontainer.id"
                android:value="MyAppHome" />
            <meta-data android:name="pico.spatial.windowcontainer.style"
                android:value="1" />
            <meta-data android:name="pico.spatial.windowcontainer.size" 
                android:value="1080x720" />
            <meta-data
                android:name="pico.spatial.windowcontainer.defaultsize.unit"
                android:value="dp" />
            <meta-data
                android:name="pico.spatial.windowcontainer.materialbackground"
                android:value="0" />
        </activity>
        
        ...
        
  </application>
```

## Step 4: Use spatial capabilities
After completing the above steps, start the app. At this point, the app has switched from the original 2D compatibility mode to spatial app mode. You can use the components of PICO Spatial SDK in an `Activity` that supports spatial capabilities.

* If your app's interface is built with Jetpack Compose, you can use the components of PICO Spatial SDK in any `@Composable` function, such as `SpatialView`:
   ```Kotlin
   package com.example.myapp.screens
   
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.fillMaxSize
   import androidx.compose.runtime.Composable
   import androidx.compose.ui.Modifier
   import com.pico.spatial.core.ecs.Entity
   import com.pico.spatial.core.ecs.resource.AssetBundle
   import com.pico.spatial.core.ecs.resource.ResourceLoadingException
   import com.pico.spatial.ui.foundation.content.SpatialView
   import kotlinx.coroutines.Dispatchers
   import kotlinx.coroutines.withContext
   
   @Composable
   fun HomeScreen(modifier: Modifier) {
       Column(modifier = modifier.fillMaxSize()) {
           SpatialView { content, _ ->
               val entity = withContext(Dispatchers.IO) {
                   try {
                       Entity.load(modelName = "Hi", bundle = AssetBundle.load("asset://hi.bundle"))
                   } catch (e: ResourceLoadingException) {
                       null
                   }
               }
               entity?.let {
                   content.addEntity(it)
               }
           }
       }
   }
   ```

* If your app's interface is still built using traditional Android View, you can add `ComposeView` to the XML layout as the entry point for PICO Spatial SDK's components:
   ```XML
   <?xml version="1.0" encoding="utf-8"?>
   <androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:tools="http://schemas.android.com/tools"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       android:id="@+id/view_root"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       tools:context=".MainActivity">
       
       ...
       
       <androidx.compose.ui.platform.ComposeView
           android:id="@+id/compose_view"
           android:layout_width="match_parent"
           android:layout_height="wrap_content"/>
   
   </androidx.coordinatorlayout.widget.CoordinatorLayout>
   ```

   Then, in the corresponding `MainActivity`, add the required components to the `ComposeView`, for example, `SpatialView`:
   ```Kotlin
   
   class MainActivity : SpatialLaunchActivity() {
   
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           
           ...
           
           findViewById<ComposeView>(R.id.compose_view)?.let { it ->
               it.setContent {
                   SpatialView(
                       modifier = Modifier
                           .width(200.dp)
                           .height(200.dp)
                   ) { content, _ ->
                       val entity = withContext(Dispatchers.IO) {
                           Entity.load("asset://example.usdz")
                       }
                       
                       content.addEntity(it)
                   }
               }
           }
       }
   }
   ```


At this point, you have successfully ported your original Android app to the PICO OS 6.
## FAQs
If you encounter issues when porting your app, refer to the following FAQs and solutions. If your issue is not covered here, visit the official community for help, or submit a ticket by clicking the link below to contact the technical support team for further assistance.
[https://picodevsupport.freshdesk.com/support/home](https://picodevsupport.freshdesk.com/support/home)
### **Gradle version too low**
When you see any of the following error messages, it means that your Gradle version is too low. Upgrade your Gradle version as prompted.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4702d4d9435f4125bc9cee21bf097bd6~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ba9f4a4ef0784611b2118ffe03669ef1~tplv-goo7wpa0wc-image.image)


</div>
</div>

### **Kotlin version too low**
The following error message indicates that your current Kotlin version is too low and needs to be upgraded.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8230f0291276417990a536b348bfcefc~tplv-goo7wpa0wc-image.image" width="2228px" /></div>

### **BuildConfig generation issues**
The following error message indicates that BuildConfig is not enabled.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6612a6de7057469e99e5a4ea4c038bc0~tplv-goo7wpa0wc-image.image" width="1626px" /></div>

In AGP 8.0 and later versions, the `BuildConfig` class is no longer generated by default. This means that after upgrading to AGP 8.0 or later, if your project uses custom `buildConfigField` or relies on constants such as `BuildConfig.DEBUG`, compilation errors or warnings may occur if the BuildConfig feature is not explicitly enabled.
To resolve this issue, add the following configuration to the build.gradle file of the affected module:
```Groovy
android {
    ....
    
    buildFeatures {
        buildConfig true
    }
}
```

### **Namescape configuration issues**
The following error messages indicate that the namespace configuration of your module is incorrect.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e7743a33f53b4a028988ecb5e270db73~tplv-goo7wpa0wc-image.image" width="1904px" /></div>

In Gradle's build file, `namespace` is used to define the top-level package name for a module's Java or Kotlin source code, as well as the package name for the generated `R` class. It is closely related to the `package` property in the AndroidManifest.xml file. Starting from AGP 7.0, `namespace` is increasingly important in Gradle files, and it has gradually replaced the main role of the `package` property in the AndroidManifest.xml file.
To resolve related issues, you need to add the following content to the build.gradle file of the affected module:
```Groovy
android {
    ....
    
    defaultConfig {
        namespace "com.example.myapp"
        ...
    }
 
 } 
```

### **Cascading issues in R files**
Before AGP 8.0, Gradle would generate cascaded `R` classes by default. This means that if `ModuleA` depends on `ModuleB`, `ModuleA` can directly access all resources defined in `ModuleB` (for example, through `ModuleB.R.id.some_id` or directly `R.id.some_id`).
After upgrading to AGP 8.0 or later, if the project still uses the legacy  behavior of cascaded `R` class, compilation errors may occur to indicate that certain resources cannot be found.
To resolve this issue, it is necessary to modify the way the `R` file is referenced, so that each module's own resources are referenced separately. For example:
```Kotlin
import com.example.myapp.R
import com.example.myapp.core.R as coreR

textView = findViewById(R.id.text)
textView.text = getString(coreR.string.hello)
```

### **Component conflicts**
The following error message indicates that you have not removed the classes in Android Jetpack Compose that conflict with components of the PICO Spatial SDK. It is necessary to refer to the previous text and use `resolutionStrategy` to remove duplicate components.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b88bf3ab9a7f4a398bbe9fa3585d563a~tplv-goo7wpa0wc-image.image" width="2162px" /></div>

### **Spatial app crashes**
This issue typically occurs when using the SpatialUI component without enabling the Jetpack Compose Compiler, which causes code annotated with `@Composable` to be parsed incorrectly.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1e91d05ef3a14995bec357467ad25c64~tplv-goo7wpa0wc-image.image" width="1330px" /></div>

Ensure that the following configuration is included in the build.gradle file of the module that uses SpatialUI:
```Groovy
android {
    ....
    
    buildFeatures {
        compose true
    }
 
 } 
```

### **Google Play Services is not supported**
If your app uses Google Mobile Services (GMS) or Firebase-related features, you may encounter error messages similar to the following at runtime:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/539e5b95d8214cf2a62f3a25eb19b8ee~tplv-goo7wpa0wc-image.image" width="1252px" /></div>

Note that, currently, PICO OS 6 does not integrate GMS components and does not support Google Play Services. You need to find alternative solutions yourself to ensure the app can run properly.

