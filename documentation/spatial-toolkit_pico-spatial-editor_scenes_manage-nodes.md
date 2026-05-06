This article describes how to manage nodes in a scene.
## What is a node
In USD (Universal Scene Description), Prims are organized into a tree hierarchy through parent-child relationships. In the tree hierarchy, each Prim is a node.
In Spatial Editor, scenes are stored in the .usda format. Therefore, each Prim in the file is displayed as a node in the **Hierarchy** window, which fully presents the hierarchical structure of the scene. For a scene, each node is an entity (Entity).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/761fc4c3e71a417a85e9c66a53f425f7~tplv-goo7wpa0wc-image.image)
The following table shows the icons for different types of nodes and their corresponding Prim types.
| **Icons** | **Prim type** | **Description** |
| --- | --- | --- |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ba3f04d8e4054aa6959ba65e77dfd36d~tplv-goo7wpa0wc-image.image) | Xform | Empty node containing a Transform component. |
|  | SkelRoot | Root node of the skeleton. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/94b0de60c854491c8d08afa258b328fa~tplv-goo7wpa0wc-image.image) | Scope | Empty node without a Transform component. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d8c45b2db9c40e093f6c823d2691002~tplv-goo7wpa0wc-image.image) | Mesh | Mesh node. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/42b568dbea79463cae478bbdcef7933d~tplv-goo7wpa0wc-image.image) | Material | Material node. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/641f96ae963a48e8b9deb8bf8b43d496~tplv-goo7wpa0wc-image.image) | Skeleton | Skeleton node of the animation file. |
|  | Joint | Joint node of the animation file. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb7604b152a848fbaa0dd06c7afbcf49~tplv-goo7wpa0wc-image.image) | Animation | Frame sequence animation node. |
|  | SkelAnimation | Skeletal animation node. |
| ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f334bde280db44e082db874d4006894c~tplv-goo7wpa0wc-image.image) | Audio File | Audio source file node. |
## Create nodes
The scene has a default Root node. All nodes you create will exist in the scene hierarchy as child nodes or descendants of the Root node. The coordinates of newly created nodes are set to (0, 0, 0) by default.
You can create nodes in the **Hierarchy** window using the following methods.

* **Create a child node for a node**: Select the node to which you want to add a child node, click the plus sign in the upper right corner of the **Hierarchy** window, and select the node type from the dropdown menu.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c485fba25c89441bbd5e2c4f5da43fd8~tplv-goo7wpa0wc-image.image)
* **Create a child node for the Root node**: Without selecting any node, click the plus sign in the upper right corner of the **Hierarchy** window, and select the node type from the dropdown menu.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/60ae75ce46204f3bb282337b5a145799~tplv-goo7wpa0wc-image.image)
* **Drag resources into the Hierarchy window**: You can drag resources from the **Project Browser** window into the scene hierarchy as new nodes.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c799309044c143f894d9288f8fc98536~tplv-goo7wpa0wc-image.image)
* **Drag resources into the viewport window**: You can drag resources from the **Project Browser** window into the viewport window. By default, resources are added as child nodes of the Root node.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/05ab300fa30a462790482dc791252334~tplv-goo7wpa0wc-image.image)

