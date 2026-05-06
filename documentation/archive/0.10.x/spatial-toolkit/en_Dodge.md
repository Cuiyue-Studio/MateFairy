Dodge is a blending operation that brightens the background layer based on the foreground. The calculation formula is `B/(1 - F)`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/95cdf06ee5154bddbef5e29f9645f31a~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground input, denoted as `F` in the formula.
* **Background**: Background input, denoted as `B` in the formula.
* **Mix**: The weight of the blending operation. The higher the value of **Mix**, the stronger the blending effect and the more pronounced the visual result. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## Node usage instructions
The **Dodge** node brightens each area of the background based on the brightness of the corresponding area in the foreground. The calculation formula is `B/(1-F)`.
The following example node graph demonstrates how to use the **Dodge** node to brighten a brick texture.
You can use the **Noise 2D** node to generate Perlin noise and output this texture as the foreground input for the **Dodge** node. This way, the background brick texture will be brightened according to the procedural pattern.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a158b3b86e3845db8cb2cc562e860258~tplv-goo7wpa0wc-image.image)


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center">Brightening effect with <strong>Mix</strong> = 0</div>


![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center">Brightening effect with <strong>Mix</strong> = 0.5</div>

<div style="text-align: center"></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aeee801117ab4195b44495c1beb99fa8~tplv-goo7wpa0wc-image.image)



</div>
</div>


