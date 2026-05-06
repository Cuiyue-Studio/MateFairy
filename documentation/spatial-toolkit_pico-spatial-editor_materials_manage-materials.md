This article describes how to manage materials in Spatial Editor.
## Create materials
Refer to the following steps to learn how to create materials.

1. In the **Hierarchy** window, click **+**, select **Materials** from the dropdown menu, and then choose a shader type. Spatial Editor will create a material node.
   In Spatial Editor, materials can use built-in shaders (including Cloth, Unlit, and Physically Based) or custom shaders created with Shader Graph. For details, see [shader](/built-in-shader).

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2682f17acdf444b19318abaea9aafd0f~tplv-goo7wpa0wc-image.image)
2. Name the created material node as needed.
3. Select the material node you created, and configure the material parameters as needed in the **Inspector** window on the right. For details, see [Material parameter description](/editor/manage-material).
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c82c95ee246a47cbae1dd0506e5fb743~tplv-goo7wpa0wc-image.image)

## Associate materials with entities
In traditional modeling workflows, binding materials to each entity individually is often inefficient, especially when multiple entities need to share a material or when replacing materials in bulk, as repeated operations can be cumbersome.
In Spatial Editor, each entity is associated with the Material Bindings component by default. This component is automatically applied to the entity and all its child entities. To allow multiple meshes to share the same material, simply place them under a parent entity and assign the material to the Material component of that parent entity. All child entities will then automatically inherit and use that material, eliminating the need for individual configuration.
Refer to the following steps to learn how to associate materials with entities.

1. In the **Hierarchy** window, select the entity you want to associate with a material.
2. In the **Inspector** window on the right, locate the **Material Bindings** component, then click the dropdown arrow next to the **Binding** parameter and select the material you want to associate from the list.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50f9a7ed7dcb4d018b506055342cc9e8~tplv-goo7wpa0wc-image.image)

