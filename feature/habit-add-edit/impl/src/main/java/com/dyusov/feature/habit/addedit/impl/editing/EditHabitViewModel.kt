package com.dyusov.feature.habit.addedit.impl.editing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.utils.onError
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.habit.DeleteHabitUseCase
import com.dyusov.core.domain.habit.GetHabitUseCase
import com.dyusov.core.domain.habit.UpdateHabitUseCase
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.model.HabitFrequency
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@HiltViewModel(assistedFactory = EditHabitViewModel.Factory::class)
class EditHabitViewModel @AssistedInject constructor(
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val getHabitUseCase: GetHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    @Assisted("habitId") private val habitId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("habitId") habitId: Long): EditHabitViewModel
    }

    private val _state = MutableStateFlow<EditHabitState>(
        EditHabitState.Initial
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getHabitUseCase(habitId)
                .onSuccess { habit ->
                    Log.d(
                        "EditHabitViewModel",
                        "Got habit with id=$habitId: $habit"
                    )
                    _state.update {
                        val frequency = habit.frequency

                        EditHabitState.Editing(
                            name = habit.name,
                            description = habit.description,
                            frequencyType = when (frequency) {
                                is HabitFrequency.Daily -> FrequencyType.DAILY
                                is HabitFrequency.Weekly -> FrequencyType.WEEKLY
                                is HabitFrequency.Custom -> FrequencyType.CUSTOM
                            },
                            selectedDaysOfTheWeek = when (frequency) {
                                is HabitFrequency.Weekly -> frequency.daysOfWeek
                                else -> setOf(LocalDate.nowClock().dayOfWeek)
                            },
                            selectedDaysOfMonth = when (frequency) {
                                is HabitFrequency.Custom -> frequency.daysOfMonth
                                else -> setOf(LocalDate.nowClock().day)
                            },
                            color = habit.color
                        )
                    }
                }
                .onError { error ->
                    Log.d(
                        "EditHabitViewModel",
                        "Got error while getting habit with id=$habitId: $error"
                    )
                    _state.update {
                        EditHabitState.Initial
                    }
                }
        }
    }

    fun processCommand(command: EditHabitCommand) {
        viewModelScope.launch {
            when (command) {
                EditHabitCommand.Back -> TODO()
                EditHabitCommand.Delete -> TODO()
                is EditHabitCommand.InputDescription -> TODO()
                is EditHabitCommand.InputName -> TODO()
                EditHabitCommand.Save -> TODO()
                is EditHabitCommand.SelectColor -> TODO()
                is EditHabitCommand.SelectFrequencyType -> TODO()
                is EditHabitCommand.ToggleDayOfMonth -> TODO()
                is EditHabitCommand.ToggleDayOfWeek -> TODO()
            }
        }
    }
}

sealed interface EditHabitCommand {

    data class InputName(val name: String) : EditHabitCommand

    data class InputDescription(val description: String) : EditHabitCommand

    data class SelectFrequencyType(val type: FrequencyType) : EditHabitCommand

    data class ToggleDayOfWeek(val day: DayOfWeek) : EditHabitCommand

    data class ToggleDayOfMonth(val day: Int) : EditHabitCommand

    data class SelectColor(val color: Int) : EditHabitCommand

    data object Save : EditHabitCommand

    data object Back : EditHabitCommand

    data object Delete : EditHabitCommand
}

sealed interface EditHabitState {
    data object Initial : EditHabitState

    data class Editing(
        val name: String,
        val description: String?,
        val frequencyType: FrequencyType,
        val selectedDaysOfTheWeek: Set<DayOfWeek>,
        val selectedDaysOfMonth: Set<Int>,
        val color: Int,
        val error: String? = null
    ) : EditHabitState {

        val frequency: HabitFrequency
            get() = when (frequencyType) {
                FrequencyType.DAILY -> HabitFrequency.Daily
                FrequencyType.WEEKLY -> HabitFrequency.Weekly(selectedDaysOfTheWeek)
                FrequencyType.CUSTOM -> HabitFrequency.Custom(selectedDaysOfMonth)
            }

        val isSaveEnabled: Boolean
            get() = name.isNotBlank() && when (frequencyType) {
                FrequencyType.DAILY -> true
                FrequencyType.WEEKLY -> selectedDaysOfTheWeek.isNotEmpty()
                FrequencyType.CUSTOM -> selectedDaysOfMonth.isNotEmpty()
            }
    }
}