# Debug Session: scene-assets-boombox

Status: OPEN

## Symptoms

1. 启动后足球、篮球、鸭子、音响短暂出现后消失。
2. 点击“打开音响”后，精灵定在原地，完全静止。

## Initial Hypotheses

1. 资源没有被销毁，而是物理激活后受重力/碰撞配置影响快速掉到视野外或穿透现实 mesh。
2. 资源没有消失，而是被挂到错误父节点或 Transform/scale 被后续 update 覆盖，导致移动到不可见位置或缩放异常。
3. `SpatialMeshManager` 生成的现实网格/遮挡材质或 occlusion 配置把虚拟资源遮住了。
4. 点击调试按钮后 `start-boombox` request 已入队，但 `InteractionActionSystem` 因 action lock、找不到 actor/object、距离/Transform 缺失等原因立即失败或无法推进。
5. `StartBoomboxActionController` 进入 `APPROACHING` 后目标位置计算异常，精灵刚体力/速度被清空或行为锁阻止常规行为恢复，表现为原地静止。

## Evidence Plan

- 在资源创建、物理激活、每秒资源 Transform 采样处加日志。
- 在 action request enqueue、系统 drain、instance 状态机转换处加日志。
- 在精灵行为系统 action lock 状态处加日志。

## Changes

- Started debug server with session `scene-assets-boombox`, output directory `.dbg`, remote URL `http://10.254.97.21:7777/event`.
- Added instrumentation-only reporting:
  - `InteractionActionCore.kt`: request enqueue accepted/rejected, temporary debug reporter.
  - `InteractionActionSystem.kt`: request drain, controller missing, instance created, completed/failed.
  - `FootballPhysicsActivationSystem.kt`: floor raycast hit and gravity fallback.
  - `HomeStage.kt`: football/basketball configuration and startup GLB attachment.
  - `ObjectInteractionActionControllers.kt`: carry/use action missing entities, state transitions, approach samples.
- Temporarily enabled `android:usesCleartextTraffic="true"` to allow the headset to report debug events over HTTP.

## Evidence

- Pre-fix logs:
  - Lines 1-4: football, basketball, rubber duck, boombox all configured/attached successfully.
  - Lines 9-12: all four resources hit `resource gravity fallback without floor hit` at ~6.66s.
  - Line 12: boombox fallback occurred at initial position `(-0.45, 0.3, -1.55)`.
  - Line 15: after clicking debug start, action target boombox was already at `targetY=-1168.1758`.
  - Lines 20-32: fairy kept chasing the falling boombox with strong negative Y force (`forceY=-25.299...`), causing the fairy to fall.

## Fix

- Minimal logic fix in `ResourcePhysicsActivationSystem`:
  - Removed fallback gravity enable when no floor raycast hit exists.
  - Resources now keep `RigidBodyComponent.isAffectedByGravity=false` until a real raycast hit is available.
  - Added throttled post-fix debug report `resource keeps gravity disabled without floor hit`.
- Updated instrumentation runId from `pre-fix` to `post-fix` for verification.
- Follow-up post-fix evidence:
  - Lines 37-44: before mesh hit, resources stayed gravity-disabled instead of falling.
  - Lines 45-48: all four resources later received floor hits around `floorY=0.391`.
  - Lines 52-92: boombox action was accepted and stayed in `APPROACHING`, but fairy moved only from distance `3.59m` to `3.32m` over a long interval, proving force-based movement was ineffective.
- Follow-up fixes:
  - `ResourcePhysicsConfigurator.configureDynamicBall()` now always uses runtime primitive sphere shapes for football/basketball instead of reusing Editor collision shapes.
  - `CarryAndUseObjectActionInstance.moveSubjectTowards()` now directly advances the fairy transform by `flySpeed * dt` and clears physics force, avoiding the static-looking force/rigidbody conflict during carry/use actions.
- Third-round evidence:
  - Lines 96-99: app startup immediately scheduled random `play-football` before any floor hit.
  - Lines 100-120: football was still not floor-activated, but its position dropped from `y=0` to `y=-73.64`.
  - Lines 136-140: `start-boombox` was accepted, but fairy subject was already at impossible coordinates (`x=-68213137408`, `z=85760335872`), so it could not visibly react.
