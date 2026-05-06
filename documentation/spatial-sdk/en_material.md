Material defines how an object's surface interacts with light, thereby determining its visual properties, such as metallic appearance, transparency, and glossiness. Materials perform lighting calculations through shaders and work with textures to produce rich visual effects.
Core components of the material system include:

* **Shader program**: defines lighting models and rendering algorithms;
* **Properties:** base color, normal, metallic, roughness, opacity, reflectivity, emissive intensity, and more.

## Material types
PICO Spatial SDK supports the following commonly used materials, including UnlitMaterial and PhysicallyBasedMaterial.
| **Material name** | **Definition** | **Main features** | **Use cases** |
| --- | --- | --- | --- |
| UnlitMaterial | UnlitMaterial is a type of material that is not affected by light, is always rendered using flat shading, and whose appearance is determined entirely by its texture and material properties. | * No lighting calculations for maximum performance. <br> * Does not cast or receive shadows. <br> * Visual effects are stable and consistent, but lack realism. | UI elements, special effect maps (such as particles, 2D icons), cartoon rendering, and objects that must always maintain a consistent appearance. |
| PhysicallyBasedMaterial | Physically Based Material (PBR, physically based rendering material) is a material that simulates the appearance of real-world objects. | * High realism. <br> * Can adapt to various lighting conditions. <br> * Computationally intensive and has high performance requirements. | High-quality games, film-level rendering, VR/AR apps, and scenarios with high requirements for realistic materials. |
The PICO Spatial SDK supports relatively advanced materials, including ShaderGraphMaterial and VideoMaterial. For more information, refer to "[ShaderGraphMaterial](/en_shader-graph-material)" and "[Use the VideoMaterial](/en_use-video-material)".
PhysicsMaterialResource is a material resource specifically used for physics simulation. It defines the surface properties of objects during physical interactions, including static friction, dynamic friction, and the restitution coefficient. Unlike rendering-oriented materials (such as UnlitMaterial or PhysicallyBasedMaterial), physical materials do not affect visual appearance or rendering effects; they only influence an object's collision, friction, and movement behavior. As its functionality is completely independent from the rendering material system described in this article, it will not be discussed in detail here.

## Load materials
You can load materials using the following methods:

* After loading a 3D model using `Entity.load` and obtaining the returned entity instance `modelEntity`, you can access the `ModelComponent` instance of `modelEntity` to retrieve the material resource list and the corresponding material resources. For more information, refer to "[Model](/en_model)".
* Call `assetBundle.loadMaterial` to load UnlitMaterial, PhysicallyBasedMaterial, and ShaderGraphMaterial from a Spatial Editor project. For more information, refer to "[AssetBundle](/en_asset-bundle)".
* Call `ShaderGraphMaterial.loadFromAssetBundle` to load the `ShaderGraphMaterial` from the Spatial Editor project. For more information, refer to "[ShaderGraphMaterial](/en_shader-graph-material)".

## Create materials
The SDK provides static functions for creating material instances. When creating a material, you need to pass the `BlendingMode` parameter to specify its blending mode.
```Kotlin
fun createMaterialResourceExample() {
    val unlitMaterial = UnlitMaterial.create(BlendingMode.OPAQUE)
    val pbrMaterial = PhysicallyBasedMaterial.create(BlendingMode.TRANSPARENT)
}
```

