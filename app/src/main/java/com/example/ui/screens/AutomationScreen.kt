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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AutomationRoutine
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun AutomationScreen(
    routines: List<AutomationRoutine>,
    onExecuteRoutine: (AutomationRoutine) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Automation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COMMAND AUTOMATION PIPELINE",
                    style = Typography.titleLarge,
                    color = CyanCore
                )
                Text(
                    text = "Multi-vector macro routines coordinated by SARA",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Routines List
        routines.forEach { routine ->
            AutomationRoutineCard(
                routine = routine,
                onExecute = { onExecuteRoutine(routine) }
            )
        }
    }
}

@Composable
fun AutomationRoutineCard(
    routine: AutomationRoutine,
    onExecute: () -> Unit
) {
    val icon: ImageVector = when (routine.iconName) {
        "security" -> Icons.Default.Security
        "warning" -> Icons.Default.Warning
        else -> Icons.Default.AutoAwesome
    }

    val accentColor = when (routine.codeName) {
        "PROTOCOL_STEALTH" -> CyanAccent
        "PROTOCOL_COMBAT" -> AmberWarning
        else -> NeonIndigo
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        glowEffect = true,
        borderColor = accentColor.copy(alpha = 0.5f)
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = routine.title,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = routine.title,
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Trigger: \"${routine.triggerPhrase}\"",
                            style = Typography.labelSmall,
                            color = CyanCore
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onExecute,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = accentColor.copy(alpha = 0.2f),
                        contentColor = accentColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("execute_routine_${routine.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Trigger",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Trigger", style = Typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.description,
                style = Typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Sequence Tags
            Text(
                text = "DISPATCH SEQUENCE:",
                style = Typography.labelSmall,
                color = TextCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                routine.actions.forEachIndexed { idx, act ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF040A18))
                            .border(1.dp, HoloBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "0${idx + 1}.",
                            style = Typography.labelSmall,
                            color = CyanCore
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${act.type.name} -> [${act.target.name}] State: ${act.state}",
                            style = Typography.labelSmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
