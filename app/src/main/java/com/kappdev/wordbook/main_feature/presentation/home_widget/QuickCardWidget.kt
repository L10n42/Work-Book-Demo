package com.kappdev.wordbook.main_feature.presentation.home_widget

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kappdev.wordbook.MainActivity
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import com.kappdev.wordbook.theme.SkyBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class QuickCardWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val localizedContext = getLocalizedContextOf(context)
        provideContent {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(SkyBlue)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .cornerRadius(16.dp)
                    .clickable(
                        onClick = actionStartActivity(
                            intent = Intent(context, MainActivity::class.java).apply {
                                action = QUICK_CARD_ACTION
                            }
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.round_add),
                    contentDescription = null,
                    modifier = GlanceModifier.size(24.dp)
                )
                Spacer(GlanceModifier.width(8.dp))
                Text(
                    text = localizedContext.getString(R.string.new_card),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }
        }
    }

    private fun getLocalizedContextOf(context: Context): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return context.applicationContext
        } else {
            val currentLocale = getCurrentAppLocale()
            return if (currentLocale != null) {
                val configuration = context.applicationContext.resources.configuration
                configuration.setLocale(currentLocale)
                context.applicationContext.createConfigurationContext(configuration)
            } else {
                context.applicationContext
            }
        }
    }

    companion object {
        const val QUICK_CARD_ACTION = "quick_card"
    }
}


class QuickCardWidgetManager(private val context: Context) {

    private val glanceAppWidgetManager = GlanceAppWidgetManager(context)
    private val scope = CoroutineScope(Dispatchers.Main)

    private var getIdsJob: Job? = null
    private var updateWidgetsJob: Job? = null

    private lateinit var glanceIds: List<GlanceId>

    init {
        getIdsJob = scope.launch {
            glanceIds = glanceAppWidgetManager.getGlanceIds(QuickCardWidget::class.java)
        }
    }

    fun updateWidgets() {
        updateWidgetsJob?.cancel()
        updateWidgetsJob = scope.launch {
            getIdsJob?.join()
            glanceIds.forEach { glanceId ->
                QuickCardWidget().update(context, glanceId)
            }
        }
    }
}