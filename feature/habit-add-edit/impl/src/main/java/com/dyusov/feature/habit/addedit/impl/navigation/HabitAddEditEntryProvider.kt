package com.dyusov.feature.habit.addedit.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dyusov.feature.habit.addedit.api.navigation.HabitAddEditNavKey
import com.dyusov.feature.habit.addedit.impl.HabitAddEditScreenPlaceholder

// todo: change placeholder
fun EntryProviderScope<NavKey>.habitAddEditEntry(
    onFirstScreenButtonClick: () -> Unit,
    onSecondScreenButtonClick: () -> Unit
) {
    entry<HabitAddEditNavKey> { key ->
        HabitAddEditScreenPlaceholder(
            key.habitId,
            onFirstScreenButtonClick,
            onSecondScreenButtonClick
        )
    }
}