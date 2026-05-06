The event system allows you to listen for specific built-in events and execute your self-defined callback logic when the event is triggered. You can also implement custom events and use EventBus to subscribe to and distribute events.
## Built-in events overview
PICO Spatial SDK provides the following built-in events:
| **Event type** | **Description** |
| --- | --- |
| ECS events | The relevant classes are `SceneEvents` and `ComponentEvents`, which includes events such as adding an entity to a scene, adding components, deleting components, and more. |
| Animation events | The relevant class is `AnimationEvents`, including events such as start, pause, resume, and more. For more information, refer to "[Animation events](/en_animation-events)". |
| Collision events | The relevant class is `CollisionEvents`, which includes events such as the start of a collision, maintaining contact, the end of a collision, and more. For more information, refer to "[Add collisions and external forces](/en_collisions-and-external-forces)". |
| Audio events | The relevant class is `AudioEvents`, which includes events such as playing, pausing, stopping audio, and more. For more information, refer to "[Use audio events](/en_audio-events)". |
| Anchor update events | The relevant class is `AnchorUpdate`, which includes events such as anchor creation, information update, load completion, and more. For more information, refer to "[Spatial anchor](/en_spatial-anchor)". |
## Use the built-in events
The general procedure for using the built-in events is as follows:

1. Subscribe to events.
2. Define callbacks.
3. Trigger events. The SDK automatically distributes events and executes the callbacks you have defined.
4. Cancel subscription (as needed).

You can subscribe to events using a valid `scene` (the `scene` instance can be obtained via `entity.scene`) or `content:SpatialViewContent`, and define the callback logic to be executed when the event is triggered within the function body. When you no longer need to listen for events, you can cancel the subscription via `cancel`. Both methods use the same interface format:
```Kotlin
/**
 * Subscribe to events of a specified type
 *
 *
 *@param T The type of event to subscribe to, which must inherit from BaseEvent. 
 *@param eventType The type of the event class to subscribe to. 
 *@param on (optional) Event source. If null, subscribe to all sources. 
 *@param componentType (optional) The type of component associated with the event. If null, it applies to all component types. 
 *@param subscriber Callback handler function that is called when the event occurs. 
 *@return A cancelable object used to cancel event subscriptions. 
 */
fun <T : Event> subscribe(
    eventType: Class<T>,
    on: EventSource? = null,
    componentType: Class<out Component>? = null,
    subscriber: EventSubscriber<T>
): Cancellable
```

The following parts uses `ComponentAddedEvent` as an example to demonstrate how to implement the above process in code.
### Step 1: Subscribe to an event
In the `initial {}` block of SpatialView, subscribe to the component addition event `ComponentEvents.ComponentAddedEvent` using the following code:
```Kotlin
SpatialView(
    initial = { content, _ ->
        val entity = Entity().apply { setName("Entity for ComponentAddedEvent") }
        content.addEntity(entity)
        content.subscribe(ComponentEvents.ComponentAddedEvent::class.java) {
            val targetEntity = it.entity
            Log.d(
                "ComponentAddedEvent",
                "Added Component(name: ${it.componentType}, Target Entity: ${targetEntity.getName()})"
            )
        }
    },
)
```

### Step 2: Define a callback
The callback to be executed when the event is triggered can be defined via the following two methods:

* **Define the callback in** **`subscribe`**
   Specify the logic to be executed when the event is triggered directly in the body of the `subscribe` function.
   ```Kotlin
   SpatialView(
       initial = { content, _ ->
           val entity = Entity().apply { setName("Entity for ComponentAddedEvent") }
           content.addEntity(entity)
           content.subscribe(ComponentEvents.ComponentAddedEvent::class.java) {
               // Callback function
               val targetEntity = it.entity
               Log.d(
                   "ComponentAddedEvent",
                   "Added Component(name: ${it.componentType}, Target Entity: ${targetEntity.getName()})"
               )
           }
       },
   )
   ```

