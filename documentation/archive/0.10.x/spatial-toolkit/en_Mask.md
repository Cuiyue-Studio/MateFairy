Outputs the area in the background that overlaps with the foreground alpha.
The **Mask** node uses the alpha channels of the foreground and background input to determine the output.

* The output RGB component is: `B⋅f`
* The output alpha component is: `b⋅f`

That is, the background is masked according to the foreground alpha, and only the parts overlapping with the foreground alpha are output.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d166afddf1b4be98ccd569616e6c989~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground input of type Color 4, where `F` represents the RGB component of this parameter and `f` represents the alpha component of this parameter.
* **Background**: Background input of type Color 4, where `B` represents the RGB component of this parameter and `b` represents the alpha component of this parameter.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value is outside the range of `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

### **Node usage instructions**
The following example node graph shows how to use the **Mask** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1800b128610f4ab9bc233bbeb0e50ac2~tplv-goo7wpa0wc-image.image)
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
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6cd3da0bd9bc4644bb37a1518bf59fac~tplv-goo7wpa0wc-image.image" width="449px" /></div>

