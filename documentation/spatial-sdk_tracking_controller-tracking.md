Through `ControllerTrackingProvider`, you can obtain the controller's pose (position and orientation) and Input actions (buttons, thumbstick, trigger, and more) in the physical world, enabling rich and precise interactions in virtual environments.
You can use this `DataProvider` to obtain the controller's pose or controller Input actions:

* **Obtain controller pose**: Use `dataFlow` to acquire real-time 6DoF pose data (`ControllerTrackingData`) for the controller in the form of a data stream. This is suitable for scenarios where virtual objects (such as virtual panels or weapons) need to remain position-synchronized with the physical controller.
* **Obtain controller Input actions**: By registering a `ControllerActionListener`, you can get a complete snapshot of the controller's button, thumbstick, trigger, and other Input states for each frame in the `onControllerAction()` callback (`ControllerActionData`). This is suitable for the following scenarios: reading Input states for A/B/X/Y, Trigger, Grip, Thumbstick in real time; implementing two-handed interactions based on dual-hand Input (such as two-handed scaling or grabbing); implementing filtering, debounce, or hand pose recognition based on "per-frame state snapshots".

## Recommended reading
It is recommended to read "[DataProvider usage instructions](/instructions-on-data-provider)" to learn how to use `DataProvider` to obtain tracking data, determine data availability, and check the status of `DataProvider`.
## Limitations
All capabilities provided by `ControllerTrackingProvider` (including pose and Input action callbacks) are only available when the application's mode is **Full Space**.
It is recommended to check the value of `provider.supportState` before or after starting tracking to confirm whether the current conditions are met for operation, and to provide appropriate UI prompts or fallback logic based on different states. Common values include:

* `SUPPORTED`: Data can be provided normally.
* `DEVICE_NOT_SUPPORTED`: Device or connection conditions are currently not met (for example, the controller is not connected), and recovery may occur automatically later.
* `WITHOUT_PERMISSION`: Permission is missing and can be restored after authorization.
* `NOT_IN_FULL_SPACE`: Not in Full Space; recovery is possible after entering Full Space.

## Prerequisites

