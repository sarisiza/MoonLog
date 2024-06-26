package com.upakon.moonlog.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upakon.moonlog.database.model.DayEntity
import com.upakon.moonlog.database.model.FeelingEntity
import com.upakon.moonlog.database.model.TrackerEntity
import com.upakon.moonlog.notes.Feeling
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    /**
     * Method to insert notes on a day
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun writeNotes(dayNotes: DayEntity)

    /**
     * Method to get the notes from a day
     */
    @Query("SELECT * FROM dailyNotes WHERE day = :day")
    fun readNote(day: String): DayEntity?

    @Delete
    suspend fun deleteNote(dayNotes: DayEntity)

    /**
     * Method to insert a new feeling on the table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFeeling(feelingEntity: FeelingEntity)

    /**
     * Method to get all the feelings from the database
     */
    @Query("SELECT * FROM feelingsTable")
    suspend fun getFeelings(): List<FeelingEntity>

    /**
     * Method to delete a feeling from the database
     */
    @Delete
    suspend fun deleteFeeling(feeling: FeelingEntity)

    /**
     * Method to add a new tracker
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTracker(trackerEntity: TrackerEntity)

    @Query("SELECT * FROM trackers")
    suspend fun getTrackers(): List<TrackerEntity>

    @Delete
    suspend fun deleteTracker(trackerEntity: TrackerEntity)

}