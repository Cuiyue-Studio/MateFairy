ToggleButton is a control defined by the PICO design guidelines that features a secondary state and responds to user click interactions. Its content area typically contains `Text` or other composable items. It is commonly used in scenarios such as creating new items or adding items, and supports setting leading and trailing icons.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5b37c5b604a94473982780e4abd2f984~tplv-goo7wpa0wc-image.image)
## API Surface

* `checked`: Whether it is selected.
* `onCheckedChange`: Callback triggered on state change.
* `enabled`: Indicates whether it is enabled. Type: Boolean value.
* `size`: The size of the control, which can be customized using the `ToggleIconButtonDefaults` method. Setting the value to `Min` uses the default size.
* `colors`: You can customize the colors of the control using the `ToggleIconButtonDefaults` method, such as `checkedContainerColor`, `checkedContainerColor`, `checkedContainerColor`, and `checkedContainerColor`.
* `leadingIcon`: `@Composable` callback function that displays the leading icon. Optional.
* `trailingIcon`: `@Composable` callback function that displays a trailing icon; optional.
* `contentPadding`: The inner padding of the content. The default value is set using the `ButtonDefaults` method.
* `shape`: The shape of the control, which can be customized. The default value is set using the `ButtonDefaults` method.
* `gap`: The spacing between the icon and the content. This is only effective when using `MutableInteractionSource` to configure `leadingIcon` and `trailingIcon`.
* `content`: The content of the control is usually `Text`.

## Basic usage
```Kotlin
/** A simple toggle button */
@Composable
fun ToggleButtonSample() {
    var isChecked by remember { mutableStateOf(false) }
    ToggleButton(isChecked, onCheckedChange = { isChecked = it }) { Text("ToggleButton") }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/030de7a48c094a6490890cafbaf469cd~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage

* You can customize the size, shape, color, header, footer, content spacing, and the spacing between the title and icon of the ToggleButton. The ToggleButton can be used in scenarios that require switching between its selected and unselected states.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ec0488fe0c5245b1b78b3be3c863dbec~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

   ```Kotlin
   @Composable
   fun ToggleButtonSample() {
       var isChecked by remember { mutableStateOf(false) }
       ToggleButton(
           isChecked,
           onCheckedChange = { isChecked = !isChecked },
           Custom colors
           colors = ToggleButtonDefaults.toggleButtonColors(
               checkedContainerColor = Color(color = 0xFF3D8BFF),
               checkedContentColor = Color(color = 0xFFFFFFFF),
               uncheckedContainerColor = Color.Black,
               uncheckedContentColor = Color(color = 0xFFFFFFFF)
           ),
           Custom size
           size = ToggleButtonDefaults.toggleButtonSize(width = 150.dp, height = 40.dp),
           Custom shapes
           shape = RoundedCornerShape(20.dp),
           Content margin
           contentPadding = PaddingValues(10.dp),
           //Spacing between the title and the icon
           gap = 10.dp,
           //Custom header icon, optional
           leadingIcon = { AnyIcon(iconSize = 22.dp) },
           //Custom footer icon, optional
           trailingIcon = { AnyIcon(iconSize = 22.dp) }
       ) {
           Text( if (isChecked) "Selected" else "Unselected")
       }
   }
   ```

* Implement a ToggleButton similar to the DropDown Trigger example.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aec0ab6ed907425fa993763caf92857e~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

   ```Kotlin
   @Composable
   fun ToggleButtonDropdownSample(){
       var isChecked by remember { mutableStateOf(false) }
       Box {
           ToggleButton(
               checked = isChecked,
               onCheckedChange = { isChecked = !isChecked },
               colors = ToggleButtonDefaults.toggleButtonColors(
                   checkedContainerColor = PicoTheme.colorScheme.onAccent,
                   checkedContentColor = PicoTheme.colorScheme.accent,
                   uncheckedContainerColor = Color(0x0A7F7F7F),
                   uncheckedContentColor = PicoTheme.colorScheme.accent
               ),
               trailingIcon = {
                   Icon(
                       modifier = Modifier.size(16.dp),
                       painter =
                       painterResource(
                          id = R.drawable.sample_icon_down
                       ),
                       contentDescription = null
                   )
               }
           ) {
               Text("Value")
           }
           if (isChecked) {
               Menu(onDismissRequest = { isChecked = false }) {
                   MenuItem(title = { Text("Option 1") })
                   MenuItem(title = { Text("Option 2") })
                   MenuItem(title = { Text("Option 3") })
               }
           }
       }
   }
   ```

