# MateFairy01 技术文档总览

> 本文档汇总了项目各阶段的技术交接与架构设计文档，用于帮助后续开发人员或 Agent 快速理解项目技术细节与接口约定。
> 最后更新：2026-06-27

---

## Phase 1 架构设计与技术交接文档

### 1. 项目概述
本项目是基于 **PICO Spatial SDK** 开发的一款名为“伴随精灵 (MateFairy)”的 Stage 全空间容器应用。
Phase 1 阶段已完成核心底层架构和基础接口的定义，主要包括 AI 抽象层、动作调度系统、情绪渲染引擎以及智能上下文管理系统。

**本文档旨在作为开发交接指南，后续开发人员或 Agent 可直接基于本阶段定义的接口进行各模块的具体实现，无需修改现有底层抽象逻辑，以避免代码冲突。**

---

### 2. 基础环境与依赖说明
- **开发语言**: Kotlin
- **框架支持**: PICO Spatial SDK, Jetpack Compose
- **SDK 版本**: `compileSdk 35`, `minSdk 35`
- **JDK 版本**: 必须使用 JDK 11 或以上版本（在 `build.gradle.kts` 中已配置 `sourceCompatibility = JavaVersion.VERSION_11`）
- **核心依赖项** (已在 `libs.versions.toml` 中配置):
  - `kotlinx-coroutines-core` & `kotlinx-coroutines-android` (版本 1.8.1)
  - `kotlinx-serialization-json` (版本 1.6.3)

---

### 3. 核心架构与接口定义

项目核心逻辑分布在 `com.example.matefairy01` 的以下包中：`ai`, `action`, `emotion`, `memory`, `di`。

#### 3.1 AI 抽象层 (`com.example.matefairy01.ai`)
该层用于隔离具体的大语言模型（LLM）网络请求逻辑，后续可以自由接入豆包、DeepSeek 等大模型。

**数据模型 (Data Models):**
- `ChatMessage`: 可序列化类，表示单条对话记录。
  - `role: String` - 角色，如 "system", "user", "assistant"
  - `content: String` - 对话内容
- `AIResponse`: 可序列化类，表示大模型返回的结构化 JSON 结果。
  - `reply_text: String` - 精灵回复的文本
  - `emotion: String` - 精灵的情绪状态（默认 "neutral"）
  - `action_intent: String` - 精灵的动作意图（默认 "none"）

**核心接口: `ILLMProvider`**
- **功能**: 大模型服务的抽象提供者接口。
- **属性**:
  - `var systemPrompt: String`: 基础系统设定（设定精灵性格、背景等）。
- **方法**:
  - `suspend fun chat(messages: List<ChatMessage>): AIResponse`
    - **功能**: 发送完整对话历史，并解析大模型的 JSON 响应。
    - **传参**: `messages` - 组装好的完整上下文消息列表。
  - `suspend fun summarize(messages: List<ChatMessage>): String`
    - **功能**: 接收被淘汰的对话历史，生成一段文本摘要，用于长期记忆压缩。
    - **传参**: `messages` - 需要被压缩的历史对话列表。

#### 3.2 动作调度系统 (`com.example.matefairy01.action`)
用于将大模型返回的 `action_intent` 字符串映射并执行为具体的 3D 空间动作（如飞行、转身、播放动画）。

**核心接口: `IActionHandler`**
- **功能**: 具体动作逻辑的处理者接口。
- **属性**:
  - `val intent: String`: 绑定的动作意图标识符（如 "come_to_player"）。
- **方法**:
  - `suspend fun execute(fairyEntity: Entity, params: Map<String, Any> = emptyMap())`
    - **功能**: 执行空间实体动作。
    - **传参**:
      - `fairyEntity`: PICO Spatial SDK 中的精灵实体。
      - `params`: 附加的动作参数字典。

**调度中心: `ActionRegistry`**
- **功能**: 基于策略模式管理和分发动作。
- **方法**:
  - `fun register(handler: IActionHandler)`: 注册新的动作处理器。
  - `fun unregister(intent: String)`: 移除动作处理器。
  - `suspend fun dispatchAction(intent: String, fairyEntity: Entity, params: Map<String, Any>)`: 根据意图查找对应的 Handler 并执行。若意图为 "none" 则忽略。

#### 3.3 情绪渲染引擎 (`com.example.matefairy01.emotion`)
用于处理大模型返回的 `emotion` 字段，并在 3D 模型或 UI 上进行表现。

**核心接口: `IEmotionRenderer`**
- **功能**: 情绪具体渲染逻辑的接口。
- **方法**:
  - `fun renderEmotion(emotion: String, fairyEntity: Entity)`
    - **功能**: 执行渲染逻辑（例如改变材质颜色、播放面部 BlendShape 等）。
    - **传参**: `emotion` (情绪标识), `fairyEntity` (精灵实体)。

**管理引擎: `EmotionEngine`**
- **功能**: 管理情绪状态机，防止相同情绪频繁触发。
- **方法**:
  - `fun triggerEmotion(emotion: String, fairyEntity: Entity)`: 触发情绪变更，如果新情绪与当前情绪一致则阻断渲染。
  - `fun resetEmotion(fairyEntity: Entity)`: 重置为 "neutral"（中立）状态。

#### 3.4 智能上下文管理系统 (`com.example.matefairy01.memory`)
解决长对话导致的 Token 消耗大和延迟增加问题。

**核心组件: `ContextMemorySystem`**
- **功能**: 基于滑动窗口的短期记忆 + 异步摘要的长期记忆系统。
- **构造参数**:
  - `llmProvider: ILLMProvider` (依赖注入大模型服务)
  - `windowSize: Int` (短期记忆保留的轮数，默认 3)
- **方法**:
  - `fun buildPromptMessages(): List<ChatMessage>`
    - **功能**: 组装发起请求时的最终 Prompt。包含 `System Prompt` + `长期记忆摘要 (Long-term Summary)` + `短期窗口内的原始对话 (Short-term)`。
  - `fun addMessage(message: ChatMessage)`
    - **功能**: 将新对话推入短期记忆。若超出 `windowSize * 2`，则弹出最旧的一轮对话，并启动后台协程调用 `llmProvider.summarize` 生成新的摘要，合并入长期记忆中。
  - `fun clearMemory()`: 清空当前全部对话记忆。

#### 3.5 依赖注入与服务定位 (`com.example.matefairy01.di`)

**定位器: `AppModule`**
- **功能**: 轻量级 Service Locator，全局单例管理核心模块。
- **管理实例**:
  - `actionRegistry: ActionRegistry`
  - `emotionEngine: EmotionEngine`
  - `contextMemorySystem: ContextMemorySystem`
- **初始化方法**:
  - `fun initialize(provider: ILLMProvider, renderer: IEmotionRenderer)`
    - **要求**: 在 App 启动 (MainActivity 或 Stage 创建) 时，必须优先调用此方法注入 `ILLMProvider` 和 `IEmotionRenderer` 的具体实现类。

---

### 4. 后续开发指南与防冲突约定

另一个 Agent 在接手 Phase 2 及后续开发时，应遵循以下约定：

1. **接口实现优先**:
   - 若要实现大模型网络请求，请创建新类并实现 `ILLMProvider` 接口，然后在 `AppModule.initialize()` 中传入，**不要修改现有的 `ILLMProvider.kt` 文件**。
   - 若要增加新的精灵动作（如 "dance"），请创建类实现 `IActionHandler`，通过 `AppModule.actionRegistry.register()` 进行注册，**不要修改 `ActionRegistry.kt` 源码**。
2. **状态管理收口**:
   - 所有情绪改变必须通过 `AppModule.emotionEngine.triggerEmotion()` 发起，**严禁绕过 Engine 直接调用 Renderer**。
   - 所有对话上下文构建必须通过 `AppModule.contextMemorySystem.buildPromptMessages()` 获取，**严禁在请求层单独拼接对话记录**。
3. **协程作用域 (Coroutine Scope)**:
   - `ContextMemorySystem` 内部使用了 `Dispatchers.IO` 协程进行异步压缩，若后续集成进 PICO Stage 生命周期，请考虑传入外部可控的 `CoroutineScope` 替换硬编码的 Scope 以防内存泄漏。

---

## Phase 2 & 3 技术交接文档

### 1. 阶段概述

本项目在 Phase 2/3 阶段主要实现了基于 PICO Spatial SDK 的 3D 空间行为引擎。核心交付物为 `FairyBehaviorSystem`，它控制精灵在用户（HMD）周边的随机巡航与动态跟随，并创新性地采用了“双圆缓冲区”算法防止边界抖动。

**本阶段重大改进**：
- 状态机重构：从布尔标志升级为四状态枚举，彻底解耦“随机等待”与“跟随悬停”逻辑。
- 旋转系统修复：解决了机器人模型在运动中失去初始姿态（面朝下）的核心问题，实现了姿态保持与水平转向的分离。
- 资源替换：从默认立方体升级为带骨骼动画的机器人 USDZ 模型。

### 2. 核心架构与类说明

#### 2.1 行为组件 (`FairyBehaviorComponent`)
- **路径**: `com.example.matefairy01.behavior.FairyBehaviorComponent`
- **功能**: 数据组件（Component），存储精灵的运动状态与参数。
- **主要参数**:
  - `innerRadius`: 内圆半径（默认 1.0m）。精灵的随机飞行目标点均会在此范围内生成。
  - `outerRadius`: 外圆半径（默认 1.5m）。用于判定精灵是否彻底脱离了用户的活动区域。
  - `speed` & `followSpeed`: 随机巡航速度与触发跟随时的加速速度。
  - `hoverHeight`: 精灵相对于 HMD 的悬浮高度偏移（默认 -0.3m，位于头显下方）。
  - `zDeviationRange`: Z 轴偏离区间，控制精灵在前后方向的飞行范围。
- **状态变量**:
  - `state`: 四状态枚举 (`RANDOM_MOVING`, `RANDOM_WAITING`, `FOLLOWING`, `FOLLOW_HOVERING`)。
  - `currentTarget`: 随机飞行的当前三维目标坐标。
  - `waitTimer`: 精灵到达目标点后的悬停等待时间（随机 1~3 秒）。
  - `initialPitch` / `initialRoll`: **关键字段**，记录 USD 模型初始旋转，用于保持机器人站立姿态。

#### 2.2 行为更新系统 (`FairyBehaviorSystem`)
- **路径**: `com.example.matefairy01.behavior.FairyBehaviorSystem`
- **功能**: 继承自 `System`，负责在每帧 `update(context: SceneUpdateContext)` 时计算精灵的新坐标与朝向。
- **逻辑流程**:
  1. 检索带有 `HMDTagComponent` 的实体，获取用户最新 HMD 坐标。
  2. 检索带有 `FairyBehaviorComponent` 的精灵实体。
  3. 计算二者的水平（X-Z）距离 `d`。
  4. **状态机** (使用 `when(behavior.state)` 精确控制):
     - `RANDOM_MOVING`: 正常随机巡航。若 `d > outerRadius`，切换为 `FOLLOWING`；若抵达目标点，切换为 `RANDOM_WAITING`。
     - `RANDOM_WAITING`: 原地悬浮等待（1~3秒），**不面向玩家**。等待结束后切换回 `RANDOM_MOVING`。
     - `FOLLOWING`: 持续向玩家当前位置飞行。若 `d <= innerRadius`，切换为 `FOLLOW_HOVERING`。
     - `FOLLOW_HOVERING`: 到达玩家附近后悬停（1~2秒），**面向玩家**。结束后切换回 `RANDOM_MOVING`。
  5. 使用 Steering Behaviors 实现平滑曲线运动与速度插值。
  6. **旋转处理**: 修改 `TransformComponent.eulerAngles` 时，**保留 `initialPitch` 和 `initialRoll`**，仅修改 `yaw`，确保机器人始终站立。

#### 2.3 HMD 数据接入与场景组装 (`HomeStage`)
- **路径**: `com.example.matefairy01.content.HomeStage`
- **核心修改**:
  - 引入了 `HMDTrackingProvider` 获取头显实时追踪数据（需要应用处于 Full Space 状态）。
  - 通过 `DisposableEffect` 注册了 `FairyBehaviorSystem`，并与 `HMDTrackingProvider` 绑定生命周期。
  - 在 `SpatialView` 中构建了影子实体 `hmdEntity`（附加了 `HMDTagComponent`），在 `update` 回调中将真实头显的姿态数据同步给该实体，供行为系统读取。
  - 加载 `AssetBundle` 并实例化 `MyScene` USD 场景，提取机器人模型。
  - **关键操作**: 在附加 `FairyBehaviorComponent` 前，读取机器人当前 `TransformComponent.eulerAngles`，将 `pitch` 和 `roll` 存入组件的 `initialPitch` / `initialRoll` 字段。

### 3. PICO Spatial SDK ECS 避坑指南

后续开发者或 Agent 在进行 PICO Spatial SDK 开发时，请注意以下未在文档中显式指出的接口规范：

1. **添加组件的方法**: `Entity` 并没有 `.addComponent()` 方法，而是维护了一个 `ComponentSet`，需要使用 `entity.components.set(YourComponent())` 或是 `entity.components[YourComponent::class.java] = YourComponent()` 来添加和替换组件。
2. **注册系统的包路径**: 使用 DSL 注册系统时，需引入 `import com.pico.spatial.ui.foundation.dsl.registerSystem`，而不是 `core.scene`。
3. **坐标系体系**: `TransformComponent` 的位置（position）为 `Vector3`，旋转（rotation）为 `Quat` 或欧拉角 `EulerAngles`。
4. **旋转覆盖陷阱（极其重要）**:
   - `TransformComponent.eulerAngles` 的 setter 会完全覆盖旋转。
   - 如果模型在 USD 中设置了初始旋转（如为了站立），任何后续代码设置 `eulerAngles = EulerAngles(0f, yaw, 0f)` 都会将其破坏。
   - **解决方案**: 在组件中记录初始 `pitch`/`roll`，后续更新时将其拼回：`EulerAngles(initialPitch, currentYaw, initialRoll)`。
5. **USD 模型引用路径**: 在 `.usda` 场景中引用外部 `.usdz` 模型时，路径必须相对于 `.usda` 文件，且需包含 `Assets/` 目录前缀（如 `@../Assets/Toy Robot 2_Anim.usdz@`）。
6. **AssetBundle 生命周期**: `AssetBundle.load()` 返回的 bundle 必须在所有 `Entity.loadSuspend()` 调用完成后才能 `close()`，否则会导致模型加载失败或应用崩溃。

### 4. 状态机设计说明

#### 4.1 为什么需要四状态？
旧版本使用 `isFollowing` + `isHovering` 两个布尔值，导致三种物理状态被压缩：
- 跟随后的悬停（应面向玩家）
- 随机运动间的等待（不应面向玩家）
- 随机运动/跟随运动（面向运动方向）

这造成了“随机等待时也会看向玩家”的 Bug。四状态枚举彻底解耦了这些逻辑。

#### 4.2 状态转换图
```
                    ┌──────────────────────────────────────────┐
                    │                                          │
                    ▼                                          │
┌──────────────┐  到达目标   ┌───────────────┐  等待结束   ┌──────────────┐
│ RANDOM_MOVING │ ──────────▶ │ RANDOM_WAITING │ ──────────▶ │ RANDOM_MOVING │
└──────────────┘             └───────────────┘             └──────────────┘
       │                                              ▲
       │ d > outerRadius                              │
       ▼                                              │
┌──────────────┐  d <= innerRadius  ┌───────────────┐  悬停结束   │
│  FOLLOWING   │ ──────────────────▶ │ FOLLOW_HOVERING│ ───────────┘
└──────────────┘                    └───────────────┘
```

### 5. 旋转系统修复详解

#### 5.1 问题现象
机器人模型在 USD 场景中已正确设置旋转 `(-90, 180, 180)` 以站立，但应用运行后始终面朝下。

#### 5.2 根因分析
`FairyBehaviorSystem` 每帧执行：
```kotlin
transform.eulerAngles = EulerAngles(0f, behavior.currentYaw, 0f)
```
这段代码将 `pitch` 和 `roll` 清零，破坏了 USD 中设置的站立旋转。

#### 5.3 修复方案
1. 在 `FairyBehaviorComponent` 中增加 `initialPitch`, `initialRoll`, `hasRecordedInitialRotation` 字段。
2. 在 `HomeStage.kt` 初始化时读取并记录模型的初始旋转。
3. 在 `FairyBehaviorSystem` 所有设置旋转的地方，使用初始 `pitch`/`roll`：
```kotlin
val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
transform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)
```

#### 5.4 前后方向反转修正
修复站立问题后，发现机器人前后方向相反。在计算运动方向和面向玩家时，对 `atan2` 结果增加 180° 偏移：
```kotlin
val targetYaw = atan2(velocity.x, velocity.z).toDegrees() + 180f
```

### 6. 资源文件说明

- `editor-asset/src/main/res3d/SpatialPackContent/Sources/Scenes/MyScene.usda`: 主场景文件，包含机器人模型引用、灯光、IBL 环境光设置。
- `editor-asset/src/main/res3d/SpatialPackContent/Sources/Assets/Toy Robot 2_Anim.usdz`: 机器人模型（带骨骼动画）。
- `app/src/main/assets/editor-asset.bundle`: 由 Gradle 插件编译生成的 AssetBundle，运行时加载。

---

**文档版本**: 2.0
**更新日期**: 2026-05-03
**涉及版本**: PICO Spatial SDK 0.11.7

## Phase 4 技术交接文档：动画调度系统与骨骼动画解耦

### 1. 阶段概述
本阶段核心完成了**动画系统的解耦与状态机集成**。开发了一个独立的 `AnimationModule`，实现了 `pico_robot_animated.glb` 模型资源的直接加载，并将骨骼动画的播放与精灵的“静止/运动”状态彻底绑定，引入了动画驱动的等待逻辑和防止过度活跃的频控机制。

### 2. 核心架构与类说明
#### 2.1 动画配置中心 (`AnimationConfig.kt`)
- 定义了 `AnimationType`（`IDLE`, `MOVING`）与枚举 `FairyAnimation`。
- 将 `trackIndex` 映射为可配置的对象，同时携带了预估的 `durationMs`。

#### 2.2 独立动画调度器 (`AnimationModule.kt`)
- 维护了模型的 `skinnedMeshEntity` 和 `animationResources` 数组。
- 提供了 `playRandomIdleAnimation()` 和 `playRandomMovingAnimation()`。
- **频控逻辑**：内置 `IDLE_ANIM_COOLDOWN_MS = 6000L` 冷却时间校验 `canPlayIdleAnimation()`。

#### 2.3 行为系统的动画扩展 (`FairyBehaviorComponent` & `FairyBehaviorSystem`)
- **新增状态位**：`FairyBehaviorComponent` 中增加了 `isWaitingForAnimation` 布尔值，用于标记当前是否处于动画驱动的挂起状态。
- **动态定时器**：在切换到 `RANDOM_WAITING` 时，优先读取播放动画的 `durationMs` 覆盖随机等待时间 `waitTimer`，倒计时归零时忽略旧的重置逻辑，直接恢复 `RANDOM_MOVING` 状态。

