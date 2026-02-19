package com.dyusov.core.ui.habit

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object HabitCardDefaults {
    val cornerRadius = 16.dp
    val horizontalPadding = 24.dp
    val verticalPadding = 24.dp
    val borderWidth = 2.dp
    val iconSize = 32.dp

    const val DEFAULT_COLOR = 0xFF4CAF50.toInt()

    val completedBackgroundColor = Color(0xFFFFCDD2) // Pastel red
    val incompleteBackgroundColor = Color(0xFFC8E6C9) // Pastel green
    val completedActionColor = Color(0xFFD32F2F) // Dark red
    val incompleteActionColor = Color(0xFF388E3C) // Dark green
}