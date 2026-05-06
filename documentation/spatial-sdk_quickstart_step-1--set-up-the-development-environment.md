Before developing spatial apps with the PICO Spatial SDK, check whether your system environment meets the requirements and install the necessary development tools.
## System requirements
Ensure that your operating system meets the following minimum requirements:
| **Operating system** | **Requirements** |
| --- | --- |
| Windows | * **Operating system version**: Windows 10 64-bit or Windows 11 64-bit <br> * **Memory**: 16 GB <br> * **Available disk space**: 40 GB <br> * **CPU**: Intel Core i5 <br> * **GPU**: NVIDIA GeForce GTX 1060 |
| macOS | * **Operating system version**: macOS 14.0 <br> * **Memory**: 16 GB <br> * **Available disk space**: 40 GB <br> * **CPU**: M1 Pro, Intel chips are not supported. |
## Step 1: Install Android Studio
Install the specified version of Android Studio, Android platform, and Android source code.

1. Go to the [Android Studio download archive page](https://developer.android.google.cn/studio/archive?hl=en).
2. Switch the display language of the page to **English**, read the terms and conditions, then click the **I agree to the terms** button at the bottom of the page.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/65738d09e3aa4b0dac959414254e1499~tplv-goo7wpa0wc-image.image)
   The page displays a list of Android Studio release versions.
3. Find any **2025.1.x**  version, download the corresponding installer for your operating system, and complete the installation.
   Currently, only the **2025.1.x**  version is supported. Do not install other versions.

   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/cca9e7a0fd824df5af58b44dbf4e1911~tplv-goo7wpa0wc-image.image)
   If a version of Android Studio other than the one above is already installed on your system and you need to install a second Android Studio on the same computer, it is recommended to use JetBrains Toolbox for installation and management.
   1. Go to the [JetBrains Toolbox official website](https://www.jetbrains.com/toolbox-app/), download and install JetBrains Toolbox.
   2. Open the installed JetBrains Toolbox application.
   3. In the **Tools** tab, under the **Installed** list, find the installed Android Studio, click the **More** button on the right, then select **Other versions** from the menu.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3f24de2cfa2d45ff972e2cd73aa4f8e8~tplv-goo7wpa0wc-image.image)
   4. In the **Android Studio versions** list, find the **2025.1.x** version, then click the **Install** button.
      ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/e84409a9cf894cc38875e63dc88fb72e~tplv-goo7wpa0wc-image.image)
   5. After installation is complete, return to the **Tools** tab, find the Android Studio version installed in the previous step in the **Installed** list, then double-click to launch it.
4. Launch Android Studio, then follow the setup wizard to complete the initial configuration.
   The **Welcome to Android Studio** window appears on the interface.
5. Go to **Android Studio** > **Settings** > **Languages & Frameworks** > **Android SDK** > **SDK Platforms**, then check the **Show Package Details** option in the lower right corner.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/bb56b1bd8c464b7d8d474e5fa95cc422~tplv-goo7wpa0wc-image.image)
   The **SDK Platform** tab displays the resources and installation status of each Android SDK Platform package.
6. Expand the **Android 15.0 ("vanillaIceCream)** directory and check whether **Android SDK Platform 35** and **Sources for Android 35** are installed. If they are not installed, check these two options, then click the **OK** button in the lower right corner to install them.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/aea9364465c0423590335aac4aacd303~tplv-goo7wpa0wc-image.image)

## Step 2: Install PICO Spatial Plugin

1. Download the .zip package of PICO Spatial Plugin from the [PICO developer official website](https://developer.picoxr.com/resources/?platform=spatial) (do not unzip).
2. Open Android Studio.
3. Go to **Plugins** > **gear icon** > **Install Plugin from Disk...**, then import the .zip package of PICO Spatial Plugin.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/5a44dacb3fac43b3af7a263bf0c0a8f7~tplv-goo7wpa0wc-image.image)
4. Click the **Restart IDE** button to restart Android Studio.

## Step 3: Install PICO Emulator and Spatial Editor

1. Go to **Android Studio** > **Settings** > **Languages & Frameworks** > **PICO Spatial Tools**.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/7921eea5279e4aa4828e5a8471cbd9a6~tplv-goo7wpa0wc-image.image)
2. In the **PICO Spatial Tools** tab:
   1. Check the **Show Package Details** checkbox in the lower-right corner.
      Spatial Editor and PICO Emulator appear in the above tool list.
   2. Check the **Spatial Editor** and **PICO Emulator** checkboxes.
   3. Click the **OK** button to install them.
   ![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/62a6ca59690a4faf89369ee7772955b8~tplv-goo7wpa0wc-image.image)