### 3. SDK/框架避坑指南
- **动画时长获取困境**：`AnimationResource` 源码未公开 `duration` 属性。不要试图通过反射等方式读取它，若需精准感知动画结束，需通过 `SpatialViewContent.subscribe(AnimationEvents.Terminated::class.java)` 捕获事件；如果不方便获取上下文，使用业务代码预估 `duration` 配置表是更简单解耦的策略。
- **文本气泡跟随修正**：使用 `Entity.load("asset://...")` 时，必须将悬浮 UI `Attachment` 绑定到该返回的动态实体（或其子节点）上，否则 UI 将固定在世界原点而无法跟随移动。

### 4. 资源文件说明
- **移除**：删除了 `HomeStage.kt` 中对 `editor-asset.bundle` 里 `Toy_Robot_2_Anim` USDZ 模型的依赖逻辑。
- **引入**：全面使用 `app/src/main/assets/pico_robot_animated.glb` 作为基础带骨架实体。

### 5. GLB 模型缩放失控避坑指南 (Phase 4.1 补充)
- **现象**：当通过 `Entity.load` 加载带有复杂骨骼或预设局部缩放（如 0.01）的 `.glb` 文件时，直接对返回的根 Entity 修改 `scaleVector` 可能会失效或被引擎管线冲掉，导致模型以极巨大的原始顶点尺寸（如 0.85米甚至更大）渲染，并在距离用户较近时被相机近裁剪面（Near Clip Plane）严重裁切。
- **原因**：部分包含骨骼动画的 GLB 文件中，骨骼根节点（如 `pico_robot`）的 Transform 数据在底层可能会覆盖或者干扰 SDK 对包裹其实体的 `TransformComponent` 修改，尤其是在还未 `addChild` 之前进行的修改。
- **解决方案（Wrapper 模式）**：
  ```kotlin
  val glbRoot = Entity.load("asset://model.glb")
  val wrapper = Entity() // 创建空包装器
  wrapper.addChild(glbRoot)

  // 在包装器上进行缩放、位移和组件挂载
  wrapper.components[TransformComponent::class.java]?.apply {
      scaleVector = Vector3(0.1f, 0.1f, 0.1f)
  }
  // 将逻辑组件挂载在包装器上
  wrapper.components.set(behaviorComponent)
  scene.addChild(wrapper)
  ```
  **注意点**：当使用了 Wrapper 之后，如果将其他附件（如 UI/文本气泡）作为 Wrapper 的子节点，该附件的 `localPosition` 同样会被缩小。例如 Wrapper 缩放了 `0.1`，要将文本顶高 20 厘米，其 Y 轴需要设置为 `2.0f` 而非 `0.2f`。

### 6. PICO SDK 的 SkinnedMesh 包围盒剔除 Bug (Phase 4.2 补充)
- **现象**：精灵飞近屏幕边缘或做较大动作（如 `TURBO_DASH`、`SPIN_LEAP`）时，其身体偶尔一闪一闪或被截断消失。
- **原因分析**：这是典型的 **Frustum Culling（视锥体剔除）错误**。当引擎评估模型是否在视野内时，依赖的是 `SkinnedMesh` 的 AABB 包围盒（由初始姿势 `T-pose` 或未缩放时计算得出）。如果模型的总体缩放被设置得太小（如 `0.1` 级别，实际高度不到 10 厘米），模型一旦做“手部或身体超出原包围盒边界”的骨骼动画，超出部分将不被引擎纳入剔除计算。一旦原 AABB 的中心离开了相机视锥，尽管此时模型由于动画舒展视觉上应该还在画面里，引擎仍会强行不渲染它，导致突然消失。
- **解决方案**：
  1. **放大基准尺寸**：提升 `FairyWrapper` 的 `scaleVector`（本例中调至 `0.35f`，将其基准体型调整至 30~40 厘米）。通过放大基准体积，包围盒自然变大，覆盖住了内部舒展动画的影响范围，大大减少误剔除的发生。
  2. **手工补丁（必要时采用）**：对于必须保持极小尺寸但动作舒展幅度很大的实体，可在 Wrapper 内再添加一个隐形（全透明材质）且巨大的 Box 网格 `Entity`。这将强行拉大 Wrapper 层级的整体 AABB 计算结果。



### 7. PICO SDK 动态 GLB 骨骼模型 (SkinnedMesh) 视锥剔除异常原因 (Phase 4.5)
- **旧逻辑（静态 Bundle）为何正常**：之前项目中使用的 `.bundle` 或静态 `.usdz` 模型，是由 PICO Spatial Editor 在 PC 端经过完整管线预处理过的。Editor 会准确计算出包裹整个网格的最大包围盒（AABB），并写入 Bundle。在引擎加载时，它的 Bounds 是完全准确的，无论多近只要还有网格在视野里就不会被错误剔除。
- **新逻辑（动态 GLB 骨骼）为何异常**：通过 `Entity.load()` 在移动端运行时直接解析原生的 `.glb` 骨骼动画模型时，由于 PICO SDK 运行时解析器的限制，它计算的 `SkinnedMesh` 初始包围盒**极有可能依赖于其缩放前的原始巨大 T-Pose 数据，或者计算中心点发生了严重的偏移**（例如包围盒中心在模型脚底极远处）。
  - 当模型被缩放至 0.35 后，这个偏移或异常的包围盒与实际视觉模型产生了严重脱节。
  - 这导致：当实际模型靠近头显时，那个**隐形且错位的包围盒**其实早就越过了相机的后方或跑到了视野之外。引擎一旦判定该错位的包围盒出界，就会立刻执行 Frustum Culling（视锥体剔除），导致近在眼前的视觉模型瞬间消失。
- **结论**：这是一个移动端运行时解析带复杂动画 GLB 文件的底层 Bounds 计算脱节 Bug。对于强依赖近距离交互的伴随精灵，最稳妥的生产级方案是**必须将 `.glb` 导入 Spatial Editor，烘焙并导出为 `.bundle` 资产**，由 Editor 管线修复其 Bounds，而不是直接在代码中通过运行时加载原始 GLB。

### 10. PICO SDK 父子层级缩放的致命陷阱：Frustum Culling 与视觉错位 (Phase 4.7)
- **现象**：精灵在远离时正常，一旦飞近玩家（如 0.5 米内），就会出现身体被“一刀切”或闪烁消失的诡异裁切。
- **根本原因（非 Bounds 偏移，而是 Scale 层级错位）**：
  - PICO SDK 的 `SkinnedMesh` 在进行视锥体剔除（Frustum Culling）时，计算世界空间包围盒（World AABB）所依赖的 `scaleVector`，**读取的是承载该 Mesh 的实体自身的 `TransformComponent.scaleVector`，而不是其父节点的累积缩放**。
  - 当开发者为了控制模型大小，创建一个空实体（Wrapper）作为父节点，将模型（GLB Root）作为子节点挂载，并在父节点上设置 `scaleVector = 0.35` 时：
    - **视觉层面**：由于引擎的渲染管线会计算完整的 Model-View 矩阵（包含父级缩放），所以玩家看到的模型是正确缩小的。
    - **剔除层面**：引擎的 Culling 管线在单独评估 `glbRoot` 实体时，读取到的是它自身 `TransformComponent` 中默认的 `scaleVector = (1, 1, 1)`（即原始未缩放尺寸）。
  - 这导致了一个“视觉模型”和“剔除盒子”严重不匹配的状态。视觉模型只有 30 厘米，但用于剔除的隐形盒子却有 85 厘米。当这个 85 厘米的隐形盒子因为精灵靠近而滑出相机视野时，引擎就会错误地将依然可见的 30 厘米模型剔除。
- **解决方案**：
  **绝对不要通过父节点缩放来控制带有骨骼动画的模型尺寸**。必须直接将缩放应用到加载出来的模型根实体上：
  ```kotlin
  // 正确做法：直接缩放 GLB 根节点
  glbRoot.components[TransformComponent::class.java]?.apply {
      scaleVector = Vector3(0.35f, 0.35f, 0.35f)
  }

  // Wrapper 仅作为逻辑容器，保持 1:1 缩放
  val robotModel = Entity()
  robotModel.addChild(glbRoot)
  robotModel.components[TransformComponent::class.java]?.apply {
      scaleVector = Vector3(1f, 1f, 1f)
  }
  ```
- **设计反思**：
  这个陷阱极具迷惑性，因为对于静态模型或无动画模型，父级缩放通常不会引发如此明显的剔除问题（因为它们的 Bounds 计算方式不同）。但对于 `SkinnedMesh`，其 Bounds 是动态且与实体自身 Transform 强绑定的，父级缩放在这里会成为一个“视觉欺骗”，导致不可预期的渲染剔除错误。

## Phase 5 技术交接文档：大模型链路与空间 UI 交互设计

### 1. 阶段概述
本阶段核心完成了**真实大模型（DeepSeek）的 API 接入**，并彻底重构了对话气泡的空间挂载逻辑和 UI 表现。确立了“大模型输出 JSON 意图 -> 本地解析分发动作 -> 空间 UI 独立朝向”的整体架构。

### 2. 核心架构与类说明

#### 2.1 大模型直连通信 (`DeepSeekLLMProvider.kt`)
- **JSON 方案**：为了保证架构的极简性和兼容性，舍弃了 `kotlinx.serialization`，采用 Android 原生的 `org.json.JSONObject` 和 `JSONArray` 进行请求体构建与响应解析。
- **结构化 Prompt**：在构建消息时，会自动向 System Prompt 追加 JSON 格式约束，确保模型输出固定的 `{"reply_text": "...", "emotion": "...", "action_intent": "..."}` 格式。
- **容错处理**：若大模型未按规范输出或解析失败，代码会自动提取文本主体作为 `reply_text`，并将意图降级为 `neutral` 和 `none`，保证对话流程不中断。

#### 2.2 集中式配置 (`AppConfig.kt` & `AppConfigLoader.kt`)
- 采用了严格的单文件配置策略，所有 AI 相关的业务配置（包含模型类型、API Key、超时时间、定时测试策略）都映射在 `AppConfig` 数据类中。
- 运行时从 `assets/app_config.json` 中反序列化配置，方便在真机调试时动态替换参数而无需重新编译代码。

#### 2.3 空间 UI 与跟随分离 (`HomeStage.kt`)
- **独立实体策略**：UI 气泡 (`dialogueAttachmentEntity`) 不再作为机器人模型 (`loadedRobotModelEntity`) 的子节点，而是作为同级的平级实体被添加到 Scene 的 Content 中。
- **空间位置同步**：在 `SpatialView` 的 `update` 回调中，每帧读取机器人的世界坐标，并将气泡实体的坐标设置为 `(robot.x, robot.y + 0.66f, robot.z)`，实现“仅跟随位置，不跟随旋转”的效果。
- **LookAt 凝视组件**：给气泡实体挂载 `LookAtComponent` 并调用 `setViewerAsTarget()`，使其 Z 轴始终指向 HMD。配合毛玻璃材质（`backgroundMaterial(Material.Regular)`），实现了具有空间纵深感的高级 UI 体验。

### 3. SDK/框架避坑指南

- **LookAtComponent 与 UI 附件的方向冲突**：
  PICO Spatial SDK 中，Compose 生成的 2D UI 附件（Attachment）挂载到 3D 实体时，默认是基于实体的局部坐标系。如果实体随模型旋转，UI 会难以阅读。
  **最佳实践**：针对需要被玩家阅读的文本气泡，务必将其与运动模型在层级上剥离。使用单独的实体承载 UI，自己手动同步位置，并借助 `LookAtComponent` 控制朝向。这能完美解决“精灵转身背对玩家时，气泡文字反转不可读”的问题。


---

## Phase 9 技术交接：手动空间网格扫描与环境碰撞实体

### 1. 阶段概述

本阶段实现了 MateFairy 在 Stage 全空间中手动开启/关闭 PICO 空间网格扫描的基础能力。开启后，应用会订阅 `MeshTrackingManager` 的 Mesh Anchor 生命周期事件，并将扫描到的真实环境网格转化为 App 侧的静态碰撞实体，为后续精灵与真实环境交互、射线检测、避障和刚体碰撞打基础。

### 2. 核心架构与类说明

#### 2.1 `SpatialMeshManager`

文件路径：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/perception/SpatialMeshManager.kt`

职责：
- 管理 `MeshTrackingManager` 的订阅、启动、停止和释放。
- 维护 `meshEntities: MutableMap<UUID, Entity>`，记录每个 Mesh Anchor 在 App ECS 场景中对应的碰撞实体。
- 将真实空间 Mesh Anchor 转换为静态物理碰撞体。

关键方法：
- `start(parentEntity: Entity)`：注册网格事件订阅并调用 `MeshTrackingManager.start()`。
- `stop(clearMeshes: Boolean = false)`：调用 `MeshTrackingManager.stop()`，可选清理已生成的环境实体。
- `dispose()`：停止扫描、清空网格实体并取消订阅。
- `handleAnchorUpdate(update: AnchorUpdate<MeshAnchor>)`：按 `ADDED`、`UPDATED`、`LOADED`、`REMOVED` 分发处理。
- `upsertMeshEntity(anchor: MeshAnchor)`：加载 MeshResource，创建 StaticMesh ShapeResource，并挂载 `CollisionComponent`。

核心流程：

```kotlin
MeshTrackingManager.subscribeAnchorUpdate { update ->
    mainHandler.post { handleAnchorUpdate(update) }
}

val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
val shape = ShapeResource.createStaticMesh(mesh)
val entity = Entity().apply {
    components[TransformComponent::class.java]?.apply {
        position = anchor.transform.position
        quaternion = anchor.transform.quaternion
    }
    components.set(
        CollisionComponent(
            collisionShape = listOf(shape),
            physicsMaterial = PhysicsMaterialResource(),
            collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
            collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
            collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
        )
    )
}
parent.addChild(entity)
```

#### 2.2 `HomeStage` 手动开关

文件路径：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`

新增内容：
- `val spatialMeshManager = remember { SpatialMeshManager(mainHandler) }`
- `var isMeshScanningEnabled by remember { mutableStateOf(false) }`
- `AttachmentPanel(id = "mesh_scan_toggle")` 常驻开关面板。
- 面板实体记录在 `HomeStageRuntimeState.meshScanToggleAttachmentEntity` 中，加入 `SpatialView` 场景并设置 `LookAtComponent`。

开关逻辑：

```kotlin
val nextEnabled = !isMeshScanningEnabled
isMeshScanningEnabled = nextEnabled
if (nextEnabled) {
    spatialMeshManager.start(rootEntity)
} else {
    spatialMeshManager.stop(clearMeshes = true)
}
```

### 3. SDK/框架避坑指南

1. **Mesh Tracking 必须运行在 Full Space**
   - 当前工程使用 `DefaultStage`，满足 Full Space 前置条件。

2. **ECS 组件操作要回到主线程**
   - `SpatialMeshManager` 通过 `Handler(Looper.getMainLooper())` 将 Mesh Anchor 回调中的实体创建和组件修改投递到主线程执行。

3. **Transform 使用 quaternion 而不是 rotation**
   - `MeshAnchor.transform.rotation` 是 `EulerAngles`。
   - `TransformComponent.quaternion` 需要 `Quat`，因此必须使用 `anchor.transform.quaternion`。

4. **Kotlin daemon 沙箱权限问题**
   - 当前命令行环境下，默认 Kotlin daemon 可能因为无法写入 `~/Library/Application Support/kotlin/daemon` 而失败。
   - 可使用 `--no-daemon -Dkotlin.compiler.execution.strategy=in-process` 进行编译验证。

5. **当前不渲染 Mesh Debug 可视化**
   - 环境实体没有 `ModelComponent`，只有 `CollisionComponent`。
   - 若要查看扫描网格，应额外给实体添加 `ModelComponent(mesh, debugMaterial)`，并提供调试开关。

### 4. 设计说明

- 本阶段选择“手动开关默认关闭”是为了先控制变量，验证 PICO 网格扫描、事件订阅、网格加载和碰撞实体生成链路是否稳定。
- 关闭扫描时使用 `clearMeshes = true`，符合当前 MVP 需求：手动关闭即清理 App 侧生成的环境碰撞体，避免用户误以为扫描仍然影响物理世界。
- 动态扫描、区块卸载、前方场景密度检测暂不进入本阶段，避免在基础链路未验证前引入复杂状态机。

### 5. 资源文件说明

- 修改：`/Users/bytedance/MateFairy/app/src/main/AndroidManifest.xml`
  - 新增 `com.picovr.permission.SPATIAL_DATA` 权限。
- 修改：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - 新增空间扫描开关 UI、扫描管理器生命周期、当前运行时架构接入。
- 新增：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/perception/SpatialMeshManager.kt`
  - 封装 MeshTrackingManager 与 Mesh Anchor 到碰撞实体的转换逻辑。


### Phase 9 补充技术说明：Debug Mesh 线框可视化

`SpatialMeshManager.upsertMeshEntity(anchor)` 当前会为扫描网格同时生成渲染组件和碰撞组件：

```kotlin
val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
val shape = ShapeResource.createStaticMesh(mesh)
val debugMaterial = UnlitMaterial.create().apply {
    setBaseColor(Color4(0.1f, 1.0f, 0.55f, 0.75f))
    setPolygonFillMode(PolygonFillMode.LINE)
}

components.set(ModelComponent(mesh, debugMaterial))
components.set(CollisionComponent(collisionShape = listOf(shape), ...))
```

注意事项：
- 线框渲染仅用于调试扫描覆盖范围和空间对齐情况，长时间开启会增加渲染开销。
- 后续如需产品化，应增加独立 Debug 开关，允许只保留碰撞体而隐藏 `ModelComponent`。
- 关闭空间扫描时，当前实现会销毁实体，因此 Debug 网格和碰撞体都会一起清理。


### Phase 9 补充技术说明：刚体精灵与射线悬浮

本阶段对精灵实体进行了全面物理化改造，使其能够与 PICO 扫描生成的环境网格进行物理碰撞，同时避免了死板的穿模。

#### 1. 实体物理配置 (`HomeStage.kt`)
```kotlin
val fairyShape = ShapeResource.createCapsule(0.1f, 0.3f)
val collision = CollisionComponent(
    collisionShape = listOf(fairyShape),
    physicsMaterial = PhysicsMaterialResource(),
    collisionResponseMode = CollisionResponseMode.COLLIDER_FULL
)
val rigidBody = RigidBodyComponent().apply {
    rigidBodyMode = RigidBodyMode.DYNAMIC
    isAffectedByGravity = false // 不受默认重力影响，完全由代码力驱动
    isRotationLocked = Bool3(true, true, true) // 锁定物理旋转
    linearDamping = 5.0f // 增加线性阻尼，避免受到冲量后过度滑行或震荡
    angularDamping = 5.0f
}
val physicsForce = PhysicsForceComponent()
```

#### 2. 行为系统改造 (`FairyBehaviorSystem.kt`)
旧有逻辑依赖于直接覆盖 `transform.position`，会破坏物理引擎计算。新逻辑基于**PD力控制**与**射线检测**：
- **速度计算**：由于 PICO SDK 未直接暴露实时物理速度的读取接口，此处通过 `(currentPos - lastPos) / dt` 来手动计算 `actualVelocity`。
- **目标追踪**：计算 `desiredVelocity`，然后通过比例增益 `(desiredVelocity - actualVelocity) * pGain` 转化为力施加到刚体上。
- **射线悬浮（Raycast Hover）**：
  ```kotlin
  val raycastDown = context.scene.rayCast(
      origin = fairyPos,
      direction = Vector3(0f, -1f, 0f),
      length = 0.6f,
      hitMode = CollisionCastHitMode.NEAREST,
      group = CollisionGroup(CollisionGroup.COLLISION_GROUP_ALL)
  )
  ```
  如果检测到下方存在刚体（如扫描的真实地板、桌子），且距离小于 `0.4m`，则施加额外的向上的强力（`suspensionForce`），像气垫船一样将精灵推离地面，实现“贴地飞行”并有效避免与凹凸不平的扫描网格直接物理磕碰导致的抽搐。
- **转向**：因物理旋转已锁定，在代码中根据速度方向继续使用 `transform.eulerAngles` 赋值实现平滑转向。


### Phase 9 补充技术说明：刚体代理与视觉模型解耦

为修复刚体改造后“模型变小”和“疯狂打转”的问题，当前架构改为：

1. `robotBody`
   - 不可见物理代理实体。
   - 挂载 `CollisionComponent`、`RigidBodyComponent`、`PhysicsForceComponent` 和 `FairyBehaviorComponent`。
   - 参与物理碰撞、受力、射线悬浮和行为状态机。

2. `robotModel`
   - 可见 GLB 视觉实体。
   - 只承载模型、动画和视觉 Transform。
   - 不再直接挂 `RigidBodyComponent`，避免物理系统修改视觉层级缩放。

核心同步逻辑位于 `FairyBehaviorSystem`：

```kotlin
val visualTransform =
    behavior.visualEntity?.components?.get(TransformComponent::class.java) ?: transform

