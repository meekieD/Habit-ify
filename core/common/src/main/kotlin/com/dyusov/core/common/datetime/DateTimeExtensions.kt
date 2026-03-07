package com.dyusov.core.common.datetime

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun LocalDate.Companion.nowClock(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Clock.System.now().toLocalDateTime(timeZone).date
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date

fun LocalDate.toTimestamp(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long =
    this.atStartOfDayIn(timeZone).toEpochMilliseconds()

fun YearMonth.plus(months: Int): YearMonth {
    var y = this.year
    var m = this.month.number + months
    while (m > 12) {
        m -= 12; y++
    }
    while (m < 1) {
        m += 12; y--
    }
    return YearMonth(y, m)
}

fun YearMonth.minus(months: Int): YearMonth = plus(-months)

fun LocalDate.isToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    return this == LocalDate.nowClock(timeZone)
}

fun LocalDate.isTomorrow(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    return this == LocalDate.nowClock(timeZone).plus(1, DateTimeUnit.DAY)
}

fun LocalDate.isYesterday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    return this == LocalDate.nowClock(timeZone).minus(1, DateTimeUnit.DAY)
}

fun LocalDate.isPast(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    return this < LocalDate.nowClock(timeZone)
}

fun LocalDate.isFuture(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    return this > LocalDate.nowClock(timeZone)
}

// Вычисление разницы в днях
fun LocalDate.daysFromToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int {
    return LocalDate.nowClock(timeZone).daysUntil(this)
}

fun LocalDate.daysUntilToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int {
    return this.daysUntil(LocalDate.nowClock(timeZone))
}

// Начало и конец недели
fun LocalDate.startOfWeek(): LocalDate {
    val dayOfWeek = this.dayOfWeek.ordinal // 0 = Monday, 6 = Sunday
    return this.minus(DatePeriod(days = dayOfWeek))
}

fun LocalDate.endOfWeek(): LocalDate {
    return startOfWeek().plus(DatePeriod(days = 6))
}

// Начало и конец месяца
fun YearMonth.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): YearMonth {
    val date = LocalDate.nowClock(timeZone)
    return YearMonth(date.year, date.month)
}

fun YearMonth.toStartOfMonthTimestamp(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long = firstDay.atStartOfDayIn(timeZone).toEpochMilliseconds()

fun YearMonth.toEndOfMonthTimestamp(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long = lastDay.plus(1, DateTimeUnit.DAY)
    .atStartOfDayIn(timeZone)
    .minus(1, DateTimeUnit.MILLISECOND)
    .toEpochMilliseconds()

// Проверка на високосный год
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

// Диапазон дат
fun LocalDate.rangeTo(other: LocalDate): List<LocalDate> {
    val days = this.daysUntil(other)

    if (days < 0) return emptyList()

    return (0..days).map { this.plus(it, DateTimeUnit.DAY) }
}