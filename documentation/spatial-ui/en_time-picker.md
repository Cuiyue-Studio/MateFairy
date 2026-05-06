TimePicker is a control for selecting time units—hour, minute, and second—designed according to the PICO design specification.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/638f9e726d9a4c9688f2fc37568a7568~tplv-goo7wpa0wc-image.image)
## API Surface

* `config`: Used to configure which elements will be displayed. You can configure it to display hours, minutes, and seconds.
* `onHoursChanged`: Called when the selected hour changes.
* `onMinutesChanged`: Called when the selected minute changes.
* `onSecondsChanged`: Called when the selected seconds change.
* `gap`: The spacing between each element. The default value is provided by `TimepickerDefaults.DefaultGap`.
* `colors`: The wheel picker colors used to customize the selector appearance. The default value is provided by `WheelPickerDefaults.wheelPickerColors()`.

## Basic usage
```Kotlin
@Composable
private fun HMSPicker() {
    Column {
        Text(text = "Single time picker to select hour/minutes/seconds")
        var hour by remember { mutableStateOf("") }
        var sec by remember { mutableStateOf("") }
        var min by remember { mutableStateOf("") }
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "Current result = $hour:$min$sec",
            style = PicoTheme.typography.labelMedium
        )

        Timepicker(
            onHoursChanged = { hour = it.toString() },
            onMinutesChanged = { min = it.toString() },
            onSecondsChanged = { sec = it.toString() }
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/795df4f447a94e29930f58ec6a6fa287~tplv-goo7wpa0wc-image.image" width="2560px" /></div>

## **Advanced usage**

* You can customize the `config` by using the `TimepickerConfig.create` method to specify which options for hour, minute, and second are displayed, as well as the labels for these options.
* You can modify the default colors by providing `colors` through the `WheelPickerDefaults.wheelPickerColors()` method.

```Kotlin
@Composable
private fun HMPicker() {
    Column {
        Text(text = "Single time picker to select hour/minutes/seconds")
        var hour by remember { mutableStateOf("") }
        var sec by remember { mutableStateOf("") }
        var min by remember { mutableStateOf("") }
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = "Current result = $hour:$min$sec",
            style = PicoTheme.typography.labelMedium
        )

        // Display only hours and minutes; append 'h' after hours and 'm' after minutes
        val ele = remember {
            TimepickerConfig.create(
                TimepickerElement.hours("h"),
                TimepickerElement.minutes("m"),
            )
        }
        Timepicker(
            config = ele,
            onHoursChanged = { hour = it.toString() },
            onMinutesChanged = { min = it.toString() },
            onSecondsChanged = { sec = it.toString() }
        )
    }

}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/20e8325570764f9492dc683386ff5ebf~tplv-goo7wpa0wc-image.image" width="2560px" /></div>



