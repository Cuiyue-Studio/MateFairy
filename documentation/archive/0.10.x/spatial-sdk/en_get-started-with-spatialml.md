This document describes how to use the PICO Spatial SDK to deploy custom algorithms to SpatialML and render algorithm-driven scene outputs to the SpatialML spatial container, thereby enabling immersive MR interaction experiences.
The following diagram illustrates the workflow of SpatialML:

1. Create a SpatialML instance, and then create a SpatialML Session within the SpatialML instance.
2. Create a Global Tensor within the SpatialML Session.
3. Create a SpatialML Pipeline within the SpatialML Session, then create Local Tensors and Placeholders in the SpatialML Pipeline, deploy custom algorithms, and perform computational operations.
4. Submit the Pipeline for execution.
5. Render the algorithm-driven scene content to the SpatialML spatial container or read the algorithm output from SpatialML.

<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/89e0f60dd3434415826e705a3a93ac93~tplv-goo7wpa0wc-image.image" width="670px" /></div>

## Prerequisites

* Add build dependencies (it is recommended to use the version catalog file [[libs.versions.toml]](https://developer.android.com/build/dependencies#add-dependency)).
   * In the `[libraries]` section of `libs.versions.toml`, add the following:
      ```Kotlin
      [libraries]
      // ...
      spatial-ml-securemr = { group = "com.pico.spatial.ml", name = "securemr", version.ref = "spatial" }
      spatial-ml-readback = { group = "com.pico.spatial.ml", name = "readback", version.ref = "spatial" }
      ```

   * In the `dependencies {}` section of the module's `build.gradle.kts` build script file, add the following:
      ```Kotlin
      dependencies {
          // ...
          implementation(libs.spatial.ml.securemr)
          implementation(libs.spatial.ml.readback)
      }
      ```


## Steps
Refer to the following steps to quickly get started with SpatialML using the PICO Spatial SDK.
### Step 1: Create a SpatialML instance and a SpatialML Session
Call the `SpatialMLInstance.create()` function to create a SpatialML instance. You must pass an Android Application Context to this function. Each app is limited to creating only one `SpatialMLInstance` object.
Call the `SpatialMLInstance.createSession()` function to create a SpatialML Session. You must wait until the `SpatialMLInstance` object's `ready` property returns `True` before creating a `SpatialMLSession` object. Therefore, it is recommended to asynchronously create the `SpatialMLSession` object within a Kotlin Coroutine Job.
When calling the `SpatialMLInstance.createSession()` function, you can:

* Specify the monocular resolution of the stereo camera used in this Session via the `imageWidth` and `imageHeight` parameters. The camera resolution cannot be changed within the same Session. Although you can set different camera resolutions for multiple Sessions in the App, to optimize performance, we recommend using the same resolution uniformly. This reduces the latency when each Session acquires stereo camera images.
* Specify the dimensions of the SpatialML spatial container via the `containerWidth`, `containerHeight`, and `containerDepth` parameters.

The following code demonstrates how to create a `SpatialMLInstance` object and a `SpatialMLSession` object.
```Kotlin
fun CoroutineScope.initializeSpatialML(appContext: Context) = async {
    val session =
        SpatialMLInstance.create(appContext)
            .also {
                while (!it.ready) {
                    delay(100)
                }
                Log.i("SpatialML", "SpatialMLInstance ready")
            }
            .createSession(InitInfo(
                1024, 1024, // camera resolution
                1200, 1200, 600 // SpatialML container size
            ))!!
}
```

### Step 2: Declare a Global Tensor in the SpatialML Session
After obtaining the `SpatialMLSession` object, you can call the `newGlobalTensor()` function to create a Global Tensor. The Global Tensor is used to transfer and share data between different Pipelines.
The following code shows how to create a 1024x2048, 3-channel, UINT8 multidimensional Global Tensor.
When creating a multidimensional Tensor, you only need to specify the following parameters in `MultiDimensionalInitInfo`:

* `dataType`: The data type of the Tensor.
* `dimensions`: The dimensions of the Tensor.
* `channel`: The number of channels of the Tensor.

The channel should not be considered a dimension of the Tensor, but rather as part of its data type.
For example, in the following code, the `textureR8G8B8` tensor contains 1024x2048 elements, with each element consisting of 3 `UINT8` values. This design is intended to maintain consistency with OpenCV. In SpatialML, linear algebra operations on multi-channel, multi-dimensional tensors behave exactly the same as operations on multi-channel `cv::Mat` in OpenCV. This allows you to more easily port your existing OpenCV preprocessing or postprocessing code.

```Kotlin
fun CoroutineScope.initializeSpatialML(appContext: Context) = async {
    val session = ...
    val textureR8G8B8 = session!!.newGlobalTensor(MultiDimensionalInitInfo(
        DataType.UINT8, // data type of the tensor
        intArrayOf(1024, 2048), // dimensions of the tensor
        3 // channel of the tensor
    ))
}
```

The following code will create a ColorArray Tensor (a type of Structured Tensor) containing 2 color values in the `R32G32B32` format.
```Kotlin
session!!.newGlobalTensor(ColorArrayInitInfo(ColorType.R32G32B32_FLOAT, 2))
```

After creating the Tensor, you can write data to it via the `tensorResource` property. Since SpatialML uses `SharedMemory` objects to transfer data between the application and SpatialML, you must use the `ByteOrder.nativeOrder()` function when writing data to ensure the correct byte order.
For example, the following code demonstrates how to write an HSV color map with a V value of 1.0 to a 1024x2048 multidimensional Tensor.
```Kotlin
fun CoroutineScope.initializeSpatialML(appContext: Context) = async {
    val session = ...
    val textureR8G8B8 =
        session
            .newGlobalTensor(
                MultiDimensionalInitInfo(
                    DataType.UINT8,
                    intArrayOf(1024, 2048),
                    3,
                )
            )
            .apply {
                SharedMemory.create(
                        "initlization_demo_ball_color",
                        1024 * 2048 * 3,
                    )
                    .use { mem ->
                        val buf = mem.mapReadWrite()
                        buf.order(ByteOrder.nativeOrder())
                        for (s in 0..<1024) {
                            for (h in 0..<2048) {
                                val color =
                                    Color.hsv(
                                        h.toFloat() / 1024 * 360,
                                        s.toFloat() / 2048,
                                        1.0f,
                                    )
                                buf.put((color.red * 255).toInt().toByte())
                                buf.put((color.green * 255).toInt().toByte())
                                buf.put((color.blue * 255).toInt().toByte())
                            }
                        }
                        SharedMemory.unmap(buf)
                        tensorResource = mem
                    }
            }
}
```

### Step 3: Create a SpatialML Pipeline
Call the `SpatialMLSession.newPipeline()` function to create a SpatialML Pipeline (that is, a `Pipeline` object). Next, you can:

* Call the `newLocalTensor()` function in the `Pipeline` object to create a Local Tensor, or call the `newPlaceholder()` function to create a Placeholder. For details, refer to [Create tensors in Pipeline](/en_sdk/en_get-started-with-spatialml_111).
* Perform computational operations in the `Pipeline` object. For details, refer to [Perform computation operations in Pipeline](/en_sdk/en_get-started-with-spatialml_111).
* Perform slicing and assignment operations in the `Pipeline` object. For details, refer to [Perform slicing and assignment operations in Pipeline](/sdk/en_get-started-with-spatialml_111).
* Deploy machine learning models in the `Pipeline` object. For details, refer to [Deploy machine learning models in the Pipeline and accelerate model inference using Qualcomm NPU](/sdk/en_get-started-with-spatialml_111).

#### Create tensors in Pipeline
SpatialML allows you to use Global Tensors directly as inputs or outputs for any operation in a `Pipeline` object. If you want the same `Pipeline` object to reuse different data at runtime, it’s recommended to use Placeholders. You can declare the operation’s inputs or outputs as Placeholders and replace them with different Global Tensors on each submit. For example, map the current camera image to a Placeholder and submit the `Pipeline` object repeatedly, replacing it with a different frame each time, so you can store consecutive images and use them for trajectory analysis, Kalman filtering, and other algorithms.
The following code demonstrates how to call the `newLocalTensor()` function to create a Local Tensor.
```Kotlin
session.newPipeline().run {
    val localTensor4x3 = newLocalTensor(
        MultiDimensionalInitInfo(DataType.FLOAT32, intArrayOf(4, 3)
    )
}
```

The following code demonstrates how to call the `newPlaceholder()` function to create a Placeholder.
```Kotlin
session.newPipeline().run {
    val localTensor4x3 = newPlaceholder(
        MultiDimensionalInitInfo(DataType.FLOAT32, intArrayOf(4, 3)
    )
}
```

You can also directly call `newPlaceholderLike()` to create a Placeholder identical to the specified Global Tensor.
```Kotlin
val originalR8G8B8 = session.newGlobalTensor(
        MultiDimensionalInitInfo(
            DataType.UINT8,
            intArrayOf(512, 512),
            3,
        )
    )
session.newPipeline().run {
    val localTensorR8G8B8 = newPlaceholderLike(originalR8G8B8)
    // equiv to
    val localTensorR8G8B8_equiv = newPlaceholder(
        MultiDimensionalInitInfo(
            DataType.UINT8,
            intArrayOf(512, 512),
            3,
        )
    )
}
```

In the `Pipeline` object, you can use common Kotlin structures directly as Local Tensors. For example, the following code converts a `Point` object into a Local Tensor:
```Kotlin
session.newPipeline().run {
    val point2 = newLocalTensor(Point(100, 200))
}
```

#### **Perform computation operations in Pipeline**
SpatialML provides a wide range of computation operations in Pipeline, including preprocessing and postprocessing, XR data acquisition, bitwise operations, logical (Boolean) operations, and executing JavaScript scripts.
For example, with the `arithmetic` function, you can add a linear algebra calculation step in Pipeline. You can define specific operations using a string expression. In the sample code below, line 9's `{}` is a placeholder. For example, `{1}` represents the Tensor at index 1 in the input Tensor array. The computation result will be written to the output Tensor you specify.
* The example code below only adds this linear algebra operation to the `Pipeline` object and does not execute it immediately. This operation will only begin computation after you submit the `Pipeline` object.
* The `arithmetic` function requires that both its input and output tensors must be two-dimensional tensors (that is, mathematical "matrices"). Similarly, other operations in the `Pipeline` object also have specific requirements for input or output tensors. You can refer to the specific API documentation for details.

```Kotlin
session.newPipeline().apply {
    val testData = newLocalTensor(...)
    val testData2 = newLocalTensor(...)
    val testData3 = newLocalTensor(...)
    val testData4 = newLocalTensor(...)
    
    // testData = testData * (tensor2 + tensor3) ^ 4 - tan(tensor4)
    arithmetic(
        "{0} * ({1} + {2}) ^ 4 - tan {3}",
        arrayOf(testData, testData2, testData3, testData4),
        testData,
    )

}
```

#### **Perform slicing and assignment operations in Pipeline**
You can use Python-like slicing operations to extract specified segments from a Local Tensor and assign them to another Local Tensor.
The following code creates a 256x256 three-channel Tensor and three 128x128 single-channel Tensors (used for R, G, and B values respectively), and performs the following operations:

1. Copy the R value Tensor to the R channel of the top-left 128x128 region of the target three-channel Tensor.
2. Copy the G value Tensor to the G channel of the top-right 128x128 region of the target three-channel Tensor.
3. Copy the B value Tensor to the B channel of the lower half region of the target three-channel Tensor, and use the `step` parameter to assign values every other pixel horizontally.

The general format of the slicing syntax is as follows: `tensor[A1..B1 step C1, A2..B2 step C2, /*...*/, An..Bn step Cn][Ac..Bc step Cc]`
This syntax consists of two parts:

* **Dimension slicing** (the first pair of `[]`): You must provide a Kotlin `IntProgression` expression (such as `0..127`) for each dimension of the Tensor, and the number of expressions must match the number of Tensor dimensions.
* **Channel slicing** (the second pair of `[]`): This part is optional and contains only one Kotlin `IntProgression` expression, used to specify the channels to operate on.

```Kotlin
session.newPipeline().apply {
    val rgbTexture = newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(256, 256)), 3
        ) // 3 channel 
    val newColorRed =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
    val newColorGreen =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
    val newColorBlue =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
        
    copy(
        newColorRed,
        rgbTexture[0..127, 0..127][0..0],
    )
    copy(
        newColorGreen[updateColorRegion],
        rgbTexture[0..127, 128..255][1..1],
    )
    copy(
        newColorBlue[updateColorRegion],
        rgbTexture[127..255 step 1, 0..255 step 2][2..2],
    )
}
```

In addition to using Kotlin `IntProgression` expressions, you can also use a special Slice Tensor as a slice index. This Tensor must be created through `com.pico.spatial.ml.securemr. Tensor.SliceInitInfo`.
Depending on the slicing target, the `size` property of the Slice Tensor has the following requirements:

* **Dimension slicing** (the first pair of `[]`): `size` must be the same as the number of dimensions of the target Tensor. For example, in the code sample below, the `size` of `updateColorRegion` is set to 2 to match the two-dimensional target Tensor `rgbTexture`.
* **Channel slicing** (the second pair of `[]`): `size` must be 1.

When creating a Slice Tensor, you need to specify its number of channels (2 or 3), which determines the format of its internal data and slicing behavior:

* **Number of channels is 2 (default)**: The data format is `[begin1, end1, begin2, end2, ...]`, which is equivalent to the Kotlin syntax `begin1..<end1, begin2..<end2, ...`.
* **Number of channels is 3**: The data format is `[begin1, end1, s1, begin2, end2, s2, ...]`, which is equivalent to the Kotlin syntax `begin1..<end1 step s1, begin2..<end2 step s2, ...`.

```Kotlin
session.newPipeline().apply {
    val updateColorRegion = newLocalTensor(SliceInitInfo(DataType.INT32, 2))
    
    val rgbTexture = newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(256, 256)), 3
        ) // 3 channel
    val newColorRed =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
    val newColorGreen =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
    val newColorBlue =
        newLocalTensor(
            MultiDimensionalInitInfo(DataType.UINT8, intArrayOf(128, 128))
        )
        
    copy(
        newColorRed,
        rgbTexture[updateColorRegion][0..0],
    )
    copy(
        newColorGreen[updateColorRegion],
        rgbTexture[updateColorRegion][1..1],
    )
    copy(
        newColorBlue[updateColorRegion],
        rgbTexture[updateColorRegion][2..2],
    )
}
```

#### **Deploy machine learning models in the Pipeline and accelerate model inference using Qualcomm NPU**
Refer to the following code to call the `runModelInference` function to deploy a custom machine learning model in the `Pipeline` object and accelerate model inference using Qualcomm NPU.
When calling the `runModelInference` function, you need to associate the input and output tensors with the nodes in the machine learning model. You need to use `Pipeline.ModelNodeEncoding(nodeName, tensor)` to specify this mapping relationship. Here, the `nodeName` parameter is the node ID or identifier to which you want to write data before the machine learning model runs, or from which you want to read results after execution.

```Kotlin
session!!.newPipeline().apply {
    // ...
    runModelInference(
        "my-face-detection-model", // model name
        Pipeline.ModelInferenceType.QNN_HTP,
        modelBinary, // model binary loaded to memory buffer
        arrayOf(
            Pipeline.ModelNodeEncoding("node0", tensorInput0),
            Pipeline.ModelNodeEncoding("node1", tensorInput1),
        ), // tensors input to nodes in the model
        arrayOf(
            Pipeline.ModelNodeEncoding("node556", tensorOutput)
        ), // tensor output extracted from the nodes in the model
    )
    // ...
}
```

### Step 4: Submit the Pipeline for execution
Refer to the following code to call the `Pipeline.submit()` function to submit the Pipeline for execution. The `Pipeline.submit()` function returns a `Task` object, which represents the task for this submission of the `Pipeline` object.
When submitting, you can specify the following parameters:

* `parameters`: a Kotlin `Map` used to map placeholders to global tensors. When the `Pipeline` executes, it will replace the corresponding placeholders with the global tensors specified in this `Map`.
* `condition`: an optional global tensor used as an execution condition. If this parameter points to a global tensor with a value of zero, the `Pipeline` will be skipped. In all other cases (for example, the parameter is `null` or the tensor value is nonzero), the `Pipeline` will execute normally.
* `waitFor`: specifies a preceding `Task` object. The current `Pipeline` will wait until the specified `Task` has completed before starting execution. Setting `waitFor` to a previous submission of the same pipeline will be ignored, because multiple submissions of the same pipeline are not executed concurrently and will always be executed sequentially in the order submitted. This is designed to avoid contention within the pipeline.

```Kotlin
val pipeline = session!!.newPipeline()
// ...
val task = pipeline.submit(
    mapOf(
        placeholder1 to globalTensor1,
        placeholder2 to globalTensor2,
        // ...
    ),
    condition = globalTensor0, 
    waitFor = preTask,
)
```

After you submit the Pipeline, SpatialML will allocate a thread from its internal thread pool to execute the operations you have defined. In the following cases, the Pipeline you submit may be delayed in execution:

* **Resource conflict**: The Global Tensor that the Pipeline needs to write to is currently occupied by another running Pipeline.
* **Task dependency**: You specified a prerequisite task to wait for at submission, but that task has not yet completed.
* **Instance conflict**: The previous instance of this Pipeline is still running.
* **Thread pool is full**: There are no available idle threads in the SpatialML thread pool.

### Step 5: Render the algorithm output-driven scene to the SpatialML spatial container
Refer to the following steps to render the algorithm output-driven scene to the SpatialML spatial container. For more details on rendering operations, see [Other rendering operations supported by the SpatialML spatial container](/sdk/en_get-started-with-spatialml_111).
* The space state of your application cannot be Full Space. If the space state of your application is Full Space, the SpatialML spatial container will be hidden and cannot be displayed simultaneously with the application's Stage container.
* When creating a SpatialML Session, you must specify a width and height greater than 0 for its spatial container. Otherwise, SpatialML will not create a spatial container for that Session.


1. Load the scene into the SpatialML spatial container.
   SceneGraph Tensor is a special type of Structured Tensor used to represent the complete scene in a SpatialML container. You can create a SceneGraph Tensor in the following two ways:
   * Create from .glTF files.
   * Directly created from the application's memory buffer.
   The following code example demonstrates how to call the `newSceneFromGLTFSuspend()` function in a SpatialML Session to create a SceneGraph Tensor from a .glTF file.
   Each SpatialML container corresponds one-to-one with a SpatialML Session, so the SceneGraph Tensor can only be created as a Global Tensor. In the Pipeline, you must reference this global SceneGraph Tensor through a Placeholder in order to operate on the scene.

   ```Kotlin
   fun CoroutineScope.initializeDemoFramework(appContext: Context) = async {
       val session = ...
       val sceneGraph = session.newSceneFromGLTFSuspend("SpatialML/tv.gltf")
   }
   ```

2. Create and submit a Pipeline for execution, and simultaneously call the `updateSceneGraphProperty()` function to set the scale and visibility of the SceneGraph Tensor.
   In the code example, a `3x1` `FLOAT32` multidimensional tensor (3-element column vector) is created for the scene's scale parameter, but only a single `Float` value `0.03` is provided to initialize it.
   This is because SpatialML uses a broadcasting mechanism similar to Numpy: when the target tensor's size is an integer multiple of the Input `Buffer` size, the system automatically repeats the data in the `Buffer` to fill the entire tensor. Therefore, although the tensor in this example requires three `FLOAT32` values, the system will copy the single input value `0.03` three times, and finally update the tensor to `[0.03, 0.03, 0.03]`.
   ```Kotlin
   val initTask =
       session.newPipeline().run {
           val sceneGraphPlaceholder = newPlaceholderLike(sceneGraph)
   
           // scale the entire scene graph to 0.03 along all 3 dimensions
           updateSceneGraphProperty(
               sceneGraphPlaceholder,
               "/", // scenegraph's root node -> scaling the entire scenegraph
               Transform.Scale,
               newLocalTensor(MultiDimensionalInitInfo(DataType.FLOAT32, intArrayOf(3, 1))).apply {
                   SharedMemory.create("3x1_scalar_static", Float.SIZE_BYTES).use { mem ->
                       val buf = mem.mapReadWrite()
                       buf.order(ByteOrder.nativeOrder())
                       buf.putFloat(0.03)
                       SharedMemory.unmap(buf)
                       tensorResource = mem
                   }
               },
           )
           
           // switch the scenegraph visibility to TRUE (1)
           switchSceneVisibility(sceneGraphPlaceholder, newLocalTensor(1))
           submit(mapOf(sceneGraphPlaceholder to sceneGraph), null, null)
       }
   ```


### (Optional) Step 6: Read algorithm output from SpatialML
To render the algorithm output in your application's spatial container or Stage, you do not need to create an associated SpatialML spatial container; you can read the output directly from the SpatialML framework.
You can read algorithm output from SpatialML in the following ways.

* **Read algorithm output by copying:** You can read the current value from any Global Tensor that is not a SceneGraph Tensor, allowing you to use the algorithm output from SpatialML in your application. However, please note that you need to obtain camera or spatial data permissions before you can read the results.
* **Read algorithm output as a dynamic texture and use it in a material:** If you have created a Dynamic-texture tensor, in addition to using this tensor as a material or texture map in the SpatialML spatial container for the scene, you can also load this tensor into your application as a `TextureResource`. This way, you can also use the output from the SpatialML algorithm as a material or texture map in your application's scene.

However, we recommend using the SpatialML spatial container. The SpatialML spatial container offers the following advantages:

* **Better performance, lower latency**: The SpatialML spatial container runs directly within the SpatialML runtime framework, which effectively reduces rendering latency, improves efficiency, and avoids memory overhead caused by data transfer between the application and the SpatialML framework.
* **Rendering without specific permissions**: If your application does not obtain user authorization for camera or spatial data, it will not be able to read the output data of the algorithm. However, because the SpatialML spatial container runs in isolation within the SpatialML framework, you can still use it to present MR effects to users.
* **Use spatial localization data in Shared space**: The SpatialML spatial container allows you to render MR effects that require spatial localization data capabilities without setting the application to Full space mode.
   Spatial positioning data allows you to anchor virtual objects (such as information labels) at specific positions in the real world. For example, you can deploy a food recognition algorithm and use spatial positioning data to anchor food names and calorie labels to the recognized food. Typically, only Stage applications running in Full space mode can use spatial positioning data. The SpatialML spatial container does not have this limitation, allowing you to display MR content that needs to be anchored at precise positions through the container while enjoying the multi-window experience of Shared space.


## Other rendering operations supported by the SpatialML spatial container
### **Update the scene in the SpatialML container based on computation results**
In the Pipeline, you can use Tensor data to update the following properties of the scene:

* The position, rotation, scale, and transformation matrix of an entity relative to its parent object.
* The anchor of an entity in the headset world coordinate system.
* The material properties of an entity, such as color, normal, metallicity, and roughness.

For example, the following code creates a Pipeline. Each time it runs, the `helmet` entity in the scene moves upward along the Y axis by 0.03 meters.
```Kotlin
val moveUpPipeline = session.newPipeline().apply {
    val sceneGraphLocal = newPlaceholderLike(sceneGraph)

    val position = newLocalTensor(
        Tensor.MultiDimensionalInitInfo(Tensor.DataType.FLOAT32, intArrayOf(3, 1))
    ).apply {
        SharedMemory.create("3x1_position_init", Float.SIZE_BYTES).use { mem ->
            val buf = mem.mapReadWrite()
            buf.order(ByteOrder.nativeOrder())
            buf.putFloat(0.0f)
            SharedMemory.unmap(buf)
            tensorResource = mem
        }
    }

    val positionDelta = newLocalTensor(
        Tensor.MultiDimensionalInitInfo(Tensor.DataType.FLOAT32, intArrayOf(3, 1))
    ).apply {
        SharedMemory.create("3x1_position_delta", 3 * Float.SIZE_BYTES).use { mem ->
            val buf = mem.mapReadWrite()
            buf.order(ByteOrder.nativeOrder())
            buf.putFloat(0.0f)
            buf.putFloat(0.03f)
            buf.putFloat(0.0f)
            SharedMemory.unmap(buf)
            tensorResource = mem
        }
    }

    // position = poistion + positionDelta
    arithmetic("{0} + {1}", arrayOf(position, positionDelta), position)

    updateSceneGraphProperty(
        sceneGraphLocal,
        "/helmet",
        SceneGraphProperty.Transform.Position,
        position,
    )
}
```

### **Use dynamic textures in the SpatialML container and update texture content in real time**
When creating a multidimensional Global Tensor, you can specify it as a Dynamic-texture tensor. This type of Tensor can be used as a material texture in the scene of the SpatialML container, such as color, normal, metallicity, and roughness textures.
The main advantage of the Dynamic-texture tensor is that when its data changes, the material using this Tensor as a texture will also be automatically updated synchronously.

1. Create a Global Tensor of type Dynamic-texture tensor in the SpatialML Session.
   ```Kotlin
   val dynamicTexture =
       session
           .newGlobalTensor(
               Tensor.MultiDimensionalInitInfo(
                   Tensor.DataType.UINT8,
                   intArrayOf(256, 256),
                   3,
                   dynamicTexture = true,
               )
           )
   ```

2. Create and immediately submit a Pipeline that executes only once, binding the Dynamic-texture tensor to the material of the scene object. Because the Dynamic-texture tensor is global, you need to create a Placeholder for it in the Pipeline first.
   In the code below, line 10 locates the rust child node of the helmet node via the `"/helmet/rust"` path, while line 11 specifies the color map of the PBR material (index 2) of that node as the update target. After this one-time Pipeline (lines 16–19) is submitted and executed, a persistent binding relationship is established. Afterwards, any update to the `dynamicTexture` Tensor will be automatically synchronized to the material map, without needing to call the `updateSceneGraphProperty()` function again.
   ```Kotlin
   val initRun =
       session.newPipeline().run {
           val sceneGraphLocal = newPlaceholderLike(sceneGraph)
           val textureLocal = newPlaceholderLike(dynamicTexture)
           // switch scenegraph to visible (1)
           switchSceneVisibility(sceneGraphLocal, newLocalTensor(1))
   
           updateSceneGraphProperty(
               sceneGraphLocal,
               "/helmet/rust",
               SceneGraphProperty.PBRMaterials[2].BaseColorTexture,
               textureLocal,
           )
           Log.i("SSMRTest", "Pipeline0::Add to pipeline: updateSceneGraphProperty")
           submit(
               mapOf(sceneGraphLocal to sceneGraph, textureLocal to dynamicTexture),
               null,
               null,
           )
       }
   ```


### **Render text in a Spatial ML container**
You can render data from any non-SceneGraph Tensor as text and display it in the scene of a SpatialML spatial container. The following code shows how to implement this:
```Kotlin
session.newPipeline().run {
    val sceneGraphLocal = newPlaceholderLike(sceneGraph)
    val text = newLocalTensor(...)
    
    // text color to be BLUE
    updateSceneGraphProperty(
        sceneGraphLocal,
        "/textbox",
        SceneGraphProperty.Text.Color,
        newLocalTensor(Color.valueOf(Color.BLUE)),
    )
    
    // text alignment to be center
    updateSceneGraphTextVerticalAlignment(
        sceneGraphLocal,
        "/textbox",
        Pipeline.TextVerticalAlignment.CENTER,
    )
    updateSceneGraphTextHorizontalAlignment(
        sceneGraphLocal,
        "/textbox",
        Pipeline.TextHorizontalAlignment.CENTER,
    )
    
    // set text content
    updateSceneGraphProperty(
        sceneGraphLocal,
        "/textbox",
        SceneGraphProperty.Text.Content,
        text,
    )
}
```

The contents of the Tensor will be rendered as text in the following manner:

* If the data type of a SCALAR_ARRAY Tensor is `UINT8` or `INT8`, SpatialML will parse its contents as a UTF-8 encoded string. Therefore, when you execute the following Pipeline, you will see `Hello World`.
   ```Kotlin
   val helloWorldBytes = "Hello World".toByteArray(Charsets.UTF_8)
   
   val text = newLocalTensor(ScalarInitInfo(DataType.UINT8, helloWorldBytes.size)).apply {
       SharedMemory.create("hello_world_buffer_mem", helloWorldBytes.size).use { mem ->
           val buf = mem.mapReadWrite()
           buf.order(ByteOrder.nativeOrder())
           buf.put(helloWorldBytes)
           SharedMemory.unmap(buf)
           tensorResource = mem
       }
   }
   ```

* For all other types of Tensors, their internal data will be rendered one by one as numeric strings. For example, when executing the following Pipeline, although the Tensor data also comes from the UTF-8 encoding of `Hello World` as in the previous example, due to the different data type, you will see a sequence of numbers representing the value of each byte, rather than a text string: `72 101 108 108 111 32 119 111 114 108 100`.
   ```Kotlin
   val helloWorldBytes = "Hello World".toByteArray(Charsets.UTF_8)
   // datatype: UINT8 -> INT32
   val text = newLocalTensor(ScalarInitInfo(DataType.INT32, helloWorldBytes.size)).apply {
       SharedMemory.create("hello_world_buffer_mem", helloWorldBytes.size * Int.SIZE_BYTES).use { mem ->
           val buf = mem.mapReadWrite()
           buf.order(ByteOrder.nativeOrder())
           for (charIdx in 0..<helloWorldBytes.size) {
               buf.putInt(helloWorldBytes[charIdx].toInt())
           }
           buf.put(helloWorldBytes)
           SharedMemory.unmap(buf)
           tensorResource = mem
       }
   }
   ```


## API reference
For details about the following SpatialML-related packages, refer to the [API reference](https://developer.picoxr.com/spatial-api/index.html).

* `com.pico.spatial.ml.securemr` package 
* `com.pico.spatial.ml.readback` package


