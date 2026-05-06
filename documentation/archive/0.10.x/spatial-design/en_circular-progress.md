The figure below shows circular progress (Circular Progress).
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d757c3fcc49449d978112d961bd4aeb~tplv-goo7wpa0wc-image.image" width="2000px" /></div>

Progress indicators can be divided into two types based on process duration:

* Deterministic type: The duration of the process is clearly known, such as file conversion.
* Indeterminate type: The duration of the process cannot be quantified, such as when loading or synchronizing complex data, as it may vary due to the user's network conditions.

A determinate progress bar displays progress by filling a linear or circular track, while an indeterminate progress bar only indicates that a process is ongoing and does not convey a specific duration.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4fb2f87bb4cd4f5ba8fc3a54b253aadd~tplv-goo7wpa0wc-image.image)

1. Determinate progress indicators: circular and linear styles
2. Indeterminate progress: Only the circular style is available

## loading indicator
Indeterminate circular progress bars are commonly used for loading or refreshing content. It is recommended to use this style when the loading time is between 200 milliseconds and 5 seconds. If the loading time is too long, it will make users anxious.
The position of the loading indicator. Common usage is as follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9582264ae1234e2ca80f68d3eb271c58~tplv-goo7wpa0wc-image.image)

1. Place in the loading view: Place it in the center of the loading view
2. When placed in other component containers, the component indicates that it is responding to user actions.

## Interactive behavior
The center of the circular progress bar can be configured with an icon and a "completed" style.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c98bb87b415a42a39be25f044097eca1~tplv-goo7wpa0wc-image.image)

1. Pause
2. Continue downloading
3. Complete
