package com.dyusov.habit_ify

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.dyusov.habit_ify.ui.theme.HabitifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
            HabitifyAppPreview()
        }
    }
}

@Composable
fun TextPreview(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp
    )
}

@Preview(showBackground = true)
@Composable
fun HabitifyAppPreview() {
    HabitifyTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TextPreview(text = "Habit-ify!")
            }
        }

    }
}