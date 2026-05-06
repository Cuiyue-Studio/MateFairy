This article describes how to install the PICO Spatial Plugin in your Android Studio.
## Environment requirements
### Operating system and hardware requirements
To use the PICO Spatial Plugin and spatial app development tools, your operating system and hardware must meet the following minimum requirements.
| **Operating system** | **Requirements** |
| --- | --- |
| Windows | * **Operating system version**: Windows 10 64-bit or Windows 11 64-bit <br> * **Memory**: 16 GB <br> * **Available disk space**: 40 GB <br> * **CPU**：Intel Core i5 <br> * **GPU**：NVIDIA GeForce GTX 1060 |
| macOS | * **Operating system version**: macOS 14.0 <br> * **Memory**: 16 GB <br> * **Available disk space**: 40 GB <br> * **CPU**: M1 Pro only; Intel chips are not supported. |
### Android Studio version and SDK package requirements
To use the PICO Spatial Plugin in Android Studio, you need to install Android Studio and Android SDK packages that meet the following requirements.
PICO Spatial Plugin only supports **Android Studio 2025.1.x**.

| **Requirements** | **Description** |
| --- | --- |
| Android Studio version | **Android Studio 2025.1.x** |
| Android SDK package | In Android Studio, go to **Settings** > **Languages & Frameworks** > **Android SDK** > **SDK Platforms**, then check the **Show Package Details** option in the lower right corner, and install **Android 15.0 ("VanillaIceCream)** with **Android SDK Platform 35** and **Sources for Android 35**. |
## Procedures
### Step 1: Install Android Studio
Before installing the PICO Spatial Plugin, you need to install the specified Android Studio and Android SDK packages. For details, see [Android Studio version and SDK package requirements](/editor/en_install-spatial-plugin).
* If you have already installed the specified version of Android Studio and downloaded the corresponding Android SDK package, skip this step.
* To install a second Android Studio on the same computer when a non-specified version is already installed on your system, it is recommended to use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/) for installation and management.


1. Go to the [Android Studio download archive page](https://developer.android.google.cn/studio/archive).
2. Switch the page display language to **English**, read the terms and conditions, then click the **I agree to the terms** button at the bottom of the page.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/65738d09e3aa4b0dac959414254e1499~tplv-goo7wpa0wc-image.image)
   The website will display a list of download files for the Android Studio archive.
3. Find any version of **2025.1.1, 2025.1.2, 2025.1.3, or 2025.1.4**, download the corresponding installer for your operating system, and complete the installation.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/359cf2bce8c8482a9cdcca77f5daa2c3~tplv-goo7wpa0wc-image.image)
4. Open Android Studio, then follow the setup wizard to complete the initial configuration.
5. Go to **Settings** > **Languages & Frameworks** > **Android SDK** > **SDK Platforms**, then check the **Show Package Details** option in the lower right corner.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bb56b1bd8c464b7d8d474e5fa95cc422~tplv-goo7wpa0wc-image.image)
   The **SDK Platform** tab displays the packages included in each Android SDK version and their installation status.
6. Expand the **Android 15.0 ("VanillaIceCream)** directory and check whether **Android SDK Platform 35** and **Sources for Android 35** are installed. If they are not installed, check these two options, then click the **OK** button in the lower right corner to install them.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aea9364465c0423590335aac4aacd303~tplv-goo7wpa0wc-image.image)

### Step 2: Install PICO Spatial Plugin

1. Download the Spatial Plugin .zip package from the [PICO developer official website](https://developer.picoxr.com/resources/?platform=spatial). Do not extract the .zip package.
   Currently, the PICO Spatial Plugin only supports offline installation. In the future, it will support downloading and installing directly from the IntelliJ plugin marketplace.

2. Open Android Studio.
3. Go to **Plugins** > **gear icon** > **Install Plugin from Disk...**, then import the PICO Spatial Plugin .zip package.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/ea89185820eb4f589e04bbc86aa9b569~tplv-goo7wpa0wc-image.image)
   After the import is complete, PICO Spatial Plugin will appear in the Plugin list.
4. Click the **Restart IDE** button, then in the confirmation window that pops up, click **Restart**  to restart Android Studio.

After Android Studio restarts, the PICO Spatial Plugin will be successfully installed in your Android Studio.

