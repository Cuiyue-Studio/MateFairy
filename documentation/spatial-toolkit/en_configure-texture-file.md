This article describes how to configure texture files in Spatial Editor.
After you add a texture file as a resource to Spatial Editor, you can configure the texture type, color space, texture shape, texture format, maximum size, MipMap, and compression quality of the texture file.
## Steps
Follow the steps below to configure your texture file.

1. In the **Project Browser** tab of Spatial Editor, locate and select the texture file you want to configure.
2. In the configuration window on the right, modify the configuration of the texture file. For details, see [Configuration reference](/editor/configure-texture-file).
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9f84ade0c5c7474cb8fbc6ed4cc296be~tplv-goo7wpa0wc-image.image)

## Configuration reference
| Parameter | Description |
| --- | --- |
| Texture Type | Texture type. <br>  <br> * **Default**: Default texture. <br> * **Normal**: Normal map, used to represent the surface bump details of an object. |
| Color Space | Color space. When **Texture Type** is set to **Normal**, this parameter is forced to **Raw** and cannot be modified. |
| Texture Shape | Texture shape. <br>  <br> * **2D**: Indicates the texture shape is a two-dimensional plane. <br> * **Cube**: Indicates the texture shape is a cube. If you add a map to an IBL-related [lighting component](/light-components), **Texture Shape** will be automatically set to **Cube**. |
| Texture Format | Texture format. |
| Compression Quality | Set the sampling quality for ASTC texture compression. The higher the sampling quality, the higher the texture quality, but the compression time will also increase accordingly. This parameter appears only when **Texture Format** is an ASTC texture compression format. <br>  <br> * **Fast**: Sampling quality is 0. <br> * **Normal**: Sampling quality is 60. <br> * **High**: Sampling quality is 98. <br> * **Best**: Sampling quality is 100. |
| Max Size | Set the maximum size of the texture after import (such as 2048, 4096). If the original image size exceeds this value, it will be automatically scaled to this size to save video memory. |
| Generate MipMaps | Whether to generate MipMap. You can improve the clarity of the texture by performing sharpening operations on the MipMap. |
| MipMap Mode | Set the algorithm for generating MipMap. <br>  <br> * **DEFAULT**: (Default) Suitable for scenarios where **Texture Shape** is **2D**. <br> * **ENV_LIGHTING**: Suitable for scenarios where **Texture Shape** is **Cube**. If you add a map to an IBL-related [lighting component](/light-components), **Texture Shape** will be automatically set to **Cube, MipMap Mode** will be automatically set to **ENV_LIGHTING.** |
| Sharpness Level | Sharpening level of the MipMap. <br>  <br> * **None**: No sharpening operation is performed on the MipMap. <br> * **Low**: Low-level sharpening. <br> * **Medium**: Medium-level sharpening. <br> * **High**: High-level sharpening |
| Y Flip | Whether to flip the Y axis (green channel) of the normal map. Used to correct the visual issue of 'opposite bumpiness' in the normal map. Typically used to adapt to different normal standards exported by modeling software. <br> This parameter only appears when **Texture** is set to **Normal**. |


