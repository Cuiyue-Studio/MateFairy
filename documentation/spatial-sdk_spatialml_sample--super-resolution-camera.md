This sample deploys the Real-ESRGAN model via SpatialML to perform super resolution reconstruction on low-resolution images captured by the camera, thereby enhancing image clarity. The application includes two modes: SecureMR and RelaxMR:

* **SecureMR mode**: Secure mode. In this mode, the application cannot directly read image data from the SpatialML framework for display; the system renders and displays the super resolution results in an isolated environment to meet stricter privacy and data security requirements.
* **RelaxMR mode**: Standard mode. The application deploys and accelerates model inference via SpatialML, displaying the upscaled image results to the user.

## Prerequisites

* Refer to "[Prepare the development environment](/set-up-development-environment)" to configure the PICO Spatial SDK development environment.
* Prepare a PICO OS 6 physical device.
   PICO Emulator does not support SpatialML, so this sample project can only run on a PICO OS 6 physical device.


## Obtain the sample project
Go to "[PICO Spatial SDK examples](/document/spatial-example/)" to download the **Use SpatialML Framework for Real-Time SuperResolution** sample project.
## Run the Obtain the sample project

1. Extract the zip package of the example project, then open the example project with Android Studio.
2. Connect the PICO OS 6 physical device.
3. Run the `SuperResolutionApp` module.

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ef9bf16808fd4a918f2cf000aea2d6ad~tplv-goo7wpa0wc-image.image" width="2160px" /></div>

After running the example, you will first see the mode selection page in a `DefaultWindowContainer`, then proceed along two different paths:

* **SecureMR mode**: In SecureMR mode, two spatial containers are displayed simultaneously: the left is the SpatialML spatial container (the system-isolated rendered "viewfinder"), used to display the upscaled camera image; the right is the main panel (containing the 3D "control handle" for adjusting the zoom ratio).
* **RelaxMR mode**: In RelaxMR mode, only the main panel spatial container is displayed, and the isolated SpatialML spatial container is no longer shown; the viewfinder and UI control components are rendered within the main panel. The application reads back the super resolution result as a texture and applies it to the screen material of `Display512.glb`; meanwhile, a toolbar appears, and the slider can be used to adjust the zoom ratio. If the VQA backend LLM service has been configured, you can also click **Ask AI** to submit the current super resolution image to the remote service and display the answer in the pop-up subwindow.
   The **Ask AI** feature, corresponding to VQA (Visual Question Answering), uses the Volcano Engine Ark service by default. To experience **Ask AI**, you must first write the API Key to the device: `adb shell setprop debug.spatialml.apikey <API-KEY>`. If the API Key is not configured, the **Ask AI** button will remain disabled. You can also customize and use other LLM services.



<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);">

<div style="text-align: center"><strong>SecureMR</strong></div>

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d76ccd58f56a4fa5bf4cb9f174d025f4~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5);margin-left: 16px;">

<div style="text-align: center"><strong>RelaxMR</strong></div>

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/550358e9cde64cd9ad3298bd707bb668~tplv-goo7wpa0wc-image.image" width="402px" /></div>



</div>
</div>

## Project structure description
The core code of the example project is located under `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/`. It is recommended to read in the following order:

* `MainApplication.kt`: Application entry point; uses `DefaultWindowContainer` to launch a 640x640 main panel
* `MainActivity.kt`: The minimal entry point for `SpatialLaunchActivity`; responsible for connecting the app to the spatial app launch chain.
* `AndroidManifest.xml`: Declares camera/network permissions, as well as the style and default size of the Planar WindowContainer.
* `view/MainContainer.kt`: The first layer of state dispatch for the app; determines whether to display the mode selection page, confirmation page, or the main SpatialView.
* `vm/SrViewModel.kt`: Stores the `AppMode` state machine, initializes algorithm instances, and triggers reading and VQA.
* `vm/SrAlgorithmImpl.kt`: Core implementation of SpatialML; creates Session, Tensor, and Pipeline, and deploys the super-resolution model.
* `helper/AsyncPipelineRunner.kt`: Wraps the Pipeline as a coroutine scheduler for either "continuous operation" or "run once after modifying parameters."
* `view/SuperResolutionSpatialView.kt`: Main view branching for SecureMR / RelaxMR, permission requests, 3D interaction, and result texture mapping.
* `view/ControlBarAugment.kt`: RelaxMR toolbar; slider controls magnification, button triggers VQA.
* `vm/VQAWrapper.kt`: Encapsulates image upload and question-answer requests; by default, connects to the Volcano Engine Ark service. You can set the API Key for the VolcanoEngine Ark service, or use other LLM services.

