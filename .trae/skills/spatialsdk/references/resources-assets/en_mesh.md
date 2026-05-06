A mesh is the fundamental block of a 3D model, defining its geometric shape and surface details. A mesh consists of vertices, edges, and faces, and is the core geometric data structure in the rendering pipeline, directly affecting rendering performance and visual quality.
## Load meshes
You can load a mesh from a file using the static function `MeshResource.load`. This function directly returns a `MeshResource` instance:
```Kotlin
fun load(path: String, loadType: LoadType = LoadType.FROM_ASSETS): MeshResource
```

When calling this function, you need to provide the file path and specify the loading type from`LoadType.FROM_ASSETS` or `LoadType.FROM_STORAGE`. The default is `LoadType.FROM_ASSETS`.
Currently only supports loading mesh data from files in OBJ format.

Note that if the loading type is `LoadType.FROM_ASSETS`, the file path must be relative to the `assets` directory. If the loading type is `LoadType.FROM_STORAGE`, the file path must be the absolute path of the file in device storage. For example, to load a mesh from the /assets/model/your_custom_mesh.obj file using each of the two methods described above, you can use the following code:
```Kotlin
fun loadMeshResourceExample(context: Context) {
    val subFolderName = "model"
    val fileName = "your_custom_mesh.obj"
    // Load mesh data from the /assets directory
    val meshFromAssets =
        MeshResource.load(path = "${subFolderName}/${fileName}", loadType = LoadType.FROM_ASSETS)

    // Copy the file from the /assets directory to device storage
    val outFile = File(context.filesDir, fileName)
    context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
        FileOutputStream(outFile).use { outputStream ->
            inputStream.copyTo(outputStream)
            outputStream.flush()
        }
    }
    // Load mesh data from device storage
    val meshFromStorage = MeshResource(outFile.absolutePath, LoadType.FROM_STORAGE)
}
```

In addition to loading meshes directly from files, you can also load meshes using the following three methods:

* After successfully loading a model with meshes, you can obtain the mesh data through `modelComponent.mesh` (where `modelComponent` is an instance of `ModelComponent`). For more information, refer to "[Model](/model)".
* After successfully obtaining plane anchors, you can use `MeshResource.loadFromPlaneAnchor` to load meshes from these plane anchors. For more information, refer to "[Plane detection](/plane-detection)".
* After successfully obtaining mesh anchors, you can use `MeshResource.loadFromMeshAnchor` to load meshes from these mesh anchors. For more information, refer to "[Spatial mesh](/spatial-mesh)".

## Create meshes
PICO Spatial SDK provides a set of static functions for `MeshResource`, allowing you to quickly create basic geometric meshes, such as Plane, Sphere, Cylinder, Cone, Capsule, Box, Torus, and VideoPanel. The code sample is as follows:
```Kotlin
fun createMeshResourceExample() {
    // Create a plane mesh
    val planeMesh = MeshResource.createPlane(width = 0.4f, height = 0.3f, cornerRadius = 0.02f)
    // Create a video panel. Recommended for use when rendering video materials
    val videoPanelMesh =
        MeshResource.createVideoPanel(width = 0.4f, height = 0.3f, cornerRadius = 0.02f)
    // Create a sphere mesh
    val sphereMesh = MeshResource.createSphere(radius = 0.5f)
    // Create a cylinder mesh
    val cylinderMesh = MeshResource.createCylinder(radius = 0.5f, height = 1.0f)
    // Create a cone mesh
    val coneMesh = MeshResource.createCone(radius = 0.5f, height = 1.0f)
    // Create a capsule mesh
    val capsuleMesh = MeshResource.createCapsule(height = 0.3f, radius = 0.3f)
    // Create a box mesh
    val boxMesh = MeshResource.createBox(size = Vector3(0.4f, 0.3f, 0.2f), cornerRadius = 0.02f)
    // Create a torus mesh
    val torusMesh = MeshResource.createTorus(outerRingRadius = 0.5f, innerRingRadius = 0.3f)
}
```

