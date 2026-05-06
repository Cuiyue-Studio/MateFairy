In a visual space, scene complexity refers to the combined effect of the number of 3D elements present simultaneously and the rendering load.
In spatial apps, "3D scene complexity" is the core factor affecting performance, immersion, and compatibility. Excessive complexity may lead to performance degradation (such as frame drops and stuttering), while insufficient complexity can diminish immersion. You need to take into account both platform performance limits and content design objectives, and find the optimal balance between realism and smoothness, so that users can naturally immerse themselves in the app's content in a seamless experience.
This article introduces what scene complexity is, the upper limit supported by the PICO, and how to optimize scenes.
## Factors and optimization methods
The complexity of a scene is influenced by many factors, including the number of models, the number of triangles, the size of textures, and more.
### **Model count**
Model count refers to the total number of independent 3D objects in a 3D virtual space (that is, a scene). Any object in the scene that can be individually identified, moved, or operated is considered a model.
Recommended optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too many objects | * Remove unnecessary or purely decorative objects and focus on core objects to reduce polygon count and rendering load. <br> * Retain objects that are essential for scene representation or functionality, and ensure that the spatial layout is clear. <br> * It is recommended that the number of models with skinned mesh in the entire scene does not exceed 15. | Remove miscellaneous stationery, books, or decorative vases from the desk top, and keep only essential furniture such as the sofa, tables, and chairs. |
| Large-scale scene and complex content | Divide large scenes into multiple subregions, and dynamically load and unload regions based on the camera or player position (streaming/partition loading). | Only load the room or area where the player is currently located. When the player approaches the doorway trigger, the system begins loading adjacent areas and unloading areas the player has left. |
| Waste of rendering resources caused by occluded objects | Enable occlusion culling to allow the rendering engine to determine whether an object is completely blocked by other opaque objects before rendering. | Even if there is an entire building in front of the camera, furniture inside the building that is not visible will not be rendered, which helps conserve GPU resources. |
| Too many draw calls | * **Static batching**: Merges static objects in the scene that share the same material into a larger mesh. <br> * **GPU instancing**: For a large number of identical meshes that require repeated rendering, such as vegetation or bullets, GPU instancing technology is used to render all instances with a single draw call. | * **Static batching**: Combines all static tables and chairs in the restaurant into a single object, reducing multiple draw calls to one. <br> * **GPU instancing**: When rendering a forest, enabling GPU instancing for all identical tree models allows tens of thousands of trees to be drawn efficiently with extremely low CPU overhead. |
### Model complexity
Complex models increase rendering load and consume more memory, which can affect rendering smoothness. The complexity of a model can be measured by the following metrics:
| **Metric** | **Description** |
| --- | --- |
| Number of faces | The face count of a model refers to the number of triangles contained in the model's mesh. For the same object, a higher face count results in a more detailed model, while a lower face count results in a rougher model. However, increasing the number of faces will increase the size of the model in memory or VRAM and finally increase the rendering load. |
| Number of textures and resolution | A texture is an image applied to the surface of a model. For the same model, the higher the texture resolution, the clearer the model appears; conversely, lower texture resolution means the model will appear blurrier. A single model can simultaneously use multiple texture maps, with each map corresponding to a different position on the model. Increasing the number and resolution of textures will also increase the size of the model in memory or video memory. |
| Number and types of materials | Materials use shaders to calculate how textures are displayed on a model, and different types of materials as well as different parameter settings can impact the final rendering result. A single model can use multiple materials simultaneously, either for different positions of the model or to achieve special rendering effects. Compared with simple materials, such as unlit materials, using complex materials, such as PBR materials, results in additional GPU computational overhead. |
| Number of bones | The bone count refers to the number of bones in a model. The greater the number of bones, the more precisely the model's deformations can be controlled. Increasing the number of bones will result in greater CPU computational overhead. |
Suggested optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too many faces | * Reduce the number of model faces; the level of detail should be determined based on the actual scenario. <br> * Use normal maps to enhance detail without increasing the polygon count. <br> * Control the total number of model faces in a single spatial container: <br>    * Shared Space: 175,000 <br>    * Full Space: 350,000 | * Using low-polygon models for small objects or distant decorations does not affect the overall effect. <br> * Use normal maps instead of high-polygon modeling for character's clothing folds and the unevenness of stone walls. |
| Too many textures with high resolution | * Merge multiple textures to reduce the number of calls. <br> * Apply reasonable compression to textures to reduce their size and runtime load. | * Merge multiple UI buttons into a texture atlas. <br> * Combine scattered small textures, such as grass fragments and stones, into a large texture to reduce draw calls. |
| Too many materials with complex types | * Prioritize using simple materials, or simplify Shader Graph. <br> * When real-time lighting is not required or lighting has already been baked, unlit material is preferable to PBR material. | * Use unlit material instead of PBR material, for distant building clusters. This offers better performance with minimal visual difference. <br> * Objects baked with Lightmap do not require real-time lighting and PBR rendering. |
| Excessive number of bones | Reduce the number of bones within a reasonable range while balancing animation requirements and performance. It is recommended that a single model contain no more than 72 bones, and that each vertex be influenced by no more than 4 weighted bones. | For distant NPCs, only the torso and limb bones are retained, while detailed bones are omitted to reduce computational load. |
### **Triangle count**
All complex 3D models, no matter how smooth or irregular their surfaces appear, are fundamentally composed of triangles at the lowest level. Characteristics of triangular faces are as follows:

