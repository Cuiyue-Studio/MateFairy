Switch is a basic control in the PICO design specification that allows toggling between two states. It can be used in scenarios such as turning a setting on or off, enabling or disabling a feature, selecting an option, and more. The control consists of two parts: the slider and the track. The thumb is the draggable part, and the track is the background. Users can drag the thumb to change the switch state.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/012cd954cb4d4f6c819c923f93867ab4~tplv-goo7wpa0wc-image.image)
## API Surface

* `checked`: Current state of the control.
* `onCheckedChange`: A callback function that is executed each time the state changes.
* `enabled`: Indicates whether the control is enabled.
* `colors`: Control colors. Users can customize the control's `checkedThumbColor`, `checkedTrackColor`, `checkedTrackShadowColor`, `uncheckedThumbColor`, `uncheckedTrackColor`, and `uncheckedTrackShadowColor`.

## Basic usage
```Kotlin
@Composable
fun SwitchSample() {
    var checked by remember { mutableStateOf(true) }
    Switch(
        checked = checked,
        onCheckedChange = { checked = it }
    )
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/72383ea2eaa54136b8bb163709bc9c04~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
Customize the color and the space occupied by the Switch control, for example, to control whether the sound is on or off in a recording scenario. The following demonstrates how to customize the use of color parameters and the occupied size.
```Kotlin
@Composable
fun SwitchSample() {
    var recordVoice by remember { mutableStateOf(true) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text( if (recordVoice)"AudioOn" else "AudioOff")
        Switch(
            checked = recordVoice,
            onCheckedChange = {
                recordVoice = it
            },
            //Customize the occupied size
            modifier = Modifier.size(60.dp, 60.dp),
            A custom color
            colors = SwitchColors.switchColors(
                checkedThumbColor = Color.White,
                checkedTrackColor= Color(0xFF3377FF),
                checkedTrackShadowColor = Color.LightGray,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0x1F3D3D3D),
                uncheckedTrackShadowColor= Color(0x0A7F7F7F)
            )
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f03cf75709ad46cca740c46fe7d824b7~tplv-goo7wpa0wc-image.image" width="1280px" /></div>




