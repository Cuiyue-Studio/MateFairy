Read the video texture bound to the current entity and sample it based on the Input texture coordinates.
The **Bound Video Texture** node is used to read the video texture bound to [VideoMaterial](/document/spatial-sdk/use-video-material/) on the current entity and sample it based on the Input texture coordinates. This node itself does not handle loading, playback, or binding of video resources. Video content is provided by **PICO Spatial SDK**. When using this node, ensure that `VideoMaterial` on the current entity is correctly bound to video content; otherwise, the node may not output valid results.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/347e5d881dac4e54bf005a2d0e43f667~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Texture Coordinates**: Specifies the two-dimensional texture coordinates used for sampling the video texture. By default, the UV coordinates of the current model are typically used.

## Node usage instructions
The following node graph demonstrates how to use the **Bound Video Texture** node to read the video texture bound to the current model and sample it based on the Input texture coordinates. In the example, mathematical operations are performed on the texture coordinates to generate a rounded rectangle mask. The video sampling result is then combined with this mask to control the material's color output and opacity output, thereby achieving the effect of displaying the video only within the rounded area while keeping the rest transparent.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/40bfcb1dd2ac479c855efe9fea69a72e~tplv-goo7wpa0wc-image.image)

