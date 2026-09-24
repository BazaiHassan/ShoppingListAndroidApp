package com.example.dailyshoppinglist.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class TextTest {
    @Test fun latinDigits() = assertEquals("0123456789 0123456789", "۰۱۲۳۴۵۶۷۸۹ ٠١٢٣٤٥٦٧٨٩".toLatinDigits())

    @Test fun normalizedName() = assertEquals("کیک شکلاتی", "  كيك   شکلاتي ".normalizedName())

    @Test fun cleanItemName() = assertEquals("Olive oil", " Olive \t oil ".cleanItemName())
}
