Chip is a control commonly used in label display scenarios under the PICO design specification. Based on differences in style and functionality, the Chip includes ButtonChip, ToggleableChip, and RemoveableChip.
## ButtonChip
ButtonChip is a control designed under the PICO design specification for displaying labels, typically used to provide content and respond to label clicks in label display scenarios.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f576788059e3446aa87451e7e95ab075~tplv-goo7wpa0wc-image.image)
### API Surface

* `label`: The current text content of the ButtonChip. This is a composable.
* `onClick`: Click event. This callback is triggered when ButtonChip is clicked.
* `leadingIcon`: Can be used to customize the content displayed on the left side of ButtonChip by adding a control. By default, nothing is displayed.
* `trailingIcon`: Can be used to customize the controls you add and configure what is displayed on the right side of ButtonChip. By default, nothing is displayed.
* `enabled`: Determines whether user hover gestures are responded to; boolean value. The default value is true, and users see the default hover effect when hovering over the ButtonChip control.
* `labelTextStyle`: Controls the text style for `label`. The default is `PicoTheme.typography.labelLarge`.
* `colors`: Used to provide the background colors for the current `label`, `leadingIcon`, `trailingIcon`, and ButtonChip. By default, these colors are provided by `ChipsDefaults.chipColors`, but you can also supply custom colors through `ChipsDefaults.chipColors`.
* `chipSize`: This parameter controls the size of ButtonChip. By default, the style is `ChipsDefaults.Small`.
* `interactionSource`: Used to listen for changes in the ButtonChip interaction state. You can provide a custom `MutableInteractionSource` to monitor interaction states of the control, such as pressed, focused, and more.

### Basic usage
```Kotlin
@Composable
fun ButtonChipSample() {
    Column {
        Display labels
        ButtonChip(label = {
          Text("Chip")
        }, onClick = {})
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/712718a41242412db5e343c172e8e87e~tplv-goo7wpa0wc-image.image)
### **Advanced usage**
Customize ButtonChip by customizing `leadingIcon`, `trailingIcon`, `chipSize`, and more to achieve a wide range of ButtonChip display effects.
```Kotlin
@Composable
fun ButtonChipsDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        ButtonChip(
            label = {
             Text("Complex")
            },
            onClick = {},
            chipSize = ChipsDefaults.Small,
            An icon is displayed on the left side.
            leadingIcon = {
                AnyIcon(iconSize = 12.dp)
            },
            // Text is also displayed on the right side
            trailingIcon = {
                Text("Right Text")
            },
            // You can customize the label color and background color
            colors = ChipsDefaults.chipColors(Color.Yellow, Color.Black)
        )
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3f2497d8dc35467b82d4eb56a7fc0846~tplv-goo7wpa0wc-image.image)
## ToggleableChip
ToggleableChip is a label used for toggling states under the PICO design guidelines. Unlike ButtonChip, ToggleableChip provides default indicators for both selected and unselected states.
### API Surface

* `label`: The current text content of ToggleableChip. This is a composable.
* `isToggleOn`: Indicates whether the ToggleableChip is currently selected.
* `onClick`: Click event. This callback is triggered when ToggleableChip is clicked.
* `leadingIcon`: Can be used to add custom controls and to configure the content displayed on the left side of ToggleableChip. Not displayed by default.
* `trailingIcon`: Can be used to add custom controls and configure the content displayed on the right side of ToggleableChip; by default, nothing is displayed.
* `enabled`: Whether to respond to user hover gestures. Boolean value. The default value is true, which enables the default hover effect when the user hovers over the ToggleableChip control.
* `labelTextStyle`: Used to control the text style of `label`; the default is `PicoTheme.typography.labelLarge`.
* `colors`: Provides the colors for the current `label`, `leadingIcon`, `trailingIcon`, as well as the selected and unselected states of ToggleableChip, and the background color of ToggleableChip. By default, these colors are supplied by `ChipsDefaults.toggleableChipColors`, but you can also pass custom colors through `ChipsDefaults.toggleableChipColors`.
* `chipSize`: Controls the size of ToggleableChip. By default, the style is `ChipsDefaults.Small`.
* `interactionSource`: Used to monitor changes in the interaction state of a ToggleableChip. You can provide a custom `MutableInteractionSource` to listen for interaction states such as pressed, focused, and other interaction states.