## Material parameter description
Different shader types correspond to different parameter settings.
### Physically Based
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d06303f9cac41dcb96f5a82eaf563c3~tplv-goo7wpa0wc-image.image)
Physically based materials support parameters such as diffuse, specular, metallic, roughness, and opacity.
| Parameters | Description |
| --- | --- |
| **Blending Mode** | Blending mode defines how the material blends with the background. <br>  <br> * **Opaque**: Opaque, completely blocks the background. <br> * **Transparent**: Transparent, blended based on Alpha. <br> * **Fade**: Fade, the object gradually disappears. <br> * **Masked**: Masked, pixels are culled based on the threshold. <br> * **Additive**: Additive, color is added to the background. |
| **Face Culling** | Face culling determines which side is culled during rendering to optimize performance. <br>  <br> * **None**: Double-sided rendering. <br> * **Front**: Cull front faces. <br> * **Back**: Cull back faces. |
| **PBR Mode** | Optimized mode for performance. <br>  <br> * **Standard**: Standard PBR calculation. <br> * **Fast**: Simplified calculation to improve performance. |
| **Base Color** | Base color (Albedo), supports texture maps. |
| **Opacity** | Opacity (0.0-1.0), supports texture maps. <br> This parameter does not appear when **Blending Mode** is **Opaque**. |
| **Metallic** | Metallic (0.0-1.0). 0.0 is non-metal (insulator), 1.0 is pure metal. Supports texture maps. |
| **Roughness** | Roughness (0.0-1.0). 0.0 is extremely smooth (mirror), 1.0 is extremely rough (diffuse). Supports texture maps. |
| **Normal** | Normal map. Used to simulate surface bump details without increasing geometry faces. |
| **Depth Write** | Depth write. When enabled, the object's pixel depth is written to the Depth Buffer, allowing it to occlude objects behind it. |
| **Depth Test** | Depth test. When enabled, pixels are rendered only if their depth meets the comparison condition (usually less than the buffer depth), meaning pixels occluded by objects in front are not rendered. |
| **Clear Coat** | Clear coat intensity. Simulates a transparent coating over the material surface (such as car paint or waxed surfaces). |
| **Clear Coat Roughness** | Clear coat roughness. Controls the glossiness of the clear coat surface. |
| **Emissive** | Emissive color. Simulates light emitted from the object's surface, unaffected by ambient lighting. |
| **Ambient Occlusion** | Ambient occlusion (AO). Simulates shadow effects in crevices or corners where light is difficult to reach. |
| **Reflectance** | Specular reflectance. Controls the reflectance at normal incidence (F0) for non-metal materials. |
| **Opacity Threshold** | Alpha cutout threshold. Pixels below this value will be discarded. <br> This parameter is only effective when **Blending Mode** is **Masked**, and is used to define the masking criteria. |
### Unlit
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3dcdfdd5d08347df8a7de8812cf36e01~tplv-goo7wpa0wc-image.image)
Unlit materials are not affected by any lighting; their color and appearance remain unchanged regardless of lighting conditions.
| Parameter | Description |
| --- | --- |
| **Blending Mode** | Blending mode defines how the material blends with the background. <br>  <br> * **Opaque**: Opaque, completely blocks the background. <br> * **Transparent**: Transparent, blended based on Alpha. <br> * **Fade**: Fade, the object gradually disappears. <br> * **Masked**: Masked, pixels are culled based on the threshold. <br> * **Additive**: Additive, color is added to the background. |
| **Face Culling** | Face culling determines which face is culled during rendering to optimize performance. <br>  <br> * **None**: Render both sides. <br> * **Front**: Cull the front face. <br> * **Back**: Cull the back face. |
| **Base Color** | Base diffuse color (Albedo), supports texture maps. |
| **Opacity** | Opacity (0.0–1.0), supports texture maps. |
| **Apply Tone Mapping** | Whether to apply tone mapping. <br>  <br> * **Enable**: Participates in post-processing exposure and tone adjustment <br> * **Disable**: Outputs the original color directly |
| **Opacity Threshold** | Alpha clipping threshold. Pixels below this value will be discarded. <br> This parameter is only effective when **Blending Mode** is **Masked**, and is used to define the criteria for pixel masking. |
### Cloth
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d7bbfe87ea4e46b39ba2be269de30f28~tplv-goo7wpa0wc-image.image)
This material type is used to simulate fabrics and other flexible materials.
| Parameter | Description |
| --- | --- |
| **Blending Mode** | Blending mode defines how the material blends with the background. <br>  <br> * **Opaque**: Opaque, completely blocks the background. <br> * **Transparent**: Transparent, blends based on alpha. <br> * **Fade**: Fade, the object gradually disappears. <br> * **Masked**: Masked, pixels are discarded based on the threshold. <br> * **Additive**: Additive, color is added to the background. |
| **Face Culling** | Face culling determines which face is culled during rendering to optimize performance. <br>  <br> * **None**: Render both sides. <br> * **Front**: Cull the front face. <br> * **Back**: Cull the back face. |
| **Base Color** | Base diffuse color (Albedo), supports texture maps. |
| **Opacity** | Opacity (0.0–1.0), supports texture maps. <br> This parameter does not appear when **Blending Mode** is **Opaque**. |
| **Normal** | Normal map. Used to simulate surface bump details without increasing polygon count. |
| **SheenColor** | Sheen color. Simulates glancing-angle sheen (back-scattering) caused by microfibers on fabric surfaces (such as velvet). |
| **Roughness** | Roughness (0.0–1.0). 0.0 is extremely smooth (mirror-like), 1.0 is extremely rough (diffuse). Supports texture maps. |
| **Subsurface Color** | Subsurface scattering color. Simulates the color of light scattered inside the object and emitted (such as skin or fabric translucency effects). |
| **Depth Write** | Depth writing. When enabled, the object's pixel depth is written to the Depth Buffer, allowing it to occlude objects behind it. |
| **Depth Test** | Depth testing. When enabled, rendering occurs only if the pixel depth meets the comparison condition (usually less than the Depth Buffer depth), that is, pixels are not rendered when occluded by objects in front. |
| **Clear Coat** | Clear coat intensity. Simulates a transparent coating over the material surface (such as car paint or waxed floors). |
| **Clear Coat Roughness** | Clear coat roughness. Controls the glossiness of the clear coat surface. |
| **Emissive** | Emissive color. Simulates light emitted from the object's surface, unaffected by ambient lighting. |
| **Ambient Occlusion** | Ambient occlusion (AO). Simulates the shadow effect produced in gaps or corners of objects where light is difficult to reach. |
| **Opacity Threshold** | Alpha clipping threshold. Pixels below this value will be discarded. <br> This parameter is only effective when **Blending Mode** is set to **Masked**, and is used to define the cutout criteria. |
### Shader Graph
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/63a8939c51654d839cc453725807dc24~tplv-goo7wpa0wc-image.image)
Custom materials.
| Parameters | Description |
| --- | --- |
| **Blending Mode** | Blending mode, defines how the material blends with the background. <br>  <br> * **Opaque**: Opaque, completely blocks the background. <br> * **Transparent**: Transparent, blends based on the alpha value. <br> * **Fade**: Fade, the object gradually becomes transparent. <br> * **Masked**: Masked, pixels are culled based on the threshold. <br> * **Additive**: Additive, color is added to the background. |
| **Face Culling** | Face culling, determines which faces are culled during rendering to optimize performance. <br>  <br> * **None**: Double-sided rendering. <br> * **Front**: Cull the front face. <br> * **Back**: Cull the back face. |
| **Depth Write** | Whether to write the object's depth information to the Depth Buffer. <br>  <br> * **Enable**: Writes the object's depth information to the Depth Buffer. The object will block other objects located behind it (typically used for opaque materials). <br> * **Disable**: Does not write the object's depth information to the Depth Buffer. The object will not block other objects located behind it (typically used for translucent materials to avoid rendering order errors). |
| **Depth Test** | Whether to check the object's depth information during rendering. <br>  <br> * **Enable**: Depth is checked during rendering. If the object is blocked by something in front, the blocked part will not be displayed (consistent with physical laws). <br> * **Disable**: Ignores depth occlusion. The object may be rendered directly on top of everything else (commonly used for HUD, UI, or perspective effects). |
| **Opacity Threshold** | Alpha clipping threshold. Pixels below this value will be discarded. <br> This parameter is only effective when **Blending Mode** is set to **Masked**, and is used to define the cutout criteria. |

