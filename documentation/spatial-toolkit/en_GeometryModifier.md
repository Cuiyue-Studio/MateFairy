Modify the vertex properties of the geometry based on input parameters. This operation is executed once per vertex.
You need to connect it to the **output** node's **Geometry Modifier**  port.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f318e20adfa74c07a3bf222a4b319a69~tplv-goo7wpa0wc-image.image)
## Parameter descriptions

* **Model Position Offset**: Model position offset. Used to offset the position of geometry vertices along a direction in model space.
* **Color**: Color property. Provides color data for the geometry, typically used as vertex colors for subsequent shading.
* **Normal**: Normal vector. Defines the surface orientation and affects lighting and reflection calculations.
* **Tangent**: Tangent vector. Defines the surface tangent space, commonly used for normal mapping and directional shading calculations.
* **Uv0**: First set of UV coordinates, typically used for main texture mapping.
* **Uv1**: Second set of UV coordinates, typically used for additional texture mapping or lightmapping.
* **Uv2 ~ Uv7**: Additional UV or custom data channels, can be used to store extended texture coordinates or other shading data.

## Node usage instructions
The following is a sample material that uses both the **PBR Surface** node and the **Geometry Modifier** node to generate a texture for a cone that is slightly inflated along the normal and features a tech-style shading effect.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/34bcd272ef1d41b3833b1a2693e0c8ed~tplv-goo7wpa0wc-image.image)
The figure below shows the effect of applying a Shader Graph material to the cone.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6e01e5b104134502ab33ff08b0d6f068~tplv-goo7wpa0wc-image.image" width="619px" /></div>


