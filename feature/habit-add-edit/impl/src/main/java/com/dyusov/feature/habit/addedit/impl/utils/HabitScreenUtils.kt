package com.dyusov.feature.habit.addedit.impl.utils

import com.dyusov.core.model.FrequencyType
import com.dyusov.feature.habit.addedit.impl.R
import kotlinx.datetime.DayOfWeek

object HabitScreenUtils {
    val habitColors = listOf(
        0xFF8B5CF6.toInt(), // violet
        0xFF10B981.toInt(), // emerald
        0xFF3B82F6.toInt(), // blue
        0xFFE85C38.toInt(), // coral
        0xFFF59E0B.toInt(), // yellow
        0xFF06B6D4.toInt(), // cyan
    )

    val frequencySelectorOptions = listOf(
        FrequencyType.DAILY  to R.string.frequency_daily,
        FrequencyType.WEEKLY to R.string.frequency_weekly,
        FrequencyType.CUSTOM to R.string.frequency_custom,
    )

    val days = listOf(
        DayOfWeek.MONDAY    to R.string.day_monday,
        DayOfWeek.TUESDAY   to R.string.day_tuesday,
        DayOfWeek.WEDNESDAY to R.string.day_wednesday,
        DayOfWeek.THURSDAY  to R.string.day_thursday,
        DayOfWeek.FRIDAY    to R.string.day_friday,
        DayOfWeek.SATURDAY  to R.string.day_saturday,
        DayOfWeek.SUNDAY    to R.string.day_sunday,
    )
}