* **Absolute planarity**: Three points always define a unique plane.
* **Simple and efficient computation:** A triangle is the simplest polygon, and its geometric calculations, such as area and normal direction, are very fast for computers.

Countless triangular faces are joined together at different angles and positions to form a complex 3D mesh, thereby simulating the surface of a real object.
Recommended optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| The model has too many polygons | * Use the level of detail (LOD) scheme to create high-, medium-, and low-resolution versions of the same object, and automatically switch to display the most appropriate version of model based on camera distance or screen coverage. <br> * For high-polygon models, use an automatic polygon reduction tool to generate lower-resolution versions and improve efficiency. | When the player approaches a car, a high-detail model with 50,000 polygons (LOD0) is displayed; when the car is farther away, it switches to a medium-detail model with 10,000 polygons (LOD1); when it appears as a small dot in view, only a low-detail model with 500 polygons (LOD2) is used. |
### **Texture size**
In a 3D scene, texture size refers to the amount of memory occupied at runtime by all image resources used for mapping the surfaces of objects. You can think of a texture as the "skin" attached to the surface of a model. A model may be nothing more than a simple cube, but once a high-resolution texture is applied, it can appear as a wooden box, a metal box, or a stone box.
Texture memory = width × height × number of channels × bytes per channel:

* **512 x 512:** Lower-resolution texture, suitable for non-primary objects. Consumes approximately 1MB of memory.
* **2048×2048:** High-definition texture, used for main characters or foreground objects. Consumes approximately 16MB of memory.

