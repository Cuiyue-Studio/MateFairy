This article describes how to drag-and-drop UI components in PICO OS 6 using Jetpack Compose's Modifier.
## Terminology
This article consistently uses the following terms to refer to various elements involved in the process of dragging and dropping UI components.

* **Drag layer**: The visual element rendered at the topmost layer of the screen during dragging, representing the content being dragged.
* **Drag shadow**: A visually simplified preview of the content being dragged, which is the main component of the drag layer.
* **Drop destination/target**: The area that can receive and process the dragged content.
* **Planar Container**: A 2D or 2.5D UI container, such as an application window.
* **WorldSpace**: The 3D space within the user's field of view, outside all application windows.

## Use case
The use case for drag-and-drop UI components includes dragging within a container, dragging across windows, and dropping into WorldSpace.
### Dragging within a container
Drag components within a single application window (Planar Container). The movement trajectory of the drag shadow is strictly parallel to the panel of its window. Depending on the drop position, the following two outcomes may occur:

* **Successful drop**: Dropping the component onto a valid drop destination enables successful data transfer.
* **Drop failure or cancellation**: If the component is released in an invalid area, the drag shadow and its projection will gradually fade out within 150 ms.

### Dragging across windows and in space
Drag UI components from one window to another, or move UI components within WorldSpace.
### Dropping into WorldSpace
Drag specific types of UI components into WorldSpace outside application windows to trigger specific actions. After releasing the component, the system will match a default Handler (first-party application) to process it based on the MIME Type of its content. The supported content types and Handlers are as follows:
| **Content type** | **MIME type example** | **Default handler (the application that is invoked)** | **Expected container type** |
| --- | --- | --- | --- |
| HTML | `text/html` | Browser | Planar |
| Image | `image/*` | Viewer | Planar |
| Video | `video/*` | Player | Planar |
| 3D model | `model/usdz`, `model/gltf` | Viewer | Volumetric |
| Plain text / other files | `text/plain`, `*/*` | None (not supported) | - |
## Implementation method
You can use the following Jetpack Compose Modifiers to implement drag-and-drop for UI components:

