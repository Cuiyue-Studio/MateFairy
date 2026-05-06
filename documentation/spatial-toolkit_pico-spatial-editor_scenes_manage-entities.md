This article describes how to manage entities in the view window, covering operations such as adding, selecting, moving, rotating, and scaling.
## Add entities
You can drag resources from the **Project Browser** window into the view window. The view window will display the resource. By default, resources are added as child nodes of the Root node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/82e1b489257a488eb134e0a2a49ac4bf~tplv-goo7wpa0wc-image.image)
## Select entities
To select entities, the toolbar on the left side of the view window must be set to **Move the selected object**, **Rotate the selected object**, or **Scale the selected object.**
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4f69f0fc9cca46b4954969c0eba7a14b~tplv-goo7wpa0wc-image.image)

You can select one or more entities using the following methods:

* **Select a single entity**: Click the target entity in the view window.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/36303c5b785e4fb0b6572daef3b9feb4~tplv-goo7wpa0wc-image.image)
* **Incrementally select multiple entities**: Hold down the Ctrl key (Windows) or Command key (macOS), then click each target entity in turn. Clicking a selected entity again will deselect it.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b2f0945bf1fd494ba03d1486481f574e~tplv-goo7wpa0wc-image.image)
* **Select multiple entities by box selection**: Drag the mouse in the view window to box-select all target entities.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e74542a67e344f0296a40edd9ade05b7~tplv-goo7wpa0wc-image.image)

## Focus on selected entities
After selecting entities, press the F key.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/32cab1b84a634f11a705f29ded7913d7~tplv-goo7wpa0wc-image.image)
## Delete entities
After selecting entities, you can press the Delete key (Windows) or Backspace key (macOS) to delete the entities.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/23eda79085c048c9885d2bc437ec7e4a~tplv-goo7wpa0wc-image.image)
## Move entities
To move entities, the toolbar on the left side of the view window must be set to **Move the selected object.**

After selecting entities, you can move them along the axis using the mouse.
* The colors of the axes represent:
   * Red: X axis.
   * Green: Y axis.
   * Blue: Z axis.
* Drag the colored square icon to move entities on a single plane. The corresponding planes for each color are as follows:
   * Blue: XY plane
   * Red: YZ plane
   * Green: XZ plane
* Hold down the Shift key and move the entity to move it in increments of 1 centimeter.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2cd0071cbdf44002860a8be8d1f0bbbe~tplv-goo7wpa0wc-image.image)
## Rotate entities
To rotate the entity, the toolbar on the left side of the view window must be set to **Rotate the selected object**.

After selecting entities, you can drag the ring to change the entities' angles.
You can rotate the entity by dragging different control rings:

* Drag the **red**, **green**, and **blue** rings to rotate around the X, Y, and Z axes, respectively.
* Drag the **gray sphere in the center** to freely rotate at any angle.
* Drag the **outer gray ring** to rotate around the axis perpendicular to your current viewpoint.
* Hold down the `Shift` key while rotating to rotate the entity in increments of 5°.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cabc1d27d75c42429b42183acd1eeb73~tplv-goo7wpa0wc-image.image)
## Scale entities
To scale the entity, the toolbar on the left side of the view window must be set to **Scale the selected object**.

After selecting entities, you can drag the axis to change the entities' sizes.
You can scale the entity in the following ways:

* Single-axis scaling: Drag the control axes of different colors to scale along a single axis.
   * Red: Scale along the X axis.
   * Green: Scale along the Y axis.
   * Blue: Scale along the Z axis.
* Uniform scaling: Drag the gray square in the center to scale the entire entity proportionally.

Additionally, if you lock the **Scale** property in the entity's Transform component, dragging any colored control axis will result in uniform scaling.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cc15a03725dc4bb9a95430eb45aabea4~tplv-goo7wpa0wc-image.image)
## Preview the display effect of entities in a spatial container
You can add a spatial container in the view window to preview how entities are displayed within it.
The display effect provided by Spatial Editor is for preview only. In your spatial app, you need to use the PICO Spatial SDK to add entities to the spatial container. For details, see [Learn about spatial containers & space state](/spatial-sdk/learn-about-spatial-containers-and-space-states/).


1. In the toolbar at the top of the view window, click the drop-down arrow to the right of **Spatial Container**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3a49106d2ea54cc5875df84b0097e220~tplv-goo7wpa0wc-image.image)
2. Select a type of spatial container. You can configure the parameters of the spatial container as needed.
   | **Spatial container** | **Description** | **Parameters** |
   | --- | --- | --- |
   | Planar | The Planar window is a WindowContainer shaped like a "flat panel" with a fixed thickness. Features are as follows: <br>  <br> * You can only modify its length (X axis) and width (Y axis); its height (Z axis) is fixed. <br> * Its center point is aligned with the world coordinate origin (0, 0, 0) of the scene. <br> * During rendering, any part of the model that exceeds the container boundary will be clipped. | * **Unit**: Length unit. <br>    * Meter: meters. <br>    * dp: device pixels. <br> * **Size**: Size. The default is 1280*720*640 dp. The minimum is 320*180*640 dp, and the maximum is 2700*1800*640 dp. <br> * **UI Sample**: Whether to show sample UI components. |
   | Volumetric | The Volumetric window is a resizable "cuboid" WindowContainer with the following features: <br>  <br> * You can modify its size along the X, Y, and Z axes. <br> * Its center point is aligned with the world coordinate origin (0, 0, 0) of the scene. <br> * During rendering, any part of the model that extends beyond the container boundaries will be clipped. | * **Unit**: Length unit. <br>    * Meter: meter. <br>    * dp: Device pixels (Device Pixels). <br> * **Size**: Size. The default is 960*960*960 dp. The minimum is 320*320*320 dp, and the maximum is 2700*2700*2700 dp. <br> * **UI Sample**: Whether to display sample UI components. |
   | Stage | An unbounded virtual stage with the following features: <br>  <br> * No restriction on the size of the model. <br> * Its origin (0, 0, 0) is by default located at the player's feet and is consistent with the origin of the viewport. | None |


