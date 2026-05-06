This article describes how to create a spatial project in Android Studio, import a Spatial Editor project into a spatial project, build a spatial app based on a spatial project, and debug a spatial project in the PICO Emulator.
## What is a spatial project
After installing the PICO Spatial Plugin, you can create a **spatial project** in Android Studio. A spatial project can be built as a spatial app.
A spatial project is an extension of a standard Android project, designed to support the development of 3D spatial content. It follows Android's design principles, encapsulating 3D assets as independent Android library modules. A spatial project consists of a main module and one or more library modules containing **Spatial Editor projects**. A Spatial Editor project is created using the Spatial Editor and provides 3D assets for the spatial project. For details, see [What is Spatial Editor](/know-spatial-editor).
A library module can only contain one Spatial Editor project. You can import an external Spatial Editor project as a library module into a spatial project. For details, see [Import the Spatial Editor project into the spatial project](/editor/manage-projects).

Your newly created spatial project includes a main module and a library module named `editor-asset` by default. In the Project view, you can see that the `editor-asset` library module contains a Spatial Editor project named `spatialPackContent`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d35025ff4c244747ae2e79dafb4d3f7e~tplv-goo7wpa0wc-image.image)
## Create a spatial project
For details, follow these steps to create a spatial project in Android Studio.

1. On the **Welcome to Android Studio** page, click **New spatial project** or go to **File** > **New** > **New spatial project...**.


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
   * **Planar Window Container**: A WindowContainer with limited thickness, similar to a "flat panel". It is mainly used to host 2D interfaces commonly found in traditional Android development, and can also be used to display smaller 3D objects.
   * **Volumetric Window Container**: Another container that presents content in the form of a window, which can be understood as a "rectangular cuboid" with dynamically adjustable dimensions. Compared to the limited-thickness Planar Window Container, the Volumetric Window Container occupies a larger spatial volume and can host larger 3D objects, thereby maximizing the user's 3D interaction experience within a limited area.
   * **Full Stage**: A Stage is an expansive, effectively unbounded space that can host a wide range of content, including UI components, 2D layouts, 3D models, and more. Different Stage styles provide different levels of immersion, enabling users to interact with the real environment to varying degrees. A Full Stage is a Stage configured with the **Full** style, which represents 100% immersion. In this mode, the application places the user in a fully virtual environment that is completely separated from the real world.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b575be4a54b74affbbb8eccbee9735a7~tplv-goo7wpa0wc-image.image)
3. Configure the project's name, package name, and save location. Then click **Finish**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a8e09f319c047c59a8cfc8dcfe06254~tplv-goo7wpa0wc-image.image)

## Import the Spatial Editor project into the spatial project
For details, refer to the following steps to import the Spatial Editor project into the spatial project in Android Studio. The Spatial Editor project will be imported into the spatial project as a library module of type **Spatial Resource Library**.

1. In Android Studio, select **File** > **New** > **New Module**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9d55d5945a524142bf0cb82e77f27f30~tplv-goo7wpa0wc-image.image)
2. On the **Create New Module**  page, in the **Templates** section, select **Spatial Resource Library**, then set the following parameters.
   | Parameter | Description |
   | --- | --- |
   | **New Spatial Editor project** | Whether to create a new Spatial Editor project. <br>  <br> * **Checked**: Create a new Spatial Editor project. <br> * **Unchecked**: Import an existing Spatial Editor project. |
   | **Editor project directory** | The root directory of the Spatial Editor project to be imported. You can directly input the address or click the folder icon on the right to select the root directory of the Spatial Editor project. As shown in the figure below, the root directory of the Spatial Editor project is the directory where the .spatialproject file is located. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa1d9ef7955a4f7e85d1db7e23fee166~tplv-goo7wpa0wc-image.image) |
   | **Module name** | Name of the library module, for example, mylibrary. |
   | **Package name** | Package name of the library module, for example, com.example.mylibrary. |
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/44561b183f91487da19d265683b3477e~tplv-goo7wpa0wc-image.image)
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

## Build the spatial project
When you compile the spatial project, Gradle will automatically package the library module where the Spatial Editor project resides as a .bundle file, and then package the .bundle file into the `assets` folder of the APK.
Therefore, you can build a spatial project as a spatial app just like building a regular Android project. For example, you can build the spatial project as an APK.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1224755187be4cc0a85ada0081ee2e78~tplv-goo7wpa0wc-image.image)
### How the library module of the Spatial Editor project is packaged into the APK
In the spatial project, the Spatial Plugin automatically adds a Gradle Plugin with id `com.pico.spatial.tools` to the `editor-asset` library module's `build.gradle` file.
This Gradle Plugin specifies the name of the bundle after packaging the Spatial Editor project via the `name` parameter, and specifies the toolchain version that the current spatial project depends on via the `spatialToolsVersion` parameter, including the PICO Spatial Editor version and the PICO Emulator version. Therefore, Gradle can accurately package the `editor-asset` library module into a valid .bundle file based on these parameters, and further package it into the `assets` folder of the APK.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/11b1adffc9994de2acbc50081602bc59~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/24a318f948e14407acefee2162d25c06~tplv-goo7wpa0wc-image.image)



</div>
</div>

Additionally, if you import other Spatial Editor projects into the spatial project, the Spatial Plugin will also automatically add a Gradle Plugin with id `com.pico.spatial.tools` to the `build.gradle.kts` file of the corresponding library modules for these Spatial Editor projects. Gradle will also package these newly imported Spatial Editor project library modules into valid .bundle files based on the parameters specified by these Gradle Plugins, and package them into the assets folder of the APK.
For example, the library modules corresponding to the two Spatial Editor projects in the spatial project (mylibrary and editor-asset) are both packaged as .bundle files. Then, these .bundle files are packaged into the assets folder of the APK.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c8344b04f1d54020a19de1459a01f46d~tplv-goo7wpa0wc-image.image)
## Debug spatial projects in PICO Emulator
After installing PICO Emulator, you can use Android Studio to open a spatial project, select the module to run (such as app), and then click the **Run** or **Debug** button to build your spatial project and install it to PICO Emulator for running and debugging. For details, see [Add and manage PICO Emulator](manage-pico-emulator).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6dd556684dbf4367a9fca6bccb94fb07~tplv-goo7wpa0wc-image.image)


