This article introduces the audio components in Spatial Editor.
Audio components can be added to or removed from entities. In .usda files, the type of audio component is `SpatialComponent`. This is a component type defined by Spatial Editor and is not native to USD.
A maximum of 39 audio files can be played simultaneously in a scene.

## Channel Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c5d9bf9cd5b244a6b21660f83c9a3143~tplv-goo7wpa0wc-image.image)
Channel audio sources do not have spatial effects, such as regular background music.
| **Parameter** |  | **Note** |
| --- | --- | --- |
| Volume |  | Volume level, ranging from 0 to 1, supports decimal values. 0 means mute, 1 means maximum volume. |
| Preview | Audio Resource | Audio source. You can: <br>  <br> * Click the dropdown menu to select an audio file that has been added to the [Audio Resource Library](/editor/audio-components) component as the audio source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b5586aeb4af740d89c6f9e39f0a7669b~tplv-goo7wpa0wc-image.image) <br> * Click the button on the right to select an audio file from the scene as the audio source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d39f88628d54432aab1b6f12c0d7b2a0~tplv-goo7wpa0wc-image.image) <br>  <br>  |
## Ambient Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d0c935871a1b49f88ee62e98a261b499~tplv-goo7wpa0wc-image.image)
An ambient audio source is an audio source with direction but no specific position, such as wind sounds.
| **Parameter** |  | **Note** |
| --- | --- | --- |
| Volume |  | Volume level, ranging from 0 to 1, supports decimal values. 0 means mute, 1 means maximum volume. |
| Preview | Audio Resource | Audio source. You can: <br>  <br> * Click the dropdown menu to select an audio file that has been added to the [Audio Resource Library](/editor/audio-components) component as the audio source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b5586aeb4af740d89c6f9e39f0a7669b~tplv-goo7wpa0wc-image.image) <br> * Click the button on the right to select an audio file from the scene as the audio source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d39f88628d54432aab1b6f12c0d7b2a0~tplv-goo7wpa0wc-image.image) <br>  <br>  |
## Object Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5ab6c79c5a9c4adea6c5372b9a39f98b~tplv-goo7wpa0wc-image.image)
This type of audio source has both a specific position and a clear direction in space, such as a virtual radio.
| **Parameter** |  | **Note** |
| --- | --- | --- |
| Volume |  | Volume level, ranging from 0 to 1, supports decimal values. 0 means mute, 1 means maximum volume. |
| Sound Radius |  | Sound source radius (meters). The default value is 0.1. |
| Distance Attenuation Mode |  | Set how sound attenuates with distance. <br>  <br> * **Fixed**: Within the range defined by the **Sound Radius** parameter, the volume remains constant and does not attenuate with distance. <br> * **Inverse Square**: (Default) Simulates the real-world effect of sound attenuation, where the volume naturally decreases as the distance from the sound source increases. <br>  <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e0f072118fd84ed2bf38cd956335adb4~tplv-goo7wpa0wc-image.image) <br>  |
| Directivity |  | Set the directionality of the sound source. The directionality of the sound source is mainly controlled by two parameters: **Pattern** and **Sharpness**. By properly configuring these two parameters, it is possible to simulate different sound source emission characteristics in a virtual environment, such as microphone pickup patterns, speaker radiation directions, or the spatial sense of instruments in an environment, making the spatial representation of sound more realistic and natural. <br> You can: <br>  <br> * Click the icon on the right to select a preset sound source directivity. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/872a3ab48dea42229fdcc749916d19be~tplv-goo7wpa0wc-image.image) <br> * Customize the sound source directivity by setting **Pattern** and **Sharpness**. For details on how to configure **Pattern** and **Sharpness**, see [Using ObjectAudioComponent](/document/spatial-sdk/use-object-audio-component/). |
| Preview | Audio Resource | Sound source. You can: <br>  <br> * Click the dropdown menu to select an audio file that has been added to the [Audio Resource Library](/editor/audio-components) component as the sound source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b5586aeb4af740d89c6f9e39f0a7669b~tplv-goo7wpa0wc-image.image) <br> * Click the button on the right to select an audio file from the scene as the sound source. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d39f88628d54432aab1b6f12c0d7b2a0~tplv-goo7wpa0wc-image.image) <br>  <br>  |
## Audio Mix Groups
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9048e865e8cb45078eac1aa5d05f9f0a~tplv-goo7wpa0wc-image.image)
The Audio Mix Groups component includes audio mix groups created through Audio Mixer. You can use audio mix groups to manage the playback properties of audio files in groups. For details, see [What is Audio Mixer](/what-is-audio-mixer).
## Audio Resource Library
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0eceab10be2d49fe855371758c1adeab~tplv-goo7wpa0wc-image.image)
The Audio Resource Library component is used to batch manage multiple audio resources. An Audio Resource Library component can include one or more audio files. You can click the plus sign to add audio files from the current project.
This component is also used to provide audio resources for the **Play Audio** action in the Timelines animation controller. For details, see [Play Audio](/editor/timeline-built-in-animation-model).

