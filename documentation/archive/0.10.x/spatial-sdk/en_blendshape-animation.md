This article describes how to control BlendShape weights in the PICO Spatial SDK to achieve BlendShape animation.
## What is BlendShape
BlendShape (also known as Morph Target) is a vertex-level geometric deformation technique. This technique requires all shape variants to maintain topological consistency, meaning the number of vertices, index order, and connections must be exactly the same. BlendShape achieves geometric deformation by performing linear interpolation between the base mesh and one or more target shapes. By combining multiple weight parameters, you can create complex composite expressions. For example, you can use "neutral expression" as the base shape and set "smile", "blink", and "open mouth" as target shapes.
## The difference between BlendShape and skeletal animation

* **Skeletal animation**: Drives the skinned mesh through bone transformations, suitable for large-scale movements such as walking, running, waving, and more.
* **BlendShape**: Directly modifies vertex position, bypassing bone and skinning calculations; suitable for local, fine deformations (expressions, muscles), and corrections at joints.

Skeletal animation and BlendShape are often used together in real-world projects: skeletal animation drives main actions, while BlendShape enriches expressions and corrects local details.
## Controlling BlendShape weights
In the PICO Spatial SDK, you can use the `BlendShapeControllerComponent` to read and set the BlendShape weights of the model mesh, and build detailed animations (expressions, muscles, local corrections, and more). The `BlendShapeControllerComponent` provides independent control by index or name, and supports grouping multiple BlendShapes into subsets for batch operations, making state management and reuse easier.
### Step 1: Create BlendShape using DCC tools
You can create and adjust BlendShape in digital content creation (DCC) tools such as Maya and Blender, and preview and verify its visual effects directly in the tool by changing the weights.
Ensure that the resources exported from the DCC tool retain all BlendShape channels, so that BlendShapes in the resource can be accessed in the PICO Spatial SDK using the same names as in the DCC tool.

### Step 2: Check whether the model contains BlendShape data
To use the `BlendShapeControllerComponent`, you must ensure that the model resource (`MeshResource`) referenced by the target entity already contains BlendShape data. If the model resource does not contain BlendShape data, the `BlendShapeControllerComponent` will not take effect.
You can use the functions provided by the `MeshResource` class to check whether the model supports BlendShape.
```Kotlin
// Assume you already have an entity with a loaded model
val modelEntity: Entity = ...

// 1. Obtain ModelComponent from the entity
val modelComponent = modelEntity.components[ModelComponent::class.java]
if (modelComponent == null) {
    println("No ModelComponent in entity.")
    return
}

// 2. Obtain MeshResource from ModelComponent
val meshResource = modelComponent.mesh

// 3. Obtain the list of BlendShape names from MeshResource
val blendShapeNames = meshResource.getBlendShapeNames()

if (blendShapeNames.isNullOrEmpty()) {
    println("Model does not contain BlendShape data, so you cannot use BlendShapeControllerComponent.")
} else {
    println("This model supports BlendShape, including: ${blendShapeNames.size} blendshapes, names: ${blendShapeNames.take(5)}...")
    // Next, you can add and use BlendShapeControllerComponent for the entity
}
```

### Step 3: Add the BlendShapeControllerComponent component to the entity
After confirming that the model contains BlendShape data, you can add the `BlendShapeControllerComponent` component to the entity.
```Kotlin
val controller = entity.components[BlendShapeControllerComponent::class.java]!!
```

### Step 4: Control BlendShape weights
Once the `BlendShapeControllerComponent` component is added to the entity, you can use the functions provided by the `BlendShapeControllerComponent` component to control the BlendShape weights in the entity.
You can control BlendShape weights by name or index, or group BlendShape weights by subset for batch control. The order of BlendShapes in a subset matches the order when the subset is created, making it convenient to read and set them as a group.
The weight is a floating-point number that indicates the degree to which the target shape affects the base shape. The common range is [0.0f, 1.0f]. 0.0f means the base shape is maintained, and 1.0f means the target shape is fully applied. In some cases, the weight can be set outside the [0.0f, 1.0f] range to achieve exaggerated or cartoon effects. However, to ensure consistent BlendShape performance across different devices and versions, it is recommended to keep the weight within the [0.0f, 1.0f] range.

