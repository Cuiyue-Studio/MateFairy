Surface shader for materials without lighting.
The **Unlit Surface** node generates a custom surface based on its Input parameters. You need to connect the output of the **Unlit Surface** node to the **output** node's  **Surface** output port.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/84c24159571e4931a7e890c815704848~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Color**: The base color of the surface.
* **Opacity**: The opacity of the surface. The default value is **1.0**.
   * When this parameter is set to **1.0**, the surface is fully opaque.
   * When the value is less than **1.0**, the surface appears semi-transparent.
   * When the value is **0**, the surface is fully transparent.

## Node usage instructions
The following is a sample material that uses only the **Unlit Surface** node to generate a texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e63165e8abfa42a0999df3db8e42da4e~tplv-goo7wpa0wc-image.image)
The figure below shows the effect of applying a Shader Graph material to a cone.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7728c387c4914472b4d943438422da50~tplv-goo7wpa0wc-image.image" width="515px" /></div>


