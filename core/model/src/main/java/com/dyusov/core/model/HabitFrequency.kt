package com.dyusov.core.model

import kotlinx.datetime.DayOfWeek

/**
 * Frequency pattern for habit execution.
 */
sealed class HabitFrequency {

    /** Every day */
    data object Daily : HabitFrequency()

    /**
     * Specific days of week.
     *
     * @property daysOfWeek Set of weekdays
     */
    data class Weekly(
        val daysOfWeek: Set<DayOfWeek>
    ) : HabitFrequency()

    /**
     * Specific days of month.
     *
     * Example: 11th and 22nd of every month
     *
     * @property daysOfMonth Set of day numbers (1..31)
     */
    data class Custom(
        val daysOfMonth: Set<Int>
    ) : HabitFrequency()
}