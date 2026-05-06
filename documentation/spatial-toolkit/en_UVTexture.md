USD UV texture reader for MaterialX.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/440236ad3efd4f46bfaa5f0f7cbb6a54~tplv-goo7wpa0wc-image.image)
## Parameter description

* **File**: The image file used as the texture.
* **St**: The two-dimensional coordinates used to read data when mapping the texture onto a surface. This node maps the st coordinates to the uv image space. In st image space:
   * Coordinate `(0,0)` maps to `(0,0)` in uv space, representing the lower left corner.
   * Coordinate `(1,1)` maps to `(1,1)` in uv space, representing the upper right corner.
* **Wrap S**: How the node handles `S` values outside the `0–1` range.
* **Wrap T**: How the node handles `T` values outside the `0–1` range.
* **Scale**: The scaling value applied by the node to all components of the texture. The node multiplies the texture values by this parameter.
* **Bias**: The offset value applied by the node to all components of the texture. The node first multiplies the texture values by **Scale**, then adds this parameter.

## Output description

* **r**: Outputs only the red component of the texture.
* **g**: Outputs only the green component of the texture.
* **b**: Outputs only the blue component of the texture.
* **a**: Outputs only the alpha component of the texture.
* **rgba**: Color 4 output of the texture, including:
   * Red
   * Green
   * Blue
   * Alpha
* **rgb**: Color 3 output of the texture, including:
   * Red
   * Green
   * Blue

## Node usage instructions
The **Wrap S** and **Wrap T** parameters of the node specify how the node handles cases when the values of `S` and `T` exceed the normal `0–1` range. The **Wrap S** and **Wrap T** parameters can be set to the following values:

* **black**: Texture coordinates outside the normal range return black.
* **clamp**: Texture coordinates outside the normal range are clamped to the normal range.
   * Values greater than `1` are set to `1`
   * Values less than `0` are set to `0`
* **periodic**: Texture coordinates outside the normal range are normalized to the `0–1` range, enabling image tiling. This is essentially equivalent to applying a **modulo 1** operation to the coordinates.
* **mirror**: Coordinates outside the range are repeated in a mirrored fashion. Adjacent intervals alternate in direction, so the texture appears as a 'mirrored tile'.



