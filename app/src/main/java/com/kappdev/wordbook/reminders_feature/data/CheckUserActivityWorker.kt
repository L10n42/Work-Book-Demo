package com.kappdev.wordbook.reminders_feature.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kappdev.wordbook.MainActivity
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import com.kappdev.wordbook.reminders_feature.domain.AppUsageRecorder

class CheckUserActivityWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val appUsageRecorder: AppUsageRecorder = AppUsageRecorderImpl(context)
    private var localizedContext: Context

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localizedContext = context
        } else {
            val currentLocale = getCurrentAppLocale()
            localizedContext = if (currentLocale != null) {
                val configuration = applicationContext.resources.configuration
                configuration.setLocale(currentLocale)
                applicationContext.createConfigurationContext(configuration)
            } else context
        }
    }

    override fun doWork(): Result {
        if (!isUserActive()) {
            sendReminderNotification()
        }
        return Result.success()
    }

    private fun isUserActive(): Boolean {
        val lastOpenTime = appUsageRecorder.getLastAppOpen()
        val currentTime = System.currentTimeMillis()
        val inactiveDuration = currentTime - lastOpenTime
        return inactiveDuration < INACTIVITY_THRESHOLD
    }

    private fun sendReminderNotification() {
        createRemainderChannel()
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppAction = PendingIntent.getActivity(context, REQUEST_CODE, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        val reminderMessage = getRandomMessage()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(localizedContext.getString(R.string.reminder_message_title))
            .setContentText(reminderMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminderMessage))
            .setSmallIcon(R.drawable.small_logo_icon)
            .setContentIntent(openAppAction)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun getRandomMessage(): String {
        val messagesArray = localizedContext.resources.getStringArray(R.array.reminder_messages)
        return messagesArray.random()
    }

    private fun createRemainderChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "REMINDER_NOTIFICATION_ID"
        private const val CHANNEL_NAME = "REMINDER_NOTIFICATION"
        private const val REQUEST_CODE = 1237

        private const val NOTIFICATION_ID = 320750
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000
        private const val INACTIVITY_THRESHOLD = ONE_DAY_MILLIS * 2
    }
}