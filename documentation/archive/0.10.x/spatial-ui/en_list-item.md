ListItem is a general-purpose information display component defined by the PICO design specification for use in vertical lists, and is commonly used as content in Column and LazyColumn. This component consists of a required left-side title area, an optional left-side icon area, a right-side content area, and an optional title.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/509df96bdba54434b37b73c73fc36371~tplv-goo7wpa0wc-image.image)
## API Surface
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/36ee438406014cc9b0f30b5ee141dc2b~tplv-goo7wpa0wc-image.image)

* `headlineContent`: The title area, where the content is usually `Text`.
* `leadingContent`: Left-side area, optional, typically contains `Icon` or `Image`.
* `trailingContent`: An optional area on the right side. The content in this area is often used together with components such as `Icon`, `Button`, `Badge`, `Switch`, and other similar components.
* `supportingContent`: An optional subtitle area, typically `Text`, used to supplement the title and provide more detailed information.
* `colors`: The component includes built-in PICO standard color values, which can be customized using this parameter.
* `padding`: The spacing between the contents of a ListItem usually does not need to be adjusted.
* `shape`: The background shape of a ListItem. You can use this parameter to define the corner radius and other parameters.

## Basic usage
ListItem is typically used as content in a `Column` or a `LazyColumn`.
```Kotlin
@Composable
fun SimpleListItemSample() {
    Column {
        ListItem(headlineContent = { Text(text = "Title 1") })
        // You can add more ListItem
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8bb5033c94dd4927ba5bd6ff62552ca2~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
The `leadingContent`, `trailingContent`, and `supportingContent` of ListItem are optional. You can flexibly define different styles based on these optional slots.
```Kotlin
@Composable
fun Demo() {
    Column {
        ListItem(
            headlineContent = { Text(text = "list area") },
            leadingContent = {
                // Typically, this can be Icon, Image, or AsyncImage (Coil, supports network images)
                // This area can typically be the avatar in a message list or the main image in news articles
            },
            trailingContent = {
                // Footer area, can be used with various controls, for example, Icon, Button, and Badge
                // for example, displaying the number of unread messages, toggles on the settings page, and so on
            },
            supportingContent = {
                // Usually 'Text'
            }
        )
    }
}
```

Usage examples are as follows. Typically, you can create the style shown in the image on the right.
```Kotlin
@Composable
fun Demo() {
    Column {
        ListItem(
            headlineContent = { Text(text = "List Title") },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.image_container),
                    contentDescription = null
                )
            },
            supportingContent = {
                Text(text = "Supporting line text lorem ipsum dolor sit amet, consectetur")
            },
            trailingContent = {
                Icon(
                    painter =painterResource(id =R.drawable.ic_sui_settinglistitem_trail_arrow),contentDescription = null
                )
            }
        )
        // with checkbox
        ListItem(
            headlineContent = { Text(text = "List Title") },
            leadingContent = {
                Icon(painter = painterResource(R.drawable.ic_sample_placeholder),null)
            },
            trailingContent = {
                Some fine-tuning may be needed on the UI.
                Box(modifier = Modifier.padding(start = 8.dp, end = 10.dp)) {
                    var checked by remember { mutableStateOf(false) }
                    Switch(checked, onCheckedChange = { checked = it })
                }
            }
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f1b5f72a72b1413bb966b67309b7ebcc~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