The following table explains the node types available in the dropdown menu:
| **Options** | **Note** |
| --- | --- |
| Reference | Add a Reference to the current scene to access resources in the current project. |
| Create Empty | Add an empty node with a Transform component to the current scene, that is, a node of type Xform. |
| Scope | Add an empty node without a Transform component to the current scene, that is, a node of type Scope. |
| Primitive Shapes | Add basic geometric shapes to the current scene. <br>  <br> * **Capsule**: Capsule model. <br> * **Cone**: Cone model. <br> * **Cube**: Cube model. <br> * **Cylinder**: Cylinder model. <br> * **Sphere**: Sphere model. <br> * **Plane**: Plane model. |
| Materials | Add materials to the current scene. <br>  <br> * **PBR (Physically Based)**: Physically based material. <br> * **Unlit**: Unlit material. <br> * **Cloth**: Cloth material. <br> * **Shader Graph**: Custom material built using Shader Graph. |
| Particle Effects | Add particle effects to the current scene. |
| Lights | Add lights to the current scene. <br>  <br> * **Directional Light**: Directional light, supports shadows. <br> * **Point Light**: Point light, does not support shadows. <br> * **Spot Light**: Spot light, supports shadows. |
| Audio | Add audio to the current scene. <br>  <br> * **Object Audio**: Spatial Audio, has direction and position. <br> * **Ambient Audio**: Ambient audio, has direction but no position. <br> * **Channel Audio**: Standard audio, has neither direction nor position. <br> * **Audio File**: An audio source file. |
## Select nodes
In the **Hierarchy window**, click the node you want to select. After selection, you can view, delete, rename, and perform other operations.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b515264df0f8415aa9caa4ddd04c4f07~tplv-goo7wpa0wc-image.image)
To select multiple nodes, hold down the Ctrl key (Windows) or Command key (macOS) and:

* Click each node you want to select one by one.
* Click one node as the starting point, then click another node as the endpoint. All nodes from the starting point to the endpoint will be selected.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fbc1d3b8256340568bbe828ae9591c15~tplv-goo7wpa0wc-image.image)
## Locate nodes
You can double-click a node in the **Hierarchy** window to quickly locate the node in the viewport.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6e1bba67c5c7467fa999b5dcc3febe8f~tplv-goo7wpa0wc-image.image)
## View nodes
After selecting a node in the **Hierarchy window**, you can view detailed information about the current node in the **Inspector window** on the right.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/906fb1ce14a64e60863c0c4f980ae516~tplv-goo7wpa0wc-image.image)
## Delete nodes
After selecting a node in the **Hierarchy window**, right-click the node and click **Delete** in the pop-up dropdown menu.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/73009935cdcf4bcca42c6f58aa135f2d~tplv-goo7wpa0wc-image.image)
## Rename nodes
Right-click the node and select **Rename**. Alternatively, you can double-click the node name in the **Inspector** window to rename the selected node.
The first character of a node name can only be an English letter or an underscore.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/30be57a1777c49b29356ca615600bc3e~tplv-goo7wpa0wc-image.image)
## Move nodes
You can adjust the node's position in the hierarchy by dragging.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d4b678f47a1746a595be6223dce58cb1~tplv-goo7wpa0wc-image.image)
## Hide or show nodes
Each node has an eye button on the right to control its visibility status. Click this button to toggle the node's show/hide status in the scene. You can also select and right-click the node, then choose **Enable**  or **Disable** in the pop-up dropdown menu. After hiding, the entity will not be rendered and will not interact with the physics system.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/90a0fc911f644ef5bee0d0e4b2054e9d~tplv-goo7wpa0wc-image.image)
## Enable or disable nodes
Right-click the node and select **Activate** or **Deactivate**. Disabled nodes will be removed from the current scene, but you can re-enable them at any time to restore them to the scene.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c478aa1a24d44ccb568cdb60bf06c85~tplv-goo7wpa0wc-image.image)
Alternatively, you can click **...** in the **Inspector** window, then select **Activate** or **Deactivate** in the dropdown menu.
## Lock or unlock nodes
Each node has a lock button on the right to control its lock status. Click this button to toggle the node's editable/non-editable status. Alternatively, you can select and right-click the node, then choose **Lock**  or **Unlock** in the pop-up dropdown menu.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a0a15f56dee34404a93a313c534f3364~tplv-goo7wpa0wc-image.image)
## Node grouping
Select one or more nodes, then right-click and choose **Group**. The Spatial Editor will automatically create a new parent node named Group of type Xform and move all selected nodes under it as child nodes.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/69006ecaa5144b53936bca34e4484070~tplv-goo7wpa0wc-image.image)
 

