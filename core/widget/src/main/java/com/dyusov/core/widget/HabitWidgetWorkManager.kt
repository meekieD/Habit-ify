package com.dyusov.core.widget

import androidx.glance.appwidget.updateAll
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitWidgetWorkManager @Inject constructor(
    private val workManager: WorkManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun forceWidgetUpdateNow() {
        scope.launch {
            val manualWorkRequest = OneTimeWorkRequestBuilder<HabitWidgetUpdateWorker>().build()

            workManager.enqueueUniqueWork(
                "WidgetManualUpdate",
                ExistingWorkPolicy.REPLACE,
                manualWorkRequest
            )
        }
    }
}