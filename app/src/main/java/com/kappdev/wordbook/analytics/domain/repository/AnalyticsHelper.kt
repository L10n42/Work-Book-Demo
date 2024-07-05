package com.kappdev.wordbook.analytics.domain.repository

import androidx.compose.runtime.staticCompositionLocalOf
import com.kappdev.wordbook.analytics.data.repository.NoOpAnalyticsHelper

interface AnalyticsHelper {
    fun logEvent(type: String, vararg extras: Pair<String, String>)
}

/**
 * Global key used to obtain access to the AnalyticsHelper
 * through a CompositionLocal.
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> { NoOpAnalyticsHelper }
