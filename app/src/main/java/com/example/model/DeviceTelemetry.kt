package com.example.model

data class DeviceTelemetry(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val batteryHealth: String = "Good",
    val batteryVoltageMv: Int = 4100,
    val currentVolumePercent: Int = 50,
    val maxVolumeSteps: Int = 15,
    val currentVolumeStep: Int = 8,
    val isFlashlightActive: Boolean = false,
    val isNetworkConnected: Boolean = true,
    val networkType: String = "Wi-Fi (Active)",
    val totalRamMb: Long = 6144,
    val availableRamMb: Long = 3280,
    val ramUsedPercent: Int = 46,
    val cpuArchitecture: String = "ARM64-v8a",
    val androidVersion: String = "Android 15 (VanillaIceCream)",
    val systemUptimeHours: String = "24h 12m",
    val securityLevel: String = "SECURE / SANDBOXED"
)

data class AutomationRoutine(
    val id: String,
    val codeName: String,
    val title: String,
    val description: String,
    val iconName: String,
    val triggerPhrase: String,
    val actions: List<CommandAction>,
    val isEnabled: Boolean = true
)

data class PromptTemplate(
    val id: String,
    val title: String,
    val category: String,
    val targetEngine: String,
    val description: String,
    val basePrompt: String,
    val suggestedTags: List<String>
)
