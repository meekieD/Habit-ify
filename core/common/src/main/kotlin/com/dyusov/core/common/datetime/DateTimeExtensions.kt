package com.dyusov.core.common.datetime

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

// Получение текущей даты
fun LocalDate.Companion.nowClock(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return Clock.System.now().toLocalDateTime(timeZone).date
}

// Проверки относительно текущей даты
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
fun LocalDate.startOfMonth(): LocalDate {
    return LocalDate(year, month, 1)
}

fun LocalDate.endOfMonth(): LocalDate {
    val daysInMonth = when (month.ordinal + 1) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> throw IllegalStateException("Invalid month")
    }
    return LocalDate(year, month, daysInMonth)
}

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