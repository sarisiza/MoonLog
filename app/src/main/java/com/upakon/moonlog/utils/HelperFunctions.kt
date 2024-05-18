package com.upakon.moonlog.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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