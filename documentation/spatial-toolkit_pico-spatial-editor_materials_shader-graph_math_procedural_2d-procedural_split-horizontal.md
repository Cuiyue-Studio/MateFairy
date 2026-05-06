Split the mask from left to right at the specified U value.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/26992209b0f84fcc9f58aebfc991fe13~tplv-goo7wpa0wc-image.image)
## **Parameter description**

* **Left**: The value of the left region after splitting.
* **Right**: The value of the right region after splitting.
* **Center**: The coordinate value that controls the output split position. This parameter ranges from **0** to **1**.
* **texcoord**: Used to read data for mapping the texture to the surface's two-dimensional coordinates. The current **UV** coordinates are used by default, where **U** is the horizontal axis and **V** is the vertical axis.

## **Node usage instructions**
This node creates two independent regions along the horizontal direction, and the division of the regions is determined by the **Center** input value. When **Center** is **0**, the split position is at the far left, so the output always equals the **Right** input value; when **Center** is **1**, the split position is at the far right.
The following example shows a node graph that uses **Split Horizontal** to create a color separation effect.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f5c7a3c44e4048d3a514c017cb222345~tplv-goo7wpa0wc-image.image)

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5117370892018779);">

<div style="text-align: center"><strong>Original material</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/548783414f47419bbe2f2c2f74516753~tplv-goo7wpa0wc-image.image" width="1254px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.48826291079812206);margin-left: 16px;">

<div style="text-align: center"><strong>Material after applying Shader Graph</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8af4227285d546e99aef66f3af998ee1~tplv-goo7wpa0wc-image.image)



</div>
</div>


