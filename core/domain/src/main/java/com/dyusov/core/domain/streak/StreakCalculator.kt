package com.dyusov.core.domain.streak

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.minus
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.startOfWeek
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitFrequency
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import javax.inject.Inject

sealed interface StreakCalculator {
    fun calculate(
        completedDates: Set<LocalDate>,
        habit: Habit,
        timeZone: TimeZone
    ): Int

    class DailyStreakCalculator @Inject constructor(
        private val dateTimeProvider: DateTimeProvider
    ) : StreakCalculator {

        override fun calculate(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            var streak = 0

            val today = dateTimeProvider.nowLocalDate()
            var current = if (today in completedDates) today else today.minus(1, DateTimeUnit.DAY)

            while (current in completedDates) {
                streak++
                current = current.minus(1, DateTimeUnit.DAY)
            }

            return streak
        }
    }

    class WeeklyStreakCalculator @Inject constructor(
        private val dateTimeProvider: DateTimeProvider
    ) : StreakCalculator {

        override fun calculate(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            val frequency = habit.frequency as? HabitFrequency.Weekly ?: return 0
            val requiredDays = frequency.daysOfWeek
            if (requiredDays.isEmpty()) return 0

            var streak = 0

            val today = dateTimeProvider.nowLocalDate()
            var weekStart = today.startOfWeek()

            while (true) {
                val requiredDatesInWeek = requiredDays
                    .map { weekStart.plus(it.ordinal, DateTimeUnit.DAY) }
                    .filter { it <= today }

                if (requiredDatesInWeek.isEmpty()) {
                    weekStart = weekStart.minus(1, DateTimeUnit.WEEK)
                    continue
                }

                val isWeekComplete = requiredDatesInWeek.all { it in completedDates }

                if (!isWeekComplete) break

                streak += requiredDays.size
                weekStart = weekStart.minus(1, DateTimeUnit.WEEK)
            }
            return streak
        }
    }

    class MonthlyStreakCalculator @Inject constructor(
        private val dateTimeProvider: DateTimeProvider
    ) : StreakCalculator {

        override fun calculate(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            val frequency = habit.frequency as? HabitFrequency.Custom ?: return 0
            val requiredDays = frequency.daysOfMonth
            if (requiredDays.isEmpty()) return 0

            var streak = 0

            val today = dateTimeProvider.nowLocalDate()
            var month = YearMonth.now(timeZone)

            while (true) {
                val requiredDatesInMonth = requiredDays
                    .filter { it <= month.numberOfDays }
                    .map { LocalDate(month.year, month.month, it) }
                    .filter { it <= today }

                if (requiredDatesInMonth.isEmpty()) {
                    month = month.minus(1)
                    continue
                }

                val isMonthComplete = requiredDatesInMonth.all { it in completedDates }

                if (!isMonthComplete) break

                streak += requiredDays.size

                month = month.minus(1)
            }
            return streak
        }
    }
}
