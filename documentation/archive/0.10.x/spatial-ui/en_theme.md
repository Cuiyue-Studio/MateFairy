PicoTheme is used to define the appearance style of Spatial Apps, including influencing the default behavior of each component.
## Introduction to the topic
A theme includes four configuration items: color, typography, general, and system materials.
### Theme color
Theme colors (ColorTheme) include the following types:

* **Semantic color** defines colors with conventional, easily understood meanings, such as warning, error, and more.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/683f30d9a4474301a1fd2261e467868e~tplv-goo7wpa0wc-image.image)
* **Accent color** defines the theme color of the system or application.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a4969dff6d214e888a9e728842076a11~tplv-goo7wpa0wc-image.image)
* **Background color** defines the background color of containers such as windows, sheets, dialogs, and more.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1c0dc6eafa5f45729122021a29fa51f8~tplv-goo7wpa0wc-image.image)
* **Forecolor** defines the foreground color for font or element fill.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5774817d96a0462cbbb314e4a0bc0853~tplv-goo7wpa0wc-image.image)
* **Sub color** defines auxiliary visual colors for the system, such as colors for labels, icons, and more.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/84c85e727d5c47058a00aa1f3487c035~tplv-goo7wpa0wc-image.image)

**Role**
The two roles you can modify: Accent, On Accent. After modifying the parameters for the corresponding role, the system will synchronize the color changes across all components that use that role. Affected components include Snack, TabBar, Button, Cursor, and more.
| **Role name** | **Note** |
| --- | --- |
| Accent | It is used as the background color for small areas and important elements, for example, the background color of a Button or a Snack. |
| On Accent | When using foreground colors on Accent, ensure visibility. |
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f673c5a4a55c4a91b5d714edf0eba890~tplv-goo7wpa0wc-image.image)
### Text layout
To address different content display requirements, the theme defines a set of text layout rules (Typography) that are applied to the corresponding components. The definitions are as follows:
| **Style name** | **Usage scenarios** |
| --- | --- |
| **DisplayLarge** <br> **DisplayMedium** <br> **DisplaySmall** | Decorative heading <br>  |
| **HeadlineLarge** <br> **HeadlineMedium** <br> **Headlinesmall** | The container page title |
| **TitleLarge** <br> **TitleMedium** <br> **TitleSmall** | Module title <br>  |
| **LabelLarge** <br> **LabelMedium** <br> **LabelSmall** | Commonly used for Action, such as the text inside a Button <br>  |
| **BodyLarge** | Commonly used for entering copy and body text with few lines (no more than five lines). |
| **BodyMedium** | Single-line body text |
| **BodySmall** | Commonly used for supporting copy, providing supplementary information, or explaining functionality |
### General
For interactive components, such as Buttons and similar components, a universal (State) configuration provides users with a consistent interactive visual experience. General configuration can be layered on top of the base style. The configuration includes the following states:
| **Status** | **Description** |
| --- | --- |
| Hover | When the controller ray moves into a component, highlight the layer and indicate that the component is interactive. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/31b68ea39084493dbb8db5b70727f7f7~tplv-goo7wpa0wc-image.image) |
| Pressed | When the user presses the controller or other interactive device, a different highlight color layer is used to indicate the Press state, prompting the user with "An interaction is occurring." <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/342ab06b16134e17845ca11922761374~tplv-goo7wpa0wc-image.image) |
| Enable & Disabled | Inform the user that this component is not interactive. <br> ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cdb0c69325d44ef482677a0bd416fb16~tplv-goo7wpa0wc-image.image) |
### System material
System material is primarily used in spatial pop-up windows such as Dialog, Menu, ToolBar, and more to achieve a consistent visual effect.
Currently, the system layer features a relatively simple effect (a white background), while the Menu component has integrated a material effect.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/83ded3ef1efd4aebbf227efb56d1abe2~tplv-goo7wpa0wc-image.image)
| **Material name** | **Usage scenarios** |
| --- | --- |
| MaterialRegular | Background color of the bottommost container. For example: Subwindow, Augment, TabBar, and ToolBar components |
| MaterialThick | Background color for large-area floating containers. For example: components such as Alert Dialog, Sheet, and Menus. |
| MaterialThickest | Background color for floating small-area containers. For example: the TextSelectionAndToolbarProvider component |
Example scenarios
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/543ca0bf1a9c42919dd7f05adc58bda2~tplv-goo7wpa0wc-image.image)
## Use themes
It is recommended to add the `PicoTheme` function to the root node of the content in the `WindowContainer`. The UI inside the `WindowContainer` will apply theme-related configurations.
```Kotlin
WindowContainer(id = "Window1") {
    // Use PicoTheme in WindowContainer DSL
    PicoTheme {
        Content()
    }
}
```

