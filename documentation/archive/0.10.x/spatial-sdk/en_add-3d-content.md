PICO Spatial SDK provides two types of 3D view for displaying 3D content: SpatialModelView and SpatialView. SpatialModelView is suitable for simply displaying models, and it enables models to resize automatically; whereas SpatialView is suitable for situations that require dynamic modification of 3D models and complex interaction with them. SpatialModelView has lower performance overhead than SpatialView.
## Store 3D resources
For the 3D models you will use, it is recommended to create an "assets" folder as described below and place the 3D model files inside it:

1. Right-click the **main** folder, then select **New** > **Directory** from the menu.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e326cb982ce4046a20a25b9fec224ce~tplv-goo7wpa0wc-image.image)
2. In the pop-up window, select **assets**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a136ac96f5354316b7690ccb70f0b2d2~tplv-goo7wpa0wc-image.image)
3. Place asset files in the **/assets** directory, including 3D models, video, audio, and more.
   To facilitate management of different types of resources, you can also create corresponding subdirectories. For example, you can use the /assets/model directory to store 3D models, the /assets/video directory to store videos, and the /assets/audio directory to store audios.

   The PICO robot model is provided in the following file, and you can use it to perform the operations in subsequent examples.
   <a href="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3f98a75a802d4f7487e14f2790940a3c~tplv-goo7wpa0wc-image.image" filename="pico_robot_static.usdz" download>pico_robot_static.usdz</a>
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6d6158a42ac548cd96b014584c154991~tplv-goo7wpa0wc-image.image)

## Use SpatialModelView
You can place 3D content in SpatialModelView, and nest SpatialModelView within a 2D layout.
### API definition
The complete API definition for SpatialModelView is as follows:
```Kotlin
/** A view that asynchronously loads and displays a 3D model from source. */
@Composable
fun SpatialModelView(
    source: Source<*>,
    modifier: Modifier = Modifier,
    resizability: Resizability = Resizability.None,
    content: @Composable SpatialModelScope.(state: ModelLoadingState) -> Unit = { state ->
        if (state is ModelLoadingState.Success) {
            Model(state.model)
        }
    },
): Unit
```

Below are parameter descriptions:
| **Parameter** | **Description** |
| --- | --- |
| source | Data source for the 3D model. Currently, support paths and bundles under the /assets directory. For example, you can use `Source.assets("model/pico_robot_static.usdz")`. |
| modifier | Modifier that can be added to SpatialModelView. |
| resizability | Defines the scaling rules for the 3D model in SpatialModelView. Different enumeration values control whether the model retains its original size, is proportionally scaled to fit inside or outside the view, or is stretched to completely fill the view's boundaries, thereby meeting various display requirements. The specific values are as follows (the dark gray transparent back panel represents the width and height of SpatialModelView): <br>  <br> * `None`: The size of SpatialModelView and the model within it are independent of each other; the model retains its original size, although it may be cropped. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/11049f856717430db276750796543173~tplv-goo7wpa0wc-image.image) <br> * `FitInside`**:** The model is fitted within the view's width and height, fills the view's layoutable area, and is scaled proportionally. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cf274613fe904e57a393deb13d06c6bc~tplv-goo7wpa0wc-image.image) <br> * `FitOutside`**:** The model is scaled to match the width and height of the view, thus filling the view's layoutable area. The model scales proportionally and may be cropped. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1df9c1841ea14d03b2cdc3e876b99f16~tplv-goo7wpa0wc-image.image) <br> * `FillBounds`: The model is fitted within the view's width and height, and it scales unproportionally. <br>    ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fa4b42fef91544e6b9b1a3ef8fa7390f~tplv-goo7wpa0wc-image.image) |
| content | A `Composable` function that takes the model's loading states (Loading, Error, Success) as input, allowing you to adjust the displayed content based on the model's loading state. |
### Code sample
The following code demonstrates how to use SpatialModelView to load the model located at "/assets/model/pico_robot_static.usdz", and display different content depending on the model's loading state.

