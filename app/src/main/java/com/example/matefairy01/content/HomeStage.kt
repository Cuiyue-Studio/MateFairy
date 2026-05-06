package com.example.matefairy01.content

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.MockLLMProvider
import com.example.matefairy01.behavior.FairyBehaviorComponent
import com.example.matefairy01.behavior.FairyBehaviorSystem
import com.example.matefairy01.behavior.HMDTagComponent
import com.example.matefairy01.di.AppModule
import com.example.matefairy01.emotion.IEmotionRenderer
import com.example.matefairy01.input.HandClapDetector
import com.example.matefairy01.input.InputControllerManager
import com.example.matefairy01.input.TextInputProvider
import com.example.matefairy01.input.VoiceInputProvider
import com.example.matefairy01.ui.FairyDialogueUI
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.ModelComponent
import com.pico.spatial.core.ecs.resource.MeshResource
import com.pico.spatial.core.ecs.resource.AssetBundle
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.tracking.controller.ControllerActionData
import com.pico.spatial.tracking.controller.ControllerTrackingProvider
import com.pico.spatial.tracking.hand.HandTrackingData
import com.pico.spatial.tracking.hand.HandTrackingProvider
import com.pico.spatial.tracking.hmd.HMDPose
import com.pico.spatial.tracking.hmd.HMDTrackingData
import com.pico.spatial.tracking.hmd.HMDTrackingProvider
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.dsl.registerSystem
import com.pico.spatial.ui.foundation.dsl.unregisterSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.pico.spatial.ui.platform.meters
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.requiredSize

