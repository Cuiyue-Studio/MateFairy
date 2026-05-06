This article introduces the animation components in PICO Spatial Editor (Spatial Editor).
The animation components can be added to or removed from an entity. In .usda files, the type of the animation component is `SpatialComponent`. This is a component type defined by Spatial Editor and is not a native USD type.
## Animation Resource Library
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dddd42af48024f569b3dbb8e5888d1ed~tplv-goo7wpa0wc-image.image)
This component provides animation to the **Play Animation** animation template in the Timeline. Entities bound with animation automatically include this component. You cannot manually add or remove this component. For details, see [Supported actions in Timelines](/timeline-built-in-animation-model).
Currently, only skeletal animation is supported in the Animation Resource Library component.

You can manage an entity's animation through this component:

* Click the plus button below to add animation to the entity.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/99bf113618f141a4b9edc89318779808~tplv-goo7wpa0wc-image.image)
* Click the plus button to the right of the animation to create a copy of the animation and trim it by adjusting the start and end times. Right-click the copy to select **Rename** or **Delete** from the dropdown menu.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/173983c585f140f5a5f74ded71cc77e1~tplv-goo7wpa0wc-image.image)




