package com.dyusov.core.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class HabitWidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("HabitWidgetUpdateWorker", "Start")
        return try {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(HabitWidget::class.java)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(applicationContext, glanceId) { prefs ->
                    prefs[HabitWidget.FORCE_UPDATE_KEY] = System.currentTimeMillis()
                }
            }
            HabitWidget().updateAll(applicationContext)
            Log.d("HabitWidgetUpdateWorker", "Finish")
            Result.success()
        } catch (e: Exception) {
            Log.d("HabitWidgetUpdateWorker", "Error: ${e.message}")
            Result.failure()
        }
    }
}