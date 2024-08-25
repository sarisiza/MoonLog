package com.upakon.moonlog.notes

import com.upakon.moonlog.database.model.FeelingEntity

data class Feeling (
    val id: Int,
    val name: String,
    val emoji: String
){
    fun toDatabase(): FeelingEntity{
        return FeelingEntity(
            id,
            name,
            emoji
        )
    }

}