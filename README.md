# kmp-plugin-ios-sim-fixes

Java bytecode patch for JetBrains IDEs (Android Studio, IntelliJ IDEA, AppCode) resolving iOS simulator discovery failures when working with newer Xcode versions and multi-runtime setups.

## What it fixes

1. **`SimulatorDeviceType.intFromNumberOrString` Null-Pointer Crash**
   - Newer Xcode toolchains can emit null or missing numeric fields when querying simulator descriptors. The patch intercepts `intFromNumberOrString` and returns 0 when null is encountered.

2. **Older Simulator Runtimes Filter Bypass**
   - JetBrains CIDR integration filters out simulators whose OS version does not match the active Xcode SDK index (e.g. iOS 26.5 simulators on Xcode 27). The patch bypasses `filterRuntimesWithAvailablePlatforms` so all installed and booted simulator devices remain available in the IDE run target selector.

## Requirements

- JDK 17+

## Build and Test

```sh
./gradlew test
./gradlew jar
```

Generated jar:
```
build/libs/kmp-plugin-ios-sim-fixes.jar
```

## IDE Installation

1. Open **Help -> Edit Custom VM Options...** in Android Studio or IntelliJ IDEA.
2. Add the path (absolute path required):
   ```
   -javaagent:/path/to/kmp-plugin-ios-sim-fixes/build/libs/kmp-plugin-ios-sim-fixes.jar
   ```
3. Restart the IDE.

## Uninstallation

Remove the `-javaagent:` line from custom VM options and restart the IDE.
