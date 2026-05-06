Trace records are important information for locating and analyzing performance issues. System Trace can record device activity over a period of time and generate a report to help you understand and troubleshoot the performance of your app.
In PICO OS 6, the execution flow of spatial apps differs from that of traditional Android apps. Therefore, PICO provides a series of dedicated types of trace records to help you diagnose performance issues in spatial apps more efficiently.
## Retrieve trace records
There are several ways to obtain trace records on the Android platform. This section focuses on how to collect trace records using Profiler and Perfetto.

* Profiler is a built-in tool in Android Studio for real-time monitoring and analysis of an app's runtime performance.
* Perfetto is a standalone performance analysis tool for collecting, viewing, and analyzing system-level trace data.

### Use Profiler
You can view the app's slice information in Profiler, which is an event record with a start and end time and represents a continuous operation or state. The steps are as follows:

1. In Android Studio, open Profiler.
2. Select **Capture System Activities** and the process you want to debug.
3. In **Start profiler task from**, select the startup mode you need,
4. Click the **Start anyway** button in the bottom-right corner.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/70fec8fb6d8b48c7a2ccbe5aa69221d4~tplv-goo7wpa0wc-image.image" width="3386px" />   </div>

   Profiler starts collecting trace records.
5. After retrieving trace records, in the app, perform the operations whose performance you want to learn about and troubleshoot.
6. After completing the operation, click the **Stop recording and show results** button.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b8ff31cbeae84682a7cd07b0faf3e334~tplv-goo7wpa0wc-image.image" width="4956px" />   </div>

   Trace records are retrieved. You will see them after a moment.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/f6994988b3cc4a389e1d7d0873809cb5~tplv-goo7wpa0wc-image.image" width="4956px" />   </div>


### Use Perfetto
The Profiler only displays slice information for the app. If you need to view counter information, which shows how values change over time, you can use Perfetto to open the trace records you just retrieved. After opening a trace record with Perfetto, you will simultaneously see slice and counter information. The steps are as follows:

1. In the **Profiler** panel, switch to the **Past Recordings** tab.
2. In the **Recordings name** list, select the previously retrieved trace record.
3. Click the **Export recording** button to export the trace record file to your PC.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b2bf2923dfc048a2af8419eb607ed699~tplv-goo7wpa0wc-image.image" width="4964px" />   </div>

