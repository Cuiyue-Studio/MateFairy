A generator for linear value gradients (ramps) from left to right.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1ec220235c4841bb9f5751118bae5c93~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Left**: The left value in the interpolation.
* **Right**: The right value in the interpolation.
* **texcoord**: The two-dimensional coordinate used to read data for mapping the texture onto the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
This node uses interpolation to generate a horizontal gradient based on two values. Any point in the gradient output is a blend of these two values. The closer a point is to one side along the horizontal direction, the more its result approaches the value corresponding to that side.
The following example shows a node graph that uses **Ramp Horizontal** to create a color gradient.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a173488f0c9a46febd807da533d1a8b1~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.504750593824228);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49524940617577196);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fec770ef4dc04d0fa7d346ad56005cf6~tplv-goo7wpa0wc-image.image)



</div>
</div>


