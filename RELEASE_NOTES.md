## لیست خرید ۲.۰ — بازنویسی کامل با Kotlin

A complete rewrite of the app in Kotlin and Jetpack Compose.

### ✨ New
- **Shopping history**: tap **Finish** when you're done and the trip is saved. You can browse past trips by month, search them, and add one item or a whole trip back to your list.
- **Smart suggestions**: as you type, items you've bought before are suggested, and your most frequent items appear as quick-add chips.
- **Quick entry**: type `2 bread`, `eggs x 12` or `۳ نان` and the quantity is set for you. Adding an item that's already on the list increases its quantity.
- **Automatic categories** (produce, dairy, bakery…) in Persian and English, with optional **Group by category**.
- **Swipe to delete with Undo**, tap to edit (name, quantity, note, category), and share the list as text.
- **Progress ring** showing how much of your list is already in the cart.

### 🎨 Design
- iOS-inspired UI: large collapsing titles, inset grouped lists, round checkmarks, bottom sheets, segmented controls.
- **Multiple themes**: Automatic / Light / Dark, plus 8 accent colors.
- **Adaptive layout**: tab bar on phones, a navigation rail and two-pane history on tablets, foldables and landscape.
- Persian (Solar Hijri dates, Persian digits, RTL) and English, switchable in Settings.
- Themed and adaptive launcher icon, splash screen, edge-to-edge display, haptic feedback.

### 🛠 Under the hood
- Kotlin, Jetpack Compose, Material 3, Room, DataStore, and MVVM with coroutines/Flow.
- Your current list is migrated from version 1 automatically.
- Requires Android 7.0 (API 24) or newer.
