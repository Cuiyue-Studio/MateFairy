`AudioGroupResource` represents a collection of audio resources.
When you play an `AudioGroupResource`, the system selects an audio from the collection for playback based on the playback mode (`AudioGroupResourcePlayMode`) you set. The audio selection occurs each time the `play()` method is called. For example, in `FORWARD` mode, after each execution of `stop()` followed by `play()`, the next audio in the list will be played.
## Play modes
`AudioGroupResource` supports the following play modes, which you can set via `AudioGroupResourcePlayMode`.
| **Mode** | **Description** |
| --- | --- |
| RANDOM | Random mode: Each time, an audio is randomly selected from the audio group for playback. |
| FORWARD (default) | Forward mode: Audios are played sequentially according to the order in which they were added to the group. After the last audio is played, playback restarts from the first audio. |
| BACKWARD | Backward mode: Audios are played in reverse order according to the order in which they were added to the group. After the first audio is played, playback restarts from the last audio. |
| UNKNOWN | Unknown mode: Reserved value for forward compatibility. Developers should not use this mode. |
## Code sample
The following code sample demonstrates how to create an `AudioGroupResource` containing various animal sounds and play it in `RANDOM` mode.
```Kotlin
// Create an audio resource array to store multiple audio clips
val audioResourceArray = Array<AudioResource>()
// Load the "dog" audio resource from /assets
val dogAudioResource = AudioResource.load(
                                    "dog",
                                    "asset://audio/dog.wav",
                                    loadType = LoadType.LOAD_FROM_ASSETS,
                                     )
// Load the "cat" audio resource from /assets
val catAudioResource = AudioResource.load(
                                    "cat",
                                    "asset://audio/cat.wav",
                                    loadType = LoadType.LOAD_FROM_ASSETS,
                                     )   
// Load the "bird" audio resource from /assets
val birdAudioResource = AudioResource.load(
                                    "bird",
                                    "asset://audio/bird.wav",
                                    loadType = LoadType.LOAD_FROM_ASSETS,
                                     ) 

// Add multiple audio resources to the array to form a playback collection
audioResourceArray.add(dogAudioResource)
audioResourceArray.add(catAudioResource)
audioResourceArray.add(birdAudioResource)

// Create an audio group resource:
// - Name: audio group test
// - Content: audioResourceArray
// - Play mode: RANDOM
val animalAuidoResource =AudioGroupResource("auido group test",audioResourceArray,RANDOM)

// Create an entity
val entity = Entity()

// Configure spatial audio properties for the entity: volume attenuation, sound directivity, and distance attenuation mode
entity.components.set(ObjectAudioComponent(
    0.5f,
    Directivity(0.235f, 0.675f),
    DistanceAttenuationMode.INVERSE_SQUARED,
))

// Bind the audio group to the entity and obtain the playback controller
val audioPlayerController = entity.prepare(animalAuidoResource)

// Start playback (randomly play one of "dog"/"cat"/"bird")
audioPlayerController.play()

// Stop playback
audioPlayerController.stop()

// Play again to test the playback behavior of different AudioGroupResource
// In RANDOM mode, each play() may play a different audio
audioPlayerController.play()
```

## API reference
For detailed information about the APIs provided by the `AudioGroupResource` class, refer to [API reference](https://developer-cn.picoxr.com/spatial-api/0.9.5/index.html?v=0.9.5).

