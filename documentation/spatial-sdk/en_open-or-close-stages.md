When an application starts, the default spatial container opens first to display the application's initial interface. Additionally, you can use the PICO Spatial SDK to open or close a spatial container.
## Open a stage
You can use the `openStage` function to open a Stage. When calling it, you must specify the following parameters:
| **Parameter** | **Required** | **Description** |
| --- | --- | --- |
| context | Yes | An Android [Context]. |
| id | Yes | The ID set when registering the Stage (that is, the name of the Stage). |
| style | Yes | The style of the Stage, which affects `immersion`. Available styles: <br>  <br> * `Automatic`: Style is determined by the system. Currently, the system's default setting is `Mixed`. <br> * `Mixed`: `immersion` is 0. The app will overlay virtual objects onto the user's real environment, enabling them to blend naturally into the background environment. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d749dc88ccb4cbcb98488f672b17028~tplv-goo7wpa0wc-image.image) <br> * `Progressive`: `immersion` is between 0 and 100. Users can manually adjust the app's immersion level through the system UI.  <br>    Note that in a Stage with the `Progressive` style, the rendering of 3D model is affected by the level of immersion — the model's display ratio is directly proportional to the current immersion level. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6eb4b7561ae04c02ab79aaa88899e0cb~tplv-goo7wpa0wc-image.image) <br> * `Full`: `immersion` is 100. The app places users in a virtual environment that is completely isolated from the real world. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7a6ae5c4fc048dc81eef749f43fb866~tplv-goo7wpa0wc-image.image) |
`openStage` is a suspend function. You need to call this function within a Coroutine or another suspend function. The code sample is as follows:
```Kotlin
val context = LocalContext.current
val coroutine = rememberCoroutineScope()
coroutine.launch { openStage(context, "HelloStage", StageStyle.FULL) }
```

* An app can have multiple Stages, but only one Stage can be open in a space at a single time; it is not possible to open more than one simultaneously.
* You need to configure a custom skybox and image-based lighting (IBL) for a Stage; otherwise, the environment will appear completely black when you open the Stage. For information on configuring IBL, refer to "[Image-based lighting](/en_image-based-lighting)".
* The `style` parameter cannot be modified dynamically after a Stage is opened; it can only be specified each time `openStage` is called.

## Close a Stage
You can close a Stage by using the `closeStage` function. `closeStage` is a suspend function. You need to call this function within a Coroutine.
Because only one open Stage can exist in a space. Therefore, there is no need to specify which Stage to close in `closeStage`; the system will close the only Stage currently opened.

If you want to close a Stage when a WindowContainer is closed (for example, automatically close the open Stage when the WindowContainer serving as the home page is closed), you can implement it as follows:
```Kotlin
@Composable
fun HomePage() {
    val coroutine = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                coroutine.launch(Dispatchers.Main.immediate) { closeStage() }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    // ...
}
```


