# MateFairy01 技术文档总览

> 本文档汇总了项目各阶段的技术交接与架构设计文档，用于帮助后续开发人员或 Agent 快速理解项目技术细节与接口约定。
> 最后更新：2026-06-06

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

## Phase 16 技术交接：篮球资源接入与球类物理复用

### 1. 阶段概述

本阶段将 Editor 场景中的 `Basketball` 按照 `Football` 的资源处理方式接入运行时，包括交互对象标记、动态刚体、连续碰撞、空间网格就绪后启用重力，以及点击测试弹飞能力。

### 2. 核心流程

`HomeStage.initial` 中新增篮球接入流程：

```kotlin
val basketball = sceneModel.findEntity("Basketball")
runtimeState.basketballEntity = basketball
basketball?.let {
    it.components.set(
        InteractionObjectComponent(
            objectId = "basketball",
            tags = setOf("sports", "physics")
        )
    )
    configureSportsBallPhysics(it, BASKETBALL_COLLIDER_RADIUS)
}
```

### 3. 球类物理配置复用

原 `configureFootballPhysics()` 已泛化为：

```kotlin
private fun configureSportsBallPhysics(ball: Entity, colliderRadius: Float)
```

该函数统一负责：

- 复用已有 `CollisionComponent.collisionShape`，如果没有则创建球形碰撞体。
- 设置 `PhysicsMaterialResource(staticFriction = 0.8f, dynamicFriction = 0.8f, restitution = 0.85f)`。
- 设置 `CollisionResponseMode.COLLIDER_FULL` 和默认碰撞过滤组。
- 设置 `RigidBodyMode.DYNAMIC`。
- 初始 `isAffectedByGravity = false`，等待空间网格地面检测完成。
- 设置 `CollisionDetectionMode.CONTINUOUS`，减少快速运动时穿透空间 mesh 的概率。
- 挂载 `FootballPhysicsActivationComponent(radius = colliderRadius)`，复用现有激活系统。

### 4. 点击测试弹飞

新增：

```kotlin
private fun applyBallTapImpulse(ball: Entity)
```

足球和篮球分别有独立 `pointerInput` 目标过滤：

- 点击 `Football` 只给足球设置 `PhysicsVelocityComponent.linearVelocity`。
- 点击 `Basketball` 只给篮球设置 `PhysicsVelocityComponent.linearVelocity`。

### 5. 当前约束

- `FootballPhysicsActivationComponent` 名称仍保留历史命名，但现在实际作为通用球类物理激活组件使用。后续可在一次小重构中改名为 `SportsBallPhysicsActivationComponent`。
- 篮球目前只完成资源/物理/交互对象接入，还没有新增专属 action；如果后续需要“精灵投篮/拍球”等动作，可基于 `objectId = "basketball"` 查询客体。

---

## Phase 17 技术交接：ResourcePhysicsConfigurator 资源物理配置中心

### 1. 阶段概述

本阶段将足球和篮球的物理配置从 `HomeStage.kt` 中抽离，新增统一配置中心 `ResourcePhysicsConfigurator`。设计目标是“统一入口、资源独立配置、场景装配层保持轻量”。

### 2. 配置中心结构

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/ResourcePhysicsConfigurator.kt`

核心入口：

```kotlin
object ResourcePhysicsConfigurator {
    fun configureFootball(football: Entity)
    fun configureBasketball(basketball: Entity)
}
```

设计约定：

- 每个资源都有独立 public 配置函数。
- 资源参数不共享常量，避免调篮球影响足球。
- 公共装配步骤可通过 private helper 复用，但对外仍保持资源独立语义。

### 3. 当前资源配置

足球：

- `colliderRadius = 0.11f`
- `staticFriction = 0.8f`
- `dynamicFriction = 0.8f`
- `restitution = 0.85f`
- `linearDamping = 0.2f`
- `angularDamping = 0.2f`

篮球：

- `colliderRadius = 0.12f`
- `staticFriction = 0.7f`
- `dynamicFriction = 0.7f`
- `restitution = 0.9f`
- `linearDamping = 0.18f`
- `angularDamping = 0.18f`

### 4. HomeStage 调用方式

`HomeStage.kt` 现在只负责资源查找与装配：

```kotlin
val football = sceneModel.findEntity("Football")
football?.let {
    it.components.set(InteractionObjectComponent(objectId = "football", tags = setOf("sports", "physics")))
    ResourcePhysicsConfigurator.configureFootball(it)
}

