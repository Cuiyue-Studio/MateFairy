Cast rays or convex shapes to detect the object hit by them. The to-be-detected targets are the collider on `CollisionComponent`.
## Prerequisites
Ensure that a `CollisionComponent` has been added to the object to be detected.
## Important notes
In ECS architecture, `referenceEntity` is the reference frame for coordinate, direction, and transformation calculations.
When performing object hit detection using the PICO Spatial SDK, the coordinates, directions, and other parameters passed in should be based on the coordinate system of the `referenceEntity`; the returned results are also based on this coordinate system. If `referenceEntity` is `null`, the coordinate system of its spatial container is used as the reference.
## Detect objects by casting rays
Use the `scene.rayCast()` function to cast a ray and detect objects that it hits along the path.
If the starting point of the ray is inside the collider of an object, that object will not be returned as a hit result.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8a7ce2e21030446492cfae0ecf62a3b4~tplv-goo7wpa0wc-image.image)
The following code sample uses the coordinate system of `rootEntity` as the reference, casting a 10-meter ray from position (0, 0, 0) in the direction of (0, 0, 1). This query detects objects in the default collision group and returns the nearest (first) hit object.
```Kotlin
val results = scene.rayCast(
    Vector3(0f, 0f, 0f),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastQueryType.NEAREST,
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6672ba9bdff14ba28c07408bd4d5d37e~tplv-goo7wpa0wc-image.image)
## Detect object by casting convex shapes
Use the `scene.convexCast()` function to cast a convex shape and detect objects that it hits along the path.
As long as there is an intersection between the collider of the object and the convex shape, the object is hit by the convex shape. Therefore, even if the object is placed before the starting point of the convex shape, it will still be detected in this cast.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/67be97b504a243f8a6b5a5236a3fc2f3~tplv-goo7wpa0wc-image.image)
The following code sample uses the coordinate system of `rootEntity` as the reference, casting a cube with a side length of 0.1 m and default pose (unit quaternion) from position (0, 0, 0) in the direction of (0, 0, 1). This cube will move 10 meters along the given direction, query objects in the default collision group, and return the nearest (first) hit object.
```Kotlin
val shape = ShapeResource.createBox(Vector3(0.1f, 0.1f, 0.1f))
val results = scene.convexCast(
    shape,
    Vector3(0f, 0f, 0f),
    Quat.identity(),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastQueryType.NEAREST,
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ef9fb749b91a4f56b9fb6d7b6cbaa393~tplv-goo7wpa0wc-image.image)
## Handle detection results
After object hit detection is complete, the function returns a `CollisionCastHitResults` as the detection result. `CollisionCastHitResults` contains `results: List<CollisionCastResult>`, from which you can retrieve the list of hit results.
```Kotlin
val results = scene.rayCast(
    Vector3(0f, 0f, 0f),
    Vector3(0f, 0f, 1f),
    10f,
    CollisionCastQueryType.NEAREST,
    CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
    rootEntity,
)
val resultList = results.results
resultList.forEach {
    it.entity
    it.shapeIndex
    it.position
    it.normal
    it.distance
}
```

`CollisionCastResult` contains the following fields:
| **Field** | **Description** |
| --- | --- |
| entity: Entity | The entity that the ray or convex shape hits. |
| shapeIndex: Int | The index of the hit entity in the `CollisionComponent`. |
| position: Vector3 | The position of the collision point. |
| normal: Vector3 | The normal at the collision point. |
| distance: Float | The distance from the starting point of the ray or convex shape to the collision point at the time of collision. |
## API reference

* The `Scene` class provides the `rayCast` and `convexCast` functions for object hit detection.
* The `CollisionCastResult` class provides data on object hit detection results.

For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).
