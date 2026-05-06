Augment is a container defined under the PICO design specification that is intended to be placed outside the main window and can be used to implement popup effects.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c7e00a946bdb43099d809e85d6cbfd34~tplv-goo7wpa0wc-image.image)
## API Surface

* `anchor`: The anchor in Augment is a normalized point relative to the top-left corner of the window container. (0, 0, 0) represents the point at the top-left corner with a z-axis value of 0, while (1, 1, 1) represents the point at the bottom-right corner with a z-axis value of 1.
* `alignment`: A normalized two-dimensional point representing a point relative to Augment itself, this point will be aligned with `anchor`. `0,0` indicates that the top-left corner of the widget is aligned with the anchor point, and `1,1` indicates that the bottom-right corner of the widget is aligned with the anchor point.
* `offset`: The absolute offset to be applied after `anchor` and alignment have been applied.
* `rotation3D`: Three-dimensional rotation of Augment relative to itself.
* `followViewpoints`: The viewpoints that Augment needs to follow. By default, it is provided by `ViewPoint.All`.
* `content`: Content of Augment

## Basic usage
```Kotlin
@Composable
private fun AugmentDemo() {
    val anchor by remember { mutableStateOf(NormalizedPoint3D(0f, 0f, 0f)) }
    var showAugment by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.size(500.dp)) {
        Button({
            showAugment = !showAugment
        }) {
            Text("Show/Hide Augment")
        }

        if (showAugment){
            // Set the anchor to the top-left corner and the alignment to TopLeft, that is, the top-left corner of Augment will align with the top-left corner of the main window.
            Augment(
                anchor = anchor,
                alignment = AugmentContentAlignment.Center,
            ) {
                Box(modifier = Modifier.size(100.dp)) {
                    Text("Augment Content")
                }

            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0e8e27c655a9463bb8c179fc6d8edbb7~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**

* `alignment` can be modified, and in combination with `anchor`, Augment can be positioned anywhere in the main window.
* You can modify `rotation3D` so that Augment achieves a 3D rotation effect.

```Kotlin
@Composable
private fun AugmentDemo() {
    val anchor by remember { mutableStateOf(NormalizedPoint3D(0f, 0f, 0f)) }
    var showAugment by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.size(500.dp)) {
        Button({
            showAugment = !showAugment
        }) {
            Text("Show/Hide Augment")
        }

        if (showAugment){
            Augment(
                anchor = anchor,
                // Set center so that the center point of the current augment aligns with the top-left corner of the anchor's current main window
                alignment = AugmentContentAlignment.Center,

                // Rotate 60 degrees around the x-axis
                rotation3D = Rotation3D(degree = 60f, axis = RotationAxis3D.X)
            ) {
                Box(modifier = Modifier.size(100.dp)) {
                    Text("Augment Content")
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9446d2534d544ea38ef406c8998e559f~tplv-goo7wpa0wc-image.image" width="1280px" /></div>


