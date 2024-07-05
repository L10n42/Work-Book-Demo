package com.kappdev.wordbook.analytics.data.repository

import android.util.Log
import com.kappdev.wordbook.analytics.domain.repository.AnalyticsHelper

object StubAnalyticsHelper : AnalyticsHelper {
    private const val TAG = "StubAnalyticsHelper"

    override fun logEvent(type: String, vararg extras: Pair<String, String>) {
        Log.d(TAG, "Analytics Event: type = $type, extras = ${extras.joinToString()}")
    }
}