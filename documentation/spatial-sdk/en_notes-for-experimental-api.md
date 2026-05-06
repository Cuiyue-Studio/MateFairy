The PICO Spatial SDK provides several experimental APIs for developers to try out and test new features. These APIs are still in the development and validation phase, so before using them, be sure to understand and follow the cautions below.
## Definition and risks
Experimental APIs refer to interfaces that have not been officially released and are still undergoing rapid iteration. Using them means you need to accept the following potential risks:

* **Functionality is not stable**: The functionality of experimental APIs may have defects or be incomplete, and their behavior may differ from the final official version.
* **No guarantee of backward compatibility**: We do not guarantee backward compatibility for experimental APIs. In future SDK versions, their interfaces, parameters, or behaviors may undergo significant changes, which may require you to refactor your app's code to adapt.
* **May be removed or modified**: Any experimental API may be significantly modified or even directly removed in future versions, and support will no longer be provided at that time.

## Enable experimental APIs
To use experimental APIs in your project, you must add the following `<meta-data>` configuration inside the `<application>` tag of your app's AndroidManifest.xml file:
```xml
<meta-data
    android:name="pico.spatial.use_experimental_api"
    android:value="1" />
```

## PICO Store listing restrictions
Please pay special attention: **any app that uses experimental APIs cannot be listed on the PICO Store**.
When submitting your app for review, the PICO Store's review system will check whether the app contains the `pico.spatial.use_experimental_api` flag. If this flag is detected, your app submission will be rejected.
Therefore, experimental APIs are only for development, testing, and prototype validation purposes. Do not use them in official versions of apps intended for release and listing.
