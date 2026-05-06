This article describes how to create a spatial project in Android Studio, import a Spatial Editor project into a spatial project, build a spatial app based on a spatial project, and debug a spatial project in PICO Emulator.
## What is a spatial project
After installing the PICO Spatial plugin, you can create a **spatial project** in Android Studio. Spatial projects can be built as spatial apps.
Spatial projects are extensions based on standard Android projects, designed to support the development of 3D spatial content. They follow Android's design principles and encapsulate 3D assets as independent Android library modules. A spatial project consists of a main module and one or more library modules containing **Spatial Editor projects**. A spatial Editor project is created using Spatial Editor and provides 3D assets for the spatial project. For details, see [What is PICO Spatial Editor](/know-spatial-editor).
A library module can contain only one Spatial Editor project. You can import an external Spatial Editor project as a library module into a spatial project. For details, see [Import the Spatial Editor project into the spatial project](/editor/en_manage-projects).

Your newly created spatial project includes a main module and a library module named `editor-asset` by default. In the Project view, you can see that the `editor-asset` library module contains a Spatial Editor project named `spatialPackContent`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d35025ff4c244747ae2e79dafb4d3f7e~tplv-goo7wpa0wc-image.image)
## Create a spatial project
For details, follow these steps to create a spatial project in Android Studio.

1. On the **Welcome to Android Studio** page, click **New Spatial Project** or go to **File** > **New** > **New Spatial Project...**.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ae00e3ffe2094cc2bddbe21d19e01c7e~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7ef8f0818f2b4b4c994c9cb0e04710b1~tplv-goo7wpa0wc-image.image)



</div>
</div>


2. On the **New Project** page, select a template. The template includes sample code and resource files. After selecting, click **Next**.
   You can choose from the following types of templates. Each template uses a different type of spatial container. For details, see [Learn about spatial containers & space state](/document/spatial-sdk/learn-about-spatial-containers-and-space-states/).
   * **P** **lanar Window Container**: A WindowContainer with limited thickness, similar to a "flat panel". It is mainly used to host 2D interfaces commonly found in traditional Android development and can also be used to display smaller 3D objects.
   * **Volumetric Window Container**: Another container that presents content in the form of a window, which can be understood as a "cuboid" with dynamically adjustable dimensions. Compared to the limited-thickness Planar container, the Volumetric container occupies a larger spatial volume and can host larger 3D objects, thereby maximizing the user's 3D interaction experience within a limited area.
   * **Full Stage**: The Stage can be regarded as a "venue" without boundary constraints, supporting placement of more content, including UI components, 2D layouts, 3D models, and more. Stage is divided into different styles, allowing users to interact with their real environment at varying levels of immersion. Full Stage indicates that the style of Stage is Full, that is, `immersion` is 100. In Full Stage, the application places the user in a virtual environment completely isolated from the real environment.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df38c653243345038d219388c9cb24b0~tplv-goo7wpa0wc-image.image)
3. Configure the project name, package name, save location, and minimum PICO OS 6 version. Then click **Finish**.
   Different versions of PICO OS 6 correspond to different versions of PICO Spatial SDK:
   
   * PICO OS 6 v0.10 preview corresponds to PICO Spatial SDK 0.10.x.
   * PICO OS 6 v0.11 preview corresponds to PICO Spatial SDK 0.11.x.

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c81d49933d8740229165280e5a6bb0ad~tplv-goo7wpa0wc-image.image)

## Import the Spatial Editor project into the spatial project
For details, refer to the following steps to import the Spatial Editor project into the spatial project in Android Studio. The Spatial Editor project will be imported into the spatial project as a library module of type **Spatial Resource Library**.

1. In Android Studio, select **File** > **New** > **New Module**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9d55d5945a524142bf0cb82e77f27f30~tplv-goo7wpa0wc-image.image)
2. On the **Create New Module**  page, in the **Templates** section, select **Spatial Resource Library**, then set the following parameters.
   | Parameter | Description |
   | --- | --- |
   | **New Spatial Editor Project** | Whether to create a new Spatial Editor project. <br>  <br> * **Selected**: Create a new Spatial Editor project. <br> * **Not selected**: Import an existing Spatial Editor project. |
   | **Editor project directory** | The root directory of the Spatial Editor project to be imported. You can directly enter the path, or click the folder icon on the right to select the root directory of the Spatial Editor project. As shown in the figure below, the root directory of the Spatial Editor project is the directory where the .spatialproject file is located. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa1d9ef7955a4f7e85d1db7e23fee166~tplv-goo7wpa0wc-image.image) |
   | **Module name** | Name of the library module, for example, mylibrary. |
   | **Package name** | Package name of the library module, for example, com.example.mylibrary. |
   | **Minimum OS version** | Minimum PICO OS 6 version. Different versions of PICO OS 6 correspond to different versions of PICO Spatial SDK: <br>  <br> * PICO OS 6 v0.10 preview corresponds to PICO Spatial SDK 0.10.x. <br> * PICO OS 6 v0.11 preview corresponds to PICO Spatial SDK 0.11.x. |


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/426c67f7884245508083abc63a9c17e8~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f0d8f1026bba4d8f8e317d1cb34682b9~tplv-goo7wpa0wc-image.image)


