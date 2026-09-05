package com.example.data.model

enum class RecurringType(val displayName: String) {
    NONE("One-time"),
    DAILY("Daily"),
    WEEKLY("Weekly")
}

enum class ReminderPreset(val displayName: String, val minutesBefore: Long) {
    NONE("No reminder", 0),
    AT_DUE("At due time", 0),
    MINUTES_15("15 minutes before", 15),
    HOUR_1("1 hour before", 60),
    HOURS_3("3 hours before", 180),
    MORNING_OF("Morning of (8:00 AM)", -1)
}

data class TaskModel(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subjectCode: String? = null,
    val dueDateEpochMs: Long? = null,
    val dueTimeStr: String? = null,
    val reminderPreset: ReminderPreset = ReminderPreset.NONE,
    val reminderEpochMs: Long? = null,
    val isCompleted: Boolean = false,
    val recurringType: RecurringType = RecurringType.NONE,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

data class UserSettings(
    val selectedLabGroup: LabGroup = LabGroup.GROUP_1,
    val classNotificationsEnabled: Boolean = true,
    val messNotificationsEnabled: Boolean = true,
    val taskNotificationsEnabled: Boolean = true,
    val mutedDays: Set<Int> = emptySet(), // 1=Sunday, 2=Monday, ..., 7=Saturday
    val isOnboardingCompleted: Boolean = false,
    val isDarkModeForced: Boolean? = null // null = system default
)

data class MessExportItem(
    val dayOfWeek: Int, // 1=Sunday, 2=Monday, ..., 7=Saturday (Calendar convention)
    val mealType: String,
    val timeSlot: String,
    val menuDescription: String
)

data class ClassExportItem(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val subjectCode: String,
    val subjectName: String,
    val faculty: String,
    val roomOrLab: String,
    val groupType: String,
    val isLab: Boolean
)

data class TimetableExportPayload(
    val exportVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val courseInfo: String = "B.Tech EE-VLSI, 3rd Sem Sec B - LH217 Bihta",
    val messMeals: List<MessExportItem>,
    val classSlots: List<ClassExportItem>
)
