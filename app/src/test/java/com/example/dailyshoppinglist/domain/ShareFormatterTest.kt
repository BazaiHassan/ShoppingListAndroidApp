package com.example.dailyshoppinglist.domain

import com.example.dailyshoppinglist.data.db.ShoppingItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareFormatterTest {
    @Test fun formatsItems() {
        val toBuy = listOf(
            ShoppingItem(id = 1, name = "Milk", quantity = 2, category = "dairy", createdAt = 0),
            ShoppingItem(id = 2, name = "Bread", category = "bakery", note = "whole grain", createdAt = 0),
        )
        val inCart = listOf(ShoppingItem(id = 3, name = "Eggs", category = "dairy", isChecked = true, createdAt = 0))
        val expected = "🛒 Groceries\n◻️ Milk ×2\n◻️ Bread — whole grain\n✅ Eggs"
        assertEquals(expected, ShareFormatter.format("Groceries", toBuy, inCart))
    }
}
