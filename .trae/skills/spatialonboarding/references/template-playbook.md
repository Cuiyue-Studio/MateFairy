# Template Playbook

This file is a supplementary reference for Spatial app development.
Use it only when the main skill reaches a decision branch.

It contains the domain-specific protocol for:
- template selection
- `SpatialModelView` vs `SpatialView + ECS`
- migration choices in later turns
- deciding whether to ask a clarifying question at all

## 1. Decision protocol

### 1.1 Ask only when a real branch exists

Do not ask generic discovery questions.

- If the path is obvious, recommend it and start immediately
- Ask at most **one** clarifying question only when the answer would materially change the project shape

Real branch points include:
- `VolumetricWindowContainer` vs `FullStage`
- `SpatialModelView` vs `SpatialView + ECS`
- fast MVP now vs laying groundwork for physics / interaction / tracking now

Bad questions include:
- “What kind of spatial app do you want?”
- “Please describe your requirements.”
- “What demo do you want?”

Good questions explicitly name the choices, explain the impact, and preferably include a recommendation.

### 1.2 Use the common templates as the blueprint

Use the three common templates as the blueprint:
- `PlanarWindowContainer`
- `VolumetricWindowContainer`
- `FullStage`

Copy from a real template first, then make minimal changes.
Do not invent a fresh project structure.

## 2. Routing defaults

Follow these defaults unless the evidence strongly suggests otherwise:

- Mostly 2D panels with light 3D decoration → `PlanarWindowContainer`
- A single 3D model or medium-complexity 3D content that should appear quickly → `VolumetricWindowContainer`
- Physics, room-scale context, tracking, or immersive interaction from the start → `FullStage`
- Show a model quickly with minimal behavior → `SpatialModelView`
- Need entity behavior, collision, physics, or custom components → `SpatialView + ECS`

If the user has no clear requirement, pick the easiest likely-to-succeed showcase and keep the first capability narrow.

## 3. Template selection

### `PlanarWindowContainer`
Use when the app is mostly 2D UI with only light 3D decoration.

### `VolumetricWindowContainer`
Use when the user wants to show a 3D model or medium-complexity 3D content quickly.

### `FullStage`
Use when the app needs immersive space, room-scale context, tracking, physics, or spatial interaction from the start.

## 4. `SpatialModelView` vs `SpatialView + ECS`

### Prefer `SpatialModelView`
Use when the goal is to show a model quickly and entity-level behavior is not yet required.

### Prefer `SpatialView + ECS`
Use when the project needs entity behavior, collision, rigid body physics, interaction, or a clear path toward those capabilities.

## 5. Common migrations

### `VolumetricWindowContainer` → `FullStage`
Typical trigger: the user wants floor contact, bounce, grab/throw behavior, or room-scale context.

Migration rule: use the `FullStage` template as the reference, preserve confirmed assets and package naming, and do not improvise a new Stage project from scratch.

### `SpatialModelView` → `SpatialView + ECS`
Typical trigger: the user needs finer interaction, manipulation, or physics.

Migration rule: preserve the visible result first, then introduce ECS behavior.

## 6. Boundary reminder

- The template is the blueprint
- Copy the template first
- Keep first-pass changes minimal
- Avoid over-abstracting for future possibilities
