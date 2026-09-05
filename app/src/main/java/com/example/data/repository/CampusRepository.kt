package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsEntity
import com.example.data.local.ClassSlotEntity
import com.example.data.local.DefaultData
import com.example.data.local.MessMealEntity
import com.example.data.local.TaskEntity
import com.example.data.model.ClassExportItem
import com.example.data.model.LabGroup
import com.example.data.model.MessExportItem
import com.example.data.model.RecurringType
import com.example.data.model.ReminderPreset
import com.example.data.model.TaskModel
import com.example.data.model.TimetableExportPayload
import com.example.data.model.UserSettings
import com.example.notifications.NotificationScheduler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CampusRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val messDao = database.messDao()
    private val classDao = database.classDao()
    private val taskDao = database.taskDao()
    private val settingsDao = database.settingsDao()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Mess Meals
    val allMessMeals: Flow<List<MessMealEntity>> = messDao.getAllMessMeals()
    fun getMealsForDay(dayOfWeek: Int): Flow<List<MessMealEntity>> = messDao.getMealsForDay(dayOfWeek)

    suspend fun updateMessMeal(meal: MessMealEntity) = withContext(Dispatchers.IO) {
        messDao.update(meal.copy(isCustomized = true))
        rescheduleNotifications()
    }

    suspend fun resetMessToDefault() = withContext(Dispatchers.IO) {
        messDao.deleteAll()
        messDao.insertAll(DefaultData.getDefaultMessMeals())
        rescheduleNotifications()
    }

    // Class Slots
    val allClassSlots: Flow<List<ClassSlotEntity>> = classDao.getAllClassSlots()
    fun getClassSlotsForDay(dayOfWeek: Int): Flow<List<ClassSlotEntity>> = classDao.getClassSlotsForDay(dayOfWeek)

    suspend fun updateClassSlot(slot: ClassSlotEntity) = withContext(Dispatchers.IO) {
        classDao.update(slot.copy(isCustomized = true))
        rescheduleNotifications()
    }

    suspend fun insertClassSlot(slot: ClassSlotEntity) = withContext(Dispatchers.IO) {
        classDao.insert(slot.copy(isCustomized = true))
        rescheduleNotifications()
    }

    suspend fun deleteClassSlot(id: Long) = withContext(Dispatchers.IO) {
        classDao.deleteById(id)
        rescheduleNotifications()
    }

    suspend fun resetClassSlotsToDefault() = withContext(Dispatchers.IO) {
        classDao.deleteAll()
        classDao.insertAll(DefaultData.getDefaultClassSlots())
        rescheduleNotifications()
    }

    // Tasks
    val allTasks: Flow<List<TaskModel>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun saveTask(task: TaskModel): Long = withContext(Dispatchers.IO) {
        val entity = task.toEntity()
        val id = if (task.id == 0L) {
            taskDao.insert(entity)
        } else {
            taskDao.update(entity)
            task.id
        }
        rescheduleNotifications()
        id
    }

    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateCompletion(id, isCompleted)
    }

    suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteById(id)
        rescheduleNotifications()
    }

    // Settings
    val userSettings: Flow<UserSettings> = settingsDao.getSettings().map { entity ->
        if (entity == null) {
            UserSettings()
        } else {
            UserSettings(
                selectedLabGroup = try { LabGroup.valueOf(entity.selectedLabGroup) } catch (e: Exception) { LabGroup.GROUP_1 },
                classNotificationsEnabled = entity.classNotificationsEnabled,
                messNotificationsEnabled = entity.messNotificationsEnabled,
                taskNotificationsEnabled = entity.taskNotificationsEnabled,
                mutedDays = if (entity.mutedDaysCsv.isBlank()) emptySet() else entity.mutedDaysCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet(),
                isOnboardingCompleted = entity.isOnboardingCompleted,
                isDarkModeForced = when (entity.isDarkModeForced) {
                    1 -> false
                    2 -> true
                    else -> null
                }
            )
        }
    }

    suspend fun updateSettings(settings: UserSettings) = withContext(Dispatchers.IO) {
        val entity = AppSettingsEntity(
            id = 1,
            selectedLabGroup = settings.selectedLabGroup.name,
            classNotificationsEnabled = settings.classNotificationsEnabled,
            messNotificationsEnabled = settings.messNotificationsEnabled,
            taskNotificationsEnabled = settings.taskNotificationsEnabled,
            mutedDaysCsv = settings.mutedDays.joinToString(","),
            isOnboardingCompleted = settings.isOnboardingCompleted,
            isDarkModeForced = when (settings.isDarkModeForced) {
                false -> 1
                true -> 2
                else -> 0
            }
        )
        settingsDao.saveSettings(entity)
        rescheduleNotifications()
    }

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val meals = messDao.getAllMessMeals().first()
        if (meals.isEmpty()) {
            messDao.insertAll(DefaultData.getDefaultMessMeals())
        } else {
            // Self-healing: if database has duplicate slots for the same day and mealType, clean them up
            val grouped = meals.groupBy { "${it.dayOfWeek}_${it.mealType}" }
            for ((_, groupList) in grouped) {
                if (groupList.size > 1) {
                    val toKeep = groupList.firstOrNull { it.isCustomized } ?: groupList.first()
                    groupList.filter { it.id != toKeep.id }.forEach {
                        messDao.deleteById(it.id)
                    }
                }
            }
        }
        val classes = classDao.getAllClassSlots().first()
        if (classes.isEmpty()) {
            classDao.insertAll(DefaultData.getDefaultClassSlots())
        }
        val settings = settingsDao.getSettingsDirect()
        if (settings == null) {
            settingsDao.saveSettings(
                AppSettingsEntity(
                    id = 1,
                    selectedLabGroup = "GROUP_1",
                    classNotificationsEnabled = true,
                    messNotificationsEnabled = true,
                    taskNotificationsEnabled = true,
                    mutedDaysCsv = "",
                    isOnboardingCompleted = false
                )
            )
        }
    }

    // Export & Import Timetables
    suspend fun exportTimetableJson(): String = withContext(Dispatchers.IO) {
        val meals = messDao.getAllMessMeals().first().map {
            MessExportItem(
                dayOfWeek = it.dayOfWeek,
                mealType = it.mealType,
                timeSlot = it.timeSlot,
                menuDescription = it.menuDescription
            )
        }
        val classes = classDao.getAllClassSlots().first().map {
            ClassExportItem(
                dayOfWeek = it.dayOfWeek,
                startTime = it.startTime,
                endTime = it.endTime,
                subjectCode = it.subjectCode,
                subjectName = it.subjectName,
                faculty = it.faculty,
                roomOrLab = it.roomOrLab,
                groupType = it.groupType,
                isLab = it.isLab
            )
        }
        val payload = TimetableExportPayload(
            messMeals = meals,
            classSlots = classes
        )
        val adapter = moshi.adapter(TimetableExportPayload::class.java).indent("  ")
        adapter.toJson(payload)
    }

    suspend fun importTimetableJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val adapter = moshi.adapter(TimetableExportPayload::class.java)
            val payload = adapter.fromJson(jsonString) ?: return@withContext false

            if (payload.messMeals.isNotEmpty()) {
                messDao.deleteAll()
                val mealEntities = payload.messMeals.map {
                    MessMealEntity(
                        dayOfWeek = it.dayOfWeek,
                        mealType = it.mealType,
                        timeSlot = it.timeSlot,
                        menuDescription = it.menuDescription,
                        isCustomized = true
                    )
                }
                messDao.insertAll(mealEntities)
            }

            if (payload.classSlots.isNotEmpty()) {
                classDao.deleteAll()
                val classEntities = payload.classSlots.map {
                    ClassSlotEntity(
                        dayOfWeek = it.dayOfWeek,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        subjectCode = it.subjectCode,
                        subjectName = it.subjectName,
                        faculty = it.faculty,
                        roomOrLab = it.roomOrLab,
                        groupType = it.groupType,
                        isLab = it.isLab,
                        isCustomized = true
                    )
                }
                classDao.insertAll(classEntities)
            }

            rescheduleNotifications()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun rescheduleNotifications() = withContext(Dispatchers.IO) {
        try {
            val settings = settingsDao.getSettingsDirect() ?: return@withContext
            val labGroup = try { LabGroup.valueOf(settings.selectedLabGroup) } catch (e: Exception) { LabGroup.GROUP_1 }
            val mutedDays = if (settings.mutedDaysCsv.isBlank()) emptySet() else settings.mutedDaysCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
            
            val meals = messDao.getAllMessMeals().first()
            val classes = classDao.getAllClassSlots().first()
            val tasks = taskDao.getAllTasks().first()

            NotificationScheduler.scheduleAll(
                context = context,
                labGroup = labGroup,
                classEnabled = settings.classNotificationsEnabled,
                messEnabled = settings.messNotificationsEnabled,
                taskEnabled = settings.taskNotificationsEnabled,
                mutedDays = mutedDays,
                meals = meals,
                classes = classes,
                tasks = tasks
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun TaskEntity.toModel(): TaskModel {
    return TaskModel(
        id = id,
        title = title,
        description = description,
        subjectCode = subjectCode,
        dueDateEpochMs = dueDateEpochMs,
        dueTimeStr = dueTimeStr,
        reminderPreset = try { ReminderPreset.valueOf(reminderPreset) } catch (e: Exception) { ReminderPreset.NONE },
        reminderEpochMs = reminderEpochMs,
        isCompleted = isCompleted,
        recurringType = try { RecurringType.valueOf(recurringType) } catch (e: Exception) { RecurringType.NONE },
        createdAtEpochMs = createdAtEpochMs
    )
}

fun TaskModel.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        subjectCode = subjectCode,
        dueDateEpochMs = dueDateEpochMs,
        dueTimeStr = dueTimeStr,
        reminderPreset = reminderPreset.name,
        reminderEpochMs = reminderEpochMs,
        isCompleted = isCompleted,
        recurringType = recurringType.name,
        createdAtEpochMs = createdAtEpochMs
    )
}
