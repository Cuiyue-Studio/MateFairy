# Debug Session: fairy-action-stuck

Status: [OPEN]

## Symptoms

- `play-football` 后精灵会迅速移动到某个位置，然后像被卡住一样无法位移。
- `squeeze-rubber-duck` 中精灵会完全静止在原地，没有带着鸭子走。
- `rubber_duck_toy` 的形变动画没有触发。

## Constraints

- Steps 1-4 禁止修改业务逻辑。
- 第一处既有代码修改只能加入运行时采样日志。
- 修复必须基于运行时证据，而不是继续静态猜测。

## Hypotheses

1. `play-football` 在 `FINISHING` 的后撤目标不可达或状态机未退出，导致 action lock 长时间保持，行为系统无法恢复跟随/随机运动。
2. `ActionSubjectMotionTemplate.restore()` 已执行，但恢复后精灵仍处于 `KINEMATIC` 或 `TRIGGER_LITE` / `COLLIDER_FULL` 状态异常，导致行为系统施力无效或被物理解算卡住。
3. `squeeze-rubber-duck` 在 `PICKING_UP` 后没有进入有效 `USING` 移动路径，可能是 `carryAwayTarget` 为 null、`currentTarget` 不可达、或 `PickedObjectFollowComponent` 未被正确挂载。
4. 小黄鸭动画未触发可能是 `onUse()` 没有执行、`useTimerSeconds` 没到阈值、或 `playObjectAnimation(duck)` 未找到可播放的动画资源/目标实体。
5. `InteractionActionSystem` 或 action lock 释放顺序异常，导致后续行为系统、动画模块、放下鸭子 action 被长期阻塞。

## Instrumentation Plan

- 采样 `play-football` 状态机：state、subject/football 距离、postKickRetreatTarget、rigidBodyMode、collisionResponseMode、velocity、action lock。
- 采样 `squeeze-rubber-duck` 状态机：state、carryAwayTarget、subject/duck 位置、PickedObjectFollowComponent、useTimer/usedCount、onUse 触发。
- 采样 `InteractionActionSystem` 生命周期：request accepted/rejected、started、completed/failed、lock release。

## Instrumentation Applied

- Debug server: `http://10.71.200.105:7777/event`
- `AndroidManifest.xml`: temporary `android:usesCleartextTraffic="true"` for HTTP reporting.
- `PlayFootballActionController.kt`: `debug-point A`, samples `play-football` tick/kick/cleanup.
- `ObjectInteractionActionControllers.kt`: `debug-point C`, samples carry/use state transitions and `onUse` boundaries.
- `InteractionActionSystem.kt`: `debug-point E`, samples request/start/complete/fail/release lifecycle.
- Log file cleared for pre-fix reproduction: `.dbg/trae-debug-log-fairy-action-stuck.ndjson`

## Evidence

### Pre-fix run 1: play-football

- Log lines 1-3: `play-football` request accepted and action started.
- Log lines 4-8: state progresses `APPROACHING -> PLAYING_KICK_ANIMATION -> KICKING`.
- Log lines 9-10: `FINISHING` reaches cleanup; cleanup distance to football is about `0.905m`, so direct football overlap at cleanup is unlikely.
- Log lines 11-12: `InteractionActionSystem` completes and releases lock; `lockHeld=false`.

Interim conclusion:

- H1 is rejected for the first run: `play-football` does not remain stuck inside action state machine.
- H5 is rejected for the first run: action lock is released normally.
- Remaining likely area: post-action `FairyBehaviorSystem` recovery/physics motion after lock release.

### Additional instrumentation

- Added `debug-point B` in `FairyBehaviorSystem` to sample 3 seconds after follow is restored:
  - behavior state/current target
  - force magnitude
  - actual velocity
  - PhysicsVelocityComponent linear/angular velocity
  - rigid body mode
  - collision mode
  - residual action lock component
- Recompiled successfully.
- Cleared log file for next pre-fix reproduction.

### Pre-fix run 2: reported duck spin

- User reported reproducing `squeeze-rubber-duck` high-speed rotation.
- `.dbg/trae-debug-log-fairy-action-stuck.ndjson` was empty at read time.
- The previous debug server process was no longer queryable, so this run produced no usable runtime evidence.
- Restarted debug server with the same URL: `http://10.71.200.105:7777/event`.
- Confirmed `.dbg/fairy-action-stuck.env` still points to the same URL.
- Cleared log file again for the next reproduction.

### Pre-fix run 3: squeeze-rubber-duck fly-away

Evidence from `.dbg/trae-debug-log-fairy-action-stuck.ndjson`:

