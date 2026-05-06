In ECS architecture, besides displaying a 3D entity within a 2D layout, there are cases where you need to directly attach a 2D panel to a 3D entity for display. By using `PanelComponent`, you can add a 2D panel as a child node or component of a 3D entity, giving it spatial properties and enabling unified management.
## Prerequisites
2D panels are generally built using SpatialUI, so you need to ensure that the project has added the relevant SpatialUI dependencies. For more information, refer to "[Dependency configuration](/project-structure-and-dependency-configuration)".
## Basic usage: Attach a 2D panel to a 3D entity
The following is a typical scenario: render a 3D model within the Stage, and attach a 2D panel near the model to display a line of text, allowing the 2D panel to move, scale, and rotate together with the 3D entity. There are two main implementation methods: encapsulate the 2D content as a `PanelComponent` and attach it to the entity component, or use the `attachments` related interfaces of `SpatialView` to create a panel entity and attach it as a child node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/951650862e824622a2c3e54b27ee36c4~tplv-goo7wpa0wc-image.image)
### **Method 1: Use** **`PanelComponent`**
Use `PanelComponent` to attach a 2D panel to a 3D entity:

* Use `panelComponent(...) { ... }` to build a 2D panel, following the same approach as a standard Compose 2D interface.
* Define the panel's size using `attachmentSize()`.
* Attach the returned `components.set(attachment)` onto the target 3D entity, allowing it to follow that entity.

The code sample is as follows:
```Kotlin
@Composable
fun SimplePanelComponent() {
    // Use PanelComponent to build a 2D panel component that can be attached to a 3D entity, and set the panel size
    val attachment = panelComponent (size = attachmentSize(1f, 1f)) {
        // 2D UI content inside the panel: The code is the same as for a regular Compose interface.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hi, I'm PICO bot",
                color = Color.White,
                fontSize = 64.sp,
                modifier = Modifier.background(Color.Blue),
            )
        }
    }
    SpatialView(
        modifier = Modifier.fillMaxSize()
    ) { content, _ ->
        // Load the 3D entity (run on the IO thread to avoid blocking the main thread)
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the 3D entity's transform (position/scale)
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Attach the 2D panel component to the 3D entity so it follows the entity
            it.components.set(attachment)
        }
        // Add the 3D entity to the scene to complete rendering
        content.addEntity(entity)
    }
}
```

### Method 2: Use the attachments interface of SpatialView
Use the `attachments` related interfaces of `SpatialView` to create a panel entity and attach it to the target 3D entity:

* In `SpatialView(attachments = { ... })`, use `AttachmentPanel()` to define the 2D panel.
* In the rendering callback, retrieve the corresponding panel entity via `attachment.entity()`, and use the `addChild()` method to attach it to the target 3D entity.

The code sample is as follows:
```Kotlin
@Composable
fun SimpleSpatialViewAttachment() {
    SpatialView(
        modifier = Modifier.fillMaxSize(),
        // Declare the 2D panel content in attachments and assign a unique id
        attachments = {
            AttachmentPanel(1) {
                Text(
                    text = "Hi, I'm PICO bot",
                    color = Color.White,
                    fontSize = 64.sp,
                    modifier = Modifier.background(Color.Blue),
                )
            }
        }
    ) { content, attachment ->
        // Load the 3D model (run on the IO thread to avoid blocking the main thread)
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the 3D entity's transform (position/scale)
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Use the id to get the corresponding panel entity from attachments and attach it to the 3D entity to enable following
            attachment.entity(1)?.let { attachment ->
                it.addChild(attachment)
            }
        }
        // Add the entity to the scene to complete rendering
        content.addEntity(entity)
    }
}
```

## Advanced usage
### Organize the 2D content's entry within the Stage
Within the Stage, you can organize the main 2D interface's entry and the main 3D content under the same container entity. This allows a single entity to uniformly control the position, scaling, and hierarchy of both 2D and 3D content, making it easier to move or replace content as a whole later.
The following is a code sample:
```Kotlin
Stage(StageName) {
    val viewModel = AmountsOfPanelsViewModel.instance()
    // Main 2D content entry: build the main panel component that can be attached to 3D entity
    // Assume that StageMainPanel is the composable function you use to build the primary 2D content within the Stage
    val stageMainPanel = StageMainPanel()
    // Add entity to the scene in SpatialView
    SpatialView { content, _ ->
        content.addEntity(
            Entity().apply {
                // Set the spatial position of the container entity; both 2D and 3D content will follow this transform
                position(Vector3(0.5f, 1.5f, -1.0f))
                // Attach the main 2D panel to this entity
                components.set(stageMainPanel)
                // Main 3D content: Attach as a child node under the same container entity
                addChild(viewModel.panelZygote)
            }
        )
    }
}
```

### Use a system to drive dynamic refresh of the 2D panel
Link the 2D panel's content with system-driven state updates, enabling the 2D panel to update in real time with ECS state changes. The core logic is as follows:

