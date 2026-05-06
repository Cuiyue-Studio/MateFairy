In PICO OS 6, the spatial container is the core component used to host and manage the content of spatial apps. You can add various types of content into it, such as traditional 2D interfaces, 3D models, and various interactive components. Different spatial containers have distinct characteristics in terms of presentation and interaction capabilities. You can select the most suitable container based on your needs, enabling users to interact with content in the most natural and intuitive way.
## Spatial container
Spatial containers are divided into two categories: WindowContainer and Stage.
### WindowContainer
WindowContainer serves as the fundamental container for an app, featuring defined spatial boundaries. Content within these boundaries is defined and managed by the app, while content extending beyond them is automatically clipped. Typically, when a user opens a WindowContainer, it appears approximately **2.5 meters** in front of the user by default. Its center aligns with the current orientation of the user's headset; in other words, the center of the WindowContainer appears in the direction the user is facing.
There are two styles of WindowContainer: Planar, which primarily displays 2D content, and Volumetric, which primarily displays 3D content. An app can flexibly open one or more WindowContainers as needed.

* **Planar**
   Planar is a WindowContainer with limited thickness, similar to a "flat panel". It is primarily used to host 2D interfaces commonly found in traditional Android development, and can also be used to display smaller 3D objects.
   If you are accustomed to designing and developing user interfaces for traditional 2D apps, or plan to port existing mobile or desktop apps to PICO OS 6, Planar is the ideal starting point. It inherits the Jetpack development paradigm and integrates the spatial UI components of PICO OS 6 (Spatial UI), enabling the spatialization of classic 2D apps. You only need to focus on the interface and functionality themselves, while PICO OS 6 handles complex spatial interaction logic and window management in a unified way, greatly reducing repetitive tasks.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6932033fe57041f9ab803ef9ecc0f5b1~tplv-goo7wpa0wc-image.image)
* **Volumetric**
   Volumetric is another style of container that presents content in a window-like form and can be understood as a rectangle with dynamically adjustable dimensions. Compared to Planar containers with limited thickness, Volumetric containers occupy a larger spatial volume and can accommodate larger 3D objects, thereby maximizing the user's 3D interaction experience within a limited area.
   Volumetric is primarily used for integrating 2D and 3D content, and supports basic 3D object display and interactive operations. Since it runs in a shared space (see the next subsection for details), this container can also interact with other app windows, thus providing a richer multitasking experience.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d7adf01799564d37b3f3767a62f939d9~tplv-goo7wpa0wc-image.image)

Note that if 3D content extends beyond the boundaries of WindowContainer, it will be clipped. Therefore, if you need to place larger 3D models, it is recommended to use Stage.
### Stage
Stage can be regarded as a boundaryless "area" that supports placing additional content, including UI components, 2D layouts, 3D models, and more.
If you need greater control over the spatial layout or want to actively modify the surrounding environment, you can use a Stage. When a Stage is opened, its center is positioned at the user's feet and uses the user's current spatial pose as the reference coordinate system, adopting a right-handed coordinate system: the X-axis points to the user's right hand, the Y-axis points straight upward, and the Z-axis points toward the user (opposite to the direction the user is facing). As a result, each time a Stage is opened, its position and orientation in space are dynamically determined based on the current position and orientation of the user's HMD. When the user's position or orientation changes, the Stage's spatial alignment is updated accordingly, ensuring that the interaction experience remains consistent and accurate.
In Stage, 3D and 2D content can be arranged more freely, and you can also apply for higher-level interaction and perception permissions to unlock hand pose operations, spatial anchors, plane detection, and other mixed reality capabilities. As the name suggests, this "stage" is the dedicated scenario for your app, where you can create unique environments and experiences—whether fully virtual or a blend of virtual and real.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/51c3e12c453d4b03a19ac08733f301ea~tplv-goo7wpa0wc-image.image)
Based on different levels of `immersion`, Stage is divided into different styles, allowing users to interact with their real environment at different levels of immersion.
| **Style** | **Description** |
| --- | --- |
| Automatic | Style is determined by the system. Currently, the system's default setting is Mixed mode. |
| Mixed | `immersion` is 0. The app will overlay virtual objects onto the user's real environment, allowing them to blend naturally into the background environment. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d749dc88ccb4cbcb98488f672b17028~tplv-goo7wpa0wc-image.image) |
| Progressive | `immersion` is between 0 and 100. Users can manually adjust the app's immersion level through the system UI. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6eb4b7561ae04c02ab79aaa88899e0cb~tplv-goo7wpa0wc-image.image) |
| Full | `immersion` is 100. The app places users in a virtual environment that is completely isolated from the real world. If a skybox is not configured for Stage, the virtual environment will appear with a solid black background to users. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7a6ae5c4fc048dc81eef749f43fb866~tplv-goo7wpa0wc-image.image) |
## Space state
WindowContainer and Stage run in two space states: Shared Space and Full Space.
| **Space State** | **Description** |
| --- | --- |
| Shared Space | In Shared Space state, a space can be occupied by multiple apps simultaneously, supporting multi-app collaboration and content integration. <br> Shared Space only supports Planar and Volumetric containers. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c4cd8d34ef24468db2c74ec2d1f4bcfd~tplv-goo7wpa0wc-image.image) |
| Full Space | In the Full Space state, a space is exclusively occupied by a single app. When the foreground app opens Stage, the system switches the space to Full Space state. At this point, the app fully occupies the current space, and the windows of other apps are sent to the background. <br> In Full Space, a foreground app can simultaneously place one Stage and multiple WindowContainers within the space. After Stage is closed, the space state will automatically switch to Shared Space. <br> Full Space supports Planar, Volumetric, and Stage containers. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/78cd54c2374e4e7dafefc42b4e43363e~tplv-goo7wpa0wc-image.image) |
Assume that when an app starts, you first open a WindowContainer, then a Stage, and finally close the Stage:

* When the WindowContainer is opened, the spatial state remains Shared Space.
* When the Stage is opened, the spatial state switches to Full Space.
* When the Stage is closed, the state reverts to Shared Space, allowing the space to be shared by multiple apps again.


PICO OS 6 dynamically adopts targeted performance optimization strategies based on different space states:

* In the Shared Space state, the system has conducted in-depth optimization of rendering hierarchies for multitasking scenarios. Through mechanisms such as task priority scheduling, dynamic resource allocation, and layer composition optimization, the system ensures smooth visuals and timely operational response even when multiple apps are running simultaneously, thereby maintaining the user experience in a multi-window environment.
* In the Full Space state, the system further enhances rendering performance, focusing on optimizing global illumination, material detail, and interaction latency control. Even in highly complex 3D scenes, it delivers a high-fidelity, low-latency, stable, and reliable immersive experience.




