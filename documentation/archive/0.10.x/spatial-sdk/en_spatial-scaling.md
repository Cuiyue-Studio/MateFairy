Spatial scalinis used to scale the SpatialModelView and SpatialView within Stage and WindowContainer, and the scaling is performed in three dimensions. The 2D and 3D content contained in the SpatialModelView and SpatialView will also be scaled synchronously and proportionally.
## Important notes

* Spatial scaling only affects the final rendering effect of an object and does not affect the actual space occupied by the object.
* In PICO spatial apps, 2D objects carry depth information. By controlling the scaling factor of a 2D object along the Z-axis, you can control how near or far the content is in space.

## Related APIs
You can implement spatial scaling through the `scale3d()` API:
```Kotlin
// API 1: Scale the content. You can specify separately the scaling factors on the X, Y, and Z axis
Modifier.scale3D(x:Float, y:Float, z:Float, anchor:NormalizedPoint3D)

// API 2: Use a unified scaling factor to scale content
Modifier.scale3D(scale:Float , anchor:NormalizedPoint3D)
```

## About the anchor
The `anchor` parameter is used to set the anchor for this scaling. An anchor is a 3D coordinate. When scaling content, the position of the anchor is fixed, and the rest of the content scales relative to the anchor according to the set scaling factor. If the `anchor` parameter is not set, the central point of the current view is used as the anchor by default, corresponding to normalized coordinates (0.5, 0.5, 0.5). If the anchor is located outside the scaled object, then when scaling the object, the distance between each point on the object and the anchor will also be multiplied by the scaling factor.
Expected effects:

* **Anchor inside the object (including edges and vertices)**


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

   Original object:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/571ad88e1c9444329a26c0042006615c~tplv-goo7wpa0wc-image.image)




</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

   The anchor is at the center of the object, and the scaling factor is 0.5:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/979e1102ae3e43069975e1056ff55f28~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

   The anchor is located at the top-left corner of the object, and the scaling factor is 0.5:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/19c313f5a56c487eb6cf3b89eaf62e1a~tplv-goo7wpa0wc-image.image)


</div>
</div>


* **Anchor outside the object**


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);">

   Original object:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/571ad88e1c9444329a26c0042006615c~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

   The anchor is outside the object, and the scaling is factor 0.5:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0848efdf7ea4475dbbed51787c3e1dc6~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 32px) * 0.3333);margin-left: 16px;">

   The anchor is outside the object, and the scaling factor is 0.5:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/45812f79b77a4acab6fbf2b8eeb91d6c~tplv-goo7wpa0wc-image.image)


</div>
</div>

## Code samples
**Code sample 1**: Specify the scaling factors of SpatialView on the X, Y, and Z axes respectively:
```Kotlin
SpatialView(
    modifier = Modifier
        .scale3D(
            x = 0.5f,
            y = 0.5f,
            z = 0.5f, 
            anchor = NormalizedPoint3D.Center),
    initial = { content, attachments ->
       ....
    },
    attachments = {
       ....
    }
) 
```

**Code sample 2**: Use a uniform scaling factor to scale SpatialView:
```Kotlin
SpatialView(
    modifier = Modifier.scale3D(scale = 0.5f, pivot = NormalizedPoint3D.Center),
    initial = { content, attachments ->
       ....
    },
    attachments = {
       ....
    }
)
```

**Code sample 3**: Define the `Scale3D` parameter through Lambda, specifying the scaling factors of SpatialView on the X, Y, and Z axes respectively:
```Kotlin
SpatialView(
    modifier =
        Modifier.scale3D {
            Scale3D(
                scaleX = 0.5f,
                scaleY = 0.5f,
                scaleZ = 0.5f,
                pivot = NormalizedPoint3D.Center,
            )
        },
    initial = { content, attachments ->
       .....
    },
    attachments = {
       .....
    },
)
```



