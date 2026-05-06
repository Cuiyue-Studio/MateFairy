A compositing operation that overlays the foreground onto the background using the foreground's alpha channel. The output RGB component is: `F + B(1 − f)`, and the output alpha component is: `f + b(1 − f)`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0bd0187459504570972e6f5e3e015627~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: a Color 4 type foreground input. Here, `F` represents the RGB component of this parameter, and `f` represents the alpha component of this parameter.
* **Background**: a Color 4 type background input. Here, `B` represents the RGB component of this parameter, and `b` represents the alpha component of this parameter.
* **Mix**: The weight of the blending operation. The higher the value of `Mix`, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the expected functionality of this node may occur.

## **Node usage instructions**
The **Over** node determines the output result based on the alpha channels of the foreground and background input. The lower the alpha value of the foreground, the more the background is blended into the foreground.
The following example node graph demonstrates how to use the **Over** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6cc4f9050c544098f2a85360d560545~tplv-goo7wpa0wc-image.image)
Below are two original images, the foreground alpha image, and the result of applying the final blended texture to the surface of a cube (**Mix** = 0.5).

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33333333333333337);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3502824858757062);margin-left: 16px;">

<div style="text-align: center"><strong>Foreground Alpha</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e790312c9db493ba5f048d9da9dfe0c~tplv-goo7wpa0wc-image.image" width="251px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3163841807909605);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)



</div>
</div>

The blended material is as follows:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dc310ba48e9345919ffc3330d280a87d~tplv-goo7wpa0wc-image.image" width="449px" /></div>


