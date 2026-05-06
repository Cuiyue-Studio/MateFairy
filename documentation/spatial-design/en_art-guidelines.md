This article introduces the Shared Space art guidelines and the Full Space art guidelines.
## Shared Space Art guidelines (commonly used in MR)
Shared Space is a mixed reality mode in which users maintain a visual and physical connection with the real world while interacting naturally with virtual content. Volumetric windows, as the core carrier of this mode, must seamlessly integrate into the physical environment as bounded 3D objects, both maintaining the credible presence of virtual objects and preserving their digital flexibility.
### **Key features**

* **Multi-volumetric collaboration:** Supports independent coexistence of multiple objects without interference.
* **Fixed scaling:** Supports proportional scaling of the entire object and independent position adjustment.
* **Eye tracking + gesture interaction:** Precise interaction is achieved through gaze and pinch gestures.

### Design principles
The design of virtual objects should prioritize real-world credibility, allowing virtual objects to become part of the real physical space.

* **Lighting:** Relies on system-level image-based lighting (IBL) to dynamically match physical environmental lighting parameters, including color temperature, intensity, and direction. Avoid adding virtual light sources and only supplement them when necessary to ensure that the direction of shadows and highlight feedback strictly follow real-world logic.
* **Model scale**
   * Built to a true 1:1 scale, with a reasonable structure that is physically accurate.
   * Model topology should avoid triangles, minimize intersection and floating geometry, and ensure the origin remains stable to guarantee the accuracy of spatial anchoring.
   * Based on performance considerations for multiple Volumetric windows, medium- to high-precision models are recommended to balance visual detail and rendering efficiency.
* **Material textures**
   * Prioritize the use of PBR materials (physically based rendering).
   * Reflection information depends on the system IBL. Avoid using custom environment maps to ensure consistent physical lighting.
   * Recommended texture size: For close-up views (≤ 1 m), use a resolution of 512×512 or higher.
* **Special effects**: It is recommended to use small-scale functional special effects, such as click feedback particles, status indicators, and more, to provide visual guidance focused on interactive behavior. It is recommended to limit the size of large, expressive visual effects to a diameter of 30 cm or less to maintain visual consistency in the environment.
* **Color control:** It is recommended to use inherent colors and natural tones to ensure that the colors of virtual objects are harmonious and consistent with the physical environment. Key visual elements may be subjectively fine-tuned by approximately 15 percent to enhance recognizability.

### Case
Build upon the small robot by adding other different Volumetric windows to demonstrate operations such as moving and scaling.
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/30837654987f4fb2ae57761404587d60~tplv-goo7wpa0wc-image.image></video>
## Full Space art guidelines (commonly used in VR)

* When separated from the physical environment, there is no need to consider real-world impact. Designers and artists focus on creating independent narratives and coherent visual expressions, both of which evoke emotional resonance.
* Supports whole-body or partial-body interaction with the virtual environment. Experience greater freedom to explore within the safe boundary of physical space.

### Design principles
Artistic expressiveness takes priority. Build a credible world through artistic expressiveness, with every detail serving the depth and persistence of immersion.

* **Lighting**
   * Based on the narrative atmosphere, use various types of light sources, including point lights, spotlights, and directional lights.
   * Custom image-based lighting (IBL) defines the base tone and light and shadow of the world.
   * Integrate and apply global illumination technologies, including baked lighting and real-time lighting, to achieve natural and realistic lighting.
* **Textures and models**
   * Prefer the PBR (physically based rendering) workflow to ensure realistic and credible material lighting and reflection.
   * To achieve high-quality detail, a texture resolution of 512×512 or higher is recommended for close-up objects (≤ 1 meter).
   * Dynamically adjust model and material level of detail (LOD) as the viewing distance changes.
   * Avoid visual defects, and eliminate pixel noise and model interpenetration.
* **Style consistency**
   * Architecture, props, color, and light and shadow must strictly adhere to the established worldview.
   * Every asset in the world should have its own unique internal logic and visual language.
* **Smooth experience**
   * Maintaining a stable and smooth frame rate of 90 Hz or higher is essential for preventing dizziness and maintaining comfort.
   * User movement and interaction are always smooth and free of lag.
* **Asset performance optimization**
   The cost of cinematic-grade real-time rendering is high. Features such as soft shadows, global illumination, complex shading, and other features are costly, so asset performance optimization should be considered throughout the process. Recommend the following optimization strategies:
   * **Distance-based level design**: During the design phase, strategies are planned to allocate resources—such as model polygon count and shader complexity—in tiers according to the user's movement path and gaze focus, with a sky dome used for the farthest areas.
   * **Lighting:** It is recommended that the number of dynamic light sources does not exceed three. Baked lighting is the preferred solution to save real-time computation cost.
   * **Assets:** Optimize the number of model faces, use efficient UV layouts and atlases, and make full use of occlusion culling technology.
   * **Material simplification:** It is recommended to use the default shader. If custom materials are used, minimize shader complexity.
   * **Efficient workflows:** Complete asset preprocessing and rendering in DCC tools, assemble and integrate scenes using the USD format, and finally import into the real-time editor for final configuration and implementation of interaction logic.

### Case
Switch to Full Space mode in VST and add movement and related simple interaction features.
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b6d23d103f874a359b3987602df8d27a~tplv-goo7wpa0wc-image.image></video>
