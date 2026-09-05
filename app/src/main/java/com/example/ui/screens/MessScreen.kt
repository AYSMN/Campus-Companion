package com.example.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.local.MessMealEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.CampusViewModel
import java.util.Calendar

data class DayTabItem(val dayInt: Int, val name: String, val shortName: String)

val ALL_DAYS_MESS = listOf(
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
fun MessScreen(
    viewModel: CampusViewModel,
    contentPadding: PaddingValues
) {
    val allMeals by viewModel.allMessMeals.collectAsState()
    val selectedDay by viewModel.selectedMessDay.collectAsState()
    val editingMeal by viewModel.editingMeal.collectAsState()

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val currentDayOfWeek = remember { CampusViewModel.getCurrentDayOfWeek() }
    val dayMeals = remember(allMeals, selectedDay) {
        allMeals
            .filter { it.dayOfWeek == selectedDay }
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

    val selectedDayObj = ALL_DAYS_MESS.firstOrNull { it.dayInt == selectedDay } ?: ALL_DAYS_MESS.first()

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
        // 1. Screen Title & Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Mess Menu",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Hostel Dining Timetable • Bihta Campus",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showResetConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset to Default",
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
                items(ALL_DAYS_MESS) { dayItem ->
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
                            .clickable { viewModel.selectMessDay(dayItem.dayInt) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        }

        // 3. Day Summary Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedDayObj.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (selectedDay == currentDayOfWeek) {
                                Spacer(modifier = Modifier.width(8.dp))
                                GlassBadge(text = "TODAY", accentColor = EmeraldMint)
                            }
                        }
                        Text(
                            text = "4 scheduled meal windows",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AmberGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. Meal Cards
        if (dayMeals.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No meal details saved for ${selectedDayObj.name}.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            if (isWideScreen) {
                val pairs = dayMeals.chunked(2)
                items(pairs) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        pair.forEach { meal ->
                            Box(modifier = Modifier.weight(1f)) {
                                MealDetailGlassCard(
                                    meal = meal,
                                    onEdit = { viewModel.openEditMeal(meal) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                items(dayMeals, key = { it.id }) { meal ->
                    MealDetailGlassCard(
                        meal = meal,
                        onEdit = { viewModel.openEditMeal(meal) }
                    )
                }
            }
        }

        // Notice Note
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tap the edit pencil on any meal to modify items. Edits are saved locally.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Edit Meal Bottom Sheet
    if (editingMeal != null) {
        EditMealBottomSheet(
            meal = editingMeal!!,
            onDismiss = { viewModel.closeEditMeal() },
            onSave = { updated -> viewModel.saveMealEdit(updated) }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Mess Menu?") },
            text = { Text("This will restore the entire weekly mess menu to the default official timetable.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetMessMenu()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetRose)
                ) {
                    Text("Reset to Default")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MealDetailGlassCard(
    meal: MessMealEntity,
    onEdit: () -> Unit
) {
    val (icon, accentColor, title) = when (meal.mealType) {
        "BREAKFAST_MORNING" -> Triple(Icons.Default.WbSunny, AmberGold, "Breakfast (Morning)")
        "BREAKFAST_EVENING" -> Triple(Icons.Default.Coffee, SunsetRose, "Evening Snack & Tea")
        "LUNCH" -> Triple(Icons.Default.Restaurant, EmeraldMint, "Lunch")
        "DINNER" -> Triple(Icons.Default.Nightlight, VioletPurple, "Dinner")
        else -> Triple(Icons.Default.LocalDining, ElectricSapphire, "Meal")
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        elevation = 6.dp
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = meal.timeSlot,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (meal.isCustomized) {
                        GlassBadge(text = "Edited", accentColor = AmberGold)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val dishes = meal.menuDescription.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            FlowDishRow(dishes = dishes, accentColor = accentColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealBottomSheet(
    meal: MessMealEntity,
    onDismiss: () -> Unit,
    onSave: (MessMealEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var menuText by remember { mutableStateOf(meal.menuDescription) }
    var timeSlotText by remember { mutableStateOf(meal.timeSlot) }

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
                text = "Edit ${getMealTitle(meal.mealType)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Update the dishes and timing served for this meal.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = timeSlotText,
                onValueChange = { timeSlotText = it },
                label = { Text("Meal Time Slot") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = menuText,
                onValueChange = { menuText = it },
                label = { Text("Menu Items (comma-separated)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

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
                            meal.copy(
                                menuDescription = menuText.trim(),
                                timeSlot = timeSlotText.trim()
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricSapphire)
                ) {
                    Text("Save Changes")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
