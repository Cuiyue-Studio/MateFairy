A node that applies an affine transformation to 2D Input.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fe432260315c4e40956d7474bb3861ff~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The vector to be transformed. This node appends an extra component with a value of **1.0** to the Input vector **In**, so that the vector's dimension matches the dimension of the 2D affine transformation matrix. After the transformation is completed, this extra component will be removed.
* **Rotation**: The counterclockwise rotation angle to apply to the Input, in degrees.
* **Scale**: The scaling to apply to the Input. This parameter stretches or scales the Input according to the specified factor.
* **Translation**: The translation to apply to the Input. Translation moves the Input in the specified direction.


