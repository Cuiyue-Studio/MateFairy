You can add the glass effect to a view in WindowContainer. PICO Spatial SDK provides four types of glass background material—Thin, Regular, Thick, and Thickest—each producing a different degree of blur effect on the content behind the background.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a4de4e12679b40d8830c6945b73f767b~tplv-goo7wpa0wc-image.image" width="3492px" /></div>

Code sample:
```Kotlin
@Composable
fun BackgroundMaterial(){
    Box(
        Modifier.size(100.dp).backgroundMaterial(
            enable = true,
            style = Material.Thickest
        )
    )
}
```

