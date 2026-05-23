# MateFairy01 技术文档总览

> 本文档汇总了项目各阶段的技术交接与架构设计文档，用于帮助后续开发人员或 Agent 快速理解项目技术细节与接口约定。
> 最后更新：2026-05-24

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

## Phase 5.1 技术交接文档：空间 UI 渲染层级与深度测试控制

### 1. 阶段概述
在 Phase 5.1 中，重点排查了 2D Compose UI 在 3D 空间中容易被大型模型遮挡的问题。通过对 SDK 源码的深入反编译和排查，确认了 `DrawOrderGroupComponent` 对 `AttachmentPanelComponent` 无效，且当前 SDK 缺乏控制其深度写入的公开 API。

### 2. 核心架构与类说明

#### 2.1 ViewLink 机制与深度遮挡限制
- **发现**: `AttachmentPanelComponent` 底层依赖 `ViewLink` 桥接 Android 原生视图，它不属于标准的 `ModelComponent` 或 `ParticleComponent`，因此无法通过 `DrawOrderGroupComponent` 进行排序，也无法直接在 `SpatialView` 层面通过修改 Material 禁用深度写入/测试（Depth Test）。

### 3. SDK/框架避坑指南

1. **空间 UI 防遮挡痛点记录**：
   在 PICO Spatial SDK 中，**对于基于 ViewLink 的 Compose UI 附件，目前不存在类似于 OpenGL `glDisable(GL_DEPTH_TEST)` 的公开 API。** 
   
   目前的最佳实践是根据交互场景做区分处理：
   - **交互面板（如输入框）**：可以动态修改其相对于玩家视角的空间物理坐标（如强行向头显拉近至 `0.65m`）来规避模型遮挡，因为这符合玩家进行输入交互的直觉。
   - **跟随气泡**：不建议为了规避遮挡而向玩家偏移位置，这会改变 UI 尺寸感知并容易引起用户的视觉辐辏冲突。目前此问题作为 Feature Request 挂起，等待官方 SDK 支持 Compose UI 的深度图层控制。
2. **实体组件遗漏陷阱**：
   使用 `attachments.entity(id)` 获取到的 Compose 面板映射实体，部分情况下可能不自带 `TransformComponent`。如果不手动判断并补全该组件，后续所有的 `setPosition` 操作将静默失效，导致 UI 无法显示。
   ```kotlin
   if (components[TransformComponent::class.java] == null) {
       components[TransformComponent::class.java] = TransformComponent()
   }
   ```
3. **四元数旋转 API 差异**：
   在 PICO Spatial SDK 中，若需使用四元数旋转一个三维向量，正确的方法签名是 `Quat.rotateVector(Vector3)`，而不是 `rotate()`。使用错误的名称将导致编译时 `Unresolved reference`。


## Phase 5.2 技术交接文档：LLM 容错机制与 HUD 级空间跟随方案

### 1. 阶段概述
在 Phase 5.2 中，主要解决了两个核心痛点：一是 DeepSeek 大模型输出格式不稳定导致的对话链路断裂（触发兜底台词）；二是放弃了不适合高频移动的 `WindowContainer`，重构了 3D 空间中的输入面板，使其具备类似 HUD 的“锁定跟随”与“始终朝向”能力。

### 2. 核心架构与类说明

#### 2.1 LLM 响应解析的智能容错 (`DeepSeekLLMProvider.kt`)
- **System Prompt 约束**：使用极度严厉的指令定义模型角色（“你是一个严格的接口服务器”），并明确指出“绝对不要输出 Markdown 标记和前置寒暄”。
- **双层兜底策略**：
  - 第一层：正常解析 JSON。
  - 第二层：尝试从混杂的文本中正则匹配 `{...}` 块。
  - 第三层：放弃 JSON 解析，将大模型返回的所有字符串直接作为纯文本，组装成默认状态（`neutral`, `none`）下发展示。这大幅提升了业务的鲁棒性。

#### 2.2 HUD 级锁定跟随计算 (`HomeStage.kt`)
- **废除 `GameUIContainer` 系统窗口**：移除了 `Main.kt` 中冗余的系统级窗口。
- **欧拉角前向向量计算**：在 PICO Spatial SDK 中，通过四元数乘法计算前向向量常因 API 版本差异导致编译失败（如 `rotate` 或 `rotateVector` 不存在）。本项目沉淀了最稳定的原生计算方案：
  ```kotlin
  val hmdPose = hmdTrackingData.hmdPose
  val euler = hmdPose.rotation.toEulerAngles() // 返回度数
  val yawRad = Math.toRadians(euler.yaw.toDouble())
  val pitchRad = Math.toRadians(euler.pitch.toDouble())
  
  // 计算前向向量 (PICO 中前向为 -Z，Y朝上)
  val forwardX = -kotlin.math.sin(yawRad) * kotlin.math.cos(pitchRad)
  val forwardY = kotlin.math.sin(pitchRad)
  val forwardZ = -kotlin.math.cos(yawRad) * kotlin.math.cos(pitchRad)
  
  // 结合头部坐标和偏移距离计算最终目标位置
  val targetPos = Vector3(
      hmdPose.position.x + (forwardX * 0.65f).toFloat(),
      hmdPose.position.y + (forwardY * 0.65f).toFloat() - 0.15f,
      hmdPose.position.z + (forwardZ * 0.65f).toFloat()
  )
  inputEntity.components[TransformComponent::class.java]?.setPosition(targetPos)
  ```

#### 2.3 交互状态提升与生命周期 (`TextInputProvider.kt` & `SharedUIManager.kt`)
- 保留了全局单例 `SharedUIManager` 用于管理输入状态，这避免了在 `InputControllerManager`（硬件输入监听）和 `HomeStage`（渲染主循环）之间来回传递复杂的回调。
- 新增 `lastActiveTime`，在 UI 侧通过 `LaunchedEffect` 每秒轮询超时时间，超过 10 秒即触发关闭事件，实现了与渲染解耦的倒计时机制。

### 3. SDK/框架避坑指南

1. **`WindowContainer` 的局限性**：
   `WindowContainer` 适用于静态的、或者由用户主动用手柄拖拽布置的大型 2D 业务面板。**它不支持在代码中通过逐帧调用位置刷新来实现 6DoF 锁定跟随**。如果需要做一个随叫随到、贴脸跟随的悬浮 UI，必须使用原生 3D `AttachmentPanel` + 每帧手动修改 `TransformComponent`。
2. **多余的 Attachment 实体**：
   在 3D 场景中，只要 `attachments { ... }` 闭包里声明了组件，无论是否当前处于显示状态，如果获取不到该实体并对其进行妥善的隐藏或移除操作，它都会残留在场景中。必须确保不需要渲染时，关闭相关的 boolean 开关，或者确保实体池中只有一个唯一映射。
3. **PICO 欧拉角单位**：
   `Quat.toEulerAngles()` 返回的 `yaw`, `pitch`, `roll` 属性，其实际单位通常是**度（Degrees）**。在代入 `kotlin.math.sin()` 或 `cos()` 进行数学计算前，必须先使用 `Math.toRadians()` 转换为弧度，否则会导致计算出的跟随位置发生极速且混乱的抖动。
