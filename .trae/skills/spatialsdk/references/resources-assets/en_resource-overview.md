In spatial app development, resources are the core component of scene construction. They manage key data such as mesh, material, texture, audio, and other important information. Proper use and management of resources not only determines the rendering quality and interaction performance of a scene, but also directly affects the app's memory usage and stability. Therefore, understanding resource types and management methods is critical for efficient development and optimization of spatial apps.
## Resource types
PICO Spatial SDK supports the following types of resources:
| **Resource Type** | **Class** | **Description** |
| --- | --- | --- |
| mesh | MeshResource | Geometric data, which is used to define objects and is mostly used in rendering and animation. It is typically simplified when used for physics simulation. |
|  | MeshInstancesResource | Used to define multiple sets of instance data for the same geometry—enabling efficient rendering of large numbers of objects with identical shapes. By sharing a single mesh and storing instance-specific properties such as transformation matrices and material variants (with materials reused whenever possible), this approach optimizes GPU instancing, significantly reducing the number of draw calls and improving rendering performance. |
| Texture map | TextureResource | Used for managing texture map resources; currently supports images in PNG, JPG, and JPEG formats. |
| Render material | UnlitMaterial | Used for rendering that is not affected by lighting, suitable for 2D graphics and specific 3D effects. |
|  | PhysicallyBasedMaterial | Used to simulate real-world optical phenomena such as lighting, reflection, refraction, and many others, and is suitable for 3D rendering. |
|  | ShaderGraphMaterial | Materials created with Shader Graph. |
| Model | / | A 3D model consisting of meshes and materials, and can take the form of simple geometric shapes, detailed character models, complex 3D scenes, and so on. PICO Spatial SDK supports models in USD and glTF formats. |
| Animation | uAnimationResource | Used for playing animations applied to a specific entity instance. |
|  <br> Physics-related | ShapeResource | Used to describe the physical shape of an object in collision detection and physics simulation, which typically simplifies the object's geometry. |
|  | PhysicsMaterialResource | Used to define the material property of an object in physics simulation, which affects how the object responds to physical forces. |
| Audio features | AudioResource | Used for audio playback, including standard audios and spatial audios. |
| Video-related | VideoMaterial | Used to apply video as a material to a model. |
| AssetBundle | AssetBundle | A bundle packaged by Spatial Editor that combines various types of resources. You can combine resources such as mesh, texture map, material, audio, and more into an AssetBundle according to project requirements, enabling efficient organization and loading of resources. |
## Built-in resource library
Spatial Editor provides built-in resources such as models, materials, audios, and many others. You can directly use them in your project.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/13b78264d66345cbb8911deb31b5f8b7~tplv-goo7wpa0wc-image.image)
## Load resources
### Load mesh resources
You can load or obtain mesh resources from the following content:

* **3D model**: After loading the model, obtain the mesh from the `ModelComponent` instance: `modelComponent.mesh`. For more information, refer to "[Model](/en_model)".
* **Mesh file**: Use the `Mesh.load` method to load files in .obj format. For more information, refer to "[Mesh](/en_mesh)".
* **Plane anchor**: Generate a mesh from a spatial plane anchor using `MeshResource.loadFromPlaneAnchor`. For more information, refer to "[Plane detection](/en_plane-detection)".
* **Mesh anchor**: Generate a mesh from a spatial mesh anchor using `MeshResource.loadFromMeshAnchor`. For more information, refer to "[Spatial mesh](/en_spatial-mesh)".

### Load texture resources
You can directly load texture files using the `TextureResource.load` method. For more information, refer to "[Texture](/en_texture)".
### Load material resources
You can load material resources in the following ways:

* After loading the model, use the `ModelComponent` instance to obtain the `materials` list, and then access the corresponding material resources. For more information, refer to "[Model](/en_model)".
* Call `assetBundle.loadMaterial` to load unlit material (`UnlitMaterial`),  physically-based rendering material (`PhysicallyBasedMaterial`), and ShaderGraph material (`ShaderGraphMaterial`) from the Spatial Editor project. For more information, refer to "[AssetBundle](/en_asset-bundle)".
* Call `ShaderGraphMaterial.loadFromAssetBundle` to load the `ShaderGraphMaterial` in the Spatial Editor project. For more information, refer to "[ShaderGraphMaterial](/en_shader-graph-material)".

