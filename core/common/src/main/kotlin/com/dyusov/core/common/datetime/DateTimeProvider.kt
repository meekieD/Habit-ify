package com.dyusov.core.common.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

interface DateTimeProvider {
    fun now(): Instant

    fun nowLocalDate(timeZone: TimeZone = timeZone()): LocalDate

    fun nowLocalDateTime(timeZone: TimeZone = timeZone()): LocalDateTime

    fun timeZone(): TimeZone

    fun getMillisToNextMidnight(timeZone: TimeZone = timeZone()): Long
}