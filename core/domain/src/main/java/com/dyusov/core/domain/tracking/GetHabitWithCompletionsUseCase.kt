package com.dyusov.core.domain.tracking

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitWithCompletionsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long): Flow<MyResult<HabitWithCompletions, MyError>> = habitRepository.getHabitWithCompletions(habitId)
}