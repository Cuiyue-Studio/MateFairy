# Debug Session: chair-command-chain

Status: [OPEN]

## Problem

玩家通过对话命令精灵“去椅子上待着”后，精灵依然没有飞到椅子上。

## Hypotheses

1. 对话意图没有稳定映射到 `stay-on-chair`，导致 action 未创建。
2. `stay-on-chair` 请求已创建，但被 action lock、驻留态或预条件拒绝。
3. `stay-on-chair` action 执行了，但语义查询没有返回 `CHAIR` 候选，或候选坐标错误。
4. 表面解析或最终目标合成产生错误目标点，导致移动目标不在椅子上。
5. action 到达/完成逻辑没有正确写入或保持 `FairySemanticResidenceComponent`。

## Evidence Plan

- 插桩对话 action 处理入口，确认 LLM/intent 输出的 actionId 与 source。
- 插桩 `InteractionActionRequestBus.enqueue/drain`，确认请求是否被拒绝或消费。
- 插桩 `InteractionActionSystem`，确认 controller 是否创建、实例是否运行、状态如何结束。
- 插桩 `StayOnSemanticObjectActionInstance`，确认每个状态下的目标、表面、距离、最终位置。
- 插桩驻留态写入与 `FairyBehaviorSystem` 驻留接管，确认锁定是否生效。

## Timeline

- Created debug session and hypotheses.
- Started debug server in remote mode.
  - URL: `http://10.4.47.36:7777/event`
  - Health: `log_count=0` before reproduction.
- Added pre-fix instrumentation only.
  - `ActionRegistry`: action handler registration and dispatch.
  - `GenericAnimationHandler`: generic animation execution.
  - `SceneInteractionActionHandler`: dialogue action enqueue bridge.
  - `InteractionActionCore`: ECS action controller registration, request enqueue/drain.
  - `InteractionActionSystem`: request execution and action lifecycle.
  - `RealWorldSemanticActionControllers`: stay-on-chair state machine, target/surface/move/settle/fail.
- Compile check after instrumentation: `./gradlew :app:compileDebugKotlin` -> `BUILD SUCCESSFUL`.

## Static Finding To Verify

`AnimationConfig.supportedActions` currently registers `stay-on-chair` through `GenericAnimationHandler`.
`MateFairyRuntimeFactory` does not currently register:

- `InteractionActionRuntimeDependencies.actionRegistry.register(StayOnChairActionController())`
- `InteractionActionRuntimeDependencies.actionRegistry.register(LeaveChairActionController())`
- `SceneInteractionActionHandler(intent = StayOnChairActionController.ACTION_ID, ...)`
- `SceneInteractionActionHandler(intent = LeaveChairActionController.ACTION_ID, ...)`

Expected pre-fix evidence if this is root cause:

- `ActionRegistry dispatch` for `intent=stay-on-chair`.
- Handler is `GenericAnimationHandler`.
- No `RequestBus enqueue` for `stay-on-chair`.
- No `ActionSystem instance created` for `stay-on-chair`.

## Pre-fix Evidence

Confirmed root cause from `.dbg/trae-debug-log-chair-command-chain.ndjson`:

```json
{"location":"ActionRegistry.kt:dispatchAction","msg":"[DEBUG] ActionRegistry dispatch","data":{"intent":"stay-on-chair","handler":"GenericAnimationHandler","registered":"dance|disco|fetch_ball|leave-chair|play-football|put-down-rubber-duck|squeeze-rubber-duck|start-boombox|stay-on-chair|stop-boombox"}}
{"location":"GenericAnimationHandler.kt:execute","msg":"[DEBUG] GenericAnimationHandler execute","data":{"intent":"stay-on-chair","hasAnimation":"false","params":"{}"}}
```

Conclusion:

- LLM/orchestrator did dispatch `stay-on-chair`.
- `ActionRegistry` routed it to `GenericAnimationHandler`.
- `GenericAnimationHandler` found no animation for `stay-on-chair`.
- No `SceneInteractionActionHandler`, `RequestBus enqueue`, or `ActionSystem instance created` log appeared for `stay-on-chair`.

Confirmed hypothesis:

- H1/H2 combined: action intent exists, but the handler registration route is wrong. `stay-on-chair` is consumed as a generic animation and never reaches ECS interaction action.

Rejected as primary cause for this failure:

- H3: semantic query was not reached.
- H4: surface resolution was not reached.
- H5: residence enter was not reached.

## Fix Applied

Minimal fix:

- Register `StayOnChairActionController` and `LeaveChairActionController` in `InteractionActionRuntimeDependencies.actionRegistry`.
- Override `stay-on-chair` and `leave-chair` in the dialogue `ActionRegistry` with `SceneInteractionActionHandler` after generic animation registration.
- Add both intents to `DefaultBehaviorDecisionMaker.actionClassIntents` so they are treated as ECS action tasks instead of ordinary animation/emotion actions.

Files changed:

- `app/src/main/java/com/example/matefairy01/runtime/MateFairyRuntimeFactory.kt`
- `app/src/main/java/com/example/matefairy01/orchestrator/decision/DefaultBehaviorDecisionMaker.kt`

Validation:

- `./gradlew :app:compileDebugKotlin` -> `BUILD SUCCESSFUL`
- Cleared debug server logs for post-fix verification: `log_count=0`.

Expected post-fix evidence:

- `ActionRegistry dispatch` for `stay-on-chair` should show `handler=SceneInteractionActionHandler`.
- `SceneInteractionActionHandler enqueue` should show `enqueued=true`.
- `RequestBus enqueue/drain` should include `stay-on-chair:DIALOGUE`.
- `ActionSystem instance created` should show `controller=StayOnChairActionController`.
- Then `StayOnChair update/resolve target/move` logs should appear.
