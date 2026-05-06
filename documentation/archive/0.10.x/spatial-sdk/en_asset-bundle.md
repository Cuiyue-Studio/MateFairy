AssetBundle can package resources from a Spatial Editor project, such as models, materials, audio files, and more, into a .bundle file. A Spatial Editor project can contain multiple scenes, and each scene can contain various resources. All of these scenes are packaged into a single AssetBundle during compilation. You can package multiple resources into a single AssetBundle file and organize them by scene rather than by resource type, so that you can load different resources at runtime according to the needs of each scene. In addition, by putting different resources into a single AssetBundle and loading them as needed, you can reduce the number of files that need to be loaded when starting a scene, thereby decreasing the scene's loading time.
## Load AssetBundle
You can use the `AssetBundle.load` static function to load a .bundle file and obtain an `AssetBundle` instance, then use this instance to load and release the resources stored within it. It is strongly recommended to hold a reference to this instance and explicitly call `release()` to release it after use. If you do not hold a reference to this instance, it will be automatically released through garbage collection after use, which is slower.
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
```

## Load resources using AssetBundle
3D scenes edited in Spatial Editor are stored as USDA files. The relationship between these files and the loading logic of the PICO Spatial SDK is as follows:

* Each USDA file corresponds to one scene;
* When loading a .bundle file using `AssetBundle.load`, the scene content is parsed as a single entity object and returned as the function's return value.

### Load scenes
Through `bundle.loadModel()`, you can load scenes created in the Spatial Editor (that is, .usda files). After loading successfully, this function will return an entity (assumed to be named `scene`), which is the parent node of the scene's hierarchical structure and represents the root node of the entire scene. If there is a top-level node named Root in the scene's Hierarchy window, you need to use `scene.getChildren().get(0)` or `scene.findEntity("Root")` (there is only this node named "Root" under `scene`) to retrieve the Root node, and then operate on specific elements in the scene.
Note that you can only load scenes located in the /Scenes directory of the Spatial Editor. The path you pass to `bundle.loadModel()` should be the path of the .usda file relative to the /Scenes directory.
For example:

* When the scene is located at `/Scenes/SceneName.usda`, the path to be passed is `"SceneName"`.
* When the scene is located at `/Scenes/Sub/SceneName.usda`, the path to be passed is `"Sub/SceneName"`;
* When the scene is located at `/Scenes/Sub/Sub/SceneName.usda`, the path to be passed is `"Sub/Sub/SceneName"`.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e5436326ca8840d3bc57315483cded28~tplv-goo7wpa0wc-image.image)
Therefore, to load the scene selected in the figure above, you can use the following code:
```Kotlin
// First load AssetBundle
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
// Then load the scene
val rootEntity = bundle.loadModel("Hi")
```

**Usage recommendations: Preload scenes**
AssetBundle supports preloading. For large scene files, before actual loading, you can first call the `bundle.preloadModel` function to preload the scenes. In this way, the scene can be presented more quickly when the scene is loaded. `preloadModel` function will preload the scene into memory. When using this function, the value of the `name` parameter must be the name of the to-be-loaded scene in the Spatial Editor project, which is the name of the USDA file.
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
bundle.preloadModel("SceneName")
```

### Load entities in the scene
After retrieving the scene entity through `bundle.loadModel()` (assumed to be named `scene`), the hierarchical structure displayed in the Hierarchy window shows the hierarchy of each entity under `scene`. When loading entities in the scene, you can accurately load the entity at the corresponding position based on this hierarchical structure. If your target entity has a unique name "name" in the entire tree structure, you can also use `scene.findEntity("name")` to load the specific entity node.
For example, if a scene in the Hierarchy window of the Spatial Editor has the hierarchical structure shown in the left figure below, then after loading the scene, the returned entity (assumed to be named "scene") will have the hierarchical structure shown in the right figure below.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3f99477479e0478c8e6dd74b2f4bda3d~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

