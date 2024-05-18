package com.upakon.moonlog.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upakon.moonlog.database.model.DayEntity
import com.upakon.moonlog.database.model.FeelingEntity

@Database(
    entities = [
        DayEntity::class,
        FeelingEntity::class
   ],
    version = 1,
    exportSchema = false
)
abstract class DailyDatabase : RoomDatabase() {
    abstract fun getDao() : DayDao
}