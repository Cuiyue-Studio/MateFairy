Known issues with PICO Spatial SDK:

* When overlaying and displaying semi-transparent 3D content and 2D panels within `WindowContainer`, the display may be abnormal.
* When using both `Modifier.spatialHoverEffect` and `Modifier.clickable` in `LazyColumn` or `LazyVerticalGrid`, you must apply `clickable` first to ensure that click events are triggered correctly; the hover effect on the first item may not work as expected.
* When applying the `Rotate3D` effect of SpatialUI to `SpatialView` or `SpatialModelView`, the anchor point may become offset.
* When multiple 2.5D view modifiers are used together in SpatialUI, some effects may be abnormal.
* The frosted glass effect of the side Tab Bar in SpatialUI may not display correctly.
* When playing spatial videos recorded on an iPhone (which corresponds to videos in MV-HEVC video), the data for the left and right eyes is swapped. This issue is expected to be resolved in the next release.

 