Related resources are located in `SuperResolutionApp/src/main/assets/`, with the most critical resources including:

* `real_esrgan_x4v3.serialized.bin`: Real-ESRGAN model for super-resolution reconstruction of low-resolution images captured by the camera.
* `Display512.glb`: Screen model for displaying results.
* `Controller.glb`: Draggable controller in SecureMR mode.

## Implement a super-resolution camera app based on SpatialML
The following uses the sample project `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrAlgorithmImpl.kt` as the main example to explain, step by step, how to implement a super-resolution camera app based on SpatialML.
### **Step 1: Initialize and declare the Global Tensor**
The initialization logic is encapsulated in the asynchronous coroutine task `sessionDeferred`: first create the `SpatialMLInstance` and wait for it to be ready, then create the `SpatialMLSession`. In SecureMR mode, this Session will be associated with a SpatialML spatial container; in RelaxMR mode, no isolated container is created (container size is set to 0).
```Kotlin
class SrAlgorithmImpl(
    private val appContext: Context,
    private val scope: CoroutineScope,
    private val useSecureMr: Boolean = false,
) {
    private val sessionDeferred =
        scope.async {
            val session =
                SpatialMLInstance.create(appContext)
                    .also {
                        while (!it.ready) {
                            delay(100)
                        }
                        Log.i("SpatialML", "SpatialMLInstance ready")
                    }
                    .createSession(
                        // note: if not SecureMR mode, no need for the SpatialML container
                        //       so let's set the SpatialML container's dimensions all to 0.
                        InitInfo(
                            imageWidth = CAMERA_IMG_SIZE,
                            imageHeight = CAMERA_IMG_SIZE,
                            containerWidth = if (useSecureMr) 1200 else 0,
                            containerHeight = if (useSecureMr) 1200 else 0,
                            containerDepth = if (useSecureMr) 200 else 0,
                        )
                    )!!
            TODO()
            session
        } // end of async
    TODO()
} // end of class
```

Next, declare the Global Tensor through this `SpatialMLSession`. Declaring the Tensor as a global type is mainly for sharing and passing data between different Pipelines. This example declares the following Global Tensors:

* `scenegraph`: The scene graph rendered in the SpatialML spatial container, corresponding to the "viewfinder" in SecureMR mode.
* `dynamicTexture`: The texture map for the "viewfinder."
* `zoomAffine`: A `2x3` matrix used to describe the affine transformation of the camera image's zoomed-in area.

```Kotlin
class SrAlgorithmImpl(
    private val appContext: Context,
    private val scope: CoroutineScope,
    private val useSecureMr: Boolean = false,
) {
    private lateinit var dynamicTexture: GlobalTensor
    private lateinit var zoomAffine: GlobalTensor
    private lateinit var zoomPoints: PipelineTensor
    
    private val sessionDeferred =
        scope.async {
            val session = ...
            if (useSecureMr) {
                // only need to have a display scene graph in SecureMR mode.
                displaySceneGraph = session.newSceneFromGLTFSuspend("Display512.glb")
            }
            dynamicTexture =
                session.newGlobalTensor(
                    MultiDimensionalInitInfo(
                        DataType.UINT8,
                        intArrayOf(ZOOMED_IMG_SIZE, ZOOMED_IMG_SIZE),
                        channel = 3,
                        dynamicTexture = true,
                    )
                )
            zoomAffine =
                session.newGlobalTensor(
                    MultiDimensionalInitInfo(DataType.FLOAT32, intArrayOf(2, 3))
                )
            session
        } // end of async
        TODO()
} // end of class
```

### **Step 2: Create a one-time Pipeline for the Global Tensor**
After completing the asynchronous tasks of initializing the `Session` and declaring the Global Tensor, you can create a one-time Pipeline and execute it immediately. This Pipeline will perform the following operations:

1. Set the `visibility` property of `scenegraph` to a non-zero Tensor to make the "viewfinder" visible.
2. Replace the color map of the "viewfinder" object in `scenegraph` with `dynamicTexture`.

This pipeline is used to configure the SceneGraph rendered in the SpatialML spatial container, so it only needs to be executed in SecureMR mode.

