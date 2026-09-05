package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClassSlotEntity
import com.example.data.model.LabGroup
import com.example.data.model.SubjectsCatalog
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.CampusViewModel
import java.util.Calendar

val CLASS_DAYS = listOf(
    DayTabItem(Calendar.MONDAY, "Monday", "Mon"),
    DayTabItem(Calendar.TUESDAY, "Tuesday", "Tue"),
    DayTabItem(Calendar.WEDNESDAY, "Wednesday", "Wed"),
    DayTabItem(Calendar.THURSDAY, "Thursday", "Thu"),
    DayTabItem(Calendar.FRIDAY, "Friday", "Fri"),
    DayTabItem(Calendar.SATURDAY, "Saturday", "Sat"),
    DayTabItem(Calendar.SUNDAY, "Sunday", "Sun")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    viewModel: CampusViewModel,
    contentPadding: PaddingValues
) {
    val allClasses by viewModel.allClassSlots.collectAsState()
    val selectedDay by viewModel.selectedClassDay.collectAsState()
    val groupFilter by viewModel.classGroupFilter.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val editingClass by viewModel.editingClass.collectAsState()

    var showLegend by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAddModal by remember { mutableStateOf(false) }

    val currentDayOfWeek = remember { CampusViewModel.getCurrentDayOfWeek() }
    val effectiveGroup = groupFilter ?: settings.selectedLabGroup

    val filteredClasses = remember(allClasses, selectedDay, effectiveGroup) {
        allClasses.filter { slot ->
            slot.dayOfWeek == selectedDay &&
                    (effectiveGroup == LabGroup.ALL ||
                            slot.groupType == "ALL" ||
                            (slot.groupType == "GROUP_1" && effectiveGroup == LabGroup.GROUP_1) ||
                            (slot.groupType == "GROUP_2" && effectiveGroup == LabGroup.GROUP_2))
        }.sortedBy { it.startTime }
    }

    val selectedDayObj = CLASS_DAYS.firstOrNull { it.dayInt == selectedDay } ?: CLASS_DAYS.first()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isWideScreen = screenWidthDp >= 600 || (isLandscape && screenWidthDp >= 500)
    val extraBottom = if (isWideScreen) 24.dp else 90.dp

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
        // 1. Header & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Class Timetable",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "B.Tech EE-VLSI • 3rd Sem Sec B • LH217 Bihta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showAddModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricSapphire)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Class Slot",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.openExportImportDialog() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync JSON",
                            tint = ElectricSapphire,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Timetable",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Day Selector Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(CLASS_DAYS) { dayItem ->
                    val isSelected = dayItem.dayInt == selectedDay
                    val isToday = dayItem.dayInt == currentDayOfWeek

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) ElectricSapphire else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.selectClassDay(dayItem.dayInt) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dayItem.shortName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (isToday) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) AmberGold else EmeraldMint)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Group Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Pair(settings.selectedLabGroup, "My Group (${settings.selectedLabGroup.shortName})"),
                    Pair(LabGroup.GROUP_1, "Group I"),
                    Pair(LabGroup.GROUP_2, "Group II"),
                    Pair(LabGroup.ALL, "All Groups")
                )

                filters.forEach { (group, label) ->
                    val isSelected = (groupFilter == group) || (groupFilter == null && group == settings.selectedLabGroup)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonCyan else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setClassGroupFilter(group) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 4. Collapsible Subject & Faculty Legend
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp,
                elevation = 2.dp,
                onClick = { showLegend = !showLegend }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = ElectricSapphire,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Subject & Faculty Legend (5 Courses + Labs)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showLegend) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AnimatedVisibility(
                        visible = showLegend,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubjectsCatalog.ALL_SUBJECTS.forEach { subj ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(subj.color))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${subj.code}: ${subj.name}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${subj.faculty} • Venue: ${subj.defaultVenue}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Schedule Content
        if (selectedDay == Calendar.SATURDAY || selectedDay == Calendar.SUNDAY) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldMint.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldMint
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Weekend Free Days",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "No classes scheduled for Saturday & Sunday. Enjoy self-study and rest!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else if (filteredClasses.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Text(
                        text = "No classes scheduled on ${selectedDayObj.name} for the selected group.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            if (isWideScreen) {
                val pairs = filteredClasses.chunked(2)
                items(pairs) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        pair.forEach { slot ->
                            Box(modifier = Modifier.weight(1f)) {
                                ClassSlotDetailGlassCard(
                                    slot = slot,
                                    onEdit = { viewModel.openEditClass(slot) },
                                    onDelete = { viewModel.deleteClassPeriod(slot.id) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(filteredClasses, key = { it.id }) { slot ->
                    ClassSlotDetailGlassCard(
                        slot = slot,
                        onEdit = { viewModel.openEditClass(slot) },
                        onDelete = { viewModel.deleteClassPeriod(slot.id) }
                    )
                }
            }
        }

        // Free Period Info Note
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Note: 8:30–10:30 AM slots on all days are free periods. Lunch break is 12:30–13:30 PM.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Edit/Add Class Bottom Sheet
    if (editingClass != null || showAddModal) {
        val slotToEdit = editingClass ?: ClassSlotEntity(
            dayOfWeek = selectedDay,
            startTime = "10:30",
            endTime = "12:30",
            subjectCode = "EC031304 Lab",
            subjectName = "Signal and Systems Lab",
            faculty = "Dr. B. C. Sahana",
            roomOrLab = "DSP Lab",
            groupType = "GROUP_1",
            isLab = true
        )

        EditClassSlotBottomSheet(
            slot = slotToEdit,
            isNew = (editingClass == null),
            onDismiss = {
                viewModel.closeEditClass()
                showAddModal = false
            },
            onSave = { saved ->
                viewModel.saveClassEdit(saved)
                showAddModal = false
            }
        )
    }

    // Reset Timetable Confirmation
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Class Timetable?") },
            text = { Text("This will restore the entire EE-VLSI Section B schedule back to the original July–Dec 2026 semester timetable.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetClassTimetable()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetRose)
                ) {
                    Text("Reset Timetable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ClassSlotDetailGlassCard(
    slot: ClassSlotEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val subject = SubjectsCatalog.findSubject(slot.subjectCode)
    val accentColor = if (subject != null) Color(subject.color) else ElectricSapphire

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassBadge(
                        text = slot.subjectCode,
                        accentColor = accentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (slot.isLab) {
                        GlassBadge(
                            text = if (slot.groupType == "GROUP_1") "Gr. I Lab (2 Hours)" else if (slot.groupType == "GROUP_2") "Gr. II Lab (2 Hours)" else "Lab (2 Hours)",
                            accentColor = NeonCyan
                        )
                    } else {
                        GlassBadge(
                            text = "Lecture",
                            accentColor = ElectricSapphire
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (slot.isCustomized) {
                        GlassBadge(text = "Edited", accentColor = AmberGold)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Slot",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = slot.subjectName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = slot.faculty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${slot.startTime} – ${slot.endTime} • ${slot.roomOrLab}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClassSlotBottomSheet(
    slot: ClassSlotEntity,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (ClassSlotEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var subjectCode by remember { mutableStateOf(slot.subjectCode) }
    var subjectName by remember { mutableStateOf(slot.subjectName) }
    var faculty by remember { mutableStateOf(slot.faculty) }
    var roomOrLab by remember { mutableStateOf(slot.roomOrLab) }
    var startTime by remember { mutableStateOf(slot.startTime) }
    var endTime by remember { mutableStateOf(slot.endTime) }
    var groupType by remember { mutableStateOf(slot.groupType) }
    var isLab by remember { mutableStateOf(slot.isLab) }

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
                text = if (isNew) "Add Class Period" else "Edit Class Slot",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Subject selector
            Text(
                text = "Preset EE-VLSI Subjects:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(SubjectsCatalog.ALL_SUBJECTS) { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(item.color).copy(alpha = 0.2f))
                            .clickable {
                                subjectCode = item.code
                                subjectName = item.name
                                faculty = item.faculty
                                roomOrLab = item.defaultVenue
                                isLab = item.isLab
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.code,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(item.color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = subjectCode,
                onValueChange = { subjectCode = it },
                label = { Text("Subject Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start (e.g. 10:30)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End (e.g. 12:30)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = faculty,
                    onValueChange = { faculty = it },
                    label = { Text("Faculty") },
                    modifier = Modifier.weight(1.2f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = roomOrLab,
                    onValueChange = { roomOrLab = it },
                    label = { Text("Room/Lab") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lab Group selector
            Text(
                text = "Target Lab Group:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Pair("ALL", "All Students"),
                    Pair("GROUP_1", "Group I Only"),
                    Pair("GROUP_2", "Group II Only")
                ).forEach { (type, label) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (groupType == type) ElectricSapphire else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { groupType = type }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (groupType == type) Color.White else MaterialTheme.colorScheme.onSurface
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
                        onSave(
                            slot.copy(
                                subjectCode = subjectCode.trim(),
                                subjectName = subjectName.trim(),
                                faculty = faculty.trim(),
                                roomOrLab = roomOrLab.trim(),
                                startTime = startTime.trim(),
                                endTime = endTime.trim(),
                                groupType = groupType,
                                isLab = isLab
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricSapphire)
                ) {
                    Text("Save Slot")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
