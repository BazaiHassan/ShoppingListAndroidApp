package com.example.dailyshoppinglist.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dailyshoppinglist.domain.Category

@Database(
    entities = [ShoppingItem::class, Trip::class, TripItem::class],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
    abstract fun historyDao(): HistoryDao

    companion object {
        /** Kept from v1 so existing lists survive the upgrade. */
        private const val NAME = "db_shopping_items"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME)
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
    }
}

/** v1 (Java app) had a single `tbl_items(id, itemName, isChecked)` table. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `shopping_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `category` TEXT NOT NULL, `note` TEXT NOT NULL, " +
                "`is_checked` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `checked_at` INTEGER)",
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `trips` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`completed_at` INTEGER NOT NULL)",
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `trip_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`trip_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `category` TEXT NOT NULL, " +
                "FOREIGN KEY(`trip_id`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_items_trip_id` ON `trip_items` (`trip_id`)")
        db.execSQL(
            "INSERT INTO `shopping_items` (`id`, `name`, `quantity`, `category`, `note`, `is_checked`, `created_at`, `checked_at`) " +
                "SELECT `id`, TRIM(`itemName`), 1, '${Category.OTHER.key}', '', `isChecked`, $now, " +
                "CASE WHEN `isChecked` != 0 THEN $now ELSE NULL END " +
                "FROM `tbl_items` WHERE `itemName` IS NOT NULL AND TRIM(`itemName`) != ''",
        )
        db.execSQL("DROP TABLE IF EXISTS `tbl_items`")
    }
}
