The geometric normal of the currently processed data in the given space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cd1d3338eb6f4ca7b6765223f9c17600~tplv-goo7wpa0wc-image.image)
## Parameter description:

* **Space**: The space coordinate used by the shader to define the normal vector. The default value is **object**. The valid values for the **Space** parameter include:
   * **model**: Local space coordinate. The space coordinate before the shader applies any local deformation or global transformation to the geometry.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **tangent**: Tangent space coordinate. A space coordinate constructed using the tangent, bitangent, and normal of the vertex as basis vectors, commonly used for lighting and texture calculations.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


