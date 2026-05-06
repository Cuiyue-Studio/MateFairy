ToggleIconButton is a control defined by the PICO design guidelines, with a secondary state and designed to respond to user click interactions. Its content area typically contains an icon or other composable elements.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/125535057eba4b3294fdf29682a6371f~tplv-goo7wpa0wc-image.image)
## API Surface

* `checked`: Whether it is selected.
* `onCheckedChange`: Callback that triggers a change of state.
* `enabled`: Indicates whether it is enabled. Boolean value.
* `size`: The size of the control can be customized using the `ToggleIconButtonDefaults` method. A value of `Min` indicates that the default size is used.
* `colors`: The colors of the control, which can be customized using the `ToggleIconButtonDefaults` method by setting `checkedContainerColor`, `checkedContainerColor`, `checkedContainerColor`, and `checkedContainerColor`.
* `shape`: The shape of the control. The default is circular `CircleShape`.
* `content`: The content of the control, which is usually an `Icon`.

## Basic usage
```Kotlin
@Composable
fun ToggleIconButtonSample() {
    var isChecked by remember { mutableStateOf(false) }
    ToggleIconButton(
        checked = isChecked,
        onCheckedChange = { isChecked = !isChecked },
    ) {
        AnyIcon()
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f9b79cd7855d4f569d280acfa26e4a8f~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
Used in multiple scenarios with toggle states, such as favorite, like, and other similar actions, with support for customizing display style and color, and for switching between cancel and confirm states.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/81f248b999384189bdd2f693b21651d7~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

```Kotlin
@Composable
fun ToggleIconButtonSample() {
    var isLike by remember { mutableStateOf(false) }
    var isCollected by remember { mutableStateOf(false) }
    Row {
        ToggleIconButton(
            onCheckedChange = {
                isLike = !isLike
            },
            checked = true,
            colors = ToggleIconButtonDefaults.toggleIconButtonColors(
                checkedContentColor = Color.Black
            )
        ) {
            Icon(
                painter = painterResource(id = if (isLike) R.drawable.sample_like else R.drawable.sample_unlike),
                contentDescription = null
            )
        }
        Spacer(Modifier. width(20.dp))
        ToggleIconButton(
            onCheckedChange = {
                isCollected = !isCollected
            },
            checked = true,
            colors = ToggleIconButtonDefaults.toggleIconButtonColors(
                checkedContentColor = Color.Black
            )
        ) {
            Icon(
                painter = painterResource(id = if (isCollected) R.drawable.sample_collected else R.drawable.sample_uncollect),
                contentDescription = null
            )
        }
    }
}
```

