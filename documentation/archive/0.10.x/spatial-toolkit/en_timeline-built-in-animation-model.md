This article describes the actions supported by Timelines and the configurable parameters for each action.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb571478242c496b88d8e520a3a9250d~tplv-goo7wpa0wc-image.image)
## Transform By
Apply incremental transformation to the entity. Overlay new changes based on the entity's current state (position, rotation, size).
You can choose **Transform To** or **Transform By** based on the use case.

* **Transform To** is suitable for resetting, restoring, or moving to a specific location (such as "Return to the starting point").
* **Transform By** is suitable for continuous actions (such as "Take a step forward" or "Turn left").

### Parameter description
After you select the **Transform By**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d086b7f3d8c43e1af3f965d20222553~tplv-goo7wpa0wc-image.image)
| Parameter | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). The default value is 1. |
| Timing Function | Easing function. Controls the speed curve of the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slow and gradually accelerates. <br> • **EaseOut:** ease out. Starts fast and gradually decelerates. <br> • **EaseInOut:** ease in and ease out. Slow at both ends, fast in the middle (most natural). |
| Position | Displacement increment. The distance accumulated on the current coordinates. For example, (0, 10, 0) means moving 10 meters in the positive direction of the Y axis. |
| Rotation | Rotation increment. The rotation value added to the current angle. For example, (0, 90, 0) means turning 90 degrees to the right. |
| Scale | Scaling factor. Multiplier based on the current size. The default value is (1, 1, 1). |
## Transform To
Smoothly transition the entity from its current state to the specified target state in world coordinates.
You can choose **Transform To** or **Transform By** according to the use case.

* **Transform To** is suitable for resetting, restoring, or moving to a specific location (such as "return to the starting point").
* **Transform By** is suitable for continuous actions (such as "take a step forward", "turn left").

