This document introduces the capabilities and usage of the DatePicker and DateRangePicker components.
## DatePicker
DatePicker is a control for selecting dates, designed under the PICO design guidelines.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b44b1c473cdf41d9b695ede057fd9039~tplv-goo7wpa0wc-image.image)
### API Surface

* `onDateSelected`: Callback function called when a date is selected.
* `state`: The state of DatePicker. You can customize rememberDatePickerState to monitor changes in the internal state of DatePicker.
* `dateFormatter`: DatePicker formatter that provides a framework for formatting date displays and converts formatted dates into date values for input.
* `colors`: DatePicker colors, used to resolve the colors applied to this date picker in different states. By default, these are provided by `DatePickerDefaults.datePickerColors()`.
* `headerStyle`: Controls the header style of DatePicker.

### Basic usage
```Kotlin
@Preview
@Composable
fun DatePickerCannotSwitchYearSample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        var millis: Long? by remember { mutableStateOf(null) }
        // Get the current date using onDateSelected
        DatePicker(
            onDateSelected = { millis = it },
        )
        Text("Selected date: ${millis.formatToText()} ", color = Color.Black)
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a3cd3f085dca4760a9992e36ded13d9a~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### **Advanced usage**

* You can modify styles via `headerStyle` and set styles for other years.
* By providing `state` through `rememberDatePickerState`, you can set other dates as the default selection to customize the behavior.

```Kotlin
@Composable
fun DatePickerCannotSwitchYearSample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        var millis: Long? by remember { mutableStateOf(null) }
        DatePicker(
            onDateSelected = { millis = it },
            headerStyle = HeaderStyle.Dropdown,
            // Provide a default start time
            state = rememberDatePickerState(initialSelectedDateMillis = 1740693600000)
        )
        Text("Selected date: ${millis.formatToText()} ", color = Color.Black)
    }
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6a0cb638a8b24c53a61c4d3db95c018f~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## DateRangePicker
DateRangePicker is a component designed according to the PICO design guidelines that allows users to select a range of dates.
### API Surface

* `onStartSelected`: Callback function that is called when the start date is selected.
* `onEndSelected`: Callback function that is called when the end date is selected.
* `state`: The state of DateRangePicker. You can customize rememberDateRangePickerState to monitor changes in the internal state of DateRangePicker.
* `dateFormatter`: The DateRangePicker formatter provides a framework for formatting date displays and converts them into date input values.
* `colors`: Specifies the colors used by the DateRangePicker in different states. By default, these colors are provided by `DatePickerDefaults.datePickerColors()`.
* `headerStyle`: Controls the header style of the DateRangePicker.

### Basic usage
```Kotlin
@Composable
fun DateRangePickerSample() {
    var start: Long? by remember { mutableStateOf(null) }
    var end: Long? by remember { mutableStateOf(null) }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        DateRangePicker(
            onStartSelected = { start = it },
            onEndSelected = { end = it },
        )
        Text(
            "Selected date range: ${start.formatToText()} -- ${end.formatToText()}",
            color = Color.Black
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/575d7dfc643a4d3db9084910a9a2c639~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### **Advanced usage**
You can modify styles using headerStyle, set styles for other years, change the color property to customize the selected color, and more.
```Kotlin
/** DateRangePickerSample */
@Preview
@Composable
fun DateRangePickerSample() {
    var start: Long? by remember { mutableStateOf(null) }
    var end: Long? by remember { mutableStateOf(null) }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        DateRangePicker(
            headerStyle = HeaderStyle.Dropdown,
            onStartSelected = { start = it },
            onEndSelected = { end = it },
        )
        Text(
            "Selected date range: ${start.formatToText()} -- ${end.formatToText()}",
            color = Color.Black
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5e2e2cb0a36c49e3808ec17771ea0460~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



