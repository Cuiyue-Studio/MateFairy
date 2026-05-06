You can use the SpatialPopup component to add pop-ups with spatial floating effects (that is, spatial pop-ups) to your app, and customize the content and layout within the pop-up.
## API Surface
The configurable parameters of the SpatialPopup component are as follows:
| **Parameter** | **Type** | **Description** |
| --- | --- | --- |
| onDismissRequest | () -> Unit | Key callback, triggered when the user clicks outside the popup or presses the system back key. You must update the state in this callback to close the popup; otherwise, the popup may not close properly. |
| modifier | Modifier | Modifier applied to the popup container. Can be used to set background, border, shadow (such as `Modifier.shadow`), constrain the maximum size, and more. |
| popupPositionProvider | PopupPositionProvider | Advanced positioning, used to provide the screen coordinates of the popup. Using `rememberSpatialPopupPositionProvider()` enables precise positioning of the popup relative to an anchor (such as a button), which is fundamental for implementing dropdown menus and tooltips. |
| cornerRadius | CornerRadius | The corner radius of the popup. Passing a `Dp` value directly (such as `8.dp`) sets all four corners uniformly. The default value is `0.dp`. |
| defaultMinWidth | Dp | The default minimum width of the popup. If set to `Dp.Unspecified`, the width of the popup is determined by its content (that is, "to wrap the content"). The default value is `160.dp`. |
| defaultMinHeight | Dp | The default minimum height of the popup. If set to `Dp.Unspecified`, the height of the popup is determined by its content. The default value is `0.dp`. |
| properties | PopupProperties | A set of behavior properties for fine-grained control of popup behavior, for example: <br>  <br> * `focusable`: Whether the popup can receive focus. <br> * `dismissOnClickOutside`: Whether the popup will close when clicking outside its area. <br> * `excludeFromSystemGesture`: Whether to exclude system hand pose. |
| content | @Composable () -> Unit | The content inside the popup. You can place any Compose UI that needs to be displayed inside the popup. |
## Limitation
Customizing the floating height of the pop-up in space is not supported.
## Basic usage
The following code demonstrates how to control the visibility of a `SpatialPopup` via state, using a `Button` as the trigger. When the user clicks the button, a spatial pop-up that can be closed appears in space.
```Kotlin
@Composable
fun SpatialPopupSample() {
    // Declare the display state of the spatial popup
    var showPopup by remember { mutableStateOf(false) }
    Box {
        // Declare a spatial popup
        if (showPopup) {
            SpatialPopup(
                onDismissRequest = {
                    // Close the popup
                    showPopup = false
                }
            ) {
                // Content inside the popup
                Text(text = "SpatialPopup", modifier = Modifier.align(Alignment.Center))
            }
        }
        // Use Button as the trigger for the popup
        Button(
            onClick = {
                // Show the popup
                showPopup = true
            }
        ) {
            Text("show popup")
        }
    }
}
```

## Advanced usage
## About the anchor view
SpatialPopup is displayed based on the spatial position of a specific view. This view acts as the anchor view, which is the parent view that SpatialPopup relies on for layout and spatial positioning.
For example, in the code below, `Column` is the anchor view for `SpatialPopup`
```Kotlin
Row {
    Column {
        SpatialPopup()
    }
}
```


* **The impact of the anchor view's padding on popup alignment**
   The `padding` on the anchor view directly affects the alignment logic of SpatialPopup. The alignment area of SpatialPopup is not based on the complete visible area of the anchor view, but on the actual content area after subtracting `padding`.
   In the following example, the overall size of `Box` is 100 dp (red area), but because `padding(20.dp)` is set, the actual area serving as the anchor for SpatialPopup is the yellow part of 60 dp with the red area subtracted.


<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.6607981220657277);">

   ```Kotlin
   Box(modifier = Modifier
       .size(100.dp)
       .background(Color.Red)
       .padding(20.dp)
       .background(Color.Yellow)
   ) {
       SpatialPopup()
   }
   ```



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.3392018779342723);margin-left: 16px;">


