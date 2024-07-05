package com.kappdev.wordbook.core.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kappdev.wordbook.main_feature.presentation.home_widget.QuickCardWidgetManager

class LocaleChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            val quickCardWidgetManager = QuickCardWidgetManager(context)
            quickCardWidgetManager.updateWidgets()
        }
    }
}