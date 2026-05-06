Interactive sound effects are used to quickly trigger instant audio feedback when key interactive behavior (such as clicking, confirming, or completing an operation) occurs.
## Use cases

* **Enhance operation confirmation**: Confirm that an operation has been triggered through auditory signals, reducing concerns about accidental actions.
* **Improve usability**: In scenarios where the user's attention is not focused, interface elements are not prominent, or visual feedback is easily obscured, sound can help users perceive the outcome of an operation.

## Built-in sound effects
`SpatialSoundEffect` defines the preset spatial sound effect enumeration in PICO OS 6. Among them, operation sound effects (`Op*`) are used for the user's direct interactive behaviors, while state sound effects (`State*`) are used to indicate state changes or result feedback.
| **Parameter name** | **Description** |
| --- | --- |
| OpClick | Operation: Click. |
| OpDragBegin | Operation: Drag start. |
| OpDragEnd | Operation: Drag end. |
| OpDragScale | Operation: Drag scaling. |
| StateSelected | State: Selected. |
| StateUnselected | State: Unselected. |
| StateOn | State: On. |
| StateOff | State: Off. |
| OpClose | Operation: Close. |
| StateSuccess | State: Success. |
| StateFailure | State: Failure. |
## Get the sound effect player
SpatialUI uses `CompositionLocalProvider` to inject the sound effect player into the Compose context.
In any `@Composable`, you can get the current player instance via `LocalAudioEffectPlayer.current`.
```Kotlin
@Composable
fun GetAudioEffectPlayer(){
    val audioEffectPlayer = LocalAudioEffectPlayer.current 
    Box(modifier = Modifier.clickable {
        audioEffectPlayer.playSystem(SpatialSoundEffect.OpClick)
    })
}
```

## Play built-in sound effects
In `@Composable`, you can get the sound effect player instance via `LocalAudioEffectPlayer.current` and call `playSystem()` in the user interaction callback to play system built-in sound effects.
It is generally recommended to use this in callbacks that are genuinely triggered by the user, such as `clickable` or `Button(onClick)`. For example, play `SpatialSoundEffect.OpClick` when clicking, and play `SpatialSoundEffect.StateSuccess` when providing feedback for successful operations.
```Kotlin
@Composable
fun SimplePlayAudioCase() {
    // Get the sound effect player
    val audioEffectPlayer = LocalAudioEffectPlayer.current
    Box(
        modifier =
            Modifier.size(100.dp).clickable {
                // Play system built-in sound effects
                audioEffectPlayer.playSystem(SpatialSoundEffect.OpDragScale)
            }
    )
}
```

It is not recommended to play sound effects directly during the composition phase or in logic not triggered by the user, to avoid repeated or unintended triggers.
## Override the default sound effects of SpatialUI components
In certain cases, you may want to replace the default sound effects of SpatialUI components. At this time, you can explicitly play the specified sound effect in the interaction callback of the component.
```Kotlin
@Composable
fun OverrideButtonAudioEffect(){
    val audioEffectPlayer = LocalAudioEffectPlayer.current
    Button(onClick = {
        audioEffectPlayer.playSystem(SpatialSoundEffect.StateSuccess)
    }) { 
        Text("Override default sound effect")
    }
}
```

## API reference
The `SpatialAudioEffectPlayer` class provides interfaces and enumeration for interactive sound effects. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

