This document includes frequently asked questions about PICO Emulator.
## How to distinguish between the PICO Emulator and a real device environment?
You can determine whether the current environment is a PICO Emulator environment by reading the Android system property `ro.boot.qemu`.
`ro.boot.qemu` is a **system property key** in Android used to identify the emulator environment, and its value is stored in the build.prop configuration file.


* If the property value is `1`, the current environment is a PICO Emulator environment.
* If the property value is `0`, the current environment is a real device environment.

**Java code example**
Do not use the `System.getProperty()` method to read the system property `ro.boot.qemu`. This method is used to obtain JVM properties (Java Virtual Machine configuration parameters), rather than Android system properties (such as `ro.boot.qemu` defined in the build.prop configuration file). You need to use reflection to call `SystemProperties.get()` to read Android system properties.

```Java
    try {
        // Use reflection to call the SystemProperties.get() method
        Class<?> systemProperties = Class.forName("android.os.SystemProperties");
        Method getMethod = systemProperties.getMethod("get", String.class);
        String propertyValue = (String) getMethod.invoke(null, "ro.boot.qemu");
        Log.d(TAG, "Properties from the Java layer: ro.boot.qemu=" + propertyValue);
    } catch (Exception e) {
        Log.e(TAG, "Exception in reading properties from the Java layer: " + e.getMessage());
    }
```

**C++ code example**
```C++
#include <sys/system_properties.h>
// Use the __system_property_get() function
     char value[10];
    __system_property_get("ro.boot.qemu", value);
}
```




#### 
