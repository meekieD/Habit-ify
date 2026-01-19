package com.dyusov.core.model

/**
 * Streak statistics for a habit.
 *
 * Computed from [HabitCompletion] records
 *
 * @property current Current streak count
 * @property longest Best streak ever achieved
 */
data class HabitStreak(
    val current: Int,
    val longest: Int
)