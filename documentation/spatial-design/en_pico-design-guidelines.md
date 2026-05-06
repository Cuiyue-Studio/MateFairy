**PICO's design originates from a fundamental belief: to open up an infinite realm for human perception and creativity.**
Here, information is no longer constrained by the limitations of physical screen size, but instead becomes a natural component of the immersive space. Users can interact with it naturally to perform complex tasks or enjoy entertainment, while still maintaining a connection to the real world. In the fusion of virtual and real, work, creation, and life are seamlessly integrated.
## An immersive space
Design should aim to enable digital information to naturally integrate into users' virtual and physical spaces, enhancing, rather than disrupting, users' sense of immersion and ability to perceive reality.
### Spatial characteristics

* **A boundaryless space**: Within a boundaryless space, users can freely browse content provided by the application, for example: Plain and Volume, and interact with it to achieve their intended goals.
* **Personalized spaces**: Users can freely create or use new spaces, which may have system features enabled by default. It also supports users in customizing the placement of virtual information, such as a weather widget, within the environment, enabling them to easily access content and enjoy a more immersive experience.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9a76c972bb484cfe916c9c13670b0789~tplv-goo7wpa0wc-image.image)
* **Virtual/real**: By rotating the crown button, users can freely adjust the level of immersion in the environment. You can provide different default immersion levels and adjustment ranges based on the requirements of the application scenario.

## Conform to human perceptual characteristics
MR experiences should respect the physiological and psychological characteristics of human senses, especially vision and hearing, and be designed to align with natural cognitive patterns, reduce cognitive load and physical discomfort such as motion sickness, and create a comfortable and intuitive experience.
### Visual focus

* **Central field of view priority**: Core content should be arranged within the clear field of view: horizontally within ±33.5° (maximum not exceeding ±42°) and vertically within ±20° (maximum not exceeding ±22.5°). Secondary content should be placed in the peripheral area. The initial window position should be set farther away to reduce eye fatigue.
   | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/46a8f27ffc9a437d86b653fd6a83f21f~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5df3953636884c95bc0c2c62e98ea679~tplv-goo7wpa0wc-image.image) |
   | --- | --- |
* **Imply information priority through depth**: In a spatial environment, every UI element has positional information. According to user perception, information that is closer is considered more important. Avoid, as much as possible, situations where persistent information contradicts its physical location or overlaps with background content.
* **Information focus:** A rounded corner design reduces visual distraction and prevents sharp edges from diverting visual focus; a horizontal layout adapts to a wide field of view.

### Auditory focus

* **An immersive audio experience**: To achieve a more immersive experience, audio can be rendered with precise spatial positioning. It is recommended to synchronize the location of audio with visual content to enhance immersion and spatial awareness.
* **Noise reduction and focus**: Users may be in noisy outdoor environments, or in quiet homes. Please clearly identify the role of auditory information. For important information, ensure that it can be noticed in all of the above environments. In a quiet environment, it may be appropriate to provide ambient sound to reduce users' feelings of loneliness.

### Comfort and stability

* **Clear and unobtrusive feedback**: Gaze acts as the cursor, gesture operations (such as pinch, drag, and more) replace traditional touch controls, and you can use visual highlights, spatial audio effects, and other feedback to compensate for the lack of tactile sensation. When simultaneously designing feedback effects, you should take into account the issue of misrecognition caused by eye saccades and avoid making feedback effects so conspicuous that they distract users. For example, sound feedback is generally not provided for the Hover state and is only used when it is necessary to emphasize the Hover state. For details, see the sound section.
* **Vestibular – visual consistency**: Virtual object movement must be consistent with the user's perception of physical movement. Movement of large areas or volumes often causes users to feel dizzy. You can reduce discomfort by using semi-transparent transitions, dynamic blur, and reducing the content FOV. At the same time, you should also try to avoid situations involving rapid information and rapid movement.

### Avoid fatigue

* **Sitting, standing, semi-reclining, lying prone, and dynamic movement in safe scenarios**: Different postures affect browsing and operation in various ways (for example, standing allows for a greater range of arm movement, sitting provides a more stable field of view, and semi-reclining reduces arm support, making it suitable for light one-handed interaction). You need to consider both the typical postures used with the application and those that require adaptation to ensure a more comfortable experience. It is not recommended to use it while moving dynamically in unsafe scenarios to avoid causing safety issues.

## Accessibility and safety
### **A natural interaction flow**
We offer a variety of interaction methods to enable users to switch naturally and seamlessly between different scenarios. For example, users can interact using eye and hand gestures in distant-view scenarios, use direct touch in close-range scenarios, or connect an external mouse and keyboard while working in an office environment. When designing applications, it is important to fully consider the effectiveness of these interaction methods and make the most of the advantages of each device—for example, providing keyboard shortcuts. You can also design the user journey by combining multiple interaction methods.
### **Prohibited Dangerous behaviors**

* Prohibit sudden panoramic occlusion
* Prohibit large-scale content shifts without prior notice
* Do not provide highly saturated color block content in dark environments.
* Do not use while moving dynamically in unsafe situations, for example, when going up or down stairs, crossing the street, driving, and more.
