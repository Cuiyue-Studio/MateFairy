# PICO Spatial SDK

## Platform Overview

PICO Spatial SDK is an **Android-based** framework for building spatial apps that blend 2D UI, 3D content, and real-world perception on PICO OS 6 headsets. Apps are built with **Kotlin + Jetpack Compose**, extended with spatial APIs for 3D scenes, tracking, and immersive rendering.

**Key pillars:**

- **ECS (Entity-Component-System):** Data-oriented scene graph for 3D content — entities hold components, systems run per-frame logic.
- **Spatial Containers:** Two container types host all content — `WindowContainer` (bounded 2D/3D panels) and `Stage` (unbounded immersive 3D space).
- **Space States:** `Shared Space` (multitask, planar/volumetric only) vs `Full Space` (single-app immersive, Stage enabled).
- **Spatial UI:** Compose-based component library with spatial extensions (hover, float, 3D layout, drag-and-drop).
- **Perception:** Hand, eye, body, HMD, and controller tracking; plane detection; spatial mesh; world anchors.
- **SpatialML:** ML inference pipeline for MR (NPU-accelerated, QNN backend).

## Architecture — ECS

Entities are nodes with a unique ID and a `ComponentSet`. Components hold data. Systems contain per-frame logic via `update(SceneUpdateContext)`.

- **Hierarchy:** App → Spatial Containers (each owns a Scene) → RootEntity → entity tree → components.
- **System registration:** `registerSystem<T>()` / `unregisterSystem<T>()`; execution order = registration order.
- **Entity creation:** `Entity()` (empty, always has `TransformComponent`) or `Entity.load("asset://...")` (model hierarchy with `ModelComponent` on mesh nodes).
- **Entity queries:** `scene.queryEntity(EntityQueryCondition)` with `hasComponent`, `customCondition`, boolean combinators. Tree traversal via `findEntity(name)`, `getChildren()`.
- **Cloning:** `Entity.clone(CloneOptions)` — `recursive`, `shouldShareMaterialInstance`; runtime state (animation, physics) not copied; custom components need `clone()` override.

## Spatial Containers

### WindowContainer
Bounded volume for 2D Compose UI with optional 3D content. Forms: **Planar** (flat panel, thin Z) and **Volumetric** (box with depth).

- Default placement: ~2.5m ahead, centered on HMD forward.
- Register: `DefaultWindowContainer { }` or `WindowContainer(id, form, ...)` in `mainApp` DSL.
- Open/close: `openWindowContainer(id, tag?, bundle?)` / `closeWindowContainer(...)`.
- Tag semantics: `null` tag = new instance each call; non-null tag = reuse + bring to front.
- Properties: `Form`, `defaultSize` (dp or meters), `resizeType`, `worldScale` (Dynamic/Fixed), caption, material, placement.

### Stage
Unbounded 3D space container. Origin at user's feet, **right-handed** coordinates (+X right, +Y up, +Z toward user).

- Only one Stage open at a time per app.
- Open: `openStage(context, id, style)` (suspend). Close: `closeStage()` (suspend).
- Styles (immutable after open): `Automatic`, `Mixed` (passthrough), `Progressive` (adjustable 0–100), `Full` (fully virtual — needs skybox or scene is black).
- Opening Stage transitions app to Full Space; closing returns to Shared Space.

### Space States
- **Shared Space:** Multitask. Only Planar and Volumetric WindowContainers allowed.
- **Full Space:** Single-app dominance. Stage + optional WindowContainers. Enables tracking, anchors, gesture permissions.

## Scene Construction

To construct a scene for a specific user scenario, you must select the appropriate container and estimate the necessary dimensions.

### Container Selection
Base your selection on the required features and the overall scale of the scene:

* **Planar Window Container:** Best for UI displays or simple 3D model representations. It is limited to a z-depth of **450dp**.
* **Volumetric Window Container:** Suited for more complex 3D content. It supports a maximum size of **2700dp x 2700dp x 2700dp**.
* **Stage:** Occupies the entire space with no theoretical size limitations. If the scenario requires hand tracking or mesh/plane detection, you must use a **Stage** as the base container, as these features only function in full-space mode.

