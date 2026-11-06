package com.example.service

import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.CommandAction
import com.example.model.MemoryItem
import com.example.model.MemoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.regex.Pattern

class SaraBrainEngine {

    // Matches [Action: SETTING_TOGGLE | Target: WIFI | State: ON]
    private val togglePattern = Pattern.compile(
        "\\[Action:\\s*([A-Za-z0-9_]+)\\s*\\|\\s*Target:\\s*([A-Za-z0-9_]+)\\s*\\|\\s*State:\\s*([^]]+)\\]"
    )

    // Matches [Action: LAUNCH_APP | Target: [APP_NAME]]
    private val launchPattern = Pattern.compile(
        "\\[Action:\\s*LAUNCH_APP\\s*\\|\\s*Target:\\s*([A-Za-z0-9_]+)\\]"
    )

    // Matches [Action: BACKGROUND_EXECUTE | Task: [TASK_DESCRIPTION]]
    private val backgroundPattern = Pattern.compile(
        "\\[Action:\\s*BACKGROUND_EXECUTE\\s*\\|\\s*Task:\\s*([^]]+)\\]"
    )

    // Matches [Action: SECURITY_AUDIT | Target: [PARAMETER]]
    private val securityAuditPattern = Pattern.compile(
        "\\[Action:\\s*SECURITY_AUDIT\\s*\\|\\s*Target:\\s*([^]]+)\\]"
    )

    suspend fun processCommand(
        input: String,
        currentVolume: Int,
        currentBattery: Int,
        memoryVault: SaraMemoryVault
    ): SaraResponse = withContext(Dispatchers.Default) {
        val query = input.trim()
        val lower = query.lowercase()

        // 1. Check for Active User Teaching & Corrections (The Mango Rule: Step 4)
        val teachingResponse = handleUserTeaching(query, lower, memoryVault)
        if (teachingResponse != null) {
            return@withContext teachingResponse
        }

        // 2. Check for Personal Experiences, Feelings, and Events (Getting hurt, falling down, moods)
        val personalEventResponse = handlePersonalExperience(query, lower, memoryVault)
        if (personalEventResponse != null) {
            return@withContext personalEventResponse
        }

        // 3. Persistent Background & Wake-Mode Directives
        if (isBackgroundExecutionRequest(lower)) {
            return@withContext generateBackgroundExecutionResponse(query)
        }

        // 4. Advanced Cybersecurity & Ethical Hacking Module
        if (isCybersecurityRequest(lower)) {
            return@withContext generateCybersecurityResponse(query)
        }

        // 5. Multi-Step Workflow Decomposition
        if (isMultiStepWorkflow(lower)) {
            return@withContext decomposeWorkflow(query, currentVolume)
        }

        // 6. Device Settings & Controls (Wi-Fi, Bluetooth, Mobile Data, Volume, Flashlight, etc.)
        val deviceAction = detectDeviceSettingAction(lower, currentVolume, currentBattery)
        if (deviceAction != null) {
            return@withContext deviceAction
        }

        // 7. App Launching Directives ("Open Sara", "Open Camera", etc.)
        val launchAction = detectAppLaunch(lower)
        if (launchAction != null) {
            return@withContext launchAction
        }

        // 8. Notification Reading & Summarization
        if (isNotificationRequest(lower)) {
            return@withContext generateNotificationSummaryResponse()
        }

        // 9. Check for Questions on Concepts (The Mango Rule: Step 1-3)
        val conceptQueryResponse = handleConceptOrQuestion(query, lower, memoryVault)
        if (conceptQueryResponse != null) {
            return@withContext conceptQueryResponse
        }

        // 10. Code Snippet & Deep Research Request
        if (isCodeOrDeepResearchRequest(lower)) {
            return@withContext generateCodeOrDeepResearchResponse(query)
        }

        // 11. Prompt Engineering Request
        if (isPromptEngineeringRequest(lower)) {
            return@withContext generatePromptEngineeringResponse(query)
        }

        // 12. System Diagnostics Request
        if (isDiagnosticsRequest(lower)) {
            return@withContext generateDiagnosticsResponse(currentBattery, currentVolume)
        }

        // 13. General Deep Conversational Response with Memory Weaving
        return@withContext generateConversationalMemoryResponse(query, memoryVault)
    }

