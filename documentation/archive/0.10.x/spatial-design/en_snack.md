Snack notifies users about processes that have been executed or will be executed by the application. It appears at the bottom of the window and does not block main content or important interactions. Moreover, it does not interrupt the user experience; users can continue their previous actions and are not required to interact with Snack.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b1cb325819bb4f8cab23c87b885e778e~tplv-goo7wpa0wc-image.image)
## Location
Snack is positioned 32 dp above the bottom of the window and shifted forward by 32 dp along the window's Z axis.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/25b75eab83764222b67625aee3dc2110~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b12bcf3383644d8cab4ff7e648c5365e~tplv-goo7wpa0wc-image.image)

1. Container: Width adapts to content, with a maximum width of 480 dp.
2. Leading element (optional): Any content is allowed; commonly used are Icon or Circular Progress.
3. Title: In principle, there is no limit on the number of words; however, concise and clear wording is recommended to make it easier for users to understand and read.
4. Secondary text (optional): Provides supplementary explanation for the Snack notification message.
5. Operation (optional): You can place up to 2 Actions.

### Color
Snack container color is `Color Role: Accent`; Snack title color is `Color Role: On Accent`. Developers can modify the color value of Role according to the characteristics of the application. Ensure accessibility and visual contrast in the modified version.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6b984fe72b174a7c8bc981bc8e2adfd7~tplv-goo7wpa0wc-image.image)
## Interactive behavior
### Appear and disappear
A Snack without an Action will automatically disappear after 4 seconds. A Snack with an Action will remain visible until the user interacts with the Snack or closes it.
When Snack appears, it does not automatically receive focus, and the user's focus position before Snack appears is preserved.
### Continuous Snack
When multiple consecutive Snacks occur at the same time, they must be displayed sequentially according to their trigger time, and multiple Snacks must not be shown simultaneously.
