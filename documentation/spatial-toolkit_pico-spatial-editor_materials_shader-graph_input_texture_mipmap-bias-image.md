An image node containing parameters for mipmap bias across multiple levels of texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1a8dd72718c349779561fb4990129fbc~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Filename**: The image file used as the texture. Supports texture formats including .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr.
* **Texture Coordinates:** Two-dimensional coordinates used to read data when mapping the texture onto a surface. By default, the current UV coordinates are used, where:
   * `U` is the horizontal axis
   * `V` is the vertical axis
* **Address Mode U:** The method by which the node handles `U` values outside the `0–1` range.
* **Address Mode V:** The method by which the node handles `V` values outside the `0–1` range.
* **Filter Type:** The type of texture filtering used by the node.
   * **Closest**: Uses the texel value closest to the sampling position, without interpolation. This method does not smooth the image and typically results in a noticeable pixelated appearance when magnified.
   * **Linear**: Blends adjacent texels using linear interpolation. This method produces smoother results and is one of the most commonly used texture filtering methods.
   * **Cubic**: Samples the texture using cubic interpolation. This method generally yields smoother and higher-quality results than **Linear**, but may incur higher computational cost.
* **Aniso Level Type**: The anisotropic filtering level used by the node, which improves texture clarity on slanted surfaces or when viewed at an angle. Higher levels typically reduce blurring and loss of detail, but also increase sampling overhead. Available values include `1`, `2`, `4`, `8`, and `16`.
* **Mipmap Bias**: Controls the bias for selecting mipmap levels. Smaller or negative values generally cause the node to prefer higher-resolution texture levels, resulting in sharper output; larger or positive values generally cause the node to prefer lower-resolution texture levels, resulting in smoother and more stable output.

## Node usage instructions
### Address Model parameters
The **Mipmap Bias Image** node can create a material based on an image file. This node samples data from a single image and maps it onto the surface of an object.
The **Mipmap Bias Image** node's **Address Mode** parameter specifies how to handle cases when the values of `U` and `V` exceed the typical `0–1` range.
**Address Mode U** and **Address Mode V** can each take one of four values to determine their behavior.

* **constant**: Boundary clamping. Texture coordinates outside the normal range are clamped to the normal range.
   * Values greater than `1` are set to `1`.
   * Values less than `0` are set to `0`.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/82d07b8e108e41e2bbaca378f98200c9~tplv-goo7wpa0wc-image.image)
* **clamp:** Edge clamping. Texture coordinates outside the normal range use the color of the edge pixel.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/da3f0d883bdd4ea29215ebdd3e5bc404~tplv-goo7wpa0wc-image.image)
* **periodic:** Texture coordinates outside the normal range "wrap around" to the normal range. This behavior is essentially equivalent to applying a **modulo 1** operation to the coordinates.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0cd70885faa6472bbfaecadfb77fa9ce~tplv-goo7wpa0wc-image.image)
* **mirror:** Texture coordinates outside the normal range are handled in a mirrored manner.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9a8a91de18874eb59a7c11012f59c83c~tplv-goo7wpa0wc-image.image)


The following is a simple node graph example showing how to use the **Image** node to create a material from an image:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/38d942237a744c1bb629d9bea3e9f941~tplv-goo7wpa0wc-image.image)
The following shows the result of applying the generated texture to a cube:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/446b5420ab6747dc9961ee811dd83425~tplv-goo7wpa0wc-image.image)
### Adjusting texture clarity
You can adjust texture clarity using the **Aniso Level Type** and **Mipmap Bias** parameters.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Aniso Level Type = 1，Mipmap Bias = 0</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/628544150fc54ff68427cecc2dfa5700~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Aniso Level Type = 1，Mipmap Bias = 1</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9c2f5f9328254b40af6d266be2570e83~tplv-goo7wpa0wc-image.image)



</div>
</div>


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49636205838164144);">

<div style="text-align: center"><strong>Aniso Level Type = 1，Mipmap Bias = 2</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/225f10c548574d8fa3e3b6840afe6da6~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5034879491179836);margin-left: 16px;">

<div style="text-align: center"><strong>Aniso Level Type = 16，Mipmap Bias = 2</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bef85e100d36445ea21f3bac578bb841~tplv-goo7wpa0wc-image.image)



</div>
</div>


