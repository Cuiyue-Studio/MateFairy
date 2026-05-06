Transform vectors using a matrix.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a1c06390a6324ed2a861bc2b3439178f~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In**: The vector to be transformed. This node appends an extra component with a value of **1.0** to the input vector **In** so that the vector's dimension matches the dimension of the matrix **Mat**. After the transformation is complete, this extra component will be removed.
* **Mat**: The matrix used to transform the input vector **In**; the default value is the identity matrix.


