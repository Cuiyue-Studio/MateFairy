TextField is a component commonly used for text input according to the PICO design specification. When you need to observe or control the detailed state of text input, such as cursor position, selection range, composing text, and so on, you can use the overload that is based on the `TextFieldValue` parameter.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fcafc85b632b428bb8c2ac95c8f3d5c8~tplv-goo7wpa0wc-image.image)
## API Surface

* `value`: The current content of the TextField.
* `onValueChange`: When the content area of the TextField changes, the callback function is triggered with the updated content of the TextField as its argument.
* `placeholder`: When the `value` is empty, the content displayed under `placeholder` will be shown.
* `leadingContent`: Displays the content shown at the far left side of the TextField. The default value is null.
* `trailingContent `: Displays content at the far right of the TextField. The default value is null.
* `supportingText`: Optional helper text displayed below the text field container.
* `enabled`: Controls whether this text field is enabled. When set to false, the component does not respond to user input and appears disabled both visually and to accessibility services.
* `readOnly`: Controls the editable state of a text field. When set to true, the text field cannot be modified. However, users can focus on it and copy text from it. Read-only text fields are typically used to display pre-filled forms that users cannot edit.
* `textStyle`: The style to be applied to input text. The default text style uses the LocalTextStyle defined by the theme.
* `isError`: A boolean value that controls the error state of a text field. When true, the text field will be highlighted with the error color.
* `visualTransformation`: A transformation applied to input text. The default is `VisualTransformation.None`. By customizing `visualTransformation`, you can implement input transformations, such as password fields.
* `keyboardOptions`: Keyboard options applied to input text. The default keyboard option is `KeyboardOptions.Default`.
* `keyboardActions`: Keyboard actions used for text input. The default keyboard operation is `KeyboardActions.Default`.
* `singleLine`: A boolean value that controls whether the text field is single-line or multi-line. When true, the text field is single-line; when false, the text field is multi-line.
* `maxLines`: Maximum number of lines to display in the text field. The default value of maxLines is `Int.MAX_VALUE`.
* `minLines`: Minimum number of lines to display in the text field. The default minLines is `1`.
* `interactionSource`: Represents the `MutableInteractionSource` of this TextField's interaction stream. You can pass in a custom `MutableInteractionSource` to observe the interaction behavior of the TextField.
* `cornerRadius`: The background corner radius of a TextField.
* `colors`: Specifies the colors of text, content (including labels, placeholders, leading and trailing icons, indicator lines), and background in different states. The default is `TextFieldDefaults.textFieldColors`, and you can also customize colors using the `TextFieldDefaults.textFieldColors` function.

## Basic usage
```Kotlin
@Composable
private fun SimpleTextFieldSample() {
    Column {
        Text("Simple Example")
        var text by remember { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { newValue -> text = newValue },
            placeholder = { Text(text = "Placeholder") },
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/17f3eb932fd34bcb8cf0b7e5e1311245~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**

* Text prompts can be set through `placeholder`.
* You can display the error style using `isError`. When `isError` is set to `true`, the displayed color is taken from `errorColor` in `colors`. By default, it is provided by`TextFieldDefaults.textFieldColors`.
* By using `leadingContent`, `trailingContent`, and `supportingText`, you can display more custom content.

```Kotlin
@Composable
private fun TextFieldFullSample() {
    Column {
        Text("Complex Demo")
        var text by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }
        TextField(
            value = text,
            // In the example, entering 9 should display an error message
            onValueChange = { newValue ->
                text = newValue
                error = text.contains("9")
            },
            // Display blank by default
            placeholder = { Text(text = "press 9 to show error") },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sample_search),
                    contentDescription = null,
                    tint = Color(color = 0x4D000000)
                )
            },
            trailingContent = {
                Icon(
                    painter =
                        painterResource(
                            id = com.pico.spatial.ui.design.R.drawable.ic_sui_dropdown_trigger_down
                        ),
                    contentDescription = null,
                    tint = Color(color = 0x4D000000)
                )
            },
            isError = error,
            Show supportingText
            supportingText = {
                Text(text = "supporting text supporting text supporting text supporting text")
            }
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a2212cd5114244f1bbabdbc4bf9a4cee~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



