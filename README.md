# Kevv Watchlist (com.kevv.watchlist)

Personal movie/TV watchlist. Kotlin, Jetpack Compose, Material 3, Room (local storage), MVVM
(Repository + ViewModel). No login, ads, or network. Min Android 8.0 (API 26).

**Status: source only. This project was NOT compiled, tested, or signed in the environment it was
written in (no Android SDK / Gradle / network). Build it on your machine as below.**

## Requirements
JDK 17+ and Android Studio (Koala or newer), or the Android SDK command-line tools
(platform 34 + build-tools).

## Build the APK (Android Studio)
1. File > Open this folder; let Gradle sync (it downloads Gradle 8.7, AGP 8.5.2, Kotlin 1.9.24).
2. Build > Generate Signed App Bundle / APK... > APK, or use the command line below.

## Build the APK (command line)
    gradle wrapper --gradle-version 8.7      # one time, creates ./gradlew (needs Gradle installed)
    ./gradlew test                           # unit tests
    ./gradlew assembleRelease
Output: `app/build/outputs/apk/release/app-release.apk`  (rename to KevvWatchlist.apk)

## Signing (keep this key!)
    keytool -genkeypair -v -keystore kevv-release.jks -alias kevv -keyalg RSA -keysize 2048 -validity 10000
Create `keystore.properties` in the project root:
    storeFile=kevv-release.jks
    storePassword=YOUR_PASSWORD
    keyAlias=kevv
    keyPassword=YOUR_PASSWORD
`assembleRelease` then signs with your key. Without that file it falls back to the debug key.
Back up the .jks file and passwords somewhere safe: to install an update over an existing install,
the APK must be signed with the same key. Never commit them (.gitignore already excludes them).

## Install
Copy the APK to the phone, open it, and allow "Install unknown apps" for your file manager/browser.
Or: `adb install -r KevvWatchlist.apk`

## Data
Stored in a local Room (SQLite) database `watchlist.db`; survives app restarts and reboots.
Uninstalling the app deletes it.
