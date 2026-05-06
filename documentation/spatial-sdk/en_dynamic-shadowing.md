Dynamic shadowing is a technology that updates shadows in real time as time passes, as users interact, or as the environment changes. By dynamically adjusting virtual elements—such as images, shadows, lighting, or user interfaces—it allows them to align with surfaces, objects, or the user's perspective in the scene, enhancing the sense of immersion.
Ground shadowing is a common application of dynamic shadowing. Its core implementation is to accurately project the shadow or image of a virtual object onto the ground or a horizontal plane within the scene, making the virtual object appear as if it is truly placed on that surface and enhancing spatial consistency and realism visually.
## Add a ground shadow
To implement ground shadows in a scene, you need to add `GroundingShadowComponent` to both the object and the ground at the same time. `GroundingShadowComponent` can dynamically simulate the shadow effect produced when a light source is positioned directly above an object, ensuring that the shadow always appears beneath the object, as shown in the figure below:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/408073beb9ab41149deb3c132a4050e2~tplv-goo7wpa0wc-image.image)
When adding `GroundingShadowComponent`, you need to configure the following parameters:

* `castsShadowEnabled`: Whether to cast a shadow
* `receivesShadowEnabled`: Whether to receive the shadow

Below is the code sample:
```Kotlin
floorEntity.components.set(GroundingShadowComponent(castsShadowEnabled = false, receivesShadowEnabled = true))
modelEntity.components.set(GroundingShadowComponent(castsShadowEnabled = true, receivesShadowEnabled = false)) 
```

## Implement dynamic lighting and shadowing simultaneously 
`GroundingShadowComponent` cannot dynamically update shadows with changes in the position of the light source. To implement shadows that change as dynamic light sources move, use components related to dynamic light sources. For more information, refer to "[Dynamic lighting](/en_dynamic-lighting)".
## API reference
The `GroundingShadowComponent` class provides properties and functions related to dynamic shadowing. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
