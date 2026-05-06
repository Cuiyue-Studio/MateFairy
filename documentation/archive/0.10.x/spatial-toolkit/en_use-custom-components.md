This article describes how to manage custom components.
## What is a custom component
You can create, update, or delete custom components for Spatial Editor projects. Custom components are used to store data for specific functionalities, thereby extending system capabilities. For more information, see [Custom systems and components](/document/spatial-sdk/customize-systems-and-components/).
You can create, update, or delete custom components for Spatial Editor projects in Android Studio, or create them in Spatial Editor.

* Any creation, update, or deletion of custom components performed in Android Studio will be automatically synchronized to Spatial Editor.
* Any creation of custom components performed in Spatial Editor will also be automatically synchronized to Android Studio. However, you can only update or delete custom components in Android Studio.

## Manage custom components in Android Studio
Refer to the steps below to create, update, or delete custom components for Spatial Editor projects in Android Studio.
### Create custom components
Refer to the following steps to create custom components for Spatial Editor projects in Android Studio. This article assumes that the library module for custom components is named editor-asset, and its package name is com.example.myapplication.editorAsset.

1. In the Project tool window on the left side of Android Studio, right-click the `/src/main/java/com.example.myapplication.editorAsset` directory under the editor-asset library module, and select **New**  > **Kotlin Class/File**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9fb6e349ca644a9e925aae3b0ce67dc5~tplv-goo7wpa0wc-image.image)
2. In the **New Kotlin Class/File** window, select **Class**, enter the name of the custom component, such as `MyComponent`, and then press Enter.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8bd48f8ebbe8416cbaf2795a08265959~tplv-goo7wpa0wc-image.image)
3. Open the newly created Kotlin Class file and create a simple custom component. At this point, this custom component will also be synchronized to Spatial Editor.
   ```Kotlin
   package com.example.myapplication.editorAsset
   
   import com.pico.spatial.core.ecs.Component
   import com.pico.spatial.core.json.annotations.JsonType
   
   @JsonType
   class MyComponent: Component() {
       var name = "Hi"
   }
   ```

4. In the Project tool window on the left side of Android Studio, locate the Spatial Editor project under the editor-asset library module and open the project using Spatial Editor. For example, you can use Spatial Editor to open the .spatialproject file directly in the file explorer, or double-click the .usda or .usdc resource file of the Spatial Editor project, then click the **Open in Editor** button on the preview page to open the project in Spatial Editor.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/97095fec5a1b432f86c8b2dce1a505dd~tplv-goo7wpa0wc-image.image)
5. In the **Inspector**  on the right side of Spatial Editor, click **Add Component**.
   In the **SpatialPackContent** section, you can see the custom component **MyComponent** that you created in Android Studio. This component can be associated with entities in the current Spatial Editor project.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/020873e687bb4d7bbb5c042129ba2593~tplv-goo7wpa0wc-image.image)

### Update custom components
You can directly modify the code in the Kotlin Class file, such as adding new variables or changing the values of existing variables. After saving the file, changes will be automatically synchronized to Spatial Editor.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/680b872fe9d44a11b72d54a2f2eaafca~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0a1c1c411c414dafa90059185fa641b6~tplv-goo7wpa0wc-image.image)



</div>
</div>

### Delete custom components
In the project tool window of Android Studio, locate and delete the corresponding Kotlin class file (such as MyComponent.kt). The custom component will then be removed from the Spatial Editor project, and will also be deleted in Spatial Editor through synchronization.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb6c7007e3e049b49997e44d0617a9d7~tplv-goo7wpa0wc-image.image)



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/07f5a46a0e39494bb1daf3af04fd3fb0~tplv-goo7wpa0wc-image.image)



</div>
</div>

## Manage custom components in Spatial Editor
Refer to the steps below to create custom components for the Spatial Editor project in Spatial Editor. Spatial Editor does not support updating or deleting custom components. You need to update or delete custom components in Android Studio. For details, see [Manage custom components in Android Studio](/en_editor/en_use-custom-components).
### Create custom components

1. In the **Inspector** window of Spatial Editor, click **Add Component**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/31a2b3bca81c4527b1b598c826b96827~tplv-goo7wpa0wc-image.image)
2. In the pop-up menu, double-click **New Component**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/280ca360d11f499f85f33bf8db55acc3~tplv-goo7wpa0wc-image.image)
3. In the **Enter a name for your new Component** window, enter the name of the custom component (for example, MyComponent), and then click **Create**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/83d5819cb27d46638219a441f5c558bf~tplv-goo7wpa0wc-image.image)
   You can view the created custom component in **Inspector**, and you can also see the corresponding code for the custom component in Android Studio at the same time.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.22961021945627258);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9bf5b3405d1648319a5af52f3abd824c~tplv-goo7wpa0wc-image.image)




</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.7703897805437274);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1072d1fc518c4b3db96328890172ebd7~tplv-goo7wpa0wc-image.image)



</div>
</div>


