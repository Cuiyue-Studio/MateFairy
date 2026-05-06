This article introduces the basic concepts of SpatialML.
## SpatialML Session
A SpatialML Session is the runtime context and entry point for SpatialML. You can create multiple Sessions. By default, data between Sessions is strictly isolated. Data can only be shared across Sessions when your application obtains the necessary authorization and actively transfers data from one Session to another. SpatialML does not proactively share data between Sessions.
## **SpatialML spatial container**
You can create an associated SpatialML spatial container in each SpatialML Session to render MR content. Currently, the SpatialML spatial container only supports the Volumetric form:

* **Volumetric**: Consistent with the application's own Volumetric container, it is a finite volume container with explicit three-dimensional boundaries.

You can use the rendering API provided by SpatialML to directly render and update MR scenes in the SpatialML spatial container based on algorithm results. To ensure user privacy, the SpatialML spatial container is completely isolated from the application's own spatial container, and scenes in both cannot interact or affect each other. In addition, if your application's space state is Full Space, the SpatialML spatial container will be hidden and cannot be displayed simultaneously with the application's Stage container.
## SpatialML Pipeline
A SpatialML Pipeline is a schedulable execution unit within a SpatialML Session (similar to a `Callable` object in Python or a `Runnable` object in Java/Kotlin). Within a SpatialML Pipeline, you can orchestrate a series of operations, including running algorithm packages, obtaining binocular or depth camera data, executing JavaScript scripts, or updating the rendering of the SpatialML spatial container.
A SpatialML Session can contain multiple SpatialML Pipelines. SpatialML has a built-in thread pool that supports serial or parallel scheduling. Multiple Pipelines within a SpatialML Session can share data. SpatialML automatically analyzes data dependencies and dynamically adjusts the execution order, effectively preventing race conditions during parallel execution.
## SpatialML Tensor
SpatialML abstracts all data that is read or written within the framework into Tensors. In SpatialML, a Tensor is classified as either a Multi-dimensional Tensor or a Structured Tensor.
### Multi-dimensional Tensor
This is the most fundamental data form, following the definition of tensors in physics and mathematics. To clearly distinguish between row vectors and column vectors in linear algebra operations, SpatialML requires that Tensors have at least two dimensions. To define a one-dimensional vector, declare it as a matrix in the form of `1xN` or `Nx1`.
### Structured Tensor
To accommodate MR use cases, SpatialML extends the definition of tensor and introduces the structured tensor. This type of tensor has specific semantic constraints on its data layout, such as:

* **POINT2 / POINT3**: Data is arranged in groups. `POINT2`: Each group of two data represents (X, Y); `POINT3`: Each group of three data represents (X, Y, Z).
* **COLOR**: Data is grouped by RGB (three values) or RGBA (four values), each representing a color channel component.

## Scope of SpatialML tensor
According to the data scope, tensors can be classified as global tensors, local tensors, and placeholders.
### Global Tensor
Used to share data between different pipelines within the same session.
### Local Tensor
Used only within a pipeline, serving as the input or output for specific operations.
### **Placeholder**
A special type of local tensor, similar to the concept of a "reference" in programming languages, which can act as a bridge between global tensors and local tensors. When a pipeline is submitted for execution, you can map a placeholder to a specific global tensor. During pipeline execution:

* If an operation in the pipeline uses this placeholder as input, it reads data from the mapped global tensor.
* If an operation in the pipeline uses this placeholder as output, it writes the result to the mapped global tensor.

Therefore, by binding different global tensors for each execution, you can reuse the same pipeline to accomplish different tasks.