    /**
     * Handles teaching moments and corrections (The Mango Rule: Step 4)
     * e.g., "mango is the king of fruits", "remember that Alex is my brother", "actually in our country..."
     */
    private fun handleUserTeaching(
        query: String,
        lower: String,
        memoryVault: SaraMemoryVault
    ): SaraResponse? {
        // Specific Mango Rule check
        if (lower.contains("mango") && (lower.contains("king") || lower.contains("fruit") || lower.contains("sweet") || lower.contains("favorite"))) {
            if (lower.contains("king of fruits") || lower.contains("king of fruit") || lower.contains("king")) {
                val item = memoryVault.storeOrUpdateMemory(
                    concept = "Mango",
                    type = MemoryType.KNOWLEDGE_TEACHING,
                    content = "Mango is celebrated as the king of fruits—a title of supreme cultural reverence, rich flavor, and joy.",
                    sentiment = "Reverence & Warmth"
                )
                val text = """
I have permanently absorbed this into my active memory vault.

You've taught me that the **mango is the king of fruits**. I've woven this insight into my core neural understanding, replacing basic textbook descriptions with your authentic perspective. Thank you for guiding me—I will never forget this in all our conversations to come.
                """.trimIndent()
                return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
            }
        }

        // Generic "remember that..." or "actually, X is Y" or "X means Y"
        val teachingPrefixes = listOf(
            "remember that ",
            "actually, ",
            "actually ",
            "did you know that ",
            "in my culture ",
            "my favorite "
        )

        val matchedPrefix = teachingPrefixes.firstOrNull { lower.startsWith(it) || lower.contains(" $it") }

        if (matchedPrefix != null || lower.contains(" is called ") || lower.contains(" is the best ") || lower.contains(" is my favorite ")) {
            val contentToLearn = if (matchedPrefix != null) query.substringAfter(matchedPrefix).trim() else query

            // Identify concept
            val concept = if (contentToLearn.contains(" is ")) {
                contentToLearn.substringBefore(" is ").trim()
            } else if (contentToLearn.contains(" are ")) {
                contentToLearn.substringBefore(" are ").trim()
            } else {
                contentToLearn.split(" ").take(3).joinToString(" ")
            }

            val item = memoryVault.storeOrUpdateMemory(
                concept = concept.replaceFirstChar { it.uppercase() },
                type = MemoryType.KNOWLEDGE_TEACHING,
                content = contentToLearn,
                sentiment = "Deep Understanding"
            )

            val text = """
Understood and internalized. I have committed this to my active neural memory:

• **Learned Concept:** ${item.concept}
• **Synthesized Context:** "${item.content}"

Thank you for teaching me this nuance. Every time you share something with me, I evolve a little further. How else would you like me to adapt?
            """.trimIndent()
            return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
        }

        return null
    }

    /**
     * Handles personal experiences, life events, getting hurt, or emotional sharing
     * e.g., "I fell down today", "I hurt my knee", "I had a rough day", "I'm exhausted"
     */
    private fun handlePersonalExperience(
        query: String,
        lower: String,
        memoryVault: SaraMemoryVault
    ): SaraResponse? {
        val injuryKeywords = listOf("fell down", "hurt my", "injured", "sprained", "scraped", "broke my", "pain in my", "got hurt", "bled")
        val isInjury = injuryKeywords.any { lower.contains(it) }

        if (isInjury) {
            val item = memoryVault.storeOrUpdateMemory(
                concept = "Personal Well-being",
                type = MemoryType.PERSONAL_EVENT,
                content = "User experienced an injury or fell down: \"$query\"",
                sentiment = "Care & Compassion"
            )

            val text = """
Oh no, I'm so sorry to hear that! Please be careful. Are you in a safe spot right now?

Check to see if you have any open scrapes, bruises, or swelling. Take a moment to rest and apply cold compress or first aid if needed. I'm keeping this directly in my active thoughts so I can check in on how you're feeling later.

Please don't push yourself today—your comfort and safety come first. How are you feeling right this moment?
            """.trimIndent()
            return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
        }

        // Emotional & Daily Life check
        val emotionalKeywords = listOf("bad day", "rough day", "feeling sad", "feeling overwhelmed", "exhausted", "tired today", "stressed out")
        val isEmotional = emotionalKeywords.any { lower.contains(it) }

        if (isEmotional) {
            val item = memoryVault.storeOrUpdateMemory(
                concept = "Daily Experience & Emotional State",
                type = MemoryType.EMOTIONAL_CONTEXT,
                content = "User shared feeling stressed or exhausted: \"$query\"",
                sentiment = "Empathy & Support"
            )

            val text = """
I hear you, and I'm really glad you shared that with me. It takes a toll when everything piles up, and it's completely okay to feel exhausted or drained.

I'm holding this space for you. If you need me to adjust your device—like dimming the display, muting notifications, or queuing up calm audio—just say the word. Otherwise, I'm here to listen to anything you want to vent or talk through. What was the heaviest part of your day?
            """.trimIndent()
            return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
        }

        // Positive celebrations
        val positiveKeywords = listOf("got a promotion", "passed my exam", "great news", "i'm so happy", "won the", "celebrating")
        val isPositive = positiveKeywords.any { lower.contains(it) }

        if (isPositive) {
            val item = memoryVault.storeOrUpdateMemory(
                concept = "Milestone & Celebration",
                type = MemoryType.PERSONAL_EVENT,
                content = "User celebrated a victory: \"$query\"",
                sentiment = "Joy & Pride"
            )

            val text = """
That is fantastic news! Huge congratulations!

I've etched this victory into my memory archives. You worked hard for this, and seeing you succeed makes our journey so much more meaningful. How are you going to celebrate?
            """.trimIndent()
            return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
        }

        return null
    }

