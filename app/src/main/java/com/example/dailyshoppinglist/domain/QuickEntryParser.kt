package com.example.dailyshoppinglist.domain

data class QuickEntry(val name: String, val quantity: Int)

/**
 * Parses quick input such as "2 bread", "2x milk", "eggs x 12" or "۳ نان".
 * A trailing number needs an explicit "x" so names like "Coke 330" are kept intact.
 */
object QuickEntryParser {
    private val leading = Regex("^(\\d{1,2})\\s*[x×]?\\s+(.+)$", RegexOption.IGNORE_CASE)
    private val trailing = Regex("^(.+?)\\s+[x×]\\s*(\\d{1,2})$", RegexOption.IGNORE_CASE)

    fun parse(raw: String): QuickEntry? {
        val text = raw.toLatinDigits().cleanItemName()
        if (text.isEmpty()) return null
        leading.matchEntire(text)?.let { match ->
            return QuickEntry(match.groupValues[2].cleanItemName(), match.groupValues[1].toQuantity())
        }
        trailing.matchEntire(text)?.let { match ->
            return QuickEntry(match.groupValues[1].cleanItemName(), match.groupValues[2].toQuantity())
        }
        return QuickEntry(text, 1)
    }

    private fun String.toQuantity(): Int = (toIntOrNull() ?: 1).coerceIn(1, MAX_QUANTITY)
}
