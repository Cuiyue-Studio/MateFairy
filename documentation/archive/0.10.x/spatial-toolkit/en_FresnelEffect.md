The Fresnel Effect is calculated based on the direction of the surface normal and the view direction.
The Fresnel Effect is typically more pronounced at the edges of a surface and weaker when facing directly toward the viewer. It can be used to achieve effects such as edge highlights, rim lighting, or glow masks.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/419bd57dcfd249708b98c8dbd83e8f3e~tplv-goo7wpa0wc-image.image)
## Input parameters

* **Normal**: Surface normal direction.
* **View Dir**: View direction.
* **Power**: Exponential control parameter for the Fresnel Effect, used to adjust the edge transition range and intensity distribution.

## Node usage instructions
The following node graph demonstrates how to use the Fresnel Effect node to enhance edge brightness.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/47b83bb3301d468585db1281c13c0e84~tplv-goo7wpa0wc-image.image)
After applying the Shader Graph to the sphere, you can observe:

* The sphere has a base color
* The sphere's edges are brighter
* It appears as a simple energy ball or glowing sphere

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5519a605a0634719bf01940ea994394a~tplv-goo7wpa0wc-image.image)

