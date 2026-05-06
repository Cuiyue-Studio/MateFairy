Spatial anchors bind positions in virtual environments to positions in the real world, thereby anchoring virtual contents at specified positions. After a spatial anchor is placed, its position is stored on the device's disk. When the user returns to the same position, the system will retrieve the anchor and return it to the app.
PICO Spatial SDK supports operations including creating spatial anchors, retrieving anchor information, loading existing anchors in the space, listening for spatial anchor events, and deleting spatial anchors. All operations are implemented through the `WorldTrackingManager` class.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6450e28a67fa462a9f8e9a6495f2be34~tplv-goo7wpa0wc-image.image" width="450px" /></div>

## Basic concepts
The basic concepts related to spatial anchors are as follows.
| **Name** | **Note** |
| --- | --- |
| UUID | The unique identifier for a spatial anchor. It is assigned when the anchor is created and can be used to load a specific anchor. |
| Stage | Scene container, with an independent lifecycle. After opening, the space will enter the Full Space state and be exclusively occupied by the app that holds this Stage. |
| Full Space | A type of space state that indicates the space is exclusively occupied by the current app. It is mutually exclusive to Shared Space. |
| WorldAnchor | An anchor instance that contains spatial anchor information, from which you can retrieve the anchor's UUID, name, and transform information. |
## Limitations
Spatial anchors can only be used when the app is in Full Space state (that is, in Stage). The Stage's coordinate system uses the soles of the user's feet as the origin, and the direction of each axis is as shown in the following figure.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/55f4f08fae0f460fa4f39b61360d6cf8~tplv-goo7wpa0wc-image.image" width="150px" /></div>

## Prerequisites

