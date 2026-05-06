A blending operation that applies multiply blending to the dark areas and screen blending to the light areas.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f237903aaa3042639fd40cd8a978244b~tplv-goo7wpa0wc-image.image)
The **Overlay** node calculates based on the value of the foreground `F` using one of the following two methods:

* When F is less than 0.5: `2⋅F⋅B`
* When F is greater than or equal to 0.5: `1−(1−F)(1−B)`. This produces the same visual effect as the **Screen** node.

## **Parameter description**

* **Foreground**: foreground input, denoted as `F` in the formula.
* **Background**: background input, denoted as `B` in the formula.
* **Mix**: The weight of the blending operation. The higher the **Mix** value, the more pronounced the blending effect. The default value is `1`. If the value exceeds the range from `0` to `1`, undefined effects beyond the intended functionality of this node may occur.

## **Node usage instructions**
Visually, the **Overlay** node makes the dark areas of the blended texture darker and the light areas lighter, thereby enhancing overall contrast.
The example node graph below demonstrates how to use the **Overlay** node to blend two materials.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/308f86ecc1ad43b693993bfae37b8da3~tplv-goo7wpa0wc-image.image)
Below are two original images, as well as the effect of applying the blended texture to the surface of a cube (**Mix** = 1).

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
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/74b8ce993ad24948aa4eaa959633900b~tplv-goo7wpa0wc-image.image" width="420px" /></div>

