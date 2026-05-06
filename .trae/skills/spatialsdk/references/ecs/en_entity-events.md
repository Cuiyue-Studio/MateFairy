PICO Spatial SDK provides multiple callbacks for entity events. You can subscribe to related events through `Scene` or `SpatialViewContent` to receive callbacks and implement custom logic.
## Important notes
Only when the event occurs within `Scene` or `SpatialView` can the corresponding event callback be received.
## Event list
`EntityEvents` object defines events related to the entity lifecycle and parent-child hierarchy, as shown in the following table:
| **Event** | **Description** |
| --- | --- |
| Enable | When an entity is enabled in a container, the `EntityEvents.Enable` event is triggered. |
| Disable | When an entity is disabled in a container, the `EntityEvents.Disable` event is triggered. |
| Destroy | When an entity is destroyed in a container, the `EntityEvents.Destroy` event is triggered. |
| ParentChanged | When the parent node of an entity in the container changes, the `EntityEvents.ParentChanged` event is triggered. |
`SceneEvents` object defines events related to changes in the scene and its entities, as shown in the following table:
| **Event** | **Description** |
| --- | --- |
| EntityAdded | When an entity is added to a container, the `SceneEvents.EntityAdded` event is triggered. |
| EntityRemove | When an entity is removed from a container, the `SceneEvents.EntityRemove` event is triggered. |
| Update | When a container updates every frame, the `SceneEvents.Update` event is triggered. |
## Subscribe to events through Scene
By subscribing to events through `Scene`, you can monitor events occurring within the entire container, including all entities that have joined the `Scene`. For example, when an entity is added to `SpatialView`, the entity's `Enable` event can be monitored as follows:
```Kotlin
// Create a SpatialView for displaying 3D content or entity in the scene
SpatialView( 
    modifier = Modifier.padding(bottom = 10.dp).background(color = Color.Transparent)
) { content, _ ->
    content.addEntity(entity)
    // Subscribe to the entity's enable event; this callback is triggered when the entity is enabled
    entity.scene?.subscribe(EntityEvents.Enable::class.java) {}
}
```

## Subscribe to events through SpatialViewContent
When subscribing to events through `SpatialViewContent`, only events occurring within the current view are monitored, and this subscription only works for a specific `SpatialView`. The following method enables you to monitoring the `Enable` event of entities within the current view without affecting other views or the global `Scene`:
```Kotlin
// Create a SpatialView for displaying 3D content or an entity in the scene
SpatialView(
    modifier = Modifier.padding(bottom = 10.dp).background(color = Color.Transparent)
) { content, _ -> 
    // Subscribe to the entity's enable event; this callback is triggered when the entity is enabled
    content.subscribe(EntityEvents.Enable::class.java) {}
}
```

## API reference
`EntityEvents` and `SceneEvents` objects provide entity-related events. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
