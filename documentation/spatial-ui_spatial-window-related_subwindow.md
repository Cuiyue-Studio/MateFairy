A Subwindow, as defined by the PICO design guidelines, is a container displayed on the left or right side of a window container, with its height always matching that of the window container.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c7952621b1840159ec402eff91df0ea~tplv-goo7wpa0wc-image.image" width="2000px" /></div>

## API Surface

* `rotation3D`: The 3D rotation angle of the SubWindow. The z parameter in the pivot of Rotation3D is ignored.
* `followViewpoints`: The viewpoints that SubWindow follows.
* `placement`: Specifies the placement of the SubWindow. By default, this is determined by the `SubwindowPlacement.Default` method.
* `offset`: The offset of the SubWindow. Based on the `placement` setting, spacing is set between the SubWindow and the side of the main window, with a default value of 24 dp.
* `content`: The content of SubWindow.

## Usage restrictions
The width of the current SubWindow is fixed at 360 dp, and its height follows the main window's height, which is provided by the `LocalConfiguration.current.screenHeightDp.dp` method.
## Basic usage
```Kotlin
@Composable
fun SubwindowSample() {
    Subwindow() {
        LazyColumn(Modifier.fillMaxSize()) {
            items(count = 100) { Text("messageItem-${it}") }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d9c54fdd93f4418cbfbd35ba847e1558~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**

* You can modify `placement` to set the display position of SubWindow in the scene.
* You can modify `rotation3D` to enable SubWindow to achieve a 3D rotation effect.

```Kotlin
@Composable
fun SubwindowSample() {
    // a message list alongside the main window with a little rotation
    val axis =
        when (LocalLayoutDirection.current) {
            LayoutDirection.Ltr -> -RotationAxis3D.Y
            LayoutDirection.Rtl -> RotationAxis3D.Y
        }

    val pivot =
        when (LocalLayoutDirection.current) {
            LayoutDirection.Ltr -> NormalizedPoint3D.Left
            LayoutDirection.Rtl -> NormalizedPoint3D.Right
        }
    Subwindow(rotation3D = Rotation3D(degree = 45f, axis, pivot)) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(count = 100) { Text("messageItem-${it}") }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3ec0cb0c0beb491cb50330461054ec19~tplv-goo7wpa0wc-image.image" width="1280px" /></div>


 


