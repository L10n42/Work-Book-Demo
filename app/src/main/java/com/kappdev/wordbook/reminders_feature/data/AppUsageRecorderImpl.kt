package com.kappdev.wordbook.reminders_feature.data

import android.content.Context
import com.kappdev.wordbook.reminders_feature.domain.AppUsageRecorder
import javax.inject.Inject

class AppUsageRecorderImpl @Inject constructor(
    context: Context
): AppUsageRecorder {

    private val sharedPreferences = context.getSharedPreferences("app_usage", Context.MODE_PRIVATE)

    override fun recordAppOpen() {
        sharedPreferences.edit().putLong(LAST_OPEN, System.currentTimeMillis()).apply()
    }

    override fun getLastAppOpen(): Long {
        return sharedPreferences.getLong(LAST_OPEN, 0L)
    }

    companion object {
        private const val LAST_OPEN = "last_app_open_timestamp"
    }
}