* Add build dependencies (it is recommended to use the version catalog file [libs.versions.toml](https://developer.android.com/build/dependencies?hl=zh-cn#add-dependency)).
   * Add the following content to the `[libraries]` section of `libs.versions.toml`:
      ```TOML
      [libraries]
      // ...
      spatial-tracking = { group = "com.pico.spatial.tracking", name = "tracking" }
      ```

   * Add the following content to the `dependencies {}` section of the module's build script file `build.gradle.kts`:
      ```Kotlin
      dependencies {
          // ...
          implementation("com.pico.spatial.tracking:tracking")
      }
      ```


## Development process
`ControllerTrackingProvider` offers two main ways to obtain data: acquiring the controller's pose via `dataFlow`, and obtaining controller Input actions via `ControllerActionListener`. You can choose either method or use both according to your requirements.
### Obtain controller pose
You can subscribe to `dataFlow` to acquire real-time pose data for the controller, suitable for scenarios where virtual objects need to be position-synchronized with the physical controller.

1. Create an instance of `ControllerTrackingProvider` in the Composable function.
   ```Kotlin
   @Composable
   fun ControllerTrackingSample() {
       val controllerTrackingProvider = remember { ControllerTrackingProvider() }
       // ...
   }
   ```

2. Use `DisposableEffect` to call `start()` when the Composable enters, and call `stop()` when it exits.
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

3. Use `collectAsState` to convert `dataFlow` into State, allowing the Composable to respond to data changes.
   You can choose different ways to obtain data based on the specific scenario. Here, in the Composable function, you can use `dataFlow` to obtain data; in ECS, you can use `latestData` to get the latest data.

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

4. In the `update` callback of `SpatialView`, read data and update the pose of the `Entity` in the scene.
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


### Obtain controller input actions
You can receive a snapshot of the controller input state for each frame by registering a `ControllerActionListener`. This is the recommended method for handling interactions such as buttons, thumbsticks, triggers, and more.
Refer to the following recommendations when handling controller input:

* **Detect key events**: To detect key "down" and "up" events, you can cache the previous frame's `ControllerActionData` and compare it with the current frame's data.
* **Avoid per-frame logging**: To prevent performance issues, avoid outputting logs on every frame.
* **UI refresh throttling**: When debugging the interface, it is recommended to throttle refresh operations, for example, updating every 50–100 milliseconds or only when values change.


1. Create a `ControllerTrackingProvider` instance.
   ```Kotlin
   val provider = ControllerTrackingProvider()
   ```

2. Create a `ControllerActionListener` instance, then call `start()` to start the `ControllerTrackingProvider` instance.
   * `start()` is used to begin receiving data from the underlying data source. If the current conditions are not met, `start()` may return `StartResult.PENDING`; when the conditions are met, data will be provided automatically.
   * The `onControllerAction()` callback is triggered on the underlying data source thread, with a frequency close to the device's output rate. Therefore, do not perform time-consuming operations in the callback (such as blocking IO, extensive logging, or complex calculations). To update the UI, or invoke logic that is restricted to the main thread, first copy/cache the data in the callback, then switch back to the main thread for processing.

   ```Kotlin
   val listener = ControllerTrackingProvider.ControllerActionListener { actionData ->
       // actionData.left / actionData.right
   }
   
   provider.addControllerActionListener(listener)
   provider.start()
   ```

3. When you no longer need to obtain controller input actions, remove the `ControllerActionListener` instance and stop the `ControllerTrackingProvider` instance.
   ```Kotlin
   provider.removeControllerActionListener(listener)
   provider.stop()
   ```


## Data structure and field description
### Controller pose
#### ControllerTrackingData
`ControllerTrackingData` represents the tracking data for both controllers.

* `left: ControllerPose`: Pose of the left controller.
* `right: ControllerPose`: Pose of the right controller.

#### ControllerPose
`ControllerPose` represents the pose of the controller in the world coordinate system.

* `position: Vector3`: Position of the controller.
* `rotation: Quat`: Rotation direction of the controller.

### Controller input actions
#### ControllerActionData
`ControllerActionData` represents a snapshot of the input state for both controllers in the same frame and includes the following fields:

* `left: ControllerAction`: Input state of the left controller (X/Y, Trigger, Grip, Thumbstick).
* `right: ControllerAction`: Input state of the right controller (A/B, Trigger, Grip, Thumbstick).

You can use this "same-frame snapshot" to process input from both controllers simultaneously, simplifying dual-hand interaction logic.
#### ControllerAction
`ControllerAction` includes input information for buttons, triggers, grips, and thumbsticks. The button names for the left and right controllers are different: the left controller typically uses X/Y, while the right controller typically uses A/B. The table below lists the main fields and their descriptions:
| **Input items** | **Left controller field (actionData.left)** | **Right controller field (actionData.right)** | **Note** |
| --- | --- | --- | --- |
| Main button 1 (Press) | `xButtonPressed: Boolean` | `aButtonPressed: Boolean` | `true` indicates that the button is currently physically pressed. |
| Main button 2 (Press) | `yButtonPressed: Boolean` | `bButtonPressed: Boolean` |  |
| Main button 1 (Touch) | `xButtonTouched: Boolean` | `aButtonTouched: Boolean` | `true` indicates that a finger is touching the button surface (supported on some devices). |
| Main button 2 (Touch) | `yButtonTouched: Boolean` | `bButtonTouched: Boolean` |  |
| Trigger | `triggerPressed: Boolean` <br> `triggerTouched: Boolean` <br> `triggerValue: Float` | `triggerPressed: Boolean` <br> `triggerTouched: Boolean` <br> `triggerValue: Float` | `triggerValue` is an analog value in the range [0, 1], indicating the degree to which the trigger is pressed. |
| Grip | `gripPressed: Boolean` <br> `gripValue: Float` | `gripPressed: Boolean` <br> `gripValue: Float` | `gripValue` is an analog value in the range [0, 1], indicating the degree to which the grip button is held. |
| Thumbstick | `thumbstickPressed: Boolean` <br> `thumbstickTouched: Boolean` <br> `thumbstickValue: ThumbstickValue` | `thumbstickPressed: Boolean` <br> `thumbstickTouched: Boolean` <br> `thumbstickValue: ThumbstickValue` | `thumbstickValue` contains two floating-point numbers, `x` and `y`, both in the range [-1, 1], representing the thumbstick's deviation. |
#### ThumbstickValue
`ThumbstickValue` represents the two-dimensional value of the thumbstick and includes the following fields:

* `x: Float` (horizontal axis, range [-1, 1])
* `y: Float` (vertical axis, range [-1, 1])

The coordinate direction may vary depending on the device or runtime (for example, pushing up may be positive or negative). It is recommended to first verify the actual direction represented by the two-dimensional value of the thumbstick before using it for movement or turning logic.
## Complete code example
### Obtain controller pose
The following code demonstrates how to set the real position of the left controller onto the `leftEntity` in the virtual scene, keeping `leftEntity` synchronized with the real left controller's position.
```Kotlin
@Composable
fun ControllerTrackingSample() {
    // Create ControllerTrackingProvider
    val controllerTrackingProvider = remember { ControllerTrackingProvider() }

    // Obtain real-time tracking data from dataFlow
    val controllerTrackingData by
        controllerTrackingProvider.dataFlow.collectAsState(
            initial = ControllerTrackingData(null, null, 0L)
        )

    // Use tracking data within the Composable lifecycle
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
                    // Convert the tracking data to the root node's coordinate system and assign it to the left controller entity
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

### Obtain controller input actions
#### Non-Compose usage
The following example covers the complete development process: creating a `ControllerTrackingProvider`, registering a `ControllerActionListener`, starting `ControllerTrackingProvider`, processing data (including edge detection for button presses), and finally removing the `ControllerActionListener` and stopping `ControllerTrackingProvider`.
```Kotlin
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.pico.spatial.tracking.DataProvider
import com.pico.spatial.tracking.controller.ControllerActionData
import com.pico.spatial.tracking.controller.ControllerTrackingProvider

class GameActivity : AppCompatActivity() {

    private val provider = ControllerTrackingProvider()
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var lastAction: ControllerActionData? = null

    private val actionListener =
        ControllerTrackingProvider.ControllerActionListener { action ->
            // Caution: The callback thread is not the main thread and runs at a high frequency; do not block

            // Example: Detect the 'down edge' of the right controller A button
            val prev = lastAction
            val wasAPressed = prev?.right?.aButtonPressed ?: false
            val isAPressed = action.right.aButtonPressed
            if (!wasAPressed && isAPressed) {
                // A button down: Only perform lightweight logic (send messages/enqueue), avoid heavy computation
            }

            lastAction = action

            // To update the UI, switch back to the main thread
            mainHandler.post {
                val trigger = action.right.triggerValue
                // triggerValueTextView.text = "%.2f".format(trigger)
            }
        }

    override fun onStart() {
        super.onStart()

        // Optional: Pre-launch checks (for prompts/fallback)
        when (provider.supportState) {
            DataProvider.SupportState.SUPPORTED -> Unit
            DataProvider.SupportState.NOT_IN_FULL_SPACE -> {
                //TODO:Prompt the user to enter Full Space (immersive mode/corresponding Stage)
            }
            DataProvider.SupportState.WITHOUT_PERMISSION -> {
                //TODO:Request the necessary permissions
            }
            DataProvider.SupportState.DEVICE_NOT_SUPPORTED -> {
                //TODO:Allow automatic recovery later (for example, waiting for controller connection)
            }
            DataProvider.SupportState.NONE -> Unit
        }

        provider.addControllerActionListener(actionListener)
        provider.start()
    }

    override fun onStop() {
        provider.removeControllerActionListener(actionListener)
        provider.stop()
        super.onStop()
    }
}
```

#### Compose usage
This example uses `remember` to register `ControllerActionListener`, ensuring that its instance remains stable during recomposition of the Composable function. The callback function executes on a background thread. Therefore, you must use `Handler` to switch back to the main thread in order to safely update the UI state created by `mutableStateOf`.
```Kotlin
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pico.spatial.tracking.controller.ControllerActionData
import com.pico.spatial.tracking.controller.ControllerTrackingProvider

@Composable
fun ControllerActionPanel() {
    val provider = remember { ControllerTrackingProvider() }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var latestAction: ControllerActionData? by remember { mutableStateOf(null) }

    val listener = remember {
        ControllerTrackingProvider.ControllerActionListener { action ->
            mainHandler.post { latestAction = action }
        }
    }

    DisposableEffect(provider) {
        provider.addControllerActionListener(listener)
        provider.start()

        onDispose {
            provider.removeControllerActionListener(listener)
            provider.stop()
        }
    }

    val left = latestAction?.left
    val right = latestAction?.right

    Column {
        Text("Left: X=${left?.xButtonPressed ?: false}, Y=${left?.yButtonPressed ?: false}")
        Text("Right: A=${right?.aButtonPressed ?: false}, B=${right?.bButtonPressed ?: false}")

        Row {
            Text("R Trigger=${"%.2f".format(right?.triggerValue ?: 0f)}  ")
            Text("R Grip=${"%.2f".format(right?.gripValue ?: 0f)}")
        }

        Row {
            val x = right?.thumbstickValue?.x ?: 0f
            val y = right?.thumbstickValue?.y ?: 0f
            Text("R Stick x=${"%.2f".format(x)} y=${"%.2f".format(y)}")
        }
    }
}
```

## API reference
The `ControllerTrackingProvider` class provides interfaces related to controller tracking. For details, see [API reference](https://developer.picoxr.com/spatial-api/index.html).
