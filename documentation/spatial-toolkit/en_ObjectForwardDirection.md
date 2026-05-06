Returns the float3 direction vector of the object's forward direction in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3235b4714a2a4c1b88fd8822d8268895~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used for the returned forward direction vector. The default is **world**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate constructed from the vertex's tangent, bitangent, and normal as basis vectors, commonly used for lighting and texture calculations.
   * **world**: World space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


