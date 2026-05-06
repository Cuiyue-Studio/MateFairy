NumberField, designed according to the PICO design guidelines, allows users to create a numeric input field with increment and decrement buttons.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/af5c20fa9bc645b7981f15f46b3477d3~tplv-goo7wpa0wc-image.image)
## API Surface
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8bb43e0c3bcb4fa78eff627582491c77~tplv-goo7wpa0wc-image.image)

* `value`: Current value of the numeric field.
* `onValueChange`: Callback triggered when the value changes.
* `increaseIcon`: The icon displayed on the increase button. By default, the icon is provided by the `NumberFieldDefaults.defaultIncreaseIcon()` method.
* `decreaseIcon`: The icon displayed on the decrease button. By default, the icon is provided by the `NumberFieldDefaults.defaultDecreaseIcon()` method.
* `stepLength`: The step size used to increase or decrease a value.
* `valueRange`: Valid value range for `value`.
* `colors`: Sets the colors for NumberField, such as background color, content color, and more. By default, these are provided by the `NumberFieldDefaults.numberFieldColors()` method.
* `size`: The size of the NumberField.
* `cornerSize`: Corner size for NumberField.
* `gap`: The spacing between `increaseIcon`, `value`, and `decreaseIcon`, which is provided by `NumberFieldDefaults.DefaultGap` by default.
* `enabled`: Indicates whether NumberField is enabled. Boolean value; default is true.
* `editable`: Specifies whether NumberField is editable. Boolean value, defaults to true.
* `textStyle`: The text style of NumberField.
* `keyboardOptions`: Keyboard control for NumberField. By default, numeric input is enabled, but you can customize this setting to allow other keyboard input methods.

## Basic usage
```Kotlin
@Composable
fun NumberFieldSample() {
    var value by remember { mutableIntStateOf(0) }
    NumberField(value = value, onValueChange = { value = it })
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/461187b6c9d54e62bf31b5d5a31c3d17~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**

* You can configure the icon display of NumberField using `decreaseIcon` and `increaseIcon`.
* You can use `stepLength` to set the step size for each increase or decrease of `value`, and use `valueRange` to limit the range of `value`.

```Kotlin
@Composable
fun NumberFieldStepLengthSample() {
    Column {
        Text("Custom Step + 2")
        var value by remember { mutableIntStateOf(0) }
        // Change the add and delete icons to custom icons, set the step to 2, set the range from -10 to 10, and observe button behavior
        NumberField(value = value, onValueChange = { value = it }, stepLength = 2, decreaseIcon = {
            AnyIcon()
        }, increaseIcon = {
            AnyIcon()
        }, valueRange = IntRange(-10,10))
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d1857c03aa6f4a3eacf0d6b7dbf640ba~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



