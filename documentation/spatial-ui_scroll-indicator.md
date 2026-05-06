The Scroll Indicator is typically used to visualize the scrolling progress on pages with long content, helping users perceive the length of the content and their current position, which enhances navigation efficiency and improves the user experience. Its features are summarized as follows:

* Update in real time while scrolling
* Adapt to horizontal/vertical scrolling scenarios

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/52c8199e87474c7281e139b8f93837ea~tplv-goo7wpa0wc-image.image)
## API Surface
The components currently supported by Scrollindicator include:

* Row
* LazyRow
* Column
* LazyColumn
* Menu
* Adapt the ScrollIndicator for Row & Column **** 
   * state: ScrollState bound to Row and Column.
   * orientation: Scroll indicator direction. Column is vertical, row is horizontal.
   * alignment: Customize the placement position within Box.
   * dismissAfter: Time to wait before disappearing after no interaction.
   * paddingForInteraction: Size of the extra interactive area.
* ScrollIndicator for LazyRow and LazyColumn
   * state: ScrollState bound to Row and Column.
   * alignment: Customize the positioning within Box.
   * dismissAfter: Duration to delay disappearance when there is no interaction.
   * paddingForInteraction: Additional touch target area size.
* Adapt to Menu and SubMenu
   * hasScrollIndicator: Determines whether the ScrollIndicator appears during scrolling when the content exceeds the Menu size limit.

## Basic usage
### Column with ScrollIndicator
```Kotlin
@Composable
fun ColumnWithScrollIndicatorDemo() {
    Box {
        // 1. define a scroll state
        val state = rememberScrollState()
        Column(
            modifier =
                Modifier.fillMaxSize()
                    // 2. apply the scroll state to the column
                    .verticalScroll(state)
        ) {
            repeat(times = 100) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Item $it")
                }
                Divider()
            }
        }
        // 3. apply the scroll state to the scroll indicator
        ScrollIndicator(state = state, orientation = Orientation.Vertical)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/422149c4ac3a4b28b64aa40a8634d877~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

### Row with ScrollIndicator
```Kotlin
@Composable
fun RowWithScrollIndicatorDemo() {
    Box {
        // 1. define a scroll state
        val state = rememberScrollState()
        Row(
            modifier =
                Modifier.fillMaxSize()
                    // 2. apply the scroll state to the Row
                    .horizontalScroll(state)
        ) {
            repeat(times = 100) {
                // add your content here
                Text("item $it")
                Divider(orientation = Orientation.Vertical)
            }
        }
        // 3. apply the scroll state to the scroll indicator
        ScrollIndicator(state = state, orientation = Orientation.Horizontal)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e59603744efb473b917ce3ab8bc792e2~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

### LazyColumn with ScrollIndicator
```Kotlin
@Composable
fun LazyColumnWithScrollIndicatorDemo() {
    Box {
        // 1. define a scroll state
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // 2. apply the scroll state to the LazyColumn
            state = state
        ) {
            items(count = 100) {
                Box(modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Item $it") }
                Divider()
            }
        }
        // 3. apply the scroll state to the scroll indicator
        ScrollIndicator(state = state)
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ff516252b55f45858d89348ea7268b1a~tplv-goo7wpa0wc-image.image)
### LazyRow with ScrollIndicator
```Kotlin
@Composable
fun LazyRowWithScrollIndicatorDemo() {
    Box {
        // 1. define a scroll state
        val state = rememberLazyListState()
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            // 2. apply the scroll state to the LazyRow
            state = state
        ) {
            items(count = 100) {
                // add your content here
                Text("item $it")
                Divider(orientation = Orientation.Vertical)
            }
        }
        // 3. apply the scroll state to the scroll indicator
        ScrollIndicator(state = state)
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/589b456b5c624706bb47127edd1a4fbd~tplv-goo7wpa0wc-image.image)
### Menus with ScrollIndicator
```Kotlin
Box {
    Button(onClick = {
        // show menu
        showMenu = true
    }) {
        Text(text = "ShowMenu")
    }
    if (showMenu) {
        Menu(
            onDismissRequest = {
                // dismiss menu
                showMenu = false
            },
            hasScrollIndicator = true
        ) {
            
            // Custom items
        }
    }
```

<div style="text-align: center"><img src="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiB2aWV3Qm94PSIwLDAsMjAlLDEwMCUiIHZlcnNpb249IjEuMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+CiAgPGRlZnM+CiAgICA8bGluZWFyR3JhZGllbnQgaWQ9ImciPgogICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjRjJGM0Y1IiBvZmZzZXQ9IjI1JSIgLz4KICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0U1RTZFQiIgb2Zmc2V0PSIzNyUiIC8+CiAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNGMkYzRjUiIG9mZnNldD0iNjMlIiAvPgogICAgPC9saW5lYXJHcmFkaWVudD4KICA8L2RlZnM+CiAgPHJlY3QgaWQ9InIiIHdpZHRoPSI0MDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZykiIC8+CiAgPGFuaW1hdGUgeGxpbms6aHJlZj0iI3IiIGF0dHJpYnV0ZU5hbWU9IngiIGZyb209Ii0zMDAlIiB0bz0iMCUiIGR1cj0iMS41cyIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiICAvPgo8L3N2Zz4=" width="2560px" /></div>


