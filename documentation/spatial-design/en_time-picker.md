The Time Picker is an interactive component that allows users to select a specific time—such as hour, minute, and in some cases, second or time range—within the interface. It is widely used in forms, scheduling, booking systems, and similar scenarios, with the goal of simplifying the time input process and reducing errors caused by manual entry.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0335f84407f94dabad67c92c1cdd6220~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/68b30f3445de4055969c0b02f41a53cd~tplv-goo7wpa0wc-image.image)

1. **Current selected time:** The current selected time is highlighted
2. **Unselected time:** Three unselected times can be displayed above and three below the selected time.
3. **Gradient mask:** A semi-transparent mask appears at the top and bottom edges

Other information:

* **Current time display box:** Used to show the time selected by the user or the default time, such as "09:30" or "14:45:30". Typically serves as an entry point for interaction; clicking it expands the selection panel.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/50db1add0b824d219e65796287688dce~tplv-goo7wpa0wc-image.image)
* The panel is the core area where users select a time, and it can be either a popup (floating above the page) or embedded (directly within a form).
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/57590dfec5db43dab5cadd572e7d682d~tplv-goo7wpa0wc-image.image)
* The time unit selection area can display the time unit selector, hour selector, minute selector, second selector, and period selector, with up to three lists shown.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/24fdca485aa640e4bc6b394cd2d1cea1~tplv-goo7wpa0wc-image.image)
   The core of the time picker is the combination of the display area, selection panel (including time units and action buttons), and auxiliary elements, which enables users to enter time efficiently and accurately. When designing, decide which components to include or exclude based on the usage scenario—such as whether seconds are needed or whether to use the 12-hour format—and balance functionality with simplicity.

## Interactive behavior

* **Scroll selection**
   * Hour, minute, second, and period (AM/PM) are divided into separate columns, and users select values by scrolling up or down.
   * When sliding, the value automatically snaps to the nearest valid value. For example, if the minute column is slid to 27 and a 5-minute interval is set, it will automatically align to 25 or 30.
   * Selected items are highlighted (background color or border change).
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/525f7ccd87b04c89a6826d332e83c3f3~tplv-goo7wpa0wc-image.image)
* **Operation button area**
   * Confirm button: When clicked, the selected time is synchronized to the display box, and the popup panel closes.
   * Cancel button: After clicking, the current selection is discarded, the original time remains unchanged, and the panel is closed.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eea92978fd2540d1984a9e784e6f050b~tplv-goo7wpa0wc-image.image)
* **Real-time feedback**
   During the selection process, the display box updates the preview in real time (for example, when sliding the minutes, "14:30" changes to "14:35"), allowing users to clearly see the result of their selection.
* **Trigger hotspot**
   Group by data columns
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a911f3ed80ca402780c948a22b5b1adf~tplv-goo7wpa0wc-image.image)


