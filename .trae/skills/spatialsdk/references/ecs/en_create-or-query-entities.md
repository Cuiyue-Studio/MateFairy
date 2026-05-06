This article introduces how to create and query entities.
## Create entities
You can create an empty entity directly, or create an entity by loading a model.
### Directly create an empty entity
You can directly create an empty entity. This invisible entity can serve as a virtual parent node or a marker query node to control or find child nodes. All newly created empty entity objects include `TransformComponent` by default. Below is a code sample:
```Kotlin
val emptyEntity = Entity()
```

### Create an entity using a model
You can obtain an `Entity` instance by loading a model using `Entity#load`. During the loading process, each node in the model is converted into an `Entity` object, and these entities are organized into a tree structure that reflects the model's original hierarchy.
The `Entity#load` function returns a root entity that represents the entire model hierarchy. This root entity acts as the parent of the original model, with all child entities attached beneath it according to the model's hierarchy. By default, all child entities are equipped with a `TransformComponent`. Child entities that contain mesh data additionally have a `ModelComponent` attached, from which you can access the `MeshResource` and the list of materials.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

For example, the hierarchy of a model is as follows:
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a7f84bee40b148bba10428c14466c80e~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

The hierarchy of the entities (assumed to be named “model”) loaded from the model is as follows:
```Plain Text
model
├── Dynamic_Group
│   ├── SM_Picodesklamp_001
│   │   ├── SM_Picodesklamp_001 (has ModelComponent)
│   ├── SM_Picoearphone_001
│   │   ├── SM_Picoearphone_001 (has ModelComponent)
│   ├── SM_Picoequipment_001
│   │   ├── SM_Picoequipment_001 (has ModelComponent)
│   ├── SM_PicoPainting_001
│   │   ├── SM_PicoPainting_001 (has ModelComponent)
│   └── SM_Picovase_001
│       ├── SM_Picovase_001 (has ModelComponent)
└── Static_Group
    ├── SM_PicoRoominterior_Splite_001
    │   ├── SM_PicoRoominterior_Splite_001 (has ModelComponent)
    ├── SM_PicoRoominterior_Splite_002
    │   ├── SM_PicoRoominterior_Splite_002 (has ModelComponent)
    ├── SM_PicoRoominterior_Splite_003
    │   ├── SM_PicoRoominterior_Splite_003 (has ModelComponent)
    └── SM_PicoRoominterior_Splite_004
        └── SM_PicoRoominterior_Splite_004 (has ModelComponent)
```



</div>
</div>

Below is the code sample for loading entities via a model:
```Kotlin
val modelEntity = Entity.load("asset://alarm.usdz")
```

In addition, after you add a component using the Spatial Editor, you can also retrieve the corresponding component from the loaded model entity and modify its parameters at runtime. To retrieve the loaded model entity, first call `rootEntity = Entity.load()`, then  call `rootEntity.getChildren().first()`.
For more information, refer to "[Model](/en_model)".
## Query entities
Entity maintains its hierarchical structure as a tree. You can use the functions provided by the PICO Spatial SDK to quickly query the target entity.
### Use scene.queryEntity
By using `scene.queryEntity(EntityQueryCondition)`, you can find all entities that meet the specified conditions. This function returns these entity objects as a list.

* **Query by components**
   Filter entities with a specific component attached. For example, filter entities with `ModelComponent`:
   ```Kotlin
   val hasComponentsCondition =
       EntityQueryCondition.hasComponent(ModelComponent::class.java)
   val entities = entity.scene!!.queryEntity(hasComponentsCondition)
   ```

* **Query by a custom condition**
   Use a lambda function to pass in custom query conditions. For example, query the entity whose name is `"PICO"`:
   ```Kotlin
   val customCondition =
       EntityQueryCondition.customCondition { it.getName() == "PICO" }
   val entities = entity.scene!!.queryEntity(customCondition)
   ```

* **Query by a combination of custom conditions**
   You can use `and` or `or` to freely combine different `EntityQueryCondition`, forming a final query condition to pass to `scene.queryEntity(`). For example, filter entities that have both `ModelComponent` and `InteractableComponent`.
   ```Kotlin
   val hasComponentsCondition =
       EntityQueryCondition.hasComponent(ModelComponent::class.java)
           .and(EntityQueryCondition.hasComponent(InteractableComponent::class.java))
   val entities = entity.scene!!.queryEntity(hasComponentsCondition)
   ```

   You can also pass multiple conditions directly into `scene.queryEntity()`, separated by `,`. However, it is recommended to combine conditions whenever possible rather than passing in too many individual conditions.
   ```Kotlin
   // Query entities that have both ModelComponent and InteractableComponent, and whose name is "PICO"
   val entities = entity.scene!!.queryEntity(hasComponentsCondition, customCondition)
   
   // Query entities that have both ModelComponent and InteractableComponent, and whose name is not 'PICO'
   val entities = entity.scene!!.queryEntity(hasComponentsCondition, !customCondition)
   ```


### Use entity.findEntity(name)
If you already know the name of the target entity, you can use `entity.findEntity(name)` to query the corresponding entity within the current entity hierarchy.
### Use entity.getChildren
You can use `entity.getChildren()` to obtain all child entities of the current entity, and traverse the entire entity tree using either Breadth-First Search (BFS) or Depth-First Search (DFS). During traversal, check each entity to determine whether it is the target entity.
## API reference
`Entity`, `Scene`, and `EntityQueryCondition` classes contain interfaces for managing entities. For more information, refer to [API Reference](https://developer.picoxr.com/spatial-api/index.html).
