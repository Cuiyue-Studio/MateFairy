Quickly control the opacity of an entity through `OpacityControllerComponent`.
## Important notes

* `OpacityControllerComponent` affects the overall opacity of its node and all child nodes.
* If both the parent node and the child node have the `OpacityControllerComponent`, the child node's final opacity is the product of the parent node's and the child node's `opacity` values. For example, if node A and node B form a parent-child hierarchy, A's `opacity = 0.6`, B's `opacity = 0.5`, then B's final rendered opacity is `0.6 × 0.5 = 0.3`.
* For an entity with the `OpacityControllerComponent` attached, if you set alpha values for the entity's SpatialView and the SpatialView's parent view, the entity's rendering opacity needs to be multiplied by those alpha values.
* `OpacityControllerComponent` affects the opacity of particles.
* `OpacityControllerComponent` does not work for `PortalMaterial`.

## Set the opacity of an entity
### 3D scene: single entity

* Directly change the entity's overall opacity by setting up `OpacityControllerComponent`:


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a38a8cc2f8e645158ad450027eb39c4a~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/68b1bd8a5dd447d192fa379e50571d66~tplv-goo7wpa0wc-image.image)


</div>
</div>


   The following code implements an interactive 3D scene interface where users can adjust and observe the opacity changes of a 3D entity in real time using a slider.
   ```Kotlin
   @Composable
   fun OpacityController3DOnly() {
       // Define the entity's initial opacity (semi-transparent)
       var entityOpacity by remember { mutableFloatStateOf(0.5f) }
       
       // Vertical layout container for placing 3D view and slider control
       Column(
           modifier = Modifier.fillMaxSize().backgroundMaterial(),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center,
       ) {
           SpatialView(
               modifier = Modifier.size(300.dp, 200.dp),
               // Trigger update callback each time the state (for example, entityOpacity) changes
               update = { content, _ ->
                   // Get the first entity in the current scene and set the OpacityControllerComponent on it
                   content.entities
                       .firstOrNull()
                       ?.components
                       ?.set(OpacityControllerComponent(entityOpacity))
               },
           ) { content, _ ->
               val entity = withContext(Dispatchers.IO) {
                   Entity.load("asset://model/pico_robot_static.usdz")
               }.also {
                   it.components.get<TransformComponent>()?.scaleBy(0.3f)
               }
               content.addEntity(entity)
           }
           Spacer(modifier = Modifier.size(20.dp))        
           // Text: Display the entity's current opacity value
           Text("Slide to change entity opacity to $entityOpacity")        
           // Slider: Used for real-time modification of the entity's opacity
           Slider(value = entityOpacity, onValueChange = { entityOpacity = it })
       }
   }
   ```

* Control the opacity of the entity through the opacity of the view:


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5c69926b74324180a74d5d924d9706ca~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dc271c11a18d4e528e21718994d80119~tplv-goo7wpa0wc-image.image)


</div>
</div>


   The following code implements an interactive example where users can adjust the view's opacity in real time via a slider, thereby affecting the overall opacity of the entire 3D SpatialModelView.
   ```Kotlin
   @Composable
   fun EntityOpacityAffectedByViewAlpha() {
       // Use Compose state to save the view's current opacity (0.5); this value will affect the overall opacity of SpatialModelView in real time
       var alpha by remember { mutableFloatStateOf(0.5f) }
       
       // vertical layout container: responsible for organizing 3D view and the opacity adjustment control
       Column(
           modifier = Modifier.fillMaxSize().backgroundMaterial().depth(400.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center,
       ) {
           SpatialModelView(
               // Size and opacity of view
               modifier = Modifier.size(400.dp, 400.dp).alpha(alpha),
               resizability = Resizability.FitInside,
               source = Source.assets("model/pico_robot_static.usdz")
           )
           Spacer(modifier = Modifier.size(30.dp))        
           // Text: display the entity's current opacity value
           Text("Slide to change view alpha to $alpha", fontSize = 28.sp, color = Color.White)        
           // Slider: Used for real-time modification of entity's opacity
           Slider(value = alpha, onValueChange = { alpha = it })
       }
   }
   ```


### 3D scene: parent-child entities
You can separately control the opacity of two entities that have a parent-child hierarchy. The actual opacity of the child entity is the product of the two opacities.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3d78a49b545e402e87b43242e8cefb85~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/1b27611a006745aea33d92ad9644e3d7~tplv-goo7wpa0wc-image.image)


</div>
</div>

