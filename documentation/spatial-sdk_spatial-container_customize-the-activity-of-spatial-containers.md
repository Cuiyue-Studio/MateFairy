Within a spatial container, Android Activity serves as the UI carrier. In most cases, you do not need to worry about this detail, because the SpatialUI and Compose development ecosystem already supports the vast majority of logic development within the Compose context.
To meet the needs of traditional Android developers, PICO Spatial SDK provides the capability to customize the Activity of spatial containers. You can handle business logic such as permission requests and dependency injection in an Activity. When the spatial container starts, it loads the Activity you specified.
## Procedure
### Step 1: Customize the Activity
You can customize Activity in the following two ways:

* Inherit from `SpatialStubActvity`. This approach is simpler.
   ```Kotlin
   // Inherit from SpatialStubActivity; when overriding a method, you must first call `Super` method.
   class MyActivity: SpatialStubActivity() {
   }
   ```

* Use `SpatialActivityDelegate` to convert a standard Activity into a SpatialActivity. When using this approach, you must call relevant code in accordance with the specification.
   ```Kotlin
   // Standard Activity: Based on the requirements of Compose, its superclass should be ComponentActivity or a subclass of it.
   // Parent Activity: Can be ComponentActivity or any subclass (such as FragmentActivity).
   class MyActivity : androidx.activity.ComponentActivity() {
       // Define SpatialActivityDelegate, then call its methods in the corresponding lifecycle. None of these can be omitted.
       private val spatialDelegate: SpatialActivityDelegate by spatialActivityDelegate()
       // You can also use SpatialActivityDelegate.newInstance(this)
   
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           // This is similar to Activity's setContentView or Compose's setContent.
           // Caution: When customizing SpatialActivity, do not call setContentView or similar methods, as this will prevent SpatialUI from loading correctly.
           spatialDelegate.setSpatialContent()
       }
   }
   ```


### Step 2: Register the Activity in the Manifest
Register the custom Activity in the AndroidManifest.xml file, as follows:
```XML
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application>
        ...
        <activity android:name=".activities.MyActivity"/>
        ...
    </application>
</manifest>
```

### Step 3: Specify the Activity of spatial containers in the DSL
In the DSL, specify the Activity of spatial containers to control its UI management and business logic.
Different spatial containers can use the same Activity.

Code sample:
```Kotlin
fun SpatialAppScope.mainApp() {
    // Register an Activity for WindowContainer 
    WindowContainer(
        id = "MyWindowContainer",
        targetActivity = MyActivity::class.java,
    ) {
        Content()
    }
    
    // Register an Activity for Stage
    Stage(
        id = StageWithCustomActivity,
        immersion = Immersion.Default,
        targetActivity = MyActivity::class.java
    ) {
         Content()
    }
}
```

### Step 4: Check whether the custom Activity has taken effect
Retrieve the Context through Compose's `LocalContext.current`, start the corresponding spatial container, and then confirm whether the custom Activity has been loaded.
```Kotlin
@Composable
fun Content() {
    val context = LocalContext.current
    Text("actvity: $context")
}
```

## Cautions

* For custom Activity, do not use `Activity.setContentView()`, `ComponentActivity.setContent()`, or similar methods to set 2D UI. You should use `SpatialActivityDelegate` to act as a delegate for the content of the Activity.
* Do not configure the `launchMode` property for a custom Activity in the AndroidManifest.xml file, or the app's behavior may not meet expectations.
   ```Kotlin
   <activity android:name=".activities.MyActivity"
       android:launchMode="xxx"/>
   ```