```Plain Text
scene
└── Root
    ├── Sphere
    │   └── SphereMaterial
    ├── Capsule
    │   ├── CapsuleMaterial
    │   └── Plane
    │       └── PlaneMaterial
    └── Cone
        ├── ConeMaterial
        └── Cube
            ├── CubeMaterial
            └── Cylinder
                └── CylinderMaterial
```



</div>
</div>

To retrieve a specific node, you need to use `scene.findEntity("name")` to get the entity node with the corresponding name. If there is a unique node with that name in the scene, the target entity can be loaded accurately. If there are multiple nodes with the same name in the scene, `scene.findEntity("name")` will return the first node found by matchmaking, which may not meet the need to retrieve a specific node. In this case, you can consider traversing the hierarchy tree using `scene.getChildren()` to precisely locate the target node.
### Load basic materials from scenes
In Spatial Editor, you can create multiple materials for a single scene, and PICO Spatial SDK supports loading materials as corresponding material instances. Before loading, you need to specify the path where the materials are located, for example:

* `"SceneName/Root/Material"`
* `"SubFolder/SceneName/Root/Material"` (The /Scenes directory contains the subdirectory /SubFolder)
* `"SubFolder/SubSubFolder/SceneName/Root/Material"` (The /Scenes directory contains the subdirectory /SubFolder/SubSubFolder)

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e5436326ca8840d3bc57315483cded28~tplv-goo7wpa0wc-image.image" width="3456px" /></div>

As shown in the figure above, when you need to load the materials in the Hi.usda scene file, you can use the following code:

* Load the UnlitMaterial named `unlitMaterial`:
   ```Kotlin
   val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
   val material = bundle.loadMaterial("Hi/Root/MyMaterials/unlitMaterial")
   // Can be converted to UnlitMaterial
   material as UnlitMaterial
   // You can then perform operations related to UnlitMaterial, such as modifying its properties or assigning it to other entities with ModelComponent
   ```

* Load the PhysicallyBasedMaterial named `pbrMaterial`:
   ```Kotlin
   val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
   val material = bundle.loadMaterial("Hi/Root/MyMaterials/pbrMaterial")
   // Can be converted to PhysicallyBasedMaterial
   material as PhysicallyBasedMaterial
   // You can then perform operations related to PhysicallyBasedMaterial, such as modifying its properties or assigning it to other entities with ModelComponent.
   ```

* Load the ShaderGraphMaterial named `shaderGraphMaterial`:
   ```Kotlin
   val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
   val material = bundle.loadMaterial("Hi/Root/MyMaterials/shaderGraphMaterial")
   // Can be converted to ShaderGraphMaterial
   material as ShaderGraphMaterial
   // You can then perform operations related to ShaderGraphMaterial, such as modifying its properties or assigning it to other entities with ModelComponent.
   ```


### Load ShaderGraphMaterial
For ShaderGraphMaterial, in addition to loading it using the `bundle.loadMaterial()` function, you can also use the `ShaderGraphMaterial.loadFromAssetBundle` static function to load it.
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")

val material = ShaderGraphMaterial.loadFromAssetBundle(bundle, "Hi/Root/MyMaterials/shaderGraphMaterial")
```

### Load audio resources
You can use the following two methods to load the audio files located in the /Root/MyAudios/objectAudioFile directory in the Default.usda scene:

* After loading the `AssetBundle` instance, use `assetBundle.loadAudioResource(path: String)` to load audio resources.
* Load audio resources using the `AudioResource.load(bundle: AssetBundle, path: String)` static function.

```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")

