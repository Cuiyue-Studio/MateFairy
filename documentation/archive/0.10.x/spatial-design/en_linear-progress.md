The following figure shows linear progress (Linear Progress).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9460ad1d353f48c6a0332989adb80fc7~tplv-goo7wpa0wc-image.image)
A progress bar shows the progress of activities, such as loading application content, submitting forms, or saving updates. When multiple projects are loaded simultaneously, use a single progress indicator to display the overall progress for the group. Do not add progress to every activity.

* For progress that is completed almost instantly, do not use a progress bar to indicate status. Recommendation: If the expected wait time is less than 200 ms, it is not recommended to display a progress bar. If a process takes too long, it is recommended to allow the process to continue in the background so that users can perform other tasks, rather than requiring them to remain in a non-interactive waiting state for an extended period.
* For processes with excessively long wait times, it is recommended to configure an operation to terminate processes. If users can interrupt the process without negative consequences, you can configure a "Cancel" button. If interrupting the process may have negative consequences, such as loss of downloaded files, you can provide a "Pause" button instead.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/46d66eeb230c475e857a5587ab6644a1~tplv-goo7wpa0wc-image.image)
## Interactive behavior
The linear progress bar only supports **deterministic progress**. If the waiting time is unknown, use [Circular Progress](/en_circular-progress).

