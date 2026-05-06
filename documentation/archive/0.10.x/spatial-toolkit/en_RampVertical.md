A vertical linear gradient generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ffed1a8b95224e19814de22bccba7ebf~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Top**: The top value in the interpolation.
* **Bottom**: The bottom value in the interpolation.
* **texcoord**: The two-dimensional coordinate used to read data for mapping the texture to the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
This node uses interpolation to generate a vertical gradient based on two values. Any point in the gradient output is a blend of these two values. The closer a point is to one side vertically, the more its result approaches the value corresponding to that side.
The following example shows a simple node graph that uses **Ramp Vertical** to create a color gradient.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3151fc7d645a410ba9e284b11718fdd2~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5051635111876076);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49483648881239245);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/824bc92fc5f04307819100839ea9152c~tplv-goo7wpa0wc-image.image)



</div>
</div>


