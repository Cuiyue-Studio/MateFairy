Transform the normal vector from one space coordinate to another space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a8894f7c481b4d6eb4c7727652650b30~tplv-goo7wpa0wc-image.image)
## Parameter description

* **In:​**The normal vector to be transformed.
* **From Space:​**The source space coordinate of the input vector **In**. Default is **world**. The following valid space coordinate values can be used:
   * **model**: The local space coordinate relative to the model.
   * **object**: The space coordinate relative to the object.
   * **world**: The global space coordinate relative to the entire world.
* **To Space**: The target space coordinate to which the input vector **In** will be transformed. Default is **world**. The following valid space coordinate values can be used:
   * **model**: The local space coordinate relative to the model.
   * **object**: The space coordinate relative to the object.
   * **world**: The global space coordinate relative to the entire world.
