PICO Spatial SDK provides a mechanism for customizing spatial hover effects—SpatialHoverEffect. You can submit a configuration descriptor to PICO OS 6, allowing PICO OS 6 to proactively change the UI presentation when a hover event occurs, without notifying the client. The entire process is performed outside the client process, preventing apps from illegally obtaining user information and providing good privacy protection. This approach is particularly well suited for eye-tracking interactions.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6c20a41394144168fc38360ad45b3f2~tplv-goo7wpa0wc-image.image" width="700px" /></div>

All interactors can trigger the spatial hover effect.

Depending on different use cases, SpatialHoverEffect includes default effect, `CustomHover, and SpatialHoverEffectGroup`. See below for a detailed explanation.
### Default effect
You can directly use the system’s built-in spatial hover effect. When 2D content is hovered over, a highlight layer is displayed. Simply configure `Modifier.spatialHoverEffect` to enable this effect. Sample code is as follows:
```Kotlin
Box(
    modifier =
        Modifier
            .size(100.dp)
            .background(Color.Yellow)
            .align(Alignment.Center)
            // The default effect is equivalent to `spatialHoverEffect(SpatialHoverStyle.Default)`
            .spatialHoverEffect() 
            //  or specify a highlight style that is not affected by changes to the system Default reference
            .spatialHoverEffect(SpatialHoverStyle.Highlight) 
) {...}
```

### CustomHover
If the default spatial hover effect does not meet your requirements, you can customize the hover effect. The following capabilities are currently supported:
| **Capability** | **Description** |
| --- | --- |
| animation | Used in combination with other effects to define the animation effect for spatial hover, including Bezier curve, animation duration, and playback delay time. |
| clipShape | Cropping, including cropping along the x-axis and y-axis and graphical cropping. <br> Supported shapes: rectangle, rounded rectangle, circle, and anchor. |
| opacity | Transparency control. |
| scaleEffect | Scaling, including scaling along the  x-axis (horizontal) and scaling along the y-axis (vertical). |
Code sample:
```Kotlin
Box(
    modifier =
        Modifier.border(width = 1.dp, color = Color.Red)
            .size(width = 200.dp, height = 60.dp)
            .spatialHoverEffect {
                val isActive = it.isActive
                val size = it.size
               
                // Animation block: controls scaling and shape changes
                // Use a 200ms tween animation with the EaseInElastic curve when activated
                // 100 ms activation delay; no delay on deactivation
                animation(
                    tween(
                        durationMillis = 200,
                        delayMillis = if (isActive) 100 else 0,
                        easing = EaseInElastic
                    )
                ) {
                    // Scale the view, with the center as the default pivot, applying uniform scaling along both the X and Y axes.
                    scale(scale = if (isActive) 1.4f else 1f)
                    // You can also set the scaling for the X axis or Y axis separately, and specify a different center for scaling, for example: `scale(scaleX: Float, scaleY: Float, origin: TransformOrigin = TransformOrigin.Center)`
                    clipShape(
                        // Shape, must be a rounded rectangle, a rectangle, or a circle
                        shape = if (isActive) RectangleShape else CircleShape,
                        // Cropping area size, calculated from the top-left corner by default
                        size =
                            if (isActive) size
                            else IntSize(width = size.height, height = size.height)                                    
                    )
                }

                // Opacity, fully opaque when hovered, semi-transparent when not hovered
                alpha(if (isActive) 1f else 0.6f)
            }
            .background(Color.Blue),
    contentAlignment = Alignment.Center,
) {
    Text("Clip & Scale & Alpha", color = Color.White)
}
```

### SpatialHoverEffectGroup
`SpatialHoverEffectGroup` is used to group multiple views together, making the parent view and child views share the same hover effect. You can use `Modifier.spatialHoverEffectGroup` to apply a unified hover effect to a group of controls.
```Kotlin
Column {
    // Use `SpatialHoverEffectGroup.obtain()` to generate a globally unique HoverGroup object
    val group = remember { SpatialHoverEffectGroup.obtain() }
    val isEnabled = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            // Implicitly set HoverGroup; the system will automatically assign HoverGroup
            .spatialHoverEffectGroup()
            .mySpatialHoverEffect()
    ) {...}

    Box(
        modifier = Modifier
            // Explicitly set HoverGroup and enable or disable it dynamically at runtime
            .spatialHoverEffectGroup(group = group, enable = isEnabled)
            .mySpatialHoverEffect()
    ) {
        // Whether set implicitly or explicitly, after calling `Modifier.spatialHoverEffectGroup()` to set a HoverGroup for a view, it will pass the HoverGroup to all of its child views
        Button(
            modifier = Modifier
                .mySpatialHoverEffect(),
            onClick = {
                // Click to enable or disable HoverGroup
                isEnabled = !isEnable
            }
        ) {...}
    }
}

fun Modifier.mySpatialHoverEffect = ...
```

