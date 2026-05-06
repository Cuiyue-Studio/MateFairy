You can manage the lifecycle and state of a WindowContainer, enabling you to better control content display, user interaction, and resource usage in spatial apps.
## Manage a WindowContainer's lifecycle
You can directly use `androidx.lifecycle` to observe the lifecycle of a WindowContainer. There is the following correspondence between the lifecycle of a WindowContainer and `androidx.lifecycle`:
| **Lifecycle.Event** | **WindowContainer's Lifecycle** |
| --- | --- |
| ON_CREATE | A WindowContainer has been created, but is not yet visible to the user. <br> The event is triggered first when a WindowContainer is opened for the first time. If you need to trigger this event again, you must close the WindowContainer and then reopen it. |
| ON_START | A WindowContainer has started and is visible to the user, but has not yet entered the foreground for interaction. <br> This event is triggered when a WindowContainer is opened. If the WindowContainer is opened for the first time, this event is triggered immediately after the `ON_CREATE` event. If the WindowContainer is reopened from the background, this event is triggered first. |
| ON_RESUME | A WindowContainer enters the foreground for interaction. <br> This event occurs immediately after the `ON_START` event and is triggered when a WindowContainer opens. It is commonly used for operations that require user interaction, such as starling camera preview, starting position updates, resuming audio and video playback, and so on. |
| ON_PAUSE | A WindowContainer is still visible but has lost focus. <br> This event is triggered under the following two circumstances: <br>  <br> * When the user clicks the minimize button on the title bar, the WindowContainer is about to be sent to the background. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6b35bae8c40c4c55bbed89f5f10ed173~tplv-goo7wpa0wc-image.image) <br> * When the user clicks the close button on the title bar, the WindowContainer is about to be closed. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8ecf542f2dde4ba6bbfdb342b5d4e678~tplv-goo7wpa0wc-image.image) <br>  <br> Commonly used to pause-related interactive operations, such as pausing animations, pausing audio and video playback, saving temporary UI states, and more. |
| ON_STOP | A WindowContainer is already completely invisible. <br> This event occurs immediately after the `ON_PAUSE` event and is triggered when a WindowContainer is about to be sent to the background or closed. It is commonly used for releasing resources, such as unregistering listeners, stopping tasks that only need to run when the interface is visible, and more. |
| ON_DESTROY | A WindowContainer is about to be destroyed. <br> When the user clicks the close button on the title bar, the `ON_PAUSE` and `ON_STOP` events are triggered in sequence, followed by the `ON_DESTROY` event. |
Typically, the event triggering conditions for each phase of the WindowContainer's lifecycle are as follows.
```Plain Text
[Launch an app]
ON_CREATE → ON_START → ON_RESUME

[The user clicks the minimize button to bring a WindowContainer to the background]
ON_PAUSE → ON_STOP

[The user clicks the app's icon to reopen a WindowContainer]
ON_START → ON_RESUME

[The user clicks the close button; or the WindowContainer is closed in other ways, such as by using the closeWindowContainer function]
ON_PAUSE → ON_STOP → ON_DESTROY
```

The following code demonstrates how to use the`Composable` method to listen to the lifecycle of a WindowContainer that contains this `Composable`.
```Kotlin
@Composable
fun LifecycleExample() {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = Unit) {
        val observer = LifecycleEventObserver { _, event ->
            Log.i("LifecycleExample", "onLifecycleEvent: $event")
            if (event == Lifecycle.Event.ON_CREATE) {
            // execute custom logic
            } else if (event == Lifecycle.Event.ON_START) {
            // execute custom logic
            } else if (event == Lifecycle.Event.ON_RESUME) {
            // execute custom logic
            } else if (event == Lifecycle.Event.ON_PAUSE) {
            // execute custom logic
            } else if (event == Lifecycle.Event.ON_STOP) {
            // execute custom logic
            } else if (event == Lifecycle.Event.ON_DESTROY) {
            // execute custom logic
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.White),contentAlignment = Alignment.Center) {
        Text(text = "LifecycleExample", fontSize = 72.sp)
    }
}
```

## Manage a WindowContainer's state
The states of a WindowContainer are defined as follows:
| **Property** | **Type** | **Description** |
| --- | --- | --- |
| isFocused | State<Boolean> | Whether the current WindowContainer has focus. |
| isOnstage | State<Boolean> | Whether the current WindowContainer is fully within the viewport and not blocked. <br> When `true`, the current WindowContainer is not blocked by other WindowContainers, and the current WindowContainer can be fully viewed without moving the viewport. |
| isSighted | State<Boolean> | Whether the current WindowContainer enters the viewport (if any pixel is visible). <br> If any pixels enter the viewport, the value is `true`. |
You can use the following code to get the state of a WidowContainer:
```Kotlin
@Composable
fun GetContainerStateExample() {
    val isFocus by LocalSpatialContainerStateManager.current.isFocused
    val isOnstage by LocalSpatialContainerStateManager.current.isOnstage
    val isSighted by LocalSpatialContainerStateManager.current.isSighted
    Column {
        Text(text = "isFocus = $isFocus")
        Text(text = "isOnstage = $isOnstage")
        Text(text = "isSighted = $isSighted")
    }
}
```