val basketball = sceneModel.findEntity("Basketball")
basketball?.let {
    it.components.set(InteractionObjectComponent(objectId = "basketball", tags = setOf("sports", "physics")))
    ResourcePhysicsConfigurator.configureBasketball(it)
}
```

### 5. 通用物理激活

原足球专用激活命名已泛化：

- `ResourcePhysicsActivationComponent`
- `ResourcePhysicsActivationSystem`

作用：

1. 资源初始设置 `RigidBodyComponent.isAffectedByGravity = false`。
2. 系统等待一段最小时间并向下 raycast。
3. 检测到空间 mesh 地面后，将资源高度修正到安全位置。
4. 开启重力并标记 `activated = true`。

### 6. 扩展规则

- 新增资源不应把物理参数写在 `HomeStage.kt`。
- 新增资源应在 `ResourcePhysicsConfigurator` 中新增独立 `configureXxx()`。
- 如果某资源不需要等待空间 mesh，可不挂载 `ResourcePhysicsActivationComponent`。
- 如果某资源不是球体，应在独立配置函数中选择合适的 `ShapeResource`，不要复用球体 helper。

---

## Phase 18 技术交接：篮球运行时交互与物理激活修复

### 1. 根因总结

篮球和足球的 Editor 场景组件不一致。`Football` 在 `MyScene.usda` 中自带多种组件，而 `Basketball` 只有：

```usda
def "Basketball" (
    prepend references = @../Assets/Basketball.usdz@
)
{
    quatf xformOp:orient = (0.7071068, -0.7071067, 0, 0)
    float3 xformOp:scale = (1, 0.99999994, 0.99999994)
    float3 xformOp:translate = (-0.74734396, 0, 0.11477584)
}
```

因此仅查找 `Basketball` 并设置刚体是不够的，运行时必须补齐交互组件。

### 2. ResourcePhysicsConfigurator 修复

`configureDynamicBall()` 现在除 `CollisionComponent`、`RigidBodyComponent` 外，还会补齐：

```kotlin
if (ball.components[PhysicsForceComponent::class.java] == null) {
    ball.components.set(PhysicsForceComponent())
}
if (ball.components[InteractableComponent::class.java] == null) {
    ball.components.set(InteractableComponent())
}
if (ball.components[HoverEffectComponent::class.java] == null) {
    ball.components.set(HoverEffectComponent())
}
```

交互规则：

- `CollisionComponent` 提供命中/物理范围。
- `InteractableComponent` 是 3D 实体接收用户交互事件的必要开关。
- `HoverEffectComponent` 提供 gaze/hand ray 悬停反馈。
- `PhysicsForceComponent` 用于对齐足球 Editor 侧已有能力，保证后续力/速度链路一致。

### 3. ResourcePhysicsActivationSystem 兜底

`ResourcePhysicsActivationComponent` 新增：

```kotlin
val fallbackEnableGravitySeconds: Float = 6f
```

逻辑：

1. 优先等待空间 mesh raycast 命中地面。
2. 命中后修正资源高度，并开启重力。
3. 如果超过兜底时间仍未命中，也会开启重力，避免资源永久悬空。

### 4. Compose 状态修复

`HomeStageRuntimeState` 中的球体引用改为 Compose state：

```kotlin
var footballEntity: Entity? by mutableStateOf(null)
var basketballEntity: Entity? by mutableStateOf(null)
```

原因：

- `SpatialView.initial` 是加载完成后才给引用赋值。
- 普通 `var` 赋值不会触发重组。
- `pointerInput(runtimeState.basketballEntity)` 需要重组后才能拿到非空篮球实体并安装手势监听。

### 5. 新资源接入检查清单

- 资源是否能通过 `sceneModel.findEntity("ResourceName")` 查到。
- 是否在 `ResourcePhysicsConfigurator` 中有独立 `configureXxx()`。
- 是否显式设置 `CollisionComponent`。
- 如果需要用户交互，是否显式设置 `InteractableComponent`。
- 如果需要 hover 反馈，是否设置 `HoverEffectComponent`。
- 如果需要物理受力，是否设置 `RigidBodyComponent` 和必要的 force/velocity 组件。
- 如果引用会影响 Compose 手势监听，是否使用 `mutableStateOf`。

---

## Phase 19 技术交接：启动静态 GLB 资产加载

### 1. 阶段概述

本阶段将两个新增 GLB 资产直接接入 Stage 游戏场景：`boombox.glb` 与 `rubber_duck_toy.glb`。它们作为静态展示物在应用启动后加载并挂载到 `rootEntity`，用户进入游戏场景即可看到。

### 2. 资源文件

- `/Users/bytedance/MateFairy/app/src/main/assets/boombox.glb`
- `/Users/bytedance/MateFairy/app/src/main/assets/rubber_duck_toy.glb`

`/Users/bytedance/MateFairy/app/build.gradle.kts` 已包含：

```kotlin
androidResources {
    noCompress += listOf("bundle", "glb", "usdz", "wav")
}
```

这保证 GLB 不会被压缩破坏运行时直接读取路径。

### 3. 启动加载流程

`HomeStage.initial` 中加载 Editor Bundle、MateFairy 机器人 GLB 和新增静态 GLB：

```kotlin
val (bundle, glbRoot, staticAssetRoots) = coroutineScope {
    val bundleDeferred = async(Dispatchers.IO) {
        AssetBundle.load("asset://editor-asset.bundle")
    }
    val glbDeferred = async(Dispatchers.IO) {
        Entity.load("asset://pico_robot_animated.glb")
    }
    val staticAssetDeferreds = STARTUP_STATIC_ASSETS.map { spec ->
        async {
            spec to Entity.loadSuspend(spec.assetUri)
        }
    }
    Triple(bundleDeferred.await(), glbDeferred.await(), staticAssetDeferreds.awaitAll())
}
```

挂载流程：

```kotlin
staticAssetRoots.forEach { (spec, assetRoot) ->
    rootEntity.addChild(createStartupStaticAsset(spec, assetRoot))
}
```

### 4. 静态资产规格

当前集中配置在 `HomeStage.kt`：

```kotlin
private val STARTUP_STATIC_ASSETS = listOf(
    StartupStaticAssetSpec(
        assetUri = "asset://boombox.glb",
        position = Vector3(-0.45f, 0.05f, -1.55f),
        scale = 0.45f
    ),
    StartupStaticAssetSpec(
        assetUri = "asset://rubber_duck_toy.glb",
        position = Vector3(0.45f, 0.05f, -1.45f),
        scale = 0.9f
    )
)
```

坐标说明：

- Stage 坐标使用米，+Y 向上。
- PICO Stage 中 +Z 朝向用户，因此放在用户前方时使用负 Z。
- 两个资产分别放在前方左右两侧，避免遮挡 MateFairy 跟随精灵。

### 5. Wrapper 实体设计

创建 wrapper 而不是直接修改 GLB 根实体：

```kotlin
private fun createStartupStaticAsset(spec: StartupStaticAssetSpec, assetRoot: Entity): Entity {
    return Entity().apply {
        components[TransformComponent::class.java] = TransformComponent().apply {
            setPosition(spec.position)
            scaleVector = Vector3(spec.scale, spec.scale, spec.scale)
        }
        addChild(assetRoot)
    }
}
```

设计原因：

- GLB 加载返回的是完整层级根实体，mesh-bearing 子节点自动携带 `ModelComponent`。
- 使用外层 wrapper 做摆放，可保留 GLB 内部 Transform、材质和子节点结构。
- 后续如果要给资产补交互/物理，可把展示 Transform、碰撞代理和视觉模型继续分离。

### 6. SDK/框架避坑指南

- GLB 是 PICO Spatial SDK 支持的 glTF 二进制格式；如果资产管线可控，USD/USDA 仍是更推荐的空间编辑格式。
- `Entity.loadSuspend("asset://xxx.glb")` 适合在 `SpatialView.initial` 中做一次性异步加载。
- 不要在 `SpatialView.update` 中加载模型；`update` 会频繁执行，重复加载会造成卡顿和资源泄漏。
- 加载成功后应把返回的根 `Entity` 挂到场景树中，而不是只操作某个 mesh 子节点。

### 7. 验证命令

```bash
./gradlew :app:compileDebugKotlin
```

验证结果：编译通过。

---

## Phase 21 技术交接：boombox_new 替换原 boombox

### 1. 阶段概述

本阶段将启动场景中的 boombox 视觉资源从旧文件 `boombox.glb` 切换为新文件 `boombox_new.glb`。运行时对象仍保持 `objectId = "boombox"`，因此 AI 语义、交互标签、物理配置和空间 mesh 交互逻辑不需要迁移。

### 2. 资源路径

新资源：

```text
/Users/bytedance/MateFairy/app/src/main/assets/boombox_new.glb
```

运行时加载 URI：

```kotlin
StartupStaticAssetSpec(
    assetUri = "asset://boombox_new.glb",
    objectId = "boombox",
    position = Vector3(-0.45f, 0.3f, -1.55f),
    visualScale = 0.45f,
    floorOffset = 0.11f,
    configurePhysics = ResourcePhysicsConfigurator::configureBoombox
)
```

### 3. 替换范围

修改文件：

```text
/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt
```

修改内容：

- `asset://boombox.glb` 替换为 `asset://boombox_new.glb`。
- `objectId = "boombox"` 不变。
- `configurePhysics = ResourcePhysicsConfigurator::configureBoombox` 不变。
- 物理代理结构不变，仍使用动态刚体父实体 + 缩放视觉 GLB 子实体。

### 4. 尺寸与动画检查

GLB JSON chunk 检查结果：

- `boombox_new.glb` 包围盒与旧 `boombox.glb` 一致。
- 尺寸约为 `0.7238m × 0.4701m × 0.1865m`。
- 动画数量为 `1`。

因此本阶段无需调整：

- `visualScale = 0.45f`
- `floorOffset = 0.11f`
- `colliderSize = Vector3(0.33f, 0.22f, 0.1f)`
- `restitution = 0.35f`

### 5. 验证命令

```bash
./gradlew :app:compileDebugKotlin
```

验证结果：编译通过。

源码检索结果：

- Kotlin 源码中不再存在 `asset://boombox.glb`。
- Kotlin 源码中仅 `HomeStage.kt` 使用 `asset://boombox_new.glb`。

---

## Phase 23 技术交接：boombox 音乐播放列表调度

### 1. 阶段概述

本阶段为 boombox 接入两首 `.wav` 音乐，并完成空间音频播放列表调度。用户触发 `start-boombox` 后，boombox 会从自身实体位置播放音乐；当前曲目播放结束后自动切到下一首；触发 `stop-boombox` 后停止音乐并取消后续调度。

### 2. 音乐资源

资源路径：

```text
/Users/bytedance/MateFairy/app/src/main/assets/Dying_Me_instrumental.wav
/Users/bytedance/MateFairy/app/src/main/assets/火星时代教育.wav
```

基础信息：

- `Dying_Me_instrumental.wav`：双声道，44100Hz，16bit，约 233.91 秒。
- `火星时代教育.wav`：双声道，44100Hz，16bit，约 196.26 秒。

`app/build.gradle.kts` 已包含 `wav` noCompress，运行时可通过 assets 直接加载。

### 3. 播放列表初始化

`MateFairyRuntimeFactory` 中初始化 boombox 播放列表：

```kotlin
val musicModule = MusicModule(context).apply {
    setSpatialMusicPlaylist(BOOMBOX_SPATIAL_MUSIC_PLAYLIST)
}

private val BOOMBOX_SPATIAL_MUSIC_PLAYLIST = listOf(
    "Dying_Me_instrumental.wav",
    "火星时代教育.wav"
)
```

### 4. Start/Stop Action 行为

启动 boombox：

```kotlin
onUse = { boombox ->
    playObjectAnimation(boombox)
    musicModule.playNextSpatialMusicAt(boombox)
}
```

停止 boombox：

