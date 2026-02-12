package com.dyusov.core.domain.tracking

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitCompletionRepository
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.data.utils.toStartOfDayTimestamp
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * Toggle the completion state for a given habit on a specific date.
 * - If there is a completion on that date, delete it.
 * - If there is no completion on that date, add a completion at the start of that day.
 */
class ToggleHabitCompletionOnDateUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val dateTimeProvider: DateTimeProvider
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate) {
        val isCompleted = when (
            val result = habitCompletionRepository.isHabitCompletedOnDate(
                habitId,
                date
            )
        ) {
            is MyResult.Success -> result.data
            is MyResult.Error -> return
        }

        if (isCompleted) {
            habitCompletionRepository.deleteCompletionByDate(habitId, date)
        } else {
            habitCompletionRepository.addCompletion(habitId, date.toStartOfDayTimestamp())
        }

        if (date == dateTimeProvider.nowLocalDate()) {
            habitRepository.toggleCompletedToday(habitId)
        }
    }
}
