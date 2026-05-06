Texture provides surface details and visual effects for 3D models and is an important part of the material system.
## Supported image formats
The following standard dynamic range (SDR) image formats are currently supported:

* **PNG** - Supports transparency and is suitable for maps that use the alpha channel;
* **JPEG** - High compression rate, suitable for diffuse maps and others that do not require transparency;
* **WebP** - Modern compression format that provides a smaller file size while maintaining quality;
* **KTX** – A texture format optimized for GPUs, supporting compressed textures.
* **EXR** - An HDR format supporting multi-channel and lossless compression, suitable for tasks such as video post-production, 3D rendering, and more. Below are more details:
   | **Tech Param** | **What's Supported** |
   | --- | --- |
   | type | **Scan line images**: Currently, only image streams stored and read by scan lines are supported. |
   | compression | The following lossless compression algorithms are supported: <br>  <br> * **NO_COMPRESSION**: No compression. <br> * **RLE_COMPRESSION**: Suitable for images with large areas of uniform color. <br> * **ZIPS_COMPRESSION**: Single-line block compression. <br> * **ZIP_COMPRESSION**: 16-line block compression, ideal for low-noise CG images. <br> * **PIZ_COMPRESSION**: Wavelet-based compression algorithm that performs better on high-noise images. |
   | channels | **RGB-based color channels**: Currently, only RGB-based three-channel storage is supported, which meets most color data exchange requirements. |
   | part | **singlepart**: Currently, only single-image files are supported. |

## Usage restrictions
Texture memory size is affected by the maximum resolution allowed.

* **The maximum memory limit for a single texture**: 256 MB. It is recommended to optimize the map's resolution based on actual use cases to avoid unnecessary memory consumption.
* **2D texture's maximum  resolution**: 16384 × 16384
* **3D texture's maximum resolution**: 2048 × 2048 × 2048
* **Cubemap texture's maximum resolution**: 16384

* Based on the 256MB memory limit and the texture compression format, the system dynamically adjusts the upper limit of texture resolution instead of always using the maximum resolution mentioned above.
* When a texture's memory exceeds the limit, the system will output an error log and return the corresponding error code.

Examples of compression formats and memory usage for 2D textures are as follows:
| **Compression format** | **Is there a Mipmap** | **The maximum resolution** | **Memory usage** |
| --- | --- | --- | --- |
| RGBA8 | None | 8192 × 8192 | ≤ 256MB |
| ASTC 4×4 | None | 16384 × 16384 | ≤ 256MB |
## Load textures
You can load texture data from a file using the static function `TextureResource.load`, which directly returns a `TextureResource` instance.
```Kotlin
fun load(path: String, loadType: LoadType = LoadType.FROM_ASSETS): TextureResource
```

You can load textures from the /app/src/main/assets directory or from the device's file system, and the loading types are `LoadType.FROM_ASSETS` and `LoadType.FROM_STORAGE` respectively. The file path must meet the following requirements:

* If the loading type is `LoadType.FROM_ASSETS`, the file path must be relative to the /assets directory.
* If the loading type is `LoadType.FROM_STORAGE`, the file path must be the absolute path to the file in device storage.

For example, to load a texture from the /app/src/main/assets/texture/your_custom_texture_map.png file using the two methods described above, you can use the following code:
```Kotlin
fun loadTextureResourceExample(context: Context) {
    val subFolderName = "texture"
    val fileName = "your_custom_texture_map.png"
    // Load meshes from the /app/src/main/assets directory
    val textureFromAssets =
        TextureResource.load(path = "${subFolderName}/${fileName}", loadType = LoadType.FROM_ASSETS)

    // Copy the file from the /app/src/main/assets directory to the device's file system
    val outFile = File(context.filesDir, fileName)
    context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
        FileOutputStream(outFile).use { outputStream ->
            inputStream.copyTo(outputStream)
            outputStream.flush()
        }
    }
    // Load meshes from the device's file system
    val textureFromStorage = TextureResource(outFile.absolutePath, LoadType.FROM_STORAGE)
}
```

To control more detailed parameters related to texture loading, use the following overloaded `load` function:
```Kotlin
public fun load(
    path: String,
    loadType: LoadType = LoadType.FROM_ASSETS,
    option: TextureCreateOption = TextureCreateOption(),
): TextureResource
```

