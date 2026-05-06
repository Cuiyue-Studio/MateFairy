When the line of sight focuses on a 3D object, or when a controller or finger approaches a 3D object, the hover highlight effect is triggered.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ece1783551e24cbdb70044d954d01783~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a03f86ef5a347a89c0a13f4ab55e775~tplv-goo7wpa0wc-image.image)


</div>
</div>

## Related components
The components required to implement the spatial hover effect are described as follows:
| **Component name** | **Description** |
| --- | --- |
| `InteractableComponent` | Used to mark the entity as interactive, enabling it to receive and process input events. |
| `CollisionComponent` | By specifying the entity's shape, material, response behavior, filtering rules, and the level of detail for collision reports, the entity is given physical interaction capabilities. <br> Through the `collisionShape` property, you can set the interactive range of the entity. For example, the entity is a sphere with a diameter of 0.5 meters. If its `collisionShape` is set to a sphere with a diameter of 1 meter, then the entity is interactive within the range of that sphere. |
| `HoverEffectComponent` | Used to add the hover highlight effect to a 3D entity. After adding this effect to the parent entity, its child entities will also display the same effect. |
## Prerequisites
Ensure that the target entity has `InteractableComponent` and `CollisionComponent` added to make it interactive.
## Add hover highlight effect to a 3D entity
Attach a `HoverEffectComponent` to a 3D entity to trigger hover highlight effect when it is interacted with. At the same time, this effect will be passed to the child entities of the entity.
Code example:
```Kotlin
entity.apply {
    components.set(InteractableComponent())
    components.set(CollisionComponent(listOf(ShapeResource.createConvexMesh(mesh)), PhysicsMaterialResource()))
    components.set(HoverEffectComponent())
}
```

