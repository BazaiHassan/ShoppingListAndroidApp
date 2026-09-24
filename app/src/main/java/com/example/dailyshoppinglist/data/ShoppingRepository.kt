package com.example.dailyshoppinglist.data

import androidx.room.withTransaction
import com.example.dailyshoppinglist.data.db.AppDatabase
import com.example.dailyshoppinglist.data.db.FrequentItem
import com.example.dailyshoppinglist.data.db.ShoppingItem
import com.example.dailyshoppinglist.data.db.Trip
import com.example.dailyshoppinglist.data.db.TripItem
import com.example.dailyshoppinglist.data.db.TripWithItems
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.domain.MAX_QUANTITY
import com.example.dailyshoppinglist.domain.cleanItemName
import com.example.dailyshoppinglist.domain.normalizedName
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(
    private val db: AppDatabase,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val itemDao = db.shoppingDao()
    private val historyDao = db.historyDao()

    val items: Flow<List<ShoppingItem>> = itemDao.observeItems()
    val trips: Flow<List<TripWithItems>> = historyDao.observeTrips()

    fun frequentItems(limit: Int): Flow<List<FrequentItem>> = historyDao.observeFrequent(limit)

    /**
     * Adds an item. If the same item is already waiting on the list, its quantity is
     * increased instead of creating a duplicate. When no category is given it is taken
     * from history, or guessed from the name.
     */
    suspend fun addItem(name: String, quantity: Int = 1, category: Category? = null, note: String = "") {
        val clean = name.cleanItemName()
        if (clean.isEmpty()) return
        db.withTransaction {
            val key = clean.normalizedName()
            val existing = itemDao.uncheckedItems().firstOrNull { it.name.normalizedName() == key }
            if (existing != null) {
                itemDao.update(existing.copy(quantity = (existing.quantity + quantity).coerceIn(1, MAX_QUANTITY)))
            } else {
                val resolved = category
                    ?: historyDao.lastCategoryFor(clean)?.let(Category::fromKey)
                    ?: Category.guess(clean)
                itemDao.insert(
                    ShoppingItem(
                        name = clean,
                        quantity = quantity.coerceIn(1, MAX_QUANTITY),
                        category = resolved.key,
                        note = note.trim(),
                        createdAt = clock(),
                    ),
                )
            }
        }
    }

    suspend fun addAll(items: List<TripItem>) = db.withTransaction {
        items.forEach { addItem(it.name, it.quantity, Category.fromKey(it.category)) }
    }

    suspend fun update(item: ShoppingItem) {
        val clean = item.name.cleanItemName()
        if (clean.isEmpty()) return
        itemDao.update(item.copy(name = clean, note = item.note.trim(), quantity = item.quantity.coerceIn(1, MAX_QUANTITY)))
    }

    suspend fun setChecked(item: ShoppingItem, checked: Boolean) =
        itemDao.setChecked(item.id, checked, if (checked) clock() else null)

    suspend fun delete(item: ShoppingItem) = itemDao.delete(item)

    /** Puts previously removed items back with their original ids (used by undo). */
    suspend fun restore(items: List<ShoppingItem>) = itemDao.upsertAll(items)

    suspend fun deleteChecked() = itemDao.deleteChecked()

    suspend fun deleteAll() = itemDao.deleteAll()

    /** Moves every checked item into a new history trip. Returns the number of items saved. */
    suspend fun finishShopping(): Int = db.withTransaction {
        val checked = itemDao.checkedItems()
        if (checked.isEmpty()) return@withTransaction 0
        val tripId = historyDao.insertTrip(Trip(completedAt = clock()))
        historyDao.insertTripItems(
            checked.map { TripItem(tripId = tripId, name = it.name, quantity = it.quantity, category = it.category) },
        )
        itemDao.deleteChecked()
        checked.size
    }

    suspend fun deleteTrip(tripId: Long) = db.withTransaction {
        historyDao.deleteTripItems(tripId)
        historyDao.deleteTrip(tripId)
    }

    suspend fun clearHistory() = db.withTransaction {
        historyDao.deleteAllTripItems()
        historyDao.deleteAllTrips()
    }
}