```Kotlin
class SrAlgorithmImpl(
    private val appContext: Context,
    private val scope: CoroutineScope,
    private val useSecureMr: Boolean = false,
) {
    private lateinit var dynamicTexture: GlobalTensor
    private lateinit var zoomAffine: GlobalTensor
    private lateinit var zoomPoints: PipelineTensor
    
    private val sessionDeferred = ...
    
    val initTask =
        if (useSecureMr) {
            scope.async {
                val session = sessionDeferred.await()
                Log.i("SpatialML", "Async task (session) is done -> init pipeline")
    
                session.newPipeline().run {
                    // step 1: use the dynamic texture to replace the Panel's color texture
                    updateSceneGraphProperty(
                        displaySceneGraph,
                        "/",
                        PBRMaterials[0].BaseColorTexture,
                        dynamicTexture,
                    )
                    // step 2: set visibility
                    switchSceneVisibility(displaySceneGraph, displaySceneGraph)
    
                    Log.i("SpatialML", "submit the init pipeline")
                    submit(mapOf(), null, null)
                }
            }
        } else {
            null
        }
}
```

### **Step 3: Create main pipeline**
The main pipeline is the core of the application: it is responsible for deploying the super-resolution model and running continuously to ensure real-time updates of image results. This pipeline performs the following operations:

1. Acquire the real-time camera background image.
2. Use the affine transformation matrix in the `zoomAffine` Global Tensor to enlarge the background image, and map its pixel values from the 0–255 range to the 0.0–1.0 range to meet the Input requirements of the super-resolution model.
3. Input the enlarged image into the super-resolution model, run the model, and obtain the output.
4. Map the pixel values of the model output back to the 0–255 range, then write the final result to the `dynamicTexture` Global Tensor.

Since `dynamicTexture` is a dynamic-texture tensor, when the result is written to `dynamicTexture`, the rendering content using this tensor will be updated accordingly. Therefore, you do not need to perform any additional operations.

The sample code provides a helper class: `AsyncPipelineRunner`, which can continuously run the main pipeline to ensure real-time updates of the enlarged image.
```Kotlin
// inside SrAlgorithmImpl, after initTask = ...
val mainPipeline =
    AsyncPipelineRunner(scope, sessionDeferred) { pipeline, _ ->
        pipeline.apply {
            // step 3.1: local tensor for camera image
            val rightEyeImg =
                newLocalTensor(
                    MultiDimensionalInitInfo(
                        DataType.UINT8,
                        intArrayOf(CAMERA_IMG_SIZE, CAMERA_IMG_SIZE),
                        3,
                    )
                )
            // step 3.2: get the camera image into the tensor
            rectifiedVSTAccess(
                rightImageResult = rightEyeImg,
                leftImageResult = null,
                timestampResult = null,
                cameraMatrixResult = null,
            )
            // step 3.3: affine the camera image
            val affinedUint8 =
                newLocalTensor(
                    MultiDimensionalInitInfo(
                        DataType.UINT8,
                        intArrayOf(AFFINE_IMG_SIZE, AFFINE_IMG_SIZE),
                        3,
                    )
                )
            applyAffine(zoomAffine, rightEyeImg, affinedUint8)
            // step 3.4: converted the image into float, and scale it to 0~1
            val affinedFloat =
                newLocalTensor(
                    MultiDimensionalInitInfo(
                        DataType.FLOAT32,
                        intArrayOf(AFFINE_IMG_SIZE, AFFINE_IMG_SIZE),
                        3,
                    )
                )
            copy(affinedUint8, affinedFloat)
            arithmetic("{0} / 255.0", arrayOf(affinedFloat), affinedFloat)
            // step 3.5: prepare the tensor to hold the output from super-resolution
            val zoomedResult =
                newLocalTensor(
                    MultiDimensionalInitInfo(
                        DataType.FLOAT32,
                        intArrayOf(ZOOMED_IMG_SIZE, ZOOMED_IMG_SIZE),
                        3,
                    )
                )
            // step 3.6: deploy the super-resolution model and run
            //           input: affinedFloat, i.e., the image after affined, type conversion and scaling
            //           output: zoomedResult
            loadAssetToSharedMemory(appContext, "real_esrgan_x4v3.serialized.bin") {
                runModelInference(
                    modelName = "real_esrgan_x4v3",
                    modelType = Pipeline.ModelInferenceType.QNN_HTP,
                    modelBinary = it,
                    inputs = arrayOf(Pipeline.ModelNodeEncoding("image", affinedFloat)),
                    outputs =
                        arrayOf(Pipeline.ModelNodeEncoding("upscaled_image", zoomedResult)),
                )
            }
            // step 3.7: scale it back to 0~255
            arithmetic("{0} * 255.0", arrayOf(zoomedResult), zoomedResult)
            copy(zoomedResult, dynamicTexture)
        }
    }
```

