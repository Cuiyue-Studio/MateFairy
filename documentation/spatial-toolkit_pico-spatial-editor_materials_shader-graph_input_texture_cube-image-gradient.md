Generate and configure cube textures. Compared to the **Cube Image** node, gradient-related settings are added.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7f0c338ad32a426699d9af847b6eb93b~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: The image file used for this texture. Supports map formats including .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr.
   You need to ensure that the map's **Texture Format** is **Cube**. You can select the map in the **Project Browser** window, and then view the map's **Texture Format** in the window on the right. If the **Texture Format** is not **Cube**, you can change it to **Cube,** and then click **Apply.**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d3a8e41c395243ed9f0e0a85296aa1b0~tplv-goo7wpa0wc-image.image)
   

* **U Wrap Mode**: Specifies how the node handles **U** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **V Wrap Mode**: Specifies how the node handles **V** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **Border Color**: The color used to fill areas of the material surface not covered by material property image content. The default value is **transparent_black**.
* **Mag Filter**: The magnification filter mode used by the node when rendering image content at sizes larger than the original image dimensions. For example, a point close to the camera may have texture coordinates corresponding to a small region less than one pixel in the texture image. In this case, the node uses **Mag Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Min Filter**: The minification filter mode used by the node when rendering image content at sizes smaller than the original image dimensions. For example, a point far from the camera may have texture coordinates corresponding to a region composed of multiple pixels in the texture image. In this case, the node uses **Min Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Mip Filter**: The mipmap filter mode used by the node when rendering image content with mipmapping. This parameter is especially useful when rendering the image at sizes smaller than the original image dimensions. If the parameter value is **None**, the node will not use mipmapping. The default value is **linear**.
* **Max Anisotropy**: The anisotropic filtering strength applied when rendering texture image content. This parameter is used when the image content is displayed at a large inclination angle relative to the camera. This parameter should only be used with mipmapping, so it is effective only when **Mip Filter** is not **None**. The default value is **1**.
* **Max LOD Clamp**: The maximum level of detail allowed when rendering image content. When an object is close to the camera, the level of detail used to render the object's texture increases, but does not exceed the maximum value defined by this parameter. The default value is **65504**.
* **Min LOD Clamp**: The minimum level of detail allowed when rendering image content. When an object moves away from the camera, the level of detail used to render the object's texture decreases, but will not fall below the minimum value defined by this parameter. The default value is **0**.
* **Texture Coordinates**: 2D coordinates used to read data and map the texture onto the surface. The default value is the current UV coordinates, where **U** represents the horizontal direction and **V** represents the vertical direction.
* **Bias**: The bias value for the level of detail (LOD) when rendering image content. When the rendered texture size falls between two LOD (Level of Detail) levels, this parameter affects whether the renderer tends to select a higher or lower level of detail. When the value is between **0 and 1**, the node tends to use **lower detail**; when the value **is greater than 1**, the node tends to use higher detail. The default value is **0**.
* **Dynamic Min Lod Clamp**: The minimum level of detail allowed when rendering image content. Similar to the **Min Lod Clamp** parameter, but this parameter can be dynamically modified at runtime.
* **Gradientcube D Pdx**: The rate of geometric change of the sampled surface or texture in the **X** direction.
* **Gradientcube D Pdy**: The rate of geometric change of the sampled surface or texture in the **Y** direction.

## Node usage instructions
### Optional values for the Wrap Mode parameter

* **clamp_to_border**: Border clamping. The node sets texture coordinates outside the normal range to the color specified by the **Border Color** parameter.
* **clamp_to_edge**: Edge clamping. The node clamps texture coordinates outside the normal range to within the valid range. That is, values greater than 1 are set to 1, and values less than 0 are set to 0. This means that the color at the edge of the image extends outward, filling the remaining area of the texture.
* **mirrored_clamp_to_edge**: Mirrored edge clamping. The node performs mirrored edge clamping for texture coordinates outside the normal range. Out-of-range portions are not repeated cyclically, but are mirrored outward along the boundary direction and ultimately limited within the edge area, so the image edge extends outward in a more continuous mirrored manner.
* **mirrored_repeat**: The node performs mirrored repeating for texture coordinates outside the normal range.
* **repeat**: The node causes texture coordinates that exceed the normal range to "wrap". This behavior is equivalent to performing a modulo 1 operation on the coordinates.

### Optional values for Mag Filter and Min Filter

* **linear**: The filter uses linear interpolation of neighboring values to determine the final rendered content.
* **nearest**: The filter uses the nearest neighbor value to determine the final rendered content.

### Optional values for Mip Filter
The **Mip Filter** parameter has the same optional values as **Mag Filter** and **Min Filter**, and additionally includes the **None** option, which indicates that mipmapping is not used.

