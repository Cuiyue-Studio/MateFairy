An Option is a basic component in the PICO design specification that features a "selected" state. You can combine multiple option elements to flexibly implement both "multiple selection" and "single selection" scenarios.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e5ec39173364dfdbec360a323166e9e~tplv-goo7wpa0wc-image.image)
## API Surface

* `selected`: Indicates whether this item is selected.
* `onSelectChange`: This callback is triggered when the selection state changes. For example, the user clicked Option.
* `content`: The content area of Option, typically used together with `Text`. By default, the color is determined by the `colors` parameter.
* `icon`: Optional; the icon for Option, typically used together with `Icon`. By default, the `Icon` follows the `colors` property’s `contentColor`. If you want to retain the original color of the `Icon`, you can set the `Icon`’s `tint` parameter to `Color.Unspecified`.
* `enabled`: Specifies whether this control is enabled.

## Basic usage
```Kotlin
@Composable
fun OptionSimpleSample() {
    var checked by remember { mutableStateOf(false) }
    Option(
        selected = checked,
        onSelectChange = { checked = !checked }
    ) {
        Text("label")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/90e54ddeab74403abf3d2857c35a4366~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

The above code will create an Option that is initially unselected. When the user clicks Option, `onSelectChange lambda` will update the `checked` state. Here is an example of an Option with `Icon`:
```Kotlin
@Composable
fun OptionWithIconSample() {
    var checked by remember { mutableStateOf(false) }
    Option(
        selected = checked,
        onSelectChange = { checked = !checked },
        icon = {
            Icon(
                painter = painterResource(R.drawable.Start),
                contentDescription = null
            )
        }
    ) {
        Text("Star")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a2d2f876acfe4dcf8ca58d151814f5b3~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**
### Custom colors
By setting the `colors` parameter combined with the `OptionDefaults.optionColors()` function, developers can customize Option colors. As follows:
```Kotlin
@Composable
private fun 
OptionSample() {
    var selected by remember { mutableStateOf(false) }
    Option(
        selected = selected,
        onSelectChange = { selected = it },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_sui_rating_star),
                contentDescription = null
            )
        },
        colors = OptionDefaults.optionColors(
            checkedContentColor = Color.Red,
            checkedContainerColor = Color.Yellow,
            unCheckedContentColor = Color.Blue,
            unCheckedContainerColor = Color.Gray
        )
    ) {
        Text("Star")
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1b9e7ce282e141aab11d58ff2765b7d9~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

### Multiple choice and single choice
Option can be used with other containers, such as Column, Row, and FlowRow. You can combine multiple Options to customize single or multiple selection operations.
In the following example, there is a set of data definitions:
```Kotlin
class Item(val title: String) {
    // selected is defined as State. When it changes, it can trigger Compose recomposition.
    var selected by mutableStateOf(false)
}
// Defines a group of fruits
val items = listOf(
    Item("Option 1"),
    Item("Option 2"),
    Item("Option 3"),
    Item("Option 4"),
    Item("Option 5"),
)
```

#### Multiple selection
When users are expected to select multiple fruits, you can edit the following code:
```Kotlin
@Composable
private fun OptionMultiSelectionSample() {
    val selectedInfo = items.filter { it.selected }.joinToString(separator = ", ") { it.title }
    Column {
        Display the selected result
        Text("You've selected: $selectedInfo")
        // You can use a flow layout to contain tags
        FlowRow(modifier = Modifier.border(1.dp, Color.Gray)) {
            items.forEach { item ->
                Option(
                    The status of the three options
                    selected = item.selected,
                    // 4 Change the selected state after clicking
                    onSelectChange = { item.selected = it },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(item.title)
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ed84f0e0c93645278168178f196cbd89~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

#### Single choice
When you need to restrict users to selecting only one option:
```Kotlin
@Composable
private fun OptionSingleSelectionSample() {
    val selectedInfo = items.filter { it.selected }.joinToString(separator = ", ") { it.title }
    Column {
        // Display the selected results
        Text("You've selected: $selectedInfo")
        // You can use flow layout to contain tags
        FlowRow(modifier = Modifier.border(1.dp, Color.Gray)) {
            items.forEach { item ->
                Option(
                    selected = item.selected,
                    onSelectChange = {
                        // Reset the selected state of other items
                        items.forEach { it.selected = false }
                        Updating the selected state of the current Option
                        item.selected = it
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(item.title)
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb978a24d482489d9c72f82c4226f693~tplv-goo7wpa0wc-image.image" width="1280px" /></div>