4. In [Perfetto UI](https://ui.perfetto.dev/), open the trace file, then select the process to analyze.
   <div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/de92443330af4e1f9287c2c679ef392e~tplv-goo7wpa0wc-image.image" width="2020px" />   </div>


## Analyze trace records
After retrieving trace records, you can analyze them to gain a detailed understanding of the app's performance and perform targeted optimization.
### Spatial app initialization
When a spatial app starts, it initializes its own unique logic. To analyze the initialization time of a spatial app, you must ensure that trace has captured the app's entire startup process.
The slice named Spatial_App_Initialize records the initialization process of the PICO Spatial SDK, during which the necessary operations for starting a spatial app are completed, followed by the callback for the first frame of the app. If your app starts slowly, you can begin troubleshooting with Spatial_App_Initialize.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bf79fba76e514847a75d68ec0869396d~tplv-goo7wpa0wc-image.image" width="2486px" /></div>

### Frame loop
After the spatial app starts, it enters the frame loop. In addition to the callbacks in the Android frame loop, spatial apps also generate trace records for the following unique callbacks, where the execution duration of the `update()` method of each registered system is recorded. Trace information related to the frame loop can be found in the "Appendix: Trace list" section.

<img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSI5MjVweCIgaGVpZ2h0PSIyNzVweCIgdmlld0JveD0iLTAuNSAtMC41IDkyNSAyNzUiPjxkZWZzLz48Zz48cmVjdCB4PSIyIiB5PSIyIiB3aWR0aD0iOTIwIiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNkNGUxZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDkxOHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDIycHg7IG1hcmdpbi1sZWZ0OiAzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxkaXYgc3R5bGU9ImZvbnQtc2l6ZToxNHB4O2NvbG9yOnJnYigzMSwgMzUsIDQxKSI+PHA+Q2hvcmVvZ3JhcGhlciNkb0ZyYW1lICo8L3A+PC9kaXY+PHNwYW4+PC9zcGFuPjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iNDIiIHk9IjYyIiB3aWR0aD0iODQwIiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNkNGUxZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDgzOHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDgycHg7IG1hcmdpbi1sZWZ0OiA0M3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJmb250LXNpemU6MTRweDtjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPkNob3Jlb2dyYXBoZXIjYmVnaW5TcGF0aWFsRnJhbWU8L3A+PC9kaXY+PHNwYW4+PC9zcGFuPjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iODIiIHk9IjEyMiIgd2lkdGg9IjUwMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZDRlMWY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiA0OThweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAxNDJweDsgbWFyZ2luLWxlZnQ6IDgzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxkaXYgc3R5bGU9ImZvbnQtc2l6ZToxNHB4O2NvbG9yOnJnYigzMSwgMzUsIDQxKSI+PHA+M2RfZWM8L3A+PC9kaXY+PHNwYW4+PC9zcGFuPjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iNjAyIiB5PSIxMjIiIHdpZHRoPSIyNDAiIGhlaWdodD0iNDAiIHJ4PSI2IiByeT0iNiIgZmlsbD0iI2Q0ZTFmNSIgc3Ryb2tlPSIjMDAwMDAwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogMjM4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMTQycHg7IG1hcmdpbi1sZWZ0OiA2MDNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iZm9udC1zaXplOjE0cHg7Y29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD5DaG9yZW9ncmFwaGVyI2VuZFNwYXRpYWxGcmFtZTwvcD48L2Rpdj48c3Bhbj48L3NwYW4+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cmVjdCB4PSIxMjIiIHk9IjE3MiIgd2lkdGg9IjQyMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZDRlMWY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiA0MThweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAxOTJweDsgbWFyZ2luLWxlZnQ6IDEyM3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJmb250LXNpemU6MTRweDtjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPlN5c3RlbV9VcGRhdGU8L3A+PC9kaXY+PHNwYW4+PC9zcGFuPjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iMTYyIiB5PSIyMzIiIHdpZHRoPSIxNjAiIGhlaWdodD0iNDAiIHJ4PSI2IiByeT0iNiIgZmlsbD0iI2Q0ZTFmNSIgc3Ryb2tlPSIjMDAwMDAwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogMTU4cHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMjUycHg7IG1hcmdpbi1sZWZ0OiAxNjNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iZm9udC1zaXplOjE0cHg7Y29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD5TeXN0ZW1fVXBkYXRlOiAqPC9wPjwvZGl2PjxzcGFuPjwvc3Bhbj48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjM0MiIgeT0iMjMyIiB3aWR0aD0iMTYwIiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNkNGUxZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDE1OHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDI1MnB4OyBtYXJnaW4tbGVmdDogMzQzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxkaXYgc3R5bGU9ImZvbnQtc2l6ZToxNHB4O2NvbG9yOnJnYigzMSwgMzUsIDQxKSI+PHA+U3lzdGVtX1VwZGF0ZTogKjwvcD48L2Rpdj48c3Bhbj48L3NwYW4+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48L2c+PC9zdmc+" from="flow-chart" payload="{&quot;data&quot;:{&quot;mxGraphModel&quot;:{&quot;dx&quot;:&quot;1251&quot;,&quot;dy&quot;:&quot;734&quot;,&quot;grid&quot;:&quot;1&quot;,&quot;gridSize&quot;:&quot;10&quot;,&quot;guides&quot;:&quot;1&quot;,&quot;tooltips&quot;:&quot;1&quot;,&quot;connect&quot;:&quot;1&quot;,&quot;arrows&quot;:&quot;1&quot;,&quot;fold&quot;:&quot;1&quot;,&quot;page&quot;:&quot;1&quot;,&quot;pageScale&quot;:&quot;1&quot;,&quot;pageWidth&quot;:&quot;827&quot;,&quot;pageHeight&quot;:&quot;1169&quot;},&quot;mxCellMap&quot;:{&quot;z2LcxPBM&quot;:{&quot;id&quot;:&quot;z2LcxPBM&quot;},&quot;8jWyTxEe&quot;:{&quot;id&quot;:&quot;8jWyTxEe&quot;,&quot;parent&quot;:&quot;z2LcxPBM&quot;},&quot;uZadubxG&quot;:{&quot;id&quot;:&quot;uZadubxG&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>Choreographer#doFrame *</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;80&quot;,&quot;y&quot;:&quot;120&quot;,&quot;width&quot;:&quot;920&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;NVl8lWEB&quot;:{&quot;id&quot;:&quot;NVl8lWEB&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>Choreographer#beginSpatialFrame</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;120&quot;,&quot;y&quot;:&quot;180&quot;,&quot;width&quot;:&quot;840&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ANpq3C80&quot;:{&quot;id&quot;:&quot;ANpq3C80&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>3d_ec</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;160&quot;,&quot;y&quot;:&quot;240&quot;,&quot;width&quot;:&quot;500&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;hTSKPRuR&quot;:{&quot;id&quot;:&quot;hTSKPRuR&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>Choreographer#endSpatialFrame</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;680&quot;,&quot;y&quot;:&quot;240&quot;,&quot;width&quot;:&quot;240&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;yKosT59Q&quot;:{&quot;id&quot;:&quot;yKosT59Q&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>System_Update</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;200&quot;,&quot;y&quot;:&quot;290&quot;,&quot;width&quot;:&quot;420&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;NjP905fN&quot;:{&quot;id&quot;:&quot;NjP905fN&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>System_Update: *</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;240&quot;,&quot;y&quot;:&quot;350&quot;,&quot;width&quot;:&quot;160&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;CNZ2MfXH&quot;:{&quot;id&quot;:&quot;CNZ2MfXH&quot;,&quot;value&quot;:&quot;<div style=\&quot;font-size:14px;color:rgb(31, 35, 41)\&quot;><p>System_Update: *</p></div><span></span>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#D4E1F5;&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;parent&quot;:&quot;8jWyTxEe&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;420&quot;,&quot;y&quot;:&quot;350&quot;,&quot;width&quot;:&quot;160&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}}},&quot;mxCellList&quot;:[&quot;z2LcxPBM&quot;,&quot;8jWyTxEe&quot;,&quot;uZadubxG&quot;,&quot;NVl8lWEB&quot;,&quot;ANpq3C80&quot;,&quot;hTSKPRuR&quot;,&quot;yKosT59Q&quot;,&quot;NjP905fN&quot;,&quot;CNZ2MfXH&quot;]},&quot;lastEditTime&quot;:0,&quot;snapshot&quot;:&quot;&quot;}" />

If `CustomSystem` has been registered in a spatial app:
```Kotlin
fun mainApp(scope: SpatialAppScope) = with(scope) {
    DefaultWindowContainer {
        HomeScreen(Modifier.windowConstraints(width = 1600.dp, height = 1000.dp))
    }
    System.register(CustomSystem::class.java)
}

class CustomSystem: System() {

    override fun update(context: SceneUpdateContext) {
        super.update(context)
        Log.d("CustomSystem", "update")
    }

}
```

You can see the slice named `System_Update: CustomSystem` in the trace record.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/668fcc05bd224e30995ef00ab57240a6~tplv-goo7wpa0wc-image.image" width="3810px" /></div>

If the app experiences a frame drop, the `frameDrop` counter will be incremented by one (recorded as 1).
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8c2448bf08bf4346be5e20f9e6576094~tplv-goo7wpa0wc-image.image" width="2500px" /></div>

Profiler does not display counter information in trace records; this information must be viewed in Perfetto UI. For more information, refer to the section titled "Use Perfetto" above.

### Resource loading
3D resources are an important part of spatial apps, but the loading process often takes some time. If the loading is performed on the main thread, the trace record will record the corresponding slice, which helps identify and optimize loading performance bottlenecks. All types of resources loaded on the main thread will be recorded in trace records as slices in the format `Load{resource_type}: {name}`. For trace information related to resource loading, refer to the "Appendix: Trace list" section.
If, on the main thread, you load the `Entity` named `"Hi"` in the `AssetBundle`:
```Kotlin
@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SpatialView { content, _ ->
            val entity =
                try {
                    Entity.load(modelName = "Hi", bundle = AssetBundle.load("asset://hi.bundle"))
                } catch (e: ResourceLoadingException) {
                    null
                }
            entity?.let {
                content.addEntity(it)
            }
        }
    }
}
```

In the trace record, you can see a slice named `LoadEntity_Asset: Hi`. By reviewing the record, you can identify which resource loads are blocking the main thread and make optimizations accordingly, for example, by moving these loads to the IO thread.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2697a9ebdf03418181560a550a5e0bda~tplv-goo7wpa0wc-image.image" width="2142px" /></div>

### Resource counting
During the operation of a spatial app, several 3D resources are generated, such as material and model. The number of each type of resource is recorded in trace records as counters. For trace information related to resource counting, refer to the "Appendix: Trace list" section.
In `SpatialView`, the following code asynchronously loads an `Entity` named `"Hi"` from an `AssetBundle` and adds it to the scene to avoid blocking the main thread.
```Kotlin
@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SpatialView { content, _ ->
            val entity = withContext(Dispatchers.IO) {
                val bundle = AssetBundle.load("asset://hi.bundle")
                val entity = Entity.load(modelName = "Hi", bundle = bundle)
                bundle.close()
                entity
            }

            entity.let {
                content.addEntity(it)
            }
        }
    }
}
```

After loading the model, close the AssetBundle. In trace, you can then observe changes in the value of the counter named `assetBundleCount`.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/468f7077abc64208aec4edce8678ad6b~tplv-goo7wpa0wc-image.image" width="3022px" /></div>

Profiler does not display counter information in trace records; this information must be viewed in Perfetto UI. For more information, refer to the section titled "Use Perfetto" above.

### Feature usage
Various 3D features, such as physics and lighting, are also used in spatial apps, and trace records will record the usage of various features. For trace information related to feature usage, refer to the "Appendix: Trace list" section.
For example, a model and a spotlight are added to hi.bundle:
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/337920956cef446299b25e36c269361c~tplv-goo7wpa0wc-image.image" width="2498px" /></div>

Then load and run the following code:
```Kotlin
@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SpatialView { content, _ ->
            val entity = withContext(Dispatchers.IO) {
                val bundle = AssetBundle.load("asset://hi.bundle")
                val entity = Entity.load(modelName = "Hi", bundle = bundle)
                bundle.close()
                entity
            }

            entity.let {
                content.addEntity(it)
            }
        }
    }
}
```

You can see a trace record containing counters named `modelComponentCount` and `spotLightComponentCount`.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e8131b2800034d65aa6de7cf3d28622f~tplv-goo7wpa0wc-image.image" width="3020px" /></div>

### Model rendering
For the rendering of each frame, the trace record of the `com.pico.spatial.runtime` process contains counter information for the number of rendered surfaces and the number of draw calls for that frame. For trace information related to model rendering, refer to the "Appendix: Trace list" section.
<div style="text-align: center"><img src="https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8b90b80b12d143098d3bac865d630a76~tplv-goo7wpa0wc-image.image" width="3022px" /></div>

## Appendix: Trace List
### Traces for the app process

* Spatial app initialization:
   | **Trace name** | **Description** |
   | --- | --- |
   | Spatial_App_Initialize | Initialization of the basic functions of spatial apps. |
* Frame loop:
   | **Trace name** | **Description** | **Note** |
   | --- | --- | --- |
   | Choreographer#beginSpatialFrame | Start a frame. | / |
   | 3d_ec | Refresh the 3D content. | / |
   | Choreographer#endSpatialFrame | End a frame. | / |
   | System_Update | Update 3D data and execute `update()` for all custom systems. | / |
   | System_Update: {name} | Define the `update()` method for a custom system. | The trace name is followed by the name of the custom system. If obfuscation is enabled for the app, the custom system's name will appear as its obfuscated version. |
   | frameDrop | Whether the current 3D rendering in the app process timed out. | This is a value that can be either 0 or 1, where 0 indicates no timeout and 1 indicates a timeout. |
* Resource loading:
   | **Trace name** | **Description** | **Note** |
   | --- | --- | --- |
   | LoadEntity: {name} | Create an entity object from a path in the UI thread. | This type of trace data is only output when resources are loaded in the UI thread. The trace name is followed by the path or name of the loaded resource. |
   | LoadEntity_Asset: {name} | Create an entity object from a bundle on the UI thread. |  |
   | LoadAsset: {name} | Create an AssetBundle object in the UI thread. |  |
   | LoadMesh: {name} | Create a MeshResource object from a path in the UI thread. |  |
   | LoadTexture: {name} | Create a TextureResource object from a path in the UI thread. |  |
* Resource counting:
   | **Trace name** | **Description** | **Note** |
   | --- | --- | --- |
   | assetBundleCount | Number of currently loaded AssetBundle. | When the corresponding quantity changes, the value of the Counter is printed and displayed as a numeric value in the trace. |
   | meshResourceCount | Number of currently loaded MeshResource. |  |
   | textureResourceCount | Number of currently loaded TextureResource. |  |
   | animationResourceCount | Number of currently loaded AnimationResource. |  |
   | physicsMaterialResourceCount | Number of currently loaded PhysicsMaterialResource. |  |
   | shapeResourceCount | Number of currently loaded ShapeResource. |  |
   | videoMaterialCount | Number of currently loaded VideoMaterial. |  |
   | shaderGraphMaterialCount | Number of currently loaded ShaderGraphMaterial. |  |
* Feature usage:
   | **Feature** | **Trace name** | **Description** | **Note** |
   | --- | --- | --- | --- |
   | Model | transformComponentCount | Number of currently loaded TransformComponent. | When the corresponding quantity changes, the value of Counter is printed, and it is displayed as a numeric value on Trace. |
   |  | modelComponentCount | Number of currently loaded ModelComponent. |  |
   | physics simulation | collisionComponentCount | Number of currently loaded CollisionComponent. |  |
   |  | rigidBodyComponentCount | Number of currently loaded RigidBodyComponent. |  |
   |  | physicsVelocityComponentCount | Number of currently loaded PhysicsVelocityComponent. |  |
   |  | physicsForceComponentCount | Number of currently loaded PhysicsForceComponent. |  |
   |  | physicsWorldComponentCount | Number of currently loaded PhysicsWorldComponent. |  |
   | Spatial video | videoComponentCount | Number of currently loaded VideoComponent. |  |
   |  | videoPlayerComponentCount | Number of currently loaded VideoPlayerComponent. |  |
   |  | videoMaterialCount | Number of currently loaded VideoMaterial. |  |
   | Spatial Audio | objectAudioComponentCount | Number of currently loaded ObjectAudioComponent. |  |
   |  | ambientAudioComponentCount | Number of currently loaded AmbientAudioComponent. |  |
   | View Attachment | viewAttachmentCount | Number of currently displayed ViewAttachment. |  |
   | Particle | particleComponentCount | Number of currently loaded ParticleComponent. |  |
   | Portal | portalComponentCount | Number of currently loaded PortalComponent. |  |
   |  | portalWorldComponentCount | Number of currently loaded PortalWorldComponent. |  |
   |  | portalCrossingComponentCount | Number of currently loaded PortalCrossingComponent. |  |
   | Shader Graph | shaderGraphMaterialCount | Number of currently loaded ShaderGraphMaterial. |  |
   | dynamic lighting | pointLightComponentCount | Number of currently loaded PointLightComponent. |  |
   |  | spotLightComponentCount | Number of currently loaded SpotLightComponent. |  |
   |  | directionalLightComponentCount | Number of currently loaded DirectionalLightComponent. |  |
   | dynamic shadowing | groundingShadowComponentCount | Number of currently loaded GroundingShadowComponent. |  |

### PICO Spatial Engine Trace
Traces related to scene rendering are in the `com.pico.spatial.runtime` process, as shown in the table below.
| **Trace name** | **Description** |
| --- | --- |
| frameRate | The rendering frame rate of the Spatial Engine. |
| 3D Mesh Draw Call Count | The number of draw calls generated by ModelComponent. |
| Particle Draw Call Count | The number of draw calls generated by ParticleComponent. |
| Shadow Draw Call Count | The number of draw calls generated by ShadowComponent. |
| Draw Call Count | The total number of draw calls currently rendered by the Spatial Engine. |
| 3D Mesh Triangle Count | The number of triangles generated by ModelComponent. |
| Particle Triangle Count | The number of triangles generated by ParticleComponent. |
| Shadow Triangle Count | The number of triangles generated by ShadowComponent. |
| Triangle Count | The total number of triangles currently rendered by the Spatial Engine. |
| 3D Mesh Vertex Count | The number of vertices generated by ModelComponent. |
| Particle Vertex Count | The number of vertices generated by ParticleComponent. |
| Shadow Vertex Count | The number of vertices generated by ShadowComponent. |
| Vertex Count | The total number of vertices currently rendered by the Spatial Engine. |
| Visible Directional Lights | The number of currently visible directional lights. |
| Visible Point Lights | The number of currently visible point lights. |
| Visible Spot Lights | The number of currently visible spotlights. |
| Visible Lights | The total number of currently visible lights. |
| Skeletal Animation Count | The number of skeletal animations currently playing in the Spatial Engine. |
| Mesh Memory | The amount of memory currently used by meshes in the Spatial Engine. |
| Scene Graph Memory | The amount of memory currently used by the scene graph in the Spatial Engine. |
| Texture2D Memory | The amount of memory currently used by Texture2D in the Spatial Engine. |
## Learn more
For more information on using Profiler, refer to its [official documentation](https://developer.android.google.cn/studio/profile). In addition, you can also capture trace records using Perfetto UI or adb commands. For more information on using Perfetto, refer to its [official documentation](https://perfetto.dev/docs/quickstart/android-tracing).

