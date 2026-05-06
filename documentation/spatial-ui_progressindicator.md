This article introduces the capabilities and usage of the LinearProgressIndicator, CircularProgressIndicator, and SymbolicCircularProgressIndicator components.
## LinearProgressIndicator
LinearProgressIndicator is a basic linear progress component under the PICO design specification. Its progress is determinate and consists of a background and a foreground. It is non-interactive and is commonly used in scenarios such as "loading content" and "file upload".
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/085d6e9403b3453599c36eeb43b46a07~tplv-goo7wpa0wc-image.image)
### API Surface

* `progress`: Callback function for progress, returns a float value representing the progress.
* `colors`: Progress bar colors. You can customize the current progress color using `indicatorColor` and the background color using `backgroundColor`.
* `edgeStyle`: The edge style at both ends of the progress line in the control. Provides two styles: `RoundCorner` and `Flat`, with  ** `RoundCorner` as the default.
* `height`: The height of the progress bar.

### Basic usage
Basic usage of the linear progress bar.
```Kotlin
@Composable
fun SimpleLinearProgressIndicatorSample() {
    var progress by remember { mutableStateOf(0f) }
    Column {
        LinearProgressIndicator({
            progress
        })
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = {
                progress += 0.1f
                if (progress > 1f) {
                    progress = 0f
                }
            }
        ) {
            Text(text = "Click to Update")
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/25de81d8d4434055a07701874b5c280c~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage
Customization of the progress bar's colors, height, and edgeStyle is commonly used in scenarios such as displaying the loading progress of network resources.
```Kotlin
@Composable
private fun LinearProgressExample() {
    val progressAnimatable = remember { androidx.compose.animation.core.Animatable(0f, 0.03f) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val title = when (progressAnimatable.value) {
        0f -> "Download"
        in 0f ..< 1f -> "Loading"
        else -> "Download Complete"
    }
    LaunchedEffect(isLoading) {
        while (progressAnimatable.value <= 1f && isLoading) {
            scope.launch {
                progressAnimatable.animateTo(progressAnimatable.value + 0.02f)
            }
            delay(16)
        }
        if (progressAnimatable.value > 1f) {
            isLoading = false
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            isLoading = !isLoading
            if (progressAnimatable.value >= 1f) {
                scope.launch{
                    progressAnimatable.animateTo(0f)
                }
            }
        }) {
            Text(text = title)
        }
        Spacer(modifier = Modifier.size(10.dp))
        LinearProgressIndicator(
            progress = { progressAnimatable.value },
            Custom sizes
            modifier = Modifier.size(width = 300.dp, height = 10.dp),
            Custom colors
            colors = LinearProgressDefaults.linearProgressColors(
                indicatorColor = Color(0xFF3377FF),
                backgroundColor = Color(0x1F3D3D3D)
            ),
            //Custom edge shape
            edgeStyle = ProgressIndicatorEdgeStyle.RoundCorner,
            // Custom height
            height = LinearProgressDefaults.linearProgressHeight(10.dp)
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8e9c152dbf44442a19ff2ced3279815~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## CircularProgressIndicator
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a7853544ae44ca5af28b03126e34b75~tplv-goo7wpa0wc-image.image)
CircularProgressIndicator is a basic circular progress component under the PICO design guidelines. It is commonly used in scenarios such as "loading content", "file uploads", and more. It currently has the following two forms:

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);">

* **Indeterminate progress**: Continuously rotates, does not reflect progress
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/58d6c6a234154239ab7e4253ebd008e0~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);margin-left: 16px;">

* **Confirm progress**: Display specific progress
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4417858b262c469198320d0016a05baa~tplv-goo7wpa0wc-image.image)


</div>
</div>

### API Surface

* `progress`: Current progress.
* `colors`: The color(s) of the progress bar.
* `edgeStyle`: The edge style at the end of the control's progress line. Two styles are available: `RoundCorner` and `Flat`. The default is  ** `RoundCorner`.
* `progressSize`: Component size. PICO provides three styles: `Small`, `Regular`, and `Max`. By default, the size is `Small`, and users can customize the size via `CircularProgressDefaults`.
* `strokeWidth`: Width of the progress line. Defaults to 0.1 times the component size, with a minimum of 2 dp. It can be customized.

### Basic usage