// Use AssetBundle
val audioA = bundle.loadAudioResource("Default/Root/MyAudios/objectAudioFile")
// Use a static function to load the audio file
val audioB = AudioResource.load(bundle, "Default/Root/MyAudios/objectAudioFile")
```

## Release resources in scenes
After loading resources of the scenes from an AssetBundle, the AssetBundle caches these resources so that they can be accessed more quickly the next time they are loaded. When you confirm that some resources are no longer needed, you can use `bundle.releaseResource()` to release them.
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
val shaderGraphMaterial = bundle.loadMaterial("Hi/Root/MyMaterials/shaderGraphMaterial")
val pbrMaterial = bundle.loadMaterial("Hi/Root/MyMaterials/pbrMaterial")
val unlitMaterial = bundle.loadMaterial("Hi/Root/MyMaterials/unlitMaterial")
// Release all types of material
bundle.releaseResource("Hi/Root/MyMaterials/shaderGraphMaterial")
bundle.releaseResource("Hi/Root/MyMaterials/pbrMaterial")
bundle.releaseResource("Hi/Root/MyMaterials/unlitMaterial")
```

## Release scenes
When you are sure that loaded or preloaded scenes are no longer needed, you need to release them after use:
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
// Preload a scene
bundle.preloadModel("Hi")
// Load a scene
val rootEntity = bundle.loadModel("Hi")
// Release a scene that have already been loaded or preloaded
bundle.releaseModel("Hi")
```

## Release AssetBundle instances
When you are sure that an `AssetBundle` instance is no longer needed and that none of the resources cached by it are needed, you can call the `close()` function to release the `AssetBundle` instance and all cached data of resources managed by it.
```Kotlin
val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle").load("asset://Bundle/SpatialPackContent.bundle")
// Use resources
...
// Release the AssetBundle instance
bundle.close()
```

## Cache management for AssetBundle 
For scenes or resources with the same path, only one copy of memory is occupied regardless of how many times they are loaded.

* **Completely release the memory occupied by models**: When loading a scene as a model, the model's `Entity` instance is returned. Therefore, you must explicitly call `entity.destroy()` to release the model entity and its associated resources. Finally, explicitly call `assetBundle.release(sceneName)` on the `AssetBundle` instance associated with the scene to completely release the underlying data of the model.
* **Completely release the memory occupied by resources**: After loading a resource, a `Resource` instance is returned. You can directly release the resource by calling `resource.close()`, or you can indirectly release the resource by calling `entity.destroy()` to destroy the `Entity` instance it belongs to. Finally, through the `AssetBundle` instance the resource belongs to, explicitly call `assetBundle.release(pathToResource)` to completely release the underlying data of the resource.

The code sample is as follows:

* Completely release the memory occupied by the model:
   ```Kotlin
   val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
   // Load the Hi.usda scene as a model multiple times, which returns different entity instances, but only one copy of memory is occupied
   val entityA = bundle.loadModel("Hi")
   val entityB = bundle.loadModel("Hi")
   val entityC = bundle.loadModel("Hi")
   val entityD = bundle.loadModel("Hi")
   
   // Destroy all loaded models and release resources via the AssetBundle instance
   entityA.destroy()
   entityB.destroy()
   entityC.destroy()
   entityD.destroy()
   bundle.releaseModel("Hi")
   ```

* Completely release the memory used by resources:
   ```Kotlin
   val bundle = AssetBundle.load("asset://path/to/bundle/YourCustomBundleName.bundle")
   
   val mesh = MeshResource.createPlane(3F, 4F, 5F)
   val shaderGraphMaterialA = bundle.loadMaterial("Hi/Root/MyMaterials/shaderGraphMaterial")
   val shaderGraphMaterialB = bundle.loadMaterial("Hi/Root/MyMaterials/shaderGraphMaterial")
   
   val entity = ModelEntity(mesh, shaderGraphMaterialA)
   
   // When the entity is destroyed, the shaderGraphMaterialA it holds will be automatically released
   entity.destroy()
   
   // For the shaderGraphMaterialB instance that is not held by the entity, you must explicitly call close() to release it
   shaderGraphMaterialB.close()
   
   // Finally, completely release resources through the AssetBundle instance
   bundle.releaseResource("Hi/Root/MyMaterials/shaderGraphMaterial")
   ```


## API reference
The `AssetBundle` class provides functions related to AssetBundle management. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

