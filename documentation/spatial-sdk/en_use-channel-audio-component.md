Channel audio maps the channels of audio resources directly to the output device, without any spatialization or reverb processing, and without considering the position or direction of the sound source. For stereo audio, regardless of which direction the listener is facing, the sound from the left channel always comes from the left side, and the sound from the right channel always comes from the right side. For mono audio, the sound is played from a fixed direction.
## Use cases
Due to the fixed directionality of sound propagation in channel audio, it is primarily used to faithfully reproduce original sound effects and provide stable auditory feedback. Whether in traditional multichannel playback or in system interface interactions, it ensures clarity and consistency of sound, while also considering performance and compatibility.

* **Restoration of original mix**
   Channel audio is suitable for reproducing the original mix, such as traditional music, film scores, or multichannel audio resources. This ensures that users hear the content exactly as intended by the creator, without being affected by head movement or position changes.
* **System prompts and sound effects**
   Channel audio is commonly used for system prompts and interface sound effects, such as button clicks, notification reminders, or interface feedback. This type of sound typically does not rely on spatial positioning, but must remain stable and clear to prevent users from missing key information when their viewpoint changes.
* **Compatibility and performance optimization**
   Channel audio is also very useful for multimedia compatibility and performance optimization. Directly outputting channel signals can reduce spatialization computation and improve processing efficiency, making it suitable for scenarios that are sensitive to latency and resource consumption, such as mobile apps or online video playback.

## General development procedure
For the general procedure of using spatial audio, refer to the "Development procedure" section in "[Spatial audio overview](/spatial-audio-overview)".
## Use channel audios
The following code demonstrates how to use channel audios, including creating entities, loading audio files, and playing, pausing, and stopping the audio through the player controller.
```Kotlin
// Create an entity instance
val entity = Entity()

// Load an audio file
val audioResource =
    AudioResource.load(
        "your_custom_name",
        "asset://your_channel_audio_file.wav",
        LoadType.FROM_ASSETS
    )

// Add the channnel audio component to the entity
val channelAudioComponent = ChannelAudioComponent(volume = 1.0f)
entity.components.set(channelAudioComponent)

// Get the audio player controller via any method
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
The `ChannelAudioComponent` class provides relevant properties. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
