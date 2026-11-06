package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.CommandAction
import com.example.model.ExecutionStatus
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.RoseCritical
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun ActionExecutionCard(
    action: CommandAction,
    onExecuteClick: (CommandAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showJson by remember { mutableStateOf(false) }

    val icon: ImageVector = when (action.type) {
        ActionType.LAUNCH_APP -> Icons.Default.RocketLaunch
        else -> when (action.target) {
            ActionTarget.WIFI -> Icons.Default.Wifi
            ActionTarget.BLUETOOTH -> Icons.Default.Bluetooth
            ActionTarget.VOLUME -> Icons.Default.VolumeUp
            ActionTarget.FLASHLIGHT -> Icons.Default.FlashlightOn
            ActionTarget.BATTERY -> Icons.Default.BatteryFull
            ActionTarget.CAMERA -> Icons.Default.CameraAlt
            ActionTarget.SETTINGS -> Icons.Default.Settings
            ActionTarget.NOTIFICATIONS -> Icons.Default.Notifications
            ActionTarget.AUTOMATION_MACRO -> Icons.Default.Autorenew
            ActionTarget.PROMPT_ENGINE -> Icons.Default.Tune
            ActionTarget.APP -> Icons.Default.Apps
            else -> Icons.Default.PlayArrow
        }
    }

    val (statusLabel, statusColor) = when (action.status) {
        ExecutionStatus.EXECUTED -> "EXECUTED" to EmeraldOnline
        ExecutionStatus.REDIRECTED_TO_SETTINGS -> "PANEL OPENED" to CyanAccent
        ExecutionStatus.READY -> "READY" to AmberWarning
        ExecutionStatus.FAILED -> "FAILED" to RoseCritical
    }

    val titleLabel = if (action.type == ActionType.LAUNCH_APP) {
        "LAUNCH_APP -> ${action.targetAppName.ifBlank { action.target.name }}"
    } else {
        "${action.type.name} -> ${action.target.name}"
    }

    val stateLabel = if (action.type == ActionType.LAUNCH_APP) {
        "App Module: ${action.targetAppName.ifBlank { action.target.name }}"
    } else {
        "State: ${action.state}"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        glowEffect = true,
        borderColor = CyanCore.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanCore.copy(alpha = 0.15f))
                            .border(1.dp, CyanCore.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = action.target.name,
                            tint = CyanCore,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = titleLabel,
                            style = Typography.labelLarge,
                            color = TextCyan
                        )
                        Text(
                            text = stateLabel,
                            style = Typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusLabel,
                            style = Typography.labelSmall,
                            color = statusColor
                        )
                    }
                }
            }

            if (action.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = action.details,
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Raw Action Command Block Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF040A18))
                    .border(1.dp, HoloBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = action.rawBlock,
                    style = Typography.labelSmall,
                    color = CyanCore,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View JSON payload toggle
                Row(
                    modifier = Modifier
                        .clickable { showJson = !showJson }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Show JSON",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showJson) "Hide JSON" else "Inspect Payload",
                        style = Typography.labelSmall,
                        color = CyanAccent
                    )
                }

                FilledTonalButton(
                    onClick = { onExecuteClick(action) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyanCore.copy(alpha = 0.2f),
                        contentColor = CyanCore
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("action_execute_btn")
                ) {
                    Icon(
                        imageVector = if (action.status == ExecutionStatus.EXECUTED) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                        contentDescription = "Execute",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (action.status == ExecutionStatus.EXECUTED) "Re-Execute" else "Dispatch",
                        style = Typography.labelSmall
                    )
                }
            }

            AnimatedVisibility(visible = showJson) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF020612))
                        .border(1.dp, NeonIndigo.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "// Structured JSON Directive Output",
                        style = Typography.labelSmall,
                        color = NeonIndigo
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = action.jsonString,
                        style = Typography.labelSmall,
                        color = Color(0xFFA5F3FC)
                    )
                }
            }
        }
    }
}
