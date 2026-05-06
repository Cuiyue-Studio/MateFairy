Skeletal animation is a technology that is widely used in character animation. Its core principle is to drive dynamic deformation of the model by simulating a hierarchical skeletal structure composed of connected joints (Skeleton). In skeletal animation:

* Each bone node can perform local translation, rotation, and scaling transformations.
* Transformations are propagated through the hierarchy, starting from the parent bone and sequentially affecting all related child bones.
* Vertices of a skinned mesh bound to the skeletal structure deform according to real-time skeletal transformations, resulting in continuous and natural animation effects.

For example, in a character walking animation, the rotation of the hip joint drives the movement of the thigh bone, which in turn affects the lower leg and foot, ultimately resulting in the complete swinging motion of the leg. In this way, complex full-body movements can be efficiently driven by a small number of skeletal transformations, making it highly suitable for real-time rendering and animation systems.
## Example project
On the [Samples page](/document/spatial-example/), you can select an animation sample, download the sample code, and try skeletal animation.
After downloading the sample and running it on a PICO headset or the PICO Emulator, select **Skeletal** in the leftmost navigation bar. The interface will display skeletal animation content. After selecting the target animation from the animation list in the middle, the playback area on the right will display the corresponding skeletal animation.

         <video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c980318598de4aaaad6057e82d351199~tplv-goo7wpa0wc-image.image></video>

## glTF vs USD
glTF and USD have significant differences in architectural design, especially in the way they handle skeletal animations.
### How glTF handles animations
glTF uses a scene graph-based approach to handle skeletons, and stores all animations in a single array. Each animation is defined independently, can be selected and switched at runtime, and can be accessed by index. Below is an example:
```JSON
{
  "animations": [
    {
      "name": "Walk",
      "channels": [
        {"sampler": 0, "target": {"node": 1, "path": "translation"}},
        {"sampler": 1, "target": {"node": 1, "path": "rotation"}}
      ],
      "samplers": [
        {"input": 0, "output": 1, "interpolation": "LINEAR"},
        {"input": 2, "output": 3, "interpolation": "STEP"}
      ]
    },
    {
      "name": "Run",
      "channels": [...],
      "samplers": [...]
    }
  ]
}
```

### How USD handles animations
A USD skeleton can only use a single `rel skel:animationSource` relationship to bind to one `SkelAnimation`. This means that USD cannot store multiple skeletal animations in the same file and switch between them at runtime as glTF does. USD typically handles multiple animations by segmenting the timeline or dynamically switching layers or files.

* **Timeline segmentation**
   Place multiple animations in different intervals within the same timeline, then use `AnimationViews` to slice the timeline into multiple animation clips.
   ```Plain Text
   Timeline:
   Frame 1-30:   Walk animation
   Frame 31-60:  Run animation 
   Frame 61-90:  Jump animation
   Frame 91-120: Wave animation
   ```

* **Dynamic layer or file switching**
   Dynamically load different animation layers or files at runtime to switch animations. Applications typically need to provide additional animation management logic.
   ```Plain Text
   Character.usd          # Main file, contains skeleton definitions
   ├── walk.usd         # Separate layer/file for the Walk animation
   ├── run.usd          # Separate layer/file for the Run animation
   ├── jump.usd         # Separate layer/file for the Jump animation
   └── wave.usd         # Separate layer/file for the Wave animation
   ```


### Comparison of use cases
Based on the technical characteristics of USD and glTF for handling multiple animations, for character models containing multiple skeletal animations, you may refer to the following format selection recommendations.

* **When to choose glTF:**
   * **Real-time interactive apps**: In scenarios where frequent switching of animation states is required, such as transitioning a character from walking to running or attacking, the independent animation array structure of glTF enables rapid switching without reloading files. This makes it suitable for game development, Web 3D presentations, and AR/VR apps.
   * **Network transmission**: When file size and loading speed are critical, the GLB format of glTF combined with Draco compression can significantly reduce file size, while the JSON structure supports progressive loading. This makes it ideal for online games, web presentations, and mobile apps.
   * **Performance-sensitive**: When efficient data structures and stable rendering performance are required, glTF's contiguous buffer layout and quantized compression are optimized for GPUs, making them suitable for mobile devices, large-scale character rendering, and other real-time renderings.
