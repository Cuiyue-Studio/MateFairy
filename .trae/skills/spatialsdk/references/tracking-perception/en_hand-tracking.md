Hand Tracking retrieves the user's hand pose information, including the position and rotation of the palm, wrist, and each finger joint. You can use this data to identify the user's current hand pose, and enable interactions with objects in the virtual scene based on the hand pose or hand position.
## Recommended content
It is recommended to read the "[Use DataProvider](/en_instructions-on-data-provider)" to learn how to use `DataProvider` to obtain tracking data, determine data availability, and determine the state of `DataProvider`.
## Limitations
Hand tracking data can only be retrieved when the app is in Full Space state.
## Implementation procedure
Hand Tracking enables you to obtain the real-time pose data of the hand and apply it to an entity in the virtual scene, achieving synchronized motions between the read hand and the virtual hand. The overall development procedure is as follows:

1. Create a `HandTrackingProvider` instance.
   ```Kotlin
   @Composable
   fun HandTrackingSample() {
       val handTrackingProvider = remember { HandTrackingProvider() }
       // ...
   }
   ```

2. Call `start()` to start `HandTrackingProvider`, and call `stop()` when it is no longer needed.
   ```Kotlin
   @Composable
   fun HandTrackingSample() {
       // ...
       DisposableEffect(handTrackingProvider) {
           handTrackingProvider.start()
           onDispose { handTrackingProvider.stop() }
       }
       // ...
   }
   ```

3. Use `dataFlow` to retrieve the pose data of the hand.
   You can choose different methods to retrieve data based on the specific scenario. For example, in a Composable function, you can use `dataFlow` to retrieve data; in ECS, you can use `latestData` to retrieve the latest data.

   ```Kotlin
   @Composable
   fun HandTrackingSample() {
       // ...
       val handTrackingData by
           handTrackingProvider.dataFlow.collectAsState(initial = HandTrackingData(null, null, 0L))
       // ...
   }
   ```

4. Read the pose data of the right index fingertip, convert the data's coordinate system, and set the result to `Entity`.
   ```Kotlin
   @Composable
   fun HandTrackingSample() {
       // ...
       val rootEntity: Entity = remember { Entity() }
       val rightIndexTipEntity: Entity = remember { Entity() }
       
       SpatialView(
           update = { _, _ ->
               handTrackingData.right?.let { right ->
                   val indexTipJoint = right[Index.INDEX_TIP]
                   val transformComponent = rightIndexTipEntity.components[TransformComponent::class.java]
                   transformComponent?.apply {
                       val position = rootEntity.convertPositionFrom(indexTipJoint.position, null)
                       val rotation = rootEntity.convertRotationFrom(indexTipJoint.rotation, null)
                       setPosition(position)
                       setQuaternion(rotation)
                   }
               }
           }
       ) { content, _ ->
           rootEntity.addChild(rightIndexTipEntity)
           content.addEntity(rootEntity)
       }
       // ...
   }
   ```


## A complete code example
The following code demonstrates how to set the real-time tracking data of the right index fingertip to `rightIndexTipEntity` in the virtual scene, allowing `rightIndexTipEntity` to move and rotate in sync with the real fingertip during each frame of rendering.
```Kotlin
@Composable
fun HandTrackingSample() {
    // Create a HandTrackingProvider
    val handTrackingProvider = remember { HandTrackingProvider() }

    // Retrieve real-time tracking data from dataFlow
    val handTrackingData by
        handTrackingProvider.dataFlow.collectAsState(initial = HandTrackingData(null, null, 0L))

    // Using tracking data within the Composable's lifecycle
    DisposableEffect(handTrackingProvider) {
        handTrackingProvider.start()
        onDispose { handTrackingProvider.stop() }
    }
    
    // Create two entities in the scene: the root node and the right index fingertip node
    val rootEntity: Entity = remember { Entity() }
    val rightIndexTipEntity: Entity = remember { Entity() }

    SpatialView(
        update = { _, _ ->
            handTrackingData.right?.let { right ->
                // Get data for the right index fingertip joint
                val indexTipJoint = right[Index.INDEX_TIP]
                val transformComponent = rightIndexTipEntity.components[TransformComponent::class.java]
                transformComponent?.apply {
                    // Convert the tracking data to the coordinate system of the root node and set it to the right index fingertip node
                    val position = rootEntity.convertPositionFrom(indexTipJoint.position, null)
                    val rotation = rootEntity.convertRotationFrom(indexTipJoint.rotation, null)
                    setPosition(position)
                    setQuaternion(rotation)
                }
            }
        }
    ) { content, _ ->
        rootEntity.addChild(rightIndexTipEntity)
        content.addEntity(rootEntity)
    }
}
```

## Hand joint reference
The Hand Tracking feature supports tracking 26 joints on two hands, as shown in the figure below:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7486fe5d2ae346baac31535dc1cbfa3f~tplv-goo7wpa0wc-image.image)
## API reference
The `HandTrackingProvider` class provides Hand Tracking-related interfaces. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
