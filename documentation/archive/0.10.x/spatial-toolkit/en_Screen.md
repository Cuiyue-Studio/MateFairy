A blending operation that brightens areas darker than white.
This node first inverts the color values of the foreground and background separately, multiplies these results, and then inverts the final result again. The calculation formula is: `1 − (1 − F)(1 − B)`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bd956d1679c744799494536f083edd92~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Foreground**: Foreground Input, denoted as `F` in the formula.
* **Background**: Background Input, denoted as `B` in the formula.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the expected functionality of this node may occur.

### **Node usage instructions**
The visual effect produced by this blending method is always as bright as the original texture or brighter than the original texture.
The example node graph below demonstrates how to use the **Screen** node to blend two images into a material.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ca68323687274a7cb621485774964403~tplv-goo7wpa0wc-image.image)
The following shows the two original images and the effect of applying the blended texture to the surface of a cube (**Mix** = 0.5).

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
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/64522931ecca452dbe15e5bf29fb30ae~tplv-goo7wpa0wc-image.image" width="393px" /></div>



