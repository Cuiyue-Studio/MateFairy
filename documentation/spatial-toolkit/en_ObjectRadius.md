Returns the bounding radius of the object in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/615d604fa01a4c7f89ac790e22704747~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate in which the radius is returned. The default is **world**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate whose anatomy is constructed from the vertex's tangent, bitangent, and normal vectors, commonly used for lighting and texture calculations.
   * **world**: World space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.
