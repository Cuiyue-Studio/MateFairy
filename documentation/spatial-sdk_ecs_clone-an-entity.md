By cloning, you can quickly generate copies of an entity, thereby simplifying the creation and management of content.
## CloneOptions description
`CloneOptions` class provides the following properties for controlling cloning behavior:
| **Property** | **Description** |
| --- | --- |
| recursive | Whether to clone the entire entity tree where the current entity is located: <br>  <br> * `true`: clone the entire entity tree. <br> * `false`: only clone the current entity. |
| shouldShareMaterialInstance | Whether to share material instances: <br>  <br> * `true`: The copy shares the same material instance with the original entity, and any modification to the material instance of either will affect the other. <br> * `false`: Create an independent material instance for the copy. |
## Important notes

* The copy shares the same name as the original entity but has a different ID.
* The copy and the original entity have independent lifecycles, thus do not affect each other.
* The copy will not automatically be added to the current entity tree, and its parent node is null.
* The runtime state of the original entity (for example, animation, physics state, and more) will not be copied.
* The time taken for cloning depends on the complexity of the to-be-cloned entity or entity tree. It is recommended to perform cloning at an appropriate time to avoid affecting runtime performance.

## Clone an entity without custom components
By using the `clone()` method, you can directly create a copy that has the same Entity-Component data structure with the original entity.
```Kotlin
val entity = Entity()
val cloneEntity = entity.clone()
```

## Clone an entity containing custom components
If an entity contains developer-customized components, the `clone()` method must be overridden for the customized components; otherwise, the component instances cannot be copied correctly.
```Kotlin
class CustomComponent: Component() {
     var value: Float = 0f
     // Override the clone method to implement the cloning logic of the component
     override fun clone(): Component {
         return CustomComponent().apply { value = this@CustomComponent.value }
     }
}

// Create an entity instance
val entity = Entity()

// Mount CustomComponent to this entity
entity.components.set(CustomComponent().apply { value = 3f })

// Call the clone() method to clone the entity
val cloneEntity = entity.clone()
```

## API reference
The `CloneOptions` and `Entity` classes provide properties and functions related to entity cloning. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

