In spatial interaction, in addition to specifying 2D components as interactive objects, the system also supports specifying 3D entities as interactive objects. Typically, the `targetedToEntity` parameter can be used in the extension methods of `PointerInputScope` to specify and match the entity that serves as the interaction target.
You must add `InteractableComponent` to entities as interactive objects; otherwise, they will not respond to any interaction events.

By using `TargetedToEntity`, you can directly specify an entity and its child entities (if any) as interactive objects. For example:
```Kotlin
val entity = remember { Entity() }

SpatialView(
        modifier =
            Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    // Set any entity as the interaction target
                    detectTapGestures(context = context, targetedToEntity = TargetEntity.hit(entity) {
                        // Event handling
                        println("tap invoked!")
                    }
                }
    ) { content, _ ->
        // Add an interaction component to the entity
        entity.components.set(InteractableComponent())
        entity.components.set(
            CollisionComponent(
                collisionShape = listOf(ShapeResource.createSphere(radius = 0.3f)),
                physicsMaterial = PhysicsMaterialResource()
            )
        )
        content.addEntity(entity)
    }
```

You can also use `TargetedToEntity` to set entities that meet certain criteria as interactive objects, such as an entity whose name starts with `"interactable"`.
```Kotlin
SpatialView(
        modifier =
            Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(context = context, targetedToEntity = TargetEntity.any { it.getName() == "interactable" }) {
                        // Event handling
                        println("tap invoked!")
                    }
                }
    ) { content, _ ->
        // Create a new entity instance
        val entity = Entity()
        entity.setName("interactable")
        // Entity is non-interactive by default. To make an entity interactive, you need to add both the InteractableComponent and the CollisionComponent to it
        entity.components.set(InteractableComponent())
        entity.components.set(
            CollisionComponent(
                collisionShape = listOf(ShapeResource.createSphere(radius = 0.3f)),
                physicsMaterial = PhysicsMaterialResource()
            )
        )
        content.addEntity(entity)
    }
```

