Generate and configure cube texture. Compared to the **Cube Image** node, gradient-related settings are added.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7f0c338ad32a426699d9af847b6eb93b~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: The image file used for this texture. Supports image files in .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr formats.
   You need to ensure that the map's **Texture Format** is **Cube**. You can select the map in the **Project Browser** window, then view the map's **Texture Format** in the window on the right. If the **Texture Format** is not **Cube**, you can change it to **Cube,** then click **Apply.**
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d3a8e41c395243ed9f0e0a85296aa1b0~tplv-goo7wpa0wc-image.image)
   

* **U Wrap Mode**: Specifies how the node handles **U** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **V Wrap Mode**: Specifies how the node handles **V** values outside the 0–1 range. The default value is **clamp_to_edge**.
* **Border Color**: The color used to fill areas of the material surface not covered by the material property image content. The default value is **transparent_black**.
* **Mag Filter**: The magnification filter mode used by the node when rendering image content larger than the original image size. For example, a point close to the camera may have texture coordinates corresponding to a small region in the texture image, less than one pixel. In this case, the node uses **Mag Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Min Filter**: The minification filter mode used by the node when rendering image content smaller than the original image size. For example, a point far from the camera may have texture coordinates corresponding to a region composed of multiple pixels in the texture image. In this case, the node uses **Min Filter** to determine the texel color sampled at that point. The default value is **linear**.
* **Mip Filter**: The mipmap filter mode used by the node when rendering image content with mipmapping. This parameter is especially useful when rendering the image at a size smaller than the original image. If this parameter is set to **None**, the node will not use mipmapping. The default value is **linear**.
* **Max Anisotropy**: The anisotropic filtering strength applied when rendering texture image content. This parameter is used when the image content is displayed at a large tilt angle relative to the camera. This parameter should only be used with mipmapping, so it is effective only when **Mip Filter** is not **None**. The default value is **1**.
* **Max LOD Clamp**: The maximum level of detail allowed when rendering image content. When an object is close to the camera, the level of detail used to render the object's texture increases, but does not exceed the maximum value defined by this parameter. The default value is **65504**.
* **Min LOD Clamp**: The minimum level of detail allowed when rendering image content. When an object moves away from the camera, the level of detail used to render the object's texture decreases, but will not drop below the minimum value defined by this parameter. The default value is **0**.
* **Texture Coordinates**: Used to read data and map the texture onto the surface's 2D coordinates. The default value is the current UV coordinates, where **U** represents the horizontal direction and **V** represents the vertical direction.
* **Bias**: The offset value for the level of detail (LOD) when rendering image content. When the rendered texture size falls between two LOD (Level of Detail) levels, this parameter affects whether the renderer prefers to select a higher or lower level of detail. When the value is between **0 and 1**, the node tends to use **lower detail**; when the value **is greater than 1**, the node tends to use higher detail. The default value is **0**.
* **Dynamic Min Lod Clamp**: The minimum level of detail allowed when rendering image content. Similar to the **Min Lod Clamp** parameter, but this parameter can be modified dynamically at runtime.
* **Gradientcube D Pdx**: The rate of geometric change of the sampled surface or texture in the **X** direction.
* **Gradientcube D Pdy**: The rate of geometric change of the sampled surface or texture in the **Y** direction.

## Node usage instructions
### Optional values for the Wrap Mode parameter

* **clamp_to_border**: The node sets texture coordinates outside the normal range to the color specified by the **Border Color** parameter.
* **clamp_to_edge**: The node clamps texture coordinates outside the normal range to within the valid range. That is, values greater than 1 are set to 1, and values less than 0 are set to 0. This means that the color at the edge of the image extends outward, filling the remaining area of the texture.
* **clamp_to_zero**: The node sets texture coordinates outside the normal range to the color value **0**, which is black. This is equivalent to using **clamp_to_border** and setting the border color to **transparent_black**.
   **clamp_to_zero** can only be used when the **Border Color** parameter is set to **transparent_black**; otherwise, the behavior of this node is **undefined**.

* **mirrored_repeat**: The node mirrors and repeats texture coordinates outside the normal range.
* **repeat**: The node wraps texture coordinates outside the normal range around. This behavior is equivalent to performing a modulo 1 operation on the coordinates.

### Optional values for Mag Filter and Min Filter

* **linear**: The filter uses linear interpolation of neighboring values to determine the final rendered content.
* **nearest**: The filter uses the nearest neighbor value to determine the final rendered content.

### Optional values for Mip filter
The **Mip Filter** parameter has the same optional values as **Mag Filter** and **Min Filter**, with the addition of the **None** option, which indicates that mipmapping is not used.

