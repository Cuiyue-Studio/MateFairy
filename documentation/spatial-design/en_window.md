## Planar windows
A Planar window is a container used to present interfaces and tasks that users are familiar with. If the main content in your application needs to be presented on a single plane, you should consider using a Planar window. 3D content can also be displayed in a Planar window.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6932033fe57041f9ab803ef9ecc0f5b1~tplv-goo7wpa0wc-image.image)
### Dimensions
The default size of the Planar window is 1280 × 720 dp, and developers can define the initial size of the window.

* The initial size should match the content to avoid excessive blank areas or a window that is too large and obstructs the user's view.
* The window size should be no less than 320 × 180 dp, as a smaller window will significantly reduce the efficiency of information display. It should also be no greater than 2700 × 1800 dp, since a larger window requires greater postural energy consumption for users to view the entire content and may obstruct their line of sight, resulting in potential safety hazards.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/27c0e74683cd4d2b8379d1be8b6dce68~tplv-goo7wpa0wc-image.image)
### Ratio
By default, the Planar window launches at a position 2.5 meters in front of the wearer. The wearer can adjust the distance between the window and themselves. During distance adjustment, the window dynamically resizes by default. Dynamic scaling ensures that graphical and textual information in flat windows remains clear and easy to read, no matter how far they are from the user, and that interactive objects can be operated effortlessly at any distance.

* When the window moves away from the wearer, PICO OS 6 automatically increases its scaling ratio.
* When the window approaches the wearer, PICO OS 6 automatically reduces its scaling ratio.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e115710130af46428abb02c83a6c8641~tplv-goo7wpa0wc-image.image)
With fixed zoom, when the window moves away from the viewer along the Z axis, it appears smaller and smaller; conversely, when the window moves toward the viewer along the Z axis, it appears larger and larger. This is similar to the effect in real life where objects appear larger when they are closer and smaller when they are farther away. If the content in your window needs to simulate the size of real-world objects, you can choose fixed scaling.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/36852716e75a411d8c298420e040e45f~tplv-goo7wpa0wc-image.image)
### Add 3D content to the Planar window
When 3D content is placed in a Planar window, it extends forward along the Z axis to highlight its three-dimensional features.

* When placing, consider orienting the most valuable information in the 3D content toward the user.
* It is recommended that the placement depth of 3D content be the same as or close to that of the Planar window. If the content is placed too far away, adjustment of eye focus when reading context may cause fatigue. Additionally, if content extends too far beyond the window surface, the system will truncate it. All content within the window needs to be displayed within a depth range of 450 dp.
* If you want to add multiple 3D content items to a Planar window, it is recommended that they be placed at the same depth.
* When resizing the window, it is recommended that 3D content scale proportionally with the window. If your 3D content needs to simulate the actual size in the physical world, we recommend using the Volume window.
* When laying out, leave enough space around 3D content to avoid blocking other surrounding elements.
* If the 3D content to be displayed does not have a contextual relationship with other 2D content, or if you need to present 3D content with greater depth, or if you want users to have more interaction with the 3D content, consider using the Volumetric window.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/54aa6d543d4a4502a96de5a53dff6b64~tplv-goo7wpa0wc-image.image)
## Volumetric Window
When the main content of a window is 3D content, you can set the window to be a Volumetric window. Volumetric windows and 2D windows can both accommodate 2D and 3D content, and have many similarities. However, the depth of Planar windows is limited, which prevents them from fully displaying larger 3D content and does not allow users to view and interact with 3D content from all angles. Therefore, when 3D content is the main content or the only content, Volumetric windows are a better choice.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/44775c4a711c46fc986fc20a625d9689~tplv-goo7wpa0wc-image.image)
### Dimensions
Volumetric window supports developers in defining dimensions. Depending on business requirements, the Volumetric window can be set to a fixed size or can automatically adjust according to its content. Provided the window is large enough to accommodate its content, its size should be defined as visually comfortable and easy to operate. When the window is resized, the content scales proportionally with the window.
### Content
A Volumetric window can accommodate both 2D and 3D content, and is primarily used for 3D. Developers can create content at any position within the window. The main body of the content should, by default, face the user, and any portion that extends beyond the window's dimensions will be clipped.

* The Volumetric window allows users to view the scene from multiple angles. However, when the window is opened, its front will face the user. Make sure the most important content is oriented toward the user by default.
* Content should fill the window as much as possible while leaving a safety margin from the window edges to prevent it from being cut off.
* When interactive 3D content is present, avoid placing other interactive content behind it.
* When 2D and 3D content coexist, it is necessary to fully consider the display effects and operability from different angles.

### Base panel
By default, a semi-transparent basepanel is displayed below the Volumetric window, helping users clearly perceive spatial boundaries. When there is a large area in the window that is not filled with content, the background panel helps users easily locate the window edge so they can use the drag controls. When the window content does not require the system baseboard, or when there is no need to scale the window, the baseboard can also be hidden.
### Argument
Volumetric supports flexibly adding Augment at any position. It is recommended to place Augment in positions that do not interfere with the main content. Augment automatically rotates to the side facing the user, allowing users to interact with the content while viewing it from multiple angles. Please use the Tabbar and Toolbar provided by system components whenever possible. If you need to customize Augment, refer to the Augment design guide to ensure the overall user experience of the window.
### Control
Depending on the size and intended use of the Volumetric window, developers can configure the window to always remain vertical relative to the ground or allow the window to tilt automatically. Positioning the window perpendicular to the ground allows the content to better integrate into the spatial environment and maintain a sense of stability. For example, a virtual desktop clock or a virtual billiard table must remain level at all times to comply with the laws of physics in the real world. Whereas automatic window tilting is suitable for windows that require extensive interaction, making it convenient for users to operate smoothly at any height.
## Augment
Consider using Augment to present controls and information related to windows, and to highlight important actions. Please give priority to using the Tabbar and Toolbar from the system components. If you need to customize Augment, refer to the [Augment Design Guide](/en_augment) to ensure the overall user experience of the window.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c7e00a946bdb43099d809e85d6cbfd34~tplv-goo7wpa0wc-image.image)
## Multiple windows
PICO OS 6 supports multi-window capability for applications. By default, any new window within an application appears in front of the user to ensure that its content is easily noticed and easy to consume.
If you want users to be able to interact with both the current window and any new windows opened from it at the same time, you can define rules to specify that new windows appear around the designated window.

* An appropriate distance should be maintained between the new window and the specified window, with a default value of 56 dp. Avoid loss of context caused by excessive distance, and avoid overlap of content caused by being too close.
* A new window can appear around the current window. When defining its position, consider the relationship between the contents of both windows to ensure alignment with users' natural visual habits of scanning from left to right and from top to bottom.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d88c2175a8fe403db248cbefad84bc3c~tplv-goo7wpa0wc-image.image)


