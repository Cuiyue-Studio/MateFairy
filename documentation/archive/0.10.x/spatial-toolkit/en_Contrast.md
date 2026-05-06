Adjust the contrast of the Input value (float or color) using a linear slope multiplier.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5205f5e05c784dea8ff17472c90eefc0~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The original Input value to be modified.
* **Amount**: A linear slope multiplier used to increase or decrease contrast.
   * When the value is between `0.0` and `1.0`, the contrast of the Input `In` is reduced.
   * When the value is greater than `1.0`, the contrast is increased.
* **Pivot**: The center point for contrast adjustment.
   * When contrast is increased, the Input values move away from this center value.
   * When contrast is reduced, the Input values move closer to this center value.

## Node usage instructions
The following is a Shader Graph example that uses the **Contrast** node (**Type** set to **Color3 FA**) to reduce the contrast of a texture, making its colors closer to gray.
The value of `Pivot` is `0.5`, representing gray. Because the value of `Amount` is `0.6`, the Input contrast is reduced, and the color values move toward the `Pivot` input. As a result, the node's output texture becomes a gray version of the original texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b6163b2ebf4a4d72a4511ee0beecc8eb~tplv-goo7wpa0wc-image.image)
The contrast effect before and after applying Shader Graph is as follows:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center">Before applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/769c71663eb74b92a8bd2159a186cd0f~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center">After applying Shader Graph</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/24e651142b6f416dbf25ddfee271cc52~tplv-goo7wpa0wc-image.image)



</div>
</div>


