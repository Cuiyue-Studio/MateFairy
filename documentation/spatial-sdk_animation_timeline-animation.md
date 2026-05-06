Timeline animations are created using the Timelines animation effector in the Spatial Editor. For details, see [What is Timelines](/document/spatial-toolkit/en_what-is-timelines).
PICO Spatial SDK only provides the functionality to play Timeline animations. To create or edit Timeline animations, you must use the Spatial Editor.
## Play Timeline animations
Because the data structure of Timeline animations differs from traditional types of animations, after the scene is loaded into the PICO Spatial SDK, you need to use the `entity.playTimeline()` function to play Timeline animations in the scene.
The `entity.playAnimation()` function cannot be used to play Timeline animations.

After the scene is loaded by the PICO Spatial SDK, each Timeline animation in the scene is loaded as an `Entity` object. The name of the `Entity` object is the same as the name of the Timeline animation you created in the Spatial Editor. For example, in the figure below, the Timeline animation named **Timeline_bird** corresponds to an `Entity` object named `Timeline_bird`. Therefore, you can find the `Entity` object corresponding to the Timeline animation by searching for the entity name.
* It is recommended to confirm the animation playback effect in advance in Spatial Editor to avoid unexpected animation results.
* After the scene is loaded by PICO Spatial SDK, it is recommended not to modify or remove properties in the scene tree through the SDK to prevent discrepancies between the animation and the preview in Spatial Editor.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c1d6e1acbc694f6e97f7f19675eb15db~tplv-goo7wpa0wc-image.image)
Refer to the following steps to use the PICO Spatial SDK to play Timeline animations.

1. Load a scene containing Timeline animations.
   ```Kotlin
   val root = withContext(Dispatchers.IO) {Entity.load("SpatialAudioScene", bundle)}
   ```

2. Find the `Entity` object corresponding to the Timeline animation by its name.
   Although the name of a Timeline animation is unique within its category, other types of entities in the scene may still have the same name. Therefore, if you search by name only, you may retrieve the wrong entity instead of the intended Timeline animation. To ensure accurate identification, it is recommended to familiarize yourself with the hierarchy of the scene before searching, allowing for more precise filtering.

   ```Kotlin
   val timelineEntity = root.findEntity("Timeline_bird")
   ```

3. Call the `entity.playTimeline()` function to play the Timeline animation in the `Entity` object. This function returns a `TimelinePlayerController` object.
   ```Kotlin
   val controller = timelineEntity.playTimeline()
   ```


Next, you can also:

* Manage the playback of Timeline animations through the `TimelinePlayerController` object. For details, see [Manage Timeline animation playback](/sdk/timeline-animation).
* Subscribe to Timeline animation playback events through the `TimelinePlayerEvents` object. For details, see [Subscribe to Timeline animation playback events](/sdk/timeline-animation).

## Manage Timeline animation playback
After you call the `entity.playTimeline()` function to play the Timeline animation in the `Entity` object, the function returns a `TimelinePlayerController` object. This object can be used to manage the playback of Timeline animations.
The following sample code demonstrates how to manage Timeline animation playback through the `TimelinePlayerController` object.
```Kotlin
// Play Timeline animation again
controller.play()
// Pause Timeline animation playback
controller.pause()
// Stop Timeline animation playback
controller.stop()
// Resume playback of a paused Timeline animation
controller.resume()

// Determine whether Timeline animation is playing
val isPlaying = controller.isPlaying()
// Determine whether Timeline animation playback is stopped
val isStopped = controller.isStopped()
// Determine whether Timeline animation playback is paused
val isPaused = controller.isPaused()
// Determine whether Timeline animation playback is completed
val isComplete = controller.isComplete()
// Get the actual playback time of Timeline animation
val duration = controller.getDuration()
```

## Subscribe to Timeline animation playback events
You can subscribe to animation playback events defined in `TimelinePlayerEvents` through the `Scene` object or the `SpatialViewContent` object. The `TimelinePlayerEvents` object includes the following events:
| Event | Note |
| --- | --- |
| `TimelinePlayerEvents.Started` | Timeline animation starts playing. This event can be triggered in the following cases: <br>  <br> * Calling the `entity.playTimeline()` function. <br> * Calling the `TimelinePlayerController.play()` function. <br> * The Timeline animation is triggered by an associated Behavior Trigger component. For details, refer to "[Add animation effects to entities](/document/spatial-toolkit/en_timeline-add-animation-to-entity)". |
| `TimelinePlayerEvents.Completed` | Timeline animation playback is completed. The `TimelinePlayerController.stop()` function does not trigger this event. |
| `TimelinePlayerEvents.Terminated` | Timeline animation playback is terminated. The `TimelinePlayerController.stop()` function triggers this event. |
| `TimelinePlayerEvents.Paused` | Timeline animation playback is paused. The `TimelinePlayerController.pause()` function triggers this event. |
| `TimelinePlayerEvents.Resumed` | Timeline animation playback is resumed. The `TimelinePlayerController.resume()` function triggers this event. |
The following sample code demonstrates how to subscribe to Timeline animation playback events occurring in the `Scene` object.
```Kotlin
entity.scene?.subscribe(TimelinePlayerEvents.Started::class.java) {}
```

The following sample code demonstrates how to subscribe to Timeline animation playback events occurring in the `SpatialViewContent` object.
```Kotlin
content.subscribe(TimelinePlayerEvents.Started::class.java) {}
```

## Manage Timeline animation playback events
When you need to handle a large number of events, you can customize an `EventManager` to manage event subscriptions and cancellations, as well as callbacks when events are triggered.
```Kotlin
object EventManager { 
    private var subscription: Cancellable? = null 
 
    fun <T : Event> subscribeTimelineEvent(content: SpatialViewContent, timelineEvent: Class<T>) { 
        when (timelineEvent) { 
            // Subscribe to Timeline playback start event
            TimelinePlayerEvents.Started::class.java -> { 
                subscription = 
                    content.subscribe(animEvent) {
                        Log.d("EventManager", "Timeline Started!") 
                        // Implement your logic here
                    }
            } 
            // Subscribe to Timeline termination event
            TimelinePlayerEvents.Terminated::class.java -> { 
                subscription = 
                    content.subscribe(animEvent) {
                        Log.d("EventManager", "Timeline Terminated!") 
                        // Implement your logic here
                    }
            } 
            else -> { 
                Log.e("EventManager", "No Matching Timeline Event Found!") 
            } 
        } 
    } 
 
    // Unsubscribe from all Timeline events
    fun unsubscribeAllTimelineEvents() { 
        subscription?.cancel() 
    } 
}
```

## API reference
For details on the following functions and classes related to Timeline animations, refer to [API reference](https://developer.picoxr.com/spatial-api/index.html).

* `entity.playTimeline()` function
* `TimelinePlayerController` class
* `TimelinePlayerEvents` class


