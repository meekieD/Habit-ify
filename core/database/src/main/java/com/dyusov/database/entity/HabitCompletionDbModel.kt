package com.dyusov.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Database entity for tracking habit completions.
 */
@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitDbModel::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class HabitCompletionDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val habitId: Long,
    val timestamp: Long
)