Recommended optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| High texture resource overhead | * **Resolution adjustment**: Use appropriate map sizes based on the importance and visible size of the object to avoid waste. <br> * **Texture compression**: Use hardware-supported compression formats such as ASTC and ETC2 to significantly reduce video memory usage and bandwidth requirements. <br> * **Texture atlas**: Combines multiple small maps into a single large atlas, allowing multiple models to share the same material and thereby reducing draw calls. <br> * **Reuse**: Create a generic texture and reuse it across multiple different objects. | * **Resolution**: A distant building or a small prop uses a 512x512 map, while the main character uses a 2048x2048 map. <br> * **Compression**: Convert the original texture to the platform-specific compressed format. <br> * **Atlas**: Combine all the maps of small components on a character, such as armor, boots, gloves, and other small components, into a single atlas, so that the entire character can be rendered with only one material. <br> * **Reuse**: All wooden furniture in the scene (tables, chairs, cabinets) shares a single high-quality "wood grain" map. |
### **Light source and shadow count**
The light source determines whether the contents of a scene are visible, while shadows affect the scene's realism and sense of space.
If the lighting in your scene does not change dynamically, you can achieve nearly the same lighting and shadow effects using methods such as image-based lighting (IBL) or light baking, thereby avoiding the performance overhead caused by dynamic lighting at runtime.
Suggested optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too many light sources | * Reduce the number of light sources in the scene and strictly control light sources computed in real time to avoid unnecessary performance overhead. <br> * Retain key light sources to ensure the brightness and visual effects of core scenes. <br> * Disable real-time lighting for non-essential or decorative light sources; use baked lighting or ambient lighting as alternatives. | In indoor scenes, only one ceiling light is kept as the main light source, while real-time lighting is disabled for all other decorative wall lights and spotlights. |
| High overhead for real-time shadows  | * Optimize real-time shadow settings, for example, by lowering shadow resolution and shortening shadow rendering distance, to reduce GPU load. <br> * Disable shadow casting for non-critical objects, or use static baked shadows instead. | * Retain high-quality real-time shadows for main characters and key NPCs. <br> * Disable real-time shadow casting for objects such as vegetation, small props, and similar objects in the scene, or use low-overhead blob shadows (Blob Shadow). |
| High lighting cost | * For static scenes and static objects, complex lighting, shadow, and global illumination effects are pre-baked into the Lightmap and sampled directly at runtime to reduce real-time computation. <br> * For dynamic objects or key characters, a small amount of real-time lighting can be retained to ensure visual effects, while trying to minimize resolution and range. | * All indoor lighting and shadow effects, including indirect lighting and soft shadows, are pre-baked in advance and sampled directly at runtime. <br> * Use Lightmap for static objects such as walls, floors, and ceilings to reduce real-time lighting calculations. <br> * For the opening or closing of doors and windows, or for moving objects, preset shadows or simple dynamic light sources can be used instead of complex real-time lighting and shadows. |
### **Dynamic rendering**
Dynamic rendering refers to the process in which characters or objects continuously move within a scene, for example, a person walking, a hand swinging, a door opening, a box rotating, and more. To animate the scene, the system needs to consume extra CPU computing power to control and calculate the current state of the animation. It also requires more GPU power to render the latest state of objects in real time.
Recommended optimization approach:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too complex Interaction designs  | * Retain functional interactions (such as opening a door to enter a new area) and reduce meaningless decorative interactions. <br> * For scenarios focused on roaming, emphasize the experience of "exploration + key triggers", maintaining immersion and playability. <br> * Use visual or audio feedback to replace complex interaction. | * **Room switching / door and window interaction**: Retain only essential open/close actions for area transitions. <br> * **Scene roaming as the main focus**: Reduce operations unrelated to the main experience, such as clicking decorative objects. <br> * **Low-interaction scene optimization**: Lighting changes, ambient sound effects, or path guidance can be used to replace complex interactions. |
### **Physics simulation**
Physics simulation is used to make objects in a scene appear realistic, such as falling, collision, bouncing, gravity, and other similar effects. The system needs to use a significant amount of CPU resources to calculate an object's new position, rotation, scale, and other related properties.
Recommended optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too many characters or complex actions | * Reduce the number of characters on the same screen, control rendering and computational load, and ensure frame rate stability. <br> * Limit the number and complexity of skeletal animations. You can use pre-baked animation or LOD animation. | * Retain only one main character in the scene to highlight the core interaction and visual focus. <br> * Remove multiple AI roaming characters and reduce unnecessary animation load. |
| Significant resource consumption of particle effects | Replace high-cost particle effects with low-cost alternatives, such as animated textures or frame animation using sprite sheets. | Implement effects such as the swaying of grass, smoke, fire, and more using frame animation instead of real-time particle effects. |
### **3D interaction**
3D interaction (Interaction) refers to the responsive relationship between the user and the scene. The system needs to track user behaviors, such as movements of the head, eyes, and hands, as well as input from other external devices, such as controllers, keyboards, and more. Then, based on user input, the system also needs to update the state of the corresponding object being interacted with, for example, clicking to open a door, dragging an object, or triggering a conversation when approaching.
Recommended optimization methods:
| **Issue type** | **Optimization suggestion** | **Example** |
| --- | --- | --- |
| Too many interactions | * Streamline interactions in the scene, avoid making "every object interactable", and retain only key functional interactions. <br> * For non-core interactions, use visual or audio cues instead of actual operations to maintain the richness of the scene without increasing operational complexity. | * Retain the "turn on the light" operation to ensure users can control ambient lighting. <br> * Remove low-value interactions such as "every book can be read" and similar features to prevent repetitive actions from distracting users. <br> * Replace unnecessary click actions with environmental feedback, such as changes in lighting effects and sound prompts. |
### Summary
In spatial apps, the factors that determine the complexity of 3D scenes are not only limited to the aspects mentioned above; these are merely the core factors that influence scene complexity. Different factors have varying requirements for the GPU and CPU:
| **Factor** | **Impact on GPU load** | **Impact on CPU load** |
| --- | --- | --- |
| Model count | General | Low |
| Triangle count | Very high | None |
| Texture size | High | None |
| Light sources and shadow count | High | Low |
| Dynamic rendering | General  | General |
| Physics simulation | None | High |
| 3D interaction | Low | High |
## Scenes of varying complexities
To intuitively understand the complexity of 3D scenes, and taking into account the platform’s technical upper limits as well as actual development requirements, scene complexity is classified into three levels: low, medium, and high.
### Low-complexity scenes

