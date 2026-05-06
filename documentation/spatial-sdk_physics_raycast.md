You can perform collision detection on objects in the scene by casting rays or geometric shapes.
## Prerequisites
Ensure that a `CollisionComponent` has been added to the object to be detected.
## Cast a ray for collision detection
Use the `scene.rayCast()` function to detect objects that are hit along the path of the cast ray.
If the starting point of the ray is inside the collider of an object, that object will not be returned as a hit result.
Therefore, the `scene.rayCast()` function is more suitable for scenarios where detection occurs "from outside to inside" (such as from the user's perspective or a pointer targeting the surface of the scene). To detect hit results starting from inside an object, you can adjust the ray's starting point or redesign the interaction flow using `scene.convexCast()`.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7ff8acee31cc41cd9ed6c7624c1730af~tplv-goo7wpa0wc-image.image)
The following sample code uses the coordinate system of `rootEntity` as the reference frame, casting a ray from position (0, 0, 0) in the direction of (0, 0, 1) with a length of 10 meters. This query detects objects in the default collision group and returns the nearest (first) object hit.
```Kotlin
val results = scene.rayCast(
    Vector3(0f, 0f, 0f),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastHitMode.NEAREST, 
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6672ba9bdff14ba28c07408bd4d5d37e~tplv-goo7wpa0wc-image.image)
## Cast a geometric shape for collision detection
Use the `scene.convexCast()` function to detect objects that are hit along the path of the cast geometric shape.
As long as the object's collider intersects with the projected geometry, the cast will hit the object. Therefore, even if the object is located ahead of the starting point of the projected geometry, it will still be detected.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8c61df848a2a4d08a8e2dab3205c565f~tplv-goo7wpa0wc-image.image)
The following sample code uses the coordinate system of `rootEntity` as the reference frame, casting a cube with a side length of 0.1 m and default pose (unit quaternion) from position (0, 0, 0) in the direction of (0, 0, 1). The cube will move 10 meters along the given direction, query objects in the default collision group, and return the nearest (first) object hit.
```Kotlin
val shape = ShapeResource.createBox(Vector3(0.1f, 0.1f, 0.1f))
val results = scene.convexCast(
    shape,
    Vector3(0f, 0f, 0f),
    Quat.identity(),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastHitMode.NEAREST,
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ef9fb749b91a4f56b9fb6d7b6cbaa393~tplv-goo7wpa0wc-image.image)
## Process collision detection results
After object collision detection is completed, the function returns a `CollisionCastHitResults` as the detection result. `CollisionCastHitResults` contains `results: List<CollisionCastResult>`, from which you can obtain the list of hit results.
When a model plays skeletal animation or BlendShape animation, changes in the spatial position of vertices are calculated in real time by the GPU. However, ray detection and other physics operations are performed by the CPU. Therefore, ray detection cannot accurately hit meshes that are deformed on the GPU by skeletal animation or BlendShape animation.
Currently, ray intersection calculations in the physics engine are performed on the CPU and are only applicable to static meshes that have not been deformed, created using `ShapeResource.createStaticMesh(mesh: MeshResource)`. When a ray or geometry intersects a static mesh collider, the system first determines the intersected triangle. It then interpolates vertex properties such as UV0 and UV1 based on the barycentric coordinates of the intersection point within the triangle, allowing the UV coordinates corresponding to the intersection point to be returned.
Therefore, the `uv0`, `uv1`, and `materialIndex` information in the ray intersection result corresponds to the static surface position of the model in its bind pose, rather than the visual surface rendered in the current frame.

```Kotlin
val results = scene.rayCast(
    Vector3(0f, 0f, 0f),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastHitMode.NEAREST, 
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
val resultList = results.results
resultList.forEach { hit ->
    val entity = hit.entity
    val shapeIndex = hit.shapeIndex
    val position = hit.position
    val normal = hit.normal
    val distance = hit.distance
    val uv0 = hit.uv0 
    val uv1 = hit.uv1
    val materialIndex = hit.materialIndex 
}
```

`CollisionCastResult` contains the following fields:
* A model can have multiple sets of UV channels: `uv0` represents the first UV channel, which is most commonly used for sampling main maps such as base color and normal; `uv1` represents the second UV channel, often used for additional purposes such as light maps, second layer decals, masks, and so on.
* `uv0`, `uv1`, and `materialIndex` only provide valid information when the ray or geometry intersects a static mesh collider created by `ShapeResource.createStaticMesh(mesh: MeshResource)` and the model itself contains the corresponding UV channels. For other types of colliders, or when the model lacks the corresponding channels, `uv0` and `uv1` will return `Vector2.ZERO`, and `materialIndex` will return -1.

| **Field** | **Description** |
| --- | --- |
| entity: Entity | The entity intersected by the ray or geometry. |
| shapeIndex: Int | The index of the intersected collider in the `CollisionComponent.collisionShape` (`List<ShapeResource>`) of the entity. |
| position: Vector3 | The position of the intersection point. |
| normal: Vector3 | The normal at the intersection point. |
| distance: Float | The distance from the ray's origin or the geometry's projection point to the intersection point at the time of intersection. |
| uv0: Vector2 | The coordinates of the intersection point on the model's first UV channel (UV0). <br> The intersection point's coordinates are returned only when the ray or geometry intersects a static mesh collider created by `ShapeResource.createStaticMesh(mesh: MeshResource)` and the model contains UV0; otherwise, `Vector2.ZERO` is returned. |
| uv1: Vector2 | The coordinates of the intersection point on the model's second UV channel (UV1). <br> The intersection point's coordinates are returned only when the ray or geometry intersects a static mesh collider created by `ShapeResource.createStaticMesh(mesh: MeshResource)` and the model contains the second UV (UV1); otherwise, `Vector2.ZERO` is returned. |
| materialIndex: Int | The index of the submesh to which the intersected face belongs. You can use this index to retrieve the corresponding material instance from the `ModelComponent.materials` list. <br> If the collider is not a static mesh collider created by `ShapeResource.createStaticMesh(mesh: MeshResource)`, this parameter returns -1. |
## Caution

* In the ECS architecture, `referenceEntity` is the reference frame for coordinates, directions, and transformation calculations. When performing object collision detection using the PICO Spatial SDK, the input parameters such as coordinates and directions should be based on the coordinate system of `referenceEntity`; the returned results are also based on this coordinate system. When `referenceEntity` is `null`, the coordinate system of its spatial container is used as the reference frame.
* The `hitMode` parameter of `scene.rayCast()` and `scene.convexCast()` is used to set the collision detection mode. You can set it to the following two modes:
   * `CollisionCastHitMode.NEAREST`: Only returns the hit result nearest to the starting point. This mode has the lowest performance overhead and is suitable for typical interaction scenarios such as clicking, pointer selection, and more.
   * `CollisionCastHitMode.ALL`: Returns all hit results along the path of the ray or the cast geometric shape. This mode is suitable for debugging or advanced interactions that require information about multiple hit points, but you need to manually iterate and filter the results, which results in higher processing overhead.
* By properly configuring `CollisionGroup`, you can filter out colliders that do not need to participate in detection during the physics intersection phase. For example, place UI, decorations, or background geometry in separate collision groups, and when calling `scene.rayCast()` or `scene.convexCast()`, only pass in the groups that actually require interaction. This significantly reduces unnecessary detection and improves performance.

## API reference

* The `Scene` class provides the `rayCast()` and `convexCast()` functions for object collision detection.
* The `CollisionCastResult` class provides data related to object collision detection results.

For detailed instructions, see [API reference](https://developer.picoxr.com/spatial-api/index.html).

