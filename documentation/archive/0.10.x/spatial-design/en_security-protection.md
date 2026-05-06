Real-world environment passthrough, virtual scenes, and environmental depth sensing help developers seamlessly integrate virtual elements with users' physical environments, enabling the creation of a greater variety of immersive application experiences. To ensure user safety during the experience, the system provides three safety modes: **Spatial safety mode, MR safety mode, and VR safety mode**. Each mode employs different strategies for managing the user's activity range and safety protection. These different security strategies not only meet users' expectations for fully immersive pure virtual reality application experiences, but also help protect users in combined physical and virtual application experiences, and meet the security requirements of various application forms.
Developers should evaluate the design and content of their applications and select an appropriate security mode based on the characteristics of their applications to ensure that users have a comfortable and secure experience when using the application.
## **Spatial safe mode**
| **Scenario** | **Movement speed detection** | **Obstacle detection** | **Safety zone detection** |
| --- | --- | --- | --- |
| In **Sharespace**, with/without virtual environment. | ✅ | ✅ | ✅ For virtual environments only |
| In **Fullspace**, no virtual environment — **Mix** mode. | ✅ | ✅ | - |
| In **Fullspace**, with virtual environment — **Progressive** mode. | ✅ | ✅ | ✅ Only for virtual environments |
| In **Fullspace**, with virtual environment — **Full** mode. | ✅ | ✅ | ✅ Exit process applies only to virtual environments <br> Once outside, the real environment is fully revealed to you. |
In this safety mode, the system performs safety circle detection, speed detection, and obstacle detection. Safety prompts or real-world passthrough are provided based on the user's distance from the initial position, movement speed, and the positional relationship between obstacles and virtual objects when occlusion occurs within the field of view.

* When the application is in fullspace and features an immersive virtual environment, the real environment is gradually revealed based on the distance from the center. The real environment begins to appear at 0.5 meters from the center. At 1 meter, half of the sphere becomes transparent to reveal the real environment. At 1.5 meters, the real environment is fully revealed. Passthrough is limited to the virtual environment and does not affect application windows.
* If the user moves too quickly or the area in front where virtual objects occlude is close to an obstacle, the view ahead will reveal the real environment.
* This security mode is suitable for most immersive or non-immersive applications. However, it is not well suited for applications that require large-scale movement in fully immersive environments.
* Under unified rendering, both ShareSpace and FullSpace use Spatial security mode by default.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/34724feb176b48be9a903cab310b0434~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/31a54335f18c4437b9240fec85203697~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9c4e5a3281ee43bb8492dd68177c7e6b~tplv-goo7wpa0wc-image.image)


</div>
</div>

## **VR safe mode**
Users can define the boundary of the safety zone according to their surrounding environment or select a quick boundary provided by the system. Within the safety zone they have defined, users are free to move or engage in rapid or relatively vigorous activities. A safety boundary alert is triggered only when the user's head or body tracker reaches the edge of the safe area. If the user's head moves beyond the safe area, the real environment will become fully visible.
This safety mode is suitable for applications that require wide-range, intense movement in fully immersive environments.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2f82b7cf49ec4b96a2482961de58ed8e~tplv-goo7wpa0wc-image.image)
Examples of application scenarios:
| **Scenario** | **Movement speed detection** | **Obstacle detection** | **Safety zone detection** |
| --- | --- | --- | --- |
| **In traditional OpenXR applications** <br> VR safe mode (default selection) | ✅ | - | ✅ |
## **Mixed Reality safe mode**
This mode provides safety assurance based on environmental obstacle detection and user movement speed detection data. Users can move within a large area, and the system does not display a safety boundary.
Mixed reality (MR) applications can fill the user's space with a range of virtual content, enable simultaneous interaction with both real and virtual objects, and encourage users to move freely within their physical environment. To enable users to use applications smoothly and without barriers in the MR environment, while ensuring safety. In MR safety mode, the user's physical activity range is not restricted. The system automatically detects objects or walls in the real environment, allowing the user to interact with both real and virtual objects or virtual environments simultaneously. Only when a virtual object or virtual environment obstructs the user's view and the user approaches an obstacle will the system make the obstructed area visible to remind the user.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/245aca6f27004ca5beb207cdeabcaa43~tplv-goo7wpa0wc-image.image)
| **Scenario** | **Movement speed detection** | **Obstacle detection** | **Safety zone detection** |
| --- | --- | --- | --- |
| **In traditional OpenXR applications** <br> MR safe mode (optional for developers) | ✅ | ✅ | - |
In most cases where there is no requirement for intense physical movement in fully immersive mode, the MR safety mode without boundaries is generally sufficient. However, for applications that require users to move intensely within physical space for a fully immersive experience, it is recommended to use the VR safety mode with boundaries and to use the safety boundary prompt feature to remind users to stay within the safe area during dynamic movement. Be sure to determine the characteristics of your application's core experience and, after comprehensive consideration of security, select an appropriate security mode to ensure user comfort and safety during immersive experiences.
For OpenXR applications with a non-unified rendering architecture, VR safety is used by default, and developers can choose MR safety.

