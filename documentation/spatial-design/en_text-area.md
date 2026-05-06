Usually refers to a text area, which is an interface element that allows users to enter multiple lines of text and is commonly found in forms, editors, and similar scenarios.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f735155c78c640efb14841054746d0c5~tplv-goo7wpa0wc-image.image)
## Anatomy

* Main input area: The core visible region used to display the text entered by the user. It is typically a rectangular box. Unlike a single-line text box (Text Input), it supports line breaks, paragraphs, and other formats, making it suitable for scenarios where a large amount of text needs to be entered, such as feedback forms, email body editing, document editors, and so on.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9ab85517b771454cb4ce721913d97179~tplv-goo7wpa0wc-image.image)
* Placeholder text: When the input area is empty, prompt text (such as "Please enter a detailed description") is displayed. The color is usually light gray. The prompt text automatically disappears when the user begins typing. Its purpose is to guide users in understanding input requirements and to reduce cognitive load.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/90c3f5de97044806ace2936a7918eb09~tplv-goo7wpa0wc-image.image)
* Scroll bars: They appear when the input content exceeds the visible area. They are divided into vertical scroll bars (which are common) and horizontal scroll bars (which are rare and usually avoided by automatic line wrapping). Hidden scrollbars are generally used and are only displayed when content overflows, reducing visual distraction.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/92503771913f47b2ab64656d34577e5e~tplv-goo7wpa0wc-image.image)
* This section is located below the input area and is used to display supplementary information:
   * Character count (for example, "50/200 characters")
   * Formatting tip (for example, "Supports line breaks; up to 3 lines")
   * Error messages (such as "Content cannot be empty", usually displayed in red text)
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d17e1ed1bff460caec5dc788e7a9ce4~tplv-goo7wpa0wc-image.image)

## Interactive behavior
The interactive behavior of the Text Area is the logic of operational feedback between the user and the input box, covering the entire process from activation to input, to modification, and then to changes in state. The following are the most common basic operations:
| **Interaction** | **Note** | **placeholder** | **value** |
| --- | --- | --- | --- |
| Idle | In an inactive state with no user interaction, the text area has neither gained focus nor is the user performing input, editing, or other interactive behaviors. This is primarily a silent state characterized by waiting for the user to initiate an action. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/006ecb31bba8425ba16319bf1eef9740~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e8a1cc8304db4ccaa79e96ba8e8fde57~tplv-goo7wpa0wc-image.image) |
| Hover | When a gesture or other method is used to hover over the text area, the text area enters a hover state and the background color changes. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d695b77add9b4f5a8e002c80c03a53fa~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e6fcd2bebff943e1959b1bb87dc943a7~tplv-goo7wpa0wc-image.image) |
| Focused <br>  | Click to display the cursor (a blinking vertical line or block), indicating the input position. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/16312956a06243e2b3613529037bd136~tplv-goo7wpa0wc-image.image) <br> Cursor appears before Placeholder with 30% opacity | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8b2e60bdbf594c219d51501f9583f9f0~tplv-goo7wpa0wc-image.image) <br> The cursor is after Value |
| Typing | Users enter characters using the virtual keyboard, with the cursor automatically moving to the right as input is entered, and the text displayed in real time within the area. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/42c4ff1215ee4a29b4eec3ff1a6a28d1~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0517589da9194813baf2820a57e1ba28~tplv-goo7wpa0wc-image.image) |
| Error | When the input does not comply with the rules, such as containing prohibited characters, the reminder is reinforced, for example by turning the border red or making it shake slightly. | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/237d0b5c20594882a837ec3651db95da~tplv-goo7wpa0wc-image.image) | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/71b6e062e476498897b87705f32c438a~tplv-goo7wpa0wc-image.image) |
| Disable <br>  | * Interaction limitations: Cannot be clicked or focused, the cursor does not appear, and keyboard input is ineffective. <br> * Visual cue: The background color turns gray and the text color becomes darker, indicating "not operable." | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/406275ba9d084a9d8b0934c8cce8a578~tplv-goo7wpa0wc-image.image) <br>  | ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/039c02d795354b71b59f3b5f2f35fc33~tplv-goo7wpa0wc-image.image) |

