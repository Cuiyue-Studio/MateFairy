Menu is a temporary list of options or functions that appears on the page to display grouped actions. It only becomes visible after the user interacts with its parent element.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4bdb533772ef4375a68efee429bc2213~tplv-goo7wpa0wc-image.image" width="1000px" /></div>

## Position
Menus typically appear below or to the right of the parent element. If there is not enough space below or to the right, display in the opposite direction.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/322e9dfa1f7e4d169dd31e6cdb0a8963~tplv-goo7wpa0wc-image.image" width="1760px" /></div>



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c33d4841557b4ca8980450047519ed65~tplv-goo7wpa0wc-image.image" width="1760px" /></div>



</div>
</div>

When in use, avoid having the menu extend too far beyond the main window (recommended maximum: 320dp). After reaching the maximum range, the submenu must align in the opposite direction.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/88523eaee4aa41d6a0ca8cf99d213551~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ac8a665696a243e89e0d5d7e0069b3e8~tplv-goo7wpa0wc-image.image)

1. Container: Height automatically adapts to content. After reaching the maximum height of 480dp, you can scroll to view more items.
2. Menu item: Includes text and optional subtext, leading icon, trailing icon, and trailing text.
3. Grouping: There is no limit to the number of groups.

### Menu item
Menu items can include text, subtext, a leading icon, a trailing icon, and trailing text. Only text is required; keep it as concise and clear as possible. The contents of all menu items in the same menu should be consistent.
Each item has Hover, Pressed, and Selected interaction states. Unavailable menu items should be displayed in a disabled state rather than removed. If all menu items in a menu are unavailable, the menu itself should remain accessible so that users can open it and see what options are included.
### **Grouping**
There are two ways to group: divider and header. You may choose either one.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aa075287e6c94e43a61e358bb8b40922~tplv-goo7wpa0wc-image.image)

* Group by header: Commonly used for grouping options with significant differences under the same menu, or for providing explanatory notes about the group.
* Group using divider: Lightweight grouping style, recommended for use.

## Interactive behavior
### Filter
The menu can be used in combination with the Input. When the user starts typing, the menu items are filtered based on the input. The menu height changes dynamically as content is input.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b5553e07cde7431191081a5c03d374eb~tplv-goo7wpa0wc-image.image)
### Submenu
Reading long menus requires users to spend more time and attention, which means they may miss commands they want to use. If a menu is too long, you can use submenus to shorten the list, for example, the "bookmarks" menu in a browser. In principle, there is no restriction on the number of submenu levels; however, to ensure user experience, it is recommended that menu depth not exceed three levels.
Hover over the parent menu item to open its submenu. When all submenus under a menu item are unavailable, it is shown as available.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a3655bdfd75243c08b8ca909c7904b92~tplv-goo7wpa0wc-image.image)
### Selection
Menu items usually have the following three meanings:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9e0987e2497348b5b0ff04cfebf7231e~tplv-goo7wpa0wc-image.image)

* Features that can be enabled or disabled: a check mark is placed before the text.
* Recommended or optional features: The checkmark is placed after the text by default. If a submenu indicator appears on the right, the checkmark is moved to the front.
* Function operations: Each operation takes effect once; no check mark is displayed after selection.

### Collapse menu
There are three ways to trigger the menu to collapse:

* After an option is selected, the value is assigned and the menu automatically collapses.
* Clicking outside the menu area will collapse the menu if no data has been modified.
* Use system buttons to return to the home page or exit the application.