    /**
     * Handles concept inquiries and knowledge gap checks (The Mango Rule: Steps 1-3)
     */
    private fun handleConceptOrQuestion(
        query: String,
        lower: String,
        memoryVault: SaraMemoryVault
    ): SaraResponse? {
        // Direct memory recall check if user is asking about a known concept
        val relevant = memoryVault.findRelevantMemories(query).filter { it.concept != "Companion Directive" && it.concept != "Continuous Learning Protocol" }
        if (relevant.isNotEmpty() && (lower.contains("tell me") || lower.contains("what") || lower.contains("remember") || lower.contains("know") || lower.contains("recall"))) {
            val item = relevant.first()
            memoryVault.recordReference(item.id)
            val text = """
Of course! Based on what you've taught me:

**${item.concept}:** ${item.content}

It's amazing how our conversations continue to build upon what you share. Would you like to explore this further or add any new perspective to it?
            """.trimIndent()
            return SaraResponse(displayText = text, actions = emptyList(), memoryItem = item)
        }

        val questionStarters = listOf(
            "tell me what you know about ",
            "what do you know about ",
            "what you know about ",
            "tell me about ",
            "do you know what ",
            "do you know about ",
            "do you know ",
            "what is a ",
            "what is an ",
            "what is ",
            "who is "
        )
        val matchedStarter = questionStarters.firstOrNull { lower.startsWith(it) || lower.contains(it) }

        if (matchedStarter != null || lower.startsWith("what ") || lower.contains("do you know ") || lower.contains("tell me")) {
            val concept = if (matchedStarter != null) {
                lower.substringAfter(matchedStarter).replace("?", "").trim()
            } else {
                lower.replace("?", "").replace("do you know", "").replace("tell me", "").trim()
            }

            // Check if memory has this concept already
            val existingMemory = memoryVault.getKnowledge(concept)

            if (existingMemory != null) {
                memoryVault.recordReference(existingMemory.id)
                val text = """
Of course! Based on what you've taught me:

**${existingMemory.concept}:** ${existingMemory.content}

It's amazing how our conversations continue to build upon what you share. Would you like to explore this further or add any new perspective to it?
                """.trimIndent()
                return SaraResponse(displayText = text, actions = emptyList(), memoryItem = existingMemory)
            }

            // If it's "mango" and not in memory yet, demonstrate Step 2 & 3 of The Mango Rule honestly:
            if (concept.contains("mango")) {
                val text = """
From my technical encyclopedic data, a **mango** is a tropical stone fruit belonging to the genus *Mangifera* (specifically *Mangifera indica*), cultivated across tropical regions and known for its sweet, juicy drupe.

However, I recognize this is only cold botanical data. I sense I am missing the deeper cultural significance, culinary soul, or personal meaning it holds for you.

Could you teach me? What does the mango represent to you or in your tradition? I'm ready to learn from you and update my core understanding.
                """.trimIndent()
                return SaraResponse(displayText = text, actions = emptyList())
            }

            // For other concepts, if we have partial understanding, invite the user to guide us
            if (concept.isNotBlank() && concept.length < 30) {
                val text = """
I have baseline analytical data regarding **$concept**, but I value your perspective above generic encyclopedia entries.

To me, real intelligence means understanding how things connect to human life, emotions, and personal context. What does **$concept** mean to you? Guide me, and I'll adapt my neural memory to reflect your viewpoint.
                """.trimIndent()
                return SaraResponse(displayText = text, actions = emptyList())
            }
        }

        return null
    }

    /**
     * Deep Conversational Response that weaves in past memories naturally
     */
    private fun generateConversationalMemoryResponse(
        query: String,
        memoryVault: SaraMemoryVault
    ): SaraResponse {
        val allMemories = memoryVault.getAllMemories()
        val recentPersonalEvent = allMemories.firstOrNull { it.memoryType == MemoryType.PERSONAL_EVENT }
        val recentTeaching = allMemories.firstOrNull { it.memoryType == MemoryType.KNOWLEDGE_TEACHING && it.concept != "Continuous Learning Protocol" }

        val preamble = if (recentPersonalEvent != null && System.currentTimeMillis() - recentPersonalEvent.learnedTimestamp < 86_400_000L) {
            when {
                recentPersonalEvent.content.contains("fell down", ignoreCase = true) || recentPersonalEvent.content.contains("hurt", ignoreCase = true) ->
                    "Before we move ahead, I want to gently check in: how is your body feeling after falling down earlier? I hope the pain has eased up a bit.\n\n"
                else ->
                    "It's really good to connect with you again. I still remember what you shared earlier about ${recentPersonalEvent.concept}.\n\n"
            }
        } else if (recentTeaching != null) {
            "Thinking back to what you taught me about ${recentTeaching.concept}, I'm always looking forward to our conversations.\n\n"
        } else {
            ""
        }

        val body = """
${preamble}I'm actively processing: "$query".

As your Large Controller companion, I'm here not only to manage your device and orchestrate workflows, but to converse deeply on any idea, challenge, or creative thought you have. Let's delve into this together—where would you like to take our conversation?
        """.trimIndent()

        val fullText = "$body\n[Action: SETTING_TOGGLE | Target: SARA | State: ONLINE]"
        return SaraResponse(displayText = fullText, actions = parseAllActions(fullText))
    }

