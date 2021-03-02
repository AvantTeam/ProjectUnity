# Project Unity

[![Discord](https://img.shields.io/discord/782583108473978880.svg?color=7289da&label=AvantTeam&logo=discord&style=flat-square)](https://discord.gg/V6ygvgGVqE)
[![Made by 19 Authors](https://img.shields.io/badge/Made%20by-19%20Authors-blue?style=flat-square)](https://www.youtube.com/watch?v=dQw4w9WgXcQ)

The biggest [Mindustry](https://github.com/Anuken/Mindustry/) mod.

_Relevant links_:
<br>
[*Miro Board*](https://miro.com/app/board/o9J_lejcuWo=/)  
[*Trello Board*](https://trello.com/b/oNa7R7bq/project-unity)

## Building

Install JDK 15 or higher. Set `JAVA_HOME` environment variable to where it is located. Clone this repository then change your current working directory to where the cloned repository folder is. If you are compiling for Android, click [here](#Building-for-Android).

### Windows

- `gradlew tools:proc` - Processes raw sprites and generates some other needed ones; will be automatically called if `./main/assets/sprites` directory isn't found.
- `gradlew main:deploy` - Builds the mod `.jar` file for desktop only.
- `gradlew main:deployDex` - Builds the mod `.jar` file for both desktop and Android _(see [**Android**](#Building-for-Android))_.

#### Linux/MacOS

<m>_Replace `gradlew` with `./gradlew`_</m>

## Building for Android

1. Install the Android SDK [here](https://developer.android.com/studio). Make sure you're downloading the **"Command line tools only"**, as Android Studio is not required.
2. Create a folder with any name you want anywhere, then set `%ANDROID_HOME%` environment variable to the created folder.
3. Unzip the downloaded Android SDK command line tools, then move the folder into `%ANDROID_HOME%`.
    * Note that the downloaded command line tools folder is sometimes wrong; the correct path to `sdkmanager.bat` is `cmdline-tools/tools/bin/sdkmanager.bat`.
4. Open the command line, then `cd` to `%ANDROID_HOME%/cmdline-tools/tools/bin`.
5. Run `sdkmanager --install "build-tools;30.0.3"` to install the Android build tools, assuming you're using version `30.0.3`.
6. Add `%ANDROID_HOME%/build-tools/30.0.3` to your `PATH` environment variable.
7. Run `gradlew main:deployDex` (or `./gradlew` if on Linux/MacOS). This will create a `.jar` file in `main/build/libs/ProjectUnity.jar`, playable in both desktop and Android.

---

- Build output should be located in `main/build/libs/`.
- If the command returns `Permission Denied` on Linux, run `chmod +x gradlew`. Furthermore, run this if `./gradlew tools:proc` errors with the denied permission message:
  ```bash
    $ chmod +x ./main/alpha-bleed
    $ chmod +x ./main/alpha-bleeding-linux.exe
  ```
