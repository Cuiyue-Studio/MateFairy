Somatosensory interaction includes eye-hand interaction, direct gesture interaction, and gesture ray interaction. This article provides design recommendations related to somatosensory interaction.
## Eye-hand interaction (gaze-pinch)
"Gaze-Pinch" is an efficient and convenient interaction method that enables users to quickly select distant targets. The specific procedure is as follows: use gaze to navigate and identify the target object, then pinch with the index finger and thumb to trigger an interaction with the target object. After pinching, moving your hand allows you to drag the object. Two-handed gestures can also be used to rotate, scale, and perform other interactions.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df420e731fa142a9a06e78fbaa6a49eb~tplv-goo7wpa0wc-image.image)
In certain scenarios, developers may need to restrict the use of this interaction method to only specified operations, or block this interaction method entirely. This can be achieved by filtering by event source type. The type label for eye-hand gaze-pinch is `.gazePinch`.
### Recommendation

* Clear interaction goals
   You should reasonably place interactive targets within the user's line of sight and pay attention to the design of color and boundaries to help users discover interactive objects, so that they can more easily operate the targets.
* A proper size layout
   It is necessary to design reasonable object sizes and spacing to ensure interface readability and interaction accuracy. For recommended values for control sizes and spacing, refer to the specifications for UI controls.
* Appropriate interaction feedback
   It is strongly recommended that you design appropriate visual feedback for interactive objects to ensure that users can anticipate the effects of upcoming interactions. To protect user privacy, we currently do not provide specific information about hover events triggered by the user's gaze point to developers and applications. We provide a default hover effect (Hover Effect) for general Spatial UI components, and you can achieve some custom effects through configuration.

## Direct gesture interactions
Direct Gesture Interaction provides users with intuitive and convenient interaction capabilities, allowing them to manipulate virtual objects just as they would operate real-world items. Direct gesture interaction is mainly presented through two methods: Poke and Pinch.

* **Poke**
   Touch gestures allow users to tap interactive objects using the tip of the index finger, and to scroll or drag within a specific plane.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bc4e42ad77f94b2b8d9da90bdca3dfc9~tplv-goo7wpa0wc-image.image)
* **Pinch**
   The pinch gesture allows users to pinch their index finger and thumb together to select interactive objects, and supports subsequent dragging. Simultaneous interaction with both hands also enables operations such as scaling, rotating, and more.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ef049e8bb55947b9a926436e3f82e4b0~tplv-goo7wpa0wc-image.image" width="700px" />   </div>


### Suggestions

* Appropriate use scenarios
   Gesture-based direct interaction is suitable for simulating experiences in the real physical world, making it easier to create interaction experiences that align with user intuition.
   * Poke interaction is suitable for tapping on the surface of objects and can also support page scrolling, but it is not suitable for dragging in space.
   * Pinch offers a broader range of use cases, enabling drag-like actions with virtual content that resemble dragging in the physical world.
   * If you want to selectively filter or block events from a specific interaction method, refer to the event source type. The tag types for index finger touch and finger pinch are `.poke` and `.directPinch`, respectively.
* Appropriate content layout
   Just like the range of motion of the hand in the real world, if you want users to interact directly with content using gestures, place the content within reach. At the same time, the size of each individual object should make it easy for users to find and directly interact with it.

## Hand Ray
Hand Ray is an intuitive far-field interaction method that allows users to interact with distant objects using rays emitted from the hand. The specific operation is as follows: adjust the orientation of the emitted ray by moving or rotating your hand to control the selection of the target; pinch with your index finger and thumb to trigger an interactive operation on the target object. After pinching, moving your hand allows you to drag the object, and using both hands can also execute interaction intents such as rotation and scaling.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/183be66292ea4a6eb95179ce000d8cf4~tplv-goo7wpa0wc-image.image)
### Suggestions
Gesture ray interaction is a common interaction method for XR devices. Developers should pay attention to the following aspects to ensure its effectiveness:

* A layout with reasonable dimensions
   It is necessary to design appropriate element sizes and object spacing to ensure interface readability and interaction accuracy. In particular, larger targets in more distant areas help users better complete interaction tasks using gesture rays. For recommended values for control sizes and spacing, refer to the UI control guidelines.
* Appropriate interactive feedback
   It is strongly recommended that you design appropriate visual feedback for interactive objects to ensure that users can anticipate the effects of upcoming interactions.
