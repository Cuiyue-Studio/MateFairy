Dynamic lighting and projection are the key technologies for achieving a realistic visual experience.
## Basic concepts
Dynamic lighting refers to a lighting system in which the light source can change based on time, scene, or user interaction. This lighting affects the brightness, shadows, and highlights of objects in real time, thereby enhancing the realism and immersion of the scene.
## Supported light source types
| **Name** | **Description** |
| --- | --- |
| Point light | A point light is a light source emits light uniformly in all directions. It can be used to simulate real-world light sources of very small sizes, such as bulbs, candles, flashlight LED beads, ceiling lamps, and more. <br> ***Note***: Currently, point light does not support shadows. |
| Directional light | A directional light is a light source that shines from a fixed direction with uniform intensity, and its rays are parallel to each other. It has no position, only direction. It is used to simulate uniform lighting from an infinitely distant source, such as sunlight. |
| Spotlight | A spotlight is a light source that emits from a single point and projects along a specific direction, with its rays gradually attenuating within a conical range. It is used to simulate cone-shaped light beams in real life, such as those from flashlights or stage lights, which emit from a point and spread within a defined angle range. |
The next parts introduce the use of three light sources in spatial apps. In all examples, a red cube is placed by default in the scene. In addition, a separate entity is used to serve as a light source (that is, `lightEntity`), and different light components are loaded onto it, thereby affecting the visual effect of the red cube in the scene.
## Limitations
The lighting system has the following limitations:
| **Light source type** | **Quantity limit** | **Over-limit behavior** |
| --- | --- | --- |
| Point light & spotlight | The total number of both should not exceed 256. | Light sources that exceed the limit are not included in lighting calculations. |
| Directional light | No more than 128. | Directional lights that exceed the limit are not included in lighting calculations. |
## Add a point light
Add a point light by adding `PointLightComponent` to an entity. Below is the expected result:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/39fd2cab904b46c3af036ae57b6c593e~tplv-goo7wpa0wc-image.image)
The code sample is as follows:
```Kotlin
val lightEntity = Entity()
// Property settings: white, the attenuation radius is 1.6 meters
val pointLightComponent = PointLightComponent(Color4.WHITE, 2000f, 1.6f)

lightEntity.components.set(pointLightComponent)
```

## Add a directional light
Add a directional light by adding `DirectionalLightComponent` to an entity. Below is the expected result:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d4ba8c0b1124457e9d1ff9011de10208~tplv-goo7wpa0wc-image.image)
Below is the code sample. The `castsShadowEnabled` property indicates whether the object should cast a shadow, and setting it to `true` means the object will cast a shadow.
```Kotlin
val lightEntity = Entity()
// Property settings: white, cast a shadow
val directionalLightComponent = DirectionalLightComponent(Color4.WHITE, 2000f, castsShadowEnabled = true)
lightEntity.components.set(directionalLightComponent)
```

## Add a spotlight
Add a spotlight by adding `SpotLightComponent` to an entity. Below is the expected result:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/107e5216efc149b6a3f3eeeeb5a4cd3f~tplv-goo7wpa0wc-image.image)
The code sample is as follows:
```Kotlin
val lightEntity = Entity()
// Property settings: white, casts a shadow, the angle is 30 degrees, the attenuation radius is 5 meters
val spotLightComponent = SpotLightComponent(
        Color4.WHITE,
        20000f,
        5f,
        30f,
        45f,
        true,
    )
lightEntity.components.set(spotLightComponent)
```

## Change the position and orientation of lighting
The position and orientation of the light follow the center of the entity it is added to, with the default orientation being (0, 0, -1). To adjust it, modify the entity’s `TransformComponent`.
### Change the position
To place a point light 0.4 meters above the top of the cube (as shown in the figure below), you can move the `lightEntity`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/94da7f06bf824bcab4858ba6e0c23cbc~tplv-goo7wpa0wc-image.image)
Below is the code sample:
```Kotlin
lightEntity.components[TransformComponent::class.java]?.apply {
    setPosition(Vector3(0f, 0.4f, 0f))
}
```

### Change the orientation
To make a spotlight shine on the cube from the front right at a 45-degree angle (as shown in the figure below), you can adjust the rotation of `lightEntity`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6eb82c3ecb654df7a1cd24f5b1890479~tplv-goo7wpa0wc-image.image)
Below is the code sample:
```Kotlin
lightEntity.components[TransformComponent::class.java]?.apply {
    setQuaternion(EulerAngles(-45f, 45f, 0f).toQuat())
}
```

## API reference
The `PointLightComponent`, `DirectionalLightComponent`, and `SpotLightComponent` classes provide properties and functions related to dynamic lighting. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

