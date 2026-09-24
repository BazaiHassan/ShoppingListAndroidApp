package com.example.dailyshoppinglist.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY created_at ASC, id ASC")
    fun observeItems(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE is_checked = 0")
    suspend fun uncheckedItems(): List<ShoppingItem>

    @Query("SELECT * FROM shopping_items WHERE is_checked = 1 ORDER BY checked_at ASC, id ASC")
    suspend fun checkedItems(): List<ShoppingItem>

    @Insert
    suspend fun insert(item: ShoppingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ShoppingItem>)

    @Update
    suspend fun update(item: ShoppingItem)

    @Delete
    suspend fun delete(item: ShoppingItem)

    @Query("UPDATE shopping_items SET is_checked = :checked, checked_at = :checkedAt WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean, checkedAt: Long?)

    @Query("DELETE FROM shopping_items WHERE is_checked = 1")
    suspend fun deleteChecked()

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()
}

@Dao
interface HistoryDao {
    @Transaction
    @Query("SELECT * FROM trips ORDER BY completed_at DESC")
    fun observeTrips(): Flow<List<TripWithItems>>

    @Insert
    suspend fun insertTrip(trip: Trip): Long

    @Insert
    suspend fun insertTripItems(items: List<TripItem>)

    @Query("DELETE FROM trip_items WHERE trip_id = :tripId")
    suspend fun deleteTripItems(tripId: Long)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: Long)

    @Query("DELETE FROM trip_items")
    suspend fun deleteAllTripItems()

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query(
        """
        SELECT ti.name AS name, ti.category AS category, COUNT(*) AS count, MAX(t.completed_at) AS lastBought
        FROM trip_items ti INNER JOIN trips t ON t.id = ti.trip_id
        GROUP BY LOWER(TRIM(ti.name))
        ORDER BY count DESC, lastBought DESC
        LIMIT :limit
        """,
    )
    fun observeFrequent(limit: Int): Flow<List<FrequentItem>>

    @Query("SELECT category FROM trip_items WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) ORDER BY id DESC LIMIT 1")
    suspend fun lastCategoryFor(name: String): String?
}
