The coordinates of the currently processed data in the given space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6200b9a697d8462f8b35f070f3d1b883~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used by the shader to define the position vector. The default value is **object**. The valid values for the **Space** parameter include:
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate constructed using the vertex's tangent, bitangent, and normal vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


