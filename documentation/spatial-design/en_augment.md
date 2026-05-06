Augment is used to present controls and information related to windows and to highlight important actions.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/040c41d779f542879fbd059354c86525~tplv-goo7wpa0wc-image.image)
## Position
Augment is displayed around its associated window, and the distance between Augment and the edge of the associated window should be less than or equal to 32 dp. If Augment is positioned in front of the associated window, a forward depth of 16 dp is recommended.
## Principles

* When using multiple Augments at the same time, avoid making the area around the window too crowded. Otherwise, it will make the App appear more complex, distract users from important content, and disrupt the overall visual balance of the window. Please refer to the range of human visual focus described in "[PICO design guidelines](/pico-design-guidelines)" when designing.
* In general, Augment should remain visible to meet users' ongoing access needs. When users are immersed in viewing content such as videos, photos, or other content, Augment can be appropriately hidden.
* You should try to ensure that the width of the horizontal Augment does not exceed the width of the main window surface, and ensure that the height of the vertical Augment does not exceed the height of the main window surface. Otherwise, this will disrupt the overall balance of the window and interfere with the user's ability to read and interact with other content.
* Avoid placing Augment too far from its associated window, as this may result in a loss of visual association and cause confusion for users when reading and interacting with it. The distance between Augment and the edge of the main plane of the window should be less than or equal to 32 dp. If Augment is positioned in front of the main window, a forward depth of 16 dp is recommended.
* Give priority to using system-provided tab bars and toolbars; there is no need to use augment to create these components.
* When using Augment, it is recommended to follow users' operational and reading habits:
   * If Augment is used for navigation between first-level content sections, place it on the left side or at the top of the main window.
   * If Augment is used to display the main actions for the current page, place it below the main window.
   * If Augment is used to display auxiliary actions and information for the current page, place it on the right side of the main window.