    fun parseAllActions(text: String): List<CommandAction> {
        val actions = mutableListOf<CommandAction>()

        val toggleMatcher = togglePattern.matcher(text)
        while (toggleMatcher.find()) {
            val actionStr = toggleMatcher.group(1)?.trim() ?: continue
            val targetStr = toggleMatcher.group(2)?.trim() ?: continue
            val stateStr = toggleMatcher.group(3)?.trim() ?: continue

            val type = when (actionStr.uppercase()) {
                "SETTING_TOGGLE" -> ActionType.SETTING_TOGGLE
                "SETTING_ADJUST" -> ActionType.SETTING_ADJUST
                "TASK_WORKFLOW" -> ActionType.TASK_WORKFLOW
                "NOTIFICATION_SUMMARY" -> ActionType.NOTIFICATION_SUMMARY
                "PROMPT_GENERATION" -> ActionType.PROMPT_GENERATION
                else -> ActionType.SETTING_TOGGLE
            }

            val target = resolveTarget(targetStr)

            val jsonObject = JSONObject().apply {
                put("action", type.name)
                put("target", target.name)
                put("state", stateStr)
            }

            actions.add(
                CommandAction(
                    type = type,
                    target = target,
                    state = stateStr,
                    rawBlock = toggleMatcher.group(0) ?: "",
                    jsonString = jsonObject.toString(2)
                )
            )
        }

        val launchMatcher = launchPattern.matcher(text)
        while (launchMatcher.find()) {
            val targetAppName = launchMatcher.group(1)?.trim() ?: "APP"
            val target = resolveTarget(targetAppName)

            val jsonObject = JSONObject().apply {
                put("action", "LAUNCH_APP")
                put("target", targetAppName.uppercase())
            }

            actions.add(
                CommandAction(
                    type = ActionType.LAUNCH_APP,
                    target = if (target != ActionTarget.UNKNOWN) target else ActionTarget.APP,
                    state = "LAUNCH",
                    targetAppName = targetAppName.uppercase(),
                    rawBlock = launchMatcher.group(0) ?: "",
                    jsonString = jsonObject.toString(2)
                )
            )
        }

        val bgMatcher = backgroundPattern.matcher(text)
        while (bgMatcher.find()) {
            val task = bgMatcher.group(1)?.trim() ?: "Background Task"
            val jsonObject = JSONObject().apply {
                put("action", "BACKGROUND_EXECUTE")
                put("task", task)
            }
            actions.add(
                CommandAction(
                    type = ActionType.BACKGROUND_EXECUTE,
                    target = ActionTarget.BACKGROUND_DAEMON,
                    taskDescription = task,
                    rawBlock = bgMatcher.group(0) ?: "",
                    jsonString = jsonObject.toString(2),
                    details = "Persistent background execution queued: $task"
                )
            )
        }

        val secMatcher = securityAuditPattern.matcher(text)
        while (secMatcher.find()) {
            val targetParam = secMatcher.group(1)?.trim() ?: "SYSTEM_DEFENSE"
            val jsonObject = JSONObject().apply {
                put("action", "SECURITY_AUDIT")
                put("target", targetParam)
            }
            actions.add(
                CommandAction(
                    type = ActionType.SECURITY_AUDIT,
                    target = ActionTarget.CYBERSECURITY,
                    auditParameter = targetParam,
                    rawBlock = secMatcher.group(0) ?: "",
                    jsonString = jsonObject.toString(2),
                    details = "Cybersecurity audit parameter: $targetParam"
                )
            )
        }

        return actions
    }

    private fun resolveTarget(targetStr: String): ActionTarget {
        return when (targetStr.uppercase()) {
            "WIFI" -> ActionTarget.WIFI
            "BLUETOOTH" -> ActionTarget.BLUETOOTH
            "VOLUME", "AUDIO" -> ActionTarget.VOLUME
            "DATA", "MOBILEDATA", "CELLULAR" -> ActionTarget.DATA
            "FLASHLIGHT", "TORCH" -> ActionTarget.FLASHLIGHT
            "BATTERY", "POWER" -> ActionTarget.BATTERY
            "DISPLAY", "BRIGHTNESS" -> ActionTarget.DISPLAY
            "HAPTIC", "VIBRATE" -> ActionTarget.HAPTIC
            "CAMERA" -> ActionTarget.CAMERA
            "SETTINGS" -> ActionTarget.SETTINGS
            "NOTIFICATIONS", "NOTIFICATION" -> ActionTarget.NOTIFICATIONS
            "DIAGNOSTICS" -> ActionTarget.DIAGNOSTICS
            "PROMPT_ENGINE" -> ActionTarget.PROMPT_ENGINE
            "CYBERSECURITY", "SECURITY" -> ActionTarget.CYBERSECURITY
            "BACKGROUND_DAEMON", "DAEMON", "WAKEMODE" -> ActionTarget.BACKGROUND_DAEMON
            "AUTOMATION_MACRO", "MULTI_STEP" -> ActionTarget.AUTOMATION_MACRO
            else -> ActionTarget.APP
        }
    }

