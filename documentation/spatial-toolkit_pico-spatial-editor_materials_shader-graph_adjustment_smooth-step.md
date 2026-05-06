Using Hermite interpolation, smoothly remap an input value within the **Low** and **High** range to an output range from 0 to 1.
If the input value is less than **Low**, the output is `0`; if the input value is greater than **High**, the output is `1`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/65d5d08e343446afb5b0eda83fa6708f~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The original input value to be remapped.
* **Low**: Defines the starting point of the input value range. Inputs less than this value will output `0`.
* **High**: Defines the end point of the input value range. Inputs greater than this value will output `1`.

## Node usage instructions
The **Smooth Step** node achieves smooth transitions through Hermite interpolation, which differs from the linear mapping of the **Remap** node. The **Smooth Step** node provides a gentler curve near the boundaries, avoiding abrupt effects caused by hard transitions.

