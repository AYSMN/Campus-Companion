package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ClassSlotEntity
import com.example.data.local.MessMealEntity
import com.example.data.model.LabGroup
import com.example.data.model.SubjectsCatalog
import com.example.data.model.TaskModel
import com.example.ui.components.AnimatedCheckmark
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.TimeOfDayPeriod
import com.example.ui.components.getCurrentTimePeriod
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.CampusViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: CampusViewModel,
    contentPadding: PaddingValues
) {
    val allMeals by viewModel.allMessMeals.collectAsState()
    val allClasses by viewModel.allClassSlots.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val isDark = LocalIsDark.current

    val currentDayOfWeek = remember { CampusViewModel.getCurrentDayOfWeek() }
    val todayMeals = remember(allMeals, currentDayOfWeek) {
        allMeals
            .filter { it.dayOfWeek == currentDayOfWeek }
            .distinctBy { it.mealType }
            .sortedBy { meal ->
                when (meal.mealType) {
                    "BREAKFAST_MORNING" -> 1
                    "LUNCH" -> 2
                    "BREAKFAST_EVENING" -> 3
                    "DINNER" -> 4
                    else -> 5
                }
            }
    }
    val todayClasses = remember(allClasses, currentDayOfWeek, settings.selectedLabGroup) {
        allClasses.filter { slot ->
            slot.dayOfWeek == currentDayOfWeek &&
                    (slot.groupType == "ALL" ||
                            (slot.groupType == "GROUP_1" && (settings.selectedLabGroup == LabGroup.GROUP_1 || settings.selectedLabGroup == LabGroup.ALL)) ||
                            (slot.groupType == "GROUP_2" && (settings.selectedLabGroup == LabGroup.GROUP_2 || settings.selectedLabGroup == LabGroup.ALL)))
        }.sortedBy { it.startTime }
    }

    val pendingTasks = remember(allTasks) {
        allTasks.filter { !it.isCompleted }.take(3)
    }

    val timePeriod = remember { getCurrentTimePeriod() }
    val greeting = when (timePeriod) {
        TimeOfDayPeriod.MORNING -> "Good morning, Engineer"
        TimeOfDayPeriod.AFTERNOON -> "Good afternoon, Engineer"
        TimeOfDayPeriod.EVENING -> "Good evening, Engineer"
        TimeOfDayPeriod.NIGHT -> "Burning the midnight oil"
    }

    val formattedDate = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        sdf.format(Date())
    }

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Bar
        item {
            HeaderSection(
                greeting = greeting,
                date = formattedDate,
                selectedGroup = settings.selectedLabGroup,
                isDark = isDark,
                onToggleTheme = {
                    viewModel.setDarkModePreference(!isDark)
                },
                onGroupToggle = {
                    val nextGroup = if (settings.selectedLabGroup == LabGroup.GROUP_1) LabGroup.GROUP_2 else LabGroup.GROUP_1
                    viewModel.updateSelectedLabGroup(nextGroup)
                },
                onOpenSettings = { viewModel.openSettings() }
            )
        }

        // 2. Dynamic Live Status Card
        item {
            LiveStatusHeroCard(
                todayClasses = todayClasses,
                todayMeals = todayMeals,
                selectedGroup = settings.selectedLabGroup
            )
        }

        // 3. Today's Mess Menu Section
        item {
            SectionHeader(
                title = "Today's Mess Menu",
                icon = Icons.Default.Restaurant,
                accentColor = AmberGold,
                actionLabel = "Full Week",
                onActionClick = { viewModel.selectTab(AppTab.MESS) }
            )
        }

        if (todayMeals.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Text(
                        text = "Loading mess menu...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(todayMeals, key = { it.id }) { meal ->
                MealGlassItem(
                    meal = meal,
                    onEdit = { viewModel.openEditMeal(meal) }
                )
            }
        }

        // 4. Today's Class Schedule Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Today's Classes & Labs",
                icon = Icons.Default.School,
                accentColor = ElectricSapphire,
                actionLabel = "Full Timetable",
                onActionClick = { viewModel.selectTab(AppTab.CLASSES) }
            )
        }

        if (currentDayOfWeek == Calendar.SATURDAY || currentDayOfWeek == Calendar.SUNDAY) {
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
                                text = "Weekend Free Period!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "No lectures or lab sessions scheduled for today.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else if (todayClasses.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Text(
                        text = "No classes for your group today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(todayClasses, key = { it.id }) { slot ->
                ClassSlotGlassItem(
                    slot = slot,
                    onEdit = { viewModel.openEditClass(slot) },
                    onSubjectClick = { viewModel.openSubjectDetail(slot.subjectCode) }
                )
            }
        }

        // 5. Priority Tasks Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Priority Tasks",
                icon = Icons.Default.MenuBook,
                accentColor = VioletPurple,
                actionLabel = "All Tasks (${allTasks.count { !it.isCompleted }})",
                onActionClick = { viewModel.selectTab(AppTab.TASKS) }
            )
        }

        if (pendingTasks.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "All caught up! 🎉",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "No pending homework or study deadlines.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ElevatedButton(
                            onClick = { viewModel.openAddTask() },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = VioletPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Task", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(pendingTasks, key = { it.id }) { task ->
                HomeTaskItem(
                    task = task,
                    onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                    onEdit = { viewModel.openEditTask(task) }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    date: String,
    selectedGroup: LabGroup,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onGroupToggle: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.example.R.drawable.img_silicon_wafer_logo),
                contentDescription = "Silicon Wafer Logo",
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Campus Companion",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricSapphire,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$date • EE-VLSI Sec B",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Lab Group Switcher Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable(onClick = onGroupToggle)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (selectedGroup == LabGroup.GROUP_1) NeonCyan else AmberGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedGroup.shortName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Quick Theme Toggle Button
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
                    tint = if (isDark) AmberGold else ElectricSapphire,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveStatusHeroCard(
    todayClasses: List<ClassSlotEntity>,
    todayMeals: List<MessMealEntity>,
    selectedGroup: LabGroup
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    val currentTimeMins = currentHour * 60 + currentMinute

    // Determine upcoming/ongoing class
    var ongoingClass: ClassSlotEntity? = null
    var nextClass: ClassSlotEntity? = null

    for (slot in todayClasses) {
        val startMins = parseTimeToMins(slot.startTime)
        val endMins = parseTimeToMins(slot.endTime)

        if (currentTimeMins in startMins..endMins) {
            ongoingClass = slot
            break
        } else if (startMins > currentTimeMins && nextClass == null) {
            nextClass = slot
        }
    }

    // Determine current meal window
    val activeMeal = todayMeals.firstOrNull { meal ->
        when (meal.mealType) {
            "BREAKFAST_MORNING" -> currentTimeMins in (7 * 60)..(9 * 60 + 30)
            "LUNCH" -> currentTimeMins in (12 * 60 + 30)..(14 * 60 + 30)
            "BREAKFAST_EVENING" -> currentTimeMins in (16 * 60 + 30)..(17 * 60 + 30)
            "DINNER" -> currentTimeMins in (20 * 60)..(22 * 60)
            else -> false
        }
    } ?: todayMeals.firstOrNull { meal ->
        // Next upcoming meal
        when (meal.mealType) {
            "BREAKFAST_MORNING" -> currentTimeMins < 7 * 60
            "LUNCH" -> currentTimeMins < (12 * 60 + 30)
            "BREAKFAST_EVENING" -> currentTimeMins < (16 * 60 + 30)
            "DINNER" -> currentTimeMins < 20 * 60
            else -> false
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        elevation = 10.dp,
        surfaceAlpha = 0.65f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EmeraldMint)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACTIVE CAMPUS SNAPSHOT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }

                GlassBadge(
                    text = "LH217 Bihta",
                    accentColor = ElectricSapphire
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (ongoingClass != null) {
                // Class in progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(ElectricSapphire, NeonCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassBadge(
                                text = "HAPPENING NOW",
                                accentColor = EmeraldMint
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${ongoingClass.startTime} – ${ongoingClass.endTime}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = ongoingClass.subjectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${ongoingClass.faculty} • ${ongoingClass.roomOrLab}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (nextClass != null) {
                // Upcoming class
                val startMins = parseTimeToMins(nextClass.startTime)
                val diffMins = startMins - currentTimeMins
                val countdownText = if (diffMins in 1..60) "Starts in ${diffMins}m" else "Next at ${nextClass.startTime}"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(ElectricSapphire, VioletPurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassBadge(
                                text = countdownText,
                                accentColor = ElectricSapphire
                            )
                            if (nextClass.isLab) {
                                Spacer(modifier = Modifier.width(6.dp))
                                GlassBadge(
                                    text = nextClass.groupType.replace("GROUP_", "Gr. "),
                                    accentColor = NeonCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = nextClass.subjectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${nextClass.faculty} • ${nextClass.roomOrLab}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "No more lectures scheduled today ✨",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (activeMeal != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDining,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${getMealTitle(activeMeal.mealType)} • ${activeMeal.timeSlot}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                            Text(
                                text = activeMeal.menuDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun MealGlassItem(
    meal: MessMealEntity,
    onEdit: () -> Unit
) {
    val (icon, color) = when (meal.mealType) {
        "BREAKFAST_MORNING" -> Pair(Icons.Default.WbSunny, AmberGold)
        "BREAKFAST_EVENING" -> Pair(Icons.Default.Coffee, SunsetRose)
        "LUNCH" -> Pair(Icons.Default.Restaurant, EmeraldMint)
        "DINNER" -> Pair(Icons.Default.Nightlight, VioletPurple)
        else -> Pair(Icons.Default.LocalDining, ElectricSapphire)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = getMealTitle(meal.mealType),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = meal.timeSlot,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dishes formatted nicely
            val dishes = meal.menuDescription.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            FlowDishRow(dishes = dishes, accentColor = color)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowDishRow(dishes: List<String>, accentColor: Color) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        dishes.forEach { dish ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = dish,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ClassSlotGlassItem(
    slot: ClassSlotEntity,
    onEdit: () -> Unit,
    onSubjectClick: () -> Unit
) {
    val subject = SubjectsCatalog.findSubject(slot.subjectCode)
    val accentColor = if (subject != null) Color(subject.color) else ElectricSapphire

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Pill
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = slot.startTime,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "to",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = slot.endTime,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onSubjectClick)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassBadge(
                        text = slot.subjectCode,
                        accentColor = accentColor
                    )
                    if (slot.isLab) {
                        Spacer(modifier = Modifier.width(6.dp))
                        GlassBadge(
                            text = if (slot.groupType == "GROUP_1") "Gr. I" else if (slot.groupType == "GROUP_2") "Gr. II" else "Lab",
                            accentColor = NeonCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = slot.subjectName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${slot.faculty} • ${slot.roomOrLab}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Class Slot",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun HomeTaskItem(
    task: TaskModel,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedCheckmark(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEdit)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!task.subjectCode.isNullOrBlank() || !task.dueTimeStr.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        if (!task.subjectCode.isNullOrBlank()) {
                            Text(
                                text = "[${task.subjectCode}]",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricSapphire,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (!task.dueTimeStr.isNullOrBlank()) {
                            Text(
                                text = "Due at ${task.dueTimeStr}",
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

private fun parseTimeToMins(timeStr: String): Int {
    val parts = timeStr.trim().split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour * 60 + min
}

fun getMealTitle(mealType: String): String {
    return when (mealType) {
        "BREAKFAST_MORNING" -> "Breakfast (Morning)"
        "BREAKFAST_EVENING" -> "Evening Snack & Tea"
        "LUNCH" -> "Lunch"
        "DINNER" -> "Dinner"
        else -> "Meal"
    }
}
