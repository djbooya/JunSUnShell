# Release Notes

## 1.0.0 — 2026-10-09

First release of this fork of [TorstenH82/JunSUnShell](https://github.com/TorstenH82/JunSUnShell), based on upstream 1.4.

### Changed
- **Builds with current Android tooling.** The build now runs on JDK 21, the JDK that comes with current Android Studio.
  - Gradle 7.4.2 → 8.9
  - Android Gradle Plugin 7.2.1 → 8.7.3
  - compileSdk 32 → 35, build tools 33.0.0 → 35.0.1
  - The app namespace moved from `AndroidManifest.xml` (`package=`) to `app/build.gradle` (`namespace`), as AGP 8 requires.
- **Builds outside AndroidIDE.** Upstream calls `LogSender`, which only exists inside AndroidIDE builds. The call was removed, so the project now builds in Android Studio or from the command line.
- **Version.** versionName is now `1.0.0`. versionCode went from 14 to 15, so this build installs over upstream 1.4.
- **Build output is no longer committed.** `app/build/` is git-ignored, and upstream's committed APK and build logs were removed.
- **APK archive.** Each `assembleDebug` copies the APK to `apk_releases/JunSUnShell-<versionName>-debug.apk`, which is committed. Older versions stay in that folder, and rebuilding the same version overwrites its file.

### Unchanged
- App behavior, minSdk 21, targetSdk 26, and the `com.thf.junsunshell` application ID are the same as upstream 1.4.

### Building
```
./gradlew assembleDebug
```
Set `JAVA_HOME` to a JDK 17 or newer, e.g. Android Studio's `jbr`. The APK is written to `app/build/outputs/apk/debug/app-debug.apk` and copied to `apk_releases/`.