You can listen to the state changes of a WidowContainer and perform specific actions in certain states. The following events of`SpatialContainerStateEvent` are related to the state of a WindowContainer:
| **Event** | **Description** |
| --- | --- |
| ON_FOCUSED | This event is triggered when a WindowContainer gains focus. A WindowContainer automatically gains focus when opened, so this event is triggered immediately. |
| ON_UNFOCUSED | This event is triggered when a WindowContainer loses focus. |
| ON_STAGED | This event is triggered when a WindowContainer is fully within the viewport and not blocked. |
| ON_UNSTAGED | This event is triggered when a WindowContainer no longer meets the condition of being "completely visible and not blocked". At this time, the WindowContainer may be partially outside the viewport or blocked by other content. |
| ON_SIGHTED | This event is triggered when any part of a WindowContainer begins to enter the viewport. |
| ON_UNSIGHTED | This event is triggered when a WindowContainer is completely out of the viewport and completely invisible. |
In general, the states of a WindowContainer may be triggered in the following cases:
```Plain Text
[Launch an app]
ON_FOCUSED

[When the monitored WindowContainer gains focus, the user presses the Home button on the controller and the launcher pops up]
ON_UNFOCUSED → ON_UNSTAGED

[When the monitored WindowContainer gains focus, the user clicks the minimize button to bring the WindowContainer to the background]
ON_UNFOCUSED

[When the monitored WindowContainer gains focus, the user clicks the close button; or if the WindowContainer is closed by other methods, such as by calling the closeWindowContainer function]
ON_UNFOCUSED

[The user click the app's icon to reopen WindowContainer]
ON_FOCUSED

[When the monitored WindowContainerA gains focus, the user opens WindowContainerB, causing WindowContainerA to be blocked]
ON_UNFOCUSED → ON_UNSTAGED

[When the monitored WindowContainerA gains focus, the user opens WindowContainerB, and WindowContainerA is not blocked]
ON_UNFOCUSED

[When the monitored WindowContainerA gains focus, the user selects another opened WindowContainerB (which blocks WindowContainerA) and move WindowContainerB backward and forward to the front of WindowContainerA]
ON_UNFOCUSED → ON_UNSTAGED →       ON_STAGED      →  ON_UNSTAGED  → ...
|__________select__________|__drag & fully visible__|__drag & block__|...

[When the monitored WindowContainerA gains focus, the user selects another opened WindowContainerB (which does not block WindowContainerA) and move WindowContainerB backward and forward to the front of WindowContainerA] 
ON_UNFOCUSED →  ON_UNSTAGED →  ON_STAGED  → ...
|___select___|_drag & block_|_drag & fully visible_|...

[When the monitored WindowContainer loses focus, the user reselects this WindowContainer]
ON_FOCUSED → ON_STAGED

[The user rotates the viewport until the WindowContainer completely leaves the field of view; then rotates the viewport back until a part of the WindowContainer appears in the field of view]
ON_UNSIGHTED → ON_SIGHTED
```

The following code demonstrates how to use the`Composable` method to listen to the state changes of a WindowContainer that contains this `Composable`.
```Kotlin
@Composable
fun ObserveContainerStateExample() {
    val stateOwner = LocalSpatialContainerStateOwner.current
    DisposableEffect(key1 = Unit) {
        val observer: SpatialContainerStateObserver = object : SpatialContainerStateObserver {
            override fun onStateChanged(
                source: SpatialContainerStateOwner,
                event: SpatialContainerStateEvent
            ) {
                Log.d("SpatialContainerState", "onStateChange: $event")
                if (event == SpatialContainerStateEvent.ON_FOCUSED) {
                    // execute custom logic when the container gains focus
                } else if (event == SpatialContainerStateEvent.ON_UNFOCUSED) {
                    // execute custom logic when the container loses focus
                }
            }
        }
        stateOwner.stateObservable.addObserver(observer)
        onDispose {
            stateOwner.stateObservable.removeObserver(observer)
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.White),contentAlignment = Alignment.Center) {
        Text(text = "ObserveContainerStateExample", fontSize = 72.sp)
    }
}
```

## API reference

* For details on the interfaces and relevant descriptions of `Lifecycle.Event`, refer to [Android's official documentation](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.Event).
* For information about the interfaces and relevant descriptions of `SpatialContainerStateEvent`, refer to the [PICO Spatial SDK's API Reference](https://developer-cn.picoxr.com/spatial-api/index.html).