Ensure the target scene aligns with the container's dimensions so that all elements remain visible to the user.


### Size Estimation

* **Container Dimensions:** Sizes are described in **dp**. You can convert these to meters using a base rate of **1250 dp = 1 meter**, though keep in mind this conversion may vary depending on device-specific dynamic scaling.
* **Model Dimensions:** Estimate the size of internal models based on common sense or by using tools like **Three.js** and **PXR** to determine precise measurements.

Once dimensions are established, scale the models to fit the chosen container and position them according to the user's requirements.

## Event System

Subscribe on `Scene` (container-wide) or `SpatialViewContent` (single-view scope) with `subscribe(eventType, ...)` → `Cancellable.cancel()`.

**Built-in event families:**

- **ECS:** `SceneEvents` (EntityAdded, EntityRemoved, Update), `ComponentEvents`, `EntityEvents` (Enable, Disable, Destroy, ParentChanged).
- **Animation:** `AnimationEvents` (Started, Paused, Resumed, Looped, Terminated, Completed).
- **Collision:** `CollisionEvents` (Enter, Update, Exit).
- **Audio:** `AudioEvents` (PlaybackStarted, Paused, Stopped, Completed, SeekCompleted).
- **Anchor:** `AnchorUpdate` events.
- **Timeline:** `TimelinePlayerEvents` (Started, Completed, Terminated, Paused, Resumed).

## Interaction & Gestures

### 3D Entity Interaction
Entities require `InteractableComponent` + `CollisionComponent` (with shapes) for hit testing. Use `targetedToEntity` / `TargetEntity` Compose extensions.

### Spatial Gestures
Compose pointer extensions: `detectSpatialTapGesture`, `detectSpatialDragGesture`, `detectSpatialRotateGesture`, `detectSpatialScaleGesture`, `detectSpatialTransformGesture`. `SpatialModelView` auto-adds interactable/collision.

### Hover Effects
- **2D (Spatial UI):** `Modifier.spatialHoverEffect()` with `SpatialHoverStyle`, groups via `spatialHoverEffectGroup()`.
- **3D (Entity):** `HoverEffectComponent` + `InteractableComponent` + `CollisionComponent`.

### Spatial Modifiers
- **Float:** `Modifier.offset(z = ...)` (dp), `Modifier.zOffset { }` (px).
- **Rotate:** `Modifier.rotate3D(degree, axis)`.
- **Scale:** `Modifier.scale3D(x, y, z, anchor)` with `NormalizedPoint3D`.

### Drag & Drop
Compose `dragAndDropSource` / `dragAndDropTarget`; supports cross-window and world-space drops.

### Sound Effects
`SpatialSoundEffect` enum + `LocalAudioEffectPlayer.current.playSystem()` for interaction feedback.

## Tracking & Perception (Full Space Only)

All tracking providers follow the same pattern: `*TrackingProvider` → `start()` / `stop()`, data via `dataFlow` (Compose) or `latestData` (ECS). Coordinate conversion with `rootEntity.convertPositionFrom`.

| Provider | Data | Notes |
|----------|------|-------|
| `HandTrackingProvider` | 26 joints per hand | No runtime permission needed |
| `EyeTrackingProvider` | `EyePose` (position + rotation) | Requires `com.picovr.permission.EYE_TRACKING` |
| `HMDTrackingProvider` | `HMDPose` | View-aligned HUD use |
| `ControllerTrackingProvider` | Left/right controller poses | Standard 6DoF |
| `BodyTrackingProvider` | 24 body joints | Requires PICO Motion Trackers; mutually exclusive with object tracking |
| `MotionTrackingProvider` | Object tracker 6DoF | Mutually exclusive with body tracking |
| `PlaneTrackingManager` | Plane anchors with semantics | Subscribe `AnchorUpdate` events |
| `MeshTrackingManager` | Scene mesh geometry | `MeshResource.loadFromMeshAnchor` |
| `WorldTrackingManager` | World anchors (persisted) | `createAnchor`, `loadAnchor`; max 1024/app |

## Resources & Rendering