## Adjust properties for materials
After creating a material, you can modify the properties of the material instance as needed. The properties supported by UnlitMaterial and PhysicallyBasedMaterial are as follows:
| **property** | **Description** | **UnlitMaterial** | **PhysicallyBasedMaterial** |
| --- | --- | --- | --- |
| CullingMode | Face culling mode, controlling which surfaces are rendered. <br>  <br> * `FRONT`: Only render the back surface <br> * `BACK`: Only render the front surface <br> * `NONE`: Render both the back and front surfaces <br> * `FRONT_AND_BACK`: Do not render both the back and surfaces | ✅ | ✅ |
| PolygonFillMode | Polygon fill mode. <br>  <br> * `FILL`: solid fill <br> * `LINE`: wireframe mode | ✅ | ✅ |
| DepthTest | Depth testing switch, which controls whether depth comparison is performed to determine the front-to-back relationship of pixels. | ✅ | ✅ |
| DepthWrite | Depth write switch, which controls whether pixel depth is written to the depth buffer. | ✅ | ✅ |
| BlendingMode | Blending mode, which controls how the current pixel blends with the background pixel. <br>  <br> * `OPAQUE`: completely opaque <br> * `TRANSPARENT`: standard alpha transparency, but maintains highlight effect <br> * `ADD`: additive color blending <br> * `FADE`: alpha fades, and highlights and reflections fade simultaneously <br> * `MASKED`: mask mode, where pixels are fully transparent or opaque depending on the threshold | ✅ | ✅ |
| Opacity | Transparency, which controls the overall transparency of the material. Range: [0.0f, 1.0f]. | ✅ | ✅ |
| BaseColor | Base color, that is, the main color of the material. In PBR, this is the diffuse color; in other materials, it is the main color. The data type is `Color4`. | ✅ | ✅ |
| BaseColorTexture | Base color map. It provides the texture map for the base color and is multiplied by `BaseColor` to produce the final color. | ✅ | ✅ |
| Roughness | Roughness, which ontrols the microscopic roughness of the surface and affects the sharpness of reflections. Range: [0.0f, 1.0f]. `0` represents a mirror-like surface, `1` represents a completely rough surface. | - | ✅ |
| RoughnessTexture | Roughness map, which provides texture variation information for surface roughness.” | - | ✅ |
| Metallic | Metallic, which controls the metallic property of the material. Range: [0.0f, 1.0f]. `0` represents non-metal (dielectric), `1` represents pure metal. | - | ✅ |
| MetallicTexture | Metallic map, which provides texture variation information for surface metalness, and can also be packed together with the roughness texture for use. | - | ✅ |
| NormalScale | Normal strength, which controls the effect of the normal map and adjusts the prominence of surface details. The range is [0.0f, 1.0f]. | - | ✅ |
| NormalTexture | Normal map, which stores normal information for surface details and is used to enhance surface relief without increasing geometric complexity. | - | ✅ |
| AmbientOcclusion | Ambient occlusion intensity, which controls the strength of the ambient occlusion map, and adjusts the level of shadowing in surface gaps and recesses to enhance the sense of depth. | - | ✅ |
| AmbientOcclusionTexture | Ambient occlusion map , which provides shadow information for surface crevices and recesses, and typically contains baked static shadow data. | - | ✅ |
| EmissiveColor | Self-illumination color, which is the color of light emitted by the material itself, unaffected by lighting. The data type is `Color4`. | - | ✅ |
| EmissiveTexture | Emissive map. It defines which areas of the material emit light, as well as the color and intensity of the light. | - | ✅ |
Among them, the common properties supported by all three materials include:

* **Rendering control**: CullingMode, PolygonFillMode, DepthTest, DepthWrite
* **Blending and transparency**: BlendingMode, Opacity
* **Basic appearance**: BaseColor, BaseColorTexture

PBR material-specific properties include:

* **Physical properties**: Roughness, Metallic, and their corresponding textures
* **Detail enhancement**: NormalScale, NormalTexture
* **Lighting enhancement**: AmbientOcclusion, EmissiveColor, and their texture

Material complexity decreases in the following order:

* **PhysicallyBasedMaterial**: Offers the most comprehensive functionality and supports all properties
* **UnlitMaterial**: Only supports basic color and rendering control

## Usage recommendations
It is recommended to use materials by the following process:

1. Select the appropriate material type based on the object type.
2. Select the required textures (must include at least the base color texture).
3. Adjust the material's properties to optimize visual effects.
4. Test the material's performance under different lighting conditions.

## API reference
`UnlitMaterial` and `PhysicallyBasedMaterial` classes provide material-related properties and functions. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