```kotlin
onBeforePutDown = { boombox ->
    playObjectAnimation(boombox)
    musicModule.stopSpatialMusic()
}
```

### 5. MusicModule 调度逻辑

核心字段：

```kotlin
private val mainHandler = Handler(Looper.getMainLooper())
private var spatialMusicTrackIndex: Int = -1
private var spatialMusicScheduleToken: Int = 0
private var spatialMusicEntity: Entity? = null
private var spatialMusicVolume: Float = 1.0f
```

核心流程：

1. `playNextSpatialMusicAt()` 计算下一首索引。
2. `playSpatialMusicAt()` 释放旧播放器，确保 boombox 实体具备 `ObjectAudioComponent`。
3. 通过 `AudioResource.load(fileName, fileName, LoadType.FROM_ASSETS)` 加载音频资源。
4. 调用 `entity.playAudio(resource)` 从 boombox 实体位置播放空间音频。
5. 设置 `AudioPlayerController.setLoop(false)`，曲目结束由调度器切换。
6. `scheduleNextSpatialTrack()` 使用 `MediaMetadataRetriever` 读取 wav 时长，并通过 `Handler.postDelayed()` 安排自动切歌。
7. `stopSpatialMusic()` 增加 `spatialMusicScheduleToken`，使所有旧调度失效，并关闭当前播放器。

### 6. 空间音频组件

boombox 播放音乐前会确保目标实体具备：

```kotlin
ObjectAudioComponent(
    volume = 1.0f,
    directivity = Directivity(pattern = 0.35f, sharpness = 0.65f),
    distanceAttenuationMode = DistanceAttenuationMode.INVERSE_SQUARED,
    reverbVolume = 0.45f
)
```

说明：

- 声音随 boombox 的世界位置移动。
- 距离越远音量越小。
- 有一定指向性，更接近真实音箱发声。

### 7. 生命周期清理

`HomeStage` 的 `DisposableEffect(runtime)` 中新增：

```kotlin
onDispose {
    runtime.avatarController.cleanup()
    runtime.musicModule.destroy()
    BehaviorRuntimeDependencies.clear()
}
```

`MusicModule.destroy()` 会：

- 停止全局 BGM。
- 停止 boombox 空间音乐。
- 停止临时 SFX。
- 关闭缓存的 `AudioResource`。

### 8. 验证命令

```bash
./gradlew :app:compileDebugKotlin
```

验证结果：编译通过。

### 9. 后续扩展建议

- 新增 `next-boombox-track`、`pause-boombox`、`resume-boombox` action intent。
- 将 `BOOMBOX_SPATIAL_MUSIC_PLAYLIST` 迁移到外部配置文件。
- 引入 AudioMixerGroup 统一控制 boombox 音量、淡入淡出和播放速度。

---

## Phase 24 技术交接：boombox action 调试开关

### 1. 阶段概述

本阶段新增一个常驻调试 UI，用于绕过 LLM/语音输入，直接触发“精灵打开音响”和“精灵关闭音响”两个 action。该入口不绕过 ECS 行为系统，只负责向 `InteractionActionRequestBus` 投递请求，因此可以用于验证完整的精灵寻路、拾取、使用、音乐播放和放下流程。

### 2. 新增 UI 文件

文件路径：

```text
/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt
```

核心逻辑：

```kotlin
val didEnqueue = InteractionActionRuntimeDependencies.requestBus.enqueue(
    InteractionActionRequest(
        actionId = actionId,
        objectIds = listOf(StartBoomboxActionController.DEFAULT_OBJECT_ID),
        source = InteractionActionSource.DEBUG
    )
)
```

说明：

- `actionId = "start-boombox"` 时触发精灵打开音响。
- `actionId = "stop-boombox"` 时触发精灵关闭音响。
- `objectIds = listOf("boombox")`，显式指定目标对象，避免依赖 controller 默认值。
- `source = DEBUG`，方便后续日志或行为统计区分调试来源。

### 3. Attachment 挂载方式

`HomeStageRuntimeState` 新增：

```kotlin
var debugActionAttachmentEntity: Entity? = null
```

`SpatialView.update` 中挂载：

```kotlin
attachments.entity("debug_action_panel")?.let { debugEntity ->
    if (runtimeState.debugActionAttachmentEntity != debugEntity) {
        runtimeState.debugActionAttachmentEntity = debugEntity
        if (debugEntity.components[TransformComponent::class.java] == null) {
            debugEntity.components[TransformComponent::class.java] = TransformComponent()
        }
        hmdEntity.addChild(debugEntity)
    }

    debugEntity.components[TransformComponent::class.java]?.apply {
        setPosition(Vector3(0.44f, -0.32f, -0.85f))
        setQuaternion(Quat.identity())
    }
}
```

位置说明：

- 父节点为 `hmdEntity`，因此面板跟随头显。
- 相对位置为右下方 `0.44m`、下方 `0.32m`、前方 `0.85m`。
- 使用 `Quat.identity()` 保持相对 HMD 的正向面板姿态。

### 4. Action Source 扩展

`InteractionActionSource` 新增：

```kotlin
enum class InteractionActionSource {
    RANDOM,
    DIALOGUE,
    DEBUG
}
```

该字段当前用于标记请求来源，不改变 action 调度优先级。由于 `InteractionActionRequestBus.enqueue()` 仍会检查 `InteractionActionRuntimeDependencies.lockState.isLocked`，调试请求不会打断正在执行的 action。

### 5. 运行时行为

流程：

1. 用户点击调试面板按钮。
2. 面板根据当前状态选择 `start-boombox` 或 `stop-boombox`。
3. 请求进入 `InteractionActionRequestBus`。
4. `InteractionActionSystem` 在下一帧 drain 请求。
5. 对应 `StartBoomboxActionController` / `StopBoomboxActionController` 创建 action instance。
6. 精灵执行完整交互流程。

### 6. 验证命令

```bash
./gradlew :app:compileDebugKotlin
```

验证结果：编译通过。

### 7. 发布前注意

该面板是开发调试入口，当前为常驻显示。如果进入非调试演示或发布构建，建议增加配置开关，例如：

```kotlin
private const val ENABLE_DEBUG_ACTION_PANEL = true
```

或接入 `BuildConfig.DEBUG` 控制显示。

---

## Phase 20 技术交接：启动 GLB 资产物理代理化

### 1. 阶段概述

本阶段将 `boombox.glb` 与 `rubber_duck_toy.glb` 从纯静态展示物升级为动态物理资源。它们现在拥有刚体、碰撞体、物理材质，并复用空间 mesh 激活系统，在现实环境网格可用后开启重力，与真实房间 mesh 发生物理交互。

### 2. ResourcePhysicsConfigurator 扩展

新增两个资源入口：

```kotlin
object ResourcePhysicsConfigurator {
    fun configureBoombox(boombox: Entity)
    fun configureRubberDuck(duck: Entity)
}
```

新增盒体资源配置：

```kotlin
private data class BoxPhysicsConfig(
    val colliderSize: Vector3,
    val staticFriction: Float,
    val dynamicFriction: Float,
    val restitution: Float,
    val linearDamping: Float,
    val angularDamping: Float
)
```

当前参数：

- boombox：`colliderSize = Vector3(0.33f, 0.22f, 0.1f)`，`restitution = 0.35f`，偏重稳定落地。
- rubber duck：`colliderSize = Vector3(0.2f, 0.26f, 0.28f)`，`restitution = 0.55f`，保留轻微弹性。

### 3. 动态刚体统一配置

`configureDynamicRigidBody()` 统一负责：

```kotlin
entity.components.set(
    CollisionComponent(
        collisionShape = shapes,
        physicsMaterial = material,
        collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
        collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
        collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
    )
)

val rigidBody = entity.components[RigidBodyComponent::class.java] ?: RigidBodyComponent()
rigidBody.apply {
    rigidBodyMode = RigidBodyMode.DYNAMIC
    isAffectedByGravity = false
    collisionDetectionMode = CollisionDetectionMode.CONTINUOUS
    this.linearDamping = linearDamping
    this.angularDamping = angularDamping
}
entity.components.set(rigidBody)
```

同时补齐：

