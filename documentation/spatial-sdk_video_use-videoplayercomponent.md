`VideoPlayerComponent` is an advanced video playback component implemented based on the SDK's built-in player (`CypressMediaPlayer`). It abstracts away complex underlying processes such as video decoding and frame rendering. You only need to simply create a `CypressMediaPlayer` and pass it to `VideoPlayerComponent` to complete the construction of the video rendering pipeline.
## About CypressMediaPlayer
`CypressMediaPlayer` is a built-in video playback component of the PICO Spatial SDK, providing basic playback control and video file loading functionality. When you create this player and pass it to the `VideoPlayerComponent`, the `VideoPlayerComponent` will automatically handle the entire video rendering process, so you no longer need to manually update the video texture. You can directly control video playback, pause, stop, and adjust the playback speed via `CypressMediaPlayer`.
### Functions
`CypressMediaPlayer` provides the following functions

* `prepareAsync()`: Asynchronously prepares `CypressMediaPlayer`.
* `play()`: Starts playback.
* `stop()`: Stops playback.
* `pause()`: Pauses playback.
* `resume()`: Resumes playback.
* `isPlaying()`: Checks if playback is ongoing.
* `setLoop()`: Sets the loop playback mode.
* `setPlaybackSpeed()`: Sets the playback speed.
* `getPlaybackSpeed()`: Gets the playback speed.
* `setVolume()`: Sets the volume.
* `getVolume()`: Gets the volume.
* `getCurPosition()`: Gets the current playback position.
* `getDuration()`: Gets the total duration of the video.
* `setDataSource()`: Sets the data source.
* `registerCypressMediaPlayerCallback()`: Registers the callback for CypressMediaPlayer.
* `unregisterCypressMediaPlayerCallback()`: Unregisters the callback for CypressMediaPlayer.
* `reset()`: Resets the player. This function is required when dynamically switching playlists.
* `getVideoWidth()`: Returns the width of the video.
* `getVideoHeight()`: Returns the height of the video.

### Encoding formats
`CypressMediaPlayer` supports the following encoding formats:

* avc(h264) 
* hevc(h265) 
* av1 
* vp9 
* vp8 
* h263 
* mpeg4

### File formats
`CypressMediaPlayer` supported the following file formats:

* MPEG-4: including .mp4, .mov, .m4a, .3gp, and .mj2 
* Matroska: including .mkv and .webm 
* .ts 
* .m2ts 
* .flv 
* .asf 
* .wmv 
* .vob 
* .avi 
* .mpg 
* .m2p 
* .mpeg

## Procedure
Integrate `VideoPlayerComponent` within the app to control video playback.
### Flowchart

