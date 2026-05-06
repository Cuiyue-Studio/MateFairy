A merging operation that overlays the foreground onto the background color, assuming that there is no overlap between the two in the shared semi-transparent region.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8351c33d8c634c6997dfd0c157112ed2~tplv-goo7wpa0wc-image.image)
The **Disjoint Over** node performs one of two different blending methods based on the Alpha channel of the foreground and background input.

* When f + b ≤ 1, the output RGB component is: `F + B`
* When f + b > 1, the output RGB component is: `(F + b(1 − f)) / b`

The output Alpha component is always the smaller value between f + b and 1: `min⁡(f + b, 1)`.
## **Parameter details**

* **Foreground**: An input of type Color4, where `F` and `f` represent the RGB and Alpha components, respectively.
* **Background**: An input of type Color4, where `B` and `b` represent the RGB and Alpha components, respectively.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## **Node usage guide**
The following is a simple node graph example demonstrating how to use the **Disjoint Over** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3173e0bd0a9d4c888dbdb5fb9257dd8e~tplv-goo7wpa0wc-image.image)
The diagram below shows two original images and the blended material.

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



