This article explains how to provide assets in different scenarios.
We recommend first learning about "[Editor and USD](/en_spatial-editor-and-usd)."

## Provide assets for Spatial Model View in traditional 2D windows

* There are no specific requirements for the position of assets when in use; they will completely follow the window anchor position.
* Spatial Model View is used for embedding 3D content within traditional 2D windows. The SDK automatically adjusts the model size to fit within the window by reading the asset’s maximum bounding box, comparing it with the window size, and then adjusting the asset's volume accordingly.
* Although Spatial Model View can automatically adapt asset size, it is still recommended to create such assets according to the object's actual size in the real world to enhance their reusability in spatial applications.
* During production in DCC software, if multiple meshes are exported as a single asset, their bounding boxes will be merged for calculation. Typically, the longest side of an object's bounding box is used as the edge length of a cube to fit the shortest side of the window, so users do not need to merge meshes or adjust the centroid in Digital Content Creation software.
* If you export to usdz, the PBR base material set in the DCC software will be used as the material in the engine. If you use the editor and perform material editing, the result from the editor takes precedence. In actual head-mounted devices, the device evaluates the real environment, simulates lighting for objects, and maintains the appearance of materials.
* When providing assets for this mode, you must be clear about usage requirements. When using a large number of models in the same window, minimize texture size and the number of textures used, and optimize the polygon count of the models. Otherwise, this may cause loading delays or window freezing. For example, limit the polygon count of a single model to around 1,500 and use textures with a resolution of 1024*1024.
* Take Blender as an example:
   * The Blender rendering is shown below. Spatial Model View does not restrict the model position, but it is still recommended to place the model at the world origin.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ae8dbfc0a0f3469195a263f9af917c58~tplv-goo7wpa0wc-image.image)
   * Screenshot showing the actual effect (with a window size of 300 DP as an example, that is, the red area).
      | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b47b11cd05084e1f91ba208f4aedbced~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/54c0f5ac06974be7839bd7d8390f8655~tplv-goo7wpa0wc-image.image) |
      | --- | --- |
   * Below is an illustration showing the effect after adjustment in Blender. Combine multiple objects and output them together.
      There is no need to adjust the center of mass of each object; USD describes their spatial relationships using the maximum extent of all objects' bounding boxes.

      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d1eba453bb749199244b2d2ba802751~tplv-goo7wpa0wc-image.image)
   * In the screenshot of the actual effect after adjustment, you can see that Spatial Model View integrates the BoundingBox of the entire scene and adapts accordingly.
      | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/37d517c2297b46d08e1b52fe471e3556~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd550376b5c34b3c9baf1ff04d27a23e~tplv-goo7wpa0wc-image.image) |
      | --- | --- |

## Provide assets for Spatial View in Volumetric mode

* PICO official definition: Describes the position of a model in Spatial View, which refers to the offset of the model root relative to the back face center of Spatial View. In Spatial SDK, you can set the width, height, and depth of a volumetric window in meters as a reference for DCC software.
* For artists or asset providers, using Blender's world space as an example, the negative direction of the y-axis represents depth, and depth increases only in the negative direction. The x-axis represents width, and the z-axis represents height.
* For example, in Blender, suppose you use 1 cubic meter of space in PICO's volumetric window:
   * Blender effect screenshot: We created a 1-cubic-meter “cage” in Blender according to the rules to experience volumetric window.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/af61a035571f4685968eba2368bde60b~tplv-goo7wpa0wc-image.image)
   * Screenshot of the actual effect, showing that the "cage" has been rendered.
      | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0cbd8ba4d1454626883676c427d840eb~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1a1afad16eee4bb28c38798c13722e2f~tplv-goo7wpa0wc-image.image) |
      | --- | --- |
   * Combine more assets in Blender's workspace, combine various fruits, and intentionally make one of them go beyond the "cage".
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b3eda713cc8e485d8d135652bbe2c3ab~tplv-goo7wpa0wc-image.image)
   * The actual effect is illustrated below. Observe the cropping effect.
      | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c74c55ca70744390a2aa99dc1638a14a~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b6bc858e1d1745e9aa2b6ef6067d8d38~tplv-goo7wpa0wc-image.image) |
      | --- | --- |

## Provide assets for Spatial View in Stage mode

* When PICO officially describes the position of Spatial View within a window, it refers to the spatial origin of Spatial View, which is defined as the intersection point where the user's headset is vertically aligned with the ground. This point serves as the world space origin, and models are offset with respect to this point. The space in Spatial View is infinite.
* In Blender's world space, the world origin can be considered the position where the headset user stands, with a height of zero.
* From the perspective of real-world physics, users have visual blind spots. When the head-mounted device enters Stage mode, it simulates the wearer's vision, but blind spots still exist. Therefore, when arranging resources in Stage mode, you need to take this into account: consider what users can see when wearing the device in a normal, forward-facing position, and what becomes visible only when they change their viewing angle.
* Take Blender as an Example:
   * The Blender rendering is shown below. When creating elements for Stage in Blender, you should try to use real-world proportions as much as possible, except for special purposes. You can adjust the overall scale later in the editor or in code.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a3e4fc0145fb4906955aa437b973d2e4~tplv-goo7wpa0wc-image.image)
   * The actual effect diagram is shown below. Stage utilizes the real-world proportions of the space around the user, and especially in mixed mode, it blends seamlessly with reality. The currently visible assets do not have shadows; shadows can be simulated using editor enhancements.
      | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2736dc33e723419e9856ff5e091e0776~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6d54755ba4d4f65bb7149bface3874e~tplv-goo7wpa0wc-image.image) |
      | --- | --- |