- Third-round fix:
  - `FairyBehaviorSystem.tryScheduleRandomPlayFootball()` now refuses random football actions until `ResourcePhysicsActivationComponent.activated == true` and target `y >= -0.5`.
  - `PlayFootballActionController` also fails early if football is not floor-activated or has fallen below `MIN_TARGET_Y`.
- Fourth-round user evidence:
  - Boombox action can reach pickup successfully and the boombox follows the fairy.
  - After pickup, the fairy may jitter near a wall or suddenly fly out of the room at high speed.
- Fourth-round fix:
  - `CarryAndUseObjectActionInstance` switches the fairy rigid body to `RigidBodyMode.KINEMATIC` during direct scripted motion, clears force/velocity, then restores the previous rigid body mode on completion/cancel.
  - Picked objects now switch to `RigidBodyMode.KINEMATIC` while held by `PickedObjectFollowComponent`, because the follow system also directly overwrites Transform every frame.
  - `PutDownObjectActionInstance` restores picked object rigid bodies to `RigidBodyMode.DYNAMIC` and re-enables gravity when the object is released.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
- Fifth-round user evidence:
  - The fairy can still pick up the boombox, but after pickup it carries the boombox and rapidly flies out of the room.
  - Latest logs show `start-boombox` reaches `PICKING_UP`, `USING`, `FINISHING`, and `action completed` while both fairy and boombox coordinates are still reasonable:
    - Lines 349-356: fairy stays around `x=-0.18, y=0.88, z=-1.17`; boombox stays around `x=0.00, y=0.84, z=-1.43`.
  - Therefore the fly-out happens after the action lock is released, not inside the carry/use approach state.
- Fifth-round fix:
  - While an object has `PickedObjectFollowComponent`, its `CollisionComponent.collisionResponseMode` is now changed from physical `COLLIDER_FULL` to `TRIGGER_LITE`.
  - This prevents the kinematic held boombox from continuously pushing the dynamic fairy after the action completes.
  - `PickedObjectFollowComponent` stores the previous collision response mode, and `PutDownObjectActionInstance` restores it before switching the object back to `RigidBodyMode.DYNAMIC`.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
- Sixth-round user evidence:
  - The symptom changed: the fairy may start a random `play-football` action, freeze midway, and then the debug panel keeps reporting that another action is running.
  - This indicates a scheduler/lifetime problem: a lower-priority random action can keep the global lock and block explicit debug requests.
- Sixth-round fix:
  - `PlayFootballActionController` now uses direct Transform stepping plus temporary `RigidBodyMode.KINEMATIC`, matching the boombox carry/use movement safety model.
  - `PlayFootballActionController` has `maxActionSeconds = 4.5f`; timeout returns `FAILED`, cleans force/velocity, removes `FairyActionLockComponent`, and restores rigid body mode.
  - `cancel()` also cleans subject motion, preventing stale action components after preemption.
  - `InteractionActionSource` now has priority: `RANDOM < DIALOGUE < DEBUG`.
  - `InteractionActionRequestBus` allows higher-priority requests through when they can preempt the current lock.
  - `InteractionActionSystem` cancels and releases the lower-priority active action before creating the higher-priority instance.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
- Seventh-round user evidence:
  - Football and basketball show two symptoms: sometimes they disappear/drop out after startup; sometimes they are not visible from startup.
  - Boombox can be picked up, but no music is audible.
  - Existing logs only covered pre-activation/floor-hit events and did not include post-activation ball trajectory or spatial audio playback result.
- Seventh-round instrumentation:
  - `ResourcePhysicsActivationSystem` now reports activated football/basketball positions, gravity state, and elapsed time every second, plus immediately when y drops below `-0.5`.
  - `MusicModule` now reports playlist configuration, next-track attempts, `playAudio` player creation/isPlaying result, and audio duration read failures.
  - Debug server restarted for session `scene-assets-boombox`; health check reports server ok.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
