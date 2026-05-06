SegmentControl is a component used to switch between multiple mutually exclusive options. It typically consists of a series of icons or text options arranged side by side, allowing users to modify the display by clicking to select one of the options. In terms of form, it can be divided into SegmentControl containers and SegmentItem container sub-items.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/def2996509614686a9962d4fc58fcf99~tplv-goo7wpa0wc-image.image)
## Usage restrictions
The minimum height of the component is controlled by `SegmentControlDefaults.Small.height`, which is currently 40 dp.
## API Surface

* SegmentControl
   * `backgroundColor`: SegmentControl container background color, by default provided by the `PicoTheme.colorTokens.FillTertiaryAlpha` method.
   * `itemSpace`: The distance between each SegmentItem in the horizontal direction. By default, this value (4 dp) is provided by the `SegmentControlDefaults.ItemSpace` method.
   * `contentPadding`: The internal padding of SegmentControl, by default provided by the `SegmentControlDefaults.ContainerPadding` method (4 dp).
   * `cornerRadius`: Controls the corner radius of the shape of SegmentControl. By default, the value is provided by the `SegmentControlDefaults.Small.containerCornerRadius()` method.
   * `content`: Provides content and internally contains one or more SegmentItem.
* SegmentItem
   * `selected`: Indicates whether the current SegmentItem is selected.
   * `textStyle`: Provides the text style for SegmentItem. By default, this is provided by the `SegmentControlDefaults.Small.textStyle()` method.
   * `colors`: The color value of SegmentItem, used to provide the color for the current selected or unselected state. By default, the value is provided by the `SegmentControlDefaults.colors()` method.
   * `title`: The custom display content for SegmentItem, usually `Text`.
   * `icon`: The custom display content for SegmentItem, typically an `Icon`. If both `title` and `icon` are present, `icon` and `title` will be arranged vertically, one after the other.
   * `contentPadding`: The inner padding of SegmentItem, with the default value provided by the `SegmentControlDefaults.Small.itemContentPadding()` method.
   * `gap`: Controls the spacing between `icon` and `title`. The default value is provided by the `SegmentControlDefaults.ItemGap` method.
   * paddings: MenuItem padding.
   * cornerSize: Size of the corner radius.

## Basic usage
```Kotlin
var selectIndex by remember { mutableStateOf(0) }
SegmentControl {
    // Set five SegmentItems
    repeat(5) { index ->
        SegmentItem(
            icon = {
                AnyIcon(
                    iconSize = 16.dp,
                )
            },
            selected = selectIndex == index,
            modifier = Modifier.clickable { selectIndex = index }
        )
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/87a4cf87fde54bb0a6e23fad5e7f105b~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## **Advanced usage**
You can modify the layout background style of SegmentControl using properties such as `backgroundColor` and `cornerRadius`, or customize `colors` through `SegmentControlDefaults.colors` to achieve more visual effects for selected and unselected SegmentItems.
```Kotlin
@Composable
fun SegmentControlDemo(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.width(400.dp)) {
        var selectIndex by remember { mutableStateOf(0) }
        // Change the default color to black and set the corner radius to 10dp
        SegmentControl(backgroundColor = Color.Black, contentPadding = 6.dp, cornerRadius = 10.dp) {
            repeat(5) { index ->
                // Display both the icon and the title, and set a gap of 6dp between them
                SegmentItem(
                    icon = {
                        AnyIcon(
                            iconSize = 16.dp,
                        )
                    },
                    title = {
                        Text(index.toString())
                    },

                    // Customize the color when selected. By default, SegmentItem is White, the content is LightGray, and when selected, the color changes to Red and White.
                    colors = SegmentControlDefaults.colors(Color.White,Color.LightGray,Color.Red,Color.White),
                    gap = 6.dp,
                    selected = selectIndex == index,
                    modifier = Modifier.clickable { selectIndex = index }
                )
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a4756852cf6946739a0843843074dd96~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