### Load model resources
You can load model resources using the following methods and obtain the returned `entity`:

* Use `assetBundle.loadModel` to load the scene (.usda file) from the Spatial Editor project as a model. For more information, refer to "[AssetBundle](/en_asset-bundle)".
* Load models using the `Entity.load` method, which supports multiple data sources such as URI, InputStream, and AssetBundle. For more information, refer to "[Model](/en_model)".

### Load animation resources
After loading the model, you can use `entity.findSkinnedMeshEntity()` to obtain the model's skinned mesh, then use the mesh to access the model's skeletal animation resources. For more information, refer to "[Skeletal animation](/en_skeletal-animation)".
### Load audio resources
You can load audio resources in the following ways:

* Use `assetBundle.loadAudioResource` to load the corresponding audio resource from the Spatial Editor project. For more information, refer to "[AssetBundle](/en_asset-bundle)".
* Use the `AudioResource.load` method to load audio resources. Multiple data sources are supported, including URI, memory, AssetBundle, and more. For more information, refer to "[Audio](/en_audio-resource)".

### Load video files
You can use an instance of the `CypressMediaPlayer` class to load video files from the `assets` folder using `assetFileDescriptor`. For more information, refer to "[Video file](/en_video-file)".
### Load an AssetBundle
AssetBundle serves as a container for multiple resources, such as materials, models, audios, and more, packaging them into a `.bundle` file. AssetBundle itself is also a type of resource. You can use the `AssetBundle.load` method to load an AssetBundle and obtain its instance. For more information, refer to "[AssetBundle](/en_asset-bundle)".
## Create resources
### Create mesh resources
You can create basic geometric meshes using the `MeshResource.createXXX` series of methods, or generate a collection of instanced meshes with `MeshInstancesResource.create`. For more information, refer to "[Mesh](/en_mesh)".
### Create texture resources
You can create texture resources in two ways:

* **Create via bitmaps**: You can call the constructor `TextureResource()` or the static function `TextureResource.create(Bitmap)` to generate textures from bitmap data.
* **Create via SDR images**: You can call the constructor `TextureResource(String, LoadType)` to load an SDR image from the specified path in the `assets` directory or storage (supported formats include PNG, JPEG, WebP, and KTX), and then generate a texture.

For more information, refer to "[Texture](/en_texture)".
### Create material resources
You can create UnlitMaterial and PhysicallyBasedMaterial using the static functions of a class:

* Create UnlitMaterial by calling `UnlitMaterial.create(BlendingMode)`.
* Create PhysicallyBasedMaterial by calling `PhysicallyBasedMaterial.create(BlendingMode)`.

For more information, refer to "[Material](/en_material)".
### Create a ModelEntity
After obtaining mesh resources and materials, you can bind them using `ModelEntity(MeshResource, Material)`, and then instantiate the model entity. This function will create an instance of `entity` with `ModelComponent` and will return it. For more information, refer to "[Model](/en_model)".
### Create animation resources
You can create a tween animation resource by calling `AnimationResource.generateWithTweenAnimation`. For more information, refer to "[Tween animation](/en_tween-animation)".
### Create physics-related resources
Shape resources and physical materials affect collision detection and collision response. You can create them in the following ways:

* **Shape resource**: Construct physical collision shapes using the `ShapeResource.createXXX` series of methods.
* **Physics material resource**: Defines an object's static friction, dynamic friction, and restitution through `PhysicsMaterialResource(staticFriction: Float, dynamicFriction: Float, restitution: Float)`.

