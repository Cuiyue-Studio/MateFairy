Returns the direction vector from the specified position to the view reference point and outputs it in the selected space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/26aec146a0b44c8d8d05cce598011177~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate of the direction vector. The default value is **object**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate whose anatomy uses the vertex's tangent, bitangent, and normal as basis vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.
