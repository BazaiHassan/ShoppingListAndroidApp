package com.example.dailyshoppinglist.domain

const val MAX_QUANTITY = 99

private val whitespace = Regex("\\s+")

/** Trims and collapses whitespace so names are stored consistently. */
fun String.cleanItemName(): String = trim().replace(whitespace, " ")

/** Key used to compare item names: case-insensitive and unifying Arabic/Persian letter variants. */
fun String.normalizedName(): String = cleanItemName()
    .replace('ي', 'ی')
    .replace('ى', 'ی')
    .replace('ك', 'ک')
    .replace("‌", " ")
    .lowercase()

/** Converts Persian (۰-۹) and Arabic-Indic (٠-٩) digits to ASCII digits. */
fun String.toLatinDigits(): String = buildString(length) {
    for (c in this@toLatinDigits) {
        append(
            when (c) {
                in '۰'..'۹' -> '0' + (c - '۰')
                in '٠'..'٩' -> '0' + (c - '٠')
                else -> c
            },
        )
    }
}
