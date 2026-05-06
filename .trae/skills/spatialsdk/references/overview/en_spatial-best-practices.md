This article introduces the best practices for SpatialML.
### Balancing user privacy and MR creativity
To protect user privacy while implementing MR creativity, we recommend following these guidelines:

* Prioritize using SpatialML spatial containers to render MR effects. This is the recommended approach that best protects user privacy.
* If SpatialML spatial containers cannot meet your rendering requirements, you may choose not to create an associated SpatialML spatial container, and instead read model inference results directly from the SpatialML framework, then render them in the application's spatial container or Stage. However, you must request camera and spatial data permissions from the user before creating a SpatialML Session. At this point, the application needs to explain to the user the specific reasons for requiring this data.
* When the user denies the application's permission request, the application should use the SpatialML spatial container as an alternative. At the same time, the application needs to inform the user that this will result in certain effects or features being limited.

### Asynchronously calling SpatialML with Kotlin coroutines
Calling certain SpatialML APIs may be time-consuming, especially in the following scenarios:

* **Assigning values to tensors or reading their results**: because this requires data transfer between your application and the SpatialML framework.
* **Creating scenegraph tensors**: because this involves I/O operations and loading rendering resources.

To avoid blocking your application, we recommend calling these time-consuming SpatialML APIs asynchronously within coroutines.
### Selecting the appropriate tensor type
To achieve optimal performance, we recommend prioritizing the use of multidimensional tensors in most cases.
This recommendation is based on performance considerations. Because tensors are strongly typed, their type cannot be changed once created. If you need to convert types, you can only copy one tensor to another tensor of a different type by assignment, which incurs additional performance overhead. Multidimensional tensors are suitable for most scenarios, and using them as the default choice can effectively avoid unnecessary type conversions.
However, in the following specific scenarios, you must use other types of tensors:

* **Camera timestamp**: You must use the `timestamp tensor` created by `TimeStampInitInfo`. It contains a 128-bit timestamp, consisting of four 32-bit signed integers (INT32), which represent the high 32 bits of seconds, the low 32 bits of seconds, the high 32 bits of nanoseconds, and the low 32 bits of nanoseconds, respectively.
* **Scene to be rendered**: For scenes that need to be rendered in a SpatialML container, you must use a scene tensor (Scenegraph tensor). For details, refer to [Step 5: Render the algorithm output-driven scene to the SpatialML spatial container](/sdk/en_get-started-with-spatialml_111).
* **Slicing and assignment operations on tensors**: For details, refer to [Perform slicing and assignment operations in Pipeline](/sdk/en_get-started-with-spatialml_111).
* **String**: To correctly display characters, the tensor must be declared as a scalar array of type `UINT8` or `INT8` to store the UTF-8 encoding of the string. For details, refer to [Render text in a Spatial ML container](/sdk/en_get-started-with-spatialml_111).

### Correct use of the tensor channel parameter
We recommend treating the channel of a tensor as part of its data type, rather than as an additional dimension. This design is consistent with the multi-channel `cv::Mat` in OpenCV, which makes it easier to port existing algorithms. At the same time, it is also similar to the way graphics APIs such as OpenGL or Vulkan define image formats.
For example, the following two tensors are identical in memory usage and data layout, but their type definitions differ.

* A tensor with dimensions `512x486`, type `UINT8`, and 3 channels is similar to an `R8G8B8_UNORM` format `Image2D` object.
* A tensor with dimensions `3x512x486`, type `UINT8`, and 1 channel is similar to an `R8_UNORM` format `Image2DArray` object.

To ensure code clarity and efficiency, we recommend using single-channel tensors in the vast majority of cases. Multi-channel tensors are only needed in the following two scenarios:

* **Representing images**: When a tensor is used to represent RGB or RGBA images, 3 or 4 channels should be used, respectively.
* **For slice indexing**: When a tensor is used as a slice tensor, 2 or 3 channels must be used.

Therefore, when you create a tensor and specify the `channel` parameter, the value should typically be 1, 2, 3, or 4. If you need to use other values, it is recommended to treat them as a new dimension of the tensor, rather than as a channel.
### Slicing and assignment operations
Slicing and assignment operations apply to all non-scene tensors. However, when using, you need to pay attention to the following points:

* Slicing very large tensors may consume a significant amount of memory.
* When performing slice assignment between tensors of different data types, the system will perform type conversion rather than a direct memory copy.
* To use slicing within a Pipeline, you must assign the slice to a local tensor or a placeholder tensor. You can use the `toPipelineTensor()` function to create a new tensor from a slice of an existing tensor. Caution: This new tensor (such as `tensorSliced`) is independent of the original tensor (`tensor`); modifying one will not affect the other.
   ```Kotlin
   val tensorSliced = tensor[0..5, 100..200]toPipelineTensor(...)
   ```


### Split long Pipelines to improve concurrency performance
Overly long Pipelines may affect concurrency performance. SpatialML executes multiple submissions from the same Pipeline sequentially to avoid conflicts with local tensors, while submissions from different Pipelines can be executed in parallel (unless you explicitly specify dependencies).
Therefore, for operations that can be processed in parallel, we recommend the following:

1. Split these operations into multiple independent Pipelines.
2. Use global tensors to transfer shared data between Pipelines.
3. Change the original local tensors within the Pipeline to placeholder tensors so that they can receive shared global tensors at runtime.

### Control the submission frequency of Pipelines
SpatialML executes submitted Pipelines through an internal task queue and thread pool. However, if you submit Pipelines too frequently, it may exceed the processing capacity of SpatialML. We recommend controlling the submission frequency in the following ways:

* **Split Pipelines**: Separate scene updates from algorithm execution operations. You can submit Pipelines used only for scene updates at a high frequency, but you should reduce the submission frequency of Pipelines that run algorithms (especially those using camera or spatial data).
* **Avoid frequent result reads**: Reading values from a tensor may block the execution of subsequent Pipelines that update that tensor. Therefore, you need to avoid frequently reading computation results from the SpatialML framework.
* **Perform one-time operations using Pipeline**: Many operations only need to be performed once in the initialized Pipeline, for example:
   * Use `switchSceneVisibility` to toggle the visibility of the scene tensor.
   * When using `Dynamic-texture tensor`, call the `updateSceneGraphProperty()` function once to replace the map of the target material with this tensor. After that, only update the value of the tensor without calling `updateSceneGraphProperty()` again.

### **Capture logs using Logcat**
To capture internal warnings and errors from SpatialML, use the Logcat tool in Android Studio and filter logs with the `Secure MR::Server` tag.
