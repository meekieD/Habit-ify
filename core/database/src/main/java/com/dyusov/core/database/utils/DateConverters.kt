package com.dyusov.core.database.utils

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Type converters for Room database to handle date-related types.
 *
 * Provides conversion between LocalDate and Long timestamps for database storage.
 * Timestamps represent the start of day in the system's current timezone.
 */
class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { timestamp ->
            Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDayIn(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds()
    }
}