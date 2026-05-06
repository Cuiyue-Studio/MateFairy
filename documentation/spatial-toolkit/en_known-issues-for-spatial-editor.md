The known issues of PICO Spatial Editor are as follows:
**Material-related**

* If the Shader Graph node contains normal, tangent, or bitangent Input parameters, these can currently only be used by connecting an Input node. Directly modifying the value of the parameter will not take effect.
* Due to permission issues, Shader Graph may sometimes fail to compile materials (uncompiled materials will appear purple). When this issue occurs, check whether the security permission switch in Android Studio is enabled. If it is not enabled, enable it.
* After changing the parent-child relationship of nodes in the Shader Graph Instance material, saving may cause the reference relationship to become invalid or the application to crash.
* In certain cases, you may not be able to add reference relationships to existing material nodes through the reference window. In this situation, it is recommended to create an empty node to use as a reference.

**Lighting-related**

* If the Image-Based Light component is exported as a .usdz format asset, environmental lighting will not take effect on devices and in the PICO Emulator.

**Animation-related**

* Due to the influence of the bounding box, models with skeletal animation may be abnormally clipped at different camera angles.

**Asset library-related**

* The native .usdz specification of macOS mainly supports texture formats such as .png and .jpeg, while some .usdz files in the asset library use .tga format textures. Therefore, when previewing .usdz files on macOS, you may see missing textures. This only affects previewing and does not affect normal loading and usage of assets.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6a2c82edbe4d4bebb9d2076e0cc5b3bc~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1527e2cd155e42b5addffe481b8c079b~tplv-goo7wpa0wc-image.image)


</div>
</div>

**Other**

* After a custom component is deleted, if a component with the same name is created again, the component will display an error.


