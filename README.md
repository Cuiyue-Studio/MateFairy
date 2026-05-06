# MateFairy

MateFairy 是一个基于 PICO Spatial SDK 开发的 PICO SWAN XR 应用。该项目在混合现实（MR）环境中构建了一个可跟随玩家的虚拟小精灵。通过接入 AI 大语言模型，小精灵能够根据玩家设定的背景与性格，与玩家进行多模态的交互。

## 核心功能

* **空间跟随机制**：基于头显（HMD）的位姿追踪数据，小精灵能够在三维空间中平滑跟随玩家移动，并保持在设定的视场角与距离范围内。
* **多模态交互**：
  * **手势与语音**：集成手部追踪（Hand Tracking），支持通过特定手势（如连续拍手）唤醒语音输入引擎。
  * **手柄与文本**：支持通过 PICO 手柄射线与空间 UI 面板进行文本交互。
* **AI 驱动响应**：内置上下文记忆系统，将用户的自然语言输入发送至 LLM，并将返回的文本通过空间气泡（Spatial Attachment）实时展示。
* **情绪与动画系统**：解析 AI 返回的情绪与动作意图，调用 3D 模型的骨骼动画序列及渲染表现，实现拟人化反馈。

## 技术栈

* **开发语言**：Kotlin
* **核心框架**：PICO Spatial SDK (ECS 架构)
* **UI 框架**：Jetpack Compose (Spatial UI 扩展)
* **3D 资产**：GLB 模型，支持蒙皮骨骼动画
* **运行环境**：PICO OS 6 (MinSDK 35)

## 核心模块说明

主要代码位于 `app/src/main/java/com/example/matefairy01/` 目录下：

* `content/HomeStage.kt`：全空间场景容器，负责加载 3D 资产、挂载 ECS 组件以及绑定空间 UI 附件。
* `behavior/`：基于 ECS 架构的跟随逻辑系统，控制精灵实体在空间中的运动轨迹与姿态计算。
* `input/`：输入控制模块，包含手柄事件分发、手势动作检测（如拍手识别）及语音/文本提供者。
* `ai/`：大语言模型接口层，负责 Prompt 构建、多轮对话上下文管理以及模型回复的解析。
* `emotion/`：情绪与动作分发器，将 AI 意图映射为具体的动画播放指令。

## 构建与运行

1. 确保已安装最新版 Android Studio 及 PICO Spatial Plugin。
2. 克隆本仓库并在 Android Studio 中打开项目。
3. 连接支持 PICO OS 6 的真实设备或 PICO 模拟器。
4. 编译并运行 `app` 模块。运行后应用将进入全空间（Full Space）模式并加载 MR 场景。
