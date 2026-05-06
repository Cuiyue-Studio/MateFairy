An operation that adds the foreground value to the background value. The calculation formula is: `B + F`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f271aa77bb7b461e8e658dbdad08f55f~tplv-goo7wpa0wc-image.image)
## **Parameter description**

   * **Foreground**: Foreground input, denoted as `F` in the formula.
   * **Background**: Background input, denoted as `B` in the formula.
   * **Mix**: The weight of the blending effect. The higher the **Mix** value, the greater the intensity of the blending operation and the more pronounced the visual effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

### **Node usage instructions**
This node uses the `Mix` input to control the weight of the foreground in the blend. The higher the `Mix` value and the closer it is to `1`, the stronger the blending effect; the lower the value and the closer it is to `0`, the weaker the blending effect.
The following example node graph demonstrates how to use the **Additive Mix** node to blend two images into a material.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/83a26913b8c9468096cc288f7e74afef~tplv-goo7wpa0wc-image.image)
Below are the two original images, as well as the effect of applying the blended texture to the surface of a cube (**Mix** = 0.5).

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>Foreground</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Background</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/42e4e77dfc574e47a630dae934e94fad~tplv-goo7wpa0wc-image.image)



</div>
</div>

The blended material is as follows:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7f5cb45fc59b4debb7bee5c0d9acbbd8~tplv-goo7wpa0wc-image.image" width="426px" /></div>