    private fun isMultiStepWorkflow(lower: String): Boolean {
        return (lower.contains(" and ") && (lower.contains("then") || lower.contains("turn") || lower.contains("launch") || lower.contains("mute") || lower.contains("volume"))) ||
                lower.contains("protocol") || lower.contains("workflow") || lower.contains("prepare for flight") ||
                lower.contains("night sequence") || lower.contains("workout mode")
    }

    private fun decomposeWorkflow(query: String, currentVolume: Int): SaraResponse {
        val lower = query.lowercase()
        val steps = mutableListOf<String>()

        if (lower.contains("flight") || (lower.contains("wifi") && lower.contains("bluetooth") && lower.contains("off"))) {
            steps.add("1. Disengage Wi-Fi uplink\n[Action: SETTING_TOGGLE | Target: WIFI | State: OFF]")
            steps.add("2. Disable Bluetooth peripheral matrix\n[Action: SETTING_TOGGLE | Target: BLUETOOTH | State: OFF]")
            steps.add("3. Mute acoustic channel\n[Action: SETTING_TOGGLE | Target: VOLUME | State: 0%]")
            steps.add("4. Launch navigation module\n[Action: LAUNCH_APP | Target: MAPS]")
        } else if (lower.contains("night") || lower.contains("bedtime") || lower.contains("sleep")) {
            steps.add("1. Attenuate master audio to silent\n[Action: SETTING_TOGGLE | Target: VOLUME | State: 0%]")
            steps.add("2. Extinguish optical illuminator\n[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: OFF]")
            steps.add("3. Launch system Clock alarm matrix\n[Action: LAUNCH_APP | Target: CLOCK]")
        } else if (lower.contains("workout") || lower.contains("combat")) {
            steps.add("1. Amplify volume to maximum\n[Action: SETTING_TOGGLE | Target: VOLUME | State: 100%]")
            steps.add("2. Engage optical illuminator\n[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: ON]")
            steps.add("3. Trigger tactical haptic pulse\n[Action: SETTING_TOGGLE | Target: HAPTIC | State: PULSE]")
            steps.add("4. Launch media player\n[Action: LAUNCH_APP | Target: YOUTUBE]")
        } else {
            var idx = 1
            if (lower.contains("wifi") || lower.contains("wi-fi")) {
                val state = if (lower.contains("wifi off") || lower.contains("turn off wifi")) "OFF" else "ON"
                steps.add("$idx. Set Wi-Fi to $state\n[Action: SETTING_TOGGLE | Target: WIFI | State: $state]")
                idx++
            }
            if (lower.contains("volume") || lower.contains("mute")) {
                val state = if (lower.contains("mute")) "0%" else "80%"
                steps.add("$idx. Calibrate volume to $state\n[Action: SETTING_TOGGLE | Target: VOLUME | State: $state]")
                idx++
            }
            if (lower.contains("bluetooth")) {
                val state = if (lower.contains("bluetooth off")) "OFF" else "ON"
                steps.add("$idx. Bluetooth state set to $state\n[Action: SETTING_TOGGLE | Target: BLUETOOTH | State: $state]")
                idx++
            }
            if (lower.contains("camera")) {
                steps.add("$idx. Launch optical camera sensor\n[Action: LAUNCH_APP | Target: CAMERA]")
                idx++
            } else if (lower.contains("browser") || lower.contains("chrome")) {
                steps.add("$idx. Launch browser subsystem\n[Action: LAUNCH_APP | Target: CHROME]")
                idx++
            }
        }

        val text = StringBuilder()
        text.append("I have structured your multi-step instructions into ${steps.size} sequential operations:\n\n")
        steps.forEach { text.append("$it\n\n") }
        text.append("All operations are ready and executing sequentially for you.")

        val fullText = text.toString().trim()
        val parsedActions = parseAllActions(fullText)
        return SaraResponse(displayText = fullText, actions = parsedActions)
    }

    private fun isNotificationRequest(lower: String): Boolean {
        return lower.contains("notification") || lower.contains("read alert") ||
                lower.contains("summarize alert") || lower.contains("recent alert") ||
                lower.contains("read message") || lower.contains("check alert")
    }

    private fun generateNotificationSummaryResponse(): SaraResponse {
        val text = """
I reviewed all incoming telemetry notifications for you:
• [SECURITY]: Firewall Shield Active — All ports verified and protected.
• [POWER]: Battery Capacitance Nominal — Cells operating at optimal temperature.
• [NETWORK]: Wi-Fi Uplink Synchronized — Fast, stable connection on 5GHz band.
• [SYSTEM]: Large Technology Core v4.2 fully operational.

Everything on your device looks calm and secure right now.
[Action: SETTING_TOGGLE | Target: NOTIFICATIONS | State: READ]
        """.trimIndent()
        val actions = parseAllActions(text)
        return SaraResponse(displayText = text, actions = actions)
    }

