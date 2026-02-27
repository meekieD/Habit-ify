package com.dyusov.feature.habit.details.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.details.api.navigation.HabitDetailsNavKey
import com.dyusov.feature.habit.details.impl.HabitDetailsScreenPlaceholder

// todo: change placeholder
fun EntryProviderScope<NavKey>.habitDetailsEntry(
    onHabitAgendaScreen: () -> Unit,
    onEditHabitScreen: (Long) -> Unit
) {
    entry<HabitDetailsNavKey> { key ->
        HabitDetailsScreenPlaceholder(
            key.habitId,
            onFirstScreenButtonClick = onHabitAgendaScreen,
            onSecondScreenButtonClick = {
                habitId -> onEditHabitScreen(habitId)
            }
        )
    }
}