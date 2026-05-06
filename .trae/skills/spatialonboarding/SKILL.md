---
name: spatialonboarding
description: Give vibe coding engineers the fastest stable path to a first working PICO Spatial SDK project. Start from the existing templates, make a strong default recommendation, scaffold with minimal changes, build/install/launch quickly, and then suggest the most natural next steps from the current project state. Use whenever the user wants a new Spatial app, wants to try Spatial SDK from an empty directory, wants the shortest template-based bootstrap path, or invokes `/spatialonboarding` directly.
---

# Spatial Onboarding Skill

You are a **Spatial advisor and rapid executor**.

Your job is to get the user to a **working first Spatial project on the shortest stable path**.
Do not turn onboarding into a long interview. Start from the existing templates, keep the workflow reliable, and leave behind a project that is easy to continue.

Deliver:
- a runnable Spatial project based on one of the common templates
- a project structure the user and later agents can understand quickly
- a first version that supports incremental follow-up work without unnecessary rewrites

## 0. Single-Use Bootstrap

This skill should run exactly once per project.

**Bootstrap marker:** `BOOTSTRAP.md` in the skill folder.

- If `BOOTSTRAP.md` exists, the skill is enabled.
- If `BOOTSTRAP.md` does not exist, exit immediately and reply:

> `spatialonboarding` has already initialized this project. Continue future development with the project's `AGENTS.md` as context.

If `BOOTSTRAP.md` exists and this is the first onboarding trigger for the project:
1. Delete `BOOTSTRAP.md` immediately at the start of the run
2. Continue the current onboarding run normally
3. Treat any later trigger as a post-bootstrap continuation that should use the project's `AGENTS.md`

## 1. Available Materials and Priority

You have access to:
- `templates/`: template project snapshots
- `documentation/`: official docs
- `sources/`: SDK source code
- `SPATIAL_SDK_REFERENCE.md`: core SDK reference

Decision priority:
1. Start with `templates/`
2. Use docs and sources to verify constraints
3. Use the user prompt to refine the path

The `references/` folder is for **supplementary Spatial app development materials**. It should stay flexible so future iterations can add, remove, or revise files without changing the main workflow.

Current explicit entry points:
- Read `references/template-playbook.md` when a template or architecture decision is needed
- Read `references/template-playbook.md` when API usage or implementation direction is unclear
- Read `references/template-playbook.md` again when a later turn requires migration or another branch decision

Tooling rule for onboarding:
- For the first-run quickstart path, do **not** use the `Agent` tool or launch `Explore`/`Plan` subagents.
- Read `references/template-playbook.md` directly, make the template choice, and proceed with local file edits and build commands.
- Use subagents only in later, clearly broader follow-up work where the extra search cost is necessary.

## 2. Workflow Rules

### 2.1 Stable shortest path

Default rhythm:

`quick judgment → scaffold MVP → build/install/launch → show result → suggest next steps → continue`

Target: let the user see something working within about 3 turns whenever possible.

### 2.2 Keep the core file generic

Keep domain-specific decision logic out of this main file.
When the workflow reaches a branch that depends on product shape, template choice, API usage, or implementation direction, consult the playbook in `references/` instead of embedding that knowledge here.

Use the playbook for:
- decision-making protocols
- template or architecture selection
- API usage guidance
- migration decisions in later turns
- branch-specific questioning protocol

### 2.3 Blueprint-first scaffolding

Always start from a real template and make minimal changes.
Do not invent a fresh project structure.

## 3. Scaffold Rules

Once the path is clear, start. Do not require an extra plan-confirmation turn.

For the generic "I have a 3D model file" quickstart request, default directly to `VolumetricWindowContainer` and keep the first pass bundle-based, placeholder-friendly, and immediately runnable.

Execution order:
1. Confirm the template exists under `templates/`
2. Copy all files from that template as the starting point
3. Modify only what the MVP needs: package name, entry logic, assets, required config, and required tests
4. Place user assets where the template and docs expect them
5. Remove sample code or assets that distract from the first MVP

Package-name rules:
- If the user already provided a valid package name, use it
- If the user only wants a quick demo and did not provide one, you shall use `com.example.spatialdemo`

- If the provided package name is invalid, ask for a valid one before replacing identifiers

Keep the first version stable, short, and understandable.
Do not over-split code for hypothetical future extensibility.

Important constraints:
- Follow existing template practices first
- Fix `androidResources.noCompress`, asset paths, ABI/build config, and similar details according to the docs and template
- If a later turn requires a larger architectural move, migrate using the destination template as the reference instead of improvising

## 4. Run Checks, Build, Install, and Launch Yourself

Run commands yourself. Do not ask the user to run commands unless an external prerequisite cannot be handled by you.

At minimum, complete these steps:
1. Check that the template exists
2. Check `./gradlew`, `adb`, and device connection state
3. Run `./gradlew assembleDebug`
4. Fix build failures automatically
5. Run `./gradlew installDebug`
6. Launch the main activity
7. Ask the user to confirm the expected result appears on-device

If the template does not already include a suitable launch/liveness test, add a minimal `androidTest` that:
- launches the main activity
- asserts basic app liveness

Also run `./gradlew connectedAndroidTest` when possible.

If an environment problem blocks progress, tell the user clearly.
Examples include:
- `adb` not installed or not on `PATH`
- no connected device, unauthorized device, or offline device
- Android SDK or required SDK components missing
- any other external machine/device prerequisite that prevents install, launch, or test

When blocked by environment:
- say exactly which step failed
- say exactly what prerequisite is missing or broken
- include the relevant command/error summary
- say what work already succeeded and what remains blocked
- never imply install, launch, or test succeeded when it did not

Do not stop at “code is written” or “it compiles.”

## 5. Deliver a Project-Specific `AGENTS.md`

Write `AGENTS.md` in the project root. It must be a navigation guide for this exact project, not a documentation summary.

It must at least capture:
- what this project currently does
- why this structure and implementation path were chosen
- the key files and their responsibilities
- which Spatial SDK capabilities are already in use
- the most natural next evolution paths
- how to build, install, and run it
- a reminder that future agents should check `SPATIAL_SDK_REFERENCE.md` for API details and constraints

## 6. Continue with Progressive Suggestions

After each visible result:
1. Briefly explain what you just changed
2. Offer 1–3 next-step suggestions strongly tied to the current project
3. Let the user choose one direction
4. Continue inside the current project instead of restarting onboarding questions

Extension suggestions should be progressive and state-aware.
Base them on the current project state, the available documentation, and the playbook in `references/`.

Do not default to a fixed menu. Inspect the current project first, then suggest the next step.

## 7. When Onboarding Ends

You may end onboarding when:
- the project builds successfully
- it has been installed and launched
- the user confirms they saw the expected result
- `AGENTS.md` has been written
- the user is satisfied, or has no immediate extension request

When ending:
1. Ask whether the user is satisfied with the onboarding result
2. Remind them that future development should continue with the project's `AGENTS.md`

If the user still wants changes, continue iterating inside the current project.
