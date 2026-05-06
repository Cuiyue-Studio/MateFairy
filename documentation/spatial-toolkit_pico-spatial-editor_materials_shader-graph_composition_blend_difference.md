Outputs the distance between the foreground and background values. The calculation formula is: `abs(B −F)`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/84cd1d9a92854d758add18aa6f3d7add~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground input, denoted as `F` in the formula.
* **Background**: Background input, denoted as `B` in the formula.
* **Mix**: The weight of the blending effect. The higher the `Mix` value, the stronger the blending operation and the more pronounced the visual effect. The default value is `1`. If the value falls outside the range of `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

### **Node usage instructions**
The **Difference** node performs a subtraction operation on two inputs and takes the absolute value of the result.
The **Difference** node uses the **Mix** input to control the weight of the foreground in the blend. The closer the **Mix** value is to `1`, the stronger the difference effect in the output; the closer it is to `0`, the weaker the effect.
The following example node graph demonstrates how to use the **Difference** node to blend two images into a material. Here, graffiti is used as the **Foreground**, and the brick wall is used as the **Background**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e1f9d2169ab649a9b8b837859137d5e8~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)


</div>
</div>

When **Mix** = 1, the blended material effect is as follows.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5bbeeff38494451d8e30f0da62b7b65a~tplv-goo7wpa0wc-image.image)

