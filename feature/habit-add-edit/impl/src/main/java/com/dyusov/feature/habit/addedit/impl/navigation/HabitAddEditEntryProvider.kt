package com.dyusov.feature.habit.addedit.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.addedit.api.navigation.HabitAddEditNavKey
import com.dyusov.feature.habit.addedit.impl.creation.CreateHabitScreen

// todo: change placeholder
fun EntryProviderScope<NavKey>.habitAddEditEntry(
    onBackToMainScreenButtonClick: () -> Unit,
) {
    entry<HabitAddEditNavKey> { key ->
        CreateHabitScreen(
            onFinished = onBackToMainScreenButtonClick
        )
    }
}