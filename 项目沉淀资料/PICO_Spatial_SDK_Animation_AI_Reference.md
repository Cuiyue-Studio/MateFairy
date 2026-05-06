# PICO Spatial SDK 动画开发 AI 参考指南

本指南专为 AI 编码助手设计，旨在快速提供基于 PICO Spatial SDK (v0.11.7) 实现 3D 动画（骨骼动画与补间动画）的核心 API 用法、标准工作流及最佳实践。

---

## 1. 核心资源与加载路径

本项目中包含两个核心机器人模型，它们存放在 `app/src/main/assets/` 目录下：

- **骨骼动画模型 (GLB)**
  - **文件名**: `pico_robot_animated.glb`
  - **加载路径**: `"asset://pico_robot_animated.glb"`
  - **用途**: 演示带有内置骨架的动画（待机、跳跃、挥手等）。
- **静态补间动画模型 (USDZ)**
  - **文件名**: `pico_robot_static.usdz`
  - **加载路径**: `"asset://pico_robot_static.usdz"`
  - **用途**: 演示代码驱动的平滑插值动画（平移、旋转、材质变化等）。

---

## 2. 核心概念与基础 API

PICO Spatial SDK 采用 ECS（Entity-Component-System，实体-组件-系统）架构。
- **Entity**：3D 场景中的基本对象。
- **Component**：附加在 Entity 上的数据（如 `TransformComponent`、`ModelComponent`）。

### 2.1 模型的异步加载
模型资源的加载通过 `Entity.load(path)` 实现。

```kotlin
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 1. 在 IO 线程加载模型 (支持 .glb, .usdz)
val modelEntity = withContext(Dispatchers.IO) { 
    Entity.load("asset://pico_robot_animated.glb") 
}

// 2. 将模型挂载到父 Entity
val rootEntity = Entity()
rootEntity.addChild(modelEntity)

// 3. 调整 Transform (位置、旋转、缩放)
modelEntity.components[TransformComponent::class.java]?.apply {
    setPosition(Vector3(0f, 0f, -1f))
    setEulerAngles(EulerAngles(0f, 90f, 0f))
    setScaleVector(Vector3(1f, 1f, 1f))
}
```

### 2.2 在 Compose UI 中渲染 3D 实体
使用 `SpatialView` 组件将 Entity 渲染到空间窗口中。

```kotlin
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.layout.requiredDepth

SpatialView(
    modifier = Modifier.fillMaxSize().requiredDepth(100.dp),
    initial = { content, _ ->
        // 将根 Entity 添加到视图内容中
        content.addEntity(rootEntity)
    }
)

// 显隐控制：直接操作 entity.enabled
rootEntity.enabled = true // 显示
rootEntity.enabled = false // 隐藏
```

---

## 3. 骨骼动画 (Skeletal Animation)

骨骼动画通常内嵌在 GLB 模型文件中。加载模型后，需要提取蒙皮网格（Skinned Mesh）并播放其内置的动画资源。

### 3.1 提取动画资源并播放
调用接口：`Entity.findSkinnedMeshEntity()` 获取蒙皮网格，`Entity.getAnimationResources()` 获取动画轨道。

```kotlin
// 1. 查找包含骨骼动画的蒙皮网格 Entity
val skinnedMeshEntities = rootEntity.findSkinnedMeshEntity().toList()

// 2. 获取该网格上挂载的所有骨骼动画资源 (AnimationResource)
// 返回的 Array<AnimationResource> 索引通常对应在建模软件中导出的动画轨道顺序
val skeletalAnimationResources = skinnedMeshEntities.firstOrNull()?.getAnimationResources()

// 3. 调用 playAnimation 播放指定索引的动画
for (meshEntity in skinnedMeshEntities) {
    val animResource = skeletalAnimationResources?.get(0) // 播放第 0 个动画
    if (animResource != null) {
        meshEntity.playAnimation(animResource)
    }
}
```

### 3.2 停止动画与资源释放
调用接口：`Entity.stopAllAnimations()` 停止播放。

```kotlin
// 停止 Entity 上的所有动画
rootEntity.stopAllAnimations()

// 释放骨骼动画资源 (在 ViewModel onCleared 或生命周期结束时调用)
skeletalAnimationResources?.forEach { it.close() }
```
**⚠️ AI 避坑指南**：骨骼动画资源如果要反复播放或切换，**不要**在每次 `playAnimation` 后立刻调用 `.close()`，必须保持资源开启，直到彻底不需要该模型为止。

---

## 4. 补间动画 (Tween Animation)

补间动画由代码动态生成，用于对 Entity 的 Transform（位移、旋转、缩放）或 Material（材质属性）进行平滑插值。

### 4.1 创建与播放 Transform 补间动画
核心调用链路：`TweenAnimation.createTweenAnimation()` -> `AnimationResource.generateWithTweenAnimation()` -> `Entity.playAnimation()`。

