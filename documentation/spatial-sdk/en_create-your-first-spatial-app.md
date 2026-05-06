This article introduces how to create and configure a Spatial project in Android Studio.
## Prerequisites
You have already installed Android Studio and Spatial Plugin. For more information, refer to [Set up the development environment](/set-up-development-environment).
## Step 1: Create a spatial project

1. Open Android Studio, then click the **New Spatial Project** button, or click **File** > **New** > **New Spatial Project...** in the top menu bar.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/42d0f1fb1424405ca3ec47ef07f4e8d1~tplv-goo7wpa0wc-image.image)
   The **New Project** window appears.
2. Select the **Spatial WindowContainer** template (or others), then click the **Next** button.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/815f8bd7419e467fbeb321afdcfab28a~tplv-goo7wpa0wc-image.image)
3. Set the project name, package name, save location, and minimum PICO OS SDK version (no higher than API 35), then click the **Finish** button.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/98003672936d42389fc1bf43f1ecf9a3~tplv-goo7wpa0wc-image.image)
   You will enter the following project edit window.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/69d3747760df434485c20476a7d46bb9~tplv-goo7wpa0wc-image.image)
   It is not recommended to modify the default configuration in newly created projects, as this may cause abnormal operation.


## Step 2: Check the SDK version number
Open the libs.versions.toml file, and check the SDK version number in the `[versions]` section. The latest 0.10.7 version is recommended.
```TOML
[versions]
// ...
bom = "0.10.7"
```

## Step 3: Configure NDK (Windows only)
If you are using the Windows operating system, in the module-level build.gradle.kts script file in the `/project/module` directory, add the following NDK configuration in the `defaultConfig {}` section within `android {}`:
```Kotlin
android {
    // ...
    defaultConfig {
        // ...
        ndk { abiFilters.add("arm64-v8a") }
    }
}
```

## Step 4: Run and view
After creating a Spatial project, you can run the project and see how it works on PICO Emulator or a PICO device.
### PICO Emulator
To run the project and view its effect without an available PICO device, use PICO Emulator.

1. Create a new virtual device:
   1. In the right sidebar of Android Studio, click **Device Manager**.
   2. Select **Create PICO Emulator** to create a new PICO Emulator.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/a59fde35efef487c8318e49c3e84751f~tplv-goo7wpa0wc-image.image)
2. Click this PICO Emulator's **Start**  button to launch it.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/4ef13c73fa0c499e810b73f41f699bb7~tplv-goo7wpa0wc-image.image)
3. Click the **Run** button in the top toolbar of Android Studio.
   <div style="text-align: baseline">Android Studio will launch PICO Emulator, build the corresponding module’s <code>.apk</code> file, and install it onto the emulator. PICO Emulator will then automatically run the module and display its scene.   </div>

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6eeb2d60dfb64c57aa3cb4431b0087c9~tplv-goo7wpa0wc-image.image)
   Below is the expected result:
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/efebe0a16e114a2a910f0b92a49679ea~tplv-goo7wpa0wc-image.image)

### PICO device
Android Studio will automatically recognize the connected PICO Emulator and PICO devices, and display them in the **Running devices** list.

1. In the **Running devices** list, select the device that needs to run the project. For example, in the following diagram: "PICO" refers to the PICO Emulator, and "Pico xx" refers to a PICO device.
2. Select the modules to run.
3. Click the **Run** button.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7f50c990e9d94597b8cd6e378407fc18~tplv-goo7wpa0wc-image.image)
   Android Studio packages the corresponding module as an .apk file and installs it on the selected device. The device will automatically run this module and display its scenes.



