2D cellular noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/025a89655bb34c958ad83802c20ba47f~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **texcoord**: The two-dimensional coordinate used when reading data, used for mapping the texture onto the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
The **Cellular Noise 2D** node procedurally generates noise patterns, which can be used to add texture and variation to the material.
For example, you can multiply the Input texture coordinates by a constant floating-point value to increase the noise frequency, making the pattern repeat more frequently. Then, use the **Convert** node to convert the floating-point output to a black-and-white color output and apply the result to the model surface.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d03fce320ce4db583793b30480bc639~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.505938242280285);">

<div style="text-align: center"><strong>Original material</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.49406175771971494);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e9a5959d7991405cac85692bcce8b619~tplv-goo7wpa0wc-image.image)



</div>
</div>


