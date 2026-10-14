# GOGGINS.OS // STAY HARD — трекер дисциплины

Полноценное Android-приложение (Kotlin + Jetpack Compose, Material3) в стиле **Nothing OS**: монохром, моноширинный шрифт, красная точка, точечная матрица, капс.

## Функционал (только нужное, без мусора)

- **СЕГОДНЯ**: список привычек, кастомный Nothing-чекбокс, кольцо прогресса + 40% RULE бар, цитата дня
- **Стрики**: авто-подсчёт подряд идущих дней, ASCII-бар
- **Зеркало ответственности**: ежедневная заметка «где сдался / где победил»
- **ДАННЫЕ**: тепловая карта 28 дней, макс. стрик, всего закрытий
- **АД (испытания)**: Hell Week, Stay Hard 30, 4x4x48, холод, бег, отжимания и т.д. с трекингом дней
- **РАЗУМ**: 30 цитат Гоггинса + кнопка «ДОБЕЙ МЕНЯ 👊» + копирование
- **Вердикты Гоггинса**: снял галочку → пинок с тряской-анимацией; закрыл → похвала
- **МАТ // PROFANITY** (настройки + онбординг):
  - `CLEAN` — вообще без мата (для чувствительных)
  - `MILD` — лёгкий («чёрт»)
  - `SAVAGE` — умеренный мат
  - `NIGHTMARE` — очень много мата
  - Есть живой предпросмотр пинка. Уведомления всегда максимум MILD.
- **Напоминание**: ежедневный WorkManager-пинок в 06:00 (время настраивается кодом, toggle в настройках)
- **Тёмная/светлая тема** Nothing Black / Paper
- **Анимации**: ProgressRing, spring-чекбокс, AnimatedContent цитат, shake на наказании

## Стек

Kotlin 2.0, Compose BOM 2024.09, Navigation, Room, DataStore, WorkManager, Coroutines, KSP.

## Сборка локально

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Требуется Android SDK 34 + JDK 17.

## Сборка через GitHub Actions (готово)

Воркфлоу `.github/workflows/android.yml`:
- ставит JDK 17 + Android SDK 34,
- собирает **debug + release APK**,
- кладёт оба в Artifacts (`goggins-debug-apk`, `goggins-release-apk`).

Просто запушь в `main` → вкладка **Actions** → скачай APK. Для подписи релиза добавь свой keystore (сейчас release собирается debug-подписью CI, для Play нужен свой ключ).

## Структура

```
app/src/main/java/com/goggins/discipline/
  MainActivity.kt, GogginsApp.kt
  data/content/{Quotes,VerbalAssault,Challenges}.kt
  data/local/{HabitDao,AppDatabase}.kt
  data/repo/HabitRepository.kt
  data/settings/{ProfanityLevel,SettingsManager}.kt
  workers/ReminderWorker.kt
  ui/{GogginsViewModel,ProgressRing}.kt
  ui/theme/{Color,Theme}.kt
  ui/components/NothingBits.kt
  ui/navigation/NavGraph.kt
  ui/screens/{Home,Stats,Challenges,Mindset,Settings}Screen.kt
```
