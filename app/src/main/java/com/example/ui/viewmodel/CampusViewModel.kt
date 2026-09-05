package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ClassSlotEntity
import com.example.data.local.MessMealEntity
import com.example.data.model.LabGroup
import com.example.data.model.RecurringType
import com.example.data.model.ReminderPreset
import com.example.data.model.SubjectsCatalog
import com.example.data.model.TaskModel
import com.example.data.model.UserSettings
import com.example.data.repository.CampusRepository
import com.example.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
    HOME("Home"),
    MESS("Mess"),
    CLASSES("Classes"),
    TASKS("Tasks")
}

data class NextClassInfo(
    val slot: ClassSlotEntity,
    val minutesUntilStart: Long,
    val isOngoing: Boolean
)

data class NextMealInfo(
    val meal: MessMealEntity,
    val label: String,
    val isServingNow: Boolean
)

class CampusViewModel(application: Application) : AndroidViewModel(application) {
    val repository = CampusRepository(application.applicationContext)

    // Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Mess Screen Day Filter (Calendar.SUNDAY=1 .. Calendar.SATURDAY=7)
    private val _selectedMessDay = MutableStateFlow(getCurrentDayOfWeek())
    val selectedMessDay: StateFlow<Int> = _selectedMessDay.asStateFlow()

    // Class Screen Day Filter (Calendar.MONDAY=2 .. Calendar.FRIDAY=6)
    private val _selectedClassDay = MutableStateFlow(if (getCurrentDayOfWeek() in 2..6) getCurrentDayOfWeek() else Calendar.MONDAY)
    val selectedClassDay: StateFlow<Int> = _selectedClassDay.asStateFlow()

    // Class Group Filter
    private val _classGroupFilter = MutableStateFlow<LabGroup?>(null) // null = user's group, or explicit
    val classGroupFilter: StateFlow<LabGroup?> = _classGroupFilter.asStateFlow()

    // Task Filter & Search
    private val _taskFilterTag = MutableStateFlow<String?>("ALL") // "ALL", "TODAY", "UPCOMING", "OVERDUE", or SubjectCode
    val taskFilterTag: StateFlow<String?> = _taskFilterTag.asStateFlow()

    private val _taskSearchQuery = MutableStateFlow("")
    val taskSearchQuery: StateFlow<String> = _taskSearchQuery.asStateFlow()

    // Settings
    val userSettings: StateFlow<UserSettings> = repository.userSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    // All meals and classes from database
    val allMessMeals: StateFlow<List<MessMealEntity>> = repository.allMessMeals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allClassSlots: StateFlow<List<ClassSlotEntity>> = repository.allClassSlots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTasks: StateFlow<List<TaskModel>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Modals / Sheets State
    private val _editingMeal = MutableStateFlow<MessMealEntity?>(null)
    val editingMeal: StateFlow<MessMealEntity?> = _editingMeal.asStateFlow()

    private val _editingClass = MutableStateFlow<ClassSlotEntity?>(null)
    val editingClass: StateFlow<ClassSlotEntity?> = _editingClass.asStateFlow()

    private val _editingTask = MutableStateFlow<TaskModel?>(null)
    val editingTask: StateFlow<TaskModel?> = _editingTask.asStateFlow()
    private val _isAddingTask = MutableStateFlow(false)
    val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private val _showExportImportDialog = MutableStateFlow(false)
    val showExportImportDialog: StateFlow<Boolean> = _showExportImportDialog.asStateFlow()

    private val _selectedSubjectDetail = MutableStateFlow<String?>(null)
    val selectedSubjectDetail: StateFlow<String?> = _selectedSubjectDetail.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            repository.rescheduleNotifications()
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectMessDay(day: Int) {
        _selectedMessDay.value = day
    }

    fun selectClassDay(day: Int) {
        _selectedClassDay.value = day
    }

    fun setClassGroupFilter(group: LabGroup?) {
        _classGroupFilter.value = group
    }

    fun setTaskFilterTag(tag: String?) {
        _taskFilterTag.value = tag
    }

    fun setTaskSearchQuery(query: String) {
        _taskSearchQuery.value = query
    }