// 物理代理位置驱动视觉模型
visualTransform.position = fairyPos

// 仅视觉层应用朝向，物理胶囊保持旋转锁定
visualTransform.eulerAngles = EulerAngles(
    pitch = pitch,
    yaw = behavior.currentYaw,
    roll = roll
)
```

朝向策略：
- 移动状态使用“目标方向”计算 yaw，避免物理位置抖动导致角度随机跳变。
- 悬停面向玩家状态使用玩家相对方向计算 yaw。
- 不再用 `actualVelocity` 直接计算 yaw。

射线悬浮过滤策略：

```kotlin
val floorHit = raycastDown.results.firstOrNull { it.entity != fairyEntity }
```

该过滤避免下方射线命中精灵自己的胶囊碰撞体，从而避免错误悬浮力反馈。


### Phase 9 补充技术说明：持续空间扫描模式

当前空间扫描模式已从“手动按钮开关”调整为“Stage 初始化后自动持续开启”。核心调用位于 `HomeStage.kt` 的 `SpatialView.initial`：

```kotlin
content.addEntity(rootEntity)
rootEntity.addChild(hmdEntity)
spatialMeshManager.start(rootEntity)
```

生命周期释放仍由 `DisposableEffect.onDispose` 统一处理：

```kotlin
spatialMeshManager.dispose()
```

`SpatialMeshManager` 当前只负责生成不可见的环境碰撞体：

```kotlin
val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
val shape = ShapeResource.createStaticMesh(mesh)
components.set(
    CollisionComponent(
        collisionShape = listOf(shape),
        physicsMaterial = PhysicsMaterialResource(),
        collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
        collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
        collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
    )
)
```

已移除内容：
- `AttachmentPanel(id = "mesh_scan_toggle")`
- `Button` / `Text` 相关 UI 控制
- `ModelComponent(mesh, debugMaterial)`
- `UnlitMaterial`、`PolygonFillMode.LINE`、`Color4` Debug 线框材质

注意：持续扫描用于验证“精灵持续感知并与现实网格交互”的体验，但它不是最终性能最优方案。正式探索模式仍建议后续增加动态扫描控制和网格区块卸载。


### Phase 9 补充技术说明：Spatial Gesture 与物理冲量 API 修正

本次排查发现，PICO Spatial SDK 0.11.7 中空间手势 API 的实际包路径为：

```kotlin
import com.pico.spatial.ui.foundation.gesture.detectSpatialTapGesture
import com.pico.spatial.ui.foundation.gesture.TargetEntity
```

`detectSpatialTapGesture` 的调用需要传入 Android `Context`：

```kotlin
detectSpatialTapGesture(
    context = context,
    targetedToEntity = TargetEntity.any { entity -> entity == football }
) {
    // on tap
}
```

物理组件注意事项：

- `PhysicsForceComponent` 是持续力/持续扭矩组件，只提供 `force` 和 `torque`。
- SDK 中不存在 `PhysicsForceComponent.addImpulse()`。
- 对“点击后给物体一个瞬时速度/冲量”这类交互，应使用 `PhysicsVelocityComponent`：

```kotlin
val velocityComp = football.components[PhysicsVelocityComponent::class.java]
    ?: PhysicsVelocityComponent().also { football.components.set(it) }
