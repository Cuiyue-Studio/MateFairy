Create a 2D texture from an image file and explicitly support specifying the LOD level used for sampling.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/712fc8e14e994013b0afd5eb11599fab~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: Specifies the image file used for this texture.
* **U Wrap Mode**: Specifies how to handle texture coordinate U values outside the range of 0 to 1. The default value is **clamp_to_edge**.
* **V Wrap Mode**: Specifies how to handle texture coordinate V values outside the range of 0 to 1. The default value is **clamp_to_edge**.
* **Border Color**: Specifies the border color used to fill areas not covered by the image content. The default value is **transparent_black**.
* **Mag Filter**: Specifies the filtering mode used when the image is magnified. The default value is **linear**.
   * **linear**: Uses linear interpolation.
   * **nearest**: Uses nearest neighbor sampling.
* **Min Filter**: Specifies the filtering mode used when the image is reduced in size. The default value is **linear**.
   * **linear**: Uses linear interpolation.
   * **nearest**: Uses nearest neighbor sampling.
* **Mip Filter**: Specifies the filtering mode used when mipmapping is enabled. If set to **None**, mipmapping is not used. The default value is **linear**.
   * **linear**: Uses linear interpolation.
   * **nearest**: Uses nearest neighbor sampling.
   * **None**: Mipmapping is not used.
* **Max Anisotropy**: Specifies the anisotropic filtering level, effective only when mipmapping is enabled. The default value is **1**.
* **Max LOD Clamp**: Specifies the maximum allowed LOD value. The default value is **65504**.
* **Min LOD Clamp**: Specifies the minimum allowed LOD value. The default value is **0**.
* **Texture Coordinates**: Specifies the 2D texture coordinates used to read texture data. The current UV coordinates are used by default.
* **No Flip V**: Specifies whether flipping in the V direction is disabled.
* **LOD**: Specifies the specific LOD level used for node sampling.

### Node usage instructions
### Optional values for the Wrap Mode parameter

* **clamp_to_border**: Border clamp. The node sets texture coordinates outside the normal range to the color specified by the **Border Color** parameter.
* **clamp_to_edge**: Edge clamp. The node clamps texture coordinates outside the normal range to within the valid range. That is, values greater than 1 are set to 1, and values less than 0 are set to 0. This means the color at the image edge extends outward to fill the remaining texture area.
* **mirrored_clamp_to_edge**: Mirrored edge clamp. The node applies mirrored edge clamping to texture coordinates outside the normal range. Out-of-range portions are not repeated cyclically; instead, they are extended outward as a mirror image along the boundary direction and ultimately constrained within the edge area. As a result, the image edges expand outward in a more continuous, mirror-like fashion.
* **mirrored_repeat**: The node applies mirrored repeat to texture coordinates that exceed the normal range.
* **repeat**: The node causes texture coordinates that exceed the normal range to wrap. This behavior is equivalent to performing a modulo 1 operation on the coordinates.

### Optional values for Mag Filter and Min Filter

* **linear**: The filter uses linear interpolation of adjacent texels to determine the final rendered content.
* **nearest**: The filter uses the nearest neighbor to determine the final rendered content.

### Optional values for Mip Filter
**Mip Filter** parameter has the same optional values as **Mag Filter** and **Min Filter**, and additionally includes the **None** option, which indicates that mipmapping is not used.

