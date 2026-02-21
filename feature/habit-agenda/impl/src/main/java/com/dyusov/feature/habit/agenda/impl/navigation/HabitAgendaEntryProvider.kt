package com.dyusov.feature.habit.agenda.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.agenda.api.navigation.HabitAgendaNavKey
import com.dyusov.feature.habit.agenda.impl.HabitAgendaScreen

fun EntryProviderScope<NavKey>.habitAgendaEntry(
    onHabitDetailsScreen: () -> Unit,
    onHabitCreationScreen: () -> Unit
) {
    entry<HabitAgendaNavKey> {
        HabitAgendaScreen(
            onHabitClick = onHabitDetailsScreen,
            onAddHabitClick = onHabitCreationScreen
        )
    }
}