velocityComp.linearVelocity = Vector3(0f, 3.0f, -3.0f)
```

后续如果需要更真实的“按点击方向踢球”，应根据 tap 命中点、HMD/手柄方向计算 `linearVelocity` 向量，而不是固定使用 `Vector3(0f, 3.0f, -3.0f)`。

---

## Phase 10 技术交接：精灵与虚拟物体 Interaction Action 架构

### 1. 阶段概述

本阶段新增一套独立于旧 `ActionRegistry/IActionHandler` 的 ECS 交互 action 基建，用于描述“主体主动交互一个或多个客体”的持续行为。旧 action registry 继续负责 LLM 意图入口，新 interaction 模块负责场景实体解析、持续调度、运动接管和物理交互。

### 2. 核心架构与类说明

- `InteractionActorComponent`：挂在 action 主体实体上，默认 `actorId = "fairy"`，当前绑定到精灵物理代理实体。
- `InteractionObjectComponent`：挂在 action 客体实体上，当前 `Football` 被标记为 `objectId = "football"`。
- `FairyActionLockComponent`：action 执行期间挂在精灵主体上，通知 `FairyBehaviorSystem` 暂停常驻巡航/跟随力控制。
- `InteractionActionRequest`：一次 action 请求，包含 `actionId`、`controllerId`、`subjectId`、`objectIds` 和自由参数 `params`。
- `InteractionActionController`：controller 工厂接口，一个 controller 对应一种 action，负责创建独立的 `InteractionActionInstance`。
- `InteractionActionInstance`：实际持续执行的 action 实例，每帧由 `InteractionActionSystem.update()` 驱动，返回 `RUNNING/COMPLETED/FAILED`。
- `InteractionActionRequestBus`：旧 action registry 与 ECS action 系统之间的轻量请求总线。
- `SceneInteractionActionHandler`：将 LLM 输出的 `play-football` intent 转换为 `InteractionActionRequest`，避免旧 action 层直接持有场景实体引用。

### 3. play-football 链路

1. LLM 或业务层 dispatch `play-football`。
2. `SceneInteractionActionHandler` 向 `InteractionActionRequestBus` 写入请求。
3. `InteractionActionSystem` drain 请求，根据 `InteractionActionRegistry` 找到 `PlayFootballActionController` 并创建实例。
4. `PlayFootballActionInstance` 解析主体 `fairy` 与客体 `football`。
5. 实例给精灵主体添加 `FairyActionLockComponent`，常驻 `FairyBehaviorSystem` 检测到锁后不再写跟随/巡航力。
6. action 计算足球位置，驱动精灵飞到足球旁边，并让精灵面向足球。
7. 到达接近半径后，action 根据精灵朝向生成踢球方向，yaw 偏移被限制在 `maxKickFanAngleDegrees` 扇面内。
8. action 通过 `PhysicsVelocityComponent.linearVelocity` 给足球设置一次性初速度，并将踢球速度裁剪到 `minKickSpeed..maxKickSpeed`。
9. 后续预留：在踢球触发点接入 Action 类动画调度器，传入 `KICK_FOOTBALL` 等枚举播放踢球动画。

### 4. 关键代码示例

```kotlin
InteractionActionRuntimeDependencies.requestBus.enqueue(
    InteractionActionRequest(
        actionId = PlayFootballActionController.ACTION_ID,
        subjectId = "fairy",
        objectIds = listOf("football"),
        params = mapOf(
            "kickSpeed" to 1.8f,
            "kickYawOffsetDegrees" to 10f,
            "maxKickFanAngleDegrees" to 25f
        )
    )
)
```

### 5. SDK/框架避坑指南

- 精灵视觉模型和物理代理仍保持分离，action 应优先控制物理代理实体，视觉模型由行为系统/锁定同步逻辑跟随。
- 复杂 action 必须通过 `FairyActionLockComponent` 与常驻行为系统仲裁，否则多个系统会同时写 `PhysicsForceComponent.force`，导致精灵运动抖动或动作失效。
- 足球应继续保留 `RigidBodyMode.DYNAMIC`、`CollisionResponseMode.COLLIDER_FULL` 和 `CollisionDetectionMode.CONTINUOUS`，否则踢球速度较大时仍可能穿透薄空间网格。
- 当前踢球使用一次性速度而不是持续 force，原因是持续 `PhysicsForceComponent` 需要额外生命周期管理，容易产生残留力导致足球持续加速。

### 6. 后续扩展约定

- 新增复杂交互时，新建一个 `InteractionActionController`，并在 `MateFairyRuntimeFactory` 注册到 `InteractionActionRuntimeDependencies.actionRegistry`。
- 新增客体时，在实体初始化处挂 `InteractionObjectComponent(objectId = "xxx")`，action 内通过 objectId 解耦实体查找。
- 当 Action 类动画调度器落地后，建议新增 `TASK_ACTION` 或专用 `ACTION` 优先级层，并在 `PlayFootballActionController` 的 TODO 位置调用踢球动画。

---

## Phase 11 技术交接：Action 触发条件、锁定与优先级调度

### 1. 阶段概述

本阶段为 `play-football` 补齐两类触发条件：精灵随机触发和对话语义触发。同时新增全局 action 锁与生命周期监听器，保证 action 运行期间不会被随机动画、对话动作动画或情绪动画打断。

### 2. 核心新增机制

- `InteractionActionSource`：区分 action 来源，当前包含 `RANDOM` 和 `DIALOGUE`。
- `InteractionActionListener`：action 生命周期监听器，包含 `onActionStarted()` 与 `onActionFinished()`。
- `InteractionActionLockState`：全局 action 锁，记录当前 `actionId/controllerId/source`，并在开始/结束时通知监听器。
- `InteractionActionRequestBus.enqueue()`：返回 `Boolean`，锁定期间直接拒绝新 action 入队。
- `InteractionActionSystem`：启动 action 前调用 `tryLock()`，结束或失败时调用 `release()`，这是之后所有 action controller 的统一生命周期出口。

### 3. 动画锁定逻辑

`AnimationModule` 实现 `InteractionActionListener`：

1. action 开始时调用 `stopAllAnimations()`，停止当前正在播放的随机/动作/情绪动画。
2. `playAnimation()` 开头检查 `InteractionActionRuntimeDependencies.lockState.isLocked`。
3. 如果 action 锁存在，直接忽略播放请求。
4. action 结束后由 `InteractionActionSystem` 发出结束信号，锁释放，后续动画请求恢复。

### 4. 随机触发逻辑

`FairyBehaviorSystem` 的随机休息调度入口变为：

1. 精灵随机移动到目标点，进入 `RANDOM_WAITING`。
2. 或者精灵跟随玩家结束，进入 `FOLLOW_HOVERING`。
3. 调用 `scheduleRandomRestBehavior()`。
4. 优先执行 `tryScheduleRandomPlayFootball()`：要求当前无 action 锁、场景中存在 `football`，并命中 `RANDOM_PLAY_FOOTBALL_CHANCE` 概率。
5. 若随机 action 未触发，才调用 `avatarController.requestIdleAnimation()` 播放随机 idle 动画。

### 5. 对话触发优先级

`DefaultBehaviorDecisionMaker` 当前规则：

- 如果 `emotion` 是 `angry/sad`，拦截所有 action，只触发负面情绪。
- 如果 `action_intent` 是 `play-football`，且不是负面情绪，则只分发 action，不触发普通情绪动画。
- 其它普通动作保持原有逻辑，可与普通情绪并行。

示例行为：

- “给我一边跳舞一边踢球” → LLM 应输出 `action_intent = play-football`，决策层只执行踢球 action。
- “你个废物，给我一边跳舞一边踢球吧” → LLM 输出 `emotion = angry`，决策层拦截 action，只播放 mad/angry 动画。
- action 正在执行期间继续对话 → 文本可正常返回，但所有动画播放请求都会被 `AnimationModule` 忽略。

### 6. Prompt 约定

`DeepSeekLLMProvider` 的结构化 prompt 已加入规则：

- 用户要求“踢球”“玩足球”“去碰/踢 Football”时，即使同时要求跳舞、挥手，`action_intent` 也必须输出 `play-football`。
- 用户同时表达辱骂、贬低、攻击等负面冒犯时，`emotion` 必须输出 `angry`，`action_intent` 可继续识别为 `play-football`，由程序侧执行负面情绪优先策略。

### 7. 扩展约定

- 之后所有复杂 action 都必须通过 `InteractionActionSystem` 完成生命周期闭环，不能绕过 `tryLock()/release()`。
- 新 action 如需参与随机调度，应接入类似 `scheduleRandomRestBehavior()` 的统一调度入口，而不是在任意帧直接触发。
- 如果要支持 action 被负面情绪中断，需要在 `InteractionActionInstance.cancel()` 中完整清理 ECS 锁组件、力组件和临时状态。

---

## Phase 12 技术交接：Action 生命周期订阅与跟随暂停机制

### 1. 阶段概述

本阶段将 action 生命周期监听器升级为订阅者模式，并新增跟随逻辑订阅者，解决 `play-football` 执行时精灵仍受 HMD 跟随高度影响的问题。

### 2. 订阅者模式设计

`InteractionActionLockState` 内部维护订阅关系：

```kotlin
private data class ListenerSubscription(
    val listener: InteractionActionListener,
    val actionIds: Set<String>
)
```

订阅规则：

- `actionIds` 为空：监听所有 action。
- `actionIds` 非空：只监听集合内指定 action。
- action 开始时调用 `onActionStarted(actionId, controllerId, source)`。
- action 完成或失败时调用 `onActionFinished(actionId, controllerId, source, status)`。

### 3. 跟随控制模块

新增 `FairyFollowControlModule`：

- 实现 `InteractionActionListener`。
- `onActionStarted()` 中设置 `isFollowEnabled = false`。
- `onActionFinished()` 中设置 `isFollowEnabled = true`。
- 在 `MateFairyRuntimeFactory` 中通过 `InteractionActionRuntimeDependencies.lockState.addListener(FairyFollowControlModule)` 注册。

`FairyBehaviorSystem` 每帧检测：

```kotlin
if (fairyEntity.components[FairyActionLockComponent::class.java] != null ||
    !FairyFollowControlModule.isFollowEnabled
) {
    visualTransform.position = transform.position
    visualTransform.eulerAngles = transform.eulerAngles
    continue
}
```

这意味着 action 期间跟随/随机巡航状态机不会继续计算 HMD 圆环目标，也不会继续覆盖 action 控制器的运动力。

### 4. play-football 高度修复

`PlayFootballActionController` 的接近目标点从：

```kotlin
y = max(footballPosition.y + hoverHeightAboveBall, subjectPosition.y)
```

改为：

```kotlin
y = footballPosition.y + hoverHeightAboveBall
```

设计原因：

- action 目标应由客体实体的真实空间位置决定，而不是继承主体当前高度。
- 当 HMD 高度较高时，主体当前高度会错误地把踢球点抬高。
- 使用足球 Y 坐标 + 小悬停高度可保证精灵移动到足球附近，而不是足球上方。

### 5. 扩展约定

- 后续模块如 UI、音频、日志、特效都可以实现 `InteractionActionListener`，通过订阅 action 生命周期解耦。
- 如果某模块只关心部分 action，使用 `addListener(listener, setOf("action-id"))`。
- action controller 自身只负责动作逻辑，不应直接调用跟随模块；模块间通信通过 action 生命周期事件完成。

---

## Phase 13 技术交接：Action Recovery 与踢球近距离门槛

### 1. 阶段概述

本阶段修复 `play-football` 结束后精灵飞回 action 前旧位置的问题，并新增近距离触发门槛，防止精灵从远处突然飞去踢球。

### 2. Action Recovery 机制

`FairyBehaviorSystem` 新增 `wasFollowEnabled`，用于检测跟随控制从关闭恢复为开启的瞬间：

```kotlin
val followEnabled = FairyFollowControlModule.isFollowEnabled
val followJustRestored = !wasFollowEnabled && followEnabled
```

恢复第一帧调用 `reconcileAfterAction()`：

- 重置 `lastPosition = fairyPos`，避免 action 位移造成速度尖峰。
- 重置 `baseY = fairyPos.y`，让悬浮基准从当前位置开始。
- 清空 `waitTimer/isWaitingForAnimation`。
- 如果精灵与 HMD 水平距离大于 `outerRadius`，进入 `FOLLOWING`，目标为 HMD 跟随内圈随机点。
- 如果仍在跟随范围内，进入 `RANDOM_MOVING`，从当前位置恢复随机运动。

### 3. action 期间状态同步

当 action 锁存在或跟随被关闭时，`FairyBehaviorSystem` 不再写跟随力，但会同步：

```kotlin
behavior.lastPosition = transform.position
behavior.hasRecordedLastPosition = true
```

这样 action 结束后不会因为 `lastPosition` 仍停留在 action 前位置而计算出异常大速度。

### 4. 近距离触发门槛

随机触发侧：

- `tryScheduleRandomPlayFootball(context, fairyPos)` 会先查找 `football`。
- 读取足球 `TransformComponent.position`。
- 计算精灵与足球的 XZ 平面距离。
- 只有距离小于 `RANDOM_PLAY_FOOTBALL_MAX_HORIZONTAL_DISTANCE = 0.85m` 时才允许入队。

action controller 侧：

- `PlayFootballActionController` 在 `DETECTING` 阶段检查 `activationHorizontalDistance`。
- 默认值为 `0.85m`，也可通过 request params 覆盖。
- 如果距离超限，action 返回 `FAILED`，不会进入接近足球流程。

### 5. 行为结果

- 踢完球后如果精灵已经超出跟随范围，会从踢球位置飞回 HMD 跟随区。
- 踢完球后如果仍在跟随范围内，会从当前位置恢复随机运动。
- 精灵不会再快速飞回 action 前旧目标点。
- 足球距离太远时，随机触发和对话触发都不会突然让精灵远距离飞去踢球。

---

## Phase 14 技术交接：Gradle Sync 与空间网格管理恢复

### 1. 阶段概述

本阶段修复项目无法启动的两类阻断问题：一类发生在 Gradle Sync 配置阶段，另一类发生在 Kotlin 编译阶段。修复后 `:app:assembleDebug` 已可成功生成 Debug APK。

### 2. Gradle Sync 阻断点

`/Users/bytedance/MateFairy/editor-asset/build.gradle` 中残留了 Git 冲突标记：

```groovy
<<<<<<< HEAD
}
=======
}
>>>>>>> commit
```

Gradle 解析 Groovy DSL 时会在 `spatial {` 附近报语法错误：

```text
Unexpected input: '{' @ line 27, column 9.
```

修复方式是保留唯一的 `spatial` 闭包：

```groovy
spatial {
    name = "editor-asset"
    spatialToolsVersion = 0.11
}
```

### 3. `SpatialMeshManager` 职责恢复

`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/perception/SpatialMeshManager.kt` 负责把 PICO Mesh Anchor 转成 ECS 静态碰撞实体。

核心字段：

- `parentEntity: Entity?`：环境 Mesh 实体挂载的父节点，当前由 `HomeStage` 传入 `rootEntity`。
- `subscription: Cancellable?`：`MeshTrackingManager.subscribeAnchorUpdate` 返回的订阅句柄。
- `meshEntities: MutableMap<UUID, Entity>`：按 Mesh Anchor UUID 管理实体，便于更新和删除。
- `occlusionMaterial: Material?`：预留遮挡材质引用，当前阶段不渲染 Mesh 模型。

核心流程：

```kotlin
fun start(parentEntity: Entity) {
    this.parentEntity = parentEntity
    if (subscription == null) {
        subscription = MeshTrackingManager.subscribeAnchorUpdate { update ->
            mainHandler.post { handleAnchorUpdate(update) }
        }
    }
    MeshTrackingManager.start()
}
```

Anchor 更新处理：

```kotlin
private fun handleAnchorUpdate(update: AnchorUpdate<MeshAnchor>) {
    when (update.event) {
        AnchorUpdate.Event.ADDED,
        AnchorUpdate.Event.UPDATED,
        AnchorUpdate.Event.LOADED -> upsertMeshEntity(update.anchor)
        AnchorUpdate.Event.REMOVED -> removeMeshEntity(update.anchor.anchorUUID)
    }
}
```

碰撞体生成：

```kotlin
val mesh = MeshResource.loadFromMeshAnchor(anchor.anchorUUID)
val shape = ShapeResource.createStaticMesh(mesh)
val entity = Entity().apply {
    components[TransformComponent::class.java]?.apply {
        setPosition(parent.convertPositionFrom(anchor.transform.position, null))
        setQuaternion(parent.convertRotationFrom(anchor.transform.rotation.toQuat(), null))
    }
    components.set(
        CollisionComponent(
            collisionShape = listOf(shape),
            physicsMaterial = PhysicsMaterialResource(),
            collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
            collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
            collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
        )
    )
}
```

### 4. 行为组件字段约定

`FairyBehaviorSystem` 当前依赖物理代理与视觉模型分离：

- 物理代理：挂载 `FairyBehaviorComponent`、`RigidBodyComponent`、`PhysicsForceComponent`。
- 视觉模型：保存在 `FairyBehaviorComponent.visualEntity`，用于同步机器人 GLB 根层级显示。

`FairyBehaviorComponent` 必须包含：

```kotlin
var visualEntity: Entity? = null
var lastPosition: Vector3 = Vector3.ZERO
var hasRecordedLastPosition: Boolean = false
```

其中 `lastPosition/hasRecordedLastPosition` 用于 action recovery 和速度采样，避免 action 结束后从旧位置计算出异常速度。

### 5. SDK/框架避坑指南

- 合并分支后如果 Android Studio 提示 `Gradle project sync failed`，先运行 `./gradlew projects --stacktrace`，该命令比完整编译更快定位 Gradle 配置阶段错误。
- PICO `MeshAnchor.transform.rotation` 在当前 SDK 中表现为欧拉角，传入 `convertRotationFrom()` 前需要调用 `toQuat()`。
- Mesh Anchor 回调不应直接修改 ECS，应通过 `Handler(Looper.getMainLooper())` 切回主线程。
- `ShapeResource.createStaticMesh(mesh)` 适合真实环境这种静态复杂几何，不要用于动态物体。
- IDE Kotlin 诊断可能在 Gradle Sync 失败后保留旧红线；命令行 `:app:compileDebugKotlin` 和 `:app:assembleDebug` 是更可靠的验证依据。

### 6. 验证命令

```bash
./gradlew projects --stacktrace
./gradlew :app:compileDebugKotlin --console=plain
./gradlew :app:assembleDebug --console=plain
```

---

## Phase 15 技术交接：足球物理材质弹性微调

### 1. 阶段概述

本阶段仅对足球自身的物理材质做小幅手感调整。实测确认足球与空间网格的碰撞、重力和反弹链路正常，因此没有改动空间网格、刚体模式、碰撞形状或 action 速度逻辑。

### 2. 修改位置

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`

方法：`configureFootballPhysics(football: Entity)`

修改后的足球碰撞材质：

```kotlin
physicsMaterial = PhysicsMaterialResource(
    staticFriction = 0.8f,
    dynamicFriction = 0.8f,
    restitution = 0.85f
)
```

### 3. 设计说明

- `restitution` 控制恢复系数，也就是碰撞后的弹性保留比例。
- 本次只把足球恢复系数显式设为 `0.85f`，属于小幅增强，不追求夸张弹跳。
- 摩擦参数保持 `0.8f`，避免足球滚动距离、停球速度和真实环境接触手感发生明显变化。
- 空间网格碰撞体仍保持原有默认物理材质，避免把整个现实环境都调成高弹材质。

### 4. 验证命令

```bash
./gradlew :app:compileDebugKotlin --no-daemon -Dkotlin.compiler.execution.strategy=in-process
```

验证结果：编译通过。

---

## Phase 16 技术交接：玩家手眼模式对象交互链路

### 1. 链路结构

玩家手眼模式下的 3D 对象交互需要同时满足三层条件：

1. **ECS 层**：目标 wrapper 必须具备 `CollisionComponent` 与 `InteractableComponent`。
2. **运行时状态层**：加载后的 wrapper 必须回写到 `HomeStageRuntimeState`，例如 `boomboxEntity`、`rubberDuckEntity`。
3. **Compose 手势层**：`SpatialView.modifier` 必须针对目标实体注册 `pointerInput`。

如果只加载实体但不回写运行时状态，或者只配置碰撞但不注册手势，玩家在手眼模式下都无法触发交互。

### 2. 小黄鸭交互

小黄鸭玩家捏合使用 `detectSpatialTapGesture`：

```kotlin
detectSpatialTapGesture(
    context = context,
    targetedToEntity = TargetEntity.any { entity -> entity == duck }
) {
    applyDuckTapInteraction(duck, runtime)
}
```

动作执行：

```kotlin
playObjectAnimationOnTarget(duck, maxDurationMs = 1500L)
runtime.musicModule.playRandomRubberDuckSfxAt(duck)
```

注意：`rubber_duck_toy.glb` 的动画资源在 GLB 内部节点上，不在外层交互 wrapper 上，所以必须使用 `playObjectAnimationOnTarget()`，不能直接对 wrapper 调用 `getAnimationResources()`。

### 3. 音响交互

音响需要区分长短捏合，因此使用 `detectSpatialPointerEvent` 而不是 `detectSpatialTapGesture`：

```kotlin
event.isDownEvent() -> downTimesByPointerId[pointerId] = event.uptimeMillis
event.isUpEvent() -> {
    val durationMs = event.uptimeMillis - downTime
    applyBoomboxPinchInteraction(boombox, runtime, durationMs)
}
```

阈值规则：

- `durationMs >= 1000L`：长捏合，若音乐正在播放则停止，否则播放下一首并开启音响。
- `durationMs < 1000L`：短捏合，调用 `playNextSpatialMusicAt(boombox)` 切换音乐。

### 4. 音频模块约定

`MusicModule` 需要提供：

- `isSpatialMusicPlaying()`：供音响长捏合判断当前开关状态。
- `setRubberDuckSfxList(listOf("rubber_duck_voice.mp3"))`：运行时初始化时必须注入，否则小黄鸭音效会因为列表为空被跳过。
- `playRandomRubberDuckSfxAt()`：播放后延迟 `1500ms` 停止当前 SFX，避免单次捏合音效过长。

### 5. 回归检查清单

- `HomeStageRuntimeState` 包含 `footballEntity`、`basketballEntity`、`rubberDuckEntity`、`boomboxEntity`。
- `STARTUP_STATIC_ASSETS` 加载完成后按 `objectId` 回写小黄鸭和音响实体。
- `SpatialView` 的 `pointerInput` 同时覆盖足球、篮球、小黄鸭、音响。
- AI 捏鸭与玩家捏鸭都通过 `playObjectAnimationOnTarget()` 播放动画。
- `./gradlew assembleDebug` 必须通过。

---

## Phase 17 技术交接：输入键位表维护约定

### 1. 文档位置

功能键位表维护在：

```text
/Users/bytedance/MateFairy/项目沉淀资料/input_mapping_guide.md
```

该文档面向玩家交互验收，记录手柄、手眼/手势、空间 UI、Debug 面板的触发方式和底层实现。

### 2. 当前模式差异

- **手柄模式**：入口集中在 `InputControllerManager.kt`，只消费右手柄 Trigger；长按 `> 1s` 触发语音，双击 `<= 500ms` 呼出文本输入框。
- **手眼/手势模式**：入口集中在 `HomeStage.kt` 的 `SpatialView.pointerInput` 链；三次拍手触发语音，捏合命中 3D 实体后触发对应交互。
- **空间 UI**：入口集中在 `GameUIContainer.kt`、`FairyDialogueUI.kt`、`DebugActionPanel.kt`，主要负责文本输入、发送、取消和调试 action。

### 3. 维护要求

- 新增手柄键位时，同步更新 `Controller Mode` 表。
- 新增玩家直接操作 3D 实体时，同步更新 `Hand & Gaze Mode` 表。
- 新增 UI 按钮或 Debug 入口时，同步更新 `Spatial UI Interactions` 表。
- 修改长短按阈值、双击阈值、音效/动画时长时，同步更新触发条件和底层实现描述。

---

## Phase 18 技术交接：双捏合文本输入入口

### 1. 设计目标

为眼手/手势模式补齐文本输入框呼出能力，使玩家无需手柄即可打开文本输入面板。

### 2. 交互规则

- 触发方式：连续捏合 2 次。
- 双捏合窗口：`700ms`。
- 捏合判定：同一只手的 `THUMB_TIP` 与 `INDEX_TIP` 距离小于 `0.05m`。
- 释放判定：处于捏合状态后，距离回到 `0.08m` 以上视为释放。
- 保护条件：`InputControllerManager.requestTextInput()` 内部检查 `TextInputProvider.isListening()` 与 `VoiceInputProvider.isListening()`，都为 false 时才允许打开。

### 3. 实现方式

`HomeStage.kt` 在 `HandTrackingProvider.dataFlow` 中同时处理拍手检测与双捏合检测：

```kotlin
handTrackingProvider.dataFlow.collect { trackingData ->
    handClapDetector.processHandTrackingData(trackingData)
    handDoublePinchDetector.processHandTrackingData(trackingData)
}
```

双捏合检测器触发后调用：

```kotlin
inputControllerManager.requestTextInput()
```

`requestTextInput()` 进入 `InputControllerManager.startTextInput()`，因此和手柄右 Trigger 双击共用同一条文本输入链路。

### 4. SDK/框架避坑

- `detectSpatialPointerEvent` 适合命中实体或 UI 的空间指针事件，不保证在完全未命中实体/UI 的空气中稳定产生事件。
- 空气类手势应优先从 `HandTrackingProvider.dataFlow` 读取关节数据，并用状态机判断连续动作。

### 5. 回归检查

- 双捏合应打开文本输入框。
- 手柄右 Trigger 双击仍应打开同一个文本输入框。
- 文本输入框已打开或语音输入中，双捏合不应重复创建新输入会话。

---

## Phase 19 技术交接：双捏合检测器运行时接入修复

### 1. 根因

之前 `HandDoublePinchDetector.kt` 已实现，但 `HomeStage.kt` 没有将检测器接入 `handTrackingProvider.dataFlow`，导致检测器运行时不处理任何手部追踪帧。

### 2. 当前正确接入点

```kotlin
val handDoublePinchDetector = remember(inputControllerManager) {
    HandDoublePinchDetector {
        inputControllerManager.requestTextInput()
    }
}

val handTrackingJob = scope.launch {
    handTrackingProvider.dataFlow.collect { trackingData ->
        handClapDetector.processHandTrackingData(trackingData)
        handDoublePinchDetector.processHandTrackingData(trackingData)
    }
}
```

### 3. 当前阈值

- `PINCH_CLOSE_THRESHOLD = 0.05f`
- `PINCH_OPEN_THRESHOLD = 0.08f`
- `PINCH_COOLDOWN_MS = 220L`
- `DOUBLE_PINCH_TIMEOUT_MS = 700L`

### 4. 维护要求

- 修改 `HandDoublePinchDetector` 后，必须确认 `HomeStage.kt` 仍然调用 `processHandTrackingData()`。
- 不要再恢复 `targetedEntity == null` 的空气 pointer 方案，该方案在真机上不稳定。

---

## Phase 18 技术交接：动画调度接口与运行时装配一致性

### 1. 阶段概述

本阶段修复启动前编译失败。根因是动画调度接口、玩家-精灵交互接口与运行时工厂装配发生漂移：接口已经升级，但 `AnimationModule` 和 `MateFairyRuntimeFactory` 仍停留在旧接线方式。

### 2. 核心接线关系

`AnimationModule` 当前必须同时承担三类职责：

```kotlin
class AnimationModule :
    AnimationController,
    ActionAnimationScheduler,
    PlayerFairyAnimationScheduler,
    InteractionActionListener
```

含义：

- `AnimationController`：给行为系统、AvatarController 和普通动画调用。
- `ActionAnimationScheduler`：给 interaction action 使用，支持 owner action id、生命周期事件与动画时长裁剪。
- `PlayerFairyAnimationScheduler`：给玩家-精灵交互 action 使用，受 `PlayerFairyInteractionRuntimeDependencies.state` 约束。
- `InteractionActionListener`：监听普通 interaction action 开始/结束，暂停或恢复 action 动画通道。

### 3. RuntimeFactory 必须注册的控制器

`MateFairyRuntimeFactory` 中必须显式注入 `animationModule`：

```kotlin
InteractionActionRuntimeDependencies.actionRegistry.register(
    PlayFootballActionController(animationModule)
)

PlayerFairyInteractionRuntimeDependencies.actionRegistry.register(
    PinchShakeAngryPlayerFairyActionController(animationModule)
)
```

构造 `MateFairyRuntime` 时也必须传入玩家-精灵调度器：

```kotlin
MateFairyRuntime(
    conversationOrchestrator = conversationOrchestrator,
    avatarController = avatarController,
    animationModule = animationModule,
    playerFairyInteractionScheduler = PlayerFairyInteractionRuntimeDependencies.scheduler,
    mcpManager = mcpManager,
    musicModule = musicModule
)
```

### 4. 动画生命周期约定

`ActionAnimationScheduler.playActionAnimation()` 当前签名为：

```kotlin
fun playActionAnimation(
    ownerActionId: String,
    animation: FairyAnimation,
    options: ActionAnimationPlayOptions = ActionAnimationPlayOptions()
): Boolean
```

实现要求：

- 必须校验 `InteractionActionRuntimeDependencies.lockState.currentActionId == ownerActionId`。
- `maxDurationSeconds` 不为空时，动画完成计时按裁剪后的时长执行。
- `publishCompletionEvent = true` 时，应发布 STARTED 与 COMPLETED/FAILED 生命周期事件。
- 失败原因使用 `AnimationEndReason.RESOURCE_MISSING`，裁剪完成使用 `AnimationEndReason.CLIPPED`。

### 5. 回归检查清单

- `./gradlew :app:compileDebugKotlin` 必须通过。
- `./gradlew :app:assembleDebug` 必须通过。
- 修改 `ActionAnimationScheduler` 后检查 `AnimationModule` 是否完整实现接口。
- 修改 `MateFairyRuntime` 构造参数后检查 `MateFairyRuntimeFactory` 是否同步传参。
- 修改玩家-精灵交互 action 后检查 `PlayerFairyInteractionRuntimeDependencies.actionRegistry` 是否注册对应 controller。

---

## Phase 19 技术交接：随机踢球临时熔断开关

### 1. 阶段概述

本阶段为 `football-flight-instability` 问题排查期间的临时风控改动：通过注释随机调度入口禁用足球随机交互，避免精灵在普通随机休息行为中自动触发 `play-football`，但完整保留玩家对话命令触发踢球的链路，用于后续可控复现和验证。

### 2. 核心修改点

修改文件：

```text
/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt
```

关键函数：

```kotlin
private fun tryScheduleRandomInteractionAction(context: SceneUpdateContext, fairyPos: Vector3): Boolean {
    val preferDuckFirst = Random.nextBoolean()
    // Temporarily disable random football interaction while preserving dialogue-triggered
    // play-football. Keep the original call sites commented for quick rollback after the
    // football post-action flight instability is fully fixed.
    return if (preferDuckFirst) {
        tryScheduleRandomSqueezeRubberDuck(context, fairyPos)
        // || tryScheduleRandomPlayFootball(context, fairyPos)
    } else {
        // tryScheduleRandomPlayFootball(context, fairyPos) ||
        tryScheduleRandomSqueezeRubberDuck(context, fairyPos)
    }
}
```

### 3. 当前行为链路

1. `scheduleRandomRestBehavior(...)` 仍调用 `tryScheduleRandomInteractionAction(...)`。
2. `tryScheduleRandomInteractionAction(...)` 当前只会尝试 `tryScheduleRandomSqueezeRubberDuck(...)`。
3. `tryScheduleRandomPlayFootball(...)` 保留但不被随机入口调用。
4. `MateFairyRuntimeFactory` 仍通过 `SceneInteractionActionHandler` 注册 `PlayFootballActionController.ACTION_ID`。
5. 玩家对话触发踢球时，仍经过 `ActionRegistryPortAdapter` 和 `PlayFootballPreconditions` 完成距离检查与请求派发。

### 4. 回滚方式

恢复随机踢球时，只需要还原 `tryScheduleRandomInteractionAction(...)` 中被注释的调用点：

```kotlin
return if (preferDuckFirst) {
    tryScheduleRandomSqueezeRubberDuck(context, fairyPos)
        || tryScheduleRandomPlayFootball(context, fairyPos)
} else {
    tryScheduleRandomPlayFootball(context, fairyPos)
        || tryScheduleRandomSqueezeRubberDuck(context, fairyPos)
}
```

### 5. 维护注意事项

- 不要删除 `tryScheduleRandomPlayFootball(...)`，它仍是随机踢球恢复时的最小回滚点。
- 不要移除 `MateFairyRuntimeFactory` 中 `PlayFootballActionController.ACTION_ID` 对应的 `SceneInteractionActionHandler`，否则玩家对话命令会失效。
- 随机踢球恢复前，需要先关闭或解决 `debug-football-flight-instability.md` 中记录的高速飞走、圆周运动、画圈前进、卡墙等问题。
- 恢复随机踢球后，应同步检查小黄鸭随机交互频率，避免足球 action 再次长期占用随机交互入口。

### 6. 验证记录

- 已执行 `./gradlew :app:compileDebugKotlin --rerun-tasks`，结果为 `BUILD SUCCESSFUL`。
- Kotlin daemon 权限异常会自动 fallback 到非 daemon 编译，该现象不是本次改动引入。
- IDE 诊断中存在既有依赖索引误报，本阶段以 Gradle Kotlin 编译通过作为有效验证依据。

---

## Phase 20 技术交接：玩家捏合精灵入口接线

### 1. 阶段概述

本阶段恢复玩家捏合精灵身体的输入链路。故障原因是 `HomeStage` 重构后丢失运行时接线，导致输入层没有命中精灵实体，也没有注册消费玩家交互请求的 ECS System。

### 2. 必须保留的接线点

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - `HomeStageRuntimeState` 必须保留 `fairyBodyEntity: Entity? by mutableStateOf(null)`。
  - Stage 生命周期必须注册 `PlayerFairyInteractionSystem`。
  - 精灵物理代理 `robotBody` 必须挂 `CollisionComponent + InteractableComponent`。
  - 推荐保留 `HoverEffectComponent`，用于交互反馈。
  - `robotBody.apply { ... }` 内必须写回 `runtimeState.fairyBodyEntity = this`。
  - `SpatialView` 必须有 `pointerInput(runtimeState.fairyBodyEntity)`，并通过 `TargetEntity.hit(fairy)` 绑定精灵身体。

### 3. 输入触发流程

1. `robotBody` 作为精灵物理代理和输入命中代理。
2. `robotBody` 挂载 `CollisionComponent` 与 `InteractableComponent`。
3. `runtimeState.fairyBodyEntity` 保存 `robotBody`。
4. Compose `pointerInput(runtimeState.fairyBodyEntity)` 在实体创建后重组。
5. `detectSpatialPointerEvent(... TargetEntity.hit(fairy))` 捕获精灵身体的空间指针事件。
6. up 事件调用 `runtime.playerFairyInteractionScheduler.pinchFairy()`。
7. `PlayerFairyInteractionSystem` 消费请求并执行玩家-精灵交互 action。

### 4. 回归检查清单

- `HomeStage.kt` 中能 grep 到 `fairyBodyEntity`。
- `HomeStage.kt` 中能 grep 到 `registerSystem<PlayerFairyInteractionSystem>()` 和对应 unregister。
- `robotBody` 同时挂有 `InteractableComponent()` 与 `HoverEffectComponent()`。
- `pointerInput(runtimeState.fairyBodyEntity)` 中调用 `runtime.playerFairyInteractionScheduler.pinchFairy()`。
- `./gradlew :app:compileDebugKotlin` 必须通过。

### 5. 维护注意事项

- 后续新增手柄按键、虚拟手碰撞等触发方式时，只应调用 `PlayerFairyInteractionScheduler`，不要绕过统一调度器。
- 后续重构 `HomeStageRuntimeState` 或 `SpatialView.pointerInput` 时，必须同步检查该入口链路。

---

## Phase 21 技术交接：双捏合检测器运行时接入修复

### 1. 根因

`HandDoublePinchDetector.kt` 已实现，但 `HomeStage.kt` 没有将检测器接入 `handTrackingProvider.dataFlow`，导致运行时不处理任何手部追踪帧。

### 2. 当前正确接入点

```kotlin
val handDoublePinchDetector = remember(inputControllerManager) {
    HandDoublePinchDetector {
        inputControllerManager.requestTextInput()
    }
}

val handTrackingJob = scope.launch {
    handTrackingProvider.dataFlow.collect { trackingData ->
        handClapDetector.processHandTrackingData(trackingData)
        handDoublePinchDetector.processHandTrackingData(trackingData)
    }
}
```

### 3. 当前阈值

- `PINCH_CLOSE_THRESHOLD = 0.05f`
- `PINCH_OPEN_THRESHOLD = 0.08f`
- `PINCH_COOLDOWN_MS = 220L`
- `DOUBLE_PINCH_TIMEOUT_MS = 700L`

### 4. 维护要求

- 修改 `HandDoublePinchDetector` 后，必须确认 `HomeStage.kt` 仍然调用 `processHandTrackingData()`。
- 不要再恢复 `targetedEntity == null` 的空气 pointer 方案，该方案在真机上不稳定。

---

## Phase 21 技术交接：虚拟手触碰精灵触发链路

### 1. 阶段概述

本阶段补齐“虚拟手直接触碰精灵身体”触发玩家-精灵交互的链路。此前 `HomeStage` 只依赖 `detectSpatialPointerEvent` 的 pointer up 命中事件，因此手部追踪数据中的直接接触不会触发 `MAD_ACTION`。

### 2. 核心类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/input/HandFairyTouchDetector.kt`
  - 输入：`HandTrackingData`。
  - 依赖：`fairyPositionProvider: () -> Vector3?`。
  - 输出：`onFairyTouched()` 回调。
  - 检测关节：`INDEX_TIP`、`MIDDLE_TIP`、`PALM`。
  - 默认触碰半径：`0.24m`。
  - 默认触发冷却：`1200ms`。

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/playerinteraction/PlayerFairyInteractionCore.kt`
  - `PlayerFairyInteractionScheduler.touchFairy()` 是虚拟手触碰的统一调度入口。
  - 内部仍复用 `PinchShakeAngryPlayerFairyActionController.ACTION_ID`。
  - trigger 使用 `PlayerFairyInteractionTrigger.VIRTUAL_HAND_TOUCH`，便于后续统计或分支表现。

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - `HandFairyTouchDetector` 在 `remember(runtime, runtimeState)` 中创建。
  - `fairyPositionProvider` 从 `runtimeState.fairyBodyEntity` 的 `TransformComponent.position` 读取精灵主体位置。
  - `handTrackingProvider.dataFlow.collect` 中调用 `handFairyTouchDetector.processHandTrackingData(trackingData)`。

### 3. 触发流程

1. `HandTrackingProvider` 输出每帧手部追踪数据。
2. `HandFairyTouchDetector` 读取手指/掌心关节位置。
3. detector 与精灵物理代理位置计算距离。
4. 距离进入 `TOUCH_RADIUS_METERS` 且通过冷却后触发 `onFairyTouched()`。
5. `HomeStage` 调用 `runtime.playerFairyInteractionScheduler.touchFairy()`。
6. `PlayerFairyInteractionSystem` 消费请求，执行“身体晃动 + 生气动画”action。

### 4. 维护注意事项

- 不要在触碰 detector 中直接创建或执行 action；它只能调用统一调度入口。
- 如果触碰过于敏感，调小 `TOUCH_RADIUS_METERS` 或增大 `TOUCH_COOLDOWN_MS`。
- 如果真机手部坐标与精灵坐标不一致，需要优先检查 HandTrackingProvider 输出坐标系和 Stage 坐标系转换。

---

## Phase 22 技术交接：精灵动画资源与轨道索引一致性

### 1. 阶段概述

本阶段修复捏合精灵身体后不播放生气动画的问题。核心原因是运行时加载的精灵 GLB 与 `AnimationConfig` 中的轨道索引配置不一致，导致 `MAD_ACTION(26)` 在旧模型资源中越界。

### 2. 资源差异

- `/Users/bytedance/MateFairy/app/src/main/assets/pico_robot_animated.glb`
  - 动画数量：8。
  - `06_mad_action` 索引：5。

- `/Users/bytedance/MateFairy/app/src/main/assets/pico_robot_animated_new.glb`
  - 动画数量：45。
  - `06_mad_action` 索引：26。
  - 当前 `AnimationConfig` 按此文件配置。

### 3. 当前约定

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - 必须加载 `asset://pico_robot_animated_new.glb`。

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/animation/AnimationConfig.kt`
  - `MAD_ACTION` 保持 `trackIndex = 26`。
  - 其他轨道索引也与 `pico_robot_animated_new.glb` 对齐。

### 4. 排查方法

如果后续出现“action 触发了但动画不播放”，优先检查：

1. 当前 `HomeStage` 加载的 GLB 文件名。
2. `AnimationModule` 初始化日志中的 animation resources 数量。
3. `AnimationConfig` 中目标动画的 `trackIndex` 是否在资源范围内。
4. `AnimationModule.canPlay()` 是否打印 `trackIndex=..., resources=...` 的越界日志。

### 5. 维护注意事项

- 替换精灵 GLB 时，必须同步更新 `AnimationConfig`。
- 不要只改模型文件名而不校验动画轨道索引。

---

## Phase 23 技术交接：玩家朝向驱动足球/篮球捏合施力

### 1. 阶段概述

本阶段修复玩家捏合/点按足球、篮球时施力方向不跟随玩家朝向的问题。核心改动位于 `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`，将球体速度方向从硬编码世界 `-Z` 改为 HMD 本地前方向量转换后的 root 坐标方向。

### 2. 核心流程

1. `SpatialView.pointerInput(runtimeState.footballEntity)` 和 `pointerInput(runtimeState.basketballEntity)` 监听球体命中。
2. `detectSpatialTapGesture` 命中后调用 `applyBallTapImpulse(ball, hmdEntity, rootEntity)`。
3. `playerForwardHorizontalDirection()` 读取 HMD 本地 `Vector3(0f, 0f, -1f)` 前方点，并通过 `convertPositionTo(..., rootEntity)` 转到 root 坐标系。
4. `horizontalDirection()` 将方向投影到 XZ 平面并归一化。
5. `PhysicsVelocityComponent.linearVelocity` 使用该水平 forward 乘以球体水平速度，并保留向上速度。

### 3. 当前实现片段

```kotlin
private fun applyBallTapImpulse(ball: Entity, hmdEntity: Entity, rootEntity: Entity) {
    val forward = playerForwardHorizontalDirection(hmdEntity, rootEntity)
    val velocityComp = ball.components[PhysicsVelocityComponent::class.java]
        ?: PhysicsVelocityComponent().also { ball.components.set(it) }
    velocityComp.linearVelocity = Vector3(
        forward.x * BALL_TAP_FORWARD_SPEED,
        BALL_TAP_UPWARD_SPEED,
        forward.z * BALL_TAP_FORWARD_SPEED
    )
}

private fun playerForwardHorizontalDirection(hmdEntity: Entity, rootEntity: Entity): Vector3 {
    val origin = hmdEntity.convertPositionTo(Vector3.ZERO, rootEntity)
    val forwardPoint = hmdEntity.convertPositionTo(HMD_LOCAL_FORWARD_POINT, rootEntity)
    return horizontalDirection(origin, forwardPoint) ?: DEFAULT_BALL_TAP_FORWARD
}
```

### 4. SDK/坐标系避坑

- Stage/SpatialView 使用右手系，项目中 HUD 面板挂在 `hmdEntity` 的本地 `z = -0.65/-0.85`，因此玩家视线前方采用 `Vector3(0f, 0f, -1f)`。
- 不要直接用固定 `Vector3(0f, 3f, -3f)` 表示玩家前方；它只在玩家默认朝向和世界 `-Z` 重合时正确。
- 不要直接把 HMD pitch 写进球体水平速度；低头/仰头会导致球异常向下或向上飞。当前实现只使用 XZ 水平分量。

### 5. 维护注意事项

- 如果后续增加手柄射线施力，应在 `applyBallTapImpulse` 之前决定输入源方向，并确保方向已转换到 `rootEntity` 坐标系。
- 修改球体速度参数时优先调整 `BALL_TAP_FORWARD_SPEED` 与 `BALL_TAP_UPWARD_SPEED`，不要重新引入硬编码向量。

---

## Phase 24 技术交接：玩家捏合打断小黄鸭交互的物理恢复链路

### 1. 阶段概述

本阶段修复“精灵带着小黄鸭时，玩家捏合精灵后精灵高速飞出或绕玩家高速旋转”的严重问题。核心策略是把玩家-精灵交互作为最高优先级接管层，并把旧 action 取消、跟随物体释放、精灵行为恢复拆成明确的安全阶段。

### 2. 当前安全链路

1. 玩家捏合命中精灵身体后，`PlayerFairyInteractionSystem` 创建 `player-pinch-shake-angry` action。
2. 玩家 action 给精灵主体挂载 `PlayerFairyInteractionComponent`，并通过 `ActionSubjectMotionTemplate.prepare()` 切到 `KINEMATIC + TRIGGER_LITE`。
3. `InteractionActionSystem.handleInterrupts()` 消费 `PLAYER_FAIRY_INTERACTION`，取消所有旧的实体交互 action。
4. 旧小黄鸭 action 的 `cancel()` 只清理 force/velocity 和 action lock；如果精灵已被玩家 action 接管，不再恢复刚体模式。
5. `PickedObjectFollowSystem` 发现 holder 带 `PlayerFairyInteractionComponent` 后释放小黄鸭，将其移到安全偏移并清零线速度/角速度。
6. `FairyBehaviorSystem` 在玩家交互期间只清理运动状态，不施加 PD 行为力。
7. 玩家 action 结束后，`ActionRecoveryGraceComponent` 保持短暂恢复期：精灵仍为 `KINEMATIC + TRIGGER_LITE`，恢复结束帧不施力，下一帧才回到常规跟随/悬停。

### 3. 关键类职责

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionSystem.kt`
  - 负责消费 `InteractionActionInterruptBus`。
  - 玩家交互 active 时不推进旧实体交互 action。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
  - 小黄鸭 carry/use action 的主体恢复逻辑必须尊重 `PlayerFairyInteractionComponent`。
  - cancel 路径必须移除 `FairyActionLockComponent` 并清理角速度。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PickedObjectFollowSystem.kt`
  - 跟随物体释放时必须先脱离 holder 碰撞体，再恢复 `COLLIDER_FULL`。
  - 当前安全释放偏移为 `Vector3(0f, -0.18f, 0.5f)`。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - 玩家交互和恢复保护期间是行为系统禁区，只允许清理 force/velocity。
  - 恢复时必须基于当前 HMD 位置重新生成目标，不能沿用 action 前的旧目标。

### 4. SDK/物理避坑

- PICO Spatial SDK 中 `COLLIDER_FULL` 恢复帧不要同时施加行为力；碰撞体刚恢复时的分离冲量和 PD 控制叠加会放大异常速度。
- action 直接写 `TransformComponent` 时，主体应临时切为 `KINEMATIC` 并清零 `PhysicsVelocityComponent.angularVelocity`，否则残留角速度可能表现为高速旋转。
- 跟随物体不能在 holder 的碰撞体内部恢复动态碰撞；必须先移动到安全位置，再恢复刚体和碰撞响应。
- 旧 action 的 `cancel()` 不应覆盖更高优先级玩家 action 已设置的物理模式，这是玩家交互接管层的关键约束。

### 5. 维护注意事项

- 后续新增“精灵携带/使用物体”的 action，应复用同一套释放和恢复规则：取消时清 force/velocity/action lock，释放物体时清角速度并远离 holder 碰撞体。
- 如果要继续重构，建议把小黄鸭、音响等 carry/use action 的主体运动恢复统一迁移到 `ActionSubjectMotionTemplate`，减少每个 action 手写刚体恢复逻辑。

---

## Phase 25 技术交接：玩家捏鸭命中与动画播放统一链路

### 1. 阶段概述

本阶段修复玩家在眼手模式下捏小黄鸭不触发的问题，并同步收敛 AI 捏鸭动画播放路径。核心原则是：小黄鸭这类 wrapper + GLB 子树资源，玩家命中应按实体树匹配，动画播放应定位到真正持有动画资源的 GLB 内部节点。

### 2. 当前玩家捏鸭流程

1. `HomeStage` 启动时加载 `asset://rubber_duck_toy.glb`。
2. `createStartupStaticAsset()` 创建 wrapper，并挂载 `InteractionObjectComponent(objectId = "rubber_duck_toy")`。
3. `ResourcePhysicsConfigurator.configureRubberDuck()` 给 wrapper 挂载 `CollisionComponent`、`RigidBodyComponent`、`InteractableComponent` 与 `HoverEffectComponent`。
4. `runtimeState.rubberDuckEntity` 保存 wrapper 引用。
5. `SpatialView.pointerInput(runtimeState.rubberDuckEntity)` 监听玩家捏合/点按，并使用 `TargetEntity.hit(duck)` 匹配 wrapper 及其子树。
6. 命中后调用 `applyDuckTapInteraction()`，统一触发动画和音效。

当前核心代码：

```kotlin
detectSpatialTapGesture(
    context = context,
    targetedToEntity = TargetEntity.hit(duck)
) {
    applyDuckTapInteraction(duck, runtime)
}

private fun applyDuckTapInteraction(duck: Entity, runtime: MateFairyRuntime) {
    val animationStarted = playObjectAnimationOnTarget(
        duck,
        maxDurationMs = RUBBER_DUCK_ANIMATION_DURATION_MS
    )
    val sfxName = runtime.musicModule.playRandomRubberDuckSfxAt(duck)
    Log.i(HOME_STAGE_TAG, "Rubber duck pinch: animationStarted=$animationStarted, sfx=$sfxName")
}
```

### 3. AI 捏鸭动画路径

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
- `SqueezeRubberDuckActionController` 当前也使用 `playObjectAnimationOnTarget(duck, maxDurationMs = 1500L)`。
- 不要恢复旧 `playObjectAnimation(duck)`；旧函数只查传入实体自身的动画资源，遇到 wrapper 会跳过真正的 GLB 内部动画节点。

### 4. SDK/交互避坑

- `TargetEntity.any { entity == wrapper }` 只适合命中实体稳定等于 wrapper 的场景。
- 对于 GLB 层级资源、wrapper 包装资源、或可能命中 mesh 子节点的对象，优先用 `TargetEntity.hit(wrapper)`。
- 用户交互必须同时具备 `CollisionComponent + InteractableComponent`；小黄鸭当前由 `ResourcePhysicsConfigurator.configureRubberDuck()` 统一保证。
- 动画 API 必须在主线程调用；玩家和 AI 捏鸭路径当前均在主线程交互/action 更新链路中触发。

### 5. 编译验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 已知环境现象：Kotlin daemon 在本机目录 `/Users/bytedance/Library/Application Support/kotlin/daemon/` 下仍有权限错误，Gradle fallback 到无 daemon 编译后可通过。

---

## Phase 26 技术交接：交互实体索引与动作实例缓存

### 1. 阶段概述

本阶段将交互模块的实体查找从“动作实例逐帧扫场景”收敛为“统一实体索引 + 实例生命周期缓存”。目标是降低 `Scene.queryEntity(...)` 在动作热路径中的重复开销，并让已启动 action 依赖稳定的实体引用。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionCore.kt`
  - `InteractionEntityIndex`：维护 `actors` 与 `objects` 两张索引表。
  - `registerActor(entity)` / `registerObject(entity)`：从 `InteractionActorComponent`、`InteractionObjectComponent` 读取 ID 后注册。
  - `findActor(actorId)` / `findObject(objectId)`：返回缓存实体，并校验实体仍挂有匹配 component；不匹配时移除脏缓存。
  - `InteractionEntityResolver`：兼容旧调用入口，先查 `entityIndex`，未命中才执行原来的 `scene.queryEntity(...)` 并回填索引。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - `SpatialView.initial` 开始时调用 `InteractionActionRuntimeDependencies.entityIndex.clear()`。
  - football、basketball、fairy actor、boombox、rubber duck 在设置 interaction component 后注册到索引。
- 动作实例缓存：
  - `PlayFootballActionInstance` 缓存 `activeSubject`、`activeFootball`。
  - `CarryAndUseObjectActionInstance` 和 `PutDownObjectActionInstance` 缓存 `activeSubject`、`activeTarget`。
  - `StayOnSemanticObjectActionInstance` 缓存 `activeSubject`。
  - `PickedObjectFollowComponent` 缓存 `cachedHolder`。

### 3. 运行流程

1. `HomeStage` 初始化 Stage，清空实体索引，避免复用上一次 Stage 生命周期的实体引用。
2. 场景对象或启动资产完成 component 标记后，立即注册到 `entityIndex`。
3. action 首帧通过 resolver 或索引解析实体，并写入 action 实例字段。
4. 后续正常帧直接复用实例字段或 component 缓存，不再重复扫场景。
5. 如果索引未命中，resolver 仍会回退到场景查询，保证未接入注册的新实体不被直接破坏。

### 4. 设计说明与避坑

- `InteractionEntityResolver` 没有删除，是为了兼容行为系统、玩家交互和未迁移控制器；删除会扩大改动面。
- 索引查找会验证实体 component ID，防止 component 被移除或替换后继续返回脏实体。
- Stage 生命周期开始时必须 `clear()`，因为 PICO Spatial 的 `Entity` 引用属于当前场景树，跨 Stage 生命周期复用会产生脏引用。
- 新增可交互对象时，推荐流程是：先 `components.set(InteractionObjectComponent(...))`，再 `InteractionActionRuntimeDependencies.entityIndex.registerObject(entity)`。

### 5. 编译验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin`
- 结果：`BUILD SUCCESSFUL`
- 已知剩余告警：`FairyAudioModule.kt:147:17 Condition is always 'false'.`

---

## Phase 27 技术交接：`InteractionEntityIndex` 缺失回归修复

### 1. 阶段概述

本阶段修复了 `HomeStage.kt` 已接入运行时实体索引、但 `InteractionActionRuntimeDependencies` 未暴露 `entityIndex` 的代码漂移问题。该问题会导致 `:app:compileDebugKotlin` 失败，从而让 PICO 0.12 模拟器启动应用的链路被构建阶段阻断。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionCore.kt`
  - `InteractionEntityIndex`
    - `actors: MutableMap<String, Entity>`：按 `InteractionActorComponent.actorId` 缓存 action 主体。
    - `objects: MutableMap<String, Entity>`：按 `InteractionObjectComponent.objectId` 缓存 action 客体。
    - `clear()`：Stage 初始化时清空旧生命周期引用。
    - `registerActor(entity)`：读取实体上的 `InteractionActorComponent` 并注册。
    - `registerObject(entity)`：读取实体上的 `InteractionObjectComponent` 并注册。
    - `findActor(actorId)` / `findObject(objectId)`：供 resolver 快速解析实体。
  - `InteractionActionRuntimeDependencies`
    - 新增 `val entityIndex = InteractionEntityIndex()`，与 `actionRegistry`、`requestBus`、`interruptBus`、`lockState` 同级管理。
  - `InteractionEntityResolver`
    - 先走 `InteractionActionRuntimeDependencies.entityIndex`。
    - 索引未命中时回退原有 `scene.queryEntity(...)`，保持对未注册实体的兼容性。

### 3. 运行流程

1. `HomeStage` 的 `SpatialView.initial` 调用 `InteractionActionRuntimeDependencies.entityIndex.clear()`，避免跨 Stage 生命周期持有旧实体。
2. football、basketball、fairy actor、boombox、rubber duck wrapper 等实体设置 interaction component 后调用 `registerActor()` 或 `registerObject()`。
3. action controller 或行为系统通过 `InteractionEntityResolver.findActor/findObject` 解析实体。
4. resolver 优先返回索引缓存；若当前实体未注册，继续使用 ECS Query 回退。

### 4. SDK/框架避坑指南

- PICO Spatial Stage 生命周期内的 `Entity` 引用不应跨 Stage 复用，因此 Stage 初始化入口必须清空运行时索引。
- 新增可交互实体时，顺序必须是先挂载 `InteractionActorComponent` 或 `InteractionObjectComponent`，再注册到 `entityIndex`；否则注册方法会因为缺少 ID 直接忽略。
- 若后续实体会被销毁或替换，应补充 unregister 或脏引用校验，避免索引返回已经脱离场景树的实体。

### 5. 编译验证记录

- 验证命令：`./gradlew :app:assembleDebug --console=plain --stacktrace`
- 结果：`BUILD SUCCESSFUL`
- 已知剩余告警：`FairyAudioModule.kt:147:17 Condition is always 'false'.`

---

## Phase 28 技术交接：球体捏合施力方向防回归

### 1. 阶段概述

本阶段再次修复足球、篮球捏合施力方向从“玩家当前朝向”退回“world 固定 `-Z`”的问题。核心文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`。

### 2. 正确实现约定

足球和篮球手势回调必须传入 HMD 与 root：

```kotlin
applyBallTapImpulse(football, hmdEntity, rootEntity)
applyBallTapImpulse(basketball, hmdEntity, rootEntity)
```

`applyBallTapImpulse` 必须使用 HMD 当前水平前方：

```kotlin
private fun applyBallTapImpulse(ball: Entity, hmdEntity: Entity, rootEntity: Entity) {
    val forward = playerForwardHorizontalDirection(hmdEntity, rootEntity)
    val velocityComp = ball.components[PhysicsVelocityComponent::class.java]
        ?: PhysicsVelocityComponent().also { ball.components.set(it) }
    velocityComp.linearVelocity = Vector3(
        forward.x * BALL_TAP_FORWARD_SPEED,
        BALL_TAP_UPWARD_SPEED,
        forward.z * BALL_TAP_FORWARD_SPEED
    )
}
```

HMD 本地前方必须转换到 root 坐标系：

```kotlin
private fun playerForwardHorizontalDirection(hmdEntity: Entity, rootEntity: Entity): Vector3 {
    val origin = hmdEntity.convertPositionTo(Vector3.ZERO, rootEntity)
    val forwardPoint = hmdEntity.convertPositionTo(HMD_LOCAL_FORWARD_POINT, rootEntity)
    return horizontalDirection(origin, forwardPoint) ?: DEFAULT_BALL_TAP_FORWARD
}
```

### 3. 禁止回归实现

不要恢复以下写法：

```kotlin
private fun applyBallTapImpulse(ball: Entity) {
    val velocityComp = ball.components[PhysicsVelocityComponent::class.java]
        ?: PhysicsVelocityComponent().also { ball.components.set(it) }
    velocityComp.linearVelocity = Vector3(0f, 3.0f, -3.0f)
}
```

原因：它只沿 root/world 固定 `-Z` 方向施力，不随玩家转身变化。

### 4. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin`
- 结果：`BUILD SUCCESSFUL`
- 静态检查：`HomeStage.kt` 中不应出现 `Vector3(0f, 3.0f, -3.0f)`。

---

## Phase 29 技术交接：联网搜索 MCP 接入链路

### 1. 阶段概述

本阶段在既有 MCP 配置模块基础上补齐精灵联网搜索的启用链路。核心目标是：DeepSeek 工具模式可以聚合 MCP Server 暴露的搜索/抓取工具，并在实时信息类问题中优先发起 tool call；未配置真实搜索服务时，应用不会因为占位配置阻塞正常对话。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/config/AppConfig.kt`
  - `parseMcpServers(...)` 支持 `enabled` 字段，`enabled=false` 的 server 不会进入运行时。
  - `containsPlaceholder(...)` 用于跳过占位 URL/Header，例如 `PLEASE_REPLACE_SEARCH_API_KEY`。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/mcp/McpServerConfig.kt`
  - `McpServerConfig.enabled` 表示配置级启停状态。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/mcp/McpClient.kt`
  - `PROTOCOL_VERSION` 使用 `2025-03-26`，适配当前 Streamable HTTP MCP 服务。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ai/DeepSeekLLMProvider.kt`
  - `buildToolEnabledSystemPrompt(...)` 新增联网搜索规则。
  - 当 `McpManager.hasAvailableTools()` 为 true 时，仍走既有 `chatWithTools(...)` function calling 循环。

### 3. 运行流程

1. `AppConfigLoader.load(context)` 读取 `assets/app_config.json`。
2. `parseMcpServers(...)` 跳过禁用项和占位项，生成可用 `McpServerConfig`。
3. `MateFairyRuntimeFactory.create(...)` 创建 `McpManager(appConfig.mcpServers)` 并传入 `LLMProviderFactory.create(...)`。
4. `DeepSeekLLMProvider.chat(...)` 首次对话时调用 `manager.ensureInitialized()`，拉取 `tools/list`。
5. 如果存在 MCP 工具，DeepSeek payload 带 `tools`，模型选择搜索工具后由 `McpManager.callTool(...)` 路由到对应 MCP Server。
6. 工具结果以 `role=tool` 回灌模型，最终仍要求输出 `AIResponse` JSON。

### 4. 配置说明

`/Users/bytedance/MateFairy/app/src/main/assets/app_config.json` 新增 `websearch` 模板：

```json
"websearch": {
  "type": "streamableHttp",
  "url": "http://10.0.2.2:3000/mcp",
  "headers": {
    "X-API-Key": "PLEASE_REPLACE_SEARCH_API_KEY"
  },
  "enabledTools": ["web_search", "ai_search", "web_scrape"]
}
```

启用时必须将 `url` 替换为真机可访问的 Streamable HTTP MCP 地址，并将 Header 中的占位 Key 替换为真实密钥。只要保留 `PLEASE_REPLACE`，该 server 会被配置加载器跳过。

### 5. 避坑指南

- Android 应用不能直接启动 stdio MCP Server，只能连接已经运行的远端或局域网 HTTP MCP Server。
- PICO 真机访问开发机服务不能使用 `localhost`；需要使用开发机局域网 IP，并确认服务监听 `0.0.0.0`。
- 当前 `McpTransportType.SSE` 只是预留枚举；博查等仅提供 SSE 的远端 MCP，需要单独实现 SSE transport 后再接入。

### 6. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin`
- 结果：`BUILD SUCCESSFUL`

---

## Phase 30 技术交接：打开音响 action 与蹦迪动画同步

### 1. 阶段概述

本阶段修复 `start-boombox` 场景交互 action 只启动音响与音乐、但不触发精灵蹦迪动画的问题。修复后，“打开音响/播放音乐”会在精灵使用音响对象时同步调度 `FairyAnimation.DISCO_DANCING_ACTION`。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
  - `StartBoomboxActionController` 构造函数接收 `MusicModule` 与 `ActionAnimationScheduler`。
  - `onUse` 执行顺序：
    1. `playObjectAnimation(boombox)` 播放音响对象动画。
    2. `actionAnimationScheduler.playActionAnimation(...)` 播放精灵蹦迪动画。
    3. `musicModule.playNextSpatialMusicAt(boombox)` 在音响位置播放空间音乐。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/runtime/MateFairyRuntimeFactory.kt`
  - 使用 `AnimationModule` 作为 `ActionAnimationScheduler` 注入 `StartBoomboxActionController`。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/animation/AnimationConfig.kt`
  - `fairyModelAssetUri` 固定指向 `asset://pico_robot_animated_new.glb`，与当前 `FairyAnimation.trackIndex` 配置同源维护。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - 精灵模型加载改为 `Entity.load(AnimationConfig.fairyModelAssetUri)`，禁止再次硬编码旧模型路径。

### 3. 关键代码路径

```kotlin
val scheduled = actionAnimationScheduler.playActionAnimation(
    ownerActionId = actionId,
    animation = FairyAnimation.DISCO_DANCING_ACTION
)
if (!scheduled) {
    Log.w(TAG, "Boombox disco animation was not scheduled for action=$actionId")
}
```

```kotlin
val glbDeferred = async(Dispatchers.IO) {
    Entity.load(AnimationConfig.fairyModelAssetUri)
}
```

### 4. SDK/框架避坑指南

- `start-boombox` 属于 ECS 场景交互 action，不属于通用 `disco` 动画 intent；不能依赖 `GenericAnimationHandler` 自动播放蹦迪动画。
- action 执行期间 `InteractionActionRuntimeDependencies.lockState` 会锁住普通动画入口，action 内部动画必须使用 `ActionAnimationScheduler.playActionAnimation(...)` 并传入当前 `ownerActionId`。
- `AnimationModule.playActionAnimation(...)` 会校验当前 lock 的 `currentActionId`，因此调度必须发生在 action 已被 `InteractionActionSystem` 加锁后的执行阶段。
- 旧 `pico_robot_animated.glb` 只有 8 条 animation，不包含索引 42；当前配置必须加载 `pico_robot_animated_new.glb`，该文件有 45 条 animation，索引 42 是 `11_disco_dancing_action`。

### 5. 验证记录

- 验证命令：`./gradlew :app:assembleDebug`
- 结果：`BUILD SUCCESSFUL`
- 资源核对：`pico_robot_animated_new.glb` 的 animation 42 为 `11_disco_dancing_action`。

---

## Phase 31 技术交接：放下物体 action 的物理恢复缓冲

### 1. 阶段概述

本阶段修复 `stop-boombox`/放下音响后精灵被快速弹飞的问题。核心改动是让通用 `PutDownObjectActionInstance` 使用 `ActionSubjectMotionTemplate` 管理精灵主体的直接控制、碰撞模式与恢复缓冲，避免被刚恢复为动态碰撞体的音响顶开。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
  - `PutDownObjectActionInstance.subjectMotion: ActionSubjectMotionTemplate`
  - 每帧 action 执行时调用 `subjectMotion.prepare(subject)`：
    - 精灵刚体切为 `RigidBodyMode.KINEMATIC`。
    - 精灵碰撞切为 `CollisionResponseMode.TRIGGER_LITE`。
    - 清零 `PhysicsForceComponent` 与 `PhysicsVelocityComponent`。
  - action 完成或取消时调用 `cleanupSubjectMotion(subject)`：
    - 移除 `FairyActionLockComponent`。
    - 调用 `subjectMotion.restore(subject)`。
    - `restore()` 内部添加 `ActionRecoveryGraceComponent`。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ActionSubjectMotionTemplate.kt`
  - `restore(subject)` 会让 `FairyBehaviorSystem.handleActionRecoveryGrace(...)` 接管短暂恢复窗口。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `handleActionRecoveryGrace(...)` 在恢复窗口内持续清零速度与力，并保持精灵碰撞为 `TRIGGER_LITE`，结束后恢复原碰撞模式。

### 3. 关键代码路径

```kotlin
subject.components.set(FairyActionLockComponent(actionId))
activeSubject = subject
subjectMotion.prepare(subject)
```

```kotlin
private fun cleanupSubjectMotion(subject: Entity) {
    subject.components.remove(FairyActionLockComponent::class.java)
    subjectMotion.restore(subject)
    activeSubject = null
}
```

```kotlin
targetTransform.position = Vector3(
    subjectTransform.position.x + forward.x * PUT_DOWN_FORWARD_DISTANCE,
    subjectTransform.position.y + PUT_DOWN_VERTICAL_OFFSET,
    subjectTransform.position.z + forward.z * PUT_DOWN_FORWARD_DISTANCE
)
```

### 4. SDK/框架避坑指南

- 放下物体时，不能只把被放下对象从 `KINEMATIC/TRIGGER_LITE` 恢复为 `DYNAMIC/COLLIDER_FULL`，还必须同步保护 action 主体，否则两个碰撞体的瞬时重叠会被物理解算成冲量。
- 使用 transform 直接驱动精灵的 action，应优先复用 `ActionSubjectMotionTemplate`，并在结束时进入 `ActionRecoveryGraceComponent` 恢复窗口。
- 如果某个 action 会把动态物体放到精灵附近，放下距离需要按精灵胶囊半径和目标物体碰撞盒尺寸留出安全余量。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin`
- 验证命令：`./gradlew :app:assembleDebug`
- 结果：`BUILD SUCCESSFUL`

---

## Phase 33 技术交接：打开音响 Debug 面板移除

### 1. 阶段概述

本阶段移除了用户视野中的音响 action 调试入口。变更只影响 `debug_action_panel` 这条 Compose attachment UI 路径，不修改 `start-boombox` / `stop-boombox` action 本身，也不影响通过语音、文本意图或物体交互触发音响播放逻辑。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - 已移除 `AttachmentPanel(id = "debug_action_panel")`。
  - 已移除 `attachments.entity("debug_action_panel")` 的 HMD 挂载逻辑。
  - 已移除 `debugActionAttachmentEntity` 状态字段。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt`
  - 文件已删除。
  - 原职责是通过按钮直接 enqueue `InteractionActionRequest(source = InteractionActionSource.DEBUG)`。

### 3. 逻辑影响范围

1. Stage 中不再生成 `debug_action_panel` attachment。
2. HMD HUD 子节点不再包含音响 debug 面板。
3. `StartBoomboxActionController`、`StopBoomboxActionController`、`ActionIntentFallbackResolver` 与音响物体 pinch 交互保持不变。

### 4. SDK/框架避坑指南

- PICO Spatial SDK 的 `AttachmentPanel` 只有注册后才能通过 `attachments.entity(id)` 获取对应实体；如果删除 attachment 注册，应同步删除 update 阶段的 `attachments.entity(id)` 挂载逻辑，避免保留无意义的状态字段和查找逻辑。
- 面向用户的 Stage HUD 不应默认挂载临时 debug 面板；需要保留时建议加 build type 或配置开关。

### 5. 验证记录

- 验证命令：`rg -n "DebugActionPanel|debug_action_panel" app/src/main/java -S`
- 结果：无源码引用残留。
- 验证命令：`./gradlew :app:compileDebugKotlin`
- 结果：`BUILD SUCCESSFUL`

---

## Phase 32 技术交接：action 后行为状态强制归零

### 1. 阶段概述

本阶段在物理恢复缓冲之外，增加 action 结束后的行为状态强制归零机制。目标是彻底消除旧 `currentTarget`、旧速度缓存、惯性滑动状态、等待状态或上一帧物理速度导致的 action 后漂移。该策略只保留精灵当前 `Transform.position`，其余行为状态全部重建为 idle。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `resetBehaviorStateAfterAction(...)`
    - action 后统一状态清理入口。
    - 清零物理力、线速度、角速度。
    - 清空 `currentTarget`、惯性、等待动画、浮动计时等行为状态。
    - 保留当前 `transform.position`，并同步 `visualTransform.position`。
    - 设置 `state = FairyState.RANDOM_WAITING`。
    - 设置 `waitTimer = POST_ACTION_IDLE_SECONDS`。
    - 调用 `avatarController?.requestStandbyAnimation()` 回到 idle。
  - `followJustRestored` 分支
    - 收到 action 结束信号后调用 `resetBehaviorStateAfterAction(...)`。
    - 立即 `continue`，避免同一帧继续执行跟随/随机目标派发。
  - `handleActionRecoveryGrace(...)`
    - 如果存在 `ActionRecoveryGraceComponent`，在恢复缓冲内只执行一次强制归零。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ActionSubjectMotionTemplate.kt`
  - `ActionRecoveryGraceComponent.hasResetBehaviorState`
    - 用于标记恢复窗口是否已经执行过行为状态归零，避免每帧重复请求 idle 动画。

### 3. 关键代码路径

```kotlin
if (followJustRestored) {
    resetBehaviorStateAfterAction(
        fairyEntity = fairyEntity,
        behavior = behavior,
        transform = transform,
        visualTransform = visualTransform,
        avatarController = avatarController
    )
    continue
}
```

```kotlin
behavior.state = FairyState.RANDOM_WAITING
behavior.currentTarget = null
behavior.waitTimer = POST_ACTION_IDLE_SECONDS
behavior.isWaitingForAnimation = false
behavior.velocity = Vector3.ZERO
behavior.inertiaVelocity = Vector3.ZERO
behavior.isInertiaSliding = false
behavior.floatTime = 0f
```

### 4. 设计说明

- 该方案故意不再尝试延续 action 前的随机巡航、跟随或悬停状态。
- action 后的唯一可信状态是当前空间位置，因此只保留 `Transform.position`。
- 强制 idle 后，常规行为系统仍会在 idle 缓冲结束后继续随机巡航/跟随等正常逻辑。
- 这是一种兜底策略，优先保证 action 后没有漂移和瞬时飞走。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin`
- 验证命令：`./gradlew :app:assembleDebug`
- 结果：`BUILD SUCCESSFUL`

---

## Phase 34 技术交接：stop-boombox 后软恢复跟随

### 1. 阶段概述

本阶段修复 stop-boombox 放下音响后精灵永久 idle、不再跟随玩家的问题。核心策略是把“防飞锚点”拆成两个阶段：短暂 idle settle 阶段负责压住放下瞬间的物理残留，随后进入脚本控制的软恢复跟随阶段，避免直接回到 `DYNAMIC + PhysicsForce` 后再次被 native physics 速度带飞。

### 2. 核心架构与类说明

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyPostActionIdleAnchorComponent`
  - 只用于 action 后短暂固定当前位置。
  - `remainingSeconds` 到期后不再永久占用行为系统。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyPostActionFollowRecoveryComponent`
  - 新增组件。
  - 表示精灵正在从 action 后 idle 状态恢复跟随。
  - 使用 `movingAnimationRequested` 避免每帧重复请求移动动画。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `applyPostActionIdleAnchor(...)`
    - 每帧清零 force/velocity，保持 `KINEMATIC + TRIGGER_LITE`。
    - 到期后移除 idle anchor，并添加 `FairyPostActionFollowRecoveryComponent`。
  - `applyPostActionFollowRecovery(...)`
    - 保持 `KINEMATIC + TRIGGER_LITE`。
    - 直接写入 `Transform.position`，以 `POST_ACTION_DIRECT_FOLLOW_SPEED` 平滑靠近 HMD 内圈目标。
    - 到达 `innerRadius` 后移除 follow recovery，回到 `RANDOM_WAITING`。
  - action lock 出现时清理两类 post-action 组件，保证新的显式 action 可以立即接管。
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
  - `PUT_DOWN_POST_ACTION_IDLE_ANCHOR_SECONDS = 2.0f` 替代旧的 `-1f` 无限锚点。
  - `start-boombox` 使用 looping action animation 播放蹦迪。
  - `stop-boombox` 显式停止 start-boombox 的 looping animation。

### 3. 关键流程

1. `stop-boombox` 完成放下动作。
2. `ActionSubjectMotionTemplate.restore(...)` 创建 `ActionRecoveryGraceComponent`，并传入 2 秒 post-action idle anchor。
3. `FairyBehaviorSystem` 在 recovery 阶段固定当前位置、清零物理状态。
4. recovery 结束后进入 `FairyPostActionIdleAnchorComponent`，继续短暂 idle 稳定。
5. idle anchor 到期后进入 `FairyPostActionFollowRecoveryComponent`。
6. follow recovery 以 kinematic 直接位移向 HMD 内圈靠近。
7. 到达内圈后移除 recovery，恢复普通随机/跟随行为。

### 4. SDK/框架避坑指南

- PICO Spatial ECS 中，从 `KINEMATIC` 切回 `DYNAMIC` 后 native physics 可能释放残留速度；action 后不要在同一帧直接让普通 `PhysicsForceComponent` 接管。
- 对需要稳定停放的 action，建议先用 `TRIGGER_LITE` 规避动态物体碰撞，再通过脚本位移恢复跟随。
- 动画循环不能只依赖单次 action 动画播放；持续状态应通过独立的 looping action animation 管理，并在关闭动作中显式停止。

### 5. 验证记录

- 验证命令：`./gradlew :app:assembleDebug`
- 结果：`BUILD SUCCESSFUL`

---

## Phase 35 技术交接：FairyBehaviorSystem 主施力安全包络

### 1. 阶段概述

本阶段把放下音响后飞走问题从 action 恢复补丁层面，收敛到正常行为控制器本身。旧 `FairyBehaviorSystem` 在 `FOLLOWING` 中每帧生成新的随机目标，并用高增益 PD 公式输出物理力，导致基准跟随力可达 `22.5N`，反向速度下可接近 `67.5N`。该阶段直接修复施力模型，删除上一轮 post-action 软恢复补丁。

### 2. 核心变更

- `FOLLOWING` 目标稳定化：
  - 仅当 `currentTarget == null`、已到达目标，或目标相对 HMD 明显过期时刷新。
  - 避免每帧目标跳变导致力方向抖动。
- 移动施力安全包络：
  - `MOVEMENT_RESPONSE_GAIN = 4.0f`
  - `MAX_MOVEMENT_FORCE_NEWTONS = 10.0f`
  - `MAX_OUTPUT_FORCE_NEWTONS = 18.0f`
  - `MAX_NATIVE_LINEAR_SPEED_METERS_PER_SECOND = 2.2f`
  - `MAX_ACTUAL_VELOCITY_METERS_PER_SECOND = 2.2f`
  - `MOVEMENT_SLOWDOWN_RADIUS = 0.8f`
- 速度约束：
  - 新增 `clampNativeVelocity(entity)`。
  - 每帧读取 `PhysicsVelocityComponent.linearVelocity`，超过阈值则写回限幅值。
  - 同时清零 `angularVelocity`，避免行为系统造成旋转残留。
- 补丁清理：
  - 删除 `FairyPostActionIdleAnchorComponent`。
  - 删除 `FairyPostActionFollowRecoveryComponent`。
  - 删除 `postActionIdleAnchorSeconds` 传参链路。

### 3. 关键代码语义

```kotlin
val actualVelocity = (
    clampNativeVelocity(fairyEntity) ?: deltaVelocity
).limitMagnitude(MAX_ACTUAL_VELOCITY_METERS_PER_SECOND)
```

```kotlin
val slowedTargetSpeed = targetSpeed *
    (dist / MOVEMENT_SLOWDOWN_RADIUS).coerceIn(0f, 1f)

totalForce = Vector3(
    (desiredVx - actualVelocity.x) * MOVEMENT_RESPONSE_GAIN,
    (desiredVy - actualVelocity.y) * MOVEMENT_RESPONSE_GAIN,
    (desiredVz - actualVelocity.z) * MOVEMENT_RESPONSE_GAIN
).limitMagnitude(MAX_MOVEMENT_FORCE_NEWTONS)
```

### 4. 设计说明

- 根因不是 `stop-boombox` action 本身，而是 action 后正常行为系统重新接管时的移动控制器过强。
- 旧方案试图通过延迟接管或脚本恢复规避问题，但无法解决恢复后的主控制器继续输出高力。
- 新方案直接约束主控制器，保证正常跟随/随机移动阶段不会再产生 67N 级别的输出。
- 保留 `ActionRecoveryGraceComponent` 的短暂清零与碰撞轻量化，因为它解决的是 action 与物理引擎交接时的瞬时残留，不再承担长期行为控制职责。

### 5. 验证记录

- 验证命令：`./gradlew :app:assembleDebug`
- 结果：`BUILD SUCCESSFUL`
- 调试 runId：`post-fix-force-envelope`

---

## Phase 36 技术交接：玩家捏合精灵入口链路恢复

### 1. 阶段概述

本阶段恢复玩家在眼手模式下捏合精灵身体触发“晃动 + 生气动画”的输入链路。该链路属于运行时接线问题，不是 `playerinteraction` action 内部状态机失效：action、scheduler、controller 均存在，但 `HomeStage` 没有把精灵实体命中、手部追踪帧和 `PlayerFairyInteractionSystem` 串起来。

### 2. 核心接线点

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - `HomeStageRuntimeState.fairyBodyEntity` 保存精灵物理代理 `robotBody`。
  - `robotBody` 必须挂载：
    - `CollisionComponent`
    - `InteractableComponent`
    - `HoverEffectComponent`
    - `InteractionActorComponent`
  - `SpatialView.pointerInput(runtimeState.fairyBodyEntity)` 使用 `TargetEntity.hit(fairy)` 绑定精灵身体。
  - pointer up 时调用 `runtime.playerFairyInteractionScheduler.pinchFairy()`。
  - `HandFairyTouchDetector` 读取手部追踪数据，直接触碰时调用 `runtime.playerFairyInteractionScheduler.touchFairy()`。
  - Stage 生命周期必须注册 `PlayerFairyInteractionSystem`。

当前核心代码语义：

```kotlin
registerSystem<PlayerFairyInteractionSystem>()
```

```kotlin
.pointerInput(runtimeState.fairyBodyEntity) {
    val fairy = runtimeState.fairyBodyEntity
    if (fairy != null) {
        detectSpatialPointerEvent(
            context = context,
            targetedToEntity = TargetEntity.hit(fairy)
        ) { events ->
            events.forEach { event ->
                if (event.isUpEvent()) {
                    runtime.playerFairyInteractionScheduler.pinchFairy()
                }
            }
            events.isNotEmpty()
        }
    }
}
```

### 3. 运行时流程

1. `robotBody` 创建完成后写入 `runtimeState.fairyBodyEntity`，触发 Compose 重组。
2. `SpatialView.pointerInput` 对 `TargetEntity.hit(fairy)` 开始监听。
3. 玩家对精灵身体产生空间捏合 up 事件。
4. `HomeStage` 调用 `runtime.playerFairyInteractionScheduler.pinchFairy()`。
5. 请求进入 `PlayerFairyInteractionRuntimeDependencies.requestBus`。
6. `PlayerFairyInteractionSystem` 在 ECS update 中消费请求。
7. `PinchShakeAngryPlayerFairyActionController` 接管精灵，执行晃动并播放 `MAD_ACTION`。

### 4. SDK/框架避坑指南

- PICO Spatial 用户交互命中需要 `CollisionComponent + InteractableComponent`，只有碰撞组件不够。
- 对可交互实体优先使用 `TargetEntity.hit(entity)`，避免 wrapper/子树命中导致严格相等判断失效。
- `PlayerFairyInteractionSystem` 是 request bus 的消费者；只调用 scheduler 但不注册 system，会表现为“日志入队但无动作”。
- 手部追踪检测器必须显式接入 `handTrackingProvider.dataFlow`，仅创建 detector 不会处理任何帧。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 37 技术交接：精灵直接位移驱动 locomotion

### 1. 阶段概述

本阶段将精灵普通跟随、随机巡游、携带物体移动从物理施力模型迁移为脚本直接位移模型。精灵本体是角色代理，不承担真实动态刚体碰撞反作用，因此普通 locomotion 不再通过 `PhysicsForceComponent` 推动，而是由 `FairyBehaviorSystem` 根据目标方向、速度和 `deltaTime` 直接更新 `TransformComponent.position`。

### 2. 核心架构变化

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - 新增/强化 `prepareScriptDrivenFairy(entity)`：
    - 清零 `PhysicsForceComponent.force`
    - 确保 `PhysicsVelocityComponent` 存在并清零线速度/角速度
    - 设置 `RigidBodyMode.KINEMATIC`
    - 设置 `CollisionResponseMode.TRIGGER_LITE`
  - `applyDirectBehaviorMotion(...)`：
    - `RANDOM_MOVING` / `FOLLOWING` 按目标方向移动。
    - `RANDOM_WAITING` / `FOLLOW_HOVERING` 只做受限的上下浮动。
    - 同步物理代理和视觉模型 transform。
  - `applyCarryingDirectMotion(...)`：
    - 携带物体时同样直接位移。
    - 到达目标或处于 waiting/hovering 时重新分配移动目标，保证音响携带期间不会长期站桩。

核心语义：

```kotlin
val direction = direction(current, target)
val step = (speed * dt).coerceAtMost(distanceToTarget)
transform.position = Vector3(
    current.x + direction.x * step,
    current.y + direction.y * step,
    current.z + direction.z * step
)
```

### 3. Action 恢复语义

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ActionSubjectMotionTemplate.kt`
  - `prepare(subject)`：action 脚本接管前设置 `KINEMATIC + TRIGGER_LITE` 并清零运动状态。
  - `restore(subject)`：action 结束后仍保持 `KINEMATIC + TRIGGER_LITE`，只挂载短暂 `ActionRecoveryGraceComponent` 让行为系统完成状态清零。
  - 删除 `hasRestoredDynamicBody` / `dynamicSettleSeconds`，不再做 dynamic settle。

### 4. 设计说明

- 旧 force 模型的问题不是单纯“力太大”，而是方向和强度由 `desiredVelocity - actualVelocity` 推导，容易把 native physics 的历史速度误当成控制输入。
- 直接位移模型的控制量更明确：
  1. 目标由状态机决定。
  2. 方向由 `target - current` 决定。
  3. 位移由 `speed * dt` 决定。
  4. 物理系统不再参与普通 locomotion 的加速度求解。
- 物理仍保留给音响、足球等真实可抛落/碰撞对象；精灵代理只保留轻量 collision 用于命中、hover 和交互。

### 5. SDK/框架避坑指南

- PICO Spatial `RigidBodyMode.DYNAMIC` 适合真实物理对象，不适合作为角色跟随控制器的默认驱动模式。
- `KINEMATIC` 是用户代码驱动运动的正确模式；配合 `TRIGGER_LITE` 可保留命中能力，同时避免物理碰撞解算把精灵弹走。
- action 结束时不要在同一帧切回 `DYNAMIC`，否则 native physics 可能释放残余速度。

### 6. 验证记录

- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 强制重编命令：`./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`，Kotlin daemon 本机权限问题触发 fallback，不影响代码验证。
- 调试 runId：`post-fix-direct-motion`

---

## Phase 38 技术交接：现实网格防穿透运动约束

### 1. 阶段概述

本阶段解决精灵改为直接位移驱动后无法自动被现实 mesh 阻挡的问题。设计上不回退到 `DYNAMIC` 刚体，也不恢复施力移动，而是在 `FairyBehaviorSystem` 写入下一帧位置前执行一次胶囊体 sweep 检测，将运动结果约束在现实网格外侧。

### 2. 现实网格基础

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/perception/SpatialMeshManager.kt`
  - `MeshTrackingManager.subscribeAnchorUpdate(...)` 接收 mesh anchor。
  - `MeshResource.loadFromMeshAnchor(anchor.anchorUUID)` 加载现实网格。
  - `ShapeResource.createStaticMesh(mesh)` 创建静态网格碰撞体。
  - mesh entity 使用 `CollisionResponseMode.COLLIDER_FULL`。
  - manager 实现 `SpatialMeshQuery`，可通过 entity 反查 anchor UUID。

### 3. 精灵运动约束

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `applyDirectBehaviorMotion(...)` 和 `applyCarryingDirectMotion(...)` 都先计算 `desiredNext`。
  - `constrainBySpatialMesh(...)` 对 `current -> desiredNext` 做 `scene.convexCast(...)`。
  - cast 形状使用精灵代理胶囊：
    - `FAIRY_SPATIAL_MESH_CAST_HEIGHT = 0.34f`
    - `FAIRY_SPATIAL_MESH_CAST_RADIUS = 0.14f`
  - 命中过滤条件：
    - 排除精灵自身。
    - 只保留 `SpatialMeshRuntimeDependencies.query.getAnchorUUID(result.entity) != null` 的现实 mesh。
  - 命中后按 `SPATIAL_MESH_SKIN_WIDTH = 0.04f` 留出安全间隔。

核心语义：

```kotlin
val hit = scene.convexCast(
    shape = castShape,
    origin = current,
    orientation = Quat.identity(),
    direction = direction,
    length = moveDistance + SPATIAL_MESH_SKIN_WIDTH,
    hitMode = CollisionCastHitMode.ALL,
    group = CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    referenceEntity = fairyEntity.getParent()
).results
    .filter { SpatialMeshRuntimeDependencies.query.getAnchorUUID(it.entity) != null }
    .minByOrNull { it.distance }
```

### 4. 生命周期接线

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`
  - Stage active 时调用 `SpatialMeshRuntimeDependencies.bind(spatialMeshManager)`。
  - dispose 时调用 `SpatialMeshRuntimeDependencies.clear()`。
  - `spatialMeshManager.start(rootEntity)` 仍负责实际 mesh anchor 订阅和 entity 创建。

### 5. 设计说明

- 直接 transform 位移不会触发 physics solver 对角色位置做阻挡修正；这是 kinematic 角色控制的正常特性。
- 使用 `convexCast` 是更适合角色代理的做法：目标方向、速度和阻挡逻辑都由行为系统显式控制。
- 当前实现优先解决“不穿透”，不做路径规划。若目标在墙另一侧，精灵会停在墙前；后续可基于命中法线做滑动或重新采样目标。

### 6. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 39 技术交接：跟随运动朝向与非线性速度曲线

### 1. 阶段概述

本阶段优化精灵跟随玩家时的运动表现。目标是在 kinematic 直接位移模型下提供更自然的实体感：进入跟随时稍慢、起步有加速、接近目标前有减速，并且在 `FOLLOWING` 状态下让精灵朝向玩家 HMD，而不是朝向随机跟随目标点。

### 2. 核心字段

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorComponent.kt`
  - `motionSpeed: Float`
    - 表示当前 locomotion 标量速度。
    - 区别于 `velocity: Vector3`，`motionSpeed` 用于速度曲线积分，`velocity` 记录上一帧实际位移结果。

### 3. 速度曲线

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `FOLLOWING_SPEED_SCALE = 0.85f`：跟随状态有效速度略低于原 `followSpeed`。
  - `MOVEMENT_ACCELERATION = 1.4f`：起步加速。
  - `MOVEMENT_DECELERATION = 2.1f`：接近目标前更快收速。
  - `MIN_MOTION_SPEED_FACTOR = 0.14f`：避免到达目标前速度过低导致迟迟无法进入到达判定。
  - `smoothStep(...)`：根据距离生成非线性到达减速曲线。

核心语义：

```kotlin
val arrivalFactor = smoothStep(
    (distanceToTarget / DIRECT_MOVEMENT_SLOWDOWN_RADIUS).coerceIn(0f, 1f)
).coerceAtLeast(MIN_MOTION_SPEED_FACTOR)
val desiredSpeed = targetSpeed * arrivalFactor
behavior.motionSpeed = approach(behavior.motionSpeed, desiredSpeed, maxDelta)
```

### 4. 朝向策略

- `FOLLOWING`：
  - 使用 `faceTargetPosition(behavior, from = next, target = hmdPos, ...)`。
  - 精灵在跟随飞行中持续看向玩家 HMD。
- `RANDOM_MOVING`：
  - 使用 `faceDirection(...)`。
  - 精灵随机巡游时仍看向自身运动方向。
- `FOLLOW_HOVERING`：
  - 保持原逻辑，悬停时面向玩家。

### 5. 状态复位

以下状态会清零 `motionSpeed`，保证下一次运动从起步加速开始：

- `RANDOM_WAITING`
- `FOLLOW_HOVERING`
- action recovery grace
- post-action reset
- semantic residence idle
- carrying action resume

### 6. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 40 技术交接：HMD 视野中心跟随目标

### 1. 阶段概述

本阶段修复精灵跟随玩家时终点偏向视野右侧的问题。根因不是速度曲线，也不是 yaw 朝向，而是 `FOLLOWING` 目标点仍然使用玩家周围随机采样；同时旧的悬停切换条件只检查是否进入玩家 `innerRadius`，导致精灵可能尚未到达视野中心就提前停下。

### 2. 核心目标点算法

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
  - `getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)`：
    - 读取 HMD entity 的 `TransformComponent.position`。
    - 读取 HMD entity 的 `TransformComponent.eulerAngles.yaw`。
    - 通过 yaw 计算水平 forward，避免玩家低头/抬头导致精灵上下大幅漂移。
    - 不再在 system update 中调用 `Entity.convertPositionTo(...)`，避免启动初期跨 entity native adapter 坐标转换风险。
    - 目标点为 HMD 正前方 `0.9m`，Y 轴使用 `behavior.hoverHeight`。

核心语义：

```kotlin
val hmdTransform = hmdEntity.components[TransformComponent::class.java]
val hmdOrigin = hmdTransform?.position ?: return behavior.currentTarget ?: Vector3.ZERO
val forward = hmdTransform?.eulerAngles?.yaw
    ?.let(::viewForwardFromYaw)
    ?: DEFAULT_HMD_FORWARD
