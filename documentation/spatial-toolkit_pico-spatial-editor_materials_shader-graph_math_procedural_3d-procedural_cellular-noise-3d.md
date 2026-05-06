3D cellular noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b1b38e6cad724ef0bad8892d8d55cf49~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **position**: Used to read data for mapping the texture to the surface's three-dimensional coordinates. The current three-dimensional object space coordinates are used by default.

## **Node usage instructions**
The **Cellular Noise 3D** shader node generates noise patterns procedurally, which can be used to add texture and variation to the material. Because this node generates three-dimensional noise, the texture does not repeat along the **Z** direction, but instead extends continuously as the depth changes.
The following example shows a node graph that uses the **Cellular Noise 3D** node to procedurally generate a black-and-white pattern. Multiplying the Input **Position** by a constant floating-point value can change the frequency of the generated noise. The larger the value, the more frequently the pattern repeats. The output of this node then passes through a **Convert** node, converting the floating-point output to a black-and-white color output.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f7b9887cfd26427aa95de306f357433b~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5129107981220657);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48708920187793425);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ec76045be85b40b59740ccfed8fea2c4~tplv-goo7wpa0wc-image.image)



</div>
</div>


