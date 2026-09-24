package com.example.dailyshoppinglist.domain

import com.example.dailyshoppinglist.data.db.FrequentItem
import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestionsTest {
    private val history = listOf(
        FrequentItem("Milk", "dairy", count = 9, lastBought = 100),
        FrequentItem("Bread", "bakery", count = 7, lastBought = 300),
        FrequentItem("Almond milk", "dairy", count = 12, lastBought = 200),
        FrequentItem("Eggs", "dairy", count = 7, lastBought = 400),
    )

    @Test fun emptyQueryReturnsMostFrequentNotOnList() {
        val result = Suggestions.rank("", history, onList = setOf("bread"))
        assertEquals(listOf("Milk", "Almond milk", "Eggs"), result.map { it.name })
    }

    @Test fun prefixMatchesComeFirst() {
        val result = Suggestions.rank("mi", history, onList = emptySet())
        assertEquals(listOf("Milk", "Almond milk"), result.map { it.name })
    }

    @Test fun exactMatchIsNotSuggested() {
        val result = Suggestions.rank("milk", history, onList = emptySet())
        assertEquals(listOf("Almond milk"), result.map { it.name })
    }

    @Test fun respectsLimit() = assertEquals(2, Suggestions.rank("", history, emptySet(), limit = 2).size)
}
