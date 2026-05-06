In the ECS architecture, besides displaying a 3D entity within a 2D layout, it is sometimes necessary to mount a 2D panel directly onto a 3D entity for display. By using `AttachmentPanelComponent`, you can add a 2D panel as a child node or as a component of a 3D entity, giving it spatial properties and enabling unified management.
## Prerequisites
2D panels are generally built using SpatialUI, so you need to ensure that SpatialUI-related dependencies have been added to the project. For details, refer to "[Dependency configuration](/project-structure-and-dependency-configuration)".
## Basic usage: Mount a 2D panel onto a 3D entity
Here is a typical scenario: rendering a 3D model within the Stage, while mounting a 2D panel near the model to display a line of text, allowing the 2D panel to move, scale, and rotate together with the 3D entity. There are two main implementation methods: encapsulate the 2D panel as an `AttachmentPanelComponent` and mount it onto the entity as a component, or use the `SpatialView` attachments interface (`attachments`) to create a panel entity and mount it as a child node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/951650862e824622a2c3e54b27ee36c4~tplv-goo7wpa0wc-image.image)
### **Method 1: Use** AttachmentPanelComponent
Use `AttachmentPanelComponent` to mount a 2D panel onto a 3D entity:

* Build the 2D panel with `attachmentPanelComponent(...) { ... }`, using the same syntax as standard Compose 2D UI.
* Define the panel size using `attachmentSize()`.
* Mount the returned `components.set(attachment)` onto the target 3D entity, so it follows the entity.

The code example is as follows:
```Kotlin
@Composable
fun SimplePanelComponent() {
    // Use AttachmentPanelComponent to build a 2D panel component that can be mounted to a 3D entity, and set the panel size
    val attachment = attachmentPanelComponent(size = panelSize(200.dp, 200.dp)) {
        // 2D UI content inside the panel: written in the same way as a regular Compose interface
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
        // Load the 3D entity (on the IO thread to avoid blocking the main thread）
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the transformation (position/scale) of the 3D entity
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Mount the 2D panel component onto the 3D entity so that it follows the entity
            it.components.set(attachment)
        }
        // Add the 3D entity to the scene to complete rendering
        content.addEntity(entity)
    }
}
```

### Method 2: Use the attachments interface of SpatialView
Use the `SpatialView` attachments interface (`attachments`) to create a panel entity and mount it onto the target 3D entity:

* Define the 2D panel in `SpatialView(attachments = { ... })` using `AttachmentPanel()`.
* In the rendering callback, obtain the corresponding panel entity via `attachment.entity()`, and use the `addChild()` method to mount it onto the target 3D entity.

The code example is as follows:
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
        // Load the 3D model (on the IO thread to avoid blocking the main thread)
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the transformation (position/scale) of the 3D entity
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Retrieve the corresponding panel entity from attachments by id and mount it under the 3D entity to enable following
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
### Organize the entry point for 2D content within the Stage
Within the Stage, you can organize the main 2D UI entry and the main 3D content under the same container entity. This allows you to use a single entity to control the position, scaling, and hierarchy of both 2D and 3D content, making it easier to move or replace content as a whole later.
The code example is as follows:
```Kotlin
Stage(StageName) {
    val viewModel = AmountsOfPanelsViewModel.instance()
    // Main 2D content entry: build a panel component that can be mounted to 3D entity
    // Assume StageMainPanel is the Composable method you use to build the main 2D content in the Stage
    val stageMainPanel = StageMainPanel()
    // Add the entity to the scene in SpatialView
    SpatialView { content, _ ->
        content.addEntity(
            Entity().apply {
                // Set the spatial position of the container entity; both 2D and 3D content will follow this transformation
                position(Vector3(0.5f, 1.5f, -1.0f))
                // Mount the main 2D panel onto this entity
                components.set(stageMainPanel)
                // Main 3D content: Attach as a child node under the same container entity
                addChild(viewModel.panelZygote)
            }
        )
    }
}
```

### Use a system to drive dynamic refresh of the 2D panel
Link the content of the 2D panel with system-driven state updates, so the 2D panel can update in real time based on ECS state. The core logic is as follows:

