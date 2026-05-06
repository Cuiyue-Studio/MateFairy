A button (Button) is a command component that can initiate an immediate action.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/715a978034f64a068e1732ee759fbe83~tplv-goo7wpa0wc-image.image)
## **Common categories of Button**

* Submission actions: such as "Submit", "Confirm", and "Save", used to perform the final operation and transmit data to the system.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a8a93a07ef984f5f9efe9b92e20760c2~tplv-goo7wpa0wc-image.image)
* Operation type: For example, "Select", "Edit", and "Copy" are used to perform specific actions on content.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/405fc00b62e04c799d9f71a09bb250af~tplv-goo7wpa0wc-image.image)
* Navigation category: For example, "Back", "Next", and "View details". These are used for navigation between pages or processes.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1d6473b3bdcf45f1b507a054833eba35~tplv-goo7wpa0wc-image.image)
* Functional category: Actions such as "Refresh", "Search", and "Print" trigger utility or auxiliary functions.
* Interactive feedback category: such as "Close popup" and "Cancel", used to interrupt or abandon the current operation.

## Anatomy
### Content
To ensure that each element within a button clearly communicates its purpose, buttons are categorized into three types: **text button**, **text + icon button**, and **plain text button without background**. For the **text + icon button**, the icon can be placed before or after the text.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9c061927d507428eab0e1713754f22fe~tplv-goo7wpa0wc-image.image)

1. Text 
2. The Leading Element is a header icon; for specific details, see icon button.
3. Trailing icon 

### Dimensions
The button features a fully rounded corner design and is available in four sizes: Min, Small, Regular, and Max. You can choose the appropriate size according to different scenarios and business requirements. As shown below, buttons of different sizes all have a minimum width.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0aa2bf9c6d0e467d9a134019c7b11ed9~tplv-goo7wpa0wc-image.image)
### Role
Buttons for different roles have different meanings, and typically one or two highlighted buttons are displayed in a view. Presenting users with too many prominent buttons increases their cognitive load, causing them to spend more time weighing options before making a decision. In contrast, buttons with filled backgrounds or vivid colors are typically more visually appealing and can help users quickly identify the action they are most likely to want to perform.
Divided into five types of role buttons:

1. Primary button: The default button that users are most likely to select. Usually, only one primary button is allowed in an operation area, for example, "Done".
2. Auxiliary button: On the interface, use the auxiliary button to perform moderately important actions.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3255b87fa7ca457881e66c6879f7cad1~tplv-goo7wpa0wc-image.image)
3. Accessible button: A button used in specific scenarios to enable the corresponding function, such as the green **Start Playing** button shown below.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b7590ab0adee4e49ab5270a43dd0e42b~tplv-goo7wpa0wc-image.image)
4. Other buttons are typically placed above images or text, and their background is usually blurred.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5f3168e8429c4c0b9e564daa1baabff6~tplv-goo7wpa0wc-image.image)
5. Borderless button: A borderless button does not have a background. This is used for infrequent operations in order to highlight other operations.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1cec724fe7994386ac729ac6f835667d~tplv-goo7wpa0wc-image.image)

As well as the **Danger** status button: used for risky operations such as deleting, moving, or modifying permissions. Such operations may cause irreversible impacts on system data, file structure, or software functionality, which can result in serious consequences such as data loss, system failure, or permission misconfiguration. To minimize potential risks caused by accidental operation, it is generally necessary to perform a secondary confirmation when carrying out such dangerous operations.

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/02c58471a8e7489b85f3dc86d08278ed~tplv-goo7wpa0wc-image.image)

## **Interactive behavior**
Buttons have four interaction states:

* Normal default state: The button's default display state when no action is taken and no special circumstances occur.
* Hover state: When the button is hovered over using a hand pose or other methods, it enters the hover state. The icon background color changes to indicate to the user that the element is clickable.
* Pressed state: When the button is pressed, a click event is triggered and the button's background color changes.
* Disable state: The button is currently disabled, so users cannot interact with it, and it appears grayed out. The button will be grayed out to visually indicate to users that it is currently unavailable, helping prevent accidental activation of its associated function at inappropriate times.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e0abe4270881476fb00738145d80d67c~tplv-goo7wpa0wc-image.image)

