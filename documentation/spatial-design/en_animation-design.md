This article provides a detailed guide to animation rigging using Blender as an example. You can also create animations using other third-party 3D software and complete the work in any 3D software that supports skinning.
## **Model specification checks**
This is used to ensure that no abnormal deformation occurs during subsequent binding.

* **Single closed mesh check** (entire mesh with seamless connections)
   * Multiple independent meshes can cause inconsistency in weight assignment and result in tearing during animation.
   * Issues may occur when creating collision bodies or navigation meshes in game development
   * Overlapping surfaces and reversed normals can cause rendering issues.
* **Geometry cleanup**
   * Select a model
   * In Edit Mode > Mesh > Cleanup
   * A panel will appear in the view
   * Select the types of issues to check:
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/359afd0cbe704bcdbe0e548b22080805~tplv-goo7wpa0wc-image.image)

## Framework
In Blender, an armature can be thought of as a skeleton similar to a real one. An armature can consist of many bones, and each bone can move freely. Anything attached to or associated with these bones will move and deform in a similar way.

* Armature: In Blender, an armature is an object type, just like a mesh object or a light. A skeleton object consists of multiple bones.
* Bone: A single element in the skeleton. They have a "root" and a "tip", which define their length and direction.
* Parenting: Bones can be connected to form hierarchical relationships. For example, the forearm bone is a child of the upper arm bone, and the hand bone is a child of the forearm bone. When the parent bone is moved, all of its child bones will move as well.

**Framework structure**

1. Hover the mouse over the 3D view, then press `Shift + A` to open the Add menu.
2. Select the armature (Armature).
3. A simple single-bone armature (named "Armature") will appear at the position of the 3D cursor.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fdb9c362b84d4674aca5f43cd17cfbba~tplv-goo7wpa0wc-image.image)

**Skeleton chain**
Bones within a skeleton can be completely independent of one another; for example, modifying one bone does not affect the others. However, the recommended approach is to connect one bone to another parent bone within the skeleton to create a bone chain. These bone chains can have branches. For example, five "finger bones" are connected to a single "hand bone".
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d4e0777a0724bf29e9912956eec7b5a~tplv-goo7wpa0wc-image.image)
### **Skeleton rigging recommendations**

* Keep joint directions in the skeletal chain as consistent as possible to avoid axial flipping when the controller is rotated.
* Joint spacing should be as reasonable and logical as possible for a more natural effect when bending.

| **Joint types** | **Recommended spacing** | **Cause** |
| --- | --- | --- |
| **Spinal joints** | Equidistant distribution | Ensure the curvature of the bend is natural |
| **Finger joints** | Decreasing distribution | Meet biomechanical proportions |

* End bones without weights can be removed to reduce engine load.
* Bone names distinguish left and right; for example: L\R Left\Right.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e2fc7897c5e349e78aee13d07a435c9d~tplv-goo7wpa0wc-image.image)
### **Control the skeleton (IK)**
**The working principle of the IK system**

* **Mathematical foundations**: The inverse kinematics solver (IK Handle) determines joint positions using trigonometric calculations
* **Effect demonstration**:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/92ee39c0b3d14061b5ad9f339734a2b8~tplv-goo7wpa0wc-image.image)
1. **Create an IK control**
   Enter Edit Mode > Select the driver bone for inverse kinematics > Create a control bone ("E" key) > Select the control bone and clear the parent relationship (Clear Parent) > Rename > Enter Pose Mode > Bone Constraints > Select the last affected bone > Add Bone Constraint > Inverse Kinematics > Set Target (Target > Armature) > Set IK target bone (Bone > IK control bone) > Adjust chain length (Chain Length)
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/05276c30624343a993f3cb04160096cc~tplv-goo7wpa0wc-image.image)
2. **Create a polar target**
   Enter Edit Mode > Select the driver bone for the pole vector > Create a control bone (press the "E" key) > Select the control bone and use Clear Parent > Rename > Enter Pose Mode > Bone Constraints > Set the pole vector target (Pole_Target > Armature) > Set the pole bone (Pole Bone > bone for limiting control) > Adjust the angle (Pole Angle).
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/562b30fd9e044ccda3e8cacb9fa50e2c~tplv-goo7wpa0wc-image.image)
3. **Set IK bones as control bones**
   Enter Pose Mode > Select the driver bone > Bone page > Deselect Deform.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9f38adf62a684485b689bb97a7f20df9~tplv-goo7wpa0wc-image.image)