return Vector3(
    hmdOrigin.x + forward.x * FOLLOW_VIEW_CENTER_DISTANCE,
    hmdOrigin.y + behavior.hoverHeight,
    hmdOrigin.z + forward.z * FOLLOW_VIEW_CENTER_DISTANCE
)
```

### 3. FOLLOWING 状态变更

- 进入 `FOLLOWING`：
  - `currentTarget = getFollowViewCenterTarget(...)`
  - 不再使用 `getRandomTargetInInnerRadius(...)`。
- `FOLLOWING` 更新中：
  - 每帧刷新视野中心目标，使精灵跟随玩家头显朝向变化。
- 结束 `FOLLOWING`：
  - 旧逻辑：`distance2D <= behavior.innerRadius`
  - 新逻辑：`hasReachedTarget(fairyPos, followTarget)`
  - 这样精灵必须真正到达视野中心目标后才会进入 `FOLLOW_HOVERING`。

### 4. 设计说明

- `RANDOM_MOVING` 仍保留随机目标，用于巡游时的自然感。
- `FOLLOWING` 是“回到用户面前”的确定性行为，不应使用随机目标。
- 该方案修正的是世界空间终点，不只是视觉朝向；因此能解决“看起来朝我，但最后停在右侧”的问题。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 41 技术交接：启动期 HMD 目标计算降级

### 1. 阶段概述

本阶段针对“资源已显示，1-2 秒后应用退出”的启动失败线索做静态排查。现象说明 `HomeStage.initial` 资源加载大概率已经成功，问题更可能发生在 ECS system 首次运行后。最近引入的 `getFollowViewCenterTarget(...)` 会在精灵首次进入 `FOLLOWING` 时调用跨 entity `convertPositionTo(...)`，因此成为最可疑风险点。

### 2. 风险点

旧实现：

```kotlin
val referenceEntity = fairyEntity.getParent()
val hmdOrigin = hmdEntity.convertPositionTo(Vector3.ZERO, referenceEntity)
val hmdForwardPoint = hmdEntity.convertPositionTo(HMD_LOCAL_FORWARD_POINT, referenceEntity)
```

风险：

- 依赖 HMD entity 与 fairy parent 均已稳定挂载到 scene。
- 依赖 native adapter 关系已建立。
- 发生在 `FairyBehaviorSystem.update(...)` 中，启动初期一旦进入 `FOLLOWING` 就会触发。

### 3. 新实现

新实现只读取 HMD 的 `TransformComponent`：

```kotlin
val hmdTransform = hmdEntity.components[TransformComponent::class.java]
val hmdOrigin = hmdTransform?.position ?: return behavior.currentTarget ?: Vector3.ZERO
val forward = hmdTransform?.eulerAngles?.yaw
    ?.let(::viewForwardFromYaw)
    ?: DEFAULT_HMD_FORWARD
