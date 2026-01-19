package com.dyusov.core.model

/**
 * A habit that user wants to track.
 *
 * @property id Unique identifier
 * @property name Habit name
 * @property description Optional info
 * @property frequency How often to perform
 * @property color ARGB color for UI
 * @property createdAt Creation timestamp (millis)
 * @property updatedAt Last update timestamp (millis)
 */
data class Habit(
    val id: Long,
    val name: String,
    val description: String?,
    val frequency: HabitFrequency,
    val color: Int,
    val createdAt: Long,
    val updatedAt: Long
)