package com.dyusov.feature.habit.details.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class HabitDetailsNavKey(val habitId: String): NavKey