* Simple usage of indeterminate progress bars
   ```Kotlin
   @Composable
   fun CircularIndicatorSample() {        
       CircularProgressIndicator()
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7795c5fc0f344941ba7f4f639cdc8cbf~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

* A simple method for determining progress
   ```Kotlin
   @Composable
   fun CircularIndicatorSample() {
       var progress by remember { mutableStateOf(0f) }
       Column(horizontalAlignment = Alignment.CenterHorizontally) {
           CircularProgressIndicator(
               progress = { progress })
           Spacer(modifier = Modifier.size(10.dp))
           Button(
               onClick = {
                   progress += 0.1f
                   if (progress > 1f) {
                       progress = 0f
                   }
               }
           ) {
               Text(text = "Click to Update")
           }
       }
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/256ca6d0585a4bc7840012716da34a10~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>


### Advanced usage
Control styles may vary depending on the scenario. You can customize the color, size, and the edge style and width of the progress line.
```Kotlin
@Composable
fun DownloadCircularProgressSample() {
    val interactionSource = remember { MutableInteractionSource() }
    var isDownloading by remember { mutableStateOf(false) }
    var isFinish by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = isDownloading) {
        if (isDownloading) {
            while (downloadProgress < 1f) {
                downloadProgress += 0.02f
                delay(16)
            }
            isFinish = downloadProgress >= 1f
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Download Progress ${downloadProgress.coerceAtMost(1.0f) * 100}%",
            style = PicoTheme.typography.titleLarge,
            color = Color.White
        )
        CircularProgressIndicator(
            modifier =
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true
            ) {
                isDownloading = !isDownloading
            },
            Custom edges
            edgeStyle = ProgressIndicatorEdgeStyle.Flat,
            Custom fill width
            strokeWidth = 10.dp,
            progress = { downloadProgress },
            A custom size
            progressSize = CircularProgressDefaults.circleProgressSize(
                size = 100.dp
            ),
            //A custom color
            colors = CircularProgressDefaults.circleProgressColors(
                backgroundColor = Color(0xFF292929),
                indicatorColor = Color(0xFF007AFF)
            )
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/76421c6c53354fb59a5799879acfd4c6~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## SymbolicCircularProgressIndicator
SymbolicCircularProgressIndicator is a basic circular progress indicator component defined by the PICO design guidelines. It is commonly used in scenarios such as "loading content" and "file upload," and allows custom icons to be added to represent the current progress status. As follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/adbd41c8cdd74a52ac0acfc2cba373f9~tplv-goo7wpa0wc-image.image)
### API Surface

* `progress`: Current progress.
* `progressSymbol`: `@Composable` callback function, typically used to display an `Icon`. Optional.
* `colors`: The colors of the progress bar.
* `edgeStyle`: The edge style at the ends of the control's progress line. Two styles are available: `RoundCorner` and `Flat`, with  ** `RoundCorner` as the default.
* `progressSize`: Component size. PICO provides three styles: `Small`, `Regular`, and `Max`. The default is `Small`, and users can customize the size via `CircularProgressDefaults`.
* `strokeWidth`: The width value of the progress line. The default is 0.1 times the control size, with a minimum of 2 dp. This value can be customized.

### Basic usage
```Kotlin
@Composable
fun SimpleSymbolicCircularIndicatorSample() {
    var progress by remember { mutableStateOf(0f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SymbolicCircularProgressIndicator(
            progress = { progress },
            progressSize = CircularProgressDefaults.Regular,
            Custom symbols
            progressSymbol = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sample_download),
                    contentDescription = null
                )
            }
        )
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = {
                progress += 0.1f
                if (progress > 1f) {
                    progress = 0f
                }
            }
        ) {
            Text(text = "Click to Update")
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/30fd7524998c44518ab0104298765107~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage
Supports customization of the color, size, edge style, and width of the progress line, and also allows switching icons at different stages of progress to notify users of the current progress status.
```Kotlin
@Composable
fun DownloadCircularProgressSample() {
    val interactionSource = remember { MutableInteractionSource() }
    var isDownloading by remember { mutableStateOf(false) }
    var isFinish by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = isDownloading) {
        if (isDownloading) {
            while (downloadProgress < 1f) {
                downloadProgress += 0.02f
                delay(16)
            }
            isFinish = downloadProgress >= 1f
        }
    }

     Icons in different states
    val resId = when {
        isFinish -> R.drawable.ic_sample_finish
        isDownloading -> R.drawable.ic_sample_pause
        else -> R.drawable.ic_sample_download
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Download Progress ${downloadProgress.coerceAtMost(1.0f) * 100}%",
            style = PicoTheme.typography.titleLarge,
            color = Color.White
        )
        SymbolicCircularProgressIndicator(modifier = Modifier.clickable(
            interactionSource = interactionSource, indication = null, enabled = true
        ) {
            isDownloading = !isDownloading
        },
            A custom edge
            edgeStyle = ProgressIndicatorEdgeStyle.Flat,
            // Custom fill width
            strokeWidth = 10.dp,
            progress = { downloadProgress },
            Custom sizes
            progressSize = CircularProgressDefaults.circleProgressSize(
                size = 100.dp
            ),
            Custom colors
            colors = CircularProgressDefaults.circleProgressColors(
                backgroundColor = Color(0xFF292929), indicatorColor = Color(0xFF007AFF)
            ),
            Custom symbols
            progressSymbol = {
                Icon(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(id = resId),
                    contentDescription = null
                )
            })
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/573fcae92d3843d78032ba3daad75799~tplv-goo7wpa0wc-image.image" width="1280px" /></div>


