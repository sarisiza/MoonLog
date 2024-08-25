package com.upakon.moonlog.utils

import android.util.Log
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.notes.Tracker
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "HelperFunctions"
/**
 * Function to convert EPOCH to LocalDate
 *
 * @return date from epoch milis
 */
fun Long?.toLocalDate(): LocalDate {
    return this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .plusDays(1)
    } ?: LocalDate.now()
}

fun YearMonth.getDisplayName() : String{
    return "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
}

fun List<DayOfWeek>.sortByFirst(firstDay : DayOfWeek) : List<DayOfWeek>{
    val result = mutableListOf<DayOfWeek>()
    result.addAll(subList(indexOf(firstDay),size))
    result.addAll(subList(0,indexOf(firstDay)))
    return result
}

fun LocalDate.isInMonth(yearMonth: YearMonth) : Boolean{
    return month == yearMonth.month && year == yearMonth.year
}

fun JsonElement.parseJsonToMap() : MutableMap<String,Any>{
    val result = jsonObject.mapValues {json ->
        when(json.key){
            "trackers" -> {
                val map = mutableMapOf<Tracker,Double>()
                (json.value as JsonArray).forEach {
                    val trackObj = it as JsonObject
                    val tracker = trackObj["tracker"]?.parseJsonToMap()
                    val value = trackObj["value"]?.jsonPrimitive?.doubleOrNull
                    if (tracker != null && value != null){
                        val name = tracker["name"] as String
                        val unit = tracker["unit"] as String
                        map[Tracker(name,unit)] = value
                    }
                }
                map
            }
            else -> {
                json.value.jsonPrimitive.content
            }
        }
    }
    return result.toMutableMap()
}

fun Map<String,Any>.toJson() : JsonElement{
    val jsonMap = mutableMapOf<String,JsonElement>()
    this.forEach { element ->
        when (element.key) {
            "trackers" -> {
                val tracker = element.value as Map<Tracker,Double>
                val trackList = tracker.map {
                    mutableMapOf(
                        "tracker" to mapOf(
                            "name" to it.key.name,
                            "unit" to it.key.unit
                        ),
                        "value" to it.value
                    ).toJson()
                }
                jsonMap["trackers"] = JsonArray(trackList)
            }
            "tracker" -> {
                val trackElement = element.value as Map<String,String>
                jsonMap["tracker"] = trackElement.toJson()
            }
            "value" -> {
                jsonMap["value"] = JsonPrimitive(element.value as Double)
            }
            else -> {
                jsonMap[element.key] = JsonPrimitive(element.value as String)
            }
        }
    }
    return JsonObject(jsonMap)
}



fun List<Any>.getNextId() : Int = this.size + 1