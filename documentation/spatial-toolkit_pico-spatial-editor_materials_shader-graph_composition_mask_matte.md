A merging operation that overlays a premultiplied Alpha foreground layer onto the background.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6abe8905536450ea060ae96f3f3623c~tplv-goo7wpa0wc-image.image)
The **Matte** node uses the Alpha channels of the foreground and background input to determine the output.

* The output RGB component is: `Ff + B(1 − f)`
* The output Alpha component is: `f + b(1 − f)`

In other words, the foreground is overlaid onto the background according to its Alpha value, and the background is retained only in areas not covered by the foreground.
## **Parameter description**

* **Foreground**: A Color 4 type foreground input, where `F` represents the RGB component of this parameter, and `f` represents the Alpha component of this parameter.
* **Background**: A Color 4 type background input, where `B` represents the RGB component of this parameter, and `b` represents the Alpha component of this parameter.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value falls outside the range of `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

### **Node usage instructions**
The following example node graph demonstrates how to use the **Matte** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e2ea9ad3dc114d47ba0b4bd2bffbab5c~tplv-goo7wpa0wc-image.image)
Below are two original images, the foreground Alpha image, and the result of applying the final blended texture to the surface of a cube (**Mix** = 1).

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
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/072b092e06e3411da8246eaedaa8774b~tplv-goo7wpa0wc-image.image" width="472px" /></div>

