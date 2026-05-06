This article will guide you in using the PICO Spatial SDK to create a Super Resolution camera app. You will learn how to use machine learning models to enlarge and enhance the resolution of images captured by the camera.
## Download source code
Go to the [PICO Spatial SDK sample](/document/spatial-example/) documentation to download the project source code for the spatial-ml-sample spatial app.
## App introduction
The Super Resolution camera deploys the Real-ESR GAN model via SpatialML to perform super resolution reconstruction on low-resolution images captured by the camera, improving image clarity and quality. This app includes two modes: RelaxMR and SecureMR.

* **RelaxMR mode**: This is the standard mode. The app deploys and accelerates the model via SpatialML, enlarges and enhances the resolution of camera images, and presents them to the user.
* **SecureMR mode**: This is the **secure** mode. In this mode, the app cannot read images from the SpatialML framework for display. Instead, the system isolates and directly displays the enlarged and enhanced camera images to the user. This mode is significant in scenarios with high requirements for data security and privacy.

When the app is running, the user interface consists of two main parts:

* **Control panel**: You can adjust the resolution here. In RelaxMR mode, the enlarged camera images are also presented in this panel.
* **SpatialML spatial container**: Used for system isolation and secure presentation of enlarged camera images, visible only in SecureMR mode. A “viewfinder” asset will be drawn in this container to display the enlarged camera images.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d76ccd58f56a4fa5bf4cb9f174d025f4~tplv-goo7wpa0wc-image.image)
In SecureMR mode, two spatial containers are displayed: the 'viewfinder' drawn in isolation by the system in the SpatialML spatial container on the left, and the 'main panel' container on the right, which contains a 'handle' to control the zoom ratio.


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/550358e9cde64cd9ad3298bd707bb668~tplv-goo7wpa0wc-image.image" width="406px" /></div>

In RelaxMR mode, only the 'main panel' spatial container is rendered, and the isolated SpatialML spatial container does not appear. The 'viewfinder' and UI control components are both rendered in the 'main panel container'.


</div>
</div>

## Implementation steps
Refer to the following steps to implement a Super Resolution camera app based on SpatialML (the relevant code is located in the `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrAlgorithmImpl.kt` file in the sample code).
### **Step 1: Initialize and declare Global Tensor**
The initialization operation is completed in the asynchronous coroutine task `sessionDeferred`. This task first creates a `SpatialMLInstance`, waits for it to be ready, and then creates a `SpatialML session` associated with the SpatialML container.
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
                        // so let's set the SpatialML container's dimensions all to 0.
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

Next, you can declare the Global Tensor through this SpatialML Session. Declaring Tensor as a global type is mainly to facilitate sharing and transferring data between different Pipelines.
As shown in the code below, the following Global Tensors are declared in this application:

* `scenegraph`: The scene graph drawn in the SpatialML spatial container, that is, the "viewfinder" in the application under SecureMR mode.
* `dynamicTexture`: The texture map for the "viewfinder".
* `zoomAffine`: A 2x3 matrix used as the affine transformation matrix for enlarging the camera image.

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

### **Step 2: Create a one-time Pipeline for Global Tensor**
After completing the asynchronous tasks of initializing `Session` and declaring Global Tensors, you can create a one-time Pipeline and execute it immediately. This Pipeline performs the following operations:

1. Set the `scenegraph`'s `visibility` property to a non-zero Tensor, making the "viewfinder" visible.
2. Replace the color map of the "viewfinder" object in `scenegraph` with `dynamicTexture`.

Note that since the purpose of this pipeline is to operate the "viewfinder" Scenegraph rendered in the SpatialML container, this one-time pipeline only needs to be executed in SecureMR mode.
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

### **Step 3: Create the Main Pipeline**
The Main Pipeline is the core of this application. It deploys the Super-Resolution model and needs to run continuously and in real time. This Pipeline performs the following operations:

1. Acquire the real-time camera background image.
2. Use the affine transformation matrix in the `zoomAffine` Global Tensor to enlarge the background image, and map its pixel values from the 0-255 range to the 0.0-1.0 range to meet the input requirements of the Super-Resolution model.
3. Input the enlarged image into the Super-Resolution model, run the model, and obtain the output.
4. Map the stylized image pixel values back to the 0-255 range, scale again using the affine transformation matrix, and then write the final result to the `dynamicTexture` Global Tensor.

Since `dynamicTexture` is a dynamic texture tensor, you do not need to perform any additional operations. Once the stylized result is written to `dynamicTexture`, the scene or UI using this Tensor will automatically update.

The sample code provides a helper class: `AsyncPipelineRunner`, which can continuously run the Main Pipeline to ensure real-time updates of the enlarged image.
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
The Affine Pipeline is used to calculate and reset the data of the `zoomAffine` Global Tensor according to the user-defined magnification ratio, ensuring that the `affinedFloat` Input for the model matches the user's magnification requirements. This Pipeline does not need to be executed in a loop; it only needs to be triggered after the user resets the magnification ratio:
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

### Step 5: Callback Affine Pipeline
As mentioned earlier, the Affine Pipeline only needs to run when the user updates the magnification ratio. Let us prepare a method for the corresponding UI event callback.
This implementation uses the auxiliary method of `AsyncPipelineRunner`: `runOnceAfterValueReset`. This method allows the caller to proactively update data in the Tensor first, and after the data is updated, automatically submit the pipeline for execution. Specifically, in the UI event callback method where the user updates the magnification factor, we can use the `runOnceAfterValueReset` method to update the data of the `zoomAffine` Global Tensor, and then trigger the execution of the Affine Pipeline.
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

### Step 6: Start the Main Pipeline in the UI
At this point, we have implemented the core algorithm of the super-resolution camera. Next, you can start the Main Pipeline in the corresponding user interface (the code is located in `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrViewModel.kt` in the sample project), allowing the super-resolution camera to begin processing and outputting images according to the established workflow and parameters.
We use 4x as the default magnification factor, so before starting the Main Pipeline, use the callback function `setUpscaleFactor` prepared in the previous step to update the affine transformation matrix with a magnification factor of 4x.
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

### Finally: Rendering in RelaxMR mode
In the previous steps, you may have noticed that we did not render the output of Super-Resolution in the app. In SecureMR mode, there is no need to implement rendering operations, because rendering in this mode is automatically performed by the system in an isolated and secure manner within the SpatialML spatial container. When you have created the `sceneGraph` and `dynamicTexture` according to the steps above, and initialized the Pipeline, updates to `dynamicTexture` will be automatically rendered inside the SpatialML spatial container.
However, in RelaxMR mode, the steps above skip the creation of the SpatialML spatial container, loading of `sceneGraph`, and initialization of the Pipeline, so the app needs to read `dynamicTexture` as a texture map and render it itself. The following code (in the sample code file `SuperResolutionApp/src/main/java/com/pico/spatial/ml/sample/sr/vm/SrAlgorithmImpl.kt`) implements this process:
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

It is worth noting that, according to the requirements in the SpatialML privacy statement, since camera data is used in this session, the app must first obtain the user's camera permission authorization before rendering operations can be performed.
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

