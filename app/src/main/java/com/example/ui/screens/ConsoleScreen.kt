package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.CommandAction
import com.example.model.MessageSender
import com.example.ui.components.ActionExecutionCard
import com.example.ui.components.GlassCard
import com.example.ui.components.HolographicOrb
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

@Composable
fun ConsoleScreen(
    messages: List<ChatMessage>,
    isProcessing: Boolean,
    isCliMode: Boolean,
    onToggleCliMode: () -> Unit,
    onSendMessage: (String) -> Unit,
    onExecuteAction: (CommandAction) -> Unit,
    isGeminiConnected: Boolean = false,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickCommands = listOf(
        "Connect Gemini API Key",
        "Check Gemini Status",
        "Run Security Audit",
        "Start Background Daemon",
        "Scan Network Ports",
        "Turn on Wi-Fi",
        "Toggle Mobile Data",
        "Volume 80%",
        "Launch Camera",
        "Prepare for flight",
        "Summarize notifications",
        "What is a mango?",
        "Generate Kotlin Code"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // Holographic Orb Deck & Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCliMode) CyanCore.copy(alpha = 0.2f) else HoloSurfaceVariant)
                    .border(1.dp, if (isCliMode) CyanCore else HoloBorder, RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggleCliMode)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("toggle_cli_mode_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "CLI Mode",
                        tint = if (isCliMode) CyanCore else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCliMode) "CLI TERMINAL [ACTIVE]" else "SWITCH TO CLI",
                        style = Typography.labelSmall,
                        color = if (isCliMode) CyanCore else TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isGeminiConnected) EmeraldOnline.copy(alpha = 0.15f) else HoloSurfaceVariant)
                    .border(
                        1.dp,
                        if (isGeminiConnected) EmeraldOnline.copy(alpha = 0.6f) else HoloBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSendMessage("Check Gemini API key status") }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("gemini_status_pill")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isGeminiConnected) EmeraldOnline else CyanCore)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isGeminiConnected) "GEMINI 2.5 LIVE" else "LOCAL ONBOARD AI",
                        style = Typography.labelSmall,
                        color = if (isGeminiConnected) EmeraldOnline else TextCyan
                    )
                }
            }
        }

        if (!isCliMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                HolographicOrb(
                    isProcessing = isProcessing,
                    onClick = { onSendMessage("System telemetry scan") }
                )
            }
        }

        // Quick Command Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(HoloSurfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, CyanCore.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable { onSendMessage(cmd) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("quick_cmd_${cmd.replace(" ", "_")}")
                ) {
                    Text(
                        text = cmd,
                        style = Typography.labelSmall,
                        color = CyanAccent
                    )
                }
            }
        }

        // Messages Stream (Normal or CLI Terminal Mode)
        if (isCliMode) {
            // Monospace CLI Terminal Stream
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF020612))
                    .border(1.dp, CyanCore.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        CliMessageItem(message = msg)
                    }

                    if (isProcessing) {
                        item {
                            Text(
                                text = "sara@large-controller:~$ [COMPUTING DIRECTIVE...]",
                                style = Typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = CyanCore
                            )
                        }
                    }
                }
            }
        } else {
            // Glassmorphic Conversational Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        onExecuteAction = onExecuteAction
                    )
                }

                if (isProcessing) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = CyanCore,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SARA computing neural sequence...",
                                style = Typography.labelSmall,
                                color = CyanCore
                            )
                        }
                    }
                }
            }
        }

        // Futuristic Input Console
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xE6050B17))
                .border(
                    width = 1.dp,
                    color = HoloBorder,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isCliMode) "Enter command (e.g. wifi off, launch camera, vol 80)..." else "Direct SARA (e.g. Turn on Wi-Fi, Launch Camera...)",
                            style = Typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sara_command_input"),
                    textStyle = if (isCliMode) Typography.labelMedium.copy(color = CyanCore) else Typography.bodyMedium.copy(color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanCore,
                        unfocusedBorderColor = HoloBorder,
                        focusedContainerColor = Color(0xFF091426),
                        unfocusedContainerColor = Color(0xFF07101E),
                        cursorColor = CyanCore
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyanCore.copy(alpha = 0.2f))
                        .border(1.dp, CyanCore, CircleShape)
                        .clickable {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("sara_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Dispatch Command",
                        tint = CyanCore,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onExecuteAction: (CommandAction) -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val actions = message.getAllActions()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isUser) "OPERATOR // DIRECTIVE" else "SARA // CORE RESPONSE",
            style = Typography.labelSmall,
            color = if (isUser) NeonIndigo else CyanCore,
            modifier = Modifier.padding(bottom = 3.dp, start = if (isUser) 0.dp else 4.dp, end = if (isUser) 4.dp else 0.dp)
        )

        if (isUser) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0x336366F1))
                    .border(1.dp, NeonIndigo.copy(alpha = 0.6f), RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    style = Typography.bodyLarge,
                    color = TextPrimary
                )
            }
        } else {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowEffect = false,
                borderColor = CyanCore.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = message.text,
                        style = Typography.bodyLarge,
                        color = TextPrimary
                    )

                    // Render all action cards for single or multi-step workflows
                    if (actions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            actions.forEach { act ->
                                ActionExecutionCard(
                                    action = act,
                                    onExecuteClick = onExecuteAction
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
fun CliMessageItem(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isUser) "> operator@client:~$ ${message.text}" else "sara@large-controller:~$ ${message.text}",
            style = Typography.labelSmall.copy(fontFamily = FontFamily.Monospace, lineHeight = 16.sp),
            color = if (isUser) Color(0xFFA5F3FC) else CyanCore
        )
    }
}