</div>
</div>


3. Click **Finish**. The **Spatial Resource Library** you created will appear in the **Project** view of the spatial project, and will also be automatically added to the settings.gradle.kts file.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">



![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6f127508f1e5410f9a109e6f010529a8~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">


![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e496a50c640b4dc8a93f434da45f4cc0~tplv-goo7wpa0wc-image.image)



</div>
</div>


4. Add an implementation statement in the app's `build.gradle.kts` file to reference the **Spatial Resource Library** you added. For example, `implementation(project(":mylibrary"))`.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/45d9a2dc24a442638595d9aea24f1a04~tplv-goo7wpa0wc-image.image)

## Build a spatial project
When you build a spatial project, Gradle will automatically package the library module containing the Spatial Editor project as a .bundle file, and then package the .bundle file into the `assets` folder of the APK. Therefore, you can build a spatial project as a spatial app just like building a regular Android project. For example, you can build the spatial project as an APK.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1224755187be4cc0a85ada0081ee2e78~tplv-goo7wpa0wc-image.image)
## Customize Spatial Editor project packaging
In a spatial project, the Spatial plugin will automatically add a Gradle plugin named Spatial Tools, with the ID `com.pico.spatial.tools`, to the `editor-asset` library module's `build.gradle` file. This plugin comes from https://artifact.bytedance.com/repository/Volcengine.
```Groovy
plugins {
    id 'com.pico.spatial.tools' version '0.11.0'
}
```

In the `editor-asset` library module's `build.gradle` file, you can configure the following parameters for Spatial Tools:

* **name**: Specifies the name of the .bundle file generated after packaging the Spatial Editor project.
* **spatialToolsVersion**: Specifies the toolchain version that the spatial project depends on, including the versions of Spatial Editor and PICO Emulator.
* **bundleOptions**: Provides optional packaging configurations. If this item is not configured, Gradle will forcibly repackage every time you build.
   * **forceBuild**: Sets whether to forcibly repackage.
      * **true**: repackaging will occur every time you build.
      * **false**: repackaging will only occur when the `name`, `spatialToolsVersion`, or `files` parameters change; otherwise, this step will be skipped to improve build efficiency. This enables incremental packaging at the granularity of .bundle files.
   * **files**: Specifies the resource files to be included in the packaging. Only files declared here will be packaged, and at least one `.usda` file must be included.
      * **include**: Used to add specific files or folders to be packaged.

To ensure that modifications to resource files in the Spatial Editor project take effect, set `forceBuild` to true to force repackaging; otherwise, the changes will not take effect.

Gradle packages the `editor-asset` library module into a compliant .bundle file according to the parameters of Spatial Tools, and then further packages it into the APK's `assets` folder. The following example code shows how to configure the parameters for Spatial Tools.
```Groovy
spatial {
    name = "editor-asset"
    spatialToolsVersion = 0.11
    
    
    bundleOptions {
        forceBuild = true
        
        files {
            include("/Sources/Assets/MyScene.usda")
            include("/Sources/Assets/box.usda")
        }
    }
}
```

Additionally, if you import other Spatial Editor projects into a spatial project, the Spatial Plugin will automatically add the Gradle Plugin with id `com.pico.spatial.tools` to the `build.gradle.kts` file of the corresponding library module for each Spatial Editor project. Gradle will also package the library modules corresponding to these newly imported Spatial Editor projects into compliant .bundle files according to the parameters specified by these Gradle Plugins, and package them into the APK's assets folder.
For example, in the following example, the two library modules (mylibrary and editor-asset) corresponding to the two Spatial Editor projects in the spatial project are both packaged as .bundle files. Then, these .bundle files are packaged into the APK's assets folder.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c8344b04f1d54020a19de1459a01f46d~tplv-goo7wpa0wc-image.image)
## Debugging a spatial project in PICO Emulator
After installing PICO Emulator, you can use Android Studio to open a spatial project, select the module to run (such as app), and then click the **Run** or **Debug** button to build your spatial project and install it in PICO Emulator for running and debugging. For details, see [Add and manage PICO Emulator](manage-pico-emulator).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6dd556684dbf4367a9fca6bccb94fb07~tplv-goo7wpa0wc-image.image)


