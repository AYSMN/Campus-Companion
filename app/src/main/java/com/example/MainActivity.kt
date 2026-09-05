package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ExportImportDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.SubjectDetailDialog
import com.example.ui.components.TimeOfDayBackground
import com.example.ui.screens.ClassesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MessScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsBottomSheet
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.CampusCompanionTheme
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.VioletPurple
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.LocalIsDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.CampusViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CampusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.userSettings.collectAsState()
            val isDark = when (settings.isDarkModeForced) {
                true -> true
                false -> false
                else -> isSystemInDarkTheme()
            }

            CampusCompanionTheme(darkTheme = isDark) {
                if (!settings.isOnboardingCompleted) {
                    OnboardingScreen(viewModel = viewModel)
                } else {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: CampusViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showSettings by viewModel.showSettingsSheet.collectAsState()
    val showExportImport by viewModel.showExportImportDialog.collectAsState()
    val selectedSubjectDetail by viewModel.selectedSubjectDetail.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        if (!snackbarMsg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMsg!!)
            viewModel.clearSnackbar()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isWideScreen = screenWidthDp >= 600 || (isLandscape && screenWidthDp >= 500)

    TimeOfDayBackground {
        if (isWideScreen) {
            // Adaptive Tablet / Landscape Mode with Side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                LiquidGlassNavRail(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    onOpenSettings = { viewModel.openSettings() }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val contentPadding = PaddingValues(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                        end = WindowInsets.navigationBars.asPaddingValues().calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
                    )

                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            val forward = targetState.ordinal > initialState.ordinal
                            (slideInHorizontally(
                                animationSpec = spring(stiffness = 500f),
                                initialOffsetX = { if (forward) it / 2 else -it / 2 }
                            ) + fadeIn()).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = spring(stiffness = 500f),
                                    targetOffsetX = { if (forward) -it / 2 else it / 2 }
                                ) + fadeOut()
                            )
                        },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            AppTab.HOME -> HomeScreen(viewModel = viewModel, contentPadding = contentPadding)
                            AppTab.MESS -> MessScreen(viewModel = viewModel, contentPadding = contentPadding)
                            AppTab.CLASSES -> ClassesScreen(viewModel = viewModel, contentPadding = contentPadding)
                            AppTab.TASKS -> TasksScreen(viewModel = viewModel, contentPadding = contentPadding)
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                    )
                }
            }
        } else {
            // Portrait Phone Mode with Floating Glass Bottom Bar
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.statusBars,
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(bottom = 90.dp)
                    )
                },
                bottomBar = {
                    LiquidGlassBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            ) { paddingValues ->
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        (slideInHorizontally(
                            animationSpec = spring(stiffness = 500f),
                            initialOffsetX = { if (forward) it / 2 else -it / 2 }
                        ) + fadeIn()).togetherWith(
                            slideOutHorizontally(
                                animationSpec = spring(stiffness = 500f),
                                targetOffsetX = { if (forward) -it / 2 else it / 2 }
                            ) + fadeOut()
                        )
                    },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        AppTab.HOME -> HomeScreen(viewModel = viewModel, contentPadding = paddingValues)
                        AppTab.MESS -> MessScreen(viewModel = viewModel, contentPadding = paddingValues)
                        AppTab.CLASSES -> ClassesScreen(viewModel = viewModel, contentPadding = paddingValues)
                        AppTab.TASKS -> TasksScreen(viewModel = viewModel, contentPadding = paddingValues)
                    }
                }
            }
        }

        // Global Sheets & Dialogs
        if (showSettings) {
            SettingsBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeSettings() }
            )
        }

        if (showExportImport) {
            ExportImportDialog(
                onDismiss = { viewModel.closeExportImportDialog() },
                onExportJson = { viewModel.getExportJson() },
                onImportJson = { json ->
                    viewModel.importJson(json) { success -> }
                }
            )
        }

        if (selectedSubjectDetail != null) {
            SubjectDetailDialog(
                subjectCode = selectedSubjectDetail!!,
                onDismiss = { viewModel.closeSubjectDetail() }
            )
        }
    }
}

