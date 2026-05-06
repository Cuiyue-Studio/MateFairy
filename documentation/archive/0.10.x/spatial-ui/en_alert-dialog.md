AlertDialog is a component defined by the PICO design guidelines, used to interrupt the user and prompt them. It can include an icon, a title, custom content, and buttons.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ce9fc043d3c64a8cb7f60f40553159d7~tplv-goo7wpa0wc-image.image)
## API Surface

* `onDismissRequest`: Callback function invoked when the user attempts to close the Alert Dialog by tapping outside the dialog or pressing the back button.
* `icon`: An icon displayed above the title, usually `Icon`.
* `title`: The title component is used to explain the purpose of the dialog box.
* `content`: Custom content area, displayed below the title.
* `buttons`: The button area is commonly used for actions such as "Confirm" or "Cancel".
* `orientation`: The orientation of AlertDialog. The default value is horizontal (`Orientation.Horizontal`).
* `padding`: The inner padding for the entire AlertDialog, which defaults to DialogPadding from `AlertDialogDefaults`.
* `cornerRadius`: The corner radius of the dialog box, with the default value provided by the `AlertDialogDefaults.DialogCornerRadius` method.
* `properties`: Used to further configure platform-specific attributes of the dialog box. By default, the `AlertDialogDefaults.DefaultAlertDialogProperties` method provides these properties. You can use them to configure the behavior of the Alert Dialog.

## Basic usage
```Kotlin
@Preview
@Composable
fun NoticeDialogWithoutButton() {
    Box(modifier = Modifier.size(600.dp)) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = "DP Firmware upgrading")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_toast_warning),
                    contentDescription = "",
                    modifier = Modifier.size(48.dp),
                )
            },
            content = {
                Text(text = "Regular Dialog")
            },
        )
    }

}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cba4eda5bc8449b69ddcaedcd0e07b46~tplv-goo7wpa0wc-image.image)
## **Advanced usage**
`orientation` determines the arrangement of the content, and `cornerRadius` determines the rounded corners of the AlertDialog. You can also modify AlertDialog properties by adding the `properties` attribute.
```Kotlin
@Preview
@Composable
fun NoticeDialogWithoutButton() {
    Box(modifier = Modifier.size(600.dp)) {
        var show by remember {
            mutableStateOf(true)
        }
        if (show){
            AlertDialog(
                onDismissRequest = {
                    show = false
                },
                title = {
                    Text(text = "DP Firmware upgrading")
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_toast_warning),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                    )
                },
                content = {
                    Text(text = "Content")
                },
                cornerRadius = 0.dp,
                orientation = Orientation.Vertical
            )
        }

    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7e7dbda5968425db9382fb7a5d3c947~tplv-goo7wpa0wc-image.image)

