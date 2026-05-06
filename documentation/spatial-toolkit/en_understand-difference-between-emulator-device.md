The PICO Emulator can simulate the PICO OS 6 runtime environment on a PC, but there are still differences in experience and functionality compared to the actual PICO device. This article provides a detailed introduction to the differences between the PICO Emulator and the actual PICO device.
PICO Emulator currently does not support some new features of the actual PICO device, but this does not affect the development and testing process. Therefore, this document will not elaborate further.

## **Differences in interaction capabilities**
### Hand pose tracking
Hand pose tracking relies on a ToF (Time of Flight) camera, but PCs do not support ToF cameras, so the PICO Emulator cannot obtain hand-related data. The hand pose tracking-related features of the PICO SDK also do not work in the PICO Emulator.
The PICO Emulator plans to add hand pose interaction simulation based on keyboard and mouse in the future.
### Movement along the Z axis in eye-hand interaction mode
In eye-hand interaction mode, the hand model does not support movement along the Z axis; you can only achieve movement along the Z axis by moving the camera position.
The PICO Emulator plans to add keyboard-simulated hand pose interaction in the future.
## **Differences in rendering and display**
### The PICO Emulator renders only the right eye view
The actual PICO device renders a binocular view, but currently the PICO Emulator renders only the right eye view.
The PICO Emulator will add the left eye view in the future, allowing you to switch between the left eye and right eye views as needed.
### Emulator clarity
The PICO Emulator's display resolution is 2K, which is lower than the resolution of the actual PICO device.
### FOV
The FOV of the PICO Emulator is smaller than that of the actual PICO device, so the visible range is also smaller than that of the actual device.
### Visual effects
The visual effects of the PICO Emulator differ from those of the actual PICO device. For example, you may notice that the size or distance of the PICO Emulator panel feels different from the actual device. This is mainly caused by factors such as visual errors, observation distance, and hardware differences.
### Foveated Rendering
Foveated Rendering is a rendering optimization technology that combines Eye Tracking, maintaining high-definition rendering in the user's gaze focus area and reducing precision in peripheral areas to save performance. This feature is not available in the PICO Emulator.
## **Differences in SDK feature support**
Due to hardware and runtime environment limitations, the PICO Emulator does not currently support some features of the PICO SDK. The following sections introduce the differences in feature support for the PICO Spatial SDK, PICO XR SDK, and WebXR/WebSpatial.
### PICO Spatial SDK
The PICO Emulator does not support the following features of the PICO Spatial SDK:

* Tracking:
   * Hand Tracking (hand pose tracking). For details, see [Hand Tracking](/document/spatial-sdk/hand-tracking/).
   * Body Tracking. For details, see [Body Tracking](/document/spatial-sdk/body-tracking/).
   * Object Tracking. For details, see [Object Tracking](/document/spatial-sdk/object-tracking/).
* Spatial ML. For details, see [SpatialML overview](/document/spatial-sdk/spatialml-overview/).
* 8K and HEVC format videos. For details, see [Video files](/document/spatial-sdk/video-file).
* Enable high-definition UI rendering. For details, see [Enable high-definition UI rendering](/document/spatial-sdk/enable-high-resolution-ui-rendering/).

### PICO XR SDK
PICO Emulator does not support the following features of PICO XR SDK (including Unity SDK, Unreal SDK, and OpenXR SDK):

* Rendering
   * Static Foveated Rendering. For details, see the documentation for each SDK.
   * Eye Tracked Foveated Rendering (ETFR). For details, see the documentation for each SDK.
   * Set Display Refresh Rate. For details, see the documentation for each SDK.
   * Late Latching. For details, see the documentation for each SDK.
   * Optimize Buffer Discards. For details, see the documentation for each SDK.
   * Render Viewport Scaling. For details, see the documentation for each SDK.
   * Adaptive Resolution. For details, see the documentation for each SDK.
   * Super Resolution. For details, see the documentation for each SDK.
   * Sharpening. For details, see the documentation for each SDK.
* Interaction
   * Haptic Feedback. For details, see the documentation for each SDK.
   * Face Tracking. For details, see the documentation for each SDK.
   * Hand pose tracking. For details, see the documentation for each SDK.
   * Body Tracking. For details, see the documentation for each SDK.
   * Body tracking sensors. For details, see the documentation for each SDK.
* Mixed reality
   * Video Seethrough (VST). For details, see the documentation for each SDK.
   * SecureMR. For details, see the documentation for each SDK.
   * Mixed Reality Capture (MRC). For details, see the documentation for each SDK.

### PICO WebXR/WebSpatial
PICO Emulator supports all features of WebXR/WebSpatial.

