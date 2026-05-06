Link is a control defined in the PICO design specification that has no background or border and is used to respond to user click interactions. Its content area typically consists of `Text` or other composable elements, and it is commonly used for navigation links, learning more, viewing details, and similar scenarios.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b572346b3464494f87a433e3a5225a70~tplv-goo7wpa0wc-image.image)
## API Surface

* `onClick`: Callback function executed when the control is clicked.
* `enabled`: Indicates whether it is enabled. Boolean value.
* `size`: The dimensions of the control, which can be customized using the `LinkDefaults.buttonSize` method. A value of `LinkDefaults.Regular` indicates the default size.
* `contentPadding`: The internal padding of the control's content. By default, its size is determined by `size`, but it can be customized.
* `shape`: The shape of the control.
* `colors`: The colors of the control, which can be customized using the `LinkDefaults.linkColors` method for `containerColor` and `contentColor`.
* `trailingIcon`: `@Composable` callback function for the trailing icon of the component; optional, typically an `Icon`.
* `content`: The content of the control is usually `Text`.

## Basic usage
```Kotlin
@Composable
fun LinkSample() {
    Link(onClick = {}) { Text("Click to learn the details") }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6a8ebb2a3f244e44a4db0c6d0844cc60~tplv-goo7wpa0wc-image.image" width="2662px" /></div>

## Advanced usage
Customize size, color, padding, and shape, and add a trailing icon.
```Kotlin
@Composable
fun LinkDetailSample() {
    Link(
        onClick = {},
        Custom size
        size = LinkDefaults.buttonSize(
            height = 24.dp,
            width = 120.dp
        ),
        Custom colors
        colors = LinkDefaults.linkColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        Custom spacings
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 2.dp),
        // Custom tail icon
        trailingIcon = { AnyIcon(iconSize = 16.dp) })
    {
        Text("Click to view")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a1517994c29d44ea9e4644f99814c996~tplv-goo7wpa0wc-image.image" width="2898px" /></div>




