package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.AutomationRoutine
import com.example.model.ChatMessage
import com.example.model.CommandAction
import com.example.model.DeviceNotification
import com.example.model.DeviceTelemetry
import com.example.model.ExecutionStatus
import com.example.model.InstalledApp
import com.example.model.MemoryItem
import com.example.model.MemoryType
import com.example.model.MessageSender
import com.example.model.PromptTemplate
import com.example.service.DeviceController
import com.example.service.GeminiService
import com.example.service.HapticFeedbackType
import com.example.service.SaraBrainEngine
import com.example.service.SaraMemoryVault
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab {
    CONSOLE,
    CONTROLS,
    SECURITY,
    MEMORY,
    PROMPTS,
    AUTOMATION,
    DIAGNOSTICS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceController = DeviceController(application)
    private val saraBrain = SaraBrainEngine()
    private val memoryVault = SaraMemoryVault(application)
    private val geminiService = GeminiService()

    private val _isGeminiConnected = MutableStateFlow(geminiService.isKeyConfigured)
    val isGeminiConnected: StateFlow<Boolean> = _isGeminiConnected.asStateFlow()

    private val _activeAiEngine = MutableStateFlow(
        if (geminiService.isKeyConfigured) "GEMINI 2.5 FLASH" else "SARA ONBOARD CORE"
    )
    val activeAiEngine: StateFlow<String> = _activeAiEngine.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.CONSOLE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isCliMode = MutableStateFlow(false)
    val isCliMode: StateFlow<Boolean> = _isCliMode.asStateFlow()

    val memories: StateFlow<List<MemoryItem>> = memoryVault.memoriesFlow

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _telemetry = MutableStateFlow(deviceController.getDeviceTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _recentNotifications = MutableStateFlow<List<DeviceNotification>>(emptyList())
    val recentNotifications: StateFlow<List<DeviceNotification>> = _recentNotifications.asStateFlow()

    private val _promptTemplates = MutableStateFlow<List<PromptTemplate>>(emptyList())
    val promptTemplates: StateFlow<List<PromptTemplate>> = _promptTemplates.asStateFlow()

    private val _activePromptText = MutableStateFlow("")
    val activePromptText: StateFlow<String> = _activePromptText.asStateFlow()

    private val _automationRoutines = MutableStateFlow<List<AutomationRoutine>>(emptyList())
    val automationRoutines: StateFlow<List<AutomationRoutine>> = _automationRoutines.asStateFlow()

    private val _lastNotification = MutableStateFlow<String?>(null)
    val lastNotification: StateFlow<String?> = _lastNotification.asStateFlow()

    init {
        loadInitialData()
        refreshTelemetry()
        loadInstalledApps()
        loadNotifications()
    }

    private fun loadInitialData() {
        val welcomeMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.SARA,
            text = """
SARA Core Intelligence [v4.2] initialized.
Continuous Learning Loop & Neural Memory Vault online.

I am your self-learning companion—ready to listen to your experiences, absorb new knowledge, control device hardware, or weave past context into our journey.
[Action: SETTING_TOGGLE | Target: SARA | State: ONLINE]
            """.trimIndent(),
            action = CommandAction(
                type = ActionType.SETTING_TOGGLE,
                target = ActionTarget.UNKNOWN,
                state = "ONLINE",
                rawBlock = "[Action: SETTING_TOGGLE | Target: SARA | State: ONLINE]",
                jsonString = "{\n  \"action\": \"SETTING_TOGGLE\",\n  \"target\": \"SARA\",\n  \"state\": \"ONLINE\"\n}",
                status = ExecutionStatus.EXECUTED,
                details = "SARA Companion Online & Adaptive"
            )
        )
        _messages.value = listOf(welcomeMessage)

        _promptTemplates.value = listOf(
            PromptTemplate(
                id = "p1",
                title = "Cinematic Sci-Fi Worldbuilder",
                category = "Worldbuilding",
                targetEngine = "Claude 3.7 / Gemini 3.8",
                description = "Generates high-tension cyber-thriller scene narratives with sensory anchors.",
                basePrompt = "Act as an elite neural decker in Neo-Tokyo navigating an unauthorized megacorp firewall intrusion. Use technical cybernetic jargon, atmospheric sensory details, and urgent pacing.",
                suggestedTags = listOf("Cinematic", "Cyberpunk", "Atmospheric", "Action")
            ),
            PromptTemplate(
                id = "p2",
                title = "High-Performance Kotlin Compose Architect",
                category = "Software Engineering",
                targetEngine = "Gemini 3.8 Flash / Coding LLMs",
                description = "Architects production Jetpack Compose components with M3 tokens.",
                basePrompt = "Write a complete Jetpack Compose module featuring custom Canvas animations, glowing glassmorphism borders, and accessibility-compliant touch targets with zero-recomposition StateFlow patterns.",
                suggestedTags = listOf("Android", "Kotlin", "Compose", "Architecture")
            ),
            PromptTemplate(
                id = "p3",
                title = "Midjourney Holographic AI Core Orb",
                category = "Visual Diffusion",
                targetEngine = "Midjourney v6 / Imagen 3",
                description = "High-fidelity prompt for futuristic UI renders and glowing holographic elements.",
                basePrompt = "Futuristic glowing holographic AI core orb with intricate cyan neon circuit rings, floating volumetric HUD telemetry displays, deep dark blue glassmorphism background, octanerender 8k --ar 16:9 --style raw",
                suggestedTags = listOf("Midjourney", "UI/UX", "3D Render", "Hologram")
            ),
            PromptTemplate(
                id = "p4",
                title = "System Prompt: Autonomous AI Companion",
                category = "System Architecture",
                targetEngine = "LLM System Instructions",
                description = "Defines a self-learning companion system prompt with dynamic memory.",
                basePrompt = "You are SARA, an autonomous command intelligence and self-learning companion by Large Technology. Continuously absorb user events, emotions, and teachings. Acknowledge knowledge gaps honestly.",
                suggestedTags = listOf("Companion", "Memory", "Learning", "System Prompt")
            )
        )

        _automationRoutines.value = listOf(
            AutomationRoutine(
                id = "r1",
                codeName = "PROTOCOL_STEALTH",
                title = "Ghost Stealth Protocol",
                description = "Mutes master audio, extinguishes illuminator, and opens wireless settings.",
                iconName = "security",
                triggerPhrase = "Ghost Protocol",
                actions = listOf(
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.VOLUME,
                        state = "0%",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: VOLUME | State: 0%]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"VOLUME\",\"state\":\"0%\"}"
                    ),
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.FLASHLIGHT,
                        state = "OFF",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: OFF]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"FLASHLIGHT\",\"state\":\"OFF\"}"
                    )
                )
            ),
            AutomationRoutine(
                id = "r2",
                codeName = "PROTOCOL_COMBAT",
                title = "High Alert / Beacon Protocol",
                description = "Engages flashlight, maximizes audio volume, and fires tactical haptic pulse.",
                iconName = "warning",
                triggerPhrase = "Combat Protocol",
                actions = listOf(
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.FLASHLIGHT,
                        state = "ON",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: ON]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"FLASHLIGHT\",\"state\":\"ON\"}"
                    ),
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.VOLUME,
                        state = "100%",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: VOLUME | State: 100%]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"VOLUME\",\"state\":\"100%\"}"
                    ),
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.HAPTIC,
                        state = "PULSE",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: HAPTIC | State: PULSE]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"HAPTIC\",\"state\":\"PULSE\"}"
                    )
                )
            ),
            AutomationRoutine(
                id = "r3",
                codeName = "PROTOCOL_WORKFLOW_FLIGHT",
                title = "Flight Readiness Workflow",
                description = "Sequentially deactivates Wi-Fi, Bluetooth, mutes volume, and launches navigation.",
                iconName = "flight",
                triggerPhrase = "Prepare for flight",
                actions = listOf(
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.WIFI,
                        state = "OFF",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: WIFI | State: OFF]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"WIFI\",\"state\":\"OFF\"}"
                    ),
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.BLUETOOTH,
                        state = "OFF",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: BLUETOOTH | State: OFF]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"BLUETOOTH\",\"state\":\"OFF\"}"
                    ),
                    CommandAction(
                        type = ActionType.SETTING_TOGGLE,
                        target = ActionTarget.VOLUME,
                        state = "0%",
                        rawBlock = "[Action: SETTING_TOGGLE | Target: VOLUME | State: 0%]",
                        jsonString = "{\"action\":\"SETTING_TOGGLE\",\"target\":\"VOLUME\",\"state\":\"0%\"}"
                    ),
                    CommandAction(
                        type = ActionType.LAUNCH_APP,
                        target = ActionTarget.APP,
                        targetAppName = "MAPS",
                        state = "LAUNCH",
                        rawBlock = "[Action: LAUNCH_APP | Target: MAPS]",
                        jsonString = "{\"action\":\"LAUNCH_APP\",\"target\":\"MAPS\"}"
                    )
                )
            )
        )
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
        deviceController.triggerTactileHaptic(HapticFeedbackType.COMMAND_CONFIRMED)
    }

    fun toggleCliMode() {
        _isCliMode.value = !_isCliMode.value
        deviceController.triggerTactileHaptic(HapticFeedbackType.COMMAND_CONFIRMED)
    }

    fun runSecurityAudit(target: String) {
        val prompt = when (target.uppercase()) {
            "PORTS" -> "Run network port scan and audit open sockets"
            "NETWORK_TRAFFIC" -> "Analyze network traffic packets and encrypted TLS streams"
            "VULNERABILITY_ASSESSMENT" -> "Run vulnerability assessment and CVE check"
            else -> "Run full cybersecurity system defense audit"
        }
        sendCommand(prompt)
    }

    fun sendCommand(userInput: String) {
        if (userInput.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.USER,
            text = userInput.trim()
        )
        _messages.value = _messages.value + userMsg
        _isProcessing.value = true

        viewModelScope.launch {
            val lower = userInput.trim().lowercase()
            val currentVol = _telemetry.value.currentVolumePercent
            val currentBat = _telemetry.value.batteryPercent
            val isKeyReady = geminiService.isKeyConfigured
            _isGeminiConnected.value = isKeyReady
            _activeAiEngine.value = if (isKeyReady) "GEMINI 2.5 FLASH" else "SARA ONBOARD CORE"

            // Direct Status & Connection Check for Gemini API Key
            if (lower.contains("connect gemini") || lower.contains("gemini api key") ||
                lower.contains("gemini status") || lower.contains("check gemini") ||
                lower == "gemini" || lower.contains("connect api key")
            ) {
                delay(300)
                val masked = if (isKeyReady) {
                    val key = geminiService.apiKey
                    if (key.length > 8) "${key.take(4)}...${key.takeLast(4)}" else "••••••••"
                } else "NOT DETECTED"

                val replyText = if (isKeyReady) {
                    """
### SARA NEURAL CORE: GEMINI API LINK ACTIVE
[Engine: ${geminiService.activeModelName} | Status: AUTHENTICATED]

• **Status:** Connected & Verified
• **Key Fingerprint:** `$masked`
• **Inference Pipeline:** Cloud Gemini Intelligence + Local Hardware Actuator
• **Tool Protocols:** System Settings, Background Daemons, Security Auditing, App Launching

SARA is running with full Gemini cognitive depth. You can ask complex questions, converse in Hinglish, generate code, or trigger device commands.
[Action: SETTING_TOGGLE | Target: SARA | State: GEMINI_CONNECTED]
                    """.trimIndent()
                } else {
                    """
### SARA NEURAL CORE: GEMINI API KEY SETUP
[Engine: SARA Onboard Core | Status: AWAITING_KEY]

`GEMINI_API_KEY` has been enabled in your `.env.example` file!
To connect your live Gemini API key:
1. Open the **Secrets panel** in the AI Studio sidebar.
2. Add secret name: `GEMINI_API_KEY` with your Google AI Studio API key.
3. The platform will automatically inject your key into `BuildConfig.GEMINI_API_KEY`.

In the meantime, SARA is fully operational with high-speed **Onboard Neural Rules & Device Actuators**.
[Action: SETTING_TOGGLE | Target: SARA | State: AWAITING_KEY]
                    """.trimIndent()
                }

                val actions = saraBrain.parseAllActions(replyText)
                val saraMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.SARA,
                    text = replyText,
                    action = actions.firstOrNull(),
                    actions = actions,
                    isActionExecuted = true
                )
                _messages.value = _messages.value + saraMsg
                _isProcessing.value = false
                return@launch
            }

            // If Gemini Key is configured, attempt Live Cloud Inference
            var responseText: String? = null
            var responseActions: List<CommandAction> = emptyList()
            var learnedMemory: MemoryItem? = null

            if (isKeyReady) {
                val pastMems = memoryVault.getAllMemories().map { "${it.concept}: ${it.content}" }
                val telemetryStr = "Battery: ${_telemetry.value.batteryPercent}%, Volume: ${_telemetry.value.currentVolumePercent}%, Network: ${_telemetry.value.networkType}, Flashlight: ${_telemetry.value.isFlashlightActive}"

                val geminiResult = geminiService.generateContent(
                    userPrompt = userInput,
                    contextMemories = pastMems,
                    telemetrySummary = telemetryStr
                )

                if (geminiResult.isSuccess) {
                    val rawText = geminiResult.getOrNull() ?: ""
                    responseText = rawText
                    responseActions = saraBrain.parseAllActions(rawText)
                }
            }

            // If Gemini wasn't ready or call failed, use robust Onboard Engine
            if (responseText == null) {
                val fallbackResponse = saraBrain.processCommand(userInput, currentVol, currentBat, memoryVault)
                responseText = fallbackResponse.displayText
                responseActions = fallbackResponse.actions
                learnedMemory = fallbackResponse.memoryItem
            }

            if (learnedMemory != null) {
                _lastNotification.value = "Absorbed Memory: ${learnedMemory.concept}"
            }

            val executedActions = mutableListOf<CommandAction>()
            for (act in responseActions) {
                val executed = deviceController.executeAction(act)
                executedActions.add(executed)
                if (responseActions.size > 1) {
                    delay(250) // multi-step sequential pacing
                }
            }
            refreshTelemetry()

            val saraMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.SARA,
                text = responseText,
                action = executedActions.firstOrNull(),
                actions = executedActions,
                isActionExecuted = executedActions.any { it.status == ExecutionStatus.EXECUTED }
            )

            _messages.value = _messages.value + saraMsg
            _isProcessing.value = false
        }
    }

    fun teachSara(concept: String, content: String) {
        val item = memoryVault.storeOrUpdateMemory(
            concept = concept,
            type = MemoryType.KNOWLEDGE_TEACHING,
            content = content,
            sentiment = "User Guided Insight"
        )
        _lastNotification.value = "Memory Assimilated: ${item.concept}"
        logSystemMessage(
            "I have absorbed your teaching on '${item.concept}' into my active memory vault.",
            "[Action: SETTING_TOGGLE | Target: SARA | State: MEMORY_UPDATE]"
        )
    }

    fun deleteMemory(id: String) {
        memoryVault.deleteMemory(id)
        _lastNotification.value = "Memory removed from vault."
    }

    fun clearAllMemories() {
        memoryVault.clearAllMemories()
        _lastNotification.value = "Neural memory vault cleared."
    }

    fun executeActionManually(action: CommandAction) {
        viewModelScope.launch {
            val updated = deviceController.executeAction(action)
            _lastNotification.value = "Executed: ${action.type.name} -> ${action.target.name}"
            refreshTelemetry()
            _messages.value = _messages.value.map { msg ->
                if (msg.action?.rawBlock == action.rawBlock || msg.actions.any { it.rawBlock == action.rawBlock }) {
                    msg.copy(
                        action = updated,
                        actions = msg.actions.map { if (it.rawBlock == action.rawBlock) updated else it },
                        isActionExecuted = updated.status == ExecutionStatus.EXECUTED
                    )
                } else msg
            }
        }
    }

    fun launchAppByName(appName: String) {
        viewModelScope.launch {
            deviceController.launchTargetApp(appName)
            _lastNotification.value = "Launched: $appName"
            logSystemMessage(
                "Launching application: $appName.",
                "[Action: LAUNCH_APP | Target: ${appName.uppercase().replace(" ", "_")}]"
            )
        }
    }

    fun toggleFlashlight() {
        val newState = !_telemetry.value.isFlashlightActive
        deviceController.toggleTorch(newState)
        refreshTelemetry()
        val block = "[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: ${if (newState) "ON" else "OFF"}]"
        logSystemMessage("Illuminator state toggled to ${if (newState) "ON" else "OFF"}.", block)
    }

    fun setVolume(percent: Int) {
        deviceController.setVolumePercent(percent)
        refreshTelemetry()
    }

    fun triggerHapticFeedback() {
        deviceController.triggerTactileHaptic(HapticFeedbackType.CRITICAL_PULSE)
        _lastNotification.value = "Tactile transducer pulse dispatched."
    }

    fun executeRoutine(routine: AutomationRoutine) {
        viewModelScope.launch {
            deviceController.triggerTactileHaptic(HapticFeedbackType.BURST_PULSE)
            val executedList = mutableListOf<CommandAction>()
            for (act in routine.actions) {
                val res = deviceController.executeAction(act)
                executedList.add(res)
                delay(250)
            }
            refreshTelemetry()
            val text = "Multi-step workflow '${routine.title}' completed across ${routine.actions.size} stages."
            val msg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.SARA,
                text = text,
                action = executedList.firstOrNull(),
                actions = executedList,
                isActionExecuted = true
            )
            _messages.value = _messages.value + msg
            _lastNotification.value = "Routine '${routine.title}' completed."
        }
    }

    fun selectPromptTemplate(template: PromptTemplate) {
        _activePromptText.value = template.basePrompt
        _currentTab.value = AppTab.PROMPTS
    }

    fun updateActivePrompt(newText: String) {
        _activePromptText.value = newText
    }

    fun refreshTelemetry() {
        _telemetry.value = deviceController.getDeviceTelemetry()
    }

    fun loadInstalledApps() {
        _installedApps.value = deviceController.getInstalledApps()
    }

    fun loadNotifications() {
        _recentNotifications.value = deviceController.getRecentNotifications()
    }

    fun clearHistory() {
        _messages.value = emptyList()
        loadInitialData()
    }

    fun clearNotification() {
        _lastNotification.value = null
    }

    private fun logSystemMessage(message: String, actionBlock: String) {
        val actions = saraBrain.parseAllActions(actionBlock)
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.SARA,
            text = "$message\n$actionBlock",
            action = actions.firstOrNull(),
            actions = actions,
            isActionExecuted = true
        )
        _messages.value = _messages.value + msg
    }
}
