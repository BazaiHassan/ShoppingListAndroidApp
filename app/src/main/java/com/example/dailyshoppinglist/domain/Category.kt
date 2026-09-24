package com.example.dailyshoppinglist.domain

import androidx.annotation.StringRes
import com.example.dailyshoppinglist.R

enum class Category(val key: String, val emoji: String, @StringRes val label: Int, keywords: List<String>) {
    PRODUCE(
        "produce", "🥦", R.string.cat_produce,
        listOf(
            "apple", "banana", "tomato", "potato", "onion", "carrot", "lettuce", "cucumber", "fruit", "vegetable",
            "orange", "lemon", "grape", "garlic", "pepper", "spinach", "herbs", "avocado", "strawberry",
            "سیب", "موز", "گوجه", "سیب زمینی", "پیاز", "هویج", "کاهو", "خیار", "میوه", "سبزی", "لیمو", "پرتقال",
            "انگور", "سیر", "فلفل", "اسفناج", "هندوانه", "خربزه", "بادمجان", "کدو",
        ),
    ),
    DAIRY(
        "dairy", "🥛", R.string.cat_dairy,
        listOf(
            "milk", "cheese", "yogurt", "yoghurt", "butter", "cream", "egg", "eggs",
            "شیر", "پنیر", "ماست", "کره", "خامه", "تخم مرغ", "دوغ", "کشک",
        ),
    ),
    BAKERY(
        "bakery", "🍞", R.string.cat_bakery,
        listOf(
            "bread", "bun", "cake", "cookie", "croissant", "bagel", "toast",
            "نان", "کیک", "بیسکویت", "شیرینی", "باگت", "لواش", "بربری", "سنگک", "تافتون",
        ),
    ),
    MEAT(
        "meat", "🍗", R.string.cat_meat,
        listOf(
            "chicken", "beef", "meat", "fish", "lamb", "sausage", "ham", "shrimp", "turkey", "salmon",
            "گوشت", "مرغ", "ماهی", "سوسیس", "کالباس", "میگو", "بوقلمون", "جوجه",
        ),
    ),
    PANTRY(
        "pantry", "🥫", R.string.cat_pantry,
        listOf(
            "rice", "pasta", "flour", "sugar", "salt", "oil", "beans", "lentils", "tuna", "sauce", "spice",
            "honey", "jam", "cereal", "noodles", "ketchup",
            "برنج", "ماکارونی", "آرد", "شکر", "نمک", "روغن", "لوبیا", "عدس", "رب", "تن ماهی", "ادویه", "عسل",
            "مربا", "نخود", "سس", "زعفران", "رشته",
        ),
    ),
    FROZEN(
        "frozen", "🧊", R.string.cat_frozen,
        listOf("ice cream", "frozen", "pizza", "ice", "بستنی", "منجمد", "یخ", "پیتزا"),
    ),
    DRINKS(
        "drinks", "🧃", R.string.cat_drinks,
        listOf(
            "water", "juice", "soda", "coffee", "tea", "cola", "beer", "wine",
            "آب", "آبمیوه", "نوشابه", "قهوه", "چای", "دلستر", "شربت", "آب معدنی",
        ),
    ),
    SNACKS(
        "snacks", "🍫", R.string.cat_snacks,
        listOf(
            "chips", "chocolate", "candy", "nuts", "snack", "popcorn", "crackers",
            "پفک", "چیپس", "شکلات", "آجیل", "تخمه", "پاستیل", "کرانچی", "خرما",
        ),
    ),
    HOUSEHOLD(
        "household", "🧻", R.string.cat_household,
        listOf(
            "detergent", "tissue", "tissues", "paper", "towel", "bleach", "sponge", "trash", "foil", "batteries",
            "مایع ظرفشویی", "دستمال", "پودر", "سفیدکننده", "کیسه زباله", "اسکاج", "فویل", "باتری", "مایع",
        ),
    ),
    PERSONAL(
        "personal", "🧴", R.string.cat_personal,
        listOf(
            "shampoo", "soap", "toothpaste", "toothbrush", "deodorant", "lotion", "razor",
            "شامپو", "صابون", "خمیردندان", "خمیر دندان", "مسواک", "کرم", "ژیلت", "نوار بهداشتی",
        ),
    ),
    OTHER("other", "🛒", R.string.cat_other, emptyList());

    private val normalizedKeywords = keywords.map { it.normalizedName() }

    companion object {
        fun fromKey(key: String?): Category = entries.firstOrNull { it.key == key } ?: OTHER

        /** Best-effort category guess from an item name, in English or Persian. */
        fun guess(name: String): Category {
            val text = name.normalizedName()
            if (text.isEmpty()) return OTHER
            val tokens = text.split(' ')
            // Multi-word keywords first ("تخم مرغ" is dairy even though "مرغ" is meat).
            entries.forEach { category ->
                if (category.normalizedKeywords.any { ' ' in it && text.contains(it) }) return category
            }
            entries.forEach { category ->
                if (category.normalizedKeywords.any { keyword -> tokens.any { it.matches(keyword) } }) return category
            }
            return OTHER
        }

        private fun String.matches(keyword: String): Boolean =
            this == keyword || this == keyword + "s" || this == keyword + "es" || this == keyword + "ها"
    }
}
