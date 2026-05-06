Particles are a real-time graphics technology used to simulate natural or abstract phenomena, which can enhance immersion, express interactive feedback, and more. Common particles include smoke, rain and snow, sparks, dust, halos, and more.
## Prerequisites

* The Spatial Tools dependency has been added to the project. For more information, refer to [Project structure and dependency configuration](/project-structure-and-dependency-configuration).
* A Spatial Editor project already exists. For information on how to create a project in Spatial Editor, refer to [Create a new project](/document/spatial-toolkit/project-management/).

## Create particles
In Spatial Editor, add a **Particle Component** to the entity that requires particle effects, and adjust the relevant **Emitter** and **Particles** parameters in the **Inspector** window to achieve the desired effect. For detailed explanations of configurable parameters, refer to the "Particle" section in "[General components](/document/spatial-toolkit/general-components/)".
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/557a0aba75f0433b87c1a137f7d8aca2~tplv-goo7wpa0wc-image.image)
Additionally, the Asset Library in Spatial Editor provides several preset particles that you can use directly or modify their existing configurations as needed.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cb38e5d11bd446a888752b7fb546f082~tplv-goo7wpa0wc-image.image" width="3456px" /></div>

## Play particles
After successfully loading the model using the PICO Spatial SDK, the particle effects will play automatically.
For example, when adding fog (along with lighting) to the PICO robot, the structure of the scene in Spatial Editor is as follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/850b7030f67d4b2083c3132295448a9e~tplv-goo7wpa0wc-image.image)
Use the `Entity.loadSuspend` or `AssetBundle.load` interfaces provided by the PICO Spatial SDK to load the PICO robot, particles, and lighting into a `Full` style Stage:
```Kotlin
fun mainApp(scope: SpatialAppScope) =
    with(scope) {
        DefaultStage { ParticleExample() }
    }

@Composable
fun ParticleExample() {
    SpatialView(
        initial = { content, _ ->
            val scene =
                Entity.loadSuspend(
                    modelName = SCENE_NAME_PARTICLE,
                    bundle = AssetBundle.load("asset://$BUNDLE_NAME.bundle")
                )
            scene.components[TransformComponent::class.java]?.apply {
                setPosition(Vector3(0f, 1.0f, -2f))
            }
            content.addEntity(scene)
        }
    )
}
```

The expected playback effect is as follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/67f4496a328847fb9bfe87f803c42b2b~tplv-goo7wpa0wc-image.image)
## Dynamically update particles at runtime
Most time-varying properties of particles can be implemented in Spatial Editor by editing variable curves; runtime dynamic property modification is mainly applicable to the following scenarios:

* **Event-triggered response**: Dynamically adjust parameters based on user interactions or specific application events;
* **Conditional logic control**: Modify property values based on application state or conditional logic;
* **Procedural generation**: Dynamically calculate and set parameters through code algorithms;
* **Real-time debugging and optimization**: Quickly adjust parameters at runtime to test different effects.

To dynamically modify particle properties at runtime, you need to use the PICO Spatial SDK. Currently, the `ParticleComponent` class provides the following properties that can be dynamically modified at runtime:
| **Properties provided by the SDK** | **Corresponding properties in Spatial Editor** | **Description** |
| --- | --- | --- |
| isEmitting | Is Emitting | Whether particles are being emitted. |
| startColor | Start Color | The initial color of particles at the beginning of their lifecycle. |
| isColorModifierEnabled | Color Enabled | Whether the color modifier is enabled. |
| isEndColorEnabled | Enable End Color | Whether to enable the end-of-life color. This can only be set when `isColorModifierEnabled = true`. |
| endColor | End Color | The final color of particles at the end of their lifecycle. This can only be set when `isEndColorEnabled = true `. |
| isAttractorEnabled | Attractor Enabled | Whether the attractor field is enabled. |
| attractorStrength | Attractor Strength | The strength that attracts particles to the center of the attractor field. This can only be set when `isAttractorEnabled = true`. |
| isVortexEnabled | Vortex Enabled | Whether the vortex field is enabled. |
| vortexStrength | Vortex Strength | The strength of the vortex. This can only be set when `isVortexEnabled = true`. |
For example, add a button in the scene; each time the button is clicked, the `startColor` of the smoke will randomly change to another color.
```Kotlin
@Composable
fun ParticleExample() {
    var color by remember { mutableStateOf(Color4.WHITE) }
    var particleComponent by remember { mutableStateOf<ParticleComponent?>(null) }
    SpatialView(
        initial = { content, attachments ->
            // Handle the current scene
            val scene =
                Entity.loadSuspend(
                    modelName = SCENE_NAME_PARTICLE,
                    bundle = AssetBundle.load("asset://$BUNDLE_NAME.bundle")
                )
            scene.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, 1.0f, -2f))
                }
                // In Spatial Editor, ParticleComponent has been added to the entity named "cloud" entity, so you need to locate this entity
                val particleEntity = this.findEntity("cloud")
                particleEntity?.components?.get(ParticleComponent::class.java)?.apply {
                    particleComponent = this
                }
                content.addEntity(this)
            }
            // Handle button attachment
            val buttonAttachment = attachments.entity("button")
            buttonAttachment?.apply {
                scene.addChild(this)
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, 1.2f, 0.8f))
                    scaleBy(2f)
                }
            }
        },
        attachments = {
            AttachmentPanel(id = "button") {
                Button(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    onClick = {
                        color = randomColor4()
                        particleComponent?.startColor =
                            ParticleColorVaryingProperty(
                                type = ParticleVaryingPropertyType.CONSTANT,
                                value = color,
                            )
                    }
                ) {
                    Text(text = "Change Color", color = Color(color.red, color.green, color.blue))
                }
            }
        }
    )
}

// Function for generating random colors
private fun randomColor4(): Color4 {
    val maxAlpha = 1f
    val minAlpha = 0.3f
    return Color4(
        Random.nextFloat(),
        Random.nextFloat(),
        Random.nextFloat(),
        Random.nextFloat() * (maxAlpha - minAlpha) + minAlpha
    )
}
```