```kotlin
import com.pico.spatial.core.ecs.animation.TweenAnimation
import com.pico.spatial.core.ecs.animation.AnimationBindTarget
import com.pico.spatial.core.ecs.resource.AnimationResource
import com.pico.spatial.core.ecs.animation.RepeatMode
import com.pico.spatial.core.ecs.animation.EaseType

// 1. 创建 TweenAnimation 对象 (以位置动画为例)
val tweenAnim = TweenAnimation.createTweenAnimation(
    bindTarget = AnimationBindTarget.bindPosition(), // 绑定目标为 Position
    to = Vector3(1f, 2f, -3f),                       // 目标值 (支持 from, to, by)
    duration = 2.0f,                                 // 动画时长 (秒)
    speed = 1.0f,                                    // 播放速度
    repeatCount = -1,                                // 重复次数 (-1 为无限循环)
    repeatMode = RepeatMode.REVERSE,                 // 重复模式 (REVERSE 往返, RESTART 重新开始)
    easeType = EaseType.EASE_INOUT                   // 缓动曲线
)

// 其他 Transform 绑定目标：
// - AnimationBindTarget.bindRotation()  配合 from/to/by 为 EulerAngles
// - AnimationBindTarget.bindScale()     配合 from/to/by 为 Vector3
// - AnimationBindTarget.bindTransform() 配合 from/to/by 为 Transform 对象

// 2. 生成 AnimationResource
val tweenAnimResource = AnimationResource.generateWithTweenAnimation(tweenAnim)

// 3. 播放动画
targetEntity.playAnimation(tweenAnimResource)

// 4. 重置/清理 (切换新动画前)
targetEntity.stopAllAnimations()
tweenAnimResource.close() // 补间动画资源属于一次性或按需生成，旧资源需要 close
```

### 4.2 创建与播放材质补间动画 (Material Tween)
材质动画必须绑定到包含 `ModelComponent` 的具体子 Entity 上，而不是根 Entity。

```kotlin
import com.pico.spatial.core.ecs.animation.MaterialTarget
import com.pico.spatial.core.ecs.ModelComponent
import com.pico.spatial.core.ecs.resource.PhysicallyBasedMaterial
import com.pico.spatial.core.ecs.resource.BlendingMode
import com.pico.spatial.core.math.Color4

// 1. 获取材质对象 (必须定位到具有几何体的子节点)
val meshEntity = rootEntity.findEntity("geo_body") // 根据静态机器人模型节点名称查找
val pbrMaterial = meshEntity?.components?.get(ModelComponent::class.java)
    ?.materials?.firstOrNull() as? PhysicallyBasedMaterial

// 如果要做透明度(Opacity)动画，必须将材质混合模式设为 TRANSPARENT
pbrMaterial?.setBlendingMode(BlendingMode.TRANSPARENT) 

// 2. 创建材质 TweenAnimation
val materialTween = TweenAnimation.createTweenAnimation(
    // 绑定到第 0 个材质的基础颜色
    bindTarget = AnimationBindTarget.bindMaterial(0, MaterialTarget.BASE_COLOR),
    to = Color4(1f, 0f, 0f, 1f), // 渐变到红色
    duration = 1.5f
)

// 其他材质绑定目标：
// - MaterialTarget.OPACITY (Float)
// - MaterialTarget.METALLIC (Float)
// - MaterialTarget.ROUGHNESS (Float)
// - MaterialTarget.EMISSIVE (Color4)

// 3. 生成并调用 playAnimation 播放
val matAnimResource = AnimationResource.generateWithTweenAnimation(materialTween)
meshEntity.playAnimation(matAnimResource) // 注意：是 meshEntity 播放，不是 rootEntity
```

---

## 5. 动画生命周期事件监听
可以通过 SpatialViewContent 订阅动画事件。

```kotlin
import com.pico.spatial.core.ecs.event.AnimationEvents

fun subscribeEvents(content: SpatialViewContent) {
    content.subscribe(AnimationEvents.Started::class.java) {
        // 动画开始
    }
    content.subscribe(AnimationEvents.Terminated::class.java) {
        // 动画结束
    }
}
```

---

## 5. 给 AI 的架构建议 (MVVM 最佳实践)

1. **分离状态与实体**：在 ViewModel 中持有 `Entity` 对象和控制状态（如 `TweenAnimationControl`），在 View（Compose）中只负责观察状态并将其塞入 `SpatialView`。
2. **使用无状态工具类**：创建一个如 `AnimationUtil` 的 object/单例，将所有的 `Entity.load`、`createTweenAnimation` 和 `playAnimation` 逻辑封装在里面。ViewModel 只需调用 `AnimationUtil.play(entity, state)`。
3. **彻底清理资源**：在 ViewModel 的 `onCleared()` 中，务必调用 `entity.stopAllAnimations()` 并对所有持有的 `AnimationResource` 执行 `.close()`。
4. **切换动画先 Reset**：在播放新的 Tween 动画前，必须 `stopAllAnimations()`、`.close()` 掉旧的资源，并把 Entity 的 Transform 或 Material 属性重置到初始状态，否则动画插值会从不可预期的状态开始。
