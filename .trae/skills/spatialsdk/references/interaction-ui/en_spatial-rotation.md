Use the `rotation3D` interface to allow UI elements to rotate realistically in space. You can specify the rotation angle, rotation axis, and rotation center point in the interface. Code sample:
```Kotlin
@Composable
fun Rotation3DSample() {
    Box(
        modifier =
            // Rotate the view 95° around the y-axis (3D rotation)
            Modifier.rotate3D(degree = 95f, axis = RotationAxis3D.Y)
                .size(200.dp)
                .background(
                    brush =
                        Brush.radialGradient(
                            colors = listOf(Color.Green, Color.Red, Color.Yellow, Color.White),
                        ),
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(text = "Rotated circle", color = { Color.White })
    }
}

/** Demonstrate a continuously rotating 3D Box */
@Composable
fun RotatingBox() {
    // Infinite animation: degree loops from 0° to 360°
    val degree by
        rememberInfiniteTransition("Rotation3D")
            .animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween()),
                label = "degree"
            )

    Box(
        modifier =
            Modifier.size(100.dp).background(Color.Green).rotate3D {
                // 3D rotation around the y-axis, using the center point as the pivot
                Rotation3D(degree = degree, RotationAxis3D.Y, NormalizedPoint3D.Center)
            },
        contentAlignment = Alignment.Center
    ) {
        // custom logic
    }
}
```

