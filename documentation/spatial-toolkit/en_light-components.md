This article introduces the lighting components in Spatial Editor.
Lighting components can be added to entities or removed from entities. In .usda files, the type of lighting component is `SpatialComponent`. This is a component type defined by Spatial Editor and is not native to USD.
# IBL
## Stage Environment Lighting
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8c715ced69a94242a5839237440e57b8~tplv-goo7wpa0wc-image.image)
Global image-based lighting provides base illumination for the entire scene and affects all entities within the scene.
| **Parameters** |  | **Description** |
| --- | --- | --- |
| System Adaptive IBL |  | Used to control the intensity of System IBL. The default value is 0, with a range from 0 to 10. System IBL is used to blend with ambient light in dim environments to ensure the lighting effect of entities with PBR materials. |
| Intensity Exponent |  | Used to control the brightness of the image. The higher the value, the brighter the image. You can adjust this parameter according to the atmosphere and requirements of the scene to achieve the desired lighting effect. The default value for this parameter is 8, with a range from -24 to 24. |
| Rotation |  | Set the rotation angle of the image. The default value is (0, 0, 0). |
| Mode |  | Texture mode <br>  <br> * **NONE**: Do not use any environment texture. <br> * **SINGLE**: (default) Use one environment texture. <br> * **BLEND**: Blend two environment textures. |
| Texture Resource 1 |  | Set the first environment texture. This parameter is only available when **Mode** is set to **Single** or **Blend**. |
| Texture Resource 2 |  | Set the second environment texture. This parameter is only available when **Mode** is set to **Blend**. |
| Blend |  | Set the blend ratio for two environment textures. The default value is 50%. |
## Environment Lighting Settings
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9d3f48497b4d41bbb90a2e1246b34728~tplv-goo7wpa0wc-image.image)
Configure the amount of light an entity receives from global image-based lighting in the current scene.
| **Parameter** | **Description** |
| --- | --- |
| Scale | Used to adjust the intensity of lighting received by this entity from global image-based lighting. The larger the value, the stronger the lighting intensity received by the entity. |
## Image Based Light
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8db30f8d43374b60b3acb3b7accb92db~tplv-goo7wpa0wc-image.image)
The local image-based light component provides a local image-based light, which will override the global image-based lighting.
| **Parameter** |  | **Description** |
| --- | --- | --- |
| Intensity Exponent |  | Used to control the brightness of the image. The larger the value, the brighter the image. You can adjust this parameter according to the atmosphere and requirements of the scene to achieve the desired lighting effect. The default value for this parameter is 8, and the value range is from -24 to 24. |
| Rotation |  | Set the rotation angle of the image. The default value is (0, 0, 0). |
| Mode |  | Select the texture mode. <br>  <br> * **NONE**: Do not use any environment texture. <br> * **SINGLE**: (default) Use one environment texture. <br> * **BLEND**: Blend two environment textures. |
| Texture Resource 1 |  | Set the first environment texture. This parameter is only available when **Mode** is set to **Single** or **Blend**. |
| Texture Resource 2 |  | Set the second environment texture. This parameter is only available when **Mode** is set to **Blend**. |
| Blend |  | Set the blend ratio for two environment textures. The default value is 50%. |
## Image Based Light Receiver
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8191223bbbf144488ba31b79c46f68a9~tplv-goo7wpa0wc-image.image)
This component can associate an entity with a specific local image-based light (Image Based Light), allowing it to receive illumination from that light source.
| **Parameter** | **Description** |
| --- | --- |
| Image Based Light | Select an entity that has an Image-based light component. |
# Lighting
## Grounding Shadow
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd8d3573846f4bea9d2f28b3781eb70b~tplv-goo7wpa0wc-image.image)
This component casts a shadow on the ground for models containing a mesh. This component can only be added to a mesh-type Prim.
| **Parameter** | **Description** |
| --- | --- |
| Cast Shadows | Controls whether the model casts shadows. The default is enabled. |
| Receive Shadows | Controls whether the model receives shadows. The default is enabled. |
## Directional Light
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6532d76510f04a98b9db4cfd34834c8b~tplv-goo7wpa0wc-image.image)
This component is used to add a directional light source with parallel rays, commonly used to simulate distant light sources such as the sun.
| **Parameter** | **Description** |
| --- | --- |
| Color | The color of the light. |
| Intensity | The intensity of the light, measured in lux (lumens per square meter). The default is 1200. |
| Enable Shadows | Controls whether shadows are enabled. Disabled by default. |
| Depth Bias | Used to adjust the depth bias of shadows. The default value is 0.01. The larger the value, the greater the offset between the shadow and the surface of the object, which can reduce shadow distortion, but excessive values may cause the shadow to separate from the object. |
| Culling Mode | Determines which faces of the model are ignored when calculating shadows to optimize rendering performance. <br>  <br> * **None**: No faces are culled; all faces participate in shadow calculation. <br> * **Front**: Cull the front faces, meaning only the back faces of the model cast shadows. <br> * **Back** (default): Cull the back faces. Only the front faces of the model cast shadows, which can improve performance without affecting visual effects. |
| Projection <br>  | Set the projection method for shadows. <br>  <br> * **Auto** (default): Automatically calculate the projection range for shadows. In this mode, you need to set **Maximum Distance**. <br> * **Fixed**: Manually define a fixed projection range. In this mode, you need to set **Z Near**, **Z Far**, **Orthographic Width**, and **Orthographic Height**. |
|  |  |
| Maximum Distance | Defines the maximum distance for shadows in automatic projection mode (meters). The default value is 5. This parameter appears only when the **Projection** parameter is set to **Auto**. |
| Z Near | Indicates the near clipping plane distance (meters) of the shadow projection range when manually defining the projection range. This parameter appears only when the **Projection** parameter is set to **Fixed**. The default value is 10. |
| Z Far | Indicates the far clipping plane distance (meters) of the shadow projection range when manually defining the projection range. This parameter appears only when the **Projection** parameter is set to **Fixed**. The default value is 0.01. |
| Orthographic Width | Indicates the width (meters) of the shadow projection range under orthographic projection when manually defining the projection range. This parameter appears only when the **Projection** parameter is set to **Fixed**. The default value is 10. |
| Orthographic Height | Indicates the height (meters) of the shadow projection range under orthographic projection when manually defining the projection range. This parameter appears only when the **Projection** parameter is set to **Fixed**. The default value is 10. |
## Point Light
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8921bb0b24be4046a79903952057eb14~tplv-goo7wpa0wc-image.image)
Defines a point light source.
| **Parameter** | **Description** |
| --- | --- |
| Color | The color of the light; the default is white. |
| Intensity | Controls the brightness of the light source, measured in lumens (lm). The default value is 15000. |
| Attenuation Radius | Controls the effective range of the light source, measured in meters. The default value is 10. |
## Spot Light
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdd3c8f045354f57b9d8f7cd41a41108~tplv-goo7wpa0wc-image.image)
This component is used to add a spotlight source, with rays emitted in a cone shape from a single point, commonly used to simulate effects such as flashlights or stage lighting.
| **Parameter** | **Description** |
| --- | --- |
| Color | The color of the light; the default is white. |
| Intensity | The intensity of the light, measured in lumens (lm); the default value is 1000. |
| Attenuation Radius | Controls the effective range of the light source, measured in meters. The default value is 10. |
| Inner Angle | The inner angle of the spotlight cone, measured in degrees (°); the default value is 45. |
| Outer Angle | The outer angle of the spotlight cone, measured in degrees (°); the default value is 60. |
| Enable Shadows | Controls whether shadows are enabled. This option is disabled by default. When enabled, additional shadow setting parameters will be displayed. |
| Depth Bias | Used to adjust the depth bias of shadows (unit: meters); the default value is 0.01. The larger the value, the greater the offset between the shadow and the surface of the object. This can reduce shadow distortion, but excessive values may cause the shadow to separate from the object. |
| Culling Mode | Determines which faces of the model are ignored when calculating shadows to optimize rendering performance. <br>  <br> * **None**: No faces are culled; all faces participate in shadow calculation. <br> * **Front**: Front faces are culled, meaning only the back faces of the model cast shadows. <br> * **Back** (default): Back faces are culled. This is the most common setting. Only the front side of the model casts shadows, which can improve performance without affecting visual quality. |
| Clipping Plane <br>  | Set the shadow clipping range to define its visible distance. <br>  <br> * **Auto**: (default) Automatically calculates the clipping range. <br> * **Fixed**: Manually defines a fixed clipping range. In this mode, you need to set the **Z Near** and **Z Far** parameters. |
| Z Near | Indicates the distance (in meters) to the near clipping plane of the shadow clipping range when manually defining the clipping range. This parameter appears only when the **Clipping Plane** parameter is set to **Fixed**. The default value is 0.01. |
| Z Far | Indicates the distance (in meters) to the far clipping plane of the shadow casting range when manually defining the clipping range. This parameter appears only when the **Clipping Plane** parameter is set to **Fixed**. The default value is 10. |
| Falloff Exponent | Indicates the exponent for shadow falloff. This parameter controls the rate at which the shadow transitions from fully opaque to fully transparent. The higher the value, the faster the shadow falls off. The default value is 2. |

