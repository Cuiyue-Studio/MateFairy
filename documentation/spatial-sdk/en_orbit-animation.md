Orbit animation creates an effect in which an object revolves around a specified axis, moving along a circular trajectory. This is similar to how a satellite orbits a planet, or how a camera circles around a target object during filming.
## Core parameters
Below are the core parameters used to control orbit animation.
| **Parameter** | **Description** |
| --- | --- |
| axis | The normal vector of the orbital plane, which is normalized internally by the system. Do not pass `(0, 0, 0)`. |
| startTransform.position | Determine the position of the starting point and the radius of the orbit (that is, the vertical distance relative to the `axis`). The farther the starting point is from the axis, the larger the orbit's radius. |
| rotationCount | The number of complete rotations to be completed within the animation duration, with one rotation equal to 360° (for example: `1 = 360°`, `2 = 720°`, `2.5 = 900°`). |
| orientToPath | Whether to set the object's orientation toward the direction of the trajectory tangent (that is, facing the direction of motion). |
| spinClockwise | Rotation direction (clockwise or counterclockwise), which is influenced by the coordinate system's handedness. |
| duration | Animation duration. |
| delay | Delay before the animation starts. |
| repeatMode | Animation loop mode. |
| repeatCount | Number of animation loops. |
## Usage tips

* **Define orbital plane**: `axis` determines the plane in which the orbit lies. For example, `axis = (0, 1, 0)` indicates the object rotates around the Y-axis on the XZ plane.
* **Control rotation speed**: speed ≈ (2πR × `rotationCount`) / `duration`.
* **Adjust starting phase**: The initial angle is determined by `startPosition`. To specify the initial angle, first rotate the starting point.
* **Handle unexpected direction**: To correct unexpected movement direction, try changing the value of `spinClockwise` or reversing the `axis` vector; enable `orientToPath` to verify the direction more intuitively.
* **Rotate around a target point:** To rotate an object around a target point other than the origin, the object can be placed as a child node under a parent node positioned at the target point; or add center point offset in animation calculation.

## Important notes

* If the starting point is on the axis line , the radius will be zero, making it impossible to see the bypass effect. In this case, the starting point needs to be adjusted.
* Different models may have different forward axes. After enabling `orientToPath`, if "sideways movement" or "backward movement" occurs, this can be compensated for by adjusting the model's rotation.
* Displacement or rotation of the parent node or child node will change the actual motion path, so it is necessary to confirm the space where the rotation center is located.
* An excessively large `rotationCount` or an excessively short `duration` will cause the rotation speed to be excessively high, which may result in interpolation errors or jitter, so it is necessary to make the values of the two parameters within a reasonable range.

## Code sample: Revolve an entity around the origin
Make the entity perform circular motion around the origin on the XZ plane, completing two rotations every four seconds, with the direction always tangent to the path. Loop the animation.
```Kotlin
// Create an orbit animation where the entity rotates around the Y axis
val orbit = OrbitAnimation.createOrbitAnimation(
    name = "SatelliteOrbit",            // Animation name
    duration = 4f,                      // Complete two rotations every four seconds
    axis = Vector3(0f, 1f, 0f),         // The rotation axis is the Y axis
    startTransform = Transform(
        position = Vector3(2f, 0f, 0f), // The initial position is 2 units from the origin, forming an orbit with a radius of approximately 2
        EulerAngles(0f, 0f, 0f),        // The initial rotation angle is 0°
        Vector3(1f, 1f, 1f),            // The initial scale factor is 1:1:1
    ),
    spinClockwise = false,              // Rotate counterclockwise
    orientToPath = true,                // Always face the tangent's direction
    rotationCount = 2f,                 // Complete two rotations during the animation (total 720°)
    delay = 0.5f,                       // Delay 0.5 seconds before the animation starts
    repeatMode = RepeatMode.RESTART,    // Start over from the beginning for each loop
    repeatCount = -1                    // Loop playback infinitely    
)

// Convert the created orbit animation to AnimationResource to quickly bind it to the entity
val resource = AnimationResource.generate(orbit)

// Make the target entity play the newly generated animation resource
entity.playAnimationResource(resource)
```

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6ee7c6c7080c4f9fb25e6087c818f53f~tplv-goo7wpa0wc-image.image" width="1226px" /></div>

## API reference
`OrbitAnimation` class provides properties and functions related to orbit animation. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
