Users can input information in a specific area.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a451ee687a1342059cf0ece9c5577430~tplv-goo7wpa0wc-image.image)
## Anatomy
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9059b7e665954857813fbb1b389934ea~tplv-goo7wpa0wc-image.image)

1. **Input area**
   * Visible text input box where users can enter text, numbers, symbols, and other content.
   * Width is typically designed based on the expected input length. For example, the phone number input field has a fixed width for 11 digits, while the username input field adapts to the content.
2. **Auxiliary elements**
   * Icons: such as the "show/hide" icon in password fields, the clear button, and the input type indicator.
   * Prompt text: Instructional notes or error messages displayed below the input field, such as "Password must include uppercase and lowercase letters and numbers" or "Password input error."
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ece4f7af11d24112a855ffb1ff7e1c43~tplv-goo7wpa0wc-image.image)
3. Placeholder
   * Placeholder text is displayed in the input box when it is empty (common phrases such as "On my way"); the text automatically disappears when input begins.
      Cannot replace labels (serves only as supplementary explanation).

      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7321ff57169a416c8afff15823311397~tplv-goo7wpa0wc-image.image)
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aa2ef699172d4719ae459ad1567bc07d~tplv-goo7wpa0wc-image.image)
   * The label for a text input box is typically positioned above or to the left of the input box to describe its purpose.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f8029df155954b16870650b6edb5f99d~tplv-goo7wpa0wc-image.image)
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9b6bff6d3085416bb4f323de077699c9~tplv-goo7wpa0wc-image.image)

## Interactive behavior
| **Interaction** | **Note** | **placeholder** | **value** |
| --- | --- | --- | --- |
| Idle | When idle, the border uses the default style, a placeholder may be displayed, and no additional interactive feedback is provided. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/109493181fa04c4a9a733f2af919b3b3~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1de8e2ee435f44bf860d7d967315628d~tplv-goo7wpa0wc-image.image) |
| Hover | When a hand pose and other methods pass over the text area, the text area enters the hover state and the background color changes. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/45f6207964314e1686cf52f9ecd2cbc5~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/00dfe3c353804b4c8f573d5718691f21~tplv-goo7wpa0wc-image.image) |
| Focused | When the user clicks the input field, it becomes active, the background color changes, the blinking cursor displays "Input is possible", and the soft keyboard automatically appears. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a8c8c47bf12041229270c55e685e9101~tplv-goo7wpa0wc-image.image) <br> The opacity of the note text is 30%. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ad71a8ff327c48bd898b4223127fafd8~tplv-goo7wpa0wc-image.image) <br> The cursor is after Value. |
| Typing | The user is entering content, and input actions are responded to in real time. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b79faf08cba94e5aa815db895f73620d~tplv-goo7wpa0wc-image.image) <br> When there is a value, a **Clear** button appears on the right. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3c0e2106c605409a98f47df9df113b65~tplv-goo7wpa0wc-image.image) <br> When a value is present, a **Clear** button appears on the right. |
| Error | If the input does not meet the required rules (for example, if the phone number does not have enough digits), the border turns red and a red error message appears below (for example, "Please enter a valid 11-digit phone number"). | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/c5ec4f85e2734547b8779a59c2f22b7b~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/414a93c8894b4db7bfb36868fb808f8e~tplv-goo7wpa0wc-image.image) |
| Disable <br>  | User interaction is not allowed. The input and its label are greyed out (with reduced saturation), clicking has no effect, and the input cannot be focused. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eb9d85bf155e4c0599ab731514ce4114~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d4724e4129c14fda8570035896767c02~tplv-goo7wpa0wc-image.image) |

