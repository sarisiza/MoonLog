package com.upakon.moonlog.utils

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

}