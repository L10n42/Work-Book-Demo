package com.kappdev.wordbook.analytics.data.repository

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.kappdev.wordbook.analytics.domain.repository.AnalyticsHelper

/**
 * Implementation of `AnalyticsHelper` which logs events
 * to a Firebase backend.
 */
class FirebaseAnalyticsHelper(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsHelper {

    override fun logEvent(type: String, vararg extras: Pair<String, String>) {
        firebaseAnalytics.logEvent(type) {
            for (extra in extras) {
                param(
                    key = extra.first.take(40),
                    value = extra.second.take(100),
                )
            }
        }
    }
}
