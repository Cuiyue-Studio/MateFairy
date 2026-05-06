Ambient audio can perceive the relative orientation between the sound source and the listener, but does not include reverb effects or consider the distance between the sound source and the listener.
`AmbientAudioComponent` can be understood as a listener-centric "audio skybox" that dynamically adjusts audio output by perceiving the relative orientation of sound sources.
## Characteristics

* **Constant volume**: Regardless of how the listener moves, the volume does not change with the listener's position.
* **Directionality limited**: Listeners can hear sound only within the sound source's directional area; outside this range, the sound is completely muted.

## Use cases
In virtual environments, ambient audio is suitable for creating a sense of direction and atmosphere, rather than creating a sense of spatial distance. Typical use cases are as follows:

* Configure non-narrative sounds, such as background music or ambient sounds, as ambient audio to prevent changes in the listener's viewpoint from affecting the listening experience.
* Sounds that convey a strong sense of direction but do not require distance attenuation, such as wind, flowing water, or distant machinery.

By using environmental audio, you can enhance the sense of immersion in the scene and provide a more natural and coherent auditory experience without increasing the complexity of spatialization calculations.
## General development procedure
For the general procedure of using spatial audio, refer to the "Development procedure" section in "[Spatial audio overview](/en_spatial-audio-overview)".
## Use ambient audios
The following code demonstrates how to use ambient audios, including creating entities, loading audio files, and using the player controller to play, pause, and stop the audio.
```Kotlin
// Create an entity instance
val entity = Entity()

// Load an audio file
val audioResource =
    AudioResource.load(
        "your_custom_name",
        "asset://your_ambient_audio_file.wav",
        LoadType.FROM_ASSETS
    )

// Add the ambient audio component to the entity
val ambientAudioComponent = AmbientAudioComponent(volume = 1.0f)
entity.components.set(ambientAudioComponent)

// Get the audio player controller (choose any method)
// Method 1:
val audioPlayerController = entity.prepareAudio(audioResource)
// Method 2:
val audioPlayerController = entity.playAudio(audioResource)

// Control audio playback, including starting, pausing, and stopping playback
audioPlayerController.play()
audioPlayerController.pause()
audioPlayerController.stop()
```

## API reference
The `AmbientAudioComponent` class provides relevant properties and functions. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

