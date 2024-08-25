package com.upakon.moonlog.utils

import com.google.gson.Gson
import com.upakon.moonlog.notes.Tracker
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TAG = "HelperFunctionsKtTest"
class HelperFunctionsKtTest{


    @Test
    fun `test trackers json parsing`(){
        val weight = Tracker("weight","kg")
        val water = Tracker("water","glasses")
        val trackMap = mutableMapOf(
            weight to 67.1,
            water to 8.0
        )
        val expectedMap = mutableMapOf(
            "trackers" to trackMap
        )
        val resultJson = Json.encodeToString(JsonElement.serializer(),expectedMap.toJson())
        val expectedJson = "{"+
                "\"trackers\":[" +
                "{" +
                "\"tracker\":{" +
                "\"name\":\"weight\"," +
                "\"unit\":\"kg\"" +
                "}," +
                "\"value\":67.1" +
                "}," +
                "{" +
                "\"tracker\":{" +
                "\"name\":\"water\"," +
                "\"unit\":\"glasses\"" +
                "}," +
                "\"value\":8.0" +
                "}" +
                "]" +
                "}"
        assertEquals(expectedJson,resultJson)
        val resultMap = Json.parseToJsonElement(resultJson).parseJsonToMap()
        assertEquals(expectedMap,resultMap)
    }

}