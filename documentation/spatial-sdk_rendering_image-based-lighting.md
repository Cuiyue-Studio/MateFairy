Image-based lighting (IBL) is an advanced lighting technique that uses an environment map or panoramic image to simulate the light from real-world environments, providing virtual objects with more realistic and natural lighting and reflection effects.
## IBL types
| **Type** | **Description** |
| --- | --- |
| Local IBL | Lighting that applies to specific objects in the scene,. It can be overlaid with environment IBL. |
| Environment IBL | Lighting that applies to the entire scene. <br> ***Note***: You can customize the environment IBL map only within a Stage; otherwise, the system-provided map is used by default. |
## Related components
| **Component name** | **Description** |
| --- | --- |
| ImageBasedLightComponent | Used to set the rotation angle, image source, and lighting intensity of a local IBL. |
| ImageBasedLightReceiverComponent | Use to set the entity that receives a local IBL. |
| StageEnvironmentLightingComponent | Used to set the rotation angle, image source, and lighting intensity for an environment IBL. <br> ***Note***: <br> The behavior of this component varies depending on the current `StageStyle`: <br>  <br> * In `StageStyle.FULL` mode, this component provides full environmental lighting to define the atmosphere of the virtual world. <br> * In `StageStyle.MIXED` mode, the stage environment lighting is inactive. To ensure visual consistency, the system prioritizes IBL derived from the real-world environment. <br> * In `StageStyle.PROGRESSIVE` mode, the lighting effect is a blend of the stage environmental lighting and the system IBL. The blend ratio is determined by the current immersion level. |
| EnvironmentLightingSettingsComponent | When mixing local IBL and environment IBL, set the weight of environmental IBL. When this component is not added, the default weight of 0.5 is used. |
## Add a local IBL to a specified object
First, define a local IBL using `ImageBasedLightComponent` and attach this component to a specified entity.
```Kotlin
val iblEntity = Entity().apply {
    val iblSource = ImageBasedLightSource.Single(iblTexture)
    val iblComponent = ImageBasedLightComponent(iblSource, 8f)
    components.set(iblComponent)
}
// Remember to add the entity with the global IBL to the scene
content.add(iblEntity)
```

Then, attach `ImageBasedLightReceiverComponent` to this entity.
```Kotlin
val iblReceiverComponent = ImageBasedLightReceiverComponent(iblEntity) 
modelEntity.components.set(iblReceiverComponent)
```

## Add an environment IBL to the scene
The custom environment IBL is only effective within a Stage.

Simply add `StageEnvironmentLightingComponent` to any entity in the scene.
```Kotlin
val environmentEntity = Entity().apply {
    val iblSource = ImageBasedLightSource.Single(iblTexture)
    val environmentLightingComponent = StageEnvironmentLightingComponent(iblSource, 8f)
    components.set(environmentLightingComponent)
}
// Remember to add the entity with the environment IBL to the scene
content.add(iblEntity)
```

By default, environment IBL and local IBL are mixed and displayed. If you want to customize the blending ratio, you can use `EnvironmentLightingSettingsComponent`.
```Kotlin
val environmentLightingSettingsComponent =
    EnvironmentLightingSettingsComponent(0.0f) // Display local IBL only
modelEntity.components.set(environmentLightingSettingsComponent)
```

## API reference
The `ImageBasedLightComponent`, `ImageBasedLightReceiverComponent`, `StageEnvironmentLightingComponent`, and `EnvironmentLightingSettingsComponent` classes provide IBL-related properties and functions. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

