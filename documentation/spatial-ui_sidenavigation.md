SideNavigation is a navigation control for sidebars defined by the PICO design guidelines. It consists of a top title section and multiple sections, and is commonly used for navigation setup, sidebar menu grouping, and similar scenarios. Structurally, it can be divided into a header and a navigation area. You can customize the title and display multiple categories grouped together.

* Header: The header of SideNavigation is located at the top and can contain a title, a search box, and other composite controls.
* Navigation area: The region excluding the header, which supports vertical scrolling and is used to place SideNavigationSection (for grouped navigation display) and SideNavigationItem (for containing content).

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dd81091e59de47a08c780fdb2e167d3d~tplv-goo7wpa0wc-image.image)
## API Surface

* SideNavigation
   * `contentPadding`: Content's inner padding. The default horizontal value is 24 dp.
   * `header`: The content at the top of the control, which is optional and typically includes a title and a search box.
   * `content`: The content of the control. You may place `SideNavigationSection` and `SideNavigationItem`.
* SideNavigationSection
   * `contentPadding`: Inner padding for group content; top padding defaults to 16 dp.
   * `titlePadding`: The padding for the group's title. The default value is `DefaultSectionTitlePadding`.
   * `title`: Header content of the group.
   * `content`: Group content, where `SideNavigationItem` is placed.
* SideNavigationItem
   * `selected`: Indicates whether the item is selected.
   * `horizontalArrangement`: The horizontal layout of the three sections—`leading`, `content`, and `trailing`—within an item.
   * `shape`: The shape of the item; the default is `RoundedCornerShape`.
   * `contentPadding`: Item padding.
   * `colors`: The color of the item, which can be customized using the `SideNavigationItemColors` method.
   * The `leading` is the content at the top of the item; it is optional and is typically an `Icon` or `Image`.
   * `trailing`: Optional content at the end of an item, commonly used with components such as `Icon`, `Button`, `Badge`, and `Switch`.
   * `content`: the content of the item.

## Basic usage
Set navigation categories in the sidebar sections and synchronize content updates; this is commonly used in secondary sidebar navigation.
```Kotlin
@Composable
fun SideNavigationSample() {
    val pins =
        listOf(
            "Recents",
            "Favorites",
            "Applications",
            "Documents",
        )

    val currentSelectedText = remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SideNavigation(
            modifier = Modifier.fillMaxHeight().weight(0.3f),
            header = {
                Column {
                    Box(
                        modifier =
                        Modifier.padding(
                            start = 8.dp,
                            top = 26.dp,
                            bottom = 26.dp,
                        )
                    ) {
                        Text(
                            "Settings",
                            style = PicoTheme.typography.titleLarge,
                            maxLines = 1,
                        )
                    }
                }
            }
        ) {
            pins.forEach {
                // Sidebar item
                SideNavigationItem(
                    selected = currentSelectedText.value == it,
                    modifier = Modifier.clickable { currentSelectedText.value = it },
                ) {
                    Text(it, maxLines = 1)
                }
            }
        }
        Box (Modifier.weight(0.7f).fillMaxHeight().background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(currentSelectedText.value)
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/76725035826b48a3a2b99f6dd4c669de~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
There may be multiple groups in the sidebar navigation, typically in scenarios where the header contains a search box.
```Kotlin
@Composable
fun SideNavigationSample() {
    val pins =
        listOf(
            "Recents",
            "Favorites",
            "Applications",
            "Documents",
        )

    val tags =
        listOf(
            Color.Red to "Red",
            Color.Green to "Green",
            Color.Blue to "Blue",
            Color.Yellow to "Yellow",
            Color.Cyan to "Cyan",
            Color.Magenta to "Magenta",
            Color.White to "White",
        )

    val currentSelectedText = remember { mutableStateOf("") }

    SideNavigation(
        modifier = Modifier.fillMaxHeight(),
        Custom header
        header = {
            Column {
                Box(
                    modifier =
                        Modifier.padding(
                            start = 8.dp,
                            top = 26.dp,
                            bottom = 26.dp,
                        )
                ) {
                    Text(
                        "Settings",
                        style = PicoTheme.typography.titleLarge,
                        maxLines = 1,
                    )
                }
                Box(modifier = Modifier.padding(bottom = 24.dp)) { SimpleSearch() }
            }
        }
    ) {
        pins.forEach {
            SideNavigationItem(
                selected = currentSelectedText.value == it,
                modifier = Modifier.clickable { currentSelectedText.value = it },
                leading = { AnyIcon(iconSize = 20.dp) },
            ) {
                Text(it, maxLines = 1)
            }
        }
        Sidebar groups
        SideNavigationSection(title = { Text("Tags") }) {
            tags.forEach {
                SideNavigationItem(
                    selected = currentSelectedText.value == it.second,
                    modifier = Modifier.clickable { currentSelectedText.value = it.second },
                    leading = {
                        Box(
                            modifier =
                                Modifier.padding(6.dp)
                                    .size(20.dp)
                                    .background(it.first, shape = CircleShape)
                                    .padding(4.dp)
                        )
                    },
                ) {
                    Text(it.second, maxLines = 1)
                }
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bc75cd5554024ce4a544847d3a1c7b38~tplv-goo7wpa0wc-image.image" width="1280px" /></div>



