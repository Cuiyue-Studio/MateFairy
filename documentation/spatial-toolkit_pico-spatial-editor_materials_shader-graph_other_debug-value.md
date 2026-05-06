Render the input value onto the surface for display.

* **Float** or **Vector**: Renders the value as text.
* **Color**: Renders directly as the corresponding color.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/835ede1e5b1644cb83d56f88ac183212~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Value**: The value to display.
* **Tiles**: Controls how many times the debug output is tiled in the preview area. This parameter is a two-dimensional value, representing the number of tiles horizontally and vertically. The default value is (1, 1).

## Node usage description
The following node graph shows how to display the input value using the **Debug Value** node. In the **Group** at the top left, a Float with a value of `1.234` is assigned to the **Named Dot** node. Below the **Group**, the output of the **Named Dot** node is connected to the **Debug Value** node, and then rendered onto the surface through the **Preview Surface** node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/672f00a123c248218a171dd55d8f6b83~tplv-goo7wpa0wc-image.image)
The figure below shows the material after the Shader Graph is applied to the cube. As shown, the value `1.234` is rendered onto the surface of the cube.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/95c8934c814e42e0a66085d8862e9503~tplv-goo7wpa0wc-image.image" width="625px" /></div>

