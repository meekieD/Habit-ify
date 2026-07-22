package com.dyusov.habit_ify

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.dyusov.habit_ify.presentation.startup.AppStartupManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class HabitifyApp : Application(), Configuration.Provider {
    @Inject
    lateinit var appStartupManager: AppStartupManager

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration
        get() = Configuration.Builder()
            .setWorkerFactory(
                EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory()
            )
            .build()

    override fun onCreate() {
        super.onCreate()
        appStartupManager.startDailyDataRefresh() // launch worker at startup
    }
}