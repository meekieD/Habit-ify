package com.dyusov.core.model

/**
 * Habit with its completion history.
 */
data class HabitWithCompletions(
    val habit: Habit,
    val completions: List<HabitCompletion>
)