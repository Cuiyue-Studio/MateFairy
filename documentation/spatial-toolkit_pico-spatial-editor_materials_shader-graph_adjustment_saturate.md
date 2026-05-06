Adjusts the saturation of a color.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2d1073be206744ad84cd4f45fe8b48d3~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The input color whose saturation will be adjusted.
* **Amount**: The multiplier used to adjust saturation. The default value is `1.0`.
* **Luma Coefficients**: Defines the weighting coefficients of the color space used to calculate luminance. Different color spaces define the contributions of the R, G, and B channels differently. Optional values are the luminance coefficients of the **acescg**, **rec2020/rec2100**, or **rec709** color spaces. The default value is the luminance coefficients of **acescg**, that is, `(0.2722287, 0.6740818, 0.0536895)`.

## Node usage instructions
The **Saturate** node performs linear interpolation between the input color value and its luminance value.

* When the **Amount** parameter is set to `0`, the output is the grayscale version of the input, which is equivalent to the output of the **Luminance** node.
* When the **Amount** parameter is set to `1`, the original color is output.

The effect of this node is different from adjusting saturation using the **HSVAdjust** node. The **Saturate** node takes color space into account when processing modifications.

The following is an example of using the **Saturate** node to modify image saturation:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2fe695f224ff4ed891137c2e12756f68~tplv-goo7wpa0wc-image.image)
The comparison of effects before and after adjusting the Amount value is as follows:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48931116389548696);">

<div style="text-align: center">Amount = 0.5</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4dc77709c33a46038e981e331afaf7e2~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5106888361045131);margin-left: 16px;">

<div style="text-align: center">Amount = 2</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6f5c0359485f451ebe642b6849f39cae~tplv-goo7wpa0wc-image.image)



</div>
</div>


