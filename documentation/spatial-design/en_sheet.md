Sheet can help users perform small-scale tasks that are closely related to their current environment. Sheet is a modal view and will block user interaction with the parent view. The characteristics of modal views are as follows:

* Ensure that users receive key information and take action on it when necessary.
* Helps users perform specific, limited tasks without losing track of their previous context.
* Provide users with an immersive experience or help them focus on complex tasks

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b753d3e4779040cb843e479ac3461195~tplv-goo7wpa0wc-image.image)
## Location
The Sheet is center-aligned with the main window and moves forward 32 dp along the Z axis, while the parent window becomes dim. Use visual cues to suggest to users that the parent element is not interactive, and keep their attention on the current task.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/02c3b9811dd64a4bb8054b9eb6d3a2df~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5d014df75715434e8e2ba838bff62157~tplv-goo7wpa0wc-image.image)

1. Container: The container height adapts to its content, with a maximum height equal to the main window height minus 64dp.
2. Close button: Because Sheet is a modal window, a close button must be provided.

## Interactive behavior
### Does not affect the Caption Bar
The appearance of the Sheet does not affect interaction with the Caption Bar. When the Caption Bar is dragged, the Sheet maintains its positional relationship with the main window.
### Resize
Resizing the window does not enlarge or reduce the size of the content in the Sheet; it can only affect the height of the Sheet. The height of the Sheet adapts to its content, with a maximum height equal to the window height minus 64 dp. When the user shrinks the window, the height of the Sheet may decrease, which can result in incomplete content display. The solution is as follows:

* Allow users to scroll the page to view all content. When scrolling, the Sheet’s title and bottom operation area remain fixed and do not move with the scroll.
* Set the minimum window height to be greater than the Sheet height plus 64 dp.

### Slide content
If the Sheet reaches its maximum height and not all content is displayed, you can scroll to view all content. While scrolling, try to keep the Title Bar and important actions fixed in position as much as possible.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/931132ced8b14c86b83077afa8e20dfb~tplv-goo7wpa0wc-image.image)

