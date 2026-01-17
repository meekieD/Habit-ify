package com.dyusov.feature.habit.addedit.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class HabitAddEditNavKey(val habitId: String): NavKey