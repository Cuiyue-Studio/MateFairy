You can manage the lifecycle and state of a Stage to better control content display, user interaction, and resource usage in spatial apps.
## Manage a Stage's lifecycle
You can use `androidx.lifecycle` to listen to the lifecycle of Stage. The lifecycle of Stage and `androidx.lifecycle` correspond as follows:
| **Lifecycle.Event** | **Stage's Lifecycle** |
| --- | --- |
| ON_CREATE | A Stage has been created but is not yet visible to users. <br> This event is triggered the first time a Stage is opened. To trigger the event again, you must close a Stage and then reopen it. |
| ON_START | A Stage has been started and is visible to the user, but has not entered the foreground for interaction. <br> This event is triggered immediately after the `ON_CREATE` event when a Stage is opened. |
| ON_RESUME | A Stage has entered the foreground for interaction. <br> This event is triggered immediately after the `ON_START` event when a Stage is opened. It is commonly used for operations that require user interaction, such as starting camera preview, initiating position updates, resuming audio and video playback, and so on. |
| ON_PAUSE | A Stage is still visible but has already lost focus. <br> This event is triggered first when the user clicks the Home button to close a Stage, then the `ON_STOP` event is triggered. |
| ON_STOP | A Stage is already completely invisible. <br> This event is triggered immediately after the `ON_PAUSE` event when a Stage is about to be closed. It is commonly used to release some resources, such as unregistering listeners, stopping tasks that only need to run while the interface is visible, and so on. |
| ON_DESTROY | A Stage is about to be destroyed. <br> When the user long-presses the menu bar icon and selects to close the app, or when the `closeStage` function is called, this event is triggered immediately. |
Typically, the event triggering conditions for each phase of the Stage's lifecycle are as follows.
```Plain Text
If the default spatial container is a Stage:
[Launch an app]
ON_CREATE → ON_START → ON_RESUME

[The user presses the Home button on the controller to bring the Stage to the background]
ON_PAUSE → ON_STOP

[The user clicks the app's icon to reopen the Stage]
ON_CREATE → ON_START → ON_RESUME

[The user long-presses the menu bar icon and then selects the option to close the app]
ON_DESTROY
---------------------------------------------------------------------------------------------
If the default spatial container is a WindowContainer, and a Stage is opened simultaneously when opening a WindowContainer:
[Launch an app]
ON_CREATE → ON_START → ON_RESUME

[The user presses the Home button on the controller to bring the Stage to the background]
ON_PAUSE → ON_STOP

[The user reopens the Stage via the menu bar]
ON_START → ON_RESUME

[The user closes the Stage via the menu bar; or the Stage is closed by other ways, such as by calling the closeStage function]
ON_DESTROY
```

The following code demonstrates how to use the`Composable` method to listen to the lifecycle of a Stage that contains this `Composable`.
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

## Manage a Stage's state
The state of a Stage is defined as follows:
| **Property** | **Type** | **Description** |
| --- | --- | --- |
| isFocused | State<Boolean> | Whether the current Stage has focus. |
You can retrieve the state of a Stage using the following code:
```Kotlin
@Composable
fun GetContainerStateExample() {
    val isFocus by LocalSpatialContainerStateManager.current.isFocused
    Column {
        Text(text = "isFocus = $isFocus")
    }
}
```

By listening to changes in a Stage's state, you can perform specific actions when it enters specific states. The following events of`SpatialContainerStateEvent` are related to the state of a Stage:
| **Event** | **Description** |
| --- | --- |
| ON_FOCUSED | Triggered when a Stage gains focus. When a Stage is opened, it automatically gains focus and therefore immediately triggers this event. |
| ON_UNFOCUSED | Triggered when a Stage loses focus. |
Generally, if the default spatial container is a Stage and no WindowContainer is opened at the same time when opening the Stage, the triggering conditions for different states of the Stage may be as follows:
```Plain Text
[Launch an app]
ON_FOCUSED

[The user presses the Home button on the controller to exit the Stage and the launcher pops up]
ON_UNFOCUSED

[The user clicks the app's icon to reopen the Stage]
ON_FOCUSED
```

The following code demonstrates how to use the`Composable` method to listen to state changes of a Stage that contains this `Composable`.
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
                    // When the container receives focus, execute custom logic
                } else if (event == SpatialContainerStateEvent.ON_UNFOCUSED) {
                    // Execute custom logic when the container loses focus
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

* For information about the relevant interfaces and details for `Lifecycle.Event`, refer to [the official Android documentation](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.Event).
* For information about the relevant interfaces and documentation for `SpatialContainerStateEvent`, refer to the [API reference](https://developer.picoxr.com/spatial-api/index.html).