* **When to choose USD:**
   * **Content creation phase**: When fine-tuning and teamwork are required, USD's sparse animation storage and external reference mechanism support independent work and version control, making it easier for animators to develop in parallel and iterate continuously.
   * **High-quality output**: In scenarios where optimal visual effects are pursued, USD provides multiple types of numerical precision and a comprehensive metadata system to ensure that animation quality does not decline due to precision loss, making it suitable for film rendering, advertisement production, and architectural visualization.
   * **Complex production pipelines**: In the face of complex workflows involving multi-software collaboration and non-linear editing, USD's layered system and external reference mechanism enable data exchange and unified resource management across DCC software, making it highly suitable for large-scale projects.

### Decision-making process
Refer to the following process to select the file format for multi-animation character models.
```Plain Text
Need to frequently switch animations at runtime?
├── Yes → glTF (separate animation arrays, supports fast switching)
└── No → Continue to the next question:
      Concerned about file size and loading speed?
      ├── Yes → glTF (GLB + Draco compression)
      └── No → Continue to the next question:
            Need fine-grained creation control?
            ├── Yes → USD (sparse animation + external references)
            └── No → Continue to the next question:
                  Involve a complex production pipeline?
                  ├── Yes → USD (layered system + metadata)
                  └── No → Choose based on target platform:
                        ├── Real-time apps / Web / Mobile → glTF
                        └── Film / High-end visualization → USD
```

## Limitations and regulations
Below are the limitations and regulations for skeletal animations:
| **Parameter** | **Usage** | **Limitations / regulations** |
| --- | --- | --- |
| Number of bones | Control the total number of available bones. | * Maximum number of PICO devices: 1024 <br> * The maximum for Spatial Editor: 512 <br> * Behavior when the limit is exceeded: When the value exceeds 512, the animation will not play |
| Number of bone weights per vertex | Used to drive vertex animation. | The system automatically selects the four bones with the highest weights and normalizes them. |
| Animation keyframe rate | Used to control the smoothness and performance of animations. | * Recommended values: 24fps / 30fps / 60fps <br> * Default: 24fps |
## Implement skeletal animations
This section uses the above-mentioned example project to walk you through the implementation of skeletal animations.
### Step 1: Get a skeletal animation
You can create or get skeletal animations in the following ways, and you must ensure that the animation model complies with [PICO animation design guidelines](/document/spatial-design/animation-design/).

