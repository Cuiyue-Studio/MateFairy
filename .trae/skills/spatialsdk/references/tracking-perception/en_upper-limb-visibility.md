The upper limb visibility capability enables the real-time segmentation and presentation of the user's arms and held objects (such as controllers), allowing real upper limbs to be correctly displayed in the virtual environment.
## Use cases

* In fully immersive or semi-immersive environments, upper limb visibility reveals the real hands and held objects, helping users more accurately perceive the operation position, thereby improving the precision and safety of interactions.
* In MR scenes, the segmented real hand replaces the virtual hand model, making the fusion of the real world and virtual content more natural and significantly enhancing immersion and realism.

## Limitations
Only supported in Stage.
## Upper limb rendering modes
`UpperLimbRenderMode` provides the following upper limb rendering modes:

* `Default`: Default mode, follows system settings;
* `Visible`: Displays the real upper limb;
* `Hidden`: Hides the real upper limb.

## Set upper limb visibility
You can set the upper limb visibility in the Stage container through Stage DSL or the `openStage` interface.
When both methods are used simultaneously, the parameters explicitly specified in `openStage` take precedence. For parameters not set in `openStage`, relevant settings in Stage DSL are used, and their effective rules remain consistent with other Stage parameters.

* **Configure via Stage DSL**
   When defining Stage, you can specify its initial configuration via DSL, including the upper limb rendering mode.
   ```Kotlin
   fun SpatialAppScope.Stage(
       id: String,
       immersion: Immersion = Immersion.Default,
       brightness: Brightness = Brightness.Automatic,
       upperLimbRenderMode: UpperLimbRenderMode = UpperLimbRenderMode.Default,
       targetActivity: Class<out ComponentActivity> = SpatialStubActivity::class.java,
       content: @Composable StageScope.() -> Unit,
   )
   ```

* **Modify configuration via** **`openStage`**
   By default, `openStage` is used to control whether Stage is visible, and it also supports overriding some parameter settings when opening Stage.
   ```Kotlin
   openStage(
       context,
       UPPERLIMBSTAGEID,
       upperLimbRenderMode = UpperLimbRenderMode.Default,
   )
   ```


## Dynamically modify upper limb visibility
At runtime, use the `setUpperLimbRenderMode()` function to dynamically modify upper limb visibility.
```Plain Text
val local = LocalStageUpperLimbRenderModelManager.current
local.setUpperLimbRenderMode(UpperLimbRenderMode.Hidden)
```

## Listen for upper limb visibility & remove listener
You can use the `addUpperLimbRenderModeChangeListener` function to listen for upper limb visibility configuration and retrieve the currently effective configuration.
When listening is not needed, use the `removeUpperLimbRenderModeChangeListener` function to remove the listener.
```Kotlin
var renderNodeMode by remember {
    mutableStateOf<UpperLimbRenderMode?>(null)
}

DisposableEffect(Unit) {
    //  Create a listener for upper limb visibility mode changes to detect changes in the upper limb rendering mode at runtime
    val listener = object :
        UpperLimbRenderModeChangeListener {
        override fun onUpperLimbRenderModeChanged(upperLimbRenderMode: UpperLimbRenderMode) {
            renderNodeMode = upperLimbRenderMode
        }

    }
    // Register a listener to monitor changes in upper limb visibility
    local.addUpperLimbRenderModeChangeListener(listener)

    onDispose {
        // Remove the listener
        local.removeUpperLimbRenderModeChangeListener(listener)
    }
}
```

