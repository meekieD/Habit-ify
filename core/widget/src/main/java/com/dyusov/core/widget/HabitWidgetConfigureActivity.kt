@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.core.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.habit.GetAllHabitsUseCase
import com.dyusov.core.model.Habit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HabitWidgetConfigureActivity : ComponentActivity() {

    @Inject
    lateinit var getAllHabitsUseCase: GetAllHabitsUseCase

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Default result in case user cancels or goes back
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                getAllHabitsUseCase().firstOrNull()
                    ?.onSuccess { habitList -> habits = habitList }
                isLoading = false
            }

            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Select a habit to track",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        )
                    }
                ) { paddingValues ->
                    if (isLoading) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            contentPadding = paddingValues
                        ) {
                            items(habits) { habit ->
                                ListItem(
                                    headlineContent = { Text(habit.name) },
                                    modifier = Modifier.clickable { selectHabit(habit.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectHabit(habitId: Long) {
        lifecycleScope.launch {
            val context = this@HabitWidgetConfigureActivity
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

            // Save the selected habitId inside the widget instance's preferences
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[HabitWidget.HABIT_ID_KEY] = habitId
            }

            // Tell Glance to refresh this widget instance immediately
            HabitWidget().update(context, glanceId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}