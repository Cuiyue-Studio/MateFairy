Dialog Alert is used to communicate critical information to users that requires immediate attention, and is commonly found in warning prompts for destructive actions. It is displayed as a modal window before the application content, interrupts the user's current task, and disables other actions until the user responds. Therefore, it should be used with caution.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3a4e60b645114a47b977cd1276b39e3a~tplv-goo7wpa0wc-image.image" width="2000px" /></div>

## Position
Dialog Alert is center-aligned with the main Window and moved forward by 64dp along the Z axis. When a Dialog Alert appears, its parent window dims to indicate that the parent window is not interactive, and guides the user to focus their attention on the Dialog Alert.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/93e90c7ce26344ab9f4a9b619bbbe6b0~tplv-goo7wpa0wc-image.image" width="1440px" /></div>

## Layout
Dialog Alert provides two layout styles, horizontal and vertical, as shown below:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/45df5d7fc301436db3aee63f91f8d951~tplv-goo7wpa0wc-image.image" width="2880px" /></div>


* Horizontal layout: Recommended for most scenarios.
* Vertical layout: If button labels are too long, or if there are more than three buttons, making the lower action area too crowded, use a vertical layout.

## Anatomy
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e62f27188be644b29913c222ea97ad14~tplv-goo7wpa0wc-image.image" width="2880px" /></div>


1. Container: The container height adapts to its content, with a maximum height equal to the main window's height minus 64dp. Dialog Alert content must not be of the input data type, such as a text input box.
2. Icon (optional): Place the icon before the title.
3. Title (optional): Copy should be as clear and concise as possible.
4. Subtext (optional): Used to provide additional explanation.
5. Actions: At least one action is required, because the user needs to interact with it to close the Dialog Alert.


## Behavior
### Caption Bar
The appearance of Dialog Alert does not affect interactions with Caption Bar. When the Caption Bar is dragged, the Dialog Alert maintains its positional relationship with the main Window.
### Resize
Resizing the Window does not enlarge or reduce the size of the content in the Dialog Alert; it may only affect the height of the Dialog Alert.
The height of Dialog Alert adapts to its content, with a maximum height equal to the window height minus 64dp. When the user reduces the size of the Window, the height of the Dialog Alert may decrease, which can result in incomplete content display. The following are two solutions:

* Allow users to scroll the page to view the full content. When scrolling, the title and bottom action area of the Dialog Alert remain fixed and do not move as you scroll.
* Set the minimum height of Window to be 64dp greater than the height of Dialog Alert. See the [Window size specifications](/en_window).


