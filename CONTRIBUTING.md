# Contributing to CALLOUS

Thanks for your interest in contributing. This is a small, opinionated project, so a few guidelines help keep it consistent.

## Before you start

- Search [existing issues](https://github.com/Blake22020/callous/issues) to see if your idea or bug is already tracked.
- For larger changes (new screens, architecture, dependencies), **open an issue first** so we can agree on the direction.
- Small fixes and typo corrections can go straight to a pull request.

## Development setup

**Requirements:** Android SDK (API 34) and JDK 17.

```bash
git clone https://github.com/Blake22020/callous.git
cd callous
./gradlew assembleDebug
```

## Making a change

1. Fork the repository and branch off `main`:
   ```bash
   git checkout -b fix/short-description
   ```
2. Keep the existing code style and structure:
   - Screens live in `ui/screens/`, shared components in `ui/components/`
   - Use Material 3 roles from `MaterialTheme.typography` / `colorScheme` rather than hard-coded sizes and hex colours
   - Red (`NothingRed`) is an accent — progress and danger states — not a general fill colour
3. Verify your change builds:
   ```bash
   ./gradlew assembleDebug
   ```
4. Commit with a clear message describing **what** and **why**.
5. Open a pull request filling in the template: what changed, why, and how you tested it.

## Reporting bugs

Open an issue with:

- Device and Android version
- App version (from **Settings**)
- Steps to reproduce
- What you expected vs. what happened

Screenshots or a short screen recording help a lot.

## Adding content

Quotes, challenges, and SOS texts live in `app/src/main/java/com/goggins/discipline/data/content/`. If you're adding quotes from David Goggins' books, keep the attribution accurate.

## Code of conduct

Be respectful and constructive. Critique the code, not the person.
