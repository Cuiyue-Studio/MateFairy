Read the video texture currently bound to the entity and sample it based on the Input texture coordinates.
In addition to supporting video content sampling based on Input texture coordinates, the **Video Texture LOD** node also allows explicit specification of the texture sampling LOD level, selection of sampling mode, and choice between left-eye or right-eye data.
The **Video Texture LOD** node is used to read the video texture bound to the [VideoMaterial](/document/spatial-sdk/use-video-material/) on the current entity and sample it based on the Input texture coordinates. This node itself does not handle video resource loading, playback, or binding. Video content is provided by the **PICO Spatial SDK**. When using this node, ensure that the `VideoMaterial` on the current entity is properly bound to video content; otherwise, the node may not output valid results.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dd671d6f93294cf0bafe3bf2e515b424~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Video Sample Name**: This parameter is reserved for future use.
* **Sample Mode**: Specifies the sampling mode for the video texture.
   * **Raw**: Directly outputs the original sampling result. When creating mirror reflection effects, the **Raw** mode can be used to output the video texture, and subsequent image processing algorithms can be applied to the sampled content to achieve the desired mirror reflection effect.
   * **Blurred**: Applies Gaussian blur processing to the sampled content, generating a blurred image. This mode is suitable for simulating depth of field or softening image edges. For example, when rendering video scenes that require a gentle atmosphere or the weakening or hiding of details, the **Blurred** mode can be used to create a dreamy effect or highlight the foreground subject by blurring the background. To build video effects with spatial layering, the blur processing of the **Blurred** mode can further enhance depth of field and spatial depth.
* **Texture Coordinates**: Specifies the two-dimensional texture coordinates used for sampling the video texture. By default, the UV coordinates of the current model are typically used.
* **Eye Index**: Selects the left-eye or right-eye image.
   * `0`: Left-eye image.
   * `1`: Right-eye image.
* **Lod**: Specifies the LOD (Level Of Detail) level used for sampling. Smaller values typically yield clearer results, while larger values generally produce smoother results.

## Output description

* **texture_size**: Outputs the size information of the current video texture, which can be used for subsequent pixel-level calculations or resolution-related processing.


