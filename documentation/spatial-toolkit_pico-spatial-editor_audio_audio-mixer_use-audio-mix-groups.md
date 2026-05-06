This article describes how to control the playback properties of audio files through audio mix groups.
You can create and manage audio mix groups in the Spatial Editor or by using the PICO Spatial SDK.

* **Spatial Editor**: You can create one or more [Audio Mix Groups](/editor/audio-components) components in the Spatial Editor and associate them with entities in the scene. In the Audio Mix Groups component, you can add one or more audio mix groups, then add one or more audio files to each audio mix group, and simultaneously control the playback properties of all added audio files within the audio mix group, including mute status, playback speed, and gain.
* **PICO Spatial SDK**: You can manage audio mix groups through the PICO Spatial SDK. For details, see [audio mix groups](/document/spatial-sdk/use-audio-mixgroups-component).

## Step 1: Create an audio mix group
You can create an audio mix group directly and associate it with the scene's Root node, or create an audio mix group and associate it with an entity. Regardless of which entity it is associated with, the Audio Mix Groups component applies to all entities in the scene.
### Create an audio mix group and associate it with the scene's Root node
At the bottom of the Spatial Editor, click the **Audio Mixer**  tab.

* If there are no audio mix groups in the scene, the **Audio Mixer** tab will prompt you to create an audio mix group. Click **Choose** to create a new audio mix group, which will be automatically associated with the scene's Root node.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7478ceaa206942d3b2f55ed321f9a52a~tplv-goo7wpa0wc-image.image)
* If there is already an audio mix group in the scene, you can click the **Audio Mixer**  tab, then click the **Root** icon on the right side and the **Create Mix Group** icon to create an audio mix group.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e0fc50e2f2344b69f220e79aee631f0~tplv-goo7wpa0wc-image.image)

### Create an audio mix group and associate it with an entity
You can also select the entity to associate with the audio mix group. In the **Inspector** window, click **Add Component**, then select **Audio Mix Groups**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f81e34b53836484ba06158fdb041245b~tplv-goo7wpa0wc-image.image)
The Audio Mixer tab will then display an audio mix group associated with that entity.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c6eaee359f794713b20b92d8e679ce4c~tplv-goo7wpa0wc-image.image)
### Rename an audio mix group
Double-click the name of the audio mix group to rename it. It is recommended to use meaningful and easily identifiable names.
The name of an Audio Mix Group can only contain letters, numbers, and underscores, and cannot begin with a number.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ebf0b093224043d6a8aad5ade8edaf44~tplv-goo7wpa0wc-image.image)
## Step 2: Associate audio files with an audio mix group
Click the **+** button in the audio mix group, and select an audio file from the scene in the dropdown menu. Alternatively, you can click **Choose...** to select an audio file that has been added to the current project.
* An audio file can belong to only one audio mix group. If you associate an audio file from another audio mix group with a new audio mix group, the audio file will be automatically removed from the original audio mix group and associated with the new one.
* To disassociate an audio file from an audio mix group, select the audio file and click the **-** button.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">


![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/67d21d224ca24c8eb9790b3250f429a6~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">


![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b8aa5602fd4a4788a49eada48581ed3b~tplv-goo7wpa0wc-image.image)



</div>
</div>

## Step 3: Control playback properties of all audio files in the audio mix group simultaneously
In the audio mix group, you can simultaneously control the playback properties of all added audio files, including mute, playback speed, and gain. After adjusting the playback properties, you can click the play button to the right of each audio file to preview the playback effect.
Only one audio file can be played at a time.

| Parameter | Description |
| --- | --- |
| Mute | Mute. When enabled, all sounds in the audio mix group will be muted. |
| Speed | Playback speed. <br>  <br> * **1.00**: Indicates the original speed. <br> * **Greater than 1.00**: Accelerated playback. <br> * **Less than 1.00**: Slowed playback. |
| dB | Adjust volume (gain). You can set the value by direct Input of a number or by dragging the slider. You can preview both dB and dBSPL simultaneously. dBSPL stands for sound pressure level and is used to simulate the actual loudness attenuation range of sound in a physical environment. <br>  <br> * **0.0**: Represents the original volume. <br> * **A negative value** (for example, -10.0): Decreases the volume. |
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1988d8324b154ac6af81ac523739c298~tplv-goo7wpa0wc-image.image)
## Next steps
The Audio Mix Groups component cannot be used to play audio files. You need to use audio components to play audio files. For details, see [audio components](/en_audio-components).
When you use the audio components to play audio in an audio mix group, the audio will automatically inherit the playback properties you set for that group.


