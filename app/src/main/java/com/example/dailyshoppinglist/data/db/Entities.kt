package com.example.dailyshoppinglist.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/** An item on the active shopping list. */
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Int = 1,
    val category: String,
    val note: String = "",
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "checked_at") val checkedAt: Long? = null,
)

/** A finished shopping trip, stored in history. */
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "completed_at") val completedAt: Long,
)

/** An item bought during a [Trip]. */
@Entity(
    tableName = "trip_items",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("trip_id")],
)
data class TripItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "trip_id") val tripId: Long,
    val name: String,
    val quantity: Int,
    val category: String,
)

data class TripWithItems(
    @Embedded val trip: Trip,
    @Relation(parentColumn = "id", entityColumn = "trip_id")
    val items: List<TripItem>,
)

/** Aggregated purchase statistics for an item name across all trips. */
data class FrequentItem(
    val name: String,
    val category: String,
    val count: Int,
    val lastBought: Long,
)
