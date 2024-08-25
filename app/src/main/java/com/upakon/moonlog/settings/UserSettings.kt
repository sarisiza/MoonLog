package com.upakon.moonlog.settings

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * [UserSettings] represents the User's configuration
 */

data class UserSettings(
    val username: String? = null,
    val lastPeriod: LocalDate? = null,
    val periodDuration: Int? = null,
    val cycleDuration: Int? = null,
    val pregnant: Boolean? = null,
    val firstDayOfWeek: DayOfWeek? = null
)
