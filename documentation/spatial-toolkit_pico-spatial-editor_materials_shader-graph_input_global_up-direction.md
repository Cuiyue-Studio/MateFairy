The direction of the upward vector.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/eb8219516ea24850815859a1dfd82eae~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used for the returned upward vector. The default is **world**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate whose anatomy consists of the vertex's tangent, bitangent, and normal vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.



