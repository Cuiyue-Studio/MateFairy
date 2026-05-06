2D Worley noise generator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dd8b91e63a314137b0ce397071b78a94~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **texcoord**: Used to read data for mapping the texture to the surface's two-dimensional coordinates. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.
* **Jitter**: The degree of jitter or offset for each cell center. The default value is 1.0. Smaller values produce more regular patterns, while 0 generates perfect squares.

## **Node usage instructions**
The **Worley Noise 2D** node programmatically generates irregular cellular regions. It creates a limited number of center points, and each region is a polygon formed around a center point, consisting of the positions closest to that center point.
The figure below shows an example of using the **Worley Noise 2D** node to programmatically generate a black-and-white pattern. Multiplying the input texture coordinates by a constant floating-point value can change the frequency of the generated noise. The larger the value, the more frequently the pattern repeats. Then, pass the output into a conversion node to turn it into black-and-white color values.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72294e15e647455e844360662f34c551~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5129107981220657);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48708920187793425);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0b3f81d51e524ee8a38714d9f561a5e2~tplv-goo7wpa0wc-image.image)



</div>
</div>