* **Encapsulate the callback in a lambda**
   Encapsulate the callback logic in a lambda of type `(ComponentEvents.ComponentAddedEvent) -> Unit`, then pass this lambda as a parameter to the `subscribe` function. This approach is especially recommended when the callback logic is relatively complex or needs to be reused.
   ```Kotlin
   // Define the callback
   private val componentAddedCallback: (ComponentEvents.ComponentAddedEvent) -> Unit = {
       val targetEntity = it.entity
       Log.d(
           "ComponentAddedEvent",
           "Added Component(name: ${it.componentType}, Target Entity: ${targetEntity.getName()})"
       )
   }
   
   // In SpatialView's initial{}
   content.subscribe(ComponentEvents.ComponentAddedEvent::class.java, subscriber = componentAddedCallback)
   ```


### Step 3: Trigger the event
When a component is added to `targetEntity`, the component addition event is automatically triggered, and the callback you defined is executed. You can add any component to entity to trigger this event.
### Step 4: Cancel the subscription
When you no longer need to use a particular event, it is recommended to unsubscribe from that event. You can define a `subscription` variable to record event subscription, and cancel the subscription in the `onDispose` function:
```Kotlin
@Composable
fun ComponentAddedEventExample() {
    val subscription = remember { mutableStateOf<Cancellable?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            subscription.value?.cancel()
            subscription.value = null
        }
    }
    SpatialView(
        initial = { content, _ ->
            val entity = Entity().apply { setName("Entity for ComponentAddedEvent") }
            content.addEntity(entity)
            // callback as lambda
            subscription.value =
                content.subscribe(
                    ComponentEvents.ComponentAddedEvent::class.java,
                    subscriber = componentAddedCallback
                )
        },
    )
}
```

## Custom EventManager
You can create a custom EventManager to manage event subscriptions. Code sample:
```Kotlin
object EventManager {
    private val subscriptions = mutableMapOf<Class<*>, Cancellable>()

    /**
     * Subscribe to an event of a given type, using a given [Scene], and a given subscriber.
     *
     * @param scene The [Scene] to subscribe to.
     * @param eventType The type of event to subscribe to.
     * @param on The event source to filter events by.
     * @param componentType The type of component to filter events by.
     * @param subscriber The subscriber to call when the event is received.
     */
    fun <T : Event> subscribe(
        scene: Scene,
        eventType: Class<T>,
        on: EventSource? = null,
        componentType: Class<out Component>? = null,
        subscriber: EventSubscriber<T>
    ): Cancellable {
        val subscription = scene.subscribe(eventType, on, componentType, subscriber)
        subscriptions[eventType] = subscription
        return subscription
    }

    /**
     * Subscribe to an event of a given type, using a given [SpatialViewContent], and a given subscriber.
     *
     * @param content The [SpatialViewContent] to subscribe to.
     * @param eventType The type of event to subscribe to.
     * @param on The event source to filter events by.
     * @param componentType The type of component to filter events by.
     * @param subscriber The subscriber to call when the event is received.
     */
    fun <T : Event> subscribe(
        content: SpatialViewContent,
        eventType: Class<T>,
        on: EventSource? = null,
        componentType: Class<out Component>? = null,
        subscriber: EventSubscriber<T>
    ): Cancellable {
        val subscription = content.subscribe(eventType, on, componentType, subscriber)
        subscriptions[eventType] = subscription
        return subscription
    }

    /**
     * Unsubscribe an event of a given type.
     *
     * @param eventType The type of event to unsubscribe.
     */
    fun unsubscribe(eventType: Class<*>) {
        subscriptions[eventType]?.cancel()
        subscriptions.remove(eventType)
    }

    /**
     * Unsubscribe all events.
     */
    fun unsubscribeAll() {
        subscriptions.values.forEach { it.cancel() }
        subscriptions.clear()
    }
}
```

## API reference
The classes involved in the event system are as follows. For more information about events and their descriptions, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

* `SceneEvents`
* `ComponentEvents`
* `AnimationEvents`
* `CollisionEvents`
* `AudioEvents`
* `AnchorUpdate`
* `Scene`
* `SpatialViewContent`