For more information, refer to "[Add collisions and external forces](/en_add-collision-and-external-factors)".
## Use and release resources
In spatial apps, resource is a special data structure used to store and manage the core data assets of the app. By using resources in the ECS architecture, you can efficiently build and display the entire 3D scene.
However, resources in 3D scenes typically consume more memory. If these resources cannot be released promptly when not in use, it will severely affect app performance and may even cause the program to crash. Due to limitations in Java object lifecycle management, resources cannot fully rely on automatic reclamation of instances. Therefore, the PICO Spatial SDK has specially designed its resource usage to ensure that resources in the app are properly released at the appropriate time.
### Properties and functions in the Resource class
The `Resource` class includes the following properties and functions:
| **Name** | **Type** | **Description** |
| --- | --- | --- |
| valid | Property | Determine whether the current resource is in a valid state. After the resource is released, the value of the property is `false`. |
| toGlobal() | Function | Persist resources. After a resource is persisted, it will not be released unless `close()` is called. |
| close() | Function | Remove the persistence of the resource. When the resource is not referenced, release it immediately. |
### Resource usage anomalies
If a resource is used after it has been released, PICO Spatial SDK will throw an exception.
```Kotlin
val texture = TextureResource("XXX.png")
texture.close()
val physicallyBasedMaterial = PhysicallyBasedMaterial.create() 
physicallyBasedMaterial.setBaseColorTexture(texture) // Throw IllegalStateException
```

### Lifecycle management
The lifecycle of a resource depends on its users.

* When a resource is referenced by a material, its lifecycle is determined by that material:
   ```Kotlin
   val texture = TextureResource("XXX.png")
   val physicallyBasedMaterial = PhysicallyBasedMaterial.create() 
   physicallyBasedMaterial.setBaseColorTexture(texture) // physicallyBasedMaterial instance will hold the reference count of the texture
   val unlitMaterial = UnlitMaterial.create()
   unlitMaterial.setBaseColorTexture(texture) // unlitMaterial instance will hold the reference count of the texture
   // When physicallyBasedMaterial and unlitMaterial are released, their corresponding textures are also released
   ```

* The entity maintains reference counts for the required material and model through `ModelComponent`, and automatically releases them upon destruction:
   ```Kotlin
   val texture = TextureResource("XXX.png")
   val physicallyBasedMaterial = PhysicallyBasedMaterial.create() 
   physicallyBasedMaterial.setBaseColorTexture(texture)
   val sphere = MeshResource.createSphere(10f)
   val entity = ModelEntity(sphere, physicallyBasedMaterial) // The entity will hold reference counts for the material and the model
   entity.destroy() // After the entity is destroyed, all associated resources will be released depending on their reference status
   ```


### Persist resources
By persisting resources, they can be made independent of the user's lifecycle.
```Kotlin
val texture = TextureResource("XXX.png")
texture.toGlobal()
{
    val physicallyBasedMaterial = PhysicallyBasedMaterial.create() 
    physicallyBasedMaterial.setBaseColorTexture(texture)
    val sphere = MeshResource.createSphere(10f)
    val entity = ModelEntity(sphere, physicallyBasedMaterial) // Entity will hold the reference counts for material and model
    entity.destroy() // After the entity is destroyed, the material and model will be released, but map resources can still be used
}
{
    val unlitMaterial = UnlitMaterial.create()
    unlitMaterial.setBaseColorTexture(texture) 
}
```

### Release resources proactively
For unused resources, calling `close()` will immediately release them.
```Kotlin
val portalMaterial = PortalMaterial()
portalMaterial.close() // If the resource has no users, release it immediately
portalMaterial.valid is false
```

For persisted resources, even if there are no users, you must call `close()` to fully release them.
```Kotlin
val audioResource =
    AudioResource.load(
        "test",
        "asset://xxx.wav",
        LoadType.FROM_ASSETS
    )
audioResource.toGlobal()
val entity = Entity()
entity.playAuido(audioResource)
entity.destroy() // Audio is a persistent resource and will not be released when the entity is destroyed
audioResource.close() // Audio resources will be unpersisted and released
```

Calling `close()` does not affect users who are already using the resource; the resource will be released when those users finish using it.
```Kotlin
val texture = TextureResource("XXX.png")
texture.toGlobal()
val physicallyBasedMaterial = PhysicallyBasedMaterial.create() 
physicallyBasedMaterial.setBaseColorTexture(texture)
// Release BaseColorTexture. After it is released, BaseColorTexture will no longer be available, but materials that have already applied BaseColorTexture still hold the reference count to the resource
texture.close()
// Releasing PhysicallyBasedMaterial will also synchronously release the textures it holds
physicallyBasedMaterial.close()
```

