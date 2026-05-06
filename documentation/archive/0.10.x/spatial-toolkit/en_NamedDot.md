Value transfer node. Functions similarly to a global variable and is used to transfer data within the Node Graph.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d1293fe72be34741b1679d8be4d95c1a~tplv-goo7wpa0wc-image.image)
## Node usage instructions
After you add a Named Dot node, two nodes will appear.
| **Node** | **Description** |
| --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c5a56948e704610b8b18a75ff188993~tplv-goo7wpa0wc-image.image) <br>  | Used for both Input and Output. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9fce3292d0854596944d9f1dd6ce13c8~tplv-goo7wpa0wc-image.image) <br>  | Used for Output only. Can output the Input of the node above. |
The node graph below shows how to transfer values using the **Named Dot** node. In the upper left **Group**, a Float with a value of `1.234` is assigned to the **Named Dot** node. Below the **Group**, the output of the **Named Dot** node is connected to the **Debug Value** node, and then rendered to the surface through the **Preview Surface** node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/672f00a123c248218a171dd55d8f6b83~tplv-goo7wpa0wc-image.image)
The figure below shows the material after the Shader Graph is applied to the cube. As shown, the value `1.234` is successfully transferred.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/95c8934c814e42e0a66085d8862e9503~tplv-goo7wpa0wc-image.image" width="625px" /></div>


