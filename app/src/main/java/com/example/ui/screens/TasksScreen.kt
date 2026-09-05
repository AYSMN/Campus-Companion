package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecurringType
import com.example.data.model.ReminderPreset
import com.example.data.model.SubjectsCatalog
import com.example.data.model.TaskModel
import com.example.ui.components.AnimatedCheckmark
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.CampusViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: CampusViewModel,
    contentPadding: PaddingValues
) {
    val allTasks by viewModel.allTasks.collectAsState()
    val filterTag by viewModel.taskFilterTag.collectAsState()
    val searchQuery by viewModel.taskSearchQuery.collectAsState()
    val isAddingTask by viewModel.isAddingTask.collectAsState()
    val editingTask by viewModel.editingTask.collectAsState()

    var showCompletedSection by remember { mutableStateOf(false) }

    val filteredTasks = remember(allTasks, filterTag, searchQuery) {
        allTasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() ||
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description.contains(searchQuery, ignoreCase = true) ||
                    (task.subjectCode?.contains(searchQuery, ignoreCase = true) == true)

            val matchesTag = when (filterTag) {
                "ALL", null -> true
                "TODAY" -> {
                    if (task.dueDateEpochMs == null) false
                    else {
                        val calTask = Calendar.getInstance().apply { timeInMillis = task.dueDateEpochMs }
                        val calNow = Calendar.getInstance()
                        calTask.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                                calTask.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)
                    }
                }
                "UPCOMING" -> {
                    task.dueDateEpochMs != null && task.dueDateEpochMs >= System.currentTimeMillis()
                }
                "OVERDUE" -> {
                    task.dueDateEpochMs != null && task.dueDateEpochMs < System.currentTimeMillis() && !task.isCompleted
                }
                else -> task.subjectCode == filterTag
            }

            matchesSearch && matchesTag
        }
    }

    val pendingTasks = remember(filteredTasks) { filteredTasks.filter { !it.isCompleted } }
    val completedTasks = remember(filteredTasks) { filteredTasks.filter { it.isCompleted } }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isWideScreen = screenWidthDp >= 600 || (isLandscape && screenWidthDp >= 500)
    val extraBottom = if (isWideScreen) 24.dp else 90.dp
    val fabBottom = if (isWideScreen) contentPadding.calculateBottomPadding() + 24.dp else contentPadding.calculateBottomPadding() + 96.dp

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + extraBottom,
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tasks & Homework",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Personal assignment tracker • Reminders enabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    GlassBadge(
                        text = "${pendingTasks.size} Pending",
                        accentColor = if (pendingTasks.isNotEmpty()) VioletPurple else EmeraldMint
                    )
                }
            }

            // 2. Search Field
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setTaskSearchQuery(it) },
                            placeholder = { Text("Search homework, notes, lab reports...") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setTaskSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // 3. Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    val filterPresets = listOf(
                        Pair("ALL", "All"),
                        Pair("TODAY", "Due Today"),
                        Pair("UPCOMING", "Upcoming"),
                        Pair("OVERDUE", "Overdue"),
                        Pair("EC031304", "Signal & Systems"),
                        Pair("EC031301", "Digital Electronics"),
                        Pair("EC031302", "Analog Electronics"),
                        Pair("EC031303", "Network Theory"),
                        Pair("CS031301", "Data Structures")
                    )

                    items(filterPresets) { (tag, label) ->
                        val isSelected = filterTag == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) VioletPurple else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setTaskFilterTag(tag) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 4. Pending Tasks List
            if (pendingTasks.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldMint.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldMint,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No pending tasks!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap the '+' button below to add homework or standing reminders.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                if (isWideScreen) {
                    val pairs = pendingTasks.chunked(2)
                    items(pairs) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            pair.forEach { task ->
                                Box(modifier = Modifier.weight(1f)) {
                                    TaskCardItem(
                                        task = task,
                                        onToggle = { viewModel.toggleTaskCompletion(task) },
                                        onEdit = { viewModel.openEditTask(task) },
                                        onDelete = { viewModel.deleteTask(task.id) }
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    items(pendingTasks, key = { it.id }) { task ->
                        TaskCardItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { viewModel.openEditTask(task) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }

            // 5. Collapsible Completed Section
            if (completedTasks.isNotEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 18.dp,
                        elevation = 2.dp,
                        onClick = { showCompletedSection = !showCompletedSection }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldMint,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Completed (${completedTasks.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (showCompletedSection) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (showCompletedSection) {
                    if (isWideScreen) {
                        val pairs = completedTasks.chunked(2)
                        items(pairs) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                pair.forEach { task ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        TaskCardItem(
                                            task = task,
                                            onToggle = { viewModel.toggleTaskCompletion(task) },
                                            onEdit = { viewModel.openEditTask(task) },
                                            onDelete = { viewModel.deleteTask(task.id) }
                                        )
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        items(completedTasks, key = { it.id }) { task ->
                            TaskCardItem(
                                task = task,
                                onToggle = { viewModel.toggleTaskCompletion(task) },
                                onEdit = { viewModel.openEditTask(task) },
                                onDelete = { viewModel.deleteTask(task.id) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.openAddTask() },
            containerColor = VioletPurple,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = fabBottom, end = 20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    // Add / Edit Task Modal Bottom Sheet
    if (isAddingTask) {
        AddTaskBottomSheet(
            taskToEdit = editingTask,
            onDismiss = { viewModel.closeTaskSheet() },
            onSave = { title, desc, subj, dueDate, dueTime, reminder, recur ->
                viewModel.saveTask(title, desc, subj, dueDate, dueTime, reminder, recur)
            }
        )
    }
}

@Composable
fun TaskCardItem(
    task: TaskModel,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val subject = if (task.subjectCode != null) SubjectsCatalog.findSubject(task.subjectCode) else null
    val subjectColor = if (subject != null) Color(subject.color) else ElectricSapphire

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = if (task.isCompleted) 1.dp else 4.dp,
        surfaceAlpha = if (task.isCompleted) 0.35f else 0.6f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedCheckmark(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    size = 24.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEdit)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SunsetRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Badges row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!task.subjectCode.isNullOrBlank()) {
                    GlassBadge(
                        text = task.subjectCode,
                        accentColor = subjectColor
                    )
                }

                if (task.dueDateEpochMs != null) {
                    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                    val dateStr = sdf.format(Date(task.dueDateEpochMs))
                    val timeStr = if (!task.dueTimeStr.isNullOrBlank()) " • ${task.dueTimeStr}" else ""
                    val isOverdue = task.dueDateEpochMs < System.currentTimeMillis() && !task.isCompleted

                    GlassBadge(
                        text = "$dateStr$timeStr",
                        accentColor = if (isOverdue) SunsetRose else AmberGold
                    )
                }

                if (task.reminderPreset != ReminderPreset.NONE) {
                    GlassBadge(
                        text = task.reminderPreset.displayName,
                        accentColor = ElectricSapphire
                    )
                }

                if (task.recurringType != RecurringType.NONE) {
                    GlassBadge(
                        text = "Repeats ${task.recurringType.displayName}",
                        accentColor = EmeraldMint
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    taskToEdit: TaskModel?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        subjectCode: String?,
        dueDateEpochMs: Long?,
        dueTimeStr: String?,
        reminderPreset: ReminderPreset,
        recurringType: RecurringType
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var selectedSubject by remember { mutableStateOf(taskToEdit?.subjectCode ?: "None") }
    var dueDateEpochMs by remember { mutableStateOf<Long?>(taskToEdit?.dueDateEpochMs) }
    var dueTimeStr by remember { mutableStateOf(taskToEdit?.dueTimeStr ?: "17:00") }
    var reminderPreset by remember { mutableStateOf(taskToEdit?.reminderPreset ?: ReminderPreset.HOUR_1) }
    var recurringType by remember { mutableStateOf(taskToEdit?.recurringType ?: RecurringType.NONE) }

    val formattedDueDate = remember(dueDateEpochMs) {
        if (dueDateEpochMs == null) "Select Due Date"
        else {
            val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            sdf.format(Date(dueDateEpochMs!!))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = if (taskToEdit == null) "New Task / Assignment" else "Edit Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                placeholder = { Text("e.g., Revise DSP Fourier Transform notes") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description / Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Link Course Subject
            Text(
                text = "Link to Course (Optional):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    val isSelected = selectedSubject == "None"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ElectricSapphire else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { selectedSubject = "None" }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "General",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                items(SubjectsCatalog.ALL_SUBJECTS) { subj ->
                    val isSelected = selectedSubject == subj.code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Color(subj.color) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { selectedSubject = subj.code }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = subj.code,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Due Date & Time Pickers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val picked = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth, 0, 0, 0)
                                }
                                dueDateEpochMs = picked.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = formattedDueDate, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                dueTimeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                            },
                            17,
                            0,
                            true
                        ).show()
                    },
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = dueTimeStr, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reminder Options
            Text(
                text = "Reminder Alert:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ReminderPreset.values().toList()) { preset ->
                    val isSelected = reminderPreset == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ElectricSapphire else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { reminderPreset = preset }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = preset.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recurring Options
            Text(
                text = "Recurring Schedule:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RecurringType.values().forEach { recur ->
                    val isSelected = recurringType == recur
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) EmeraldMint else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { recurringType = recur }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = recur.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(
                                title,
                                description,
                                selectedSubject,
                                dueDateEpochMs,
                                dueTimeStr,
                                reminderPreset,
                                recurringType
                            )
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPurple)
                ) {
                    Text("Save Task")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