@Composable
fun HomeStage() {
    val scope = rememberCoroutineScope()
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    // 追踪数据提供者
    val hmdTrackingProvider = remember { HMDTrackingProvider() }
    val hmdTrackingData by hmdTrackingProvider.dataFlow.collectAsState(
        initial = HMDTrackingData(HMDPose(Vector3.ZERO, Quat.identity()), 0L)
    )

    val controllerTrackingProvider = remember { ControllerTrackingProvider() }

    // 手部追踪提供者（手眼模式）
    val handTrackingProvider = remember { HandTrackingProvider() }
    val handTrackingData by handTrackingProvider.dataFlow.collectAsState(
        initial = HandTrackingData(null, null, 0L)
    )

    // 输入提供者
    val context = LocalContext.current
    val textInputProvider = remember { TextInputProvider() }
    val voiceInputProvider = remember { VoiceInputProvider(context) }

    // AI 对话状态
    var dialogueText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // 初始化 AI 模块
    DisposableEffect(Unit) {
        val mockProvider = MockLLMProvider()
        val emotionRenderer = object : IEmotionRenderer {
            override fun renderEmotion(emotion: String, fairyEntity: Entity) {
                // 简单的情绪渲染：这里可以扩展为播放动画或改变材质
            }
        }
        AppModule.initialize(
            provider = mockProvider,
            renderer = emotionRenderer
        )
        onDispose { }
    }

    // 输入控制器管理器（手柄模式）
    val inputControllerManager = remember {
        InputControllerManager(
            textInputProvider = textInputProvider,
            voiceInputProvider = voiceInputProvider,
            onTextInputResult = { text ->
                handleUserInput(text, scope, onDialogueUpdate = { dialogueText = it }, onProcessing = { isProcessing = it })
            },
            onVoiceInputResult = { text ->
                handleUserInput(text, scope, onDialogueUpdate = { dialogueText = it }, onProcessing = { isProcessing = it })
            }
        )
    }

    // 拍手检测器（手眼模式）
    val handClapDetector = remember {
        HandClapDetector {
            // 检测到3次拍手，开始语音输入
            voiceInputProvider.startListening { result ->
                handleUserInput(result, scope, onDialogueUpdate = { dialogueText = it }, onProcessing = { isProcessing = it })
            }
        }
    }

    // 控制器动作监听
    var latestControllerAction by remember { mutableStateOf<ControllerActionData?>(null) }

    val controllerListener = remember {
        ControllerTrackingProvider.ControllerActionListener { action ->
            mainHandler.post {
                latestControllerAction = action
            }
        }
    }

    DisposableEffect(hmdTrackingProvider, controllerTrackingProvider, handTrackingProvider) {
        hmdTrackingProvider.start()
        controllerTrackingProvider.addControllerActionListener(controllerListener)
        controllerTrackingProvider.start()
        handTrackingProvider.start()
        registerSystem<FairyBehaviorSystem>()

        onDispose {
            unregisterSystem<FairyBehaviorSystem>()
            hmdTrackingProvider.stop()
            controllerTrackingProvider.removeControllerActionListener(controllerListener)
            controllerTrackingProvider.stop()
            handTrackingProvider.stop()
            inputControllerManager.cleanup()
            handClapDetector.cleanup()
        }
    }

    val rootEntity = remember { Entity() }
    val hmdEntity = remember { Entity().apply { components.set(HMDTagComponent()) } }

    SpatialView(
        modifier = Modifier.requiredSize(10.meters),
        update = { _, _ ->
            // 更新 HMD 位置 - 直接使用追踪数据的世界坐标
            hmdTrackingData.hmdPose.let { pose ->
                val transformComponent = hmdEntity.components[TransformComponent::class.java]
                transformComponent?.apply {
                    // HMD 追踪数据已经是场景世界坐标，直接设置
                    setPosition(pose.position)
                    setQuaternion(pose.rotation)
                }
            }

            // 处理控制器输入（手柄模式）
            latestControllerAction?.let { action ->
                inputControllerManager.processControllerAction(action)
            }

            // 处理手部追踪输入（手眼模式）
            handClapDetector.processHandTrackingData(handTrackingData)
        },
        initial = { content, attachments ->
            val bundle =
                withContext(kotlinx.coroutines.Dispatchers.IO) { AssetBundle.load("asset://editor-asset.bundle") }
            val model = Entity.loadSuspend(modelName = "MyScene", bundle = bundle)

            var loadedRobotModel: Entity? = null
            model.apply {
                components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, 0f, 0f))
                }

                findEntity("Sky_Sphere")?.destroy()
                findEntity("box")?.destroy()

                // 移除旧的静态/USDZ模型
                findEntity("Toy_Robot_2_Anim")?.destroy()

                // 加载新的包含动画序列的 GLB 模型
                val glbRoot = withContext(kotlinx.coroutines.Dispatchers.IO) {
                    Entity.load("asset://pico_robot_animated.glb")
                }

                // 关键修复：GLB 根节点自带 scale=0.01，且骨骼动画在子节点上播放。
                // 如果直接对根节点设置 scale=0.0045，会覆盖默认的 0.01，导致 SDK 的
                // 视锥体剔除使用错误的包围盒（基于未缩放的原始尺寸 ~85米）。
                // 解决方案：保留根节点的默认 scale=0.01，对其子节点 "root" 设置额外缩放。
                // 这样整体缩放 = 0.01 × 0.45 = 0.0045，视觉尺寸正确，
                // 且 SDK 的层级包围盒计算能正确处理子节点的缩放。
                glbRoot.findEntity("root")?.components[TransformComponent::class.java]?.apply {
                    scaleVector = Vector3(0.45f, 0.45f, 0.45f)
                } ?: run {
                    // 如果找不到 "root" 子节点，回退到根节点缩放
                    glbRoot.components[TransformComponent::class.java]?.apply {
                        scaleVector = Vector3(0.0045f, 0.0045f, 0.0045f)
                    }
                }

                // 创建一个包装实体，用于控制整体位移和附加行为组件
                val robotModel = Entity()
                loadedRobotModel = robotModel

                // 将已缩放的 GLB 挂载到 Wrapper
                robotModel.addChild(glbRoot)

                robotModel.apply {
                    val robotTransform = components[TransformComponent::class.java]
                    robotTransform?.apply {
                        position = Vector3(0f, 0f, 0f)
                        // Wrapper 本身不再承担缩放职责，保持 1:1
                        scaleVector = Vector3(1f, 1f, 1f)
                    }

                    // hoverHeight: 精灵相对于头显的高度偏移
                    val behaviorComponent = FairyBehaviorComponent(
                        innerRadius = 1.0f,
                        outerRadius = 1.5f,
                        hoverHeight = -0.1f,  // 眼睛视平线往下 10 厘米
                        zDeviationRange = 0.2f // 缩小Z轴随机扰动，保持更好的圆环跟随
                    )

                    // 由于包装实体初始没有旋转，将其欧拉角作为初始参考
                    val initialEuler = robotTransform?.eulerAngles
                    initialEuler?.let {
                        behaviorComponent.initialPitch = it.pitch
                        behaviorComponent.initialRoll = it.roll
                        behaviorComponent.initialYaw = it.yaw
                        behaviorComponent.hasRecordedInitialRotation = true
                        behaviorComponent.currentYaw = it.yaw
                    }

                    components.set(behaviorComponent)
                }

                // 初始化动画模块（传入 GLB 根节点，以便查找 SkinnedMeshEntity）
                AppModule.animationModule.initialize(glbRoot)
                AppModule.animationModule.playAnimation(com.example.matefairy01.animation.FairyAnimation.TURBO_DASH)
                
                addChild(robotModel)
                
                rootEntity.addChild(this)
            }

            bundle.close()

            // 设置对话文本附件位置（精灵头顶）
            val textAttachment = attachments.entity(id = "text")
            textAttachment?.apply {
                components[TransformComponent::class.java]?.apply {
                    // Wrapper 的 scale 为 1.0，局部坐标等于世界坐标。
                    // 精灵高度约 0.38 米，文本放在头顶上方 0.25 米处。
                    setPosition(Vector3(0f, 0.63f, 0.0f))
                }
                // 附加到 loadedRobotModel 上，让文本气泡跟随机器人移动
                loadedRobotModel?.addChild(this)
            }

            content.addEntity(rootEntity)

            // HMD 实体直接添加到场景 content 中（不是任何实体的子节点）
            // 这样 HMD 位置就是绝对世界坐标，与精灵模型在同一坐标系中
            content.addEntity(hmdEntity)
        },
        attachments = {
            // 文本输入面板
            AttachmentPanel(id = "input_panel") {
                if (textInputProvider.showInputDialog) {
                    Box(
                        modifier = Modifier
                            .size(640.dp, 480.dp)
                            .background(Color(0xCC000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        FairyDialogueUI.TextInputDialog(
                            text = textInputProvider.currentText,
                            onTextChange = { textInputProvider.updateText(it) },
                            onSubmit = { textInputProvider.submitText() },
                            onCancel = { textInputProvider.cancelInput() }
                        )
                    }
                }
            }

            // 录音状态指示器面板
            AttachmentPanel(id = "recording_indicator") {
                if (voiceInputProvider.isListening()) {
                    Box(
                        modifier = Modifier
                            .size(200.dp, 200.dp)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        FairyDialogueUI.RecordingIndicator(isRecording = true)
                    }
                }
            }

            // AI 回复对话气泡面板 (关联到 text 附件)
            AttachmentPanel(id = "text") {
                if (dialogueText.isNotBlank() && !isProcessing) {
                    Box(
                        modifier = Modifier
                            .size(400.dp, 300.dp)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        FairyDialogueUI.DialogueBubble(text = dialogueText)
                    }
                }
            }
        }
    )
}

/**
 * 处理用户输入并调用 AI
 */
private fun handleUserInput(
    text: String,
    scope: CoroutineScope,
    onDialogueUpdate: (String) -> Unit,
    onProcessing: (Boolean) -> Unit
) {
    scope.launch {
        onProcessing(true)
        onDialogueUpdate("思考中...")

        try {
            // 添加用户消息到上下文
            AppModule.contextMemorySystem.addMessage(ChatMessage(role = "user", content = text))

            // 构建 Prompt 并请求 AI
            val messages = AppModule.contextMemorySystem.buildPromptMessages()
            val response = AppModule.llmProvider.chat(messages)

            // 添加 AI 回复到上下文
            AppModule.contextMemorySystem.addMessage(
                ChatMessage(role = "assistant", content = response.reply_text)
            )

            // 更新对话显示
            onDialogueUpdate(response.reply_text)

            // 触发情绪和动作
            AppModule.emotionEngine.triggerEmotion(response.emotion, Entity())
            AppModule.actionRegistry.dispatchAction(response.action_intent, Entity())

        } catch (e: Exception) {
            onDialogueUpdate("抱歉，我遇到了一些问题，请稍后再试。")
        } finally {
            onProcessing(false)
        }
    }
}
