In spatial apps, audio resources are essential elements for creating immersive experiences. They are responsible not only for playing background music, environmental sound effects, and interactive sound effects, but also for enhancing the sense of presence through spatial positioning. Proper management of audio resources is essential for better app performance and user experience.
## Format requirements
The audio files used in PICO Spatial SDK must comply with the specified file format, encoding format, and channel layout to ensure they can be correctly loaded and played.
### File format
The supported audio file formats are as follows:
| **File format** | **Description** | **Use cases** |
| --- | --- | --- |
| .wav / .wave | Common lossless audio files | High-fidelity audio effects and asset storage |
| .mp3 | Lossy compression audio formats | Background music and voice audio |
| .aac | Efficient compressed audio format | Streaming playback and real-time communication |
| .wma | Windows Media Audio | Legacy audio library compatibility |
| .amr | Commonly used for voice recording | Voice message, low-bitrate audio |
| .ogg / .ogv | Open-source audio container format | Open-source sound effects in games and apps |
| .pcm | Raw PCM data | Development and debugging, customization of audio processing |
| .flac | Lossless compressed audio | High-quality sound effects and music appreciation |
| .opus | Efficient low-latency audio encoding | RTC, interactive scenarios |
| .mkv | A Matroska container which can contain audio and video | Video playback, multimedia content |
| .webm | Web media formats | Streaming media: WebXR, online video streaming |
### Encoding format
Supports PCM encoding, commonly used for an engine's underlying processing and development debugging.
### Channel layout
Supported channel layouts are as follows:
| **Enumeration value** | **Description** | **Use cases** |
| --- | --- | --- |
| OutputLayout_Mono | Mono layout | Simple voice prompts, single-point audio sources |
| OutputLayout_Stereo | Stereo layout | Music playback, standard videos |
| OutputLayout_Quad | Four-channel layout | Ambient sound effects, surrounding spatial audio |
| OutputLayout_QuadSide | Four-channel side layout | Immersive environment simulation |
| OutputLayout_5_1 | 5.1 ambisonics | Cinema mode and immersive experience |
| OutputLayout_6_1 | 6.1 ambisonics | Precise positioning of sound effects in games |
| OutputLayout_7_1 | 7.1 ambisonics | High-end cinema, VR panoramic sound |
| OutputLayout_5_1_2 | 5.1.2 spatial channel layout (including the height channel) | VR/AR spatial audio effects and 3D positioning |
## Load audio resources
PICO Spatial SDK supports loading audio files from a specified path, URI, or AssetBundle.
### Load from a specified path
Specify a custom audio name, the path to the audio file, and the loading type. Use the following interface to load audio files from either the /assets directory or the device's file system (the corresponding loading types are `LoadType.FROM_ASSETS` and `LoadType.FROM_STORAGE`):
```Kotlin
fun load(name: String, path: String, loadType: LoadType = LoadType.FROM_ASSETS): AudioResource
```

File paths must meet the following requirements:

* If the loading type is `LoadType.FROM_ASSETS`, the file path must be relative to the /assets directory.
* If the loading type is `LoadType.FROM_STORAGE`, the file path must be the absolute path to the file in device storage.

The code samples are as follows:
Load the audio file from the /assets/audio/your_custom_audio.wav file:
```Kotlin
fun loadAudioFromAsset() {
    val subFolderName = "audio"
    val fileName = "your_custom_audio.wav"
    val audioName = "YourCustomAudioName"
    // Load audio from the /assets directory
    val audioFromAssets =
        AudioResource.load(
            name = audioName,
            path = "${subFolderName}/${fileName}",
            loadType = LoadType.FROM_ASSETS
        )
}
```

Load the audio file from device storage using the absolute file path:
```Kotlin
fun loadAudioFromStorageViaAbsolutePath(context: Context) {
    val subFolderName = "audio"
    val fileName = "your_custom_audio.wav"
    val audioName = "YourCustomAudioName"
    // Copy the file from the /assets directory to device storage
    val temFile = File(context.filesDir, fileName)
    context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
        FileOutputStream(temFile).use { outputStream ->
            inputStream.copyTo(outputStream)
            outputStream.flush()
        }
    }
    // Load the audio from the file's absolute path
    val audioFromFilePath =
        AudioResource.load(
            name = audioName,
            path = temFile.absolutePath,
            loadType = LoadType.FROM_STORAGE
        )
}
```

### Load from a specified URI
Specify a custom audio name, the URI of the audio file, and the context, and use the following interface to load the audio file from the specified URI. Supported URI schemes include `file://`, `content://`, and `android.resource://`.
```Kotlin
fun load(name: String, uri: Uri, context: Context): AudioResource
```