- Eighth-round evidence and fix:
  - Existing logs show football moved while still reporting `resource keeps gravity disabled without floor hit`, then fell from `y=-10` to below `y=-1600`.
  - This confirms a resource can be moved by action/physics before floor activation, and the activation system previously did not recover it once it was below the raycast range.
  - `ResourcePhysicsActivationSystem` now keeps football/basketball visible at `PRE_ACTIVATION_VISIBLE_Y = 0.65f` while floor is not ready.
  - If an activated football/basketball falls below `FALL_RESET_Y = -0.5f`, it is reset to `lastSafePosition`, gravity is disabled, velocity/force are cleared, and floor activation restarts.
  - A previous attempt changed boombox to be released after opening, but this violated the intended design that the fairy should keep carrying the boombox.
  - That release-after-open behavior was reverted.
  - New carrying fix: while the fairy is carrying any `PickedObjectFollowComponent` object, `FairyBehaviorSystem` uses kinematic direct movement with capped speed instead of dynamic rigid-body force control.
  - The boombox still follows the fairy while held, but the fairy no longer relies on physics force feedback during carrying movement.
  - `MusicModule` now falls back to normal `MediaPlayer` asset playback if spatial `entity.playAudio(resource)` returns null, so "open boombox" should be audible even if spatial audio fails.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
  - Debug server logs were cleared before post-fix verification.

## Conclusion

- Confirmed root cause: fallback gravity activation without a valid floor mesh caused all dynamic resources to fall into the void. The boombox then became a target thousands of meters below the room, and the fairy action system correctly tried to approach it, pulling the fairy downward.
- Hypothesis 1 confirmed.
- Hypothesis 4 rejected for enqueue/controller lookup: logs show `start-boombox` request was accepted, drained, and instance was created.
- Hypothesis 5 partially confirmed as a downstream effect: action state machine did not freeze; it kept running and applying downward force toward a fallen target.
- Second round: basketball-specific fall is likely caused by reused Editor collision shapes; fixed by forcing primitive runtime sphere collider.
- Second round: fairy "static" is caused by force-based carry/use movement barely changing transform under physics constraints; fixed by direct transform stepping for carry/use actions.
- Third round: startup random football action was firing before spatial floor activation, causing football and then fairy state corruption. Random football action is now gated by resource activation.
- Fourth round pending runtime validation: remaining high-speed fly-out/jitter is likely caused by direct Transform control fighting dynamic rigid bodies. Fairy and carried object are now isolated as kinematic during scripted/follow motion.
- Fifth round pending runtime validation: action completes with valid positions, so the remaining fly-out is likely caused by the held kinematic boombox retaining `COLLIDER_FULL` and physically pushing the fairy after normal behavior resumes. Held objects now use `TRIGGER_LITE` until put down.
- Sixth round pending runtime validation: random `play-football` can no longer permanently block debug boombox actions because it has a hard timeout and can be preempted by `DEBUG`.
- Seventh round pending runtime evidence: determine whether ball disappearance is no-floor suspension, post-activation falling, or action-driven movement; determine whether boombox silence is empty playlist, asset load/play failure, or player not actually playing.
- Eighth round pending runtime validation: sports balls should stay visible/safe even before floor activation and auto-recover after falling; boombox should play through spatial audio or BGM fallback while continuing to be carried by the fairy without flying out.
- Ninth-round user evidence and fix:
  - Remaining issues after stabilization:
    - The boombox is held too far from the fairy, weakening the pickup effect.
    - The boombox still has no audible music after `start-boombox`.
    - Pressing debug "close boombox" during `start-boombox` is rejected by the current action lock.
  - Fixes:
    - `StartBoomboxActionController` hold offset changed from `Vector3(0f, -0.04f, 0.32f)` to `Vector3(0f, -0.03f, 0.16f)` so the boombox stays closer to the fairy.
    - `MusicModule.playSpatialMusicAt()` now falls back to asset BGM when `spatialMusicPlayer?.isPlaying() != true`, not only when `playAudio()` returns null.
    - `InteractionActionLockState.canPreempt()` now allows a `DEBUG` request to preempt another `DEBUG` request when controller IDs differ, so `stop-boombox` can interrupt `start-boombox`.
  - Verified with `./gradlew :app:compileDebugKotlin`: build passed.