#### **Control BlendShape weights by name or index**
Retrieve the weight of a single BlendShape.
```Kotlin
// Get the weight of a BlendShape by index
val weight = controller.getBlendShapeWeight(index)

// Get the weight of a BlendShape by name
val weight = controller.getBlendShapeWeight(name)
```

Set the weight of a single BlendShape.
```Kotlin
// Set the weight of a BlendShape by index
controller.setBlendShapeWeight(index, weight)

// Set the weight of a BlendShape by name
controller.setBlendShapeWeight(blendShapeName, weight)
```

Get the weights of all BlendShapes in batch.
```Kotlin
// Return a list containing the weights of all BlendShapes, in the same order as getBlendShapeNames()
val weights = controller.getBlendShapeWeights()
```

Set the weights of all BlendShapes in batch.
```Kotlin
// Pass in a list containing all weights. The list size must match the total number of BlendShapes in the model.
controller.setBlendShapeWeights(weights)
```

#### Control BlendShape weights by subset grouping
You can aggregate multiple BlendShapes into a named collection, making it easier to control BlendShape weights in batch. Subsets are suitable for complex facial expression control. For example, "happy" requires simultaneously driving the corners of the mouth upward and slightly squinting the eyes. Grouping these BlendShapes into a "smile subset" can simplify the control logic.
Create a subset using BlendShape index or name.
```Kotlin
// Create a subset using a list of BlendShape indices.
controller.createBlendShapeSubsetByIndices("subset_smile", listOf(0,1))

// Create a subset using a list of BlendShape names.
controller.createBlendShapeSubsetByNames("subset_smile",listOf("blendShape_mouth_up", "blendShape_eye_squint"))
```

Remove a subset by BlendShape name.
```Kotlin
controller.removeBlendShapeSubset("subset_smile")
```

Retrieve the weights of all BlendShapes in a subset.
```Kotlin
// Return a list containing the weights of all BlendShapes in the subset.
// The order of the list must match the index/name order provided when creating the subset.
controller.getBlendShapeWeights("subset_smile")
```

Set the weights of all BlendShapes in a subset.
```Kotlin
// Set the weights of all BlendShapes in the subset all at once.
// The size of the weights list must match the number of BlendShapes in the subset.
controller.setBlendShapeWeights("subset_smile", listOf(0.2f,0.3f))
```

## Other operations
### Manage BlendShape data in PICO Spatial Editor
For USD resources, you can view and modify BlendShape data in real time in the Blend Shape Info module of PICO Spatial Editor (Spatial Editor). Once the resource is packaged into the PICO Spatial SDK, your modifications in Spatial Editor will take effect.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/266f62b72e3249709ab7481b5962bec7~tplv-goo7wpa0wc-image.image" width="1845px" /></div>

## Notes
When you use the PICO Spatial SDK to dynamically modify the mesh of the `ModelComponent` (for example, replacing the character's head, switching LOD, or replacing it with another skinned mesh), the original BlendShape structure will typically change. This causes the cached mapping information in the `BlendShapeControllerComponent` component to no longer match the new mesh, resulting in BlendShape control becoming invalid.
To ensure consistency and validity between the component and the new mesh, you must follow the process below:

1. **Recreate the component:** After replacing the mesh, you must first remove the old `BlendShapeControllerComponent` component from the entity, and then add a brand new `BlendShapeControllerComponent` component to the entity.
2. **Rebuild subsets:** Since the mesh has changed, any previously created subsets will become invalid. These subsets must be rebuilt based on the new mesh before they can be used.

## Frequently asked questions
### **How to determine which BlendShapes an entity can control?**
After obtaining the entity's `MeshResource`, call `getBlendShapeNames()` to return a list of available BlendShape names. If the list is empty, it means the model does not support BlendShape.
### **What happens when read or write operations are performed on a non-existent BlendShape name or index?**
The spatial app will not crash. Write operations will return `false` to indicate failure; read operations will return `null` (for a single item) or an empty list (for a subset).
BlendShape names are case-sensitive.

### **What are subsets used for? When should subsets be used?**
Subsets are used for managing BlendShapes in groups. When a logical "state" (such as a complete expression) needs to drive multiple BlendShapes simultaneously, batch read and write operations using a unified name can simplify logic and improve readability.
## API reference
`BlendShapeControllerComponent` class provides properties and functions related to BlendShape animation. For details, refer to [API reference](https://developer.picoxr.com/spatial-api/index.html).

