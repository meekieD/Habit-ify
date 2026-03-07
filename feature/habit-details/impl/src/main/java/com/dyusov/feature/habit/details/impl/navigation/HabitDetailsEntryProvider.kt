package com.dyusov.feature.habit.details.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.details.api.navigation.HabitDetailsNavKey
import com.dyusov.feature.habit.details.impl.HabitDetailsScreen

fun EntryProviderScope<NavKey>.habitDetailsEntry(
    onHabitAgendaScreen: () -> Unit,
    onEditHabitScreen: (Long) -> Unit
) {
    entry<HabitDetailsNavKey> { key ->
        HabitDetailsScreen(
            habitId = key.habitId,
            onFinished = onHabitAgendaScreen,
            onEditHabit = { habitId ->
                onEditHabitScreen(habitId)
            }
        )
    }
}