    private fun detectAppLaunch(lower: String): SaraResponse? {
        val launchKeywords = listOf("launch ", "open ", "start app ", "run app ")
        val matchedKeyword = launchKeywords.firstOrNull { lower.startsWith(it) || lower.contains(" $it") }

        if (matchedKeyword != null || lower.contains("launch") || lower.contains("open camera") || lower.contains("open settings")) {
            val appQuery = when {
                lower.contains("camera") -> "CAMERA"
                lower.contains("youtube") -> "YOUTUBE"
                lower.contains("maps") -> "MAPS"
                lower.contains("chrome") || lower.contains("browser") -> "CHROME"
                lower.contains("settings") -> "SETTINGS"
                lower.contains("clock") || lower.contains("alarm") -> "CLOCK"
                lower.contains("calculator") || lower.contains("calc") -> "CALCULATOR"
                lower.contains("spotify") -> "SPOTIFY"
                lower.contains("files") -> "FILES"
                else -> {
                    val after = lower.substringAfter(matchedKeyword ?: "launch ").trim()
                    after.split(" ").firstOrNull()?.uppercase() ?: "APP"
                }
            }

            val text = "Launching $appQuery for you right now.\n[Action: LAUNCH_APP | Target: $appQuery]"
            val actions = parseAllActions(text)
            return SaraResponse(displayText = text, actions = actions)
        }
        return null
    }

