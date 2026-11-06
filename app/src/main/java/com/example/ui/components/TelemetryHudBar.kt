package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.DeviceTelemetry
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurface
import com.example.ui.theme.RoseCritical
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.Typography

@Composable
fun TelemetryHudBar(
    telemetry: DeviceTelemetry,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HoloSurface.copy(alpha = 0.85f))
            .border(1.dp, HoloBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onRefreshClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("telemetry_hud_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SARA Core Online Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldOnline)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SARA ONLINE",
                    style = Typography.labelSmall,
                    color = EmeraldOnline
                )
            }

            // Battery telemetry
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                    contentDescription = "Battery",
                    tint = if (telemetry.batteryPercent < 20) RoseCritical else CyanCore,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${telemetry.batteryPercent}%",
                    style = Typography.labelSmall,
                    color = TextPrimary
                )
            }

            // Audio Vol telemetry
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = CyanAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${telemetry.currentVolumePercent}%",
                    style = Typography.labelSmall,
                    color = TextPrimary
                )
            }

            // Flashlight indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FlashlightOn,
                    contentDescription = "Flashlight",
                    tint = if (telemetry.isFlashlightActive) CyanCore else TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (telemetry.isFlashlightActive) "ON" else "OFF",
                    style = Typography.labelSmall,
                    color = if (telemetry.isFlashlightActive) CyanCore else TextMuted
                )
            }

            // RAM load
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "RAM",
                    tint = CyanCore,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${telemetry.ramUsedPercent}%",
                    style = Typography.labelSmall,
                    color = TextPrimary
                )
            }
        }
    }
}
