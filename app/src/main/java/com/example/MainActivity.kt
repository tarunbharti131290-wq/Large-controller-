package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.HolographicBackground
import com.example.ui.components.TelemetryHudBar
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.ConsoleScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.MemoryVaultScreen
import com.example.ui.screens.PromptStudioScreen
import com.example.ui.screens.QuickControlsScreen
import com.example.ui.screens.SecurityAuditScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.LargeControllerTheme
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LargeControllerTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val promptTemplates by viewModel.promptTemplates.collectAsState()
    val activePromptText by viewModel.activePromptText.collectAsState()
    val automationRoutines by viewModel.automationRoutines.collectAsState()
    val isCliMode by viewModel.isCliMode.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val recentNotifications by viewModel.recentNotifications.collectAsState()
    val lastNotification by viewModel.lastNotification.collectAsState()

    // Handle Android system back press to return to Console
    BackHandler(enabled = currentTab != AppTab.CONSOLE) {
        viewModel.setTab(AppTab.CONSOLE)
    }

    HolographicBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                HolographicBottomNav(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setTab(it) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                // Top Header Branding & System Identification
                TopHeaderBar(
                    memoriesCount = memories.size,
                    onOpenVault = { viewModel.setTab(AppTab.MEMORY) },
                    onClearHistory = { viewModel.clearHistory() }
                )

                // Real-time Telemetry HUD Bar
                TelemetryHudBar(
                    telemetry = telemetry,
                    onRefreshClick = { viewModel.refreshTelemetry() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                // Execution / Memory Learning Notification Banner
                AnimatedVisibility(
                    visible = lastNotification != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (lastNotification != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanCore.copy(alpha = 0.2f))
                                .border(1.dp, CyanCore, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lastNotification ?: "",
                                    style = Typography.labelSmall,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = { viewModel.clearNotification() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = CyanCore,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Tab Content
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (currentTab) {
                        AppTab.CONSOLE -> {
                            ConsoleScreen(
                                messages = messages,
                                isProcessing = isProcessing,
                                isCliMode = isCliMode,
                                onToggleCliMode = { viewModel.toggleCliMode() },
                                onSendMessage = { viewModel.sendCommand(it) },
                                onExecuteAction = { viewModel.executeActionManually(it) }
                            )
                        }
                        AppTab.CONTROLS -> {
                            QuickControlsScreen(
                                telemetry = telemetry,
                                installedApps = installedApps,
                                notifications = recentNotifications,
                                onToggleFlashlight = { viewModel.toggleFlashlight() },
                                onSetVolume = { viewModel.setVolume(it) },
                                onTriggerHaptic = { viewModel.triggerHapticFeedback() },
                                onLaunchApp = { viewModel.launchAppByName(it) },
                                onExecuteAction = { viewModel.executeActionManually(it) },
                                onSummarizeNotifications = {
                                    viewModel.setTab(AppTab.CONSOLE)
                                    viewModel.sendCommand("Summarize notifications")
                                }
                            )
                        }
                        AppTab.SECURITY -> {
                            SecurityAuditScreen(
                                onRunAudit = { viewModel.runSecurityAudit(it) },
                                onExecuteCommand = { viewModel.sendCommand(it) }
                            )
                        }
                        AppTab.MEMORY -> {
                            MemoryVaultScreen(
                                memories = memories,
                                onTeachSara = { concept, content -> viewModel.teachSara(concept, content) },
                                onDeleteMemory = { viewModel.deleteMemory(it) },
                                onClearAll = { viewModel.clearAllMemories() },
                                onAskInConsole = {
                                    viewModel.setTab(AppTab.CONSOLE)
                                    viewModel.sendCommand(it)
                                }
                            )
                        }
                        AppTab.PROMPTS -> {
                            PromptStudioScreen(
                                templates = promptTemplates,
                                activePromptText = activePromptText,
                                onPromptChange = { viewModel.updateActivePrompt(it) },
                                onTestInConsole = {
                                    viewModel.setTab(AppTab.CONSOLE)
                                    viewModel.sendCommand("Synthesize Prompt: $it")
                                }
                            )
                        }
                        AppTab.AUTOMATION -> {
                            AutomationScreen(
                                routines = automationRoutines,
                                onExecuteRoutine = { viewModel.executeRoutine(it) }
                            )
                        }
                        AppTab.DIAGNOSTICS -> {
                            DiagnosticsScreen(
                                telemetry = telemetry,
                                onRefreshTelemetry = { viewModel.refreshTelemetry() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeaderBar(
    memoriesCount: Int,
    onOpenVault: () -> Unit,
    onClearHistory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "LARGE CONTROLLER",
                    style = Typography.headlineMedium,
                    color = CyanCore
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonIndigo.copy(alpha = 0.3f))
                        .border(1.dp, NeonIndigo.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "v4.2",
                        style = Typography.labelSmall,
                        color = Color.White
                    )
                }
            }
            Text(
                text = "SARA Self-Learning Companion // Large Technology",
                style = Typography.labelSmall,
                color = TextCyan
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Neural Vault Quick Access Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanCore.copy(alpha = 0.15f))
                    .border(1.dp, CyanCore.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenVault)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("header_vault_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Vault",
                        tint = CyanCore,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$memoriesCount Mems",
                        style = Typography.labelSmall,
                        color = CyanCore
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C192E))
                    .border(1.dp, HoloBorder, RoundedCornerShape(8.dp))
                    .clickable(onClick = onClearHistory)
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("reset_console_button")
            ) {
                Text(
                    text = "RESET",
                    style = Typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun HolographicBottomNav(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xF0040A18))
            .border(
                width = 1.dp,
                color = HoloBorder,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("holographic_bottom_nav")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Console",
                icon = Icons.Default.SmartToy,
                isSelected = currentTab == AppTab.CONSOLE,
                onClick = { onSelectTab(AppTab.CONSOLE) },
                testTag = "nav_tab_console"
            )
            NavTabItem(
                label = "Controls",
                icon = Icons.Default.Tune,
                isSelected = currentTab == AppTab.CONTROLS,
                onClick = { onSelectTab(AppTab.CONTROLS) },
                testTag = "nav_tab_controls"
            )
            NavTabItem(
                label = "Security",
                icon = Icons.Default.Shield,
                isSelected = currentTab == AppTab.SECURITY,
                onClick = { onSelectTab(AppTab.SECURITY) },
                testTag = "nav_tab_security"
            )
            NavTabItem(
                label = "Vault",
                icon = Icons.Default.Psychology,
                isSelected = currentTab == AppTab.MEMORY,
                onClick = { onSelectTab(AppTab.MEMORY) },
                testTag = "nav_tab_memory"
            )
            NavTabItem(
                label = "Prompts",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentTab == AppTab.PROMPTS,
                onClick = { onSelectTab(AppTab.PROMPTS) },
                testTag = "nav_tab_prompts"
            )
            NavTabItem(
                label = "Automate",
                icon = Icons.Default.Bolt,
                isSelected = currentTab == AppTab.AUTOMATION,
                onClick = { onSelectTab(AppTab.AUTOMATION) },
                testTag = "nav_tab_automate"
            )
            NavTabItem(
                label = "Diagnostics",
                icon = Icons.Default.Analytics,
                isSelected = currentTab == AppTab.DIAGNOSTICS,
                onClick = { onSelectTab(AppTab.DIAGNOSTICS) },
                testTag = "nav_tab_diagnostics"
            )
        }
    }
}

@Composable
fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = CyanCore
    val inactiveColor = TextMuted

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSelected) activeColor.copy(alpha = 0.2f) else Color.Transparent)
                .border(
                    width = if (isSelected) 1.dp else 0.dp,
                    color = if (isSelected) activeColor.copy(alpha = 0.6f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = Typography.labelSmall,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}
