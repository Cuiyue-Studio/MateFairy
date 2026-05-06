This article describes how to manage resources in Android Studio.
## What are resources
In a spatial app, resources are core components for building scenes, including key data such as meshes, materials, textures, and audio. For more information, see [Resource overview](/document/spatial-sdk/resource-overview/).
## Add resources to a Spatial Editor project
Refer to the following steps to add resource files to a Spatial Editor project in Android Studio.

1. In the Project tool window on the left side of Android Studio, right-click the directory where you want to add resource files, and in the pop-up menu, select **Add Files to**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72f3104bbe884878a5136079ee57dbeb~tplv-goo7wpa0wc-image.image)
2. Select the resource files to add, and then click **Open**. The resource file will be added to the directory you specified.

## Preview .usda or .usdz files
You can preview 3D resource files in .usda or .usdz format in Android Studio.
In the Project tool window on the left side of Android Studio, double-click the resource file you want to preview to open the preview page. The first time you preview a resource file, loading may take a long time. Please wait patiently. 
You can also click the **Open In Editor** button to open the .usda file in Spatial Editor.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9f48cac46bb44d208585f2f452a454d2~tplv-goo7wpa0wc-image.image)
On the preview page, you can use the mouse to zoom in, zoom out, rotate, and move the resource.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7973f6b7ab9b41c6a9975e8b7195d8c5~tplv-goo7wpa0wc-image.image)
If the resource file is updated in Spatial Editor, the preview page will also automatically synchronize the updated resource file.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d894dfa95f8249bc8d1f26b5d07b1db7~tplv-goo7wpa0wc-image.image)
## Reference resources using PICO Spatial SDK
You can reference resources in the Spatial Editor project of a Spatial project through the PICO Spatial SDK, or reference resources in an external .bundle file compiled from a Spatial Editor project.

* [Reference resources in a Spatial Editor project](/editor/ni3ive3z): Applicable when the Spatial Editor project is a module of your main project and is developed together with the application code.
* [Reference resources from external .bundle files](/editor/en_manage-resource): Applicable when you have obtained a pre-compiled .bundle file and need to manually integrate it into your project.

### Reference resources in a Spatial Editor project
When you build a spatial app, the Spatial Editor project in the Spatial project is compiled into a .bundle file and packaged into the APK's `assets` folder. The name of the .bundle file is the same as the name of the library module where the Spatial Editor project is located. Therefore, you can use the `AssetBundle.load()` method provided by the PICO Spatial SDK to load the Spatial Editor project. The `AssetBundle.load()` method returns an `AssetBundle` instance that has loaded the .bundle file. Then, you can use the `AssetBundle` instance to load resources included in the Spatial Editor project.
The following example demonstrates how to reference resources from the library module named editor-asset.
```Kotlin
var bundle: AssetBundle? = null
bundle = AssetBundle.load("asset://editor-asset.bundle")
Entity.load(modelName = "Hi", bundle = bundle)
```

### Reference resources from external .bundle files
If your Spatial project already contains the `app/src/main/assets` directory, and this directory includes .bundle files compiled by the Spatial Editor project, you can directly reference resources from these .bundle files.
You can use the `AssetBundle.load()` method provided by the PICO Spatial SDK to load .bundle files compiled by the Spatial Editor project from the `app/src/main/assets` directory.
If your Spatial project does not have an `app/src/main/assets` directory, refer to the following steps to create an `app/src/main/assets` directory.

1. In the project tool window on the left side of Android Studio, right-click the app/src/main directory and select **New** > **Directory**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/87ec589ef0a043148aa4b7dabcda07e6~tplv-goo7wpa0wc-image.image)
2. In the **New Directory** window, select **assets**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/78e7fa5492ee4731a9ca32089a579d92~tplv-goo7wpa0wc-image.image)


