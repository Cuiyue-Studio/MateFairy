Similar to `Offset(x, y)` in traditional Android development. In PICO Spatial SDK, you can set the offset of a view along the z-axis to make elements appear floating.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/087e4b6c051e4ba6856aecb85a4e037a~tplv-goo7wpa0wc-image.image" width="600px" /></div>

The related functions are as follows:
| **Function** | **Description** |
| --- | --- |
| offset() | Offset the view along the z-axis by the specified Dp value. |
| zOffset() | Set the offset of the view along the z-axis in pixels (Px) or by dynamic calculation method. |
The code sample is as follows:
```Kotlin
/** Static offset along the z axis */
@Composable
fun OffsetZSample() {
    // Offset 10.dp along the z-axis to make the Box float in space
    Box(
        modifier = Modifier
            .offset(z = 10.dp) // Offset by 10.dp along the z-axis to make the Box appear floating in space
            .size(100.dp)
            .background(color = Color.Red)
    ) {
        // Box content area, where other components can be placed
    }
}



/** Dynamic offset along the z-axis (floating or sinking animation can be configured) */
@Composable
fun AnimatedOffsetZSample() {
    var isFloating by remember { mutableStateOf(false) } // Control hover state
    
    // Generate animation value based on isFloating state, from 0.dp to 100.dp
    val offsetZInDp by
        animateDpAsState(targetValue = if (isFloating) 100.dp else 0.dp, label = "offsetZ")

    Box(
        modifier =
            Modifier.zOffset { offsetZInDp.toPx() } // Convert Dp to Px and apply z-axis offset to achieve floating animation
                .size(100.dp) 
                .background(color = Color.Black)
                .clickable { isFloating = !isFloating } // Click to switch to the floating state and trigger the animation
    )
}
```