Sample codes are as follows:

* Load the audio file from device storage via the `file://` URI:
   ```Kotlin
   fun loadAudioFromStorageViaFileUri(context: Context) {
       val subFolderName = "audio"
       val fileName = "your_custom_audio.wav"
       val audioName = "YourCustomAudioName"
       // Copy the file from the /assets directory to device storage
       val temFile = File(context.filesDir, fileName)
       context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
           FileOutputStream(temFile).use { outputStream ->
               inputStream.copyTo(outputStream)
               outputStream.flush()
           }
       }
       // Load the audio from the file URI
       val uri = Uri.fromFile(temFile)
       val audioFromFileUri = AudioResource.load(name = audioName, uri = uri, context = context)
   }
   ```

* Load the audio file using the `content://` URI:
   ```Kotlin
   fun loadAudioFromContentUri(context: Context) {
       val subFolderName = "audio"
       val fileName = "your_custom_audio.wav"
       val audioName = "YourCustomAudioName"
       // Copy the file from the /assets directory to device storage
       val temFile = File(context.filesDir, fileName)
       context.assets.open("${subFolderName}/${fileName}").use { inputStream ->
           FileOutputStream(temFile).use { outputStream ->
               inputStream.copyTo(outputStream)
               outputStream.flush()
           }
       }
       // Load the audio file from content's URI
       val uri = Uri.parse("content://${context.packageName}.youraudioprovider/$fileName")
       val audioFromContentUri = AudioResource.load(name = audioName, uri = uri, context = context)
   }
   ```

* Load the audio file via `android.resource://` URI:
   ```Kotlin
   fun loadAudioFromAndroidResourceUri(context: Context) {
       val audioName = "YourCustomAudioName"
       val resId = R.raw.your_custom_audio
       // Load the audio file from the URI of AndroidResource
       val uri = Uri.parse("android.resource://${context.packageName}/${resId}")
       val audioFromAndroidResourceUri =
           AudioResource.load(name = audioName, uri = uri, context = context)
   }
   ```


### Load from an AssetBundle
For information on how to directly use an AssetBundle instance to load audio files in the Spatial Editor project, refer to "[AssetBundle](/en_asset-bundle)".
## Use audio resources
After successfully loading `AudioResource`, you can preprocess the audio using the `entity.prepareAudio(audioResource: AudioResource)` method, such as decoding or buffering, to optimize playback performance. After preprocessing is complete, call `entity.playAudio(audioResource: AudioResource)` to start audio playback.
Each of the methods above returns an `AudioPlayerController` instance, which provides fine-grained control over audio playback, including functions such as `play`, `pause`, `resume`, `seekTo`, `setLoop`, and more.
Note that `AudioPlayerController` inherits from the `Closable` interface and is a resource that requires explicit release. To avoid memory leaks or excessive resource usage, it is recommended to explicitly call `audioPlayerController.close()` and `audioResource.close()` to release resources after use.
## Manage audio resources
You can use `AudioResourceLibraryComponent` to batch manage multiple audio resources. `AudioResourceLibraryComponent` manages audio resources in the form of a dictionary, allowing the name to be used as the key to add, remove, retrieve, and clear audio resources. These keys can be used in code or timeline operations to enable subsequent audio playback. The functions contained in `AudioResourceLibraryComponent` are as follows:
| **Function** | **Description** |
| --- | --- |
| add | Adds the `AudioResource` with the specified name to the `AudioResourceLibraryComponent`. |
| remove | Removes the `AudioResource` with the specified name from the `AudioResourceLibraryComponent`. |
| get | Gets an `AudioResource` with a specified name from `AudioResourceLibraryComponent`. |
| contains | Checks whether `AudioResourceLibraryComponent` contains an `AudioResource` with the specified name. |
| getAllNames | Gets the names of all `AudioResource` in `AudioResourceLibraryComponent` |
| getAllAudioResources | Gets all `AudioResources` in `AudioResourceLibraryComponent`. |
| clear | Clears all `AudioResource` in `AudioResourceLibraryComponent`. |
## Restrictions on simultaneous playback

* **Maximum simultaneous playback count**: 39 audio sources.
* **Over-limit behavior**: Due to limitations of the Android platform, the 40th and subsequent audio sources will not be played.
* **Editor behavior**: In Spatial Editor, if more than 39 audio sources are selected, the system will dynamically switch audio sources to ensure that no more than 39 audio sources are played simultaneously.


