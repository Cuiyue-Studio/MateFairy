Transform a 3D vector from one space coordinate to another.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/102af87d432e46d5a573c1c64cca17c3~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In:** The 3D vector to be transformed.
* **From Space:** The source space coordinate of the input vector **In**. The default value is **world**. The following valid space coordinate values can be used:
   * **model**: The model's local space coordinate.
   * **object**: The object's space coordinate.
   * **world**: The global space coordinate of the entire world.
* **To Space**: The target space coordinate to which the input vector **In** will be transformed. The default value is **world**. The following valid space coordinate values can be used:
   * **model**: The model's local space coordinate.
   * **object**: The object's space coordinate.
   * **world**: The global space coordinate of the entire world.
