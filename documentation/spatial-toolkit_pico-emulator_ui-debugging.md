Key logical elements such as collision volume, spatial anchor, and environmental occlusion are often "invisible," making debugging UI elements in 3D space more complex than on a 2D screen. The UI debugging feature of PICO Emulator is designed to break this black box, converting underlying spatial data into intuitive 3D auxiliary graphics to help you break free from reliance on log output and significantly improve debugging efficiency.
On the debugging page of the **Settings** panel, you can select one or more UI debugging modes.

* **Audio**: Displays audio components in the scene.
* **Axes**: Displays the axes of each 3D model (that is, entities containing a mesh) in the scene. The axes of 2D UI components are not displayed.
* **Collision Bounds**: Displays the collision bounds of each entity with a Collision component and each 2D UI component in the scene.
* **Container Bounds**: Displays the bounding box of spatial containers in the scene.
* **Container Clipping**: For models clipped beyond the container range, the clipped parts are highlighted in yellow.
* **Model Bounds**: Displays the model bounds of each 3D model (that is, entities containing a mesh) in the scene. The type of bounding box is AABB (Axis-Aligned Bounding Box). The model bounds of 2D UI components are not displayed.
* **Plane Detection**: Displays planes detected by the system. Each plane has a semantic label. Plane detection is a key environmental perception technology in augmented reality (AR) and mixed reality (MR), used to help the system identify planes in the real world so that virtual objects can interact and merge precisely with the real environment.
* **Spatial anchor**: Displays spatial anchors. The spatial anchor feature binds the position in the virtual environment to the position in the real world, anchoring virtual content at a specified position. Spatial anchors can only be used when the application is in Full Space status (that is, in Stage).
* **Spatial Mesh**: Displays the spatial mesh. Each mesh has a semantic label. Spatial mesh captures and reconstructs information about real objects in space.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5036c7d1ebce43a486379969a0e3b761~tplv-goo7wpa0wc-image.image)
## Audio
Select **Audio** to view the position of the audio source in space. For the Object Audio component, you can also view the orientation of the audio source.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a30f77f3b9764e76afbe0ae85845bb4a~tplv-goo7wpa0wc-image.image)
## Axes
Select **Axes** to view the spatial positioning of the 3D model.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/914f146a3f024cdebfb7ccc4046b5365~tplv-goo7wpa0wc-image.image)
## Collision Bounds
Select **Collision Bounds** to view the collision areas of 2D UI components and 3D models.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdf0c1933da749b9afe42f8d22a68422~tplv-goo7wpa0wc-image.image)
## Container Bounds
Select **Container Bounds** to view the actual occupied space of the spatial container.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/78d18cc658244eb38cdf3a6f5a337537~tplv-goo7wpa0wc-image.image)
## Container Clipping
Select **Container Clipping** to view the parts of the model that are clipped outside the container range.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b3bb4f76b14f4fefbc59c2ba0d34c5b5~tplv-goo7wpa0wc-image.image)
## Model Bounds
Select **Model Bounds** to view the actual occupied space of the 3D model.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7a877170e51543cb9d680c0ea08c09c0~tplv-goo7wpa0wc-image.image)
## Plane Detection
Select **Plane Detection** to view the planes detected by the system in the real world.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/46cf966404584f44a43575d2efee08b5~tplv-goo7wpa0wc-image.image)
## **Spatial Anchor**
Select **Spatial Anchor** to view the spatial anchor.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/94a012405220482584d43c1af2411f48~tplv-goo7wpa0wc-image.image)
## **Spatial Mesh**
Select **Spatial Mesh** to view the Spatial Mesh.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7997027c133b4c60bcb77a372ea3f99c~tplv-goo7wpa0wc-image.image)

