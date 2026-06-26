# Debug Session: pico-emulator-launch

Status: [OPEN]
Created: 2026-06-22

## Symptom

PICO Emulator 0.12 still appears to fail to start or cannot keep running after the previous compile fix.

## Initial Evidence

- `adb devices -l` currently returns no connected devices.
- Android Studio log shows the latest PICO 0.12 run did start and boot far enough to deploy:
  - APK installed from `/Users/bytedance/MateFairy/app/build/intermediates/apk/debug/app-debug.apk`.
  - `am start` launched `com.example.matefairy01/.platform.LaunchActivity`.
  - Later log shows `FINISH_SYSTEM_SYNC`, `EmulatorQtWindow::slot_requestClose`, then device `OFFLINE` and `DISCONNECTED`.

## Hypotheses

1. H1: App process crashes shortly after `LaunchActivity`, and PICO Emulator closes as a secondary effect.
2. H2: PICO Emulator 0.12 completes system sync then auto-closes due to emulator/plugin state, independent of app code.
3. H3: The emulator is being closed by user/IDE/device manager action after launch, not by app crash.
4. H4: Deployment succeeds but Stage initialization fails or blocks, making the app look like it did not start while the emulator remains unstable.
5. H5: PICO 0.12 emulator runtime/cache state is corrupted, causing shutdown after boot and deploy.

## Next Evidence To Collect

- Android Studio/PICO emulator log segments before and after latest `FINISH_SYSTEM_SYNC`.
- Android emulator/CrashReporter/DiagnosticReports entries around the same timestamp.
- If the emulator can be relaunched, `adb logcat` filtered for `AndroidRuntime`, `FATAL EXCEPTION`, `com.example.matefairy01`, and PICO Spatial tags.

## Evidence Collected

### 2026-06-22 19:07

Manual launch with the correct PICO AVD environment reproduced the emulator-side failure before app install:

```text
ERROR | A snapshot operation for 'PICO_0.12' is pending and timeout has expired. Exiting...
```

`-no-snapshot-load -no-snapshot-save` did not bypass this early pending-snapshot check.

### 2026-06-22 19:13

Launching with `-wipe-data` bypassed the pending snapshot state and booted successfully:

```text
Boot completed in 43117 ms
```

ADB confirmed the emulator is online:

```text
emulator-5554 device product:swan model:SDK_Spaceos_built_for_arm64 device:pico_spaceos_emulator_arm64
```

### App Verification

Installed current debug APK with `adb install -t -r` and launched `LaunchActivity`.

```text
Success
Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.example.matefairy01/.platform.LaunchActivity }
```

Process/activity state confirms the app is running:

```text
4949:com.example.matefairy01 ... (top-activity)
mFocusedApp=ActivityRecord{... com.example.matefairy01/.platform.LaunchActivity ...}
```

No `FATAL EXCEPTION`, `AndroidRuntime` crash, or ANR was observed in the filtered startup log. Remaining logs are Spatial runtime/performance warnings and emulator MoltenVK pipeline compile warnings.

## Current Conclusion

- H1 App process crash: rejected by evidence. App process is alive and top activity.
- H2 Emulator/plugin state issue: confirmed. Pending snapshot state blocked startup.
- H3 User/IDE close: not needed to explain the reproduced CLI failure.
- H4 Stage init hard failure: rejected as a startup blocker; app renders enough for Spatial watchdog to track it.
- H5 PICO 0.12 runtime/cache state corrupted: confirmed at least for snapshot state.

## Applied Environment Fix

Ran PICO Emulator 0.12 with `-wipe-data`, which reset the emulator user state and cleared the pending snapshot blocker. This removed installed apps/state inside the PICO emulator, but did not change project source code.
