This article describes how to manage components in Spatial Editor.
## What are components
A component is a set of data in a specific format that grants an entity particular functions and properties.

* In Spatial Editor, different types of entities have different built-in components. Additionally, you can add other built-in components or custom components to entities to meet specific requirements and further extend the entity's functionality.
* In PICO Spatial SDK, `Component` refers to data in a specific format associated with an `Entity`. Built-in `Component` types include 3D model components, spatial transform components, rendering components, physics components, animation components, and more. In addition, you can create custom `Components` to store data for specific functions, thereby extending system capabilities.

## Add components
After selecting an entity, click the **Add Component** button at the bottom of the **Inspector** window to view all available components.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d8cad5087cef4170a62a4d55dbdf106d~tplv-goo7wpa0wc-image.image)
## Delete components
After selecting an entity, locate the component to delete in the **Inspector** window, click the **···** on its right, and then select **Delete** to remove the component.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7d171a094c3f4ad5ab1e4b2de18d014e~tplv-goo7wpa0wc-image.image)
## Copy component parameters
After selecting an entity, locate the component to copy in the **Inspector** window, click the **···** on its right, and then select **Copy Component** to copy the component's parameter configuration.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e3f10fc19e9d4efa8358b728e5682f78~tplv-goo7wpa0wc-image.image)
## Paste component parameters
After selecting an entity, locate the component in the **Inspector** window to paste into, click the **···** on its right, and then select **Paste Component** to paste the previously copied component parameters into this component, overwriting its parameters.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4b1e992c6f474adcb7c6f046a0f03ef2~tplv-goo7wpa0wc-image.image)
## Undo component parameter changes
After selecting an entity, locate the component to revert in the **Inspector** window, click the **···** on its right, and then select **Remove Overrides** to undo all changes made to the component's parameters, restoring them to their default or inherited values.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f0f190032e224246a08a56752c2a1363~tplv-goo7wpa0wc-image.image)
## Other operations
Click the **···** in the upper right corner of the **Inspector** window to expand a dropdown menu. The description for each option is as follows:
| **Option** | **Description** |
| --- | --- |
| **Copy Object Path** | Copy the entity's relative path in the scene. |
| **Copy Components** | Copy all components of the entity. |
| **Paste Components** | Paste all components of the entity. |
| **Deactivate/Activate** | Disable or enable the entity. After disabling, the entity will be removed from the scene, but you can re-enable it at any time to restore it to the scene. |
| **Disable/Enable** | Hide or show the entity. After hiding, the entity will not be rendered and will not interact with the physics system. |
| **Lock/Unlock** | Lock or unlock the entity. After locking, the entity will not accept any modifications. |
| **Remove Overrides** | Remove all modifications to component parameters. |
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3bc3cf2f33bb49f1bd4fd1daa374abfe~tplv-goo7wpa0wc-image.image)

