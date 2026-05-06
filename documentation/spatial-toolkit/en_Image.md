Samples data from a single image or a specific layer within a multilayer image.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/52ce26301fa143e380c4cce015159bab~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Filename**: The image file used as a texture. Supports image files in .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr formats.
* **Texture Coordinates:** The two-dimensional coordinates used to read data when mapping the texture onto a surface. By default, the current UV coordinates are used, where:
   * `U` is the horizontal axis
   * `V` is the vertical axis
* **Address Mode U:** The method by which the node handles `U` values outside the `0–1` range.
* **Address Mode V:** The method by which the node handles `V` values outside the `0–1` range.
* **Filter Type:** The type of texture filtering used by the node.
   * **Closest**: Uses the texel value closest to the sampling position, without interpolation. This method does not smooth the image and typically results in a pronounced pixelated appearance when magnified.
   * **Linear**: Uses linear interpolation to blend adjacent texels. This method produces smoother results and is one of the most commonly used texture filtering methods.
   * **Cubic**: Uses cubic interpolation for texture sampling. This method generally yields smoother and higher-quality results than **Linear**, but may incur higher computational cost.

## Node usage instructions
The **Image** node can create a material based on an image file. This node samples data from a single image and maps it onto the surface of an object. The **Image** node's **Address Mode** parameter specifies how to handle `U` and `V` values that fall outside the typical `0–1` range.
**Address Mode U** and **Address Mode V** can each take one of four values to determine their behavior.

* **constant**: Currently behaves the same as **periodic**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d734420ee1943d7ab4e1eecf5ed166c~tplv-goo7wpa0wc-image.image)
* **clamp:** Boundary clamping. Texture coordinates outside the normal range are clamped to within the valid range.
   * Values greater than `1` are set to `1`
   * Values less than `0` are set to `0`
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e9904e1705ae4824a37e44055e87fb66~tplv-goo7wpa0wc-image.image)
* **periodic:** Texture coordinates outside the normal range are wrapped back into the valid range. This behavior is essentially equivalent to applying a **modulo 1** operation to the coordinates.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d734420ee1943d7ab4e1eecf5ed166c~tplv-goo7wpa0wc-image.image)
* **mirror:** Texture coordinates outside the normal range are handled in a mirrored fashion.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9507cad41267487393f0bc1417eb344b~tplv-goo7wpa0wc-image.image)


The following is a simple node graph example demonstrating how to use the **Image** node to create a material from an image:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b167b63800e45398072fa4dfcc8506a~tplv-goo7wpa0wc-image.image)
The following shows the result of applying the generated texture to a cube:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdfde48ec4884555a23a5e6db564e938~tplv-goo7wpa0wc-image.image)

