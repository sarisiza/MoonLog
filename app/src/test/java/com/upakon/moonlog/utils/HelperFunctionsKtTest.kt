package com.upakon.moonlog.utils

import com.google.gson.Gson
import com.upakon.moonlog.notes.Tracker
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TAG = "HelperFunctionsKtTest"
class HelperFunctionsKtTest{


    @Test
    fun `test parsing a JSON to a map`(){

        val sJson = "{\n" +
                "  \"intercourse\":true,\n" +
                "  \"tracking\":[\n" +
                "    {\n" +
                "      \"label\":\"weight\",\n" +
                "      \"value\":68.5,\n" +
                "      \"unit\":\"kg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"label\":\"water\",\n" +
                "      \"value\":3,\n" +
                "      \"unit\":\"glass\"\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        val json = Json.parseToJsonElement(sJson)

        val map = json.parseJsonToMap()

        assertEquals(2,map.size)
        assertTrue(map["intercourse"] as Boolean)

        val tracking : List<Map<String,Any>> = map["tracking"] as List<Map<String,Any>>? ?: emptyList()

        assertEquals(2,tracking.size)
        assertEquals(tracking[0]["label"] as String,"weight")
        assertEquals(tracking[0]["value"] as Double,68.5,0.0)
        assertEquals(tracking[1]["value"] as Int,3)

    }

    @Test
    fun `test json conversion from tracker map`(){
        val weightTracker = Tracker("weight","kg")
        val waterTracker = Tracker("water","glasses")
        val trackMap = mapOf(weightTracker to 67.1, waterTracker to 1.0)
        val bigMap = mapOf("trackers" to trackMap)
        val gson = Gson()
        val json = gson.toJson(bigMap)
        val expectedJson = "\"{" +
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
                "\"value\":1.0" +
                "}" +
                "]" +
                "}\""
        val jsonMap = Json.parseToJsonElement(json).parseJsonToMap()
        val expectedMap = mapOf("trackers" to mapOf(weightTracker to 67.1, waterTracker to 1.0))
    }

}