### Step 4: Create **Affine Pipeline**
The Affine Pipeline is used to calculate and update the contents of the `zoomAffine` Global Tensor according to the magnification ratio set by the user, ensuring that the `affinedFloat` Input to the model matches the user's magnification requirements. This pipeline does not need to run in a loop; it only needs to be triggered after the user updates the magnification ratio:
```Kotlin
// inside SrAlgorithmImpl, after initTask = ...
// delcare the zoomPoints as a class member, because we may reset its values in
// UI callback triggered by user events.
private lateinit var zoomPoints: PipelineTensor

private val affinePipeline =
    AsyncPipelineRunner(scope, sessionDeferred) { pipeline, _ ->
        pipeline.apply {
            zoomPoints =
                newLocalTensor(MultiDimensionalInitInfo(DataType.FLOAT32, intArrayOf(3, 1), 2))

            // affine transform: from the region selected by zoomPoints to
            //                   a square of 128x128 (because our super-resolution mode
            //                   expects a 128x128 input image)
            val targetZoomPoints =
                newLocalTensor(zoomPoints.config).apply {
                    SharedMemory.create("affined_dst_points", 6 * Float.SIZE_BYTES).use { mem ->
                        val buf = mem.mapReadWrite()
                        buf.order(ByteOrder.nativeOrder())
                        buf.putFloat(0.0f)
                        buf.putFloat(0.0f)

                        buf.putFloat(AFFINE_IMG_SIZE.toFloat() - 1) // AFFINE_IMG_SIZE = 128
                        buf.putFloat(0.0f)

                        buf.putFloat(0.0f)
                        buf.putFloat(AFFINE_IMG_SIZE.toFloat() - 1)

                        SharedMemory.unmap(buf)
                        tensorResource = mem
                    }
                }

            getAffine(zoomPoints, targetZoomPoints, zoomAffine)
        }
    }
```

### Step 5: Trigger Affine Pipeline via callback
As mentioned earlier, the Affine Pipeline only needs to run when the user updates the magnification ratio. The sample provides a method as the UI event callback entry point.
This implementation uses `AsyncPipelineRunner.runOnceAfterValueReset`: it allows the caller to update the Tensor data first, then submit the pipeline for execution. This enables writing new `zoomPoints/zoomAffine` when the user updates the magnification ratio, and then triggering the Affine Pipeline calculation to take effect.
```Kotlin
// inside SrAlgorithmImpl, after affinePipeline = ...

/**
 * Callback when the upscale ratio is changed.
 *
 *@param upscaleRatio a value between 1.0 to 16.0. 16.0 means 16x upscale. 1.0 means no
 *   upscale.
 */
fun setUpscaleFactor(
    upscaleRatio: Float,
    prevTask: Deferred<Pipeline.RunTask>? = null,
): Deferred<Pipeline.RunTask> =
    affinePipeline.runOnceAfterValueReset(prevTask) {
        val zoomFactor = 1f - 16 * UPSCALE_CONSTANT / upscaleRatio
        if (zoomFactor !in 0.0f..<1.0f) {
            Log.e("SpatialML", "wrong zoom factor $zoomFactor (upscale ratio = $upscaleRatio)")
            throw SpatialMLException("zoom factor must be [0.0, 1.0), got $zoomFactor")
        }

        val beginAfterZoom = CAMERA_IMG_SIZE * zoomFactor / 2
        val endAfterZoom = CAMERA_IMG_SIZE * (1.0f - zoomFactor / 2.0f) - 1.0f

        SharedMemory.create("update_affine_points", 6 * Float.SIZE_BYTES).use { mem ->
            val buffer = mem.mapReadWrite()
            buffer.order(ByteOrder.nativeOrder())
            buffer.putFloat(beginAfterZoom)
            buffer.putFloat(beginAfterZoom)

            buffer.putFloat(endAfterZoom)
            buffer.putFloat(beginAfterZoom)

            buffer.putFloat(beginAfterZoom)
            buffer.putFloat(endAfterZoom)

            SharedMemory.unmap(buffer)
            zoomPoints.tensorResource = mem
        }
    }
```