@Composable
fun LiquidGlassNavRail(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onOpenSettings: () -> Unit
) {
    val isDark = LocalIsDark.current

    val surfaceColor = if (isDark) {
        Color(0xCC0F172A)
    } else {
        Color(0xEEFFFFFF)
    }

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.9f),
            if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.4f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.displayCutout))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.2f),
                    spotColor = if (isDark) Color.Black else ElectricSapphire.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(surfaceColor)
                .border(
                    width = 1.dp,
                    brush = borderBrush,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(vertical = 14.dp, horizontal = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Monogram Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricSapphire, VioletPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Campus Companion",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Nav Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NavRailItem(
                        tab = AppTab.HOME,
                        selected = currentTab == AppTab.HOME,
                        selectedIcon = Icons.Default.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        accentColor = ElectricSapphire,
                        onClick = { onTabSelected(AppTab.HOME) }
                    )
                    NavRailItem(
                        tab = AppTab.MESS,
                        selected = currentTab == AppTab.MESS,
                        selectedIcon = Icons.Default.Restaurant,
                        unselectedIcon = Icons.Outlined.Restaurant,
                        accentColor = Color(0xFFF59E0B),
                        onClick = { onTabSelected(AppTab.MESS) }
                    )
                    NavRailItem(
                        tab = AppTab.CLASSES,
                        selected = currentTab == AppTab.CLASSES,
                        selectedIcon = Icons.Default.School,
                        unselectedIcon = Icons.Outlined.School,
                        accentColor = NeonCyan,
                        onClick = { onTabSelected(AppTab.CLASSES) }
                    )
                    NavRailItem(
                        tab = AppTab.TASKS,
                        selected = currentTab == AppTab.TASKS,
                        selectedIcon = Icons.Default.CheckCircle,
                        unselectedIcon = Icons.Outlined.CheckCircle,
                        accentColor = VioletPurple,
                        onClick = { onTabSelected(AppTab.TASKS) }
                    )
                }

                // Settings at bottom
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NavRailItem(
    tab: AppTab,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isDark = LocalIsDark.current
    val bg = if (selected) {
        accentColor.copy(alpha = if (isDark) 0.28f else 0.18f)
    } else {
        Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = tab.title,
            tint = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tab.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) (if (isDark) Color.White else accentColor) else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
fun LiquidGlassBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val isDark = LocalIsDark.current

    val surfaceColor = if (isDark) {
        Color(0xCC0F172A)
    } else {
        Color(0xEEFFFFFF)
    }

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.9f),
            if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFCBD5E1).copy(alpha = 0.4f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = if (isDark) Color.Black else Color(0xFF64748B).copy(alpha = 0.2f),
                    spotColor = if (isDark) Color.Black else ElectricSapphire.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(surfaceColor)
                .border(
                    width = 1.dp,
                    brush = borderBrush,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    tab = AppTab.HOME,
                    selected = currentTab == AppTab.HOME,
                    selectedIcon = Icons.Default.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    accentColor = ElectricSapphire,
                    onClick = { onTabSelected(AppTab.HOME) }
                )

                BottomNavItem(
                    tab = AppTab.MESS,
                    selected = currentTab == AppTab.MESS,
                    selectedIcon = Icons.Default.Restaurant,
                    unselectedIcon = Icons.Outlined.Restaurant,
                    accentColor = Color(0xFFF59E0B),
                    onClick = { onTabSelected(AppTab.MESS) }
                )

                BottomNavItem(
                    tab = AppTab.CLASSES,
                    selected = currentTab == AppTab.CLASSES,
                    selectedIcon = Icons.Default.School,
                    unselectedIcon = Icons.Outlined.School,
                    accentColor = NeonCyan,
                    onClick = { onTabSelected(AppTab.CLASSES) }
                )

                BottomNavItem(
                    tab = AppTab.TASKS,
                    selected = currentTab == AppTab.TASKS,
                    selectedIcon = Icons.Default.CheckCircle,
                    unselectedIcon = Icons.Outlined.CheckCircle,
                    accentColor = VioletPurple,
                    onClick = { onTabSelected(AppTab.TASKS) }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    tab: AppTab,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val isDark = LocalIsDark.current

    val bg = if (selected) {
        accentColor.copy(alpha = if (isDark) 0.25f else 0.15f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = tab.title,
                tint = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            if (selected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else accentColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}