### Resource Types
- **Model:** USD (`.usd/.usda/.usdc/.usdz`) preferred, glTF/GLB supported. Load via `Entity.load(uri)`.
- **AssetBundle:** Spatial Editor scenes(`.bundle`) in APK assets. Retrieve the bundle using `AssetBundle.load("asset://name.bundle")` and instantiate the scene using `Entity.load(String, AssetBundle)` afterward.
- **Mesh:** Primitives via `MeshResource.create*(Plane|Sphere|Box|...)`, OBJ via `MeshResource.load`, anchors via `loadFromPlaneAnchor/MeshAnchor`. GPU instancing with `MeshInstancesResource`.
- **Material:** `UnlitMaterial`, `PhysicallyBasedMaterial` (PBR), `ShaderGraphMaterial` (editor bundles), `VideoMaterial`, `PortalMaterial`. Blending modes: Opaque, Transparent, Add, Fade, Masked.
- **Texture:** `TextureResource.load` / `.create(Bitmap)`. Formats: PNG, JPEG, WebP, KTX, EXR (subset). Limits: 256 MB/surface, max 16k dims (2D).
- **AssetBundle:** Packages Spatial Editor scenes → `.bundle` in APK assets. `AssetBundle.load("asset://name.bundle")`.

### Lighting
- `PointLightComponent` (no shadows, max 256), `DirectionalLightComponent` (shadows, max 128), `SpotLightComponent` (shadows).
- `GroundingShadowComponent` for cheap grounding shadows.
- `ImageBasedLightComponent` + `ImageBasedLightReceiverComponent` for IBL.

### Visual Effects
- **Opacity:** `OpacityControllerComponent(opacity)` — hierarchical multiply with parent and view alpha.
- **Glass:** `Modifier.backgroundMaterial(style)` with `Material.Thin` through `Thickest`.
- **Vibrant:** `Modifier.vibrantEffect(Vibrant.*)` for adaptive contrast.
- **Portal:** `PortalWorldComponent` + `PortalMaterial` + `PortalComponent` for see-through portals with optional entity crossing.
- **Draw order:** `DrawOrderGroupComponent` for transparent entity sorting.

### Coordinate Systems
- **Entity / SpatialView:** Right-handed, meters, origin at view center.
- **Stage:** Right-handed, meters, origin at user's feet (+Z toward user).
- **WindowContainer views:** Left-handed virtual pixels (+Y down) for Compose; right-handed meters for entities.
- Conversion: `convertPositionTo/From`, `convertRotationTo`, `convertScaleTo`, `convertTransformTo`.
- Unit helpers: `PhysicalLengthConverter` (dp ↔ meters), `Modifier.size(1.meters)`.

## Animation System

| Type | API | Notes |
|------|-----|-------|
| Skeletal | `getAnimationResources()` → `playAnimation` | Max 1024 bones (device), 512 (editor); 4 weights/vertex |
| BlendShape | `BlendShapeControllerComponent` | Per-index/name weights, subset grouping |
| Tween | `TweenAnimation.createTweenAnimation` → `AnimationResource.generateWithTweenAnimation` | Targets: position, rotation, scale, transform, material properties |
| Orbit | `OrbitAnimation.createOrbitAnimation` → `AnimationResource.generate` | Circular path around axis |
| Timeline | `entity.playTimeline()` → `TimelinePlayerController` | Editor-authored only |
| Particles | `ParticleComponent` | Editor-authored; runtime control of emission, color |

**Composition:** `AnimationResource.repeat(count)`, `group(list)` (parallel), `sequence(list)` (serial).
**Playback:** `AnimationPlaybackController` — `pause`, `resume`, `stop`, `setSpeed`, `setTime`, `close()`.
**Blending:** `AnimationPlayConfig` — `transitionDuration`, `transitionMode` (Default, Crossfade, Compose, StopAndCrossfade), `blendLayer`, `blendWeight`.

## Physics

- **`RigidBodyComponent`:** Modes — Dynamic (simulated), Kinematic (user-driven), Static (implicit default).
- **`CollisionComponent`:** Shapes (`ShapeResource` box/sphere/capsule/convex/mesh), response modes (Trigger/Collider), collision filter, physics material (friction, restitution).
- **Forces:** `PhysicsForceComponent` (continuous force/torque), `PhysicsVelocityComponent` (one-shot impulse).
- **World:** `PhysicsWorldComponent` on ancestor — `gravity`, `solverIterations`, `simulationClock`. All interacting bodies must share the same world.
- **Raycasting:** `scene.rayCast(origin, dir, maxDistance, queryType, group, referenceEntity)`, `scene.convexCast(...)`.