### Resource-consuming functions
Currently, the following two functions in the PICO Spatial SDK consume the non-global resources passed to them and immediately release them after use:

*  `MassProperties.generateByShapesAndMass()`
*  `MassProperties.generateByShapesAndDensity()`

```Kotlin
val shapeResource = ShapeResource.createBox(Vector3(1f, 1f, 1f))
// MassProperties does not have the capability to store resources; it converts resources into data, so the resource will be consumed
val massProperties = MassProperties.generateByShapesAndDensity(listOf(shapeResource), 1f)
shapeResource.valid is false 
// If you need to use this resource continuously, make sure to persist it in advance
```

### Situations that may lead to resource leaks

* If resources are created within a local scope but are not used by any entity or material, they will not be managed and may cause leaks.
   ```Kotlin
   val mesh = MeshResource.createCone(2f, 2f)
   val entity = ModelEntity(MeshResource.createCone(2f, 2f), UnlitMaterial.create())
   entity.destroy()
   // mesh is not used, and there are resources remaining unreleased after the mesh goes out of scope
   ```

* Persisted resources have not been released by calling `close()`.
   ```Kotlin
   val mesh = MeshResource.createCone(2f, 2f)
   mesh.toGlobal()
   val entity = ModelEntity(mesh, UnlitMaterial.create())
   entity.destroy() // The material is released, but the persisted mesh still exists
   ```


### Scenario where resources are released in advance and become unavailable
Within the same scope, if a resource is released in advance along with its user, the resource becomes invalid and any subsequent use will throw an exception.
```Kotlin
val mesh = MeshResource.createCone(2f, 2f)
val material = UnlitMaterial.create()
val entity = ModelEntity(mesh, material)
entity.destroy() // The model and material are released with the entity
val entity2 = ModelEntity(mesh, material) // Using resources that have already been released will throw an exception
mesh.valid is false 
material.valid is false 
```

### Best practices in the ECS architecture
Use the same material resource to dynamically generate objects of various shapes in the scene, and adjust their colors uniformly.
```Kotlin
// Create a persisted material
val unlitMaterial = UnlitMaterial.create()
unlitMaterial.toGlobal()

// Update the color of the material in system
class MaterialSystem : System() {
    override fun update(context: SceneUpdateContext) {
        val randomColorValueR = xxxx
        unlitMaterial.setBaseColor(Color4(randomColorValueR, 0, 0, 1))
    }
}

// Create a cone and play the scale animation. Destroy the entity after the animation finishes
{
    val entity = ModelEntity(MeshResource.createCone(1f, 1f), unlitMaterial)
    val animation =
        TweenAnimation.createTweenAnimation(
            bindTarget = AnimationBindTarget.bindScale(),
            from = Vector3(0.5F),
            to = Vector3(2F),
            duration = 2F
        )
    val sub = entity.scene?.subscribe<AnimationEvents.Completed>(entity) { 
        entity.destroy()
    }
    entity.playAnimation(AnimationResource.generateWithTweenAnimation(animation))
}

// Create a box and play the rotation animation. Destroy the entity after the animation finishes
{
    val entity = ModelEntity(MeshResource.createBox(Vector3(1f)), unlitMaterial)
    val animation =
        TweenAnimation.createTweenAnimation(
            bindTarget = AnimationBindTarget.bindRotation(),
            from = Rotator(0f, 0f, 0f),
            to = Rotator(0f, 90f, 0f),
            duration = 3F
        )
    val sub = entity.scene?.subscribe<AnimationEvents.Completed>(entity) { 
        entity.destroy()
    }
    entity.playAnimation(AnimationResource.generateWithTweenAnimation(animation))
}

// Release the persisted material
unlitMaterial.close()
```

The scene contains temporarily created model and animation resources, which are released when the entity is destroyed. Persisted resources are released by calling `close()`.
### Release AssetBundle and its loaded resources
For information on how to release resources loaded by AssetBundle, as well as how to release AssetBundle itself, refer to "[AssetBundle](/en_asset-bundle)".
