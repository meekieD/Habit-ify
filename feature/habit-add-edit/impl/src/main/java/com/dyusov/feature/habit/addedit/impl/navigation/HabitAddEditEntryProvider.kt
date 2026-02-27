package com.dyusov.feature.habit.addedit.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.addedit.api.navigation.CreateHabitNavKey
import com.dyusov.feature.habit.addedit.api.navigation.EditHabitNavKey
import com.dyusov.feature.habit.addedit.impl.creation.CreateHabitScreen
import com.dyusov.feature.habit.addedit.impl.editing.EditHabitScreen

fun EntryProviderScope<NavKey>.createHabitEntry(
    onBackToMainScreenButtonClick: () -> Unit,
) {
    entry<CreateHabitNavKey> {
        CreateHabitScreen(
            onFinished = onBackToMainScreenButtonClick
        )
    }
}

fun EntryProviderScope<NavKey>.editHabitEntry(
    onBackToMainScreenButtonClick: () -> Unit,
) {
    entry<EditHabitNavKey> { key ->
        EditHabitScreen(
            habitId = key.habitId,
            onFinished = onBackToMainScreenButtonClick
        )
    }
}