- `PhysicsForceComponent`
- `InteractableComponent`
- `HoverEffectComponent`
- `ResourcePhysicsActivationComponent(floorOffset = floorOffset)`

### 4. floorOffset 激活机制

`ResourcePhysicsActivationComponent` 现在支持非球形资源：

```kotlin
class ResourcePhysicsActivationComponent(
    val radius: Float = 0.11f,
    val floorOffset: Float = radius,
    val minWaitSeconds: Float = 1.5f,
    val rayStartHeight: Float = 0.6f,
    val rayLength: Float = 1.5f,
    val fallbackEnableGravitySeconds: Float = 6f,
) : Component()
```

落地高度计算：

```kotlin
val safeY = floorY + activation.floorOffset + 0.03f
```

说明：

- 球体资源仍可默认使用 `floorOffset = radius`。
- 盒体资源使用 `floorOffset = colliderSize.y / 2f`。
- 这样空间 mesh raycast 命中地面后，资源中心会被修正到“半高 + 3cm 安全余量”。

### 5. HomeStage 物理代理结构

`StartupStaticAssetSpec` 当前结构：

```kotlin
private data class StartupStaticAssetSpec(
    val assetUri: String,
    val objectId: String,
    val position: Vector3,
    val visualScale: Float,
    val floorOffset: Float,
    val configurePhysics: (Entity) -> Unit
)
```

创建流程：

```kotlin
private fun createStartupStaticAsset(spec: StartupStaticAssetSpec, assetRoot: Entity): Entity {
    return Entity().apply {
        components[TransformComponent::class.java] = TransformComponent().apply {
            setPosition(spec.position)
            scaleVector = Vector3(1f, 1f, 1f)
        }
        components.set(
            InteractionObjectComponent(
                objectId = spec.objectId,
                tags = setOf("startup_asset", "physics")
            )
        )
        spec.configurePhysics(this)

        val visualEntity = Entity().apply {
            components[TransformComponent::class.java] = TransformComponent().apply {
                setPosition(Vector3(0f, -spec.floorOffset, 0f))
                scaleVector = Vector3(spec.visualScale, spec.visualScale, spec.visualScale)
            }
            addChild(assetRoot)
        }
        addChild(visualEntity)
    }
}
```

结构含义：

- 父实体：物理代理，scale 固定为 1，承载刚体、碰撞体和物理激活组件。
- 子实体：视觉模型 wrapper，负责 GLB 缩放和底部对齐。
- GLB 根实体：保持原始层级，作为视觉 wrapper 的子节点。

### 6. 与现实环境网格的关系

现实环境 mesh 由 `SpatialMeshManager` 创建，核心条件：

- mesh 实体使用 `ShapeResource.createStaticMesh(mesh)`。
- mesh 实体的 `CollisionResponseMode` 是 `COLLIDER_FULL`。
- 动态资源的 `CollisionResponseMode` 也是 `COLLIDER_FULL`。
- 动态资源和 mesh 实体都挂在同一个 `rootEntity` 体系下，共用 `PhysicsWorldComponent`。

满足以上条件后，boombox 和 rubber duck 会在 mesh 就绪后开启重力，并与现实环境 mesh 产生物理阻挡。

### 7. SDK/框架避坑指南

- 不要直接给缩放后的 GLB 根节点挂动态刚体；GLB 内部层级、缩放和渲染包围盒容易与物理模拟耦合。
- 对动态资源优先使用 primitive shape，如 box/sphere/capsule；复杂 mesh 碰撞更适合静态环境。
- `ResourcePhysicsActivationSystem` 的 raycast 会排除自身及子级实体，防止命中自己的视觉模型或碰撞体。
- 启动时 mesh 扫描速度不稳定，动态资源应先关闭重力，等待 mesh raycast 或兜底时间后再开启。

### 8. 验证命令

```bash
./gradlew :app:compileDebugKotlin
```

验证结果：编译通过。

---

## Phase 22 技术交接：boombox 与 rubber_duck_toy action 控制器

### 1. 阶段概述

本阶段为 `boombox_new.glb` 与 `rubber_duck_toy.glb` 新增 interaction action 控制器，并引入通用“被拾取物体跟随精灵”的 ECS 组件/系统。新 action 复用既有 action 调度、锁定、监听与跟随暂停机制。

### 2. 拾取跟随系统

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PickedObjectFollowSystem.kt`

核心组件：

```kotlin
class PickedObjectFollowComponent(
    val holderActorId: String = DEFAULT_FAIRY_ACTOR_ID,
    val localOffset: Vector3 = Vector3(0f, -0.05f, 0.28f)
) : Component()
```

运行逻辑：

1. 查询所有挂载 `PickedObjectFollowComponent` 的物体。
2. 通过 `holderActorId` 找到精灵主体。
3. 根据精灵位置和 yaw 将 local offset 转换为世界 offset。
4. 更新物体位置，使物体表现为被精灵抓起。
5. 跟随期间关闭重力，并清零 force/velocity，避免物理系统和跟随系统抢控制。

### 3. 新增 action 控制器

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`

action 列表：

- `StartBoomboxActionController.ACTION_ID = "start-boombox"`
- `StopBoomboxActionController.ACTION_ID = "stop-boombox"`
- `SqueezeRubberDuckActionController.ACTION_ID = "squeeze-rubber-duck"`
- `PutDownRubberDuckActionController.ACTION_ID = "put-down-rubber-duck"`

默认客体：

- boombox：`objectId = "boombox"`
- 小黄鸭：`objectId = "rubber_duck_toy"`

### 4. 启动音响 action 流程

`start-boombox`：

1. 解析主体 `fairy` 和客体 `boombox`。
2. 精灵向 boombox 附近寻路。
3. 到达后给 boombox 挂 `PickedObjectFollowComponent`。
4. boombox 跟随精灵，表现为被抓起。
5. 调用对象动画 helper 尝试播放 boombox 动画轨道。
6. 调用 `MusicModule.playRandomSpatialMusicAt(boombox)`，声源实体是 boombox，因此音频空间位置会跟随模型。

### 5. 关闭音响 action 流程

`stop-boombox`：

1. 停止空间音乐：`MusicModule.stopSpatialMusic()`。
2. 尝试触发 boombox 关闭动画轨道。
3. 移除 `PickedObjectFollowComponent`。
4. 将 boombox 放到精灵前方。
5. 恢复 `RigidBodyComponent.isAffectedByGravity = true`。

### 6. 捏小黄鸭 action 流程

`squeeze-rubber-duck`：

1. 精灵寻路到 `rubber_duck_toy`。
2. 到达后挂 `PickedObjectFollowComponent`。
3. 每 2 秒触发一次小黄鸭动画。
4. 每次触发动画时调用 `MusicModule.playRandomRubberDuckSfxAt(duck)`。
5. 当前默认触发 3 次后 action 完成，但小黄鸭保持被拾取状态，直到执行放下 action。

### 7. 放下小黄鸭 action 流程

`put-down-rubber-duck`：

1. 移除 `PickedObjectFollowComponent`。
2. 将小黄鸭放到精灵前方。
3. 恢复重力，让小黄鸭重新进入物理世界。

### 8. 音频模块扩展

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

新增能力：

```kotlin
fun setSpatialMusicPlaylist(fileNames: List<String>)
fun playRandomSpatialMusicAt(entity: Entity, volume: Float = 1.0f): String?
fun stopSpatialMusic()
fun setRubberDuckSfxList(fileNames: List<String>)
fun playRandomRubberDuckSfxAt(entity: Entity, volume: Float = 1.0f): String?
```

实现要点：

- 播放前确保声源实体挂载 `ObjectAudioComponent`。
- 使用 `AudioResource.load(fileName, fileName, LoadType.FROM_ASSETS)` 加载音频。
- 使用 `entity.playAudio(resource)` 播放，使声音跟随实体位置。
- 曲库为空时安全跳过，不抛异常。

### 9. LLM 与优先级

新增 action 已加入 `AnimationConfig.supportedActions`。

`DefaultBehaviorDecisionMaker` 将这些 action 归类为 action 类任务：

- 高于普通动画和普通情绪。
- 低于 `angry` / `sad` 负面高优情绪。
- 因此如果用户辱骂并要求打开音响/捏小黄鸭，程序侧仍会优先执行负面情绪动画并拦截 action。

