The PICO Spatial SDK provides the audio mixer group feature, allowing you to simultaneously control the volume and playback speed of all audio files within an audio mixer group.
## Basic concepts
Before using the audio mixer group feature, it is recommended to first understand these basic concepts.
### AudioMixerGroupsComponent
`AudioMixerGroupsComponent` is a container component used to manage `AudioMixerGroupResource`. You can use `AudioMixerGroupsComponent` to add, retrieve, or delete `AudioMixerGroupResource`.
### AudioMixerGroupResource
`AudioMixerGroupResource` represents an audio mixer group and is used to simultaneously control the playback speed and volume of all `AudioResource` objects with the same `mixerGroupId`.
### AudioResourceConfig
`AudioResourceConfig` is used to associate `AudioResource` with `AudioMixerGroupResource`.
If the `mixerGroupId` property of an `AudioResource` object is the same as the `name` property of an `AudioMixerGroupResource` object, then that `AudioResource` object belongs to the audio mix group corresponding to the `AudioMixerGroupResource` object.
The following diagram shows the relationship between `AudioMixerGroupsComponent`, `AudioMixerGroupResource`, and `AudioResourceConfig`.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e41bc2c20aed47cc90d5152412bc70f7~tplv-goo7wpa0wc-image.image" width="597px" /></div>

## Use case
The use cases for audio mixer groups are as follows:

* **Global/category volume control**: You can assign background music (BGM), sound effects (SFX), and voice to different audio mix groups, enabling independent control of the volume for each type of audio. For example, when adjusting the volume of sound effects, the application only needs to obtain the corresponding `AudioMixerGroupResource` object and call the `setVolume()` function, without having to modify the volume of each audio source individually.
* **Dynamic environmental sound effects**: When entering underwater or activating slow motion and other special scenarios, you can simultaneously control the playback speed of a specific audio mixer group using the `setPlaybackSpeed()` function to create dynamic auditory effects that match the scenario.
* **Quick mute/restore**: You can set the volume of all audio files in an `AudioMixerGroupResource` object to `0.0f` to mute them all at once, or set it to `1.0f` to restore the volume uniformly.

## Development process
Refer to the following steps to simultaneously control the volume and playback speed of audio files within an audio mixer group using `AudioMixerGroupResource`.
### Step 1: Create AudioMixGroupResource
Call the `AudioMixerGroupResource()` function to create an `AudioMixerGroupResource` object.
* An `AudioMixerGroupResource` object takes effect immediately after creation, regardless of whether it is added to an `AudioMixerGroupsComponent`.
* `AudioMixerGroupResource()` function's `name` parameter only supports letters and numbers, with a maximum length of 256 bytes. If the name does not meet the requirements, the system will throw an `IllegalArgumentException` and cause construction to fail.
* Operations related to the `AudioMixerGroupResource` object can be called on the main thread or on internal scheduling threads.

```Kotlin
val audioMixerGroup = AudioMixerGroupResource(name = "YourGroupName", volume = 0.8f, playbackSpeed = 1.0f)
```

The parameters of the constructor `AudioMixerGroupResource()` are as follows:
| Parameter | Required | Description |
| --- | --- | --- |
| name | Yes | Name of the audio mixer group. This name can only consist of English letters (A-Za-z) and numbers (0-9), with a maximum length of 256 bytes. |
| volume | No | Volume. Valid range is [0.0f, 1.0f]. The default value is 1.0f, which represents the original volume. |
| playbackSpeed | No | Playback speed. Valid range is (0.25f, 4.0f], and the default value is 1.0f, which represents the original playback speed. |
### **Step 2: Associate audio resources with** AudioMixerGroupResource
To associate audio resources with a specified `AudioMixerGroupResource` object, you need to first create an `AudioResourceConfig` object and set its `mixerGroupId` property to the `name` of the `AudioMixerGroupResource` object. Then, when calling `AudioResource.load()` to load audio resources, pass in an `AudioResourceConfig` object.
To successfully associate an audio resource with `AudioMixerGroupResource`, ensure that the `mixerGroupId` property you set for the `AudioResourceConfig` object is the same as the `name` property of the `AudioMixerGroupResource` object.

```Kotlin
fun configAudioResource(name: String, path: String): AudioResource {
    val config = AudioResourceConfig(mixerGroupId = name)
    return AudioResource.load(
        name = name,
        path = path,
        loadType = LoadType.FROM_ASSETS,
        config = config
    )
}
```

