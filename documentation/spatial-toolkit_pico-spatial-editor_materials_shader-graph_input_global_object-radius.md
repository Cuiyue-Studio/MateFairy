Returns the bounding radius of the object in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/615d604fa01a4c7f89ac790e22704747~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used for the returned up vector. The default is **world**. **Space** can be set to the following values.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry but before global transformation is applied.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.
