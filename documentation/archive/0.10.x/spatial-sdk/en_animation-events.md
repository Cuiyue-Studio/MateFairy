Animation events are used to execute custom logic at specific points during animation playback. To do this, you need to subscribe to the target event and define a callback to be executed when the event is triggered. During animation playback, when the system detects that the corresponding event has been triggered, it will automatically call the callback function you defined.
## Animation events overview
The `AnimationEvents` class in the PICO Spatial SDK defines the following animation events:
| **Animation event** | **Trigger conditions** |
| --- | --- |
| `AnimationEvents.Started` | The animation starts playing. |
| `AnimationEvents.Paused` | The animation is paused. |
| `AnimationEvents.Resumed` | The animation resumes playing after being paused. |
| `AnimationEvents.Looped` | The animation has completed a cycle. |
| `AnimationEvents.Terminated` | The playback of the animation has been terminated. Triggered when the `stop()` method is called. |
| `AnimationEvents.Completed` | The animation has finished playing and stopped. It is not triggered when the `stop()` method is called. |
## Subscribe to animation events
You can subscribe to animation events through a valid scene (for entity-based subscription) or SpatialViewContent (for content-based subscription via SpatialView), and define the logic to be executed when the event is triggered within the function body, such as playing sound effects, switching skills, displaying special effects, and more. When you no longer need to listen for events, you can cancel the subscription using `cancel`.
```Kotlin
@Composable
fun AnimationStartedEventExample() {
    val subscription = remember { mutableStateOf<Cancellable?>(null) }
    // DisposableEffect is used to register and unregister events within the lifecycle of the Composable
    DisposableEffect(Unit) {
        onDispose {
            // Automatically cancels event subscription when this Composable is removed
            subscription.value?.cancel()
            subscription.value = null
        }
    }
    SpatialView(
        initial = { content, _ ->
            val entity = Entity().apply { setName("Entity for AnimationStartedEvent") }
            content.addEntity(entity)
            // Subscribe to AnimationEvents.Started, and output a log when this event is triggered
            content.subscribe(AnimationEvents.Started::class.java) {
                val controller = it.playbackController
                Log.d("AnimationStartedEvent", "Animation Started on entity: ${controller.entity}")
            }
        },
    )
}
```

## Manage animation events
When you need to handle a large number of events, you can customize an `EventManager` to manage the subscription and unsubscription of events, as well as the callbacks to be executed when events are triggered.
```Kotlin
// This object is used to centrally manage the subscription and unsubscription of animation events
object EventManager {
    private var subscription: Cancellable? = null

    fun <T : Event> subscribeAnimationEvent(content: SpatialViewContent, animEvent: Class<T>) {
        when (animEvent) {
            // Subscribe to the animation start event
            AnimationEvents.Started::class.java -> {
                subscription =
                    content.subscribe(animEvent) {
                        Log.d("EventManager", "Animation Started!")
                        // Implement your logic here
                    }
            }
            // Subscribe to the animation termination event
            AnimationEvents.Terminated::class.java -> {
                subscription =
                    content.subscribe(animEvent) {
                        Log.d("EventManager", "Animation Terminated!")
                        // Implement your logic here
                    }
            }
            else -> {
                Log.e("EventManager", "No Matching Animation Event Found!")
            }
        }
    }

    // Unsubscribe from all animation events
    fun unsubscribeAllAnimationEvents() {
        subscription?.cancel()
    }
}
```

## API reference
The `AnimationEvents` object provides animation-related events. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

