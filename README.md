# MateFairy

MateFairy 是一个面向 PICO OS 6 的空间陪伴应用。它把一个会飞行、会看向玩家、会被触碰打断、能和现实/虚拟物体互动的精灵放进 Full Space 场景里，再用 LLM、MCP 工具、长期记忆和人格设定把它从一个 3D 模型推进到一个有陪伴感的空间角色。

当前工程以 Kotlin + Jetpack Compose + PICO Spatial SDK 为基础，主场景运行在 Stage 中，UI 通过 Spatial Attachment 和 Planar WindowContainer 呈现，3D 内容由 Spatial Editor bundle、GLB/USDZ 资源和 ECS 系统共同驱动。

## 当前体验

进入应用后，用户会看到一个 MateFairy 精灵出现在混合现实空间中。精灵会围绕玩家自然悬浮和跟随，玩家可以通过手柄、手势、语音或文本与它对话，也可以直接触碰它、点击球类、操作小黄鸭和音响等虚拟物体。AI 回复会以空间对话气泡显示，同时驱动情绪动画或具体动作。

应用目前包含这些关键体验：

- 精灵跟随玩家移动，并在合适距离内随机游走、悬停、转身看向玩家。
- 玩家可以长按手柄 trigger 语音输入，双击 trigger 打开文本输入。
- 手眼模式下支持三次拍手触发语音输入、空中双捏触发文本输入。
- 玩家直接捏/触碰精灵时，会中断当前动作并触发抖动与生气动画。
- 对话可以触发跳舞、踢足球、打开/关闭音响、挤小黄鸭等动作。
- 足球、篮球可被直接点击并沿玩家朝向飞出。
- 小黄鸭可播放动画和音效，boombox 可播放/切换/停止空间音乐。
- 精灵名称、对玩家的称呼和五类人格属性可在空间设置面板中编辑。
- 对话系统带有短期上下文、Room 持久记忆、事实抽取、长期叙事记忆和人格文件。
- MCP / 本地工具可为 LLM 提供联网搜索等外部信息能力。

## 技术栈

- 语言：Kotlin
- UI：Jetpack Compose + PICO Spatial UI
- 空间框架：PICO Spatial SDK `0.11.7`
- 构建：Gradle `8.13.x`、AGP `8.13.2`、Kotlin `2.0.0`、KSP
- 持久化：Room `2.6.1` + 应用私有文件 Markdown
- 网络：OkHttp
- 运行目标：PICO OS 6，`minSdk = 35`，`targetSdk = 35`
- ABI：`arm64-v8a`

## 工程入口

主要入口在 `app/src/main/java/com/example/matefairy01/`：

- `Main.kt`：注册 `DefaultStage` 和 `game_user_ui` Planar WindowContainer。
- `platform/SpatialApplication.kt`：读取 `app_config.json`，创建 application-scope `MateFairyRuntime`，启动 Spatial app。
- `platform/LaunchActivity.kt`：继承 `SpatialLaunchActivity`，在 `onPause` 时 flush 记忆写入队列。
- `runtime/MateFairyRuntimeFactory.kt`：集中装配 LLM、MCP、记忆、动画、音乐、动作和编排器。
- `content/HomeStage.kt`：主 Stage 场景，负责加载 3D 资源、注册 ECS 系统、启动追踪、挂载空间 UI。

运行模式由 `AndroidManifest.xml` 配置为 Stage：

- `pico.spatial.stage.id = YourStage`
- `pico.spatial.stage.style = 1`，即 Mixed Stage，虚拟内容与现实环境同时显示
- 需要 Full Space，因为项目使用 HMD、手部、手柄、mesh/plane 感知等空间能力

## 架构概览

```text
SpatialApplication
    -> AppConfigLoader
    -> MateFairyRuntimeFactory
        -> LLMProvider + McpManager + WebSearchTool
        -> ContextMemorySystem
        -> Room stores: EpisodicStore / SemanticStore
        -> PermanentStore: SOUL.md / USER.md / MEMORY.md
        -> IngestionWorker + DreamJob
        -> AnimationModule + EmotionEngine
        -> Interaction action registries
        -> ConversationOrchestrator

mainApp
    -> DefaultStage
        -> HomeStage
            -> SpatialView
            -> Entity tree
            -> ECS systems
            -> Spatial attachments
    -> WindowContainer("game_user_ui")
```

