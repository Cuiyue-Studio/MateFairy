Subtract the foreground value from the background value.
The **Subtractive Mix** node performs a subtraction operation on two Inputs and uses the **Mix** Input to determine the weight of the foreground in the blend. The formula is: `B − F`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/884fdd7d2d6a461f8f2eea83dabb942c~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground Input, denoted as `F` in the formula.
* **Background**: Background Input, denoted as `B` in the formula.
* **Mix**: The weight of the blending effect. The higher the **Mix** value, the stronger the blending operation and the more pronounced the visual effect. The default value is `1`. If the value is outside the range of `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## **Node usage instructions**
The following is a simple node graph example that demonstrates how to use the **Subtractive Mix** node to blend two images into a single material.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72fa8e4c8458442e90b89cb172f46e34~tplv-goo7wpa0wc-image.image)
The figure below shows the two original images and the resulting material after blending (**Mix** = 1).

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
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/323f12919f8a44ef8345590f472bff4b~tplv-goo7wpa0wc-image.image" width="590px" /></div>


<div style="text-align: center"></div>

