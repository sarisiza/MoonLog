package com.upakon.moonlog.database.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.upakon.moonlog.database.model.DayEntity
import com.upakon.moonlog.database.model.FeelingEntity
import com.upakon.moonlog.database.model.TrackerEntity

@Database(
    entities = [
        DayEntity::class,
        FeelingEntity::class,
        TrackerEntity::class
   ],
    version = 3,
    exportSchema = false,
)
abstract class DailyDatabase : RoomDatabase() {
    abstract fun getDao() : DayDao
}

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE dailyNotes ADD COLUMN notes STRING")
        db.execSQL("ALTER TABLE dailyNotes DROP COLUMN intercourse")
        db.execSQL("ALTER TABLE dailyNotes DROP COLUMN wasProtected")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE `trackers` (`name` TEXT NOT NULL DEFAULT '', `unit` TEXT NOT NULL DEFAULT '', PRIMARY KEY(`name`))")
    }

}