In spatial apps, performance is critical to user experience. A high-quality spatial app can respond quickly to user interactions and display various types of content smoothly.
## Key metrics
Typically, a spatial app's performance can be measured with the following key metrics:

* **Startup and loading times**
   This includes the cold start time of the spatial app and the time required to load its content. Accelerating the loading speed can reduce users' wait times.
* **Interaction smoothness**
   When users interact with the UI or objects in a spatial app, they should be able to receive real-time feedback. For example, after clicking a button, a popup should appear immediately, or when picking up a virtual object by hand, the object should closely follow the movement of the user's hand.
* **Rendering smoothness**
   Unlike traditional apps, spatial apps have much stricter requirements for rendering smoothness. Dropped frames and stuttering can severely affect the user experience and may even cause discomfort.
* **Memory usage**
   A spatial app can run in Shared Space alongside other spatial apps. Minimizing memory usage as much as possible not only improves the app's own operating efficiency but also enhances the user experience when multitasking.
* **Battery consumption**
   Complex tasks result in faster power consumption and faster device heating, which reduces the device's battery life and may further lead to system-imposed restrictions on apps. To prevent a negative impact on user experience, spatial apps should make more efficient use of limited device resources, such as reducing the number of threads and unnecessary background tasks.

## Common factors affecting performance
### 2D UI
In spatial apps, 2D UI is one of the important components. Although Compose UI itself delivers good performance, it is still necessary to follow its best practices to avoid common issues and enhance the user experience. For more information, refer to [Jetpack Compose Performance](https://developer.android.com/develop/ui/compose/performance).
### Model complexity
Complex models increase rendering load and consume more memory, which can affect rendering smoothness. The complexity of a model is typically measured by its number of faces, number and resolution of textures, number and type of materials, and number of bones.
### ECS complexity
In ECS, the complexity of entity and system both affects performance.

* **System**: To maintain a frame rate of 90 fps, the computation for each frame must be completed within 11 ms. In a spatial app, each frame's computation includes not only the app-defined system, but also your Compose UI recomposition, animation, and physics simulation logic. This requires that the operations within the system be performed efficiently.
* **Entity**: The number of entities not only affects memory usage, but also increases the time required for certain operations, such as querying entities in a system. If your app creates a large number of entities, the app's smoothness may be affected.

### Physics simulation
If physics-related components are added to an entity, physics simulation will continuously run, calculating each entity's collisions, forces, speed, and other quantities every frame. This process determines the new position and pose of each entity, making the scene more realistic. However, as the complexity of forces increases or the number of entities involved in collision calculations grows, the physics simulation computation for each frame becomes more complex and takes longer to complete.
### Dynamic light sources and shadows
Dynamic light sources can add real-time lighting and shadow effects to scenes and models, making scenes more realistic. However, dynamic lighting calculations are highly demanding on performance. As the number of light sources increases and more objects are affected by lighting, the computational cost also rises.
## Performance troubleshooting tool
### Android Studio Profiler
Android Studio Profiler provides several out-of-the-box performance analysis tools, including:

* **System Trace**
   Capture trace records and analyze operational status.
* **Analyze Memory Usage**
   Dump heap memory. Analyze memory usage.
* **Find CPU Hotspots**
   Use Callstack Sample or Method Recording to record the time spent by each method on the CPU, thereby identifying performance bottlenecks.
* **Track Memory Consumption**
   Track the creation and reclamation of Java/native object, and analyze memory usage.
* **View Live Telemetry**
   Display real-time CPU and memory usage.

For more information on using Android Studio Profiler, refer to its [official documentation](https://developer.android.com/studio/profile).
### Perfetto
Perfetto is an open-source trace recording and analysis tool that can be used to record, view, and analyze trace records for systems and apps. With Perfetto, you can understand and analyze the runtime records of spatial apps. In addition, Perfetto can also be used together with System Trace.
### Spatial app's performance data
Spatial apps have features and content that distinguish them from traditional apps, such as spatial containers, 3D content, ECS, and many others. To this end, PICO provides a series of trace data to help analyze the operational status and usage of this unique content. For more information, refer to "[Retrieve and analyze trace records](/en_retrieve-and-analyze-trace-records)".
## Performance optimization
Performance optimization is a core aspect of spatial app development and extends throughout the entire development cycle. It not only ensures that app runs smoothly and reliably in complex scenarios, but also ensures that the app delivers a higher-quality and more immersive experience for users. For the process for optimizing spatial apps' performance, as well as methods for troubleshooting and resolving common issues, refer to "[Performance optimization](/en_app-performance-optimization)".

