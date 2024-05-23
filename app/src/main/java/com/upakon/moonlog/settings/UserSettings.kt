package com.upakon.moonlog.settings

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * [UserSettings] represents the User's configuration
 */

data class UserSettings(
    val username: String = "",
    val lastPeriod: LocalDate = LocalDate.now(),
    val periodDuration: Int = 0,
    val cycleDuration: Int = 0,
    val pregnant: Boolean = false,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY
)
