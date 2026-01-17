package com.dyusov.feature.habit.agenda.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.agenda.api.navigation.HabitAgendaNavKey
import com.dyusov.feature.habit.agenda.impl.HabitAgendaScreenPlaceholder

// todo: change placeholder
fun EntryProviderScope<NavKey>.habitAgendaEntry(
    onFirstScreenButtonClick: () -> Unit,
    onSecondScreenButtonClick: () -> Unit
) {
    entry<HabitAgendaNavKey> {
        HabitAgendaScreenPlaceholder(onFirstScreenButtonClick, onSecondScreenButtonClick)
    }
}