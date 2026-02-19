package com.dyusov.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dyusov.core.model.FrequencyType
import kotlinx.datetime.DayOfWeek

/**
 * Database entity representing a habit.
 *
 * Frequency is stored using [frequencyType] to determine which fields apply:
 * - DAILY: No extra fields needed
 * - WEEKLY: Uses [weeklyDays]
 * - CUSTOM: Uses [monthlyDays]
 */
@Entity(tableName = "habits")
data class HabitDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String?,
    val isCompletedToday: Boolean,
    val frequencyType: FrequencyType,
    val weeklyDays: Set<DayOfWeek>?,
    val monthlyDays: Set<Int>?,
    val createdAt: Long,
    val updatedAt: Long,
    val color: String?
)