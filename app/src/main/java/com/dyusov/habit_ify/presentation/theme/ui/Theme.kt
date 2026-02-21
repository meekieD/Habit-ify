package com.dyusov.habit_ify.presentation.theme.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HabitifyTheme(
    content: @Composable () -> Unit
) {
    MaterialExpressiveTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}