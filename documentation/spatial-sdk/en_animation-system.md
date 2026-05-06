The animation system is a comprehensive set of mechanisms and tools used to drive dynamic effects for content in a scene. With the animation system, you can add dynamic visual and interactive effects to characters, objects, and UI elements in the scene.
## Core features
PICO Spatial SDK provides a complete animation system framework, covering the following core features:

* **Animation data management**: Supports reading animation data bound to 3D models.
* **Animation playback control**: Includes functions such as play, pause, stop, loop, and more.
* **Programmatic generation**: Achieves tween animation through programmatic generation.

## Animation types
PICO Spatial SDK supports skeletal animation, tween animation, and orbit animation, and also provides various animation events.

* **Skeletal animation**: Character animation based on skeletal hierarchy and skinning weights, suitable for natural movement of complex models such as creatures. For details, see "[Skeletal animation](/skeletal-animation)".
* **Tween animation**: Lightweight animation that drives property changes through interpolation, suitable for scenarios where Transform or Material undergo simple changes. For details, see "[Tween animation](/tween-animation)".
* **Orbit animation**: Orbit animation is an animation effect that revolves around a specified axis. It causes an object to move along a circular path around a certain axis. For details, see "[Orbit animation](/orbit-animation)".
* **Timeline animation**: Timeline animation is created using the Timelines animation effector in Spatial Editor. For details, see "[Timeline animation](/timeline-animation)".
* **Animation events**: Embeds callback mechanisms during animation playback to enable custom controls such as sound effect triggering and logic synchronization. For details, see "[Animation events](/animation-events)".

