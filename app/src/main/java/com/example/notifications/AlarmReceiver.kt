package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all alarms on phone reboot
            CoroutineScope(Dispatchers.IO).launch {
                val repository = CampusRepository(context)
                repository.rescheduleNotifications()
            }
            return
        }

        if (action == ACTION_REMINDER) {
            val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)
            val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: NotificationHelper.CHANNEL_CLASSES
            val title = intent.getStringExtra(EXTRA_TITLE) ?: "Campus Companion"
            val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Upcoming schedule alert"
            val subText = intent.getStringExtra(EXTRA_SUBTEXT)

            NotificationHelper.showNotification(
                context = context,
                notificationId = notificationId,
                channelId = channelId,
                title = title,
                message = message,
                subText = subText
            )

            // Reschedule next occurrences in background
            CoroutineScope(Dispatchers.IO).launch {
                val repository = CampusRepository(context)
                repository.rescheduleNotifications()
            }
        }
    }

    companion object {
        const val ACTION_REMINDER = "com.example.ACTION_REMINDER"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_SUBTEXT = "extra_subtext"
    }
}
