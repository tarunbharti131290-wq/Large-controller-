package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.CommandAction
import com.example.model.DeviceNotification
import com.example.model.DeviceTelemetry
import com.example.model.InstalledApp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurfaceVariant
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.RoseCritical
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun QuickControlsScreen(
    telemetry: DeviceTelemetry,
    installedApps: List<InstalledApp>,
    notifications: List<DeviceNotification>,
    onToggleFlashlight: () -> Unit,
    onSetVolume: (Int) -> Unit,
    onTriggerHaptic: () -> Unit,
    onLaunchApp: (String) -> Unit,
    onExecuteAction: (CommandAction) -> Unit,
    onSummarizeNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    var volumeSliderValue by remember(telemetry.currentVolumePercent) {
        mutableFloatStateOf(telemetry.currentVolumePercent.toFloat())
    }
    var appSearchQuery by remember { mutableStateOf("") }

    val filteredApps = if (appSearchQuery.isBlank()) {
        installedApps.take(12)
    } else {
        installedApps.filter { it.name.contains(appSearchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEVICE CONTROL MATRIX",
                    style = Typography.titleLarge,
                    color = CyanCore
                )
                Text(
                    text = "Tactile actuators & installed application launcher",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // 1. Primary Flashlight & Optical Illuminator Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowEffect = telemetry.isFlashlightActive,
            borderColor = if (telemetry.isFlashlightActive) CyanCore else HoloBorder
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (telemetry.isFlashlightActive) CyanCore.copy(alpha = 0.25f) else HoloSurfaceVariant)
                            .border(1.dp, if (telemetry.isFlashlightActive) CyanCore else HoloBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (telemetry.isFlashlightActive) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = if (telemetry.isFlashlightActive) CyanCore else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Optical Flashlight (Torch)",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = if (telemetry.isFlashlightActive) "Illuminator Active // Beaming" else "Standby Mode // Dark",
                            style = Typography.bodySmall,
                            color = if (telemetry.isFlashlightActive) CyanCore else TextSecondary
                        )
                    }
                }

                Switch(
                    checked = telemetry.isFlashlightActive,
                    onCheckedChange = { onToggleFlashlight() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanCore,
                        checkedTrackColor = CyanCore.copy(alpha = 0.35f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = HoloSurfaceVariant
                    ),
                    modifier = Modifier.testTag("flashlight_switch")
                )
            }
        }

        // 2. Master Acoustic Volume Controller Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (volumeSliderValue == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Master Acoustic Volume",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "${volumeSliderValue.toInt()}%",
                        style = Typography.labelLarge,
                        color = CyanCore
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = volumeSliderValue,
                    onValueChange = { volumeSliderValue = it },
                    onValueChangeFinished = {
                        onSetVolume(volumeSliderValue.toInt())
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanCore,
                        activeTrackColor = CyanCore,
                        inactiveTrackColor = HoloSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("volume_slider")
                )

                // Volume Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(0 to "Mute", 30 to "30%", 60 to "60%", 80 to "80%", 100 to "Max").forEach { (vol, label) ->
                        FilledTonalButton(
                            onClick = {
                                volumeSliderValue = vol.toFloat()
                                onSetVolume(vol)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (volumeSliderValue.toInt() == vol) CyanCore.copy(alpha = 0.25f) else HoloSurfaceVariant,
                                contentColor = if (volumeSliderValue.toInt() == vol) CyanCore else TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("vol_preset_$label")
                        ) {
                            Text(text = label, style = Typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 3. Quick System Actuators Grid (Wi-Fi, Bluetooth, Mobile Data, Display, Battery)
        Text(
            text = "SYSTEM SETTINGS ACTUATORS",
            style = Typography.labelLarge,
            color = CyanAccent,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SystemActuatorTile(
                title = "Wi-Fi Matrix",
                subtitle = "Manage Uplinks",
                icon = Icons.Default.Wifi,
                accentColor = CyanCore,
                modifier = Modifier.weight(1f),
                onClick = {
                    onExecuteAction(
                        CommandAction(
                            type = ActionType.SETTING_TOGGLE,
                            target = ActionTarget.WIFI,
                            state = "SETTINGS",
                            rawBlock = "[Action: SETTING_TOGGLE | Target: WIFI | State: SETTINGS]",
                            jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"WIFI\",\"state\":\"SETTINGS\"}"
                        )
                    )
                }
            )

            SystemActuatorTile(
                title = "Bluetooth",
                subtitle = "Peripherals",
                icon = Icons.Default.Bluetooth,
                accentColor = NeonIndigo,
                modifier = Modifier.weight(1f),
                onClick = {
                    onExecuteAction(
                        CommandAction(
                            type = ActionType.SETTING_TOGGLE,
                            target = ActionTarget.BLUETOOTH,
                            state = "SETTINGS",
                            rawBlock = "[Action: SETTING_TOGGLE | Target: BLUETOOTH | State: SETTINGS]",
                            jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"BLUETOOTH\",\"state\":\"SETTINGS\"}"
                        )
                    )
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SystemActuatorTile(
                title = "Cellular Data",
                subtitle = "Mobile Carrier",
                icon = Icons.Default.NetworkCell,
                accentColor = CyanAccent,
                modifier = Modifier.weight(1f),
                onClick = {
                    onExecuteAction(
                        CommandAction(
                            type = ActionType.SETTING_TOGGLE,
                            target = ActionTarget.WIFI,
                            state = "MOBILE_DATA",
                            rawBlock = "[Action: SETTING_TOGGLE | Target: WIFI | State: MOBILE_DATA]",
                            jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"MOBILE_DATA\",\"state\":\"SETTINGS\"}"
                        )
                    )
                }
            )

            SystemActuatorTile(
                title = "Tactile Haptic",
                subtitle = "Transducer Test",
                icon = Icons.Default.Vibration,
                accentColor = EmeraldOnline,
                modifier = Modifier.weight(1f),
                onClick = onTriggerHaptic
            )
        }

        // 4. Installed Applications Matrix (Search & Launch)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "Apps",
                            tint = CyanCore,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Installed Applications Matrix",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "${installedApps.size} APPS",
                        style = Typography.labelSmall,
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = appSearchQuery,
                    onValueChange = { appSearchQuery = it },
                    placeholder = {
                        Text(
                            text = "Filter installed apps (e.g. Camera, Chrome, Maps...)",
                            style = Typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_search_input"),
                    textStyle = Typography.bodySmall.copy(color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanCore,
                        unfocusedBorderColor = HoloBorder,
                        focusedContainerColor = Color(0xFF040A18),
                        unfocusedContainerColor = Color(0xFF040A18)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // App chips grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredApps.forEach { app ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF071224))
                                .border(1.dp, CyanCore.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .clickable { onLaunchApp(app.name) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("launch_app_${app.name.replace(" ", "_")}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Launch",
                                    tint = CyanCore,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = app.name,
                                    style = Typography.labelSmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Notification Stream HUD & Summarizer
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notification Telemetry Stream",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    FilledTonalButton(
                        onClick = onSummarizeNotifications,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CyanCore.copy(alpha = 0.2f),
                            contentColor = CyanCore
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("summarize_notifications_btn")
                    ) {
                        Text(text = "Summarize", style = Typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    notifications.forEach { notif ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF030814))
                                .border(1.dp, HoloBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (notif.category == "SECURITY") RoseCritical else CyanCore)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${notif.appName}] ${notif.title}",
                                    style = Typography.labelSmall,
                                    color = TextCyan
                                )
                                Text(
                                    text = notif.message,
                                    style = Typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SystemActuatorTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF091427))
            .border(1.dp, HoloBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("actuator_tile_${title.replace(" ", "_")}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = Typography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = Typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
