package com.kappdev.wordbook.core.data.repository

import android.content.Context
import com.kappdev.wordbook.core.domain.repository.FirstAppLaunchRecorder
import javax.inject.Inject

class FirstAppLaunchRecorderImpl @Inject constructor(
    context: Context
) : FirstAppLaunchRecorder {

    private val sharedPreferences = context.getSharedPreferences("app_usage", Context.MODE_PRIVATE)

    override fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true)
    }

    override fun markAsLaunched() {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, false).apply()
    }

    companion object {
        private const val FIRST_LAUNCH = "is_first_launch"
    }
}