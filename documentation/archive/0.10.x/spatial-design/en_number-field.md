Number Field is an interactive component for receiving, displaying, and processing numerical input, and is widely used in forms, data entry, settings panels, and more. Its core functionality is to ensure that users can efficiently and accurately input numeric values, such as integers, decimals, and negative numbers, through constraint-based design and assisted interactions. It also supports validation, adjustment, and formatted display of these values.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9fed5229d7b8492aaab47d6589884299~tplv-goo7wpa0wc-image.image)
## Anatomy
### Content
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b0d7874465f744c1892c34d74abf956c~tplv-goo7wpa0wc-image.image)

1. **Fine-tuning button:**
   The increment and decrement buttons (➕/➖) located next to the Input area allow you to make small adjustments to the value when clicked. The adjustment step can be customized, for example, plus or minus 1 or 0.1, making these buttons suitable for precise adjustment.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/df5e60097c79431a88d0754b524056a8~tplv-goo7wpa0wc-image.image)
2. **The input area:**
   * This is the main body of the numeric input box, used to display and receive numeric values from user input (such as integers, decimals, negative numbers, and more).
   * Supports text cursor positioning and allows users to directly edit content.
   * For example, for the number of people in the room shown below, you can input values such as `6`.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a4acd39764ca4d8696e792cf2ccc13a3~tplv-goo7wpa0wc-image.image)

### Dimensions
Two sizes are available: Default size: 152 × 40 dp; Small size: 152 × 32 dp. If the content exceeds the specified size, it will be limited to fit within the range.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a4815c91725c429ea258815ea6b76fe7~tplv-goo7wpa0wc-image.image)
### Roles

* Numerical restrictiveness
   Unlike a standard text Input box, Number Field actively filters out non-numeric characters, such as letters and special symbols, and only allows the entry of valid elements including digits (0–9), decimal points (.), and minus signs (–). The format can also be restricted as needed, for example, by limiting the number of decimal places or specifying whether negative numbers are permitted.
* Functional specificity
   Focused on numerical processing, it integrates numerical validation (such as range limitation and format checking), fine-tuning features (such as increment and decrement arrows), formatting options (such as unit display), and other dedicated features to reduce user input errors.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b66fd87531a342a98e90be67f91e78da~tplv-goo7wpa0wc-image.image)
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/281756a1a8fd478aa493f255e5e0eadc~tplv-goo7wpa0wc-image.image)
* Ease of interaction
   By using auxiliary controls, such as spin buttons, and real-time feedback, such as error messages and automatic formatting correction, user effort is reduced. This approach is especially suitable for scenarios that require precise numerical adjustments, such as price setting and quantity input.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/040b5c2c79fd4404aac4f35e20a8ebab~tplv-goo7wpa0wc-image.image)
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0ab61d3855bf43e5bdbe829a928cff46~tplv-goo7wpa0wc-image.image)

## Interactive behavior
Interactive feedback is provided for the overall component area, button interactions, and numeric input area, including error messages and disabled feedback.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d81c942e98e34b27886f70b6a6245974~tplv-goo7wpa0wc-image.image)
For interactive hot zones, the Default interactive hot zone is 40 × 40 dp, while the Small interactive hot zone is 32 × 32 dp.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/262b434de96d4f1c9a204e4e4551fa8d~tplv-goo7wpa0wc-image.image)