    // User settings updates
    fun updateSelectedLabGroup(group: LabGroup) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(selectedLabGroup = group))
            _snackbarMessage.value = "Updated Lab Group to ${group.displayName}"
        }
    }

    fun toggleClassNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(classNotificationsEnabled = enabled))
        }
    }

    fun toggleMessNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(messNotificationsEnabled = enabled))
        }
    }

    fun toggleTaskNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(taskNotificationsEnabled = enabled))
        }
    }

    fun toggleMutedDay(dayOfWeek: Int) {
        viewModelScope.launch {
            val current = userSettings.value
            val newMuted = current.mutedDays.toMutableSet()
            if (newMuted.contains(dayOfWeek)) {
                newMuted.remove(dayOfWeek)
            } else {
                newMuted.add(dayOfWeek)
            }
            repository.updateSettings(current.copy(mutedDays = newMuted))
        }
    }

    fun setDarkModePreference(isDark: Boolean?) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(isDarkModeForced = isDark))
        }
    }

    fun completeOnboarding(selectedGroup: LabGroup) {
        viewModelScope.launch {
            val current = userSettings.value
            repository.updateSettings(current.copy(selectedLabGroup = selectedGroup, isOnboardingCompleted = true))
        }
    }

    // Meal Actions
    fun openEditMeal(meal: MessMealEntity) {
        _editingMeal.value = meal
    }

    fun closeEditMeal() {
        _editingMeal.value = null
    }

    fun saveMealEdit(updatedMeal: MessMealEntity) {
        viewModelScope.launch {
            repository.updateMessMeal(updatedMeal)
            _editingMeal.value = null
            _snackbarMessage.value = "Mess menu updated!"
        }
    }

    fun resetMessMenu() {
        viewModelScope.launch {
            repository.resetMessToDefault()
            _snackbarMessage.value = "Mess menu reset to default timetable"
        }
    }

    // Class Actions
    fun openEditClass(slot: ClassSlotEntity) {
        _editingClass.value = slot
    }

    fun closeEditClass() {
        _editingClass.value = null
    }

    fun saveClassEdit(slot: ClassSlotEntity) {
        viewModelScope.launch {
            if (slot.id == 0L) {
                repository.insertClassSlot(slot)
                _snackbarMessage.value = "New class period added!"
            } else {
                repository.updateClassSlot(slot)
                _snackbarMessage.value = "Class timetable updated!"
            }
            _editingClass.value = null
        }
    }

    fun deleteClassPeriod(id: Long) {
        viewModelScope.launch {
            repository.deleteClassSlot(id)
            _editingClass.value = null
            _snackbarMessage.value = "Class slot deleted"
        }
    }

    fun resetClassTimetable() {
        viewModelScope.launch {
            repository.resetClassSlotsToDefault()
            _snackbarMessage.value = "Class timetable reset to default schedule"
        }
    }

    // Task Actions
    fun openAddTask() {
        _isAddingTask.value = true
        _editingTask.value = null
    }

    fun openEditTask(task: TaskModel) {
        _editingTask.value = task
        _isAddingTask.value = true
    }

    fun closeTaskSheet() {
        _isAddingTask.value = false
        _editingTask.value = null
    }

    fun saveTask(
        title: String,
        description: String,
        subjectCode: String?,
        dueDateEpochMs: Long?,
        dueTimeStr: String?,
        reminderPreset: ReminderPreset,
        recurringType: RecurringType
    ) {
        viewModelScope.launch {
            val existing = _editingTask.value
            val reminderEpochMs = calculateReminderEpoch(dueDateEpochMs, dueTimeStr, reminderPreset)

            val taskToSave = TaskModel(
                id = existing?.id ?: 0L,
                title = title.trim(),
                description = description.trim(),
                subjectCode = if (subjectCode.isNullOrBlank() || subjectCode == "None") null else subjectCode,
                dueDateEpochMs = dueDateEpochMs,
                dueTimeStr = dueTimeStr,
                reminderPreset = reminderPreset,
                reminderEpochMs = reminderEpochMs,
                isCompleted = existing?.isCompleted ?: false,
                recurringType = recurringType,
                createdAtEpochMs = existing?.createdAtEpochMs ?: System.currentTimeMillis()
            )
            repository.saveTask(taskToSave)
            closeTaskSheet()
            _snackbarMessage.value = if (existing == null) "Task added!" else "Task updated!"
        }
    }

    fun toggleTaskCompletion(task: TaskModel) {
        viewModelScope.launch {
            val newStatus = !task.isCompleted
            repository.setTaskCompleted(task.id, newStatus)

            // If it's recurring and marked completed, schedule next occurrence
            if (newStatus && task.recurringType != RecurringType.NONE && task.dueDateEpochMs != null) {
                val nextDue = when (task.recurringType) {
                    RecurringType.DAILY -> task.dueDateEpochMs + (24 * 60 * 60 * 1000L)
                    RecurringType.WEEKLY -> task.dueDateEpochMs + (7 * 24 * 60 * 60 * 1000L)
                    RecurringType.NONE -> task.dueDateEpochMs
                }
                val nextReminder = calculateReminderEpoch(nextDue, task.dueTimeStr, task.reminderPreset)
                val recurringTask = task.copy(
                    id = 0,
                    dueDateEpochMs = nextDue,
                    reminderEpochMs = nextReminder,
                    isCompleted = false,
                    createdAtEpochMs = System.currentTimeMillis()
                )
                repository.saveTask(recurringTask)
                _snackbarMessage.value = "Task completed! Next ${task.recurringType.displayName} occurrence created."
            } else {
                _snackbarMessage.value = if (newStatus) "Task completed! 🎉" else "Task reopened"
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
            _snackbarMessage.value = "Task deleted"
        }
    }

    // Export & Import
    fun openExportImportDialog() {
        _showExportImportDialog.value = true
    }

    fun closeExportImportDialog() {
        _showExportImportDialog.value = false
    }

    suspend fun getExportJson(): String {
        return repository.exportTimetableJson()
    }

    fun importJson(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importTimetableJson(json)
            if (success) {
                _snackbarMessage.value = "Timetables imported successfully!"
                _showExportImportDialog.value = false
            } else {
                _snackbarMessage.value = "Failed to parse timetable JSON"
            }
            onResult(success)
        }
    }

    // Modals
    fun openSettings() {
        _showSettingsSheet.value = true
    }

    fun closeSettings() {
        _showSettingsSheet.value = false
    }

    fun openSubjectDetail(code: String) {
        _selectedSubjectDetail.value = code
    }

    fun closeSubjectDetail() {
        _selectedSubjectDetail.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun triggerTestNotification(type: String) {
        NotificationScheduler.triggerTestNotification(getApplication(), type)
        _snackbarMessage.value = "Sent test $type notification!"
    }

    // Helper functions
    private fun calculateReminderEpoch(dueDate: Long?, timeStr: String?, preset: ReminderPreset): Long? {
        if (dueDate == null || preset == ReminderPreset.NONE) return null

        val cal = Calendar.getInstance().apply {
            timeInMillis = dueDate
            if (!timeStr.isNullOrBlank()) {
                val parts = timeStr.split(":")
                set(Calendar.HOUR_OF_DAY, parts.getOrNull(0)?.toIntOrNull() ?: 12)
                set(Calendar.MINUTE, parts.getOrNull(1)?.toIntOrNull() ?: 0)
            } else {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return when (preset) {
            ReminderPreset.NONE -> null
            ReminderPreset.AT_DUE -> cal.timeInMillis
            ReminderPreset.MINUTES_15 -> cal.timeInMillis - (15 * 60 * 1000L)
            ReminderPreset.HOUR_1 -> cal.timeInMillis - (60 * 60 * 1000L)
            ReminderPreset.HOURS_3 -> cal.timeInMillis - (3 * 60 * 60 * 1000L)
            ReminderPreset.MORNING_OF -> {
                cal.set(Calendar.HOUR_OF_DAY, 8)
                cal.set(Calendar.MINUTE, 0)
                cal.timeInMillis
            }
        }
    }

    companion object {
        fun getCurrentDayOfWeek(): Int {
            return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        }
    }
}