* **Model count**: up to 20 objects
* **Model count:** around 100,000
* **Map resolution**: medium (≤2048x2048)
* **Lighting**: a single real-time light source and multiple baked light sources
* **Use case**: entry-level experience, interactive demonstration, static display

### Moderate-complexity scenes

* **Model count**: Up to 50 objects, 2 to 3 interactive characters
* **Model count:** around 200,000
* **Map resolution**: medium (≤2048x2048)
* **Lighting**: up to two real-time lights; all others must be baked or ambient lights
* **Use case**: lifestyle experiences, story-driven content

### High-complexity scenes

* **Model count**: 50+ objects, one skybox
* **Map resolution**: Medium (≤2048x2048)
* **Number of triangles**: **** 300,000+
* **Lighting**: a dynamic light source
* **Use case**: natural roaming, educational experiences, and relaxation scenarios

In summary, the characteristics and applicable scenarios of the three different scenario levels are as follows:
|  | **Simple-complexity scenes** | **Moderate-complexity scenes** | **High-complexity scenes** |
| --- | --- | --- | --- |
| **Spatial structure** | Single room | Combination of multiple rooms | Open space |
| **Model count** | Low (No more than 20) | Medium (approximately 20 to 50) | High (50 or more) |
| **Triangle count per eye** | ≤ 100,000 | 100,000 to 200,000 | 200,000 to 300,000 |
| **Texture resource** | Few, primarily compressed images <br> Total ≤ 32 MB | Low and medium resolution <br> Total ≤ 100MB | Multiple, medium resolution <br> Total ≥ 150 MB |
| **Lighting** | A dynamic light source | 1 to 2 dynamic light sources, including local shadows | A dynamic light source |
| **Animation and interaction** | Simple actions and a small number of clickable objects | Moderate interaction, such as switching rooms or opening doors and windows | Primarily scene roaming; suggest reducing interaction |
| **Recommended number of characters** | Maximum of 1 active character | 1 to 3 | 1 to 3 |
| **Extended interaction space** | Sufficient | Limited (the structure has already consumed half of the performance) | Very small (already close to the device's limit) |
| **Recommendations** | * Control map size <br> * Low-poly model | * Room-based partitioning logic and loading <br> * Control the number of interactive objects | * Merge textures <br> * Control the precision of light sources and shadows |
## 
