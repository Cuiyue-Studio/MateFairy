Sample data from a single image or from a specific layer in a multilayer image.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/52ce26301fa143e380c4cce015159bab~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Filename**: The image file used as a texture.
* **Texture Coordinates:** The two-dimensional coordinates used to read data when mapping the texture onto a surface. By default, the current UV coordinates are used, where:
   * `U` is the horizontal axis
   * `V` is the vertical axis
* **Address Mode U:** How the node handles `U` values outside the `0–1` range.
* **Address Mode V:** How the node handles `V` values outside the `0–1` range.
* **Filter Type:** The type of texture filtering used by the node.
   * **Closest**: Uses the texel value closest to the sampling position, without interpolation. This method does not smooth the image and typically results in a pronounced pixelated appearance when magnified.
   * **Linear**: Blends adjacent texels using linear interpolation. This method produces smoother results and is one of the most commonly used texture filtering methods.
   * **Cubic**: Samples the texture using cubic interpolation. This method generally yields smoother and higher-quality results than **Linear**, but may incur higher computational cost.

## Node usage instructions
The **Image** node can create a material based on an image file. This node samples data from a single image and maps it onto the surface of an object. The **Image** node's **Address Mode** parameter specifies how to handle cases where the values of `U` and `V` exceed the typical `0–1` range.
**Address Mode U** and **Address Mode V** can each take one of the following four values to determine their behavior.

* **constant**: Boundary clamp. Texture coordinates outside the normal range are clamped to the valid range.
   * Values greater than `1` are set to `1`
   * Values less than `0` are set to `0`
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/82d07b8e108e41e2bbaca378f98200c9~tplv-goo7wpa0wc-image.image)
* **clamp:** Edge clamp. Texture coordinates outside the normal range use the color of the edge pixel.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/da3f0d883bdd4ea29215ebdd3e5bc404~tplv-goo7wpa0wc-image.image)
* **periodic:** Texture coordinates outside the normal range are wrapped back into the valid range. This behavior is essentially equivalent to applying a **modulo 1** operation to the coordinates.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0cd70885faa6472bbfaecadfb77fa9ce~tplv-goo7wpa0wc-image.image)
* **mirror:** Texture coordinates outside the normal range are handled in a mirrored fashion.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9a8a91de18874eb59a7c11012f59c83c~tplv-goo7wpa0wc-image.image)

The following is a simple node graph example showing how to use the **Image** node to create a material from an image:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b167b63800e45398072fa4dfcc8506a~tplv-goo7wpa0wc-image.image)
The following shows the result of applying the generated texture to a cube:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/446b5420ab6747dc9961ee811dd83425~tplv-goo7wpa0wc-image.image)


