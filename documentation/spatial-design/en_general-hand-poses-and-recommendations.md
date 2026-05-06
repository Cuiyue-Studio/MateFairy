When designing general hand poses, you need to follow the guidelines below:

* Usability: The experience of various operations should be as user-friendly as possible and should not cause excessive physiological or psychological burden.
* Consistency: Event responses across different interaction methods should be as unified as possible, appropriately unified with physical experience and intuition, and consistent with traditional interaction experience where appropriate.
* Compatibility: Support as many interaction methods as possible (such as hand poses, eye movement, and more), and adapt to different user scenarios and requirements.

| **Event** | **Description** | **Suggestions** |
| --- | --- | --- |
| Hover | A state in which the user's gaze, controller ray, or hand pose hovers over a virtual object, without physical contact but triggering interactive feedback. | * Design appropriate sizes and layouts for interactive targets to make them easy to discover. <br> * Avoid layouts that are too far or too close, and avoid a field of view that is too small. <br> * Adopt appropriate feedback effects to enhance users' sense of certainty regarding their expectations |
| Tap/Click | The action of quickly clicking virtual objects or UI elements, similar to clicking on a traditional screen, is the most basic confirmation action in XR. | * Adopt effective feedback results. <br> * Note: Pay attention to the temporal and spatial thresholds for event activation to avoid confusion with operations such as dragging and similar actions in the user experience. <br>  <br>  |
| LongPress | An interaction triggered after holding a virtual object for more than a certain amount of time, used to invoke menus or other special functions. | * Set appropriate feedback effects to prevent user confusion during the long press process. <br> * Caution: Pay attention to the temporal and spatial thresholds for event activation to avoid confusion in the user experience with operations such as drag-and-drop, clicking, and similar actions. |
| Drag | Hold the virtual object, then move the controller or perform a hand pose so that the object follows your movement. This operation is used to adjust the object's position. | * Avoid excessively frequent and long-distance dragging, as it can cause user fatigue. <br> * Caution: Pay attention to the temporal and spatial thresholds for event activation to avoid confusion in the user experience with actions such as clicking. |
| Scale | Interaction for scaling the size of virtual objects using a two-finger hand pose or two controllers. | * Use the zoom effects provided by generic events, maintaining a relatively natural zoom center and magnification. <br> * Restrict the zoom scale to a reasonable range to prevent objects from becoming too small and disappearing or too large and obscuring the scene. |
| Rotate | Interactions for rotating the orientation of virtual objects using actions such as single-finger rotation, two-finger twisting, controller circling, and similar actions. | * Use the zoom effect provided by general events, maintaining a relatively natural rotation center and speed. |

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

Scale:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/83cb8b47519c4031977392e6dd1dd8cb~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

Rotate:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3e2048234d32441387c13af67113954a~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

Pinch + Poke:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ac8dc72d4a8d44f8b7a514cbeb815e2f~tplv-goo7wpa0wc-image.image)


</div>
</div>


