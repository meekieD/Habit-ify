package com.dyusov.habit_ify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dyusov.feature.habit.addedit.api.navigation.HabitAddEditNavKey
import com.dyusov.feature.habit.addedit.impl.navigation.habitAddEditEntry
import com.dyusov.feature.habit.agenda.api.navigation.HabitAgendaNavKey
import com.dyusov.feature.habit.agenda.impl.navigation.habitAgendaEntry
import com.dyusov.feature.habit.details.api.navigation.HabitDetailsNavKey
import com.dyusov.feature.habit.details.impl.navigation.habitDetailsEntry

@Composable
fun NavRoute() {
    val backstack = rememberNavBackStack(HabitAgendaNavKey)

    val appProvider = entryProvider {
        habitAgendaEntry(
            onFirstScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAddEditNavKey("fromAgenda"))
            }, onSecondScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitDetailsNavKey("fromAgenda"))
            }
        )
        habitAddEditEntry(
            onFirstScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAgendaNavKey)
            }, onSecondScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitDetailsNavKey("fromAddEdit"))
            }
        )
        habitDetailsEntry(
            onFirstScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAgendaNavKey)
            }, onSecondScreenButtonClick = {
                backstack.removeLastOrNull()
                backstack.add(HabitAddEditNavKey("fromDetails"))
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