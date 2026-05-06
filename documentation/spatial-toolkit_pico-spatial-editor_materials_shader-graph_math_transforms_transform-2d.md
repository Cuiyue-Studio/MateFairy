A node that applies an affine transformation to 2D input.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fe432260315c4e40956d7474bb3861ff~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The vector to be transformed. This node appends an extra component with a value of **1.0** to the input vector **In**, so that the vector's dimension matches that of the 2D affine transformation matrix. After the transformation is complete, this extra component is removed.
* **Rotation**: The counterclockwise rotation angle to apply to the input, in degrees.
* **Scale**: The scaling factor to apply to the input. This parameter stretches or scales the input by the specified factor.
* **Translation**: The translation to apply to the input. Translation moves the input in the specified direction.


