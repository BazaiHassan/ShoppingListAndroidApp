package com.example.dailyshoppinglist.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuickEntryParserTest {
    @Test fun plainName() = assertEquals(QuickEntry("Milk", 1), QuickEntryParser.parse("  Milk  "))

    @Test fun blankIsIgnored() = assertNull(QuickEntryParser.parse("   "))

    @Test fun leadingQuantity() = assertEquals(QuickEntry("bread", 2), QuickEntryParser.parse("2 bread"))

    @Test fun leadingQuantityWithX() = assertEquals(QuickEntry("eggs", 12), QuickEntryParser.parse("12x eggs"))

    @Test fun trailingQuantity() = assertEquals(QuickEntry("eggs", 6), QuickEntryParser.parse("eggs × 6"))

    @Test fun persianDigits() = assertEquals(QuickEntry("نان", 3), QuickEntryParser.parse("۳ نان"))

    @Test fun numberInsideNameIsKept() = assertEquals(QuickEntry("Coke 330", 1), QuickEntryParser.parse("Coke 330"))

    @Test fun nameStartingWithDigitIsKept() = assertEquals(QuickEntry("7up", 1), QuickEntryParser.parse("7up"))

    @Test fun zeroBecomesOne() = assertEquals(QuickEntry("apples", 1), QuickEntryParser.parse("0 apples"))

    @Test fun collapsesWhitespace() = assertEquals(QuickEntry("olive oil", 1), QuickEntryParser.parse("olive    oil"))
}