`PicoTheme` leverages Compose's [CompositionLocal](https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal) mechanism to propagate theme configuration down the Compose View Tree.
In any child node of `PicoTheme`, you can quickly access the theme system’s redefined colors and typography through the `PicoTheme` object.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/34ff0debfae3460fb2dc4ee141d08c70~tplv-goo7wpa0wc-image.image)
### Use theme colors and text formatting
By default, the built-in components of the SpatialUI component library have overridden the theme configuration. When your custom UI needs to remain consistent with the theme, you can refer to the following examples:
```Kotlin
@Composable
fun Demo() {
    Box(modifier = Modifier.size(200.dp)
        // Use the theme color as the background; when the theme changes, it refreshes automatically
        .background(PicoTheme.colorScheme.accent)
    ) {
        Text(
            "Text For Theme",
            // Uses the theme color for content. When the theme changes, the content color updates accordingly.
            color = PicoTheme.colorScheme.onAccent,
            // Use the theme's text style; it will refresh when the theme changes
            style = PicoTheme.typography.bodyMedium
        )
    }
}
```

### Use a general status
SpatialUI uses the Compose [Indication](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/handling-interactions?hl=zh-cn#consume-emit) mechanism to handle interaction states, and leverages the [CompositionLocal](https://developer.android.com/reference/kotlin/androidx/compose/runtime/CompositionLocal) mechanism to ensure the consistency of interaction states across all components within PicoTheme.

* The SpatialUI component library has been adapted for interaction states, so you do not need to take any action.
* By default, the `Modifier.clickable` modifier automatically adapts to the interaction state through `LocalIndication`. When your custom component has a background shape, please use it in combination with [clip](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier#(androidx.compose.ui.Modifier).clip(androidx.compose.ui.graphics.Shape)).

```Kotlin
@Composable
fun Demo() {
    Box(modifier = Modifier
        // Optional, if you need to define a background shape.
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White)
        // Do not define shapes using this method
        // .background(Color.White, RoundedCornerShape(10.dp))
        .clickable {  }
    ) {
    }
}
```

### Use the general disable state
In the PICO standard, a component's disabled state is achieved by adjusting its transparency. When custom components need to use the PICO standard specification, you may refer to the following code:
```Kotlin
@Composable
fun Demo(enable: Boolean = true) {
    // Get opacity based on status
    val alpha = if(enable) {
        1f
    } else {
        // Get from CompositionLocal
        LocalDisableAlpha.current
    }
    Box(modifier = Modifier
        .graphicsLayer {
            // Set opacity
            this.alpha = alpha
        }
        .background(Color.Red)
    ) {
    }
}
```

## Custom themes
### Customize theme colors and typography
While maintaining design specifications, PicoTheme allows a certain degree of flexibility for your custom themes.
```Kotlin
WindowContainer(id = "Window1") {
    // Use PicoTheme in WindowContainer DSL
    PicoTheme(
        // Custom theme color
        colorScheme = PicoTheme.colorScheme.copy(
            accent = xx,
        ),
        Customize text layouts
        typography = PicoTheme.typography.copy(
            displayMedium = xxx
        )
    ) {  }
}
```

### Custom interaction states
You can achieve your own interaction state by [customizing Indication](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/handling-interactions?hl=zh-cn#replace-effect), and then use `CompositionLocalProvider` to replace the Indication for the node.
```Kotlin
Custom Indication
object MyIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return xxx
    }
}

@Composable
fun Demo() {
    // Replace with custom Indication
    CompositionLocalProvider(LocalIndication provides MyIndication) {
        // content
    }
}
```

### Customize Disable transparency
```Kotlin
@Composable
fun Demo() {
    CompositionLocalProvider(LocalDisableAlpha provides 0.5f) {
        // content
    }
}
```


