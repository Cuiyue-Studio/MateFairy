Transform a 3D vector from one space to another space.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/102af87d432e46d5a573c1c64cca17c3~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In:​**The 3D vector to be transformed.
* **From Space:​**The source space coordinate of the input vector **In**. The default value is **world**. The following valid space values are available:
   * **model**: The local space coordinate relative to the model.
   * **object**: The space coordinate relative to the object.
   * **world**: The global space coordinate relative to the entire world.
* **To Space**: The target space coordinate to which the input vector **In** will be transformed. The default value is **world**. The following valid space values are available:
   * **model**: The local space coordinate relative to the model.
   * **object**: The space coordinate relative to the object.
   * **world**: The global space coordinate relative to the entire world.
