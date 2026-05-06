This article describes how to manage scenes in Spatial Editor.
## What is a scene
A scene defines a 3D space and all the objects within it. For example, if you have a sports car 3D model, you can build a scene that includes the sports car 3D model, dynamic LED car light effects (such as turn signal blinking and brake light fading), a dashboard panel displaying real-time speed and fuel data, interactive driving animations (such as steering wheel rotation and throttle response), and supporting audio (engine roar, brake sounds, and turn signal prompts). With scenes, your application can load these resources from a single file.

* In Spatial Editor, scenes are stored in the .usda file format, where each Prim corresponds to a node in the scene's hierarchical tree structure. Each node is an entity. Therefore, after adding resources to the project, you must also add them as nodes to the scene in order to use those resources within the scene. For details, see [Manage nodes](/en_node-management).
* In PICO Spatial SDK, the `Scene` class manages multiple `Entity` objects as a scene. Typically, a spatial container hosts a scene, and all 3D entities created within the container belong to that scene. You can call `bundle.loadModel()` to load scenes created in Spatial Editor. For details, see [AssetBundle](/spatial-sdk/asset-bundle/).

## Create a scene
Refer to the following steps to create a scene in the current Spatial Editor project.

1. Click the plus sign in the scene tab bar.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/79a3629f93b749bc8b977a7c65c270ff~tplv-goo7wpa0wc-image.image)
2. In the pop-up dialog box, input the scene name, set tags, select the directory to save to, and then click **Save**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4bda35e9169340c9b6f43885d473d24a~tplv-goo7wpa0wc-image.image)
3. You can see the newly created scene in Spatial Editor.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/26766eeca42f47ce8653372e11de88d8~tplv-goo7wpa0wc-image.image)

## Close a scene
In the scene tab bar, find the tab for the scene you want to close, and click the close button on that tab to close the scene.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8607bf94c0b949a2aba5a07e7e5df7f3~tplv-goo7wpa0wc-image.image)
## Search for a scene
In the scene tab bar, find the scene tab, right-click, and select **Find in Project** from the dropdown menu. The **Project Browser** window at the bottom of the Spatial Editor interface will display the scene file.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a8fa562dcc234112921ee8ebd596db24~tplv-goo7wpa0wc-image.image)



