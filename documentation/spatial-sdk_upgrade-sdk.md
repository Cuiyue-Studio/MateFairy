After downloading the 0.11 companion tools (Spatial Editor and PICO Emulator) from the Settings interface, you need to update your PICO Spatial SDK version. Please modify your configuration as follows:

1. **Update the PICO Spatial SDK BOM version**
   In `gradle/libs.versions.toml`, update the BOM version for `"com.pico.spatial:bom"` to `0.11.7`:
   ```TOML
   [versions]
   spatialBom = "0.11.7"
   ```

   If you are not using version catalogs, update the BOM version directly in the `dependencies {}` block of your module-level `build.gradle.kts`.


**2.** **Update Android Gradle Plugin (AGP)**

   In `gradle/libs.versions.toml`, set `agp` to `8.8.0` or higher:
   ```TOML
   [versions]
   agp = "8.8.0"
   ```


**3.** **Update Kotlin and Compose Compiler**

   Ensure your Kotlin version is `2.0.0` and update your Compose Compiler dependency accordingly. Refer to the [Compose Compiler documentation](https://developer.android.com/develop/ui/compose/compiler) for the correct version mapping.

**4.** **Update SpatialTools version**

   If you use the `editor-asset` module, update the `'com.pico.spatial.tools'` plugin and `spatialToolsVersion` in your `editor-asset` module-level `build.gradle`:
   ```Groovy
   plugins {
       ...
       id 'com.pico.spatial.tools' version '0.11.5'
   }
   
   spatial {
       name = "editor-asset"
       spatialToolsVersion = 0.11
   }
   ```

