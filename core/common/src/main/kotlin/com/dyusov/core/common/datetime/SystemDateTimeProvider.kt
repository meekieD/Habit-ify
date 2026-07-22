package com.dyusov.core.common.datetime

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class SystemDateTimeProvider @Inject constructor() : DateTimeProvider {
    override fun now(): Instant = Clock.System.now()

    override fun nowLocalDate(timeZone: TimeZone): LocalDate {
        return now().toLocalDateTime(timeZone).date
    }

    override fun nowLocalDateTime(timeZone: TimeZone): LocalDateTime {
        return now().toLocalDateTime(timeZone)
    }

    override fun timeZone(): TimeZone {
        return TimeZone.currentSystemDefault()
    }

    override fun getMillisToNextMidnight(timeZone: TimeZone): Long {
        val currentInstant = now()

        val tomorrowLocalDate = currentInstant
            .toLocalDateTime(timeZone)
            .date
            .plus(1, DateTimeUnit.DAY)
        val nextMidnightInstant = tomorrowLocalDate
            .atStartOfDayIn(timeZone)

        return nextMidnightInstant.toEpochMilliseconds() - currentInstant.toEpochMilliseconds()
    }
}