This article describes how to manage spatial app development tools in Android Studio.
## What are spatial app development tools
Spatial app development tools include PICO Emulator and PICO Spatial Editor (Spatial Editor).

* PICO Emulator is a tool that simulates the PICO OS 6 environment on a PC. You can debug spatial apps in PICO Emulator. For details, see [What is PICO Emulator](/learn-about-pico-emulator).
* Spatial Editor is a visual editor for managing 3D scenes. For details, see [What is PICO Spatial Editor](/know-spatial-editor).

## Install, update, or uninstall spatial app development tools
After installing the PICO Spatial Plugin, you can install, update, or uninstall spatial app development tools in Android Studio under **Settings...** > **Languages & Frameworks** > **PICO Spatial Tools**, including PICO Emulator and Spatial Editor. All spatial app development tools are installed to the path specified by the **PICO Spatial Tools Location** parameter.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8e4951b1497a42ba92645006e356b686~tplv-goo7wpa0wc-image.image)
## Configure the Spatial Editor installation path in spatial projects
After installing Spatial Editor, the **Spatial Editor Location** parameter is automatically set to the installation path of Spatial Editor. For subsequently created spatial projects, the path specified by the **Spatial Editor Location** parameter will be synchronized to the `local.properties` file's `spatial.editor.dir` parameter. This allows you to preview resources in Android Studio or open resources with Spatial Editor.
To update the Spatial Editor path in existing spatial projects, manually modify the `spatial.editor.dir` parameter in the `local.properties` file.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b8fc621d3b724b13ba266f2ed4a7f59a~tplv-goo7wpa0wc-image.image)
## View the spatial app development homepage
PICO Spatial Plugin provides a spatial app development homepage on the right side of Android Studio. On this page, you can perform the following actions:

* View the version numbers of the PICO Spatial SDK, Spatial Editor, and PICO Emulator in the current spatial project.
* Open the locally installed Spatial Editor.
* Access the PICO Spatial SDK developer documentation.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/74ba7e2f4f9c420a84503576f19faaca~tplv-goo7wpa0wc-image.image)

