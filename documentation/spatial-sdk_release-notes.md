This article records the changes in each version of the PICO Spatial SDK.
## 0.11.x - 2026-04-23
The changes in version 0.11.x are as follows. For how to upgrade your SDK to the latest version, refer to [Upgrade SDK](/sdk/en_upgrade-sdk).
**Added** 

* **SpatialPack:**
   * Added `AudioGroupResource` for managing audio groups, supporting playback in multiple modes: `FORWARD`, `BACKWARD`, and `RANDOM`.
   * Added support for loading `.obj` and `.stl` model file formats.
   * Added `uv` and `materialIndex` properties to `CollisionCastResult` (for raycasting and convex casting) to retrieve texture coordinates and material index at the collision point. This information is available only when the collider is created using a static mesh.
   * Added `toneMappingEnabled` property to `UnlitMaterial` to control whether tone mapping is applied.
* **TrackingPack:**
   * Added controller button events. Use `ControllerAction` and `ControllerActionData` to listen for press/touch states on specific buttons, as well as analog values for trigger and grip inputs.
* **SpatialML:**
   * Introduced `PipelineArithmeticScope`, a structured DSL for expressing tensor operations. This replaces the string-based `arithmetic(expression, ...)` API.
* **SpatialML:**
   * Added `Pipeline.JavaScriptIO`, enabling custom preprocessing and postprocessing operators for ML pipelines using JavaScript.
   * Added audio I/O support, enabling pipelines to capture microphone input and output synthesized audio directly.


**Changed** 

* **Upgraded the build toolchain:**
   * **Kotlin**: Upgraded to **2.0** with the **K2** compiler enabled by default.
   * **Compose Compiler**: Now bundled as part of the Kotlin plugin. Please follow the [official migration guide](https://kotlinlang.org/docs/compose-compiler-migration-guide.html) to update your configuration.
   * **Android Gradle Plugin (AGP)**: The recommended baseline version is now **8.8.0**. While Kotlin 2.0 officially requires a minimum of AGP 8.5, build validation has shown compatibility issues with lower versions during release builds.
* **SpatialUI:**
   * Refactored the color system. The previous `accent`/`onAccent` + `Vibrant` model has been replaced with a semantic `ColorScheme` / `*Colors` system (e.g., `fillPrimary`, `labelPrimary`). Component colors are now driven by semantic roles. Custom themes must be migrated to the new system.
   * Moved `HorizontalPlacement` and `VerticalPlacement` (previously under `Menu`) to a new package path: `com.pico.spatial.ui.design.menu` → `com.pico.spatial.ui.design.windows.popup`. Update your import statements accordingly.
* **SpatialPack:**
   * `ObjectAudioComponent` now exposes a `soundRadiusLevel` property to control the perceived radius of a sound source.
   * `AudioResourceConfig` now supports `randomStart` and `loopEnable` flags for finer-grained control over audio playback behavior.
   * When loading a `MeshResource` fails, only `ResourceLoadingException` is now thrown, simplifying exception handling.


**Deprecated** 

* **SpatialML:**
   * The string-based `Pipeline.arithmetic(expression: String, ...)` method is deprecated. Migrate to the new `Pipeline.arithmetic(result: Tensor, operations: PipelineArithmeticScope.() -> ...)` overload, which provides improved type safety and a more expressive DSL.


**Removed** 

* **SpatialUI:**
   * Removed all `*Vibrants` data classes (e.g., `ButtonVibrants`, `SliderVibrants`, `ChipVibrants`) and their associated `*Defaults` methods. Removed all `vibrant*` parameters and related overloads from components such as `IconKt`, `TextKt`, and `ButtonKt`. Removed the `ColorStyle` class, which previously coupled color and visual effect behavior. The `Vibrant` enum has also been simplified — `LightenHover` and `Termination` variants are removed. All color and vibrant capabilities have been consolidated into the `ColorScheme` / `*Colors` system and must be remapped to the appropriate semantic roles.
* **SpatialPack:**
   * Removed `setHardwareBuffer` from `VideoMaterial` and removed `getTransformMode` and `setCropRect` from `VideoComponent`. Spatial video playback via `VideoComponent` now uses the `SurfaceRenderTexture` pipeline; `setHardwareBuffer` is no longer supported. Cropping behavior is now built-in, so `setCropRect` calls can be removed.


**Fixed** 

* **SpatialPack:**
   * Fixed an error where loading a model via `ContentResolver` + `Uri` (e.g., from a SAF file picker) would incorrectly report "format not supported".
   * Fixed a rendering issue that occurred when multiple `Attachment` instances had a parent-child relationship.
* **SpatialUI:**
   * Fixed the blend mode for `Checkbox` to ensure it renders correctly.
   * Fixed a jitter issue that could occur when moving the cursor in text input components such as `TextField`.


---


## 0.10.x - 2026-03-02
The first official release of the PICO Spatial SDK is now available for devices running PICO OS 6. This release enables developers to build spatial applications with the following capabilities:

* **3D Scene / Entity / Asset System**: Core 3D functionality including asset loading, scene composition, animation playback, physics simulation, and collision detection, forming the foundation for spatial experiences.
* **Spatial UI Framework**: Window management, 2D UI layout and component library, and foundational spatial gestures, bridging 2D content into 3D scenes.
* **Spatial Audio & Video**: Spatial audio rendering and spatial video playback to reproduce stereoscopic soundscapes and visuals for immersive in-app experiences.
* **Environment Awareness**: System-level environmental mesh scanning that enables applications to perceive and interact with real-world spatial information.
* **SpatialML**: An ML inference runtime framework designed for mixed reality scenarios. Supports running custom models trained with mainstream frameworks directly on PICO devices, enhanced with camera and spatial positioning data for improved scene understanding.
* **Developer Toolchain & Sample Projects**: Companion tools including Spatial Editor, Spatial Plugin, and PICO Emulator, along with template and sample projects to accelerate development and debugging of spatial applications.