## Spatial Audio

Three spatialization modes (pick one component per entity):
- **`ChannelAudioComponent`:** Direct multichannel, no spatialization.
- **`AmbientAudioComponent`:** Directional cone, no distance falloff.
- **`ObjectAudioComponent`:** Full 3D with HRTF, distance attenuation (Fixed / InverseSquared), directivity.

**Workflow:** Load `AudioResource` → add component → `prepareAudio` / `playAudio` → `AudioPlayerController` (play, pause, seekTo, setLoop, close). Max 39 simultaneous sources.

**Mix groups:** `AudioMixerGroupResource(name, volume, speed)` → link via `AudioResourceConfig(mixerGroupId)` → `AudioMixerGroupsComponent` for bus-level volume/speed control.

## Video

- **`VideoComponent`:** BYO player — `MeshResource` + `VideoMaterial` + `SurfaceRenderTexture`; you manage MediaPlayer lifecycle.
- **`VideoPlayerComponent`:** Higher-level — pass `CypressMediaPlayer` + mesh + material; handles texture updates.
- **`VideoMaterial`:** `VideoDimensionMode` (Mono, TopAndDown, SideBySide, MultipleView/MV-HEVC), blending (Opaque/Transparent).
- Formats: mp4, mov, mkv, webm; codecs: H.264/265, AV1, VP8/9.

## SpatialML (ML for MR)

Data-driven MR inference pipeline with NPU acceleration (Qualcomm QNN).

- **Session:** Isolated context with camera/sensor access.
- **Pipeline:** Schedulable unit with models, ops, tensors; dependency-aware ordering.
- **Tensors:** Multidimensional preferred; Global / Local / Placeholder lifecycle.
- **Rendering:** SpatialML container (privacy-safe, no camera permission) or readback to app (requires `CAMERA` + `SPATIAL_DATA` permissions).
- **Not supported on PICO Emulator.**

## Spatial UI Component Library

Compose-based UI components wrapped in `PicoTheme`. Key categories:

**Layout & Chrome:** `TitleBar`, `ToolBar` (bottom augment), `TabBar` (top/left navigation on augment), `Augment` (floating content outside window), `Subwindow` (side panel), `Divider`, `ListItem`.

**Overlays:** `Sheet` (modal), `SpatialPopup`, `AlertDialog`, `Menu` / `SubMenu`, `Snack` (toast), `CoachmarkBox`.

**Actions:** `Button`, `IconButton`, `ToggleButton`, `ToggleIconButton`, `Link`, `ButtonChip` / `ToggleableChip` / `RemovableChip`, `SegmentControl`.

**Input:** `TextField`, `SearchField`, `NumberField`, `CheckBox` / `TriStateCheckbox`, `Switch`, `Option`, `Slider` / `SegmentSlider` / `SymbolSlider`, `DatePicker` / `DateRangePicker`, `Timepicker`.

**Display & Feedback:** `Badge` / `DotBadge` / `NumberBadge`, `LinearProgressIndicator`, `CircularProgressIndicator`, `PageControl`, `ScrollIndicator`.

**Theming:** `PicoTheme` provides `colorScheme` (semantic/accent/background/forecolor), `typography` (Display/Headline/Title/Label/Body), state indication (hover/pressed/disabled), and system materials (Regular/Thick/Thickest).

## Spatial Editor & Toolkit

### Development Environment
- **Android Studio 2025.1.x** with **SDK Platform 35**.
- **PICO Spatial Plugin:** ZIP install; provides project templates (Planar, Volumetric, Full Stage), emulator management, editor integration.
- **Spatial Editor:** USD scene authoring, Shader Graph, Timelines (Beta), Audio Mixer. Projects compile to `.bundle` via Gradle plugin.
- **PICO Emulator:** Simulates gaze–hand and controller input, MR simulation (VST, spatial mesh). No real hand tracking (no ToF on PC); limited FOV; many Spatial SDK features disabled.

