Controller tracking can obtain the controller's current pose in the scene. You can use these data to obtain the position of the user's controller and add a control panel to the user's wrist, or you can add collision spheres to the hand to enable interaction with the virtual environment.
## Recommended content
It is recommended to read the "[Use DataProvider](/en_instructions-on-data-provider)" to learn how to use `DataProvider` to obtain tracking data, determine data availability, and determine the state of `DataProvider`.
## Limitations
Controller tracking data can only be retrieved when the app is in Full Space state.
## Implementation procedure
Controller Tracking enables you to obtain the real-time pose data of controllers and apply it to an entity in the virtual scene, achieving synchronized motions between physical controllers and virtual controllers. The overall development procedure is as follows:

1. Create a `ControllerTrackingProvider` instance.
   ```Kotlin
   @Composable
   fun ControllerTrackingSample() {
       val controllerTrackingProvider = remember { ControllerTrackingProvider() }
       // ...
   }
   ```

2. Call `start()` to start `ControllerTrackingProvider`, and call `stop()` when it is no longer needed.
   ```Kotlin
   @Composable
   fun ControllerTrackingSample() {
       // ...
       DisposableEffect(controllerTrackingProvider) {
           controllerTrackingProvider.start()
           onDispose { controllerTrackingProvider.stop() }
       }
       //...
   }
   ```

3. Use `dataFlow` to retrieve the pose data of the controller.
   You can choose different methods to retrieve data based on the specific scenario. For example, in a Composable function, you can use `dataFlow` to retrieve data; in ECS, you can use `latestData` to retrieve the latest data.

   ```Kotlin
   @Composable
   fun ControllerTrackingSample() {
       // ...
   val controllerTrackingData by
       controllerTrackingProvider.dataFlow.collectAsState(
           initial = ControllerTrackingData(null, null, 0L)
       )
       // ...
   }
   ```

4. Read the pose data of the left controller, convert the data's coordinate system, and set the result to `Entity`.
   ```Kotlin
   @Composable
   fun ControllerTrackingSample() {
       // ...
       val rootEntity: Entity = remember { Entity() }
       val leftEntity: Entity = remember { Entity() }
       SpatialView(
           update = { _, _ ->
               controllerTrackingData.left?.let { left ->
                   val transformComponent = leftEntity.components[TransformComponent::class.java]
                   transformComponent?.apply {
                       val position = rootEntity.convertPositionFrom(left.position, null)
                       val rotation = rootEntity.convertRotationFrom(left.rotation, null)
                       setPosition(position)
                       setQuaternion(rotation)
                   }
               }
           }
       ) { content, _ ->
           rootEntity.addChild(leftEntity)
           content.addEntity(rootEntity)
       }
       // ...
   }
   ```


## Complete code sample
The following code demonstrates how to set the real position of the physical left controller to `leftEntity` in the virtual scene, so that the position of `leftEntity` stays synchronized with that of the physical left controller.
```Kotlin
@Composable
fun ControllerTrackingSample() {
    // Create a ControllerTrackingProvider
    val controllerTrackingProvider = remember { ControllerTrackingProvider() }

    // Get real-time tracking data from dataFlow
    val controllerTrackingData by
        controllerTrackingProvider.dataFlow.collectAsState(
            initial = ControllerTrackingData(null, null, 0L)
        )

    // Use tracking data within the Composable's lifecycle
    DisposableEffect(controllerTrackingProvider) {
        controllerTrackingProvider.start()
        onDispose { controllerTrackingProvider.stop() }
    }

    // Create two entities in the scene: the root node and the left controller node
    val rootEntity: Entity = remember { Entity() }
    val leftEntity: Entity = remember { Entity() }

    SpatialView(
        modifier = Modifier.size(1.dp),
        update = { _, _ ->
            controllerTrackingData.left?.let { left ->
                val transformComponent = leftEntity.components[TransformComponent::class.java]
                transformComponent?.apply {
                    // Convert the tracking data to the coordinate system of the root node and set it to the left controller Entity
                    val position = rootEntity.convertPositionFrom(left.position, null)
                    val rotation = rootEntity.convertRotationFrom(left.rotation, null)
                    setPosition(position)
                    setQuaternion(rotation)
                }
            }
        }
    ) { content, _ ->
        rootEntity.addChild(leftEntity)
        content.addEntity(rootEntity)
    }
}
```

## API reference
The `ControllerTrackingProvider` class provides Controller Tracking-related interfaces. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