### Parameter description
After you select the **Transform To**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fc49f69692e6421fabd027972969b865~tplv-goo7wpa0wc-image.image)
| Parameter | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). The default value is 1. |
| Timing Function | Easing function. Controls the speed curve of the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slow and gradually accelerates. <br> • **EaseOut:** ease out. Starts fast and gradually decelerates. <br> • **EaseInOut:** ease in and ease out. Slow at both ends, fast in the middle (most natural). |
| Transform Mode | Spatial coordinate type. <br>  <br> * **Global**: (default) world coordinate system. <br> * **Local**: local coordinate system. |
| Position | The world coordinates (x, y, z) of the entity's movement endpoint. |
| Rotation | The Euler angles (x, y, z) of the entity's rotation endpoint. |
| Scale | The entity's final scaling ratio. The default value is (1, 1, 1), which is the original size. |
## Spin
Rotate the entity.
### Parameter description
After you select the **Spin**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/daa9a517a22a43b894b96eadf1e0d6af~tplv-goo7wpa0wc-image.image)
| Parameter | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). The default value is 1. |
| Revolutions | The number of entity rotations; the default value is 1. This value can be positive, negative, or a decimal: <br>  <br> * **Positive**: rotates in the direction specified by the **Spin Direction** parameter. <br> * **Negative**: rotates in the opposite direction specified by the **Spin Direction** parameter. <br> * **Decimal**: indicates a rotation of less than one full turn. For example, 0.5 represents a half rotation. |
| Timing Function | Easing function. Controls the speed variation curve during the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slowly and gradually accelerates. <br> • **EaseOut:** ease out. Starts quickly and gradually decelerates. <br> • **EaseInOut:** ease in and ease out. Slow at both ends, fast in the middle (most natural). |
| Axis | Rotation axis vector. The default is (0, 1, 0). |
| Spin Direction | Rotation direction. <br>  <br> * **Clockwise**: (default) rotates clockwise. <br> * **Counter Clockwise**: rotates counterclockwise. |
## Hide Entity
Hide the entity. This animation essentially sets the Opacity parameter value of the entity's Opacity Controller component to 0.
If the entity is not associated with an Opacity Controller component, when you drag the **Hide Entity** action onto the track, Spatial Editor will prompt you to add the component to the entity first. You need to click **Add** in the prompt message, and Spatial Editor will add the Opacity Controller component to the entity.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/49e82302d9c4423d962019a7bd3cae28~tplv-goo7wpa0wc-image.image)
### Parameter description
After you select the **Hide Entity**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c191881a26f4b50b8c5286653815710~tplv-goo7wpa0wc-image.image)
| Parameters | Descriptions |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). The default is 1. |
| Timing Function | Easing function. Controls the speed variation curve during the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slowly and gradually accelerates. <br> • **EaseOut:** ease out. Starts quickly and gradually decelerates. <br> • **EaseInOut:** ease in and ease out. Slow at both ends, fast in the middle (most natural). |
## Show Entity
Show the entity. This animation essentially sets the Opacity parameter value of the entity's Opacity Controller component to 1.
If the entity is not associated with an Opacity Controller component, when you drag the **Show Entity** action onto the track, Spatial Editor will prompt you to add the component to the entity first. You need to click **Add** in the prompt message, and Spatial Editor will add the Opacity Controller component to the entity.
### Parameter description
After you select the **Show Entity**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d445c7c693c47c28ddce91afaebe429~tplv-goo7wpa0wc-image.image)
| Parameters | Descriptions |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). The default is 1. |
| Timing Function | Easing function. Controls the speed variation curve during the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slowly and gradually accelerates. <br> • **EaseOut:** ease out. Starts quickly and gradually decelerates. <br> • **EaseInOut:** ease in and ease out. Slow at both ends, fast in the middle (most natural). |
## Disable Entity
Disable the entity. After being disabled, the entity cannot be rendered and cannot interact with the physics system.
### Parameter description
After you select the **Disable Entity**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/04d7f7383caf46d78b8fb7c08114e038~tplv-goo7wpa0wc-image.image)
| Parameters | Descriptions |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
## Enable Entity
Enable the entity. After being enabled, the entity can be rendered and can interact with the physics system.
### Parameter description
After you select the **Enable Entity**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ac4be5d7c1f544a2b715e9d2e68d6422~tplv-goo7wpa0wc-image.image)
| Parameters | Descriptions |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
## Play Audio
Play audio files from the Audio Resource Library component. For details, see [Audio Resource Library](/editor/audio-components).
If the entity is not associated with an Audio Resource Library component, when you drag the **Play Audio** action onto the track, Spatial Editor will prompt you to add the component to the entity first. Click **Add** in the prompt message. Spatial Editor will add an Audio Resource Library component to the entity.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b584e9438c06483799fd3cf37b5b5ab6~tplv-goo7wpa0wc-image.image)
### Parameter description
After selecting the **Play Audio**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/20a32ddaebf54a0493beccc2083b06cd~tplv-goo7wpa0wc-image.image)
| Parameters | Descriptions |
| --- | --- |
| Audio | The audio file to be played. You can only select audio files that have been added to the Audio Resource Library component. For details on how to add audio files to the Audio Resource Library component, see [Add audio files to the Audio Resource Library component associated with the entity](/editor/timeline-built-in-animation-model). |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds), defaults to the duration of the audio file. |
| Volume | Volume level, with a value range from 0 to 1. Decimals are supported. 0 indicates mute, and 1 indicates maximum volume. |
| Repeat Count | The number of times the audio is played repeatedly. The default is 0, which means it is played only once and does not repeat. |
| Repeat Forever | When this option is selected, the audio will play in an infinite loop. If not selected, it will play according to the number set in **Repeat Count**. |
### Add audio files to the Audio Resource Library component associated with the entity
Refer to the following steps to add audio files to the Audio Resource Library component associated with the entity.

1. Select the entity to which the **Play Audio** action has been added. In the **Inspector** window on the right, locate the **Audio Resource Library** component.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/17a8eba573114238b552127a31ae107b~tplv-goo7wpa0wc-image.image)
2. Click the **+** button. The drop-down menu that appears will display all audio files in the **Hierarchy** window. You can select the audio file to add, or click **Choose...** Select the audio file to add from the resources of the current project.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/344d3483b13846fda4d257ea75106141~tplv-goo7wpa0wc-image.image)
3. After the audio file is added to the Audio Resource Library component, you can select this audio file in the Audio parameter of the **Play Audio** action.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e64a1e3af203419f9469f87de9490044~tplv-goo7wpa0wc-image.image)




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7a51611210c943a598364be0b8159ceb~tplv-goo7wpa0wc-image.image)






