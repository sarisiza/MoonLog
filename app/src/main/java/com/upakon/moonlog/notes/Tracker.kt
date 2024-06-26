package com.upakon.moonlog.notes

import android.util.Log
import com.upakon.moonlog.database.model.TrackerEntity

private const val TAG = "Tracker"
data class Tracker(
    val name: String,
    val unit: String
)

fun List<TrackerEntity>.toTrackers() : List<Tracker> =
    map {
        Tracker(
            it.name,
            it.unit
        )
    }
