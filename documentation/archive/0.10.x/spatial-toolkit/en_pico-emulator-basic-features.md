This article describes how to run Spatial projects in PICO Emulator, install .apk files in PICO Emulator, and push .obb files or .bundle files to spatial apps in PICO Emulator.
## Run Spatial projects in PICO Emulator
In Android Studio, click the **Run** or **Debug** button to build your Spatial project and install it to run in PICO Emulator.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6dd556684dbf4367a9fca6bccb94fb07~tplv-goo7wpa0wc-image.image)
## Install .apk files to PICO Emulator
If you have a built .apk file, you can drag and drop the .apk file directly onto the PICO Emulator interface. PICO Emulator will automatically install the .apk file.
Additionally, you can also install using the command line. You need to ensure that PICO Emulator is started and can be recognized by the `adb devices` command. The specific steps are as follows:

1. Execute the `adb install <.apk file path>` command, for example `adb install /Users/xxx/Downloads/app-release.apk`.
2. If the terminal outputs `Success`, the installation is successful and the app will automatically appear in the PICO Emulator app list.

## Push .obb files to spatial apps in PICO Emulator
When a spatial app contains large resource files (such as high-definition textures, 3D models, and more), these resources are usually separated into .obb files instead of being directly packaged in the APK. To run such apps in PICO Emulator, manually push the .obb file to the specified directory in the emulator to ensure the spatial app can load resources correctly.
Refer to the following steps to push .obb files to spatial apps in PICO Emulator:

1. Rename the .obb file in the format `main.<version>.<package name>.obb`. For example, `main.100.com.PicoGame.GameName.obb`.
   The version number and package name in the filename must be consistent with the relevant information in the application's .apk file.

2. Use the `adb push` command to push the renamed .obb file to the `/storage/emulated/0/Android/obb/{package name}` directory. If the directory does not exist, execute the `adb shell mkdir -p /storage/emulated/0/Android/obb/{package name}` command to create it.
3. Start the spatial app in PICO Emulator and verify whether the resources corresponding to the .obb file are loaded correctly.

## Push .bundle files to spatial apps in PICO Emulator
.bundle files can be used for modular resource management. You can also push .bundle files directly to spatial apps in PICO Emulator.
Refer to the following steps to push .bundle files to spatial apps in PICO Emulator:

1. Use the `adb push` command to push the .bundle file to the `/storage/emulated/0/Android/obb/{package name}` directory. If the directory does not exist, execute the `adb shell mkdir -p /storage/emulated/0/Android/obb/{package name}` command to create it.
2. Start the spatial app in PICO Emulator and verify whether the resources corresponding to the .bundle file are loaded correctly.
   If the application fails to start (for example, if the PICO Emulator interface remains black), check the `/storage/emulated/0/Android/obb/{package name}` directory to ensure that all .bundle files are correctly placed.




