Button is a common control in the PICO design specification, typically used to respond to user clicks. The Spatial UI component library includes several types of PICO brand-style buttons, and you can also adjust parameters to achieve your desired button appearance. It is usually used together with `Text`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8aa53c71a28948319053bc49a210d0de~tplv-goo7wpa0wc-image.image)
## API Surface

* `onClick`: Callback function for click events.
* `enabled`: Sets whether the control is enabled.
* `size`: Control size. You can customize it using the `ButtonDefaults.buttonSize` method. Setting the value to `IconButtonDefaults.Regular` indicates that the default size is used.
* `colors`: Control colors can be customized using the `ButtonDefaults.buttonColors` method. You can define the `containerColor` for the background color and the `contentColor` for the content color.
* `leadingIcon`: Icon displayed at the start; optional, usually an `Icon`.
* `trailingIcon`: An icon displayed at the end, optional and usually an `Icon`.
* `contentPadding`: The inner padding of the control's content.
* `shape`: The shape of the control. Customizable.
* `gap`: The spacing between `content`, `leadingIcon`, and `trailingIcon`.
* `content`: The content of the control.

## Basic usage
```Kotlin
@Composable
fun ButtonSample() {
    Button(
        onClick = {
            // do something
        }
    ) {
        Text("Click me")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ee67c4f16fbe4f28979676812dcaf69e~tplv-goo7wpa0wc-image.image" width="2464px" /></div>

## Advanced usage
Customize Button. Set colors using `ButtonDefaults.buttonColors`. Optionally, add icons to the start or end.
```Kotlin
@Composable
fun ButtonListSample() {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        CustomButton(
            title = "Click me",
            onClick = {},
            leadingIcon = {
                AnyIcon()
            }
        )
        Spacer(Modifier.height(10.dp))
        CustomButton(
            title = "Click me",
            onClick = {},
            trailingIcon = {
                AnyIcon()
            }
        )
        Spacer(Modifier.height(10.dp))
        CustomButton(
            title = "Click me",
            onClick = {},
            leadingIcon = {
                AnyIcon()
            },
            trailingIcon = {
                AnyIcon()
            }
        )
    }
}

@Composable
fun CustomButton(title:String,
                 onClick: () -> Unit,
                 leadingIcon: (@Composable () -> Unit)? = null,
                 trailingIcon: (@Composable () -> Unit)? = null
                 ) {
    Button(
        onClick = onClick,
        size = ButtonDefaults.Regular,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(color = 0xFF3D8BFF),
            contentColor = Color(color = 0xFFFFFFFF)
        ),
        enabled = true,
        leadingIcon = { leadingIcon?.invoke() },
        trailingIcon = { trailingIcon?.invoke() }
    ) {
        Text(text = title)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e24de7d82414396a2ccbaa5c4602610~tplv-goo7wpa0wc-image.image" width="2461px" /></div>



