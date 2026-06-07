package com.example.matefairy01.content

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
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
import com.example.matefairy01.behavior.FairyBehaviorComponent
import com.example.matefairy01.behavior.FairyBehaviorSystem
import com.example.matefairy01.behavior.BehaviorRuntimeDependencies
import com.example.matefairy01.behavior.HMDTagComponent
import com.example.matefairy01.config.AppConfigLoader
import com.example.matefairy01.input.HandClapDetector
import com.example.matefairy01.input.InputControllerManager
import com.example.matefairy01.interaction.InteractionActionSystem
import com.example.matefairy01.interaction.InteractionActorComponent
import com.example.matefairy01.interaction.InteractionObjectComponent
import com.example.matefairy01.perception.SpatialMeshManager
import com.example.matefairy01.runtime.MateFairyRuntime
import com.example.matefairy01.ui.FairyDialogueUI
import com.example.matefairy01.ui.GameUIContainer
import com.example.matefairy01.ui.SharedUIManager
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.LookAtComponent
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.PhysicsWorldComponent
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.simulation.CollisionDetectionMode
import com.pico.spatial.core.ecs.simulation.CollisionFilter
import com.pico.spatial.core.ecs.simulation.CollisionInfoDetailLevel
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.math.Bool3
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.tracking.controller.ControllerTrackingProvider
import com.pico.spatial.tracking.hand.HandTrackingProvider
import com.pico.spatial.tracking.hmd.HMDPose
import com.pico.spatial.tracking.hmd.HMDTrackingData
import com.pico.spatial.tracking.hmd.HMDTrackingProvider
import androidx.compose.ui.input.pointer.pointerInput
import com.pico.spatial.ui.foundation.gesture.detectSpatialTapGesture
import com.pico.spatial.ui.foundation.gesture.TargetEntity
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.dsl.registerSystem
import com.pico.spatial.ui.foundation.dsl.unregisterSystem
import com.pico.spatial.ui.platform.meters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.matefairy01.memory.permanent.MdTemplates

data class DialogueBubbleState(
    val text: String,
    val isVisible: Boolean
)

private class HomeStageRuntimeState {
    var loadedRobotModelEntity: Entity? = null
    var dialogueAttachmentEntity: Entity? = null
    var userInputAttachmentEntity: Entity? = null
    var editorSceneEntity: Entity? = null
    var footballEntity: Entity? = null
}

const val DEFAULT_TEST_DIALOGUE_TEXT = "你好！我是你的MateFairy。"