核心原则是把“玩家输入 -> AI 决策 -> 情绪/动作 -> 空间表现”拆成稳定的几层：UI 只负责输入和展示，`ConversationOrchestrator` 负责对话编排，`AnimationModule` 和 action 系统负责具体表现，记忆系统在后台异步沉淀上下文。

## 模块说明

### 空间场景

`content/HomeStage.kt` 是当前最重要的场景文件。它做了几件事：

- 创建 `rootEntity`，挂载 `PhysicsWorldComponent`。
- 创建 HMD proxy entity，用来承载跟随头显的 UI attachment。
- 加载 `asset://editor-asset.bundle` 中的 `MyScene`。
- 加载精灵模型 `asset://pico_robot_animated_new.glb`。
- 从 editor scene 中识别 `Football`、`Basketball`。
- 从 APK assets 加载 `boombox_new.glb` 和 `rubber_duck_toy.glb`。
- 给可交互对象注册 `InteractionObjectComponent`，给精灵主体注册 `InteractionActorComponent`。
- 注册 `FairyBehaviorSystem`、`InteractionActionSystem`、`PickedObjectFollowSystem`、`PlayerFairyInteractionSystem`、`ResourcePhysicsActivationSystem`。
- 启动 HMD、Controller、Hand tracking、Spatial Mesh 和语义感知管理器。

`editor-asset/` 是 Spatial Editor 工程，Gradle 插件会把其中的 SpatialPackContent 编译为 `editor-asset.bundle`，运行时由 `HomeStage` 通过 `AssetBundle.load("asset://editor-asset.bundle")` 加载。

### 精灵行为

`behavior/FairyBehaviorSystem.kt` 驱动精灵主体运动。状态包括：

- `RANDOM_MOVING`：在玩家附近随机游走。
- `RANDOM_WAITING`：随机等待/悬浮，并触发闲置动画。
- `FOLLOWING`：超出外圈距离后向玩家视野中心附近回归。
- `FOLLOW_HOVERING`：跟随完成后短暂停留并面向玩家。

行为系统还会处理这些边界：

- action lock 存在时让出控制权，避免常驻跟随和任务动作抢控制。
- 玩家触碰精灵时让出控制权，由 player-fairy interaction 接管。
- 携带物体时保留跟随目标，避免拿起/放下动作后漂移。
- 通过 spatial mesh 做移动约束，减少穿入真实环境网格。

### 动画与情绪

`animation/AnimationConfig.kt` 映射精灵 GLB 中的动画轨道。当前包含：

- 情绪动画：`angry`、`happy`、`sad`
- 指令动画：`dance`、`disco`、踢球、拾取等
- 常驻动画：idle、look around、wave、walk/dash 等

`animation/AnimationModule.kt` 是统一动画调度器。它按优先级处理动画：

1. 指令动作最高优先级。
2. 情绪动画中等优先级。
3. 常驻 idle/moving 动画最低优先级。

任务型 action 可以订阅动画生命周期，玩家触碰精灵也会通过同一调度器播放生气动画，从而避免多个系统同时抢动画资源。

### 输入方式

`input/` 下是输入层：

- `InputControllerManager`：右手柄 trigger 长按 1 秒启动语音输入，短按双击打开文本输入。
- `HandClapDetector`：双手三次拍手触发语音输入。
- `HandDoublePinchDetector`：空中双捏触发文本输入。
- `HandFairyTouchDetector`：手指/掌心进入精灵半径后触发玩家触碰精灵动作。
- `TextInputProvider`、`VoiceInputProvider`：给 UI 和对话链路提供统一输入结果。

### AI 对话与动作决策

主链路在 `orchestrator/ConversationOrchestrator.kt`：

