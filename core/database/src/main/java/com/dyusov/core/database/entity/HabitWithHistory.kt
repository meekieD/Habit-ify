package com.dyusov.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents a habit with its completion history.
 */
data class HabitWithHistory(
    @Embedded
    val habit: HabitDbModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val completions: List<HabitCompletionDbModel>
)
