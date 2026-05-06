Returns the float3 direction vector of the object's forward direction in the specified space coordinate.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3235b4714a2a4c1b88fd8822d8268895~tplv-goo7wpa0wc-image.image)
## Parameter description

* **Space**: The space coordinate used for the returned forward direction vector. The default is **world**. **Space** can be set to the following values.
   * **object**: Local space coordinate. The space coordinate in which the shader has applied local deformation to the geometry, but has not yet applied global transformation.
   * **view**: Camera space coordinate. After the shader applies both local deformation and global transformation to the geometry, it is then converted to the space coordinate relative to the current camera. In this space coordinate, direction and position are represented relative to the camera.
   * **world**: Global space coordinate. The space coordinate after the shader applies both local deformation and global transformation to the geometry.


