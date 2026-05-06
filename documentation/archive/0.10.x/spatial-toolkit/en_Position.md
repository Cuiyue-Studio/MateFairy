The coordinates of the data being processed within the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6200b9a697d8462f8b35f070f3d1b883~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate in which the shader defines the position vector. The default value is **object**. Valid values for the **Space** parameter include:
   * **model**: local space coordinate. The space coordinate prior to the shader applying any local deformation or global transformation to the geometry.
   * **object**: local space coordinate. The space coordinate after the shader applies local deformation, but before applying global transformation to the geometry.
   * **tangent**: tangent space coordinate. The space coordinate constructed using the vertex's tangent, bitangent, and normal as basis vectors, which is commonly used for lighting and texture calculations.
   * **world**: global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


