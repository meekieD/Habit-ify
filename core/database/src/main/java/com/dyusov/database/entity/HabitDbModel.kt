package com.dyusov.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dyusov.core.model.PeriodType
import java.time.DayOfWeek

/**
 * Database entity representing a habit.
 *
 * Frequency is stored using [frequencyType] to determine which fields apply:
 * - DAILY: No extra fields needed
 * - WEEKLY: Uses [weeklyDays]
 * - CUSTOM: Uses [periodType] + [timesPerPeriod]
 */
@Entity(tableName = "habits")
data class HabitDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String?,
    val frequencyType: FrequencyType,
    val weeklyDays: Set<DayOfWeek>?,
    val periodType: PeriodType?,
    val timesPerPeriod: Int?,
    val createdAt: Long,
    val updatedAt: Long
)