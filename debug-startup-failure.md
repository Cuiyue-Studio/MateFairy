# Debug Session: startup-failure

Status: [OPEN]

## Symptom

用户反馈应用启动失败。当前本地 Gradle 构建此前通过，但设备侧启动失败原因未知，需要收集运行时日志确认。

## Session

- sessionId: `startup-failure`
- startedAt: `2026-06-26`

## Hypotheses

- H1: 最近 `FOLLOWING` 目标改为 HMD 视野中心后，启动早期调用 `convertPositionTo(...)` 时相关 entity 尚未挂载到 scene，导致 native/SDK 异常。
- H2: `SpatialMeshManager` / `MeshTrackingManager.start()` 在当前设备、权限或空间状态下启动失败。
- H3: `HomeStage.initial` 中资源加载、bundle material 或 GLB 加载失败，导致 Stage 初始化中断。
- H4: ECS system 注册或 runtime dependency 绑定顺序异常，启动时 system update 早于关键 entity 初始化。
- H5: 安装包本身可构建但设备侧安装/启动命令或 Activity 入口异常。

## Evidence Plan

- 定位可用 adb 路径。
- 收集设备列表和最近崩溃栈。
- 若设备可用，清理 logcat 后主动启动 app 复现。
- 根据 `FATAL EXCEPTION` / `AndroidRuntime` / PICO SDK error 定位根因。

## Evidence Collected

- `adb` 不在 shell PATH 中。
- 已定位 adb 路径：`/Users/bytedance/Library/Android/sdk/platform-tools/adb`。
- 执行 `/Users/bytedance/Library/Android/sdk/platform-tools/adb devices -l`：
  - 结果：无设备连接。
- 因无设备连接，无法读取 `logcat` 或主动启动 `com.example.matefairy01/.platform.LaunchActivity` 复现。
- 执行 `./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`：
  - 结果：`BUILD SUCCESSFUL`。

## Current Assessment

- 当前证据只能排除编译/打包失败。
- 启动失败仍需设备侧崩溃栈确认，尤其是 `AndroidRuntime` / `FATAL EXCEPTION`。
- 在拿到 logcat 前，不修改业务逻辑。

## Follow-up Attempt

- 用户反馈重新启动仍失败，要求查看日志。
- 再次执行 `/Users/bytedance/Library/Android/sdk/platform-tools/adb devices -l`：
  - 结果仍为空设备列表。
- 执行 `pidof com.example.matefairy01`：
  - 结果：`adb: no devices/emulators found`。
- 执行 `logcat`：
  - 结果：卡在 `waiting for device`，无法获取设备日志。
- 本地项目内未发现可用的设备启动崩溃栈，只能看到源码和既有构建/调试日志。

## Static Code Investigation After User Clue

- 用户补充：启动瞬间能看到足球、音响、鸭子、精灵已加载，1-2 秒后资源消失且游戏退出。
- 该现象更符合：`HomeStage.initial` 资源加载已成功，随后 ECS system update 运行时崩溃。
- 最近朝向/跟随改动中，`FairyBehaviorSystem` 会在启动后第一次进入 `FOLLOWING` 时调用：
  - `hmdEntity.convertPositionTo(Vector3.ZERO, fairyEntity.getParent())`
  - `hmdEntity.convertPositionTo(HMD_LOCAL_FORWARD_POINT, fairyEntity.getParent())`
- 这条路径依赖 HMD entity、fairy parent、scene adapter 均处于稳定挂载状态。启动初期跨 entity/native 坐标转换是当前最可疑风险点。

## Mitigation Applied

- 保留“HMD 视野中心跟随目标”的行为。
- 移除 `FairyBehaviorSystem.getFollowViewCenterTarget(...)` 中的 `Entity.convertPositionTo(...)` 调用。
- 改为读取 `hmdEntity.components[TransformComponent]` 中已由 `HomeStage.update` 同步好的：
  - `position`
  - `eulerAngles.yaw`
- 通过 yaw 计算水平视线 forward，再生成 HMD 正前方 `0.9m` 目标点。
- 验证：
  - `./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL`
  - `./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL`

## Pending Evidence

- 仍需要设备侧复测确认是否不再启动退出。
- 若仍失败，必须获取 `AndroidRuntime` / `FATAL EXCEPTION` 日志；当前本机 adb 仍看不到设备。

## Provided Log File Check

- 用户提供路径：`/Users/bytedance/MateFairy/报错日志.txt`。
- 本地检查结果：
  - `ls -l /Users/bytedance/MateFairy/报错日志.txt` 显示文件大小为 `0` bytes。
  - `wc -c /Users/bytedance/MateFairy/报错日志.txt` 显示 `0`。
- 当前无法从该文件读取任何 `FATAL EXCEPTION` / `AndroidRuntime` 栈。

## Crash Log Evidence

- 用户重新写入 `/Users/bytedance/MateFairy/报错日志.txt`。
- 文件大小：`1048701 bytes`，共 `7532` 行。
- 关键崩溃栈：
  - `FATAL EXCEPTION: main`
  - `Process: com.example.matefairy01`
  - `java.lang.IllegalArgumentException: Vector3 x value cannot be Infinite or NaN`
  - `at com.pico.spatial.core.ecs.Scene.convexCast(...)`
  - `at com.example.matefairy01.behavior.FairyBehaviorSystem.constrainBySpatialMesh(FairyBehaviorSystem.kt:907)`
  - `at com.example.matefairy01.behavior.FairyBehaviorSystem.applyDirectBehaviorMotion(FairyBehaviorSystem.kt:428)`
  - `at com.example.matefairy01.behavior.FairyBehaviorSystem.update(FairyBehaviorSystem.kt:262)`
- 崩溃前存在大量：
  - `SPC-...SingleHitInfo E The object is invalid`
  - `Entity RemoveCollisionComponent`
  - `Entity RemoveTransformComponent`
  - `MeshResource::loadFromMeshAnchorUUID`
- 结论：
  - H1（HMD `convertPositionTo`）不是当前日志中的直接崩溃点。
  - H2 部分成立：Spatial mesh anchor 高频更新/替换期间，`scene.convexCast(...)` 返回或转换了包含 NaN/Inf 的命中数据，SDK `Vector3` 构造时抛异常。

## Fix Applied

- 修改 `FairyBehaviorSystem.constrainBySpatialMesh(...)`：
  - 在 cast 前检查 `current` / `desiredNext` / `direction` 是否全为 finite。
  - `moveDistance` 非 finite 时直接返回 `desiredNext`。
  - 用 `runCatching { scene.convexCast(...) }` 包裹 SDK cast。
  - 如果 Spatial SDK 在 mesh 更新期间抛出 `IllegalArgumentException`，降级为返回 `desiredNext`，避免应用崩溃。
  - 对返回结果继续过滤 `distance.isFinite()` 与 `distance >= 0f`。
- 验证：
  - `./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL`
  - `./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL`
  - `git diff --check` => pass