```

`viewForwardFromYaw(...)`：

```kotlin
private fun viewForwardFromYaw(yawDegrees: Float): Vector3 {
    val radians = yawDegrees * Math.PI.toFloat() / 180f
    return Vector3(-sin(radians), 0f, -cos(radians))
}
```

### 4. 设计说明

- `HomeStage.update` 已经把 HMD tracking pose 转换并写入 `hmdEntity.components[TransformComponent]`。
- 行为系统读取该 transform 比在 system update 中再次做跨 entity 坐标转换更稳定。
- 该修改不改变目标语义：精灵仍飞向 HMD 正前方 `0.9m` 的视野中心点。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 42 技术交接：Spatial Mesh ConvexCast 异常降级

### 1. 阶段概述

本阶段基于设备侧日志修复启动后一段时间退出的问题。日志证明直接崩溃点位于 `FairyBehaviorSystem.constrainBySpatialMesh(...)` 的 `scene.convexCast(...)`，而非 HMD 目标计算。现实 mesh anchor 高频更新时，Spatial SDK 的 cast 结果转换出了 NaN/Inf `Vector3`，导致 `Vector3` 构造器抛出 `IllegalArgumentException`。

### 2. 崩溃证据

关键日志：

```text
FATAL EXCEPTION: main
java.lang.IllegalArgumentException: Vector3 x value cannot be Infinite or NaN
    at com.pico.spatial.core.math.Vector3.<init>
    at com.pico.spatial.core.internal.AdapterConvertersKt.toVector3
    at com.pico.spatial.core.ecs.Scene.convexCast
    at com.example.matefairy01.behavior.FairyBehaviorSystem.constrainBySpatialMesh