### 10. 后续接入 TODO

- 音乐资源未配置：需要后续在 runtime 初始化阶段调用 `setSpatialMusicPlaylist()`。
- 小黄鸭音效未配置：需要后续在 runtime 初始化阶段调用 `setRubberDuckSfxList()`。
- boombox 和 duck 的动画轨道目前通过 `entity.getAnimationResources()` 尝试触发；如果 GLB 动画挂在子实体，应扩展为递归查找带动画资源的子节点。

---

## Phase 23 技术交接：资源物理激活与持有物稳定性

### 1. 阶段概述

本阶段修复了资源启动后消失、篮球穿地、boombox action 卡住/下坠/飞出房间等问题。核心设计原则是：任何依赖真实空间网格承托的动态资源，必须等 spatial mesh floor hit 确认后才能开启重力；任何由脚本每帧直接写入 `TransformComponent.position` 的实体，不能同时保持 `RigidBodyMode.DYNAMIC` 参与完整物理求解。

### 2. 资源重力激活规则

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/FootballPhysicsActivationSystem.kt`

关键规则：

- `ResourcePhysicsActivationComponent.activated == false` 时，资源保持 `RigidBodyComponent.isAffectedByGravity = false`。
- 系统从资源上方向下 raycast，只有命中真实 floor 后才把资源移动到安全高度并开启重力。
- 如果没有命中 floor，不再使用固定 6 秒兜底开启重力。

核心效果：

```kotlin
if (hit == null) {
    return@forEach
}

val floorY = origin.y - hit.distance
val safeY = floorY + activation.floorOffset + 0.03f
if (position.y < safeY) {
    transform.position = Vector3(position.x, safeY, position.z)
}

rigidBody.isAffectedByGravity = true
activation.activated = true
```

### 3. 球体碰撞体规则

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/ResourcePhysicsConfigurator.kt`

足球和篮球不再复用 Editor 场景中的碰撞体，而是统一创建运行时球体：

```kotlin
ShapeResource.createSphere(config.colliderRadius)
```

原因：

- Editor 碰撞体可能与运行时缩放、层级或资源替换不完全一致。
- 篮球曾出现单独穿透 floor 的现象，运行时 primitive sphere 更可控。

### 4. Action 前置条件

文件：

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PlayFootballActionController.kt`

随机踢球和主动踢球都必须满足：

```kotlin
val activation = football.components[ResourcePhysicsActivationComponent::class.java]
if (activation != null && !activation.activated) return false
if (footballTransform.position.y < -0.5f) return false
```

设计原因：

- Stage 启动初期 spatial mesh 可能尚未生成。
- 如果此时随机 action 介入，会让足球在无地面状态下进入物理/action 链路，后续可能带坏精灵位置。

### 5. 脚本运动与刚体模式

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`

`CarryAndUseObjectActionInstance` 会直接推进精灵 Transform：

```kotlin
transform.position = Vector3(
    transform.position.x + direction.x * step,
    transform.position.y + direction.y * step,
    transform.position.z + direction.z * step
)
```

因此 action 运行期间必须把精灵从动态刚体切到运动学刚体：

```kotlin
rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
rigidBody.isAffectedByGravity = false
subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
subject.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
```

完成或取消 action 时恢复：

```kotlin
rigidBody.rigidBodyMode = previousSubjectRigidBodyMode ?: RigidBodyMode.DYNAMIC
rigidBody.isAffectedByGravity = false
```

### 6. 持有物跟随规则

文件：

- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`
- `/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PickedObjectFollowSystem.kt`

boombox/duck 被拿起时会挂 `PickedObjectFollowComponent`。跟随系统每帧根据精灵位置直接写入物体 Transform，因此持有期间物体也必须是 `KINEMATIC`：

```kotlin
picked.components[RigidBodyComponent::class.java]?.let { rigidBody ->
    rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
    rigidBody.isAffectedByGravity = false
}
picked.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
picked.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
```

放下物体时恢复动态刚体和重力：

```kotlin
target.components[RigidBodyComponent::class.java]?.let { rigidBody ->
    rigidBody.rigidBodyMode = RigidBodyMode.DYNAMIC
    rigidBody.isAffectedByGravity = true
}
```

### 7. 避坑指南

- 不要在 `RigidBodyMode.DYNAMIC` 实体上长期直接改 `TransformComponent.position`，这会和物理求解器产生速度/冲量冲突。
- 被持有物如果仍是动态刚体，哪怕每帧清速度，也可能因为碰撞求解与 follow transform 冲突导致抖动或把持有者顶飞。
- Full Space spatial mesh 生成前不能假设 floor 已存在；固定延迟开启重力不可靠。
- 如果后续仍出现墙边卡住，优先检查脚本 action 期间是否需要临时禁用与 spatial mesh 的碰撞，或给 action 增加避障逻辑。

---

## Phase 24 技术交接：持有物碰撞响应隔离

### 1. 阶段概述

本阶段进一步修复 boombox 被拿起后带着精灵高速飞出房间的问题。日志证明 action 完成时坐标仍正常，因此根因不在 `APPROACHING` 的目标点计算，也不在 `PICKING_UP` 的瞬间位移，而更可能是 action 完成后持有物仍作为物理碰撞体持续推挤精灵。

### 2. 核心规则

当实体由 `PickedObjectFollowSystem` 每帧直接跟随精灵时，它不应该继续使用 `CollisionResponseMode.COLLIDER_FULL`。

原因：

- `COLLIDER_FULL` 是标准物理碰撞，会产生阻挡、反弹和推挤。
- 被持有物是 `KINEMATIC`，它的位置由脚本决定，不受物理反作用约束。
- 如果 kinematic boombox 与 dynamic fairy 或 spatial mesh 发生重叠，物理求解器可能把 dynamic fairy 推开，表现为高速飞出房间。

### 3. 组件字段

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PickedObjectFollowSystem.kt`

`PickedObjectFollowComponent` 新增字段：

```kotlin
class PickedObjectFollowComponent(
    val holderActorId: String = DEFAULT_FAIRY_ACTOR_ID,
    val localOffset: Vector3 = Vector3(0f, -0.05f, 0.28f)
) : Component() {
    var previousCollisionResponseMode: CollisionResponseMode? = null
}
```

用途：

- 记录被拿起前的碰撞响应模式。
- 放下物体时恢复，避免破坏物体后续与真实 mesh 的物理交互。

### 4. 持有期间碰撞隔离

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PickedObjectFollowSystem.kt`

持有物每帧跟随时执行：

```kotlin
picked.components[CollisionComponent::class.java]?.let { collision ->
    if (follow.previousCollisionResponseMode == null) {
        follow.previousCollisionResponseMode = collision.collisionResponseMode
    }
    collision.collisionResponseMode = CollisionResponseMode.TRIGGER_LITE
}
```

效果：

- 保留 trigger 能力。
- 不再产生 `COLLIDER_FULL` 的物理推挤。
- 避免 held boombox 在精灵恢复普通行为后继续顶飞精灵。

### 5. 拿起与放下流程

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`

拿起时立即切换：

```kotlin
val follow = PickedObjectFollowComponent(
    holderActorId = request.subjectId,
    localOffset = config.holdOffset
)
target.components[CollisionComponent::class.java]?.let { collision ->
    follow.previousCollisionResponseMode = collision.collisionResponseMode
    collision.collisionResponseMode = CollisionResponseMode.TRIGGER_LITE
}
target.components.set(follow)
```

放下时恢复：

```kotlin
val follow = target.components[PickedObjectFollowComponent::class.java]
target.components.remove(PickedObjectFollowComponent::class.java)
target.components[CollisionComponent::class.java]?.collisionResponseMode =
    follow?.previousCollisionResponseMode ?: CollisionResponseMode.COLLIDER_FULL
```

### 6. 后续排查方向

如果该修复后仍有飞出现象，优先排查：

- `FairyBehaviorSystem` 在 action lock 释放后的 `reconcileAfterAction()` 是否产生过大的恢复力。
- `PlayFootballActionController` 随机 action 是否在 boombox action 前后残留速度或状态。
- 是否需要在持有期间让精灵也临时使用 trigger-only 碰撞，避免与 spatial mesh 墙面产生卡死。

