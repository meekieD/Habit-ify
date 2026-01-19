package com.dyusov.core.model

import java.time.DayOfWeek

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
     * Flexible goal: N times per period.
     *
     * Example: 3 times per week, 5 times per month
     *
     * @property period Time period (week or month)
     * @property timesPerPeriod How many times to complete (must be positive)
     */
    data class Custom(
        val period: PeriodType,
        val timesPerPeriod: Int
    ) : HabitFrequency()
}

/**
 * Time period type for custom frequency.
 */
enum class PeriodType {
    WEEK, MONTH
}