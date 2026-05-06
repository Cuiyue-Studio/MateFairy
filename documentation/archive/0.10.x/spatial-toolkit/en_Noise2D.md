2D Perlin noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b4ce2debcd0464fbc9b70e49ce89f3a~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Amplitude**: The intensity of the generated noise. The higher the value, the more pronounced the variations in the noise pattern.
* **Pivot**: The reference value of the noise. This value can be considered the minimum value of the noise and will be added to the output after the result is multiplied by **Amplitude**.
* **texcoord**: The two-dimensional coordinates used to read data for mapping the texture onto the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
The **Noise 2D** shader node generates Perlin noise patterns procedurally, which can be used to add texture and variation to the material. Before applying **Amplitude** and **Pivot**, all procedurally generated noise values are within the range of 0 **to** 1.
The following example shows a node graph that uses the **Noise 2D** node to procedurally generate a black-and-white pattern. Multiplying the input texture coordinates by a constant floating-point value increases the frequency of the generated noise, making the pattern repeat more frequently.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/766b52d7348641a887b4aca79bb1cd1b~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.505938242280285);">

<div style="text-align: center"><strong>Original material</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49406175771971494);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a6d454f965c9412ca9627ad6903514c7~tplv-goo7wpa0wc-image.image)



</div>
</div>


