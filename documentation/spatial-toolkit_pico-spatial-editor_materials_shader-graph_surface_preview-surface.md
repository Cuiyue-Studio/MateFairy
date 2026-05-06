MaterialX version of USD Preview Surface.
The **Preview Surface** node generates a custom surface based on its Input parameters. You need to connect the output of the **Preview Surface** node to the **output** node's **Surface** output port.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2fd6039ffeb847dd8d442fafe47e4355~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Diffuse Color**: The base display color of the surface. This is the color the object appears under pure white lighting.
* **Emissive Color**: The surface's self-illumination color. This is the color shown when the surface appears to emit light on its own.
* **Use Specular Workflow:** Specifies whether the material uses the specular workflow. When enabled, the material's reflection property is interpreted according to the specular model, rather than being controlled by the metallic model.
   * **0**: Uses the Metallic workflow.
   * **1**: Uses the Specular workflow.
* **Metallic**: Indicates whether the surface is metallic. The default value is 0.0.
   * **1**: Metallic surface.
   * **0**: Non-metallic surface.
* **Roughness**: The surface's roughness. This value ranges from 0 to 1.0. The default value is 0.5.
   * **0**: Surface with perfect specular reflection.
   * **1.0**: Maximum roughness.
* **Clearcoat**: The second transparent reflective layer on the surface. This property produces a highly glossy effect. The default value is 0.0.
* **Clearcoat Roughness**: The roughness of the clearcoat layer; default value is 0.01.
* **Opacity**: The opacity of the surface. The default value is 1.0.
   * When this value is 1.0, the surface is fully opaque.
   * When this value is less than 1.0, the surface appears semi-transparent.
   * When this value is 0, the surface is fully transparent.
* **Ior**: The index of refraction used by the node when the surface is semi-transparent or specular. The index of refraction defines the degree to which light bends or refracts as it passes through the material. The default value is 1.5.
* **Normal**: The normal vector in tangent space; default value is (0, 0, 1).
* **Occlusion**: The degree of ambient occlusion received by the surface. This value is used to simulate soft shadows and subtle variations in shading. The default value is 1.0.
   * **0.0**: Indicates fully occluded areas.
   * **1.0**: Indicates unoccluded areas.

## Node usage instructions
The following is a sample material that uses only the **Preview Surface** node to generate a texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3c2cddf234dc458ba9d322bc0c88a104~tplv-goo7wpa0wc-image.image)
The figure below shows the effect of applying a Shader Graph material to a cone.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f9b2f0204f584c61b28830c76146c528~tplv-goo7wpa0wc-image.image" width="524px" /></div>