---

## Phase 25 技术交接：action 优先级与踢球超时保护

### 1. 阶段概述

本阶段修复随机踢球 action 卡死后阻塞“打开音响”调试 action 的问题。核心原则是：随机行为不能无限占用全局 action lock；显式用户/调试指令应能抢占低优先级随机 action。

### 2. Action 优先级

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionCore.kt`

优先级定义：

```kotlin
private val InteractionActionSource.priority: Int
    get() = when (this) {
        InteractionActionSource.RANDOM -> 0
        InteractionActionSource.DIALOGUE -> 1
        InteractionActionSource.DEBUG -> 2
    }
```

`InteractionActionLockState.canPreempt()` 判断新请求是否可以抢占当前 action：

```kotlin
fun canPreempt(request: InteractionActionRequest): Boolean {
    val source = currentSource ?: return false
    return request.source.priority > source.priority
}
```

### 3. 请求入队策略

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionCore.kt`

`InteractionActionRequestBus.enqueue()` 原来只要锁存在就拒绝。现在规则改为：

- 如果当前锁不可被抢占，拒绝。
- 如果新请求优先级更高，允许入队，并在日志中记录 `willPreempt = true`。

### 4. 抢占执行策略

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionSystem.kt`

系统 drain 请求后，如果可以抢占：

```kotlin
activeActions.remove(preemptedControllerId)?.cancel()
InteractionActionRuntimeDependencies.lockState.release(
    preemptedControllerId,
    InteractionActionStatus.FAILED
)
```

然后再对新请求执行 `tryLock()` 和实例创建。

### 5. PlayFootball 安全生命周期

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/PlayFootballActionController.kt`

关键修复：

- `play-football` action 期间临时将精灵刚体切为 `RigidBodyMode.KINEMATIC`。
- 移动方式改为直接 Transform 步进，而不是持续写 `PhysicsForceComponent.force`。
- 新增 `maxActionSeconds = 4.5f`，超过时间直接 `FAILED`。
- `COMPLETED` / `FAILED` / `cancel()` 都调用统一清理逻辑：

```kotlin
private fun cleanupSubjectMotion(subject: Entity) {
    subject.components.remove(FairyActionLockComponent::class.java)
    subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
    subject.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
    subject.components[RigidBodyComponent::class.java]?.let { rigidBody ->
        rigidBody.rigidBodyMode = previousSubjectRigidBodyMode ?: RigidBodyMode.DYNAMIC
        rigidBody.isAffectedByGravity = false
    }
}
```

### 6. 验证重点

- 随机踢球如果卡住，应在约 4.5 秒后自动失败并释放 lock。
- 随机踢球执行中点击“打开音响”，`DEBUG` 请求应抢占 `RANDOM` 请求。
- 抢占后不应残留 `FairyActionLockComponent`、速度或力。

---

## Phase 26 技术交接：动态资源安全回收与音频 fallback

### 1. 阶段概述

本阶段处理两个高风险稳定性问题：

- 足球/篮球在 floor activation 前后可能掉出 spatial mesh 支撑范围，导致不可见或掉入虚空。
- boombox 打开后长期被精灵持有，叠加音频 spatial 播放失败时，表现为精灵飞出房间且无音乐。

### 2. 足球/篮球可见性与安全回收

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/FootballPhysicsActivationSystem.kt`

新增规则：

- `football` / `basketball` 在未激活时，如果 `y < 0.65f`，强制悬停到 `PRE_ACTIVATION_VISIBLE_Y = 0.65f`。
- 未激活阶段始终关闭重力并清空 force/velocity。
- floor hit 成功时记录 `lastSafePosition`。
- 激活后如果 `y < -0.5f`，自动回收到 `lastSafePosition`，关闭重力，清空速度和力，并把 `activated` 重新置为 `false`。

核心代码：

```kotlin
if (isSportsBall(objectId) && position.y < PRE_ACTIVATION_VISIBLE_Y) {
    transform.position = Vector3(position.x, PRE_ACTIVATION_VISIBLE_Y, position.z)
    clearPhysicsMotion(resource, rigidBody)
}
```

```kotlin
if (position.y < FALL_RESET_Y && isSportsBall(objectId)) {
    resetResourceToSafeSuspendedPose(resource, transform, rigidBody, activation)
    return@forEach
}
```

### 3. Boombox 携带期间移动策略

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/behavior/FairyBehaviorSystem.kt`

功能设计要求：

- 精灵打开 boombox 后应继续拿着音响到处跑。
- 不能通过“自动放下 boombox”规避飞出问题。

稳定性策略：

- 如果场景中存在 `PickedObjectFollowComponent(holderActorId = fairy)`，说明精灵正在携带物体。
- 携带期间精灵移动不再使用动态刚体力控，而是切换到 `RigidBodyMode.KINEMATIC` 并通过 Transform 直移。
- 移动速度上限为 `MAX_CARRYING_DIRECT_SPEED = 0.65f`，避免瞬时速度被物理求解放大。
- 被携带物仍由 `PickedObjectFollowSystem` 跟随精灵，不自动移除。

### 4. 音乐播放 fallback

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

空间音频优先：

```kotlin
spatialMusicPlayer = entity.playAudio(resource)?.apply {
    setVolume(volume.coerceIn(0f, 1f))
    setLoop(false)
}
```

如果 `entity.playAudio(resource)` 返回 null：

```kotlin
if (spatialMusicPlayer == null) {
    playBgmFromAsset(fileName, loop = false, volume = volume)
}
```

这样即使 spatial audio 在当前实体/资源状态下启动失败，也会通过普通 `MediaPlayer` 播放同一首 assets wav，保证用户听到音乐。

`stopSpatialMusic()` 同时调用 `stopBgm()`，避免 fallback 音乐无法停止。

### 5. 验证重点

- 启动后足球/篮球是否立刻可见。
- mesh floor 还没 ready 时，足球/篮球应悬停而不是掉落。
- 如果足球/篮球被 action 或物理异常推到 `y < -0.5f`，应自动回收到安全位置。
- boombox 打开后应播放音乐，并继续被精灵拿着移动。
- 精灵携带 boombox 移动时不应被物理力控或碰撞反馈放大到飞出房间。

---

## Phase 27 技术交接：boombox debug 控制细化

### 1. 拾取偏移

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/ObjectInteractionActionControllers.kt`

`StartBoomboxActionController` 当前持有偏移：

```kotlin
holdOffset = Vector3(0f, -0.03f, 0.16f)
```

设计说明：

- 原 `z = 0.32f` 让 boombox 距离精灵过远，视觉上像悬浮跟随而不是被拿起。
- 新值把 boombox 收近到精灵前方约 16cm，保留轻微下偏移，增强拾取感。

### 2. 音乐播放 fallback

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

当前策略：

```kotlin
if (spatialMusicPlayer?.isPlaying() != true) {
    playBgmFromAsset(fileName, loop = false, volume = volume)
}
```

设计说明：

- `entity.playAudio(resource)` 可能返回非空 player，但 player 未立即进入 playing。
- 仅判断 player 是否为 null 不足以保证用户能听到声音。
- fallback 使用同一 assets wav 通过 `MediaPlayer` 播放，优先保证功能可感知。

### 3. DEBUG 同级抢占

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/interaction/InteractionActionCore.kt`

当前抢占规则：

```kotlin
if (request.source.priority > source.priority) return true
return request.source == InteractionActionSource.DEBUG &&
    source == InteractionActionSource.DEBUG &&
    request.controllerId != currentControllerId
```

设计说明：

- `RANDOM < DIALOGUE < DEBUG` 的优先级仍然保留。
- 额外允许不同 controllerId 的 `DEBUG` action 互相抢占。
- 这使 `stop-boombox` 可以打断正在执行的 `start-boombox`，避免 debug 控制按钮被自身 action lock 卡住。

### 4. 验证重点

- boombox 是否贴近精灵手边/身体附近。
- 如果 spatial audio 未实际播放，是否能听到 fallback BGM。
- `start-boombox` 尚未完成时点击 `stop-boombox`，应能抢占当前 action。

---

## Phase 28 技术交接：普通 BGM 最小验证按钮

