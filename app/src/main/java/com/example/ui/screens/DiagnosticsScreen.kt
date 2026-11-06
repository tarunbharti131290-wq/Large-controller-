package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.DeviceTelemetry
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurfaceVariant
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun DiagnosticsScreen(
    telemetry: DeviceTelemetry,
    onRefreshTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Diagnostics Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SYSTEM TELEMETRY DIAGNOSTICS",
                    style = Typography.titleLarge,
                    color = CyanCore
                )
                Text(
                    text = "Deep substrate inspection & hardware metrics",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }

            FilledTonalButton(
                onClick = onRefreshTelemetry,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = CyanCore.copy(alpha = 0.2f),
                    contentColor = CyanCore
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("refresh_diagnostics_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Scan", style = Typography.labelSmall)
            }
        }

        // RAM & Memory Load Gauge
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowEffect = true,
            borderColor = CyanCore.copy(alpha = 0.4f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Memory",
                            tint = CyanCore,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RAM Volatile Allocation",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "${telemetry.ramUsedPercent}%",
                        style = Typography.labelLarge,
                        color = CyanCore
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { telemetry.ramUsedPercent / 100f },
                    color = CyanCore,
                    trackColor = HoloSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Available: ${telemetry.availableRamMb} MB",
                        style = Typography.labelSmall,
                        color = EmeraldOnline
                    )
                    Text(
                        text = "Total System: ${telemetry.totalRamMb} MB",
                        style = Typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Power Grid Cells (Battery)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = "Power",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Power Grid Subsystem",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = if (telemetry.isCharging) "CHARGING" else "DISCHARGING",
                        style = Typography.labelSmall,
                        color = if (telemetry.isCharging) EmeraldOnline else CyanAccent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticMetricRow(label = "Charge Level", value = "${telemetry.batteryPercent}% Capacitance")
                DiagnosticMetricRow(label = "Battery Health", value = telemetry.batteryHealth)
                DiagnosticMetricRow(label = "Cell Potential", value = "${telemetry.batteryVoltageMv} mV")
            }
        }

        // Substrate & SARA Core Specifications
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Core",
                        tint = NeonIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Large Technology Core v4.2",
                        style = Typography.titleMedium,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticMetricRow(label = "Intelligence Core", value = "SARA (Frontier Engine)")
                DiagnosticMetricRow(label = "Platform OS", value = telemetry.androidVersion)
                DiagnosticMetricRow(label = "Instruction Set", value = telemetry.cpuArchitecture)
                DiagnosticMetricRow(label = "Neural Sandbox", value = telemetry.securityLevel)
                DiagnosticMetricRow(label = "Acoustic Output", value = "${telemetry.currentVolumePercent}% (${telemetry.currentVolumeStep}/${telemetry.maxVolumeSteps} steps)")
                DiagnosticMetricRow(label = "Optical Torch", value = if (telemetry.isFlashlightActive) "ENGAGED" else "STANDBY")
            }
        }
    }
}

@Composable
fun DiagnosticMetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = Typography.labelSmall,
            color = TextPrimary
        )
    }
}
