Receives an RGB color as Input and outputs a grayscale value that contains the luminance information of that color in all color channels.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c8d3ef152c834dd79a32787b8d9d8168~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The Input color to be processed.
* **Luma Coefficients**: Defines the weighting coefficients of the color space used to calculate luminance. Different color spaces define the contributions of the R, G, and B channels differently. Optional values are the luminance coefficients of the **acescg**, **rec2020/rec2100**, or **rec709** color spaces. The default value is the luminance coefficients of **acescg**, that is, `(0.2722287, 0.6740818, 0.0536895)`.

## Node usage instructions
This node calculates the final grayscale value by computing the dot product of the Input color vector and the luminance coefficients (<a i=1>Luma Coefficients</a>).
The following is a simple node graph example showing how to use the **Luminance** node (with **Type** set to **Color 3**) to convert an image to grayscale:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/07e6b9fc66b54312a665640e0bdb36db~tplv-goo7wpa0wc-image.image)
The comparison before and after applying Shader Graph is as follows:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48931116389548696);">

<div style="text-align: center">Before applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/219e9c9ab5dc4b3caaf00dbb05be813c~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5106888361045131);margin-left: 16px;">

<div style="text-align: center">After applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/416bb827c55e4303825b9c8421fafb87~tplv-goo7wpa0wc-image.image)



</div>
</div>


