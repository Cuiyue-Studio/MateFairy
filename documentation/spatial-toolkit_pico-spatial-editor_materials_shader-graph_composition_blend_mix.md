Blend the foreground input and background input based on the weight of the **Mix** value. The calculation formula is: `F*m + B(1 − m)`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b7c747f6bf3840259ded512b4d26a5cb~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground input, denoted as `F` in the formula.
* **Background**: Background input, denoted as `B` in the formula.
* **Mix**: The weight value that determines whether the output is closer to the foreground or background. The default value is `0`. If the value falls outside the range of `0` to `1`, undefined effects beyond the intended functionality of this node may occur. In mathematical formulas, it is denoted as `m`.

## **Node usage instructions**
The **Mix** node is used to blend two input values together.
When **Mix** = 1, the output is identical to **Foreground**; when **Mix** = 0, the output is identical to **Background**. The closer the `Mix` value is to `0` or `1`, the closer the output is to the corresponding input.
The **Mix** node can be used to blend between two different textures to create transition effects, interpolate between two colors, or mix shader parameters.
The example node graph below demonstrates how to use the **Mix** node to blend two images into a material.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d0b519b562d24bdaa0d702a79f51d4d4~tplv-goo7wpa0wc-image.image)
The two original images are shown below.

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

When the **Mix** value is `0.1`, `0.5`, and `0.9`, the effect of applying the blended texture to the surface of the cube is as follows:
<div style="text-align: center"></div>


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

<div style="text-align: center"><strong>Mix</strong> = 0.1</div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/afd759d45ed44cffba463981263b1114~tplv-goo7wpa0wc-image.image" width="266px" /></div>




</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

<div style="text-align: center"><strong>Mix</strong> = 0.5</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0fee2c1ede224763a37199e7f21aa4d0~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

<div style="text-align: center"><strong>Mix</strong> = 0.9</div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/63932204294546b0a96157bda1c6b4ba~tplv-goo7wpa0wc-image.image)



</div>
</div>