## Use the MeshInstance
The PICO Spatial SDK leverages GPU instancing technology to achieve efficient batch rendering through `MeshInstance`. In complex scenarios, `MeshInstance` supports rendering a large number of repeated mesh objects—such as vegetation, particles, and building clusters—in a single draw call, significantly reducing communication overhead between the CPU and GPU.
The implementation principle is as follows: for instances with the same geometric shape and material, the same mesh and material data are shared, and only the necessary differentiating properties—such as transformation matrix, color, and more—are passed to each instance.
This mechanism offers the following advantages:

* **Reduce draw calls**: Multiple calls are merged into one, significantly reducing CPU load.
* **Reduce memory usage**: Only one copy of mesh and material data needs to be stored.
* **Support dynamic updates**: Instances can be added or deleted, and their properties can be modified in real time, making this suitable for dynamic scenarios such as particle system and crowd simulation.

The following code simulates a large-scale instanced rendering scenario: it creates 500 mesh instances and renders them using `MeshInstancesResource`.
```Kotlin
fun meshInstanceExample() {  
    // Prepare 500 instances and set their id and transform
    val instances = mutableListOf<Instance>()  
    for (i in 0..500) {  
        instances.add(Instance("yourmesh_$i", randomTransformByPosition()))  
    }  
    // Create MeshInstanceResource and add instances
    val meshInstancesResource = MeshInstancesResource.create("yourmesh")  
    instances.forEach { meshInstancesResource.add(it) }  
    // Bind MeshInstanceResource to the model entity
    val entity = ModelEntity(  
        mesh = MeshResource.createTorus(0.6f, 0.4f),  Shared mesh
        material = UnlitMaterial.create().apply { setBaseColor(Color4.GREEN) }  Shared material
    )  
    entity.components[ModelComponent::class.java]!!.meshInstances = meshInstancesResource  
}  

// Generate a transformation matrix for a random position
fun randomTransformByPosition(): Transform {  
    val position = Vector3(  
        x = (Random.nextFloat() * 200) - 100,  // X range: [-100, 100]
        y = Random.nextFloat(),                // Y range: [0, 1]
        z = (Random.nextFloat() * 200) - 100   // Z range: [-100, 100]
    )  
    return Transform(position, EulerAngles(0F, 0F, 0F), Vector3(1F, 1F, 1F))  
}  
```

## Usage recommendations
Recommendations for mesh usage:

* Control the number of triangles in the model to achieve optimal performance.
* Merge static meshes or use `MeshInstance` to reduce draw calls.
* Use LOD technology to dynamically adjust mesh complexity based on the distance between objects and the camera. PICO Spatial SDK does not currently support APIs related to LOD. You need to implement LOD functionality yourself according to your project requirements.

Recommendations for `MeshInstance` usage:

* Prioritize batch creation. Use `MeshInstancesResource.create(name, list)` to add a large number of instances at once, avoiding the performance overhead caused by repeatedly calling `add` in a loop.
* Minimize property differences. Retain only necessary differences between instances, such as transformations or a few properties, like color. Minimize variation in meshes or materials as much as possible to improve rendering efficiency.
* Select the appropriate batching method. For fully static scenes, such as buildings, prioritize static batching; for dynamic scenes, such as moving enemies, use GPU instancing.

The comparison of draw call optimization solutions is as follows:
| **Optimization Method** | **Use Cases** | **Advantages** | **Disadvantages** |
| --- | --- | --- | --- |
| Mesh merging | Static scenes (such as terrain) | Thoroughly reduce draw calls | Dynamic updates are not supported |
| Static batch processing | Static objects (such as buildings) | Simple and easy to use | Movement is not supported, high memory usage |
| GPU instancing | Dynamic and repeated objects (such as particles and NPCs) | Supports dynamic updates, low memory usage | Mesh and material must be shared |
## API reference
The `MeshResource` class provides mesh-related functions. For more information, refer to [API Reference](https://developer.picoxr.com/spatial-api/index.html).
