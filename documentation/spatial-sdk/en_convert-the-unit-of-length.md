Spatial UI supports using physics units (meters, centimeters) to set the position and size of elements and components, and provides the ability to convert between physical units and dp (density-independent pixels).
* The conversion result may be affected by the `worldScale` of WindowContainer.
* Currently, you cannot directly set the size of WindowContainer using physical dimensions.

Related functions are as follows:
| **Function** | **Description** |
| --- | --- |
| dpToLength() | Convert dp to a physics unit. |
| lengthToDp() | Convert a physics unit to dp. |
The code sample is as follows:
```C#
@Composable
fun PhysicalLengthConverterSample() {
    // Convert dp to a meter
    val meter = LocalPhysicalLengthConverter.current.dpToLength(dp = 100.dp, LengthUnit.METERS)
    // Convert meter to dp
    val dpLens = LocalPhysicalLengthConverter.current.lengthToDp(length = 1.2f, LengthUnit.METERS)
    // Use meter to set the size of WindowContainer
    Box(modifier = Modifier.size(1.meters).background(color = Color.Red)) {}
}
```
