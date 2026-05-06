IconButton is a common control under the PICO design guidelines used to respond to user clicks. Unlike the Button, the content area of an IconButton is typically filled with an `Icon` (it can also be filled with `Text`), while the content area of a Button is usually filled with `Text`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/76996ba305b744ca9ef0924681779e44~tplv-goo7wpa0wc-image.image)
## API Surface

* `onClick`: Callback function triggered on click.
* `enabled`: Sets whether the control is enabled.
* `size`: Control size, which can be customized using the `ButtonDefaults.buttonSize` method. A value of `IconButtonDefaults.Regular` indicates that the default size is used.
* `colors`: Control colors, which can be customized using the `ButtonDefaults.buttonColors` method. You can specify `containerColor` for the background color and `contentColor` for the content color.
* `shape`: The control shape is customizable. A value of `CircleShape` indicates that the default shape is used.
* `content`: Control content, usually an `Icon`.

## Basic usage
The default IconButton is circular and displays the theme color.
```Kotlin
/** A simple usage of [IconButton] */
@Composable
fun IconButtonSample() {
    IconButton(
        onClick = {},
    ) {
        AnyIcon(iconSize = 20.dp)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e2570171158467aa748b42bc4907b9c~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
Customize and modify the size, color, shape, and icon of IconButton.
```Kotlin
@Composable
fun IconButtonSample() {
    var clickCount by remember { mutableStateOf(0) }
    Column {
        IconButton(
            onClick = {
                clickCount++
            },
            A custom size
            size = IconButtonDefaults.iconButtonSize(60.dp),
            Custom colors
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(color = 0xFFFF4D4D),
                contentColor = Color.White
            ),
            Custom shapes
            shape = RoundedCornerShape(20.dp),
            enabled = true,
        ) {
            Custom icons
            AnyIcon(iconSize = 20.dp)
        }
        Text(text = "Click count: $clickCount")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ae8f79acbe8748048eabaedf2fa82c46~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

In some usage scenarios, the icon color of the IconButton varies. Without changing  **** `colors ` in the IconButton, and to avoid being affected by the default IconButton color, there are currently two approaches you can use.
**Method 1**
```Kotlin
IconButton(
    onClick = {

    }
) {
    Image(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = R.drawable.ic_sample_download),
        contentDescription = null
    )
}
```

**Method 2**
```Kotlin
IconButton(
    onClick = {

    }
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_sample_download),
        contentDescription = null,
        tint = Color.Red
    )
}
```