1. 接收用户输入。
2. 写入短期上下文。
3. 调用 `ContextMemorySystem.buildPromptMessages()` 注入 SOUL、USER、相关回忆和短期窗口。
4. 调用 `ILLMProvider.chat()` 获取结构化回复。
5. 用 `ActionIntentFallbackResolver` 修正模型可能漏掉的动作意图。
6. 用 `DefaultBehaviorDecisionMaker` 决定是否触发情绪或动作。
7. 分发到 `EmotionEngine` 和 `ActionRegistry`。
8. 异步把对话送入 IngestionWorker，进行摘要、事实抽取和长期记忆沉淀。

LLM 回复协议固定为 JSON：

```json
{
  "status": "ok",
  "reply_text": "回复给玩家的自然语言",
  "emotion": "neutral",
  "action_intent": "none"
}
```

`ai/DeepSeekLLMProvider.kt` 支持普通 JSON 回复，也支持 OpenAI-compatible tool calling。若 MCP 或本地工具可用，会先让模型调用工具，再要求模型输出最终结构化 JSON。

### MCP 与本地工具

`mcp/` 实现了 streamable HTTP MCP 客户端：

- `McpClient`：负责 initialize、tools/list、tools/call。
- `McpManager`：聚合多个 server 的工具，并把工具包装成 OpenAI function calling schema。
- `McpServerConfig`：从 `app_config.json` 解析 MCP server。

`tools/WebSearchTool.kt` 是内置本地工具，支持：

- DuckDuckGo HTML 搜索兜底。
- Brave / Tavily API 搜索。
- 缺少 API Key 时自动降级到 DuckDuckGo。

### 记忆系统

记忆系统分为几层：

- 短期窗口：`ContextMemorySystem` 保留最近几轮对话。
- 长期摘要：滑出短期窗口的对话会异步总结。
- L2 情景记忆：`EpisodicStore` 使用 Room 保存对话片段和向量。
- L3 语义记忆：`SemanticStore` 保存事实和知识三元组。
- L4 永久层：`PermanentStore` 在应用私有目录维护 `SOUL.md`、`USER.md`、`MEMORY.md`。
- Dream：`DreamJob` 在空闲期把事实和情景记忆蒸馏到永久层。

默认文件路径在设备应用私有目录：

```text
/data/data/com.example.matefairy01/files/memory/SOUL.md
/data/data/com.example.matefairy01/files/memory/USER.md
/data/data/com.example.matefairy01/files/memory/MEMORY.md
```

`SOUL.md` 是精灵人设，用户设置面板会写入它；`USER.md` 和 `MEMORY.md` 由后台 Dream 流程逐步生成。

### 人格设定

`persona/FairySoulProfile.kt` 和 `ui/FairySettingsUI.kt` 负责精灵属性设置。当前支持：

- 精灵名称。
- 精灵对玩家的称呼。
- 五类人格维度：外向性、尽责性、开放性、亲和性、情绪稳定性。
- 每类人格有低/中/强/超强四档，并带有中文人格说明。

保存后，新的 SOUL 配置会在下一次 AI 回复时进入 system prompt，影响语言风格，但不覆盖工程动作协议。

### 交互动作

`interaction/` 是实体交互 action 层。当前已接入对话动作分发的能力包括：

- `play-football`：精灵飞向足球，播放踢球动画，给足球施加速度，然后回到行为系统。
- `start-boombox` / `stop-boombox`：精灵靠近 boombox，播放/停止空间音乐，并可触发 disco 动画。
- `squeeze-rubber-duck` / `put-down-rubber-duck`：精灵拿起小黄鸭，播放动画和音效，再放下。
- `dance` / `disco` 等普通非任务动画：通过 `GenericAnimationHandler` 直接映射。

`RealWorldSemanticActionControllers.kt` 中已有 `stay-on-chair` / `leave-chair` 的 action 实现，语义感知管线也已启动；但当前 `MateFairyRuntimeFactory` 还没有把这两个 action 注册进对话 action registry，所以它们属于“已实现但需接线验证”的能力。

### 现实感知

`perception/` 下有两条空间感知链路：

- `SpatialMeshManager`：订阅 MeshAnchor，把真实空间 mesh 转成碰撞体，用于遮挡/碰撞/移动约束。
- `RealWorldSemanticManager`：订阅 Mesh 和 Plane 语义 anchor，缓存现实物体语义快照。

