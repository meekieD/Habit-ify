package com.dyusov.feature.habit.agenda.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.utils.onError
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.habit.GetAllHabitsUseCase
import com.dyusov.core.domain.habit.UpdateHabitUseCase
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitAgendaViewModel @Inject constructor(
    private val getAllHabitsUseCase: GetAllHabitsUseCase,
    // TODO: for test
    private val updateHabitUseCase: UpdateHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitAgendaState())
    val state: StateFlow<HabitAgendaState> = _state.asStateFlow()

    init {
        createTestHabits()
        loadHabits()
    }

    private fun loadHabits() {
        getAllHabitsUseCase()
            .onEach { result ->
                result
                    .onSuccess { habitList ->
                        Log.d(
                            "HabitAgendaViewModel",
                            "Got habits: $habitList"
                        )
                        _state.update { currentState ->
                            currentState.copy(items = habitList)
                        }
                    }
                    .onError { error ->
                        Log.d(
                            "HabitAgendaViewModel",
                            "Got error while getting habits: $error"
                        )
                        _state.update {
                            it.copy(error = error.toString())
                        }
                    }
            }.launchIn(viewModelScope)
    }

    // TODO: for test
    private fun createTestHabits() {
        viewModelScope.launch {
            updateHabitUseCase(
                Habit(
                    id = 1,
                    isCompletedToday = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    name = "Зарядка",
                    description = "Утренняя зарядка 10 минут",
                    frequency = HabitFrequency.Daily,
                    color = 0xFF4CAF50.toInt()
                )

            )
            updateHabitUseCase(
                Habit(
                    id = 2,
                    isCompletedToday = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    name = "Чтение",
                    description = "Читать 30 минут перед сном",
                    frequency = HabitFrequency.Daily,
                    color = 0xFF2196F3.toInt()
                )
            )
            updateHabitUseCase(
                Habit(
                    id = 3,
                    isCompletedToday = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    name = "Медитация",
                    description = "5 минут медитации",
                    frequency = HabitFrequency.Daily,
                    color = 0xFF9C27B0.toInt()
                )
            )
        }

    }
}

// commands
sealed interface HabitAgendaCommand {
    data object RefreshData : HabitAgendaCommand
    data class ToggleHabitCompletion(val habitId: String) : HabitAgendaCommand
}

// screen state
data class HabitAgendaState(
    val items: List<Habit> = emptyList(),
    val selectedDate: LocalDate = LocalDate.nowClock(),
    val error: String? = null
)