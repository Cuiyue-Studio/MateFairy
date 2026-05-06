Read the video texture currently bound to the entity and sample it based on the Input texture coordinates.
In addition to supporting video content sampling based on Input texture coordinates, the **Video Texture LOD** node also allows explicit specification of the LOD (Level Of Detail) level for texture sampling, selection of the sampling mode, and choosing left-eye or right-eye data.
The **Video Texture LOD** node is used to read the video texture bound to the [VideoMaterial](/document/spatial-sdk/use-video-material/) on the current entity and sample it based on the Input texture coordinates. This node itself does not handle loading, playback, or binding of video resources. Video content is provided by the **PICO Spatial SDK**. When using this node, ensure that the `VideoMaterial` on the current entity is correctly bound to video content; otherwise, the node may not output valid results.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dd671d6f93294cf0bafe3bf2e515b424~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Video Sample Name**: This parameter is reserved.
* **Sample Mode**: Used to specify the sampling mode for the video texture.
   * **Raw**: Directly outputs the original sampling result. When creating mirror reflection effects, you can use the **Raw** mode to output the video texture, and then process the sampled content using subsequent image processing algorithms to achieve the desired mirror reflection effect.
   * **Blurred**: Applies Gaussian blur to the sampled content, generating a blurred image. This is suitable for scenarios such as simulating depth of field or softening image edges. For example, when creating a soft atmosphere or weakening or hiding details in video rendering, use the **Blurred** mode to display dreamy effects or highlight the foreground subject by blurring the background. To build video effects with spatial layering, you can further enhance depth of field and spatial depth by using the blur processing of the **Blurred** mode.
* **Texture Coordinates**: Used to specify the two-dimensional texture coordinates for sampling the video texture. By default, the UV coordinates of the current model are typically used.
* **Eye Index**: Selects the left-eye or right-eye image.
   * `0`: Left-eye image.
   * `1`: Right-eye image.
* **Lod**: Used to specify the LOD (Level Of Detail) level for sampling. Smaller values typically yield clearer results, while larger values usually produce smoother results.

## Output description

* **texture_size**: Outputs the size information of the current video texture, which can be used for subsequent pixel-level calculations or resolution-related processing.


