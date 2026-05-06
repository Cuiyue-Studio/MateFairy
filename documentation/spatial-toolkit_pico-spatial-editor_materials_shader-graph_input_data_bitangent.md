The geometric bitangent vector of the data being processed in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8d19ce1da45344ffa2831b0a25d60d43~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used by the shader to define the bitangent vector. The default value is **object**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate whose anatomy consists of the vertex's tangent, bitangent, and normal as basis vectors, commonly used for lighting and texture calculations.
   * **world**: World space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.



