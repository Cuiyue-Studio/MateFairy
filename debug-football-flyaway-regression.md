# Debug Session: football-flyaway-regression

Status: [OPEN]

## Problem

After `play-football`, the fairy still has a significant chance to fly away at high speed or enter high-speed circular motion.

The user explicitly asked for a regression-oriented investigation:

- The original `play-football` implementation did not have this problem.
- The current implementation has repeatedly failed after several patch attempts.
- We must identify what factor(s) were added in the middle and why they caused the regression.

## Hypotheses

- H1: Increased football / spatial mesh restitution turns minor overlap or contact into a high-energy impulse.
- H2: The subject collision mode is restored to `COLLIDER_FULL` while the fairy is still in an unsafe relationship with the football or mesh.
- H3: The post-kick retreat / face-direction logic creates a moving yaw target or circular control loop.
- H4: `FairyBehaviorSystem` post-action recovery converts one abnormal transform jump into huge velocity and PD force.
- H5: Later recovery fallback logic itself introduces large teleport / retarget / pull-back behavior that presents as fly-away.

## Evidence Plan

- First inspect current diff and current code paths without modifying business logic.
- Then add minimal instrumentation only if existing evidence is insufficient.
- Fix only after one or more hypotheses are confirmed by evidence.

## Evidence

### Static Regression Inventory

Compared with the early `play-football` path, the current codebase has added several factors that can affect stability:

- `PLAYING_KICK_ANIMATION`: the fairy now waits near the football while repeatedly facing it before applying velocity.
- `postKickRetreatTarget`: after kicking, the fairy performs a scripted retreat and repeatedly changes yaw toward the retreat point.
- `ActionSubjectMotionTemplate`: scripted movement now switches the subject to `KINEMATIC` + `TRIGGER_LITE`, then restores through `ActionRecoveryGraceComponent`.
- `FairyBehaviorSystem` recovery fallback: abnormal velocity / recovery grace can now trigger force suppression or safe-zone repositioning.
- Resource physics changed:
  - football friction decreased and damping decreased.
  - football restitution remains high (`0.88f`).
  - spatial mesh restitution is high (`0.75f`), affecting floor and walls.
- Fairy collision capsule was changed to radius `0.18f`, height `0.12f`.

Regression implication:

- The most suspicious new combination is not a single line but an interaction:
  - long animation wait near a dynamic football
  - high restitution football + high restitution spatial mesh
  - scripted transform movement with later physics restoration
  - post-action behavior recovery trying to resume motion from potentially abnormal position/yaw.

### Instrumentation Applied

- `DebugEventReporter` now reports to session `football-flyaway-regression`, run `pre-fix`.
- `PlayFootballActionController` now reports:
  - subject position/yaw
  - football position/yaw
  - 3D and horizontal distance
  - post-kick retreat target
  - subject rigid body/collision/linear velocity/angular velocity
  - football rigid body/collision/linear velocity/angular velocity
- `FairyBehaviorSystem` now reports:
  - recovery snap events
  - raw actual speed
  - recovery grace flag
  - abnormal recovery motion flag
- Debug server:
  - URL: `http://10.4.19.96:7777/event`
  - log: `.dbg/trae-debug-log-football-flyaway-regression.ndjson`
- Gradle validation passed after instrumentation.
- Diagnostics returned empty.

## Fix

### Phase 1 Trial: simple chain + animation completion event

Implemented the user-approved first-stage validation chain:

- Reverted `play-football` to the simple action path:
  - approach football
  - face football
  - play kick animation
  - wait for animation completion event
  - set football `PhysicsVelocityComponent.linearVelocity`
  - finish action
- No post-kick retreat logic is used in this phase.
- Fairy and football remain decoupled:
  - the fairy does not kick through physical collision
  - football movement is still script-driven by velocity assignment
- `play-football` now uses `ActionSubjectMotionTemplate`, so the fairy is `KINEMATIC + TRIGGER_LITE` while script-controlled.
- Added animation lifecycle event model in `AnimationController.kt`:
  - `ActionAnimationPlayOptions`
  - `AnimationLifecycleEvent`
  - `AnimationLifecycleState`
  - `AnimationEndReason`
  - `AnimationLifecycleListener`
  - `AnimationLifecycleSubscription`
- `AnimationModule.playActionAnimation()` now accepts options and publishes:
  - `STARTED`
  - `COMPLETED`
  - `FAILED` for missing resource / invalid track
- Kick animation is clipped through `ActionAnimationPlayOptions(maxDurationSeconds = 2f)`.
- `play-football` subscribes only to:
  - `ownerActionId == "play-football"`
  - `animation == AnimationConfig.playFootballKickAnimation`
  - `state == COMPLETED`
- If the animation event is missing, `play-football` uses a timeout fallback at `2.35s` to avoid permanent action lock.
- `AnimationConfig` now includes `KICK_ACTION(44, ..., 2000L)` and `playFootballKickAnimation`.
- `MateFairyRuntimeFactory` now injects `animationModule` into `PlayFootballActionController`.

Validation:

- `./gradlew :app:compileDebugKotlin --rerun-tasks`: passed.
- `GetDiagnostics`: empty.

Post-fix runtime verification is pending.

### Phase 1 Follow-up: model and track mapping fix

User reported that the kick animation did not appear to play.

Analysis:

- The first-stage animation event chain was present, but `HomeStage` still loaded `asset://pico_robot_animated.glb`.
- `AnimationConfig.KICK_ACTION` used track index `44`, which belongs to `pico_robot_animated_new.glb`.
- Therefore the likely runtime behavior was:
  - `AnimationModule.canPlay(KICK_ACTION)` rejects the animation because `trackIndex=44` is out of range for the old model.
  - `play-football` sees `scheduled=false`.
  - `play-football` uses fallback and applies football velocity immediately.
  - The football moves, but the fairy does not show the kick animation.

Changes:

- `HomeStage.kt`: changed robot GLB path to `asset://pico_robot_animated_new.glb`.
- `AnimationConfig.kt`: updated valid track mapping for `pico_robot_animated_new.glb`:
  - `0`: idle
  - `10`: jump
  - `14`: look around
  - `18`: walk forward
  - `22`: wave
  - `26`: mad
  - `30`: happy
  - `34`: dance
  - `38`: sad
  - `40`: pick
  - `42`: disco dancing
  - `44`: kick

Validation:

- `./gradlew :app:compileDebugKotlin --rerun-tasks`: passed.
- `GetDiagnostics`: empty.
