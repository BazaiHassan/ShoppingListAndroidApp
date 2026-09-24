# 🛒 Shopping List · لیست خرید

A simple, polished shopping list for Android, written in **Kotlin** with **Jetpack Compose**.
It uses an iOS-inspired design, supports several themes, adapts to every screen size, and keeps a history of your shopping trips.

یک لیست خرید ساده و حرفه‌ای با طراحی الهام‌گرفته از اپلیکیشن‌های اپل، چند پوسته، سازگار با همه اندازه‌های صفحه و ذخیره تاریخچه خریدها.

## Features

| | |
|---|---|
| ✅ **Fast list** | Add items with suggestions from your history. Type `2 bread` or `۳ نان` to set a quantity. Tap an item to edit it, swipe it away to delete it (with undo). |
| 🕘 **History** | Tap **Finish** to save a trip. Browse trips by month, search them, and add one item or a whole trip back to your list. |
| ⭐ **Frequent items** | The items you buy most often become quick-add chips. |
| 🗂 **Categories** | Items are categorized automatically in Persian and English. You can turn on grouping by category. |
| 🎨 **Themes** | Automatic, Light and Dark modes with 8 Apple system accent colors. |
| 📱 **Responsive** | Tab bar on phones, a navigation rail and a two-pane history on tablets, foldables and landscape screens. Content width stays readable on large displays. |
| 🌐 **Languages** | Persian by default (RTL, Solar Hijri dates, Persian digits), with English available in Settings. |

## Tech stack

- Kotlin 2.0, Jetpack Compose (Material 3), adaptive window size classes
- Room (with a migration from the v1 Java database) and Preferences DataStore
- MVVM with `ViewModel`, `StateFlow` and coroutines
- Core SplashScreen, edge-to-edge, per-app locales

```
app/src/main/java/com/example/dailyshoppinglist/
├── data/          Room entities & DAOs, repository, settings (DataStore)
├── domain/        Categories, quick-entry parser, suggestions, share formatting
└── ui/            Theme, shared components, list / history / settings screens
```

## Build

Requirements: JDK 17 and the Android SDK (API 35).

```bash
./gradlew testDebugUnitTest   # unit tests
./gradlew assembleDebug       # debug APK
./gradlew assembleRelease     # minified release APK
```

## Releases

GitHub Actions (`.github/workflows/android.yml`) runs tests, lint and a release build on every push.
Pushing a `v*` tag (or a commit whose message contains `[release]`) publishes a GitHub Release with the APK attached. The release notes come from `RELEASE_NOTES.md`.

To sign releases with your own key, add these repository secrets:

- `SIGNING_KEYSTORE_BASE64`: the keystore, base64-encoded (`base64 -w0 release.jks`)
- `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`

Without them, the APK is signed with a temporary debug key. You'll need to uninstall such a build before installing one signed with a different key.
