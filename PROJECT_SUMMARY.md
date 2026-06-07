# MateFairy 项目总结

> **平台：** PICO OS 6 (Android) · **SDK：** PICO Spatial SDK 0.10.7  
> **语言：** Kotlin + Jetpack Compose · **包名：** `com.example.matefairy01`

---

## 一、产品概述

MateFairy 是一个运行在 PICO XR 头显上的 **3D AI 陪伴精灵**应用。用户戴上头显后，一只 PICO 机器人小精灵会悬浮在视野中，自主在玩家周围飞行、跟随，并与用户进行自然语言对话。精灵根据 AI 回复展现对应情绪，并可执行挥手、点头等肢体动作。

---

## 二、现有功能清单

| 功能 | 状态 | 描述 |
|------|------|------|
| 3D 精灵渲染 | ✅ | 加载 `pico_robot_animated.glb`，挂载到 Stage 场景 |
| 自主行为 AI | ✅ | 有限状态机：随机飞行 → 跟随 → 悬停，附带惯性滑行 |
| 对话气泡 | ✅ | AI 回复文字浮于精灵头顶，LookAt 跟随玩家视角 |
| 文字输入对话 | ✅ | 手柄触发打开输入框，支持 10s 自动超时关闭 |
| 语音输入对话 | ✅ | 基于 Android `SpeechRecognizer`，拍手 3 次触发录音 |
| 手势拍手检测 | ✅ | 检测双手掌心距离 <15cm 为拍手，连续 3 次触发录音 |
| LLM 对话（DeepSeek） | ✅ | HTTP 调用 DeepSeek API，强制结构化 JSON 响应协议 |
| LLM Mock 模式 | ✅ | 无网络时可用 MockLLMProvider 替代 |
| 上下文记忆 | ✅ | 滑动窗口（近 3 轮）+ 异步压缩长期摘要 |
| 情绪系统 | ✅ 框架 | EmotionEngine 框架完整，Renderer 目前为 NoOp |
| 动作分发 | ✅ 框架 | ActionRegistry 策略模式，处理器目前为空注册 |
| 行为决策 | ✅ | 情绪/动作优先级仲裁（负面情绪拦截动作） |
| 骨骼动画 | ✅ | 5 条动画轨道：待机、旋转跳跃、好奇张望、极速冲刺、挥手 |
| HMD 追踪 | ✅ | 实时获取头显位姿，用于精灵跟随逻辑 |
| 手部追踪 | ✅ | HandTrackingProvider，用于拍手检测 |
| 手柄追踪 | ✅ | ControllerTrackingProvider，用于按键触发输入 |
| Debug 自动测试 | ✅ | Debug 包可配置随机 Prompt 定时自动发送 |

---

## 三、代码架构

### 3.1 整体架构图

```
Application Layer
├── platform/
│   ├── LaunchActivity.kt       # 入口 Activity
│   └── SpatialApplication.kt  # Application 初始化
│
├── Main.kt                     # 声明两个 Spatial Container
│   ├── DefaultStage → HomeStage()        # 全沉浸 3D 场景
│   └── WindowContainer (game_user_ui)    # 覆盖层 UI（输入/录音指示）
│
├── content/
│   └── HomeStage.kt            # 3D 场景主入口，负责：
│                                  - 加载 AssetBundle + GLB 模型
│                                  - 启动 HMD/手柄/手部追踪
│                                  - 管理对话 Attachment 气泡
│                                  - 处理输入并调用 AI
│
Runtime Layer
├── runtime/
│   ├── MateFairyRuntime.kt     # 运行时对象（ConversationOrchestrator + AvatarController）
│   └── MateFairyRuntimeFactory.kt  # 工厂，集中组装所有核心依赖
│
├── orchestrator/               # 编排层
│   ├── ConversationOrchestrator.kt   # 主流程：输入→上下文→LLM→决策→分发
│   ├── decision/
│   │   ├── BehaviorDecisionMaker.kt          # 决策接口
│   │   └── DefaultBehaviorDecisionMaker.kt   # 情绪/动作优先级仲裁
│   ├── ports/
│   │   ├── EmotionCommandPort.kt   # 情绪分发接口
│   │   └── ActionCommandPort.kt    # 动作分发接口
│   └── adapters/
│       ├── EmotionEnginePortAdapter.kt
│       └── ActionRegistryPortAdapter.kt
│
Domain Layer
├── ai/                         # LLM 模块
│   ├── ILLMProvider.kt         # 接口（chat + summarize）
│   ├── AIResponse.kt           # 结构化响应：reply_text / emotion / action_intent
│   ├── ChatMessage.kt
│   ├── DeepSeekLLMProvider.kt  # 真实实现，OkHttp 调用 DeepSeek API
│   ├── MockLLMProvider.kt      # 离线 Mock
│   └── LLMProviderFactory.kt   # 根据 AppConfig 选择 Provider
│
├── memory/
│   └── ContextMemorySystem.kt  # 滑动窗口 + 异步摘要压缩
│
├── emotion/
│   ├── EmotionEngine.kt        # 情绪状态机 + 频控
│   ├── IEmotionRenderer.kt     # 渲染接口
│   └── NoOpEmotionRenderer.kt  # 空实现（待扩展）
│
├── action/
│   ├── ActionRegistry.kt       # 策略模式动作分发器
│   └── IActionHandler.kt       # 处理器接口
│
├── animation/
│   ├── FairyAnimation.kt       # 动画枚举（5条轨道）
│   ├── AnimationType.kt        # IDLE / MOVING
│   ├── AnimationConfig.kt      # 动画分组配置
│   ├── AnimationController.kt  # 控制接口
│   └── AnimationModule.kt      # 实现：骨骼动画管理 + 频控
│
├── avatar/
│   ├── AvatarController.kt         # 接口
│   └── DefaultAvatarController.kt  # 代理 AnimationModule 的动画请求
│
├── behavior/                   # ECS 行为系统
│   ├── FairyBehaviorComponent.kt   # 精灵行为数据组件
│   ├── FairyBehaviorSystem.kt      # 每帧运行的状态机（跟随/随机/悬停/惯性）
│   ├── HMDTagComponent.kt          # HMD 实体标记组件
│   └── BehaviorRuntimeDependencies.kt  # 全局依赖注入点（AvatarController）
│
├── input/                      # 输入模块
│   ├── IUserInputProvider.kt
│   ├── TextInputProvider.kt    # 文字输入状态管理
│   ├── VoiceInputProvider.kt   # Android SpeechRecognizer
│   ├── InputControllerManager.kt   # 手柄按键统一管理
│   └── HandClapDetector.kt     # 手势拍手检测（3次 → 触发录音）
│
├── ui/                         # UI 组件
│   ├── FairyDialogueUI.kt      # 对话气泡 / 文字输入框 / 录音指示器
│   ├── GameUIContainer.kt      # 输入容器（含10s超时逻辑）
│   └── SharedUIManager.kt      # 全局 TextInputProvider / VoiceInputProvider 单例
│
└── config/
    └── AppConfig.kt            # AppConfig / AIConfig / DeepSeekConfig，从 assets/app_config.json 加载
```

