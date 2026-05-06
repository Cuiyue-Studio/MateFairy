Shader for surfaces using physically based rendering (PBR) materials.
The **PBR Surface** node generates a custom surface based on its input parameters. You need to connect the output of the **PBR Surface** node to the **output** node's **Surface** output port.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2d90174f46894484862b149ae9aa6350~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Base Color**: The base display color of the surface. This is the color the object appears under pure white lighting.
* **Opacity**: The opacity level of the surface. The default value is 1.0.
   * When this value is **1.0**, the surface is fully opaque.
   * When this value is less than **1.0**, the surface appears semi-transparent.
   * When this value is **0**, the surface is fully transparent.
* **Metallic**: Indicates whether the surface is metallic. The default value is 0.0.
   * **1**: Metallic surface
   * **0**: Non-metallic surface
* **Roughness**: The roughness of the surface. This value ranges from 0 to 1.0. The default value is 0.5.
   * **0**: Surface with perfect mirror reflection
   * **1.0**: Maximum roughness
* **Normal**: The normal vector in tangent space. The default value is (0,0,1).
* **Clearcoat**: The second transparent reflective layer on the surface. This property can produce a highly glossy effect. The default value is 0.0.
* **Clearcoat Normal**: The normal vector of the clearcoat layer in tangent space. The default value is (0,0,1).
* **Clearcoat Roughness**: The roughness of the clearcoat layer on the surface. The default value is 0.01.
* **Emissive**: The emissive color of the surface. This is the color displayed when the surface appears to emit light itself.
* **Ambient Occlusion**: The degree of ambient lighting received by the surface. This value is used to simulate soft shadows and subtle light variations.
* **Reflectance**: The reflectance of the surface, that is, the surface's ability to reflect light. The default value is 0.5.
* **Anisotropy Level**: The degree of anisotropy of the surface, used to control the surface's reflection characteristics in different directions. The default value is 0.0.
* **Anisotropy Direction**: The direction of anisotropy of the surface, used to specify the exact direction of the surface's reflection characteristics in different directions. The default value is (0,0,0).

## Node usage instructions
The following is a sample material that uses only the **PBR Surface** node to generate a texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f37794c195484a3a88ccabd5c8944b1b~tplv-goo7wpa0wc-image.image)
The figure below shows the effect of applying a Shader Graph material to a cone.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e2ab748c6b948cca348a00a849fd267~tplv-goo7wpa0wc-image.image" width="530px" /></div>


