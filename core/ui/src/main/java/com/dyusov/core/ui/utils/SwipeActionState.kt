package com.dyusov.core.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.dyusov.core.ui.habit.HabitCardDefaults

data class SwipeActionState(
    val isCompleted: Boolean
) {
    val backgroundColor: Color
        get() = if (isCompleted) {
            HabitCardDefaults.completedBackgroundColor
        } else {
            HabitCardDefaults.incompleteBackgroundColor
        }

    val actionColor: Color
        get() = if (isCompleted) {
            HabitCardDefaults.completedActionColor
        } else {
            HabitCardDefaults.incompleteActionColor
        }

    val actionIcon: ImageVector
        get() = if (isCompleted) {
            Icons.Default.Close
        } else {
            Icons.Default.Check
        }

    val actionText: String
        get() = if (isCompleted) {
            "Undo"
        } else {
            "Done"
        }
}
