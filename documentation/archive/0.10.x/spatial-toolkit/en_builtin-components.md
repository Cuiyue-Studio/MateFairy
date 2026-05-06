Basic components are automatically added based on the type of entity when an entity is created, and cannot be deleted.
## Static
Also named Static Batching. After the scene is loaded, the engine batches all meshes marked as Static and submits them to the GPU in a single operation, reducing N draw calls to one or a small number.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/05e0daaaf54d4674bd090c461f0d6457~tplv-goo7wpa0wc-image.image)
## Transform
The spatial position information component allows you to modify position, rotation, and scale.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/361d2e0b10d34816ae738b89422e9bb6~tplv-goo7wpa0wc-image.image)
## References
With this component, you can add references to other USD files on the current entity.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8033918b37b7459c88d4e54bde3aa110~tplv-goo7wpa0wc-image.image)
## Material Bindings
The material binding component allows you to select materials in the current scene and also override the materials of child objects.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/314dcdcbd9864fa5bc9d06c28bd8d0f8~tplv-goo7wpa0wc-image.image)

