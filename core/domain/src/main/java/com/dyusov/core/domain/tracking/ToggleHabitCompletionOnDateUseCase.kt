package com.dyusov.core.domain.tracking

import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.data.repo.HabitCompletionRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import com.dyusov.core.data.utils.toStartOfDayTimestamp

/**
 * Toggle the completion state for a given habit on a specific date.
 * - If there is a completion on that date, delete it.
 * - If there is no completion on that date, add a completion at the start of that day.
 */
class ToggleHabitCompletionOnDateUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate) {
        habitCompletionRepository.isHabitCompletedOnDate(
            habitId = habitId,
            date = date
        ).onSuccess { isCompleted ->
            if (isCompleted) {
                habitCompletionRepository.deleteCompletionByDate(
                    habitId = habitId,
                    date = date
                )
            } else {
                habitCompletionRepository.addCompletion(
                    habitId = habitId,
                    timestamp = date.toStartOfDayTimestamp()
                )
            }
        }
    }
}
