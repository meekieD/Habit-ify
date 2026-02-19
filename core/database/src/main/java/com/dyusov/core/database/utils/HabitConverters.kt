package com.dyusov.core.database.utils

import androidx.room.TypeConverter
import com.dyusov.core.model.FrequencyType
import kotlinx.datetime.DayOfWeek

/**
 * Type converters for Room database to handle custom types in habit-related entities.
 *
 * Provides conversion between Kotlin types (enums, sets) and database-compatible types (strings).
 */
class HabitConverters {
    @TypeConverter
    fun fromFrequencyType(value: FrequencyType): String = value.name

    @TypeConverter
    fun toFrequencyType(value: String): FrequencyType = enumValueOf(value)

    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String?): Set<DayOfWeek>? {
        return value
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { enumValueOf<DayOfWeek>(it) }
            ?.toSet()
    }

    @TypeConverter
    fun fromIntSet(days: Set<Int>?): String? {
        return days?.joinToString(",")
    }

    @TypeConverter
    fun toIntSet(value: String?): Set<Int>? {
        return value
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map {
                it.toInt()
            }
            ?.toSet()
    }
}