<img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSIzNjVweCIgaGVpZ2h0PSIzNTVweCIgdmlld0JveD0iLTAuNSAtMC41IDM2NSAzNTUiPjxkZWZzLz48Zz48cmVjdCB4PSIyIiB5PSIyIiB3aWR0aD0iMjgwIiBoZWlnaHQ9IjI4MCIgZmlsbD0iI2ZmMzMzMyIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iNTIiIHk9IjUyIiB3aWR0aD0iMTgwIiBoZWlnaHQ9IjE4MCIgZmlsbD0iI2ZmZmYwMCIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iNTIiIHk9IjIzMiIgd2lkdGg9IjMxMCIgaGVpZ2h0PSIxMjAiIGZpbGw9IiNjY2U1ZmYiIHN0cm9rZT0ibm9uZSIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDMwOHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDI5MnB4OyBtYXJnaW4tbGVmdDogNTNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+UG9wdXA8L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjwvZz48L3N2Zz4=" from="flow-chart" payload="{&quot;data&quot;:{&quot;mxGraphModel&quot;:{&quot;dx&quot;:&quot;1426&quot;,&quot;dy&quot;:&quot;855&quot;,&quot;grid&quot;:&quot;1&quot;,&quot;gridSize&quot;:&quot;10&quot;,&quot;guides&quot;:&quot;1&quot;,&quot;tooltips&quot;:&quot;1&quot;,&quot;connect&quot;:&quot;1&quot;,&quot;arrows&quot;:&quot;1&quot;,&quot;fold&quot;:&quot;1&quot;,&quot;page&quot;:&quot;1&quot;,&quot;pageScale&quot;:&quot;1&quot;,&quot;pageWidth&quot;:&quot;827&quot;,&quot;pageHeight&quot;:&quot;1169&quot;},&quot;mxCellMap&quot;:{&quot;aWM2ZIZB&quot;:{&quot;id&quot;:&quot;aWM2ZIZB&quot;},&quot;OY9en9Pd&quot;:{&quot;id&quot;:&quot;OY9en9Pd&quot;,&quot;parent&quot;:&quot;aWM2ZIZB&quot;},&quot;dx6L30zo&quot;:{&quot;id&quot;:&quot;dx6L30zo&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#FF3333;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;OY9en9Pd&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;240&quot;,&quot;y&quot;:&quot;200&quot;,&quot;width&quot;:&quot;280&quot;,&quot;height&quot;:&quot;280&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;fYlobgww&quot;:{&quot;id&quot;:&quot;fYlobgww&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;fillColor=#FFFF00;strokeColor=none;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;OY9en9Pd&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;290&quot;,&quot;y&quot;:&quot;250&quot;,&quot;width&quot;:&quot;180&quot;,&quot;height&quot;:&quot;180&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ivdbqqN1&quot;:{&quot;id&quot;:&quot;ivdbqqN1&quot;,&quot;value&quot;:&quot;Popup&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;fillColor=#CCE5FF;strokeColor=none;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;OY9en9Pd&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;290&quot;,&quot;y&quot;:&quot;430&quot;,&quot;width&quot;:&quot;310&quot;,&quot;height&quot;:&quot;120&quot;,&quot;as&quot;:&quot;geometry&quot;}}},&quot;mxCellList&quot;:[&quot;aWM2ZIZB&quot;,&quot;OY9en9Pd&quot;,&quot;dx6L30zo&quot;,&quot;fYlobgww&quot;,&quot;ivdbqqN1&quot;]},&quot;lastEditTime&quot;:0,&quot;snapshot&quot;:&quot;&quot;}" />



</div>
</div>


   Many components in the component library (such as `Button`, `IconButton`, and more) have default padding internally. When these components are used directly as the anchor for SpatialPopup, visual misalignment may occur.
   **Recommended practice**:
   Place the `Button` and `SpatialPopup` in the same `Box`, using `Box` as the anchor view to avoid alignment issues caused by padding.
* **The impact of child views within the anchor view on the anchor area**
   Other child views inside the anchor view also affect the size of the anchor area, so extra caution is needed when placing additional child views in the anchor view.
   For example, in the following code, because `CustomView` uses `fillMaxSize`, `Box` is filled completely, resulting in SpatialPopup not aligning correctly with `Button`.
   ```Kotlin
   Box {
       CustomView(modifier = Modifier.fillMaxSize)
       var showSpatialPopup by remember {mutableStateOf(false)}
       Button() {
           Text("show SpatialPopup")
       }
       if(showSpatialPopup) {
           SpatialPopup()
       }
   }
   ```

   **Recommended practice**:
   Do not use any size-related modifier on `Box`**,** and use `Box` to contain the target view (such as `Button`) and SpatialPopup. Do not place any other views inside `Box` except for the target view and SpatialPopup. As follows:
   ```Kotlin
   Box {
       // Can be Button or other components
       Button() {}
       // Popup menu relative to the position of Button
       SpatialPopup()
      
   }
   ```


### Customize SpatialPopup's popup position
Spatial UI provides a set of position layout rules for SpatialPopup based on the anchor view, used to describe the position where the popup pops up relative to the anchor (such as `Button`) in space. This position is jointly determined by horizontal and vertical dimensions.

<div style="display: flex;">
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.5246478873239439);">

Horizontal layout rules:

<img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSI1NjVweCIgaGVpZ2h0PSIyNDZweCIgdmlld0JveD0iLTAuNSAtMC41IDU2NSAyNDYiPjxkZWZzLz48Zz48cmVjdCB4PSIyIiB5PSIyIiB3aWR0aD0iODAiIGhlaWdodD0iODAiIGZpbGw9IiNlNmU2ZTYiIHN0cm9rZT0ibm9uZSIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDc4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogNDJweDsgbWFyZ2luLWxlZnQ6IDNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGZvbnQgc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij50b1N0YXJ0T2Y8L2ZvbnQ+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cmVjdCB4PSIxMDIiIHk9IjIiIHdpZHRoPSI4MCIgaGVpZ2h0PSI4MCIgZmlsbD0iI2U2ZTZlNiIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogNzhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiA0MnB4OyBtYXJnaW4tbGVmdDogMTAzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxmb250IHN0eWxlPSJmb250LXNpemU6MTRweCI+YWxpZ25TdGFydDwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjI0MiIgeT0iMiIgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZTZlNmU2IiBzdHJva2U9Im5vbmUiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiA3OHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDQycHg7IG1hcmdpbi1sZWZ0OiAyNDNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij5jZW50ZXI8L3NwYW4+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cmVjdCB4PSIzODIiIHk9IjIiIHdpZHRoPSI4MCIgaGVpZ2h0PSI4MCIgZmlsbD0iI2U2ZTZlNiIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogNzhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiA0MnB4OyBtYXJnaW4tbGVmdDogMzgzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6MTRweCI+YWxpZ25FbmQ8L3NwYW4+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cmVjdCB4PSI0ODIiIHk9IjIiIHdpZHRoPSI4MCIgaGVpZ2h0PSI4MCIgZmlsbD0iI2U2ZTZlNiIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogNzhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiA0MnB4OyBtYXJnaW4tbGVmdDogNDgzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6MTRweCI+dG9FbmRPZjwvc3Bhbj48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjEwMiIgeT0iMTAyIiB3aWR0aD0iMzYwIiBoZWlnaHQ9IjcwIiBmaWxsPSIjMzMzMzMzIiBzdHJva2U9Im5vbmUiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiAzNThweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAxMzdweDsgbWFyZ2luLWxlZnQ6IDEwM3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48Zm9udCBjb2xvcj0iI2ZmZmZmZiIgc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij5BbmNob3IgVmlldzwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gOTIgMjQyIEwgOTIgMiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IjMgMyIgcG9pbnRlci1ldmVudHM9InN0cm9rZSIvPjxwYXRoIGQ9Ik0gNDcyIDI0MiBMIDQ3MiAyIiBmaWxsPSJub25lIiBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgc3Ryb2tlLWRhc2hhcnJheT0iMyAzIiBwb2ludGVyLWV2ZW50cz0ic3Ryb2tlIi8+PC9nPjwvc3ZnPg==" from="flow-chart" payload="{&quot;data&quot;:{&quot;mxGraphModel&quot;:{&quot;dx&quot;:&quot;1018&quot;,&quot;dy&quot;:&quot;630&quot;,&quot;grid&quot;:&quot;1&quot;,&quot;gridSize&quot;:&quot;10&quot;,&quot;guides&quot;:&quot;1&quot;,&quot;tooltips&quot;:&quot;1&quot;,&quot;connect&quot;:&quot;1&quot;,&quot;arrows&quot;:&quot;1&quot;,&quot;fold&quot;:&quot;1&quot;,&quot;page&quot;:&quot;1&quot;,&quot;pageScale&quot;:&quot;1&quot;,&quot;pageWidth&quot;:&quot;827&quot;,&quot;pageHeight&quot;:&quot;1169&quot;},&quot;mxCellMap&quot;:{&quot;Np0fFpNZ&quot;:{&quot;id&quot;:&quot;Np0fFpNZ&quot;},&quot;yIe1hGvl&quot;:{&quot;id&quot;:&quot;yIe1hGvl&quot;,&quot;parent&quot;:&quot;Np0fFpNZ&quot;},&quot;WWowiHzL&quot;:{&quot;id&quot;:&quot;WWowiHzL&quot;,&quot;value&quot;:&quot;<font style=\&quot;font-size:14px\&quot;>toStartOf</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;60&quot;,&quot;y&quot;:&quot;360&quot;,&quot;width&quot;:&quot;80&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ZRerXDS5&quot;:{&quot;id&quot;:&quot;ZRerXDS5&quot;,&quot;value&quot;:&quot;<font style=\&quot;font-size:14px\&quot;>alignStart</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;160&quot;,&quot;y&quot;:&quot;360&quot;,&quot;width&quot;:&quot;80&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;APdFb2eJ&quot;:{&quot;id&quot;:&quot;APdFb2eJ&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>center</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;300&quot;,&quot;y&quot;:&quot;360&quot;,&quot;width&quot;:&quot;80&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;E7x2WOcg&quot;:{&quot;id&quot;:&quot;E7x2WOcg&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>alignEnd</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;440&quot;,&quot;y&quot;:&quot;360&quot;,&quot;width&quot;:&quot;80&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;rOkIq5um&quot;:{&quot;id&quot;:&quot;rOkIq5um&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>toEndOf</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;540&quot;,&quot;y&quot;:&quot;360&quot;,&quot;width&quot;:&quot;80&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;kCJC1ufr&quot;:{&quot;id&quot;:&quot;kCJC1ufr&quot;,&quot;value&quot;:&quot;<font color=\&quot;#ffffff\&quot; style=\&quot;font-size:14px\&quot;>Anchor View</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#333333;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;160&quot;,&quot;y&quot;:&quot;460&quot;,&quot;width&quot;:&quot;360&quot;,&quot;height&quot;:&quot;70&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;7iDUMIG0&quot;:{&quot;id&quot;:&quot;7iDUMIG0&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;endArrow=none;dashed=1;html=1;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;dashed&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;width&quot;:&quot;50&quot;,&quot;height&quot;:&quot;50&quot;,&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;150&quot;,&quot;y&quot;:&quot;600&quot;,&quot;as&quot;:&quot;sourcePoint&quot;},&quot;-1-mxPoint&quot;:{&quot;x&quot;:&quot;150&quot;,&quot;y&quot;:&quot;360&quot;,&quot;as&quot;:&quot;targetPoint&quot;}}},&quot;c6h9WCoY&quot;:{&quot;id&quot;:&quot;c6h9WCoY&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;endArrow=none;dashed=1;html=1;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;dashed&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;width&quot;:&quot;50&quot;,&quot;height&quot;:&quot;50&quot;,&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;530&quot;,&quot;y&quot;:&quot;600&quot;,&quot;as&quot;:&quot;sourcePoint&quot;},&quot;-1-mxPoint&quot;:{&quot;x&quot;:&quot;530&quot;,&quot;y&quot;:&quot;360&quot;,&quot;as&quot;:&quot;targetPoint&quot;}}}},&quot;mxCellList&quot;:[&quot;Np0fFpNZ&quot;,&quot;yIe1hGvl&quot;,&quot;WWowiHzL&quot;,&quot;ZRerXDS5&quot;,&quot;APdFb2eJ&quot;,&quot;E7x2WOcg&quot;,&quot;rOkIq5um&quot;,&quot;kCJC1ufr&quot;,&quot;7iDUMIG0&quot;,&quot;c6h9WCoY&quot;]},&quot;lastEditTime&quot;:0,&quot;snapshot&quot;:&quot;&quot;}" />



</div>
<div style="flex-shrink: 0;width: calc((100% - 16px) * 0.4753521126760563);margin-left: 16px;">

Vertical layout rules:

<img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSI0ODZweCIgaGVpZ2h0PSI1MjVweCIgdmlld0JveD0iLTAuNSAtMC41IDQ4NiA1MjUiPjxkZWZzLz48Zz48cmVjdCB4PSIzMjIiIHk9IjIiIHdpZHRoPSIxMDAiIGhlaWdodD0iODAiIGZpbGw9IiNlNmU2ZTYiIHN0cm9rZT0ibm9uZSIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDk4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogNDJweDsgbWFyZ2luLWxlZnQ6IDMyM3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48Zm9udCBzdHlsZT0iZm9udC1zaXplOjE0cHgiPmFib3ZlPC9mb250PjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iMzIyIiB5PSIxMDIiIHdpZHRoPSIxMDAiIGhlaWdodD0iODAiIGZpbGw9IiNlNmU2ZTYiIHN0cm9rZT0ibm9uZSIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDk4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMTQycHg7IG1hcmdpbi1sZWZ0OiAzMjNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGZvbnQgc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij5hbGlnblRvcDwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjMyMiIgeT0iMjIyIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZTZlNmU2IiBzdHJva2U9Im5vbmUiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiA5OHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDI2MnB4OyBtYXJnaW4tbGVmdDogMzIzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6MTRweCI+Y2VudGVyPC9zcGFuPjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iMzIyIiB5PSIzNDIiIHdpZHRoPSIxMDAiIGhlaWdodD0iODAiIGZpbGw9IiNlNmU2ZTYiIHN0cm9rZT0ibm9uZSIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDk4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMzgycHg7IG1hcmdpbi1sZWZ0OiAzMjNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PHNwYW4gc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij5hbGlnbkJvdHRvbTwvc3Bhbj48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjMyMiIgeT0iNDQyIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZTZlNmU2IiBzdHJva2U9Im5vbmUiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiA5OHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDQ4MnB4OyBtYXJnaW4tbGVmdDogMzIzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxzcGFuIHN0eWxlPSJmb250LXNpemU6MTRweCI+YmVsb3c8L3NwYW4+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cmVjdCB4PSI5MiIgeT0iMTAyIiB3aWR0aD0iMjEwIiBoZWlnaHQ9IjMyMCIgZmlsbD0iIzMzMzMzMyIgc3Ryb2tlPSJub25lIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogMjA4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMjYycHg7IG1hcmdpbi1sZWZ0OiA5M3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48Zm9udCBjb2xvcj0iI2ZmZmZmZiIgc3R5bGU9ImZvbnQtc2l6ZToxNHB4Ij5BbmNob3IgVmlldzwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gMiA5MiBMIDQ4MiA5MiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IjMgMyIgcG9pbnRlci1ldmVudHM9InN0cm9rZSIvPjxwYXRoIGQ9Ik0gMiA0MzIgTCA0ODIgNDMyIiBmaWxsPSJub25lIiBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgc3Ryb2tlLWRhc2hhcnJheT0iMyAzIiBwb2ludGVyLWV2ZW50cz0ic3Ryb2tlIi8+PC9nPjwvc3ZnPg==" from="flow-chart" payload="{&quot;data&quot;:{&quot;mxGraphModel&quot;:{&quot;dx&quot;:&quot;1018&quot;,&quot;dy&quot;:&quot;630&quot;,&quot;grid&quot;:&quot;1&quot;,&quot;gridSize&quot;:&quot;10&quot;,&quot;guides&quot;:&quot;1&quot;,&quot;tooltips&quot;:&quot;1&quot;,&quot;connect&quot;:&quot;1&quot;,&quot;arrows&quot;:&quot;1&quot;,&quot;fold&quot;:&quot;1&quot;,&quot;page&quot;:&quot;1&quot;,&quot;pageScale&quot;:&quot;1&quot;,&quot;pageWidth&quot;:&quot;827&quot;,&quot;pageHeight&quot;:&quot;1169&quot;},&quot;mxCellMap&quot;:{&quot;Np0fFpNZ&quot;:{&quot;id&quot;:&quot;Np0fFpNZ&quot;},&quot;yIe1hGvl&quot;:{&quot;id&quot;:&quot;yIe1hGvl&quot;,&quot;parent&quot;:&quot;Np0fFpNZ&quot;},&quot;WWowiHzL&quot;:{&quot;id&quot;:&quot;WWowiHzL&quot;,&quot;value&quot;:&quot;<font style=\&quot;font-size:14px\&quot;>above</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;480&quot;,&quot;width&quot;:&quot;100&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ZRerXDS5&quot;:{&quot;id&quot;:&quot;ZRerXDS5&quot;,&quot;value&quot;:&quot;<font style=\&quot;font-size:14px\&quot;>alignTop</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;580&quot;,&quot;width&quot;:&quot;100&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;APdFb2eJ&quot;:{&quot;id&quot;:&quot;APdFb2eJ&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>center</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;700&quot;,&quot;width&quot;:&quot;100&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;E7x2WOcg&quot;:{&quot;id&quot;:&quot;E7x2WOcg&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>alignBottom</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;820&quot;,&quot;width&quot;:&quot;100&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;rOkIq5um&quot;:{&quot;id&quot;:&quot;rOkIq5um&quot;,&quot;value&quot;:&quot;<span style=\&quot;font-size:14px\&quot;>below</span>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#E6E6E6;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;920&quot;,&quot;width&quot;:&quot;100&quot;,&quot;height&quot;:&quot;80&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;kCJC1ufr&quot;:{&quot;id&quot;:&quot;kCJC1ufr&quot;,&quot;value&quot;:&quot;<font color=\&quot;#ffffff\&quot; style=\&quot;font-size:14px\&quot;>Anchor View</font>&quot;,&quot;style&quot;:&quot;rounded=0;whiteSpace=wrap;html=1;strokeColor=none;fillColor=#333333;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;Rectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;250&quot;,&quot;y&quot;:&quot;580&quot;,&quot;width&quot;:&quot;210&quot;,&quot;height&quot;:&quot;320&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;7iDUMIG0&quot;:{&quot;id&quot;:&quot;7iDUMIG0&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;endArrow=none;dashed=1;html=1;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;dashed&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;width&quot;:&quot;50&quot;,&quot;height&quot;:&quot;50&quot;,&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;160&quot;,&quot;y&quot;:&quot;570&quot;,&quot;as&quot;:&quot;sourcePoint&quot;},&quot;-1-mxPoint&quot;:{&quot;x&quot;:&quot;640&quot;,&quot;y&quot;:&quot;570&quot;,&quot;as&quot;:&quot;targetPoint&quot;}}},&quot;vxmHis94&quot;:{&quot;id&quot;:&quot;vxmHis94&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;endArrow=none;dashed=1;html=1;&quot;,&quot;parent&quot;:&quot;yIe1hGvl&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;dashed&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;width&quot;:&quot;50&quot;,&quot;height&quot;:&quot;50&quot;,&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;160&quot;,&quot;y&quot;:&quot;910&quot;,&quot;as&quot;:&quot;sourcePoint&quot;},&quot;-1-mxPoint&quot;:{&quot;x&quot;:&quot;640&quot;,&quot;y&quot;:&quot;910&quot;,&quot;as&quot;:&quot;targetPoint&quot;}}}},&quot;mxCellList&quot;:[&quot;Np0fFpNZ&quot;,&quot;yIe1hGvl&quot;,&quot;WWowiHzL&quot;,&quot;ZRerXDS5&quot;,&quot;APdFb2eJ&quot;,&quot;E7x2WOcg&quot;,&quot;rOkIq5um&quot;,&quot;kCJC1ufr&quot;,&quot;7iDUMIG0&quot;,&quot;vxmHis94&quot;]},&quot;lastEditTime&quot;:0,&quot;snapshot&quot;:&quot;&quot;}" />



</div>
</div>


In practice, you can customize the popup position of SpatialPopup by using `rememberSpatialPopupPositionProvider` in combination with `HorizontalPlacement` and `VerticalPlacement`.
Additionally, without changing the overall layout rules, you can use `offset` to precisely control the final popup position of SpatialPopup. The value of `offset` follows the definition of View's local coordinate system.
```Kotlin
popupPositionProvider = rememberSpatialPopupPositionProvider(
    horizontalPlacement = HorizontalPlacement.toStartOf(offset = -8.dp),
    verticalPlacement = VerticalPlacement.alignBottom
)
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/0d0e22edc501454c91370bc7ff5e57b0~tplv-goo7wpa0wc-image.image)
The following code customizes `popupPositionProvider` to align SpatialPopup with the left edge of the anchor view and to display it a certain distance below, with state control to show and close the popup.
```Kotlin
@Composable
fun CustomPopupPositionSample() {
    // Declare the display state of the spatial popup
    var showPopup by remember { mutableStateOf(false) }
    Box {
        if (showPopup) {
            SpatialPopup(
                // Customize the popup position of SpatialPopup
                popupPositionProvider = rememberSpatialPopupPositionProvider(
                    horizontalPlacement = HorizontalPlacement.alignStart(),
                    verticalPlacement = VerticalPlacement.below(offset = 10.dp)
                ),
                onDismissRequest = { showPopup = false },
            ) {
                Text(text = "SpatialPopup", modifier = Modifier.align(Alignment.Center))
            }
        }
        Button(onClick = { showPopup = true }) { Text("show popup") }
    }
}
```

![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/383e5289b65f47b085bce4198977a76b~tplv-goo7wpa0wc-image.image)
### Make SpatialPopup adapt to the size of its content
By default, SpatialPopup uses preset minimum dimensions for layout. By setting `defaultMinWidth` and `defaultMinHeight` to `Dp.Unspecified`, SpatialPopup's size can automatically adjust according to its internal content, enabling adaptive content size display.
```Kotlin
@Composable
fun SpatialPopupWrapContentSizeSample() {
    var showPopup by remember { mutableStateOf(false) }
    Box {
        if (showPopup) {
            SpatialPopup(
                // Use Dp.Unspecified to wrap (adapt to) the content size
                defaultMinHeight = Dp.Unspecified,
                defaultMinWidth = Dp.Unspecified,
                onDismissRequest = { showPopup = false },
            ) {
                Text(text = "SpatialPopup", modifier = Modifier.align(Alignment.Center))
            }
        }
        Button(onClick = { showPopup = true }) { Text("show popup") }
    }
}
```

