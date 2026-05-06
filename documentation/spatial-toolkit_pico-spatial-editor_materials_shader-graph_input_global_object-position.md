Returns the float3 position vector of the object's origin in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/fbf0b741311f41c6b963d8bdac813e3e~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used for the returned position vector. The default is **world**. **Space** can be set to the following values.
   * **object**: Local space coordinate. The space coordinate after the shader applies local deformation to the geometry, but before applying global transformation.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


