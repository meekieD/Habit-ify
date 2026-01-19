package com.dyusov.core.model

/**
 * A record of habit completion at specific time.
 *
 * @property id Unique identifier
 * @property habitId Reference to habit
 * @property timestamp When it was completed (millis)
 */
data class HabitCompletion(
    val id: Long,
    val habitId: Long,
    val timestamp: Long
)