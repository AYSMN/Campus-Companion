package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_CLASSES = "channel_campus_classes"
    const val CHANNEL_MESS = "channel_campus_mess"
    const val CHANNEL_TASKS = "channel_campus_tasks"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Classes Channel
            val classChannel = NotificationChannel(
                CHANNEL_CLASSES,
                "Class & Lab Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "15-minute alerts before lectures and lab sessions"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }

            // Mess Channel
            val messChannel = NotificationChannel(
                CHANNEL_MESS,
                "Mess Menu Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily meal alerts and menu announcements"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
            }

            // Tasks Channel
            val taskChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Tasks & Deadlines",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Homework reminders and study deadlines"
                enableLights(true)
                lightColor = Color.MAGENTA
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(classChannel, messChannel, taskChannel))
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
        subText: String? = null
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (!subText.isNullOrBlank()) {
            builder.setSubText(subText)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