## Skin weight painting
The weight painting mechanism is used as a key step in controlling the quality of model deformation.

* **Use automatic skinning weights**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eeb4ac44250a45c6b95815f83f4fba11~tplv-goo7wpa0wc-image.image)
* **The golden rule of weight painting**
   * **Principle of gradual transition**: A 50%-50% weight distribution is used in the joint boundary region.
   * **Joint weight distribution model**:
      | Main joint | 70%-100% |
      | --- | --- |
      | Transition zone | 30%-70% |
      | Secondary joint | 0%-30% |
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5949b01e7e574e87bbd21861046684e2~tplv-goo7wpa0wc-image.image)
* **Custom weight rendering**
   Enter Object Mode > select the armature > also select the mesh to be weight painted > enter Weight Mode > enable Auto Normalize > paint weights > smooth weights.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d74955967f8c43299547c15adadfd896~tplv-goo7wpa0wc-image.image)

## Key points of animation production
### **Keyframe animation principles**
In Blender, keyframe animation is the foundation for creating all animation. You only need to define the state of the object at specific points in time (keyframes). Blender will automatically calculate the intermediate transition frames to create a smooth animation.
**Core concepts:**

* Keyframe: A marker that records the value of specific attributes of an object at a particular moment, such as position, rotation, scale, color, shape, and more. You do not need to set each frame manually; simply set a few keyframes at important moments, and Blender will handle the interpolation.
* Timeline: The central control hub for animation production, where you can move the current frame, set keyframes, and preview the animation sequence.
* Interpolation: The method for calculating values between two keyframes. Blender provides several interpolation modes, such as linear, Bézier, elastic, and more, to control the rhythm and feel of animation movement.

### Keyframe animations
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4441d71f7b514b108af46878980366dc~tplv-goo7wpa0wc-image.image)

1. Select an object: Select the object, bone, or even a material property to be animated.
2. Move to the starting frame: On the timeline, drag the green playhead to the point where you want the animation to start, for example, frame 1.
3. Set the first keyframe
   1. Hover the mouse over the property that requires animation (for example, the position of an object in the 3D view, or the value of a modifier property).
   2. Press the shortcut key `I`, and a menu will pop up. Select the type of keyframe to record.
   3. For moving, rotating, or scaling an object, the most commonly used properties are `Location`, `Rotation`, `Scale`, or `All animatable attributes`.
   4. After the setting is applied, a yellow marker line will appear on the corresponding frame in the timeline, and the property field will also turn yellow. This indicates that the property has a keyframe at that frame.
4. Move to the end frame: Drag the playhead to the time point where the animation ends (for example, frame 60).
5. Change attributes and set the second keyframe:
   1. Move, rotate, or scale objects in the 3D view, or modify values in the properties panel.
   2. Press the `I` key again, and select the same keyframe type to set the second keyframe.
6. Play animation: Press the play button on the timeline (or press the `Space` key), and Blender will automatically play the animation between the two created points.

## Naming recommendations
### **Naming conventions**
Used to ensure the engine correctly recognizes the skeletal system.

* Skeleton naming does not contain duplicate names
* Naming prefix: It is recommended to use bone prefixes or suffixes according to project requirements, such as `L\R Left\Right`

### File export format (final integrated export, including models, animations, and formats) to be determined

* USD format
   * `.usdc`: A variant of the binary format.
   * `.usdz`: Uncompressed USD archive format.
   * Case illustration:
      * Exporting a `.usdc` file includes a texture folder
         ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c5ed47781c7443b7bcc5c50864ce1e3c~tplv-goo7wpa0wc-image.image)
      * `.usdz` is a single file, with textures included within the file
         ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9cff0d11ea7849768501521e419557bb~tplv-goo7wpa0wc-image.image)