这些能力必须在 Full Space 下运行。PICO Emulator 对真实手部、mesh、语义能力支持有限，最终效果需要真机验证。

## 资源说明

主要运行时资源在 `app/src/main/assets/`：

- `pico_robot_animated_new.glb`：当前精灵主模型和动画资源。
- `boombox_new.glb`：音响模型。
- `rubber_duck_toy.glb`：小黄鸭模型。
- `Dying_Me_instrumental.wav`、`火星时代教育.wav`：boombox 空间音乐列表。
- `rubber_duck_voice.mp3`：小黄鸭音效。
- `app_config.json`：AI、MCP、WebSearch、记忆系统配置。

Spatial Editor 源工程在 `editor-asset/src/main/res3d/SpatialPackContent/`，当前场景源文件为 `Sources/Scenes/MyScene.usda`，包含足球、篮球、IBL 等场景资源。

## 配置说明

`app/src/main/assets/app_config.json` 是运行时配置入口，主要字段：

- `ai.provider`：`deepseek` 或 `mock`。
- `ai.systemPrompt`：基础人格提示。
- `ai.deepseek`：OpenAI-compatible chat endpoint、model、超时和 token 参数。
- `mcpServers`：MCP server 列表。含 placeholder 的 server 会被自动跳过。
- `webSearch`：本地联网搜索工具配置。
- `memory`：摘要、事实抽取、Dream、召回、Embedding 参数。

安全注意：

- 不要在公开仓库长期保留真实 API Key。
- 如果真实 Key 已被提交到远端，应立即在服务商后台轮换。
- 更稳妥的做法是把密钥迁移到 `local.properties`、CI secrets、设备本地文件或运行时注入方案。

## 构建与运行

环境要求：

- Android Studio 2025.1.x 或兼容版本。
- Android SDK Platform 35。
- PICO Spatial Plugin。
- 支持 PICO OS 6 的真机。模拟器可用于部分调试，但感知和手部能力不完整。

常用命令：

```bash
./gradlew projects
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

运行步骤：

1. 用 Android Studio 打开仓库根目录。
2. 确认 `app/src/main/assets/app_config.json` 中的 AI / Embedding / MCP 配置可用。
3. 连接 PICO OS 6 设备。
4. 运行 `app` 模块。
5. 启动后应用进入 Mixed Stage，加载精灵、Editor 场景、空间 UI 和追踪系统。

## 测试

当前保留的 JVM/Android 测试主要覆盖：

- 记忆系统离线集成：`MemoryPipelineTest`
- SOUL 人设 Markdown 编解码：`FairySoulProfileMarkdownCodecTest`
- PermanentStore 人设读写：`PermanentStoreSoulProfileTest`
- 运行时上下文注入：`RuntimeContextInjectionTest`

部分 `LiveTest` 依赖真实网络或外部 API Key，不适合作为默认 CI 必跑项。

## 当前仓库边界

仓库在最近一次清理后只保留游戏运行和构建所需内容：`app/`、`editor-asset/`、Gradle 配置和 README。以下内容被保留在本地但不再跟踪到 GitHub：

- IDE 配置和 Kotlin/Gradle 缓存。
- Agent 技能、SDK 离线文档、模板工程。
- 项目沉淀资料和阶段报告。
- 一次性 debug 记录、dump 脚本和探索性测试探针。

这样做是为了让 GitHub 仓库更接近一个可交付的游戏工程，而不是把本地调试环境和资料库一起发布。

## 已知工程备注

- `settings.gradle.kts` 当前仍包含 `include(":mylibrary")`，但当前 Git 跟踪树中没有该模块源码；如后续不再使用，应移除该 include。
- `app_config.json` 仍是 assets 内配置文件，适合快速原型，不适合长期保存真实密钥。
- PICO Spatial 感知能力需要 Full Space 和真机环境；模拟器只能验证部分 UI、构建和基础流程。
- `stay-on-chair` / `leave-chair` action 类已存在，但当前运行时尚未完整注册到对话 action 分发链路。
