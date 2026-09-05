# 📱 Campus Companion
> **The modern, liquid-glass offline personal utility app for engineering college students (EE-VLSI).**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Offline First](https://img.shields.io/badge/Privacy-100%25%20Offline-00C853)](https://github.com)
[![Direct Download](https://img.shields.io/badge/Download-APK-FF6F00?logo=android&logoColor=white)](#-download-apk--install-on-your-phone)

---

## 📥 Download APK & Install on Your Phone

You can install Campus Companion directly on any Android phone without needing a computer or the Google Play Store.

### 🔗 Direct Download Links
* **Primary (Latest Release)**: Go to the [**GitHub Releases Page**](../../releases/latest) and tap **`CampusCompanion.apk`** to download.
* **Alternative (Actions Artifacts)**: Visit the [**Actions Tab**](../../actions), click on the latest successful build run, and download the **`CampusCompanion-APK`** zip.

---

### 📲 Step-by-Step Installation Guide

Follow these simple steps on your Android device:

```text
1. Download CampusCompanion.apk  ➔  2. Open from notification/Downloads
                                  ➔  3. Enable "Allow from this source" (if prompted)
                                  ➔  4. Tap "Install"
```

1. **Download the APK**:
   Tap on **`CampusCompanion.apk`** from the [Releases tab](../../releases/latest) in your phone's browser (Chrome, Firefox, Brave, etc.).

2. **Open the File**:
   Once downloaded, tap the download completion notification, or open your phone's **Files** or **Downloads** app and tap `CampusCompanion.apk`.

3. **Allow Installation from Unknown Sources**:
   If your phone displays a security prompt saying:
   > *"For your security, your phone is not allowed to install unknown apps from this source"*
   - Tap **Settings** on the prompt.
   - Toggle **"Allow from this source"** ON.
   - Tap your phone's **Back** button to return to the installer.

4. **Tap Install**:
   - Tap **Install**.
   - If Google Play Protect displays a dialog stating *"Unrecognized app"*, simply tap **"More details"** and select **"Install anyway"** (this standard warning appears for all sideloaded apps not distributed via Google Play Store).

5. **Open & Enjoy**:
   Launch **Campus Companion** from your home screen or app drawer!

---

## ✨ Features

### 🍱 Weekly Mess Timetable
- Complete 7-day meal schedules for **Breakfast**, **Lunch**, **Evening Snacks & Tea**, and **Dinner**.
- **Self-Healing & Deduplication**: Ensures zero repeated slots and chronological meal display.
- **Visual Dish Chips**: Modern tag chips for quick scanning of menus.
- **Editable & Customizable**: Add, edit, or adjust items directly in the app.

### 🎓 EE-VLSI Class Timetable
- Tailored for engineering students with day-by-day lecture slots, timings, and venues.
- **Lab Batch Toggle**: Seamlessly switch between **G1** and **G2** batches.
- **Course Catalog Integration**: Shows course codes, subject names, faculty, and room locations.
- **Export & Backup**: Export or import your schedule cleanly using clipboard JSON.

### ⏰ Exact Meal & Class Alarms
- Powered by Android's **`AlarmManager.setAlarmClock()`**, the highest-precision alarm API on Android.
- Triggers at the exact start time, completely immune to Android Doze mode and background battery optimizations.

### 📝 Tasks & Assignment Tracker
- Homework and deadline organizer with priority flags and subject tagging.
- Reminders, recurrence, search filters, and collapsible completed tasks section.

### 🎨 Liquid Glass UI & Adaptive Layout
- **Liquid Glass Aesthetic**: Translucent frosted-glass cards, subtle depth, high-contrast typography, and smooth spring physics animations.
- **Dynamic Sky Themes**: Shifting time-of-day gradient backgrounds (warm dawn, crisp afternoon, twilight evening, deep night).
- **Light & Dark Mode**: Full support for both themes with dedicated frosted styling.
- **Responsive Tablet & Landscape Mode**:
  - Automatically activates an ergonomic **Frosted-Glass Navigation Rail** on horizontal phones and tablets (`screenWidthDp >= 600dp` or landscape `screenWidthDp >= 500dp`).
  - Automatic **2-column card layouts** for wide screens so content never looks stretched.

### 🛡️ 100% Offline & Private
- **Zero Internet Permissions**: `AndroidManifest.xml` does not request `android.permission.INTERNET`.
- **Zero Cloud / Backend Tracking**: No Gemini API, no external servers, no ads, no analytics.
- **Local SQLite Persistence**: All schedules, tasks, and settings are saved on your device via Room.

---

## 🛠️ Building from Source

If you prefer to build the APK yourself using Android Studio:

### Prerequisites
- [Android Studio Ladybug (2024.2.1+)](https://developer.android.com/studio) or newer
- JDK 21 / 17
- Android SDK (API Level 36, Min SDK 24)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/<your-repo-name>.git
   cd <your-repo-name>
   ```

2. Build the Debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. The generated APK will be available at:
   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```

4. Install directly to a connected phone (USB Debugging enabled):
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

5. Run local unit & Robolectric tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🏗️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & `StateFlow`
- **Database**: [Android Room](https://developer.android.com/training/data-storage/room) (Local SQLite)
- **Scheduling**: Android `AlarmManager` with `AlarmClockInfo`
- **Serialization**: Moshi (Local JSON backup/restore)
- **Testing**: Robolectric & Roborazzi screenshot verification
- **CI/CD**: GitHub Actions (automatic APK compilation & release publishing)

---

## 📄 License
Created for engineering students. Free and open source.
