package com.dyusov.feature.habit.addedit.impl.utils

import com.dyusov.core.model.FrequencyType
import com.dyusov.feature.habit.addedit.impl.R

object AddEditHabitScreenUtils {
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
}