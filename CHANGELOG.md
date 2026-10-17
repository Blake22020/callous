# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.1] - 2026-10-17

### Fixed

- Onboarding no longer overflows: profanity options showed their long description inline, so rows grew to several lines — only 2 of 4 were visible and the continue button was pushed off-screen
  - Each option is now a single line (title + marker); the description appears once under the list for the selected level
  - Onboarding content scrolls, with the continue button pinned so it is always reachable

## [1.4.0] - 2026-10-17

### Changed

- **Full redesign in the spirit of Nothing OS, built on Material 3:**
  - Cards are now flat tonal surfaces instead of outlined boxes, removing the "grid of boxes" look
  - Introduced a real Material 3 type scale (`headlineSmall`, `titleLarge/Medium`, `body*`, `label*`) in place of bold-everything headings
  - Red is now a rare accent (progress, active checkbox, danger zone) rather than a fill colour across the UI
  - Bottom navigation switched from Unicode glyphs to real Material icons, with a tonal indicator
  - ASCII progress bars (`█░`) replaced with `LinearProgressIndicator`
  - Heatmap legend now uses colour chips instead of ASCII block characters
  - Dialogs and buttons moved to Material 3 shapes and typography roles
  - Button copy names the action (`ADD`, `SAVE`)

### Added

- `material-icons-extended` for the Material icon set
- `LICENSE` (MIT), `CONTRIBUTING.md`, and this changelog
- APKs are now named `callous-<version>-<variant>.apk` by CI

### Fixed

- Header subtitle no longer overlaps the section title (`GlyphHeader` uses `Row` with weights)

## [1.3.1] - 2026-10-15

### Fixed

- Card content no longer stacks on top of each other — `NothingCard` now lays out as a `Column`, not a `Box`
- Section subtitles (`NO LIES`, `ЧЕСТНО`, …) no longer overlap their titles

## [1.3.0] - 2026-10-14

### Fixed

- Replaced `LazyColumn` with `Column` + `verticalScroll` across all five screens to avoid a GPU recycling artefact that rendered "ghost" text over content
- Removed problematic positional animation parameters carried over from 1.2.0

## [1.2.0] - 2026-10-14

### Fixed

- Removed `animateFloatAsState` / `animateColorAsState` calls where a `label` was being passed positionally into the `finishedListener` slot, which triggered per-frame recomposition and text ghosts
- Switched away from `FontFamily.Monospace`, which produced broken Cyrillic metrics on some Android versions

## [1.1.0] - 2026-10-14

### Added

- 60 quotes (up from 30)
- SOS first-aid kit: 8 state-of-mind texts with copy-to-clipboard

### Fixed

- Quote card no longer overlaps its buttons (dropped `AnimatedContent` in `LazyColumn`)

## [1.0.0] - 2026-10-14

### Added

- Initial release: habits, streaks, progress ring with the 40% rule
- Accountability mirror (daily note)
- 28-day heatmap and statistics
- 9 challenges with per-day tracking
- Goggins quotes and a *Punch me* button
- Profanity levels: CLEAN / MILD / SAVAGE / NIGHTMARE
- Daily reminder and light/dark themes

[Unreleased]: https://github.com/Blake22020/callous/compare/v1.4.1...HEAD
[1.4.1]: https://github.com/Blake22020/callous/compare/v1.4.0...v1.4.1
[1.4.0]: https://github.com/Blake22020/callous/compare/v1.3.1...v1.4.0
[1.3.1]: https://github.com/Blake22020/callous/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/Blake22020/callous/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/Blake22020/callous/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/Blake22020/callous/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/Blake22020/callous/releases/tag/v1.0.0
