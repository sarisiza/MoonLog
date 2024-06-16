package com.upakon.moonlog.utils

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

class PrimitiveConverter(
    private val primitive: JsonPrimitive
) {

    private fun isInt() : Boolean = primitive.intOrNull != null
    private fun isBoolean() : Boolean = primitive.booleanOrNull != null
    private fun isDouble() : Boolean = primitive.doubleOrNull != null
    private fun isFloat() : Boolean = primitive.floatOrNull != null
    private fun isLong() : Boolean = primitive.longOrNull != null

    fun convertPrimitive() : Any {
        return if(isInt())
            primitive.int
        else if (isBoolean())
            primitive.boolean
        else if (isDouble())
            primitive.double
        else if (isFloat())
            primitive.float
        else if (isLong())
            primitive.long
        else primitive.content
    }

}