This document provides a detailed modeling guide using Blender as an example. You may also use other third-party 3D software to create models.
## **Units and scale**

* **Unit**
   Used to ensure that the model's unit scale is correct in the editor.
* **Key concepts**
   * System unit: standard for physical calculations (recommended: metric units)
   * Display unit = Window visual reference
* **Unit settings**
   1. Find the Properties Panel on the right side of the Blender main window.
   2. Select the Scene icon > Units > Length.
   3. Length defaults to meters.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e0c2c1ae15a4cb4b5df38053e829336~tplv-goo7wpa0wc-image.image)

### Topology optimization
Optimization strategies: Use more quadrilateral faces, eliminate triangles and n-gons, and ensure edge loops follow the structure.

* **Topology check**
   * Wiring type (for example: three-sided panel / four-sided panel)
   * Grid density
   * Geometric integrity
* **Recommendations for handling**
   * Proportion of quadrilaterals ≥ 95%
   * A uniform distribution without abrupt changes
   * No free vertices or overlapping faces

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3894d39b50d24716a6604f0dd4614dcd~tplv-goo7wpa0wc-image.image)
### **Normal direction**

* **Redundant normals**
   * Meshes imported from other applications.
   * Geometry generated through complex operations (such as Boolean objects).
* **View normals**
   * When an object is created, normals are generated automatically. Using these default normals ensures that the object is rendered correctly.
   * Sometimes it is necessary to adjust normals and check the direction of the model's normals. If the interior is inverted or there are holes, some normals may point in the wrong direction.
* **Show normal**
   Viewport Shading > Geometry > Enable Face Orientation. By default, blue indicates the correct normal direction, while red indicates the incorrect normal direction.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c22b1ddafaea4fba9e2fa4b323d27e1a~tplv-goo7wpa0wc-image.image)
* **Flip normals**
   Edit Mode > Mesh > Normals > Flip. This tool is only used to reverse the normal direction of selected faces.
   Since only the normal direction of selected faces will be flipped, it can be used to precisely adjust the normal direction (not the orientation; the normal is always perpendicular to the face).

* **Recalculation**
   * Edit Mode > Mesh > Normals > Recalculate Outside
   * Edit mode > Mesh > Normals > Recalculate inside
   * Keyboard shortcuts: `Shift + N` and `Shift + Ctrl + N`
   These tools will recalculate the normals of the selected faces so that they point toward the exterior (or interior) of the body to which the faces belong. The body does not need to be closed; the interior and exterior are determined by the angles between adjacent faces. This means that the face of interest must be adjacent to at least one other face that is not coplanar. For example, for grid primitives, recalculating normals does not produce meaningful results.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8c7ba4e8baf24b798b7b5bfff217cb00~tplv-goo7wpa0wc-image.image)

### Smooth shading and flat shading
The appearance of mesh edges in the 3D viewport and rendering is either uniform or well-defined. In editing mode, you can select individual faces to determine which ones are smoothed or flattened.
**Smooth shading**

* Right-click the model > Object > Shade Smooth.
* When interpolated vertex normals are used, the edges of mesh surfaces become blurred, resulting in a smoother appearance.

**Flat coloring**

* Right-click the model > Object > Shade Flat.
* Face normals are displayed uniformly, making the edges of all selected meshes easier to see.

### Coordinate system

* **Global**
   Align the transformation axis with world space. The world axis is displayed both in the navigation controller in the upper right corner of the viewport and in the grid plane.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2b69aee007e5444188da3f8258b210d3~tplv-goo7wpa0wc-image.image)
* **Partial**
   Align the transformation axis with the direction of the active object.
* **Universal joint**
   The oriented transformation axis mode is a visual object rotation mode in which the object rotates around one axis at a time. In this mode, the rotation axes are not necessarily perpendicular to each other and may even overlap, a phenomenon known as gimbal lock, which increases the complexity of animation.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/84a2982ca4354e00b632f309fd7674d2~tplv-goo7wpa0wc-image.image)
* **View**
   Align the transformation axes with the view (this means they will change as you orbit):
   * X: Left/right
   * Y: up/down
   * Z: Approach the screen / move away from the screen
* **Cursor**
   Align the transformation axis with the 3D cursor.
* **Parent**
   Align the transform axis to its parent.