```

崩溃前上下文：

```text
SPC-...SingleHitInfo E The object is invalid
Entity RemoveCollisionComponent
Entity RemoveTransformComponent
MeshResource::loadFromMeshAnchorUUID
```

含义：空间 mesh anchor 正在删除旧 collider 并创建新 collider，cast 期间可能遇到无效对象或 SDK 返回异常 hit 数据。

### 3. 修复策略

`constrainBySpatialMesh(...)` 的语义是“防穿透增强”，不是核心 locomotion。它失败时正确降级策略应该是跳过本帧 mesh 约束，而不是让主循环崩溃。

核心修改：

```kotlin
if (!desiredNext.isFiniteVector()) return current
if (!current.isFiniteVector()) return desiredNext

val moveDistance = delta.magnitudeOrZero()
if (!moveDistance.isFinite() || moveDistance <= MIN_SPATIAL_MESH_CAST_DISTANCE) {
    return desiredNext
}

val castResults = runCatching {
    scene.convexCast(...).results
}.getOrElse {
    return desiredNext
}
```

返回结果过滤：

```kotlin
.filter { result ->
    result.entity != fairyEntity &&
        result.distance.isFinite() &&
        result.distance >= 0f &&
        SpatialMeshRuntimeDependencies.query.getAnchorUUID(result.entity) != null
}
```

### 4. 设计说明

- 角色主移动仍保持 kinematic direct motion。
- spatial mesh cast 是环境约束层，必须具备容错能力。
- 当 SDK cast 失败时，宁可本帧不做 mesh 阻挡，也不能让应用退出。
- 后续如需更强稳定性，可以在 `SpatialMeshManager` 中引入 mesh anchor 更新节流或双缓冲，避免同一帧 destroy/recreate collider 与 cast 竞争。

### 5. 验证记录

- 验证命令：`./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过