</div>
</div>

## Play Animation
Play skeletal animations in the Animation Resource Library component. For details, see [Animation Resource Library](/editor/animation-component).
When you drag the **Play Animation** action onto the track:

* If the entity type added to the Timeline is SkelRoot, Spatial Editor will automatically add an Animation Resource Library component to the entity. Skeletal animations under the entity will be automatically added to the Animation Resource Library component.
* If the entity type added to the Timeline is not SkelRoot, Spatial Editor will traverse its child entities until it finds an entity of type SkelRoot. Spatial Editor will automatically add an Animation Resource Library component to the entity. Skeletal animations under the entity will be automatically added to the Animation Resource Library component.

### Parameter description
After selecting the **Play Animation**  action, you can configure the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3b4073a1f2c84746bab3869637704dc8~tplv-goo7wpa0wc-image.image)
| Parameters | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Animation | The skeletal animation to be played. You can only select skeletal animations that have been added to the Animation Resource Library component or skeletal animation clips edited through the Animation Resource Library component. For details, see: <br>  <br> * [Add skeletal animation using the Animation Resource Library component](/editor/timeline-built-in-animation-model) <br> * [Edit skeletal animation using the Animation Resource Library component](/editor/timeline-built-in-animation-model) |
| Repeat Count | The number of times the skeletal animation is played repeatedly. The default is 0, which means it is played only once and does not repeat. |
| Repeat Forever | When this option is selected, the skeletal animation will play in an infinite loop. If not selected, it will play according to the number set in **Repeat Count**. |
### Add skeletal animations using the Animation Resource Library component
Refer to the following steps to add skeletal animations using the Animation Resource Library component.

1. Select the entity to which the Animation Resource Library component has been added. In the **Inspector** window on the right, locate the **Animation Resource Library** component.
   The logic for adding the Animation Resource Library component is as follows:
   
   * If the entity type added to the Timeline is SkelRoot, Spatial Editor will automatically add an Animation Resource Library component to that entity.
   * If the entity type added to the Timeline is not SkelRoot, Spatial Editor will traverse its child entities until it finds an entity of the SkelRoot type. Spatial Editor will automatically add an Animation Resource Library component to that SkelRoot type entity.

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ec66bc7733df40e590e0090b91d25995~tplv-goo7wpa0wc-image.image)
2. Click the **+** button below the component, and select the skeletal animation to add from the resources of the current project (only .usdz format is supported). The Animation Resource Library includes **default_animation** by default, which is the original unedited skeletal animation.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6867fe01f3d94080a82b95ed2d22463a~tplv-goo7wpa0wc-image.image)

### Edit skeletal animations using the Animation Resource Library component
Refer to the following steps to edit skeletal animations using the Animation Resource Library component.

1. Select the entity to which the **Play Animation** action has been added. In the **Inspector** window on the right, locate the **Animation Resource Library** component.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ec66bc7733df40e590e0090b91d25995~tplv-goo7wpa0wc-image.image)
2. Locate the skeletal animation that needs to be edited, and click the **+** button on the right side of the skeletal animation to create one or more copies.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/033a2f71b0e34e5f8d7edc8af188eae7~tplv-goo7wpa0wc-image.image)
3. Adjust the start and end times of the copies to clip the desired animation segments.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2a8da49ccbac419cbd0801610b6a902b~tplv-goo7wpa0wc-image.image)

## Play Particle
Play the particle effect in the Particle component. For details, see [Particle](/editor/general-components).
When you drag the **Play Particle** action onto the track:

* If the entity added to the Timeline has a Particle component, the particle effect of that component will play in the **Play Particle** action.
* If the entity added to the Timeline does not have a Particle component, Spatial Editor will prompt you that the entity is missing a Particle component. You can click **Add** to add a Particle component.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e597fe319d084861be188d75bf13cc31~tplv-goo7wpa0wc-image.image)

