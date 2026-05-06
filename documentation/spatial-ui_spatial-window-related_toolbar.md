The Toolbar is a type of container designed according to the PICO design specifications. It is positioned at the bottom center of the WindowContainer and can be used to display additional hint controls.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c0edadcc71f34257b976abbadf8739b3~tplv-goo7wpa0wc-image.image)
## API Surface

* `cornerSize`: Controls the corner radius of the Toolbar. Default value is 16 dp.
* `followViewpoints`: The ViewPoint that the toolbar follows. The default value is `ViewPoint.All`.
* `content`: Content placed inside the Toolbar.

## Basic usage
```Kotlin
@Preview
@Composable
private fun ToolbarDemo() {
    Toolbar {
        repeat(4) {
            Box {
                Button(
                    onClick = { // Custom content}
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Text("Action")
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e2d2102ecd654a5ca9c53ed775b1881a~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

## **Advanced usage**
You can customize the content of the Toolbar by configuring the Toolbar's `cornerSize`.
```Kotlin
@Preview
@Composable
private fun ToolbarDemo() {
    Toolbar(cornerSize = 0.dp) {
        repeat(4) {
            Box {
                Button(
                    onClick = { // Custom content}
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Text("Action")
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3ff4651b62f84cf6b801eb3eb50f7bac~tplv-goo7wpa0wc-image.image" width="2560px" /></div>



