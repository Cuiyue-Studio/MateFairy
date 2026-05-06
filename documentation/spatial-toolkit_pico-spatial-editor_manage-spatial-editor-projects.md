This article describes how to create a Spatial Editor project, import an existing Spatial Editor project, import a Spatial Editor project into a spatial project, and package a Spatial Editor project as a .zip file.
## What is a Spatial Editor project
A Spatial Editor project is used to manage the 3D resources of a spatial app. The following figure shows the file structure of a Spatial Editor project. The following items are included:

* `Sources/Assets`: All 3D models, materials, textures, audio, and other resources included in the project.
* `Sources/Scenes`: All scene files (.usda files) in the project.
* UserSettings: User personalization settings for the editor only (such as viewport, history, and more). These settings do not affect the actual content or operation of the project.
* .spatialproject: The project's configuration and metadata. Opening this file allows you to view and manage the project in Spatial Editor.
* ModelView: Specifies the scene that is loaded first when the project starts in Spatial Editor.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/75436cb880c2437c95358de78efc0394~tplv-goo7wpa0wc-image.image)
A Spatial Editor project provides 3D resources for a spatial project. A spatial project contains at least one Spatial Editor project. The following figure shows the position of the Spatial Editor project within a spatial project. For details, see [Manage spatial projects](/en_manage-projects).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f4e04ea546ec451f87a9ddeccf22657c~tplv-goo7wpa0wc-image.image)
## Create a Spatial Editor project
Open Spatial Editor, click **Create Project**, enter the name of the Spatial Editor project in the **Save As** input box, and then click **OK**.
You will enter the editing interface of the Spatial Editor project. Each newly created Spatial Editor project has a default scene (scene name: Default). This default scene corresponds to a .usda file (Default.usda). You can add resources to the default scene or create scenes within the Spatial Editor project.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e6e6d7088c14094b23136cd4fed538c~tplv-goo7wpa0wc-image.image)
## Open a Spatial Editor project
Open Spatial Editor and click **Open**.
In the pop-up window, locate the Spatial Editor project you want to open, select the .spatialproject file corresponding to the project, and then click **Open**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/95664b9ba1404fe18f445df8eb867f31~tplv-goo7wpa0wc-image.image)
## Import a Spatial Editor project into a spatial project
You can import a Spatial Editor project into a spatial project. For details, see [Manage spatial projects](/en_manage-projects).
## Package a Spatial Editor project as a zip file
Follow the steps below to package a Spatial Editor project as a .zip file.
To reduce file size, the zip file does not include .Library files or .CacheData files.


1. In the top menu bar, select **File** > **Zip Project**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/34bbc67d93454564a392a9ca7fa98d01~tplv-goo7wpa0wc-image.image)
2. In the pop-up window, enter the name of the zip file, select the destination path to save the .zip file, and finally click **Save**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5daede60cb7c4a3981a5bdb23394ee49~tplv-goo7wpa0wc-image.image)
3. After packaging is complete, Spatial Editor will display a window indicating that packaging was successful. You can click **Show** to view the packaged .zip file in the file explorer.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2e3789a246ee4e27a35c94fa560f6741~tplv-goo7wpa0wc-image.image)