### Build Pipeline
1. **Spatial Editor** authors `.usda` scenes and assets.
2. **Gradle plugin** (`com.pico.spatial.tools`) compiles editor project → `.bundle` into APK `assets/`.
3. Runtime loads: `AssetBundle.load("asset://name.bundle")`.
4. `androidResources.noCompress` must include `.bundle`, `.glb`, `.wav`, `.usdz`.

### Dependencies (BOM)
```
com.pico.spatial:bom:0.10.7
com.pico.spatial.core:core
com.pico.spatial.ui:platform / foundation / design
com.pico.spatial:sense
com.pico.spatial:tracking
com.pico.spatial.spatialml:securemr / readback
```

## Performance Budgets

| Metric | Shared Space (Planar/Volumetric) | Full Space (Stage) |
|--------|---|----|
| Triangles | ~175k | ~350k |
| Draw calls | ~80 | ~90 |
| Active entities | ~30 | ~60 |
| Textures | ≤2048 (ASTC 6x6) | ≤4096 |
| Skinned mesh bones | ≤72 | ≤120 |
| Bone weights/vertex | 4 | 4 |
| Skinned meshes | ≤15 | ≤15 |

**Key practices:** Async model loading (off main thread), `MeshInstancesResource` for draw call reduction, LOD, limit dynamic lights (≤3 recommended), bake IBL when possible.

## Design Principles

- **Visual focus:** Primary UI in comfortable central field; depth conveys priority (nearer = more important); rounded corners, horizontal layouts for wide FOV.
- **Comfort:** No sudden full occlusion, unannounced large motion, or high-saturation blocks; vestibular–visual consistency.
- **Units:** Spatial panels use **dp** (auto-converted from **dmm**); 3D content uses **meters**. Minimum hit target: **56 × 56 dp**.
- **Window defaults:** Planar 1280 × 720 dp, clamp 320 × 180 – 2700 × 1800; default 2.5m distance.
- **Interaction modalities:** Gaze–pinch, direct poke/pinch, hand ray, controllers, mouse. System auto-switches by proximity.
- **Motion:** Purpose-driven, physics-like easing, <300ms for small feedback, 300–800ms for large transitions.
- **Audio:** Spatial audio aligned with visuals; prefer light, warm, natural sound design; 48 kHz / 24-bit / -13 LUFS.

## Important Constraints

1. **Experimental APIs:** Opt-in via manifest `pico.spatial.use_experimental_api=1` — apps with this flag **cannot ship on PICO Store**.
2. **No GMS:** PICO OS 6 has no Google Mobile Services — replace Firebase/GMS dependencies.
3. **Stage exclusivity:** Only one Stage open per app at a time.
4. **Tracking requires Full Space:** All `*TrackingProvider` APIs only work in Full Space (Stage open).
5. **Animation threading:** All animation APIs must run on `@MainThread`.
6. **NDK:** Only `arm64-v8a` ABI supported.
7. **MV-HEVC:** Spatial video from iPhone may have swapped L/R eyes (known issue, fix pending).

## Key References
When starting spatial application development, please adhere to these guidelines:

1. **Documentation**: Visit the `documentation/` directory to carefully read the spatial application development guide. MUST read related documentation before you starting. 
	- **Important: You must thoroughly review all related documentation before beginning.**

2. **Templates**: When creating a new project, you must refer to the project templates in the `templates/` directory:
   - Use `PlanarWindowContainer` for generating a Planar-based container app template.
   - Use `VolumetricWindowContainer` for a Volumetric-based container spatial app template.
   - Use `FullStage` for a Stage-based container app template.
   
3. **Core SDK Sources**: If you need to access deeper interface definitions and underlying implementation logic, please refer to the core source code of the corresponding components in the `sources/` directory. 
	- **Important: If you cannot locate the correct imports or usage for specific classes, review the SDK source files directly for accurate API references.**

4. **Examples & Best Practices**: When implementing specific functionalities, deeply refer to the offical examples [https://developer.picoxr.com/document/spatial-example/](https://developer.picoxr.com/document/spatial-example/) to obtain production-grade, complete feature code and best practices. 

