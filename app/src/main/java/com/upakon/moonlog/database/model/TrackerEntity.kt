package com.upakon.moonlog.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upakon.moonlog.notes.Tracker


@Entity(tableName = "trackers")
data class TrackerEntity(
    @PrimaryKey val name: String,
    val unit: String
)

fun Tracker.toDatabase() : TrackerEntity = TrackerEntity(this.name,this.unit)
