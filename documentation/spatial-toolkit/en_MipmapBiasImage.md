An image node containing parameters for mipmap bias across multiple levels of texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1a8dd72718c349779561fb4990129fbc~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Filename**: The image file used as the texture.
* **Texture Coordinates:** The two-dimensional coordinates used to read data when mapping the texture onto a surface. By default, the current UV coordinates are used, where:
   * `U` is the horizontal axis
   * `V` is the vertical axis
* **Address Mode U:** The method by which the node handles `U` values outside the `0–1` range.
* **Address Mode V:** The method by which the node handles `V` values outside the `0–1` range.
* **Filter Type:** The type of texture filtering used by the node.
   * **Closest**: Uses the texel value closest to the sampling position, without interpolation. This method does not smooth the image and typically results in a pronounced pixelated appearance when magnified.
   * **Linear**: Uses linear interpolation to blend adjacent texels. This method produces smoother results and is one of the most commonly used texture filtering methods.
   * **Cubic**: Uses cubic interpolation for texture sampling. This method generally yields smoother and higher-quality results than **Linear**, but may incur higher computational cost.
* **Aniso Level Type**: The level of anisotropic filtering used by the node, which improves texture clarity on slanted surfaces or when viewed at an angle. Higher levels typically reduce blurring and loss of detail, but also increase sampling overhead. Available values include `1`, `2`, `4`, `8`, and `16`.
* **Mipmap Bias**: Used to control the level selection offset for mipmap textures. Smaller or negative values generally cause the node to prefer higher-resolution texture levels, resulting in sharper output; larger or positive values typically cause the node to prefer lower-resolution texture levels, producing smoother and more stable results.

## Node usage instructions
### Address Model parameters
**Mipmap Bias Image** nodes can create materials based on image files. This node samples data from a single image and maps it onto the surface of an object.
The **Mipmap Bias Image** node's **Address Mode** parameter specifies how to handle cases when `U` and `V` values fall outside the typical `0–1` range.
**Address Mode U** and **Address Mode V** can each take one of four values to determine their behavior.

* **constant**: Currently behaves the same as periodic.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d734420ee1943d7ab4e1eecf5ed166c~tplv-goo7wpa0wc-image.image)
* **clamp:** Boundary clamping. Texture coordinates outside the normal range are clamped to the normal range.
   * Values greater than `1` are set to `1`.
   * Values less than `0` are set to `0`.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e9904e1705ae4824a37e44055e87fb66~tplv-goo7wpa0wc-image.image)
* **periodic:** Texture coordinates outside the normal range are "wrapped" into the valid range. This behavior is essentially equivalent to applying a **modulo 1** operation to the coordinates.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d734420ee1943d7ab4e1eecf5ed166c~tplv-goo7wpa0wc-image.image)
* **mirror:** Texture coordinates outside the normal range are processed in a mirrored way.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9507cad41267487393f0bc1417eb344b~tplv-goo7wpa0wc-image.image)


The following is a simple node graph example that demonstrates how to use the **Image** node to create a material from an image:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/38d942237a744c1bb629d9bea3e9f941~tplv-goo7wpa0wc-image.image)
The following shows the result of applying the generated texture to a cube:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdfde48ec4884555a23a5e6db564e938~tplv-goo7wpa0wc-image.image)
### Adjust texture clarity
You can adjust texture clarity using the **Aniso Level Type** and **Mipmap Bias** parameters.
<div style="text-align: center"><strong>Aniso Level Type = 1, Mipmap Bias = 0</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/823155ea0dda431f95dd3afc1eaadf87~tplv-goo7wpa0wc-image.image" width="430px" /></div>



<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5070508944861545);">

<div style="text-align: center"><strong>Aniso Level Type = 1, Mipmap Bias = 1</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/281c108f5db74c4d99439bab27047a8c~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49279911301347046);margin-left: 16px;">

<div style="text-align: center"><strong>Aniso Level Type = 16, Mipmap Bias = 1</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bcaa057d313a44ee83f9eb44c0ff3d8f~tplv-goo7wpa0wc-image.image)



</div>
</div>


