3D Worley noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4a2fc4497ee74b2b84bfd08475d544fa~tplv-goo7wpa0wc-image.image)
## Parameter description

* **position**: The three-dimensional coordinates used by the node to read data, which are used to map the texture to the surface. By default, the current **object space 3D coordinates** are used.
* **Jitter**: The degree of jitter or offset applied to each cell center point. The default value is **1.0**. Smaller values produce more regular patterns, while a value of **0** generates a perfect grid.

## Node usage instructions
The **Worley Noise 3D** node can generate irregular cell-like regions. This node creates a limited number of center points, and each region is a polygonal area formed around the location closest to a particular center point. Because this node generates 3D noise, the texture does not repeat along the Z axis but instead continues to extend as depth changes.
The following is a node graph example that demonstrates how to use the **Worley Noise 3D** node to generate a black-and-white pattern. Multiply the input texture coordinates by a constant float value to change the frequency of the generated noise. The higher the value, the more frequently the pattern repeats. Then pass the output to a conversion node to convert it into black-and-white color values.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ebf83c586ee04aa1890397cd2478b02b~tplv-goo7wpa0wc-image.image)


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Material with Shader Graph applied</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5daffa7c5237477499af878607d9941f~tplv-goo7wpa0wc-image.image)



</div>
</div>



