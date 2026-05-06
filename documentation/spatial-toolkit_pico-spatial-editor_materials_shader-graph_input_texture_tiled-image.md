Used for sampling data from an image and provides offset and tiling functionality in UV space.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d58945415a84b68b053f3bb79ec7aa1~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: The image file used as a texture. Supports texture formats including .png, .jpg/.jpeg, .bmp, .tga, .hdr, and .exr.
* **texcoord**: The two-dimensional coordinates used to read data when mapping the texture onto a surface. By default, the current UV coordinates are used, where:
   * `U` is the horizontal axis
   * `V` is the vertical axis
* **UV Tiling**: The tiling rate of the given image along the `U` and `V` axes. Mathematically, this is equivalent to multiplying the Input texture coordinates by the given two-dimensional vector value. This tiling rate controls the repetition frequency of **File** within the texture.
* **UV Offset**: The offset of the given image along the `U` and `V` axes. Mathematically, this is equivalent to subtracting the given two-dimensional vector value from the Input texture coordinates.
* **Real World Image Size**: The real-world size represented by the **File** image.
* **Real World Tile Size**: The real-world size of a single `0–1` UV square tiling unit.
* **Filter Type**: The type of texture filtering to use.
   * **Closest**: Uses the texel value closest to the sampling position, without interpolation. This method does not smooth the image and typically results in a pronounced pixelated appearance when magnified.
   * **Linear**: Blends adjacent texels using linear interpolation. This method produces smoother results and is one of the most commonly used texture filtering methods.
   * **Cubic**: Samples the texture using cubic interpolation. This method generally yields smoother and higher-quality results than **Linear** , but may incur higher computational cost.

## Node usage instructions
The **Tiled Image** node maps the texture onto the surface in a repeated tiling manner. It allows you to specify a texture image and control its tiling and real-world size properties. The **UV Tiling** parameter repeats the given file input to generate a tiled image. The **Real World Image Size** parameter determines the real-world size of the image. The generated image is then applied to a single `0–1` UV square tiling unit. The **Real World Tile Size** parameter determines the size of this tiling unit. The size of this tiling unit can differ from the size of the image.

* If the image size is larger than the size of the tile unit it is applied to, only part of the image will be displayed in the resulting texture.
* If the tile unit size is larger than the image size, the image will be repeated in the resulting texture.

To repeat the image, use the **UV Tiling** parameter. Although you can also create repeating textures using the **Real World Image Size** and **Real World Tile Size** parameters, these size parameters are better suited for describing:

* The size of the image in the real world
* and the real-world size of the surface it is applied to

The following is a simple node graph example using the **Tiled Image** node. If the **UV Tiling** parameter is `(2, 2)`, the pattern will:

* repeat twice horizontally
* repeat twice vertically

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ef8786e36356437bbe6489a8282ec014~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>UV Tiling</strong> parameter is <code>(1, 1)</code></div>


![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d9a896a48e784e4ebdcff1d94cdaeb3a~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>UV Tiling</strong> parameter is <code>(2, 2)</code></div>

<div style="text-align: center"></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eb1c54dbddaf4eb68be43a6ef488471b~tplv-goo7wpa0wc-image.image)



</div>
</div>


