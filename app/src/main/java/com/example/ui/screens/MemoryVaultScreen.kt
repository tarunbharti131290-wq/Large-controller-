package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.MemoryItem
import com.example.model.MemoryType
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
fun MemoryVaultScreen(
    memories: List<MemoryItem>,
    onTeachSara: (concept: String, content: String) -> Unit,
    onDeleteMemory: (String) -> Unit,
    onClearAll: () -> Unit,
    onAskInConsole: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newConcept by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var showTeachDialog by remember { mutableStateOf(false) }

    val quickTeachingSeeds = listOf(
        "Mango is the king of fruits",
        "I fell down and hurt my knee",
        "My favorite drink is iced matcha latte",
        "I am working on quantum AI algorithms"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vault Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SARA NEURAL MEMORY VAULT",
                    style = Typography.titleLarge,
                    color = CyanCore
                )
                Text(
                    text = "Continuous Learning Loop & Dynamic Episodic Absorption",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonIndigo.copy(alpha = 0.25f))
                    .border(1.dp, NeonIndigo, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${memories.size} MEMORIES",
                    style = Typography.labelSmall,
                    color = Color.White
                )
            }
        }

        // Protocol Info Banner (The Mango Rule)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowEffect = true,
            borderColor = CyanCore.copy(alpha = 0.4f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Protocol",
                        tint = CyanCore,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "The Learning Loop // Continuous Absorption",
                        style = Typography.titleMedium,
                        color = TextCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "SARA actively listens to every experience, feeling, and correction you share. Knowledge gaps are acknowledged openly—when you guide SARA (like teaching that 'mango is the king of fruits'), she captures it permanently and weaves it into future conversations.",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Quick Teaching & Experience Chips
        Text(
            text = "QUICK TEACHING & EXPERIENCE PROBES",
            style = Typography.labelSmall,
            color = CyanAccent
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickTeachingSeeds.forEach { probe ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HoloSurfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, CyanCore.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { onAskInConsole(probe) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("probe_${probe.take(10)}")
                ) {
                    Text(
                        text = probe,
                        style = Typography.labelSmall,
                        color = TextPrimary
                    )
                }
            }
        }

        // Direct Teaching Input Workbench
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Teach",
                            tint = EmeraldOnline,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Teach SARA Directly",
                            style = Typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            if (newConcept.isNotBlank() && newContent.isNotBlank()) {
                                onTeachSara(newConcept, newContent)
                                newConcept = ""
                                newContent = ""
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EmeraldOnline.copy(alpha = 0.2f),
                            contentColor = EmeraldOnline
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_memory_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Absorb",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Absorb", style = Typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newConcept,
                    onValueChange = { newConcept = it },
                    placeholder = {
                        Text(
                            text = "Concept / Topic (e.g. Mango, My Dog, Coffee Preference...)",
                            style = Typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teach_concept_input"),
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    placeholder = {
                        Text(
                            text = "Nuance, meaning, or personal context (e.g. 'is the king of fruits')...",
                            style = Typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teach_content_input"),
                    textStyle = Typography.bodySmall.copy(color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanCore,
                        unfocusedBorderColor = HoloBorder,
                        focusedContainerColor = Color(0xFF040A18),
                        unfocusedContainerColor = Color(0xFF040A18)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        }

        // Active Memories Stream
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NEURAL MEMORY ARCHIVES",
                style = Typography.labelLarge,
                color = CyanAccent
            )

            if (memories.isNotEmpty()) {
                Text(
                    text = "Clear Vault",
                    style = Typography.labelSmall,
                    color = RoseCritical,
                    modifier = Modifier
                        .clickable(onClick = onClearAll)
                        .padding(4.dp)
                )
            }
        }

        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Neural memory is clear. Talk or teach SARA anything to begin continuous learning.",
                    style = Typography.bodyMedium,
                    color = TextMuted
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                memories.forEach { item ->
                    MemoryItemCard(
                        item = item,
                        onDelete = { onDeleteMemory(item.id) },
                        onRecall = { onAskInConsole("Tell me what you remember about ${item.concept}") }
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryItemCard(
    item: MemoryItem,
    onDelete: () -> Unit,
    onRecall: () -> Unit
) {
    val (icon: ImageVector, color: Color) = when (item.memoryType) {
        MemoryType.KNOWLEDGE_TEACHING -> Icons.Default.School to CyanCore
        MemoryType.PERSONAL_EVENT -> Icons.Default.Favorite to RoseCritical
        MemoryType.USER_PREFERENCE -> Icons.Default.Lightbulb to CyanAccent
        MemoryType.EMOTIONAL_CONTEXT -> Icons.Default.Psychology to NeonIndigo
        MemoryType.VOCABULARY_STYLE -> Icons.Default.AutoAwesome to EmeraldOnline
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        glowEffect = false,
        borderColor = color.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f))
                            .border(1.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.concept,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.concept,
                            style = Typography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "${item.memoryType.name} // ${item.emotionalSentiment}",
                            style = Typography.labelSmall,
                            color = color
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF071224))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ref: ${item.timesReferenced}x",
                            style = Typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Forget",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.content,
                style = Typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyanCore.copy(alpha = 0.15f))
                        .border(1.dp, CyanCore.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable(onClick = onRecall)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Recall in SARA Console",
                        style = Typography.labelSmall,
                        color = CyanCore
                    )
                }
            }
        }
    }
}
