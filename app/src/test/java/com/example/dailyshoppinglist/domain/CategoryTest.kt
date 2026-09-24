package com.example.dailyshoppinglist.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryTest {
    @Test fun english() {
        assertEquals(Category.DAIRY, Category.guess("Milk"))
        assertEquals(Category.PRODUCE, Category.guess("Green apples"))
        assertEquals(Category.FROZEN, Category.guess("Vanilla ice cream"))
        assertEquals(Category.PERSONAL, Category.guess("toothpaste"))
    }

    @Test fun persian() {
        assertEquals(Category.BAKERY, Category.guess("نان سنگک"))
        assertEquals(Category.BAKERY, Category.guess("شیرینی"))
        assertEquals(Category.MEAT, Category.guess("مرغ"))
        assertEquals(Category.PANTRY, Category.guess("برنج"))
    }

    @Test fun multiWordKeywordsWin() {
        // "تخم‌مرغ" (eggs) contains "مرغ" (chicken) but is dairy.
        assertEquals(Category.DAIRY, Category.guess("تخم‌مرغ"))
        assertEquals(Category.PANTRY, Category.guess("تن ماهی"))
    }

    @Test fun arabicLetterVariantsAreNormalized() = assertEquals(Category.DAIRY, Category.guess("پنير"))

    @Test fun unknownIsOther() = assertEquals(Category.OTHER, Category.guess("Birthday card"))

    @Test fun fromKeyFallsBack() {
        assertEquals(Category.DRINKS, Category.fromKey("drinks"))
        assertEquals(Category.OTHER, Category.fromKey("unknown"))
        assertEquals(Category.OTHER, Category.fromKey(null))
    }
}
