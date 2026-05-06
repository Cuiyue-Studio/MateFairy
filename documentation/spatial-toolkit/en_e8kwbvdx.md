This document introduces the Shader Graph user interface.
The **Shader Graph** tab is located at the bottom of the PICO Spatial Editor and includes the **Input Node** panel, workspace, and **Shader Graph Inspector** window.
## Input Node panel
On the far left of the **Shader Graph** tab, you can add Input nodes.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b1bf0788bb1146499d7ecda19c7ebfbb~tplv-goo7wpa0wc-image.image)
Shader Graph supports the following types of Input nodes.
| **Input types** | **Port color** | **Connection color** |
| --- | --- | --- |
| Integer | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bd09621679fd4621bda9b60fc613cb00~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bae7c44c733c4e95be315193a849b68d~tplv-goo7wpa0wc-image.image) |
| Float | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/029be866369542b4a1494b8b55397311~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3879c81c66b4734a4b443074684d9fb~tplv-goo7wpa0wc-image.image) |
| Boolean | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/55ac1c1a092e4c3e869e269586ffbbbd~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7a2233c8852043c9a2d655d18632d782~tplv-goo7wpa0wc-image.image) |
| Vector2 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9dd5005fbb2b4a08a535055c4985cf98~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ea4e0ac25c75438088eeae2bd7790dd8~tplv-goo7wpa0wc-image.image) |
| Vector3 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c2b70181baea45a4941604361552c417~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d5df2beca95c4f1c9c636262916da178~tplv-goo7wpa0wc-image.image) |
| Vector4 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50fd6db941b04daf816ccb94ba8dd66e~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/11ceb0ac265240cab050ad5a65a0768a~tplv-goo7wpa0wc-image.image) |
| Color3 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/db8553b9bb1c4b8da74decdc51432b74~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b688bafd31b241b2afe26ade9964ce08~tplv-goo7wpa0wc-image.image) |
| Color4 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/80cf55a6298b4bcca3bf6b86e707e191~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fff28c9384df43e7b05258ff53e5744a~tplv-goo7wpa0wc-image.image) |
| Filename | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d0ed1cc74fb4dbfb6920e5fc1b0e9fe~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/63c5800376064650a51aefc8a7eb059e~tplv-goo7wpa0wc-image.image) |
| Matrix3 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/86117b2f7ca547ce93dc2223ff2ba6ca~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aa044c9264144ef78c8a5f79f5c78467~tplv-goo7wpa0wc-image.image) |
| Matrix4 | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1e32de0645f245edaac307fa86d49d32~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3107ca2bd33040c693b0d295d5fb3204~tplv-goo7wpa0wc-image.image) |
## Workspace
In the workspace at the center of the **Shader Graph** tab, you can add processing nodes to Shader Graph, and connect Input nodes, processing nodes, and output nodes.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/35fdcc2df8504dba9d377753ab3ddf14~tplv-goo7wpa0wc-image.image)
### Open the node creation page
You can open the node creation page using any of the following methods.

* Click the upper right **➕New Node**.
* Double-click the left mouse button in a blank area.
* Click the input or output port.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d2bc311e4eb9420bad588d01b8568e28~tplv-goo7wpa0wc-image.image)
### Right-click shortcut menu
In the workspace, you can use the right-click shortcut menu to create sticky notes, zoom to fit, auto layout nodes, compose or decompose node graph nodes, convert between constant nodes and Input nodes, and perform copy, paste, delete, and other operations on nodes.
| **Options** | **Note** |
| --- | --- |
| Create Sticky Note | Create a sticky note. |
| Zoom Fit | Zoom to fit, shortcut key is F. |
| Auto Layout | Auto layout nodes. |
| Compose Node Graph | Compose the selected nodes into a Node Graph node. |
| Decompose Node Graph | Decompose the selected Node Graph node into multiple nodes (this option is available only for Node Graph nodes). |
| Create Node Graph Instance | Create an instance of a Node Graph node (this option is available only for Node Graph nodes). |
| Convert to Input | Convert a constant node to an Input node (this option is available only for constant nodes). |
| Convert to Constant | Convert an Input node to a constant node (this option is available only for Input nodes). |
| Cut | Cut, shortcut key Command/Ctrl+X. |
| Copy | Copy, shortcut key Command/Ctrl+C. |
| Paste | Paste, shortcut key Command/Ctrl+V. |
| Duplicate | Duplicate, shortcut key Command/Ctrl+D. |
| Delete | Delete, shortcut key Backspace/Delete. |
## Shader Graph Inspector window
On the far right of the **Shader Graph** tab, you can use the **Shader Graph Inspector** to set the properties of the selected node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/81d010c1ef7448c0a89f038cd4ca547f~tplv-goo7wpa0wc-image.image)
When you create a new Shader Graph, the system automatically creates a PreviewSurface node and an output node (Outputs node).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8c9f47e544ae4213956bb7e7031028e5~tplv-goo7wpa0wc-image.image)

