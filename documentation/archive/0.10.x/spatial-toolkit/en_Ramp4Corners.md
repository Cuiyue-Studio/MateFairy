Four-point linear value gradient (ramp) generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/786b931a174d4949ba5435a4030e41cf~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Top Left**: The value at the top left corner in four-point interpolation.
* **Top Right**: The value at the top right corner in four-point interpolation.
* **Bottom Left**: The value at the bottom left corner in four-point interpolation.
* **Bottom Right**: The value at the bottom right corner in four-point interpolation.
* **texcoord**: The two-dimensional coordinates used to read data for mapping the texture onto the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
This node uses bilinear interpolation to generate a gradient based on the values of the four corner points. Any point in the output gradient is a blended result of the four corner values. The closer a point is to a particular corner, the more its value approaches that corner's value.
The following example shows a node graph that uses **Ramp 4 Corners** to create a gradient composed of four different colors.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9baefa7191cb4a1391a3054507ef8061~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49406175771971494);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.505938242280285);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b3f45ccf4bc0425f8cd18d428ab13187~tplv-goo7wpa0wc-image.image)



</div>
</div>


