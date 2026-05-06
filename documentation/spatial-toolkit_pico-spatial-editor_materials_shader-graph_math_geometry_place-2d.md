Transforms the UV texture coordinates used for 2D texture placement.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/47128d2ff90c4e669cc82c1492aa5788~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Texture Coordinates**: The input texture coordinates to be transformed. The default value is the texture coordinates of the current surface, with an index of `0`.
* **Pivot**: The pivot point used for scaling and rotating the texture coordinates. Before applying scaling or rotation, the node subtracts this value from the **U** and **V** coordinates, then adds it back afterward.
* **Scale**: The value used to scale the texture coordinates. The node divides the **U** and **V** coordinates by this value. The default value is `(1,1)`.
* **Rotate**: The angle used to rotate the texture coordinates, in degrees. A positive value rotates the texture coordinates counterclockwise, resulting in the image rotating clockwise; a negative value does the opposite. The default value is `0`.
* **Offset**: The value used to offset the position of the texture coordinates. After scaling, rotating, and adding back the pivot point, the node subtracts this value from the texture coordinates. The default value is `(0,0)`.
* **Operation Order:** Specifies the execution order of texture coordinate transformation operations, including the following options:
   * **SRT**: Scale, rotate, translate.
   * **TRS**: Translate, rotate, scale.

## Node usage instructions
The **Place 2D** node can apply basic transformations to texture coordinates, such as scaling, rotation, and offset.
For example, if the pivot point is set to `(0.5, 0.5)`, the input texture coordinates can be scaled to half their original size, rotated 180 degrees, and offset by 0.5 in both the U and V directions. Since texture coordinates are usually in the range of 0–1, setting the pivot point to `(0.5, 0.5)` means scaling and rotation are performed around the center of the image.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/33107cf941c64dfe827ba5751f392471~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49287410926365793);">

<div style="text-align: center"><strong>Before transformation</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/45739ff2fb454517ab6b1f48cb96a224~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5071258907363421);margin-left: 16px;">

<div style="text-align: center"><strong>After transformation</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7a3ec0262e54279805bcfc4735cecb6~tplv-goo7wpa0wc-image.image)



</div>
</div>


