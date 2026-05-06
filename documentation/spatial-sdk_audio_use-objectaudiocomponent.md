If you need to simulate the orientation, position, and distance of a sound source in the real world, you can use `ObjectAudioComponent`. This component can sense the position and orientation of the sound source in space, and dynamically adjust audio output based on this information, enabling the listener to perceive the orientation, distance, and spatial position of the sound.
After adding this component to the sound source entity, when the position or orientation of the sound source or listener changes, the volume, sound field, and audio effects will be adjusted accordingly. For example, sounds that are closer are louder, sounds that are farther away are quieter, and sounds come from different directions.
## Use cases
Object audio is suitable for scenarios with high requirements for spatial sense and interactivity. Typical use cases are as follows

* **VR games**: Players can perceive enemy footsteps coming from behind or distinguish subtle changes in the sound of distant water.
* **Immersive video**: The sound and visuals are spatially aligned to enhance the sense of presence.
* **Virtual performance**: Align the stage's sound sources with the audience’s position at the spatial level.
* **Education and training**: Enhance the immersiveness of learning or training through realistic spatial audio effects.

## Core concepts
### Volume
Volume is used to control the output level of the sound source and can be divided into total volume and reverb volume.
| **Volume type** | **Note** |
| --- | --- |
| Total volume | Total volume represents the overall loudness of audio output and is used to control the final volume produced by the sound source. Regardless of whether the sound has been spatially processed, the total volume will affect the loudness level perceived by the audience. You can balance the relative loudness of different sound sources by adjusting the total volume, ensuring that key sound effects are not masked by other sounds. |
| Reverb volume | The reverb volume is used to control the intensity of sound reflections and reverb in the environment. The higher the reverb volume, the more pronounced the sound diffusion in a space, allowing listeners to better perceive the spatial characteristics of the sound source's environment, such as room size, material, and echo effects. Setting the reverb volume appropriately can significantly enhance realism and immersiveness. |
### Distance attenuation
Distance attenuation describes the phenomenon in which sound becomes weaker as the distance between the sound source and the listener increases. By applying distance attenuation, sounds that are farther away are perceived as quieter, while those that are closer are louder. Distance attenuation is typically calculated based on physics models, such as linear attenuation, exponential attenuation, or custom curves, to meet the needs of different use cases.
PICO Spatial SDK provides two distance attenuation modes, which you can configure using `DistanceAttenuationMode`.
| **Distance attenuation mode** | **Description** |
| --- | --- |
| FIXED | Audio's volume remains constant. No matter how far the listener is from the sound source, the volume always remains the same. |
| INVERSE_SQUARED (default) | Audio's volume decreases in proportion to the inverse square of the distance. In other words, as the distance between the listener and the sound source increases, the volume decreases more rapidly, simulating the natural laws of sound propagation in the real world. |
### Directivity
In reality, the propagation intensity of a sound source differs depending on the direction. Directivity describes the distribution of sound wave energy emitted by a sound source in different directions. The directivity of a sound source is typically represented using the polar pattern, with the coordinate's origin located at the sound source's position. The 0-degree direction aligns with the forward direction of the sound source, and the radius indicates the sound intensity in that direction.
In the `ObjectAudioComponent`, directivity is primarily controlled by two parameters: `pattern` and `sharpness`. By appropriately configuring these two parameters, it is possible to simulate the acoustic characteristics of different sound sources in a virtual environment, such as microphone pickup patterns, speaker radiation directions, or the spatial sense of instruments in the environment, making the spatial representation of sound more realistic and natural.
| **Parameter** | **Description** |
| --- | --- |
| `directivity.pattern` | The pattern of directivity, which defines the pattern by which sound energy is distributed in different directions. Common patterns include omnidirectional, cardioid, supercardioid, figure-8, and more. Different patterns have a significant effect on the direction and coverage area of sound radiation. For example, in the omnidirectional pattern, the sound source emits sound evenly in all directions; in the cardioid pattern, the sound source is strongest in the forward direction and weakest in the rear direction.  <br> The following figure illustrates the impact of the `pattern` value on the polar pattern when `shapness`is set to `1`. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/48e4a16fa5174a5284e913f238f381a3~tplv-goo7wpa0wc-image.image) |
| `directivity.shapness` | The sharpness of directivity, which controls how concentrated the directivity is (in other words, the concentration level of sound energy in the main radiation direction). The higher the sharpness, the more concentrated the sound from the sound source is in the forward direction, and the weaker the sound energy is to the sides and rear. The lower the sharpness, the wider the sound distribution, making it closer to omnidirectional.  <br> The figure below shows the effect of the sharpness value on the Polar Pattern when the pattern value is 0.5. <br> The following diagram illustrates the impact of the `shapness` value on the polar pattern when `pattern` is set to `0.5`. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b4f1250ead94ba68a830022f1a21f9a~tplv-goo7wpa0wc-image.image) |
The following is the quick reference table for `pattern` and `sharpness`. Specifically, `pattern` corresponds to `alpha` in the diagram, and `sharpness` corresponds to `order`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f443ad50b6894f29bf7bfd5e676e818d~tplv-goo7wpa0wc-image.image)
## General development procedure
For the general procedure of using spatial audio, refer to the "Development procedure" section in "[Spatial audio overview](/spatial-audio-overview)".
## Use object audios
The following code demonstrates how to create an entity in a spatial audio system, load an audio file, and use the player controller to play, pause, and stop the audio.
```Kotlin
// Create an entity
val entity = Entity()

// Load an audio file
val audioResource =
    AudioResource.load(
        "your_custom_name",
        "asset://your_object_audio_file.wav",
        LoadType.FROM_ASSETS
    )

// Add the object audio component to the entity
val objectAudioComponent =
    ObjectAudioComponent(
        volume = 1.0f,
        Directivity(pattern = 0.235f, sharpness = 0.675f),
        distanceAttenuationMode = DistanceAttenuationMode.FIXED,
        reverbVolume = 0.5f
    )
entity.components.set(objectAudioComponent)

// Obtain the audio player controller (choose any method)
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
The `ObjectAudioComponent` class provides relevant properties and functions. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).


