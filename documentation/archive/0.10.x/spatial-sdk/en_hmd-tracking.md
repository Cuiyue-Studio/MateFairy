HMD Tracking retrieves the current pose information of the HMD in the scene. You can use this data to determine the position and orientation of the user's viewport, or to create a HUD that follows the user's field of view.
## Recommended content
It is recommended to read the "[Use DataProvider](/en_instructions-on-data-provider)" to learn how to use `DataProvider` to obtain tracking data, determine data availability, and determine the state of `DataProvider`.
## Limitations
HMD tracking data can only be retrieved when the app is in Full Space state.
## Implementation procedure
HMD Tracking enables you to obtain the real-time pose data of the HMD and apply it to an entity in the virtual scene, achieving synchronized motions between the physical HMD and the virtual HMD. The overall development procedure is as follows:

1. Create an `HMDTrackingProvider()` instance.
   ```Kotlin
   @Composable
   fun HMDTrackingSample() {
       // ...
       val hmdTrackingProvider = remember { HMDTrackingProvider() }
       // ...
   }
   ```

2. Call `start()` to start `HMDTrackingProvider`, and call `stop()` when it is no longer needed.
   ```Kotlin
   @Composable
   fun HMDTrackingSample() {
       // ...
       DisposableEffect(hmdTrackingProvider) {
           hmdTrackingProvider.start()
           onDispose { hmdTrackingProvider.stop() }
       }
       // ...
   }
   ```

3. Use `dataFlow` to retrieve the pose data of the HMD.
   You can choose different methods to retrieve data based on the specific scenario. For example, in a Composable function, you can use `dataFlow` to retrieve data; in ECS, you can use `latestData` to retrieve the latest data.

   ```Kotlin
   @Composable
   fun HMDTrackingSample() {
       // ...
       val hmdTrackingData by
           hmdTrackingProvider.dataFlow.collectAsState(
               initial = HMDTrackingData(HMDPose(Vector3.ZERO, Quat.identity()), 0L)
           )
       // ...
   }    
   ```

4. Read the pose data of the HMD, convert the data's coordinate system, and set the result to `Entity`.
   ```Kotlin
   @Composable
   fun HMDTrackingSample() {
       // ...
       val rootEntity: Entity = remember { Entity() }
       val hmdEntity: Entity = remember { Entity() }
       SpatialView(
           update = { _, _ ->
               hmdTrackingData.hmdPose.let {
                   val transformComponent = hmdEntity.components[TransformComponent::class.java]
                   transformComponent?.apply {
                       val position =
                           rootEntity.convertPositionFrom(it.position, null)
                       val rotation = 
                           rootEntity.convertRotationFrom(it.rotation, null)
                       setPosition(position)
                       setQuaternion(rotation)
                   }
               }
           }
       ) { content, _ ->
           rootEntity.addChild(hmdEntity)
           content.addEntity(rootEntity)
       }
       // ...
   }
   ```


## Complete code sample
The following code demonstrates how to set the real HMD's position to the `hmdEntity` in the virtual scene, so that the `hmdEntity`'s position and the real HMD's position remain synchronized.
```Kotlin
@Composable
fun HMDTrackingSample() {
    // Create an HMDTrackingProvider
    val hmdTrackingProvider = remember { HMDTrackingProvider() }

    // Retrieve real-time tracking data from dataFlow
    val hmdTrackingData by
        hmdTrackingProvider.dataFlow.collectAsState(
            initial = HMDTrackingData(HMDPose(Vector3.ZERO, Quat.identity()), 0L)
        )

    // Use tracking data within the Composable's lifecycle
    DisposableEffect(hmdTrackingProvider) {
        hmdTrackingProvider.start()
        onDispose { hmdTrackingProvider.stop() }
    }

    // Create two entities in the scene: the root node and the HMD node
    val rootEntity: Entity = remember { Entity() }
    val hmdEntity: Entity = remember { Entity() }
    SpatialView(
        update = { _, _ ->
            hmdTrackingData.hmdPose.let {
                val transformComponent = hmdEntity.components[TransformComponent::class.java]
                transformComponent?.apply {
                    // Convert the tracking data to the coordinate system of the root node and set it to the HMD entity
                    val position =
                        rootEntity.convertPositionFrom(it.position, null)
                    val rotation = 
                        rootEntity.convertRotationFrom(it.rotation, null)
                    setPosition(position)
                    setQuaternion(rotation)
                }
            }
        }
    ) { content, _ ->
        rootEntity.addChild(hmdEntity)
        content.addEntity(rootEntity)
    }
}
```

## API reference
The `HMDTrackingProvider` class provides HMD Tracking-related interfaces. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