* **Creating with DCC software**: Use [Blender](https://www.blender.org/), [Maya](https://www.autodesk.com/products/maya/overview?term=1-YEAR&tab=subscription&plc=MAYA), [3ds Max](https://www.autodesk.com/products/3ds-max/overview?term=1-YEAR&tab=subscription), [Character Creator](https://www.reallusion.com/character-creator/), and other software of this kind to create skeletal animations. The overall workflow includes modeling, rigging, skinning, creating keyframe animation, and more.
* **Download or purchase ready-made animation assets**: Purchase ready-made animation assets from asset stores such as [Mixamo](https://www.mixamo.com/#/), [Sketchfab](https://sketchfab.com/feed), [Unity Asset Store](https://assetstore.unity.com/?srsltid=AfmBOopcIpcIQsWIaCSZE3fgSXogUWn6N7fPFoxmqbqRXakIROIgYKC1), and [Fab](https://www.fab.com/) (Unreal Engine Marketplace).
* **Commissioned production**: Commission professional animators or studios to create specific skeletal animations as required.

PICO Spatial SDK only supports importing assets in usdc, usda, usdz, gltf, or glb formats. Ensure that the models you create, download, or purchase are in these formats.

### Step 2: Load a 3D model
In this section, we use the 3D model file (including skeletal animation) **/app/src/main/assets/pico_robot_animated.glb** from the sample project as an example to demonstrate how to load a 3D model. In `SkeletalAnimationEntity.kt`, the following initialization code loads the model, adjusts its size and position, and adds the created `Entity` instance as a child node of the `SkeletalAnimationEntity` instance.
```Kotlin
private suspend fun initialize() {
    // Load the 3D model from the path "app/src/main/assets/pico_robot_animated.glb" and assign it to the variable character
    val character = withContext(Dispatchers.IO) { load(ANIMATED_ROBOT) }
    // Add the character node as a child of the current object so that it is bound to the parent node in the scene hierarchy
    addChild(character)
    // Get the model's TransformComponent, which is used to control scale, position, rotation, and other properties
    character.components[TransformComponent::class.java]?.apply {
        setPosition(INITIAL_POSITION_ANIMATED_ROBOT)
        setScaleVector(INITIAL_SCALE_ANIMATED_ROBOT)
    }
    // ...
}

// ...
companion object {
    private const val ANIMATED_ROBOT = "asset://pico_robot_animated.glb"
    // Set the model's position to (-0.12, -0.2, 0.1)
    private val INITIAL_POSITION_ANIMATED_ROBOT = Vector3(-0.12f, -0.2f, 0.1f)
    // Scale the model by a factor of 0.0045
    private val INITIAL_SCALE_ANIMATED_ROBOT = Vector3(0.0045f)
}
```

### Step 3: Query the skinned mesh
Essentially, skeletal animation is the process in which the skinned mesh undergoes dynamic deformation over a time sequence. Therefore, skeletal animation is generally bound to the skinned mesh. You can use `fun findSkinnedMeshEntity(includeInactive: Boolean = false): Array<Entity>` to query entities with skinned meshes in the current entity and its child nodes. The `includeInactive` parameter determines whether to include entities with `enable = false`. This method returns all entities that meet the criteria as an `Array<Entity>`. After you find the skinned mesh, you can retrieve the corresponding skeletal animation resources.
```Kotlin
val skinnedMeshEntityArray = findSkinnedMeshEntity()
```

### Step 4: Retrieve animation resources
After retrieving an array of entities with skinned meshes, you can use the `getAnimationResources()` method to retrieve the animation resources bound to each of these entities. This method stores all animations on the specified skinned mesh in an array of type `AnimationResource` in sequence, and returns the array.
```Kotlin
for (entity in skinnedMeshEntityArray) {
    skeletalAnimationResources = entity.getAnimationResources()
}
```

### Step 5: Play animations
After obtaining animation resources, you can use `entity.playAnimation(animationResource)` to play animations.
```Kotlin
for (entity in skinnedMeshEntityArray) {
    skeletalAnimationResources = entity.getAnimationResources()
    skeletalAnimationResources!![skeletalAnimationState.value].use {
        entity.playAnimation(it)
    }
}
```

You can encapsulate the logic for querying skinned meshes, retrieving animation resources, and playing the corresponding skeletal animations in the `playSkelAnimation()` function.
```Kotlin
// Play the skeletal animation with a specified name
fun playSkelAnimation(animName: String) {
    
    // Query all entities with skinned meshes in the current entity and its child nodes
    val skinnedMeshEntityArray = findSkinnedMeshEntity()
    
    // If no skinned mesh entities are found, output an error log and exit the method
    if (skinnedMeshEntityArray.isEmpty()) {
        Log.e("SkeletalAnimation", "No Skinned Mesh Found!")
    } else {
        // Output debug log showing the number of skinned mesh entities found
        Log.d("SkeletalAnimation", "Found ${skinnedMeshEntityArray.size} Skinned Mesh!")
        
        // Traverse all kinned mesh entities found
        for (entity in skinnedMeshEntityArray) {
            // Retrieve the list of skeletal animation resources for the current entity (may include multiple animations)
            skeletalAnimationResources = entity.getAnimationResources()
            // Query the index of the target animation from the map, retrieve the corresponding animation from the animation resource array, and play it on the current entity
            skeletalAnimationResources!![skeletalAnimationState.value].use {
                entity.playAnimation(it)
            }
        }
    }
}
```

In the above code, the `use` function uses `AnimationResource` to play an animation. After each animation resource has been used, you must call `Resource.close()` to clear it. Therefore, if you use a `Resource` that inherits from `Closable` in the `use` function, animation resources will be automatically released after the program finishes executing the `use` function's code block.
### Step 6: Stop playback & release animation resources
When you need to stop all animations, you can use the `entity.stopAllAnimations()` method. In addition, it is recommended that you explicitly call the `animationResource.close()` method to release animation resources when you no longer use them. In `SkeletalAnimationEntity.kt`, the following function implements stopping all animations and performing resource release.
```Kotlin
fun reset() {
    this.stopAllAnimations()
    skeletalAnimationResources?.forEach { it.close() }
    skeletalAnimationResources = null
}
```

## API reference
The `AnimationBindTarget`, `AnimationPlaybackController`, and `AnimationResource` classes provide functions related to skeletal animation. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
