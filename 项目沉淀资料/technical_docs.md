# MateFairy01 技术文档总览

> 本文档汇总了项目各阶段的技术交接与架构设计文档，用于帮助后续开发人员或 Agent 快速理解项目技术细节与接口约定。
> 最后更新：2026-06-05

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
