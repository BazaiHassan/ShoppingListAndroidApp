package com.example.dailyshoppinglist.domain

import com.example.dailyshoppinglist.data.db.ShoppingItem

object ShareFormatter {
    fun format(title: String, toBuy: List<ShoppingItem>, inCart: List<ShoppingItem>): String = buildString {
        append("🛒 ").append(title)
        toBuy.forEach { append('\n').append("◻️ ").append(line(it)) }
        inCart.forEach { append('\n').append("✅ ").append(line(it)) }
    }

    private fun line(item: ShoppingItem): String = buildString {
        append(item.name)
        if (item.quantity > 1) append(" ×").append(item.quantity)
        if (item.note.isNotBlank()) append(" — ").append(item.note.trim())
    }
}
