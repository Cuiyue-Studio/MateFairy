This article introduces the capabilities and usage of the Divider, HorizontalDivider, and VerticalDivider components.
## Divider
Divider is a component under the PICO design specification. It is used to separate interface areas and typically appears as a linear component.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/db70722e1420492c8ff5c429b4d37e23~tplv-goo7wpa0wc-image.image)
### API Surface

* `color`: The color value of the Divider
* `thickness`: Current thickness of the Divider. If `orientation` is `Horizontal`, it represents the vertical thickness; if `orientation` is `Vertical`, it represents the horizontal thickness.
* `orientation`: Sets the layout direction of the Divider. The default is `Orientation.Horizontal`, which arranges it horizontally.

### Basic usage
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column {
            Text("Horizontal Divider Start")
            Divider()
            Text("Horizontal Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ec38039f2b6b480d8465831bd31294de~tplv-goo7wpa0wc-image.image)
### **Advanced usage**
You can achieve customized display effects by setting `orientation`, `color`, and `thickness` in combination with `Modifier`.
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.height(IntrinsicSize.Max)) {
            Text("Divider Start")
            Divider(modifier = Modifier.padding(horizontal = 20.dp).fillMaxHeight(), thickness = 2.dp, orientation = Orientation.Vertical, color = Color.Red)
            Text("Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/48d75a1f094d410b99d91cad4294873f~tplv-goo7wpa0wc-image.image)
## HorizontalDivider
HorizontalDivider is a component defined in the PICO design specification, specifically used to divide interface areas horizontally.
### API Surface

* `color`: The color value of HorizontalDivider.
* `thickness`: The current thickness of the Divider. The default value is 1 dp.

### Basic usage
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.height(IntrinsicSize.Max)) {
            Text("Divider Start")
            HorizontalDivider()
            Text("Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8b3a2e727ff8465285a9794c98bd27bd~tplv-goo7wpa0wc-image.image)
### **Advanced usage**
You can achieve a more customized display effect by setting `thickness` and `color` in combination with `Modifier`.
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.height(IntrinsicSize.Max)) {
            Text("Horizontal Divider Start")
            // Set thickness to 5dp and change color to red
            HorizontalDivider(color = Color.Red, thickness = 10.dp, modifier = Modifier.padding(5.dp))
            Text("Horizontal Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3b6d373953441ff9f721be289bf1761~tplv-goo7wpa0wc-image.image)
## VerticalDivider
VerticalDivider is a component under the PICO design guidelines, specifically used to divide interface areas vertically.
### API Surface

* `color`: The color value of VerticalDivider.
* `thickness`: The current thickness of the Divider. The default value is 1 dp.

### Basic usage
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.height(IntrinsicSize.Max)) {
            Text("Vertical Divider Start")
            VerticalDivider()
            Text("Vertical Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/22baad0a5b4c4c9a9f5df98eae37fb8a~tplv-goo7wpa0wc-image.image)
### **Advanced usage**
You can achieve a more customized display effect by setting `thickness` and `color` in combination with `Modifier`.
```Kotlin
@Composable
fun DividersDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.height(IntrinsicSize.Max)) {
            Text("Vertical Divider Start")
            VerticalDivider(color = Color.Red, thickness = 10.dp, modifier = Modifier.padding(horizontal = 5.dp))
            Text("Vertical Divider End")
        }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ad56a43aebca40beba0ba25bf60850e2~tplv-goo7wpa0wc-image.image)

