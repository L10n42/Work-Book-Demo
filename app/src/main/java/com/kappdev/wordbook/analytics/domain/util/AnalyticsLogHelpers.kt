package com.kappdev.wordbook.analytics.domain.util

import com.google.firebase.analytics.FirebaseAnalytics
import com.kappdev.wordbook.analytics.domain.repository.AnalyticsHelper

fun AnalyticsHelper.logDBAction(name: String) {
    logEvent(
        AnalyticsEventType.DB_ACTION,
        AnalyticsParamKey.ACTION_NAME to name
    )
}

fun AnalyticsHelper.logShare(label: String) {
    logEvent(
        FirebaseAnalytics.Event.SHARE,
        AnalyticsParamKey.LABEL to label
    )
}

fun AnalyticsHelper.logButtonClick(buttonName: String) {
    logEvent(
        AnalyticsEventType.BUTTON_CLICK,
        AnalyticsParamKey.BUTTON_NAME to buttonName
    )
}

fun AnalyticsHelper.logBackupAction(actionName: String) {
    logEvent(
        AnalyticsEventType.BACKUP_ACTION,
        AnalyticsParamKey.ACTION_NAME to actionName
    )
}