package com.dyusov.core.common.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

interface DateTimeProvider {
    fun now(): Instant

    fun nowLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate

    fun nowLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime
}