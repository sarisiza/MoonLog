package com.upakon.moonlog.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upakon.moonlog.notes.Feeling

@Entity(tableName = "feelingsTable")
data class FeelingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val emoji: String
)

fun List<FeelingEntity>.toFeelings() : List<Feeling>{
    return this.map {
        Feeling(
            it.id,
            it.name,
            it.emoji
        )
    }
}

