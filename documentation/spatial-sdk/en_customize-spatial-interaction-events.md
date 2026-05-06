Not all hand poses are implemented through out-of-the-box hand pose modifiers. If you want to customize hand poses for 3D interaction, you can do so by accessing the raw pointer events and combining them as needed. There are two ways to implement this:

* **Use the** **`position3d()` function**
   For `PointerInputChange`, PICO Spatial SDK provides the following extension function:
   ```Kotlin
   fun PointerInputChange.position3d(): Offset3D
   ```

   By using this extension function, you can obtain the 3D coordinates of any pointer event:
   ```Kotlin
   @Composable
   private fun LogPointerEvents() {
       var log by remember { mutableStateOf("") }
       Column {
           Text(log)
           Box(
               Modifier
                   .size(100.dp)
                   .background(Color.Red)
                   .pointerInput(Unit) {
                       awaitPointerEventScope {
                           while (true) {
                               val event = awaitPointerEvent()
                               // Handle pointer events
                               if (event.type == PointerEventType.Press) {
                                   log = "${event.type}, ${event.changes.first().position3d()}"
                               }
                           }
                       }
                   }
           )
       }
   }
   ```

* **Use the** **`detectSpatialPointerEvent()` function**
   Use the `detectSpatialPointerEvent` function to extract `SpatialPointer Info`. `SpatialPointerInfo` contains the 3D coordinates and content of the interaction event. The following code demonstrates how to scale the `targetEntity` in `SpatialPointerInfo` after a press.
   ```Kotlin
   @Composable
   fun SpatialPointerEventDemo() {
       val context = LocalContext.current
       SpatialView(
           modifier =
               Modifier.fillMaxSize().pointerInput(Unit) {
                   detectSpatialPointerEvent(context = context) { eventList ->
                       eventList.forEachIndexed { index, spatialPointerInfo ->
                           spatialPointerInfo.targetedEntity?.components.get<TransformComponent>?.apply {
                               scaleBy(if (spatialPointerInfo.pressed) 1.4f else 1f)
                           }
                       }
                       false
                   }
               }
       ) { content, _ ->
           val entity = Entity()
           entity.set(
               ModelComponent(
                   mesh = MeshResource.createSphere(radius = 0.3f),
                   material = BasicMaterial.create().apply {
                       setBaseColor(
                           Color4.GREEN
                       )
           })
   )
           // Entity is not interactive by default. To make the entity interactive, you need to add both InteractableComponent and CollisionComponent to it
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


