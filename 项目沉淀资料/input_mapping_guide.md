# MateFairy 输入与控制键位映射指南

本文档汇总 MateFairy 工程在 PICO Spatial SDK 空间下支持的玩家交互输入模式及触发条件，涵盖手眼/手势、手柄、空间 UI 与调试入口。

最后更新：2026-06-18

## 1. 手眼/手势模式 (Hand & Gaze Mode)

当前代码未接入 `EyeTrackingProvider`。工程里的“手眼模式”实际由 `HandTrackingProvider`、HMD/空间指针命中、凝视/射线目标与手势捏合共同构成。

| 交互功能 | 触发方式 | 底层实现/条件 |
| :--- | :--- | :--- |
| **触发语音输入（录音）** | **连续拍手 3 次** | `HandClapDetector` 计算左右手掌心 `HandJoint.Index.PALM` 距离，距离 `< 0.15m` 且从分开变为靠近时计为一次拍手；单次冷却 `300ms`，需在 `2000ms` 内累计 3 次。 |
| **呼出文本输入框** | **连续捏合 2 次** | `HandDoublePinchDetector` 直接读取 `HandTrackingProvider.dataFlow`，计算 `THUMB_TIP` 与 `INDEX_TIP` 距离；两次捏合间隔需 `<= 700ms`，触发后调用 `InputControllerManager.requestTextInput()`，复用手柄双击的文本输入链路。 |
| **踢飞足球 Football** | **注视/命中足球 + 食指拇指捏合 Tap** | `detectSpatialTapGesture` 绑定 `footballEntity`，触发后 `applyBallTapImpulse()` 给足球写入 `Vector3(0f, 3.0f, -3.0f)` 瞬时速度。 |
| **拍飞篮球 Basketball** | **注视/命中篮球 + 食指拇指捏合 Tap** | `detectSpatialTapGesture` 绑定 `basketballEntity`，触发后复用 `applyBallTapImpulse()`，给篮球写入同样的物理速度。 |
| **捏小黄鸭 Rubber Duck** | **注视/命中小黄鸭 + 食指拇指捏合 Tap** | `detectSpatialTapGesture` 绑定 `rubberDuckEntity`，触发 `playObjectAnimationOnTarget(..., maxDurationMs = 1500L)` 播放小黄鸭内部动画轨道，并调用 `playRandomRubberDuckSfxAt()` 播放 `rubber_duck_voice.mp3`，音效上限 `1.5s`。 |
| **音响开关 Boombox** | **注视/命中音响 + 长捏合 >= 1s** | `detectSpatialPointerEvent` 绑定 `boomboxEntity`，记录 down/up 的 `uptimeMillis`；持续 `>= 1000ms` 时，如果空间音乐正在播放则 `stopSpatialMusic()`，否则 `playNextSpatialMusicAt(boombox)` 开启播放。 |
| **音响切歌 Boombox** | **注视/命中音响 + 短捏合 < 1s** | 同一套 `detectSpatialPointerEvent`，持续 `< 1000ms` 时调用 `playNextSpatialMusicAt(boombox)` 切换到下一首空间音乐。 |

## 2. 手柄模式 (Controller Mode)

依托 `ControllerTrackingProvider` 监听 `ControllerActionData`。当前只消费右手柄 Trigger：`actionData.right?.triggerPressed`。

| 交互功能 | 触发手柄键位 | 底层实现/条件 |
| :--- | :--- | :--- |
| **触发语音输入（开始录音）** | **长按右侧 Trigger > 1s** | `InputControllerManager` 在 Trigger 按下后延迟 `1000ms` 检测，仍保持按下则调用 `startVoiceInput()`；松开 Trigger 时调用 `stopVoiceInput()`。 |
| **呼出文本输入框** | **双击右侧 Trigger** | 两次短按 Trigger 的间隔需 `<= 500ms`，触发 `startTextInput()`，并在视野内挂载 3D 输入面板。 |

## 3. 空间 UI 面板与调试操作 (Spatial UI Interactions)

