package com.dyusov.core.domain.streak

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.minus
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.plus
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
    fun calculate(completedDates: Set<LocalDate>, habit: Habit, timeZone: TimeZone): Int
    fun calculateBest(completedDates: Set<LocalDate>, habit: Habit, timeZone: TimeZone): Int

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

        override fun calculateBest(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            if (completedDates.isEmpty()) {
                return 0
            }

            val sorted = completedDates.sorted()
            var best = 1
            var current = 1

            for (i in 1 until sorted.size) {
                current =
                    if (sorted[i] == sorted[i - 1].plus(1, DateTimeUnit.DAY)) current + 1 else 1
                if (current > best) best = current
            }

            return best
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

                if (!requiredDatesInWeek.all { it in completedDates }) break

                streak += requiredDays.size
                weekStart = weekStart.minus(1, DateTimeUnit.WEEK)
            }
            return streak
        }

        override fun calculateBest(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            val frequency = habit.frequency as? HabitFrequency.Weekly ?: return 0
            val requiredDays = frequency.daysOfWeek
            if (requiredDays.isEmpty() || completedDates.isEmpty()) return 0

            val firstDate = completedDates.min()
            var weekStart = firstDate.startOfWeek()
            val today = dateTimeProvider.nowLocalDate()

            var best = 0
            var current = 0

            while (weekStart <= today) {
                val requiredDatesInWeek = requiredDays
                    .map { weekStart.plus(it.ordinal, DateTimeUnit.DAY) }
                    .filter { it <= today }

                if (requiredDatesInWeek.isNotEmpty()) {
                    if (requiredDatesInWeek.all { it in completedDates }) {
                        current += requiredDays.size
                        if (current > best) best = current
                    } else {
                        current = 0
                    }
                }
                weekStart = weekStart.plus(1, DateTimeUnit.WEEK)
            }
            return best
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

                if (!requiredDatesInMonth.all { it in completedDates }) break

                streak += requiredDays.size

                month = month.minus(1)
            }
            return streak
        }

        override fun calculateBest(
            completedDates: Set<LocalDate>,
            habit: Habit,
            timeZone: TimeZone
        ): Int {
            val frequency = habit.frequency as? HabitFrequency.Custom ?: return 0
            val requiredDays = frequency.daysOfMonth
            if (requiredDays.isEmpty() || completedDates.isEmpty()) return 0

            val firstDate = completedDates.min()
            var month = YearMonth(firstDate.year, firstDate.month)
            val today = dateTimeProvider.nowLocalDate()
            val currentMonth = YearMonth.now(timeZone)

            var best = 0
            var current = 0

            while (month <= currentMonth) {
                val requiredDatesInMonth = requiredDays
                    .filter { it <= month.numberOfDays }
                    .map { LocalDate(month.year, month.month, it) }
                    .filter { it <= today }

                if (requiredDatesInMonth.isNotEmpty()) {
                    if (requiredDatesInMonth.all { it in completedDates }) {
                        current += requiredDays.size
                        if (current > best) best = current
                    } else {
                        current = 0
                    }
                }
                month = month.plus(1)
            }
            return best
        }
    }
}