---

## Phase 43 技术交接：动画朝向策略与 Fairy Yaw 统一写入

### 1. 阶段概述

本阶段修复“精灵播放动画时固定朝向一个方向”的表现问题。修复思路是保持动画模块只负责播放资源，行为系统统一负责空间朝向。通过新增动画朝向策略，`FairyBehaviorSystem` 可以知道当前动画是否需要面向玩家，并把同一个 yaw 同步到物理代理 body 与 visual wrapper。

### 2. 核心架构与类说明

- `AnimationFacingPolicy`
  - 定义位置：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/animation/AnimationController.kt`
  - 枚举值：
    - `KEEP_BEHAVIOR`：沿用行为系统原有移动/跟随朝向。
    - `FACE_PLAYER`：播放期间面向 HMD。
    - `KEEP_ACTION_TARGET`：任务动作保留 action controller 写入的目标朝向。
- `AnimationController.currentFacingPolicy`
  - 由 `AnimationModule` 实现。
  - 普通 idle、情绪、非任务指令动画映射为 `FACE_PLAYER`。
  - `BASE_MOVING` 映射为 `KEEP_BEHAVIOR`。
  - `playActionAnimation(...)` 与 looping action 映射为 `KEEP_ACTION_TARGET`。
  - 玩家-精灵交互动画映射为 `FACE_PLAYER`。
- `AvatarController.currentFacingPolicy`
  - 由 `DefaultAvatarController` 从 `AnimationController` 透传。
  - `FairyBehaviorSystem` 通过 `BehaviorRuntimeDependencies.avatarController` 读取该策略。
- `FairyBehaviorSystem.applyFairyYaw(...)`
  - 统一把 `behavior.currentYaw` 写入：
    - 精灵物理代理 `transform.eulerAngles.yaw`
    - 视觉模型 `visualTransform.eulerAngles.yaw`
  - visual 的 pitch/roll 继续使用模型初始姿态，避免破坏 GLB 站立姿态。

### 3. 运行流程

1. 上层触发动画播放，例如情绪动画、普通指令动画、随机 idle 动画。
2. `AnimationModule.executePlay(...)` 启动 skinned mesh animation，同时设置 `activeFacingPolicy`。
3. `FairyBehaviorSystem.update(...)` 每帧读取 `avatarController.currentFacingPolicy`。
4. 如果策略是 `FACE_PLAYER`，行为系统用精灵当前位置和 HMD 位置计算目标 yaw，并用 `lerpAngle(...)` 平滑转向。
5. 行为系统调用 `applyFairyYaw(...)`，同步 body 与 visual 的 yaw。
6. 如果策略是 `KEEP_ACTION_TARGET`，行为系统不抢任务 action 的朝向，只同步 action 写入的 body yaw。

### 4. 关键代码片段

```kotlin
enum class AnimationFacingPolicy {
    KEEP_BEHAVIOR,
    FACE_PLAYER,
    KEEP_ACTION_TARGET
}
```

```kotlin
private fun FairyAnimation.defaultFacingPolicy(): AnimationFacingPolicy {
    return when (type) {
        AnimationType.BASE_MOVING -> AnimationFacingPolicy.KEEP_BEHAVIOR
        AnimationType.BASE_IDLE,
        AnimationType.EMOTION_REACTION,
        AnimationType.NON_TASK_ACTION -> AnimationFacingPolicy.FACE_PLAYER
    }
}
```

```kotlin
private fun applyFairyYaw(
    transform: TransformComponent,
    visualTransform: TransformComponent,
    behavior: FairyBehaviorComponent
) {
    val bodyEuler = transform.eulerAngles
    transform.eulerAngles = EulerAngles(
        pitch = bodyEuler.pitch,
        yaw = behavior.currentYaw,
        roll = bodyEuler.roll
    )

    val visualPitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
    val visualRoll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
    visualTransform.eulerAngles = EulerAngles(
        pitch = visualPitch,
        yaw = behavior.currentYaw,
        roll = visualRoll
    )
}
```

### 5. SDK/框架避坑指南

- 不建议直接给精灵 visual wrapper 加 `LookAtComponent`。当前行为系统已经手写 `TransformComponent.eulerAngles`，再叠加 LookAt 会产生控制权竞争。
- skinned mesh animation 不等于实体朝向控制。`playAnimation(...)` 只驱动骨骼/动画轨道，空间 yaw 仍应由父级 wrapper 或行为系统控制。
- body 与 visual 分离后，必须保持 yaw 同步。否则 action lock 或 player interaction 分支会把 visual 拉回 body 的旧朝向。
- 任务动作需要保留目标朝向，例如踢球必须面向足球，因此不能把所有动画一刀切成 `FACE_PLAYER`。

### 6. 验证记录

- 验证命令：`./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 验证命令：`./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 已知无关日志：Kotlin daemon 权限失败后 fallback 到无 daemon 编译成功；`FairyAudioModule.kt` 存在既有条件恒 false warning。

---

## Phase 44 技术交接：SOUL.md 结构化人设与精灵属性设置 UI

### 1. 阶段概述

本阶段新增用户可编辑的精灵属性设置能力。核心目标是让用户通过空间 UI 修改精灵名称、精灵对用户的称呼以及五大性格属性，并把结果以规范结构写入现有 `SOUL.md`。主对话链路仍通过 `ContextMemorySystem.buildPromptMessages(...)` 每轮读取 `PermanentStore.readSoul()` 注入 system prompt，因此本阶段没有改变 LLM 调用入口和记忆召回顺序。

### 2. 核心架构与类说明

- `FairySoulProfile`
  - 定义位置：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/persona/FairySoulProfile.kt`
  - 字段：
    - `fairyName`：精灵名称。
    - `userAddress`：精灵对用户的称呼。
    - `personality`：五大性格选择，类型为 `PersonalitySelections`。
  - `normalized()` 会裁剪空白并对空字段回退默认值。
  - `toPromptMarkdown()` 会生成可直接注入 LLM 的人设说明。
- `FairyPersonalityCatalog`
  - 定义五大性格维度和每个维度的四档选项。
  - UI 展示和 `SOUL.md` prompt 文本都使用同一份 catalog，避免文案分叉。
- `FairySoulProfileMarkdownCodec`
  - `toMarkdown(profile, previousMarkdown)`：生成 `SOUL.md` 内容。
  - `decode(markdown)`：从 `SOUL.md` 的 `<!-- matefairy:soul-profile:v1 -->` 标记块回读结构化 profile。
  - 如果旧版 `SOUL.md` 没有结构化块，会回退默认 profile；保存时会把旧内容保留在结构化块之后，结构化块优先级更高。
- `PermanentStore`
  - 新增 `readSoulProfile()` 与 `writeSoulProfile(...)`。
  - 仍写入 `MdTemplates.FILE_SOUL`，即 `SOUL.md`。
  - 继续复用原有 `writeSafe(...)` 的备份和原子写策略。
- `FairySettingsProvider`
  - 定义位置：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/FairySettingsProvider.kt`
  - 只保存 UI 状态：当前 profile、草稿 profile、面板显隐、性格展开状态、保存状态。
  - 不做文件 IO，避免破坏 runtime 依赖边界。
- `FairySettingsUI`
  - 定义位置：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/FairySettingsUI.kt`
  - `SettingsEntryButton(...)`：右下角入口按钮。
  - `SettingsPanel(...)`：毛玻璃设置面板。
  - `PersonalityExpandedContent(...)`：性格模块展开后占满面板主体区域。
- `HomeStage`
  - 通过 `LaunchedEffect(runtime)` 从 `runtime.permanentStore.readSoulProfile()` 初始化设置状态。
  - 通过 `AttachmentPanel(id = "fairy_settings_button")` 和 `AttachmentPanel(id = "fairy_settings_panel")` 渲染空间 UI。
  - 在 `SpatialView.update` 中把两个 attachment entity 挂到 `hmdEntity` 下，使用 HMD 局部坐标固定在用户视野前方。
- `DeepSeekLLMProvider`
  - 新增 `buildSoulInstructionGuard()`。
  - 工具模式和非工具结构化模式都会插入该规则，确保 `SOUL.md` 不能覆盖 JSON 协议和 action intent 路由。

### 3. 运行流程

1. App 启动后 `PermanentStore.ensureInitialized()` 保证 `SOUL.md` 存在。
2. `HomeStage` 首次进入时读取 `readSoulProfile()`，写入 `FairySettingsProvider.currentProfile`。
3. 用户点击 HMD 右下角“设置精灵属性”按钮。
4. `FairySettingsProvider.openPanel()` 把当前 profile 复制到草稿。
5. 用户修改名称、称呼或五大性格等级。
6. 用户点击保存，`HomeStage.saveFairySoulProfile(...)` 在 IO 线程调用 `PermanentStore.writeSoulProfile(...)`。
7. 下一轮用户输入触发 `ConversationOrchestrator.processUserInput(...)`。
8. `ContextMemorySystem.buildPromptMessages(...)` 读取最新 `SOUL.md` 并拼到基础 system prompt。
9. `DeepSeekLLMProvider` 在外层协议中加入 SOUL 守卫规则，模型只把人设用于 `reply_text` 表达，不破坏 JSON/action 协议。

### 4. 设计说明

- 不迁移 `SOUL.md` 文件名：当前 L4 永久人设注入链路已经固定读取 `MdTemplates.FILE_SOUL`，保持大写文件名可以避免和现有上下文、ADB 调试习惯、清记忆逻辑发生分叉。
- 结构化 JSON 与 Markdown 并存：JSON 负责 UI 可回读，Markdown 负责 LLM 可读性。这样比只写自然语言更稳定，也比只写 JSON 更容易让模型遵守。
- 设置 UI 不直接依赖 `PermanentStore`：`FairySettingsProvider` 只做状态，保存动作由 `HomeStage` 持有 runtime 后执行，符合现有 Stage/运行时边界。
- prompt 守卫放在 provider 层：`ContextMemorySystem` 继续负责组装记忆，`DeepSeekLLMProvider` 继续负责最终输出协议，避免把 JSON/action 协议散落到 `SOUL.md` 或 UI 模块。

### 5. SDK/框架避坑指南

- 视野跟随 UI 继续使用 Stage attachment，而不是 `WindowContainer`。本项目现有输入框也是通过 `AttachmentPanel` 挂到 HMD 子节点实现视野跟随，复用同一路径可以避免 Shared Space / Full Space 窗口行为差异。
- attachment entity 需要在 `SpatialView.update` 中保证挂载到渲染树。只在 `attachments {}` 中声明 Compose 内容，不代表 3D 场景里已经有可见 entity。
- 不要把 SOUL 个性化规则拼到结构化协议最后。最终 JSON 协议、工具路由和 action 枚举必须保留最高优先级，否则容易导致模型输出非 JSON 或 action_intent 失效。

### 6. 验证记录

- 验证命令：`./gradlew :app:assembleDebug :app:testDebugUnitTest --tests "com.example.matefairy01.persona.FairySoulProfileMarkdownCodecTest" --tests "com.example.matefairy01.memory.permanent.PermanentStoreSoulProfileTest" --no-daemon -Dkotlin.compiler.execution.strategy=in-process`
- 结果：`BUILD SUCCESSFUL`
- 格式检查：`git diff --check` 通过
- 已知无关问题：全量 `:app:testDebugUnitTest` 仍会执行既有 Dump 反射测试和 WebSearch live 测试，其中部分失败与本阶段改动无关。
