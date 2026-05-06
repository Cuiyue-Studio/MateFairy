A compositing operation that overlays the foreground onto the background color, assuming that the two do not overlap in the shared semi-transparent region.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8351c33d8c634c6997dfd0c157112ed2~tplv-goo7wpa0wc-image.image)
The **Disjoint Over** node performs one of two different blending methods based on the alpha channels of the foreground and background inputs.

* When f + b ≤ 1, the output RGB component is: `F + B`
* When f + b > 1, the output RGB component is: `(F + b(1 − f)) / b`

The output alpha component is always the smaller value between f + b and 1: `min⁡(f + b, 1)`.
## **Parameter description**

* **Foreground**: A Color 4 type foreground input, where `F` represents the RGB component of this parameter, and `f` represents the alpha component of this parameter.
* **Background**: A Color 4 type background input, where `B` represents the RGB component of this parameter, and `b` represents the alpha component of this parameter.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. Values outside the range from `0` to `1` may result in undefined effects beyond the intended functionality of this node.

## **Node usage instructions**
The following is a simple node graph example demonstrating how to use the **Disjoint Over** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3173e0bd0a9d4c888dbdb5fb9257dd8e~tplv-goo7wpa0wc-image.image)
The diagram below shows the two original images and the blended material.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)


</div>
</div>

The blended material is as follows:
<div style="text-align: center"></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5f9cadad265a4435bf97d408f5a80954~tplv-goo7wpa0wc-image.image" width="564px" /></div>



