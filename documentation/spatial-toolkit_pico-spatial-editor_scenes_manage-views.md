This article describes how to adjust the view in the viewport, including common operations such as focusing, panning, rotating, and camera navigation.
The maximum range of the mesh in the scene is 2000m x 2000m. The mesh uses 0.1m x 0.1m as the base unit and dynamically scales at a ratio of up to 10x depending on the camera height.
## Focus on the current scene
You can focus the view on the current scene using any of the following methods:

* Press the shortcut Ctrl + F (Windows) or Cmd + F (macOS).
* Select the Root node of the scene in the **Hierarchy** window, then press the F key.

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3ab265cdd94f4ae381ad8e93744f3434~tplv-goo7wpa0wc-image.image)
## Pan the view
Hold down the middle mouse button and drag to pan the view.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bdcfbeb9711a40ab979d578dc32bb2e7~tplv-goo7wpa0wc-image.image)
## Rotate the view around the focus point
Hold down the Option (macOS) or Alt (Windows) key and drag the left mouse button to rotate the view around the focus point. This operation is commonly used to observe model assets from different angles.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9b83150d24674e0d98daa3a99af531fc~tplv-goo7wpa0wc-image.image)
## Rotate the current view
Hold down the right mouse button and move to rotate the current view. This operation simulates a first-person perspective, making it easier for you to look around the entire scene.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/d0e62739600d43148bca9d1875bd1704~tplv-goo7wpa0wc-image.image)
## Zoom the view
Scroll the middle mouse button to zoom the view. Alternatively, you can hold down the Option (macOS) or Alt (Windows) key and drag the right mouse button.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/9da1cc6364954b3d8484bdb1cfcfffde~tplv-goo7wpa0wc-image.image)
## Move the camera
Hold down the right mouse button and use the following keys to move the camera in the scene:

* W: Forward
* S: Backward
* A: Left
* D: Right
* E: Up
* Q: Down

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3a441354f38e402bab271ba12960ad19~tplv-goo7wpa0wc-image.image)
## Adjust the camera movement speed
Hold down the right mouse button and scroll the middle mouse button to adjust the camera movement speed.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/41d04cb09ec644499c8ba03ff2653231~tplv-goo7wpa0wc-image.image)
## Quickly switch views
You can click the view switcher at the top right of the viewport to quickly switch views. The views represented by each color are as follows:

* Blue: Front view (the opposite is the rear view)
* Green: Top view (the opposite is the bottom view)
* Red: Right view (the opposite is the left view)

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ae3f41825cd54731933692acde291fd9~tplv-goo7wpa0wc-image.image)
## Select the scene's coordinate space and projection mode
At the top right of the viewport, you can select the scene's coordinate space and projection mode.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/035bc28449ed4aeaa7b7dcce02c96c65~tplv-goo7wpa0wc-image.image)
The available coordinate spaces are as follows:

* Local: Local coordinate space
* Global: Global coordinate space (default)

The available projection modes are as follows:

* Persp: Perspective
* Ortho: Orthographic (default)

## Replace the scene background
Click the **Environment Setting** button in the upper right corner of the view window. In the pop-up **Environment Preview** window, you can replace the default solid color background of the view window with different environments to preview how the model appears under various lighting conditions. Different environments provide different lighting. These backgrounds are used only for previewing in the Spatial Editor.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2280c6189c844435815d9926f954b2fb~tplv-goo7wpa0wc-image.image)
The **Environment Preview** window includes the following parameters:
| **Parameter** | **Description** |
| --- | --- |
| Background Color | Set the background color of the default background; this does not affect environmental lighting. |
| Custom Environment | When enabled, you can use custom images and built-in images for environment settings. |
| Rotate | Rotate the background environment along the Y axis to observe the model's effect in different environments. |
| Exposure | The exposure intensity of environmental lighting, used to control the brightness of environmental lighting. |
## Select debug view
Click the **Debug View** button in the upper right corner of the view window to select a debug view from the dropdown menu. A debug view is a special visual mode that helps you better understand the various elements in the scene and their states. These views are used only for previewing in the Spatial Editor.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/37f26e08db2a4e808ff13dfd46f4d278~tplv-goo7wpa0wc-image.image)


</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5000);margin-left: 16px;">

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/315b853a44034060be8d5fac4e53b3b3~tplv-goo7wpa0wc-image.image)


</div>
</div>

The **Debug View** dropdown menu includes the following options:

* **Shaded**: Displays standard shading and lighting effects.
* **Wireframe**: Displays the model in wireframe mode.
* **Shaded Wireframe**: Displays both the model's shading effects and wireframe.
* **Base Color**: Displays only the model's base color map in no-lighting mode.
* **Lighting**: Displays only the scene's lighting and normal information, excluding other visual effects.
* **Opacity Alpha**: Displays the alpha channel information of translucent materials.
* **World Normal**: Displays the world space normals and normal map of the object's surface.
* **UV Density**: Visualizes the density of the model's UV map using different colors.
* **VertexUV**: Displays the UV coordinate values of the model's vertices.
* **Derived Metallic**: Displays the material's metallic information, combining material properties and maps.
* **Derived Specular**: Displays the material's specular information, combining material properties and maps.
* **Derived Roughness**: Displays the material's roughness information, combining material properties and maps.
* **Derived Ambient Occlusion**: Displays the material's ambient occlusion (AO) information, combining material properties and maps.
* **Derived Emissive**: Displays the material's emissive information, combining material properties and maps.
* **Derived Clearcoat**: Displays the material's clearcoat layer information.
* **Derived Clearcoat Roughness**: Displays the roughness information of the material's clearcoat layer.

## Show or hide gizmo
Click the **Gizmo Setting** button in the upper right corner of the view window to choose to hide or show the gizmo from the dropdown menu.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/290612f9b97f4016a9a64b8247073eb1~tplv-goo7wpa0wc-image.image)
The **Gizmo Setting** dropdown menu includes the following options:

* **Grid**: Ground mesh.
* **Colliders**: Collision bodies.
* **Particles**: Particle emitters.
* **Audio Sources**: Audio sources.
* **Lighting**: Lights.
* **Bounding Box**: Bounding boxes for entities.

## Manage camera presets
Click the  **Camera Presets** button in the upper right corner of the view window to select options in the dropdown menu for saving and managing camera presets. The camera presets feature helps you manage camera positions and quickly switch to previously saved camera positions.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b92fb7e5ebbf44d998fbcf87ee0193e8~tplv-goo7wpa0wc-image.image)
## Reset the camera
Click the **Reset Camera** button in the upper right corner of the view window to reset the camera's position to the scene origin (0, 0, 0).
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2159dfb47f5c499cad65fd83c9eb4a82~tplv-goo7wpa0wc-image.image)

