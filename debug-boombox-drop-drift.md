# Debug Session: boombox-drop-drift

Status: [OPEN]

## Symptom

After `stop-boombox` / put-down action, the fairy pauses near the drop location for a few seconds, then rapidly flies away. The issue persists even after physical recovery buffering and post-action behavior-state reset.

## Session

- sessionId: `boombox-drop-drift`
- runId: `pre-fix`
- debugServerUrl: `http://10.4.93.9:7777/event`

## Hypotheses

- H1: Post-action reset executes, but normal behavior later re-enters `RANDOM_MOVING`/`FOLLOWING` and assigns a target that is far from the current fairy position.
- H2: Physics velocity or force becomes non-zero after recovery ends, meaning another system or collision resolution is injecting motion after our reset.
- H3: The visual model and physics proxy diverge; the physics body stays put while the animated visual GLB appears to fly due to animation/root-motion or parent/child transform mismatch.
- H4: HMD/root coordinate conversion or HMD entity position is stale/wrong after action, causing the behavior system to compute an unexpected target away from the user.
- H5: A second action/random behavior is enqueued after `stop-boombox` and overrides the post-action idle state.

## Instrumentation Plan

- Log put-down action lifecycle: drop position, subject position, target collision/rigidbody/velocity.
- Log action completion and lock release.
- Log post-action reset and recovery-grace end.
- Log first target assignment after reset and any non-zero force/velocity spike.
- Log visual-vs-physics positions for a few seconds after reset.

## Pre-Fix Evidence

- Logs confirm `resetBehaviorStateAfterAction(...)` executed after `stop-boombox`.
- During recovery grace, position stayed stable at `0.356,1.599,-0.859`, `target=null`, `collision=TRIGGER_LITE`.
- Immediately after recovery ended, the fairy position jumped to `0.195,1.621,-1.565`, `distance2D=1.715`, state became `FOLLOWING`, and behavior force spiked to about `67N`.
- `PhysicsVelocityComponent` was reported as `null`, so previous velocity clearing did not write a velocity component for the fairy.

## Evidence Decision

- H1 confirmed: behavior re-entered `FOLLOWING` after recovery because the physical position drifted outside `outerRadius`.
- H2 confirmed: runtime movement happened after recovery despite reset; missing velocity component means the prior clear operation was incomplete.
- H3 mostly rejected: `visualDelta` stayed small, so visual-only root motion is not the primary cause.
- H4 not primary in current logs: HMD position is stable and targets are around HMD.
- H5 not observed in current logs: no second action is needed to reproduce the drift.

## Post-Fix Plan

- Keep the fairy rigidbody `KINEMATIC` during action recovery grace.
- Ensure `PhysicsVelocityComponent` exists and is explicitly zeroed during restore/reset/recovery.
- Switch instrumentation `runId` to `post-fix` for comparison.

## Post-Fix Code Changes

- `ActionSubjectMotionTemplate.clearMotion(...)`
  - If `PhysicsVelocityComponent` is missing, create it and set both linear/angular velocity to zero.
  - This fixes the observed `velocity=null` gap in pre-fix logs.
- `FairyBehaviorSystem.handleActionRecoveryGrace(...)`
  - Keep fairy rigidbody as `KINEMATIC` during recovery grace.
  - Keep collision as `TRIGGER_LITE`.
  - Clear force and velocity every recovery tick.
  - Before recovery ends, clear force/velocity again, then restore `DYNAMIC`.
- Instrumentation run id switched to `post-fix` for verification comparison.

## Current Build

- `./gradlew :app:compileDebugKotlin`: `BUILD SUCCESSFUL`

## Post-Fix Evidence

- `post-fix` logs show velocity is no longer `null`.
- Recovery grace now reports `rb=KINEMATIC`, `collision=TRIGGER_LITE`, and stable position during the recovery window.
- Drift still starts after recovery ends, when the fairy collision mode is restored and behavior resumes; the first spike shows `state=RANDOM_MOVING`, then `FOLLOWING`.
- This narrows the remaining cause to collision/physics restoration after recovery, not the initial action reset.

## Post-Fix Collision-Lite Change

- `ActionSubjectMotionTemplate.restore(...)` now accepts `restoreCollisionModeOverride`.
- `PutDownObjectActionInstance.cleanupSubjectMotion(...)` restores the fairy with `CollisionResponseMode.TRIGGER_LITE` after put-down.
- Rationale: put-down objects are restored as dynamic colliders near the fairy. Restoring the fairy to `COLLIDER_FULL` reintroduces physical collision resolution exactly when the recovery window ends.
- New instrumentation run id: `post-fix-collision-lite`.
- Build: `./gradlew :app:assembleDebug` => `BUILD SUCCESSFUL`.

## Root Fix: Force Envelope

- User feedback after `post-fix-soft-follow-recovery`: the fairy can recover normal following briefly, then flies away again.
- Static review confirms the remaining root issue is the normal `FairyBehaviorSystem` locomotion controller:
  - `FOLLOWING` generated a fresh random HMD-near target every frame, creating discontinuous force directions.
  - Movement used `force = (desiredVelocity - actualVelocity) * 15`, so baseline follow force could be `1.5 * 15 = 22.5N`, and opposite velocity could produce about `67.5N`.
  - Final force cap was `80N`, far above what the fairy locomotion needs.
- Fix implemented:
  - Removed `FairyPostActionIdleAnchorComponent` and `FairyPostActionFollowRecoveryComponent`.
  - Removed `postActionIdleAnchorSeconds` plumbing from action recovery.
  - `FOLLOWING` now keeps a stable target and refreshes only when reached/stale.
  - Movement now uses a bounded force envelope:
    - response gain `4.0`
    - movement force cap `10N`
    - output force cap `18N`
    - native linear velocity cap `2.2m/s`
    - arrival slowdown radius `0.8m`
  - New instrumentation run id: `post-fix-force-envelope`.
- Build: `./gradlew :app:assembleDebug` => `BUILD SUCCESSFUL`.

## Root Fix: Direct Kinematic Locomotion

- User feedback after `post-fix-force-envelope`: limiting the force range still does not solve the direction/力度 design problem; the fairy should not need physical force to perform normal follow/random locomotion.
- Architecture decision:
  - Fairy autonomous locomotion is now script-driven: `nextPosition = currentPosition + direction * speed * dt`.
  - The fairy rigid body stays `KINEMATIC` and collision stays `TRIGGER_LITE` during normal behavior and action recovery.
  - `PhysicsForceComponent.force` and `PhysicsVelocityComponent` are still cleared for compatibility, but they are no longer the source of normal fairy movement.
- Code changes:
  - `FairyBehaviorSystem.applyDirectBehaviorMotion(...)` directly updates `TransformComponent.position` and syncs the visual entity transform.
  - `ActionSubjectMotionTemplate.restore(...)` no longer restores the fairy to `DYNAMIC`; it keeps `KINEMATIC + TRIGGER_LITE` and starts only a short recovery grace.
  - Initial fairy setup in `HomeStage.kt` now creates the fairy proxy as `KINEMATIC + TRIGGER_LITE`.
  - Removed obsolete force-envelope constants/helpers and dynamic-settle recovery state.
  - New instrumentation run id: `post-fix-direct-motion`.
- Build:
  - `./gradlew :app:assembleDebug --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL`.
  - `./gradlew :app:compileDebugKotlin --rerun-tasks --no-daemon -Dkotlin.compiler.execution.strategy=in-process` => `BUILD SUCCESSFUL` after Kotlin daemon fallback.
