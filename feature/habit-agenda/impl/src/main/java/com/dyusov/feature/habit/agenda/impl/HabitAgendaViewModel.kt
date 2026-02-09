package com.dyusov.feature.habit.agenda.impl

import androidx.lifecycle.ViewModel
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

@HiltViewModel
class HabitAgendaViewModel @Inject constructor(
    // TODO: add use-cases
) : ViewModel() {

    private val _state = MutableStateFlow(HabitAgendaState())
    val state: StateFlow<HabitAgendaState> = _state.asStateFlow()
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