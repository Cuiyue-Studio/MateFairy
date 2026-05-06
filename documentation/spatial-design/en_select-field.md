Select Field is commonly used to create dropdown selection boxes that allow users to choose one or more values from a predefined list of options. Typically, a dropdown box displays multiple options for users to select, and users can also enter their own input.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5f4c1f15a45c42588a145b67f30ad18f~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2641e7f5d8494b3f8c082ed591abf382~tplv-goo7wpa0wc-image.image)

1. A visible interactive entry point typically displays the currently selected item. If no item is selected, a placeholder prompt is shown, such as "Please select a city."
2. Interactive button: A dropdown button displays a list of options when clicked by the user.

## Interactive behavior

1. **Expand and collapse**
   * Automatically expand the Option Menu when it receives focus. Option Menu only provides quick selections or recommended content; users can enter their own content.
   * Automatic collapse after selecting an option. In single-select mode, clicking an option automatically collapses the list. In multi-select mode, the list does not collapse automatically; users must confirm manually.
   * Click outside area: When expanded, clicking other blank areas on the page will automatically collapse the list.
2. **Expansion direction**
   Right-align the list and the Select Field. The width of the list must not exceed that of the Select Field. The length of the list can be set according to the content. If there is too much content, you can use a fixed length and enable scrolling.
3. **Filter**
   When the list is expanded, entering text allows options to be filtered in real time (for example, entering "sports" displays "sports center"), improving selection efficiency.
4. **Select feedback**
   * A checkmark icon (✓) is displayed next to options.
   * The trigger area updates in real time to display the content of the selected item. For example, it changes from "Please select" to "Sports Center (current application)."
5. **Status feedback**
   * On focus: The trigger area displays a highlighted style.
   * On hover: The option currently under the mouse pointer in the options list displays the hover style (background changes).
   * When disabled: The trigger area is grayed out, cannot be clicked or focused, and provides no feedback on mouse hover.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/14d199eae9e34d0493a7fafd39977a02~tplv-goo7wpa0wc-image.image)

