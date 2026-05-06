Sheet is a component designed under the PICO design guidelines, used to present popup content and perform tasks related to popups.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/40313f72581546d8819e0066dd6655a5~tplv-goo7wpa0wc-image.image)
## API Surface

* `onDismissRequest`: Executes a callback when the user clicks outside the popup.
* `properties`: Used to configure the behavioral properties of the Sheet, such as whether clicking outside is allowed. By default, these are provided by the `SheetDefaults.DefaultSheetsProperties` method.
* `title`: A component displayed as a heading, usually as text (`Text`), with customizable content.
* `leadingAction`: The component displayed at the top left corner of the form (`Sheet`), which by default is the close button provided by `DefaultCloseIconButton`.
* `trailingAction`: A component displayed at the top right corner of the form (`Sheet`).
* `bottom`: A component displayed at the bottom of the basic form (`BasicSheet`).
* `content`: The content of `Sheet`.

## Basic usage
```Kotlin
@Composable
fun SheetDemo() {
    var showSheet by remember { mutableStateOf(false) }

    Button({
        showSheet = !showSheet
    }) {
        Text("Show/Hide Sheet")
    }

    if (showSheet){
        Sheet(
            // When the user clicks outside the Sheet, you can close the Sheet here
            onDismissRequest = { showSheet = false},
            // A close button is present by default; it can be set to null
            leadingAction = null,
        ) {
            Box(modifier = Modifier.size(300.dp)) {
                Image(
                    modifier = Modifier.matchParentSize(),
                    painter = painterResource(id = R.drawable.image_container),
                    contentDescription = "",
                )
            }
        }
    }

}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2147d0d88ce14403ba5a423a61db0f2c~tplv-goo7wpa0wc-image.image" width="864px" /></div>

## **Advanced usage**

* `content` can accept custom content, enabling custom effects.
* `Sheet` allows you to add a `title` to set the title. By configuring `leadingAction`, `trailingAction`, or `bottom`, you can add content around `content`.

```Kotlin
@Composable
fun SheetDemo() {
    var showSheet by remember { mutableStateOf(false) }

    Button({
        showSheet = !showSheet
    }) {
        Text("Show/Hide Sheet")
    }

    if (showSheet){
        Sheet(
            // Clicking outside the Sheet can close the Sheet here
            onDismissRequest = { showSheet = false},
            title = {
                Text("Title")
            },
            leadingAction = {
                Button({
                    showSheet = false
                }) {
                    Text("Left Button")
                }
            },
            trailingAction = {
                Button({
                    showSheet = false
                }) {
                    Text("Right Button")
                }
            },
            bottom = {
                Button({
                    showSheet = false
                }) {
                    Text("Bottom Button")
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier.matchParentSize(),
                    painter = painterResource(id = R.drawable.image_container),
                    contentDescription = "",
                )
            }
        }
    }

}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cc414b5a233149328e3d638310a2e1f6~tplv-goo7wpa0wc-image.image" width="852px" /></div>