* Display a circular progress bar during the model's loading process.
* When the model is loaded successfully, it is placed at the center of SpatialModelView and filled within SpatialModelView using the `FitInside` mode.
* If an error occurs when loading the model, an error message will be displayed.

```Kotlin
@Composable
fun SpatialModelViewExample() {
    SpatialModelView(
        modifier =
            Modifier.fillMaxSize().background(Color.DarkGray.copy(0.6f), RoundedCornerShape(20.dp)),
        source = Source.assets("model/pico_robot_static.usdz"),
        resizability = Resizability.FitInside
    ) { state ->
        when (state) {
            is ModelLoadingState.Loading -> CircularProgressIndicator()
            is ModelLoadingState.Error -> Text(text = "Load model failed: ${state.reason}")
            is ModelLoadingState.Success -> Model(model = state.model)
        }
    }
}
```

### Limitations
Although SpatialModelView makes it easy for a model to automatically resize according to the dimensions of the view and different resizability settings, it does not allow for custom manipulation of the model. For example, you cannot use SpatialModelView to control the model's position, rotation, or arbitrary scale, nor can you use components to add additional features and effects to the model and implement control over it.
## Use SpatialView
When you want to customize how you control the model, or interact with the model in more complex ways, you can use SpatialView.
### API definition
The complete API definition for SpatialView is as follows:
```Kotlin
/** The container for 3D content. */
@Composable
public fun SpatialView(
    modifier: Modifier = Modifier,
    initial: suspend (content: SpatialViewContent, attachments: SpatialViewAttachments) -> Unit,
    update: ((content: SpatialViewContent, attachments: SpatialViewAttachments) -> Unit)? = null,
    attachments: (AttachmentPanelBuilder.() -> Unit)? = null
): Unit 
```

The parameter descriptions are as follows:
| **Parameter** | **Description** |
| --- | --- |
| modifier | Configure the layout and state of SpatialView. It affects the 2D part of SpatialView, such as background rendering, and also applies to the 3D part of SpatialViewContent, such as the entity's transparency, transform, and other properties. |
| initial | SpatialView is called once after it is added to a UI node. You can create, initialize, and configure 3D content in the `initial` method. For example, you can add an entity to the scene (using `content.addEntity()`), bind a UI attachment to the entity, and more. |
| update | After `initial` is called, `update` will be automatically called once. Whenever the compose state in SpatialView changes, `update` is called. In addition, you need to pay attention to whether the recomposition of the parent view that contains SpatialView will trigger a state change in SpatialView. |
| attachments | Add UI components to the build function of SpatialView. In `attachments`, you can define a UI component using `AttachmentPanel(id: Any, content: @Composable () -> Unit): Unit`. In `initial`, you can locate the corresponding attachment entity by its ID, and then bind the attachment to the target entity, such as a 3D model. |
* SpatialView does not support adaptive sizing, so if no scaling operation is performed after the model is loaded, it will be displayed at its original size. If the display size of the model needs to be limited within SpatialView, you should adjust the model's `scale` value to get an appropriate size.
* Currently, nesting SpatialView within AttachmentPanel  or another SpatialView is not supported.