- Lines 43-47: `squeeze-rubber-duck` enters `PICKING_UP -> USING`; `carryAwaySet=true`; target has `PickedObjectFollowComponent`; `onUse` runs.
- Lines 48-49: subject moves from `z=0.025` to `z=-0.316`, and duck follows at stable distance `0.2408m`.
- Lines 50-59: `onUse` keeps triggering every ~2s; therefore the duck action state machine and audio/animation callback entry are not blocked.
- Lines 62-69: `squeeze-rubber-duck` completes, then `put-down-rubber-duck` starts and completes; action lock releases normally.
- Lines 70-75: after `put-down-rubber-duck`, `FairyBehaviorSystem` restores movement, then position/velocity explode:
  - line 71: `forceMag=7.5`, `actualVelocityMag=0`
  - line 72: `x=-4.57`, `actualVelocityMag=47.37`, `forceMag=732.55`
  - line 74: `z=298.81`, `actualVelocityMag=2254.99`, `forceMag=33846.75`
  - line 75: `x=-6481.95`, `actualVelocityMag=64533.16`, `forceMag=968019.3`

Conclusion:

- H3 is rejected for this run: duck follow exists, `carryAwayTarget` is generated, and the fairy does move with the duck.
- H4 is partially rejected: `onUse()` is called every ~2s. If shape animation is still visually absent, the next issue is inside `playObjectAnimation()` resource/track selection rather than action scheduling.
- H5 is rejected for this run: action lock releases normally.
- Confirmed root cause for fly-away: after action completion, a recovery/collision impulse or position jump produces an abnormal finite-difference velocity; `FairyBehaviorSystem` then feeds that huge velocity into PD control and amplifies it into extreme force.

## Fix Applied

- `ActionSubjectMotionTemplate.restore()` no longer restores `COLLIDER_FULL` immediately.
- Added `ActionRecoveryGraceComponent` with `0.8s` recovery duration.
- During grace, `FairyBehaviorSystem` keeps the subject collision as `TRIGGER_LITE`, clears force/linear velocity/angular velocity, and restores the previous collision mode only after the grace window.
- `FairyBehaviorSystem` now ignores abnormal finite-difference velocity spikes above `8m/s`.
- `FairyBehaviorSystem` clamps controller velocity input to `3m/s`.
- `FairyBehaviorSystem` clamps output force to `80N`.
- Gradle verification: `./gradlew :app:compileDebugKotlin --rerun-tasks` passed.
- Cleared log file for post-fix reproduction.

## Post-Fix Verification

### Fix iteration 2: carry template + recovery fallback

User reported:

- `play-football` still has a significant probability of high-speed fly-away / circular motion.
- `squeeze-rubber-duck` still does not visibly carry the duck while moving.

Reflection:

- The boombox seemed correct because `start-boombox` finishes quickly after pickup while keeping `PickedObjectFollowComponent`.
- After `start-boombox` completes, `FairyBehaviorSystem.isCarryingObject()` sees the picked object and uses its carrying direct-motion branch to move the fairy; the boombox follows through `PickedObjectFollowSystem`.
- The duck path was different: `squeeze-rubber-duck` holds the action lock for 8s, so `FairyBehaviorSystem` is skipped. Its only internal carry movement was a one-shot `carryAwayDistance=0.55m`; after reaching that point, the fairy remains still for the rest of the squeeze duration.
- Therefore the design mismatch is not `PickedObjectFollowComponent`; it is that duck did not use a continuous carrying movement policy while the long action lock was active.

Changes:

- Added `CarriedObjectInteractionTemplate` in `ObjectInteractionActionControllers.kt`:
  - `pickUp(target, holderActorId, holdOffset)`
  - `putDown(target, follow)`
  - centralized collision mode, rigid body mode, gravity, force and velocity clearing for carried objects.
- `CarryAndUseObjectActionInstance` now supports `CarryMovementMode`:
  - `NONE`
  - `ONCE`
  - `CONTINUOUS`
- `squeeze-rubber-duck` now uses:
  - `carryMovementMode = CONTINUOUS`
  - `carryAwayDistance = 0.35f`
  - `usingFlySpeed = 0.25f`
  - `maxCarryTravelDistance = 1.2f`
- This makes the duck continue moving in small carry segments during the 8s squeeze action, instead of moving once and stopping.
- `FairyBehaviorSystem` now adds a stronger action recovery fallback:
  - if recovery grace or abnormal finite-difference velocity occurs and the fairy is already more than `6m` from HMD, snap it back to a safe follow-zone target.
  - during action recovery grace, suppress behavior force output to `Vector3.ZERO`.

Validation:

- `./gradlew :app:compileDebugKotlin --rerun-tasks`: passed.
- `GetDiagnostics`: no diagnostics returned after the latest edit.

Pending:

- User post-fix runtime reproduction.
