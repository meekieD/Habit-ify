package com.dyusov.feature.habit.addedit.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class EditHabitNavKey(val habitId: Long): NavKey