### Example
A cube with rotation manipulators active in multiple transformation coordinate systems.
| **Description** | **Illustration** |
| --- | --- |
| Selected the global transformation coordinate system and its default cube. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/57d4f889dbec45c79630cb9eac7a7a4e~tplv-goo7wpa0wc-image.image) |
| A cube rotated in the global coordinate system; the controller remains unchanged. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a6813a85bba84c099115562a71531281~tplv-goo7wpa0wc-image.image) |
| Local orientation: Controller rotation matches object rotation | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6dba84c662d492581f73d2e93fd86c5~tplv-goo7wpa0wc-image.image) |
| Gimbal coordinate system | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7f2b4441a20c4556ab48279025307b39~tplv-goo7wpa0wc-image.image) |
| The view transformation coordinate system | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a158d04c7df4356a7681657c1cf2985~tplv-goo7wpa0wc-image.image) |
| The parent transformation coordinate system. The cube uses the rotating empty object as its parent. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/774fd779d70d4db4b146e10371f51535~tplv-goo7wpa0wc-image.image) |
### UV operations
Edit Mode > Title bar > UV. Blender provides several UV mapping methods. A relatively simple projection method uses a mapping formula from three-dimensional space to two-dimensional space to interpolate the position of a point on the surface defined by a point, axis, or plane. More advanced methods can be applied to more complex models and have more specific applications.

* **Expand**
   * Edit mode > UV > Unwrap
   * Shortcut key: `U`
   * Flatten the mesh cut along the [seams](https://docs.blender.org/manual/en/4.2/modeling/meshes/uv/unwrapping/seams.html); it can be used for organic shapes.
   * Select all faces you want to expand. In the 3D view, select **UV > Unwrap** from the menu, or press `U` and select Unwrap. You can also use **UV > Unwrap** or `U` in the UV editor to perform this operation. This method will unwrap all faces and reset previous results. Once expanded, the UV menu will appear in the UV editor.
   * The UV texture of a surface only requires a portion of the image, rather than the entire image. Similarly, multiple surfaces can share the same part of an image, which reduces the image area occupied after mapping.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/04f7fd6910bc4fa69a715d697611ddd9~tplv-goo7wpa0wc-image.image)
* **Intelligent UV projection**
   * Edit mode > UV > Smart UV projection.
   * Intelligent UV projection can segment the mesh based on an angle threshold (changes in angle within the mesh), allowing for precise control over how automatic seams are created. This is an effective approach for simple geometric shapes, such as mechanical objects or buildings.
   * The algorithm examines the shape of the object, the selected faces, and their relationships, and creates a UV map based on this information and the settings you provide.
   As shown in the following example, intelligent projection maps all faces of the cube into a neat arrangement of three faces on the top and three on the bottom. The six faces of the cube are adjusted to be square, just like the surface of the original object. For more complex mechanical objects, this operation can quickly and easily create regular and intuitive UV layouts.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b62bce33b156437bb18e87e971dd6d2b~tplv-goo7wpa0wc-image.image)
* **Cube projection**
   Cube projection maps the mesh onto the surface of an unfolded cube. The mesh is projected onto six separate planes, creating six UV islands. Overlapping may occur in the UV/Image Editor, but they can be moved.
* **Cylindrical projection**
   To unwrap a cylinder (tube), you need to cut it longitudinally and flatten it. Blender prefers the view to be vertical, with the tube standing upright. Different views project the tube onto the UV map in different ways, which can distort the image if these projections are used directly. However, you can manually set the axis for the calculation.
* **Spherical projection**
   Spherical projection is similar to cylindrical projection, but the difference is that cylindrical projection projects the UV onto a plane as if wrapping a cylinder, while spherical projection takes the curvature of the sphere into account, resulting in evenly spaced latitude lines. Spherical projection is suitable for spherical shapes, such as eyes, planets, and so on.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aec136e080a0458bb9adbc539db95a6c~tplv-goo7wpa0wc-image.image)
* **Reset**
   * Reset UVs, mapping each face to fill the entire UV grid and assigning the same mapping to each face.
   * If a repeatable image is used, the object's surface will be covered with repeated instances of that image, and the image will be adjusted to fit each individual face. Use this expand option to reset mappings and undo all expansions, returning to the initial state.

## PBR material
The engine supports Shader Graph, Cloth, Unlit, and PBR materials, as well as blend modes including Fade, Additive, Masked, Transparent, and Opaque.

