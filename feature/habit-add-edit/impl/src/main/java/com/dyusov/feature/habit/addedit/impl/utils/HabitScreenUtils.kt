package com.dyusov.feature.habit.addedit.impl.utils

import com.dyusov.core.model.FrequencyType
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
        FrequencyType.DAILY to "Daily",
        FrequencyType.WEEKLY to "Weekly",
        FrequencyType.CUSTOM to "Custom"
    )

    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S",
    )
}