### Step 3 (Optional) : Add AudioMixerGroupResource to AudioMixerGroupsComponent
To manage `AudioMixerGroupResource` within `AudioMixerGroupsComponent`, call the `addMixerGroup()` function to add an `AudioMixerGroupResource` object to an `AudioMixerGroupsComponent` object. You can associate an `AudioMixerGroupsComponent` object with any `Entity`.
* Operations related to the `AudioMixerGroupsComponent` object must be called on the main thread.
* In `AudioMixerGroupsComponent`, you can manage `AudioMixerGroupResource` through the following functions:
   * `addMixerGroup()`: Add an `AudioMixerGroupResource` object.
   * `clear()`: Delete all `AudioMixerGroupResource` objects.
   * `getAllMixerGroups()`: Retrieve all `AudioMixerGroupResource` objects.
   * `getMixerGroup()`: Retrieve the specified `AudioMixerGroupResource` object.
   * `removeMixerGroup()`: Delete the specified `AudioMixerGroupResource` object.

```Kotlin
entity.components.set(AudioMixerGroupsComponent().apply { addMixerGroup(audioMixerGroup) })
```

### **Step 4: Control the volume and playback speed of all audio in** AudioMixerGroupResource simultaneously
You can use the following functions on an `AudioMixerGroupResource` object to control the volume and playback speed of all audio in the mixer group at the same time.

* `getVolume()`: Get the current unified volume of all audio in the `AudioMixerGroupResource` object.
* `setVolume()`: Set the volume of all audio in the `AudioMixerGroupResource` object simultaneously.
* `getPlaybackSpeed()`: Get the current unified playback speed of all audio in the `AudioMixerGroupResource` object.
* `setPlaybackSpeed()`: Set the playback speed of all audio in the `AudioMixerGroupResource` object simultaneously.

To retrieve an `AudioMixerGroupResource` that has been added to `AudioMixerGroupsComponent`, you can call the `getMixerGroup()` function.

```Kotlin
fun getAudioMixerGroupVolume(entity: Entity, mixerGroupId: String): Float {
    val audioMixerGroup =
        entity.components[AudioMixerGroupsComponent::class.java]?.getMixerGroup(mixerGroupId)
    if (audioMixerGroup != null && audioMixerGroup.valid) {
        return audioMixerGroup.getVolume()
    }
    return 0.0f
}

fun setAudioMixerGroupVolume(entity: Entity, mixerGroupId: String, volume: Float) {
    val audioMixerGroup =
        entity.components[AudioMixerGroupsComponent::class.java]?.getMixerGroup(mixerGroupId)
    if (audioMixerGroup != null && audioMixerGroup.valid) {
        audioMixerGroup.setVolume(volume)
    }
}

fun getAudioMixerGroupPlaybackSpeed(entity: Entity, mixerGroupId: String): Float {
    val audioMixerGroup =
        entity.components[AudioMixerGroupsComponent::class.java]?.getMixerGroup(mixerGroupId)
    if (audioMixerGroup != null && audioMixerGroup.valid) {
        return audioMixerGroup.getPlaybackSpeed()
    }
    return 0.0f
}

fun setAudioMixerGroupPlaybackSpeed(entity: Entity, mixerGroupId: String, playbackSpeed: Float) {
    val audioMixerGroup =
        entity.components[AudioMixerGroupsComponent::class.java]?.getMixGroup(mixerGroupId)
    if (audioMixerGroup != null && audioMixerGroup.valid) {
        audioMixerGroup.setPlaybackSpeed(playbackSpeed)
    }
}
```

## Best practices
### Use AudioMixerGroupResource together with AudioPlayerController
`AudioPlayerController` is used to control the playback behavior of individual audio tracks (such as play, pause, stop, loop, and fade in/out). You can combine this with the group-level playback controls (playback speed, volume) provided by `AudioMixerGroupResource`.
### Play audio resources
`AudioMixerGroupResource` does not provide playback functionality. To play audio, you need to call the `AudioPlayerController.play()` function or the `Entity.playAudio()` function.
### Resource lifecycle management
Since the `AudioMixerGroupResource` class is a subclass of the `Resource` class, pay attention to the following points when managing resources:

* **Release a single resource**: When an `AudioMixerGroupResource` object is no longer needed, it is recommended to call its `close()` function to explicitly release the underlying resources.
* **Clear all resources**: When an `AudioMixerGroupsComponent` object is destroyed or removed from an entity, you can call the `clear()` function to clear all mixer groups contained within it at once.
* **Avoid operating on closed resources**: Do not perform any operations on resources that have been closed. For example, calling the `getVolume()` method on a closed `AudioMixerGroupResource` object will cause the program to throw an `IllegalStateException` exception.

## API reference
The `AudioMixerGroupsComponent` class, `AudioMixerGroupResource` class, and `AudioResourceConfig` class provide properties and functions related to audio mixer groups. For details, see the [API reference](https://developer.picoxr.com/en/spatial-api/0.9.5/index.html?v=0.9.5).
