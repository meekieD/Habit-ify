package com.dyusov.habit_ify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dyusov.feature.habit.addedit.api.navigation.CreateHabitNavKey
import com.dyusov.feature.habit.addedit.api.navigation.EditHabitNavKey
import com.dyusov.feature.habit.addedit.impl.navigation.createHabitEntry
import com.dyusov.feature.habit.addedit.impl.navigation.editHabitEntry
import com.dyusov.feature.habit.agenda.api.navigation.HabitAgendaNavKey
import com.dyusov.feature.habit.agenda.impl.navigation.habitAgendaEntry
import com.dyusov.feature.habit.details.api.navigation.HabitDetailsNavKey
import com.dyusov.feature.habit.details.impl.navigation.habitDetailsEntry

@Composable
fun NavRoute() {
    val backstack = rememberNavBackStack(HabitAgendaNavKey)

    val appProvider = entryProvider {
        habitAgendaEntry(
            onHabitDetailsScreen = { habitId: Long ->
                backstack.removeLastOrNull()
                backstack.add(HabitDetailsNavKey(habitId))
            },
            onHabitCreationScreen = {
                backstack.removeLastOrNull()
                backstack.add(CreateHabitNavKey)
            }
        )
        createHabitEntry(
            onBackToMainScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAgendaNavKey)
            }
        )
        editHabitEntry(
            onBackToMainScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAgendaNavKey)
            }
        )
        habitDetailsEntry(
            onHabitAgendaScreen = {
                backstack.removeLastOrNull()
                backstack.add(HabitAgendaNavKey)
            },
            onEditHabitScreen = { habitId: Long ->
                backstack.removeLastOrNull()
                backstack.add(EditHabitNavKey(habitId))
            }
        )
    }

    NavDisplay(
        backStack = backstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = appProvider
    )
}