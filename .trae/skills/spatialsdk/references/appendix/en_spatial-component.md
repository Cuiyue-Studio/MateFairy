You can add spatial components in WindowContainer to enhance the sense of space in your app.
## Augment
Function Area is a unified design specification for Planar and Volumetric windows. The plane containing the window is typically divided into four regions: the top navigation area, the left navigation area, the bottom operation area, and the right extension area. In PICO OS 6, a series of sub-windows called Augment are also defined, which can extend beyond the boundaries of the main window. These sub-windows are distributed around the main window and its surrounding regions and, together with the main window, form the complete anatomy of the app's window.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/db3d585f2ea949e681df52f34408548b~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/85d4ffd786f44207aee8550b9b4b9ec3~tplv-goo7wpa0wc-image.image)


</div>
</div>

* Augment affects the bounding box size of WindowContainer.
* The caption bar of WindwoContainer dynamically avoids Augment.

For more information, refer to "[Augment](/document/spatial-ui/augment/)".
## Subwindow
Subwindow is a control used to host auxiliary operations and information. It is located outside WindowContainer, but depends on it for its existence.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e966165797f74d75bae00f097c67a30f~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/552848d3ac834b7992c937031f814669~tplv-goo7wpa0wc-image.image" width="341px" /></div>



</div>
</div>

Subwindow has built-in RTL support. In default mode (`SubwindowPlacement.Default`), whether to display on the left or right side of the WindowContainer is automatically determined based on the RTL configuration.
For more information, refer to "[Subwindow](/document/spatial-ui/subwindow/)".
## ToolBar
ToolBar is a tool control fixed at the bottom of WindowContainer. When an app needs to control, modify, create, or delete the contents of the current WindowContainer, it is preferable to use the system-provided ToolBar control.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df129eeb99f5465aaae1feca62350108~tplv-goo7wpa0wc-image.image" width="348px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c4787f4a7424bb380c2079a8e32019b~tplv-goo7wpa0wc-image.image" width="406px" /></div>



</div>
</div>

For more information, refer to "[ToolBar](/document/spatial-ui/tool-bar/)".
## TabBar
TabBar is a navigation control that helps users switch between different modules in an app. Depending on your actual needs, you can place it at the top or on the left side of WindowContainer.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e71ad3155b9e48ebb2e54cf716205916~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b5ed4d3be0e6431583d4ac07d414e5a7~tplv-goo7wpa0wc-image.image" width="344px" /></div>



</div>
</div>


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/23979aa56a3f42d7a3f97bb9405cb904~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fd9691d0335f4bea987f95099b73480d~tplv-goo7wpa0wc-image.image" width="348px" /></div>



</div>
</div>

For more information, refer to "[TabBar](/document/spatial-ui/tab-bar/)".
