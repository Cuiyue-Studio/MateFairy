In spatial apps, video resources are important elements for creating dynamic visual effects and immersive scenes. By properly controlling the loading of videos, you can ensure that the scene runs smoothly and avoid excessive memory usage or resource leaks.
## Format requirements
Video files used in the PICO Spatial SDK must comply with the specified file format and encoding format to ensure they can be loaded and played correctly.
### File format
Supported video file formats:
| **Format** | **Description** | **Use cases** |
| --- | --- | --- |
| .mp4 | The most common MPEG-4 container format | Various types of video playback and streaming media |
| .mov | QuickTime file format | Record video on iOS and macOS |
| .m4a | MPEG-4-specific audio format | Music or audio stream |
| .3gp | Mobile 3GPP file formats | Videos from old mobile phones, low-bitrate scenarios |
| .mj2 | Motion JPEG 2000 | High-quality archives and special images |
| .mkv | Open source multimedia container | HD videos and external subtitles |
| .webm | A Matroska-based web video container | WebXR and online video streaming |
| .ts | MPEG transport stream | Digital television broadcasting and streaming media |
| .m2ts | The transport stream format used by Blu-ray discs | Blu-ray high-definition video |
| .flv | Flash video format | Historical web video formats |
| .asf | Advanced system format | Windows media streaming |
| .wmv | Windows media video | Videos compatible Windows platforms |
| .vob | DVD video object file | DVD video playback |
| .avi | Audio-video interleaved format | Common video containers in the early days |
| .mpg / .mpeg | MPEG program stream format | VCD/DVD and legacy video archives |
| .m2p | MPEG-2 program stream format | Broadcast and video distribution |
### Encoding format
The supported video encoding formats are as follows:
| **Encoding format** | **Description** | **Use cases** |
| --- | --- | --- |
| avc (H.264) | A highly compatible video encoding standard | Mainstream video playback and live streaming |
| hevc (H.265) | Efficient video encoding, higher compression ratio | High-definition, 4K, and 8K video |
| av1 | Efficient open-source video encoding | Next-generation streaming media (YouTube/Netflix) |
| vp9 | Video encoding developed by Google | WebM, YouTube HD |
| vp8 | Encoding standard preceding VP9 | WebRTC, legacy WebM |
| h263 | Early video encodings | Mobile video calling (old standard) |
| mpeg4 | MPEG-4 Part 2 encoding | Legacy video files, compatibility-oriented scenarios |
## Load video files
If you choose to use the CypressMediaPlayer provided by the PICO Spatial SDK, when loading a video file, you need to use the `context. `*`assets`*`.openFd` method to obtain the `AssetFileDescriptor` for the video file, and then pass it to the `cypressMediaPlayer.setDataSource(assetFileDescriptor)` function to set the video data source.
```Kotlin
fun setupVideoPlayer(context: Context) {
    val player = CypressMediaPlayer()
    val callBack =
        object : CypressMediaPlayerCallback {
            override fun onPrepared() {
                // Custom logic executed after the video is ready
            }
            override fun onStarted() {
                // Custom logic executed after the video starts playing
            }
            override fun onCompleted() {
                // Custom logic executed after the video finishes playing
            }
            override fun onSeekToCompleted() {
                // Custom logic executed after the video jumps to the end
            }
            override fun onUnknown() {
                // Custom logic executed after the video status is unknown
            }
            override fun onError() {
                // Custom logic executed after a video error occurs
            }
            override fun onFormatChanged() {
                // Custom logic executed after the video format changes
            }
        }
    val subFolder = "video"
    val fileName = "your_custom_video.mp4"
    player.registerCypressMediaPlayerCallback(callBack)
    context.assets.openFd("asset://${subFolder}/${fileName}").use { assetFileDescriptor ->
        player.setDataSource(assetFileDescriptor)
    }
    player.prepareAsync()
    // Other operations...
}
```