* Add build dependencies (recommended using the version catalog file [libs.versions.toml](https://developer.android.com/build/dependencies?hl=zh-cn#add-dependency)).
   * Add the following to the `[libraries]` section of libs.versions.toml:
      ```Kotlin
      [libraries]
      // ...
      spatial-sense = { group = "com.pico.spatial.sense", name = "sense" }
      ```

   * In the module's build script file build.gradle.kts, add the following to the `dependencies {}` section:
      ```Kotlin
      dependencies {
          // ...
          implementation(libs.spatial.sense)
      }
      ```

* When retrieving spatial anchor data, the app must be in Full Space state.

## Create spatial anchors
Call the `WorldTrackingManager.createAnchor` interface to create a spatial anchor. You need to specify the expected pose information when creating an anchor, and can also assign a name to the anchor. For example, an anchor can be created using controller pose data retrieved from `ControllerTrackingProvider`. 
`createAnchor()` is a suspend function and returns a value of type `WorldTrackingResult<WorldAnchor>`. You can determine whether the spatial anchor was created successfully by checking the returned result. You can continue to retrieve the corresponding spatial anchor instance from this result only if the creation is successful. It is recommended to save the UUID of the spatial anchor after it has been created, so that you can use the anchor again more conveniently later.
Code sample:
```Kotlin
val createResult = WorldTrackingManager.createAnchor(Vector3(0F), EulerAngles(0F, 0F, 0F))
when (createResult) {
    is WorldTrackingResult.Success -> {
        val worldAnchor = createResult.data
    }
    is WorldTrackingResult.Error -> {
        println("Error: Code=${createResult.errorCode}, Message=${createResult.errorMessage}")
    }
}
```

When a spatial anchor is successfully created using the above code, you can obtain the corresponding spatial anchor instance through `createResult.data`. If creation fails, an error code and error message will be given.
When designing the positions of anchors, it is necessary to take the user's interaction experience into account. For example, the distance between the anchor and the user should be kept within 3 meters whenever possible. After the user places an anchor, prompt them to move their viewpoint within a 3-meter range to help the system build the map more effectively.
When searching for an anchor, users should be reminded to observe and move around near the anchor. The retrieval range of an anchor depends on the range of the user's viewpoint movement after placing the anchor, with a maximum radius of 5 meters. If the distance exceeds 5 meters and there are no other anchors nearby, the anchor may not be successfully retrieved.
## Retrieve spatial anchor information
After retrieving a spatial anchor instance, you can use the instance to access the following information:
| **Information** | **Description** | **How to retrieve** |
| --- | --- | --- |
| UUID | After saving the UUID, you can use this information to manage the corresponding spatial anchor. | `worldAnchor.anchorUUID` |
| Name | If no name is entered when creating a spatial anchor, the default name `""` will be used. | `worldAnchor.name` |
| Transform | The pose information of the spatial anchor under the current coordinate system. | `worldAnchor.transform` |
Code sample:
```Kotlin
val createResult = WorldTrackingManager.createAnchor(Vector3(0F), EulerAngles(0F, 0F, 0F))
when (createResult) {
    is WorldTrackingResult.Success -> {
        val worldAnchor = createResult.data!!
        val uuid = worldAnchor.anchorUUID
        val transform = worldAnchor.transform
        val anchorName = worldAnchor.name
    }
    is WorldTrackingResult.Error -> {
        println("Error: Code=${createResult.errorCode}, Message=${createResult.errorMessage}")
    }
}
```

## Load spatial anchors
Only supports loading spatial anchors created by the current app.

All spatial anchors are stored on the local disk of the PICO device. Each app can store up to 1,024 spatial anchors. It is recommended that you save the UUIDs of spatial anchors after they are successfully created, so that you can use them again more conveniently later.
You can use `WorldTrackingManager.loadAnchor(uuids: Array<UUID> = arrayOf())` to load spatial anchors. After you provide an array of previously saved UUIDs, the corresponding anchors will be loaded. If no UUID is provided or an empty array is passed, all anchors saved in the app will be loaded by default.
`loadAnchor()` is a suspend function that returns a value of type `WorldTrackingResult<Array<WorldAnchor>>`. You can determine whether the spatial anchors have been loaded successfully by checking the returned result. Only after a successful load can you continue to retrieve the corresponding array of spatial anchor instances from the result.
Code sample:
```Kotlin
val loadResult = WorldTrackingManager.loadAnchor() // Load all anchors
when (loadResult) {
    is WorldTrackingResult.Success -> {
        println("Loaded anchors: ${loadResult.data?.map { it.name }}")
    }
    is WorldTrackingResult.Error -> {
        println("Failed to load anchors: ${loadResult.errorMessage}")
    }
}
```

When spatial anchors are successfully loaded using the code above, you can retrieve the corresponding array of spatial anchor instances through `loadResult.data`. If loading fails, an error code and error message will be given.
If the user previously placed an anchor but is now unable to retrieve information about it, you can guide the user back to the position where the anchor was previously placed. If the user does not need to retrieve previous anchors or wants to place new anchors elsewhere, you can let the user reposition interactive objects or calibrate the current space, and then experience the app in a new location.
## Subscribe to spatial anchor events
If you want to execute custom callbacks when a spatial anchor is created, loaded, deleted, or its information changes, you can use spatial anchor events. PICO Spatial SDK provides the following spatial anchor events:
| **Spatial anchor event** | **Trigger condition** |
| --- | --- |
| `AnchorUpdate.Event.ADDED` | A spatial anchor has been created. |
| `AnchorUpdate.Event.UPDATED` | Spatial anchor information has been updated. |
| `AnchorUpdate.Event.LOADED` | A spatial anchor has been loaded. |
| `AnchorUpdate.Event.REMOVED` | A spatial anchor has been deleted. |
When the coordinate system of the virtual space changes (for example, when the user recalibrates the coordinates), the position information of spatial anchors will be updated. At this time, to ensure that objects in the virtual scene remain "anchored" at their previous positions in the real-world scene, you need to update the virtual objects placed at these spatial anchors. Therefore, you need to monitor anchor update events to handle custom logic in the app.

You can use `WorldTrackingManager.subscribeAnchorUpdate{} ` to subscribe to all of the above spatial anchor events, and define the logic to be executed in the function body when any spatial anchor event is triggered, such as displaying information about the current anchor. You can also define the specific logic to be executed for different events within the function body. For example, when anchor information is updated, synchronously update the transform of the virtual object corresponding to that anchor.
The following is a code example:
```Kotlin
val sub = WorldTrackingManager.subscribeAnchorUpdate{
    val anchor = it.anchor
    val message = "Anchor with UUID: ${anchor.anchorUUID} and name: ${anchor.name}, was "
    when (it.event) {
        AnchorUpdate.Event.ADDED -> {
            println(message + "added.")
            // Other operations, such as playing a sound effect when an anchor is successfully created
        }
        AnchorUpdate.Event.LOADED -> {
            println(message + "loaded.")
            // Other operations, such as playing a sound effect when an anchor is successfully loaded
        }
        AnchorUpdate.Event.REMOVED -> {
            println(message + "removed.")
            // Other operations, such as playing a sound effect when an anchor is successfully deleted
        }
        AnchorUpdate.Event.UPDATED -> {
            println(message + "updated.")
            // Other operations, such as using the anchor's transform to update the model
        }
    }
}
```

When you no longer need to use spatial anchor events, it is recommended to unsubscribe from them and stop related information services to reduce the performance overhead.
```Kotlin
sub.cancel()
```

## Delete spatial anchors
When you no longer need spatial anchors, you can delete them to free up the space they occupy on the local disk. You can use `WorldTrackingManager.removeAnchor(uuid: UUID)` to delete a spatial anchor that is no longer needed. `removeAnchor()` is a suspend function that returns a value of type `WorldTrackingResult<WorldAnchor>`. You can determine whether the spatial anchor has been deleted successfully by checking the returned result.
Code sample:
```Kotlin
val removeResult = WorldTrackingManager.removeAnchor(savedUUID) // previously saved UUID
when (removeResult) {
    is WorldTrackingResult.Success -> {
        println("WorldAnchor removed successfully.")
    }
    is WorldTrackingResult.Error -> {
        println("Error: Code=${removeResult.errorCode}, Message=${removeResult.errorMessage}")
    }
}
```

When the anchor is successfully deleted using the code above, a corresponding message will be output. If the deletion fails, an error code and error message will be given.
## API reference
The `WorldAnchor` and `WorldTrackingManager` classes provide functions related to spatial anchors. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
