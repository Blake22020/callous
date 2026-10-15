# CALLOUS

**Stay hard. No lies.** A David Goggins-inspired discipline tracker for Android, styled after Nothing OS: monochrome, dot-matrix details, monospace caps, one red dot.

> Nobody is coming to save you. This app is your accountability mirror: habits, streaks, hell-week challenges, and a Goggins-style kick when you uncheck a box.

[![Android Build](https://github.com/Blake22020/callous/actions/workflows/android.yml/badge.svg)](https://github.com/Blake22020/callous/actions/workflows/android.yml)
[![Latest release](https://img.shields.io/github/v/release/Blake22020/callous)](https://github.com/Blake22020/callous/releases/latest)
![Platform](https://img.shields.io/badge/platform-Android%2026%2B-3DDC84)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## Download

Grab the APK from the [**latest release**](https://github.com/Blake22020/callous/releases/latest):

| File | What it is |
|---|---|
| `app-debug.apk` | Install straight away (signed with the debug key) |
| `app-release-unsigned.apk` | Unsigned release build — needs your own keystore |

No Play Store, no accounts, no analytics. Your data stays on your device.

## Features

| Screen | What it does |
|---|---|
| **TODAY** | Habit list with a custom Nothing-style checkbox, daily progress ring with the 40% rule bar, quote of the day |
| **Streaks** | Automatic consecutive-day counting per habit |
| **MIRROR** | Daily accountability note: where you quit, where you won. Honesty only |
| **DATA** | 28-day heatmap, max streak, total completions |
| **HELL** | 9 multi-day challenges (Hell Week, Stay Hard 30, 4x4x48…) with per-day tracking. Miss a day — back to day one |
| **MINDSET** | 60 Goggins quotes, "PUNCH ME" button, copy-to-clipboard |
| **SOS** | 9 first-aid texts by state of mind (lazy, unmotivated, want to quit…). Read it twice |
| **Verdicts** | Uncheck a habit — get a verbal kick with a shake animation. Complete one — get a nod |

Plus: adjustable profanity level (`CLEAN` / `MILD` / `SAVAGE` / `NIGHTMARE`, with live preview), daily 06:00 reminder via WorkManager, Nothing Black / Paper themes, full Russian + English UI.

## Screenshots

Screenshots live in [`docs/screenshots/`](docs/screenshots/) — drop yours there after a release build.

## Tech stack

- **Kotlin 2.0**, **Jetpack Compose** (Material3, BOM 2024.09), Navigation Compose
- **Room** (habits, completions, challenge progress), **DataStore** (settings)
- **WorkManager** (daily reminder), Coroutines + StateFlow, KSP
- Min SDK 26, target/compile SDK 34, JDK 17
- CI: GitHub Actions builds debug + release APKs on every push to `main`

## Build it yourself

Requirements: Android SDK 34 + JDK 17.

```bash
git clone https://github.com/Blake22020/callous.git
cd callous
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Every push to `main` also builds both APKs in **Actions** — download them from the run's Artifacts. To sign a release for the Play Store, add your own keystore.

## Project structure

```
app/src/main/java/com/goggins/discipline/
├── MainActivity.kt / GogginsApp.kt
├── data/
│   ├── content/   # Quotes, Challenges, Sos, VerbalAssault
│   ├── local/     # Room: HabitDao, AppDatabase
│   ├── repo/      # HabitRepository
│   └── settings/  # ProfanityLevel, SettingsManager (DataStore)
├── workers/       # ReminderWorker
└── ui/
    ├── GogginsViewModel.kt / ProgressRing.kt
    ├── theme/       # Nothing Black / Paper
    ├── components/  # NothingCard, GlyphHeader, DotMatrix
    ├── navigation/  # NavGraph
    └── screens/     # Home, Stats, Challenges, Mindset, Settings
```

## Contributing

Issues and pull requests are welcome. Keep it simple, keep it hard:

1. Fork, create a branch, make the change
2. Make sure `./gradlew assembleDebug` passes
3. Open a PR with a short description of what and why

## License

MIT — see [LICENSE](LICENSE). Quotes: D. Goggins, *Can't Hurt Me* / *Never Finished*.

**STAY HARD.**
