The menu component displays selectable list items as a floating window within a space. The list content is typically used with MenuItem, but the list content can also be fully customized.
Structurally, it can be divided into a main menu and a submenu.

* Main menu: Menu
* SubMenu: A secondary menu under Menu.

Menu content is typically implemented using MenuItem, but you can also freely customize the UI style of menu options.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/496e7cfb8c5741e28f65041a0ce83841~tplv-goo7wpa0wc-image.image)
The menu background color matches the system theme. Customization is not supported.
## Usage restrictions

* Width: Minimum `DimensionTokens.WidthMin`, Maximum `DimensionTokens.WidthExtraLarge`
* Height: maximum `DimensionTokens.HeightExtraLarge`

## API Surface

* Menu & SubMenu:
   * `content`: Menu content, usually a MenuItem, but can also be a custom View.
   * `onDismissRequest`: Callback triggered when the menu is hidden, for example, when clicking on a blank area outside the menu. You typically need to update the menu's visibility state here.
   * `position`: The position of the View relative to the anchor point.
   * `padding`: Menu padding.
   * `cornerRadius`: Size of the rounded corners.
* MenuItem
   * `title`: The title area, typically `Text`.
   * subtitle: Subtitle area, optional, typically contains `Text`.
   * subMenu: Slot for SubMenu.
   * onClick: An optional click callback, consistent with `modifier.clickable{}`
   * leadingIcon: Typically an `Icon` displayed in the left content area.
   * trailingIcon: Right-side content area, typically an `Icon`.
   * contentColors: Allows customization of MenuItem colors.
   * paddings: The inner padding of MenuItem.
   * cornerSize: Corner radius.

## Basic usage
Examples of Menu are as follows:
```Kotlin
@Composable
private fun ButtonWithMenu() {
    var showPopup by remember { mutableStateOf(false) }
    Box {
        // Anchor view
        Button(onClick = { showPopup = true }) {
            // Text
            Text(text = "Show Menu")
        }
        // Drop-down menu
        if (showPopup) {
            Menu(onDismissRequest = { showPopup = false }) {
                MenuItem(title = { 
                    Text("Option 1")
                })
                MenuItem(title = { 
                    Text("Option 2")
                })
                MenuItem(title = { 
                    Text("Option 3")
                })
            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/76b8ad375f674102adf5bd11d247e490~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

An example of using SubMenu is as follows:
```Kotlin
@Composable
private fun ButtonWithMenu() {
    var showPopup by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { showPopup = true }) {
            // Text
            Text(text = "Show Menu")
        }
        // Drop-down menu
        if (showPopup) {
            // Main menu
            Menu(onDismissRequest = { showPopup = false }) {
                //
                repeat(4) { index ->
                    var showSubMenu by remember { mutableStateOf(false) }
                    MenuItem(title = {
                        Text("Option $index")
                    }, onClick = {
                        showSubMenu = true
                    }, subMenu = {
                        if (showSubMenu) {
                            SubMenu(onDismissRequest = {showSubMenu = false}) {
                                MenuItem(
                                    title = { Text("Option") },
                                    onClick = { showSubMenu = false }
                                )
                            }
                        }
                    })
                }

            }
        }
    }
}
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4e4d55aefba84c22a0c468233667bdac~tplv-goo7wpa0wc-image.image" width="1280px" /></div>

## Advanced usage
### Anchor Point View rules

* The menu pops up based on the position of a View, with the anchor View being the direct parent View of the Menu. For example, in the following code, Column is the anchor view for Menu.
   ```Kotlin
   Row {
       Column {
           Menu()
       }
   }
   ```

