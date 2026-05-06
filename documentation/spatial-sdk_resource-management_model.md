A model is a complete 3D asset package that contains mesh, material, animation, and other data, and serves as the core visual element in spatial apps.
## Supported model formats
### USD
USD (Universal Scene Description) is an open-source 3D scene description format developed by Pixar. It supports complex hierarchical structures and a rich set of features, making it the preferred format for spatial app development.
The USD format variants supported by PICO Spatial SDK include:

* **.usd**: Text format, suitable for editing and version control.
* **.usda**: ASCII-encoded text format, directly readable.
* **.usdc**: Binary format with a fast loading speed and small storage footprint.
* **.usdz**: A compressed package format that contains the model and all dependent resources.

Features of the USD format supported by PICO Spatial SDK include:

* **Basic geometry**
   * Primitive shapes: Supports standard geometric objects such as box, sphere, and others.
   * Polygon mesh: Supports submesh partitioning.
* **Animation system**
   * Skeleton system (Skeleton): Complete skeletal binding and skinning support.
   * Timesampled animation: Animation data based on keyframes.
* **Shaders and materials**
   * Shader Graph: Supports node-based shader editing.
   * Material system: Supports the USD Preview Surface standard material and can define PBR properties such as metallic and roughness.

### glTF
glTF (GL Transmission Format) is a 3D model format introduced by the Khronos Group, specifically designed for real-time rendering. It features a small file size and fast loading speed. For more information about glTF, please refer to its [official documentation](https://github.khronos.org/glTF-Tutorials/gltfTutorial/).
A model in the glTF format usually consists of three parts:

* **.gltf**: JSON description file that stores core data, including model structure, material information, animation definitions, and more. It is directly readable and easy to edit.
* **.bin**: Binary file containing vertex data, index data, animation keyframes, and more.
* **Image files**: Texture maps, including .png, .jpg, and other formats.

.glb file is the binary container format for glTF. It packages the JSON description file, binary file, and image files into a single binary file, including:

* **Header information (Header)**: Contains file type, version, and length information.
* **JSON block**: Contains information describing the model structure and can be compressed using the gzip tool.
* **Binary block**: Contains geometry data, animation, and embedded textures.

PICO Spatial SDK supports most of the basic properties of glTF format models, including mesh, material, texture, basic animation, and many others; however, the following usage limitations apply:

* **Scene component limitations**: Importing camera and light source nodes is not supported.
* **Mesh limitations**: Mesh Primitive mode does not support `POINT`, `LINES`, `LINE_LOOP`, and `LINE_STRIP`.
* **Animation limitation**: `animation.sampler.interpolation` does not support the STEP interpolation method.

Below are the GlTF extensions supported by PICO Spatial SDK:
| **Extension name** | **Function description** | **Reamrks** |
| --- | --- | --- |
| KHR_materials_pbrSpecularGlossiness | Supports specular glossiness PBR workflow. | Choose either the current workflow or the metal roughness workflow. |
| KHR_materials_unlit | Supports unlit materials. | Applicable to UI elements and special effects. |
| KHR_materials_sheen | Supports fabric gloss effect. | `sheenRoughness` input is not currently supported. |
| KHR_materials_clearcoat | Supports varnish effect. | Can simulate transparent coatings such as car paint, water surface, and others. |
| KHR_materials_ior | Supports refractive index control. | Affects the light refraction effect of transparent objects. |
| KHR_materials_emissive_strength | Supports self-emissive intensity control. | For performance reasons, enabling the Bloom effect is not recommended. |
| KHR_texture_transform | Supports texture coordinate transformation. | Supports texture translation, rotation, and scaling. |
| KHR_texture_basisu | Supports Basis Universal texture compression. | Significantly reduce memory usage. |
| EXT_texture_webp | Supports WebP format as a texture source. | Compared to JPEG or PNG, it typically has a smaller file size. |
| KHR_draco_mesh_compression | Supports modes for the Draco geometry compression library, supporting streaming of compressed geometry data instead of raw data. | For models larger than 1 MB in which geometric data accounts for a significant proportion, Draco can reduce the file size by approximately 95% in many cases. |
| KHR_mesh_quantization | Supports using 8-bit or 16-bit storage instead of 32-bit floating point numbers. | Lower bit-width storage can result in loss of precision, and its impact on model quality should be evaluated. |
| EXT_meshopt_compression | Supports the meshoptimizer library, providing lightweight decoders and fast runtime decompression. <br>  | Although decoding is fast, CPU time is still required for decompression; the decompressed data requires additional memory space. |
| EXT_mesh_gpu_instancing | Support GPU instanced rendering. | Enables efficient rendering of a large number of identical geometry instances, shares geometry data, reduces memory usage, fully utilizes GPU parallel processing capabilities, and significantly reduces the number of draw calls. |
| KHR_animation_pointer | Support for animation property pointers <br>  <br> * Node transform: Supports node transform animation. <br> * Material properties: Supports animation of the following properties in the PBR metallic-roughness workflow: <br>    * baseColorFactor (base color coefficient) <br>    * metallicFactor (metallicity coefficient) <br>    * roughnessFactor (roughness coefficient) <br>    * emissiveFactor (emissive coefficient) | Note: Be aware of performance impacts. <br>  <br> * Runtime overhead <br>    * Parsing JSON pointers requires additional CPU time. <br>    * A large number of property animations may affect overall performance. <br>    * It is recommended to optimize and cache key properties. <br> * Memory usage <br>    * Additional animation data increases file size <br>    * More memory is required at runtime to store animation states. <br>    * Plan the quantity and complexity of animations reasonably. |
## Load models
PICO Spatial SDK supports 3D models in USD and glTF formats, and supports the following methods for loading models and creating entity instances:

* ***Method 1***: Load via URI string. The following schemes are supported:
   * `"asset://"` or `"assets://"`: Load the specified model in the /assets directory.
      ```Kotlin
      // Load the specified model from the /assets directory using a URI string with the scheme "asset://"
      suspend fun loadEntityFromAsset() {
          val subFolderName = "model"
          val fileName = "your_custom_model.usdz"
          val entity =
              withContext(Dispatchers.IO) {
                  Entity.load(uriString = "asset://${subFolderName}/${fileName}")
              }
      }
      ```

   * `"file://"`: Loads the specified model file from the device's storage. The path in the URI string must be an absolute path.
      ```Kotlin
      // Load the specified model file from device storage using a URI string or File object with the scheme "file://":
      suspend fun loadEntityFromStorageViaFileUri(context: Context) {
          // Load an entity from file URI
          val entityFromFileUri =
              withContext(Dispatchers.IO) { Entity.load("file://your_file_path}") }
      }
      ```

* ***Method 2***: Load via the `File` object.
   ```Kotlin
   suspend fun loadEntityFromStorageViaFileObject(context: Context) {
       // Load an entity from a file object
       val entityFromFileObject = withContext(Dispatchers.IO) { Entity.load(yourFile) }
   }
   ```

* ***Method 3***: Using the `ContentResolver` and `URI` objects, load the model pointed to by a URI whose scheme is `"content://"`.
   ```Kotlin
   suspend fun loadEntityFromContentUri(context: Context) {
       // Create the URI for content
       val contentUri = Uri.parse("content://${context.packageName}.yourmodelprovider/$fileName")
       // Load an entity from ContentResolver and content URI
       val entityFromContentUri =
           withContext(Dispatchers.IO) { Entity.load(context.contentResolver, contentUri) }
   }
   ```

* ***Method 4***: Use `InputStream` and `ModelFormat` to load model files that have already been converted to `InputStream`. Among them, `ModelFormat` is used to supplement the format information of the original file and currently supports two types: `ModelFormat.USDZ` and `ModelFormat.GLTF`.
   ```Kotlin
   suspend fun loadEntityFromInputStream(context: Context) {
       // Load an entity from InputStream
       val entityFromInputStream =
           withContext(Dispatchers.IO) { Entity.load(inputStream, ModelFormat.USD) }
   }
   ```

* ***Method 5***: Load the target scene in Spatial Editor as a model by specifying the path of the AssetBundle instance and the USDA file name, and return the corresponding entity instance.
   ```Kotlin
   suspend fun loadEntityFromBundle() {
       val entity =
           withContext(Dispatchers.IO) {
               Entity.load(
                   modelName = "YourCustomSceneName",
                   bundle = AssetBundle.load("asset://your_custom_asset_bundle.bundle")
               )
           }
   }
   ```

* ***Method 6***: Load directly via AssetBundle. For information on how to directly use an AssetBundle instance to load scenes from a Spatial Editor project as models and obtain child entities within the scene, refer to "[AssetBundle](/en_asset-bundle)".

In addition, all the above APIs have the suspend version. You can load a model using `Entity.loadSuspend`, which is recommended to be called on the main thread.
## Create a ModelEntity
A model consists of meshes and materials. After you obtain a mesh and material by loading or creating, you can create a `ModelEntity` using the following method:
```Kotlin
fun createModelEntityExample(mesh: MeshResource, material1: Material, material2: Material) {
    val modelEntityWithSingleMaterial = ModelEntity(mesh, material1)
    val modelEntityWithMultiMaterials = ModelEntity(mesh, arrayOf(material1, material2))
}
```

For how to load or create meshes and materials, refer to "[Mesh](/en_mesh)" and "[Model](/en_model)".
## Control model-related properties
After a model is successfully loaded, you can control its properties using the`ModelComponent`. Note that only nodes that contain a model mesh have a `ModelComponent`. Otherwise, you will not be able to access relevant properties.
### Control the rendering state
You can control the rendering state of `ModelComponent` through `entity.components[ModelComponent::class.java]?.isRendererEnabled`. The default value is `true`. This parameter only affects the visual display of the model and does not impact other components or system functions of the entity. When `isRendererEnabled` is `true`, the model is rendered and displayed normally. When `isRendererEnabled` is `false`, the model component will not be rendered, but the other components and functions of the entity will continue to operate normally.
```Kotlin
// Hide the rendering effect of the entity's model, retain other components and functions
entity.components[ModelComponent::class.java]?.isRendererEnabled = false
// Re-enable the rendering effect for the entity's model
entity.components[ModelComponent::class.java]?.isRendererEnabled = true
```

`entity.components[ModelComponent::class.java]?.isRendererEnabled` should be distinguished from `entity.enabled`. The former only affects the model's rendering, while the latter controls the enabled state of the entire entity and affects all of its behaviors and functions.
The default value of `entity.enabled` is `true` (enabled). If a child entity is enabled but its parent entity is disabled, this property returns `false`. When `entity.enabled` is `false`, the entity will not be rendered, and all its components, systems, and child entities will be disabled, but it will still be included in the results of `scene.queryEntity(EntityQueryCondition)`.
Therefore, when you need to keep logical functions active but hide visual effects—for example, temporarily hiding them during debugging—you can set `isRendererEnabled` to `false`. If you need to hide the entire entity hierarchy and disable all functions at that level, you can set `entity.enabled` to `false` to stop all related calculations and improve performance.
## Thread usage considerations
When performing model loading and entity/component-related operations, you need to select the appropriate thread based on the interface type:

* Interfaces for synchronous loading  (such as `Entity.load`) are time-consuming, so it is recommended to call them in a background thread. It is recommended to use `withContext( Dispatchers.IO)` to perform loading on the IO thread, preventing UI lag caused by blocking the main thread.
* API for asynchronous loading (such as `Entity.loadSuspend`) can be called directly on the main thread. The underlying system automatically manages the loading coroutine, so you don’t need to handle thread scheduling, making it more convenient and efficient.
* After the model has finished loading and entity instances have been created, all entity and component-related operations must be performed on the main thread, including:
   * **Scene operations**: Get scene, modify enabled status, and more;
   * **Component management**: Modify or add various components and other items;
   * **Hierarchical traversal**: Traverse the entity hierarchy tree to access child nodes and more;
   * **Coordinate space transformation**: Convert an entity's position, rotation, scale, and other properties to different coordinate spaces;
   * **Animation control**: Play or stop skeletal animation (the model file must contain animation resources), and other related functions;
   * **Audio processing**: Preparing or playing audio resources, and more.

## API reference
The `Entity`, `ModelEntity`, and `ModelComponent` classes provide functions model management. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