### 1. 背景

boombox 音乐无声可能来自多个层级：

- PICO spatial audio 链路未播放。
- `ObjectAudioComponent` 衰减、方向或组件配置导致不可听。
- Android `MediaPlayer` 也无法输出。
- 模拟器/真机系统音量或音频路由异常。

为了先隔离系统音频输出和 assets wav 可读性，新增一个最小验证按钮。

### 2. UI 入口

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt`

`DebugActionPanel` 接收 `MusicModule`：

```kotlin
@Composable
fun DebugActionPanel(musicModule: MusicModule)
```

新增按钮：

```kotlin
Button(
    onClick = {
        musicModule.playBgmFromAsset(DEBUG_BGM_FILE, loop = false, volume = 1.0f)
        statusText = "已直接播放 assets BGM：$DEBUG_BGM_FILE"
    }
) {
    Text("测试普通音乐")
}
```

停止按钮：

```kotlin
Button(
    onClick = {
        musicModule.stopBgm()
        statusText = "已停止普通音乐测试"
    }
) {
    Text("停止普通音乐")
}
```

测试文件：

```kotlin
private const val DEBUG_BGM_FILE = "Dying_Me_instrumental.wav"
```

### 3. 接线方式

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`

debug attachment 中传入当前 runtime 的音乐模块：

```kotlin
AttachmentPanel(id = "debug_action_panel") {
    DebugActionPanel(runtime.musicModule)
}
```

### 4. 判定方法

- 点击 `测试普通音乐` 后如果能听见声音：
  - assets wav、APK noCompress、Android `MediaPlayer`、设备/模拟器音频输出基本正常。
  - 后续重点排查 PICO `AudioResource`、`AudioPlayerController.play()`、`ObjectAudioComponent` 或 spatial audio 后端。
- 点击后仍无声：
  - 后续重点排查系统音量、模拟器音频输出、MediaPlayer 异常日志或 assets 读取问题。

---

## Phase 29 技术交接：boombox ObjectAudio 可听性测试配置

### 1. 背景

普通 `MediaPlayer` 已验证可以播放 assets wav，因此基础音频输出、资源读取和 `noCompress("wav")` 均正常。用户要求 boombox 必须保留空间声效，声源需要始终跟随音响实体。本阶段仅调整 `ObjectAudioComponent` 的可听性配置，不切换到普通 BGM。

### 2. 修改位置

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

当前 `ensureObjectAudio(entity)`：

```kotlin
private fun ensureObjectAudio(entity: Entity) {
    entity.components.set(
        ObjectAudioComponent(
            volume = 1.0f,
            directivity = Directivity(pattern = 0f, sharpness = 0f),
            distanceAttenuationMode = DistanceAttenuationMode.FIXED,
            reverbVolume = 0f
        )
    )
}
```

### 3. 设计说明

- 每次播放前强制 `components.set(ObjectAudioComponent(...))`，避免旧的 `INVERSE_SQUARED` 或方向性参数残留。
- `DistanceAttenuationMode.FIXED` 用于排除距离平方衰减导致“播放但听不见”的可能。
- `Directivity(pattern = 0f, sharpness = 0f)` 用于测试尽量全向，排除 boombox 朝向导致音量过低。
- `reverbVolume = 0f` 用于减少混响变量，便于判断主声源是否可听。

### 4. 复测结论判定

- 如果本轮有声：
  - 说明 spatial audio 链路可用，问题主要在原来的衰减或方向性配置。
  - 后续可以逐步恢复更真实的 boombox 扬声器方向性。
- 如果本轮无声：
  - 下一步验证 `AudioPlayerController.play()` 是否必须显式调用。
  - 可进一步尝试 `prepareAudio(resource)` 后手动 `play()`，以排除 `entity.playAudio(resource)` 启动语义差异。

---

## Phase 30 技术交接：boombox 空间音频 prepareAudio-play 验证

### 1. 背景

Phase 29 已将 boombox 的 `ObjectAudioComponent` 调整为固定衰减、全向、无混响，但用户复测仍无声。因此需要继续排查 PICO spatial audio 的启动语义：`entity.playAudio(resource)` 是否会自动播放，或者是否必须显式调用 controller 的 `play()`。

### 2. 修改位置

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

当前 `playSpatialMusicAt(entity, fileName, volume)` 核心逻辑：

```kotlin
private fun playSpatialMusicAt(entity: Entity, fileName: String, volume: Float): Boolean {
    releaseSpatialMusicPlayer()
    return runCatching {
        ensureObjectAudio(entity)
        val resource = getSpatialAudioResource(fileName)
        spatialMusicPlayer = entity.prepareAudio(resource)?.apply {
            setVolume(volume.coerceIn(0f, 1f))
            setLoop(false)
            play()
        }
        spatialMusicPlayer != null
    }.onFailure {
        Log.e(TAG, "Failed to play spatial music: $fileName", it)
    }.getOrDefault(false)
}
```

### 3. 设计说明

- 使用 `prepareAudio(resource)` 明确创建/准备 `AudioPlayerController`。
- 在 controller 上显式调用 `play()`，避免 SDK 版本差异导致 `playAudio(resource)` 不自动出声。
- 本轮验证不走普通 `MediaPlayer` fallback，避免出现“听到声音但不是空间声源”的误判。
- `ObjectAudioComponent` 仍保留 Phase 29 的最易听配置：`FIXED` 衰减、全向、无混响。

### 4. 下一步判断

- 如果本轮有声：
  - 保持 `prepareAudio -> play` 作为 boombox 空间音乐的正式启动方式。
- 如果本轮仍无声：
  - 创建 HMD 前方固定空间音频测试实体，直接挂 `ObjectAudioComponent` 播放同一 wav。
  - 如果固定测试实体有声，问题在 boombox 实体层级、位置跟随或 action 调用链。
  - 如果固定测试实体无声，问题更可能在 PICO spatial audio 后端、`AudioResource.load` 参数形式或模拟器 spatial audio 支持。

---

## Phase 31 技术交接：独立 HMD 空间音频测试实体

### 1. 背景

Phase 30 使用 `prepareAudio -> play` 后，用户仍反馈 boombox 无声。此时 boombox 的距离衰减、方向性和播放启动语义都已被排除，下一步需要绕过 boombox action、实体跟随、物理代理和空间位置变量，直接测试 PICO spatial audio 后端是否能播放。

### 2. 核心改动

#### 2.1 `MusicModule`

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

- 新增公共测试入口：

```kotlin
fun playSpatialAudioDebugAt(
    entity: Entity,
    fileName: String = DEFAULT_DEBUG_SPATIAL_AUDIO,
    volume: Float = 1.0f
): Boolean
```

- `AudioResource.load` 改为文档推荐形式：

```kotlin
AudioResource.load(fileName, "asset://$fileName", LoadType.FROM_ASSETS)
```

#### 2.2 `HomeStage`

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/content/HomeStage.kt`

- 新增 HMD 子实体作为独立空间音频测试声源：

```kotlin
val spatialAudioTestEntity = remember {
    Entity().apply {
        components.set(
            TransformComponent().apply {
                setPosition(Vector3(0f, -0.05f, -0.5f))
                setQuaternion(Quat.identity())
            }
        )
    }
}
```

- 在场景初始化时挂到 `hmdEntity`：

```kotlin
hmdEntity.addChild(spatialAudioTestEntity)
```

#### 2.3 `DebugActionPanel`

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt`

- 新增 `测试空间音乐` 按钮。
- 点击后直接在 `spatialAudioTestEntity` 上播放 `Dying_Me_instrumental.wav`。
- `停止音乐测试` 同时调用 `stopBgm()` 和 `stopSpatialMusic()`。

### 3. 判定逻辑

- `测试空间音乐` 有声：
  - spatial audio 后端和 `AudioResource` 加载可用。
  - boombox 无声应继续查 boombox 实体是否被正确加入 Scene、是否被代理实体层级/Transform/刚体状态影响，或 action 是否调用到正确实体。
- `测试空间音乐` 无声：
  - 说明问题不在 boombox action。
  - 下一步应 A/B `ObjectAudioComponent`、`AmbientAudioComponent`、`ChannelAudioComponent`，区分是 Object Audio 类型问题，还是整个 PICO spatial audio 在当前运行环境不可用。

