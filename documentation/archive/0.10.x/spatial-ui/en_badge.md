This article introduces the features and usage of the Badge, DotBadge, and NumberBadge components.
## Badge
Badge, under the PICO design guidelines, is typically used to display indicators of dynamic information. It can be overlaid on other components as a small icon or a number to notify users.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7650fc23cc0946c6aad6f109b933e4a1~tplv-goo7wpa0wc-image.image)
### API Surface

* `badgeColor`: Used to set the Badge color.
* `badgeSize`: Sets the size of the Badge. The default value is `BadgeDefaults.Small`.
* `radius`: Sets the corner radius for the Badge. By default, the corner radius size is that of `BadgeDefaults.Small`.
* `contentPadding`: Sets the inner padding of the Badge.
* `textStyle`: Used to set the text style inside the Badge.
* `content`: Content within the Badge control. You can choose to add custom content.

### Basic usage
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Badge {
            Text("Badge Content")
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6c21f53d27f8483e809ed3a61b54f763~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

### **Advanced usage**
By configuring parameters such as Badge's `badgeColor`, `radius`, and `contentPadding`, you can further customize the content.
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
       // Set Badge background color to red, text color to white, corner radius to 6dp, and padding to 6dp
        Badge(badgeColor = BadgeDefaults.badgeColors(Color.Red,Color.White), radius = 16.dp, contentPadding = PaddingValues(6.dp)){
            Text("Badge Content")
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d67014f1fd794ce58d095e58e5b13d71~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

## DotBadge
DotBadge is a badge, defined by the PICO design guidelines, that serves as a dot indicator and is typically used for simple message alerts.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f972d36542b64bc5bbe3563335a97652~tplv-goo7wpa0wc-image.image)
### API Surface
`color`: Used to set the DotBadge color.
### Basic usage
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        DotBadge()
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdf11629d51b41c9b7624edb8326f6c3~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

### **Advanced usage**
DotBadge works in conjunction with other controls to provide a wider range of notification features.
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Box {
            Text("A simple message")
            DotBadge(modifier = Modifier.align(Alignment.TopEnd).offset(x= 10.dp), color = Color.Yellow)
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5f90dd27627f497f88c8ddc41ee178fc~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

## NumberBadge
NumberBadge is a badge, defined by the PICO design guidelines, for displaying numbers.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dcc7c4e9f6cd47bdbfc4f0fdd26348f8~tplv-goo7wpa0wc-image.image)
### API Surface

* `number`: The size of the number displayed by NumberBadge.
* `threshold`: Sets the maximum display value for NumberBadge. If `number` is greater than `threshold`, the effect specified by `overflow` will be shown.
* `overflow`: The style displayed when `number` exceeds `threshold`. The default is `Overflow.Plus`.
* `contentPadding`: Sets the inner padding for Badge.
* `textStyle`: Used to set the text style for `number`.
* `badgeSize`: Sets the size of the NumberBadge. You can set a custom size by passing in a `BadgeSize` object.
* `badgeColor`: Used to set the NumberBadge color.
* `contentPadding`: Sets the inner padding of NumberBadge.

### Basic usage
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        NumberBadge(number = 1)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9531cb8b44af4748b29be86057c4c94b~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

### **Advanced usage**
By using NumberBadge's `threshold` and `overflow`, you can achieve different display effects.
```Kotlin
@Composable
fun BadgeDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumberBadge(number = 1, badgeColor = BadgeDefaults.badgeColors())
            NumberBadge(number = 10, badgeColor = BadgeDefaults.badgeColors())
            // The default Overflow.Plus effect
            NumberBadge(number = 100, threshold = 9, badgeColor = BadgeDefaults.badgeColors())
            NumberBadge(number = 100, threshold = 99, badgeColor = BadgeDefaults.badgeColors())
            The Overflow.Ellipsis effect
            NumberBadge(
                number = 100,
                threshold = 99,
                overflow = Overflow.Ellipsis,
                badgeColor = BadgeDefaults.badgeColors(),
            )
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/62cd42682733439ca0185f954790f2dc~tplv-goo7wpa0wc-image.image" width="2560px" /></div>


