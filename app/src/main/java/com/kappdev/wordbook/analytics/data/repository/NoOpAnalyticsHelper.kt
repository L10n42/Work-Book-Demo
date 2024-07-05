package com.kappdev.wordbook.analytics.data.repository

import com.kappdev.wordbook.analytics.domain.repository.AnalyticsHelper

object NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(type: String, vararg extras: Pair<String, String>) = Unit
}