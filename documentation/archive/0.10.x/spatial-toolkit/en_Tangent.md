The geometric tangent vector of the data currently being processed in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9102427d92744dc784c9f9aa52a95d3d~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used by the shader to define the tangent vector. The default value is **object**. **Space** can be set to the following values.
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. The space coordinate with the anatomy of the vertex's tangent, bitangent, and normal as basis vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.