### Parameter description
After you select the **Play Particle** action, you can set the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/014db838d36d4eb3aef2898b0b095cef~tplv-goo7wpa0wc-image.image)
| Parameters | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Action duration (seconds). |
| Repeat Count | The number of times the particle effect is played repeatedly. The default is 0, which means it is played only once and does not repeat. |
| Repeat Forever | When this option is selected, the particle effect will play in an infinite loop. If not selected, it will play according to the number set in **Repeat Count**. |
## Notification
Add a message notification.
When the Timeline animation reaches the time point corresponding to the **Notification** action, a message notification will be sent to all [Behavior Trigger](/editor/general-components) components in the scene that listen for message notifications, thereby triggering the Timeline animation associated with the Behavior Trigger component to play.
### Parameter description
After you select the **Notification** action, you can set the following parameters in the **Inspector** window:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fb7fbf0364cb40a1b60e6819712bf84a~tplv-goo7wpa0wc-image.image)
| Parameters | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Identifier | The ID of the message notification. |
### Trigger Timeline animation via Notification action
Suppose you add Timeline animations to two entities in the scene using Spatial Editor:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

The Toy_Biplane_Anime entity corresponds to the Timeline_plane animation. The Timeline_plane animation includes the **Play Animation** action and the **Notification** action.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/459e2b8565aa4b02a32d92dde2856d12~tplv-goo7wpa0wc-image.image)
Meanwhile, the **Identifier** of the Notification action is `start_celestial_globe`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8d7c153bf13846d0bd02f2a3bfd75b45~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

The Celestial_Globe_Anime entity corresponds to the Timeline_globe animation. The Timeline_globe animation includes the **Play Animation** action.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/272800d6e1a94537ab48d05ab5be361e~tplv-goo7wpa0wc-image.image)
Meanwhile, the Celestial_Globe_Anime entity has added a Behavior Trigger component, and the **On Notification** module of this component has **Identifier** set to `start_celestial_globe`, and Action set to `Timeline_globe`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4d776ed073fd4ee8aec6720ff2e79468~tplv-goo7wpa0wc-image.image)



</div>
</div>

If the Timeline_plane animation corresponding to the Toy_Biplane_Anime entity is played first, then when the animation reaches the time point corresponding to the Notification action, the Timeline_globe animation corresponding to the Celestial_Globe_Anime entity will be triggered to play.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fd2e31f0564845f4b0e03b8cb9599084~tplv-goo7wpa0wc-image.image)
## Shader Graph
Modify the Shader Graph material property of the entity.
If there are Shader Graph actions in multiple tracks at the same time, the Shader Graph action in the bottom track takes effect.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5016cbc1bb8b4989a69b6f9f9051b910~tplv-goo7wpa0wc-image.image)
### Parameter description
| Parameters | Description |
| --- | --- |
| Start Time | The start time of the action on the timeline, in seconds. You can also manually drag the action on the timeline to adjust the value of this parameter. |
| Duration | Duration of the action (seconds). |
| Timing Function | Easing function. Controls the speed curve of the animation process: <br> • **Linear:** (default) linear. Constant speed throughout. <br> • **EaseIn:** ease in. Starts slowly and gradually accelerates. <br> • **EaseOut:** ease out. Starts quickly and gradually decelerates. <br> • **EaseInOut:** ease in and out. Slow at both ends, fast in the middle (most natural). |
| Material | The Shader Graph material of the entity that needs to be modified. |
| Input Node | The input node of the Shader Graph. |
| Input Value | The value of the input node of the Shader Graph. |
### Create a gradient animation via Shader Graph action
Suppose you create a Cube and set its Shader Graph material. The material contains an Input node **DiffuseColor**. The initial value of **DiffuseColor** is black.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1a21acf87118475e8ca3ed55cc89c62b~tplv-goo7wpa0wc-image.image)
You create a Timeline animation for the Cube, add a **Shader Graph** action to the track, set **Input Node** to `DiffuseColor`, and set **Input Value** to green.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f9c7b24ab48a443d8ed6498d8a3e9e5b~tplv-goo7wpa0wc-image.image)
This allows you to create a gradient animation for the material color.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bd0d31f9d1c3459ab3e5856eb0679aaf~tplv-goo7wpa0wc-image.image)