* [Modifier.dragAndDropSource](https://developer.android.com/reference/kotlin/androidx/compose/foundation/draganddrop/package-summary?hl=zh-cn#(androidx.compose.ui.Modifier).dragAndDropSource(kotlin.Function1)): Marks a Composable as a drag source.
* [Modifier.dragAndDropTarget](https://developer.android.com/reference/kotlin/androidx/compose/foundation/draganddrop/package-summary?hl=zh-cn#(androidx.compose.ui.Modifier).dragAndDropTarget(kotlin.Function1,androidx.compose.ui.draganddrop.DragAndDropTarget)): Marks a Composable as a drop destination.

### Mark a Composable as a drag source
You can use `Modifier.dragAndDropSource` to mark a Composable as a drag source. This Modifier provides two overloaded versions, with the main difference being the strategy for rendering the drag shadow.

* **Default drag shadow**: The system automatically captures the rendered content of the current Composable as the drag shadow. Suitable for standard UI elements or compact layouts.
* **Custom-drawn drag shadow**: Using the `DrawScope` callback, you can manually draw the drag shadow. Suitable for large touch areas with small content.

#### Default **drag shadow**
The following code demonstrates how to mark a Composable as a drag source using the default drag shadow.
```Kotlin
// Default drag-and-drop shadow strategy: the shadow size follows the component size
Modifier.dragAndDropSource(transferData: (Offset) -> DragAndDropTransferData?)

// In this case, the drag shadow size is fixed at 200dp, and is unrelated to the size of the content drawn inside the box
Box(
    modifier = modifier
        .size(200.dp)
        .dragAndDropSource(transferData = {data})
) {
    ...
}
```

#### Custom drag shadow
The following code demonstrates how to mark a Composable as a drag source using a custom drag shadow.
```Kotlin
// Custom drag shadow strategy
Modifier.dragAndDropSource(
    drawDragDecoration: DrawScope.() -> Unit,
    transferData: (Offset) -> DragAndDropTransferData?
)
Box(
    modifier = modifier
        .size(200.dp)
        .dragAndDropSource(
            transferData = {data},
            drawDragDecoration = {
                // Draw a custom image as the drag shadow; the shadow area remains the size of the box, but the displayed content is the developer's custom image
                drawDragDecoration = { drawImageAsShadow(customShadowBitMap) },
            }
        )
) {
    ...
}
```

For either overloaded version, `transferData` is a required parameter. You need to use `transferData` to return a `DragAndDropTransferData` object. This object contains `ClipData`, which is responsible for carrying the actual data being transferred, such as text, URIs, and more. In addition, you can use the `flags` parameter to control the permissions for data transfer. For example, setting `flags = View.DRAG_FLAG_GLOBAL` allows data to be transferred across applications (windows). By default, if this parameter is not set, data can only be received within the current window.
```Kotlin
transferData = {
    DragAndDropTransferData(
        ClipData.newPlainText("label", text),
        flags = View.DRAG_FLAG_GLOBAL
    )
}
```

### Mark a Composable as a drop target
You can use `Modifier.dragAndDropTarget` to mark a Composable as a drop target.
This Modifier provides a series of callbacks to respond to drag events:

* `onEntered`: Called when the dragged item enters the hot area.
* `onExited`: Called when the dragged item leaves the hot area.
* `onDrop`: Called when the dragged item is released on the target. The Boolean return value of this callback indicates whether the drop was successfully received. Returning `true` means you have successfully handled the event.
* `onEnded`: Called when the current drag event ends, regardless of success or failure.
* `shouldStartDragAndDrop`: Controls whether a drop target should respond to the current drag event.

#### Respond to drag events
You can use the `onEntered`, `onExited`, `onDrop`, and `onEnded` callbacks to respond to drag events.
```Kotlin
var isDragging by remember { mutableStateOf(false) }
var isHovering by remember { mutableStateOf(false) }
// Use remember to avoid repeatedly creating the target
val target = remember {
    object : DragAndDropTarget {
        // When drag-and-drop starts, mark isDragging as true
        override fun onStarted(event: DragAndDropEvent) {
            isDragging = true
        }
        // When drag-and-drop ends, restore the original state of the Box
        override fun onEnded(event: DragAndDropEvent) {
            isDragging = false
            isHovering = false
        }
        // When the drag-and-drop source enters the current target, trigger the hover effect
        override fun onEntered(event: DragAndDropEvent) {
            isHovering = true
        }
        // When the drag-and-drop source leaves the enter state, remove the hover effect
        override fun onExited(event: DragAndDropEvent) {
            isHovering = false
        }
    }
// By listening to drag-and-drop events and changing the state variable, the box displays different background colors at different stages of drag-and-drop, allowing the user to perceive the process
Box(
    modifier = modifier
        .size(200.dp)
        .dragAndDropTarget(
            // If shouldStartDragAndDrop = { false }, none of the event callbacks will be triggered
            // The box background color will always be Color.Gray.copy(alpha = 0.2f)
            shouldStartDragAndDrop = { true },
            target = target,
        )
        .background(
            color = if (isHovering) Color.Green.copy(alpha = 0.5f) else if (isDragging) Color.Yellow.copy(
                alpha = 0.5f
            ) else Color.Gray.copy(alpha = 0.2f)
        ),
    contentAlignment = Alignment.Center
) 
```

#### Control whether to respond to the current drag event
You can use the `shouldStartDragAndDrop` callback to control whether a drop target should respond to the current drag event.

* If `true` is returned, it means the target accepts this drag-and-drop operation, and other callbacks in its `DragAndDropTarget` (such as `onEntered` and `onDrop`) will also be triggered as expected.
* If `false` is returned, the target will ignore this drag-and-drop operation. In this case, none of the target's other callbacks will be triggered before the `onEnded` event occurs.

This setting only affects the current component and does not affect other components on the page that return `true`. Whenever a new drag-and-drop event starts, the system will call `shouldStartDragAndDrop` again to make a determination.
```Kotlin
shouldStartDragAndDrop = { startEvent ->         
    // 1. Check whether the dragged data contains a URI (path)
    val hasUri = startEvent
        .mimeTypes()
        .contains(ClipDescription.MIMETYPE_TEXT_URILIST)                  
    // 2. If there is a URI, I am interested in this drag-and-drop operation and return true to enable subsequent event listening
    // 3. If not (for example, the user drags plain text), return false, and subsequent onEntered/onDrop will not be triggered
    hasUri      
}
```

## API reference
For details, refer to the Jetpack Compose developer documentation on drag-and-drop UI components:

* [Modifier.dragAndDropSource](https://developer.android.com/reference/kotlin/androidx/compose/foundation/draganddrop/package-summary?hl=zh-cn#(androidx.compose.ui.Modifier).dragAndDropSource(kotlin.Function1))
* [Modifier.dragAndDropTarget](https://developer.android.com/reference/kotlin/androidx/compose/foundation/draganddrop/package-summary?hl=zh-cn#(androidx.compose.ui.Modifier).dragAndDropTarget(kotlin.Function1,androidx.compose.ui.draganddrop.DragAndDropTarget))
