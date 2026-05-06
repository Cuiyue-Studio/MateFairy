Outputs the regions of the foreground that do not overlap with the background.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/99e646d398524586a1f0ad318dec33b6~tplv-goo7wpa0wc-image.image)
The **Out** node uses the alpha channels of the foreground and background input to determine the output result. Visually, this means that only the parts of the foreground that do not overlap with the background alpha are retained.

* The output RGB component is: F⋅(1 − b)
* The output alpha component is: f⋅(1 − b)

## **Parameter descriptions**

* **Foreground**: a Color4-type foreground input, where `F` represents the RGB component of this parameter, and `f` represents the alpha component of this parameter.
* **Background**: a Color4-type background input, where `B` represents the RGB component of this parameter, and `b` represents the alpha component of this parameter.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## **Node usage instructions**
The following example node graph demonstrates how to use the **Out** node to blend two textures.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b4652bdc02a421db53a4b6152abb4e9~tplv-goo7wpa0wc-image.image)
Below are two original images, the background alpha image, and the result of applying the blended texture to the surface of a cube (**Mix** = 0.5).

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3277777777777778);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3350417002959376);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.33718052192628456);margin-left: 16px;">

<div style="text-align: center"><strong>Background Alpha</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e790312c9db493ba5f048d9da9dfe0c~tplv-goo7wpa0wc-image.image" width="251px" /></div>




</div>
</div>

The blended material is as follows:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a227dc78f55f4eabaf336b6e0b0e2548~tplv-goo7wpa0wc-image.image" width="555px" /></div>


