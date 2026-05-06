3D Perlin noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/56f0c57d49a241af8ac743092f198827~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Amplitude**: The intensity of the generated noise. The higher the amplitude, the more pronounced the variations in the noise pattern.
* **Pivot**: The neutral value of the noise. This value is the minimum value of the noise. After the node multiplies the output by the amplitude, this value is added to the final output.
* **Position**: The three-dimensional coordinates used when reading data, which are used to map the texture onto the surface. By default, the current object space 3D coordinates are used.

## Node usage instructions
The **Noise 3D** shader node can procedurally generate Perlin noise patterns. You can use it to add texture and variation to materials. Before applying **Amplitude** and **Pivot**, all procedurally generated noise values are between 0 and 1. Because this node generates 3D noise, the texture does not repeat along the Z direction but continues to extend as depth changes.
The following is a node graph example that demonstrates how to use the **Noise 3D** node to procedurally generate a black-and-white pattern. Multiply the input **Position** by a constant **Float**. This Float increases the frequency of the generated noise, so the pattern repeats more frequently.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e7c168784e74ef48fd06c7562df2aa2~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fc3c01a6b10c480c9f23802b4c7c1272~tplv-goo7wpa0wc-image.image)



</div>
</div>



