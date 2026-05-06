This article describes how to manage assets in the **Project Browser** window of PICO Spatial Editor.
You can use the PICO Spatial SDK to load assets as resources from a Spatial Editor project. For details, see [AssetBundle](/document/spatial-sdk/asset-bundle/).
## Import assets from local storage
Click the plus sign, select the asset file to import in the pop-up file selection dialog, and it will be imported into the current Spatial Editor project. After importing, you need to add the asset to the scene. For details, see [Manage nodes](/en_node-management).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50e56d1e670b4a5cb58848206d0567cc~tplv-goo7wpa0wc-image.image)
## Import assets from the Assets Library
The Assets Library includes some commonly used assets, such as models, materials, audio, and effects. All assets must be downloaded from the internet before they can be used. To add assets from the asset library to the scene, drag the asset directly into the scene view window. You can also drag the asset into the corresponding folder in the **Project Browser**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/96f3fc6a2dfb4acb8604796f9c75db65~tplv-goo7wpa0wc-image.image)
## Delete assets
The delete assets operation does not support undo. Proceed with caution.

Right-click the asset and select **Delete**. In the pop-up confirmation window, select **Delete**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0c11e6d402c84dc280f29f1194c6e518~tplv-goo7wpa0wc-image.image)
## Search assets
Enter a keyword in the input box to search all files and folders containing the keyword in the current project.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a88aaa617ebe4b759e01c1ebdf645e8a~tplv-goo7wpa0wc-image.image)
## View asset information
After selecting an asset, you can view its information in the panel on the right.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/31df9057849542c1a5834b9dc8394279~tplv-goo7wpa0wc-image.image)
## Preview .usdz files in the file explorer
In the file explorer, right-click a .usdz file and select Spatial Editor as the open method to preview the file.
In the preview window, you can use the mouse to perform the following camera operations:

* **Move the camera forward and backward**: Scroll the mouse wheel
* **Pan the camera up, down, left, and right**: Hold and drag the middle mouse button

In the upper right corner of the preview window, you can play animations or click the camera icon to reset the camera coordinates to (0, 1.6, 0).
When the original bounding box size of an object exceeds **5m × 5m × 5m** , the system will enforce calculation and processing using **5m × 5m × 5m** . This limit box is built outward from the center of the object's own bounding box, rather than being based on the world coordinate origin (0, 0, 0), to ensure that the interaction logic moves correctly with the object's position.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/455eff4827e44fd2928b0e8845fa4256~tplv-goo7wpa0wc-image.image)





 

