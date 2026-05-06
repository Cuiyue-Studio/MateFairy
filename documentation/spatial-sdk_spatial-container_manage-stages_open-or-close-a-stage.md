When the application starts, the default spatial container is opened first to display the application's initial interface. Additionally, you can use the PICO Spatial SDK to open or close a spatial container.
## Open Stage
You can open a Stage by calling the `openStage()` function. The required parameters are as follows:
| **Parameter** | **Required** | **Description** |
| --- | --- | --- |
| id | Yes | The ID (that is, the name) set when declaring the Stage. |
| style | No | The style of `Stage`, used to control the fusion of the real environment's Video Seethrough (VST) and the virtual scene, as well as the behavior of image-based lighting (IBL) and the rendering of virtual entities. <br>  <br> * `Automatic`: The style is determined by the system. Currently, the system's default setting is the `Mixed` style. <br> * `Mixed`: Virtual entities are always rendered, and image-based lighting is entirely derived from the real environment's Video Seethrough (VST). <br>    The following figure shows a scene in Mixed style. In this example, a virtual metallic sphere is enclosed by a sky sphere. Although the sky sphere uses the "Night Art Museum" map and image-based lighting is enabled, the sphere still reflects the Video Seethrough (VST) of the real environment (the bedroom). This is because, in `Mixed` mode, the environment lighting is entirely sourced from the Video Seethrough (VST) of the real room, not the Night Art Museum. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19a80d5a89724216a5570af27db82450~tplv-goo7wpa0wc-image.image) <br> * `Progressive`: Allows you to adjust the degree of immersion to change the fusion of the real environment's Video Seethrough (VST) and virtual entities. You can set the degree of immersion using the `Stage`'s `immersion` parameter. The value range for immersion is 0~100: <br>       * **immersion is 0**: The experience is similar to the `Mixed` style, and you can still see the real environment. However, unlike the Mixed style, virtual entities are not rendered. Therefore, both the metal sphere and the night art museum will disappear. <br>       * **immersion greater than 0 and less than 100**: As the `immersion` value increases, the rendering of the real environment gradually decreases, while the rendering of virtual entities gradually increases. <br>       * **immersion equals 100**: Equivalent to the `Full` style. For details, refer to the description of the `Full` style. <br>    The following image shows the scene with `immersion` set to 50 in the Progressive style. In this example, a virtual metal sphere is enclosed by a sky sphere. The sky sphere uses the "night art museum" map and enables image-based environment lighting. You can see that the reflection effect of the metal sphere is a blend of the real environment (bedroom) Video Seethrough (VST) and the night art museum. The front of the metal sphere displays the real environment's Video Seethrough (VST), while the edges and back show the art museum. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c2e62feb1274952a37708921af99308~tplv-goo7wpa0wc-image.image) <br> * `Full`: Virtual entities are always rendered, and image-based environment lighting comes entirely from the virtual scene. Therefore, if you do not set a virtual scene, the application will display a pure black background due to the lack of lighting. <br>    The following image shows the scene in the `Full` style. In this example, a virtual metal sphere is enclosed by a sky sphere. The sky sphere uses the "night art museum" map and enables image-based environment lighting. You can see that the metal sphere fully reflects the virtual night art museum. The real environment (bedroom) is completely blocked. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3e504c44118a45c480a5e58e83f0068f~tplv-goo7wpa0wc-image.image) |
| bundle | No | Custom passthrough data, which can be accessed at the content root node of the Stage. |
| upperLimbRenderMode | No | Controls the visibility effect of the upper limbs in the Stage. <br>  <br> * `UpperLimbRenderMode.Default`: Follows system settings. <br> * `UpperLimbRenderMode.Visible`: Upper limbs are visible. <br> * `UpperLimbRenderMode.Hidden`: Upper limbs are not visible. |
`openStage()` is a suspend function. You need to call this function within a Coroutine or another suspend function. Sample code:
```Kotlin
val coroutine = rememberCoroutineScope()
// Method 1 (recommended): Use com.pico.spatial.ui.platform.containers.SpatialNavigator.openStage
val navigator = LocalSpatialNavigator.current
coroutine.launch { 
    val result = navigator.openStage("HelloStage", StageStyle.Full)
    when (result) {
        is OpenStageResult.Allowed -> {
            // Stage approved for opening
            Log.i("Stage", "Stage successfully opened.")
        }
        is OpenStageResult.NotAllowed -> {
            // Intercepted by system policy
            Log.w("Stage", "Opening Stage is not allowed.")
        }
        is OpenStageResult.Error -> {
            // Technical exception
            Log.e("Stage", "Failed to open Stage: ${result.code} - ${result.reason}")
        }
    }
}
// Method 2: Use com.pico.spatial.ui.platform.containers.openStage
val context = LocalContext.current
coroutine.launch { 
    val result = context.openStage("HelloStage", StageStyle.Full) 
    // It is also recommended to handle the result return value
}
```

* An application can have multiple Stages, but only one Stage can be opened in space at a time; it is not possible to open multiple Stages simultaneously.
* You need to configure a custom skybox and image-based lighting (IBL) using a map for the Stage; otherwise, after opening the Stage, the environment will appear completely black. For instructions on how to configure IBL, refer to [Image-based Lighting](/image-based-lighting).
* `The style` parameter cannot be modified dynamically after Stage is opened, and can only be specified each time `openStage()` is called.

## Close Stage
You can close a Stage by calling the `closeStage()` function. `closeStage` is a suspend function. You need to call this function within a Coroutine.
Since only one Stage can be open in the space at a time. Therefore, there is no need to specify the Stage to be closed in `closeStage` ; the system will close the only currently open Stage.

To close a Stage when a WindowContainer is closed (for example, to automatically close an open Stage when the main WindowContainer is closed), you can implement it as follows:
```Kotlin
@Composable
fun HomePage() {
    val navigator = LocalSpatialNavigator.current
    val coroutine = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                coroutine.launch(Dispatchers.Main.immediate) { 
                    navigator.closeStage() 
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    // ...
}
```



