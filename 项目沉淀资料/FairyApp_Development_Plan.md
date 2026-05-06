# PICO Spatial SDK - “伴随精灵” (Fairy Companion) 空间应用开发计划

## 一、 项目概述
本项目旨在基于 **PICO Spatial SDK** 开发一款 **Stage（全空间）** 容器应用。核心体验为：一只具备AI能力的虚拟精灵陪伴在玩家身边，具备自主飞行、闲置行为、语音识别、多模态AI对话（文本+表情/动作）以及基于语义意图驱动的动作执行能力。

## 二、 核心架构与技术选型

### 1. 空间容器与基础渲染
*   **技术栈**: PICO Spatial SDK (Kotlin + Jetpack Compose) + ECS (实体组件系统)。
*   **容器选择**: `Stage` 容器（Full Space 全空间模式），用于获取玩家真实头部位置（HMD Tracking）。

### 2. 精灵行为与追踪引擎 (ECS System)
*   **HMD 追踪**: `HMDTrackingProvider` 实时获取玩家视角坐标，构建动态“圆周活动边界”。
*   **随机巡航与跟随**: 自定义 `FairyBehaviorSystem`，利用 `TweenAnimation` 平滑移动；在玩家移动超出阈值时自动跟随。
*   **闲置行为机**: 随机触发内置骨骼动画（`playAnimation`）与表情符号。

### 3. 可扩展与解耦的 AI/交互层 (Extensible Architecture)
为了保证系统具备高度的复用性和可维护性，将采用接口化、插件化的设计模式：
*   **AI API 抽象化 (`ILLMProvider`)**: 封装通用网络请求模块，支持轻松切换不同的大模型（如 DeepSeek, 豆包, OpenAI 等）。
*   **动作模组注册表 (`ActionRegistry`)**: 采用策略模式（Strategy Pattern）。每个动作（如“飞过来”、“跳舞”）实现 `IActionHandler` 接口，系统根据大模型解析的 `action_intent` 动态分发，便于后续零耦合增加新动作。
*   **情绪系统抽象 (`EmotionEngine`)**: 独立控制情绪频控和渲染，实现 `IEmotionRenderer`，可以对接 UI 表情或 3D 模型动画。

### 4. 智能上下文管理系统 (Context Memory System)
采用 **滑动窗口 + 异步并行压缩缓存** 策略，极大地提升精灵响应速度，同时解决上下文过长导致 Token 消耗与延迟增加的问题：
*   **滑动窗口 (Short-term Memory)**: 仅在内存中保留最近 $N$ 轮的原始对话（例如最近3轮），保证当前对话的绝对精确度。
*   **异步并行压缩 (Parallel Compression)**: 
    *   *您的设想非常巧妙！* 我们将在玩家思考/说话的间隙，或者精灵正在执行动作/播放语音的间隙（Idle Time），开启一个**后台协程 (Background Coroutine)**。
    *   该协程会将滑出窗口的历史对话发送给 LLM 进行**摘要压缩 (Summarization)**。
*   **缓存与组装 (Prompt Assembly)**: 
    *   压缩后的摘要被持久化缓存（Long-term Memory）。
    *   每次发起新对话时，Prompt 的组装公式为：`系统基础设定 (Persona) + 历史摘要缓存 (Long-term) + 最近 N 轮对话 (Short-term)`。
    *   这样在主对话链条中，**无需等待召回和压缩计算**，实现了低延迟的极速响应。

### 5. 初始化与人格配置面板 (Onboarding Setup)
*   游戏首次启动时，弹出基于 Compose Spatial UI 构建的 2D 设定面板（`PlanarWindowContainer`）。
*   玩家在此输入精灵的名字、性格（如“傲娇”、“温柔”）、背景故事等。
*   这些设定被转化为 JSON 或固定文本，持久化存储（如 DataStore），并作为 LLM 系统提示词（System Prompt）的最高优先级基座，确保人设绝不偏离。

---

## 三、 开发阶段划分 (Milestones)

### Phase 1: 基础工程架构与接口定义 (预计周期: 10%)
*   [ ] 使用 `FullStage` 模板初始化 PICO Spatial SDK 项目。
*   [ ] 定义核心解耦接口：`ILLMProvider`, `IActionHandler`, `IEmotionRenderer`。
*   [ ] 搭建项目的依赖注入或注册表结构。

### Phase 2: 初始面板设定与空间渲染 (预计周期: 20%)
*   [ ] 开发启动时的精灵人设配置面板（Compose UI）。
*   [ ] 导入精灵 3D 模型资产，编写 ECS 逻辑将模型加载到 Stage 中。
*   [ ] 接入 `HMDTrackingProvider` 实时获取玩家视角坐标。

### Phase 3: 行为树与空间交互引擎 (预计周期: 20%)
*   [ ] 实现 `FairyBehaviorSystem`：围绕玩家坐标的随机飞行与平滑转向。
*   [ ] 编写基础的闲置状态机（巡航 -> 停留 -> 播放随机动作 -> 巡航）。
*   [ ] 开发精灵头部的 Spatial UI 悬浮对话框（Billboard 效果）。

### Phase 4: 语音、AI接入与上下文缓存系统 (预计周期: 30%)
*   [ ] 集成 Android 语音转文本 (STT) 机制。
*   [ ] 实现 `ILLMProvider`，跑通“语音转文本 -> 请求AI -> UI显示文本”。
*   [ ] **重点开发**：实现并行的“滑动窗口+异步摘要”上下文管理系统，利用 Kotlin 协程在后台压缩历史对话并缓存。

### Phase 5: 意图调度与情绪模组 (预计周期: 20%)
*   [ ] 让 LLM 返回包含 `reply_text`, `emotion`, `action_intent` 的 JSON 结构。
*   [ ] 实现 `ActionRegistry`，开发 1~2 个基础动作模组（例如 "come_to_player" 飞向玩家）。
*   [ ] 完善 `EmotionEngine` 情绪渲染与频控策略。
*   [ ] 整体功能解耦测试与性能调优。