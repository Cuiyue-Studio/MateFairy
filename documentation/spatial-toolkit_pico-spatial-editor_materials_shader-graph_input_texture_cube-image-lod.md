Generate and configure cube textures. Compared to the **Cube Image** node, this node adds the LOD parameter.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3679f82d64ad40cb9aace337b4389509~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: The image file used for this texture. Supports map formats including .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr.
   You need to ensure that the map's **Texture Format** is **Cube**. You can select the map in the **Project Browser** window and then view the map's **Texture Format** in the window on the right. If **Texture Format** is not **Cube**, modify it to **Cube,** then click **Apply.**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d3a8e41c395243ed9f0e0a85296aa1b0~tplv-goo7wpa0wc-image.image)

* **U Wrap Mode**: Specifies how the node handles **U** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **V Wrap Mode**: Specifies how the node handles **V** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **Border Color**: The color used to fill areas of the material surface not covered by material property image content. The default value is **transparent_black**.
* **Mag Filter**: The magnification filter mode used by the node when rendering image content larger than the original image size. For example, a point close to the camera may have texture coordinates corresponding to a small region in the texture image, less than one pixel. In this case, the node uses **Mag Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Min Filter**: The minification filter mode used by the node when rendering image content smaller than the original image size. For example, a point far from the camera may have texture coordinates corresponding to a region composed of multiple pixels in the texture image. In this case, the node uses **Min Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Mip Filter**: The mipmap filter mode used by the node when rendering image content with mipmapping. This parameter is especially useful when rendering images at sizes smaller than the original image. If the parameter value is **None**, the node will not use mipmapping. The default value is **linear**.
* **Max Anisotropy**: The anisotropic filtering strength applied when rendering texture image content. This parameter is used when the image content is displayed at a large tilt angle relative to the camera. This parameter should only be used with mipmapping, so it is effective only when **Mip Filter** is not **None**. The default value is **1**.
* **Max LOD Clamp**: The maximum level of detail allowed when rendering image content. When an object is close to the camera, the level of detail used to render the object's texture increases, but does not exceed the maximum value defined by this parameter. The default value is **65504**.
* **Min LOD Clamp**: The minimum level of detail allowed when rendering image content. When an object moves away from the camera, the level of detail used to render the object's texture decreases, but will not fall below the minimum value defined by this parameter. The default value is **0**.
* **Texture Coordinates**: 2D coordinates used to read data and map the texture onto the surface. The default value is the current UV coordinates, where **U** represents the horizontal direction and **V** represents the vertical direction.
* **LOD**: Specifies the LOD (Level Of Detail) level used during sampling. Smaller values typically produce sharper results, while larger values generally yield smoother results.

## Node usage guide
### Optional values for the Wrap Mode parameter

* **clamp_to_border**: Clamp to border. The node sets texture coordinates outside the normal range to the color specified by the **Border Color** parameter.
* **clamp_to_edge**: Clamp to edge. The node clamps texture coordinates outside the normal range to within the valid range. That is, values greater than 1 are set to 1, and values less than 0 are set to 0. This means the color at the image edge extends outward, filling the remaining area of the texture.
* **mirrored_clamp_to_edge**: Mirrored clamp to edge. The node applies mirrored edge clamping to texture coordinates outside the normal range. Out-of-range portions are not repeated cyclically, but are mirrored outward along the boundary direction and ultimately restricted within the edge area, so the image edge expands outward in a more continuous mirrored manner.
* **mirrored_repeat**: The node applies mirrored repeat to texture coordinates outside the normal range.
* **repeat**: The node wraps texture coordinates outside the normal range in a cyclic manner. This behavior is equivalent to performing a modulo 1 operation on the coordinates.

### Optional values for Mag Filter and Min Filter

* **linear**: The filter uses linear interpolation of neighboring values to determine the final rendered content.
* **nearest**: The filter uses the nearest neighbor value to determine the final rendered content.

### Optional values for Mip Filter
The **Mip Filter** parameter has the same optional values as **Mag Filter** and **Min Filter**, and additionally includes the **None** option, which means **mipmapping is not used**.