The expected effect is as follows:
<video src=https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b8bf5e543c254e98b40ef40ed5e48ed9~tplv-goo7wpa0wc-image.image></video>
This is just a simple example. You can modify the corresponding properties as needed according to the actual design of your application to achieve the desired effect.
Note that before retrieving the `ParticleComponent`, the above code uses `findEntity("cloud")` to locate the target entity, because in the Spatial Editor, the node named "cloud" is the one that actually contains the `ParticleComponent`.
Additionally, in the example, only `startColor` is changed, while `endColor` remains white. Therefore, throughout the entire lifecycle of the particles, their color will be interpolated between `startColor` and `endColor` (white) according to the coefficient `t`: `t = (t_current - t_birth)/T_life`, where `T_life` is the lifecycle. As a result, you will also see other colors.
## Learn more: About the `ParticleColorVaryingProperty` class
The `ParticleColorVaryingProperty` class is used to describe the color of particles. Its instances can be used to set the `startColor` and `endColor` properties of particles. The `ParticleColorVaryingProperty` class has three properties: `type`, `value`, and `range`. Depending on the `type`, the meanings of `value` and `range` differ, and the displayed color of `startColor`/`endColor` also varies:

* When `type = ParticleVaryingPropertyType.CONSTANT`, the color is a fixed value. Each particle's `startColor`/`endColor` is determined by `value`, and `range` is ignored.
* When `type = ParticleVaryingPropertyType.RANDOM`, the color is a random value. Each particle's `startColor`/`endColor` will be randomly selected within the [value, range] range.
* When `type = ParticleVaryingPropertyType.VARYING`, the color is variable. Each particle's `startColor`/`endColor` is interpolated according to the normalized emission time: `t_emit/T_emit`, where `T_emit` is the emission duration, `value` is the color at `t_emit = 0`, and `range` is the color at `t_emit = T_emit`.

In the following three examples, only `startColor` is set for the particles, `T_emit = 4`, and lifecycle = 3. You can observe how the color of the particles changes when the settings for `type`, `value`, and `range` differ.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

* type = CONSTANT
* value = #5943ff
* range: none

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d67d7da4bd8c4fa494dd7cd38b2f4a99~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

* type = RANDOM
* value = #5943ff
* range = #ffffff

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4c5a9b195c0e4f8386cf221534158836~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

* type = VARYING
* value = #5943ff
* range = #ffffff

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b7da727954c44eddadb84e49eef705da~tplv-goo7wpa0wc-image.image)


</div>
</div>

## Important notes

* When using code to retrieve the `ParticleComponent`, ensure that the entity it operates on is one that already has a `ParticleComponent` added in the Spatial Editor. You can use the `findEntity(name)` function with the node name to locate the correct entity.
* For `ParticleColorVaryingProperty` instances, their properties do not support direct modification via setters. To adjust the value of any of these properties, you must create a new instance.
   Additionally, the property values returned by the getter of `ParticleColorVaryingProperty` are not references to the original properties; modifying these values will not affect the actual properties inside the instance.
* The `startColor` and `endColor` properties retrieved via the getter are not references to the original properties of the `ParticleComponent`. Modifying the values of these two properties will not automatically affect the original properties in the `ParticleComponent`. To modify these two properties, construct a new `ParticleColorVaryingProperty` instance and use the setter to assign the new values to these two properties.

## API reference
The `ParticleComponent` and `ParticleColorVaryingProperty` classes provide properties and functions related to particles. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
