The position of the camera in the scene.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/036d37f20d5a4820a0438b316a6d4edf~tplv-goo7wpa0wc-image.image)
## Parameter descriptions

* **Space**: The space coordinate used to determine the camera position. The default value is **world**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate constructed using the vertex's tangent, bitangent, and normal vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


