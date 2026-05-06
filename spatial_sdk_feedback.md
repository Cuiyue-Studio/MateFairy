# PICO Spatial SDK 架构与开发体验反馈

## 1. 动态加载骨骼动画模型 (GLB) 的包围盒与视锥体剔除异常 (Frustum Culling)

**问题描述：**
在纯代码驱动的开发流中，直接使用 `Entity.load()` 异步加载带有骨骼动画的 `.glb` 模型时，底层渲染引擎无法正确地随着骨骼动画的播放动态更新实体的包围盒。当模型因骨骼动画产生位移或形变，由于初始包围盒未覆盖动画产生的形变区域，极易触发视锥体剔除，导致用户在视觉上看到一个"无形的矩形裁切框"，模型的身体部分被异常切断。

**业务影响：**
开发者必须依赖 PICO Spatial Editor 将模型重新打包为 `.bundle` 才能获得正确的包围盒行为，这严重阻碍了那些需要从云端动态下发原始 `.glb` 资产、或完全采用代码构建场景的敏捷开发工作流。

## 2. ECS 架构下动画控制与主线程的调度冲突

**问题描述：**
在 SDK 的 ECS 设计中，System 的 `update(SceneUpdateContext)` 驱动着场景的每帧逻辑。但在处理动画状态机时，`playAnimation()` 和 `AnimationPlaybackController` 的所有操作被严格限制在 `@MainThread`。由于业务逻辑（如 AI 状态决策、寻路计算）往往运行在后台协程，当需要根据逻辑状态切换动画时，频繁的线程切换导致代码冗余，且破坏了 ECS System 纯数据驱动的优雅性。

**业务影响：**
开发者无法在纯粹的 `System.update()` 中顺畅地分发动画指令，必须手动维护大量的控制器引用，并在不同线程之间抛递状态，极易引发内存泄漏或状态不同步。

## 3. TransformComponent.eulerAngles Setter 完全覆盖旋转，缺乏增量更新机制

**问题描述：**
`TransformComponent.eulerAngles` 的 setter 会完全覆盖实体的旋转状态，而非增量更新。这意味着如果开发者在 USD 场景文件或初始化代码中为模型设置了初始旋转（例如使人形模型正确站立的 pitch/roll），任何后续在 System.update() 中仅想修改水平朝向（yaw）的代码，都必须手动拼回初始的 pitch 和 roll 值。

**具体表现：**
我们在 USD 中为机器人模型设置了 `eulerAngles(-90, 180, 180)` 使其站立，但在 `FairyBehaviorSystem` 中每帧设置 `eulerAngles = EulerAngles(0f, currentYaw, 0f)` 以控制水平转向时，模型的 pitch 和 roll 被清零，导致机器人瞬间"面朝下"。

**业务影响：**
开发者必须在组件中额外存储 `initialPitch` 和 `initialRoll` 字段，并在所有更新旋转的地方重复拼接代码。这种设计违反了 ECS 的数据驱动原则，增加了不必要的样板代码，且容易因遗漏而导致难以排查的朝向 Bug。

**建议：**
提供增量旋转 API（如 `rotateYaw(delta)`）或支持局部坐标系下的旋转修改，使开发者无需关心其他轴向的基准值。

## 4. Entity 组件添加 API 命名不直观

**问题描述：**
`Entity` 并没有 `.addComponent()` 方法，而是通过 `ComponentSet` 的 `set()` 或索引器 `[]` 来添加/替换组件。这与主流 ECS 框架（如 Unity DOTS、Bevy）以及开发者直觉严重不符。`set()` 的命名暗示"设置属性"而非"添加组件"，而索引器赋值 `entity.components[Component::class.java] = component` 的语法在 Kotlin 中也显得冗长且不自然。

**业务影响：**
新接入的开发者极易误以为 SDK 不支持动态添加组件，或在尝试 `addComponent()` 报错后花费大量时间排查。这种隐式 API 设计增加了学习成本，且容易在代码审查中被忽略。

**建议：**
提供显式的 `addComponent()` / `removeComponent()` 方法作为 `set()` 的语法糖，或在文档中更突出地说明 `set()` 的实际语义是"添加或替换"。

## 5. USD 模型引用路径规则缺乏文档说明

**问题描述：**
在 `.usda` 场景文件中引用外部 `.usdz` 模型时，路径必须相对于 `.usda` 文件且需包含 `Assets/` 目录前缀（如 `@../Assets/Toy Robot 2_Anim.usdz@`）。但 Spatial Editor 在导出时并不会自动修正路径，且官方文档未明确说明这一规则。

**具体表现：**
我们在 Spatial Editor 中导入模型后，`.usda` 中的引用路径为 `@../Toy Robot 2_Anim.usdz@`（缺少 `Assets/` 前缀），导致运行时 `Entity.load()` 无法找到模型，应用启动后模型缺失且无任何有意义的错误日志。

**业务影响：**
开发者只能通过反复试错或反编译 `.bundle` 文件来推断正确的路径规则，调试效率极低。在团队协作中，美术人员使用 Spatial Editor 导出后，程序员需要手动修正 `.usda` 文件，增加了不必要的沟通成本。

**建议：**
1. 在 Spatial Editor 导出时自动修正引用路径。
2. 在文档中明确说明 `.usda` 引用外部资源的完整路径规则。
3. 当资源加载失败时，在 Logcat 中输出详细的资源查找路径和失败原因。

