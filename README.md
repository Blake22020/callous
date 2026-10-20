<div align="center">

# CALLOUS

**Stay hard. No lies.**

A David Goggins–inspired discipline tracker for Android,
built with Jetpack Compose and styled after Nothing OS.

[![Android Build](https://github.com/Blake22020/callous/actions/workflows/android.yml/badge.svg)](https://github.com/Blake22020/callous/actions/workflows/android.yml)
[![Latest Release](https://img.shields.io/github/v/release/Blake22020/callous)](https://github.com/Blake22020/callous/releases/latest)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Platform](https://img.shields.io/badge/platform-Android%2026%2B-3DDC84)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4)

[Download](#download) · [Features](#features) · [Build](#building-from-source) · [Contributing](#contributing) · [License](#license)

</div>

---

Nobody is coming to save you. **CALLOUS** is your accountability mirror: daily habits, streaks, multi-day hell-week challenges, and a Goggins-style kick the moment you try to uncheck a box.

No accounts. No analytics. No ads. Your data never leaves the device.

## Table of contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Download](#download)
- [Building from source](#building-from-source)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Changelog](#changelog)
- [License](#license)

## Features

| Screen | What it does |
| :--- | :--- |
| **Today** | One work goal, one discipline action, one respect action, and a 25-minute focus timer; habits stay available in a compact list |
| **Streaks** | Automatic consecutive-day counting per habit |
| **Review** | Evening accountability note and a local bank of proof-of-progress entries |
| **Data** | Weekly commitment and focus summary, 28-day heatmap, streaks, and total completions |
| **Challenges** | 9 multi-day challenges (Hell Week, Stay Hard 30, 4×4×48…) with expandable rules and progress |
| **Laws** | A daily practical lesson inspired by one of the 48 laws, with an example, application, defense, and action |
| **Mindset tools** | 60 Goggins quotes, SOS texts, and a tone-aware *Punch me* button |
| **Verdicts** | Uncheck a habit and get a verbal kick; complete one and get a nod |

Also included:

- **Profanity levels** — `CLEAN` / `MILD` / `SAVAGE` / `NIGHTMARE`, with a live preview
- **Daily reminder** — a nudge at 06:00 via WorkManager (time is configurable)
- **Themes** — Nothing Black and Paper, following Material 3 light/dark schemes
- **Bilingual UI** — Russian and English

## Screenshots

Drop device screenshots into [`docs/screenshots/`](docs/screenshots/) and link them here.

## Download

Grab the APK from the [**latest release**](https://github.com/Blake22020/callous/releases/latest):

| Asset | Description |
| :--- | :--- |
| `callous-<version>-debug.apk` | Install straight away — signed with the debug key |
| `callous-<version>-release-unsigned.apk` | Release build, unsigned — sign it with your own keystore |

> **Note:** the debug build uses its own application ID suffix (`.debug`), so it installs alongside the release build instead of overwriting it.

## Building from source

**Requirements:** Android SDK (API 34) and JDK 17.

```bash
git clone https://github.com/Blake22020/callous.git
cd callous
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

For a release build:

```bash
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release-unsigned.apk
```

The unsigned release APK needs signing before installation — see [Android's signing docs](https://developer.android.com/studio/publish/app-signing).

### Continuous integration

Every push to `main` runs [`.github/workflows/android.yml`](.github/workflows/android.yml), which:

- installs JDK 17 and the Android 34 platform,
- builds **both** debug and release APKs,
- renames them to `callous-<version>-<variant>.apk`,
- uploads them as workflow artifacts.

Pushing a `v*` tag runs the same build and publishes both APKs as GitHub release assets. You can download development builds from the **Actions** tab without building locally.

## Tech stack

- **Kotlin 2.0** with KSP
- **Jetpack Compose** (Material 3, Compose BOM 2024.09) and Navigation Compose
- **Room** for local persistence (habits, completions, challenge progress)
- **DataStore** for settings and daily productivity records
- **WorkManager** for the daily reminder
- **Coroutines + StateFlow** for reactive state
- Min SDK 26 · Target/Compile SDK 34 · JDK 17

## Project structure

```
app/src/main/java/com/goggins/discipline/
├── MainActivity.kt          # app shell, onboarding, bottom navigation
├── GogginsApp.kt
├── data/
│   ├── content/             # Quotes, Challenges, Sos, VerbalAssault
│   ├── local/               # Room: HabitDao, AppDatabase
│   ├── productivity/        # Daily commitments, focus sessions, proof bank
│   ├── repo/                # HabitRepository
│   └── settings/            # ProfanityLevel, SettingsManager (DataStore)
├── workers/                 # ReminderWorker
└── ui/
    ├── GogginsViewModel.kt  # single source of truth for the UI
    ├── ProgressRing.kt
    ├── theme/               # color roles, Material 3 typography & shapes
    ├── components/          # NothingCard, GlyphHeader, DotMatrix
    ├── navigation/          # NavGraph, routes
    └── screens/             # Home, Stats, Challenges, Mindset, Settings
```

## Roadmap

- [ ] Ship light/dark screenshots for the README
- [ ] Custom app icon (currently the system default)
- [ ] Optional export of habit history
- [ ] Tablet / expanded-width layout (navigation rail)

Ideas and PRs are welcome — open an issue first if it's a big one.

## Contributing

Contributions make this project better. To get started:

1. Fork the repository and create a feature branch
2. Make your change, keeping the existing code style
3. Ensure `./gradlew assembleDebug` passes
4. Open a pull request describing **what** changed and **why**

Bug reports and feature requests are welcome too — please use [GitHub Issues](https://github.com/Blake22020/callous/issues).

## Changelog

All notable changes are documented in [CHANGELOG.md](CHANGELOG.md).

## License

This project is released under the [MIT License](LICENSE).

Quotes and references are attributed to David Goggins, *Can't Hurt Me* and *Never Finished*. This is an unofficial fan project and is not affiliated with or endorsed by David Goggins.

---

<div align="center">

**STAY HARD.**

</div>
