package com.example.dailyshoppinglist.domain

import com.example.dailyshoppinglist.data.db.FrequentItem

object Suggestions {
    /**
     * Ranks previously bought items for the text being typed. Items already on the list are
     * skipped; prefix matches come first, then the most frequently and recently bought.
     */
    fun rank(query: String, history: List<FrequentItem>, onList: Set<String>, limit: Int = 8): List<FrequentItem> {
        val q = query.toLatinDigits().normalizedName()
        val candidates = history.filter { it.name.normalizedName() !in onList }
        if (q.isEmpty()) return candidates.take(limit)
        return candidates
            .filter { it.name.normalizedName().contains(q) && it.name.normalizedName() != q }
            .sortedWith(
                compareByDescending<FrequentItem> { it.name.normalizedName().startsWith(q) }
                    .thenByDescending { it.count }
                    .thenByDescending { it.lastBought },
            )
            .take(limit)
    }
}
