Users can quickly and accurately select a date or date range on the device. A visual interactive interface replaces the complex process of manually entering dates, reduces input errors, and optimizes the user experience.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a94cf2b1d0084aee9b378c3aa61d6401~tplv-goo7wpa0wc-image.image)
Date Picker can be placed directly in Window or inside Sheet.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c574ecc30e904ecf9eacae5f883a6204~tplv-goo7wpa0wc-image.image)
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7dd4353532b04b75aa1b348ecf09c3ad~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bcbb66d0cc224babb0862d728b73caf4~tplv-goo7wpa0wc-image.image)

* **Year and month navigation bar:**
   * Month and year display (such as "October 2024")
   * Toggle button: Toggle between months or years
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/46f9584bd43941d7b769ddbfa53e7bfc~tplv-goo7wpa0wc-image.image)
* **Week header bar:**
   Displays weekday information (for example, "日" to "六" or "Sun" to "Sat"; internationalization is supported).
* **Date mesh:**
   * Dates for the current month are displayed in a grid format, which typically also includes some dates from the previous and next months. These dates are shown in gray and cannot be selected.
   * The styles for today’s date, selected date, and disabled date are different.

You can select one or multiple dates in the selection area. When making a selection, if you choose a date outside the current month, the month and year displayed at the top will automatically switch to the corresponding month and year.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/345878a02ec749068b6fc9d31b641bd3~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd76731a9d094b57aca83c17147b7f91~tplv-goo7wpa0wc-image.image)


</div>
</div>

In addition to mesh display, vertical scrolling is also supported. For detailed specifications, refer to [Time Picker](/en_time-picker).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a1ef601c3314460af6f80036b01300c~tplv-goo7wpa0wc-image.image)
## Interactive behavior

* **Feedback on date status**
   Dates are categorized into dates in the current month, dates not in the current month, today, and selected dates.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bb9c9e20e16f4b1c98213b4c8256b580~tplv-goo7wpa0wc-image.image)
* **Auxiliary interactions**
   * **Trigger method:** When the trigger area (input box, calendar icon) is clicked, the date panel pops up (popup mode); in embedded mode, it is displayed directly without the need for a trigger.
   * **Closing methods:** Click the "Close" button to close the panel; click the "OK" button to save the information and close the panel.


