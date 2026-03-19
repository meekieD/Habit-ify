package com.dyusov.habit_ify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dyusov.core.designsystem.ThemeViewModel
import com.dyusov.habit_ify.presentation.navigation.NavRoute
import com.dyusov.habit_ify.presentation.theme.ui.HabitifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // make the app drawing area fullscreen (draw behind status and nav bars)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        /*
            disable enforced contrast between the navigation bar and its content,
            preventing unwanted darkening or tinting in dark-themed app backgrounds.
        */
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            HabitifyTheme(themeMode = themeMode) {
                NavRoute()
            }
        }
    }
}