### Basic usage
```Kotlin
@Composable
fun ToggleableChipSample() {
    Column(modifier = Modifier.padding(start = 12.dp)) {
        var selected by remember { mutableStateOf(false) }
        Display of the selected state
        ToggleableChip(
            label = {
              Text("Chip 1")
            },
            isToggleOn = selected,
            onClick = { selected = !selected },
        )

        // Display when not selected
        ToggleableChip(
            label = {
              Text("Chip 2")
            },
            isToggleOn = !selected,
            onClick = { selected = !selected },
            )
    }
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/644e1c9b406843f0864e8590913f4b5f~tplv-goo7wpa0wc-image.image)
### **Advanced usage**
Customize ToggleableChip by configuring `leadingIcon`, `trailingIcon`, `chipSize`, `colors`, and more to achieve a wide range of ToggleableChip display effects.
```Kotlin
@Composable
fun ToggleableChipSample(){
    var selected by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        ToggleableChip(
            label = {
               Text("Complex")
            },
            isToggleOn = selected,
            onClick = {selected = !selected},
            chipSize = ChipsDefaults.Small,
            An icon is displayed on the left side.
            leadingIcon = {
                AnyIcon(iconSize = 12.dp)
            },
            // Text is also displayed on the right side
            trailingIcon = {
                Text("Right Text")
            },
            // You can customize the label color and background color, and set the selected label color to Color.Red and the selected background color to Color.Blue.
            colors = ChipsDefaults.toggleableChipColors(Color.Yellow, Color.Black,Color.Red,Color.Blue)
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b94b507a5b124c91a46f8f5ea57c36ed~tplv-goo7wpa0wc-image.image" width="5120px" /></div>

## RemovableChip
RemovableChip is a dynamic label, defined by the PICO design specification, that can be displayed or deleted and allows customization of the "delete" style.
### API Surface

* `label` : The current text content of RemovableChip. This is a composable.
* `onLeadingClick`: Callback for click events when clicking the `leadingIcon`, `label`, or other elements in the leading area.
* `onTrailingRemoveClick`: Event callback triggered when the right-side close button is clicked.
* `visible`: Controls whether the current RemovableChip is visible.
* `leadingIcon`: Can be used to customize and add controls, configuring the content displayed on the left side of RemovableChip; not displayed by default.
* `enabled`: Indicates whether the system responds to the user's hover gestures; Boolean value. The default value is true, and users see the default hover effect when hovering over the RemovableChip control.
* `labelTextStyle`: Used to control the text display style of `label`. The default value is `PicoTheme.typography.labelLarge`.
* `colors`: Used to provide the background color for the current `label`, `leadingIcon`, and RemovableChip. By default, the color is provided by `ChipsDefaults.chipColors`, but you can also provide custom colors through `ChipsDefaults.chipColors`.
* `chipSize`: Used to control the size of RemovableChip. By default, the style is `ChipsDefaults.Small`.
* `contentPadding`: The inner padding for the content displayed by RemovableChip. You can customize the horizontal and vertical padding by passing a `PaddingValues` object.
* `contentGap`: Used to set the spacing between `leadingIcon` and `label`.
* `shape`: Used to set the shape style of RemovableChip; the internal content will be displayed with the shape applied.
* `interactionSource`: Used to listen for changes in the interaction state of RemovableChip. You can provide a custom `MutableInteractionSource` to monitor interaction states such as pressed and focused for the control.

### Basic usage
```Kotlin
@Composable
fun RemovableChipDemo(){
    var visible by remember {
        mutableStateOf(true)
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        RemovableChip(
            label = {
             Text("RemovableChip")
            },
            // Hide RemovableChip after clicking the close button on the right
            onTrailingRemoveClick = {
                visible = !visible
            },
            // Hide RemovableChip after clicking the close button on the right
            onLeadingClick = {
                visible = !visible
            },
            visible = visible
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2fd3dd362d274541b8f2b1de620a1e4b~tplv-goo7wpa0wc-image.image" width="5120px" /></div>

### **Advanced usage**
Customize RemovableChip by customizing `leadingIcon`, `contentPadding`, `contentGap`, `shape`, and other properties to achieve a wide range of RemovableChip display effects.
```Kotlin
@Composable
fun RemovableChipDemo(){
    var visible by remember {
        mutableStateOf(true)
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        RemovableChip(
            label = {
               Text("RemovableChip")
            },
            // Hide RemovableChip after clicking the close button on the right
            onTrailingRemoveClick = {
                visible = !visible
            },
            // Hide RemovableChip after clicking the close button on the right
            onLeadingClick = {
                visible = !visible
            },
            Set the icon displayed on the left side
            leadingIcon = {
                AnyIcon(iconSize = 12.dp)
            },
            // Set the spacing between label and leadingIcon to 10dp
            contentGap = 10.dp,
            // Set horizontal and vertical padding to 10dp
            contentPadding = PaddingValues(10.dp,10.dp),
            visible = visible
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50b632542a8742c3b764ae2e4e456257~tplv-goo7wpa0wc-image.image" width="5120px" /></div>



