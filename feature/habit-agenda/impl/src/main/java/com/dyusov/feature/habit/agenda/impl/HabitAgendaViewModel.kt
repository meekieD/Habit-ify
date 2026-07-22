package com.dyusov.feature.habit.agenda.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.datetime.toTimestamp
import com.dyusov.core.common.utils.onError
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.habit.DeleteHabitUseCase
import com.dyusov.core.domain.tracking.GetAllHabitsWithCompletionsUseCase
import com.dyusov.core.domain.tracking.ToggleHabitCompletionOnDateUseCase
import com.dyusov.core.model.Habit
import com.dyusov.core.widget.HabitWidget
import com.dyusov.core.widget.HabitWidgetWorkManager
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
    private val getAllHabitsWithCompletionsUseCase: GetAllHabitsWithCompletionsUseCase,
    private val toggleHabitCompletionOnDateUseCase: ToggleHabitCompletionOnDateUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val dateTimeProvider: DateTimeProvider,
    private val habitWidgetWorkManager: HabitWidgetWorkManager
) : ViewModel() {

    private val _state = MutableStateFlow(HabitAgendaState())
    private val _isRefreshing = MutableStateFlow(false)

    val state: StateFlow<HabitAgendaState> = _state.asStateFlow()
    val refreshState = _isRefreshing.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        getAllHabitsWithCompletionsUseCase()
            .onEach { result ->
                result
                    .onSuccess { habitsWithCompletions ->
                        Log.d(
                            "HabitAgendaViewModel",
                            "Got habits with completions: $habitsWithCompletions"
                        )
                        val todayTimestamp = dateTimeProvider.nowLocalDate().toTimestamp()
                        val habitList = habitsWithCompletions.map { habitWithCompletions ->
                            val isCompletedToday = habitWithCompletions.completions.any {
                                it.timestamp >= todayTimestamp
                            }
                            habitWithCompletions.habit.copy(isCompletedToday = isCompletedToday)
                        }
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

    fun processCommand(command: HabitAgendaCommand) {
        viewModelScope.launch {
            when (command) {
                HabitAgendaCommand.RefreshData -> {
                    _isRefreshing.update { true }
                    viewModelScope.launch {
                        loadHabits() // TODO: check performance
                        _isRefreshing.update { false }
                    }
                }

                is HabitAgendaCommand.ToggleHabitCompletion -> {
                    toggleHabitCompletionOnDateUseCase(
                        habitId = command.habitId,
                        date = dateTimeProvider.nowLocalDate()
                    )
                    habitWidgetWorkManager.forceWidgetUpdateNow()
                }

                is HabitAgendaCommand.DeleteHabit -> {
                    deleteHabitUseCase(habitId = command.habitId)
                }
            }
        }
    }
}

// commands
sealed interface HabitAgendaCommand {
    data object RefreshData : HabitAgendaCommand
    data class ToggleHabitCompletion(val habitId: Long) : HabitAgendaCommand
    data class DeleteHabit(val habitId: Long) : HabitAgendaCommand
}

// screen state
data class HabitAgendaState(
    val items: List<Habit> = emptyList(),
    val selectedDate: LocalDate = LocalDate.nowClock(),
    val error: String? = null
)