### 3.2 核心数据流

```
用户输入（文字 / 语音 / 拍手）
       ↓
InputControllerManager / HandClapDetector
       ↓
HomeStage.handleUserInput()
       ↓ (协程)
ContextMemorySystem.buildPromptMessages()  ← 短期记忆 + 长期摘要
       ↓
ILLMProvider.chat()  →  AIResponse { reply_text, emotion, action_intent }
       ↓
ConversationOrchestrator
       ↓
BehaviorDecisionMaker.decide()  →  BehaviorDecision（优先级仲裁）
       ↓
EmotionCommandPort.triggerEmotion()   →  EmotionEngine  →  IEmotionRenderer
ActionCommandPort.dispatchAction()    →  ActionRegistry →  IActionHandler
       ↓
UI：对话气泡更新（dialogueText）
```

### 3.3 精灵行为状态机

```
RANDOM_MOVING ──到达目标──→ RANDOM_WAITING ──等待结束──→ RANDOM_MOVING
      │                            │
      │ 超出 outerRadius           │ 超出 outerRadius
      ↓                            ↓
   FOLLOWING ──进入 innerRadius──→ FOLLOW_HOVERING ──等待结束──→ RANDOM_MOVING

所有状态切换时触发"惯性滑行"（INERTIA_SLIDING），用速度衰减模拟物理感）
```

---

## 四、配置文件

**`assets/app_config.json`** — 运行时配置：

```json
{
  "ai": {
    "provider": "deepseek",           // "mock" | "deepseek"
    "systemPrompt": "你是...",
    "deepseek": {
      "apiKey": "sk-xxx",
      "model": "deepseek-chat",
      "maxTokens": 512,
      "temperature": 0.7
    },
    "autoTest": {
      "enabled": false,
      "intervalMs": 3000,
      "promptPool": ["你好", "给我讲个故事"]
    }
  }
}
```

---

## 五、关键约束 & 已知限制

| 项 | 说明 |
|----|------|
| **情绪渲染** | `NoOpEmotionRenderer` — 框架已通，具体表现（BlendShape / 材质变化）**尚未实现** |
| **动作处理器** | `ActionRegistry` 没有注册任何处理器（wave/nod 等动作无实际响应） |
| **HomeStage 重复逻辑** | `HomeStage.handleUserInput()` 与 `ConversationOrchestrator` 存在逻辑重叠，`AppModule` 旧式依赖注入和新 `MateFairyRuntimeFactory` 并存 |
| **TTS 缺失** | AI 回复只显示文字，无语音播报 |
| **Full Space 限制** | 手部/HMD 追踪、Stage 均依赖 Full Space，不支持 Shared Space |

---

## 六、待开发模块方向

基于当前架构，以下模块可直接插入：

1. **情绪渲染器**：实现 `IEmotionRenderer`，驱动 BlendShape 动画或粒子效果
2. **动作处理器**：实现 `IActionHandler`，注册 `wave / nod / shake_head / think` 对应骨骼动画
3. **TTS 语音播报**：接入 Android TTS / 第三方 SDK，在 `ConversationOrchestrator` 回复后触发
4. **更多输入方式**：实现 `IUserInputProvider` 接口，扩展眼神注视触发、手势识别等
5. **更多 LLM 接入**：实现 `ILLMProvider`，接入豆包、GPT 等
6. **场景/环境互动**：利用 PICO Plane Detection / Spatial Anchor，让精灵"落在"真实桌面上