The `option` parameter is a `TextureOption` object, through which you can set the texture map's name, color space, whether to use gamma correction, whether to generate mipmaps, and so on.
For example, when loading the same texture map as mentioned earlier, you can choose to add the `option` parameter for some additional configuration:
```Kotlin
fun loadTextureWithOptionExample(context: Context) {
    val subFolderName = "texture"
    val fileName = "your_custom_texture_map.png"
    // Load texture from the /app/src/main/assets directory with the option parameter
    val textureFromAssets =
        TextureResource.load(
            path = "${subFolderName}/${fileName}",
            loadType = LoadType.FROM_ASSETS,
            option =
                TextureCreateOption().apply {
                    name = "your_texture_name" // If the texture name is not set, it is null
                    useGamma = false // If the setting for using gamma correction is not specified, the default is true (enabled)
                    colorSpace = TextureColorSpace.RAW // If the texture's color space is not set, textureColorSpace.SRGB is used by default
                    mipmapMode = TextureMipmapMode.NONE // If mipmap mode is not set, TextureMipmapMode.GENERATE_ALL is used by default
                }
        )
}
```

## Create textures
You can create texture by using texture maps or bitmaps in formats supported by the SDK.
### Use texture maps to create texture resources
When creating a texture resource using a texture map, you can choose to obtain image files from the /app/src/main/assets directory or the device's file system, corresponding to the load types `LoadType.FROM_ASSETS` and `LoadType.FROM_STORAGE`, respectively. The file path must meet the following requirements:

* If the load type is `LoadType.FROM_ASSETS`, the file path must be relative to the /app/src/main/assets directory.
* If the load type is `LoadType.FROM_STORAGE`, the file path must be the absolute path of the file in device storage.

```Kotlin
fun createTextureResourceExample(context: Context) {
    /**
     * Create texture resource from texture map
     */
    val subFolderName = "texture"
    val fileName = "your_custom_texture_map.png"
    // Create texture resource from /app/src/main/assets directory
    val textureFromAssets =
        TextureResource(path = "${subFolderName}/${fileName}", loadType = LoadType.FROM_ASSETS)

    // Copy files from the /app/src/main/assets directory to the device's file system
    val outFile = File(context.filesDir, fileName)
    context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
        FileOutputStream(outFile).use { outputStream ->
            inputStream.copyTo(outputStream)
            outputStream.flush()
        }
    }
    // Create texture resource from the device's file system
    val textureFromStorage = TextureResource(outFile.absolutePath, LoadType.FROM_STORAGE)
```

When creating texture resources using texture maps, you can add the `option` parameter for additional configuration.
```Kotlin
fun createTextureWithOptionExample(context: Context) {
    val subFolderName = "texture"
    val fileName = "your_custom_texture_map.png"
    // Create texture resource from /app/src/main/assets directory with the option parameter
    val textureFromAssets =
        TextureResource(
            path = "${subFolderName}/${fileName}",
            loadType = LoadType.FROM_ASSETS,
            option =
                // Apply additional configurations
                TextureCreateOption().apply {
                    name = "your_texture_name"
                    useGamma = true
                    colorSpace = TextureColorSpace.SRGB
                    mipmapMode = TextureMipmapMode.GENERATE_ALL
                }
        )
}
```

### Use bitmaps to create texture resources
PICO Spatial SDK supports creating texture resources using bitmaps in the following formats.
| **Format** | **Description** |
| --- | --- |
| ALPHA_8 | Single-channel opacity, where each pixel stores only the 1 byte occupied  by the alpha value. This format is suitable for efficiently storing textures that do not require color information, such as masks. |
| RGB_565 | Compact RGB format, each pixel occupies 2 bytes (5 bits for red/blue, 6 bits for green). This format is suitable for opaque textures that do not require high color fidelity. This format may cause slight color deviation and requires optimization with a dithering algorithm. |
| ARGB_8888 | Standard full-color format, each pixel occupies 4 bytes, with the four channels R, G, B, and A each having 8-bit precision. This format provides the best image quality and flexibility. |
| RGBA_F16 | High-precision float format, each pixel occupies 8 bytes, and the R, G, B, and A channels use half-precision float storage. This format is suitable for wide color gamut and HDR content. |
| HARDWARE | Hardware-accelerated format, the texture is stored directly in video memory and cannot be modified. This format is suitable for screen drawing and provides optimal rendering performance. |
| RGBA_1010102 | High color depth compact format, each pixel occupies 4 bytes (10 bits for each of R, G, and B , 2 bits for alpha). While occupying the same amount of memory as the ARGB_8888 format, this format provides higher color precision and is suitable for wide color gamut and HDR content that do not require alpha blending. |
Assuming the bitmap file path is /app/src/main/assets/texture/your_custom_texture_map.png, the following code can be used to create a texture resource:
```Kotlin
fun createTextureResourceExample(context: Context) {  
    val bitmap = createBitmap(200, 100, Bitmap.Config.ARGB_8888)
    val textureFromBitmap = TextureResource.create(bitmap)
}
```

## API reference
The `TextureResource` class provides relevant functions. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
