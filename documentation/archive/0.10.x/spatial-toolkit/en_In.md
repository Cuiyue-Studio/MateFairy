Outputs the regions of the foreground where the Alpha channel overlaps with the background Alpha.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2fdf8f5fecb84ff8b8068c33956c2985~tplv-goo7wpa0wc-image.image)
**In** node uses the Alpha channels from the foreground and background inputs to determine the output result. Visually, this means that only the parts of the foreground that overlap with the background Alpha are retained.

* The output RGB component is: `F⋅b`
* The output Alpha component is: `f⋅b`

## **Parameter description**

* **Foreground**: Foreground Input of type Color 4, where `F` represents the RGB component and `f` represents the Alpha component.
* **Background**: Background Input of type Color 4, where `B` represents the RGB component and `b` represents the Alpha component.
* **Mix**: The weight for the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## **Node usage instructions**
The following example node graph demonstrates how to use the **In** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a706231ef124f20984d79bec547ad3f~tplv-goo7wpa0wc-image.image)
Below are two original images, the background Alpha image, and the result of applying the final blended texture to the surface of a cube (**Mix** = 0.5).

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3277777777777778);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3277777777777778);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3444444444444444);margin-left: 16px;">

<div style="text-align: center"><strong>Background Alpha</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e790312c9db493ba5f048d9da9dfe0c~tplv-goo7wpa0wc-image.image" width="251px" /></div>




</div>
</div>

The blended material is as follows:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19da8ffa71724bb795c9a4b5e764e180~tplv-goo7wpa0wc-image.image" width="540px" /></div>

