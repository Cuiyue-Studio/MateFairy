Split the mask from top to bottom at the specified V value.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4d8491a5bfe749649070315a003be011~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Top**: The value of the top region after splitting.
* **Bottom**: The value of the bottom region after splitting.
* **Center**: The **V** value at which the output is split. The part above this value equals the **Top** Input, and the part below this value equals the **Bottom** Input. This parameter ranges from **0** to **1**.
* **texcoord**: Two-dimensional coordinates used to map the texture onto the surface. By default, the current **UV** coordinates are used, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
This node creates two independent regions along the vertical axis, with the split position determined by the **Center** Input value. When **Center** is **0**, the split position is at the very top, so the output always equals the **Top** Input; when **Center** is **1**, the split position is at the very bottom.
The following example shows a node graph that uses **Split Vertical** to create a color separation effect. By adjusting the value of **Center**, you can adjust the proportion of the top or bottom region in the texture. The figure below shows the final generated texture.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19a6ad60607942f9b2285426b1d6e18b~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5129107981220657);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48708920187793425);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4328f6a9a936420a9f3bfe8665bf4896~tplv-goo7wpa0wc-image.image)


</div>
</div>




