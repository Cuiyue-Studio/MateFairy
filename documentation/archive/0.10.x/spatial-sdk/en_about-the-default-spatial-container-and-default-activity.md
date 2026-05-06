The overall design goal of the PICO Spatial SDK is to reduce direct dependency on `Activity`. That is, during regular development, you do not need to pay attention to the lifecycle of `Activity` or explicitly manage `Activity`; you only need to focus on declaring the spatial container and the Composable content inside the container.
## Unique characteristics of the splash screen
The app's splash screen has unique characteristics. Due to the requirements of the Android launch mechanism, it is necessary to register container-related information for `LaunchActivity` in `AndroidManifest.xml`.
In general, there is a one-to-one relationship between `Activity` and the spatial container. When the app starts, the system will first trigger the logic to "open the default container," and then open the `Activity` bound to that container. The "Activity bound to the default container" is the main `Activity` declared in `AndroidManifest.xml`. This `Activity` needs to inherit from `SpatialLaunchActivity`.
For example:

* Declared like this in Kotlin code:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7ca20b73761940d7b9c522c617ab4fb7~tplv-goo7wpa0wc-image.image)
* Registered like this in AndroidManifest.xml:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2d91d2974d894d218f9e7b714d8c216c~tplv-goo7wpa0wc-image.image)

Therefore, when the app starts and the system opens the default container, the system actually starts the `LaunchActivity` declared here.
It is important to note that in the function body of `DefaultWindowContainer {}` or `DefaultStage {}`, what you write is the `content`, not the `SpatialContainer` instance itself.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bfbfc7cf06c641fe966f776c4e126ea3~tplv-goo7wpa0wc-image.image" width="1694px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/92c39bb999554247a1b5acf0e165041b~tplv-goo7wpa0wc-image.image" width="1504px" /></div>



</div>
</div>

Currently, the PICO Spatial SDK also supports customizing `Activity` for spatial containers. For detailed instructions, refer to "[Customize the Activity of spatial containers](/customize-the-activity-for-spatial-containers)".
## About the lifecycle
The lifecycle of the default spatial container precedes that of the default `Activity`. That is, the `onCreate()` of the default spatial container is called before the `onCreate()` of `SpatialLaunchActivity`.
In addition, in the `onCreate()` of `SpatialLaunchActivity`, the SDK will call `setContent()` to set the `content` declared in the function body of `DefaultWindowContainer {}` or `DefaultStage {}`.
## FAQs
### What is the role of the spatial container set by `DefaultWindowContainer {}` / `DefaultStage {}`?
`DefaultWindowContainer {}` or `DefaultStage {}` sets the `content` of the default spatial container. The default spatial container will launch the default `Activity`, rather than the default `Activity` launching the default spatial container.
### How is the content of the default spatial container loaded into the default Activity?
The `content` declared in `DefaultWindowContainer {}` / `DefaultStage {}` will be loaded into the `Activity` after the default spatial container is started and bound to the default `Activity`.
