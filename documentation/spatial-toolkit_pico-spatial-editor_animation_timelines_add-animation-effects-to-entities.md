This article describes how to use Timelines to add animation effects to entities.
You can create multiple Timeline animations in a scene. In a Timeline animation, you can set animations for one or more entities, and each entity can contain one or more animation tracks. Timelines provide a variety of preset actions (that is, animation templates). Simply drag these actions onto the track to apply the corresponding animation effects to entities.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e9912200f64488db8f331c2eb960972~tplv-goo7wpa0wc-image.image)
## How to play Timeline animations in a spatial app
In a spatial app, Timeline animations can be triggered by the Behavior Trigger component or played using the `entity.playTimeline()` function of the PICO Spatial SDK.

* You can trigger Timeline animations by adding the Behavior Trigger component to an entity. For details, see [Behavior Trigger](/editor/general-components).
* Timeline is saved as a SpatialTimeline-type component in the scene's .usda file. Since the data structure of Timeline animations differs from traditional types of animation, after the scene is loaded into the PICO Spatial SDK, you can use `entity.playTimeline()` to play Timeline animations in the scene. For details, see [Timeline animation](/document/spatial-sdk/timeline-animation).
   The `entity.playAnimation(animationResource)` function cannot be used to play Timeline animations.


## Steps
Refer to the following steps to add animation effects to 3D models in the scene.
### Step 1: Create a Timeline animation
At the bottom of the Spatial Editor, click the **Timelines-Beta** tab.

* If there is no Timeline animation in the scene, the **Timelines-Beta** tab will prompt you to create a Timeline. Click **Create Timeline**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dc4e714725f04bd39181aca53bdf5ba6~tplv-goo7wpa0wc-image.image)
* If the scene already has Timeline animations, you can click the plus button in the **Timelines** section on the left side of the **Timelines-Beta** tab to create a Timeline.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/71034c2d8c9a4931bce4b2006cb48fd1~tplv-goo7wpa0wc-image.image)

It is recommended to set a meaningful and unique name for each Timeline animation. This is because when you play animations using the PICO Spatial SDK, you need to call the `entity.playTimeline()` function through an `Entity` object with the same name.
The name of a Timeline can only contain letters, numbers, and underscores, and cannot begin with a number.

To rename a Timeline animation, double-click the animation name in the **Timelines** section and enter a new name.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/528318cebaf94c1abbce81ff9c2dd155~tplv-goo7wpa0wc-image.image)
### Step 2: Add entities to the Timeline
In the **Animated Object** section, click **Choose**. The Spatial Editor will pop up a window prompting you to select an entity as the target of the animation. At this point, you need to click to select an entity in the **Hierarchy** window. After selecting, click **Done** in the prompt window. The entity will then be added to the **Animated Object** section.
Additionally, you can also drag entities directly from the **Hierarchy** window to the **Animated Object** panel.
Only entities bound to a Transform component can be added to a Timeline.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4debd89884684553b9583451aa809339~tplv-goo7wpa0wc-image.image)
### Step 3: Create tracks for entities
In the **Animated Object** panel, click the add track icon to the right of the entity to add a track. You can create one or more tracks.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/62fa9a21f02a4e04a4faec7f99ddf7b8~tplv-goo7wpa0wc-image.image)
You can adjust the order of tracks by dragging them up or down.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bbc7f98597b6411e8d1050a59a78c09d~tplv-goo7wpa0wc-image.image)
You can also click the button on the left side of a track to hide or lock the track.

* Once hidden, actions in the track will no longer take effect.
* Once locked, actions in the track can no longer be modified.

To delete a track, first select the track, then press the Backspace key (Windows) or Delete key (macOS).
### Step 4: Add actions to tracks
In the **Action** area on the right side of the track, drag the desired action onto a track. You can adjust the position of an action on the track by dragging it.
For details on how to configure each action, refer to [Supported actions in Timelines](/timeline-built-in-animation-model).

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1ada4f5e956745edb6fa9b1b50e5f9f0~tplv-goo7wpa0wc-image.image)
You can right-click an action and select cut, copy, duplicate, paste, delete, disable, or lock from the dropdown menu.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ff0160c2aa3649d0b91a4d61481f7c7f~tplv-goo7wpa0wc-image.image)
You can also adjust the duration of an action on the track by dragging its left or right boundary. This operation essentially adjusts the **Duration** parameter of the action. In the figure below, note the change in the **Duration** parameter value in the upper right corner when the length of the action is adjusted.
**Play Audio** and **Play Animation** actions cannot have their left or right boundaries adjusted, and do not have a **Duration** parameter, because the lengths of audio and skeletal animation are fixed. However, you can set the number of times they play by using the **Repeat Count** parameter, or check **Repeat Forever** to make them loop forever.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9f527312d974484ba68b71bc32b9934f~tplv-goo7wpa0wc-image.image)
If actions on different tracks overlap on the timeline, the animation effects of the different actions will be combined. The figure below shows, from left to right:

* The combined effect of **Transform By** and **Play Animation**. **Spin** is hidden.
* The combined effect of **Transform By**, **Play Animation**, and **Spin**.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Transform By and Play Animation combined</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2e0c0a5c954a455bbde9aefa6b52d0a9~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Transform By, Play Animation, and Spin combined</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2873654186644388a4e8880578064b79~tplv-goo7wpa0wc-image.image)



</div>
</div>

You can drag the slider on the right to zoom in or out on the timeline.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c4c15fcfee584de2999c1ca1a4628fb0~tplv-goo7wpa0wc-image.image)
### Step 5: Preview animation effects
Click the timeline to move the playback ruler and set the starting point for animation playback. After setting the playback starting point, click the play button to play the animation.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a43134333fe64d2f8bbac9fda3203ff1~tplv-goo7wpa0wc-image.image)
## Other operations
### Nest timeline animation
You can nest a timeline animation into another timeline animation.
In the example below, you nest `Timeline_Plane` into `Timeline_Bird`. You can right-click `Timeline_Plane` and select **Insert into Timeline** from the drop-down menu, or directly drag `Timeline_Plane` onto a track of `Timeline_Bird`.
* The length of a nested timeline animation cannot be modified.
* Timeline animations do not support circular nesting. For example, you cannot nest `Timeline_Bird` into a `Timeline_Plane` that already contains a nested `Timeline_Bird`.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d23ddbf2433e4cf98e93f4f2999179aa~tplv-goo7wpa0wc-image.image)
The following compares the playback effect of two nested timeline animations to that of a single timeline animation:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.501187648456057);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5bab75a3f85e4a418a9f0648bab86ddd~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.498812351543943);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f648c92bae7941ae8d5c8912d93688cc~tplv-goo7wpa0wc-image.image)



</div>
</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/12e789ea5a6b4b4aa91262b92bce7f41~tplv-goo7wpa0wc-image.image)