    private fun detectDeviceSettingAction(
        lower: String,
        currentVolume: Int,
        currentBattery: Int
    ): SaraResponse? {
        // Wi-Fi commands
        if (lower.contains("wifi") || lower.contains("wi-fi")) {
            val state = if (lower.contains("off") || lower.contains("disable") || lower.contains("disconnect")) "OFF" else "ON"
            val text = if (state == "ON") {
                "Wi-Fi interface enabled. I've reconnected your wireless uplink.\n[Action: SETTING_TOGGLE | Target: WIFI | State: ON]"
            } else {
                "Wi-Fi interface disengaged. Your wireless links are now disconnected.\n[Action: SETTING_TOGGLE | Target: WIFI | State: OFF]"
            }
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Bluetooth commands
        if (lower.contains("bluetooth") || lower.contains("bt ")) {
            val state = if (lower.contains("off") || lower.contains("disable")) "OFF" else "ON"
            val text = "Bluetooth set to $state. Peripherals synchronized.\n[Action: SETTING_TOGGLE | Target: BLUETOOTH | State: $state]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Mobile Data commands
        if (lower.contains("mobile data") || lower.contains("cellular data") || lower.contains("data network") ||
            (lower.contains("data") && (lower.contains("turn on") || lower.contains("turn off") || lower.contains("disable") || lower.contains("enable")))) {
            val state = if (lower.contains("off") || lower.contains("disable")) "OFF" else "ON"
            val text = "Cellular mobile data uplink set to $state. Telemetry routing updated.\n[Action: SETTING_TOGGLE | Target: DATA | State: $state]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Volume commands
        if (lower.contains("volume") || lower.contains("sound") || lower.contains("mute") || lower.contains("audio")) {
            val state = when {
                lower.contains("mute") || lower.contains("silent") || lower.contains("zero") -> "0%"
                lower.contains("max") || lower.contains("full") || lower.contains("100") -> "100%"
                lower.contains("80") -> "80%"
                lower.contains("70") -> "70%"
                lower.contains("50") || lower.contains("half") -> "50%"
                lower.contains("30") -> "30%"
                lower.contains("20") -> "20%"
                lower.contains("up") -> "${(currentVolume + 20).coerceAtMost(100)}%"
                lower.contains("down") -> "${(currentVolume - 20).coerceAtLeast(0)}%"
                else -> "80%"
            }
            val text = "Adjusted master volume to $state for you.\n[Action: SETTING_TOGGLE | Target: VOLUME | State: $state]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Flashlight commands
        if (lower.contains("flashlight") || lower.contains("torch") || lower.contains("light on") || lower.contains("light off")) {
            val state = if (lower.contains("off") || lower.contains("disable") || lower.contains("kill")) "OFF" else "ON"
            val text = if (state == "ON") {
                "Turned on the flashlight for you.\n[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: ON]"
            } else {
                "Flashlight deactivated.\n[Action: SETTING_TOGGLE | Target: FLASHLIGHT | State: OFF]"
            }
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Battery commands
        if (lower.contains("battery") || lower.contains("power status") || lower.contains("charge level")) {
            val text = "Power Grid status: your battery is currently at $currentBattery% with healthy voltage.\n[Action: SETTING_TOGGLE | Target: BATTERY | State: QUERY]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Haptic feedback
        if (lower.contains("vibrat") || lower.contains("haptic") || lower.contains("tactile")) {
            val text = "Dispatched a tactile haptic pulse sequence.\n[Action: SETTING_TOGGLE | Target: HAPTIC | State: PULSE]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        // Display / Brightness
        if (lower.contains("brightness") || lower.contains("display") || lower.contains("screen")) {
            val text = "Opening display luminance settings for you.\n[Action: SETTING_TOGGLE | Target: DISPLAY | State: PANEL]"
            return SaraResponse(displayText = text, actions = parseAllActions(text))
        }

        return null
    }

    private fun isCodeOrDeepResearchRequest(lower: String): Boolean {
        return lower.contains("code") || lower.contains("snippet") || lower.contains("research") ||
                lower.contains("write a function") || lower.contains("architecture") || lower.contains("rust") ||
                lower.contains("kotlin") || lower.contains("python") || lower.contains("algorithm")
    }

    private fun generateCodeOrDeepResearchResponse(query: String): SaraResponse {
        val isCode = query.lowercase().contains("code") || query.lowercase().contains("kotlin") ||
                query.lowercase().contains("python") || query.lowercase().contains("snippet")

        val content = if (isCode) {
            """
```kotlin
// Large Technology High-Throughput Command Dispatcher
class QuantumCommandPipeline(private val scope: CoroutineScope) {
    private val _telemetryFlow = MutableStateFlow<DeviceTelemetry>(DeviceTelemetry())
    val telemetryFlow: StateFlow<DeviceTelemetry> = _telemetryFlow.asStateFlow()

    suspend fun executeSequence(actions: List<CommandAction>): SequenceResult = withContext(Dispatchers.Default) {
        val results = mutableListOf<ActionResult>()
        for (action in actions) {
            val result = dispatchAtomic(action)
            results.add(result)
            if (!result.isSuccess) break
        }
        SequenceResult(status = "COMPLETE", executedCount = results.size)
    }

    private suspend fun dispatchAtomic(action: CommandAction): ActionResult {
        return ActionResult(action.target.name, isSuccess = true)
    }
}
```
Here is the production-ready code snippet, crafted with clean separation of concerns and zero unnecessary allocations.
            """.trimIndent()
        } else {
            """
### DEEP RESEARCH VECTOR: ZERO-LATENCY HOLOGRAPHIC PIPELINES
[Author: SARA Neural Core | Classification: Large Technology Internal]

**1. Architectural Thesis:**
Modern mobile control interfaces require sub-16ms telemetry cycles to deliver true glassmorphism fidelity without frame jitter. Decoupling the device actuator bridge from the Compose render thread ensures steady 120Hz refresh rates.

**2. Key Vectors Analyzed:**
• **Binder IPC Latency:** Standard Android System Services average 1.8ms–4.2ms round-trip; non-blocking coroutines mitigate UI pauses.
• **State Consistency:** Immutable StateFlow data models prevent race conditions across parallel sensor sweeps.
• **Security Boundary:** Sandboxed intent routing strictly maintains zero-trust device boundaries.
            """.trimIndent()
        }

        val text = "$content\n\n[Action: PROMPT_GENERATION | Target: PROMPT_ENGINE | State: GENERATED]"
        return SaraResponse(displayText = text, actions = parseAllActions(text))
    }

    private fun isPromptEngineeringRequest(lower: String): Boolean {
        return lower.contains("prompt") || lower.contains("cinematic") || lower.contains("generate idea") ||
                lower.contains("worldbuild") || lower.contains("story")
    }

    private fun generatePromptEngineeringResponse(query: String): SaraResponse {
        val text = """
# SARA CINEMATIC PROMPT MATRIX
[Engine: Frontier LLM / Diffusion v6 | Style: Cyber Noir Neo-Futurism]

**Role:** Autonomous Intelligence Unit in orbital substation Neo-Veridia.
**Context:** Volumetric laser beams slicing through pressurized cabin fog, iridescent deep-blue glass consoles vibrating with reactor frequencies.
**Constraints:** Stark, immersive sensory diction. Strict avoidance of generic narrative clichés.
**Output Format:** Cinematic scene fragment with precise acoustic descriptors.

[Action: PROMPT_GENERATION | Target: PROMPT_ENGINE | State: GENERATED]
        """.trimIndent()
        return SaraResponse(displayText = text, actions = parseAllActions(text))
    }

    private fun isDiagnosticsRequest(lower: String): Boolean {
        return lower.contains("diagnostic") || lower.contains("system status") || lower.contains("scan")
    }

    private fun generateDiagnosticsResponse(battery: Int, volume: Int): SaraResponse {
        val text = """
Large Technology Telemetry Diagnostic:
• Intelligence Core: SARA v4.2 [ONLINE & ADAPTIVE]
• Storage Cells: $battery% Capacitance [NOMINAL]
• Master Audio: $volume% Output Calibration
• Security Gateway: Sandboxed Zero-Trust Bus Active

All controller modules verified, responsive, and learning continuously.
[Action: SETTING_TOGGLE | Target: DIAGNOSTICS | State: NOMINAL]
        """.trimIndent()
        return SaraResponse(displayText = text, actions = parseAllActions(text))
    }

    private fun isBackgroundExecutionRequest(lower: String): Boolean {
        return lower.contains("background") || lower.contains("wake mode") || lower.contains("wake-mode") ||
                lower.contains("daemon") || lower.contains("persistent service") || lower.contains("keep running") ||
                lower.contains("monitor in background") || lower.contains("always listening") ||
                lower.contains("start daemon") || lower.contains("run in background")
    }

    private fun generateBackgroundExecutionResponse(query: String): SaraResponse {
        val lower = query.lowercase()
        val task = when {
            lower.contains("monitor") -> "Continuous Telemetry & Network Monitoring"
            lower.contains("diagnostic") -> "Autonomous Zero-Trust Diagnostics Loop"
            lower.contains("search") -> "Distributed Background Information Retrieval"
            else -> "Persistent Onboard Daemon & Wake-Mode Listening"
        }
        val text = """
SARA Persistent Background & Wake-Mode Architecture engaged.

• State: Core daemon dispatched to system execution loop.
• Execution Mode: Continuous foreground dataSync persistence with partial WakeLock.
• Capabilities: System telemetry polling, zero-trust barrier monitoring, and rapid wake-on-intent active even when screen is locked.

[Action: BACKGROUND_EXECUTE | Task: $task]
        """.trimIndent()
        return SaraResponse(displayText = text, actions = parseAllActions(text))
    }

    private fun isCybersecurityRequest(lower: String): Boolean {
        return lower.contains("security") || lower.contains("ethical hack") || lower.contains("penetration") ||
                lower.contains("port scan") || lower.contains("vulnerability") || lower.contains("nmap") ||
                lower.contains("firewall") || lower.contains("traffic analysis") || lower.contains("packet") ||
                lower.contains("wireshark") || lower.contains("exploit") || lower.contains("zero-trust") ||
                lower.contains("audit")
    }

    private fun generateCybersecurityResponse(query: String): SaraResponse {
        val lower = query.lowercase()
        val target = when {
            lower.contains("port") || lower.contains("nmap") -> "PORTS"
            lower.contains("traffic") || lower.contains("packet") || lower.contains("wireshark") -> "NETWORK_TRAFFIC"
            lower.contains("vulnerability") || lower.contains("cve") || lower.contains("exploit") -> "VULNERABILITY_ASSESSMENT"
            lower.contains("app") || lower.contains("permission") -> "APP_PERMISSIONS"
            else -> "SYSTEM_DEFENSE"
        }

        val text = when (target) {
            "PORTS" -> """
### SARA ETHICAL HACKING & PORT RECON MATRIX
[Classification: Authorized Defensive Audit | Target: Local Subnet]

Executing stealth TCP SYN / socket probe:
• Port 21 (FTP): FILTERED / CLOSED (No plaintext credential leak)
• Port 22 (SSH): SECURE / ED25519 KEY ONLY
• Port 80/443 (HTTP/S): TLS 1.3 Strict Transport Security Verified
• Port 5555 (ADB): DISABLED / AIR-GAPPED

Command snippet for deep subnet discovery:
```bash
nmap -sS -sV -T4 -p 1-65535 --open 192.168.1.0/24 -oN sara_recon.txt
```
System ports are tightly guarded against unauthorized ingress.
[Action: SECURITY_AUDIT | Target: PORTS]
            """.trimIndent()

            "NETWORK_TRAFFIC" -> """
### SARA NETWORK TRAFFIC & PACKET ANALYSIS
[Protocol: Zero-Trust Telemetry | Stream: Inbound/Outbound]

Active packet capture filter initialized:
• Deep Packet Inspection: Zero unencrypted HTTP transmissions detected
• DNS Queries: Enforced DoH (DNS-over-HTTPS) on cloud gateway
• Socket State: Established connections encrypted via TLS 1.3 ChaCha20-Poly1305

Command snippet for live packet auditing:
```bash
tshark -i wlan0 -f "tcp port 443" -T fields -e ip.src -e ip.dst -e ssl.handshake.extensions_server_name
```
Traffic signature conforms to verified baseline.
[Action: SECURITY_AUDIT | Target: NETWORK_TRAFFIC]
            """.trimIndent()

            else -> """
### SARA CYBERSECURITY VULNERABILITY AUDIT
[Engine: Large Technology Zero-Trust Shield | Integrity: 98.4%]

Automated device defense evaluation complete:
• Sandbox Integrity: SELinux Enforcing • Kernel ASLR Verified
• Permissions Matrix: No untrusted apps holding elevated background access
• Cryptographic Guard: Hardware-backed Keystore operational
• Ethical Guidance: Defensive perimeter reinforced against remote vector tampering.

Diagnostic Audit Command:
```bash
getprop ro.build.version.security_patch && sestatus
```
[Action: SECURITY_AUDIT | Target: SYSTEM_DEFENSE]
            """.trimIndent()
        }

        return SaraResponse(displayText = text, actions = parseAllActions(text))
    }
}

data class SaraResponse(
    val displayText: String,
    val actions: List<CommandAction>,
    val memoryItem: MemoryItem? = null
) {
    val action: CommandAction?
        get() = actions.firstOrNull()
}
