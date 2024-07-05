package com.kappdev.wordbook.reminders_feature.domain

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kappdev.wordbook.reminders_feature.data.CheckUserActivityWorker
import java.util.concurrent.TimeUnit

class RemindersWorkManager(
    context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicWork(repeatInterval: Long = 6, timeUnit: TimeUnit = TimeUnit.HOURS) {
        val repeatingRequest = PeriodicWorkRequestBuilder<CheckUserActivityWorker>(repeatInterval, timeUnit)
            .build()

        workManager.enqueueUniquePeriodicWork(
            /* uniqueWorkName = */ WORK_NAME,
            /* existingPeriodicWorkPolicy = */ ExistingPeriodicWorkPolicy.UPDATE,
            /* periodicWork = */ repeatingRequest
        )
    }

    fun cancelPeriodicWork() {
        workManager.cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "check_user_activity"
    }
}