@Composable
fun HomeStage() {
    val scope = rememberCoroutineScope()
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val runtimeState = remember { HomeStageRuntimeState() }
    val rootEntity = remember { Entity().apply { components.set(PhysicsWorldComponent(Vector3(0f, -9.81f, 0f))) } }
    val hmdEntity = remember { Entity().apply { components.set(HMDTagComponent()) } }
    val spatialMeshManager = remember { SpatialMeshManager(mainHandler) }

    // 追踪数据提供者
    val hmdTrackingProvider = remember { HMDTrackingProvider() }
    val hmdTrackingData by hmdTrackingProvider.dataFlow.collectAsState(
        initial = HMDTrackingData(HMDPose(Vector3.ZERO, Quat.identity()), 0L)
    )
    val controllerTrackingProvider = remember { ControllerTrackingProvider() }

    // 手部追踪提供者（手眼模式）
    val handTrackingProvider = remember { HandTrackingProvider() }

    // 输入提供者
    val context = LocalContext.current
    val appConfig = remember(context) { AppConfigLoader.load(context) }
    val textInputProvider = SharedUIManager.textInputProvider
    val voiceInputProvider = SharedUIManager.voiceInputProvider
    // runtime 由 SpatialApplication 在 onCreate 阶段一次性装配；
    // 此处仅取引用，避免每次进入 HomeStage 都重新创建（embedder / mcp 等也会被多重持有）。
    val runtime = remember(context) {
        (context.applicationContext as com.example.matefairy01.platform.SpatialApplication).runtime
    }


    // AI 对话状态
    var dialogueText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var dialogueBubbleState by remember {
        mutableStateOf(
            DialogueBubbleState(
                text = DEFAULT_TEST_DIALOGUE_TEXT,
                isVisible = false
            )
        )
    }

    DisposableEffect(runtime) {
        BehaviorRuntimeDependencies.bindAvatarController(runtime.avatarController)
        onDispose {
            runtime.avatarController.cleanup()
            BehaviorRuntimeDependencies.clear()
        }
    }

    // 输入控制器管理器（手柄模式）
    val inputControllerManager = remember {
        InputControllerManager(
            textInputProvider = textInputProvider,
            voiceInputProvider = voiceInputProvider,
            onTextInputResult = { text ->
                handleUserInput(
                    text,
                    scope,
                    runtime = runtime,
                    onDialogueUpdate = {
                        dialogueText = it
                        dialogueBubbleState = dialogueBubbleState.copy(
                            text = it,
                            isVisible = true
                        )
                    },
                    onProcessing = { isProcessing = it }
                )
            },
            onVoiceInputResult = { text ->
                handleUserInput(
                    text,
                    scope,
                    runtime = runtime,
                    onDialogueUpdate = {
                        dialogueText = it
                        dialogueBubbleState = dialogueBubbleState.copy(
                            text = it,
                            isVisible = true
                        )
                    },
                    onProcessing = { isProcessing = it }
                )
            }
        )
    }

    // 拍手检测器（手眼模式）
    val handClapDetector = remember {
        HandClapDetector {
            // 检测到3次拍手，开始语音输入
            voiceInputProvider.startListening { result ->
                handleUserInput(
                    result,
                    scope,
                    runtime = runtime,
                    onDialogueUpdate = {
                        dialogueText = it
                        dialogueBubbleState = dialogueBubbleState.copy(
                            text = it,
                            isVisible = true
                        )
                    },
                    onProcessing = { isProcessing = it }
                )
            }
        }
    }

    val controllerListener = remember {
        ControllerTrackingProvider.ControllerActionListener { action ->
            mainHandler.post {
                inputControllerManager.processControllerAction(action)
            }
        }
    }

    DisposableEffect(
        hmdTrackingProvider,
        controllerTrackingProvider,
        handTrackingProvider,
        inputControllerManager,
        handClapDetector,
        spatialMeshManager
    ) {
        hmdTrackingProvider.start()
        controllerTrackingProvider.addControllerActionListener(controllerListener)
        controllerTrackingProvider.start()
        handTrackingProvider.start()
        registerSystem<FairyBehaviorSystem>()
        registerSystem<InteractionActionSystem>()
        registerSystem<FootballPhysicsActivationSystem>()

        val handTrackingJob = scope.launch {
            handTrackingProvider.dataFlow.collect { trackingData ->
                handClapDetector.processHandTrackingData(trackingData)
            }
        }

        onDispose {
            handTrackingJob.cancel()
            unregisterSystem<FootballPhysicsActivationSystem>()
            unregisterSystem<InteractionActionSystem>()
            unregisterSystem<FairyBehaviorSystem>()
            hmdTrackingProvider.stop()
            controllerTrackingProvider.removeControllerActionListener(controllerListener)
            controllerTrackingProvider.stop()
            handTrackingProvider.stop()
            inputControllerManager.cleanup()
            handClapDetector.cleanup()
            spatialMeshManager.dispose()
        }
    }

    SpatialView(
        modifier = Modifier.requiredSize(10.meters)
            .pointerInput(runtimeState.footballEntity) {
                val football = runtimeState.footballEntity
                if (football != null) {
                    detectSpatialTapGesture(
                        context = context,
                        targetedToEntity = TargetEntity.any { entity -> entity == football }
                    ) {
                        // 给足球施加一个弹飞的速度（向上和向后）
                        val velocityComp = football.components[com.pico.spatial.core.ecs.PhysicsVelocityComponent::class.java] 
                            ?: com.pico.spatial.core.ecs.PhysicsVelocityComponent().also { football.components.set(it) }
                        velocityComp.linearVelocity = Vector3(0f, 3.0f, -3.0f)
                    }
                }
            },
        update = { content, attachments ->
            // 必须在 update 中保证所有的 Attachments 被添加到场景中
            // 因为 Compose 附件的生命周期独立于 3D 渲染，只有挂载后 SDK 才能进行深度计算和渲染
            if (runtimeState.dialogueAttachmentEntity == null) {
                attachments.entity("text")?.let {
                    runtimeState.dialogueAttachmentEntity = it
                    if (it.components[TransformComponent::class.java] == null) {
                        it.components[TransformComponent::class.java] = TransformComponent()
                    }
                    it.components[LookAtComponent::class.java] = LookAtComponent().apply {
                        setViewerAsTarget()
                        alignLocalUpToWorldUp = true
                    }
                    // !关键修复点! 将这个挂载了 Compose UI 的 Entity 正式加入 3D 渲染树
                    content.addEntity(it)
                }
            }

            attachments.entity("user_input_panel")?.let { inputEntity ->
                if (runtimeState.userInputAttachmentEntity != inputEntity) {
                    runtimeState.userInputAttachmentEntity = inputEntity
                    if (inputEntity.components[TransformComponent::class.java] == null) {
                        inputEntity.components[TransformComponent::class.java] = TransformComponent()
                    }
                    hmdEntity.addChild(inputEntity)
                }

                inputEntity.components[TransformComponent::class.java]?.apply {
                    setPosition(Vector3(0f, -0.15f, -0.65f))
                    setQuaternion(Quat.identity())
                }
            }

            // 更新 HMD 位置：先转换到 rootEntity 所在坐标系，再驱动其子节点 HUD
            hmdTrackingData.hmdPose.let { pose ->
                val transformComponent = hmdEntity.components[TransformComponent::class.java]
                transformComponent?.apply {
                    val convertedPosition = rootEntity.convertPositionFrom(pose.position, null)
                    val convertedRotation = rootEntity.convertRotationFrom(pose.rotation, null)
                    setPosition(convertedPosition)
                    setQuaternion(convertedRotation)
                }
            }

            val robotTransform = runtimeState.loadedRobotModelEntity?.components?.get(TransformComponent::class.java)
            val textAttachmentTransform =
                runtimeState.dialogueAttachmentEntity?.components?.get(TransformComponent::class.java)

            if (robotTransform != null) {
                val robotPosition = robotTransform.position

                // 将对话气泡放在机器人头顶
                textAttachmentTransform?.setPosition(
                    Vector3(
                        robotPosition.x,
                        robotPosition.y + 0.66f,
                        robotPosition.z
                    )
                )
            }
        },
        initial = { content, attachments ->
            // 由于不能直接在 initial 闭包中构造包含 attachments 的 Entity
            // 且之前的直接获取机制会导致 null 的生命周期问题
            // 所以我们这里只加载基础包，完全依赖 update 闭包来实时扫描和挂载 Attachments

            val (bundle, glbRoot) = coroutineScope {
                val bundleDeferred = async(Dispatchers.IO) {
                    com.pico.spatial.core.ecs.resource.AssetBundle.load("asset://editor-asset.bundle")
                }
                val glbDeferred = async(Dispatchers.IO) {
                    Entity.load("asset://pico_robot_animated.glb")
                }
                bundleDeferred.await() to glbDeferred.await()
            }
            val model = Entity()

            // 加载 Editor 里排好的场景
            val sceneModel = Entity.loadSuspend(modelName = "MyScene", bundle = bundle)
            runtimeState.editorSceneEntity = sceneModel
            val editorOcclusionMaterial = runCatching {
                bundle.loadMaterial(OCCLUSION_MATERIAL_PATH)
            }.onSuccess {
                Log.i(HOME_STAGE_TAG, "Loaded occlusion material: $OCCLUSION_MATERIAL_PATH")
            }.onFailure {
                Log.w(
                    HOME_STAGE_TAG,
                    "Occlusion material not found: $OCCLUSION_MATERIAL_PATH; fallback material will not reliably occlude passthrough",
                    it
                )
            }.getOrNull()
            spatialMeshManager.setOcclusionMaterial(editorOcclusionMaterial)
            
            val football = sceneModel.findEntity("Football")
            runtimeState.footballEntity = football
            football?.let {
                it.components.set(InteractionObjectComponent(objectId = "football", tags = setOf("sports", "physics")))
                configureFootballPhysics(it)
            }

            model.apply {
                components[TransformComponent::class.java] = TransformComponent().apply {
                    setPosition(Vector3(0f, 0f, 0f))
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

                // 物理代理和视觉模型分离，避免刚体系统干扰 GLB 缩放/动画层级。
                val robotBody = Entity()
                val robotModel = Entity()
                runtimeState.loadedRobotModelEntity = robotModel

                // 物理化配置：刚体 + 碰撞体 + 力组件
                val fairyShape = ShapeResource.createCapsule(0.35f, 0.1f)
                val collision = CollisionComponent(
                    collisionShape = listOf(fairyShape),
                    physicsMaterial = PhysicsMaterialResource(),
                    collisionResponseMode = CollisionResponseMode.COLLIDER_FULL
                )
                val rigidBody = RigidBodyComponent().apply {
                    rigidBodyMode = RigidBodyMode.DYNAMIC
                    isAffectedByGravity = false // 手动射线悬浮
                    isRotationLocked = Bool3(true, true, true) // 锁定物理旋转，通过脚本手动旋转
                    linearDamping = 5.0f // 增加线性阻尼，避免过度震荡
                    angularDamping = 5.0f
                }
                val physicsForce = PhysicsForceComponent()

                robotBody.components.set(collision)
                robotBody.components.set(rigidBody)
                robotBody.components.set(physicsForce)

                // 将已缩放的 GLB 挂载到 Wrapper
                robotModel.addChild(glbRoot)

                robotModel.apply {
                    val robotTransform = components[TransformComponent::class.java]
                    robotTransform?.apply {
                        position = Vector3(0f, 0f, 0f)
                        // Wrapper 本身不再承担缩放职责，保持 1:1
                        scaleVector = Vector3(1f, 1f, 1f)
                    }
                }

                robotBody.apply {
                    val bodyTransform = components[TransformComponent::class.java]
                    bodyTransform?.apply {
                        position = Vector3(0f, 0f, 0f)
                        scaleVector = Vector3(1f, 1f, 1f)
                    }

                    // hoverHeight: 精灵相对于头显的高度偏移
                    val behaviorComponent = FairyBehaviorComponent(
                        innerRadius = 1.0f,
                        outerRadius = 1.5f,
                        hoverHeight = -0.1f,  // 眼睛视平线往下 10 厘米
                        zDeviationRange = 0.2f // 缩小Z轴随机扰动，保持更好的圆环跟随
                    ).apply {
                        visualEntity = robotModel
                    }

                    // 由于包装实体初始没有旋转，将其欧拉角作为初始参考
                    val initialEuler = bodyTransform?.eulerAngles
                    initialEuler?.let {
                        behaviorComponent.initialPitch = it.pitch
                        behaviorComponent.initialRoll = it.roll
                        behaviorComponent.initialYaw = it.yaw
                        behaviorComponent.hasRecordedInitialRotation = true
                        behaviorComponent.currentYaw = it.yaw
                    }

                    components.set(behaviorComponent)
                    components.set(InteractionActorComponent())
                }

                // 初始化动画模块（传入 GLB 根节点，以便查找 SkinnedMeshEntity）
                runtime.avatarController.initialize(glbRoot)
                runtime.avatarController.playSpawnAnimation()

                addChild(robotBody)
                addChild(robotModel)

                rootEntity.addChild(this)
            }

            // 将 Editor 里编辑好的整个场景加到现实房间的中心（原点）
            rootEntity.addChild(sceneModel)

            // 设置对话文本附件位置（精灵头顶）
            // 在 SDK 新版本中，不再需要在 initial 中预挂载 attachments，
            // 全部由 update 中扫描补齐

            content.addEntity(rootEntity)

            rootEntity.addChild(hmdEntity)
            spatialMeshManager.start(rootEntity)
            
            // 释放 bundle
            bundle.close()
        },
        attachments = {
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

            // 用户输入面板 (回归原生 3D 附件方案)
            val needUI = textInputProvider.showInputDialog || voiceInputProvider.isListening()
            if (needUI) {
                AttachmentPanel(id = "user_input_panel") {
                    GameUIContainer(
                        onClearMemory = { clearAllMemory(scope, runtime) }
                    )
                }
            }
        }
    )
}

private fun configureFootballPhysics(football: Entity) {
    val existingCollision = football.components[CollisionComponent::class.java]
    val shapes = existingCollision?.collisionShape?.takeIf { it.isNotEmpty() }
        ?: listOf(ShapeResource.createSphere(FOOTBALL_COLLIDER_RADIUS))

    football.components.set(
        CollisionComponent(
            collisionShape = shapes,
            physicsMaterial = PhysicsMaterialResource(),
            collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
            collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
            collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
        )
    )

    val rigidBody = football.components[RigidBodyComponent::class.java] ?: RigidBodyComponent()
    rigidBody.apply {
        rigidBodyMode = RigidBodyMode.DYNAMIC
        isAffectedByGravity = false
        collisionDetectionMode = CollisionDetectionMode.CONTINUOUS
        linearDamping = 0.2f
        angularDamping = 0.2f
    }
    football.components.set(rigidBody)

    football.components.set(
        FootballPhysicsActivationComponent(radius = FOOTBALL_COLLIDER_RADIUS)
    )
}

private const val FOOTBALL_COLLIDER_RADIUS = 0.11f
private const val OCCLUSION_MATERIAL_PATH = "MyScene/Root/MyMaterials/OcclusionMaterial"
private const val HOME_STAGE_TAG = "HomeStage"

/**
 * 清除所有持久化记忆：L2 情景 + L3 语义（事实/三元组）+ L4 永久层（USER/MEMORY 重置为模板）。
 * SOUL.md 为人设，不动。
 */
private fun clearAllMemory(
    scope: CoroutineScope,
    runtime: MateFairyRuntime
) {
    scope.launch {
        runCatching {
            runtime.episodicStore.deleteAll()
            runtime.semanticStore.deleteAllFacts()
            runtime.semanticStore.deleteAllTriples()
            withContext(Dispatchers.IO) {
                runtime.permanentStore.writeUser(MdTemplates.USER)
                runtime.permanentStore.writeMemory(MdTemplates.MEMORY)
            }
            Log.d(HOME_STAGE_TAG, "all memory cleared")
        }.onFailure {
            Log.e(HOME_STAGE_TAG, "clear memory failed", it)
        }
    }
}

/**
 * 处理用户输入并调用 AI
 */
private fun handleUserInput(
    text: String,
    scope: CoroutineScope,
    runtime: MateFairyRuntime,
    onDialogueUpdate: (String) -> Unit,
    onProcessing: (Boolean) -> Unit
) {
    scope.launch {
        android.util.Log.d(HOME_STAGE_TAG, "handleUserInput: '$text'")
        onProcessing(true)
        onDialogueUpdate("思考中...")

        try {
            val result = runtime.conversationOrchestrator.processUserInput(text)
            android.util.Log.d(HOME_STAGE_TAG, "processUserInput done: '${result.replyText}'")
            onDialogueUpdate(result.replyText)

        } catch (e: Exception) {
            android.util.Log.e(HOME_STAGE_TAG, "processUserInput failed", e)
            onDialogueUpdate("抱歉，我遇到了一些问题，请稍后再试。")
        } finally {
            onProcessing(false)
        }
    }
}