当呼出输入框或展示 Debug 面板时，支持以下基于空间射线/注视点/指针的 UI 操作：

| 交互功能 | 触发方式 | 底层实现/位置 |
| :--- | :--- | :--- |
| **编辑文本消息** | **点击文本输入框并输入文字** | `FairyDialogueUI.TextInputDialog` 内部使用 Compose `TextField`，`onValueChange` 转发给 `TextInputProvider.updateText()`。 |
| **发送文本消息** | **点击输入面板上的「发送」按钮** | 仅在输入框不为空时启用，`onSubmit` 最终进入 `runtime.conversationOrchestrator.processUserInput(text)`。 |
| **关闭文本输入框** | **点击面板下方「X」悬浮按钮** | `GameUIContainer` 调用 `TextInputProvider.cancelInput()`；文本输入面板也会在 `10s` 无活动后自动关闭。 |
| **强制测试精灵动作** | **点击「Debug Action」面板按钮** | `DebugActionPanel` 提供「打开音响/关闭音响」切换按钮，向 `InteractionActionRuntimeDependencies.requestBus` 投递 `start-boombox` 或 `stop-boombox`。 |
| **查看录音状态** | **语音输入过程中自动显示** | `voiceInputProvider.isListening()` 为 true 时显示录音状态面板；该面板只是状态展示，不是语音触发入口。 |

## 4. 手柄模式与手眼模式差异

| 功能/能力 | 手柄模式 | 手眼/手势模式 | 差异说明 |
| :--- | :--- | :--- | :--- |
| **语音输入** | 右 Trigger 长按 `> 1s` 开始，松开结束 | 连续拍手 3 次触发语音输入 | 两者都能进入语音输入，但触发方式完全不同；手柄依赖按钮持续状态，手眼依赖双手掌心距离序列。 |
| **文本输入框** | 右 Trigger 双击呼出 | 连续捏合 2 次呼出 | 两种模式现在都能呼出文本输入框，最终都复用 `InputControllerManager` 的文本输入链路。 |
| **足球/篮球物理互动** | 当前无专用手柄键位 | 注视/命中球体 + 捏合 Tap | 球类交互是手眼模式独有，直接作用于 3D 实体。 |
| **小黄鸭互动** | 当前无专用手柄键位 | 注视/命中小黄鸭 + 捏合 Tap | 玩家直接捏鸭是手眼模式独有；AI 精灵也可通过对话 action 触发捏鸭，但不属于手柄键位。 |
| **音响开关/切歌** | 当前无专用手柄键位 | 长捏合开关，短捏合切歌 | 玩家直接控制音响是手眼模式独有；Debug 面板和 AI action 是独立路径。 |
| **空间 UI 操作** | 可通过系统射线/指针点击 UI | 可通过空间指针/手势点击 UI | UI 面板本身两种模式都可操作，差异主要在“如何呼出输入框”和“是否直接操作 3D 实体”。 |

## 5. 关键代码索引

| 模块 | 位置 | 说明 |
| :--- | :--- | :--- |
| 手柄 Trigger 输入 | `InputControllerManager.kt` | 右 Trigger 长按语音、双击文本输入。 |
| 拍手检测 | `HandClapDetector.kt` | 三次拍手进入语音输入。 |
| 手部/3D 手势绑定 | `HomeStage.kt` | 手部双捏合打开输入框，以及足球、篮球、小黄鸭、音响的 `pointerInput` 注册。 |
| 小黄鸭动画播放 | `ObjectAnimationPlayer.kt` | 从 wrapper 查找真正持有动画轨道的 GLB 内部节点。 |
| 空间音乐与音效 | `MusicModule.kt` | 音响歌单播放/停止/切歌，小黄鸭音效播放与 1.5s 截断。 |
| 输入 UI | `GameUIContainer.kt` / `FairyDialogueUI.kt` | 文本输入框、发送、取消、录音状态展示。 |
| Debug 动作 | `DebugActionPanel.kt` | 开关音响的调试按钮入口。 |
