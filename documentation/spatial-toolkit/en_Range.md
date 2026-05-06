Remaps an Input value from one range to another. Gamma correction and output clamping options are also available.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/01c6d0918a194068af120d62fb6ee450~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The original Input value to be remapped.
* **In Low**: Defines the starting point of the Input value range.
* **In High**: Defines the end point of the Input value range.
* **Gamma**: Controls the inverse exponent applied to the Input value. The node first maps the Input range to the `0..1` interval, then applies the inverse exponent. The default value is `1.0`.
* **Out Low**: Defines the starting point of the output value range.
* **Out High**: Defines the end point of the output value range.
* **Do Clamp**: Controls whether the output value is clamped.
   * **true**: The output will be clamped to the range defined by the **Out Low** and **Out High** parameters.
   * **false**: (default) The output will not be clamped to the range defined by the **Out Low** and **Out High** parameters.

## Node usage instructions
The **Range** node is used to remap Input values from one range to another. The node also supports applying gamma correction during the intermediate stage of the conversion process. The **Gamma** value represents the inverse exponent applied to the Input value. For example, when **Gamma** = 2, the Input value is raised to the power of 1/2, which is equivalent to taking the square root. The node also supports clamping the output result. When this option is enabled, output values less than **Out Low** are set to **Out Low**, and output values greater than **Out High** are set to **Out High**.
