Performance optimization is an important part of spatial app development and runs throughout the entire development cycle. It not only determines the user's sense of immersion and the smoothness of interactions, but is also directly related to device battery life, scalability, and other aspects. Only through reasonable performance optimization can apps maintain high frame rates and low latency, while also balancing a smooth experience, power consumption efficiency, and scalability, thereby enhancing the user experience.
## Performance optimization workflow
Performance optimization is usually divided into the following steps:

1. **Check performance issues**: Use various performance debugging tools or other methods to identify and analyze performance issues in the app to find bottlenecks.
2. **Optimize performance**: Make modifications to address identified performance bottlenecks and optimize performance.
3. **Continuously monitor performance**: Establish baseline metrics for areas that have a clear impact on user experience and for identified performance issues, and continuously monitor them throughout the development process and production environment.

### Check performance issues
There are several ways to discover performance issues. New performance issues can be discovered through manual analysis. Changes in existing performance metrics can also be detected through performance benchmark testing. Additionally, performance-related feedback can be collected through testing, user feedback, and other channels.
| **Method** | **Description** |
| --- | --- |
| Manual analysis | Spatial apps can be analyzed using tools such as Android Studio Profiler and Prefetto, and analysis can be performed from multiple perspectives: <br>  <br> * By recording a System Trace, a more detailed Callstack Sample, or Method Recording, the program's execution state during periods of lag can be analyzed to identify the corresponding bottlenecks. <br> * Analyze the app's memory usage and detect memory leaks by using Heap Dump and memory allocation records. <br> * Analyze the app's feature usages via the spatial app performance data provided by PICO in trace records. <br> * You can even use the Trace API provided by Android to add your own trace records, allowing you to analyze the runtime duration and status of important processes in your app. |
| Performance benchmark testing | You can use AndroidX Benchmark to write performance benchmark testings and run them regularly, such as in the CI pipeline, to continuously monitor these performance metrics. For more information about Androidx benchmark, refer to the [official documentation](https://developer.android.com/topic/performance/benchmarking/benchmarking-overview). |
### Optimize performance
After identifying performance bottlenecks, you can optimize them to improve performance. For spatial apps, common and effective optimization methods are as follows:
| **Method** | **Description** |
| --- | --- |
| Asset optimization | Reasonable optimizations, such as reducing scene complexity, compressing maps, and baking lighting, can significantly reduce the cost of rendering scenes with almost no impact on performance. |
| Lazy loading of resources | Do not load all required resources right at initialization, as this will only slow down app startup and increase memory overhead during runtime. |
| Preloading of resources | Preloading the resources needed for the next scene before the user acts can effectively reduce the wait time after the user's operation. Effectively combining lazy loading and preloading can balance runtime resource usage and users' wait time. |
| Asynchronous and multithreaded computing | Move heavy computations from the main thread to other threads to prevent the main thread from being occupied for extended periods, which can cause lag. |
After completing the optimization, you can perform another analysis to verify its effectiveness. It is also important to ensure that optimizing one aspect of performance does not introduce other performance issues.
### Monitor performance continuously
After optimizing performance, you can create a performance testing case for the corresponding issue and include it in regular performance testings to prevent the same issue from occurring again. At the same time, for spatial apps that have already been launched, it is also necessary to continuously pay attention to user feedback and performance issues identified through online monitoring, and to analyze and solve them.
## Troubleshoot common issues
In actual spatial app development, numerous factors often affect the performance of spatial apps. For both identified and potential performance issues, various tools can be used for troubleshooting.
### Slow cold start
Cold start duration, as perceived by users, refers to the time from clicking the app icon to when the app content is fully displayed and the app is ready for use. For spatial apps, during the cold start, in addition to the 2D view layout and rendering, and core module initialization that conventional apps perform, it is also common to load or create 3D assets.
For the cold start of spatial apps, in addition to the considerations for traditional apps, the following should also be noted:

* At startup, only load the assets required by the default container to avoid unnecessary asset loading.
* Optimize assets, such as compressing textures, reducing model polygon count, and more, to decrease asset loading time.
* If your app needs to display a complex scene after launch, consider showing a splash screen before loading is complete instead of leaving the screen blank or black.

When you begin troubleshooting cold start issues, you can use System Trace in Android Studio Profiler or Perfetto to capture trace records during the app startup phase and analyze them. In addition to checking the regular cold startup duration for Android apps, you need to also pay attention to whether there is slice information related to asset loading during the cold start phase.
For example, the following code will cause stuttering at runtime because AssetBundle and Entity are loaded on the main thread:
```Kotlin
@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SpatialView { content, _ ->
            val entity =
                try {
                    Entity.load(modelName = "Hi", bundle = AssetBundle.load("asset://hi.bundle"))
                } catch (e: ResourceLoadingException) {
                    null
                }
            entity?.let {
                content.addEntity(it)
            }
        }
    }
}
```

The following will appear in Trace:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b234364592ca47418acd5c053c662f20~tplv-goo7wpa0wc-image.image" width="2142px" /></div>

After analyzing the trace records, if you find that other slices have unexpected time consumption, you can use the Find CPU Hotspots feature in Android Studio Profiler to record and analyze methods with long execution times during the cold start phase, and then perform targeted optimization:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9102540ecb3b455ea6dcddc46abb5066~tplv-goo7wpa0wc-image.image" width="4962px" /></div>

Trace information related to asset loading:
| **Trace name** | **Description** |
| --- | --- |
| LoadEntity: {name} | Create an entity object from a path in the UI thread. |
| LoadEntity_Asset: {name} | Create an entity object from a bundle in the UI thread. |
| LoadAsset: {name} | Create an AssetBundle object in the UI thread. |
| LoadMesh: {name} | Create a MeshResource object from a Path in the UI thread. |
| LoadTexture: {name} | Create a TextureResource object from a Path in the UI thread. |
### Slow responses and ANR
When an app occupies the main thread for an extended period, it can cause lag or even result in an ANR. In general, delays exceeding 100 ms are clearly noticeable to users, while delays exceeding 5 seconds will trigger ANR.
For spatial apps, it is important to avoid long blocking operations in the `initial` and `update` blocks of `System.update` and `SpatialView`, as these methods run on the main thread. Pay attention to the following:

* Avoid synchronously loading 3D assets in the `initial` and `update` blocks of `System.update` and `SpatialView`, as loading 3D assets often takes tens of milliseconds or more.
* In `SpatialView`, if possible, use a `remember` block to obtain the required entity object in advance, rather than searching for it during each update.
* In `System.update`, use `EntityQueryCondition.hasComponent()` to find the required entity object.
* Reduce the number of object allocations in the `update` block of `System.update` and `SpatialView`. Because these two methods are executed frequently, allocating objects within them can significantly increase memory pressure and the frequency of garbage collection.

When troubleshooting stuttering issues, you can use the System Trace feature of Android Studio Profiler or Perfetto to capture trace records during the app's runtime and analyze them. In the trace records, you can find the slice of each system on the main thread:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/acdb67a19d064c2dad796badf933a484~tplv-goo7wpa0wc-image.image" width="3810px" /></div>

Each registered system leaves a slice named "System_update: {name}" in the trace log at each runtime. Here, the name of system will be the system class name at runtime. In other words, if the system class name is obfuscated, the displayed name will also be the obfuscated name.

For the `initial` and `update` blocks of SpatialView, you can use the Find CPU Hotspots feature in Android Studio Profiler. By searching for "SpatialView" within it, you can find the execution status of SpatialView during recomposition.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/111d7533d1ab493a9a18e87a738f860d~tplv-goo7wpa0wc-image.image" width="4960px" /></div>

Alternatively, you can use the [Composition tracing](https://developer.android.com/develop/ui/compose/tooling/tracing) tool provided by Compose UI to troubleshoot lag caused by SpatialView recomposition.
### Rendering frame drops
If the 3D scene in a spatial app is too complex, it can result in significant rendering load and may even cause frame drops. In this case, users will experience stuttering across the entire display, and the display becomes choppy when turning head or moving. For the complexity of 3D scenes, refer to "[Scene complexity](/en_scene-complexity)".
When troubleshooting rendering frame drop issues, you can use System Trace in Android Studio Profiler or Perfetto to capture trace records during app's runtime. Open the trace records in [Perfetto UI](https://ui.perfetto.dev/) and analyze them. In trace records, you can find the usage of 3D resources and features during runtime. You can pay particular attention to resource-intensive features such as lighting, physics, particles, and PBR material.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/997286ee339c45ca877aad19a905939e~tplv-goo7wpa0wc-image.image" width="3022px" /></div>

The Profiler does not display counter information from trace records; this information must be viewed in Perfetto UI.

The features and trace information that require special attention here are:

* **Physics simulation**
   Using physical components will leavecounter information in the trace records, including:
   | **Trace name** | **Description** |
   | --- | --- |
   | collisionComponentCount | The number of currently loaded `CollisionComponent` instances. |
   | rigidBodyComponentCount | The number of currently loaded `RigidBodyComponent` instances. |
   | physicsVelocityComponentCount | The number of currently loaded `PhysicsVelocityComponent` instances. |
   | physicsForceComponentCount | The number of currently loaded `PhysicsForceComponent` instances. |
   | physicsWorldComponentCount | The number of currently loaded `PhysicsWorldComponent` instances. |
   After physics properties have been added to a large number of objects, CPU computational overhead will increase significantly. In this case, you can consider the following methods to reduce overhead:
   * Reduce unnecessary objects in physics simulation.
   * Set appropriate collision mode and collision group for the collider.
   * Set an appropriate continuous collision detection mode for physical rigid bodies to balance accuracy and performance.
   * Add `PhysicsWorldComponent`:
      * Divide the non-interacting parts of the environment into separate physical worlds.
      * Within a reasonable range, reduce the number of iterations and increase the interval for physics update.
* **Dynamic lighting**
   When dynamic lighting is present in the scene, it significantly increases rendering pressure. In most cases, using baked lighting or IBL can achieve good results without significant performance overhead. If your scene requires dynamic lighting to achieve the desired effect, try to limit the number of light sources.
   All components related to dynamic lighting leave counter information in trace records, including:
   | **Trace name** | **Description** |
   | --- | --- |
   | pointLightComponentCount | The number of currently loaded `PointLightComponent` instances. |
   | spotLightComponentCount | Number of  currently loaded `SpotLightComponent` instances. |
   | directionalLightComponentCount | The number of currently loaded `DirectionalLightComponent` instances. |

To analyze data, such as the number of polygons in the currently rendered models and the number of draw calls, you need to use Perfetto UI to view the trace record of the `com.pico.spatial.runtime` process:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3ef6fcd4b5bc4c9ab1158dc927bc17db~tplv-goo7wpa0wc-image.image" width="3012px" /></div>

Data such as the number of model polygons per frame and the number of draw calls represent the total across all processes currently running in the system. In Shared Space, you can determine the current number of containers by calculating the difference between the number of containers before and after startup.

In simple terms, the lower the number of model polygons and draw calls, the less rendering pressure there is. The recommended number of model polygons and draw calls for the entire scene are as follows:

* **Model polygon count**: No more than 350,000
* **Draw call count**: No more than 80 in Shared Space; no more then 90 in Full Space

When a model has too many polygons, it is necessary to reduce them within a reasonable range and compensate for the visual quality using methods such as normal maps.
When there are too many draw calls, you can merge multiple models to reduce the number of models and thereby lower the number of draw calls, or use `MeshInstance` to display multiple identical objects.
## Trace lists
For trace information unique to spatial apps, refer to "[Retrieve and analyze trace records](/en_retrieve-and-analyze-trace-records)".

