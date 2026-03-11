package com.dyusov.core.domain.streak

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.toLocalDate
import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.common.utils.map
import com.dyusov.core.data.repo.HabitCompletionRepository
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitFrequency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository,
    private val dailyStreakCalculator: StreakCalculator.DailyStreakCalculator,
    private val weeklyStreakCalculator: StreakCalculator.WeeklyStreakCalculator,
    private val monthlyStreakCalculator: StreakCalculator.MonthlyStreakCalculator,
    private val dateTimeProvider: DateTimeProvider
) {
    operator fun invoke(habit: Habit): Flow<MyResult<StreakResult, MyError>> {
        return habitCompletionRepository.getCompletionsByHabitId(habit.id)
            .map { result ->
                result.map { completions ->
                    val completedDates = completions
                        .map { it.timestamp.toLocalDate(dateTimeProvider.timeZone()) }
                        .toSet()

                    val calculator: StreakCalculator = when (habit.frequency) {
                        is HabitFrequency.Daily -> dailyStreakCalculator
                        is HabitFrequency.Weekly -> weeklyStreakCalculator
                        is HabitFrequency.Custom -> monthlyStreakCalculator
                    }

                    StreakResult(
                        current = calculator.calculate(
                            completedDates = completedDates,
                            habit = habit,
                            timeZone = dateTimeProvider.timeZone()
                        ),
                        best = calculator.calculateBest(
                            completedDates = completedDates,
                            habit = habit,
                            timeZone = dateTimeProvider.timeZone()
                        )
                    )
                }
            }
    }
}