* The padding of the Anchor View affects the alignment logic of the Menu. In the following code, although the actual size of the Box is 100 dp, the anchor for the Menu is the 60 dp yellow block, rather than being aligned based on the red area.
   Most components in the component library, such as Button and IconButton, include padding. As a result, when these components are used as anchors for a Menu, visual misalignment may occur. The best practice for resolving this issue is to place both the Button and the Menu inside the same Box.

   ```Kotlin
   Box(modifier = Modifier
       .size(100.dp)
       .background(Color.Red)
       .padding(20.dp)
       .background(Color.Yellow)
   ) {
       Menu()
   }
   ```

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/21167b3ae3c74b47866a6c87622e4711~tplv-goo7wpa0wc-image.image)
* Child Views of the Anchor View can affect the anchor area, so exercise caution when placing other Views inside the Anchor View. In the following code, CustomView causes Box to expand and fill the available space, which prevents Menu from aligning with Button.
   ```Kotlin
   Box {
       CustomView(modifier = Modifier.fillMaxSize)
       var showMenu by remember {mutableStateOf(false)}
       Button() {
           Text("show Menu")
       }
       if(showMenu) {
           Menu()
       }
   }
   ```


**Best practices**
Use a Box to contain the target View (such as a Button) and a Menu. Do not place any views inside the Box except for the target View and Menu. As follows:
```Kotlin
// Do not use any size-related Modifiers on Box
// Only place Anchor View and Menu inside Box; do not place other views.
Box {
    // Can be a Button or other components
    Button() {}
    // Popup menu relative to the Button's position
    Menu()
   
}
```

### The custom menu popup location
Spatial UI defines a set of position layouts for arranging menus.

* The horizontal arrangement around the anchor view, for example, a Button, is as follows:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/381127378f374bc3874e6a35ef986d98~tplv-goo7wpa0wc-image.image)
* Vertical arrangement rules are as follows:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6edffaa7d1934141aa6b53e9942c6b54~tplv-goo7wpa0wc-image.image)
* In theory, you can combine `rememberMenuPositionProvider` and `rememberSubMenuPositionProvider` to create multiple position definitions.
* Offset conforms to the definition of the View coordinate system.
   For example, when you need to achieve the following effect, you must use:
   ```Kotlin
   positionProvider = rememberMenuPositionProvider(
       horizontalPlacement = HorizontalPlacement.toStartOf(offset = -8.dp),
       verticalPlacement = VerticalPlacement.alignBottom
   )
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ee71547b669942a48be0fc74bc76db2f~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>

   Example:
   ```Kotlin
   @Composable
   private fun ButtonWithMenu() {
       var showPopup by remember { mutableStateOf(false) }
       Box {
           Button(onClick = { showPopup = true }) {
               // Text
               Text(text = "Show Menu")
           }
           // Drop-down menu
           if (showPopup) {
               Menu(
                   positionProvider =
                       rememberMenuPositionProvider(
                           horizontalPlacement = HorizontalPlacement.alignEnd(),
                           verticalPlacement = VerticalPlacement.above(offset = 10.dp)
                       ),
                   onDismissRequest = { showPopup = false }) {
                   repeat(4) { index ->
                       MenuItem(title = {
                           Text("Option $index")
                       })
                   }
   
               }
           }
       }
   }
   ```

   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d1171a5401c4dd197b866b4b35f4b1a~tplv-goo7wpa0wc-image.image" width="1280px" />   </div>


### Examples of using in combination with DropDown
```Kotlin
@Composable
private fun ButtonWithMenu() {
    var showPopup by remember { mutableStateOf(false) }
    // Record the selected index
    var selectedIndex by remember { mutableStateOf(-1) }
    // Menu data list
    val itemData = remember {
        listOf(
            "Option 1", "Option 2", "Option 3", "Option 4", "Option 5",
        )
    }
    Column {
        Text("You've chosen: ${itemData.getOrNull(selectedIndex)}")
        Box {
            Button(onClick = { showPopup = true }, trailingIcon = {
                Icon(
                    painter = painterResource(.R.drawable.ic_arrow),
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                // Text
                Text(text = "Choose from")
            }
            // Drop-down menu
            if (showPopup) {
                Menu(
                    onDismissRequest = { showPopup = false }) {
                    itemData.forEachIndexed { index, item ->
                        MenuItem(title = {
                            Text(item)
                        }, trailingIcon = {
                            Display the status
                            if (selectedIndex == index) {
                                Icon(painter = painterResource(id = R.drawable.ic_sample_listitem_check),null)
                            }
                        }, onClick = {
                            // Update selected index
                            selectedIndex = index
                            // Hide menu
                            showPopup = false
                        })
                    }

                }
            }
        }
    }
}
```


