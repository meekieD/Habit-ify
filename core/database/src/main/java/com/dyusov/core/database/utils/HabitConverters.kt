package com.dyusov.core.database.utils

import androidx.room.TypeConverter
import com.dyusov.core.database.entity.FrequencyType
import com.dyusov.core.model.PeriodType
import java.time.DayOfWeek

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
    fun fromPeriodType(value: PeriodType?): String? = value?.name

    @TypeConverter
    fun toPeriodType(value: String?): PeriodType? {
        return value?.let {
            try {
                enumValueOf<PeriodType>(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String?): Set<DayOfWeek>? {
        return value
            ?.takeIf {
                it.isNotEmpty()
            }
            ?.split(",")
            ?.map {
                enumValueOf<DayOfWeek>(it)
            }
            ?.toSet()
    }
}