# JunSUnShell

A minimal root shell for JunSun-style Android car head units. Type a shell command, tap the run button, and see its output, all running as root through the head unit's built-in `su`.

This is a fork of [TorstenH82/JunSUnShell](https://github.com/TorstenH82/JunSUnShell), updated to build with current Android tooling. See [RELEASE_NOTES.md](RELEASE_NOTES.md) for changes.

## Features

- **Root commands without a root manager.** It uses the head unit's own `su` binary, which these units protect with a fixed password argument. No Magisk or SuperSU is needed.
- **Command pop-up.** Commands are typed in a pop-up that opens when the app starts and from the **Run command** button.
- **Command history.** Your last 50 commands are saved on the device and listed in the pop-up, newest first.
- **Obvious exit.** A large red **EXIT** button closes the app and removes it from the recent apps list.
- **Output on screen.** The command and its standard output and error are shown on the main screen. You can select and copy the text.
- **Small and simple.** One screen, no background service, no settings.

## Requirements

- An Android head unit with JunSun-style firmware whose `su` accepts the password `@#zxcvbnmasdfghjklqwertyuiop1234567890,.` (see [How It Works](#how-it-works))
- Android 5.0 (API 21) or later

## Installation

1. Download the latest APK (`JunSUnShell-*-debug.apk`) from [Releases](https://github.com/djbooya/JunSUnShell/releases) or the [`apk_releases`](apk_releases) folder.
2. Copy it to a USB drive or to the radio's storage.
3. On the radio, allow your file manager to install unknown apps, then open the APK.
4. Launch **JunSUnShell** from the app drawer.

## Usage

1. The command pop-up opens when the app starts. Later, tap **Run command** to open it again.
2. Type a command, e.g. `id`, `getprop ro.build.display.id` or `ls /data`, or tap one in the history list to copy it into the field.
3. Tap **Run** or press Enter on the keyboard.
4. The command and its output appear on the main screen.
5. Tap **EXIT** when you're done.

In the history list:
- **Tap** a command to put it in the field, where you can edit it before running.
- **Long-press** a command to remove it.
- **Clear history** removes every command.

Running a command moves it to the top of the history. The same command is never listed twice.

Tips:
- The field takes one line. To run several commands, join them with `;` or `&&`, e.g. `cd /data/local/tmp && ls -l`.
- Each tap starts a new root shell, so `cd` and variables don't carry over to the next tap.
- To keep long output, redirect it to a file, e.g. `logcat -d > /sdcard/logcat.txt`.

## How It Works

1. On launch, the app reads the `su` command line from `app/src/main/res/values/strings.xml` (`su @#zxcvbnm…,.`).
2. When you run a command, it starts that `su` process, writes your command followed by `exit` to its input, and reads standard output and standard error on two threads.
3. When the process exits, the collected lines are shown in the output box. If `su` can't be started, the error message is shown instead.

4. The command is added to the history, which is saved in the app's private storage (SharedPreferences).

The code is in [`MainActivity.java`](app/src/main/java/com/thf/junsunshell/MainActivity.java), [`utils/Util.java`](app/src/main/java/com/thf/junsunshell/utils/Util.java) and [`utils/CommandHistory.java`](app/src/main/java/com/thf/junsunshell/utils/CommandHistory.java).

## Known Limitations

- **The app freezes while a command runs.** Commands run on the UI thread with no timeout. A command that doesn't exit on its own, like `logcat` without `-d`, `top` or `ping` without `-c`, will hang the app until you force-stop it.
- **No interactive commands.** There is no keyboard input after the command starts, so editors, pagers and prompts won't work.
- **Output order isn't guaranteed.** Standard output and error are read in parallel, so their lines can be interleaved out of order.
- **Other devices.** On units whose `su` doesn't use this password, every command fails or returns nothing.

## Building

Requires JDK 17 or newer (Android Studio's bundled `jbr` works) and the Android SDK with platform 35 and build tools 35.0.1.

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk` and copied to `apk_releases/JunSUnShell-<versionName>-debug.apk`. Older versions stay in that folder, so bump `versionName` and `versionCode` in [`app/build.gradle`](app/build.gradle) for each release.

## Credits

Original app by [TorstenH82](https://github.com/TorstenH82).
