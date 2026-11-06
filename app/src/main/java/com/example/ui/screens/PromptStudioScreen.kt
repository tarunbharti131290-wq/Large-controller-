package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.PromptTemplate
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanCore
import com.example.ui.theme.EmeraldOnline
import com.example.ui.theme.HoloBorder
import com.example.ui.theme.HoloSurfaceVariant
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PromptStudioScreen(
    templates: List<PromptTemplate>,
    activePromptText: String,
    onPromptChange: (String) -> Unit,
    onTestInConsole: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedRole by remember { mutableStateOf("Autonomous Agent") }
    var selectedTone by remember { mutableStateOf("Futuristic & Concise") }
    var selectedEngine by remember { mutableStateOf("Gemini 3.8") }
    var isCopied by remember { mutableStateOf(false) }

    val categories = listOf("All", "Worldbuilding", "Software Engineering", "Visual Diffusion", "System Architecture")
    val roles = listOf("Autonomous Agent", "Cyber Operative", "Lead Architect", "Creative Chronicler")
    val tones = listOf("Futuristic & Concise", "Technical Deep-Dive", "Cyber Noir", "Brutal Minimalist")
    val engines = listOf("Gemini 3.8", "Claude 3.7", "Midjourney v6", "GPT-4o")

    val filteredTemplates = if (selectedCategory == "All") {
        templates
    } else {
        templates.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI PROMPT STUDIO",
                    style = Typography.titleLarge,
                    color = CyanCore
                )
                Text(
                    text = "High-precision prompt engineering & synthesis by SARA",
                    style = Typography.bodySmall,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = "Prompt Studio",
                tint = CyanCore,
                modifier = Modifier.size(28.dp)
            )
        }

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyanCore.copy(alpha = 0.2f) else HoloSurfaceVariant)
                        .border(1.dp, if (isSelected) CyanCore else HoloBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("prompt_cat_$cat")
                ) {
                    Text(
                        text = cat,
                        style = Typography.labelSmall,
                        color = if (isSelected) CyanCore else TextSecondary
                    )
                }
            }
        }

        // Active Prompt Synthesis Workbench
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowEffect = true,
            borderColor = CyanCore.copy(alpha = 0.4f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PROMPT SYNTHESIS WORKBENCH",
                    style = Typography.labelLarge,
                    color = TextCyan
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Parameter Selectors
                Text(
                    text = "Agent Persona Role:",
                    style = Typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    roles.forEach { role ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedRole == role) NeonIndigo.copy(alpha = 0.3f) else Color(0xFF07101E))
                                .border(1.dp, if (selectedRole == role) NeonIndigo else HoloBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedRole = role }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = role,
                                style = Typography.labelSmall,
                                color = if (selectedRole == role) Color.White else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Target Inference Engine:",
                    style = Typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    engines.forEach { eng ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedEngine == eng) CyanCore.copy(alpha = 0.2f) else Color(0xFF07101E))
                                .border(1.dp, if (selectedEngine == eng) CyanCore else HoloBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedEngine = eng }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = eng,
                                style = Typography.labelSmall,
                                color = if (selectedEngine == eng) CyanCore else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input / Prompt Area
                OutlinedTextField(
                    value = activePromptText,
                    onValueChange = onPromptChange,
                    placeholder = {
                        Text(
                            text = "Enter raw concept, creative idea, or system requirements...",
                            style = Typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("prompt_studio_input"),
                    textStyle = Typography.bodyMedium.copy(color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanCore,
                        unfocusedBorderColor = HoloBorder,
                        focusedContainerColor = Color(0xFF050B17),
                        unfocusedContainerColor = Color(0xFF050B17)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Compile / Synthesize Button
                    FilledTonalButton(
                        onClick = {
                            val synthesized = compileStructuredPrompt(
                                base = activePromptText.ifBlank { "Futuristic holographic AI system interface" },
                                role = selectedRole,
                                tone = selectedTone,
                                engine = selectedEngine
                            )
                            onPromptChange(synthesized)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CyanCore.copy(alpha = 0.2f),
                            contentColor = CyanCore
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("compile_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Compile",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Synthesize", style = Typography.labelSmall)
                    }

                    // Copy to Clipboard
                    FilledTonalButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("SARA Prompt", activePromptText)
                            cm?.setPrimaryClip(clip)
                            isCopied = true
                            coroutineScope.launch {
                                delay(2000)
                                isCopied = false
                            }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HoloSurfaceVariant,
                            contentColor = if (isCopied) EmeraldOnline else TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("copy_prompt_button")
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isCopied) "Copied" else "Copy", style = Typography.labelSmall)
                    }

                    // Send to SARA Console
                    FilledTonalButton(
                        onClick = {
                            onTestInConsole(activePromptText.ifBlank { "Test prompt synthesis" })
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = NeonIndigo.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("test_prompt_console_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test in SARA",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Inject", style = Typography.labelSmall)
                    }
                }
            }
        }

        // Templates Header
        Text(
            text = "PRE-ENGINEERED PROMPT TEMPLATES",
            style = Typography.labelLarge,
            color = CyanAccent
        )

        // Template Cards
        filteredTemplates.forEach { tpl ->
            PromptTemplateCard(
                template = tpl,
                onSelect = { onPromptChange(tpl.basePrompt) }
            )
        }
    }
}

@Composable
fun PromptTemplateCard(
    template: PromptTemplate,
    onSelect: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("template_card_${template.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.title,
                    style = Typography.titleSmall,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonIndigo.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = template.targetEngine,
                        style = Typography.labelSmall,
                        color = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = template.description,
                style = Typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                template.suggestedTags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = Typography.labelSmall,
                        color = CyanCore.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

private fun compileStructuredPrompt(
    base: String,
    role: String,
    tone: String,
    engine: String
): String {
    return """
# SARA SYNTHESIS DIRECTIVE [Engine: $engine]
[Role: $role | Persona: Large Technology v4.2 | Tone: $tone]

**Core Directive:**
$base

**Constraints & Execution Protocol:**
1. Zero generic pleasantries or introductory fluff; immediately output high-density results.
2. Maintain strict internal logic, verified domain terminology, and grounded technical vectors.
3. Structure output with designated section markers for seamless downstream integration.
    """.trimIndent()
}