## 6. AssetBundle 生命周期管理缺乏显式约束

**问题描述：**
`AssetBundle.load()` 返回的 bundle 对象必须在所有 `Entity.loadSuspend()` 调用完成后才能调用 `close()`，否则会导致模型加载失败或应用崩溃。但这一约束并未在 API 签名或文档中显式体现，开发者很容易误以为 `load()` 后即可关闭 bundle。

**具体表现：**
我们在加载场景后立即调用了 `bundle.close()`，导致后续 `Entity.load()` 返回的实体树中所有子实体都缺失 `ModelComponent`，应用运行时视野中空无一物，且崩溃堆栈指向底层 native 代码，难以定位。

**业务影响：**
资源加载代码与实体实例化代码之间的耦合关系被隐式放大，开发者需要手动维护 bundle 的生命周期作用域。在协程和异步加载场景中，这种隐式约束极易引发时序相关的崩溃 Bug。

**建议：**
1. 提供 `AssetBundle.use { }` 或 `AssetBundle.loadEntities { }` 等带作用域的 API，自动管理生命周期。
2. 在 `close()` 的文档中明确标注"必须在所有实体加载完成后调用"。
3. 当在实体未完全加载时关闭 bundle，抛出带有明确提示的异常。

## 7. registerSystem DSL 包路径分散且文档缺失

**问题描述：**
使用 `registerSystem<T>()` DSL 注册自定义 System 时，正确的包路径是 `com.pico.spatial.ui.foundation.dsl.registerSystem`，而非直觉上的 `com.pico.spatial.core.scene` 或 `com.pico.spatial.core.ecs`。官方文档和代码示例中均未明确标注此导入路径。

**业务影响：**
开发者需要花费大量时间在源码中搜索 `registerSystem` 的定义位置，或尝试多个可能的包路径才能编译通过。这种分散的包结构设计增加了接入成本，且与 ECS 核心逻辑位于 `core` 模块的直觉预期不符。

**建议：**
1. 在 `core.ecs` 或 `core.scene` 模块中提供 `registerSystem` 的重新导出（re-export）。
2. 在官方文档的"自定义 System"章节中，将正确的 import 语句作为代码示例的首行展示。

## 8. EulerAngles 旋转顺序缺乏显式文档

**问题描述：**
`EulerAngles` 使用 ZXY 外旋顺序（extrinsic rotations），即先绕世界 Z 轴 roll，再绕世界 X 轴 pitch，最后绕世界 Y 轴 yaw。但这一关键信息未在官方文档中明确说明，开发者只能通过对 `toQuat()` 源码的反编译来推断。

**业务影响：**
当开发者从 Spatial Editor 中获取旋转参数（如 X: -90, Y: 180, Z: 180）并映射到代码时，由于不清楚旋转顺序，极易将参数错误地赋值给 `EulerAngles` 的构造器，导致模型朝向与预期完全不符。我们在调试机器人"面朝下"问题时，花费了大量时间验证不同旋转顺序的组合。

**建议：**
在 `EulerAngles` 的 KDoc 和官方文档中明确标注：
- 旋转顺序：ZXY（外旋）
- 参数映射：`pitch` 对应 X 轴，`yaw` 对应 Y 轴，`roll` 对应 Z 轴
- 提供与 Spatial Editor 旋转参数的映射示例

## 9. 缺乏运行时旋转调试工具

**问题描述：**
在排查模型朝向问题时，SDK 没有提供任何运行时查看或可视化实体旋转的工具。开发者无法确认当前实体的 `eulerAngles` 或 `rotation` 四元数的实际值，也无法在场景中绘制坐标轴辅助线。

**业务影响：**
调试旋转问题完全依赖"修改代码 -> 编译 -> 安装 -> 观察"的循环，效率极低。对于需要反复微调旋转参数的场景（如模型校准、动画对齐），这种缺乏反馈的开发体验非常痛苦。

**建议：**
1. 在 Debug 模式下提供 `TransformComponent` 的可视化 Gizmo（坐标轴辅助线）。
2. 提供运行时 Inspector 面板，可查看选中实体的 position/rotation/scale 数值。
3. 在 Logcat 中支持打印指定实体的变换矩阵（通过标签或调试开关控制）。

## 10. Spatial Editor 与 Runtime 的坐标系/旋转语义不一致

**问题描述：**
Spatial Editor 中显示的 Rotation (X, Y, Z) 与 Runtime 代码中的 `EulerAngles(pitch, yaw, roll)` 存在语义映射关系，但官方未提供明确的对应表。例如，Editor 中的 X 旋转对应 `pitch`，Y 对应 `yaw`，Z 对应 `roll`，但开发者在不清楚这一映射时，很容易将参数填错位置。

**业务影响：**
美术在 Editor 中调整好模型朝向后，程序员需要反复试验才能将相同的旋转复现到代码中。这种"Editor 一套，Runtime 一套"的语义割裂增加了协作成本。

**建议：**
1. 在 Spatial Editor 的 Rotation 面板中，同时显示对应的 `EulerAngles` 参数名（如 X (pitch), Y (yaw), Z (roll)）。
2. 提供"Copy Runtime Code"功能，一键生成设置旋转的 Kotlin 代码片段。
3. 在文档中提供 Editor 与 Runtime 旋转参数的完整映射表。

---

**反馈日期**: 2026-05-03
**涉及版本**: PICO Spatial SDK 0.11.7
**反馈项目**: MateFairy01
