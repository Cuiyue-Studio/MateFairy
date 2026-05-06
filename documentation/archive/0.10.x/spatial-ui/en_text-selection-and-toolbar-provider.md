TextSelectionAndToolbarProvider is a component developed under the PICO design guidelines that provides configuration for text selection and toolbar colors. It is commonly used in scenarios such as changing the cursor or selection color in a TextField, or displaying a toolbar.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c631cc1bedcc4f5280e93c6f356906f2~tplv-goo7wpa0wc-image.image)
## API Surface

* `toolbar`: Used to display the toolbar in `content`.
* `colors`: Used to customize the cursor color and the selected text color for components such as TextField.
* `content`: The content of TextSelectionAndToolbarProvider.

## Basic usage
```Kotlin
@Composable
private fun SingleLineCustomizeColors() {
    var text by rememberRandomString()
    Title("Single-line text with designated color")
    // Change cursor color to red and selection background to blue
    TextSelectionAndToolbarProvider(
        colors =
            TextSelectionColors(
                handleColor = Color.Red,
                backgroundColor = Color.Blue,
            ),
    ) {
        TextField(
            text,
            onValueChange = {
                text = it
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            singleLine = true,
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e5a1488f62d449db37705e52062813b~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**
By configuring TextSelectionAndToolbarProvider at different levels, you can achieve different selection styles.
```Kotlin
@Composable
private fun SingleLineCustomizeColors() {
    var text by rememberRandomString()
    TextSelectionAndToolbarProvider(
        colors =
            TextSelectionColors(
                handleColor = Color.Red,
                backgroundColor = Color.Blue,
            ),
    ) {
        Row {
            // Cursor is shown in red, selection color is blue
            TextField(
                text,
                onValueChange = {
                    text = it
                },
                modifier =
                    Modifier
                        .padding(16.dp),
                singleLine = true,
            )
            // Display cursor as green, selection color as black
            TextSelectionAndToolbarProvider(
                colors =
                    TextSelectionColors(
                        handleColor = Color.Green,
                        backgroundColor = Color.Black,
                    ),
            ){
                TextField(
                    text,
                    onValueChange = {
                        text = it
                    },
                    modifier =
                        Modifier
                            .padding(16.dp),
                    singleLine = true,
                )
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/41b7c01efeaf4171bd75f4ecd1e0b569~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



