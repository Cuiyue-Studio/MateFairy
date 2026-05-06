PICO always attaches great importance to protecting user privacy. Because the algorithms deployed in SpatialML may use binocular cameras, depth cameras, or other spatial data as input, a spatial app must first obtain authorization from the user for camera and spatial data before it can read the algorithm output from the SpatialML framework.
### Camera and spatial data permissions
When your app uses SpatialML, the framework analyzes the data sources required by the algorithm and checks whether your app has obtained the necessary authorization. The specific rules are as follows:

* **No special permissions required**: If your app does not use any data that requires camera or spatial data permissions, SpatialML will directly allow it to read the algorithm output.
* **Camera permissions required**: If your app uses a binocular camera as input, SpatialML will check whether the app has obtained camera permission (`android.permission.CAMERA`). If authorization has not been obtained, SpatialML will deny the app's request to read the algorithm output.
* **Spatial data permissions required**: Similarly, if your app uses a depth camera or spatial positioning data, SpatialML will check whether the app has obtained spatial data permission (`com.picovr.permission.SPATIAL_DATA`). If authorization has not been obtained, the request will also be denied.

### Direct rendering in the SpatialML spatial container
To balance MR creative implementation and user privacy protection, SpatialML provides the spatial container feature. You do not need to read the algorithm output from SpatialML externally; instead, you can directly use SpatialML's rendering API to render and update MR scenes inside the spatial container. In this case, you do not need to obtain special permissions.
To ensure user privacy, the SpatialML spatial container is completely isolated from the app's own spatial container, and the scenes in both cannot interact or affect each other. In addition, if your app's space state is Full Space, the SpatialML spatial container will be hidden and cannot be displayed simultaneously with the app's Stage container.