* Register or unregister the system in `@Composable` using `DisposableEffect`;
* Mount both `AttachmentPanelComponent` (responsible for rendering the 2D UI) and a custom component (responsible for storing state) onto the 3D entity;
* Update the state at each time step in the custom `System.update()`, and write the latest state back to the panel content via `attachmentPanelComponent.content { ... }`, thereby achieving the separation of "logic in System, presentation in Panel".

The following is a code example:
```Kotlin
@Composable
fun SystemUpdatePanelComponent() {
    // Register/unregister the system within the Compose lifecycle to ensure it is active when entering the page and released when exiting
    DisposableEffect(Unit) {
        registerSystem<TrafficLightSystem>()
        onDispose { unregisterSystem<TrafficLightSystem>() }
    }
    // Build the 2D panel component (its content will be dynamically updated by the system later)
    val attachmentPanelComponent = attachmentPanelComponent { Text("Traffic Light") }
    // Create the scene and add an entity with both panel rendering capability and business state
    SpatialView { content, _ ->
        content.addEntity(
            Entity().apply {
                // Attach the panel component: Used to display 2D UI
                components.set(attachmentPanelComponent)
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

    // Advance the logic: decrement the countdown by 1; when it reaches 0, switch the traffic light and reset the duration
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
    // Accumulate time to convert per-frame updates into "update once every second"
    var timeInterval = 0f

    override fun update(context: SceneUpdateContext) {
        // Accumulate frame interval time (deltaTime is the time spent per frame, unit: seconds)
        timeInterval += context.deltaTime
        // Perform state advancement and UI refresh once every second
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
                    // Write the latest state back to the panel content (countdown + background color)
                    it.components.get<AttachmentPanelComponent>()?.content {
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
        // Delegate to the base class to continue processing the system update chain
        super.update(context)
    }
}
```

### Set alignment
When mounting a 2D panel to a 3D entity, you can use the `alignment` property of `AttachmentPanelComponent`  to align the 3D entity with a specific position of the panel. Currently, nine positions are supported: the four corners of the panel, the midpoints of the four edges, and the center of the panel.
By adjusting the alignment, you can control the positional relationship between the 3D entity and the panel when the panel appears, avoiding occlusion or ensuring the visual layout meets expectations.
The following code example demonstrates how to align a 3D entity to the top-left corner of the panel:
```Kotlin
@Composable
fun SimplePanelComponentWithAlignment() {
    // Build a 2D panel component that can be attached to a 3D entity, and specify the panel size and alignment
    val attachment = attachmentPanelComponent(
        size = panelSize(1f, 0.5f),
        // Specify the alignment method: Align the entity with the panel's top-left corner when attaching the panel
        alignment = AttachmentPanelComponent.Alignment.TOP_LEFT,
    ) {
        // 2D UI content inside the panel (standard Compose approach)
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
        // Load the 3D model (move to the IO thread to avoid blocking the main thread)
        val entity = withContext(Dispatchers.IO) {
            Entity.load("asset://model/pico_robot_static.usdz")
        }.also {
            // Configure the 3D entity's transformation (position/scale)
            it.components.get<TransformComponent>()?.apply {
                position = Vector3(0f, 1f, -1f)
                scaleBy(0.5f)
            }
            // Attach the panel component to the 3D entity; alignment determines how the entity and panel are aligned
            it.components.set(attachment)
        }
        // Add the 3D entity to the scene to complete rendering
        content.addEntity(entity)
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/65700e2d3dc54fd5beac447ac8657256~tplv-goo7wpa0wc-image.image)
## Caution

* After changing the `content` of `AttachmentPanelComponent` , the created view will not be updated automatically. To make the change take effect, you need to reset the `content` of`Composable` .
* It is usually not necessary to explicitly set the size of the 2D panel. By default, the panel automatically adapts to the size of its content.
* When the size of the 2D panel needs to differ from the content size, use `requiredSize` to explicitly specify the size of the content.
* By default, the center of the 2D panel coincides with the origin of the target 3D entity.


