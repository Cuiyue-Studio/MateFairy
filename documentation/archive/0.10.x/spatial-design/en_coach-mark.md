Coach Mark is commonly used to help users understand new features or guide their actions. It generally does not block user interaction, and UI elements on the page that are not covered remain responsive. It is recommended to display only one Coach Mark at a time to ensure users can focus their attention.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a05dbd67edc243208741112092f66186~tplv-goo7wpa0wc-image.image)
## Position
By default, it is displayed on the right side of the parent element, but developers can adjust the position to avoid covering other important information.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b02a3a36566545da8afd17488d49d639~tplv-goo7wpa0wc-image.image)

1. Left of parent
2. Right of parent (default position)
3. Above parent
4. Below parent

## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5722b6445e244247b03da1e244d4d1a5~tplv-goo7wpa0wc-image.image)

1. Container: The height of the container adapts to its content.
2. Media elements (optional): Supports images and videos. If the video loading time is too long, it is recommended to add a loading animation.
3. Title: Make the copy as clear and concise as possible.
4. Subtext (optional): Used to provide additional explanation.
5. Action: A maximum of 2 Actions can be configured.

## Interactive behavior
### Appear and disappear
As long as one of the conditions in the table below is met, Coach Mark will appear or disappear.
| **Appear** | **Disappear** |
| --- | --- |
| 1. Click the parent element <br> 2. The page is loading and new features are being explained. | 1. Interact with the Action on Coach Mark <br> 2. Click the parent element |

