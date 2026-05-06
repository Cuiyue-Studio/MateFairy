Generate a 2D texture based on an image file.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/abea7f9a7b904c65bd4256bc26488000~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: Specifies the image file used for this texture.
* **U Wrap Mode**: Specifies how to handle texture coordinate U values outside the range of 0 to 1. The default value is **clamp_to_edge**.
* **V Wrap Mode**: Specifies how to handle texture coordinate V values outside the range of 0 to 1. The default value is **clamp_to_edge**.
* **Border Color**: Specifies the border color used to fill areas not covered by the image content. The default value is **transparent_black**.
* **Mag Filter**: Specifies the filtering mode used when the image is magnified. The default value is **linear**.
* **Min Filter**: Specifies the filtering mode used when the image is minified. The default value is **linear**.
* **Mip Filter**: Specifies the filtering mode used when mipmapping is enabled. If set to **None**, mipmapping is not used. The default value is **linear**.
* **Max Anisotropy**: Specifies the level of anisotropic filtering, effective only when mipmapping is enabled. The default value is **1**.
* **Max LOD Clamp**: Specifies the maximum LOD value allowed. The default value is **65504**.
* **Min LOD Clamp**: Specifies the minimum LOD value allowed. The default value is **0**.
* **Texture Coordinates**: Specifies the two-dimensional texture coordinates used to read texture data. The current UV coordinates are used by default.
* **No Flip V**: Specifies whether to disable flipping in the V direction.
* **Bias**: Specifies the LOD bias, which affects sampling preference between two LOD levels. The default value is **0**.
* **Dynamic Min LOD Clamp**: Specifies the minimum LOD value that can be dynamically modified at runtime.

## Node usage instructions
### Optional values for the Wrap Mode parameter

* **clamp_to_border**: Border clamp. The node sets texture coordinates outside the normal range to the color specified by the **Border Color** parameter.
* **clamp_to_edge**: Edge clamp. The node clamps texture coordinates outside the normal range to within the normal range. That is, values greater than 1 are set to 1, and values less than 0 are set to 0. This means the color at the edge of the image extends outward to fill the remaining area of the texture.
* **mirrored_clamp_to_edge**: Mirrored edge clamp. The node applies mirrored edge clamping to texture coordinates outside the normal range. Out-of-range portions are not repeated cyclically, but are mirrored outward along the boundary direction and ultimately restricted within the edge region, so the image edge extends outward in a more continuous mirrored manner.
* **mirrored_repeat**: The node mirrors and repeats texture coordinates that exceed the normal range.
* **repeat**: The node wraps texture coordinates that exceed the normal range. This behavior is equivalent to performing a modulo 1 operation on the coordinates.

### Optional values for Mag Filter and Min Filter

* **linear**: The filter uses linear interpolation of neighboring values to determine the final rendered content.
* **nearest**: The filter uses the nearest value to determine the final rendered content.

### Optional values for Mip Filter
The **Mip Filter** parameter has the same optional values as **Mag Filter** and **Min Filter**, and additionally includes the **None** option, which indicates that mipmapping is not used.
### Node usage example
The following node graph shows how to create a 2D material from an image file.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2a7f6aa70fff49eabb480973b4c5f09b~tplv-goo7wpa0wc-image.image)
The diagram below shows the effect of rendering a 2D material onto a cube.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Select Flip V</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8fef771af67404995358d7861e575f3~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Do not select Flip V</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/91ca512ea0d64d53bb5abc058f4370aa~tplv-goo7wpa0wc-image.image)



</div>
</div>