* Register/unregister the system in `@Composable` using `DisposableEffect`;
* Attach both `PanelComponent` (responsible for rendering the 2D UI) and a custom component (responsible for storing state) to the 3D entity;
* In the custom `System.update()`, update the state by time step, and use `PanelComponent.content { ... }` to write the latest state back to the panel's content, achieving the separation of "logic in system, presentation in panel."

The following is a code sample:
```Kotlin
@Composable
fun SystemUpdatePanelComponent() {
    // Register/unregister the system within the Compose lifecycle to ensure activation when entering the UI and release when exiting the UI
    DisposableEffect(Unit) {
        registerSystem<TrafficLightSystem>()
        onDispose { unregisterSystem<TrafficLightSystem>() }
    }
    // Build the 2D panel component (its content will be dynamically updated by the system later)
    val panelComponent = panelComponent { Text("Traffic Light") }
    // Create the scene and add an entity to the scene that has both panel rendering capability and state
    SpatialView { content, _ ->
        content.addEntity(
            Entity().apply {
                // Attach the panel component: Used to display the 2D UI
                components.set(panelComponent)
                // Attach the state component: Used to store the traffic light state and countdown
                components.set(TrafficLightComponent())
            }
        )
    }
}

class TrafficLightComponent : Component() {
    // Whether the current light is green
    var greenLight = true
    // Current countdown (unit: seconds)
    var countdownTime = 10

    // Advance the logic once: countdown -1; when it reaches 0, switch the traffic light and reset the duration
    fun tick() {
        countdownTime--
        if (countdownTime <= 0) {
            greenLight = !greenLight
            countdownTime = if (greenLight) 10 else 5
        }
    }

    override fun toString(): String {
        return "TrafficLightComponent(greenLight=$greenLight, countdownTime=$countdownTime)"
    }
}

class TrafficLightSystem : System() {
    // Accumulate time to convert per-frame updates into 'update once every 1 second'
    var timeInterval = 0f

    override fun update(context: SceneUpdateContext) {
        // Accumulate frame interval time (deltaTime is the time consumed per frame, unit: seconds)
        timeInterval += context.deltaTime
        // Every 1 second, perform a state update and UI refresh
        if (timeInterval >= 1) {
            timeInterval = 0f
            // Query all entities with the TrafficLightComponent attached
            context.scene
                .queryEntity(EntityQueryCondition.hasComponent(TrafficLightComponent::class.java))
                .forEach {
                    // Read and update the state component
                    val trafficLightComponent =
                        it.components.get<TrafficLightComponent>() ?: return@forEach
                    trafficLightComponent.tick()
                    // Write the latest state back to the panel's content (countdown + background color)
                    it.components.get<PanelComponent>()?.content {
                        Text(
                            text = "${trafficLightComponent.countdownTime}",
                            fontSize = 64.sp,
                            color = Color.White,
                            modifier =
                                Modifier.background(
                                    if (trafficLightComponent.greenLight) Color.Green else Color.Red
                                ),
                        )
                    }
                }
        }
        // Pass to the base class to continue processing the system update chain
        super.update(context)
    }
}
```

### Set alignment
When attaching a 2D panel to a 3D entity, you can use the `PanelComponent`'s `alignment` property to align the 3D entity with a specific position on the panel. Currently, nine positions are supported: the four corners of the panel, the midpoints of the four edges, and the center of the panel.
By adjusting the alignment, you can control the relative position of the 3D entity to the panel when it appears, avoiding occlusion or making the visual layout more in line with expectations.
The following code sample shows how to align a 3D entity to the top-left corner of the panel:
```Kotlin
@Composable
fun SimplePanelComponentWithAlignment() {
    // Build a 2D panel component that can be attached to a 3D entity, and specify the panel's size and alignment position
    val attachment = panelComponent(
        size = panelSize(1f, 0.5f),
        // Specify the alignment method: Align the entity attaching this panel to the top-left corner of the panel
        alignment = PanelComponent.Alignment.TOP_LEFT,
    ) {
        // 2D UI content inside the panel (standard Compose syntax)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(16.dp))
                .background(Color.Cyan),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hi, I'm PICO bot",
                color = Color.White,
                fontSize = 64.sp,
            )
        }
    }
    SpatialView(
        modifier = Modifier.fillMaxSize()
    ) { content, _ ->
        // Load the 3D model (place on the IO thread to avoid blocking the main thread)
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the 3D entity's transform (position/scale)
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Attach the panel component to the 3D entity; `alignment` will determine how the entity aligns with the panel
            it.components.set(attachment)
        }
        // Add the 3D entity to the scene to complete rendering
        content.addEntity(entity)
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/65700e2d3dc54fd5beac447ac8657256~tplv-goo7wpa0wc-image.image)
## Important notes

* Changes to the `content` of `PanelComponent` will not automatically update the created view. To make updates take effect, you need to reset the `content` of `Composable`.
* It is usually unnecessary to explicitly set the size of the 2D panel. By default, the panel automatically adapts to the size of its content.
* When the size of the 2D panel needs to differ from the content size, use `requiredSize` to explicitly specify the size of the content.
* By default, the center of the 2D panel coincides with the origin of the target 3D entity.
