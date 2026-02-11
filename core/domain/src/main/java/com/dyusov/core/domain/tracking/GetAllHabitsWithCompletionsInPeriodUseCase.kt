package com.dyusov.core.domain.tracking

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllHabitsWithCompletionsInPeriodUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<MyResult<List<HabitWithCompletions>, MyError>> {
        return habitRepository.getAllHabitsWithCompletionsInPeriod(
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp
        )
    }
}