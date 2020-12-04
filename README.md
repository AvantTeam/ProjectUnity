# Project Unity

[![Discord](https://img.shields.io/discord/704355237246402721.svg?color=7289da&label=AvantTeam&logo=discord&style=flat-square)](https://discord.gg/V6ygvgGVqE)
![Made with Conflicts](https://img.shields.io/badge/Made%20with-conflicts%20<3-red?style=flat-square)

The biggest Mindustry mod, created by 19 authors.

[*Wiki*]()  
[*Miro Board*](https://miro.com/app/board/o9J_lejcuWo=/)  
[*Trello Board*](https://trello.com/b/oNa7R7bq/project-unity)

## Building

Install JDK 14 or higher (you can do `apt install openjdk-14-jdk-headless` on Linux). Set JAVA_HOME environment variable to where it is located. Clone this repository then change your current working directory to where the cloned repository folder is. If you are compiling for Android, click [here](#Android).

### Windows

- `gradlew main:jar` - Desktop
- `gradlew main:deploy` - Desktop & Android
- `gradlew tools:proc` - Generate Sprites

### Linux/MacOS

- `./gradlew main:jar` - Desktop
- `./gradlew main:deploy` - Desktop & Android
- `./gradlew tools:proc` - Generate Sprites

### Android

1. Install the Android SDK [here](https://developer.android.com/studio). Make sure you're downloading the "Command line tools only", as Android Studio is not required. Get the API level 30 (or higher build) tools version.
3. Set the ANDROID_HOME environment variable to point to your unzipped Android SDK directory.
4. Run `gradlew main:deploy` (or ./gradlew if on linux/mac). This will create a JAR file in `main/build/libs/ProjectUnity.jar`, playable in both desktop and Android.

---

Build output should be located in `main/build/libs/*.jar`.
