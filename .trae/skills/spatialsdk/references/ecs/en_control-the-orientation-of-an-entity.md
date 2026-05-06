`LookAtComponent` enables you to control an entity's orientation, for example: making a character or UI panel always face the user.
## Set an oriented target for an entity
To make an entity orient to a specific target, simply attach `LookAtComponent` to the entity, then specify a target. By default, the entity's +Z axis always orients to the target.

* **Orient to the user (HMD)**
   By using `setViewerAsTarget()` to set the user's HMD as the target, the current entity will always orient to the HMD regardless of its movement.
   ```Kotlin
   Entity().apply { 
       components[LookAtComponent::class.java] = LookAtComponent().apply { 
           setViewerAsTarget()
       }
   }
   ```

* **Orient to another entity**
   By using `setEntityAsTarget(targetEntity)` to set another entity in the scene as the target, the current entity will always orient to the other entity regardless of its movement.
   ```Kotlin
   // Use a red sphere with a diameter of 0.04 meters as the target, and the current entity's +Z axis always orients to the red sphere
   val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
   Entity().apply { 
       components[LookAtComponent::class.java] = LookAtComponent().apply { 
           setEntityAsTarget(targetEntity)
       }
   }
   ```


## Clear the target an entity orients to
After clearing the target using `clearTarget()`, the entity's orientation will no longer change, meaning it will always maintain the initial orientation.
```Kotlin
// Use a red sphere with a diameter of 0.04 meters as the target, and the current entity's +Z axis always orients to the red sphere
val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
val lookAtEntity = Entity().apply { 
    components[LookAtComponent::class.java] = LookAtComponent().apply { 
        setEntityAsTarget(targetEntity)
    }
}

// Clear this red sphere target
lookAtEntity.components[LookAtComponent::class.java]?.apply { 
    clearTarget()  
}
```

## Set Y axis alignment
Through the `alignLocalUpToWorldUp` property, you can control whether the local Y axis of an entity aligns with the Y axis of the coordinate system of its container, that is, whether the two Y axes remain parallel.

* `true`: Align, meaning keeping two Y axes parallel.
* `false`: Not align, allowing the entity to rotate freely.

```Kotlin
// Use a red shperewith a diameter of 0.04 meters as the target, and the current entity's +Z axis always orients to the red sphere
val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
val lookAtEntity = Entity().apply { 
    components[LookAtComponent::class.java] = LookAtComponent().apply { 
        setEntityAsTarget(targetEntity)
    }
}
lookAtEntity.components[LookAtComponent::class.java]?.apply { 
    // Enable Y axis alignment
    alignLocalUpToWorldUp = true

    // Disable Y axis alignment
    alignLocalUpToWorldUp = false
}
```

## Set an entity's face to orient to the target
By default, the entity's +Z axis (`POSITIVE_Z`) always orients to the set target. You can modify which face of the entity orients to the target by using `lookAtForwardDirection`, for example, to make the entity's -Z axis face the target:
```Kotlin
// Use a red sphere with a diameter of 0.04 meters as the target, and the current entity's +Z axis always orients to the red sphere by default
val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
val lookAtEntity = Entity().apply { 
    components[LookAtComponent::class.java] = LookAtComponent().apply { 
        setEntityAsTarget(targetEntity)
    }
}
lookAtEntity.components[LookAtComponent::class.java]?.apply { 
    // Make the current entity's -Z axis orients to the red sphere
    lookAtForwardDirection = LookAtForwardDirection.NEGATIVE_Z
}
```

## Get target type
Query the current target type through `getLookAtTargetType()`.
| **Enumeration value** | **Description** |
| --- | --- |
| `LookAtTargetType.NONE` | No target (default). |
| `LookAtTargetType.ENTITY` | The target is another entity. |
| `LookAtTargetType.VIEWER` | The target is the user (HMD). |
```Kotlin
// Use a red sphere with a diameter of 0.04 meters as the target, and the current entity's +Z axis always orients to the red sphere
val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
val lookAtEntity = Entity().apply { 
    components[LookAtComponent::class.java] = LookAtComponent().apply { 
        setEntityAsTarget(targetEntity)
    }
}
lookAtEntity.components[LookAtComponent::class.java]?.apply { 
    // Get the type of the target that the current entity orients to
    val type = getLookAtTargetType()
}
```

## Get the face of the entity oriented to the target
Get the face of the target that the entity is oriented towards through `getLookAtForwardDirection`.
| **Enumeration value** | **Description** |
| --- | --- |
| `LookAtForwardDirection.POSITIVE_Z` | The entity's positive Z face is oriented toward the target (default). |
| `LookAtForwardDirection.NEGATIVE_Z` | The entity's negative Z face is oriented towards the target. |
```Kotlin
// Use a red entity with a diameter of 0.04 meters as the target, and the current entity's +Z axis always faces the red sphere
val targetEntity = remember { LookAtTargetEntity(Color.red, 0.04f) }
val lookAtEntity = Entity().apply { 
    components[LookAtComponent::class.java] = LookAtComponent().apply { 
        setEntityAsTarget(targetEntity)
    }
}
lookAtEntity.components[LookAtComponent::class.java]?.apply { 
    // Get the face of the current entity oriented toward the target red entity
    val direction = lookAtForwardDirection
}
```

## API reference
`LookAtComponent` class provides functions and properties for managing the orientation of an entity. For more information, refer to [API Reference](https://developer.picoxr.com/spatial-api/index.html).
