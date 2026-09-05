package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.ClassSlotEntity
import com.example.data.local.MessMealEntity
import com.example.data.local.TaskEntity
import com.example.data.model.LabGroup
import java.util.Calendar

object NotificationScheduler {

    fun scheduleAll(
        context: Context,
        labGroup: LabGroup,
        classEnabled: Boolean,
        messEnabled: Boolean,
        taskEnabled: Boolean,
        mutedDays: Set<Int>,
        meals: List<MessMealEntity>,
        classes: List<ClassSlotEntity>,
        tasks: List<TaskEntity>
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Classes: 15 minutes before each class starts
        if (classEnabled) {
            for (slot in classes) {
                // Check if day is muted
                if (mutedDays.contains(slot.dayOfWeek)) continue

                // Check group eligibility:
                // If student is GROUP_1, they attend GROUP_1 and ALL.
                // If student is GROUP_2, they attend GROUP_2 and ALL.
                val matchesGroup = when (slot.groupType) {
                    "ALL" -> true
                    "GROUP_1" -> labGroup == LabGroup.GROUP_1 || labGroup == LabGroup.ALL
                    "GROUP_2" -> labGroup == LabGroup.GROUP_2 || labGroup == LabGroup.ALL
                    else -> true
                }
                if (!matchesGroup) continue

                val (startHour, startMinute) = parseTime(slot.startTime)
                val targetCalendar = getNextOccurrence(slot.dayOfWeek, startHour, startMinute, minutesOffset = -15)

                val requestCode = 20000 + slot.id.toInt()
                val groupLabel = when (slot.groupType) {
                    "GROUP_1" -> " [Gr. I]"
                    "GROUP_2" -> " [Gr. II]"
                    else -> ""
                }
                val title = "Class in 15 mins: ${slot.subjectCode}$groupLabel"
                val message = "${slot.subjectName} by ${slot.faculty} in ${slot.roomOrLab} (${slot.startTime}–${slot.endTime})"

                scheduleAlarm(
                    context = context,
                    alarmManager = alarmManager,
                    requestCode = requestCode,
                    triggerAtMillis = targetCalendar.timeInMillis,
                    channelId = NotificationHelper.CHANNEL_CLASSES,
                    title = title,
                    message = message,
                    subText = "Room: ${slot.roomOrLab}"
                )
            }
        }

        // 2. Mess Meals: At EXACT start of meal window (e.g. 7:00 AM, 12:30 PM, 4:30 PM, 8:00 PM)
        if (messEnabled) {
            val distinctMeals = meals.distinctBy { Pair(it.dayOfWeek, it.mealType) }
            for (meal in distinctMeals) {
                if (mutedDays.contains(meal.dayOfWeek)) continue

                val (hour, minute) = parseMealStartTime(meal.timeSlot, meal.mealType)
                val targetCalendar = getNextOccurrence(meal.dayOfWeek, hour, minute, minutesOffset = 0)

                val mealTypeIndex = when (meal.mealType) {
                    "BREAKFAST_MORNING" -> 1
                    "LUNCH" -> 2
                    "BREAKFAST_EVENING" -> 3
                    "DINNER" -> 4
                    else -> 5
                }
                val requestCode = 30000 + (meal.dayOfWeek * 10) + mealTypeIndex
                val mealName = when (meal.mealType) {
                    "BREAKFAST_MORNING" -> "Breakfast is ready!"
                    "LUNCH" -> "Lunch is ready!"
                    "BREAKFAST_EVENING" -> "Evening Snacks & Tea are ready!"
                    "DINNER" -> "Dinner is ready!"
                    else -> "Mess Meal is ready!"
                }

                scheduleAlarm(
                    context = context,
                    alarmManager = alarmManager,
                    requestCode = requestCode,
                    triggerAtMillis = targetCalendar.timeInMillis,
                    channelId = NotificationHelper.CHANNEL_MESS,
                    title = "$mealName (${meal.timeSlot})",
                    message = meal.menuDescription,
                    subText = "Mess Timetable"
                )
            }
        }

        // 3. Tasks reminders
        if (taskEnabled) {
            val now = System.currentTimeMillis()
            for (task in tasks) {
                if (task.isCompleted) continue
                val reminderTime = task.reminderEpochMs ?: continue
                if (reminderTime > now) {
                    val requestCode = 40000 + (task.id % 10000).toInt()
                    val title = "Task Due: ${task.title}"
                    val message = if (task.subjectCode.isNullOrBlank()) {
                        task.description.ifBlank { "Reminder for your task" }
                    } else {
                        "[${task.subjectCode}] ${task.description.ifBlank { task.title }}"
                    }

                    scheduleAlarm(
                        context = context,
                        alarmManager = alarmManager,
                        requestCode = requestCode,
                        triggerAtMillis = reminderTime,
                        channelId = NotificationHelper.CHANNEL_TASKS,
                        title = title,
                        message = message,
                        subText = "Campus Tasks"
                    )
                }
            }
        }
    }

