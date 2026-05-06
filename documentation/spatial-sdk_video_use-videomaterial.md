`VideoMaterial` is a specialized material designed to carry video textures. It not only maps video content onto object surfaces for display, but also provides a variety of rendering-related properties, ensuring that video is played and presented appropriately in different scenarios. By flexibly configuring these properties, you can achieve effects such as transparent video overlay, single-sided or double-sided rendering, and planar and stereoscopic video layouts.
## Property description
### BlendingMode
`BlendingMode` defines how the video material blends with the background or other materials, thereby controlling color blending behavior in the rendering pipeline. PICO Spatial SDK provides the following blending modes:
| **Mode** | **Description** | **Use cases** |
| --- | --- | --- |
| `OPAQUE` (default) | In opaque mode, the material completely covers the background, and transparency blending calculation is not performed. | Physical object |
| `TRANSPARENT` | Transparent mode, which supports semi-transparent effects. You can control transparency by setting the value of alpha channel. | Transparent materials, such as glass and liquids |
| `ADD` | Additive mode, whose color values are added linearly. <br> Calculation formula: Result = SourceColor + DestColor | Effects such as luminous objects and halos that require brightness blending |
| `FADE` | Fading mode, which provides a smooth transition in transparency and affects the transparency of both diffuse and specular reflection. | Fade in and fade out effects for objects |
| `MASKED` | Masked mode, which is based on threshold binarization. Use the `alphaTestThreshold` parameter as the transparency threshold, with a range of 0 to 1. The material is displayed when the alpha channel's value is greater than or equal to `alphaTestThreshold`; otherwise, it is fully transparent. | Material used where precise control of transparent and opaque areas is required, for example, leaves, fences, and mesh-like objects |
Video material only supports two blending modes: `OPAQUE` and `TRANSPARENT`.

### MaterialCullingMode
`MaterialCullingMode` is used to control the culling logic for front and back faces of polygons in the rendering pipeline, directly affecting rendering performance and visual correctness. Material culling modes provided by PICO Spatial SDK are as follows:
| **Mode** | **Description** | **Use cases** |
| --- | --- | --- |
| `NONE` | Disable culling, that is, both faces will be rendered. | Objects that are visible from both faces, such as transparent material, fabric, and many other similar items. |
| `FRONT` | Cull the front face; render only the back face of polygon. | Scenarios such as reverse modeling or those that require observing the internal structure of an object. |
| `BACK` (default) | Cull the back face; render only the front face of polygon. | Only the front face of regular 3D models needs to be displayed to optimize rendering performance. |
| `FRONT_AND_BACK` | Bidirectional culling; both the font and back faces are not rendered. | Special purposes, such as placeholders, debug mode, and other similar uses. |
### VideoDimensionMode
`VideoDimensionMode` is used to define the view layout and encoding method of video content in 3D or VR scenes, which in turn determines the compatibility, image quality, and compression efficiency of spatial video. PICO Spatial SDK provides the following video dimension modes:
| **Mode** | **Description** | **Use cases** |
| --- | --- | --- |
| `MONO` (default) | Monocular mode. The left and right eye images are identical, and the same frame is reused for both eyes during rendering to conserve rendering resources. | Standard 2D videos and 180°/360° panoramic videos. |
| `TOP_AND_DOWN` | The top-bottom mode. The upper half of the video frame displays the left eye view, while the lower half displays the right eye view. For example, when the frame resolution is 3840×1920, each of the left and right eye images is 3840×960. <br> Supports horizontal compression formats for 3D 180° and 360° video. | 180°/360° panoramic 3D video. |
| `SIDE_BY_SIDE` | The left-right mode. The left half of the video frame is the left eye view, and the right half is the right eye view. For example, when the frame resolution is 3840×1920, the resolution for each eye is 1920×1920. <br> Supports horizontal compression formats for 3D 180° and 360° video. | 180°/360° panoramic 3D videos. |
| `MULTIPLE_VIEW` | Multi-view mode, which uses double-buffered independent rendering and allocates separate video buffers for the left and right eyes (for example, MV-HEVC encoding). This mode offers the highest image quality and requires double the video memory and bandwidth. | High-resolution 3D videos of high-performance devices. |
## Create video materials
You can use the constructor to create a video material and customize the material's `BlendingMode`, `VideoDimensionMode`, `MaterialCullingMode`, and `Color4` properties. If you do not set the above properties, the default values will be used.
```Kotlin
val videoMaterial =
    VideoMaterial(
        BlendingMode.OPAQUE,
        VideoDimensionMode.MONO,
        MaterialCullingMode.BACK,
        Color4.BLACK
    )
```

## Bind ShaderGraphMaterial to the video material
You can bind `ShaderGraphMaterial` to the video material to render custom effects on the video texture.

1. Create a video material:
   ```Kotlin
   val videoMaterial =
       VideoMaterial(
           BlendingMode.OPAQUE,
           VideoDimensionMode.MONO,
           MaterialCullingMode.BACK,
           Color4.BLACK
       )
   // You can also use the VideoMaterial.create(BlendingMode) interface to create a video material
   ```

2. Load `ShaderGraphMaterial` and bind it to the video material.
   ```Kotlin
   val bundle = AssetBundle.load("asset://your_shaderGraphMaterial_name.bundle")
   val shaderMat = ShaderGraphMaterial.loadFromAssetBundle(bundle, "relative_path_in_AssetBundle")
   shaderMat.toGlobal() // To reuse this component, you need to call toGlobal first
   videoMaterial.attachShaderGraphMaterial(shaderGraphMaterial)
   ```

3. Assign the video material to `VideoComponent` or `VideoPlayerComponent`.
   ```Kotlin
   val component = VideoComponent(mesh, videoMaterial)
   // Or val component = VideoPlayerComponent(player, mesh, videoMaterial)
   entity.components.set(component)
   ```


## API reference
The `VideoMaterial` class provides functions related to video material. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

