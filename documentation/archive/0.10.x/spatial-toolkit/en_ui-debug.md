Key logic such as collision volumes, spatial anchors, and environmental occlusion is often 'invisible', making debugging UI elements in 3D space more complex than on a 2D screen. The UI debugging feature of PICO Emulator is designed to break this black box, converting underlying spatial data into intuitive 3D auxiliary graphics to help you move away from reliance on log output and significantly improve debugging efficiency.
On the debugging page of the **Setting** panel, you can select one or more UI debugging modes.

* **Show Audio**: Displays audio components in the scene.
* **Show Axis**: Displays the axes of each 3D model (that is, entities containing a mesh) in the scene. The axes of 2D UI components will not be displayed.
* **Show Collision Bounds**: Displays the collision bounding boxes of each entity with a Collision component and 2D UI components in the scene.
* **Show Mesh Bounds**: Displays the model bounding boxes of each 3D model (that is, entities containing a mesh) in the scene. The type of bounding box is AABB (Axis-Aligned Bounding Box). The model bounding boxes of 2D UI components will not be displayed.
* **Show Window Container Bounds**: Displays the bounding boxes of spatial containers in the scene.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5623085e87bf40a1b01362c4cebf46f5~tplv-goo7wpa0wc-image.image)
## Show Audio
Select **Show Audio** to view the position of audio sources in space. For Object Audio components, you can also view the orientation of the audio source.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/10e3c16d0ee243d09e613c59e5f14e20~tplv-goo7wpa0wc-image.image)
## Show Axis
Select **Show Axis** to view the spatial positioning of 3D models.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3807268e4cf540539f8f3ef7943a6a15~tplv-goo7wpa0wc-image.image)
## Show Collision Bounds
Select **Show Collision Bounds** to view the collision areas of 2D UI components and 3D models.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/448fc9d03a414444a32e349543005fbc~tplv-goo7wpa0wc-image.image)
## Show Mesh Bounds
Select **Show Mesh Bounds** to view the actual occupied space of 3D models.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a6c18cad69c94a2e8739243f7cc8efbb~tplv-goo7wpa0wc-image.image)
## Show Window Container Bounds
Select **Show Window Container Bounds** to view the actual occupied space of spatial containers.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/37dc254658cf4abebf89a94b38f6cc81~tplv-goo7wpa0wc-image.image)