### Step 6: Start main pipeline in the UI
At this point, the core algorithm of the super-resolution camera has been completed. Next, it is necessary to start the main pipeline in the UI layer (in the sample project at `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrViewModel.kt`) so that the algorithm continuously processes and outputs image results according to the established workflow.
The sample uses `4x` as the default magnification. Therefore, before starting the main pipeline, `setUpscaleFactor(4.0f, ...)` is called to update the affine matrix, and then inference tasks are continuously submitted:
```Kotlin
private lateinit var superResolution: SrAlgorithmImpl

// ... other init

superResolution =
    SrAlgorithmImpl(
        appContext,
        viewModelScope,
        useSecureMr = appMode.value == AppMode.SECURE_MR_CONFIRMED,
    )
val affineTask = superResolution.setUpscaleFactor(4.0f, superResolution.initTask)
superResolution.mainPipeline.runContinuously(10, affineTask)
```

### 
```Kotlin
private lateinit var superResolution: SrAlgorithmImpl

// ... other init

superResolution =
    SrAlgorithmImpl(
        appContext,
        viewModelScope,
        useSecureMr = appMode.value == AppMode.SECURE_MR_CONFIRMED,
    )
val affineTask = superResolution.setUpscaleFactor(4.0f, superResolution.initTask)
superResolution.mainPipeline.runContinuously(10, affineTask)
```

### Step 7: Implement rendering in RelaxMR mode
In the previous steps, we did not implement the logic for "direct rendering of the super-resolution output by the application". This is because, in SecureMR mode, rendering is isolated by the system within the SpatialML spatial container: after creating the `sceneGraph` and `dynamicTexture` and initializing the pipeline, updates to `dynamicTexture` are automatically reflected in the rendering results of this isolated container.
In RelaxMR mode, the above process skips the creation of the SpatialML spatial container, loading of the SceneGraph, and pipeline initialization; therefore, the application must read `dynamicTexture` itself, use it as a texture map, and complete the rendering. The following code (located at `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrAlgorithmImpl.kt`) demonstrates how to read back `dynamicTexture` as a renderable texture resource:
```Kotlin
// inside SrAlgorithmImpl

/**
 * The callback to read the upscaled image as a dynamic texture so that it can be rendered
 * inside the [com.pico.spatial.ml.sample.sr.view.SuperResolutionSpatialView] container.
 */
fun imageReadbackAsTexture() =
    scope.async {
        sessionDeferred.await()
        dynamicTexture.readbackAsTextureResourceSuspend()
    }
```

According to the requirements of the SpatialML privacy statement: when a session uses camera data, the application must obtain user camera permission before performing readback and rendering operations. In the example, camera permission is requested only in `RELAX_MR_CONFIRMED` mode, and the algorithm is started after authorization:
```Kotlin
@Composable
fun SuperResolutionSpatialView(srViewModel: SrViewModel = viewModel()) {
    // first, apply for camera permission ONLY in RELAX MR mode
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            hasCameraPermission = isGranted
        }

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission && appMode == SrViewModel.AppMode.RELAX_MR_CONFIRMED) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            srViewModel.init(context, 4.0f)
        }
    }
    
    if (appMode != SrViewModel.AppMode.RELAX_MR_CONFIRMED || hasCameraPermission) {
        SpatialView(
            ...,
            update = { content, _ ->
                // textureHasReset: viewmodel state
                // to ensure the texture is only reset ONCE
                if (!srViewModel.textureHasReset) {
                    content.entities
                        .filter { it.getName() == "display512" }
                        .forEach { display ->
                            val newMat = UnlitMaterial.create()
                            ... // init the newMat
                            srViewModel.useZoomedImageAsBaseColor(newMat)
                            // which calls:
                            //     newMat.setBaseColorTexture(
                            //             superResolution.imageReadbackAsTexture().await()
                            //    )
                            //    srViewModel.textureHasReset = true
                            
                            // Then: replace the display entity's material with the
                            //       newly-created UnlitMaterial whose base color uses
                            //       the dynamic-texture from the super-resolution.
                            display.components[ModelComponent::class.java]
                                ?.materials
                                ?.set(0, newMat)
                        }
                }
            },
        )
    }
}
```