---

## Phase 32 技术交接：空间音频成功路径与音量配置

### 1. 成功路径结论

用户复测确认：

- `测试空间音乐` 可以播放。
- `打开音响` 后 boombox 绑定空间音乐也可以播放。

当前成功路径如下：

1. `AudioResource.load(fileName, "asset://$fileName", LoadType.FROM_ASSETS)` 加载 assets wav。
2. 目标实体设置 `ObjectAudioComponent`。
3. 调用 `entity.prepareAudio(resource)` 获取 `AudioPlayerController`。
4. 设置音量、循环状态。
5. 显式调用 `AudioPlayerController.play()`。

关键经验：PICO spatial audio 的 `AudioResource.load` 第二参数应使用 `asset://xxx.wav`，裸文件名虽然普通 `MediaPlayer` 可用，但 spatial audio 链路会无声。

### 2. 当前音量配置

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

```kotlin
private const val DEFAULT_SPATIAL_MUSIC_VOLUME = 0.35f
```

- `playNextSpatialMusicAt(...)` 默认使用 `0.35f`。
- `playRandomSpatialMusicAt(...)` 默认使用 `0.35f`。
- `playSpatialAudioDebugAt(...)` 默认使用 `0.35f`。

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt`

```kotlin
private const val DEBUG_MUSIC_TEST_VOLUME = 0.35f
```

- `测试普通音乐` 和 `测试空间音乐` 均使用 `0.35f`，便于对比普通音频与空间音频听感。

### 3. 当前空间声源配置

`ObjectAudioComponent` 仍保持 Phase 29 的验证配置：

```kotlin
ObjectAudioComponent(
    volume = 1.0f,
    directivity = Directivity(pattern = 0f, sharpness = 0f),
    distanceAttenuationMode = DistanceAttenuationMode.FIXED,
    reverbVolume = 0f
)
```

说明：

- 该配置用于稳定验证空间声源是否跟随 boombox，不强调距离衰减。
- `FIXED` 可避免音量随距离剧烈变化，便于优先判断方向与跟踪。
- 后续若需要更真实的音响效果，可逐步恢复 `INVERSE_SQUARED` 或调整 directivity。

---

## Phase 33 技术交接：boombox 空间音频正式实现

### 1. 阶段概述

本阶段将 boombox 空间音乐从验证态收敛为正式实现：保留已验证成功的 PICO spatial audio 播放链路，恢复真实距离衰减，并删除前期调试入口和远端上报逻辑。

### 2. 当前正式播放链路

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

boombox 音乐仍由 `MusicModule.playNextSpatialMusicAt(entity)` 驱动，核心路径为：

```kotlin
private fun playSpatialMusicAt(entity: Entity, fileName: String, volume: Float): Boolean {
    releaseSpatialMusicPlayer()
    return runCatching {
        ensureObjectAudio(entity)
        val resource = getSpatialAudioResource(fileName)
        spatialMusicPlayer = entity.prepareAudio(resource)?.apply {
            setVolume(volume.coerceIn(0f, 1f))
            setLoop(false)
            play()
        }
        spatialMusicPlayer != null
    }.getOrDefault(false)
}
```

资源加载必须使用 `asset://` 形式：

```kotlin
AudioResource.load(fileName, "asset://$fileName", LoadType.FROM_ASSETS)
```

关键经验：普通 Android `MediaPlayer` 可使用裸 assets 文件名，但 PICO spatial audio 链路需要 `asset://xxx.wav`，否则可能无声。

### 3. 当前 ObjectAudio 配置

```kotlin
ObjectAudioComponent(
    volume = 1.0f,
    directivity = Directivity(pattern = 0f, sharpness = 0f),
    distanceAttenuationMode = DistanceAttenuationMode.INVERSE_SQUARED,
    reverbVolume = 0f
)
```

设计说明：

- `INVERSE_SQUARED` 用于恢复更真实的距离衰减，声源仍绑定在 boombox 实体上。
- `Directivity(pattern = 0f, sharpness = 0f)` 暂时保持全向，避免方向性与距离衰减同时变化导致听感难以判断。
- `DEFAULT_SPATIAL_MUSIC_VOLUME = 0.35f` 保持低音量，便于用户判断空间定位和跟随效果。

### 4. 已删除的临时调试逻辑

- 删除 `SceneAssetsBoomboxDebugReporter` 远端 HTTP 上报对象。
- 删除所有 `debug-point` 上报块。
- 删除 HMD 前方 `spatialAudioTestEntity` 测试声源。
- 删除 debug 面板中的：
  - `测试普通音乐`
  - `测试空间音乐`
  - `停止音乐测试`
- 删除 Manifest 中仅用于 HTTP debug server 的 `android:usesCleartextTraffic="true"`。

### 5. 保留的调试能力

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/ui/DebugActionPanel.kt`

仍保留一个 debug 面板按钮，用于在开发阶段直接切换 boombox action：

- 未开启时：点击触发 `StartBoomboxActionController.ACTION_ID`。
- 已开启时：点击触发 `StopBoomboxActionController.ACTION_ID`。
- 请求来源仍为 `InteractionActionSource.DEBUG`，可复用当前 action 优先级和抢占机制。

### 6. 验证结果

- `./gradlew :app:compileDebugKotlin` 编译通过。
- `app/src/main` 下已确认无临时 debug reporter、音频测试按钮、HMD 测试声源和 cleartext 调试开关残留。

---

## Phase 34 技术交接：boombox 音量与收音机效果可行性

### 1. 当前音量参数

文件：`/Users/bytedance/MateFairy/app/src/main/java/com/example/matefairy01/audio/MusicModule.kt`

```kotlin
private const val DEFAULT_SPATIAL_MUSIC_VOLUME = 0.18f
private const val OBJECT_AUDIO_SOURCE_VOLUME = 0.85f
```

播放时的有效响度由两层控制共同决定：

1. `AudioPlayerController.setVolume(DEFAULT_SPATIAL_MUSIC_VOLUME)` 控制当前播放实例音量。
2. `ObjectAudioComponent.volume = OBJECT_AUDIO_SOURCE_VOLUME` 控制空间声源总音量。

当前空间声源配置：

```kotlin
ObjectAudioComponent(
    volume = OBJECT_AUDIO_SOURCE_VOLUME,
    directivity = Directivity(pattern = 0f, sharpness = 0f),
    distanceAttenuationMode = DistanceAttenuationMode.INVERSE_SQUARED,
    reverbVolume = 0f
)
```

### 2. 距离衰减限制

根据本地 PICO Spatial SDK 文档，`DistanceAttenuationMode` 当前只提供：

- `FIXED`：不随距离衰减。
- `INVERSE_SQUARED`：平方反比距离衰减。

当前未发现可配置参数：

- 参考距离 / min distance。
- 最大距离 / max distance。
- rolloff factor。
- 自定义 attenuation curve。

因此，如果用户继续觉得衰减不明显，优先调节：

- `DEFAULT_SPATIAL_MUSIC_VOLUME`
- `OBJECT_AUDIO_SOURCE_VOLUME`
- boombox 与 HMD/精灵之间的空间距离

### 3. 收音机失真效果可行性

已调查能力：

- `ObjectAudioComponent`：空间化、方向、距离衰减、reverb。
- `AudioMixerGroupResource`：统一 volume、playback speed。
- `AudioPlayerController`：播放、暂停、停止、循环、音量等基础控制。

未发现能力：

- 实时 distortion。
- EQ / band-pass / low-pass / high-pass。
- bitcrush / sample-rate reduction。
- 自定义 DSP callback。
- 对 PICO spatial audio 输出挂 Android `AudioEffect` 的稳定入口。

推荐实现方案：

1. **资源级方案**：离线制作 `*_radio.wav`，把音乐预处理成收音机效果，再通过当前 spatial audio 链路播放。
2. **叠加素材方案**：额外准备一条 radio static / crackle wav，作为低音量空间 SFX 绑定到 boombox，与音乐同时播放。
3. **不推荐方案**：直接尝试 Android `AudioEffect`，因为 PICO `AudioPlayerController` 没有暴露可绑定的 Android audio session id，稳定性不可控。