    private fun scheduleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        triggerAtMillis: Long,
        channelId: String,
        title: String,
        message: String,
        subText: String?
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_REMINDER
            putExtra(AlarmReceiver.EXTRA_NOTIFICATION_ID, requestCode)
            putExtra(AlarmReceiver.EXTRA_CHANNEL_ID, channelId)
            putExtra(AlarmReceiver.EXTRA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
            putExtra(AlarmReceiver.EXTRA_SUBTEXT, subText)
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)

        // Show Intent for AlarmClockInfo
        val showIntent = Intent(context, com.example.MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(context, requestCode + 50000, showIntent, flags)

        try {
            // Using setAlarmClock guarantees highest priority exact execution without Doze throttling or delay
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (e: SecurityException) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parseMealStartTime(timeSlot: String, mealType: String): Pair<Int, Int> {
        val defaultTime = when (mealType) {
            "BREAKFAST_MORNING" -> Pair(7, 0)
            "LUNCH" -> Pair(12, 30)
            "BREAKFAST_EVENING" -> Pair(16, 30)
            "DINNER" -> Pair(20, 0)
            else -> Pair(8, 0)
        }

        if (timeSlot.isBlank()) return defaultTime

        try {
            val startPart = timeSlot.split("–", "-", "to").firstOrNull()?.trim() ?: return defaultTime
            val upper = startPart.uppercase(java.util.Locale.ENGLISH)
            val isPm = upper.contains("PM")
            val isAm = upper.contains("AM")

            val cleanTime = upper.replace("AM", "").replace("PM", "").trim()
            val timeTokens = cleanTime.split(":")
            val rawHour = timeTokens.getOrNull(0)?.trim()?.toIntOrNull() ?: return defaultTime
            val rawMinute = timeTokens.getOrNull(1)?.trim()?.toIntOrNull() ?: 0

            var hour = rawHour
            if (isPm) {
                if (hour in 1..11) hour += 12
            } else if (isAm) {
                if (hour == 12) hour = 0
            }

            return Pair(hour.coerceIn(0, 23), rawMinute.coerceIn(0, 59))
        } catch (e: Exception) {
            return defaultTime
        }
    }

    fun triggerTestNotification(context: Context, type: String) {
        when (type) {
            "CLASS" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 9901,
                    channelId = NotificationHelper.CHANNEL_CLASSES,
                    title = "Class in 15 mins: EC031301 Lab [Gr. I]",
                    message = "Digital Electronics Lab with Dr. Gaurav Varshney in DE Lab (10:30–12:30)",
                    subText = "Room: DE Lab"
                )
            }
            "MESS" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 9902,
                    channelId = NotificationHelper.CHANNEL_MESS,
                    title = "Lunch Menu (12:30 PM – 2:30 PM)",
                    message = "Rajma, Rice, Roti, Sabji, Bhujiya, Salad, Pickles, Seasonal Fruit",
                    subText = "Mess Timetable"
                )
            }
            "TASK" -> {
                NotificationHelper.showNotification(
                    context = context,
                    notificationId = 9903,
                    channelId = NotificationHelper.CHANNEL_TASKS,
                    title = "Task Reminder: Submit DSP Lab Report",
                    message = "[EC031304 Lab] Complete simulation results and transfer characteristics",
                    subText = "Due Today"
                )
            }
        }
    }

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        val parts = timeStr.trim().split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return Pair(hour, minute)
    }

    private fun getNextOccurrence(dayOfWeek: Int, hour: Int, minute: Int, minutesOffset: Int): Calendar {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.MINUTE, minutesOffset)

        var daysToAdd = (dayOfWeek - currentDay + 7) % 7
        if (daysToAdd == 0 && calendar.timeInMillis <= System.currentTimeMillis()) {
            daysToAdd = 7
        }
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        return calendar
    }
}