The following code implements an example of opacity control between a parent and a child entities, enabling users to adjust the opacity of the parent entity and the child entity separately through sliders and observe the final effect in the scene.
```Kotlin
@Composable
fun OpacityTimesInEntityTree() {
    // Define the initial opacity of the parent entity and child entity (fully opaque)
    var parentOpacity by remember { mutableFloatStateOf(1.0f) }
    var childOpacity by remember { mutableFloatStateOf(1.0f) }
    
    // Use Column layout to center the 3D view and opacity control slider
    Column(
        modifier = Modifier.fillMaxSize().backgroundMaterial(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SpatialView(
            modifier = Modifier.size(300.dp, 200.dp),
            update = { content, _ ->
                // Get the parent entity
                val parent = content.entities
                    .firstOrNull()
                // Get the child entity named "child"
                val child = parent?.getChildren()?.firstOrNull {it.getName() == "child"}
                // Set OpacityControllerComponent for the parent entity
                parent?.components
                    ?.set(OpacityControllerComponent(parentOpacity))
                // Set OpacityControllerComponent for the child entity
                child?.components
                    ?.set(OpacityControllerComponent(childOpacity))
            },
        ) { content, _ ->
            val entity = withContext(Dispatchers.IO) {
                Entity.load("asset://model/pico_robot_static.usdz")
            }.also {
                it.components.get<TransformComponent>()?.scaleBy(0.3f)
            }
            // Clone a complete entity from the parent entity and set it as a child entity of the parent entity
            entity.clone(Entity.CloneOptions(recursive = true))?.also {
                it.setName("child") 
                it.components.set(TransformComponent().apply {
                    position = Vector3(0.6f, 0f, 0f)
                })
                entity.addChild(it)
            }
            // Add the parent entity into the rendering content
            content.addEntity(entity)
        }
        Spacer(modifier = Modifier.size(20.dp))
        // Text: display the current opacity value of the parent entity
        Text("Slide to change parent opacity to $parentOpacity", fontSize = 28.sp, color = Color.White)
        // Slider: used to control the opacity of the parent entity
        Slider(value = parentOpacity, onValueChange = { parentOpacity = it })
        Spacer(modifier = Modifier.size(20.dp))
        // Text: display the current opacity value of the child entity
        Text("Slide to change child opacity to $childOpacity", fontSize = 28.sp, color = Color.White)
        // slider: used to control the opacity of the child entity
        Slider(value = childOpacity, onValueChange = { childOpacity = it })
    }
}
```

### 2D-3D mixed scene
For an entity with the `OpacityControllerComponent` attached, if alpha values are set for the entity's SpatialView and its parent view, then the entity's rendering opacity needs to be multiplied by those alpha values.
As shown in the following example, as the cyan background of the view becomes more transparent, the robot model also appears increasingly transparent—exceeding its own configured opacity.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dccb23ec02004e598f396a7a9fcbacf3~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aaff9dac01a442a2976c0af98649d872~tplv-goo7wpa0wc-image.image)


</div>
</div>

The following code implements an example of opacity control for a 2D-3D mixed scene, where users can separately adjust the opacity of the 3D model itself and the opacity of the entire view via sliders to observe the final combined effect (entity opacity × view alpha).
```Kotlin
@Composable
fun EntityOpacityTimesViewAlpha() {
    // Define the entity's initial opacity (fully opaque)
    var entityOpacity by remember { mutableFloatStateOf(1.0f) }
    // Define the view's initial opacity (fully opaque)
    var viewOpacity by remember { mutableFloatStateOf(1.0f) }
    // Layout container used for vertically arranging a view and two opacity control sliders
    Column(
        modifier = Modifier.fillMaxSize().backgroundMaterial(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SpatialView(
            // Set the view's size, opacity, and background color
            modifier = Modifier.size(300.dp, 200.dp).alpha(viewOpacity).background(Color.Cyan),
            update = { content, _ ->
                content.entities
                    // Get the first entity in the current scene
                    .firstOrNull()
                    ?.components
                    // Set OpacityControllerComponent for the entity
                    ?.set(OpacityControllerComponent(entityOpacity))
            },
        ) { content, _ ->
            val entity = withContext(Dispatchers.IO) {
                Entity.load("asset://model/pico_robot_static.usdz")
            }.also {
                it.components.get<TransformComponent>()?.scaleBy(0.3f)
            }
            content.addEntity(entity)
        }
        Spacer(modifier = Modifier.size(20.dp))
        // Text: display the entity's current opacity value
        Text("Slide to change entity opacity to $entityOpacity", fontSize = 28.sp, color = Color.White)
        // Slider: used to control the entity's opacity
        Slider(value = entityOpacity, onValueChange = { entityOpacity = it })
        Spacer(modifier = Modifier.size(20.dp))
        // Text: display the view's current opacity
        Text("Slide to change view opacity to $viewOpacity", fontSize = 28.sp, color = Color.White)
        // Slider: used to control the view's opacity
        Slider(value = viewOpacity, onValueChange = { viewOpacity = it })
    }
}
```

## Impact on particle's opacity
The particle system in the scene, as an entity, can also have its opacity controlled through `OpacityControllerComponent`.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ba8837ffe9bd4932815ea4fb6b5ef55d~tplv-goo7wpa0wc-image.image)
The following code implements an interactive example where users can adjust the opacity of the particle system entity in real time using a slider to observe the changes within the scene.
```Kotlin
@Composable
fun OpacityControllerAndParticle() {
    // Define the initial opacity of the entity (fully opaque)
    var entityOpacity by remember { mutableFloatStateOf(1.0f) }
    
    // Use Column layout to center 3D view and control slider
    Column(
        modifier = Modifier
            .fillMaxSize()
            .backgroundMaterial(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // SpatialView: used to display a 3D entity in Compose (here, the particle system)
        SpatialView(
            modifier = Modifier.size(300.dp, 300.dp),
            // `update` callback: triggered when entityOpacity changes
            update = { content, _ ->
                // Get the first entity in the current scene (that is, the particle system)
                content.entities
                    .firstOrNull()
                    ?.components
                    // Set OpacityControllerComponent for this entity
                    ?.set(OpacityControllerComponent(entityOpacity))
            },
        ) { content, _ ->
            withContext(Dispatchers.IO) {
                AssetBundle.load("asset://${Configs.BUNDLE_NAME}.bundle")
                    .loadModel("SimpleParticle")
            }.also {
                // Add entity as the rendering content of SpatialView
                content.addEntity(it)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        // Text: display the particle's current opacity value
        Text(
            "Slide to change particle opacity to $entityOpacity",
            fontSize = 28.sp,
            color = Color.White
        )
        // Slider: used to control the opacity of particles
        Slider(value = entityOpacity, onValueChange = { entityOpacity = it })
    }
}
```

## API reference
`OpacityControllerComponent` class provides properties and functions for controlling an entity's opacity. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

