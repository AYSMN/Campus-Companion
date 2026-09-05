package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "mess_meals")
data class MessMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // Calendar.SUNDAY=1, Calendar.MONDAY=2, etc.
    val mealType: String, // BREAKFAST_MORNING, BREAKFAST_EVENING, LUNCH, DINNER
    val timeSlot: String,
    val menuDescription: String,
    val isCustomized: Boolean = false
)

@Entity(tableName = "class_slots")
data class ClassSlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // Calendar.MONDAY=2, etc.
    val startTime: String, // "10:30"
    val endTime: String, // "12:30"
    val subjectCode: String, // "EC031304 Lab"
    val subjectName: String, // "Signal and Systems Lab"
    val faculty: String, // "Dr. B. C. Sahana"
    val roomOrLab: String, // "DSP Lab"
    val groupType: String, // "GROUP_1", "GROUP_2", "ALL"
    val isLab: Boolean,
    val isCustomized: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val subjectCode: String? = null,
    val dueDateEpochMs: Long? = null,
    val dueTimeStr: String? = null,
    val reminderPreset: String = "NONE",
    val reminderEpochMs: Long? = null,
    val isCompleted: Boolean = false,
    val recurringType: String = "NONE",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val selectedLabGroup: String = "GROUP_1",
    val classNotificationsEnabled: Boolean = true,
    val messNotificationsEnabled: Boolean = true,
    val taskNotificationsEnabled: Boolean = true,
    val mutedDaysCsv: String = "", // Comma-separated day ints (e.g., "1,7" for Sun,Sat)
    val isOnboardingCompleted: Boolean = false,
    val isDarkModeForced: Int = 0 // 0=auto, 1=light, 2=dark
)
