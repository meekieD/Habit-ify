package com.dyusov.core.common.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class SystemDateTimeProvider : DateTimeProvider {
    override fun now(): Instant = Clock.System.now()

    override fun nowLocalDate(timeZone: TimeZone): LocalDate {
        return now().toLocalDateTime(timeZone).date
    }

    override fun nowLocalDateTime(timeZone: TimeZone): LocalDateTime {
        return now().toLocalDateTime(timeZone)
    }
}