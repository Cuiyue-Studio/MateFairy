Text Selector is a tool or feature used to locate and select specific segments within textual content. Its primary function is to help users or programs accurately obtain the required text information for subsequent operations such as copying, editing, analyzing, and processing.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fbaa8cd021ad481a8f16791e5c40fc1b~tplv-goo7wpa0wc-image.image)
## Anatomy
The composition of the Text Selector primarily refers to the visual elements and interactive forms presented in the user interface. While the selector’s visual appearance can vary significantly across different scenarios, its core purpose is to use visual feedback to enable users to clearly perceive the selection criteria, matching range, and operation status.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/87c40a44a0b24e7e992227c1e72fd8b5~tplv-goo7wpa0wc-image.image)

1. **Highlight the selected region**
   The background of selected text is displayed in white, creating a clear contrast with unselected text and visually indicating the selection range.
2. **Selector**
   * Appearance: During selection, the cursor changes to the "I" shape (text input mode), indicating that the user can currently perform a selection action.
   * Boundary control points: When text is selected, small draggable points appear at the beginning and end of the selected text, allowing users to drag and adjust the selection range.
3. **Tips for assisting operations**
   After selecting text, a floating toolbar may appear, visually presented as text, icons, or in user-defined formats to guide subsequent actions.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/25a2c3cf61ec4923b763b7f2e265e843~tplv-goo7wpa0wc-image.image)

### Toolbar

* The toolbar can contain text, icons (Icon), or containers. The content in the red section can be defined by developers, for example, its color.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0dd09fc950744e2b812167c2a92cbe1f~tplv-goo7wpa0wc-image.image)
* The toolbar supports grouping, with no restrictions on the number or position of groups.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bf28e23c477c4ca0bdd011996f0d8431~tplv-goo7wpa0wc-image.image)

### Dimensions

* The width automatically adjusts based on the content to fully display the Action text. The maximum width of size is set to 480 dp. When the content exceeds this maximum width, left and right paging operations are supported.
* The height of the background box for selected text is equal to the line height.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c6cfa9c25196433d9ca36835c97254c5~tplv-goo7wpa0wc-image.image)
### Location
Positioned 8 dp above the selector's starting point (in actual implementation, 24 dp from the selected text). The Z-axis elevation matches that of the Menu, and it is centered on the selector's ending position.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c6312c70743a4263a0b6677923d624b0~tplv-goo7wpa0wc-image.image)
## Interactive behavior

* The selector's non-mouse and mouse interactions are as follows. The red areas indicate interactive hotspots.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e1d9a27e3c374860833959d606b65475~tplv-goo7wpa0wc-image.image)
* The interactive states of the toolbar are as follows: when the toolbar contains icons or containers, you can configure a Tooltip to appear when hovering over an icon.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/328bfe92ecb644e699656178cd636e61~tplv-goo7wpa0wc-image.image)
