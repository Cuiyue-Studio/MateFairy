Audio events are used to trigger custom logic at specific points during audio playback. You can implement audio events by subscribing to target events and defining callback functions. During audio playback, when the system detects that an event has been triggered, it automatically executes the callback you registered.
## Audio events overview
The `AudioEvents` class in the PICO Spatial SDK defines the following audio events:
| **Audio event** | **Trigger condition** |
| --- | --- |
| `AudioEvents.PlaybackStarted` | Audio playback has started. |
| `AudioEvents.PlaybackPaused` | Audio playback has been paused. |
| `AudioEvents.PlaybackStopped` | Audio playback has been terminated. The event is triggered when the `stop()` method is called. |
| `AudioEvents.PlaybackCompleted` | Audio playback finished and stopped. Calling the `stop()` method will not trigger this event. |
| `AudioEvents.PlaybackSeekCompleted` | The audio is successfully positioned at a specific time, that is, the `seekTo()` method is called and the seek operation is completed. |
| `AudioEvents.PlaybackUnknown` | An error occurs during audio playback, such as a decoding error or an I/O error. |
## Subscribe to audio events
You can subscribe to audio events through scene (for subscriptions via entity) or SpatialViewContent (for subscriptions via the content of SpatialView). You also need to define the logic that should be executed when the event is triggered.
The following code implements the creation of a SpatialView with an entity in the Jetpack Compose environment, and triggers custom logic when audio playback is completed by subscribing to `AudioEvents.PlaybackCompleted`.
```Kotlin
@Composable
fun AudioPlaybackCompletedEventExample() {
    val subscription = remember { mutableStateOf<Cancellable?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            subscription.value?.cancel()
            subscription.value = null
        }
    }
    
    // Create SpatialView and initialize the content
    SpatialView(
        initial = { content, _ ->
            // Create an entity and add it to the scene
            val entity = Entity().apply { setName("Entity for AudioPlaybackCompleted") }
            content.addEntity(entity)
            // Subscribe to the audio playback completion event
            content.subscribe(AudioEvents.PlaybackCompleted::class.java) {
                Log.d("AudioPlaybackCompleted", "Audio playback completed for entity: $entity")
                // Execute custom logic after audio playback is complete
            }
        },
    )
}
```

## Manage audio events
When you need to handle a large number of events, you can create an `EventManager` to centrally manage event subscription, unsubscription, and callbacks. In the following code sample, an `AudioEventManager` is defined to centrally manage all logic related to audio events.
```Kotlin
object EventManager {
    // The event object currently subscribed to, which is used for unsubscription later
    private var subscription: Cancellable? = null

    // Subscribe to the corresponding audio event based on the audio event type passed in
    fun <T : Event> subscribeAudioEvent(content: SpatialViewContent, audioEvent: Class<T>) {
        when (audioEvent) {
            // "Audio playback started" event
            AudioEvents.PlaybackStarted::class.java -> {
                subscription =
                    content.subscribe(audioEvent) {
                        Log.d("EventManager", "Audio Started!")
                        // Implement custom logic
                    }
            }
  
            // "Audio playback paused" event
            AudioEvents.PlaybackPaused::class.java -> {
                subscription =
                    content.subscribe(audioEvent) {
                        Log.d("EventManager", "Audio Paused!")
                        // Implement custom logic
                    }
            }

            // "Audio playback completed" event
            AudioEvents.PlaybackCompleted::class.java -> {
                subscription =
                    content.subscribe(audioEvent) {
                        Log.d("EventManager", "Audio Completed!")
                        // Implement custom logic
                    }
            }
            
            // No known audio event type was matched
            else -> {
                Log.e("EventManager", "No Matching Audio Event Found!")
            }
        }
    }

    // Cancel all audio event subscriptions
    fun unsubscribeAllAudioEvents() {
        subscription?.cancel()
    }
}
```

## API reference
The `AudioEvents` class provides audio-related events. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

