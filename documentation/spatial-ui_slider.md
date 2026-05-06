This article introduces the features and usage of the Slider, SegmentSlider, and SymbolSlider components.
## Slider
The Slider is a component defined by the PICO design guidelines that allows users to set property values by dragging, for example, adjusting screen brightness or volume, and more. It includes a track and a slider. By moving the slider along the track, the user can obtain the value at the slider's current position.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fe71236fa3d642a0a8a2be9b9cfc30c4~tplv-goo7wpa0wc-image.image)
### API Surface

* `value`: The current value. The input value is between the minimum and maximum of `valueRange`.
* `valueRange`: Range of values, default: 0f to 1f
* `onValueChange`: A callback function that is executed each time the value changes.
* `onValueChangeFinished`: Callback function triggered when the value change is finished, which occurs when dragging or sliding stops.
* `enabled`: Sets whether the control is enabled.
* `sliderSpec`: A parameter that defines the occupied space and can specify `thumbAreaSize`, `thumbSize`, `thumbPressedSize`, and `trackHeight`.
* `colors`: Control colors can be customized via `SliderDefaults`, including `trackColor`, `progressColor`, `progressHighColor`, `thumbColor`, and `thumbHighColor`.

### Basic usage
```Kotlin
@Composable
fun SliderRegularSample() {
    Column {
        var sliderValue by remember { mutableStateOf(0f) }
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { },
            sliderSpec = SliderDefaults.Regular
        )
        Text(text = "$sliderValue")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8ad59c9e8db145dbb8e984d37347575f~tplv-goo7wpa0wc-image.image" width="864px" /></div>

### Advanced usage
Customize the current value, value range, size, and color of the slider.
```Kotlin
@Composable
fun SliderRegularSample() {
    Column {
        var sliderValue by remember { mutableStateOf(50f) }
        Slider(
            value = sliderValue,
            The custom value range
            valueRange = 0f..100f,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { },
            Custom dimensions
            sliderSpec = SliderDefaults.sliderSpec(
                thumbAreaSize = 60.dp,
                thumbSize = 20.dp,
                thumbPressedSize = 20.dp,
                thumbHoverSize = 20.dp,
                trackHeight = 40.dp
            ),
            Custom colors
            colors = SliderDefaults.sliderColors(
                thumbColor = Color.Red,
                trackColor = Color.Blue,
                progressColor = Color.White,
                segmentDotColor = Color.Yellow
            )
        )
        Text(text = "$sliderValue")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/66ffc4af763e4dc2958c7196bc90ac27~tplv-goo7wpa0wc-image.image" width="864px" /></div>

## SegmentSlider
SegmentSlider is a segmented slider bar designed according to the PICO design guidelines, allowing users to set attribute values by dragging. It can be used in step-by-step scenarios or in scenarios with multiple nodes. When the user slides the slider along the track, it snaps to the nearest node.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7533a8fcbfe451d9f12e957f2b7c0dd~tplv-goo7wpa0wc-image.image)
### API Surface

* `initialStep`: The current initial value, which is between 0 and `segmentCount`.
* `segmentCount`: Total number of segments.
* `onStepChange`: Callback function for step changes.
* `enabled`: Specifies whether the control is enabled.
* `sliderSpec`: Defines parameters for the space it occupies, allowing you to specify `thumbAreaSize`, `thumbSize`, `thumbPressedSize`, `trackHeight`, and `segmentDotSize`.
* `colors`: Colors of the control, which can be customized via SliderDefaults, including `trackColor`, `progressColor`, `progressHighColor`, `thumbColor`, `thumbHighColor`, `segmentDotColor`, and `segmentDotHighColor`.

### Basic usage
```Kotlin
@Composable
fun SegmentSliderSmallSample() {
    Column {
        var step by remember { mutableStateOf(2) }
        SegmentSlider(initialStep = step, segmentCount = 5, onStepChange = { step = it })
        Text(text = "$step")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a52fe5ac4464364bfa9d7ede2c7bb01~tplv-goo7wpa0wc-image.image" width="862px" /></div>

### Advanced usage
Customize the number of segments, color, and the size that SegmentSlider occupies.
```Kotlin
@Composable
fun SegmentSliderSmallSample() {
    Column {
        var step by remember { mutableStateOf(2) }
        SegmentSlider(
            initialStep = step,
            modifier = Modifier.size(600.dp, 60.dp),
            Custom number of segments
            segmentCount = 5,
            onStepChange = {
                step = it
            },
            //Custom slider size parameters
            sliderSpec = SliderDefaults.sliderSpec(
                thumbAreaSize = 50.dp,
                thumbSize = 40.dp,
                thumbPressedSize = 40.dp,
                thumbHoverSize = 40.dp,
                trackHeight = 60.dp,
                segmentDotSize = 16.dp
            ),
            Custom colors
            colors = SliderDefaults.sliderColors(
                thumbColor = Color.White,
                trackColor = Color(0x3D919191),
                progressColor = Color.White,
                segmentDotColor = Color.DarkGray,
                thumbHighColor = Color.White,
                segmentDotHighColor = Color.White
            )
        )
        Text(text = "$step")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/620e81ed7c9f4766bae970e5a3c17b41~tplv-goo7wpa0wc-image.image" width="864px" /></div>

## SymbolSlider
SymbolSlider is a slider designed according to the PICO design guidelines. It allows users to set attribute values by dragging, and features a symbol displayed at the head of the slider. This component can be used in scenarios such as adjusting screen brightness, volume, and more, and supports customization of the symbol shown at the head. When the user slides the slider along the track, the displayed symbols can be updated based on different values.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/676a7bb023f046108959ee6c49c6a171~tplv-goo7wpa0wc-image.image)
### API Surface

* `value`: The current value. The input value is between the minimum and maximum values of `valueRange`.
* `valueRange`: The default range is 0f to 1f, and the range can be customized.
* `onValueChange`: Callback function for value changes, executed each time the value changes.
* `icon`: Top symbol, customizable by the user.
* `onValueChangeFinished`: A callback function that is triggered when a value change is completed, which occurs when dragging or sliding stops.
* `enabled`: Specifies whether the control is enabled.
* `sliderSpec`: Parameters that define the occupied space, including `thumbAreaSize`, `thumbSize`, `thumbPressedSize`, and `trackHeight`.
* `colors`: Control colors, which can be customized via SliderDefaults for `trackColor`, `progressColor`, `progressHighColor`, `thumbColor`, and `thumbHighColor`.

### Basic usage
Add a custom icon display
```Kotlin
@Composable
fun SymbolSliderSimple() {
    var sliderValue by remember { mutableStateOf(0f) }
    SymbolSlider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        Add icon
        icon = {
            Icon(
                painter =
                painterResource(
                    id = R.drawable.sample_circle
                ),
                contentDescription = null
            )
        },
        onValueChangeFinished = {},
        sliderSpec = SliderDefaults.Regular
    )
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9db41c678cac4fad8cfbda48e0d4de05~tplv-goo7wpa0wc-image.image" width="860px" /></div>

### Advanced usage
SymbolSlider, with customizable color, size, and dynamic icon switching, can be used in scenarios such as volume, lighting, and more.
```Kotlin
@Composable
fun SymbolSliderRegularSample() {
    var sliderValue by remember { mutableStateOf(0f) }
    SymbolSlider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        Custom icons
        icon = {
            Icon(
                painter =
                painterResource(
                    id =
                    if (sliderValue > 0) R.drawable.sample_open_voice
                    else R.drawable.sample_close_voice
                ),
                contentDescription = null
            )
        },
        onValueChangeFinished = {},
        Custom dimensions
        sliderSpec = SliderDefaults.sliderSpec(
            thumbAreaSize = 60.dp,
            thumbSize = 20.dp,
            thumbPressedSize = 20.dp,
            thumbHoverSize = 20.dp,
            trackHeight = 40.dp
        ),
        Custom colors
        colors = SliderDefaults.sliderColors(
    thumbColor = Color.White,
    trackColor = Color.LightGray,
    progressColor = Color(0x1FFFFFFF),
    segmentDotColor = Color.White
)
        )
    )
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4f20b34db284435aa5580802ba133319~tplv-goo7wpa0wc-image.image)