* PBR material sphere: base color, metallic, roughness, normal
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8cc7e4a379fb457dab8f9d583cf4c230~tplv-goo7wpa0wc-image.image)
* Examples of the final effects of 3D materials
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dc5cba1f8ab04876a9489247a0a397e8~tplv-goo7wpa0wc-image.image)

### Material specifications

* **Material types**
   * Metal (such as gold or iron): The reflected color is determined by the environment, Metallic = 1.
   * Non-metals (such as plastic and wood): Albedo is the inherent color, Metallic = 0.
   * For hybrid materials, such as rusted metal, you need to define the metallic transition area.
* **Base texture**
   * Albedo (Base Color): base color.
   * Normal Map: A normal map is a texture that affects the bumps and dents on an object's surface.
   * Metallic map: Controls Fresnel reflection.
   * Roughness Map: Controls the microscopic roughness of the surface.
   * Ambient Occlusion: AO simulates the effect of ambient light occlusion.
* **Extended texture**
   * Height Map: Used for parallax displacement effect.
   * Emissive Map: self-illuminated areas (such as signs, screens).
   * Opacity Map: transparent materials (for example, leaves and glass).

### Texture guidelines

* Common texture formats: png, tga, jpeg, and other image formats.
* Common texture sizes: 512x512, 1024x1024, and 2048x2048 (all are powers of two).
* Texture size adaptation for devices (recommended within 2K resolution)

**Base Color** (**Base Color**) defines the basic color of a material and does not include lighting or shadow information.

* Format: sRGB color space
* Naming structure: for example, TeaSet_b

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d2f3bc5635eb411590b13cf2ccedaa08~tplv-goo7wpa0wc-image.image)
**Metallic map (Metallic)** identifies metal (white) and non-metal (black) areas

* Format: grayscale image.
* Control the Fresnel reflection
* Blended materials use grayscale transition.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/414a24488e3e4e33995a735c6bd09c22~tplv-goo7wpa0wc-image.image)
**Roughness** controls the surface roughness (black = smooth, white = rough).

* Format: Grayscale image controls the microscopic roughness of the surface.
* Determine whether the material exhibits specular reflection or diffuse reflection.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ed37ac69a3434d159da6ff6840eebb61~tplv-goo7wpa0wc-image.image)
**Ambient Occlusion** simulates ambient light occlusion effects, enhancing shadows in crevices and recessed areas.

* Format: grayscale image

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d29a1ef0305462db7d7043bf982918d~tplv-goo7wpa0wc-image.image)
**Normal** simulates the bumpiness of fine surface details, such as scratches and brick joints

* Format: tangent space normal map (typically bluish purple, 8-bit or 16-bit).
* Without altering the actual geometry of the model, enhance the expression of light and shadow details.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/627ae2aaf41c4e83bd804a5beafdcfdf~tplv-goo7wpa0wc-image.image)
**Opacity** controls material transparency

* Use hard-edged alpha (avoid semi-transparent gradients unless feathering is required).
* For PNG images, remove transparent areas and retain visible content.
* TGA (with alpha channel; by convention, black is transparent and white is opaque).
* Control hollowed-out areas, such as leaves.
* Naming structure: for example, Earth_o.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/43228e6a483548d3a78610ce3fd9ba58~tplv-goo7wpa0wc-image.image)
Effects of Opacity:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5199530516431925);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/281fede13850472da14d42ce176c9f08~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.4800469483568075);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8062bcbae654be0a00ae796d1228c88~tplv-goo7wpa0wc-image.image)


</div>
</div>

Engine PBR material sphere: Base Color), Opacity, Roughness, Normal.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/05878377978d47a8b64ca23ec3185d3a~tplv-goo7wpa0wc-image.image)
**Emission** makes the material emit light

* Does not actually illuminate the scene (unless global illumination is used).
* Simulated light sources, display screens, and more.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/94393ff14621425aa3fa2689dcb9c2d0~tplv-goo7wpa0wc-image.image)
Example of Emission Effect:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3b8b233c170d47aebf91fe68f79dca7c~tplv-goo7wpa0wc-image.image)
Engine PBR material sphere: Base Color, Metallic, Roughness, Normal, and Emissive.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1b1c925ef3c8402bb795d95f159680d3~tplv-goo7wpa0wc-image.image)
# 
