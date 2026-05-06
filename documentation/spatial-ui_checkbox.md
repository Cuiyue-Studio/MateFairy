This article introduces the features and usage of the CheckBox and TriStateCheckbox components.
## CheckBox
CheckBox is a basic control provided under the PICO design guidelines that allows users to select one or more options from a list. You can enable or disable a feature using a checkbox; you can make independent choices from multiple options in a list, such as agreeing to and accepting a protocol.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5829538bc59d4f7891ab792b5efa1d14~tplv-goo7wpa0wc-image.image)
### API Surface

* `checked`: Indicates whether it is currently selected.
* `onCheckedChange`: Callback for checked state changes. The callback is executed when the checked state changes.
* `enabled`: Indicates whether it is enabled.
* `contentSize`: Size, which can be customized using the `CheckboxContentSize` method.
* `colors`: The color, which can be customized using the `CheckboxColor` method.

### Basic usage
```Kotlin
@Composable
fun CheckBoxSample() {
    var checked by remember { mutableStateOf(true) }
    Checkbox(
        checked = checked,
        onCheckedChange = { checked = !checked }
    )
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c8a4648e3d74c6dbab8debb21431c2a~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage
Color can be customized and is used in scenarios where you can select multiple options individually, such as adding cream, coconut, or ice to milk tea.
```Kotlin
@Composable
fun CheckboxesExample33() {
    // Initialize state
    val childCheckedStates = remember {
        mutableStateListOf(
            false,
            false,
            false,
            false
        )
    }
    val names = listOf(
        "Option 1",
        "Option 2",
        "Option 3",
        "Option 4"
    )
    Column {
        childCheckedStates.forEachIndexed { index, checked ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(names[index])
                Checkbox(
                    checked = checked,
                    Custom colors
                    colors = CheckboxColor(
                        backgroundColor = Color.Gray,
                        contentColor = Color.White,
                        borderColor = Color.Black
                    ),
                    onCheckedChange = { isChecked ->
                        // Update the individual child state
                        childCheckedStates[index] = isChecked
                    }
                )
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5e8fb39a9fc749fa8e08c84af7b6bf6f~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## TriStateCheckbox
TriStateCheckbox is a checkbox with three states, suitable for scenarios such as all selected, partially selected, and unselected.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fbe9d4994d4740dcbf72b57b370401c5~tplv-goo7wpa0wc-image.image)
### API Surface

* `state`: The current status. Possible values in `ToggleableState` are On, Off, or ** Indeterminate.
* `onCheckedChange`: Callback for changes in the checked state. The callback is executed when the checked value changes.
* `enabled`: Indicates whether it is enabled.
* `contentSize`: Size. Can be customized via `CheckboxContentSize`.
* `colors`: Colors can be customized via `CheckboxColor`.

### Basic usage
```Kotlin
@Composable
fun TriStateCheckboxSample() {
    var state by remember { mutableStateOf(ToggleableState.Indeterminate) }
    TriStateCheckbox(state = state, onClick = {
    Next state for each state
        state = when (state) {
            ToggleableState.On -> ToggleableState.Off
            ToggleableState.Off -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.On
        }
    })
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bfb61af79e6749bf91bc4f148c86cdea~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Advanced usage
Applicable to scenarios where multiple conditions must be met simultaneously in order to achieve a specific goal.
```Kotlin
@Composable
fun CheckboxExample() {
    val childCheckedStates = remember { mutableStateListOf(false, false, false) }
    //Update state based on the states of multiple sub-controls
    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("All")
            TriStateCheckbox(
                state = parentState,
                onClick = {
                    val newState = parentState != ToggleableState.On
                    childCheckedStates.forEachIndexed { index, _ ->
                        childCheckedStates[index] = newState
                    }
                }
            )
        }
        childCheckedStates.forEachIndexed { index, checked ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Option ${index + 1}")
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        // Update the individual child state
                        childCheckedStates[index] = isChecked
                    }
                )
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2b483b4602fd46a499e68d9d9fa213fa~tplv-goo7wpa0wc-image.image" width="1280px" /></div>




