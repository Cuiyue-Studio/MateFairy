This document introduces the audio components in PICO Spatial Editor (Spatial Editor).
Audio components can be added to entities or removed from entities. In .usda files, the type of audio component is `SpatialComponent`. This is a component type defined by Spatial Editor and is not native to USD.
A maximum of 39 audio files can be played simultaneously in a scene.

## Channel Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c5d9bf9cd5b244a6b21660f83c9a3143~tplv-goo7wpa0wc-image.image)
Channel audio sources do not have spatial effects, such as standard background music.
| **Parameters** |  | **Note** |
| --- | --- | --- |
| Volume |  | Volume level, ranging from 0 to 1, supports decimals. 0 means mute, 1 means maximum volume. |
| Preview | Audio Resource | Audio source. You can: <br>  <br> * Click the dropdown menu to select an audio source file node as the audio source. <br> * Click the button on the right to select an audio file from the scene as the audio source. <br>  <br> The **Audio Resource** associated with the **Preview** parameter is for preview only, and the audio file is not actually associated with the Channel Audio component. You must use the PICO Spatial SDK to associate the audio file with the Channel Audio component. <br>  |
## Ambient Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d0c935871a1b49f88ee62e98a261b499~tplv-goo7wpa0wc-image.image)
Ambient audio sources are directional but do not have a specific position, such as wind sounds.
| **Parameters** |  | **Note** |
| --- | --- | --- |
| Volume |  | Volume level, ranging from 0 to 1, supports decimals. 0 means mute, 1 means maximum volume. |
| Preview | Audio Resource | Audio source. You can: <br>  <br> * Click the dropdown menu to select an audio source file node as the audio source. <br> * Click the button on the right to select an audio file from the scene as the audio source. <br>  <br> The **Audio Resource** associated with the **Preview** parameter is for preview only, and the audio file is not actually associated with the Ambient Audio component. You must use the PICO Spatial SDK to associate the audio file with the Ambient Audio component. <br>  |
## Object Audio
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5ab6c79c5a9c4adea6c5372b9a39f98b~tplv-goo7wpa0wc-image.image)
This type of audio source has both a specific position and a clear direction in space, such as a virtual radio.
| **Parameters** |  | **Description** |
| --- | --- | --- |
| Volume |  | The volume level, ranging from 0 to 1, supports decimal values. 0 means mute, and 1 means maximum volume. |
| Sound Radius |  | The radius of the sound source (meters). The default value is 0.1. |
| Distance Attenuation Mode |  | Set the attenuation mode for sound as distance changes. <br>  <br> * **Fixed**: Within the range defined by the **Sound Radius** parameter, the volume remains constant and does not attenuate with distance. <br> * **Inverse Square**: (Default) Simulates real-world sound attenuation, where the volume naturally decreases as the distance from the sound source increases. <br>  <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e0f072118fd84ed2bf38cd956335adb4~tplv-goo7wpa0wc-image.image) <br>  |
| Directivity |  | Set the directionality of the sound source. The directionality of the sound source is mainly controlled by two parameters: **Pattern** and **Sharpness**. By properly configuring these two parameters, you can simulate the emission characteristics of different sound sources in a virtual environment, such as microphone pickup patterns, speaker radiation directions, or the spatial sense of instruments in an environment, making the spatial representation of sound more realistic and natural. <br> You can: <br>  <br> * Click the icon on the right to select a preset sound source directivity. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/872a3ab48dea42229fdcc749916d19be~tplv-goo7wpa0wc-image.image) <br> * Customize the sound source directivity by setting **Pattern** and **Sharpness**. For details on how to configure **Pattern** and **Sharpness**, see [Using ObjectAudioComponent](/document/spatial-sdk/use-object-audio-component/). |
| Preview | Audio Resource | Audio source. You can: <br>  <br> * Click the dropdown menu to select an audio file node as the audio source. <br> * Click the button on the right to select an audio file from the scene as the audio source. <br>  <br> The **Audio Resource** associated with the **Preview** parameter is for preview only, and the audio file is not actually associated with the Object Audio component. You must use the PICO Spatial SDK to associate the audio file with the Object Audio component. <br>  |
## Audio Mix Groups
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9048e865e8cb45078eac1aa5d05f9f0a~tplv-goo7wpa0wc-image.image)
The Audio Mix Groups component includes audio mix groups created through Audio Mixer. You can use audio mix groups to group and manage the playback properties of audio files. For details, see [What is Audio Mixer](/what-is-audio-mixer).
## Audio Resource Library
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0eceab10be2d49fe855371758c1adeab~tplv-goo7wpa0wc-image.image)
The Audio Resource Library component can include one or more audio source file nodes. You can click the plus sign to add audio source file nodes from the current project.
The Audio Resource Library component has the following main uses:

* After the scene is loaded into the PICO Spatial SDK, you can obtain the entity's `AudioResourceLibraryComponent` and then play the audio resources in the Audio Resource Library component.
* Provide audio resources for the **Play Audio** action in the Timelines animation effecter. For details, see [Play Audio](/editor/timeline-built-in-animation-model).

## Audio Resource
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/542a544422814ca38de892c681ab50c2~tplv-goo7wpa0wc-image.image)
Audio resource. Each Audio Resource component can be associated with one audio file.
Audio sources added to a Spatial Editor project come with an Audio Resource component.
The Audio Resource component cannot be added using the **Inspector** window's **Add Component** button at the bottom. You can add an audio file node with an Audio Resource component by clicking the + button in the **Hierarchy** window, then selecting **Audio Asset** > **Audio Resource** from the dropdown menu.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0a29102ac2494c9481f249d34870d374~tplv-goo7wpa0wc-image.image)

| **Parameters** | **Description** |
| --- | --- |
| Source File | The associated audio file. Click the button on the right to select an audio file from the scene. |
| Ambisonics Type | Set the Spatial Audio processing method for Ambisonics (high-fidelity surround sound reproduction). <br>  <br> * **None**: No Ambisonics processing. <br> * **ACN_SN3D_1**: Uses ACN ordering and SN3D normalization, suitable for first-order Ambisonics audio. <br> * **ACN_SN3D_2**: Uses ACN ordering and SN3D normalization, suitable for second-order Ambisonics audio. |
| Audio Mixer Group | Audio mixer group. You can group and manage the playback properties of audio files using audio mixer groups. For details, see [What is Audio Mixer](/what-is-audio-mixer). |
| Loop | Whether to loop playback. |
| Random Start | Each time playback occurs, a random time point is selected from the total length of the audio as the starting point. |
## Audio Group Resource
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a89127c2e2b14c6bbcfdf569c165ef7f~tplv-goo7wpa0wc-image.image)
Audio group resource. An audio group resource is an audio playlist. Each Audio Group Resource component can be associated with one or more audio files. You can click **+** to add audio files.
The Audio Group Resource component cannot be added using the **Inspector** window's **Add Component** button at the bottom. You can add an audio file node with a built-in Audio Group Resource component by clicking the + button in the **Hierarchy** window, then selecting **Audio Asset** > **Audio Group Resource** from the dropdown menu. The audio files you add will become child nodes of the audio file node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0a29102ac2494c9481f249d34870d374~tplv-goo7wpa0wc-image.image)

| **Parameters** |  | **Description** |
| --- | --- | --- |
| Play Mode |  | The playback mode of the audio group. <br>  <br> * **Random**: Random playback. <br> * **Forward:** Sequential playback. <br> * **Backward**: Reverse playback. |