### Code sample
The following code sample loads a PICO robot model, creates an entity from it, and adds two attachments to the entity. Of these two attachments, one displays the current time in HH:MM:SS format, and the other is a continuously rotating star icon.
In `initial`, the code loads the model, creates the robot entity and the attachment entity, adds the robot entity to SpatialView using `content.addEntity()`, and adds the attachment entity as a child node of the robot model.
In `update`, the code updates the current time and rotates the star icon around the Z axis by changing the star icon’s `roll` value, causing it to rotate counterclockwise by 0.02 degrees on each `update`.
```Kotlin
@Composable
fun SpatialViewExample() {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val currentTime = remember { mutableStateOf("") }
    var roll by remember { mutableFloatStateOf(0f) }
    SpatialView(
        modifier =
            Modifier.fillMaxSize().background(Color.DarkGray.copy(0.6f), RoundedCornerShape(20.dp)),
        attachments = {
            // attachment: text
            AttachmentPanel(id = "time") {
                Box(
                    modifier =
                        Modifier.background(Color(color = 0xB33D8BFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(text = currentTime.value, color = Color(color = 0xF2FFFFFF))
                }
            }
            // attachment: icon
            AttachmentPanel(ic_sui_rating_star) {
                Icon(
                    painter = painterResource(id = ic_sui_rating_star),
                    contentDescription = null,
                    tint = Color(color = 0xffffaa44)
                )
            }
        },
        initial = { content, attachments ->
            val robot =
                withContext(Dispatchers.IO) { Entity.load("asset://model/pico_robot_static.usdz") }
            robot.apply {
                components[TransformComponent::class.java]!!.apply {
                    setPosition(Vector3(0f, -0.23f, 0.3f))
                    scaleBy(0.6f)
                }
                content.addEntity(this)
            }
            // attachment entity: text
            attachments.entity("time")?.apply {
                components[TransformComponent::class.java]!!.apply {
                    setPosition(Vector3(0.38f, 0.4f, 0.23f))
                    scaleBy(5f)
                }
                robot.addChild(this)
            }
            // attachment entity: icon
            attachments.entity(ic_sui_rating_star)?.apply {
                components[TransformComponent::class.java]!!.apply {
                    setPosition(Vector3(-0.38f, 0.45f, -0.015f))
                    scaleBy(8f)
                }
                setName("star")
                robot.addChild(this)
            }
        },
        update = { content, _ ->
            currentTime.value = LocalTime.now().format(formatter)
            roll = (roll + 0.02f) % 360

            content.entities.firstOrNull()?.findEntity("star")?.apply {
                this.components[TransformComponent::class.java]!!.setEulerAngles(
                    EulerAngles(0f, 0f, roll)
                )
            }
        }
    )
}
```

The expected result is as follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cbf0ab1e79e24c13ac324de11acee771~tplv-goo7wpa0wc-image.image)
### Important notes
Key points to note about SpatialView are as follows:

* There is a `rootEntity` inside SpatialView that developers cannot access. Whenever you use `content.addEntity(entity)`, you are actually adding entity as a child node of the `rootEntity`.
* The origin of the SpatialView's coordinate space is at its geometric center, and the `rootEntity` is located at the origin (World Transform = Local Transform = 0). TransformComponent modifies the Local Transform, which is the transform relative to the parent node. After you use `content.addEntity(entityA)` to add an entityA to SpatiView, the Local Transform of that entityA becomes equivalent to its World Transform. Therefore, modifying the TransformComponent on entityA is equivalent to modifying the World Transform of entityA. If you again add entityB as a child node of entityA using `entityA.addChild(entityB)`, then when you modify entityB's TransformComponent, the modification is relative to entityA's Local Transform.
* The ID type of AttachmentPanel is `Any`. It is recommended to use `const val` or `enum class` to maintain the ID of AttachmentPanel.
* When SpatialView exits composition, its associated entity instances are not automatically destroyed. You need to manage them manually based on your use case.
   * **Reuse an entity through an external strong reference**: When you need to reuse the same entity (such as 3D models, game characters, and more) across SpatialView's lifecycle, you can achieve efficient reuse by using an external strong reference. For example, you can use a ViewModel or another external object to maintain a strong reference to the entity, allowing you to rebind it to a new SpatialView for reuse.
   * **Active destruction**: If you are certain that the entity instance will no longer be used, it is recommended to explicitly call `entity.destroy` within `DisposableEffect` to destroy the entity when unmounting the component.
      ```Kotlin
      DisposableEffect(Unit) {
          onDispose { 
              entity.destroy() // manually destroy unused entity instance
          }
      }
      ```

   * **Automatic recycling when not manually destroyed and no strong references exist**: The system monitors objects using a weak reference mechanism and destroys the relevant objects and reclaims the associated memory during the next garbage collection.
      This approach may result in delayed resource release, which can affect performance. Please use it carefully.

