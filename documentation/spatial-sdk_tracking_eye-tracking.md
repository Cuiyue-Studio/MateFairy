The Eye Tracking functionality enables real-time acquisition of the user's current gaze position and gaze direction within the app.
## Recommended content
It is recommended to read the article "[Use DataProvider](/instructions-on-data-provider)" to learn how to use `DataProvider` to retrieve tracking data, determine data availability, and understand the status of `DataProvider`.
## Limitations
Eye tracking data can only be retrieved when the app's mode is Full Space.
## Implement the Eye Tracking functionality
### Request eye tracking permission
You must request permission before using the Eye Tracking functionality. The application can access gaze position and direction data only after the user has explicitly granted authorization.
The eye tracking permission is `com.picovr.permission.EYE_TRACKING`. This permission is a runtime permission and must be requested dynamically during app execution. For the complete process of requesting and handling runtime permissions, refer to the [Android Developers official documentation](https://developer.android.com/training/permissions/requesting).
Code sample:
```Kotlin
if (
    ContextCompat.checkSelfPermission(activity!!, "com.picovr.permission.EYE_TRACKING") !=
        PackageManager.PERMISSION_GRANTED
) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf("com.picovr.permission.EYE_TRACKING"),
        YOUR_REQUEST_CODE,
    )
}
```

### Use eye tracking data

1. Create an `EyeTrackingProvider` instance.
   ```Kotlin
   @Composable
   fun EyeTrackingSample() {
       val eyeTrackingProvider = remember { EyeTrackingProvider() }
       // ...
   }
   ```

2. Call `start()` to start `EyeTrackingProvider`, and call `stop()` when it is no longer needed.
   ```Kotlin
   @Composable
   fun EyeTrackingSample() {
       // ...
       DisposableEffect(EyeTrackingProvider) {
           eyeTrackingProvider.start()
           onDispose { eyeTrackingProvider.stop() }
       }
       // ...
   }
   ```

3. Use `dataFlow` to retrieve eye tracking data.
   You can choose different methods to retrieve data based on the specific scenario. For example, in a Composable function, you can use `dataFlow` to retrieve data; in ECS, you can use `latestData` to retrieve the latest data.

   ```Kotlin
   @Composable
   fun EyeTrackingSample() {
       // ...
       val EyeTrackingData by
           eyeTrackingProvider.dataFlow.collectAsState(
               initial = EyeTrackingData(EyePose(Vector3.ZERO, Quat.identity()), 0L))
       // ...
   }
   ```

4. Read the position and direction data of the gaze, and then convert the coordinate system of the data.
   ```Kotlin
   @Composable
   fun EyeTrackingSample() {
       // ...
       val position =
           rootEntity.convertPositionFrom(eyeTrackingData.eyePose.position, null)
       val rotation =
           rootEntity.convertRotationFrom(eyeTrackingData.eyePose.rotation, null)
       // ...
   }
   ```


## Demo
Draw a blue sphere 0.2 meters in front of the user's current gaze direction, and make it move and rotate in real time with the gaze.
```Kotlin
@Composable
fun EyeTrackingSample() {
    // Create eye tracking Provider and subscribe to gaze data
    val eyeTrackingProvider = remember { EyeTrackingProvider() }
    val eyeTrackingData by
        eyeTrackingProvider.dataFlow.collectAsState(
            EyeTrackingData(EyePose(Vector3.ZERO, Quat.identity()), 0L)
        )
    // Start eye tracking and stop it when the component is destroyed
    DisposableEffect(Unit) {
        eyeTrackingProvider.start()
        onDispose {
            eyeTrackingProvider.stop()
        }
    }
    // Create a blue sphere (for visualizing gaze direction)
    val mesh = remember { MeshResource.createSphere(0.01f) }
    val material = remember { UnlitMaterial.create().apply { setBaseColor(Color4.BLUE) } }
    val rootEntity = remember { Entity() }
    val eyeModel = remember {
        Entity().apply {
            val ball =
                Entity().apply {
                    components.set(ModelComponent(mesh, material))
                    // Place the sphere 0.2m in front in the local coordinate system
                    components.get<TransformComponent>()?.apply {
                        setPosition(Vector3(0f, 0f, -0.2f))
                    }
                }
            addChild(ball)
            rootEntity.addChild(this)
        }
    }
    SpatialView(
        update = { _, _ ->
            eyeModel.components.get<TransformComponent>()?.apply {
                // Convert gaze pose to scene coordinate system
                val position =
                    rootEntity.convertPositionFrom(eyeTrackingData.eyePose.position, null)
                val rotation =
                    rootEntity.convertRotationFrom(eyeTrackingData.eyePose.rotation, null)
                // Update model pose so that the sphere always stays 0.2m in front of the gaze
                setPosition(position)
                setQuaternion(rotation)
            }
        }
    ) { content, _ ->
        content.addEntity(rootEntity)
    }
}
```