<img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSI1OTVweCIgaGVpZ2h0PSI0NzBweCIgdmlld0JveD0iLTAuNSAtMC41IDU5NSA0NzAiPjxkZWZzLz48Zz48cGF0aCBkPSJNIDE1NyA0MiBMIDE1NyA2MiBMIDE1NyA1MiBMIDE1NyA2NS42MyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJzdHJva2UiLz48cGF0aCBkPSJNIDE1NyA3MC44OCBMIDE1My41IDYzLjg4IEwgMTU3IDY1LjYzIEwgMTYwLjUgNjMuODggWiIgZmlsbD0iIzAwMDAwMCIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48cmVjdCB4PSI5NyIgeT0iMiIgd2lkdGg9IjEyMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZWJlZmY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiAxMThweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAyMnB4OyBtYXJnaW4tbGVmdDogOThweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iY29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPkVudGl0eS5sb2FkKCkvY3JlYXRlKCk8L2ZvbnQ+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gMTU3IDExMiBMIDE1NyAxMzIgTCAxNTcgMTIyIEwgMTU3IDEzNS42MyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJzdHJva2UiLz48cGF0aCBkPSJNIDE1NyAxNDAuODggTCAxNTMuNSAxMzMuODggTCAxNTcgMTM1LjYzIEwgMTYwLjUgMTMzLjg4IFoiIGZpbGw9IiMwMDAwMDAiIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHBhdGggZD0iTSAyNTIgOTIgTCAzNTUuNjMgOTIiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzk5MzNmZiIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0ic3Ryb2tlIi8+PHBhdGggZD0iTSAzNjAuODggOTIgTCAzNTMuODggOTUuNSBMIDM1NS42MyA5MiBMIDM1My44OCA4OC41IFoiIGZpbGw9IiM5OTMzZmYiIHN0cm9rZT0iIzk5MzNmZiIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iNjIiIHk9IjcyIiB3aWR0aD0iMTkwIiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNlYmVmZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDE4OHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDkycHg7IG1hcmdpbi1sZWZ0OiA2M3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+cGxheWVyID0gQzwvZm9udD48c3BhbiBzdHlsZT0iYmFja2dyb3VuZC1jb2xvcjppbml0aWFsIj55cHJlc3NNZWRpYVBsYXllcjwvc3Bhbj48c3BhbiBzdHlsZT0iYmFja2dyb3VuZC1jb2xvcjppbml0aWFsIj4oKTwvc3Bhbj48L3A+PHA+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gNDc3IDExMiBMIDQ3NyAxMzIgTCA0NzcgMTIyIEwgNDc3IDEzNS42MyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTkzM2ZmIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJzdHJva2UiLz48cGF0aCBkPSJNIDQ3NyAxNDAuODggTCA0NzMuNSAxMzMuODggTCA0NzcgMTM1LjYzIEwgNDgwLjUgMTMzLjg4IFoiIGZpbGw9IiM5OTMzZmYiIHN0cm9rZT0iIzk5MzNmZiIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iMzYyIiB5PSI3MiIgd2lkdGg9IjIzMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZWJlZmY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiAyMjhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiA5MnB4OyBtYXJnaW4tbGVmdDogMzYzcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxkaXYgc3R5bGU9ImNvbG9yOnJnYigzMSwgMzUsIDQxKSI+PHA+PGZvbnQgc3R5bGU9ImZvbnQtc2l6ZToxMnB4Ij5wbGF5ZXIucmVnaXN0ZXJQbGF5ZXJDYWxsQmFjayhjYWxsYmFjayk8L2ZvbnQ+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxyZWN0IHg9IjM3MiIgeT0iMTQyIiB3aWR0aD0iMjEwIiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNlYmVmZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDIwOHB4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDE2MnB4OyBtYXJnaW4tbGVmdDogMzczcHg7Ij48ZGl2IHN0eWxlPSJib3gtc2l6aW5nOiBib3JkZXItYm94OyBmb250LXNpemU6IDA7IHRleHQtYWxpZ246IGNlbnRlcjsgIj48ZGl2IHN0eWxlPSJkaXNwbGF5OiBpbmxpbmUtYmxvY2s7IGZvbnQtc2l6ZTogMTJweDsgZm9udC1mYW1pbHk6IEhlbHZldGljYTsgY29sb3I6ICMwMDAwMDA7IGxpbmUtaGVpZ2h0OiAxLjI7IHBvaW50ZXItZXZlbnRzOiBhbGw7IHdoaXRlLXNwYWNlOiBub3JtYWw7IHdvcmQtd3JhcDogbm9ybWFsOyAiPjxkaXYgc3R5bGU9ImNvbG9yOnJnYigzMSwgMzUsIDQxKSI+PHA+PGZvbnQgc3R5bGU9ImZvbnQtc2l6ZToxMnB4Ij5wbGF5ZXIuc2V0RGF0YVNvdXJjZSgidmlkZW9QYXRoIik8L2ZvbnQ+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gMTU3IDE4MiBMIDE1NyAyMDIgTCAxNTcgMTkyIEwgMTU3IDIwNS42MyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJzdHJva2UiLz48cGF0aCBkPSJNIDE1NyAyMTAuODggTCAxNTMuNSAyMDMuODggTCAxNTcgMjA1LjYzIEwgMTYwLjUgMjAzLjg4IFoiIGZpbGw9IiMwMDAwMDAiIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iNjkuNSIgeT0iMTQyIiB3aWR0aD0iMTc1IiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNlYmVmZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDE3M3B4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDE2MnB4OyBtYXJnaW4tbGVmdDogNzFweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iY29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPnZpZGVvTWF0ID0gVmlkZW9NYXRlcmlhbCgpPC9mb250PjwvcD48L2Rpdj48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPjxzcGFuPjwvc3Bhbj48L2ZvbnQ+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cGF0aCBkPSJNIDE1NyAyNTIgTCAxNTcgMjcyIEwgMTU3IDI2MiBMIDE1NyAyNzUuNjMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0ic3Ryb2tlIi8+PHBhdGggZD0iTSAxNTcgMjgwLjg4IEwgMTUzLjUgMjczLjg4IEwgMTU3IDI3NS42MyBMIDE2MC41IDI3My44OCBaIiBmaWxsPSIjMDAwMDAwIiBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxyZWN0IHg9IjIiIHk9IjIxMiIgd2lkdGg9IjMxMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZWJlZmY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiAzMDhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAyMzJweDsgbWFyZ2luLWxlZnQ6IDNweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iY29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPlZpZGVvUGxheWVyQ29tcG9uZW50KHBsYXllcixFbnRpdHkubWVzaCx2aWRlb01hdCk8L2ZvbnQ+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gMTU3IDMyMiBMIDE1NyAzNDIgTCAxNTcgMzMyIEwgMTU3IDM0NS42MyIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDAwMDAwIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJzdHJva2UiLz48cGF0aCBkPSJNIDE1NyAzNTAuODggTCAxNTMuNSAzNDMuODggTCAxNTcgMzQ1LjYzIEwgMTYwLjUgMzQzLjg4IFoiIGZpbGw9IiMwMDAwMDAiIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PHJlY3QgeD0iNjQuNSIgeT0iMjgyIiB3aWR0aD0iMTg1IiBoZWlnaHQ9IjQwIiByeD0iNiIgcnk9IjYiIGZpbGw9IiNlYmVmZjUiIHN0cm9rZT0iIzAwMDAwMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKC0wLjUgLTAuNSkiPjxmb3JlaWduT2JqZWN0IHN0eWxlPSJvdmVyZmxvdzogdmlzaWJsZTsgdGV4dC1hbGlnbjogbGVmdDsiIHBvaW50ZXItZXZlbnRzPSJub25lIiB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIj48ZGl2IHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hodG1sIiBzdHlsZT0iZGlzcGxheTogZmxleDsgYWxpZ24taXRlbXM6IHVuc2FmZSBjZW50ZXI7IGp1c3RpZnktY29udGVudDogdW5zYWZlIGNlbnRlcjsgd2lkdGg6IDE4M3B4OyBoZWlnaHQ6IDFweDsgcGFkZGluZy10b3A6IDMwMnB4OyBtYXJnaW4tbGVmdDogNjZweDsiPjxkaXYgc3R5bGU9ImJveC1zaXppbmc6IGJvcmRlci1ib3g7IGZvbnQtc2l6ZTogMDsgdGV4dC1hbGlnbjogY2VudGVyOyAiPjxkaXYgc3R5bGU9ImRpc3BsYXk6IGlubGluZS1ibG9jazsgZm9udC1zaXplOiAxMnB4OyBmb250LWZhbWlseTogSGVsdmV0aWNhOyBjb2xvcjogIzAwMDAwMDsgbGluZS1oZWlnaHQ6IDEuMjsgcG9pbnRlci1ldmVudHM6IGFsbDsgd2hpdGUtc3BhY2U6IG5vcm1hbDsgd29yZC13cmFwOiBub3JtYWw7ICI+PGRpdiBzdHlsZT0iY29sb3I6cmdiKDMxLCAzNSwgNDEpIj48cD48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPmFkZCBWaWRlb0NvbXBvbmVudCB0byBFbnRpdHk8L2ZvbnQ+PC9wPjwvZGl2Pjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+PHNwYW4+PC9zcGFuPjwvZm9udD48L2Rpdj48L2Rpdj48L2Rpdj48L2ZvcmVpZ25PYmplY3Q+PC9nPjxwYXRoIGQ9Ik0gMTU3IDM5MiBMIDE1NyA0MzAuNjMiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBwb2ludGVyLWV2ZW50cz0ic3Ryb2tlIi8+PHBhdGggZD0iTSAxNTcgNDM1Ljg4IEwgMTUzLjUgNDI4Ljg4IEwgMTU3IDQzMC42MyBMIDE2MC41IDQyOC44OCBaIiBmaWxsPSIjMDAwMDAwIiBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgcG9pbnRlci1ldmVudHM9ImFsbCIvPjxwYXRoIGQ9Ik0gMjI0LjUgMzcyIEwgMzYwLjYzIDM3MiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTkzM2ZmIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IjMgMyIgcG9pbnRlci1ldmVudHM9InN0cm9rZSIvPjxwYXRoIGQ9Ik0gMzY1Ljg4IDM3MiBMIDM1OC44OCAzNzUuNSBMIDM2MC42MyAzNzIgTCAzNTguODggMzY4LjUgWiIgZmlsbD0iIzk5MzNmZiIgc3Ryb2tlPSIjOTkzM2ZmIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48cmVjdCB4PSI4OS41IiB5PSIzNTIiIHdpZHRoPSIxMzUiIGhlaWdodD0iNDAiIHJ4PSI2IiByeT0iNiIgZmlsbD0iI2ViZWZmNSIgc3Ryb2tlPSIjMDAwMDAwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogMTMzcHg7IGhlaWdodDogMXB4OyBwYWRkaW5nLXRvcDogMzcycHg7IG1hcmdpbi1sZWZ0OiA5MXB4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+cGxheWVyLnByZXBhcmVBc3luYzwvZm9udD48L3A+PC9kaXY+PGZvbnQgc3R5bGU9ImZvbnQtc2l6ZToxMnB4Ij48c3Bhbj48L3NwYW4+PC9mb250PjwvZGl2PjwvZGl2PjwvZGl2PjwvZm9yZWlnbk9iamVjdD48L2c+PHJlY3QgeD0iMTIyIiB5PSI0MzciIHdpZHRoPSI3MCIgaGVpZ2h0PSIzMCIgcng9IjQuNSIgcnk9IjQuNSIgZmlsbD0iI2ViZWZmNSIgc3Ryb2tlPSIjMDAwMDAwIiBwb2ludGVyLWV2ZW50cz0iYWxsIi8+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTAuNSAtMC41KSI+PGZvcmVpZ25PYmplY3Qgc3R5bGU9Im92ZXJmbG93OiB2aXNpYmxlOyB0ZXh0LWFsaWduOiBsZWZ0OyIgcG9pbnRlci1ldmVudHM9Im5vbmUiIHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiPjxkaXYgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJkaXNwbGF5OiBmbGV4OyBhbGlnbi1pdGVtczogdW5zYWZlIGNlbnRlcjsganVzdGlmeS1jb250ZW50OiB1bnNhZmUgY2VudGVyOyB3aWR0aDogNjhweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiA0NTJweDsgbWFyZ2luLWxlZnQ6IDEyM3B4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+ZW5kPC9mb250PjwvcD48L2Rpdj48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPjxzcGFuPjwvc3Bhbj48L2ZvbnQ+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48cGF0aCBkPSJNIDQ3NyAzOTIgTCA0NzcgNDEyIEwgMTY4LjM3IDQxMiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTkzM2ZmIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHN0cm9rZS1kYXNoYXJyYXk9IjMgMyIgcG9pbnRlci1ldmVudHM9InN0cm9rZSIvPjxwYXRoIGQ9Ik0gMTYzLjEyIDQxMiBMIDE3MC4xMiA0MDguNSBMIDE2OC4zNyA0MTIgTCAxNzAuMTIgNDE1LjUgWiIgZmlsbD0iIzk5MzNmZiIgc3Ryb2tlPSIjOTkzM2ZmIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48cmVjdCB4PSIzNjciIHk9IjM1MiIgd2lkdGg9IjIyMCIgaGVpZ2h0PSI0MCIgcng9IjYiIHJ5PSI2IiBmaWxsPSIjZWJlZmY1IiBzdHJva2U9IiMwMDAwMDAiIHBvaW50ZXItZXZlbnRzPSJhbGwiLz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41IC0wLjUpIj48Zm9yZWlnbk9iamVjdCBzdHlsZT0ib3ZlcmZsb3c6IHZpc2libGU7IHRleHQtYWxpZ246IGxlZnQ7IiBwb2ludGVyLWV2ZW50cz0ibm9uZSIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSI+PGRpdiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgc3R5bGU9ImRpc3BsYXk6IGZsZXg7IGFsaWduLWl0ZW1zOiB1bnNhZmUgY2VudGVyOyBqdXN0aWZ5LWNvbnRlbnQ6IHVuc2FmZSBjZW50ZXI7IHdpZHRoOiAyMThweDsgaGVpZ2h0OiAxcHg7IHBhZGRpbmctdG9wOiAzNzJweDsgbWFyZ2luLWxlZnQ6IDM2OHB4OyI+PGRpdiBzdHlsZT0iYm94LXNpemluZzogYm9yZGVyLWJveDsgZm9udC1zaXplOiAwOyB0ZXh0LWFsaWduOiBjZW50ZXI7ICI+PGRpdiBzdHlsZT0iZGlzcGxheTogaW5saW5lLWJsb2NrOyBmb250LXNpemU6IDEycHg7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2E7IGNvbG9yOiAjMDAwMDAwOyBsaW5lLWhlaWdodDogMS4yOyBwb2ludGVyLWV2ZW50czogYWxsOyB3aGl0ZS1zcGFjZTogbm9ybWFsOyB3b3JkLXdyYXA6IG5vcm1hbDsgIj48ZGl2IHN0eWxlPSJjb2xvcjpyZ2IoMzEsIDM1LCA0MSkiPjxwPjxmb250IHN0eWxlPSJmb250LXNpemU6MTJweCI+cGxheWVyLnBhdXNlKCkvcmVzdW1lKCkvc3RvcCgpLi4uPC9mb250PjwvcD48L2Rpdj48Zm9udCBzdHlsZT0iZm9udC1zaXplOjEycHgiPjxzcGFuPjwvc3Bhbj48L2ZvbnQ+PC9kaXY+PC9kaXY+PC9kaXY+PC9mb3JlaWduT2JqZWN0PjwvZz48L2c+PC9zdmc+" from="flow-chart" payload="{&quot;data&quot;:{&quot;mxGraphModel&quot;:{&quot;dx&quot;:&quot;1426&quot;,&quot;dy&quot;:&quot;855&quot;,&quot;grid&quot;:&quot;1&quot;,&quot;gridSize&quot;:&quot;10&quot;,&quot;guides&quot;:&quot;1&quot;,&quot;tooltips&quot;:&quot;1&quot;,&quot;connect&quot;:&quot;1&quot;,&quot;arrows&quot;:&quot;1&quot;,&quot;fold&quot;:&quot;1&quot;,&quot;page&quot;:&quot;1&quot;,&quot;pageScale&quot;:&quot;1&quot;,&quot;pageWidth&quot;:&quot;827&quot;,&quot;pageHeight&quot;:&quot;1169&quot;},&quot;mxCellMap&quot;:{&quot;i1qC4xsJ&quot;:{&quot;id&quot;:&quot;i1qC4xsJ&quot;},&quot;RXGw1PSx&quot;:{&quot;id&quot;:&quot;RXGw1PSx&quot;,&quot;parent&quot;:&quot;i1qC4xsJ&quot;},&quot;2yLGeKez&quot;:{&quot;id&quot;:&quot;2yLGeKez&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;eEVvZXiR&quot;,&quot;target&quot;:&quot;f6nIvn4F&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;eEVvZXiR&quot;:{&quot;id&quot;:&quot;eEVvZXiR&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>Entity.load()/create()</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;gradientColor=none;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;215&quot;,&quot;y&quot;:&quot;120&quot;,&quot;width&quot;:&quot;120&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ZHLgGiBZ&quot;:{&quot;id&quot;:&quot;ZHLgGiBZ&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;f6nIvn4F&quot;,&quot;target&quot;:&quot;LL1VaAjX&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;Nd8mECGe&quot;:{&quot;id&quot;:&quot;Nd8mECGe&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#9933FF;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;f6nIvn4F&quot;,&quot;target&quot;:&quot;9XItvDMt&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;f6nIvn4F&quot;:{&quot;id&quot;:&quot;f6nIvn4F&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>player = C</font><span style=\&quot;background-color:initial\&quot;>ypressMediaPlayer</span><span style=\&quot;background-color:initial\&quot;>()</span></p><p></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;180&quot;,&quot;y&quot;:&quot;190&quot;,&quot;width&quot;:&quot;190&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;7Yu4hVQV&quot;:{&quot;id&quot;:&quot;7Yu4hVQV&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;strokeColor=#9933FF;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;9XItvDMt&quot;,&quot;target&quot;:&quot;fumBn6QV&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;9XItvDMt&quot;:{&quot;id&quot;:&quot;9XItvDMt&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>player.registerPlayerCallBack(callback)</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;480&quot;,&quot;y&quot;:&quot;190&quot;,&quot;width&quot;:&quot;230&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;fumBn6QV&quot;:{&quot;id&quot;:&quot;fumBn6QV&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>player.setDataSource(\&quot;videoPath\&quot;)</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;490&quot;,&quot;y&quot;:&quot;260&quot;,&quot;width&quot;:&quot;210&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;6iGFa9TS&quot;:{&quot;id&quot;:&quot;6iGFa9TS&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;LL1VaAjX&quot;,&quot;target&quot;:&quot;LevruvVE&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;LL1VaAjX&quot;:{&quot;id&quot;:&quot;LL1VaAjX&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>videoMat = VideoMaterial()</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;187.5&quot;,&quot;y&quot;:&quot;260&quot;,&quot;width&quot;:&quot;175&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;E4fKtMk1&quot;:{&quot;id&quot;:&quot;E4fKtMk1&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;LevruvVE&quot;,&quot;target&quot;:&quot;tSbZ1054&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;LevruvVE&quot;:{&quot;id&quot;:&quot;LevruvVE&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>VideoPlayerComponent(player,Entity.mesh,videoMat)</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;120&quot;,&quot;y&quot;:&quot;330&quot;,&quot;width&quot;:&quot;310&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;ITqRglJf&quot;:{&quot;id&quot;:&quot;ITqRglJf&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;tSbZ1054&quot;,&quot;target&quot;:&quot;vsqriArx&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;tSbZ1054&quot;:{&quot;id&quot;:&quot;tSbZ1054&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>add VideoComponent to Entity</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;182.5&quot;,&quot;y&quot;:&quot;400&quot;,&quot;width&quot;:&quot;185&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;b5PYGB3P&quot;:{&quot;id&quot;:&quot;b5PYGB3P&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;vsqriArx&quot;,&quot;target&quot;:&quot;NhP1N37F&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;uaxRHcz9&quot;:{&quot;id&quot;:&quot;uaxRHcz9&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;strokeColor=#9933FF;dashed=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;vsqriArx&quot;,&quot;target&quot;:&quot;8Mnct80F&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;YYOP3npV&quot;:{&quot;id&quot;:&quot;YYOP3npV&quot;,&quot;value&quot;:&quot;&quot;,&quot;style&quot;:&quot;edgeLabel;html=1;align=center;verticalAlign=middle;resizable=0;points=[];&quot;,&quot;parent&quot;:&quot;uaxRHcz9&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;connectable&quot;:&quot;0&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;0.186&quot;,&quot;y&quot;:&quot;-1&quot;,&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;as&quot;:&quot;offset&quot;}}},&quot;vsqriArx&quot;:{&quot;id&quot;:&quot;vsqriArx&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>player.prepareAsync</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;207.5&quot;,&quot;y&quot;:&quot;470&quot;,&quot;width&quot;:&quot;135&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;NhP1N37F&quot;:{&quot;id&quot;:&quot;NhP1N37F&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>end</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;240&quot;,&quot;y&quot;:&quot;555&quot;,&quot;width&quot;:&quot;70&quot;,&quot;height&quot;:&quot;30&quot;,&quot;as&quot;:&quot;geometry&quot;}},&quot;RCXUa5cE&quot;:{&quot;id&quot;:&quot;RCXUa5cE&quot;,&quot;style&quot;:&quot;edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;exitX=0.5;exitY=1;exitDx=0;exitDy=0;strokeColor=#9933FF;dashed=1;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;source&quot;:&quot;8Mnct80F&quot;,&quot;edge&quot;:&quot;1&quot;,&quot;-0-mxGeometry&quot;:{&quot;relative&quot;:&quot;1&quot;,&quot;as&quot;:&quot;geometry&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;280&quot;,&quot;y&quot;:&quot;530&quot;,&quot;as&quot;:&quot;targetPoint&quot;},&quot;-1-Array&quot;:{&quot;as&quot;:&quot;points&quot;,&quot;-0-mxPoint&quot;:{&quot;x&quot;:&quot;595&quot;,&quot;y&quot;:&quot;530&quot;}}}},&quot;8Mnct80F&quot;:{&quot;id&quot;:&quot;8Mnct80F&quot;,&quot;value&quot;:&quot;<div style=\&quot;color:rgb(31, 35, 41)\&quot;><p><font style=\&quot;font-size:12px\&quot;>player.pause()/resume()/stop()...</font></p></div><font style=\&quot;font-size:12px\&quot;><span></span></font>&quot;,&quot;style&quot;:&quot;rounded=1;whiteSpace=wrap;html=1;fillColor=#EBEFF5;&quot;,&quot;parent&quot;:&quot;RXGw1PSx&quot;,&quot;vertex&quot;:&quot;1&quot;,&quot;diagramName&quot;:&quot;RoundedRectangle&quot;,&quot;diagramCategory&quot;:&quot;general&quot;,&quot;-0-mxGeometry&quot;:{&quot;x&quot;:&quot;485&quot;,&quot;y&quot;:&quot;470&quot;,&quot;width&quot;:&quot;220&quot;,&quot;height&quot;:&quot;40&quot;,&quot;as&quot;:&quot;geometry&quot;}}},&quot;mxCellList&quot;:[&quot;i1qC4xsJ&quot;,&quot;RXGw1PSx&quot;,&quot;2yLGeKez&quot;,&quot;eEVvZXiR&quot;,&quot;ZHLgGiBZ&quot;,&quot;Nd8mECGe&quot;,&quot;f6nIvn4F&quot;,&quot;7Yu4hVQV&quot;,&quot;9XItvDMt&quot;,&quot;fumBn6QV&quot;,&quot;6iGFa9TS&quot;,&quot;LL1VaAjX&quot;,&quot;E4fKtMk1&quot;,&quot;LevruvVE&quot;,&quot;ITqRglJf&quot;,&quot;tSbZ1054&quot;,&quot;b5PYGB3P&quot;,&quot;uaxRHcz9&quot;,&quot;YYOP3npV&quot;,&quot;vsqriArx&quot;,&quot;NhP1N37F&quot;,&quot;RCXUa5cE&quot;,&quot;8Mnct80F&quot;]},&quot;lastEditTime&quot;:0,&quot;snapshot&quot;:&quot;&quot;}" />

### Step 1: Create a CypressMediaPlayer instance
Create a `CypressMediaPlayer` instance to configure video playback parameters, including loop mode, volume, and more.
The code sample is as follows:
```Kotlin
class CypressMediaPlayerHelper(ctx: Context, videoPath: String, isAssetPath: Boolean) {

    private var context: Context? = null
    private var videoPath: String = ""
    private var assetPath: String = ""
    private var cypressMediaPlayer: CypressMediaPlayer? = null

    private val callBack =
        object : CypressMediaPlayerCallback {
            override fun onPrepared() {
                cypressMediaPlayer?.apply {
                    play()
                    Log.i(TAG, "onPrepared Event")
                }
            }
            override fun onStarted() {
                Log.i(TAG, "onStarted Event")
            }
            override fun onCompleted() {
                cypressMediaPlayer?.apply { seekTo(0) }
                Log.i(TAG, "onCompleted Event")
            }
            override fun onSeekToCompleted() {
                Log.i(TAG, "onSeekToCompleted Event")
            }
            override fun onPaused() {
                Log.i(TAG, "onPaused Event")
            }
            override fun onStopped() {
                Log.i(TAG, "onStopped Event")
            }
            override fun onVideoSizeChanged(width: Int, height: Int) {
                Log.i(TAG, "onVideoSizeChanged Event")
            }
            override fun onError(error: CypressMediaPlayerErrorCode) {
                Log.i(TAG, "onError code ${error.code}")
            }
        }

    init {
        this.context = ctx
        cypressMediaPlayer = CypressMediaPlayer()
        cypressMediaPlayer!!.registerCypressMediaPlayerCallback(callBack)
        if (isAssetPath) {
            this.assetPath = videoPath
            val afd = this.context!!.assets.openFd(this.assetPath)
            cypressMediaPlayer?.setDataSource(afd)
            afd.close()
        } else {
            this.videoPath = videoPath
            val ret = cypressMediaPlayer!!.setDataSource(this.videoPath)
            Log.i(TAG, "assetPath: $ret")
        }
        Log.i(TAG, "assetPath: $assetPath")
        Log.i(TAG, "videoPath: ${this.videoPath}")
        Log.i(TAG, "isAssetPath: $isAssetPath")
    }

    /** prepareAsync */
    fun start() {
        cypressMediaPlayer?.apply {
            cypressMediaPlayer!!.prepareAsync()
            Log.i(TAG, "prepareAsync pressed")
        }
    }

    /** play */
    fun play() {
        cypressMediaPlayer?.apply { play() }
    }

    /** pause */
    fun pause() {
        cypressMediaPlayer?.apply { pause() }
    }

    /** stop */
    fun stop() {
        cypressMediaPlayer?.apply { stop() }
    }

    /** isPlaying */
    fun isPlaying(): Boolean {
        cypressMediaPlayer?.apply {
            return isPlaying()
        }
        return false
    }

    /** resume */
    fun resume(): Boolean {
        cypressMediaPlayer?.apply {
            return resume()
        }
        return false
    }

    /** setLoop */
    fun setLoop(loop: Boolean): Boolean {
        cypressMediaPlayer?.apply {
            return setLoop(loop)
        }
        return false
    }

    /** setVolume */
    fun setVolume(volume: Float): Boolean {
        cypressMediaPlayer?.apply {
            return setVolume(volume)
        }
        return false
    }
    
    /** get the video frame's width*/
    fun getVideoWidth(): Int {
        cypressMediaPlayer?.apply {
            return getVideoWidth()
         }
         return 0
    }
    
    /** get the video's frame height */
    fun getVideoHeight(): Int {
        cypressMediaPlayer?.apply {
             return getVideoHeight()
        }
        return 0
    }
    
    /** exit */
    fun exit() {
        cypressMediaPlayer?.apply {
            stop()
            unregisterCypressMediaPlayerCallback()
            close()
        }
        if (File(this.videoPath).exists()) {
            File(this.videoPath).delete()
        }
        cypressMediaPlayer = null
    }

    /** Companion */
    companion object {
        /** TAG */
        private const val TAG = "CypressMediaPlayerHelper"
    }
}

val player = CypressMediaPlayerHelper(context, "your_assets_video_path", true)
```

### Step 2: Create a VideoPlayerComponent and add it to the entity
Based on the given `CypressMediaPlayer`, `MeshResource`, and `VideoMaterial`, create a `VideoPlayerComponent` and add it to the `Entity`.

* `MeshResource`: Used to represent the geometry of 3D objects that hold video frames, for example, sphere, hemisphere, cylinder, 3D patch, and many others.
* `VideoMaterial`: Used to hold video frames.

The code sample is as follows:
```Kotlin
// Get the entity's mesh
val entity = Entity()
// Assume the video is 16:9.
val mesh = MeshResource.createVideoPanel(1.6f, 0.9f, 0.1f)
if (mesh.valid) {

    // Create a VideoMaterial
    val videoMat =
        VideoMaterial(
            BlendingMode.OPAQUE,
            VideoDimensionMode.SIDE_BY_SIDE,
            MaterialCullingMode.BACK
        )
    // Create a VideoPlayerComponent
    val videoPlayerComponent = VideoPlayerComponent(player,mesh, videoMat)
    
    // Add the VideoPlayerComponent to the entity
    entity.components.set(videoPlayerComponent)
   }
```

### Step 3: Control video playback
Use the created `CypressMediaPlayer` instance to control video playback.
```Kotlin
// Start playback
player.start()

// Pause playback
player.pause()

// Set up a loop
player.setLoop(true)

// Set volume
player.setVolume(0.5)

......

// Exit and destroy the player
player.exit()
```

### Step 4: Release the CypressMediaPlayer instance
Release the `CypressMediaPlayer` instance when you no longer need to use it to avoid resource leaks.
```Kotlin
player.exit()
```

## Advanced settings
### Update DataSource dynamically
`DataSource` enables dynamic video switching.
When switching videos, first call the `reset()` method to reset the player, and then use `setDataSource()` to set a new `DataSource`.
```Kotlin
// Steps to dynamically update the DataSource
player?.stop() // Stop the currently playing video
player?.reset() // Reset the player
player?.setDataSource("Video/1080p60fps-av1.mp4", true) // Update DataSource
player?.prepare() // Prepare for playback
player?.play() // Start playback
```

### Set video display mode
The `DisplayMode` property is used to set the display mode of `VideoPlayerComponent` when playing 3D videos containing binocular parallax information.
The `DisplayMode` property is used to set the display mode of `VideoComponent` when playing 3D videos containing binocular parallax information.

The available video display modes are as follows:
| **Mode** | **Description** | **Use case** |
| --- | --- | --- |
| `NONE` (default) | Initial display state of video. The video display method is determined by the `VideoDimensionMode` property of the video material. | The player automatically determines the display of video based on the 3D encapsulation format of the video itself. |
| `MONO` | Display the video as a flat image without producing a stereoscopic effect. | When playing 3D videos but stereoscopic vision is not required, stereoscopic videos can be displayed as 2D. |
| `STEREO` | Display the video in a stereoscopic view, presenting a distinct 3D effect. | For 3D videos that require a high level of visual immersion, such as movie playback, virtual cinemas, or interactive experiences. |
You can use the `setDisplayMode` function to set the display mode of `VideoPlayerComponent`:
```Kotlin
Button(
    onClick = {
        entity?.apply {
            if (selectName == R.string.mv_hevc) {
            
                // Ensure the entity has a VideoPlayerComponent attached
                if (entity.components.has(VideoPlayerComponent::class.java)) {
                
                    // Retrieve the VideoPlayerComponent
                    val videoPlayerComponent = entity.components[VideoPlayerComponent::class.java]!!
                    
                    // Switch between MONO and STEREO modes
                    if (displayMode == "MONO") {
                        videoPlayerComponent.setDisplayMode(DisplayMode.MONO)
                        displayMode = "STEREO"
                    } else {
                        videoPlayerComponent.setDisplayMode(DisplayMode.STEREO)
                        displayMode = "MONO"
                    }
                }
            }
        }
    },
    size = IconButtonDefaults.iconButtonSize(200.dp, 36.dp),
    modifier =
        Modifier.width(200.dp).height(36.dp).background(color = Color.Transparent),
) {
    
    // Show the display mode of the current video
    Text(text = displayMode, color = Color.White, fontSize = 25.sp)
}
```

## API reference
The `VideoPlayerComponent` and `CypressMediaPlayer` classes provide properties and functions related to video playback. For more information, refer to the [API Reference](https://developer.picoxr.com/spatial-api/index.html).

