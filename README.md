# Submarine Hunt ⚓️ Modern Android Sonar Game

**Submarine Hunt** is a modern, offline Android game built with **Jetpack Compose** and **Material 3**. Search the ocean grid, follow sonar distance cues, and locate the hidden submarine in as few searches as possible!

---

## 🌟 Key Features

* 🗺️ **7×7 Tactical Grid**: Interactive board labeled A–G and 1–7 with smooth touch feedback and dynamic coordinate markers.
* 🛰️ **Sonar Distance Feedback**: Calculates exact Manhattan distance ($|\Delta \text{row}| + |\Delta \text{col}|$) to give precise *Closer*, *Farther*, *Very Close*, or *Same Distance* pings.
* 🔊 **Procedural Audio & Tactile Haptics**: Native sound effects generated via `ToneGenerator` and tactile haptic vibration patterns without external audio assets.
* 📊 **Comprehensive Game Statistics**: Tracks your best search count, highest score, current win streak, best win streak, search accuracy percentage, and average searches per hunt.
* 🎨 **Ocean Material 3 Theming**: Beautiful custom dark and light ocean themes, support for Android Dynamic Colors, and animated sonar Canvas elements.
* 📱 **Landscape & Adaptive Layouts**: Fully responsive UI supporting both Portrait and Landscape orientations.
* 🔒 **100% Offline & Private**: Zero network dependencies, zero tracking, no accounts, and no required permissions.

---

## 🎮 How to Play

1. **Ping a Sector**: Tap any cell on the 7×7 ocean grid (A1 to G7) to launch a sonar ping.
2. **Read the Sonar**:
   * **Closer**: Your current ping is nearer to the submarine than your previous search.
   * **Farther**: You moved further away from the hidden target.
   * **Very Close**: Distance to submarine is $\le 2$ sectors.
   * **Same Distance**: Equidistant from the submarine as the previous ping.
3. **Triangulate**: Use distance history and warmer/colder feedback to narrow down the target location.
4. **Target Found**: Tap the submarine's exact coordinate to complete the hunt!

---

## 📐 Scoring & Rules

* **Board Size**: 7 rows × 7 columns (49 sectors total).
* **Distance Formula**: Manhattan Distance $D = |r_1 - r_2| + |c_1 - c_2|$.
* **Score Calculation**:
  $$\text{Score} = \max\left(50, \, 500 - ((\text{Searches} - 1) \times 25)\right)$$
* **Accuracy Calculation**:
  $$\text{Accuracy} = \frac{1}{\text{Total Searches}} \times 100\%$$

---

## 🛠️ Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
* **Architecture Pattern**: MVVM (Model-View-ViewModel) with unidirectional data flow
* **Async & State**: Kotlin Coroutines & `StateFlow`
* **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
* **Local Storage**: `SharedPreferences` with reactive flow updates
* **Audio & Haptics**: Android `ToneGenerator` & `Vibrator` APIs
* **Testing**: JUnit4 unit tests for game engine rules and preference persistence

---

## 📁 Project Structure

```
com.example.submarinehunt/
├── audio/            # Audio & Haptic feedback helper
├── data/             # PreferencesManager & settings/statistics data classes
├── engine/           # Core SubmarineHuntEngine & game rules logic
├── model/            # Coordinate, GamePhase, DistanceCategory, HuntGameState
├── ui/
│   ├── components/   # SubmarineHuntBoard, SonarVisual, StatsCard, FeedbackCard, etc.
│   ├── navigation/   # Compose Navigation graph & screen destinations
│   ├── screens/      # HomeScreen, GameScreen, ResultScreen, SettingsScreen, AboutScreen, SplashScreen
│   ├── theme/        # Color palette, Material3 Theme, Typography
│   └── viewmodel/    # SubmarineHuntViewModel managing game state & settings
```

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug (2024.2) or newer recommended.
* **JDK**: Version 11 or higher.
* **Android SDK**: Min SDK 24 (Android 7.0), Target SDK 37 (Android 15+).

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Submarine-Hunt.git
   cd Submarine-Hunt
   ```

2. Build debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

3. Run unit tests:
   ```bash
   ./gradlew test
   ```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

Developed by **[Abdullah Ateeq](https://github.com/abdullah-ateeq)**.
