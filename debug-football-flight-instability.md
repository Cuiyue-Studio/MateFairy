# Debug Session: football-flight-instability

Status: [OPEN]

## Problem

After `play-football`, the fairy may:

- fly away at high speed
- orbit around the player at high speed
- move forward in circular loops
- fly fast for a while and then get stuck near a wall

## Constraints

- No business logic modification before runtime evidence is collected.
- First code modification after this file must be instrumentation only.
- Cleanup must wait for user confirmation.

## Hypotheses

1. `FairyBehaviorSystem` amplifies abnormal post-action position deltas into large PD forces.
   - Observation: post-action `actualVelocity`, `totalForce`, `state`, `currentTarget`, and `distance2D`.
2. The fairy leaves `play-football` with an unsafe rigid body or collision state.
   - Observation: subject `RigidBodyMode`, gravity, `CollisionResponseMode`, force, linear velocity, angular velocity before/after cleanup.
3. The action completion handoff produces an invalid or far-away behavior target.
   - Observation: `followJustRestored`, `reconcileAfterAction`, `currentTarget`, HMD position, fairy position.
4. The visual circular motion is yaw/target feedback rather than physical angular velocity.
   - Observation: `currentYaw`, visual yaw, target yaw, angular velocity, movement direction.
5. The football or spatial mesh collision energy injects displacement before behavior recovery.
   - Observation: fairy/football distance, football velocity, fairy collision mode, and position jump immediately after kick.

## Instrumentation Plan

- Add debug reporting to `play-football` states and cleanup.
- Add debug reporting to `FairyBehaviorSystem` for 5 seconds after action recovery.
- Report compact JSON-like payloads to the Debug Server.

## Evidence

Instrumentation added and compiled.

### Instrumentation

- `PlayFootballActionController.kt`
  - Reports `update`, `kick`, `cleanup-before`, `cleanup-after`.
  - Captures state, subject position/yaw, subject rigid body mode, gravity, collision mode, force, linear velocity, angular velocity, football position, football velocity, and fairy-football distance.
- `FairyBehaviorSystem.kt`
  - Reports `follow-restored`, `recovery-grace`, and `post-action-sample`.
  - Samples 5 seconds after action handoff.
  - Captures behavior state, fairy position/yaw/currentYaw, behavior target, HMD position, rigid body mode, gravity, collision mode, component velocity, angular velocity, raw speed, clamped actual velocity, force, output force magnitude, recovery grace state, and follow flag.

### Debug Server

- Session: `football-flight-instability`
- Endpoint: `http://10.71.200.128:7777/event`
- Log file: `.dbg/trae-debug-log-football-flight-instability.ndjson`

### Validation

- `./gradlew :app:compileDebugKotlin --rerun-tasks`: `BUILD SUCCESSFUL`
- `GetDiagnostics`: `[]`

## Fix

Pending evidence.
