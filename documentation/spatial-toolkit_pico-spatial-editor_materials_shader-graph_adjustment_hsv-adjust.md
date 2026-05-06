Adjusts the HSV of the input RGB color using a vector.
This node does not change the Alpha channel value of the **Color 4** type.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b39bc67c74464795b06573a42ca6fd6d~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: Input color.
* **Amount**: HSV adjustment amount. The default value is `(0,1,1)`, which means no adjustment to HSV. The three **Float** values of the **Amount** vector represent the following:
   * **Hue**: Adds the first value of the `Amount` vector to the hue of the color. A positive value rotates the hue in the direction of 'red → green → blue'. A value of `1` represents a full rotation, resulting in no change to the color.
   * **Saturation**: Multiplies the saturation of the color by the second value of the `Amount` vector.
   * **Value**: Multiplies the value (brightness) of the color by the third value of the `Amount` vector.

## Node usage instructions
The following is a Shader Graph example that uses the **HSV Adjust** node (**Type** set to **Color 3**) to adjust the HSV of the texture to (0.5, 1, 1).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6379b0cc91b84d8bb4ee283ef755f3ea~tplv-goo7wpa0wc-image.image)
The comparison of the effect before and after applying the Shader Graph is as follows:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48931116389548696);">

<div style="text-align: center">Before applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/769c71663eb74b92a8bd2159a186cd0f~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5106888361045131);margin-left: 16px;">

<div style="text-align: center">After applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1615dd7d8a7e4a978c0ebbd9c16ed345~tplv-goo7wpa0wc-image.image)



</div>
</div>


