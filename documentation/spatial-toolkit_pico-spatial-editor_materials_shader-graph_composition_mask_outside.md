Multiply the complement of the mask by all channels of the Input.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/353548a33f5e41b69b6ab8c6717c085d~tplv-goo7wpa0wc-image.image)
**Outside** node multiplies `1 − mask` by all channels of the Input. Visually, this is equivalent to retaining the areas outside the mask.
## **Parameter description**

* **In**: Input value to which the mask is applied.
* **Mask**: Value multiplied with the Input.

## Node usage instructions
The following node graph shows how to apply the mask to a specified material.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19d670d7da224ae7b2c11cd1bd10b1aa~tplv-goo7wpa0wc-image.image)
The diagram below shows the effect of applying the mask's complement to the brick wall material.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><strong>In</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c3e2f17110504d4491426bf3e2a4cc55~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><strong>Mask</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/390fc9411ab548a58dc3614e9e833506~tplv-goo7wpa0wc-image.image)


</div>
</div>

The blended material is as follows:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/02fc5d1daf554ce58e5e10d98f1c922f~tplv-goo7wpa0wc-image.image" width="497px" /></div>


